package com.miapp.greenbunny.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.miapp.greenbunny.model.CartItem

class CartManager(context: Context) {
    private val prefs = context.getSharedPreferences("cart", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getItems(): List<CartItem> {
        val json = prefs.getString(KEY_CART, null) ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveItems(items: List<CartItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString(KEY_CART, json).apply()
    }

    fun addOrUpdate(productId: Int, name: String, price: Int, quantity: Int, imageUrl: String?) {
        val current = getItems().toMutableList()
        val idx = current.indexOfFirst { it.productId == productId }
        if (idx >= 0) {
            val existing = current[idx]
            val updated = existing.copy(quantity = existing.quantity + quantity, name = name, price = price, imageUrl = imageUrl)
            current[idx] = updated
        } else {
            current.add(CartItem(productId, name, price, quantity, imageUrl))
        }
        saveItems(current)
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        val current = getItems().toMutableList()
        val idx = current.indexOfFirst { it.productId == productId }
        if (idx >= 0) {
            val updated = current[idx].copy(quantity = quantity)
            current[idx] = updated
            saveItems(current)
        }
    }

    fun remove(productId: Int) {
        val current = getItems().toMutableList()
        val filtered = current.filter { it.productId != productId }
        saveItems(filtered)
    }

    fun clear() {
        prefs.edit().remove(KEY_CART).apply()
    }

    fun getTotal(): Int {
        return getItems().sumOf { it.price * it.quantity }
    }

    companion object {
        private const val KEY_CART = "cart_items"
    }
}
