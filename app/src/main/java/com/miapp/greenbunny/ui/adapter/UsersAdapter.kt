package com.miapp.greenbunny.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.ItemUserBinding
import com.miapp.greenbunny.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersAdapter(
    private var users: List<User> = emptyList(),
    private val onEdit: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        with(holder.binding) {
            tvName.text = listOfNotNull(user.name, user.lastName).joinToString(" ")
            tvEmail.text = user.email ?: ""
            tvRole.text = "Rol: ${user.role ?: "-"}"
            tvStatus.text = "Estado: ${user.status ?: "-"}"

            btnEdit.setOnClickListener { onEdit(user) }

            // Botón Eliminar: confirmación + llamada DELETE + actualización local
            btnDelete.setOnClickListener {
                val context = holder.itemView.context
                AlertDialog.Builder(context)
                    .setTitle("Eliminar usuario")
                    .setMessage("¿Seguro que deseas eliminar este usuario?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val service = RetrofitClient.createUserService(context)
                                val userId = user.id
                                    ?: throw IllegalArgumentException("ID de usuario nulo")

                                // Llamada DELETE en hilo de IO
                                withContext(Dispatchers.IO) {
                                    service.deleteUser(userId)
                                }

                                Toast.makeText(
                                    context,
                                    "Usuario eliminado",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Actualizar lista local y notificar
                                val mutable = users.toMutableList()
                                val idx = holder.bindingAdapterPosition
                                if (idx != RecyclerView.NO_POSITION) {
                                    mutable.removeAt(idx)
                                    users = mutable
                                    notifyItemRemoved(idx)
                                } else {
                                    // Fallback por si el índice no es válido
                                    users = users.filter { it.id != user.id }
                                    notifyDataSetChanged()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error al eliminar: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}
