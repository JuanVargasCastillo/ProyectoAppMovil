package com.miapp.greenbunny.api // Paquete donde vive el cliente Retrofit

import android.content.Context // Import para usar Context al construir interceptores dependientes de token
import com.miapp.greenbunny.api.ApiConfig.authBaseUrl // Base URL de autenticación (Xano)
import com.miapp.greenbunny.api.ApiConfig.storeBaseUrl // Base URL de tienda/productos
import okhttp3.OkHttpClient // Cliente HTTP subyacente usado por Retrofit
import okhttp3.logging.HttpLoggingInterceptor // Interceptor de logging para depuración
import retrofit2.Retrofit // Clase principal para construir el cliente Retrofit
import retrofit2.converter.gson.GsonConverterFactory // Convertidor JSON (Gson) para serialización/deserialización
import java.util.concurrent.TimeUnit // Utilidad para definir timeouts

/**
 * RetrofitClient
 * Centraliza la creación de instancias de Retrofit y OkHttp.
 *
 * Flujo soportado:
 * 1. Servicios de autenticación (login público y /auth/me privado).
 * 2. Servicios de productos (API de tienda).
 * 3. Servicios de subida de archivos.
 * 4. Servicios de usuarios (admin) sobre la API de autenticación.
 */
object RetrofitClient { // Objeto singleton que expone métodos de fábrica

    // Builder base de OkHttp configurado con logging y timeouts.
    private fun baseOkHttpBuilder(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor().apply {
            // Nivel BODY útil en desarrollo para ver requests y responses completas.
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
    }

    // Construye Retrofit con baseUrl y cliente.
    private fun retrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /**
     * Fábrica para AuthService.
     * @param requiresAuth Si es true, agrega interceptor de token (para /auth/me, etc.).
     */
    fun createAuthService(context: Context, requiresAuth: Boolean = false): AuthService {
        val clientBuilder = baseOkHttpBuilder()

        if (requiresAuth) {
            val tokenManager = TokenManager(context)
            clientBuilder.addInterceptor(AuthInterceptor { tokenManager.getToken() })
        }

        val client = clientBuilder.build()
        return retrofit(authBaseUrl, client).create(AuthService::class.java)
    }

    /**
     * Fábrica para ProductService (API de tienda).
     */
    fun createProductService(context: Context): ProductService {
        val tokenManager = TokenManager(context)
        val client = baseOkHttpBuilder()
            .addInterceptor(AuthInterceptor { tokenManager.getToken() })
            .build()
        return retrofit(storeBaseUrl, client).create(ProductService::class.java)
    }

    /**
     * Fábrica para UploadService (subida de archivos en API de tienda).
     */
    fun createUploadService(context: Context): UploadService {
        val tokenManager = TokenManager(context)
        val client = baseOkHttpBuilder()
            .addInterceptor(AuthInterceptor { tokenManager.getToken() })
            .build()
        return retrofit(storeBaseUrl, client).create(UploadService::class.java)
    }

    fun createUserService(context: Context): UserService {
        val tokenManager = TokenManager(context)
        val client = baseOkHttpBuilder()
            .addInterceptor(AuthInterceptor { tokenManager.getToken() })
            .build()
        return retrofit(authBaseUrl, client).create(UserService::class.java)
    }

    
}
