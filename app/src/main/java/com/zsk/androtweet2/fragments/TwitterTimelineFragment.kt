package com.zsk.androtweet2.fragments

import android.support.design.widget.BottomSheetBehavior
import android.widget.RelativeLayout
import android.widget.Toast
import com.zsk.androtweet2.R
import com.zsk.androtweet2.adapters.AdapterFactory
import com.zsk.androtweet2.components.twitter.UserTimeline
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.TWEET
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes.TWITTER
import kotlinx.android.synthetic.main.actions_bottom_sheet.*

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
class TwitterTimelineFragment : TimelineFragment() {
    override val layoutId: Int
        get() = R.layout.twitter_timeline_layout
    override val bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>?
        get() = BottomSheetBehavior.from(bottom_sheet)

    fun getInstance(@FragmentContentTypes content_type: Long = TWEET) = super.getInstance(TWITTER, content_type)
    override fun initializeScreenObjects() {
        super.initializeScreenObjects()

        select_all.setOnClickListener {
            select_all_icon.isChecked = !select_all_icon.isChecked
        }

        select_all_icon.setOnCheckedChangeListener { _, _ ->
            Toast.makeText(context, "select_all : " + select_all_icon.isChecked, Toast.LENGTH_SHORT).show()
        }
        add_queue.setOnClickListener {
            Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show()
        }
    }

    override fun designScreen() {
        timeline_tweet = UserTimeline()

        adapter = AdapterFactory().create(context, timeline_tweet)
        super.designScreen()
    }

}
