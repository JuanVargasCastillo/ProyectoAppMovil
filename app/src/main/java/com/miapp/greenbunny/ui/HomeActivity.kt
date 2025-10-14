package com.miapp.greenbunny.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.miapp.greenbunny.api.TokenManager
import com.miapp.greenbunny.databinding.ActivityHomeBinding
import com.miapp.greenbunny.ui.fragments.AddProductFragment
import com.miapp.greenbunny.ui.fragments.ProductsFragment
import com.miapp.greenbunny.ui.fragments.ProfileFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Mostramos saludo con el nombre del usuario
        binding.tvWelcome.text = "Hola ${tokenManager.getUserName()}!"

        // Cargamos inicialmente Productos
        replaceFragment(ProductsFragment())

        // Listener de BottomNavigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.miapp.greenbunny.R.id.nav_products -> replaceFragment(ProductsFragment())
                com.miapp.greenbunny.R.id.nav_add -> replaceFragment(AddProductFragment())
                com.miapp.greenbunny.R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    // Función para reemplazar fragmentos
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}
