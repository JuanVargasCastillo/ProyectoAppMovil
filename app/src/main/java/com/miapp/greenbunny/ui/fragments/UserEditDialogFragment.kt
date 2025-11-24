package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.miapp.greenbunny.api.UserService
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.FragmentUserEditBinding
import com.miapp.greenbunny.model.UpdateUserRequest
import com.miapp.greenbunny.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserEditDialogFragment : DialogFragment() {
    private var _binding: FragmentUserEditBinding? = null
    private val binding get() = _binding!!

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getSerializable("user") as? User
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
        prefill()
        binding.btnSave.setOnClickListener { save() }
        setupBlockToggle()
    }

    private fun setupSpinner() {
        val roles = listOf("admin", "client")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRole.adapter = adapter
    }

    private fun prefill() {
        val u = user ?: return
        binding.etName.setText(u.name)
        binding.etLastName.setText(u.lastName ?: "")
        binding.etEmail.setText(u.email)
        binding.etAddress.setText(u.address ?: "")
        binding.etPhone.setText(u.phone ?: "")
        val roleIndex = if (u.role == "admin") 0 else 1
        binding.spRole.setSelection(roleIndex)
    }

    private fun save() {
        val u = user ?: return
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        if (name.isBlank() || email.isBlank() || !email.contains("@")) {
            Toast.makeText(requireContext(), "Nombre y email válidos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        binding.progress.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        val req = UpdateUserRequest(
            name = name,
            lastName = binding.etLastName.text?.toString()?.trim().takeIf { !it.isNullOrBlank() },
            email = email,
            role = listOf("admin", "client")[binding.spRole.selectedItemPosition],
            address = binding.etAddress.text?.toString()?.trim().takeIf { !it.isNullOrBlank() },
            phone = binding.etPhone.text?.toString()?.trim().takeIf { !it.isNullOrBlank() }
        )

        lifecycleScope.launch {
            try {
                val service = RetrofitClient.createUserService(requireContext())
                val id = u.id ?: throw IllegalArgumentException("ID de usuario nulo")
                val updated = withContext(Dispatchers.IO) { service.updateUser(id, req) }
                Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show()
                parentFragmentManager.setFragmentResult("user_edit_result", Bundle())
                dismiss()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progress.visibility = View.GONE
                binding.btnSave.isEnabled = true
            }
        }
    }

    private fun setupBlockToggle() {
        val u = user ?: return
        updateBlockButtonText(u)

        binding.btnBlockToggle.setOnClickListener {
            val service = RetrofitClient.createUserService(requireContext())
            lifecycleScope.launch {
                try {
                    binding.progress.visibility = View.VISIBLE
                    val id = u.id ?: throw IllegalArgumentException("ID de usuario nulo")
                    val currentlyBlocked = isBlocked(u.status)
                    val updated = withContext(Dispatchers.IO) {
                        if (currentlyBlocked) service.unblockUser(id) else service.blockUser(id)
                    }
                    // Actualizar status local según requerimiento (español)
                    user = (user ?: updated).copy(status = if (currentlyBlocked) "activo" else "bloqueado")
                    updateBlockButtonText(user!!)
                    Toast.makeText(requireContext(), if (currentlyBlocked) "Usuario desbloqueado" else "Usuario bloqueado", Toast.LENGTH_SHORT).show()
                    // Notificar a UsersFragment
                    parentFragmentManager.setFragmentResult("user_edit_result", Bundle())
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    binding.progress.visibility = View.GONE
                }
            }
        }
    }

    private fun isBlocked(status: String?): Boolean = status == "blocked" || status == "bloqueado"

    private fun updateBlockButtonText(u: User) {
        val blocked = isBlocked(u.status)
        binding.btnBlockToggle.text = if (blocked) "Desbloquear usuario" else "Bloquear usuario"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(user: User): UserEditDialogFragment {
            val frag = UserEditDialogFragment()
            val args = Bundle()
            args.putSerializable("user", user)
            frag.arguments = args
            return frag
        }
    }
}
