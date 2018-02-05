package com.zsk.androtweet2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.TwitterAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.iid.FirebaseInstanceId
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.User
import com.zsk.androtweet2.activities.LoginActivity
import com.zsk.androtweet2.activities.MainActivity
import com.zsk.androtweet2.components.twitter.CustomTwitterApiClient
import com.zsk.androtweet2.helpers.bases.BaseActivity
import com.zsk.androtweet2.helpers.utils.Enums.RequestCodes.RC_SIGN_IN
import com.zsk.androtweet2.models.TwitterAccount
import org.jetbrains.anko.alert
import org.jetbrains.anko.email


class SplashScreen : BaseActivity() {
//    var signInProviders = Arrays.asList(
//            AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
//             AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//             AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
//            AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
//            AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()
//    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        getRemoteSettings()
    }

    private fun getRemoteSettings() {
        with(firebaseService) {
            var cacheSize = 3600L

            if (config.info.configSettings.isDeveloperModeEnabled)
                cacheSize = 0

            config.fetch(cacheSize)
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            config.getKeysByPrefix("twitter_").asSequence().forEach { configKey ->
                                getTwitterSettings()?.let { twitterSettings ->
                                    when {
                                        twitterSettings.getString(configKey, "") != config.getString(configKey) -> {
                                            twitterSettings.edit().putString(configKey, config.getString(configKey)).apply()
                                        }
                                    }

                                }
                            }
                            config.activateFetched()
                            loadingComplete()

                        } else {
                            getMaintenanceAlert()
                        }

                    }
                    .addOnFailureListener { exception ->
                        handleException(exception.message)
                    }
        }
    }

    private fun getMaintenanceAlert() {
        alert("Maintenance Time! plase try again few later...") {
            negativeButton("Close App", {
                finish()
            })
            neutralPressed("Report", {
                email(
                        "support@androtweet.net",
                        "App Working Issue[SplashScreen]",
                        "App doesn't open I do not know why!"
                )
            })
        }.show()
    }

    private fun loadingComplete() {
        twitterImplementation()
        checkLogin()
    }

    private fun checkLogin() = if (firebaseService.isSignedIn()) {
        onActivityResult(-1, -1, intent)
    } else {
//        val build = AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(signInProviders)
//                .setIsSmartLockEnabled(true, true)
//                .setAllowNewEmailAccounts(true)
//                .build()

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    private fun handleException(errorMessage: String?) {
        alert("Something goes Wrong") {
            negativeButton("Close App", {
                finish()
            })
            positiveButton("Try Again", {
                twitterImplementation()
            })
            neutralPressed("Report", {
                email(
                        "support@androtweet.net",
                        "App Working Issue[SplashScreen]",
                        "App doesn't open I do not know why!\n Reason[$errorMessage]"
                )
            })
        }.show()
    }

    private fun twitterImplementation() {
        val twitterSettings = getTwitterSettings()
        val consumerKey = twitterSettings?.getString("twitter_consumer_key", "")
        val consumerSecret = twitterSettings?.getString("twitter_consumer_secret", "")

        if (consumerKey == "" || consumerSecret == "")
            handleException("does not get consumer keys")

        val twitterAuthConfig = TwitterAuthConfig(consumerKey, consumerSecret)
        val twitterConfig = TwitterConfig.Builder(this@SplashScreen)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(twitterAuthConfig)
                .debug(BuildConfig.DEBUG)
                .build()
        Twitter.initialize(twitterConfig)
        Twitter.getLogger().logLevel = Log.ASSERT

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == RC_SIGN_IN) {
                handleTwitterLogin()
            } else {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun handleTwitterLogin() {
        val session = TwitterCore.getInstance().sessionManager.activeSession
        getAppSettings()?.put("selectedProfile", session.userId)
        CustomTwitterApiClient(session).accountService
                .verifyCredentials(false, true, false)
                .enqueue(object : Callback<User>() {
                    override fun success(userResult: Result<User>?) {
                        val user = userResult?.data
                        if (user == null) {
                            failure(TwitterException("Twitter user data is null", NullPointerException()))
                            return
                        }
                        startFirebaseSignIn(TwitterAccount(user, session.authToken))
                    }

                    override fun failure(exception: TwitterException?) {
                        Log.e(baseActivity.TAG, "exception", exception)
                    }
                })
    }

    private fun startFirebaseSignIn(twitterAccount: TwitterAccount) {

        val credential = TwitterAuthProvider.getCredential(twitterAccount.token, twitterAccount.secret)
        val deviceTokenObject = HashMap<String, String>()
        deviceTokenObject[twitterAccount.idStr] = FirebaseInstanceId.getInstance().token!!

        firebaseService.apply {
            auth.signInWithCredential(credential).addOnSuccessListener {
                DEVICE_TOKENS.updateWithUID(deviceTokenObject)

                if (it.additionalUserInfo.isNewUser) {
                    PROFILES?.updateWithUID(twitterAccount,
                            DatabaseReference.CompletionListener { databaseError, databaseReference ->
                                if (databaseError == null) {
                                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                                    finish()
                                }
                            }
                    )
                } else {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                }
            }
        }

    }

}


