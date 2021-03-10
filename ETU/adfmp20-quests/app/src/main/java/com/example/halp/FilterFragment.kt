package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.navigation.findNavController
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import kotlinx.android.synthetic.main.fragment_filter.view.*


class FilterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filter, container, false)

        setup(view);

        view.findViewById<Button>(R.id.filter_search_button).setOnClickListener { v ->
            getFilter(view);
            v.findNavController().popBackStack()
        }

        view.filter_reset_button.setOnClickListener { v ->
            (activity as MainActivity).filterQuests = null
            v.findNavController().popBackStack()
        }

        return view
    }

    fun setupSeekbars(v: View)
    {
        /*v.filter_duration.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            v.filter_duration_max.setText(maxValue.toString())
            v.filter_duration_min.setText(minValue.toString())
        }*/

        v.filter_price.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            v.filter_price_max.setText(maxValue.toString())
            v.filter_price_min.setText(minValue.toString())
        }

        /*v.filter_people.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            v.filter_people_max.setText(maxValue.toString())
            v.filter_people_min.setText(minValue.toString())
        }*/
    }

    fun setupDropdowns(v: View)
    {
        val difficultyList = arrayOf("","Easy", "So-so", "Hard", "Nuts")
        val diffAdapter =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, difficultyList) }
        diffAdapter?.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        v.filter_difficulty.adapter = diffAdapter

        val genresList = arrayOf("","Touchy", "Notouchy", "Scary", "Boring")
        val genreAdapter =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, genresList) }
        genreAdapter?.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        v.filter_genre.adapter = genreAdapter
    }

    fun setup(v: View)
    {
        setupSeekbars(v);
        setupDropdowns(v);
    }

    fun getFilter(v: View)
    {
        (activity as MainActivity).filterQuests = QuestFilter(
           0,
            0,
            v.filter_price_min.text.toString().toInt(),
            v.filter_price_max.text.toString().toInt(),
            0,
           0,
            v.filter_difficulty.selectedItem.toString(),
            v.filter_genre.selectedItem.toString(),
            v.filter_name.text.toString()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
