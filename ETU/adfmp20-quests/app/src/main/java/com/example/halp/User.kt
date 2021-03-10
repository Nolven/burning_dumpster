package com.example.halp

import java.util.*
import com.google.firebase.firestore.FirebaseFirestore

class User {
    companion object{
        @JvmStatic val db: FirebaseFirestore = FirebaseFirestore.getInstance();
    }

    var id: String? = null
    var name: String? = null
    var mail: String? = null
    var phone: String? = null
    var password: String? = null
    var birthday: Date? = null
    var favourites: ArrayList<String> = ArrayList<String>()
    var orders: ArrayList<Order> = ArrayList<Order>()

    fun updateDB()
    {
        id?.let { db.collection("users").document(it).update(
            "name", name,
            "mail", mail,
            "phone", phone,
            "password", password,
            "birthday", birthday,
            "favourites", favourites,
            "orders", orders
        ) }
    }
}