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
        binding.btnAddToCart.setOnClickListener {
            val price = product.price
            if (price == null || price <= 0) {
                android.widget.Toast.makeText(requireContext(), "Precio no disponible", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (product.stock <= 0) {
                android.widget.Toast.makeText(requireContext(), "Sin stock", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cm = com.miapp.greenbunny.api.CartManager(requireContext())
            val imageUrl = product.images?.firstOrNull()?.url
            cm.addOrUpdate(product.id, product.name, price, 1, imageUrl)
            android.widget.Toast.makeText(requireContext(), "Agregado al carrito", android.widget.Toast.LENGTH_SHORT).show()
        }
        binding.btnGoCart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((view.parent as ViewGroup).id, com.miapp.greenbunny.ui.fragments.CartFragment())
                .addToBackStack(null)
                .commit()
        }
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

