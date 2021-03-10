package com.example.halp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.include_quest_main_info.view.*

class QuestListAdapter(val quests: ArrayList<Quest>, var act: MainActivity) :
    RecyclerView.Adapter<QuestListAdapter.QuestItemHolder>()
{

    class QuestItemHolder(view: View) :
        RecyclerView.ViewHolder(view)
    {
        var company: TextView = view.findViewById(R.id.quest_item_company)
        var name: TextView = view.findViewById(R.id.quest_item_name)
        var people: TextView = view.findViewById(R.id.quest_item_people)
        var duration: TextView = view.findViewById(R.id.quest_item_duration)
        var complexity: TextView = view.findViewById(R.id.quest_item_complexity)
        var cost: TextView = view.findViewById(R.id.quest_item_cost)
        var like: Button = view.findViewById(R.id.quest_item_like)
        var genre: TextView = view.quest_item_genre

        var informationButton: Button = view.findViewById(R.id.quest_item_button)
        var image: ImageView = view.findViewById(R.id.quest_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestItemHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_quest, parent, false)
        return QuestItemHolder(view)
    }

    override fun getItemCount(): Int {
        return quests.size
    }

    override fun onBindViewHolder(holder: QuestItemHolder, position: Int) {
        holder.company.text = quests[position].company
        holder.name.text = quests[position].name
        holder.people.text = (quests[position].min_people.toString() + " - " + quests[position].max_people.toString())
        holder.complexity.text = quests[position].complexity
        holder.duration.text = (quests[position].duration.toString() + " min")
        holder.cost.text = quests[position].cost.toString()
        holder.genre.text = quests[position].genre

        holder.informationButton.setOnClickListener { v ->
            act.quest = quests[position]
            v.findNavController().navigate(R.id.questCardFragment)
        }

        if( act.user == null )
            holder.like.visibility = View.INVISIBLE;
        else
            {
                //Sets buttons color to red
                if( act.user!!.favourites.contains(quests[position].id) )
                    holder.like.setBackgroundColor(Color.RED)
                else
                    holder.like.setBackgroundResource(android.R.drawable.btn_default)


                holder.like.setOnClickListener {
                    if( act.user!!.favourites.contains(quests[position].id) )
                    {
                        act.user!!.favourites.remove(quests[position].id)
                        holder.like.setBackgroundResource(android.R.drawable.btn_default)
                    }
                    else
                    {
                        act.user!!.favourites.add(quests[position].id)
                        holder.like.setBackgroundColor(Color.RED)
                    }

                    act.user!!.updateDB();
                }
            }
        }
}