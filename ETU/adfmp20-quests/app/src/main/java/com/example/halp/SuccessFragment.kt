package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class SuccessFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_success, container, false)

        view.findViewById<Button>(R.id.success_booking_to_orders_button).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_successFragment_to_navigation_orders)
        }

        view.findViewById<Button>(R.id.success_booking_to_pay_button).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_successFragment_to_paymentFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
