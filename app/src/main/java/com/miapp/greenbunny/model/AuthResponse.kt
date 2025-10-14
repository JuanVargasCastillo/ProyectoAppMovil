package com.miapp.greenbunny.model

data class AuthResponse(
    val token: String, // Aquí Xano devuelve el JWT
    val user: User     // Objeto con id, name, email
)
