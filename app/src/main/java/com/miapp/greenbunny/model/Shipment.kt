package com.miapp.greenbunny.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Shipment(
    @SerializedName("id") val id: Int,
    @SerializedName(value = "shipping_address", alternate = ["address", "direccion"]) val address: String?,
    @SerializedName(value = "shipping_date", alternate = ["shipped_at"]) val shipped_at: Long?,
    @SerializedName(value = "shipping_status", alternate = ["status", "estado"]) val status: String?,
    @SerializedName("order_id") val order_id: Int
) : Serializable

data class CreateShipmentRequest(
    @SerializedName("shipping_address") val address: String,
    @SerializedName("order_id") val order_id: Int,
    @SerializedName("shipping_status") val status: String = "pendiente"
)

data class UpdateShipmentRequest(
    @SerializedName("shipping_address") val address: String? = null,
    @SerializedName("shipping_status") val status: String? = null
)
