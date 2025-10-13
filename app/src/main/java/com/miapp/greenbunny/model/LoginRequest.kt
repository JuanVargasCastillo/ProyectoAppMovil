package com.miapp.greenbunny.model

/**
 * LoginRequest
 * Cuerpo del POST de login.
 */
data class LoginRequest(
    val email: String,
    val password: String
)