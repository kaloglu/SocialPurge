package zao.kaloglu.com.socialpurge.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import zao.kaloglu.com.socialpurge.fragments.BaseFragment

/**
 * Created by kaloglu on 1.01.2018.
 */
class AdapterFactory {

    fun create(context: Context, timeline: Timeline<Tweet>, toggleSheetMenuListener: BaseFragment.ToggleSheetMenuListener? = null): zao.kaloglu.com.socialpurge.adapters.TimelineAdapter {
        adapter = zao.kaloglu.com.socialpurge.adapters.TimelineAdapter(context, timeline, toggleSheetMenuListener)

        return adapter as zao.kaloglu.com.socialpurge.adapters.TimelineAdapter
    }

    lateinit var adapter: RecyclerView.Adapter<*>

}
