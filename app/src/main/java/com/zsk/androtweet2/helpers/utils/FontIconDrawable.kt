package com.zsk.androtweet2.helpers.utils

/**
 * Created by kaloglu on 04/11/2017.
 */

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.annotation.Dimension.DP
import android.support.annotation.Dimension.PX
import android.support.v4.content.ContextCompat
import android.text.TextPaint
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import com.zsk.androtweet2.Sealed.Enums
import com.zsk.androtweet2.components.CustomTypeFace

/**
 * A custom [Drawable] which can display icons from icon fonts.
 */
class FontIconDrawable @JvmOverloads constructor(
        context: Context,
        icon: String = "",
        color: Int = -1,
        isResourceName: Boolean = context.resources.getIdentifier("ic_" + icon, "string", context.packageName) != 0
) : Drawable() {
    private var mContext: Context? = context
    private var mSizeX = -1
    private var mSizeY = -1

    private var mRespectFontBounds = false

    /**
     * Returns the icon color
     */
    var color: Int = 0
        private set
    private var mIconPaint: Paint? = null
    private var mContourPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null

    private var mRoundedCornerRx = -1
    private var mRoundedCornerRy = -1

    private var mPaddingBounds: Rect? = null
    private var mPathBounds: RectF? = null

    private var mPath: Path? = null

    private var mIconPadding: Int = 0

    private var mIconOffsetX = 0
    private var mIconOffsetY = 0

    /**
     * just a helper method to get the alpha value
     *
     * @return
     */
    var compatAlpha = 255
        private set

    private var mDrawContour: Boolean = false

    /**
     * @return the PlainIcon which is used inside this FontIconDrawable
     */
    var plainIcon: String? = null
        private set

    private var mTint: ColorStateList? = null
    private var mTintMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN
    private var mTintFilter: ColorFilter? = null
    private var mColorFilter: ColorFilter? = null
    private var DEFAULT_SIZE = 0


    private fun prepare() {
        DEFAULT_SIZE = 40// buraya ekran boyua göre özel hesaplama gelecek.
        mIconPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mIconPaint!!.style = Paint.Style.FILL
        mIconPaint!!.textAlign = Paint.Align.CENTER
        mIconPaint!!.isUnderlineText = false
        mIconPaint!!.isAntiAlias = true
        mIconPaint!!.color = color

        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        mContourPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mContourPaint!!.style = Paint.Style.STROKE

        mPath = Path()

        mPathBounds = RectF()
        mPaddingBounds = Rect()
    }

    /**
     * Loads and draws given.
     *
     * @param icon
     * @return The current IconExtDrawable for chaining.
     */
    fun icon(icon: String): FontIconDrawable = iconText(icon)

    /**
     * Loads and draws given text
     *
     * @param icon
     * @return The current IconExtDrawable for chaining.
     */
    fun iconText(icon: String?): FontIconDrawable {
        plainIcon = icon
        //        mIconPaint.setTypeface(Typeface.DEFAULT);
        invalidateSelf()
        return this
    }

    /**
     * Set the color of the drawable.
     *
     * @param color The color, usually from android.graphics.Color or 0xFF012345.
     * @return The current IconExtDrawable for chaining.
     */
    fun color(@ColorInt color: Int): FontIconDrawable {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        mIconPaint!!.color = Color.rgb(red, green, blue)
        this.color = color
        alpha = Color.alpha(color)
        invalidateSelf()
        return this
    }

    /*
    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color));
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
    */


    /**
     * Set the size of this icon to the standard Android ActionBar.
     *
     * @return The current IconExtDrawable for chaining.
     */
    @Deprecated("use actionBar() instead")
    fun actionBarSize(): FontIconDrawable = sizeDp(ANDROID_ACTIONBAR_ICON_SIZE_DP.toFloat())


    /**
     * Set the size of the drawable.
     *
     * @param size The size in density-independent pixels (dp).
     * @return The current IconExtDrawable for chaining.
     */
    fun sizeDp(@Dimension(unit = DP) size: Float): FontIconDrawable =
            sizePx(convertDpToPx(mContext, size))

    /**
     * Set the size of the drawable.
     *
     * @param size The size in pixels (px).
     * @return The current IconExtDrawable for chaining.
     */
    fun sizePx(@Dimension(unit = PX) size: Int): FontIconDrawable {
        this.mSizeX = size
        this.mSizeY = size
        setBounds(0, 0, size, size)
        invalidateSelf()
        return this
    }


    /**
     * Sets the opacity
     *
     * @param alpha
     * @return The current IconExtDrawable for chaining.
     */
    fun alpha(alpha: Int): FontIconDrawable {
        setAlpha(alpha)
        return this
    }

    /**
     * Sets the style
     *
     * @param style
     * @return The current IconExtDrawable for chaining.
     */
    fun style(style: Paint.Style): FontIconDrawable {
        mIconPaint!!.style = style
        return this
    }

    /**
     * sets the typeface of the drawable
     * NOTE THIS WILL OVERWRITE THE ICONFONT!
     *
     * @param typeface
     * @return
     */
    fun typeface(typeface: Typeface): FontIconDrawable {
        mIconPaint?.typeface = typeface
        return this
    }

    override fun draw(canvas: Canvas) {
        if (plainIcon != null) {
            val viewBounds = bounds

            updatePaddingBounds(viewBounds)
            updateTextSize(viewBounds)
            offsetIcon(viewBounds)

            if (mBackgroundPaint != null && mRoundedCornerRy > -1 && mRoundedCornerRx > -1) {
                canvas.drawRoundRect(RectF(0f, 0f, viewBounds.width().toFloat(), viewBounds.height().toFloat()), mRoundedCornerRx.toFloat(), mRoundedCornerRy.toFloat(), mBackgroundPaint!!)
            }

            mPath!!.close()

            if (mDrawContour) {
                canvas.drawPath(mPath!!, mContourPaint!!)
            }

            mIconPaint!!.alpha = compatAlpha
            mIconPaint!!.colorFilter = if (mColorFilter == null) mTintFilter else mColorFilter

            canvas.drawPath(mPath!!, mIconPaint!!)
        }
    }

    override fun setTint(tintColor: Int) {
        setTintList(ColorStateList.valueOf(tintColor))
    }

    override fun setTintList(tint: ColorStateList?) {
        mTint = tint
        mTintFilter = updateTintFilter(tint, mTintMode)
        invalidateSelf()
    }

    override fun setTintMode(tintMode: PorterDuff.Mode) {
        mTintMode = tintMode
        mTintFilter = updateTintFilter(mTint, tintMode)
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        offsetIcon(bounds)
        mPath!!.close()
        super.onBoundsChange(bounds)
    }

    override fun isStateful(): Boolean = true

    override fun setState(stateSet: IntArray): Boolean {
        alpha = compatAlpha
        return super.setState(stateSet)
    }

    override fun onStateChange(stateSet: IntArray): Boolean {
        if (mTint != null && mTintMode != null) {
            mTintFilter = updateTintFilter(mTint, mTintMode)
            invalidateSelf()
            return true
        }
        return false
    }

    override fun getIntrinsicWidth(): Int {
        if (mSizeX <= 0)
            mSizeX = mIconPaint!!.measureText(plainIcon).toInt()
        return mSizeX
    }

    override fun getIntrinsicHeight(): Int {
        if (mSizeY <= 0)
            mSizeX = (mIconPaint!!.fontMetrics.bottom - mIconPaint!!.fontMetrics.top).toInt()
        return mSizeY
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE


    override fun setAlpha(alpha: Int) {
        mIconPaint!!.alpha = alpha
        compatAlpha = alpha
        invalidateSelf()
    }

    override fun getAlpha(): Int = compatAlpha

    override fun setColorFilter(cf: ColorFilter?) {
        mColorFilter = cf
        invalidateSelf()
    }

    override fun clearColorFilter() {
        mColorFilter = null
        invalidateSelf()
    }

    //------------------------------------------
    // PRIVATE HELPER METHODS
    //------------------------------------------

    /**
     * Update the Padding Bounds
     *
     * @param viewBounds
     */
    private fun updatePaddingBounds(viewBounds: Rect) {
        if (mIconPadding >= 0
                && mIconPadding * 2 <= viewBounds.width()
                && mIconPadding * 2 <= viewBounds.height()) {
            mPaddingBounds!!.set(
                    viewBounds.left + mIconPadding,
                    viewBounds.top + mIconPadding,
                    viewBounds.right - mIconPadding,
                    viewBounds.bottom - mIconPadding)
        }
    }

    /**
     * Update the TextSize
     *
     * @param viewBounds
     */
    private fun updateTextSize(viewBounds: Rect) {
        var textSize = viewBounds.height().toFloat() * if (mRespectFontBounds) 1 else 2
        mIconPaint!!.textSize = textSize

        val textValue = plainIcon.toString()
        mIconPaint!!.getTextPath(textValue, 0, textValue.length, 0f, viewBounds.height().toFloat(), mPath)
        mPath!!.computeBounds(mPathBounds, true)

        if (!mRespectFontBounds) {
            val deltaWidth = mPaddingBounds!!.width().toFloat() / mPathBounds!!.width()
            val deltaHeight = mPaddingBounds!!.height().toFloat() / mPathBounds!!.height()
            val delta = if (deltaWidth < deltaHeight) deltaWidth else deltaHeight
            textSize *= delta

            mIconPaint!!.textSize = textSize

            mIconPaint!!.getTextPath(textValue, 0, textValue.length, 0f, viewBounds.height().toFloat(), mPath)
            mPath!!.computeBounds(mPathBounds, true)
        }
    }

    /**
     * Set the icon offset
     *
     * @param viewBounds
     */
    private fun offsetIcon(viewBounds: Rect) {
        val startX = viewBounds.centerX() - mPathBounds!!.width() / 2
        val offsetX = startX - mPathBounds!!.left

        val startY = viewBounds.centerY() - mPathBounds!!.height() / 2
        val offsetY = startY - mPathBounds!!.top

        mPath!!.offset(offsetX + mIconOffsetX, offsetY + mIconOffsetY)
    }


    /**
     * Ensures the tint filter is consistent with the current tint color and
     * mode.
     */
    private fun updateTintFilter(tint: ColorStateList?, tintMode: PorterDuff.Mode?): PorterDuffColorFilter? {
        if (tint == null || tintMode == null) {
            return null
        }
        // setMode, setColor of PorterDuffColorFilter are not public method in SDK v7. (Thanks @Google still not accessible in API v24)
        // Therefore we create a new one all the time here. Don't expect this is called often.
        val color = tint.getColorForState(state, Color.TRANSPARENT)
        return PorterDuffColorFilter(color, tintMode)
    }


    companion object {
        val ANDROID_ACTIONBAR_ICON_SIZE_DP = 24
        val ANDROID_ACTIONBAR_ICON_SIZE_PADDING_DP = 1

        fun convertDpToPx(context: Context?, dp: Float): Int =
                applyDimension(COMPLEX_UNIT_DIP, dp, context!!.resources.displayMetrics).toInt()
    }

    init {
        var iconstr = icon
        if (color == -1)
            this.color = com.mikepenz.materialdrawer.R.attr.material_drawer_primary_text
        else
            this.color = ContextCompat.getColor(context, color)

        prepare()
        CustomTypeFace.getTypeFace(context, Enums.FontType.FONT_ICON)?.let { typeface(it) }
        if (isResourceName)
            iconstr = mContext!!.getString(context.resources.getIdentifier("ic_" + iconstr, "string", context.packageName))
        icon(iconstr)
    }
}
