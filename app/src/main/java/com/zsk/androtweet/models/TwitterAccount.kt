package com.zsk.androtweet.models

import com.twitter.sdk.android.core.TwitterAuthToken

/**
 * Created by kaloglu on 12/11/2017.
 */
data class TwitterAccount(
        val id: Long = 0,
        val name: String? = null,
        val realname: String? = null,
        val profilePic: String? = null,
        val authToken: CustomAuthToken? = null
) {
    class CustomAuthToken : TwitterAuthToken {
        constructor() : super("", "")
        constructor(authToken: TwitterAuthToken?) : super(authToken?.token, authToken?.secret)
    }
}
