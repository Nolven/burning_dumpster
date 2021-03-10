package carcassonne.se.carcassonnecustomclone

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_pause.*

class PauseDialog : DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //isCancelable = false
        resumeButton.setOnClickListener {
            dismiss()
        }

        rulesButton.setOnClickListener {
            val openRulesActivity = Intent(activity, RulesActivity::class.java)
            startActivity(openRulesActivity)
        }

        exitButton.setOnClickListener {
            val exitDialog = GameExitDialog()
            exitDialog.show(activity?.supportFragmentManager, "GameExitDialog")
        }

        settingsButton.setOnClickListener {
            val openSettingsActivity = Intent(activity, SettingsActivity::class.java)
            startActivity(openSettingsActivity)
        }

        infoButton.setOnClickListener {
            val openInfoActivity = Intent(activity, InfoActivity::class.java)
            startActivity(openInfoActivity)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hideDialogSystemUI(dialog, activity)
        return inflater.inflate(R.layout.dialog_pause, container)
    }
}