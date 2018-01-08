package com.zsk.androtweet2

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.Drawer
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import com.zsk.androtweet2.components.twitter.TwitterApiClient
import com.zsk.androtweet2.helpers.utils.FirebaseService
import com.zsk.androtweet2.models.TwitterAccount


/**
 * Created by kaloglu on 22/10/2017.
 */
class AndroTweetApp : Application() {

    private object Holder {
        val INSTANCE = AndroTweetApp()
    }

    lateinit var accountHeader: AccountHeader
    lateinit var navigationDrawer: Drawer

    companion object {
        val instance: AndroTweetApp by lazy { Holder.INSTANCE }
    }

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        val configSettings = FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build()
        FirebaseService().config.setConfigSettings(configSettings)
        super.onCreate()
    }

    fun initializeActiveUserAccount(activeAccount: TwitterAccount) {
        val activeTwitterProfile = TwitterCore.getInstance()
        val session = with(activeAccount, { TwitterSession(twitterAuth(), id, name) })

        activeTwitterProfile.sessionManager.activeSession = session
        activeTwitterProfile.addApiClient(session, TwitterApiClient(session))
    }

}
