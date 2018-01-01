package com.zsk.androtweet2.components.twitter

import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterSession

/**
 * Created by kaloglu on 1.01.2018.
 */
class TwitterApiClient(session: TwitterSession) : TwitterApiClient(session) {
    /**
     * Provide CustomService with defined endpoints
     */
    fun getUserService(): UserService {
        return getService(UserService::class.java)
    }
}
