package com.example.halp

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AccountSettingsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_accout_settings, container, false)

        val activity = activity as MainActivity

        view.findViewById<Button>(R.id.account_settings_change_button).setOnClickListener { v ->
            val name = view.findViewById<TextInputEditText>(R.id.as_name).text.toString();
            val mail = view.findViewById<TextInputEditText>(R.id.as_mail).text.toString();
            val phone = view.findViewById<TextInputEditText>(R.id.as_phone).text.toString();
            val birthday = view.findViewById<TextInputEditText>(R.id.as_birthday).text.toString();
            val old_password = view.findViewById<TextInputEditText>(R.id.as_old_password).text.toString();
            val new_password = view.findViewById<TextInputEditText>(R.id.as_new_password).text.toString();

            val upd = hashMapOf<String, Any>() //update map
            var skip = false;

            if(name.isNotEmpty())
                upd["name"] = name
            if(mail.isNotEmpty())
                upd["mail"] = mail
            if(phone.isNotEmpty())
                upd["phone"] = phone
            if(birthday.isNotEmpty())
                try {
                    upd["birthday"] = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                }
                catch (t: Throwable)
                {
                    skip = true;
                }
            if(new_password.isNotEmpty())
                upd["new_password"] = new_password

            if( old_password == activity.user?.password  && !skip ) {
                if (mail.isNotEmpty()) {
                    activity.db.collection("users")
                        .whereEqualTo("mail", mail)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty)
                                activity.user?.id?.let {
                                    activity.db.collection("users")
                                        .document(it).update(upd)
                                }
                        }
                } else
                    activity.user?.id?.let {
                        activity.db.collection("users")
                            .document(it).update(upd)
                    }
            }

            v.findNavController().navigate(R.id.action_accountSettingsFragment_to_navigation_profile)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
