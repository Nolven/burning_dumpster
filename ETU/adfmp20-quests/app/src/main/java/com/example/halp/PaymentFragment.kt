package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class PaymentFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_payment_page, container, false)

        view.findViewById<Button>(R.id.payment_pay_button).setOnClickListener { v ->
            val act = activity as MainActivity
            act.user?.orders?.get(act.orderNum)?.status = "PAYED"
            act.user?.updateDB()
            act.orderNum = 1;
            v.findNavController().navigate(R.id.action_paymentFragment_to_navigation_orders)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
