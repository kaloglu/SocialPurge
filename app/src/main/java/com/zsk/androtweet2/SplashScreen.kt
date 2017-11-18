package com.zsk.androtweet2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.zsk.androtweet2.models.TwitterConsumer
import java.util.*


class SplashScreen : BaseActivity() {
    var signInProviders = Arrays.asList(
            AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
//            , AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//            AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
//            AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
//            AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        if (firebaseService.currentUser != null) {
            onActivityResult(-1, -1, intent)
        } else {
            val build = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(signInProviders)
                    .setIsSmartLockEnabled(true, true)
                    .setAllowNewEmailAccounts(true)
                    .build()

            startActivityForResult(
                    build,
                    RC_SIGN_IN)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var idpResponse: IdpResponse? = null
        val intent = Intent(this, MainActivity::class.java)
        when (requestCode) {
            RC_SIGN_IN -> idpResponse = IdpResponse.fromResultIntent(data)
        }
        idpResponse?.let { intent.putExtra("my_token", idpResponse!!.idpToken) }

        if (resultCode == Activity.RESULT_OK) {

            getTwitterConsumers(intent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getTwitterConsumers(intent: Intent) {
        with(firebaseService) {
            CONSUMERS.getDatabaseReference().addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(dataSnapShot: DataSnapshot?) {

                    dataSnapShot!!.children.forEach { snapshot: DataSnapshot? ->
                        androTweetApp.twitterConsumers.add(snapshot!!.getValue<TwitterConsumer>(TwitterConsumer::class.java)!!)
                    }

                    initTwitter()
                    startActivity(intent)
                }
            })
        }
    }
}

