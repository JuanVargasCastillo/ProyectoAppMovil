package com.miapp.greenbunny.api // Paquete del servicio de autenticación

import com.miapp.greenbunny.model.AuthResponse // Import del modelo de respuesta de login
import com.miapp.greenbunny.model.LoginRequest // Import del modelo de request de login
import com.miapp.greenbunny.model.CreateUserRequest
import com.miapp.greenbunny.model.User
import retrofit2.http.Body // Import de anotación para cuerpo de la solicitud
import retrofit2.http.GET
import retrofit2.http.POST // Import de anotación para método HTTP POST

/**
 * AuthService
 * Define el endpoint de login (y potencialmente logout) de la API de Xano.
 * Base URL usada: ApiConfig.authBaseUrl
 * Todas las líneas están comentadas para fines didácticos.
 */
interface AuthService { // Interfaz de Retrofit para autenticación
    @POST("auth/login") // Endpoint POST /login
    suspend fun login(@Body request: LoginRequest): AuthResponse // Métodoo suspend que envía email/password y recibe token + user

    // Signup
    @POST("auth/signup")
    suspend fun signup(@Body request: LoginRequest): AuthResponse

    @POST("auth/signup")
    suspend fun signup(@Body request: CreateUserRequest): AuthResponse

    
    // ¡NUEVO! Endpoint para obtener datos del usuario autenticado
    @GET("auth/me")
    suspend fun getMe(): User // Devuelve directamente el objeto User
}
