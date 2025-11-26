package com.miapp.greenbunny.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miapp.greenbunny.databinding.ItemCartBinding
import com.miapp.greenbunny.model.CartItem

class CartAdapter(
    private var items: MutableList<CartItem>,
    private val onQuantityChange: (productId: Int, newQty: Int) -> Unit,
    private val onRemove: (productId: Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    inner class VH(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.ivImage.load(item.imageUrl)
        holder.binding.tvName.text = item.name
        holder.binding.tvUnitPrice.text = "Precio: ${item.price}"
        holder.binding.tvQuantity.text = item.quantity.toString()
        holder.binding.btnMinus.setOnClickListener {
            val newQty = (item.quantity - 1).coerceAtLeast(1)
            onQuantityChange(item.productId, newQty)
        }
        holder.binding.btnPlus.setOnClickListener {
            val newQty = item.quantity + 1
            onQuantityChange(item.productId, newQty)
        }
        holder.binding.btnRemove.setOnClickListener { onRemove(item.productId) }
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<CartItem>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }
}
