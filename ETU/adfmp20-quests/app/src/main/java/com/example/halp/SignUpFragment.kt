package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import java.sql.Timestamp
import java.util.*

class SignUpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        val activity = activity as MainActivity

        view.findViewById<Button>(R.id.signup_signup_button).setOnClickListener { v ->
            val name = view.findViewById<TextInputEditText>(R.id.signup_name).text.toString()
            val mail = view.findViewById<TextInputEditText>(R.id.signup_mail).text.toString()
            val phone = view.findViewById<TextInputEditText>(R.id.signup_phone).text.toString()
            val password = view.findViewById<TextInputEditText>(R.id.signup_password).text.toString()

            activity.db.collection("users").whereEqualTo("mail", mail)
                .get().addOnSuccessListener { docs ->
                    if (docs.isEmpty)
                    {
                        val user = hashMapOf(
                            "name" to name ,
                            "mail" to mail ,
                            "phone" to phone ,
                            "password" to password,
                            "favourites" to arrayListOf<String>(),
                            "orders" to arrayListOf<String>(),
                            "birthday" to Timestamp(0)
                        )

                        /*var us: User = User();

                        us.name = name;
                        us.mail = mail;
                        us.phone = phone;
                        us.password = password;
                        us.favourites = arrayListOf<String>()
                        us.orders = arrayListOf<Order>()

                        activity.user = us*/

                        activity.db.collection("users").add(user)/*.addOnSuccessListener { documentReference ->
                            activity.user?.id = documentReference.id;
                        }*/

                        v.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                    }
                }
        }

        view.findViewById<TextView>(R.id.signup_signin_button).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
