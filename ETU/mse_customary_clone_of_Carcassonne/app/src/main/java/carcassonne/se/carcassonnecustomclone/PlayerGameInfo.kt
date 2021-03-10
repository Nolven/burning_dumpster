package carcassonne.se.carcassonnecustomclone


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.view_player_game_info.view.*


@SuppressLint("ViewConstructor")
class PlayerGameInfo(context: Context, playerInfo: PlayerInfo) : ConstraintLayout(context) {

    private var coinCount: Int = 0
        set(value) {
            if (value >= 0) {
                field = value
                coinCounter.text = value.toString()
            }
        }

    var figurineCount: Int = 7
        set(value) {
            if (value in 0..7) {
                field = value
                figurineCounter.text = value.toString()
            }
        }



    init {
        View.inflate(context, R.layout.view_player_game_info, this)
        setBackgroundResource(R.drawable.game_player_info)
        (playerIcon.background as? GradientDrawable)?.setColor(playerInfo.color)
        DrawableCompat.setTint(figurineIcon.drawable, playerInfo.color)
        playerName.text = playerInfo.name
        coinCount = 0
        figurineCount = 7
        this.setOnClickListener {
            //Заглушка
        }
    }

    fun setCurrent(isChosen: Boolean) {
        if(isChosen) {
            setBackgroundResource(R.drawable.game_player_info_choosed)
        } else {
            setBackgroundResource(R.drawable.game_player_info)
        }
    }
}