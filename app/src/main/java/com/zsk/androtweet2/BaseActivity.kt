package com.zsk.androtweet2

import android.content.Context
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import com.mikepenz.iconics.context.IconicsContextWrapper
import com.mikepenz.iconics.context.IconicsLayoutInflater2

open class BaseActivity : AppCompatActivity() {
    private object Holder {val INSTANCE = BaseActivity()}

    val RC_SIGN_IN: Int = 100
    val LOGOUT: Long = -99998
    val androTweetApp = AndroTweetApp.instance
    val TAG = "AndroTweet"

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, IconicsLayoutInflater2(getDelegate()));
        initializeScreenObject()
        initFirebase()
        super.onCreate(savedInstanceState)
    }

    open fun initFirebase() {
        addEventListenerForFirebase()
    }

    open fun addEventListenerForFirebase() {
    }

    companion object {
        val baseActivity: BaseActivity by lazy { BaseActivity.Holder.INSTANCE }
    }

    open fun initializeScreenObject() {}
}
