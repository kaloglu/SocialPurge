package com.zsk.androtweet2

import android.Manifest
import android.app.Application
import com.google.android.gms.analytics.Tracker
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.Drawer
import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterSession
import com.zsk.androtweet2.helpers.utils.FirebaseService
import com.zsk.androtweet2.models.FirebaseObject
import com.zsk.androtweet2.models.TwitterAccount


/**
 * Created by kaloglu on 22/10/2017.
 */
class AndroTweetApp : Application() {
    var activeUserAccountItem: TwitterApiClient? = null
    private var mTracker: Tracker? = null

    private object Holder {
        val INSTANCE = AndroTweetApp()
    }

    lateinit var accountHeader: AccountHeader
    lateinit var navigationDrawer: Drawer

    companion object {
        val instance: AndroTweetApp by lazy { Holder.INSTANCE }
    }

    private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        val configSettings = FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build()
        FirebaseService().config.setConfigSettings(configSettings)
        super.onCreate()
    }

    fun initializeActiveUserAccount(activeAccount: FirebaseObject) {
        val unit = when (activeAccount) {
            is TwitterAccount -> activeUserAccountItem = getActiveTwitterUserAccount(activeAccount)
//            is FacebookAccount -> getActiveFaceboookUserAccount(activeAccount)
//            is IsntagramAccount -> getActiveInstagramUserAccount(activeAccount)
            else -> null
        }
    }

    fun TwitterAccount.twitterAuth(): TwitterAuthToken = authToken?.let { TwitterAuthToken(it.token, it.secret) }!!

    private fun getActiveTwitterUserAccount(twitterAccount: TwitterAccount): TwitterApiClient =
            TwitterApiClient(with(twitterAccount, { TwitterSession(twitterAuth(), id, name) }))

//    private fun getActiveFacebookUserAccount(facebookAccount: FacebookAccount) {
//    TODO: Facebook session staff
//    }
//    private fun getActiveInstagramUserAccount(instagramAccount: InstagramAccount) {
//    TODO: Isntagram session staff
//    }

}
