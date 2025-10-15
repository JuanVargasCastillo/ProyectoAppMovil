package com.miapp.greenbunny.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miapp.greenbunny.databinding.ItemProductBinding
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.ui.ProductDetailActivity

class ProductAdapter(private var products: List<Product> = emptyList()) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.binding.tvTitle.text = product.name
        holder.binding.tvDescription.text = product.description ?: ""
        holder.binding.tvPrice.text = product.price?.let { "Precio: $it" } ?: ""

        // Mostrar la primera imagen disponible de la lista images
        val imageUrl = product.images?.firstOrNull()?.url
        if (imageUrl != null) {
            holder.binding.imgProduct.load(imageUrl) {
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_dialog_alert)
            }
        } else {
            holder.binding.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_EXTRA", product)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}
