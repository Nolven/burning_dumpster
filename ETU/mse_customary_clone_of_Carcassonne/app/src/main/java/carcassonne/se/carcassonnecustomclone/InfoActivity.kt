package carcassonne.se.carcassonnecustomclone

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        setButtonListeners()
        infoText.text = Html.fromHtml(resources.getString(R.string.info_text))

    }

    override fun onResume() {
        super.onResume()
        hideSystemUI(window)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI(window)
    }


    private fun setButtonListeners() {
        backButton.setOnClickListener {
            finish()
        }
    }
}
