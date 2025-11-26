package com.miapp.greenbunny.model

import java.io.Serializable

data class Shipment(
    val id: Int,
    val address: String,
    val shipped_at: Long?,
    val status: String,
    val order_id: Int
) : Serializable

data class CreateShipmentRequest(
    val address: String,
    val order_id: Int,
    val status: String = "pendiente"
)

data class UpdateShipmentRequest(
    val address: String? = null,
    val status: String? = null
)
