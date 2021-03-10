package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class BookingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)
        val act = activity as MainActivity

        view.findViewById<DatePicker>(R.id.booking_date_picker).minDate = System.currentTimeMillis() - 1000

        view.findViewById<Button>(R.id.booking_reserve_button).setOnClickListener { v ->
            addOrder(act) //adds order to local user object & updates db
            v.findNavController().navigate(R.id.successFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity as MainActivity

        fillFields(act);
    }

    fun fillFields(act: MainActivity)
    {
        view?.findViewById<TextView>(R.id.booking_price)?.text = act.quest?.cost.toString()
    }

    fun addOrder(act: MainActivity) {
        val o = Order()
        o.comment = view?.findViewById<TextInputEditText>(R.id.booking_comment)?.text.toString()
        o.people = view?.findViewById<TextInputEditText>(R.id.booking_people)?.text.toString().toInt()
        o.quest_id = act.quest?.id
        o.order_date = Date()
        val d = view?.findViewById<DatePicker>(R.id.booking_date_picker)
        if (d != null) {
            o.quest_date = Date(d.year, d.month, d.dayOfMonth)
        }
        o.cost = o.people * act.quest?.cost.toString().toInt()
        o.status = "Reserved"


        act.user?.orders?.add(o);
        act.orderNum = act.user?.orders?.size?.minus(1)!!
        act.user?.updateDB();
    }
}
