package com.miapp.greenbunny.ui

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.miapp.greenbunny.databinding.ActivityProductDetailBinding
import com.miapp.greenbunny.model.Product

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
        binding.tvProductStock.text = "Stock: ${product.stock ?: "No especificado"}"

        // Cargar la primera imagen en el ViewPager2 usando un ImageView temporal
        // Aquí creamos un pequeño truco: el ViewPager2 no puede usar Coil directamente,
        // así que simplemente reemplazamos su contenido por un ImageView con la imagen principal
        val firstImageUrl = product.images?.firstOrNull()?.url
        if (firstImageUrl != null) {
            val imageView = androidx.appcompat.widget.AppCompatImageView(this).apply {
                layoutParams = binding.imageViewPager.layoutParams
                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                load(firstImageUrl) {
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_dialog_alert)
                }
            }
            binding.imageViewPager.removeAllViews()
            binding.imageViewPager.addView(imageView)
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
