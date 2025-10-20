package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.ProductImage
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Servicio de subida de imágenes.
 * Devuelve siempre una lista de ProductImage, aunque subas solo una.
 */
interface UploadService {

    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Part content: MultipartBody.Part
    ): List<ProductImage>
}
