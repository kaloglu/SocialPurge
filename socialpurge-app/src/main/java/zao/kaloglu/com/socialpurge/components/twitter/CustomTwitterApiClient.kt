package zao.kaloglu.com.socialpurge.components.twitter

import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import zao.kaloglu.com.socialpurge.BuildConfig.DEBUG


/**
 * Created by kaloglu on 1.01.2018.
 */
class CustomTwitterApiClient(
        session: TwitterSession=TwitterCore.getInstance().sessionManager.activeSession,
        client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(
                        HttpLoggingInterceptor().setLevel(if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC)
                )
                .build()!!
) : TwitterApiClient(session, client) {


    /**
     * Provide CustomService with defined endpoints
     */
    fun getUserService(): UserService = getService(UserService::class.java)

    fun getTimeLineService(): TimelineService = getService(TimelineService::class.java)

}
