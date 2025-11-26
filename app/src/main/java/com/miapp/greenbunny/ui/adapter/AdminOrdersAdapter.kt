package com.miapp.greenbunny.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miapp.greenbunny.databinding.ItemAdminOrderBinding
import com.miapp.greenbunny.model.Order

class AdminOrdersAdapter(
    private var items: MutableList<Order>,
    private val onAccept: (Order) -> Unit,
    private val onReject: (Order) -> Unit
) : RecyclerView.Adapter<AdminOrdersAdapter.VH>() {

    inner class VH(val binding: ItemAdminOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAdminOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val order = items[position]
        holder.binding.tvOrderId.text = "Orden #${order.id}"
        holder.binding.tvOrderDate.text = "Fecha: ${order.created_at ?: 0}"
        holder.binding.tvOrderTotal.text = "Total: ${order.total}"
        holder.binding.tvOrderStatus.text = "Estado: ${normalizeStatus(order.status)}"
        holder.binding.btnAccept.setOnClickListener { onAccept(order) }
        holder.binding.btnReject.setOnClickListener { onReject(order) }
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<Order>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    private fun normalizeStatus(status: String?): String {
        val s = status?.trim()?.lowercase() ?: ""
        return when (s) {
            "pending" -> "pendiente"
            "paid" -> "pagado"
            "rejected" -> "rechazado"
            else -> s
        }
    }
}
