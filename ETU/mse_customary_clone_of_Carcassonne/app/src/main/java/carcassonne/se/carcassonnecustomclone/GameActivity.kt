package carcassonne.se.carcassonnecustomclone


import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import carcassonne.se.carcassonnecustomclone.zoom.ZoomLayout
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.math.abs
import kotlin.math.sqrt

class GameActivity : AppCompatActivity() {

    private var players: ArrayList<PlayerInfo>? = null
    private var currentPlayerIndex: Int = -1
    private var field: Canvass? =  null

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI(window)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI(window)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val canvass = Canvass(this)
        canvass.activity = this
        canvass.layoutParams = ViewGroup.LayoutParams(24000, 24000)
        this.field = canvass
        zoomLayout.addView(canvass)
        setButtonListeners()
        players = intent.getParcelableArrayListExtra("players")
        displayPlayers()
        nextPlayer()
        hideOkButton()
        hideDeclineButton()
        updateRemainingTilesButton(field?.tiles?.size ?: 0)
    }

    /*Добавляет игроков на панель игроков слева*/
    private fun displayPlayers() {
        players?.forEach {
            addPlayer(it)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        //TODO: я вот совсем не уверен что так делать хорошо
        showPauseDialog()
    }

    private fun addPlayer(playerInfo: PlayerInfo) {
        val player = PlayerGameInfo(this, playerInfo)
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val displayHeight = dm.heightPixels
        val margin = pxToDp((displayHeight - resources.getDimension(R.dimen.menu_button_height).toInt() * 6) / 7)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, (margin * 1.5).toInt(), 0, 0)
        player.layoutParams = params
        playerInfoArea.addView(player)
    }

    fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density + 0.5f).toInt()
    }

    private fun showPauseDialog() {
        val pauseDialog = PauseDialog()
        pauseDialog.show(supportFragmentManager, "PauseDialog")
    }

    fun setCurrentTile(bitmap: Bitmap) {
        currentTileView.setImageBitmap(bitmap)
    }

    class Canvass : View {
        var side__ = 200f
        private var zoomContainer: ZoomLayout? = null
        var hexagonesList = ArrayList<ArrayList<Hexagon>>(0)
        var shouldInit = true
        var tiles: ArrayList<TileInfo> = ArrayList()
        var defaultTile: TileInfo
        var startTile: TileInfo
        var currentTile: TileInfo
        var xTilesMax: Int = 0
        var yTilesMax: Int = 0
        var tilePlaced = false
        var currentCoords = Point(0,0)
        var activity: GameActivity? = null

        constructor(context: Context) : super(context) {
            val tileResources = resources.getStringArray(R.array.TilesInfo)

            for (i in 0 until tileResources.size) {
                var tileDrawableId = 0
                try {
                    tileDrawableId = resources.getIdentifier("tile${i + 1}", "drawable", context.packageName)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                var bitmap = BitmapFactory.decodeResource(resources, tileDrawableId)
                bitmap = Bitmap.createScaledBitmap(bitmap, (side__ * sqrt(3f)).toInt(), (side__ * 2).toInt(), false)
                for (j in 0 until tileResources[i][6].toString().toInt()) {
                    val tileSidesType = ArrayList<sideType>()
                    for (k in 0..5) {
                        tileSidesType.add(sideType.values()[tileResources[i][k].toString().toInt()])
                    }
                    tiles.add(TileInfo(bitmap, tileSidesType))
                }
            }

            val defaultSides = ArrayList<sideType>()
            for (i in 0..5) {
                defaultSides.add(sideType.EMPTY)
            }
            defaultTile = TileInfo(
                BitmapFactory.decodeResource(resources, R.drawable.default_tile),
                defaultSides
            )

            val startTileSides = ArrayList<sideType>()
            startTileSides.add(sideType.TOWN)
            startTileSides.add(sideType.ROAD)
            startTileSides.add(sideType.FIELD)
            startTileSides.add(sideType.TOWN)
            startTileSides.add(sideType.ROAD)
            startTileSides.add(sideType.FIELD)
            startTile = TileInfo(
                BitmapFactory.decodeResource(resources, R.drawable.start_tile),
                startTileSides
            )
            currentTile = getNextTile()




        }

        fun getNextTile(): TileInfo {
            if (tiles.size == 0)
                return defaultTile
            val rand = (Math.random() * tiles.size).toInt()
            val result = tiles[rand]
            tiles.removeAt(rand)
            return result
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            super.onLayout(changed, l, t, r, b)
            zoomContainer = parent as ZoomLayout
            if (shouldInit) {
                val ancho = width
                val alto = height
                var oddFlag = true
                var size = side__
                var widthScreenAlign = side__
                var heightScreenAlign = side__ + side__ * 1 / 5
                var center = PointF(widthScreenAlign, heightScreenAlign)

                var hexVertAlign = 3f / 2 * size
                var hexHorizAlign = (sqrt(3f) / 2) * size

                while ((center.y + hexVertAlign) < alto) {
                    hexagonesList.add(ArrayList(0))
                    while ((center.x + hexHorizAlign) < ancho) {

                        hexagonesList.last().add(
                            Hexagon(
                                center.x,
                                center.y,
                                size,
                                Color.argb(
                                    255, (Math.random() * 255).toInt(),
                                    (Math.random() * 255).toInt(), (Math.random() * 255).toInt()
                                ),
                                defaultTile.bitmap,
                                defaultTile.sides,
                                false
                            )
                        )
                        center.x += (hexHorizAlign * 2) + 4
                    }
                    if (oddFlag) {
                        oddFlag = false
                        center.x = widthScreenAlign + hexHorizAlign + 2
                    } else {
                        oddFlag = true
                        center.x = widthScreenAlign
                    }
                    center.y += hexVertAlign + 4

                }
                shouldInit = false
            }
            xTilesMax = hexagonesList[0].size - 1
            yTilesMax = hexagonesList.size - 1
            activity?.setCurrentTile(currentTile.bitmap)
            activity?.currentTileView?.setOnClickListener {
                currentTile.rotate()
                activity?.setCurrentTile(currentTile.bitmap)
            }

            activity?.okButton?.setOnClickListener {
                nextTurn()
            }

            activity?.declineButton?.setOnClickListener {
                cancelPlaceTile()
            }

            hexagonesList[yTilesMax/2][xTilesMax/2].placeOnMap(startTile)
        }

        fun getAdjacentHex(hexIndex: Point, sideId: Int): Point {
            var isOdd = false
            var result = Point()
            if (hexIndex.y % 2 == 0) {
                isOdd = true
            }

            if (sideId == 0) {
                result.x = hexIndex.x
                result.y = hexIndex.y - 1
            } else if (sideId == 1) {
                result.x = hexIndex.x + 1
                result.y = hexIndex.y
                return result
            } else if (sideId == 2) {
                result.x = hexIndex.x
                result.y = hexIndex.y + 1
            } else if (sideId == 3) {
                result.x = hexIndex.x - 1
                result.y = hexIndex.y + 1
            } else if (sideId == 4) {
                result.x = hexIndex.x - 1
                result.y = hexIndex.y
                return result
            } else if (sideId == 5) {
                result.x = hexIndex.x - 1
                result.y = hexIndex.y - 1
            }
            if (!isOdd) {
                result.x += 1
            }
            return result
        }

        fun checkCoordsOverflow(coords: Point): Boolean {
            var tmp = coords.x
            if (coords.y % 2 != 0) {
                tmp += 1
            }

            if (coords.x < 0 || coords.y < 0)
                return true


            if (tmp > xTilesMax || coords.y > yTilesMax)
                return true

            return false
        }

        fun getHexToPointDist(hexCenter: PointF, destPoint: PointF): Float {
            var deltaX = hexCenter.x - destPoint.x
            var deltaY = hexCenter.y - destPoint.y
            return (deltaX * deltaX) + (deltaY * deltaY)
        }

        fun getIndexHexOnTap(destPoint: PointF, radius: Float): Point {
            var result = Point(-1, -1)
            var bestDistance: Float = -1f
            for (i in 0..hexagonesList.size - 1) {
                for (j in 0..hexagonesList[i].size - 1) {

                    if (((hexagonesList[i][j].center.x - destPoint.x) >= abs(radius)) || ((hexagonesList[i][j].center.y - destPoint.y) >= abs(
                            radius
                        ))
                    )
                        continue

                    var res = getHexToPointDist(hexagonesList[i][j].center, destPoint)
                    if ((radius * radius >= res)) {
                        if (bestDistance == -1f) {
                            bestDistance = res
                            result.x = j
                            result.y = i
                        } else if (res < bestDistance) {
                            bestDistance = res
                            result.x = j
                            result.y = i
                            return result
                        }

                    }
                }
            }
            return result
        }

        private val MAX_CLICK_DURATION = 300
        private var timeDown = 0L

        fun nextTurn()
        {
            currentTile = getNextTile()
            tilePlaced = false
            activity?.setCurrentTile(currentTile.bitmap)
            activity?.hideOkButton()
            activity?.hideDeclineButton()
            activity?.nextPlayer()
            activity?.updateRemainingTilesButton(tiles.size)
        }

        fun cancelPlaceTile()
        {
            tilePlaced = false
            hexagonesList[currentCoords.y][currentCoords.x].removeFromMap(defaultTile)
            activity?.hideOkButton()
            activity?.hideDeclineButton()
            invalidate()
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    timeDown = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    if (System.currentTimeMillis() - timeDown < MAX_CLICK_DURATION) {
                        val shiftX = (zoomContainer?.panX ?: 0f)
                        val shiftY = (zoomContainer?.panY ?: 0f)
                        val zoom = (zoomContainer?.realZoom ?: 1f)
                        var res = getIndexHexOnTap(PointF(event.x / zoom - shiftX, event.y / zoom - shiftY), side__)
                        if(tilePlaced) {
                            if(res == currentCoords){
                                if(activity?.currentPlayerInfo()?.figurineCount == 0) {
                                    activity?.makeToast("You have no enough tokens for this")
                                    return true
                                }

                                hexagonesList[res.y][res.x].placeToken(event.x / zoom - shiftX, event.y / zoom - shiftY,
                                    activity?.players!![activity?.currentPlayerIndex!!].color)
                                activity?.currentPlayerInfo()?.figurineCount =
                                        activity?.currentPlayerInfo()?.figurineCount!!.minus(1)

                                nextTurn()

                            }
                            else
                            {
                                activity?.makeToast("Last placed tile must be selected")
                                return true
                            }
                        }
                        else if (res.x != -1) {
                            if (currentTile == defaultTile || hexagonesList[res.y][res.x].isChosen()) {
                                activity?.makeToast("Tile can be placed only on empty space")
                                return true
                            }

                            var emptyAroundCounter = 0
                            for (i in 0..5) {
                                var tmp = getAdjacentHex(res, i)
                                if (checkCoordsOverflow(tmp)) {
                                    emptyAroundCounter++
                                    continue
                                }

                                var currSide: sideType = currentTile.sides[i]
                                var adjacentSide: sideType = hexagonesList[tmp.y][tmp.x].sides[(i + 3) % 6]
                                if(adjacentSide == sideType.EMPTY)
                                    emptyAroundCounter++

                                if ((currSide != adjacentSide) && (adjacentSide != sideType.EMPTY)) {
                                    activity?.makeToast("Can't be placed here, check nearby tiles")
                                    return true
                                }

                            }

                            if(emptyAroundCounter==6) {
                                activity?.makeToast("Can't be placed here, there are no tiles nearby")
                                return true
                            }

                            hexagonesList[res.y][res.x].placeOnMap(currentTile)


                            currentCoords = res
                            tilePlaced = true
                            activity?.showOkButton()
                            activity?.showDeclineButton()




                        }
                        invalidate()
                    }
                }
            }
            return true
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))

            for (hexagonesString in hexagonesList) {
                for (elem in hexagonesString) {
                    elem.draw(canvas)
                }
            }
        }
    }

    private fun nextPlayer() {
        currentPlayerInfo()?.setCurrent(false)
        if (currentPlayerIndex != players?.size?.minus(1)) {
            currentPlayerIndex++
        } else {
            currentPlayerIndex = 0
        }
        currentPlayerInfo()?.setCurrent(true)
        makeToast("${players?.get(currentPlayerIndex)?.name} turn now")
    }

    fun currentPlayerInfo(): PlayerGameInfo? {
        return if (currentPlayerIndex in 0..(players?.size ?: 0)) {
            (playerInfoArea.getChildAt(currentPlayerIndex) as PlayerGameInfo)
        } else {
            null
        }
    }


    fun updateRemainingTilesButton(remTiles: Int) {
        remainingTiles.text = "$remTiles/88"
    }


    private fun showOkButton() {
        okButton.visibility = View.VISIBLE
    }

    private fun showDeclineButton() {
        declineButton.visibility = View.VISIBLE
    }

    private fun hideOkButton() {
        okButton.visibility = View.INVISIBLE
    }

    private fun hideDeclineButton() {
        declineButton.visibility = View.INVISIBLE
    }


    private fun setButtonListeners() {


        pauseButton.setOnClickListener {
            showPauseDialog()
        }

        remainingTiles.setOnClickListener {
            val tilesDialog = TilesDialog()
            if(field != null) {
                tilesDialog.tileArray = field!!.tiles
            }
            tilesDialog.show(supportFragmentManager, "TilesDialog")
        }
    }
}

