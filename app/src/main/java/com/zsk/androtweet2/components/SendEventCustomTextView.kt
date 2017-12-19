package com.zsk.androtweet2.components

import android.util.Log
import android.view.View
import com.zsk.androtweet2.AndroTweetApp

class SendEventCustomTextView(private val onClickListener: View.OnClickListener?) : View.OnClickListener {

    override fun onClick(v: View?) {
        val cButton = v as CustomTextView

        if (!cButton.getEventAction().isNullOrBlank())
            Log.d("sendEvent","staffs.")
        onClickListener?.onClick(v)
    }

}
