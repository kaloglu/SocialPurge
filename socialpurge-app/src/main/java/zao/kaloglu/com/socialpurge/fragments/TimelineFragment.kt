package zao.kaloglu.com.socialpurge.fragments

import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import kotlinx.android.synthetic.main.actions_bottom_sheet.*
import kotlinx.android.synthetic.main.actions_bottom_sheet.view.*
import zao.kaloglu.com.socialpurge.SocialPurgeApp
import zao.kaloglu.com.socialpurge.helpers.utils.Enums.FragmentContentTypes
import zao.kaloglu.com.socialpurge.helpers.utils.Enums.FragmentItemTypes.LIST
import zao.kaloglu.com.socialpurge.helpers.utils.Enums.FragmentTypes


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
            timelineRV = findViewById(zao.kaloglu.com.socialpurge.R.id.timeline_rv)
            timelineRV.layoutManager=LinearLayoutManager(this.context)
            val dividerItemDecoration = DividerItemDecoration(
                    timelineRV.context,
                    LinearLayoutManager.VERTICAL
            )

            dividerItemDecoration.setDrawable(
                    ContextCompat.getDrawable(context!!, zao.kaloglu.com.socialpurge.R.drawable.divider_default)!!
            )
            timelineRV.addItemDecoration(dividerItemDecoration)
        }
        
        open_sheet?.setOnClickListener {
            toggleSheetMenu()
        }

        SocialPurgeApp.instance.deleteQueue.registerObserver(object : zao.kaloglu.com.socialpurge.components.ListObserver<String>() {
            override fun onChanged() {
                super.onChanged()
                val queueSize = zao.kaloglu.com.socialpurge.SocialPurgeApp.instance.deleteQueue.size()
                if (queueSize > 0)
                    bottom_sheet?.queue?.text = String.format(getString(zao.kaloglu.com.socialpurge.R.string.queue_info), queueSize)
                else
                    bottom_sheet?.queue?.text = ""
            }
        })
    }

    override fun designScreen() {
        timelineRV.adapter = adapter
    }

    companion object {
        lateinit var timeline_tweet: Timeline<Tweet>
    }

}

