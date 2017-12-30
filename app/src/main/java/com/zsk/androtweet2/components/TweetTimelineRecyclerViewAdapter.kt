package com.zsk.androtweet2.components

import android.content.Context
import android.view.ViewGroup
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.TweetBuilder
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter

/**
 * Created by kaloglu on 30.12.2017.
 */
class TweetTimelineRecyclerViewAdapter(//Factory
        context: Context?,
        timeline_tweet: Timeline<Tweet>
) {
    var adapter: TweetTimelineRecyclerViewAdapter

    init {
        adapter = CustomTweetTimelineRecyclerViewAdapter(context, timeline_tweet)
    }

    private class CustomTweetTimelineRecyclerViewAdapter(
            context: Context?,
            timeline: Timeline<Tweet>?
    ) : TweetTimelineRecyclerViewAdapter(context, timeline) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TweetViewHolder? {
            val tweet = TweetBuilder().build()
            val compactTweetView = CustomCompactTweetView(context, tweet, styleResId)
            compactTweetView.setOnActionCallback(actionCallback)
            return TweetViewHolder(compactTweetView)
        }
    }

}


