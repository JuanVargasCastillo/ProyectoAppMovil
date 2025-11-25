package com.miapp.greenbunny.model

data class UpdateProductRequest(
    val name: String,
    val description: String?,
    val price: Int?,
    val stock: Int = 0,
    val status: String? = null,
    val category_id: Int? = null,
    val images: List<ProductImage>? = null
)
