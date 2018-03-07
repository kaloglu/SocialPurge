package com.zsk.androtweet2.components.twitter

import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import com.zsk.androtweet2.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


/**
 * Created by kaloglu on 1.01.2018.
 */
class CustomTwitterApiClient(
        session: TwitterSession=TwitterCore.getInstance().sessionManager.activeSession,
        client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(
                        HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC)
                )
                .build()!!
) : TwitterApiClient(session, client) {

    /**
     * Provide CustomService with defined endpoints
     */
    fun getUserService(): UserService = getService(UserService::class.java)

    fun getTimeLineService(): TimelineService = getService(TimelineService::class.java)

}
