package com.miapp.greenbunny.api

import android.content.Context
import com.miapp.greenbunny.model.*

class CheckoutCoordinator(private val context: Context) {
    private val orderService = RetrofitClient.createOrderService(context)
    private val orderProductService = RetrofitClient.createOrderProductService(context)
    private val shipmentService = RetrofitClient.createShipmentService(context)

    suspend fun checkout(cartItems: List<CartItem>, shippingAddress: String, acceptPayment: Boolean): Pair<Order, Shipment?> {
        val total = cartItems.sumOf { it.price * it.quantity }
        val order = withRetry { orderService.createOrder(CreateOrderRequest(total = total)) }
        for (item in cartItems) {
            withRetry {
                orderProductService.createOrderProduct(
                    CreateOrderProductRequest(
                        order_id = order.id,
                        product_id = item.productId,
                        quantity = item.quantity,
                        unit_price = item.price
                    )
                )
            }
            val jitter = (100..300).random().toLong()
            kotlinx.coroutines.delay(800 + jitter)
        }
        val finalStatus = if (acceptPayment) "pagado" else "rechazado"
        var updated: Order = try {
            withRetry { orderService.updateOrder(order.id, UpdateOrderRequest(status = finalStatus)) }
        } catch (e: Exception) {
            // Fallback: continuar aunque PATCH falle; usar estado en memoria
            Order(id = order.id, created_at = order.created_at, total = order.total, status = finalStatus, user_id = order.user_id)
        }
        if (!acceptPayment) return updated to null
        var shipment = withRetry { shipmentService.createShipment(CreateShipmentRequest(address = shippingAddress, order_id = order.id)) }
        shipment = try {
            withRetry { shipmentService.updateShipment(shipment.id, UpdateShipmentRequest(address = shippingAddress, status = shipment.status)) }
        } catch (e: Exception) { shipment }
        return updated to shipment
    }

    private suspend fun <T> withRetry(block: suspend () -> T): T {
        var attempt = 0
        var delayMs = 300L
        while (true) {
            try {
                return block()
            } catch (e: retrofit2.HttpException) {
                val code = e.code()
                if (code == 429 || (code >= 500 && code < 600)) {
                    if (attempt < 3) {
                        val retryAfterHeader = e.response()?.headers()?.get("Retry-After")
                        val serverDelay = retryAfterHeader?.toLongOrNull()?.let { it * 1000 }
                        val jitter = (100..300).random().toLong()
                        val wait = serverDelay ?: (delayMs + jitter)
                        kotlinx.coroutines.delay(wait)
                        delayMs *= 2
                        attempt++
                        continue
                    }
                }
                throw e
            } catch (e: java.io.IOException) {
                if (attempt < 3) {
                    val jitter = (100..300).random().toLong()
                    kotlinx.coroutines.delay(delayMs + jitter)
                    delayMs *= 2
                    attempt++
                    continue
                }
                throw e
            }
        }
    }
}
