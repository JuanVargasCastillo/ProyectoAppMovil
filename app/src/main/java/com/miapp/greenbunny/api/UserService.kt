package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.UpdateUserRequest
import com.miapp.greenbunny.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @GET("user")
    suspend fun getUsers(): List<User>

    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: Int): User

    @PATCH("user/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body request: UpdateUserRequest): User

    @POST("user/{id}/block")
    suspend fun blockUser(@Path("id") id: Int): User

    @POST("user/{id}/unblock")
    suspend fun unblockUser(@Path("id") id: Int): User
}
