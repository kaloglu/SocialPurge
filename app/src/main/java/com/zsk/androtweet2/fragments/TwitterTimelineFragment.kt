package com.zsk.androtweet2.fragments

import com.twitter.sdk.android.tweetui.UserTimeline
import com.zsk.androtweet2.R
import com.zsk.androtweet2.adapters.AdapterFactory
import com.zsk.androtweet2.helpers.bases.BaseActivity
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.TWEET
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes.TWITTER

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
class TwitterTimelineFragment : TimelineFragment() {
    override val layoutId: Int
        get() = R.layout.twitter_timeline_layout

    fun getInstance(@FragmentContentTypes content_type: Long = TWEET) = super.getInstance(TWITTER, content_type)

    override fun initializeScreenObjects() {
        super.initializeScreenObjects()
    }

    override fun designScreen() {
        timeline_tweet = UserTimeline.Builder()
                .includeRetweets(true)
                .includeReplies(true)
                .userId(BaseActivity.androTweetApp.accountHeader.activeProfile.identifier)
                .build()

        adapter = AdapterFactory().create(context, timeline_tweet)
        super.designScreen()
    }

}


