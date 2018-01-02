package com.zsk.androtweet2.components.twitter

import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterSession
import com.zsk.androtweet2.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


val level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
val build = OkHttpClient.Builder()
        .addInterceptor(
                HttpLoggingInterceptor().setLevel(level)
        )
        .build()!!

/**
 * Created by kaloglu on 1.01.2018.
 */
class TwitterApiClient(
        session: TwitterSession,
        client: OkHttpClient = build
) : TwitterApiClient(session, client) {

    /**
     * Provide CustomService with defined endpoints
     */
    fun getUserService(): UserService = getService(UserService::class.java)

    fun getTimeLineService(): TimelineService = getService(TimelineService::class.java)

}
