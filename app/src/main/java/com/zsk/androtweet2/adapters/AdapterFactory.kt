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

}
