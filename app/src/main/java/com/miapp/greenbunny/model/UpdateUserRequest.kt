package com.miapp.greenbunny.model

import com.google.gson.annotations.SerializedName

/**
 * UpdateUserRequest
 * Todos los campos opcionales para enviar solo cambios.
 */
data class UpdateUserRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("phone")
    val phone: String? = null,
)