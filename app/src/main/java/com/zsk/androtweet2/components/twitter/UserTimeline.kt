/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.zsk.androtweet2.components.twitter

import android.util.Log
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import retrofit2.Call

/**
 * UserTimeline provides a timeline of tweets from the statuses/userTimeline API source.
 */
class UserTimeline : BaseTimeline(), Timeline<Tweet> {

    /**
     * Loads Tweets with id greater than (newer than) sinceId. If sinceId is null, loads the newest
     * Tweets.
     * @param sinceId minimum id of the Tweets to load (exclusive).
     * @param cb callback.
     */
    override fun next(sinceId: Long?, cb: Callback<TimelineResult<Tweet>>) {
        createUserTimelineRequest(sinceId, null).enqueue(BaseTimeline.TweetsCallback(cb))
    }

    /**
     * Loads Tweets with id less than (older than) maxId.
     * @param maxId maximum id of the Tweets to load (exclusive).
     * @param cb callback.
     */
    override fun previous(maxId: Long?, cb: Callback<TimelineResult<Tweet>>) {
        // user timeline api provides results which are inclusive, decrement the maxId to get
        // exclusive results
        createUserTimelineRequest(null, BaseTimeline.decrementMaxId(maxId)).enqueue(BaseTimeline.TweetsCallback(cb))
    }

    override fun getTimelineType(): String = SCRIBE_SECTION

    private val TAG: String? = this.javaClass.simpleName

    private fun createUserTimelineRequest(sinceId: Long?, maxId: Long?): Call<List<Tweet>> {
        val twitterCore = TwitterCore.getInstance()
        val activeSession = twitterCore.sessionManager.activeSession
        val userName = activeSession.userName
        val userId = activeSession.userId
        Log.e(TAG, "screenName: " + userName)
        val twitterApiClient = twitterCore.apiClient as CustomTwitterApiClient
        return twitterApiClient.getTimeLineService().userTimeline(userId, userName, sinceId, maxId, 30)
    }

    companion object {
        private val SCRIBE_SECTION = "user"
    }
}
