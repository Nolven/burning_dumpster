package com.example.halp

import com.google.firebase.Timestamp
import java.util.*

class Order {
    operator fun set(key: String, value: Any) {
        when(key)
        {
            "quest_id" -> quest_id = value as String
            "quest_date" -> quest_date = (value as Timestamp).toDate()
            "order_date" -> order_date = (value as Timestamp).toDate()
            "people" -> people = (value as Number).toInt()
            "cost" -> cost = (value as Number).toInt()
            "status" -> status = value as String
            "comment" -> comment = value as String
        }
    }

    var quest_id: String? = null
    var quest_date: Date? = null
    var order_date: Date? = null
    var people = 0
    var cost = 0
    var status: String? = null
    var comment: String? = null
}