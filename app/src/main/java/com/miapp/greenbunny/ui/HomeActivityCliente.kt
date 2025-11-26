package com.miapp.greenbunny.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.miapp.greenbunny.api.TokenManager
import com.miapp.greenbunny.databinding.ActivityHomeClienteBinding
import com.miapp.greenbunny.ui.fragments.ProductsFragment
import com.miapp.greenbunny.ui.fragments.ProfileFragment

class HomeActivityCliente : AppCompatActivity() {

    private lateinit var binding: ActivityHomeClienteBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        binding.tvWelcome.text = "Hola ${tokenManager.getUserName()}!"

        replaceFragment(ProductsFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.miapp.greenbunny.R.id.nav_catalogo ->
                    replaceFragment(ProductsFragment())

                com.miapp.greenbunny.R.id.nav_carrito -> {
                    replaceFragment(com.miapp.greenbunny.ui.fragments.CartFragment())
                }

                com.miapp.greenbunny.R.id.nav_profile ->
                    replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}

