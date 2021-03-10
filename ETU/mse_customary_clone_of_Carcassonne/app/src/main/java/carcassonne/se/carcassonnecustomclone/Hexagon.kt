package carcassonne.se.carcassonnecustomclone

import android.graphics.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.R.attr.angle

class Hexagon(X: Float, Y: Float, side: Float, roundColor: Int, patternBitmap: Bitmap, sideTypes: ArrayList<sideType>, choosed: Boolean) {
    var center: PointF
    var sideLen: Float
    private var mPath = Path()
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isChoosed: Boolean = false
    private var color: Int = 0
    private var selfColor: Int
    private var bitmap: Bitmap
    private var tapCounter: Int = 0
    private var realBitmap: Bitmap
    var tokenPlaced = false
    var tokenXCoord = 0f
    var tokenYCoord = 0f
    var sides: ArrayList<sideType>

    init {
        center = PointF(X, Y)
        sideLen = side
        selfColor = roundColor
        if (choosed)
            show()
        else
            hide()
        bitmap = patternBitmap
        mPath = getHexagonPath()
        realBitmap = bitmap
        sides = sideTypes

    }

    fun placeOnMap(tile: TileInfo) {
        show()
        setBitmap(tile.bitmap)
        sides = tile.sides
    }

    fun removeFromMap(defaultTile: TileInfo)
    {
        hide()
        setBitmap(defaultTile.bitmap)
        sides = defaultTile.sides
    }

    fun updateBitmap()
    {
        if(!isChosen())
            return
        realBitmap = Bitmap.createScaledBitmap(bitmap, (sideLen * sqrt(3f)).toInt(), (sideLen * 2).toInt(), false)
        bitmap = realBitmap
    }

    fun setBitmap(patternBitmap: Bitmap) {
        bitmap = patternBitmap
        updateBitmap()
    }

    fun isChosen(): Boolean {
        return isChoosed
    }

    fun hide() {
        isChoosed = false
        mPaint.color = Color.TRANSPARENT
    }

    fun show() {
        isChoosed = true
        mPaint.color = selfColor
    }

    fun setColor(newColor: Int) {
        mPaint.color = color
        selfColor = color

    }

    fun getColor(): Int {
        return mPaint.color
    }

    fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    private fun calcHexVertex(number: Int): PointF {
        var angleDeg = 60 * number + 30
        var angleRad = PI / 180 * angleDeg
        return PointF(
            (center.x + sideLen * cos(angleRad)).toFloat(),
            (center.y + sideLen * sin(angleRad).toFloat())
        )
    }

    fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    private fun getHexagonPath(): Path {
        mPath.reset()
        mPath.moveTo(center.x, center.y + sideLen)
        for (i in 0..5) {
            var currVertex = calcHexVertex(i + 1)
            mPath.lineTo(currVertex.x, currVertex.y)
        }
        mPath.lineTo(center.x, center.y + sideLen)
        mPath.close()
        updateBitmap()
        return mPath
    }

    fun onBoundsChange(bounds: Rect?) {
        // заглушечка
    }


    fun setAlpha(alpha: Int) {
        mPaint.setAlpha(alpha)
    }

    fun placeToken(x: Float, y: Float, color: Int)
    {
        tokenPlaced = true
        tokenXCoord = x
        tokenYCoord = y
        this.color = color
        setColor(color)
    }

    fun draw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
        if(this.isChosen())
        {
            canvas.drawBitmap(bitmap, center.x - (bitmap.width / 2), center.y - (bitmap.height / 2), mPaint)
            if(tokenPlaced)
                canvas.drawCircle(tokenXCoord, tokenYCoord, sideLen/5, mPaint)
        }
    }

    fun rotate() {
        val angle: Float = (60 * tapCounter).toFloat()
        val matrix = Matrix()
        matrix.postRotate(angle)
        val rotatedBitmap = Bitmap.createBitmap(
            this.realBitmap, 0, 0,
            this.realBitmap.width, this.realBitmap.height, matrix, true
        )
        bitmap = rotatedBitmap
    }

    fun setSideLength(length: Float) {
        sideLen = length
        getHexagonPath()
    }
}