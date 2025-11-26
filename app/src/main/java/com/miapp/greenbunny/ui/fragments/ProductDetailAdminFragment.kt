package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.databinding.FragmentProductDetailAdminBinding
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.ui.adapter.ImageSliderAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailAdminFragment : Fragment() {

    private var _binding: FragmentProductDetailAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductDetailAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = arguments?.getSerializable("PRODUCT_EXTRA") as? Product ?: return
        displayProduct(product)

        binding.btnEdit.setOnClickListener {
            val intent = android.content.Intent(requireContext(), com.miapp.greenbunny.ui.HomeActivityAdmin::class.java)
            intent.putExtra("PRODUCT_TO_EDIT", product)
            startActivity(intent)
        }

        binding.btnDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de eliminar este producto?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            val service = RetrofitClient.createProductService(requireContext())
                            withContext(Dispatchers.IO) { service.deleteProduct(product.id) }
                            Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                            activity?.finish()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnVolver.setOnClickListener { activity?.finish() }
    }

    private fun displayProduct(product: Product) {
        binding.tvProductName.text = "Nombre: ${product.name}"
        binding.tvProductPrice.text = product.price?.let { "Precio: $it" } ?: "Precio no disponible"
        binding.tvProductDescription.text = "Descripción: ${product.description ?: "Sin descripción"}"
        binding.tvProductStock.text = "Stock: ${product.stock}"
        product.images?.let { images ->
            val adapter = ImageSliderAdapter(images)
            binding.vpProductImages.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

