package com.miapp.greenbunny.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * User
 * Modelo de usuario extendido para la vista ADMIN.
 * Campos opcionales para evitar romper flujos existentes.
 */
data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    // El campo "created_at" también se puede incluir si lo necesitas
    @SerializedName("created_at")
    val createdAt: Long?
) : Serializable