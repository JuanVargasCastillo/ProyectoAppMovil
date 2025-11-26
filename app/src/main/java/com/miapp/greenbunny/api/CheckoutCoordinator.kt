package com.miapp.greenbunny.api

import android.content.Context
import com.miapp.greenbunny.model.*

class CheckoutCoordinator(private val context: Context) {
    private val orderService = RetrofitClient.createOrderService(context)
    private val orderProductService = RetrofitClient.createOrderProductService(context)
    private val shipmentService = RetrofitClient.createShipmentService(context)

    suspend fun checkout(cartItems: List<CartItem>, shippingAddress: String, acceptPayment: Boolean): Pair<Order, Shipment?> {
        val total = cartItems.sumOf { it.price * it.quantity }
        val order = orderService.createOrder(CreateOrderRequest(total = total))
        for (item in cartItems) {
            orderProductService.createOrderProduct(
                CreateOrderProductRequest(
                    order_id = order.id,
                    product_id = item.productId,
                    quantity = item.quantity,
                    unit_price = item.price
                )
            )
        }
        val finalStatus = if (acceptPayment) "pagado" else "rechazado"
        val updated = orderService.updateOrder(order.id, UpdateOrderRequest(status = finalStatus))
        if (!acceptPayment) return updated to null
        var shipment = shipmentService.createShipment(CreateShipmentRequest(address = shippingAddress, order_id = order.id))
        shipment = shipmentService.updateShipment(shipment.id, UpdateShipmentRequest(address = shippingAddress, status = shipment.status))
        return updated to shipment
    }
}
