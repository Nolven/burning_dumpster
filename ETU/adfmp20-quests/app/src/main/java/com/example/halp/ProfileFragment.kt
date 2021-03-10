package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val act = activity as MainActivity

        view.findViewById<Button>(R.id.profile_account_setting_button).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_navigation_profile_to_accountSettingsFragment)
        }

       /* view.findViewById<Button>(R.id.profile_account_preferences_button).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_navigation_profile_to_preferencesFragment)
        }*/

        view.findViewById<Button>(R.id.profile_account_about_button).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_navigation_profile_to_aboutFragment)
        }

        view.findViewById<Button>(R.id.profile_account_logout_button).setOnClickListener { v ->
            act.user = null
            v.findNavController().navigate(R.id.action_navigation_profile_to_newUserFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity as MainActivity
        if( act.user == null )
            view.findNavController().navigate(R.id.newUserFragment)
    }
}
