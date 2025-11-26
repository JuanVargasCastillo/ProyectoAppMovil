package com.miapp.greenbunny.model

import java.io.Serializable

data class Order(
    val id: Int,
    val created_at: Long?,
    val total: Int,
    val status: String,
    val user_id: Int?
) : Serializable

data class CreateOrderRequest(
    val total: Int,
    val status: String = "pendiente"
)

data class UpdateOrderRequest(
    val status: String
)
