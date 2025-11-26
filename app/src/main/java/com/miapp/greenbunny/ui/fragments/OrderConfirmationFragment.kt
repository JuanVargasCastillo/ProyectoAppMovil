package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.miapp.greenbunny.databinding.FragmentOrderConfirmationBinding
import com.miapp.greenbunny.model.Order
import com.miapp.greenbunny.model.Shipment

class OrderConfirmationFragment : Fragment() {

    private var _binding: FragmentOrderConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = arguments?.getSerializable("ORDER") as? Order ?: return
        val shipment = arguments?.getSerializable("SHIPMENT") as? Shipment
        binding.tvOrderId.text = "Orden: ${order.id}"
        binding.tvOrderStatus.text = "Estado orden: ${order.status}"
        binding.tvOrderTotal.text = "Total: ${order.total}"
        if (shipment != null) {
            val addr = shipment.address ?: "-"
            val st = shipment.status ?: "-"
            binding.tvShipmentAddress.text = "Dirección: $addr"
            binding.tvShipmentStatus.text = "Estado envío: $st"
        } else {
            binding.tvShipmentAddress.text = "Dirección: -"
            binding.tvShipmentStatus.text = "Estado envío: -"
        }
        binding.btnVolver.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(order: Order, shipment: Shipment?): OrderConfirmationFragment {
            val f = OrderConfirmationFragment()
            f.arguments = Bundle().apply {
                putSerializable("ORDER", order)
                putSerializable("SHIPMENT", shipment)
            }
            return f
        }
    }
}
