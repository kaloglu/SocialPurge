package com.zsk.androtweet2.fragments

import android.support.v7.widget.RecyclerView
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import com.twitter.sdk.android.tweetui.TwitterListTimeline
import com.twitter.sdk.android.tweetui.UserTimeline
import com.zsk.androtweet2.R
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentItemTypes.LIST
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
abstract class TimelineFragment : BaseFragment() {
    protected lateinit var timeline_rv: RecyclerView
    protected lateinit var adapter: TweetTimelineRecyclerViewAdapter
    protected lateinit var timeline_tweet: UserTimeline

    fun getInstance(@FragmentTypes fragment_type: Long, @FragmentContentTypes content_type: Long) =
            super.getInstance(fragment_type, content_type, LIST)


    override fun initializeScreenObjects() {
        with(view!!) {
            timeline_rv = findViewById(R.id.timeline_rv)
            adapter = TweetTimelineRecyclerViewAdapter(context, timeline_tweet)
        }
    }

    override fun designScreen() {
        timeline_rv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}

