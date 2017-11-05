package com.zsk.androtweet2

import android.content.Context
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import com.mikepenz.iconics.context.IconicsContextWrapper
import com.mikepenz.iconics.context.IconicsLayoutInflater2

open class BaseActivity : AppCompatActivity() {
    private object Holder {val INSTANCE = BaseActivity()}

    val NO_USER: Long = -1
    val RC_SIGN_IN: Int = 100
    val LOGIN: Long = -99999
    val LOGOUT: Long = -99998
    val androTweetApp = AndroTweetApp.instance

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, IconicsLayoutInflater2(getDelegate()));

        super.onCreate(savedInstanceState)
    }

    companion object {
        val baseActivity: BaseActivity by lazy { BaseActivity.Holder.INSTANCE }
    }
}
