package com.zsk.androtweet2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.mikepenz.iconics.context.IconicsContextWrapper
import com.mikepenz.iconics.context.IconicsLayoutInflater2
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.email

open class BaseActivity : AppCompatActivity() {
    val TOKEN_ERROR = "Failed to get request token"
    val CANCEL_LOGIN = "Failed to get authorization, bundle incomplete"
    private object Holder {
        val INSTANCE = BaseActivity()
    }

    var firebaseService = FirebaseService()
    val RC_SIGN_IN: Int = 100
    val LOGOUT: Long = -99999
    val ADD_TWITTER_ACCOUNT: Long = -99998
    val MANAGE_TWITTER_ACCOUNT: Long = -99997
    val androTweetApp = AndroTweetApp.instance
    val TAG = "AndroTweet"

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }

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

    companion object {
        val baseActivity: BaseActivity by lazy { BaseActivity.Holder.INSTANCE }
    }

    open fun initializeScreenObject() {}

    open fun String?.getOrDefault(): String {
        if (this == null)
            return ""

        return this
    }

    open fun ImageView.loadFromUrl(context: Context, profilePic: String?) {
        if (!profilePic.isNullOrEmpty())
            Picasso.with(context).load(profilePic).into(this)
    }

    fun initTwitter(intent: Intent) {
        with(firebaseService) {
            var cacheSize = 3600L

            if (config.info.configSettings.isDeveloperModeEnabled)
                cacheSize = 0

            config.fetch(cacheSize).addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    val twitterAuthConfig = TwitterAuthConfig(config.getString("twitter_consumer_key"), config.getString("twitter_consumer_secret"))
                    val twitterConfig = TwitterConfig.Builder(this@BaseActivity)
                            .logger(DefaultLogger(Log.DEBUG))
                            .twitterAuthConfig(twitterAuthConfig)
                            .debug(BuildConfig.DEBUG)
                            .build()
                    Twitter.initialize(twitterConfig)
                    config.activateFetched()
                    startActivity(intent)
                } else
                    alert("Maintenance Time! plase try again few later...") {
                        negativeButton("Close App", {
                            finish()
                        })
                        positiveButton("Try Again", {
                            initTwitter(intent)
                        })
                        neutralPressed("Report", {
                            email(
                                    "support@androtweet.net",
                                    "App Working Issue[SplashScreen]",
                                    "App doesn't open I do not know why!"
                            )
                        })
                    }

            }.addOnFailureListener { exception ->
                alert("Something goes Wrong") {
                    negativeButton("Close App", {
                        finish()
                    })
                    positiveButton("Try Again", {
                        initTwitter(intent)
                    })
                    neutralPressed("Report", {
                        val errorMessage = exception.message
                        email(
                                "support@androtweet.net",
                                "App Working Issue[SplashScreen]",
                                "App doesn't open I do not know why!\n Reason[$errorMessage]"
                        )
                    })
                }
            }


        }

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
}
