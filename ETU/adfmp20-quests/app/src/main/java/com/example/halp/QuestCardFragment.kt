package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_quest_card.*

class QuestCardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quest_card, container, false)

        view.findViewById<Button>(R.id.quest_card_booking_button).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_questCardFragment_to_bookingFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity as MainActivity

        if( act.user == null )
            view.findViewById<Button>(R.id.quest_card_booking_button).visibility = View.INVISIBLE;

        setFields(act.quest, view);

        quest_card_map_button.setOnClickListener { v ->
            //val coord = act.quest.coords
            v.findNavController().navigate(R.id.navigation_map)
        }
    }

    fun setFields(q: Quest?, v: View){
        var company: TextView = v.findViewById(R.id.quest_item_company)
        var name: TextView = v.findViewById(R.id.quest_item_name)
        var people: TextView = v.findViewById(R.id.quest_item_people)
        var duration: TextView = v.findViewById(R.id.quest_item_duration)
        var complexity: TextView = v.findViewById(R.id.quest_item_complexity)
        //var cost: TextView = v.findViewById(R.id.quest_item_cost)
        var description: TextView = v.findViewById(R.id.quest_card_description)
        var phone: TextView = v.findViewById(R.id.quest_card_phone)
        var address: TextView = v.findViewById(R.id.quest_card_address)

        if (q != null) {
            company.setText(q.company)
        }
        if (q != null) {
            name.setText(q.name)
        }
        if (q != null) {
            people.setText((q.min_people.toString() + " - " + q.max_people.toString()))
        }
        if (q != null) {
            duration.setText(q.duration.toString())
        }
        if (q != null) {
            complexity.setText(q.complexity)
        }
        /*if (q != null) {
            cost.setText(q.cost.toString())
        }*/
        if (q != null) {
            description.setText(q.description)
        }
        if (q != null) {
            phone.setText(q.phone)
        }
        if (q != null) {
            address.setText(q.address)
        }



    }
}
