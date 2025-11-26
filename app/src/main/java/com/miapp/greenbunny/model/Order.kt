package com.miapp.greenbunny.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Order(
    @SerializedName("id") val id: Int,
    @SerializedName("created_at") val created_at: Long?,
    @SerializedName("total") val total: Int,
    @SerializedName(value = "status", alternate = ["estado"]) val status: String,
    @SerializedName(value = "user_id", alternate = ["usuario_id"]) val user_id: Int?
) : Serializable

data class CreateOrderRequest(
    @SerializedName("total") val total: Int,
    val status: String = "pendiente"
)

data class UpdateOrderRequest(
    val status: String
)
