package com.miapp.greenbunny.model

import java.io.Serializable

data class OrderProduct(
    val id: Int,
    val quantity: Int,
    val unit_price: Int,
    val subtotal: Int,
    val order_id: Int,
    val product_id: Int
) : Serializable

data class CreateOrderProductRequest(
    val order_id: Int,
    val product_id: Int,
    val quantity: Int,
    val unit_price: Int
)

data class UpdateOrderProductRequest(
    val quantity: Int?
)
