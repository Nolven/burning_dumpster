package carcassonne.se.carcassonnecustomclone.zoom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.support.annotation.IntDef
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.OverScroller
import carcassonne.se.carcassonnecustomclone.zoom.ZoomApi.Companion.TRANSFORMATION_CENTER_CROP
import carcassonne.se.carcassonnecustomclone.zoom.ZoomApi.Companion.TRANSFORMATION_CENTER_INSIDE
import carcassonne.se.carcassonnecustomclone.zoom.ZoomApi.Companion.TRANSFORMATION_NONE
import carcassonne.se.carcassonnecustomclone.zoom.ZoomApi.Companion.TYPE_REAL_ZOOM
import carcassonne.se.carcassonnecustomclone.zoom.ZoomApi.Companion.TYPE_ZOOM

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.ArrayList


/**
 * A low level class that listens to touch events and posts zoom and pan updates.
 * The most useful output is a [Matrix] that can be used to do pretty much everything,
 * from canvas drawing to View hierarchies translations.
 *
 *
 * Users are required to:
 * - Pass the container view in the constructor
 * - Notify the helper of the content size, using [.setContentSize]
 * - Pass touch events to [.onInterceptTouchEvent] and [.onTouchEvent]
 *
 *
 * This class will apply a base transformation to the content, see [.setTransformation],
 * so that it is laid out initially as we wish.
 *
 *
 * When the scaling makes the content smaller than our viewport, the engine will always try
 * to keep the content centered.
 */
class ZoomEngine
/**
 * Constructs an helper instance.
 * Deprecated: use [.addListener] to add a listener.
 *
 * @param context   a valid context
 * @param container the view hosting the zoomable content
 */
    (context: Context, private val mContainer: View) : ZoomApi {

    private val mListeners = ArrayList<Listener>()
    private var mMatrix = Matrix()
    private val mOutMatrix = Matrix()
    @State
    private var mState = NONE
    private var mContainerWidth: Float = 0.toFloat()
    private var mContainerHeight: Float = 0.toFloat()
    private var mInitialized: Boolean = false
    private var mTransformedRect = RectF()
    private var mContentRect = RectF()
    private var mMinZoom = 0.8f
    private var mMinZoomMode = TYPE_ZOOM
    private var mMaxZoom = 2.5f
    private var mMaxZoomMode = TYPE_ZOOM
    /**
     * Gets the current zoom value, which can be used as a reference when calling
     * [.zoomTo] or [.zoomBy].
     *
     *
     * This can be different than the actual scale you get in the matrix, because at startup
     * we apply a base transformation, see [.setTransformation].
     * All zoom calls, including min zoom and max zoom, refer to this axis, where zoom is set to 1
     * right after the initial transformation.
     *
     * @return the current zoom
     * @see .getRealZoom
     */
    @ZoomApi.Zoom
    @get:ZoomApi.Zoom
    override var zoom = 1f
//        set(value: Float) {
//            super.zoom = value
//        } // Not necessarily equal to the matrix scale.
    private var mBaseZoom: Float = 0.toFloat() // mZoom * mBaseZoom matches the matrix scale.
    private var mTransformation = TRANSFORMATION_CENTER_INSIDE
    private var mTransformationGravity = Gravity.CENTER
    private var mOverScrollHorizontal = true
    private var mOverScrollVertical = true
    private var mHorizontalPanEnabled = true
    private var mVerticalPanEnabled = true
    private var mOverPinchable = true
    private var mZoomEnabled = true
    private var mClearAnimation: Boolean = false
    private val mFlingScroller: OverScroller
    private var mAnimationDuration = DEFAULT_ANIMATION_DURATION

    private val mScaleDetector: ScaleGestureDetector
    private val mFlingDragDetector: GestureDetector

    /**
     * Returns the current matrix. This can be changed from the outside, but is not
     * guaranteed to remain stable.
     *
     * @return the current matrix.
     */
    val matrix: Matrix
        get() {
            mOutMatrix.set(mMatrix)
            return mOutMatrix
        }

    private// Get a scaled pan value.
    val maxOverScroll: Int
        @ZoomApi.ScaledPan
        get() {
            val overX = mContainerWidth * DEFAULT_OVERSCROLL_FACTOR
            val overY = mContainerHeight * DEFAULT_OVERSCROLL_FACTOR
            return (Math.min(overX, overY) * zoom).toInt()
        }

    private val maxOverPinch: Float
        @ZoomApi.Zoom
        get() = 0.1f * (resolveZoom(mMaxZoom, mMaxZoomMode) - resolveZoom(mMinZoom, mMinZoomMode))

    /**
     * Gets the current zoom value, including the base zoom that was eventually applied during
     * the starting transformation, see [.setTransformation].
     * This value will match the scaleX - scaleY values you get into the [Matrix],
     * and is the actual scale value of the content from its original size.
     *
     * @return the real zoom
     */
    override val realZoom: Float
        @ZoomApi.RealZoom
        get() = zoom * mBaseZoom

    /**
     * Returns the current horizontal pan value, in content coordinates
     * (that is, as if there was no zoom at all) referring to what was passed
     * to [.setContentSize].
     *
     * @return the current horizontal pan
     */
    override val panX: Float
        @ZoomApi.AbsolutePan
        get() = scaledPanX / realZoom

    /**
     * Returns the current vertical pan value, in content coordinates
     * (that is, as if there was no zoom at all) referring to what was passed
     * to [.setContentSize].
     *
     * @return the current vertical pan
     */
    override val panY: Float
        @ZoomApi.AbsolutePan
        get() = scaledPanY / realZoom

    private val scaledPanX: Float
        @ZoomApi.ScaledPan
        get() = mTransformedRect.left

    private val scaledPanY: Float
        @ZoomApi.ScaledPan
        get() = mTransformedRect.top

    private val mScrollerValuesX = ScrollerValues()
    private val mScrollerValuesY = ScrollerValues()

    /**
     * An interface to listen for updates in the inner matrix. This will be called
     * typically on animation frames.
     */
    interface Listener {

        /**
         * Notifies that the inner matrix was updated. The passed matrix can be changed,
         * but is not guaranteed to be stable. For a long lasting value it is recommended
         * to make a copy of it using [Matrix.set].
         *
         * @param engine the engine hosting the matrix
         * @param matrix a matrix with the given updates
         */
        fun onUpdate(engine: ZoomEngine, matrix: Matrix)

        /**
         * Notifies that the engine is in an idle state. This means that (most probably)
         * running animations have completed and there are no touch actions in place.
         *
         * @param engine this engine
         */
        fun onIdle(engine: ZoomEngine)
    }

    /**
     * A simple implementation of [Listener] that will extract the translation
     * and scale values from the output matrix.
     */
    abstract class SimpleListener : Listener {

        private val mMatrixValues = FloatArray(9)

        override fun onUpdate(engine: ZoomEngine, matrix: Matrix) {
            matrix.getValues(mMatrixValues)
            val panX = mMatrixValues[Matrix.MTRANS_X]
            val panY = mMatrixValues[Matrix.MTRANS_Y]
            val scaleX = mMatrixValues[Matrix.MSCALE_X]
            val scaleY = mMatrixValues[Matrix.MSCALE_Y]
            val scale = (scaleX + scaleY) / 2f // These should always be equal.
            onUpdate(engine, panX, panY, scale)
        }

        /**
         * Notifies that the engine has computed new updates for some of the pan or scale values.
         *
         * @param engine the engine
         * @param panX   the new horizontal pan value
         * @param panY   the new vertical pan value
         * @param zoom   the new scale value
         */
        internal abstract fun onUpdate(engine: ZoomEngine, panX: Float, panY: Float, zoom: Float)
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(NONE, SCROLLING, PINCHING, ANIMATING, FLINGING)
    private annotation class State

    /**
     * Constructs an helper instance.
     * Deprecated: use [.addListener] to add a listener.
     *
     * @param context   a valid context
     * @param container the view hosting the zoomable content
     * @param listener  a listener for events
     */
//    @Deprecated("")
//    constructor(context: Context, container: View, listener: Listener) : this(context, container) {
//        addListener(listener)
//    }

    init {
        mFlingScroller = OverScroller(context)
        mScaleDetector = ScaleGestureDetector(context, PinchListener())
        if (Build.VERSION.SDK_INT >= 19) mScaleDetector.isQuickScaleEnabled = false
        val gestureDetector = GestureDetector(context, FlingScrollListener())
        gestureDetector.setOnDoubleTapListener(null)
        mFlingDragDetector = gestureDetector
        mContainer.viewTreeObserver.addOnGlobalLayoutListener {
            setContainerSize(
                mContainer.width.toFloat(),
                mContainer.height.toFloat()
            )
        }
    }

    /**
     * Registers a new [Listener] to be notified of matrix updates.
     *
     * @param listener the new listener
     */
    fun addListener(listener: Listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener)
        }
    }

    /**
     * Removes a previously registered listener.
     *
     * @param listener the listener to be removed
     */
    fun removeListener(listener: Listener) {
        mListeners.remove(listener)
    }

    // Returns true if we should go to that mode.
    @SuppressLint("SwitchIntDef")
    private fun setState(@State state: Int): Boolean {
        LOG.v("trySetState:", getStateName(state))
        if (!mInitialized) return false
        if (state == mState) return true
        val oldMode = mState

        when (state) {
            SCROLLING -> if (oldMode == PINCHING || oldMode == ANIMATING) return false
            FLINGING -> if (oldMode == ANIMATING) return false
            PINCHING -> if (oldMode == ANIMATING) return false
            NONE -> dispatchOnIdle()
        }

        // Now that it succeeded, do some cleanup.
        when (oldMode) {
            FLINGING -> mFlingScroller.forceFinished(true)
            ANIMATING -> mClearAnimation = true
        }

        LOG.i("setState:", getStateName(state))
        mState = state
        return true
    }

    //region Overscroll

    /**
     * Controls whether the content should be over-scrollable horizontally.
     * If it is, drag and fling horizontal events can scroll the content outside the safe area,
     * then return to safe values.
     *
     * @param overScroll whether to allow horizontal over scrolling
     */
    override fun setOverScrollHorizontal(overScroll: Boolean) {
        mOverScrollHorizontal = overScroll
    }

    /**
     * Controls whether the content should be over-scrollable vertically.
     * If it is, drag and fling vertical events can scroll the content outside the safe area,
     * then return to safe values.
     *
     * @param overScroll whether to allow vertical over scrolling
     */
    override fun setOverScrollVertical(overScroll: Boolean) {
        mOverScrollVertical = overScroll
    }

    /**
     * Controls whether horizontal panning using gestures is enabled.
     *
     * @param enabled true enables horizontal panning, false disables it
     */
    override fun setHorizontalPanEnabled(enabled: Boolean) {
        mHorizontalPanEnabled = enabled
    }

    /**
     * Controls whether vertical panning using gestures is enabled.
     *
     * @param enabled true enables vertical panning, false disables it
     */
    override fun setVerticalPanEnabled(enabled: Boolean) {
        mVerticalPanEnabled = enabled
    }

    /**
     * Controls whether the content should be overPinchable.
     * If it is, pinch events can change the zoom outside the safe bounds,
     * than return to safe values.
     *
     * @param overPinchable whether to allow over pinching
     */
    override fun setOverPinchable(overPinchable: Boolean) {
        mOverPinchable = overPinchable
    }

    /**
     * Controls whether zoom using pinch gesture is enabled or not.
     *
     * @param enabled true enables zooming, false disables it
     */
    override fun setZoomEnabled(enabled: Boolean) {
        mZoomEnabled = enabled
    }

    //endregion

    //region Initialize

    /**
     * Sets the base transformation to be applied to the content.
     * Defaults to [.TRANSFORMATION_CENTER_INSIDE] with [Gravity.CENTER],
     * which means that the content will be zoomed so that it fits completely inside the container.
     *
     * @param transformation the transformation type
     * @param gravity        the transformation gravity. Might be ignored for some transformations
     */
    override fun setTransformation(transformation: Int, gravity: Int) {
        mTransformation = transformation
        mTransformationGravity = gravity
    }

    /**
     * Notifies the helper of the content size (be it a child View, a Bitmap, or whatever else).
     * This is needed for the helper to start working.
     *
     */
    @Deprecated("use {@link #setContentSize(float, float)} instead.")
    fun setContentSize(rect: RectF) {
        setContentSize(rect.width(), rect.height())
    }

    /**
     * Notifies the helper of the content size (be it a child View, a Bitmap, or whatever else).
     * This is needed for the helper to start working.
     *
     * @param width               the content width
     * @param height              the content height
     * @param applyTransformation whether to apply the transformation defined by [.setTransformation]
     */
    @JvmOverloads
    fun setContentSize(width: Float, height: Float, applyTransformation: Boolean = false) {
        if (width <= 0 || height <= 0) return
        if (mContentRect.width() != width || mContentRect.height() != height || applyTransformation) {
            mContentRect.set(0f, 0f, width, height)
            onSizeChanged(applyTransformation)
        }
    }

    /**
     * Sets the size of the container view. Normally you don't need to call this because the size
     * is detected from the container passed to the constructor using a global layout listener.
     *
     *
     * However, there are cases where you might want to update it, for example during
     * [View.onSizeChanged] (called during shared element transitions).
     *
     *
     * Shorthand for [.setContainerSize] with no transformation.
     *
     * @param width  the container width
     * @param height the container height
     */
    fun setContainerSize(width: Float, height: Float) {
        setContainerSize(width, height, false)
    }

    /**
     * Sets the size of the container view. Normally you don't need to call this because the size
     * is detected from the container passed to the constructor using a global layout listener.
     *
     *
     * However, there are cases where you might want to update it, for example during
     * [View.onSizeChanged] (called during shared element transitions).
     *
     * @param width               the container width
     * @param height              the container height
     * @param applyTransformation whether to apply the transformation defined by [.setTransformation]
     */
    fun setContainerSize(width: Float, height: Float, applyTransformation: Boolean) {
        if (width <= 0 || height <= 0) return
        if (width != mContainerWidth || height != mContainerHeight || applyTransformation) {
            mContainerWidth = width
            mContainerHeight = height
            onSizeChanged(applyTransformation)
        }
    }

    private fun onSizeChanged(applyTransformation: Boolean) {
        // We will sync them later using matrix.mapRect.
        mTransformedRect.set(mContentRect)

        if (mContentRect.width() <= 0
            || mContentRect.height() <= 0
            || mContainerWidth <= 0
            || mContainerHeight <= 0
        )
            return

        LOG.w(
            "onSizeChanged:", "containerWidth:", mContainerWidth,
            "containerHeight:", mContainerHeight,
            "contentWidth:", mContentRect.width(),
            "contentHeight:", mContentRect.height()
        )

        // See if we need to apply the transformation. This is the easier case, because
        // if we don't want to apply it, we must do extra computations to keep the appearance unchanged.
        setState(NONE)
        val apply = !mInitialized || applyTransformation
        LOG.w("onSizeChanged: will apply?", apply, "transformation?", mTransformation)
        if (apply) {
            // First time. Apply base zoom, dispatch first event and return.
            mBaseZoom = computeBaseZoom()
            mMatrix.setScale(mBaseZoom, mBaseZoom)
            mMatrix.mapRect(mTransformedRect, mContentRect)
            zoom = 1f
            LOG.i("onSizeChanged: newBaseZoom:", mBaseZoom, "newZoom:", zoom)
            @ZoomApi.Zoom val newZoom = checkZoomBounds(zoom, false)
            LOG.i("onSizeChanged: scaleBounds:", "we need a zoom correction of", newZoom - zoom)
            if (newZoom != zoom) applyZoom(newZoom, false)

            // pan based on transformation gravity.
            @ZoomApi.ScaledPan val newPan = computeBasePan()
            @ZoomApi.ScaledPan val deltaX = newPan[0] - scaledPanX
            @ZoomApi.ScaledPan val deltaY = newPan[1] - scaledPanY
            if (deltaX != 0f || deltaY != 0f) applyScaledPan(deltaX, deltaY, false)

            ensurePanBounds(false)
            dispatchOnMatrix()
            if (!mInitialized) {
                mInitialized = true
            }
        } else {
            // We were initialized, but some size changed. Since applyTransformation is false,
            // we must do extra work: recompute the baseZoom (since size changed, it makes no sense)
            // but also compute a new zoom such that the real zoom is kept unchanged.
            // So, this method triggers no Matrix updates.
            LOG.i("onSizeChanged: Trying to keep real zoom to", realZoom)
            LOG.i("onSizeChanged: oldBaseZoom:", mBaseZoom, "oldZoom:$zoom")
            @ZoomApi.RealZoom val realZoom = realZoom
            mBaseZoom = computeBaseZoom()
            zoom = realZoom / mBaseZoom
            LOG.i("onSizeChanged: newBaseZoom:", mBaseZoom, "newZoom:", zoom)

            // Now sync the content rect with the current matrix since we are trying to keep it.
            // This is to have consistent values for other calls here.
            mMatrix.mapRect(mTransformedRect, mContentRect)

            // If the new zoom value is invalid, though, we must bring it to the valid place.
            // This is a possible matrix update.
            @ZoomApi.Zoom val newZoom = checkZoomBounds(zoom, false)
            LOG.i("onSizeChanged: scaleBounds:", "we need a zoom correction of", newZoom - zoom)
            if (newZoom != zoom) applyZoom(newZoom, false)

            // If there was any, pan should be kept. I think there's nothing to do here:
            // If the matrix is kept, and real zoom is kept, then also the real pan is kept.
            // I am not 100% sure of this though, so I prefer to call a useless dispatch.
            ensurePanBounds(false)
            dispatchOnMatrix()
        }
    }

    /**
     * Clears the current state, and stops dispatching matrix events
     * until the view is laid out again and [.setContentSize]
     * is called.
     */
    fun clear() {
        mContainerHeight = 0f
        mContainerWidth = 0f
        zoom = 1f
        mBaseZoom = 0f
        mTransformedRect = RectF()
        mContentRect = RectF()
        mMatrix = Matrix()
        mInitialized = false
    }

    private fun computeBaseZoom(): Float {
        when (mTransformation) {
            TRANSFORMATION_CENTER_INSIDE -> {
                val scaleX = mContainerWidth / mTransformedRect.width()
                val scaleY = mContainerHeight / mTransformedRect.height()
                LOG.v("computeBaseZoom", "centerInside", "scaleX:", scaleX, "scaleY:", scaleY)
                return Math.min(scaleX, scaleY)
            }
            TRANSFORMATION_CENTER_CROP -> {
                val scaleX = mContainerWidth / mTransformedRect.width()
                val scaleY = mContainerHeight / mTransformedRect.height()
                LOG.v("computeBaseZoom", "centerCrop", "scaleX:", scaleX, "scaleY:", scaleY)
                return Math.max(scaleX, scaleY)
            }
            TRANSFORMATION_NONE -> return 1f
            else -> return 1f
        }
    }

    // TODO support START and END correctly.
    @SuppressLint("RtlHardcoded")
    @ZoomApi.ScaledPan
    private fun computeBasePan(): FloatArray {
        val result = floatArrayOf(0f, 0f)
        val extraWidth = mTransformedRect.width() - mContainerWidth
        val extraHeight = mTransformedRect.height() - mContainerHeight
        if (extraWidth > 0) {
            // Honour the horizontal gravity indication.
            when (mTransformationGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.LEFT -> result[0] = 0f
                Gravity.CENTER_HORIZONTAL -> result[0] = -0.5f * extraWidth
                Gravity.RIGHT -> result[0] = -extraWidth
            }
        }
        if (extraHeight > 0) {
            // Honour the vertical gravity indication.
            when (mTransformationGravity and Gravity.VERTICAL_GRAVITY_MASK) {
                Gravity.TOP -> result[1] = 0f
                Gravity.CENTER_VERTICAL -> result[1] = -0.5f * extraHeight
                Gravity.BOTTOM -> result[1] = -extraHeight
            }
        }
        return result
    }

    //endregion

    //region Private helpers

    private fun dispatchOnMatrix() {
        val matrix = matrix
        for (listener in mListeners) {
            listener.onUpdate(this, matrix)
        }
    }

    private fun dispatchOnIdle() {
        for (listener in mListeners) {
            listener.onIdle(this)
        }
    }

    /**
     * Checks the current zoom state.
     * Returns 0 if we are in a valid state, or the zoom correction to be applied
     * to get into a valid state again.
     */
    @ZoomApi.Zoom
    private fun checkZoomBounds(@ZoomApi.Zoom value: Float, allowOverPinch: Boolean): Float {
        var value = value
        var minZoom = resolveZoom(mMinZoom, mMinZoomMode)
        var maxZoom = resolveZoom(mMaxZoom, mMaxZoomMode)
        if (allowOverPinch && mOverPinchable) {
            minZoom -= maxOverPinch
            maxZoom += maxOverPinch
        }
        if (value < minZoom) value = minZoom
        if (value > maxZoom) value = maxZoom
        return value
    }

    /**
     * Checks the current pan state.
     * Returns 0 if we are in a valid state, or the pan correction to be applied
     * to get into a valid state again.
     */
    @ZoomApi.ScaledPan
    private fun checkPanBounds(horizontal: Boolean, allowOverScroll: Boolean): Float {
        @ZoomApi.ScaledPan val value = if (horizontal) scaledPanX else scaledPanY
        val viewSize = if (horizontal) mContainerWidth else mContainerHeight
        @ZoomApi.ScaledPan val contentSize = if (horizontal) mTransformedRect.width() else mTransformedRect.height()

        val overScrollable = if (horizontal) mOverScrollHorizontal else mOverScrollVertical
        @ZoomApi.ScaledPan val overScroll = (if (overScrollable && allowOverScroll) maxOverScroll else 0).toFloat()
        return getPanCorrection(value, viewSize, contentSize, overScroll)
    }

    @ZoomApi.ScaledPan
    private fun getPanCorrection(
        @ZoomApi.ScaledPan value: Float, viewSize: Float,
        @ZoomApi.ScaledPan contentSize: Float, @ZoomApi.ScaledPan overScroll: Float
    ): Float {
        @ZoomApi.ScaledPan val tolerance = overScroll.toInt()
        var min: Float
        var max: Float
        if (contentSize <= viewSize) {
            // If contentSize <= viewSize, we want to stay centered.
            // Need a positive translation, that shows some background.
            min = (viewSize - contentSize) / 2f
            max = (viewSize - contentSize) / 2f
        } else {
            // If contentSize is bigger, we just don't want to go outside.
            // Need a negative translation, that hides content.
            min = viewSize - contentSize
            max = 0f
        }
        min -= tolerance.toFloat()
        max += tolerance.toFloat()
        var desired = value
        if (desired < min) desired = min
        if (desired > max) desired = max
        return desired - value
    }

    /**
     * Calls [.checkPanBounds] on both directions
     * and applies the correction to the matrix if needed.
     */
    private fun ensurePanBounds(allowOverScroll: Boolean) {
        @ZoomApi.ScaledPan val fixX = checkPanBounds(true, allowOverScroll)
        @ZoomApi.ScaledPan val fixY = checkPanBounds(false, allowOverScroll)
        if (fixX != 0f || fixY != 0f) {
            mMatrix.postTranslate(fixX, fixY)
            mMatrix.mapRect(mTransformedRect, mContentRect)
        }
    }

    @ZoomApi.Zoom
    private fun resolveZoom(zoom: Float, @ZoomApi.ZoomType mode: Int): Float {
        when (mode) {
            TYPE_ZOOM -> return zoom
            TYPE_REAL_ZOOM -> return zoom / mBaseZoom
        }
        return -1f
    }

    @ZoomApi.ScaledPan
    private fun resolvePan(@ZoomApi.AbsolutePan pan: Float): Float {
        return pan * realZoom
    }

    @ZoomApi.AbsolutePan
    private fun unresolvePan(@ZoomApi.ScaledPan pan: Float): Float {
        return pan / realZoom
    }

    /**
     * This is required when the content is a View that has clickable hierarchies inside.
     * If true is returned, implementors should not pass the call to super.
     *
     * @param ev the motion event
     * @return whether we want to intercept the event
     */
    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return processTouchEvent(ev) > TOUCH_LISTEN
    }

    /**
     * Process the given touch event.
     * If true is returned, implementors should not pass the call to super.
     *
     * @param ev the motion event
     * @return whether we want to steal the event
     */
    fun onTouchEvent(ev: MotionEvent): Boolean {
        return processTouchEvent(ev) > TOUCH_NO
    }

    private fun processTouchEvent(event: MotionEvent): Int {
        LOG.v("processTouchEvent:", "start.")
        if (mState == ANIMATING) return TOUCH_STEAL

        var result = mScaleDetector.onTouchEvent(event)
        LOG.v("processTouchEvent:", "scaleResult:", result)

        // Pinch detector always returns true. If we actually started a pinch,
        // Don't pass to fling detector.
        if (mState != PINCHING) {
            result = result or mFlingDragDetector.onTouchEvent(event)
            LOG.v("processTouchEvent:", "flingResult:", result)
        }

        // Detect scroll ends, this appears to be the only way.
        if (mState == SCROLLING) {
            val a = event.actionMasked
            if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_CANCEL) {
                LOG.i("processTouchEvent:", "up event while scrolling, dispatching onScrollEnd.")
                onScrollEnd()
            }
        }

        if (result && mState != NONE) {
            LOG.v("processTouchEvent:", "returning: TOUCH_STEAL")
            return TOUCH_STEAL
        } else if (result) {
            LOG.v("processTouchEvent:", "returning: TOUCH_LISTEN")
            return TOUCH_LISTEN
        } else {
            LOG.v("processTouchEvent:", "returning: TOUCH_NO")
            setState(NONE)
            return TOUCH_NO
        }
    }

    private inner class PinchListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @ZoomApi.AbsolutePan
        private var mAbsTargetX = 0f
        @ZoomApi.AbsolutePan
        private var mAbsTargetY = 0f

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (!mZoomEnabled) {
                return false
            }

            if (setState(PINCHING)) {
                val eps = 0.0001f
                if (Math.abs(mAbsTargetX) < eps || Math.abs(mAbsTargetY) < eps) {
                    // We want to interpret this as a scaled value, to work with the *actual* zoom.
                    @ZoomApi.ScaledPan var scaledFocusX = -detector.focusX
                    @ZoomApi.ScaledPan var scaledFocusY = -detector.focusY
                    LOG.i(
                        "onScale:",
                        "Setting focus.",
                        "detectorFocusX:",
                        scaledFocusX,
                        "detectorFocusX:",
                        scaledFocusY
                    )

                    // Account for current pan.
                    scaledFocusX += scaledPanX
                    scaledFocusY += scaledPanY

                    // Transform to an absolute, scale-independent value.
                    mAbsTargetX = unresolvePan(scaledFocusX)
                    mAbsTargetY = unresolvePan(scaledFocusY)
                    LOG.i("onScale:", "Setting focus.", "absTargetX:", mAbsTargetX, "absTargetY:", mAbsTargetY)
                }

                // Having both overPinch and overScroll is hard to manage, there are lots of bugs if we do.
                val factor = detector.scaleFactor
                val newZoom = zoom * factor
                applyPinch(newZoom, mAbsTargetX, mAbsTargetY, true)
                return true
            }
            return false
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            LOG.i(
                "onScaleEnd:", "mAbsTargetX:", mAbsTargetX, "mAbsTargetY:",
                mAbsTargetY, "mOverPinchable;", mOverPinchable
            )
            mAbsTargetX = 0f
            mAbsTargetY = 0f
            if (mOverPinchable) {
                // We might have over pinched. Animate back to reasonable value.
                @ZoomApi.Zoom var zoom = 0f
                @ZoomApi.Zoom val maxZoom = resolveZoom(mMaxZoom, mMaxZoomMode)
                @ZoomApi.Zoom val minZoom = resolveZoom(mMinZoom, mMinZoomMode)
                if (zoom < minZoom) zoom = minZoom
                if (zoom > maxZoom) zoom = maxZoom
                LOG.i(
                    "onScaleEnd:", "zoom:", zoom, "max:",
                    maxZoom, "min;", minZoom
                )
                if (zoom > 0) {
                    animateZoom(zoom, true)
                    return
                }
            }
            setState(NONE)
        }
    }


    private inner class FlingScrollListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true // We are interested in the gesture.
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val vX = (if (mHorizontalPanEnabled) velocityX else 0f).toInt()
            val vY = (if (mVerticalPanEnabled) velocityY else 0f).toInt()
            return startFling(vX, vY)
        }


        /**
         * Scroll event detected.
         *
         *
         * We assume overScroll is true. If this is the case, it will be reset in [.onScrollEnd].
         * If not, the [.applyScaledPan] function will ignore our delta.
         *
         *
         * TODO this this not true! ^
         */
        override fun onScroll(
            e1: MotionEvent, e2: MotionEvent,
            @ZoomApi.AbsolutePan distanceX: Float, @ZoomApi.AbsolutePan distanceY: Float
        ): Boolean {
            var distanceX = distanceX
            var distanceY = distanceY
            if (setState(SCROLLING)) {
                // Change sign, since we work with opposite values.
                distanceX = -distanceX
                distanceY = -distanceY

                // See if we are overscrolling.
                val fixX = checkPanBounds(true, false)
                val fixY = checkPanBounds(false, false)

                // If we are overscrolling AND scrolling towards the overscroll direction...
                if (fixX < 0 && distanceX > 0 || fixX > 0 && distanceX < 0) {
                    // Compute friction: a factor for distances. Must be 1 if we are not overscrolling,
                    // and 0 if we are at the end of the available overscroll. This works:
                    val overScrollX = Math.abs(fixX) / maxOverScroll // 0 ... 1
                    val frictionX = 0.6f * (1f - Math.pow(overScrollX.toDouble(), 0.4).toFloat()) // 0 ... 0.6
                    LOG.i("onScroll", "applying friction X:", frictionX)
                    distanceX *= frictionX
                }
                if (fixY < 0 && distanceY > 0 || fixY > 0 && distanceY < 0) {
                    val overScrollY = Math.abs(fixY) / maxOverScroll // 0 ... 1
                    val frictionY = 0.6f * (1f - Math.pow(overScrollY.toDouble(), 0.4).toFloat()) // 0 ... 10.6
                    LOG.i("onScroll", "applying friction Y:", frictionY)
                    distanceY *= frictionY
                }

                // If disabled, reset to 0.
                if (!mHorizontalPanEnabled) distanceX = 0f
                if (!mVerticalPanEnabled) distanceY = 0f

                if (distanceX != 0f || distanceY != 0f) {
                    applyScaledPan(distanceX, distanceY, true)
                }
                return true
            }
            return false
        }
    }

    private fun onScrollEnd() {
        if (mOverScrollHorizontal || mOverScrollVertical) {
            // We might have over scrolled. Animate back to reasonable value.
            @ZoomApi.ScaledPan val fixX = checkPanBounds(true, false)
            @ZoomApi.ScaledPan val fixY = checkPanBounds(false, false)
            if (fixX != 0f || fixY != 0f) {
                animateScaledPan(fixX, fixY, true)
                return
            }
        }
        setState(NONE)
    }

    //endregion

    //region Position APIs

    /**
     * A low level API that can animate both zoom and pan at the same time.
     * Zoom might not be the actual matrix scale, see [.getZoom] and [.getRealZoom].
     * The coordinates are referred to the content size passed in [.setContentSize]
     * so they do not depend on current zoom.
     *
     * @param zoom    the desired zoom value
     * @param x       the desired left coordinate
     * @param y       the desired top coordinate
     * @param animate whether to animate the transition
     */
    override fun moveTo(@ZoomApi.Zoom zoom: Float, @ZoomApi.AbsolutePan x: Float, @ZoomApi.AbsolutePan y: Float, animate: Boolean) {
        if (!mInitialized) return
        if (animate) {
            animateZoomAndAbsolutePan(zoom, x, y, false)
        } else {
            applyZoomAndAbsolutePan(zoom, x, y, false)
        }
    }

    /**
     * Pans the content until the top-left coordinates match the given x-y
     * values. These are referred to the content size passed in [.setContentSize],
     * so they do not depend on current zoom.
     *
     * @param x       the desired left coordinate
     * @param y       the desired top coordinate
     * @param animate whether to animate the transition
     */
    override fun panTo(@ZoomApi.AbsolutePan x: Float, @ZoomApi.AbsolutePan y: Float, animate: Boolean) {
        panBy(x - panX, y - panY, animate)
    }

    /**
     * Pans the content by the given quantity in dx-dy values.
     * These are referred to the content size passed in [.setContentSize],
     * so they do not depend on current zoom.
     *
     *
     * In other words, asking to pan by 1 pixel might result in a bigger pan, if the content
     * was zoomed in.
     *
     * @param dx      the desired delta x
     * @param dy      the desired delta y
     * @param animate whether to animate the transition
     */
    override fun panBy(@ZoomApi.AbsolutePan dx: Float, @ZoomApi.AbsolutePan dy: Float, animate: Boolean) {
        if (!mInitialized) return
        if (animate) {
            animateZoomAndAbsolutePan(zoom, panX + dx, panY + dy, false)
        } else {
            applyZoomAndAbsolutePan(zoom, panX + dx, panY + dy, false)
        }
    }

    /**
     * Zooms to the given scale. This might not be the actual matrix zoom,
     * see [.getZoom] and [.getRealZoom].
     *
     * @param zoom    the new scale value
     * @param animate whether to animate the transition
     */
    override fun zoomTo(@ZoomApi.Zoom zoom: Float, animate: Boolean) {
        if (!mInitialized) return
        if (animate) {
            animateZoom(zoom, false)
        } else {
            applyZoom(zoom, false)
        }
    }

    /**
     * Applies the given factor to the current zoom.
     *
     * @param zoomFactor a multiplicative factor
     * @param animate    whether to animate the transition
     */
    override fun zoomBy(zoomFactor: Float, animate: Boolean) {
        @ZoomApi.Zoom val newZoom = zoom * zoomFactor
        zoomTo(newZoom, animate)
    }


    /**
     * Applies a small, animated zoom-in.
     * Shorthand for [.zoomBy] with factor 1.3.
     */
    override fun zoomIn() {
        zoomBy(1.3f, true)
    }

    /**
     * Applies a small, animated zoom-out.
     * Shorthand for [.zoomBy] with factor 0.7.
     */
    override fun zoomOut() {
        zoomBy(0.7f, true)
    }

    /**
     * Animates the actual matrix zoom to the given value.
     *
     * @param realZoom the new real zoom value
     * @param animate  whether to animate the transition
     */
    override fun realZoomTo(realZoom: Float, animate: Boolean) {
        zoomTo(resolveZoom(realZoom, TYPE_REAL_ZOOM), animate)
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
     * @see .TYPE_ZOOM
     *
     * @see .TYPE_REAL_ZOOM
     */
    override fun setMaxZoom(maxZoom: Float, @ZoomApi.ZoomType type: Int) {
        if (maxZoom < 0) {
            throw IllegalArgumentException("Max zoom should be >= 0.")
        }
        mMaxZoom = maxZoom
        mMaxZoomMode = type
        if (zoom > resolveZoom(maxZoom, type)) {
            zoomTo(resolveZoom(maxZoom, type), true)
        }
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
    override fun setMinZoom(minZoom: Float, @ZoomApi.ZoomType type: Int) {
        if (minZoom < 0) {
            throw IllegalArgumentException("Min zoom should be >= 0")
        }
        mMinZoom = minZoom
        mMinZoomMode = type
        if (zoom <= resolveZoom(minZoom, type)) {
            zoomTo(resolveZoom(minZoom, type), true)
        }
    }

    //endregion

    //region Apply values

    /**
     * Calls [.applyZoom] repeatedly
     * until the final zoom is reached, interpolating.
     *
     * @param newZoom        the new zoom
     * @param allowOverPinch whether overpinching is allowed
     */
    private fun animateZoom(@ZoomApi.Zoom newZoom: Float, allowOverPinch: Boolean) {
        var newZoom = newZoom
        newZoom = checkZoomBounds(newZoom, allowOverPinch)
        if (setState(ANIMATING)) {
            mClearAnimation = false
            val startTime = System.currentTimeMillis()
            @ZoomApi.Zoom val startZoom = zoom
            @ZoomApi.Zoom val endZoom = newZoom
            mContainer.post(object : Runnable {
                override fun run() {
                    if (mClearAnimation) return
                    val time = interpolateAnimationTime(System.currentTimeMillis() - startTime)
                    LOG.v("animateZoomAndAbsolutePan:", "animationStep:", time)
                    @ZoomApi.Zoom val zoom = startZoom + time * (endZoom - startZoom)
                    applyZoom(zoom, allowOverPinch)
                    if (time >= 1f) {
                        setState(NONE)
                    } else {
                        mContainer.postOnAnimation(this)
                    }
                }
            })
        }
    }

    /**
     * Calls [.applyZoomAndAbsolutePan] repeatedly
     * until the final position is reached, interpolating.
     *
     * @param newZoom         new zoom
     * @param x               final abs pan
     * @param y               final abs pan
     * @param allowOverScroll whether to overscroll
     */
    private fun animateZoomAndAbsolutePan(
        @ZoomApi.Zoom newZoom: Float,
        @ZoomApi.AbsolutePan x: Float, @ZoomApi.AbsolutePan y: Float,
        allowOverScroll: Boolean
    ) {
        var newZoom = newZoom
        newZoom = checkZoomBounds(newZoom, allowOverScroll)
        if (setState(ANIMATING)) {
            mClearAnimation = false
            val startTime = System.currentTimeMillis()
            @ZoomApi.Zoom val startZoom = zoom
            @ZoomApi.Zoom val endZoom = newZoom
            @ZoomApi.AbsolutePan val startX = panX
            @ZoomApi.AbsolutePan val startY = panY
            LOG.i(
                "animateZoomAndAbsolutePan:",
                "starting.",
                "startX:",
                startX,
                "endX:",
                x,
                "startY:",
                startY,
                "endY:",
                y
            )
            LOG.i("animateZoomAndAbsolutePan:", "starting.", "startZoom:", startZoom, "endZoom:", endZoom)
            mContainer.post(object : Runnable {
                override fun run() {
                    if (mClearAnimation) return
                    val time = interpolateAnimationTime(System.currentTimeMillis() - startTime)
                    LOG.v("animateZoomAndAbsolutePan:", "animationStep:", time)
                    @ZoomApi.Zoom val zoom = startZoom + time * (endZoom - startZoom)
                    @ZoomApi.AbsolutePan val targetX = startX + time * (x - startX)
                    @ZoomApi.AbsolutePan val targetY = startY + time * (y - startY)
                    applyZoomAndAbsolutePan(zoom, targetX, targetY, allowOverScroll)
                    if (time >= 1f) {
                        setState(NONE)
                    } else {
                        mContainer.postOnAnimation(this)
                    }
                }
            })
        }
    }

    /**
     * Calls [.applyScaledPan] repeatedly
     * until the final delta is applied, interpolating.
     *
     * @param deltaX          a scaled delta
     * @param deltaY          a scaled delta
     * @param allowOverScroll whether to overscroll
     */
    private fun animateScaledPan(
        @ZoomApi.ScaledPan deltaX: Float, @ZoomApi.ScaledPan deltaY: Float,
        allowOverScroll: Boolean
    ) {
        if (setState(ANIMATING)) {
            mClearAnimation = false
            val startTime = System.currentTimeMillis()
            @ZoomApi.ScaledPan val startX = scaledPanX
            @ZoomApi.ScaledPan val startY = scaledPanY
            @ZoomApi.ScaledPan val endX = startX + deltaX
            @ZoomApi.ScaledPan val endY = startY + deltaY
            mContainer.post(object : Runnable {
                override fun run() {
                    if (mClearAnimation) return
                    val time = interpolateAnimationTime(System.currentTimeMillis() - startTime)
                    LOG.v("animateScaledPan:", "animationStep:", time)
                    @ZoomApi.ScaledPan val x = startX + time * (endX - startX)
                    @ZoomApi.ScaledPan val y = startY + time * (endY - startY)
                    applyScaledPan(x - scaledPanX, y - scaledPanY, allowOverScroll)
                    if (time >= 1f) {
                        setState(NONE)
                    } else {
                        mContainer.postOnAnimation(this)
                    }
                }
            })
        }
    }

    override fun setAnimationDuration(duration: Long) {
        mAnimationDuration = duration
    }

    private fun interpolateAnimationTime(delta: Long): Float {
        val time = Math.min(1f, delta.toFloat() / mAnimationDuration.toFloat())
        return ANIMATION_INTERPOLATOR.getInterpolation(time)
    }

    /**
     * Applies the given zoom value, meant as a [Zoom] value
     * (so not a [RealZoom]).
     * The zoom is applied so that the center point is kept in its place
     *
     * @param newZoom        the new zoom value
     * @param allowOverPinch whether to overpinch
     */
    private fun applyZoom(@ZoomApi.Zoom newZoom: Float, allowOverPinch: Boolean) {
        var newZoom = newZoom
        newZoom = checkZoomBounds(newZoom, allowOverPinch)
        val scaleFactor = newZoom / zoom
        mMatrix.postScale(
            scaleFactor, scaleFactor,
            mContainerWidth / 2f, mContainerHeight / 2f
        )
        mMatrix.mapRect(mTransformedRect, mContentRect)
        zoom = newZoom
        ensurePanBounds(false)
        dispatchOnMatrix()
    }

    /**
     * Applies both zoom and absolute pan. This is like specifying a position.
     * The semantics of this are that after the position is applied, the zoom corresponds
     * to the given value, getPanX() returns x, getPanY() returns y.
     *
     *
     * Absolute panning is achieved through [Matrix.preTranslate],
     * which works in the original coordinate system.
     *
     * @param newZoom         the new zoom value
     * @param x               the final left absolute pan
     * @param y               the final top absolute pan
     * @param allowOverScroll whether to overscroll
     */
    private fun applyZoomAndAbsolutePan(
        @ZoomApi.Zoom newZoom: Float,
        @ZoomApi.AbsolutePan x: Float, @ZoomApi.AbsolutePan y: Float,
        allowOverScroll: Boolean
    ) {
        var newZoom = newZoom
        // Translation
        @ZoomApi.AbsolutePan val deltaX = x - panX
        @ZoomApi.AbsolutePan val deltaY = y - panY
        mMatrix.preTranslate(deltaX, deltaY)
        mMatrix.mapRect(mTransformedRect, mContentRect)

        // Scale
        newZoom = checkZoomBounds(newZoom, false)
        val scaleFactor = newZoom / zoom
        // TODO: This used to work but I am not sure about it.
        // mMatrix.postScale(scaleFactor, scaleFactor, getScaledPanX(), getScaledPanY());
        // It keeps the pivot point at the scaled values 0, 0 (see applyPinch).
        // I think we should keep the current top, left.. Let's try:
        mMatrix.postScale(scaleFactor, scaleFactor, 0f, 0f)
        mMatrix.mapRect(mTransformedRect, mContentRect)
        zoom = newZoom

        ensurePanBounds(allowOverScroll)
        dispatchOnMatrix()
    }

    /**
     * Applies the given scaled translation.
     *
     *
     * Scaled translation are applied through [Matrix.postTranslate],
     * which acts on the actual dimension of the rect.
     *
     * @param deltaX          the x translation
     * @param deltaY          the y translation
     * @param allowOverScroll whether to overScroll
     */
    private fun applyScaledPan(@ZoomApi.ScaledPan deltaX: Float, @ZoomApi.ScaledPan deltaY: Float, allowOverScroll: Boolean) {
        mMatrix.postTranslate(deltaX, deltaY)
        mMatrix.mapRect(mTransformedRect, mContentRect)
        ensurePanBounds(allowOverScroll)
        dispatchOnMatrix()
    }

    /**
     * Helper for pinch gestures. In these cases what we know is the detector focus,
     * and we can use it in [Matrix.postScale] to avoid
     * buggy translations.
     *
     * @param newZoom        the new zoom
     * @param targetX        the target X in abs value
     * @param targetY        the target Y in abs value
     * @param allowOverPinch whether to overPinch
     */
    private fun applyPinch(
        @ZoomApi.Zoom newZoom: Float, @ZoomApi.AbsolutePan targetX: Float, @ZoomApi.AbsolutePan targetY: Float,
        allowOverPinch: Boolean
    ) {
        var newZoom = newZoom
        // The pivotX and pivotY options of postScale refer (obviously!) to the visible
        // portion of the screen, since the (0,0) point is remapped to be in top-left of the view.
        // The right coordinates to use are the view coordinates.
        // This means we should use scaled coordinates, but remove the current pan.

        @ZoomApi.ScaledPan val scaledX = resolvePan(targetX)
        @ZoomApi.ScaledPan val scaledY = resolvePan(targetY)
        newZoom = checkZoomBounds(newZoom, allowOverPinch)
        val scaleFactor = newZoom / zoom
        mMatrix.postScale(
            scaleFactor, scaleFactor,
            scaledPanX - scaledX,
            scaledPanY - scaledY
        )

        mMatrix.mapRect(mTransformedRect, mContentRect)
        zoom = newZoom
        ensurePanBounds(false)
        dispatchOnMatrix()
    }

    //endregion

    //region Fling

    // Puts min, start and max values in the mTemp array.
    // Since axes are shifted (pans are negative), min values are related to bottom-right,
    // while max values are related to top-left.
    private fun computeScrollerValues(horizontal: Boolean, output: ScrollerValues) {
        @ZoomApi.ScaledPan val currentPan = (if (horizontal) scaledPanX else scaledPanY).toInt()
        val viewDim = (if (horizontal) mContainerWidth else mContainerHeight).toInt()
        @ZoomApi.ScaledPan val contentDim = (if (horizontal) mTransformedRect.width() else mTransformedRect.height()).toInt()
        val fix = checkPanBounds(horizontal, false).toInt()
        if (viewDim >= contentDim) {
            // Content is smaller, we are showing some boundary.
            // We can't move in any direction (but we can overScroll).
            output.minValue = currentPan + fix
            output.startValue = currentPan
            output.maxValue = currentPan + fix
        } else {
            // Content is bigger, we can move.
            // in this case minPan + viewDim = contentDim
            output.minValue = -(contentDim - viewDim)
            output.startValue = currentPan
            output.maxValue = 0
        }
        output.isInOverScroll = fix != 0
    }

    private class ScrollerValues {
        @ZoomApi.ScaledPan
        internal var minValue: Int = 0
        @ZoomApi.ScaledPan
        internal var startValue: Int = 0
        @ZoomApi.ScaledPan
        internal var maxValue: Int = 0
        internal var isInOverScroll: Boolean = false
    }

    private fun startFling(@ZoomApi.ScaledPan velocityX: Int, @ZoomApi.ScaledPan velocityY: Int): Boolean {
        // Using actual pan values for the scroller.
        // Note: these won't make sense if zoom changes.
        computeScrollerValues(true, mScrollerValuesX)
        computeScrollerValues(false, mScrollerValuesY)
        @ZoomApi.ScaledPan val minX = mScrollerValuesX.minValue
        @ZoomApi.ScaledPan val startX = mScrollerValuesX.startValue
        @ZoomApi.ScaledPan val maxX = mScrollerValuesX.maxValue
        @ZoomApi.ScaledPan val minY = mScrollerValuesY.minValue
        @ZoomApi.ScaledPan val startY = mScrollerValuesY.startValue
        @ZoomApi.ScaledPan val maxY = mScrollerValuesY.maxValue
        if (mScrollerValuesX.isInOverScroll || mScrollerValuesY.isInOverScroll) {
            // Don't accept new flings when in overscroll. This causes artifacts.
            return false
        }
        if (minX >= maxX && minY >= maxY && !mOverScrollVertical && !mOverScrollHorizontal) {
            return false
        }
        // Must be after the other conditions.
        if (!setState(FLINGING)) return false

        @ZoomApi.ScaledPan val overScrollX = if (mOverScrollHorizontal) maxOverScroll else 0
        @ZoomApi.ScaledPan val overScrollY = if (mOverScrollVertical) maxOverScroll else 0
        LOG.i("startFling", "velocityX:", velocityX, "velocityY:", velocityY)
        LOG.i("startFling", "flingX:", "min:", minX, "max:", maxX, "start:", startX, "overScroll:", overScrollY)
        LOG.i("startFling", "flingY:", "min:", minY, "max:", maxY, "start:", startY, "overScroll:", overScrollX)
        mFlingScroller.fling(
            startX, startY,
            velocityX, velocityY,
            minX, maxX, minY, maxY,
            overScrollX, overScrollY
        )

        mContainer.post(object : Runnable {
            override fun run() {
                if (mFlingScroller.isFinished) {
                    setState(NONE)
                } else if (mFlingScroller.computeScrollOffset()) {
                    @ZoomApi.ScaledPan val newPanX = mFlingScroller.currX
                    @ZoomApi.ScaledPan val newPanY = mFlingScroller.currY
                    // OverScroller will eventually go back to our bounds.
                    applyScaledPan(newPanX - scaledPanX, newPanY - scaledPanY, true)
                    mContainer.postOnAnimation(this)
                }
            }
        })
        return true
    }

    //endregion

    //region scrollbars helpers

    /**
     * Helper for implementing [View.computeHorizontalScrollOffset]
     * in custom views.
     *
     * @return the horizontal scroll offset.
     */
    fun computeHorizontalScrollOffset(): Int {
        return (-scaledPanX).toInt()
    }

    /**
     * Helper for implementing [View.computeHorizontalScrollRange]
     * in custom views.
     *
     * @return the horizontal scroll range.
     */
    fun computeHorizontalScrollRange(): Int {
        return mTransformedRect.width().toInt()
    }

    /**
     * Helper for implementing [View.computeVerticalScrollOffset]
     * in custom views.
     *
     * @return the vertical scroll offset.
     */
    fun computeVerticalScrollOffset(): Int {
        return (-scaledPanY).toInt()
    }

    /**
     * Helper for implementing [View.computeVerticalScrollRange]
     * in custom views.
     *
     * @return the vertical scroll range.
     */
    fun computeVerticalScrollRange(): Int {
        return mTransformedRect.height().toInt()
    }

    companion object {

        // TODO add OverScrollCallback and OverPinchCallback.
        // Should notify the user when the boundaries are reached.

        // TODO expose friction parameters, use an interpolator.

        // TODO Make public, add API.
        private val DEFAULT_OVERSCROLL_FACTOR = 0.10f

        // TODO Make public, add API. Use androidx.Interpolator?
        private val ANIMATION_INTERPOLATOR = AccelerateDecelerateInterpolator()

        val DEFAULT_ANIMATION_DURATION: Long = 280

        private val TAG = ZoomEngine::class.java.simpleName
        private val LOG = ZoomLogger.create(TAG)

        private const val NONE = 0
        private const val SCROLLING = 1
        private const val PINCHING = 2
        private const val ANIMATING = 3
        private const val FLINGING = 4

        private fun getStateName(@State state: Int): String {
            when (state) {
                NONE -> return "NONE"
                FLINGING -> return "FLINGING"
                SCROLLING -> return "SCROLLING"
                PINCHING -> return "PINCHING"
                ANIMATING -> return "ANIMATING"
            }
            return ""
        }

        //endregion

        //region Touch events and Gesture Listeners

        // Might make these public some day?
        private val TOUCH_NO = 0
        private val TOUCH_LISTEN = 1
        private val TOUCH_STEAL = 2
    }

    //endregion
}
/**
 * Notifies the helper of the content size (be it a child View, a Bitmap, or whatever else).
 * This is needed for the helper to start working.
 * Shorthand for [.setContentSize] without applying transformations.
 *
 * @param width  the content width
 * @param height the content height
 */