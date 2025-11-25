package com.miapp.greenbunny.model

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    val name: String?,
    @SerializedName("last_name") val lastName: String?,
    val email: String?,
    val address: String?,
    val phone: String?,
    val role: String?,
    val status: String?
)

