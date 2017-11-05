package com.zsk.androtweet

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import com.firebase.ui.auth.AuthUI
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.squareup.picasso.Picasso
import org.jetbrains.anko.toast


open class MainActivity : BaseActivity(), Drawer.OnDrawerItemClickListener {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        DrawerImageLoader.init(PicassoLoader())

        createNavigationDrawer(savedInstanceState, toolbar)

        super.onCreate(savedInstanceState)
    }


    private fun createNavigationDrawer(toolbar: Toolbar) {
        createNavigationDrawer(null, toolbar)
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
                        /**ProfileDrawerItem().withName("User added not yet").withIdentifier(NO_USER),*/

//                        Settings for Account
                        ProfileSettingDrawerItem().withName("Add Account")
                                .withIcon(FontIconDrawable(this, getString(R.string.ic_user_plus))),
                        ProfileSettingDrawerItem().withName("Manage Account")
                                .withDescription("Add / Remove your accounts")
                                .withIcon(FontIconDrawable(this, getString(R.string.ic_cog)))
                )
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
                })
    }

    private fun createDrawer(toolbar: Toolbar, headerResult: AccountHeader, savedInstanceState: Bundle?): Drawer {
        return DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult, true)
                .inflateMenu(R.menu.activity_main_drawer)
                .addStickyDrawerItems(
                        PrimaryDrawerItem().withName("Logout").withIdentifier(LOGOUT)
                )
                .withOnDrawerItemClickListener(this)
                .withSavedInstance(savedInstanceState)
                .withCloseOnClick(true)
                .withFullscreen(false)
                .build()
    }

    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
        when (drawerItem?.identifier) {
            baseActivity.LOGOUT -> signOut()
        }

        view?.context?.toast("clicked ${drawerItem?.identifier} +++ ${view.id} (${AndroTweetApp.instance.accountHeader.activeProfile?.name})")
        return false
    }

    class PicassoLoader : AbstractDrawerImageLoader() {

        override fun set(imageView: ImageView?, uri: Uri?, placeholder: Drawable?, tag: String?) {
            Picasso.with(imageView?.context).load(uri).placeholder(placeholder).into(imageView)
        }

        override fun cancel(imageView: ImageView?) {
            Picasso.with(imageView?.context).cancelRequest(imageView)
        }
        /*
        @Override
        public Drawable placeholder(Context ctx) {
            return super.placeholder(ctx);
        }

        @Override
        public Drawable placeholder(Context ctx, String tag) {
            return super.placeholder(ctx, tag);
        }
        */
    }
}
