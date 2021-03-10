package carcassonne.se.carcassonnecustomclone


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class PlayerInfo(var name: String, var color: Int) : Parcelable