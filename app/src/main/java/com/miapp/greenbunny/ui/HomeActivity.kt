package com.miapp.greenbunny.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.miapp.greenbunny.api.TokenManager
import com.miapp.greenbunny.databinding.ActivityHomeBinding
import com.miapp.greenbunny.model.Product
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

        // Saludo
        binding.tvWelcome.text = "Hola ${tokenManager.getUserName()}!"

        // Rol actual
        val role = tokenManager.getUserRole()
        Log.d("HomeActivity", "Role en sesión: $role")
        Toast.makeText(this, "Role en sesión: $role", Toast.LENGTH_LONG).show()


        binding.bottomNav.menu.findItem(com.miapp.greenbunny.R.id.nav_users)?.isVisible = role == "admin"



        // Si venía un producto para editar → abrir AddProductFragment
        val productToEdit = intent.getSerializableExtra("PRODUCT_TO_EDIT") as? Product
        if (productToEdit != null) {
            val fragment = AddProductFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("PRODUCT_TO_EDIT", productToEdit)
                }
            }
            replaceFragment(fragment)
        } else {
            // Fragment inicial
            replaceFragment(ProductsFragment())
        }

        // Navegación inferior
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.miapp.greenbunny.R.id.nav_products ->
                    replaceFragment(ProductsFragment())

                com.miapp.greenbunny.R.id.nav_add ->
                    replaceFragment(AddProductFragment())

                com.miapp.greenbunny.R.id.nav_users -> {
                    Toast.makeText(this, "Sección de usuarios no disponible", Toast.LENGTH_SHORT).show()
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
