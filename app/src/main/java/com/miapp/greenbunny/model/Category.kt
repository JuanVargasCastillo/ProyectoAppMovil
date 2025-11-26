package com.miapp.greenbunny.model

import java.io.Serializable

data class Category(
    val id: Int,
    val nombre: String,
    val descripcion: String?
) : Serializable

data class CreateCategoryRequest(
    val nombre: String,
    val descripcion: String?
)

data class UpdateCategoryRequest(
    val nombre: String?,
    val descripcion: String?
)
