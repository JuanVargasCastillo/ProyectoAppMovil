package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.ProductImage
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {

    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Part content: MultipartBody.Part
    ): List<ProductImage>
}
