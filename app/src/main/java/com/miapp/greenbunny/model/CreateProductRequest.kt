package com.miapp.greenbunny.model

data class CreateProductRequest(
    val name: String,
    val description: String?,
    val price: Int?,
    val stock: Int = 0,
    val status: String = "activo",
    val category_id: Int? = null,
    val images: List<ProductImage>? = emptyList()
)
