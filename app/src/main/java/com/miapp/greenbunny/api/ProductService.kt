package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.CreateProductRequest
import com.miapp.greenbunny.model.CreateProductResponse
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.model.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST

interface ProductService {

    @GET("product")
    suspend fun getProducts(): List<Product> // Para listar todos los productos

    @POST("product")
    suspend fun createProduct(@Body request: CreateProductRequest): CreateProductResponse // Para crear un producto

    @PATCH("product/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body request: UpdateProductRequest
    ): CreateProductResponse // Para editar un producto

    @DELETE("product/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<Void> // Para eliminar un producto
}
