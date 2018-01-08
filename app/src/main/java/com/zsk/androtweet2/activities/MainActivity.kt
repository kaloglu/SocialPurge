package com.zsk.androtweet2.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.firebase.ui.auth.AuthUI
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
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.User
import com.zsk.androtweet2.BuildConfig
import com.zsk.androtweet2.R
import com.zsk.androtweet2.SplashScreen
import com.zsk.androtweet2.components.SimpleChildEventListener
import com.zsk.androtweet2.helpers.bases.BaseActivity
import com.zsk.androtweet2.helpers.utils.Enums.DrawItemTypes.*
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.TWEET
import com.zsk.androtweet2.helpers.utils.FirebaseService
import com.zsk.androtweet2.helpers.utils.FontIconDrawable
import com.zsk.androtweet2.models.TwitterAccount
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast


open class MainActivity : BaseActivity(), Drawer.OnDrawerItemClickListener, AccountHeader.OnAccountHeaderListener {
    var selectedProfile = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar!!)
        DrawerImageLoader.init(PicassoLoader())
        createNavigationDrawer(savedInstanceState, toolbar)
        selectedProfile = getAppSettings()?.get("selectedProfile", -1L) as Long

    }

    override fun addEventListenerForFirebase() {
        super.addEventListenerForFirebase()
        with(firebaseService) {
            TWITTER_ACCOUNTS.putChildEventListener(object : SimpleChildEventListener {

                override fun onChildAdded(dataSnapShot: DataSnapshot?, p1: String?) {
                    androTweetApp.accountHeader.let { accountHeader ->
                        val index = if (accountHeader.profiles.count() >= 2) accountHeader.profiles.count() - 2 else 0
                        dataSnapShot?.getValue<TwitterAccount>(TwitterAccount::class.java)
                                ?.let { account ->
                                    accountHeader.addProfile(getProfileDrawerItem(account), index)
                                    index.inc()
                                }
                    }
                }

                override fun onChildChanged(dataSnapShot: DataSnapshot?, p1: String?) {
                    super.onChildChanged(dataSnapShot, p1)
                    androTweetApp.accountHeader.let { accountHeader ->
                        if (selectedProfile != -1L)
                            accountHeader.setActiveProfile(selectedProfile, true)
                    }
                }

                override fun onChildRemoved(dataSnapShot: DataSnapshot?) {
                    androTweetApp.accountHeader.let { accountHeader ->
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

    override fun initializeScreenObject() {
        twitterLogin.callback = object : TwitterLoginCallBack(firebaseService) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        twitterLogin.onActivityResult(requestCode, resultCode, data)
    }

    private fun createNavigationDrawer(savedInstanceState: Bundle?, toolbar: Toolbar) {
        androTweetApp.accountHeader = createAccountHeader(savedInstanceState)
        androTweetApp.navigationDrawer = createDrawer(toolbar, androTweetApp.accountHeader, savedInstanceState)
    }

    private fun createAccountHeader(savedInstanceState: Bundle?): AccountHeader {
        return AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.md_red_500)
                .addProfiles(
//                        Settings for Account
                        ProfileSettingDrawerItem().withName("Add Account")
                                .withIdentifier(ADD_TWITTER_ACCOUNT)
                                .withIcon(FontIconDrawable(this, getString(R.string.ic_user_plus))),
                        ProfileSettingDrawerItem().withName("Manage Account")
                                .withIdentifier(MANAGE_ACCOUNTS)
                                .withDescription("Add / Remove your accounts")
                                .withIcon(FontIconDrawable(this, getString(R.string.ic_cog)))
                )
                .withOnAccountHeaderListener(this)
                .withAlternativeProfileHeaderSwitching(true)
                .withCurrentProfileHiddenInList(true)
                .withThreeSmallProfileImages(true)
                .withSavedInstance(savedInstanceState)
                .build()
    }

    open fun signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener({ _ ->
                    startActivity(Intent(this, SplashScreen::class.java))
                    finish()
                })
    }

    private fun createDrawer(toolbar: Toolbar, headerResult: AccountHeader, savedInstanceState: Bundle?): Drawer {
        return DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult, true)
                .inflateMenu(R.menu.activity_main_drawer)
                .addStickyDrawerItems(PrimaryDrawerItem().withName("Logout").withIdentifier(LOGOUT))
                .withOnDrawerItemClickListener(this)
                .withSavedInstance(savedInstanceState)
                .withCloseOnClick(true)
                .withFullscreen(false)
                .build()
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
        when (drawerItem?.identifier) {
            LOGOUT -> signOut()
            ADD_TWITTER_ACCOUNT -> twitterLogin.callOnClick()
            MANAGE_ACCOUNTS -> getManageActivity()
        }
        return false
    }

    private fun getManageActivity() {
        startActivity(Intent(this, ManageTwitterAccounts::class.java))
    }

    override fun onProfileChanged(view: View?, profile: IProfile<*>?, current: Boolean): Boolean {
        profile.let {
            if (it is ProfileSettingDrawerItem) return onItemClick(view, -1, it)

            if (it is ProfileDrawerItem) {
                getAppSettings()?.put("selectedProfile", it.identifier)
                val profileModel = it.tag

                when (profileModel) {
                    is TwitterAccount -> {
                        androTweetApp.initializeActiveUserAccount(profileModel)

                        startFragment(TWEET.twitter_timeline())

                        if (BuildConfig.DEBUG)
                            toast(profileModel.name)
                    }
                }
            }
        }
        return false
    }

    open class TwitterLoginCallBack(private val firebaseServ: FirebaseService) : Callback<TwitterSession>() {
        override fun success(sessionResult: Result<TwitterSession>?) {
            if (sessionResult?.data == null)
                return

            TwitterApiClient(sessionResult.data).accountService.verifyCredentials(true, true, true)
                    .enqueue(object : Callback<User>() {
                        override fun success(userResult: Result<User>?) {
                            userResult?.data?.let { user ->
                                with(firebaseServ) {
                                    TWITTER_ACCOUNTS?.updateWithUID(TwitterAccount(user, sessionResult.data.authToken))
                                }
                            }
                        }

                        override fun failure(exception: TwitterException?) {
                            Log.e(baseActivity.TAG, "exception", exception)
                        }
                    })

        }

        override fun failure(exception: TwitterException?) {
        }

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
