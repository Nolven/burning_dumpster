package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class NewUserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_newuser, container, false)

        view.findViewById<Button>(R.id.newuser_signin).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_newUserFragment_to_loginFragment)
        }

        view.findViewById<Button>(R.id.newuser_signup).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_newUserFragment_to_signUpFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
