package com.zsk.androtweet2.components.twitter

import android.content.Context
import android.view.ViewGroup
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter

/**
 * Created by kaloglu on 1.01.2018.
 */
private class TweetTimelineRecyclerViewAdapter(
        context: Context?,
        timeline: Timeline<Tweet>?
) : TweetTimelineRecyclerViewAdapter(context, timeline) {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TweetViewHolder {
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun refresh(cb: Callback<TimelineResult<Tweet>>?) {
        super.refresh(cb)
    }

    override fun onBindViewHolder(holder: TweetViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)
    }
}
