package com.example.halp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(val orders: ArrayList<Order>?, var act: MainActivity) :
    RecyclerView.Adapter<OrdersAdapter.OrderItemHolder>()
{
    class OrderItemHolder(view: View) :
        RecyclerView.ViewHolder(view)
    {
        var comment: TextView = view.findViewById(R.id.item_order_comment)
        var cost: TextView = view.findViewById(R.id.item_order_cost)
        var orderDate: TextView = view.findViewById(R.id.item_order_order_date)
        var people: TextView = view.findViewById(R.id.item_order_persons)
        var questDate: TextView = view.findViewById(R.id.item_order_quest_date)
        var quest: TextView = view.findViewById(R.id.item_order_quest)
        var status: TextView = view.findViewById(R.id.item_order_status)

        var payButton: Button = view.findViewById(R.id.item_order_pay_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderItemHolder(view)
    }

    override fun getItemCount(): Int {
        return orders?.size ?: 0
    }

    override fun onBindViewHolder(holder: OrderItemHolder, position: Int) {
        holder.comment.text = orders?.get(position)?.comment.toString();
        holder.cost.text = orders?.get(position)?.cost.toString()
        holder.people.text = orders?.get(position)?.people.toString()
        holder.orderDate.text = orders?.get(position)?.order_date.toString()
        holder.questDate.text = orders?.get(position)?.quest_date.toString()
        holder.quest.text = orders?.get(position)?.quest_id.toString() //TODO quest name
        holder.status.text = orders?.get(position)?.status.toString()

        if( orders?.get(position)?.status.toString() == "PAYED" )
            holder.payButton.visibility = View.GONE
        else
            holder.payButton.visibility = View.VISIBLE //TODO button to payment

        holder.payButton.setOnClickListener { v ->
            act.orderNum = position
            v.findNavController().navigate(R.id.paymentFragment)

        }
    }
}