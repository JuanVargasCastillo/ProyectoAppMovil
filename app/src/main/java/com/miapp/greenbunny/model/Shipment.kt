package com.miapp.greenbunny.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Shipment(
    @SerializedName("id") val id: Int,
    @SerializedName(value = "address", alternate = ["direccion"]) val address: String,
    @SerializedName("shipped_at") val shipped_at: Long?,
    @SerializedName(value = "status", alternate = ["estado", "estado_envio"]) val status: String,
    @SerializedName(value = "order_id", alternate = ["orden_id"]) val order_id: Int
) : Serializable

data class CreateShipmentRequest(
    @SerializedName("direccion") val address: String,
    @SerializedName("orden_id") val order_id: Int,
    @SerializedName("estado") val status: String = "pendiente"
)

data class UpdateShipmentRequest(
    @SerializedName("direccion") val address: String? = null,
    @SerializedName("estado") val status: String? = null
)
