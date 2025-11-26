package com.miapp.greenbunny.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.miapp.greenbunny.api.TokenManager
import com.miapp.greenbunny.databinding.ActivityProductDetailHostBinding
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.ui.fragments.ProductDetailAdminFragment
import com.miapp.greenbunny.ui.fragments.ProductDetailClienteFragment

class ProductDetailHostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("PRODUCT_EXTRA", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("PRODUCT_EXTRA") as? Product
        } ?: return

        val tokenManager = TokenManager(this)
        val role = tokenManager.getUserRole()
        val fragment: Fragment = if (role == "admin") ProductDetailAdminFragment() else ProductDetailClienteFragment()
        fragment.arguments = android.os.Bundle().apply { putSerializable("PRODUCT_EXTRA", product) }

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}

