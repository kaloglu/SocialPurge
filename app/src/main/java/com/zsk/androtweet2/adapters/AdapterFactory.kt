package com.zsk.androtweet2.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline

/**
 * Created by kaloglu on 1.01.2018.
 */
class AdapterFactory {

    companion object {
        lateinit var adapter: RecyclerView.Adapter<*>
    }

    fun create(context: Context, timeline: Timeline<Tweet>): RecyclerView.Adapter<*> {
        adapter = TweetTimelineAdapter(context, timeline)

        return adapter
    }

//    private class TweetTimelineAdapter(
//            context: Context?,
//            timeline: Timeline<Tweet>?,
//            cb: Callback<Tweet>?
//    ) : TweetTimelineAdapter(context, timeline, R.style.tw__TweetLightStyle, cb) {
//
//        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TweetViewHolder {
//            val tweet = TweetBuilder().build()
//            val compactTweetView = CompactTweetView(context, tweet)
//            return TweetViewHolder(compactTweetView as com.twitter.sdk.android.tweetui.CompactTweetView)
//        }
//
//        override fun getItemCount(): Int {
//            return super.getItemCount()
//        }
//
//        override fun refresh(cb: Callback<TimelineResult<Tweet>>?) {
//            super.refresh(cb)
//        }
//
//        override fun onBindViewHolder(holder: TweetViewHolder?, position: Int) {
//            super.onBindViewHolder(holder, position)
//        }
//    }
}
