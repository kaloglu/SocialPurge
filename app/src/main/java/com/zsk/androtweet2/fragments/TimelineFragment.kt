package com.zsk.androtweet2.fragments

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.zsk.androtweet2.R
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentItemTypes.LIST
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes
import com.zsk.androtweet2.helpers.utils.tweetui.TweetTimelineRecyclerViewAdapter
import com.zsk.androtweet2.helpers.utils.tweetui.UserTimeline

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
abstract class TimelineFragment : BaseFragment() {
    protected lateinit var timelineRV: RecyclerView
    protected lateinit var adapter: TweetTimelineRecyclerViewAdapter
    protected lateinit var timeline_tweet: UserTimeline

    fun getInstance(@FragmentTypes fragment_type: Long, @FragmentContentTypes content_type: Long) =
            super.getInstance(fragment_type, content_type, LIST)


    override fun initializeScreenObjects() {
        with(view!!) {
            timelineRV = findViewById(R.id.timeline_rv)
            timelineRV.layoutManager=LinearLayoutManager(this.context)
        }
    }

    override fun designScreen() {
        timelineRV.adapter = adapter
    }

}

