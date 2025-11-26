package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.miapp.greenbunny.databinding.FragmentProductDetailClienteBinding
import com.miapp.greenbunny.model.Product
import com.miapp.greenbunny.ui.adapter.ImageSliderAdapter

class ProductDetailClienteFragment : Fragment() {

    private var _binding: FragmentProductDetailClienteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductDetailClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = arguments?.getSerializable("PRODUCT_EXTRA") as? Product ?: return
        displayProduct(product)
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

