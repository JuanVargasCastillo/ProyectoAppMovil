package com.miapp.greenbunny.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miapp.greenbunny.databinding.ItemUserBinding
import com.miapp.greenbunny.model.User

class UsersAdapter(
    private val onEdit: (User) -> Unit,
    private val onToggleBlock: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    private var users: List<User> = emptyList()

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvName.text = user.name ?: ""
        holder.binding.tvEmail.text = user.email ?: ""
        holder.binding.tvRole.text = user.role ?: ""
        holder.binding.tvStatus.text = user.status ?: ""

        val isBlocked = user.status?.lowercase() in setOf("blocked", "bloqueado", "inactive", "inactivo")
        holder.binding.btnBlock.text = if (isBlocked) "Desbloquear" else "Bloquear"

        holder.binding.btnEdit.setOnClickListener { onEdit(user) }
        holder.binding.btnBlock.setOnClickListener { onToggleBlock(user) }
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newList: List<User>) {
        users = newList
        notifyDataSetChanged()
    }
}
