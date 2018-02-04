package com.zsk.androtweet2.fragments

import android.app.AlertDialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import com.zsk.androtweet2.R
import com.zsk.androtweet2.adapters.AdapterFactory
import com.zsk.androtweet2.adapters.TimelineAdapter
import com.zsk.androtweet2.components.twitter.UserTimeline
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.TWEET
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes.TWITTER
import kotlinx.android.synthetic.main.actions_bottom_sheet.*
import kotlinx.android.synthetic.main.twitter_timeline_layout.*

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
class TwitterTimelineFragment : TimelineFragment() {
    override val layoutId: Int
        get() = R.layout.twitter_timeline_layout
    override val bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>?
        get() = BottomSheetBehavior.from(bottom_sheet)

    override val fab: FloatingActionButton
        get() = add_queue_fab

    fun getInstance(@FragmentContentTypes content_type: Long = TWEET) = super.getInstance(TWITTER, content_type)
    override fun initializeScreenObjects() {
        super.initializeScreenObjects()
        select_all.setOnClickListener {
            select_all_icon.isChecked = !select_all_icon.isChecked
            adapter.selectAll(select_all_icon.isChecked)
        }

        select_all_icon.setOnCheckedChangeListener { _, isChecked ->
            adapter.selectAll(isChecked)
        }
        add_queue_fab.setOnClickListener {
            AlertDialog.Builder(context).create().apply {
                setMessage("Are you sure delete all selected tweets?")
                setButton(AlertDialog.BUTTON_POSITIVE, "YES", { _, _ ->
                    adapter.addAll()
                })
                setButton(AlertDialog.BUTTON_NEGATIVE, "NO", { _, _ ->
                    dismiss()
                })
                show()
            }

        }


    }

    override fun designScreen() {
        timeline_tweet = UserTimeline()

        adapter = AdapterFactory().create(context!!, timeline_tweet, toggleSheetMenuListener)
        super.designScreen()
    }

    private fun RecyclerView.Adapter<*>.addAll() {
        (this as? TimelineAdapter)?.addAll()
    }
    private fun RecyclerView.Adapter<*>.selectAll(checked: Boolean) {
        (this as? TimelineAdapter)?.selectAll(checked)
    }

}
