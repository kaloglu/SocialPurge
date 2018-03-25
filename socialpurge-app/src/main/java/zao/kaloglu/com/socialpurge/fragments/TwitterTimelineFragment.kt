package zao.kaloglu.com.socialpurge.fragments

import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.actions_bottom_sheet.*
import kotlinx.android.synthetic.main.twitter_timeline_layout.*
import org.jetbrains.anko.alert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zao.kaloglu.com.socialpurge.SocialPurgeApp
import zao.kaloglu.com.socialpurge.adapters.AdapterFactory
import zao.kaloglu.com.socialpurge.adapters.TimelineAdapter
import zao.kaloglu.com.socialpurge.components.twitter.TimelineDelegate
import zao.kaloglu.com.socialpurge.components.twitter.UserTimeline
import zao.kaloglu.com.socialpurge.helpers.responses.CheckResponse
import zao.kaloglu.com.socialpurge.helpers.services.SocialPurgeApiClient
import zao.kaloglu.com.socialpurge.helpers.utils.Enums.FragmentContentTypes
import zao.kaloglu.com.socialpurge.helpers.utils.Enums.FragmentContentTypes.TWEET
import zao.kaloglu.com.socialpurge.helpers.utils.Enums.FragmentTypes.TWITTER

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
class TwitterTimelineFragment : TimelineFragment() {
    override val layoutId: Int
        get() = zao.kaloglu.com.socialpurge.R.layout.twitter_timeline_layout
    override val bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>?
        get() = BottomSheetBehavior.from(bottom_sheet)
    override val fab: FloatingActionButton
        get() = add_queue_fab

    fun getInstance(@FragmentContentTypes content_type: Long = TWEET) = super.getInstance(TWITTER, content_type)
    override fun initializeScreenObjects() {
        SocialPurgeApp.instance.getRewardedAd(context!!)
        super.initializeScreenObjects()
        select_all.setOnClickListener {
            select_all_icon.isChecked = !select_all_icon.isChecked
            adapter.selectAll(select_all_icon.isChecked)
        }

        select_all_icon.setOnCheckedChangeListener { _, isChecked ->
            adapter.selectAll(isChecked)
        }
        add_queue_fab.setOnClickListener {
            with(activity!!) {
                alert(
                        "Are you sure delete all selected tweets?"
                ) {
                    positiveButton("YES", {
                        adapter.addAll()
                        SocialPurgeApp.instance.showMobileAd(context)

                        SocialPurgeApiClient()
                                .getSimpleGetServices()
                                .check().enqueue(
                                        object : Callback<CheckResponse> {
                                            override fun onFailure(call: Call<CheckResponse>?, t: Throwable?) {
                                                Toast.makeText(context, "error : ${t?.message}", Toast.LENGTH_SHORT).show()
                                                if (call != null) {
                                                    val url = call.request().url().toString()
                                                    Log.e(TAG, "$url  -> ", t)
                                                }
                                            }

                                            override fun onResponse(call: Call<CheckResponse>?, response: Response<CheckResponse>?) {
                                                response?.let {
                                                    if (response.body().status) {
                                                        Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }

                                        }
                                )
                    })
                    negativeButton("No", { })
                }.show()
            }
        }
        swipe_refresh.setOnRefreshListener {
            adapter.checkNewItems {
                adapter.timelineDelegate?.let {
                    if (it.tweetList.size == 0)
                        it.previous()
                }
                swipe_refresh.isRefreshing = false
                timeline_rv.smoothScrollToPosition(0)
            }
        }
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */

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

    private fun RecyclerView.Adapter<*>.checkNewItems(refreshListener: () -> Unit) {
        (this as? TimelineAdapter)?.checkNewItems(refreshListener)
    }

    private val RecyclerView.Adapter<*>.timelineDelegate: TimelineDelegate?
        get() = (this as? TimelineAdapter)?.timelineDelegate
}

