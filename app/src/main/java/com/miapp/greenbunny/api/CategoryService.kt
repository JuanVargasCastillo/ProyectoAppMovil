package com.miapp.greenbunny.api

import com.miapp.greenbunny.model.Category
import com.miapp.greenbunny.model.CreateCategoryRequest
import com.miapp.greenbunny.model.UpdateCategoryRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST

interface CategoryService {
    @GET("category")
    suspend fun getCategories(): List<Category>

    @POST("category")
    suspend fun createCategory(@Body request: CreateCategoryRequest): Category

    @DELETE("category/{id}")
    suspend fun deleteCategory(@Path("id") id: Int)

    @GET("category/{id}")
    suspend fun getCategory(@Path("id") id: Int): Category

    @PATCH("category/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body request: UpdateCategoryRequest): Category
}
