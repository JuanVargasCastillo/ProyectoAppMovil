package com.miapp.greenbunny.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.DialogCreateUserBinding
import com.miapp.greenbunny.model.CreateUserRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateUserDialogFragment(private val onUserCreated: (() -> Unit)? = null) : DialogFragment() {

    private var _binding: DialogCreateUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogCreateUserBinding.inflate(LayoutInflater.from(requireContext()))
        setupSpinners()
        setupActions()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupSpinners() {
        val roles = listOf("admin", "user")
        val adapterRoles = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapterRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRole.adapter = adapterRoles
        binding.spRole.setSelection(1)

        val statuses = listOf("activo", "inactivo")
        val adapterStatus = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spStatus.adapter = adapterStatus
        binding.spStatus.setSelection(0)
    }

    private fun setupActions() {
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnCreate.setOnClickListener { submit() }
    }

    private fun submit() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val lastName = binding.etLastName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()
        val address = binding.etAddress.text?.toString()?.trim().orEmpty()
        val phone = binding.etPhone.text?.toString()?.trim().orEmpty()
        val role = binding.spRole.selectedItem?.toString()
        val status = binding.spStatus.selectedItem?.toString()

        if (name.isBlank()) {
            Toast.makeText(requireContext(), "Nombre requerido", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isBlank()) {
            Toast.makeText(requireContext(), "Email requerido", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(requireContext(), "Password mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progress.visibility = android.view.View.VISIBLE
        binding.btnCreate.isEnabled = false

        lifecycleScope.launch {
            try {
                val request = CreateUserRequest(
                    name = name,
                    lastName = lastName.ifBlank { null },
                    email = email,
                    password = password,
                    address = address.ifBlank { null },
                    phone = phone.ifBlank { null },
                    role = role,
                    status = status
                )
                val service = RetrofitClient.createAuthService(requireContext(), requiresAuth = false)
                withContext(Dispatchers.IO) { service.signup(request) }
                Toast.makeText(requireContext(), "Usuario creado", Toast.LENGTH_SHORT).show()
                onUserCreated?.invoke()
                dismiss()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnCreate.isEnabled = true
            } finally {
                binding.progress.visibility = android.view.View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
