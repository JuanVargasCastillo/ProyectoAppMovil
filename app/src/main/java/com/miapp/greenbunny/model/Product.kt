package com.miapp.greenbunny.model

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Int?,
    val stock: Int,
    val brand: String? = null,
    val category: String? = null,
    val images: List<ProductImage>? = emptyList()
) : Serializable
