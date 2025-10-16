package com.miapp.greenbunny.model

/**
 * AuthResponse
 * Respuesta del endpoint de login.
 * Xano devuelve un único campo: "authToken".
 */
data class AuthResponse(
    val authToken: String
)
