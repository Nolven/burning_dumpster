package carcassonne.se.carcassonnecustomclone

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_change_name.*
import java.util.ArrayList

class ChangeNameDialog : DialogFragment() {
    var nameField: TextView? = null
    var player: PlayerInfo? = null
    var players: ArrayList<PlayerInfo>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.decorView?.setOnSystemUiVisibilityChangeListener {
            hideSystemUI(activity?.window)
        }
        okButton.setOnClickListener {
            if (newName.text.isNotEmpty()) {
                if (isNameUnique(newName.text.toString())) {
                    nameField?.text = newName.text
                    player?.name = newName.text.toString()
                    dismiss()
                } else {
                    Toast.makeText(context, "Name already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Name is empty", Toast.LENGTH_SHORT).show()
            }
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hideDialogSystemUI(dialog, activity)
        return inflater.inflate(R.layout.dialog_change_name, container)
    }

    override fun onStart() {
        super.onStart()
        newName.setText(nameField?.text)
    }


    //Проверяет уникально ли новое имя
    private fun isNameUnique(name: String): Boolean {
        val foundPlayer = players?.find { it.name.trim() == name.trim() } ?: return true
        if(foundPlayer == player) {
            return true
        }
        return false
    }
}