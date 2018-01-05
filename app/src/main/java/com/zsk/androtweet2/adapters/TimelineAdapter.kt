/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.zsk.androtweet2.adapters

import android.content.Context
import android.database.DataSetObserver
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.TweetBuilder
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import com.zsk.androtweet2.components.twitter.TimelineDelegate
import com.zsk.androtweet2.fragments.BaseFragment
import com.zsk.androtweet2.helpers.utils.twitter.components.views.CompactTweetView

/**
 * TimelineAdapter is a RecyclerView adapter which can provide Timeline Tweets to
 * RecyclerViews.
 */
class TimelineAdapter private constructor(
        private val context: Context,
        private val timelineDelegate: TimelineDelegate<Tweet>
) : RecyclerView.Adapter<TimelineAdapter.TweetViewHolder>() {
    private var previousCount: Int = 0

    fun selectAll(checked: Boolean) {
        timelineDelegate.selectAll(checked)
    }

    constructor(
            context: Context,
            timeline: Timeline<Tweet>,
            toggleSheetMenuListener: BaseFragment.ToggleSheetMenuListener? = null
    ) : this(context, TimelineDelegate<Tweet>(context, timeline, toggleSheetMenuListener = toggleSheetMenuListener))

    init {
        timelineDelegate.refresh(object : Callback<TimelineResult<Tweet>>() {
            override fun success(result: Result<TimelineResult<Tweet>>) {
                notifyDataSetChanged()
                previousCount = timelineDelegate.itemList.size
            }

            override fun failure(exception: TwitterException) {

            }
        })

        val dataSetObserver = object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                when (previousCount) {
                    0, itemCount -> notifyDataSetChanged()
                    else -> {
                        notifyItemRangeInserted(previousCount, itemCount - previousCount)
                    }
                }
                previousCount = itemCount
            }

            override fun onInvalidated() {
                notifyDataSetChanged()
                super.onInvalidated()
            }
        }

        timelineDelegate.registerDataSetObserver(dataSetObserver)
    }

    override fun getItemCount(): Int = timelineDelegate.itemList.size


    class TweetViewHolder(itemView: CompactTweetView) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        val tweet = TweetBuilder().build()
        val compactTweetView = CompactTweetView(context, tweet, timelineDelegate)
        return TweetViewHolder(compactTweetView)
    }

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        val compactTweetView = holder.itemView as CompactTweetView
        compactTweetView.tweet = timelineDelegate.getItem(position)
    }

    fun refresh(cb: Callback<TimelineResult<Tweet>>) {
        timelineDelegate.refresh(cb)
        previousCount = 0
    }

}
