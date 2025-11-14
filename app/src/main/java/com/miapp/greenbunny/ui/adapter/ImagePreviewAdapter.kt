package com.miapp.greenbunny.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.miapp.greenbunny.databinding.ItemImagePreviewBinding

/**
 * ImagePreviewAdapter
 * Muestra imágenes por URI y permite eliminar con long press (callback opcional).
 */
class ImagePreviewAdapter(
    private val uris: List<Uri>,
    private val onRemove: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ivPreviewItem.load(uris[position])
        holder.binding.root.setOnLongClickListener {
            onRemove?.invoke(position)
            true
        }
    }

    override fun getItemCount(): Int = uris.size
}
