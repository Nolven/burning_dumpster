package carcassonne.se.carcassonnecustomclone

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_tiles.*

class TilesDialog : DialogFragment() {

    var tileArray: ArrayList<TileInfo> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        cancelButton.setOnClickListener {
            dismiss()
        }




        var numOfRows = tileArray.size / 4
        if (numOfRows % 4 != 0) {
            numOfRows++
        }


        for (i in 0 until numOfRows) {
            val tileRow = TableRow(activity)
            val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            tileRow.layoutParams = params
            val countRow = TableRow(activity)
            countRow.layoutParams = params
            for (j in 0 until 4) {
                if (i * 4 + j + 1 > tileArray.size) {
                    break
                }
                val tile = ImageView(activity)

                tile.setImageBitmap(tileArray[i * 4 + j].bitmap)
                val imageParams =
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                imageParams.width = resources.getDimension(R.dimen.remaining_tile_size).toInt()
                imageParams.height = resources.getDimension(R.dimen.remaining_tile_size).toInt()
                tile.layoutParams = imageParams
                tileRow.addView(tile)
                val count = TextView(activity, null, 0, R.style.PlayerName)
                count.textSize = resources.getDimension(R.dimen.small_text_size) / resources.displayMetrics.density
                count.text = "2/3"
                count.gravity = Gravity.CENTER_HORIZONTAL
                val countParams =
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                countParams.width = resources.getDimension(R.dimen.remaining_tile_size).toInt()
                countRow.addView(count)
            }
            tiles.addView(tileRow, params)
                //tiles.addView(countRow, params)

        }


//        for (i in 0 until tileResources.size) {
//            var tileDrawableId = 0
//            try {
//                tileDrawableId = resources.getIdentifier("tile${i + 1}", "drawable", context?.packageName)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//
//            var bitmap = BitmapFactory.decodeResource(resources, tileDrawableId)
//            for (j in 0 until tileResources[i][6].toString().toInt()) {
//                val tileSidesType = ArrayList<sideType>()
//                for (k in 0..5) {
//                    tileSidesType.add(sideType.values()[tileResources[i][k].toString().toInt()])
//                }
//            }
//        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hideDialogSystemUI(dialog, activity)
        return inflater.inflate(R.layout.dialog_tiles, container)
    }
}