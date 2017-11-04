package com.zsk.androtweet

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.squareup.picasso.Picasso
import org.jetbrains.anko.toast
import java.util.*


class MainActivity : BaseActivity() {
    val LOGIN: Long = -99999
    val LOGOUT: Long = -99998
    val androTweetApp = AndroTweetApp.instance
    var stickyItems: Map<Long, String> = mapOf(
            LOGIN to "Login",
            LOGOUT to "Logout"
    )
    var drawerItems = ArrayList<PrimaryDrawerItem>()

    init {
        stickyItems.forEach { entry ->
            drawerItems.add(PrimaryDrawerItem().withIdentifier(entry.key).withName(entry.value))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //initialize and create the image loader logic
        DrawerImageLoader.init(PicassoLoader())

        androTweetApp.accountHeader = createAccountHeader(savedInstanceState)
        androTweetApp.navigationDrawer = createDrawer(toolbar, androTweetApp.accountHeader, savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    private fun createAccountHeader(savedInstanceState: Bundle?): AccountHeader {
        return AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.md_red_500)
                .addProfiles(
                        ProfileDrawerItem().withName("User added not yet").withIdentifier(NO_USER),
                        ProfileDrawerItem().withName("User2").withIdentifier(NO_USER),
                        ProfileDrawerItem().withName("User3").withIdentifier(NO_USER),
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
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


    private fun createDrawer(toolbar: Toolbar, headerResult: AccountHeader, savedInstanceState: Bundle?): Drawer {
        return DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult, true) //set the
                .withStickyDrawerItems(drawerItems.toList())
                .withOnDrawerItemClickListener(DrawerItemClickListener())
                .withSavedInstance(savedInstanceState)
                .withCloseOnClick(true)
                .withFullscreen(false)
                .build()
    }

    open class DrawerItemClickListener : Drawer.OnDrawerItemClickListener {

        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
            view?.context?.toast("clicked ${drawerItem?.identifier} (${AndroTweetApp.instance.accountHeader.activeProfile.name})")
            return false
        }

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
