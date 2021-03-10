package carcassonne.se.carcassonnecustomclone

import android.graphics.Bitmap
import android.graphics.Matrix

class TileInfo(patternBitmap: Bitmap, sideTypes: ArrayList<sideType>)
{
    var bitmap: Bitmap
    var sides: ArrayList<sideType>

    init
    {
        bitmap = patternBitmap
        sides = sideTypes
    }

    fun rotate() {
        val matrix = Matrix()
        matrix.preRotate(60f)
        //matrix.postScale(0.7f, 0.7f)
        val rotatedBitmap = Bitmap.createBitmap(
            this.bitmap, 0, 0,
            this.bitmap.width, this.bitmap.height, matrix, true
        )

        var lastSide = sides[5]
        for(i in 4 downTo 0)
        {
            sides[i+1] = sides[i]
        }
        sides[0] = lastSide
        //val x_Offset = (sideLen/2) * cos(30f)
        //val y_Offset = (sideLen/2) * cos(60f) //TODO сделать нормально, а не ущербно
        var deltaWidth = rotatedBitmap.width - bitmap.width
        var deltaHeight = rotatedBitmap.height - bitmap.height
        bitmap = Bitmap.createBitmap(rotatedBitmap, deltaWidth/2, deltaHeight/2, rotatedBitmap.width-deltaWidth, rotatedBitmap.height-deltaHeight)
        println("hello")
    }

}