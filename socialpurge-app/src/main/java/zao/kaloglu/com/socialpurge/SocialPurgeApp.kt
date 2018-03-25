package zao.kaloglu.com.socialpurge

import android.content.Context
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.Drawer
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import zao.kaloglu.com.socialpurge.helpers.AppSettings
import zao.kaloglu.com.socialpurge.helpers.utils.FirebaseService
import zao.kaloglu.com.socialpurge.models.TwitterAccount


/**
 * Created by kaloglu on 22/10/2017.
 */
class SocialPurgeApp : MultiDexApplication(), RewardedVideoAdListener {
    private lateinit var mInterstitialAd: InterstitialAd
    private var mRewardedAd: RewardedVideoAd? = null

    fun getRewardedAd(context: Context): RewardedVideoAd {
        if (mRewardedAd == null)
            initRewardedAd(context)
        return mRewardedAd!!
    }

    private fun initRewardedAd(context: Context) {
        mRewardedAd = MobileAds.getRewardedVideoAdInstance(context)
        mRewardedAd?.rewardedVideoAdListener = this
    }


    private fun showRewardedVideoAd() {
        showRewardedVideoAd(null)
    }

    private fun showRewardedVideoAd(context: Context?) {
        if (context == null && mRewardedAd == null)
            return
        else if (context!=null && mRewardedAd==null)
            initRewardedAd(context)

        mRewardedAd?.loadAd(AppSettings.ADMOB_REWARDED_VIDEO_UNIT_ID, AdRequest.Builder().build())
    }

    private fun initInterstitialAd() {
        mInterstitialAd = InterstitialAd(baseContext)
        mInterstitialAd.adUnitId = AppSettings.ADMOB_INTERSTITIAL_UNIT_ID

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.e("Admob", "AdLoaded")
                mInterstitialAd.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.e("Admob", "onAdFailedToLoad: " + errorCode)
            }

            override fun onAdOpened() {
                Log.e("Admob", "AdOpened")
            }

            override fun onAdLeftApplication() {
                Log.e("Admob", "AdLeftApplication")
            }

            override fun onAdClosed() {
                showRewardedVideoAd()
            }
        }
    }

    fun showMobileAd(context: Context?) {
//        mInterstitialAd.loadAd(AdRequest.Builder().build())
        showRewardedVideoAd(context)
    }

    override fun onRewardedVideoAdClosed() {
        Log.e("Admob", "RewardedAdLoaded")
    }

    override fun onRewardedVideoAdLeftApplication() {
        Log.e("Admob", "RewardedVideoAdLeftApplication")
    }

    override fun onRewardedVideoAdLoaded() {

        mRewardedAd?.show()
//        with(activity!!) {
//            alert(
//                    "When you finished video ad, removed all of ads in app for 3 days!"
//            ) {
//                positiveButton("Remove Ads", { mRewardedAd.show() })
//                negativeButton("No Thanks", {})
//            }.show()
//        }

    }

    override fun onRewardedVideoAdOpened() {
        Log.e("Admob", "RewardedVideoAdOpened")
    }

    override fun onRewarded(rewardedItem: RewardItem?) {
        Log.e("Admob", "Rewarded " + rewardedItem?.amount + " " + rewardedItem?.type)
    }

    override fun onRewardedVideoStarted() {
        Log.e("Admob", "RewardedVideoStarted")
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        Log.e("Admob", "RewardedAdFailedLoad => " + p0)
    }

    private object Holder {
        val INSTANCE = SocialPurgeApp()
    }

    lateinit var accountHeader: AccountHeader
    lateinit var navigationDrawer: Drawer

    companion object {
        val instance: SocialPurgeApp by lazy { SocialPurgeApp.Holder.INSTANCE }
    }

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        val configSettings = FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build()
        FirebaseService().config.setConfigSettings(configSettings)
//        initRewardedAd()
//        initInterstitialAd()
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
