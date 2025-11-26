package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.greenbunny.api.RetrofitClient
import com.miapp.greenbunny.api.ShipmentService
import com.miapp.greenbunny.databinding.FragmentAdminOrdersBinding
import com.miapp.greenbunny.model.Order
import com.miapp.greenbunny.model.UpdateOrderRequest
import com.miapp.greenbunny.model.UpdateShipmentRequest
import com.miapp.greenbunny.ui.adapter.AdminOrdersAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminOrdersFragment : Fragment() {

    private var _binding: FragmentAdminOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdminOrdersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdminOrdersAdapter(mutableListOf(), ::acceptOrder, ::rejectOrder)
        binding.rvOrders.adapter = adapter
        binding.rvOrders.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        loadPending()
    }

    private fun loadPending() {
        lifecycleScope.launch {
            try {
                val orderService = RetrofitClient.createOrderService(requireContext())
                val orders = withContext(Dispatchers.IO) { orderService.getOrders() }
                val pending = orders.filter { normalizeStatus(it.status) == "pendiente" }
                    .sortedByDescending { it.created_at ?: 0L }
                adapter.setData(pending)
                val isEmpty = pending.isEmpty()
                binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
                binding.rvOrders.visibility = if (isEmpty) View.GONE else View.VISIBLE
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando órdenes: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                
            }
        }
    }

    private fun normalizeStatus(status: String?): String {
        val s = status?.trim()?.lowercase() ?: ""
        return when (s) {
            "pending" -> "pendiente"
            "paid" -> "pagado"
            "rejected" -> "rechazado"
            else -> s
        }
    }

    private fun acceptOrder(order: Order) {
        lifecycleScope.launch {
            try {
                val orderService = RetrofitClient.createOrderService(requireContext())
                val shipmentService: ShipmentService = RetrofitClient.createShipmentService(requireContext())
                withContext(Dispatchers.IO) { orderService.updateOrder(order.id, UpdateOrderRequest(status = "pagado")) }
                val shipments = withContext(Dispatchers.IO) { shipmentService.getShipments() }
                val shipment = shipments.firstOrNull { it.order_id == order.id }
                if (shipment != null) {
                    withContext(Dispatchers.IO) { shipmentService.updateShipment(shipment.id, UpdateShipmentRequest(status = "enviado")) }
                }
                Toast.makeText(requireContext(), "Orden ${order.id} aceptada", Toast.LENGTH_SHORT).show()
                loadPending()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error aceptando orden: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun rejectOrder(order: Order) {
        lifecycleScope.launch {
            try {
                val orderService = RetrofitClient.createOrderService(requireContext())
                withContext(Dispatchers.IO) { orderService.updateOrder(order.id, UpdateOrderRequest(status = "rechazado")) }
                Toast.makeText(requireContext(), "Orden ${order.id} rechazada", Toast.LENGTH_SHORT).show()
                loadPending()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error rechazando orden: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
