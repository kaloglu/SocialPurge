package zao.kaloglu.com.socialpurge

import android.support.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.Drawer
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import zao.kaloglu.com.socialpurge.helpers.utils.FirebaseService
import zao.kaloglu.com.socialpurge.models.TwitterAccount


/**
 * Created by kaloglu on 22/10/2017.
 */
class SocialPurgeApp : MultiDexApplication() {

    private object Holder {
        val INSTANCE = zao.kaloglu.com.socialpurge.SocialPurgeApp()
    }

    lateinit var accountHeader: AccountHeader
    lateinit var navigationDrawer: Drawer

    companion object {
        val instance: zao.kaloglu.com.socialpurge.SocialPurgeApp by lazy { zao.kaloglu.com.socialpurge.SocialPurgeApp.Holder.INSTANCE }
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
        activeTwitterProfile.addApiClient(session, zao.kaloglu.com.socialpurge.components.twitter.CustomTwitterApiClient(session))
    }

    val deleteQueue: zao.kaloglu.com.socialpurge.components.List<String> = zao.kaloglu.com.socialpurge.components.List()

}
