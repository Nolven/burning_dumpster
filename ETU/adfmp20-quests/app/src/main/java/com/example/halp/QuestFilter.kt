package com.example.halp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class QuestFilter(val peopleMax: Int = 100,
                  val peopleMin: Int = 0,
                  val priceMin: Int = 0,
                  val priceMax: Int = 4000,
                  val durationMax: Int = 4000,
                  val durationMin: Int = 0,
                  val difficulty: String = "",
                  val genre: String = "",
                  val name: String = "") {
    companion object{
        @JvmStatic val db: FirebaseFirestore = FirebaseFirestore.getInstance();
    }

    fun applyFilter(): Query {
        var query = db.collection("quests")
            .whereLessThanOrEqualTo("cost", priceMax)
            .whereGreaterThanOrEqualTo("cost", priceMin)

            /*.whereLessThanOrEqualTo("duration", durationMax)
            .whereGreaterThanOrEqualTo("duration", durationMin)

            .whereLessThanOrEqualTo("max_people", peopleMax)
            .whereGreaterThanOrEqualTo("min_people", peopleMin)*/

        if( name != "" )
            query = query.whereEqualTo("name", name)

        if( difficulty != "" )
            query = query.whereEqualTo("complexity", difficulty)

        if( genre != "" )
            query = query.whereEqualTo("genre", genre)


        return query
    }
}