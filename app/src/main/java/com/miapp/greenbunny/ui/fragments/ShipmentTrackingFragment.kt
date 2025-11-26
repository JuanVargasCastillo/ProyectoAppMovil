package com.miapp.greenbunny.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.miapp.greenbunny.databinding.FragmentShipmentTrackingBinding
import com.miapp.greenbunny.model.Shipment

class ShipmentTrackingFragment : Fragment() {
    private var _binding: FragmentShipmentTrackingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShipmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shipment = arguments?.getSerializable("SHIPMENT") as? Shipment ?: return
        binding.tvShipmentId.text = "Envío: ${shipment.id}"
        binding.tvShipmentAddress.text = "Dirección: ${shipment.address}"
        binding.tvShipmentStatus.text = "Estado: ${shipment.status}"
        binding.btnVolver.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(shipment: Shipment): ShipmentTrackingFragment {
            val f = ShipmentTrackingFragment()
            f.arguments = Bundle().apply { putSerializable("SHIPMENT", shipment) }
            return f
        }
    }
}
