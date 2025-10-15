package com.miapp.greenbunny.ui

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.miapp.greenbunny.databinding.ActivityProductDetailBinding
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.ui.adapter.ImageSliderAdapter

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val product = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("PRODUCT_EXTRA", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("PRODUCT_EXTRA") as? Product
        }

        product?.let { displayProduct(it) }

        binding.btnVolver.setOnClickListener { finish() }
    }

    private fun displayProduct(product: Product) {
        title = product.name
        binding.tvProductName.text = "Nombre: ${product.name}"
        binding.tvProductPrice.text = product.price?.let { "Precio: $it" } ?: "Precio no disponible"
        binding.tvProductDescription.text = "Descripción: ${product.description ?: "Sin descripción"}"
        binding.tvProductStock.text = "Stock: ${product.stock}"

        // Configurar carrusel de imágenes
        product.images?.let { images ->
            val adapter = ImageSliderAdapter(images)
            binding.vpProductImages.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
