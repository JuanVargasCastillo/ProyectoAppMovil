package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.greenbunny.api.CartManager
import com.miapp.greenbunny.api.CheckoutCoordinator
import com.miapp.greenbunny.databinding.FragmentCartBinding
import com.miapp.greenbunny.model.CartItem
import com.miapp.greenbunny.ui.adapter.CartAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartManager: CartManager
    private lateinit var adapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartManager = CartManager(requireContext())
        adapter = CartAdapter(cartManager.getItems().toMutableList(), ::onQuantityChange, ::onRemove)
        binding.rvCart.adapter = adapter
        refresh()

        binding.btnCheckout.setOnClickListener {
            val address = binding.etAddress.text?.toString()?.trim().orEmpty()
            if (address.isBlank()) {
                Toast.makeText(requireContext(), "Ingresa dirección de envío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val items = cartManager.getItems()
            if (items.isEmpty()) {
                Toast.makeText(requireContext(), "Carrito vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.progress.visibility = View.VISIBLE
            lifecycleScope.launch {
                try {
                    // Validar precios actuales contra catálogo para evitar desfasajes
                    val productService = com.miapp.greenbunny.api.RetrofitClient.createProductService(requireContext())
                    val catalog = withContext(Dispatchers.IO) { productService.getProducts() }
                    val updatedItems = items.map { ci ->
                        val p = catalog.firstOrNull { it.id == ci.productId }
                        val price = p?.price ?: ci.price
                        CartItem(ci.productId, ci.name, price, ci.quantity, ci.imageUrl)
                    }
                    // Persistir precios actualizados
                    updatedItems.forEach {
                        val existingQty = cartManager.getItems().firstOrNull { c -> c.productId == it.productId }?.quantity ?: 0
                        val delta = it.quantity - existingQty
                        if (delta != 0) {
                            cartManager.addOrUpdate(it.productId, it.name, it.price, delta, it.imageUrl)
                        } else {
                            // Si solo cambió el precio y no la cantidad, forzar actualización
                            cartManager.addOrUpdate(it.productId, it.name, it.price, 0, it.imageUrl)
                        }
                    }
                    val coordinator = CheckoutCoordinator(requireContext())
                    val (order, shipment) = withContext(Dispatchers.IO) {
                        coordinator.checkout(cartManager.getItems(), address, acceptPayment = true)
                    }
                    cartManager.clear()
                    Toast.makeText(requireContext(), "Orden ${order.id} ${order.status}", Toast.LENGTH_LONG).show()
                    parentFragmentManager.beginTransaction()
                        .replace((view.parent as ViewGroup).id, com.miapp.greenbunny.ui.fragments.OrderConfirmationFragment.newInstance(order, shipment))
                        .addToBackStack(null)
                        .commit()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error en checkout: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    binding.progress.visibility = View.GONE
                }
            }
        }
    }

    private fun refresh() {
        val items = cartManager.getItems()
        adapter.setData(items)
        binding.tvTotal.text = "Total: ${cartManager.getTotal()}"
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun onQuantityChange(productId: Int, newQty: Int) {
        cartManager.updateQuantity(productId, newQty)
        refresh()
    }

    private fun onRemove(productId: Int) {
        cartManager.remove(productId)
        refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
