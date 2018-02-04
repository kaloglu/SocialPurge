package com.zsk.androtweet2.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.twitter.sdk.android.core.*
import com.zsk.androtweet2.R
import com.zsk.androtweet2.components.twitter.CustomTwitterApiClient
import com.zsk.androtweet2.helpers.bases.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.design.longSnackbar


/**
 * Created by kaloglu on 4.02.2018.
 */
class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_login)
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        progress_bar.visibility = VISIBLE
        twitterLogin.visibility = GONE
    }

    override fun initializeScreenObject() {
        twitterLogin.setOnClickListener {
            it.visibility = View.GONE
            progress_bar.visibility = VISIBLE
        }
        twitterLogin.callback = object : Callback<TwitterSession>() {
            override fun success(sessionResult: Result<TwitterSession>?) {
                if (sessionResult?.data == null)
                    return
                TwitterCore.getInstance().addApiClient(sessionResult.data, CustomTwitterApiClient())
                setResult(Activity.RESULT_OK)
            }

            override fun failure(exception: TwitterException?) {
                longSnackbar(twitterLogin, exception?.message!!)
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        twitterLogin.onActivityResult(requestCode, resultCode, data)
        finish()
    }
}
