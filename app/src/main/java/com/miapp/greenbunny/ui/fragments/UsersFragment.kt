package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.api.UserService
import com.miapp.greenbunny.databinding.FragmentUsersBinding
import com.miapp.greenbunny.model.User
import com.miapp.greenbunny.ui.adapter.UsersAdapter
import kotlinx.coroutines.*
import retrofit2.HttpException

class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UsersAdapter
    private lateinit var service: UserService
    private var allUsers: List<User> = emptyList()
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        service = RetrofitClient.createUserService(requireContext())
        setupRecycler()
        setupSearch()
        setupRetry()
        setupEditResultListener()
        loadUsers()
    }

    private fun setupRecycler() {
        adapter = UsersAdapter(
            onEdit = { user ->
                val dialog = UserEditDialogFragment.newInstance(user)
                dialog.show(parentFragmentManager, "edit_user")
            }
        )
        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsers.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchUsers.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString()?.trim().orEmpty()
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(300)
                performSearch(query)
            }
        }
    }

    private fun setupRetry() {
        binding.btnRetry.setOnClickListener { loadUsers() }
    }

    private fun setupEditResultListener() {
        // Escucha resultados desde el diálogo de edición
        parentFragmentManager.setFragmentResultListener("user_edit_result", viewLifecycleOwner) { _, _ ->
            loadUsers()
        }
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            try {
                showLoading(true)
                val result = withContext(Dispatchers.IO) {
                    service.getUsers(if (query.isBlank()) null else query)
                }
                allUsers = result
                adapter.updateData(result)
                showStatesForData(result)
            } catch (e: Exception) {
                val msg = buildErrorMessage(e)
                Log.e("UsersFragment", "Error al buscar usuarios: $msg", e)
                showError(msg)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                val users = withContext(Dispatchers.IO) { service.getUsers(null) }
                allUsers = users
                adapter.updateData(users)
                showStatesForData(users)
            } catch (e: Exception) {
                val msg = buildErrorMessage(e)
                Log.e("UsersFragment", "Error al cargar usuarios: $msg", e)
                showError(msg)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showStatesForData(data: List<User>) {
        binding.tvEmpty.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
        binding.viewError.visibility = View.GONE
        binding.recyclerUsers.visibility = if (data.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun showLoading(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String?) {
        binding.viewError.visibility = View.VISIBLE
        // El primer hijo de viewError es el TextView del mensaje
        val tv = binding.viewError.getChildAt(0)
        if (tv is android.widget.TextView) {
            tv.text = message ?: "Ocurrió un error"
        }
        binding.recyclerUsers.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
    }

    private fun buildErrorMessage(e: Exception): String {
        return if (e is HttpException) {
            val code = e.code()
            val body = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
            "Error al cargar usuarios: $code ${body ?: e.message()}"
        } else {
            "Error al cargar usuarios: ${e.message}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
