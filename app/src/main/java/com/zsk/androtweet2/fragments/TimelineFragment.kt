package com.zsk.androtweet2.fragments

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import com.zsk.androtweet2.R
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentItemTypes.LIST
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes
import kotlinx.android.synthetic.main.actions_bottom_sheet.*

/**
* Created by kaloglu on 16.12.2017.
*/

abstract class TimelineFragment : BaseFragment() {
    private lateinit var timelineRV: RecyclerView
    protected lateinit var adapter: RecyclerView.Adapter<*>

    fun getInstance(@FragmentTypes fragment_type: Long, @FragmentContentTypes content_type: Long) =
            super.getInstance(fragment_type, content_type, LIST)


    override fun initializeScreenObjects() {
        with(view!!) {
            timelineRV = findViewById(R.id.timeline_rv)
            timelineRV.layoutManager=LinearLayoutManager(this.context)
        }
        
        open_sheet?.setOnClickListener {
            toggleSheetMenu()
        }
    }

    override fun designScreen() {
        timelineRV.adapter = adapter
    }

    companion object {
        lateinit var timeline_tweet: Timeline<Tweet>
    }

}

