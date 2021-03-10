package carcassonne.se.carcassonnecustomclone

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_exit.*

class GameExitDialog : DialogFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        noButton.setOnClickListener {
            dismiss()
        }
        yesButton.setOnClickListener {
            val openMainMenuActivity = Intent(activity, MainMenuActivity::class.java)
            openMainMenuActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            openMainMenuActivity.putExtra("EXIT", true)
            startActivity(openMainMenuActivity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hideDialogSystemUI(dialog, activity)
        return inflater.inflate(R.layout.dialog_game_exit, container)
    }

}