package zao.kaloglu.com.socialpurge.components

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by kaloglu on 21/10/2017.
 */
class CustomTextView : TextView {
    private var attrs: AttributeSet? = null

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {

        context?.theme?.obtainStyledAttributes(attrs, zao.kaloglu.com.socialpurge.R.styleable.View, 0, 0)?.let { obtainStyledAttr ->

            val fontTypeId = obtainStyledAttr.getInteger(zao.kaloglu.com.socialpurge.R.styleable.View_textType, 0)
            val fontType = zao.kaloglu.com.socialpurge.Sealed.Enums.FontType.values()[fontTypeId]
            if (fontTypeId > 0)
                setCustomTypeFace(getContext(), fontType)
            obtainStyledAttr.recycle()
        }
    }


    fun setCustomTypeFace(context: Context, fontType: zao.kaloglu.com.socialpurge.Sealed.Enums.FontType) {
        typeface = zao.kaloglu.com.socialpurge.components.CustomTypeFace.Companion.getTypeFace(context, fontType)
    }

    fun setFontIcon(fontIcon: String) {
        setFontIcon(fontIcon, 0)
    }

    fun setFontIcon(value: String?, color: Int) {
        var fontIcon = value
        var fontIconColor = color

        if (fontIcon == null)
            return

        if (fontIconColor == 0) {
            try {
                fontIconColor = ContextCompat.getColor(context, getColorRes(fontIcon))
                setTextColor(fontIconColor)
            } catch (ignored: Exception) {
            }

        } else {
            setTextColor(ContextCompat.getColor(context, fontIconColor))
        }

        if (!fontIcon.contains("ic_"))
            fontIcon = "ic_" + fontIcon

        try {
            fontIcon = context.getString(getStringRes(fontIcon))
        } catch (ignored: Exception) {
            if (fontIcon.contains("notify_")) {
                fontIcon = context.getString(getStringRes("ic_notify_default"))
            }
        }

        text = fontIcon


    }

    private fun getColorRes(fontIcon: String): Int =
            context.resources.getIdentifier(fontIcon, "color", context.packageName)

    private fun getStringRes(fontIcon: String): Int =
            context.resources.getIdentifier(fontIcon, "string", context.packageName)
}

