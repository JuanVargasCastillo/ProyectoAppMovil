package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.FragmentUsersBinding
import com.miapp.greenbunny.model.User
import com.miapp.greenbunny.ui.adapter.UsersAdapter
import com.miapp.greenbunny.ui.fragments.CreateUserDialogFragment
import com.miapp.greenbunny.ui.fragments.UserEditDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UsersAdapter
    private var allUsers: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSearch()
        setupFab()
        loadUsers()
    }

    private fun setupRecycler() {
        adapter = UsersAdapter(
            onEdit = { user ->
                UserEditDialogFragment(user) { loadUsers() }
                    .show(parentFragmentManager, "UserEditDialog")
            },
            onToggleBlock = { user ->
                toggleBlock(user)
            }
        )
        binding.recyclerUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerUsers.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    private fun setupFab() {
        binding.fabAddUser.setOnClickListener {
            CreateUserDialogFragment {
                loadUsers()
            }.show(parentFragmentManager, "CreateUserDialog")
        }
    }

    private fun filter(query: String?) {
        val q = query?.trim()?.lowercase().orEmpty()
        val data = if (q.isBlank()) allUsers else allUsers.filter {
            (it.name ?: "").lowercase().contains(q) || (it.email ?: "").lowercase().contains(q)
        }
        adapter.updateData(data)
        binding.tvEmpty.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun loadUsers() {
        binding.progress.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val service = RetrofitClient.createUserService(requireContext())
                val users = withContext(Dispatchers.IO) { service.getUsers() }
                allUsers = users
                adapter.updateData(users)
                binding.tvEmpty.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                binding.tvError.visibility = View.VISIBLE
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
    }

    private fun toggleBlock(user: User) {
        binding.progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val service = RetrofitClient.createUserService(requireContext())
                val blockedStatuses = setOf("blocked", "bloqueado", "inactive", "inactivo")
                val isBlocked = user.status?.lowercase() in blockedStatuses
                withContext(Dispatchers.IO) {
                    if (isBlocked) service.unblockUser(user.id) else service.blockUser(user.id)
                }
                loadUsers()
            } catch (e: Exception) {
                binding.tvError.visibility = View.VISIBLE
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
