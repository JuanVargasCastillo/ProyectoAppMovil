package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.CreateProductRequest
import com.miapp.greenbunny.model.CreateProductResponse
import com.miapp.greenbunny.model.Product
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProductService {

    @GET("product")
    suspend fun getProducts(): List<Product> // Para listar todos los productos

    @POST("product")
    suspend fun createProduct(@Body request: CreateProductRequest): CreateProductResponse // Para crear un producto
}
