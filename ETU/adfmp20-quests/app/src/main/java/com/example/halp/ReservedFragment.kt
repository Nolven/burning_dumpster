package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_reserved_list.view.*

class ReservedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserved_list, container, false)

        updateList(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity as MainActivity
        if( act.user == null )
            view.findNavController().navigate(R.id.loadingFragment)
        view.reserved_list_recycler.adapter?.notifyDataSetChanged()
    }

    fun updateList(v: View) {
        val act = activity as MainActivity

        val rv: RecyclerView = v.reserved_list_recycler
        val rva = OrdersAdapter(act.user?.orders, act)

        rv.adapter = rva
        rv.layoutManager = LinearLayoutManager(context)
    }
}
