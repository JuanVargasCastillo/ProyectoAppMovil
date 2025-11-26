package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.CreateOrderProductRequest
import com.miapp.greenbunny.model.OrderProduct
import com.miapp.greenbunny.model.UpdateOrderProductRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST

interface OrderProductService {
    @GET("order_product")
    suspend fun getOrderProducts(): List<OrderProduct>

    @POST("order_product")
    suspend fun createOrderProduct(@Body request: CreateOrderProductRequest): OrderProduct

    @DELETE("order_product/{id}")
    suspend fun deleteOrderProduct(@Path("id") id: Int)

    @GET("order_product/{id}")
    suspend fun getOrderProduct(@Path("id") id: Int): OrderProduct

    @PATCH("order_product/{id}")
    suspend fun updateOrderProduct(@Path("id") id: Int, @Body request: UpdateOrderProductRequest): OrderProduct
}
