package com.example.halp

import com.google.firebase.firestore.GeoPoint
import com.yandex.mapkit.geometry.Point

class Quest
{
    var id = ""
    var name = ""
    var description = ""
    var duration: Number = 0
    var min_people: Number = 0
    var max_people: Number = 0
    var company = ""
    var complexity = ""
    var genre = ""
    var coords: Point = Point(59.945933,30.320045)
    var address: String = ""
    var cost: Number = 0
    var phone: String = ""
    var img_url = ""
}