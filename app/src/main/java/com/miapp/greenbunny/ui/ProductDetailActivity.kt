package com.miapp.greenbunny.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AlertDialog
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.ActivityProductDetailBinding
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.ui.adapter.ImageSliderAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        product?.let { prod ->
            displayProduct(prod)

            // Editar: navega a HomeActivity con el producto a editar
            binding.btnEdit.setOnClickListener {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("PRODUCT_TO_EDIT", prod)
                startActivity(intent)
            }

            // Eliminar: confirma y llama DELETE
            binding.btnDelete.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Eliminar producto")
                    .setMessage("¿Estás seguro de eliminar este producto?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        lifecycleScope.launch {
                            try {
                                val service = RetrofitClient.createProductService(this@ProductDetailActivity)
                                withContext(Dispatchers.IO) { service.deleteProduct(prod.id) }
                                Toast.makeText(this@ProductDetailActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
                                finish()
                            } catch (e: Exception) {
                                Toast.makeText(this@ProductDetailActivity, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

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
