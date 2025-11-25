package com.miapp.greenbunny.model

import com.google.gson.annotations.SerializedName

data class CreateUserRequest(
    val name: String,
    @SerializedName("last_name") val lastName: String?,
    val email: String,
    val password: String,
    val address: String?,
    val phone: String?,
    val role: String?,
    val status: String?
)

