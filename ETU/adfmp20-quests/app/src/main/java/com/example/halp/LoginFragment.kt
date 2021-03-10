package com.example.halp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val activity = activity as MainActivity

        view.findViewById<Button>(R.id.login_login_button).setOnClickListener { v ->

            val login = view.findViewById<TextInputEditText>(R.id.login).text.toString();
            val password = view.findViewById<TextInputEditText>(R.id.password).text.toString();

            activity.db.collection("users")
               .whereEqualTo("password", password)
               .whereEqualTo("mail", login)
               .get().addOnSuccessListener { documents ->
                  if( !documents.isEmpty ) {
                      for( doc in documents ) {
                          val us: User = User()

                          us.id = doc.id
                          us.favourites = doc["favourites"] as ArrayList<String>
                          us.phone = doc["phone"].toString()
                          us.mail = doc["mail"].toString()
                          us.name = doc["name"].toString()
                          for( order in doc["orders"] as ArrayList<HashMap<String, Any>> )
                          {
                              val o = Order()
                              for( (key, value) in order )
                                  o[key] = value;
                              us.orders.add(o)
                          }
                          us.birthday = doc.getDate("birthday")
                          us.password = doc["password"].toString()

                          activity.user = us
                      }
                      v.findNavController().navigate(R.id.action_loginFragment_to_navigation_profile)
                  }
              }
        }

        view.findViewById<TextView>(R.id.login_password_recovery_label).setOnClickListener { v ->
            v.findNavController().navigate(R.id.action_loginFragment_to_passwordRestoreFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
