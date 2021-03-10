package carcassonne.se.carcassonnecustomclone.zoom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Matrix
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import carcassonne.se.carcassonnecustomclone.R
import carcassonne.se.carcassonnecustomclone.zoom.ZoomApi.Companion.TRANSFORMATION_CENTER_INSIDE
import carcassonne.se.carcassonnecustomclone.zoom.ZoomApi.Companion.TYPE_ZOOM


/**
 * Uses [ZoomEngine] to allow zooming and pan events onto a view hierarchy.
 * The hierarchy must be contained in a single view, added to this layout
 * (like what you do with a ScrollView).
 *
 *
 * If the hierarchy has clickable children that should react to touch events, you are
 * required to call [.setHasClickableChildren] or use the attribute.
 * This is off by default because it is more expensive in terms of performance.
 *
 *
 * Currently padding to this view / margins to the child view are NOT supported.
 *
 *
 * TODO: support padding (from inside ZoomEngine that gets the view)
 * TODO: support layout_margin (here)
 */
class ZoomLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ZoomEngine.Listener, ZoomApi {

    /**
     * Gets the backing [ZoomEngine] so you can access its APIs.
     *
     * @return the backing engine
     */
    val engine: ZoomEngine
    private val mMatrix = Matrix()
    private val mMatrixValues = FloatArray(9)
    private var mHasClickableChildren: Boolean = false

    /**
     * Gets the current zoom value, which can be used as a reference when calling
     * [.zoomTo] or [.zoomBy].
     *
     *
     * This can be different than the actual scale you get in the matrix, because at startup
     * we apply a base zoom to respect the "center inside" policy.
     * All zoom calls, including min zoom and max zoom, refer to this axis, where zoom is set to 1
     * right after the initial transformation.
     *
     * @return the current zoom
     * @see .getRealZoom
     */
    override var zoom: Float = 0.0f
        get() = engine.zoom

    /**
     * Gets the current zoom value, including the base zoom that was eventually applied when
     * initializing to respect the "center inside" policy. This will match the scaleX - scaleY
     * values you get into the [Matrix], and is the actual scale value of the content
     * from its original size.
     *
     * @return the real zoom
     */
    override val realZoom: Float
        get() = engine.realZoom

    /**
     * Returns the current horizontal pan value, in content coordinates
     * (that is, as if there was no zoom at all).
     *
     * @return the current horizontal pan
     */
    override val panX: Float
        get() = engine.panX

    /**
     * Returns the current vertical pan value, in content coordinates
     * (that is, as if there was no zoom at all).
     *
     * @return the current vertical pan
     */
    override val panY: Float
        get() = engine.panY

    init {

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ZoomEngine, defStyleAttr, 0)
        val overScrollHorizontal = a.getBoolean(R.styleable.ZoomEngine_overScrollHorizontal, true)
        val overScrollVertical = a.getBoolean(R.styleable.ZoomEngine_overScrollVertical, true)
        val horizontalPanEnabled = a.getBoolean(R.styleable.ZoomEngine_horizontalPanEnabled, true)
        val verticalPanEnabled = a.getBoolean(R.styleable.ZoomEngine_verticalPanEnabled, true)
        val overPinchable = a.getBoolean(R.styleable.ZoomEngine_overPinchable, true)
        val zoomEnabled = a.getBoolean(R.styleable.ZoomEngine_zoomEnabled, true)
        val hasChildren = a.getBoolean(R.styleable.ZoomEngine_hasClickableChildren, false)
        val minZoom = a.getFloat(R.styleable.ZoomEngine_minZoom, -1f)
        val maxZoom = a.getFloat(R.styleable.ZoomEngine_maxZoom, -1f)
        @ZoomApi.ZoomType val minZoomMode = a.getInteger(R.styleable.ZoomEngine_minZoomType, TYPE_ZOOM)
        @ZoomApi.ZoomType val maxZoomMode = a.getInteger(R.styleable.ZoomEngine_maxZoomType, TYPE_ZOOM)
        val transformation = a.getInteger(R.styleable.ZoomEngine_transformation, TRANSFORMATION_CENTER_INSIDE)
        val transformationGravity = a.getInt(R.styleable.ZoomEngine_transformationGravity, Gravity.CENTER)
        val animationDuration =
            a.getInt(R.styleable.ZoomEngine_animationDuration, ZoomEngine.DEFAULT_ANIMATION_DURATION.toInt()).toLong()
        a.recycle()

        engine = ZoomEngine(context, this)
        engine.addListener(this)
        setTransformation(transformation, transformationGravity)
        setOverScrollHorizontal(overScrollHorizontal)
        setOverScrollVertical(overScrollVertical)
        setHorizontalPanEnabled(horizontalPanEnabled)
        setVerticalPanEnabled(verticalPanEnabled)
        setOverPinchable(overPinchable)
        setZoomEnabled(zoomEnabled)
        setAnimationDuration(animationDuration)
        if (minZoom > -1) setMinZoom(minZoom, minZoomMode)
        if (maxZoom > -1) setMaxZoom(maxZoom, maxZoomMode)
        setHasClickableChildren(hasChildren)

        setWillNotDraw(false)
    }

    //region Internal

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // Measure ourselves as MATCH_PARENT
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == View.MeasureSpec.UNSPECIFIED || heightMode == View.MeasureSpec.UNSPECIFIED) {
            throw RuntimeException("$TAG must be used with fixed dimensions (e.g. match_parent)")
        }
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)

        // Measure our child as unspecified.
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        measureChildren(spec, spec)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (childCount == 0) {
            child.viewTreeObserver.addOnGlobalLayoutListener {
                engine.setContentSize(
                    child.width.toFloat(),
                    child.height.toFloat()
                )
            }
            super.addView(child, index, params)
        } else {
            throw RuntimeException("$TAG accepts only a single child.")
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return engine.onInterceptTouchEvent(ev) || mHasClickableChildren && super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return engine.onTouchEvent(ev) || mHasClickableChildren && super.onTouchEvent(ev)
    }

    override fun onUpdate(engine: ZoomEngine, matrix: Matrix) {
        mMatrix.set(matrix)
        if (mHasClickableChildren) {
            if (childCount > 0) {
                val child = getChildAt(0)
                mMatrix.getValues(mMatrixValues)
                child.pivotX = 0f
                child.pivotY = 0f
                child.translationX = mMatrixValues[Matrix.MTRANS_X]
                child.translationY = mMatrixValues[Matrix.MTRANS_Y]
                child.scaleX = mMatrixValues[Matrix.MSCALE_X]
                child.scaleY = mMatrixValues[Matrix.MSCALE_Y]
            }
        } else {
            invalidate()
        }

        if ((isHorizontalScrollBarEnabled || isVerticalScrollBarEnabled) && !awakenScrollBars()) {
            invalidate()
        }
    }

    override fun onIdle(engine: ZoomEngine) {}

    override fun computeHorizontalScrollOffset(): Int {
        return engine.computeHorizontalScrollOffset()
    }

    override fun computeHorizontalScrollRange(): Int {
        return engine.computeHorizontalScrollRange()
    }

    override fun computeVerticalScrollOffset(): Int {
        return engine.computeVerticalScrollOffset()
    }

    override fun computeVerticalScrollRange(): Int {
        return engine.computeVerticalScrollRange()
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val result: Boolean

        if (!mHasClickableChildren) {
            val save = canvas.save()
            canvas.concat(mMatrix)
            result = super.drawChild(canvas, child, drawingTime)
            canvas.restoreToCount(save)
        } else {
            result = super.drawChild(canvas, child, drawingTime)
        }

        return result
    }

    //endregion

    //region APIs

    /**
     * Whether the view hierarchy inside has (or will have) clickable children.
     * This is false by default.
     *
     * @param hasClickableChildren whether we have clickable children
     */
    fun setHasClickableChildren(hasClickableChildren: Boolean) {
        LOG.i("setHasClickableChildren:", "old:", mHasClickableChildren, "new:", hasClickableChildren)
        if (mHasClickableChildren && !hasClickableChildren) {
            // Revert any transformation that was applied to our child.
            if (childCount > 0) {
                val child = getChildAt(0)
                child.scaleX = 1f
                child.scaleY = 1f
                child.translationX = 0f
                child.translationY = 0f
            }
        }
        mHasClickableChildren = hasClickableChildren

        // Update if we were laid out already.
        if (width > 0 && height > 0) {
            if (mHasClickableChildren) {
                onUpdate(engine, mMatrix)
            } else {
                invalidate()
            }
        }
    }

    //endregion

    //region ZoomApis

    /**
     * Controls whether the content should be over-scrollable horizontally.
     * If it is, drag and fling horizontal events can scroll the content outside the safe area,
     * then return to safe values.
     *
     * @param overScroll whether to allow horizontal over scrolling
     */
    override fun setOverScrollHorizontal(overScroll: Boolean) {
        engine.setOverScrollHorizontal(overScroll)
    }

    /**
     * Controls whether the content should be over-scrollable vertically.
     * If it is, drag and fling vertical events can scroll the content outside the safe area,
     * then return to safe values.
     *
     * @param overScroll whether to allow vertical over scrolling
     */
    override fun setOverScrollVertical(overScroll: Boolean) {
        engine.setOverScrollVertical(overScroll)
    }

    /**
     * Controls whether horizontal panning using gestures is enabled.
     *
     * @param enabled true enables horizontal panning, false disables it
     */
    override fun setHorizontalPanEnabled(enabled: Boolean) {
        engine.setHorizontalPanEnabled(enabled)
    }

    /**
     * Controls whether vertical panning using gestures is enabled.
     *
     * @param enabled true enables vertical panning, false disables it
     */
    override fun setVerticalPanEnabled(enabled: Boolean) {
        engine.setVerticalPanEnabled(enabled)
    }

    /**
     * Controls whether the content should be overPinchable.
     * If it is, pinch events can change the zoom outside the safe bounds,
     * than return to safe values.
     *
     * @param overPinchable whether to allow over pinching
     */
    override fun setOverPinchable(overPinchable: Boolean) {
        engine.setOverPinchable(overPinchable)
    }

    /**
     * Controls whether zoom using pinch gesture is enabled or not.
     *
     * @param enabled true enables zooming, false disables it
     */
    override fun setZoomEnabled(enabled: Boolean) {
        engine.setZoomEnabled(enabled)
    }

    /**
     * Sets the base transformation to be applied to the content.
     * Defaults to [.TRANSFORMATION_CENTER_INSIDE] with [Gravity.CENTER],
     * which means that the content will be zoomed so that it fits completely inside the container.
     *
     * @param transformation the transformation type
     * @param gravity        the transformation gravity. Might be ignored for some transformations
     */
    override fun setTransformation(transformation: Int, gravity: Int) {
        engine.setTransformation(transformation, gravity)
    }

    /**
     * A low level API that can animate both zoom and pan at the same time.
     * Zoom might not be the actual matrix scale, see [.getZoom] and [.getRealZoom].
     * The coordinates are referred to the content size so they do not depend on current zoom.
     *
     * @param zoom    the desired zoom value
     * @param x       the desired left coordinate
     * @param y       the desired top coordinate
     * @param animate whether to animate the transition
     */
    override fun moveTo(zoom: Float, x: Float, y: Float, animate: Boolean) {
        engine.moveTo(zoom, x, y, animate)
    }

    /**
     * Pans the content until the top-left coordinates match the given x-y
     * values. These are referred to the content size so they do not depend on current zoom.
     *
     * @param x       the desired left coordinate
     * @param y       the desired top coordinate
     * @param animate whether to animate the transition
     */
    override fun panTo(x: Float, y: Float, animate: Boolean) {
        engine.panTo(x, y, animate)
    }

    /**
     * Pans the content by the given quantity in dx-dy values.
     * These are referred to the content size so they do not depend on current zoom.
     *
     *
     * In other words, asking to pan by 1 pixel might result in a bigger pan, if the content
     * was zoomed in.
     *
     * @param dx      the desired delta x
     * @param dy      the desired delta y
     * @param animate whether to animate the transition
     */
    override fun panBy(dx: Float, dy: Float, animate: Boolean) {
        engine.panBy(dx, dy, animate)
    }

    /**
     * Zooms to the given scale. This might not be the actual matrix zoom,
     * see [.getZoom] and [.getRealZoom].
     *
     * @param zoom    the new scale value
     * @param animate whether to animate the transition
     */
    override fun zoomTo(zoom: Float, animate: Boolean) {
        engine.zoomTo(zoom, animate)
    }

    /**
     * Applies the given factor to the current zoom.
     *
     * @param zoomFactor a multiplicative factor
     * @param animate    whether to animate the transition
     */
    override fun zoomBy(zoomFactor: Float, animate: Boolean) {
        engine.zoomBy(zoomFactor, animate)
    }

    /**
     * Applies a small, animated zoom-in.
     */
    override fun zoomIn() {
        engine.zoomIn()
    }

    /**
     * Applies a small, animated zoom-out.
     */
    override fun zoomOut() {
        engine.zoomOut()
    }

    /**
     * Animates the actual matrix zoom to the given value.
     *
     * @param realZoom the new real zoom value
     * @param animate  whether to animate the transition
     */
    override fun realZoomTo(realZoom: Float, animate: Boolean) {
        engine.realZoomTo(realZoom, animate)
    }

    /**
     * Which is the max zoom that should be allowed.
     * If [.setOverPinchable] is set to true, this can be over-pinched
     * for a brief time.
     *
     * @param maxZoom the max zoom
     * @param type    the constraint mode
     * @see .getZoom
     * @see .getRealZoom
     */
    override fun setMaxZoom(maxZoom: Float, type: Int) {
        engine.setMaxZoom(maxZoom, type)
    }

    /**
     * Which is the min zoom that should be allowed.
     * If [.setOverPinchable] is set to true, this can be over-pinched
     * for a brief time.
     *
     * @param minZoom the min zoom
     * @param type    the constraint mode
     * @see .getZoom
     * @see .getRealZoom
     */
    override fun setMinZoom(minZoom: Float, type: Int) {
        engine.setMinZoom(minZoom, type)
    }

    /**
     * Sets the duration of animations triggered by zoom and pan APIs.
     * Defaults to [ZoomEngine.DEFAULT_ANIMATION_DURATION].
     *
     * @param duration new animation duration
     */
    override fun setAnimationDuration(duration: Long) {
        engine.setAnimationDuration(duration)
    }

    companion object {

        private val TAG = ZoomLayout::class.java.simpleName
        private val LOG = ZoomLogger.create(TAG)
    }

    //endregion
}
