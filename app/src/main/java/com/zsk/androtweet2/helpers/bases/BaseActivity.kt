package com.zsk.androtweet2.helpers.bases

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.mikepenz.iconics.context.IconicsContextWrapper
import com.mikepenz.iconics.context.IconicsLayoutInflater2
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterAuthException
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.zsk.androtweet2.AndroTweetApp
import com.zsk.androtweet2.fragments.BaseFragment
import com.zsk.androtweet2.fragments.TwitterTimelineFragment
import com.zsk.androtweet2.helpers.utils.FirebaseService
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.email

open class BaseActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName
    val TOKEN_ERROR = "Failed to get request token"
    val CANCEL_LOGIN = "Failed to get authorization, bundle incomplete"

    private object Holder {
        val INSTANCE = BaseActivity()
    }

    companion object {
        val baseActivity: BaseActivity by lazy { Holder.INSTANCE }

        var firebaseService = FirebaseService()
        val androTweetApp = AndroTweetApp.instance

    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

    fun getTwitterSettings(): SharedPreferences? =
            getSharedPreferences("twitter_settings", Context.MODE_PRIVATE)

    fun getAppSettings(): SharedPreferences? =
            getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, IconicsLayoutInflater2(delegate))
        initializeScreenObject()
        initFirebase()

        super.onCreate(savedInstanceState)
    }

    open fun initFirebase() {
        addEventListenerForFirebase()
    }

    open fun addEventListenerForFirebase() {
    }

    open fun initializeScreenObject() {}

    open fun ImageView.loadFromUrl(context: Context, profilePic: String?) {
        if (!profilePic.isNullOrEmpty())
            Picasso.with(context).load(profilePic).into(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            handleTwitterAuthError(data)
        }
    }

    private fun handleTwitterAuthError(data: Intent?) {
        data!!.let {
            val hasError = data.hasExtra("auth_error")
            if (hasError) {
                val authException = data.getSerializableExtra("auth_error") as TwitterAuthException
                val errorMessage = authException.message
                twitterLogin?.let { view ->
                    when (errorMessage) {
                        TOKEN_ERROR -> handleTokenError(view, errorMessage)
                        CANCEL_LOGIN -> handleIncompleteLogin(view)
                        else -> handleTokenError(view, errorMessage)
                    }
                }
            }
        }
    }

    private fun handleIncompleteLogin(view: View): Snackbar {
        return longSnackbar(
                view,
                "Twitter login process incomplete",
                "Try again"
        ) {
            when (view) {
                is TwitterLoginButton -> {
                    view.callOnClick()
                }
                else -> {
                }
            }
        }
    }

    private fun handleTokenError(view: View, errorMessage: String?): Snackbar {
        return longSnackbar(
                view,
                "Temporarily unable to support.\n Contact: support@androtweet.net",
                "Report"
        ) {
            email(
                    "support@androtweet.net",
                    "App Working Issue[SplashScreen]",
                    "Can not add Twitter account!\n Reason[$errorMessage]"
            )
        }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    fun AppCompatActivity.startFragment(fragment: Fragment, containerViewId: Int = fragment_container.id) =
            supportFragmentManager.let({
                it.inTransaction({
                    fragment.let {
                        when {
                            it.isAdded -> replace(containerViewId, it, it.id.toString())
                            else -> add(containerViewId, it, it.id.toString())
                        }
                    }
                })
            })

    /** use Long with {@code @Enum.FragmentContentTypes} {@link FragmentContentTypes com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes}*/
    fun Long.twitterTimeline(): BaseFragment = TwitterTimelineFragment().getInstance(this)

    internal fun SharedPreferences.put(key: String, value: Any) {

        val editor = this.edit()

        when (value) {
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is String -> editor.putString(key, value)
        }

        editor.apply()
    }

    internal fun SharedPreferences.get(key: String, default: Any): Any = when (default) {
        is Long -> this.getLong(key, default)
        is Float -> this.getFloat(key, default)
        is Boolean -> this.getBoolean(key, default)
        is Int -> this.getInt(key, default)
        is String -> this.getString(key, default)
        else -> ""
    }
}
