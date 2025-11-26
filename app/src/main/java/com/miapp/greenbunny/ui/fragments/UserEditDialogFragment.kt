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
import com.miapp.greenbunny.model.UpdateUserRequest
import com.miapp.greenbunny.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserEditDialogFragment(
    private val user: User,
    private val onUserUpdated: (() -> Unit)? = null
) : DialogFragment() {

    private var _binding: DialogCreateUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogCreateUserBinding.inflate(LayoutInflater.from(requireContext()))
        setupSpinners()
        populate()
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

        val statuses = listOf("activo", "inactivo")
        val adapterStatus = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spStatus.adapter = adapterStatus
    }

    private fun populate() {
        binding.etName.setText(user.name ?: "")
        binding.etLastName.setText(user.lastName ?: "")
        binding.etEmail.setText(user.email ?: "")
        binding.etPassword.setText("")
        binding.etAddress.setText(user.address ?: "")
        binding.etPhone.setText(user.phone ?: "")

        val roleIndex = listOf("admin", "user").indexOf(user.role?.lowercase() ?: "user").let { if (it >= 0) it else 1 }
        binding.spRole.setSelection(roleIndex)

        val statusMap = listOf("activo", "inactivo")
        val statusIndex = statusMap.indexOf(user.status?.lowercase() ?: "activo").let { if (it >= 0) it else 0 }
        binding.spStatus.setSelection(statusIndex)
    }

    private fun setupActions() {
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnCreate.text = "Guardar"
        binding.btnCreate.setOnClickListener { submit() }
    }

    private fun submit() {
        val name = binding.etName.text?.toString()?.trim()
        val lastName = binding.etLastName.text?.toString()?.trim()
        val email = binding.etEmail.text?.toString()?.trim()
        val address = binding.etAddress.text?.toString()?.trim()
        val phone = binding.etPhone.text?.toString()?.trim()
        val role = binding.spRole.selectedItem?.toString()
        val status = binding.spStatus.selectedItem?.toString()

        if (email.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Email requerido", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progress.visibility = android.view.View.VISIBLE
        binding.btnCreate.isEnabled = false

        lifecycleScope.launch {
            try {
                val request = UpdateUserRequest(
                    name = name?.takeIf { it.isNotBlank() },
                    lastName = lastName?.takeIf { it.isNotBlank() },
                    email = email,
                    address = address?.takeIf { it.isNotBlank() },
                    phone = phone?.takeIf { it.isNotBlank() },
                    role = role,
                    status = status
                )
                val service = RetrofitClient.createUserService(requireContext())
                withContext(Dispatchers.IO) { service.updateUser(user.id, request) }
                Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show()
                onUserUpdated?.invoke()
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
