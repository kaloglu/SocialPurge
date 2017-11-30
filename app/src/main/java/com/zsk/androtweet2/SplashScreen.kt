package com.zsk.androtweet2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import java.util.*


class SplashScreen : BaseActivity() {
    var signInProviders = Arrays.asList(
            AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
            , AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
            , AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
//            AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
//            , AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()
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

            initTwitter(intent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}


