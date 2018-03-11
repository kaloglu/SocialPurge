package zao.kaloglu.com.socialpurge.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import com.google.firebase.database.DataSnapshot
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import zao.kaloglu.com.socialpurge.BuildConfig
import zao.kaloglu.com.socialpurge.components.SimpleChildEventListener
import zao.kaloglu.com.socialpurge.helpers.bases.BaseActivity
import zao.kaloglu.com.socialpurge.helpers.utils.Enums.DrawItemTypes.LOGOUT
import zao.kaloglu.com.socialpurge.helpers.utils.Enums.FragmentContentTypes.TWEET
import zao.kaloglu.com.socialpurge.models.TwitterAccount


open class MainActivity : BaseActivity(), Drawer.OnDrawerItemClickListener, AccountHeader.OnAccountHeaderListener {
    var selectedProfile = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(zao.kaloglu.com.socialpurge.R.layout.activity_main)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar!!)
        DrawerImageLoader.init(zao.kaloglu.com.socialpurge.activities.MainActivity.PicassoLoader())
        createNavigationDrawer(savedInstanceState, toolbar)
        selectedProfile = getAppSettings()?.get("selectedProfile", -1L) as Long

//        mInterstitialAd = InterstitialAd(this)
//        mInterstitialAd.adUnitId = AppSettings.ADMOB_INTERSTITIAL_UNIT_ID
//
//        mInterstitialAd.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                Log.e("Admob", "AdLoaded")
//                mInterstitialAd.show()
//            }
//
//            override fun onAdFailedToLoad(errorCode: Int) {
//                Log.e("Admob", "onAdFailedToLoad: " + errorCode)
//            }
//
//            override fun onAdOpened() {
//                Log.e("Admob", "AdOpened")
//            }
//
//            override fun onAdLeftApplication() {
//                Log.e("Admob", "AdLeftApplication")
//            }
//
//            override fun onAdClosed() {
//                Log.e("Admob", "AdClosed")
//            }
//        }
//
//        mInterstitialAd.loadAd(AdRequest.Builder().build())

    }

    override fun addEventListenerForFirebase() {
        super.addEventListenerForFirebase()
        firebaseService.apply {
            PROFILES.putChildEventListener(object : SimpleChildEventListener {

                override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                    socialPurgeApp.accountHeader.let {
                        val index: Int = when {
                            it.profiles.count() >= 2 -> {
                                it.profiles.count() - 2
                            }
                            else -> 0
                        }
                        dataSnapshot?.getValue<TwitterAccount>(TwitterAccount::class.java)
                                ?.let { account ->
                                    account.id = dataSnapshot.key.toLong()
                                    it.addProfile(getProfileDrawerItem(account), index)
                                    index.inc()
                                }
                        if (selectedProfile != -1L)
                            it.setActiveProfile(selectedProfile, true)
                    }
                }

                override fun onChildChanged(dataSnapShot: DataSnapshot?, p1: String?) {
                    super.onChildChanged(dataSnapShot, p1)
//                    socialPurgeApp.accountHeader.let { accountHeader ->
//                        if (selectedProfile != -1L)
//                            accountHeader.setActiveProfile(selectedProfile, true)
//                    }
                }

                override fun onChildRemoved(dataSnapShot: DataSnapshot?) {
                    socialPurgeApp.accountHeader.let { accountHeader ->
                        dataSnapShot?.getValue<TwitterAccount>(TwitterAccount::class.java)
                                ?.let { account ->
                                    accountHeader.removeProfileByIdentifier(account.id)
                                }
                    }
                }

            })
        }

    }

    private fun getProfileDrawerItem(account: TwitterAccount): IProfile<*> {
        return ProfileDrawerItem().withIdentifier(account.id)
                .withName(account.name)
                .withEmail(account.realname)
                .withIcon(account.profilePic)
                .withTag(account)
    }

    private fun createNavigationDrawer(savedInstanceState: Bundle?, toolbar: Toolbar?) {
        socialPurgeApp.accountHeader = createAccountHeader(savedInstanceState)
        socialPurgeApp.navigationDrawer = createDrawer(toolbar, socialPurgeApp.accountHeader, savedInstanceState)
    }

    private fun createAccountHeader(savedInstanceState: Bundle?): AccountHeader {
        return AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withOnAccountHeaderListener(this)
                .withCompactStyle(true)
                .withSelectionListEnabledForSingleProfile(false)
//                .withHeaderBackground(R.color.material_drawer_dark_background)
//                .addProfiles(
////                        Settings for Account
//                        ProfileSettingDrawerItem()
//                                .withName("Add Account")
//                                .withDescription("Coming soon...")
////                                .withTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
////                                .withDescriptionTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
//                                .withIdentifier(ADD_TWITTER_ACCOUNT)
//                                .withIcon(FontIconDrawable(this, getString(R.string.ic_user_plus)))
//                                .withEnabled(false),
//                        ProfileSettingDrawerItem().withName("Manage Account")
//                                .withIdentifier(MANAGE_ACCOUNTS)
//                                .withDescription("Add / Remove your accounts")
////                                .withTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
////                                .withDescriptionTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
//                                .withIcon(FontIconDrawable(this, getString(R.string.ic_cog)))
//                                .withDescription("Coming soon...")
//                                .withEnabled(false)
//                )
//                .withAlternativeProfileHeaderSwitching(true)
//                .withCurrentProfileHiddenInList(true)
//                .withThreeSmallProfileImages(true)
                .withSavedInstance(savedInstanceState)
                .build()
    }

    open fun signOut() {
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener({ _ ->
//                })
        firebaseService.auth.signOut().doAsyncResult {
            this.onComplete {
                startActivity(Intent(this@MainActivity, zao.kaloglu.com.socialpurge.SplashScreen::class.java))
                finish()
            }
        }
    }

    private fun createDrawer(toolbar: Toolbar?, headerResult: AccountHeader, savedInstanceState: Bundle?): Drawer {
//        getMenuItems()

        val withFullscreen = DrawerBuilder()
        withFullscreen.apply {
            withActivity(this@MainActivity)
            withHasStableIds(true)
            withFireOnInitialOnClick(true)
            withAccountHeader(headerResult, true)
            inflateMenu(zao.kaloglu.com.socialpurge.R.menu.activity_main_drawer)
            addStickyDrawerItems(PrimaryDrawerItem().withName("Logout").withIdentifier(LOGOUT))
            withOnDrawerItemClickListener(this@MainActivity)
            withSavedInstance(savedInstanceState)
            withCloseOnClick(true)
            withFullscreen(false)

            toolbar?.let {
                withToolbar(toolbar)

            }
        }
        return withFullscreen
                .build()
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {

        when (drawerItem?.identifier) {
            zao.kaloglu.com.socialpurge.R.id.all_tweets_by_me.toLong() -> {
                if (socialPurgeApp.accountHeader.activeProfile == null)
                    return false
                startFragment(TWEET.twitterTimeline())
            }
            LOGOUT -> signOut()
//            ADD_TWITTER_ACCOUNT -> twitterLogin.callOnClick()
//            MANAGE_ACCOUNTS -> getManageActivity()
        }
        return false
    }

    private fun getManageActivity() {
        startActivity(Intent(this, zao.kaloglu.com.socialpurge.activities.ManageTwitterAccounts::class.java))
    }

    override fun onProfileChanged(view: View?, profile: IProfile<*>?, current: Boolean): Boolean {
        profile.let {
            if (it is ProfileSettingDrawerItem) return onItemClick(view, -1, it)

            if (it is ProfileDrawerItem) {
                getAppSettings()?.put("selectedProfile", it.identifier)
                val profileModel = it.tag

                when (profileModel) {
                    is TwitterAccount -> {
                        val userId = profileModel.id.toString()
                        firebaseService.apply {
                            DELETION_QUEUE.orderByChild("userId").equalTo(userId)
                                    .addChildEventListener(object : zao.kaloglu.com.socialpurge.components.SimpleChildEventListener {
                                        override fun onChildAdded(dataSnapShot: DataSnapshot?, p1: String?) {
                                            dataSnapShot?.let {
                                                socialPurgeApp.deleteQueue.add(it.key)
                                            }
                                        }

                                        override fun onChildRemoved(dataSnapShot: DataSnapshot?) {
                                            dataSnapShot?.let {
                                                socialPurgeApp.deleteQueue.remove(it.key)
                                            }
                                        }
                                    })
                        }

                        socialPurgeApp.initializeActiveUserAccount(profileModel)
                                .doAsyncResult {
                                    this.uiThread {
                                        socialPurgeApp.navigationDrawer.setSelection(zao.kaloglu.com.socialpurge.R.id.all_tweets_by_me.toLong())
                                    }
                                }

                        if (BuildConfig.DEBUG)
                            toast(profileModel.name)
                    }
                }
            }
        }
        return true
    }

    class PicassoLoader : AbstractDrawerImageLoader() {

        override fun set(imageView: ImageView?, uri: Uri?, placeholder: Drawable?, tag: String?) {
            Picasso.with(imageView?.context).load(uri).placeholder(placeholder).into(imageView)
        }

        override fun cancel(imageView: ImageView?) {
            Picasso.with(imageView?.context).cancelRequest(imageView)
        }
    }

}

