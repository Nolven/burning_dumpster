package carcassonne.se.carcassonnecustomclone

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_players.*
import java.util.*


class PlayersActivity : AppCompatActivity() {

    private var players: ArrayList<PlayerInfo> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)
        addNewPlayer()
        addNewPlayer()
        setButtonListeners()
    }


    override fun onResume() {
        super.onResume()
        hideSystemUI(window)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI(window)
    }


    /*Устанавливает слушатели на кнопки меню*/
    private fun setButtonListeners() {
        playButton.setOnClickListener {
            val openGameActivity = Intent(this, LoadingActivity::class.java)
            openGameActivity.putParcelableArrayListExtra("players", players)
            startActivity(openGameActivity)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    /*Добавление нового игрока*/
    private fun addNewPlayer() {
        playerIcons.removeView(findViewById(R.id.addPlayerButton))
        players.add(PlayerInfo(getNewPlayerName(), getNewPlayerColor()))
        addPlayerViews(players.last())
        addAddButton()
    }

    //Цвет нового игрока
    private fun getNewPlayerColor(): Int {
        val colors = resources.getStringArray(R.array.PlayerColors)
        for(i in 0 until colors.size) {
            players.find {
                it.color == Color.parseColor(colors[i])
            } ?: return Color.parseColor(colors[i])
        }
        return Color.GRAY
    }

    //Имя нового игрока
    private fun getNewPlayerName(): String {
        for(i in 1..6) {
            val possibleName = "Player $i"
            players.find {
                it.name == possibleName
            } ?: return possibleName
        }
        return "Player 0"
    }



    /*Добавляет иконку и имя игрока в список*/
    private fun addPlayerViews(player: PlayerInfo) {
        val newPlayerIcon = ImageButton(this, null, 0, R.style.PlayerSelectIcon)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val margin = resources.getDimension(R.dimen.default_margin).toInt()
        params.setMargins(margin, margin, margin, margin)
        newPlayerIcon.layoutParams = params
        newPlayerIcon.setImageResource(R.drawable.ic_player)
        (newPlayerIcon.background as? GradientDrawable)?.setColor(player.color)

        val newPlayerName = TextView(this,null, 0, R.style.PlayerName)
        params.width = resources.getDimension(R.dimen.player_menu_button_size).toInt()
        newPlayerName.layoutParams = params
        newPlayerName.gravity = Gravity.CENTER_HORIZONTAL
        newPlayerName.text = player.name
        newPlayerName.textSize = resources.getDimension(R.dimen.small_text_size) / resources.displayMetrics.density
        newPlayerName.setOnClickListener {
            val changeNameDialog = ChangeNameDialog()
            changeNameDialog.nameField = newPlayerName
            changeNameDialog.player = player
            changeNameDialog.players = players
            changeNameDialog.show(supportFragmentManager, "ChangeNameDialog")
        }
        newPlayerIcon.setOnClickListener {
            if (players.size > 2) {
                players.remove(player)
                playerIcons.removeView(newPlayerIcon)
                playerNames.removeView(newPlayerName)
                if (players.size == 5) {
                    addAddButton()
                }
            }
        }
        playerIcons.addView(newPlayerIcon)
        playerNames.addView(newPlayerName)
    }


    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }

    /*Устанавливает кнопку добавления нового игрока*/
    private fun addAddButton() {
        if (players.size < 6) {
            val newAddButton = ImageButton(this, null, 0, R.style.PlayerSelectIcon)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val margin = resources.getDimension(R.dimen.default_margin).toInt()
            params.setMargins(margin, margin, margin, margin)
            params.width = resources.getDimension(R.dimen.player_menu_button_size).toInt()
            newAddButton.layoutParams = params
            newAddButton.setImageResource(R.drawable.ic_add)
            newAddButton.id = R.id.addPlayerButton
            (newAddButton.background as? GradientDrawable)?.setColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorMenuButton
                )
            )
            newAddButton.setOnClickListener {
                addNewPlayer()
            }
            playerIcons.addView(newAddButton)
        }
    }


}



