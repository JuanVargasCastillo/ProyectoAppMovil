package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.CreateOrderRequest
import com.miapp.greenbunny.model.Order
import com.miapp.greenbunny.model.UpdateOrderRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST

interface OrderService {
    @GET("order")
    suspend fun getOrders(): List<Order>

    @POST("order")
    suspend fun createOrder(@Body request: CreateOrderRequest): Order

    @DELETE("order/{id}")
    suspend fun deleteOrder(@Path("id") id: Int)

    @GET("order/{id}")
    suspend fun getOrder(@Path("id") id: Int): Order

    @PATCH("order/{id}")
    suspend fun updateOrder(@Path("id") id: Int, @Body request: UpdateOrderRequest): Order
}
