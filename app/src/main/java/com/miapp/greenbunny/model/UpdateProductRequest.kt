package com.miapp.greenbunny.model

/**
 * UpdateProductRequest
 * Payload para PATCH /product/{id} con campos opcionales.
 */
data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Int? = null,
    val stock: Int? = null,
    val status: String? = null,
    val category_id: Int? = null,
    val images: List<ProductImage>? = null
)