package com.miapp.greenbunny.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.miapp.greenbunny.api.TokenManager
import com.miapp.greenbunny.databinding.FragmentProfileBinding
import com.miapp.greenbunny.ui.MainActivity

/**
 * ProfileFragment
 * Muestra los datos básicos del usuario logeado y permite cerrar sesión.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tm = TokenManager(requireContext())

        // Asignar nombre y email del usuario
        binding.tvName.text = tm.getUserName()
        binding.tvEmail.text = tm.getUserEmail()

        // Usar imagen de perfil local
        binding.ivProfile.setImageResource(com.miapp.greenbunny.R.drawable.ic_profile_placeholder)

        // Cerrar sesión
        binding.btnLogout.setOnClickListener {
            tm.clear()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
