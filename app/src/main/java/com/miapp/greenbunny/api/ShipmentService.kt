package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.CreateShipmentRequest
import com.miapp.greenbunny.model.Shipment
import com.miapp.greenbunny.model.UpdateShipmentRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST

interface ShipmentService {
    @GET("shipment")
    suspend fun getShipments(): List<Shipment>

    @POST("shipment")
    suspend fun createShipment(@Body request: CreateShipmentRequest): Shipment

    @DELETE("shipment/{id}")
    suspend fun deleteShipment(@Path("id") id: Int)

    @GET("shipment/{id}")
    suspend fun getShipment(@Path("id") id: Int): Shipment

    @PATCH("shipment/{id}")
    suspend fun updateShipment(@Path("id") id: Int, @Body request: UpdateShipmentRequest): Shipment
}
