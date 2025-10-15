package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.CreateProductRequest
import com.miapp.greenbunny.model.CreateProductResponse
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.model.ProductImage
import retrofit2.http.*

interface ProductService {

    @GET("product")
    suspend fun getProducts(): List<Product>

    @POST("product")
    suspend fun createProduct(@Body request: CreateProductRequest): CreateProductResponse

    // PATCH para actualizar imágenes del producto
    @PATCH("product/{id}")
    suspend fun updateProductImages(
        @Path("id") productId: Int,
        @Body images: Map<String, List<ProductImage>>
    ): Product
}
