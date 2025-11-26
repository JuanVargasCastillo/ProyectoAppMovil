package com.miapp.greenbunny.model

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String?
)
