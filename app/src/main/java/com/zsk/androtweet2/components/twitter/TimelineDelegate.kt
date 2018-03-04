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

package com.zsk.androtweet2.components.twitter

import android.content.Context
import android.database.DataSetObservable
import android.os.Handler
import android.os.Looper
import com.google.firebase.database.DatabaseReference
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import com.zsk.androtweet2.AndroTweetApp
import com.zsk.androtweet2.fragments.BaseFragment
import com.zsk.androtweet2.helpers.bases.BaseActivity.Companion.firebaseService
import com.zsk.androtweet2.helpers.utils.twitter.components.others.TweetRepository
import com.zsk.androtweet2.models.DeleteTweetObject
import java.util.*

/**
 * TimelineDelegate manages timeline data items and loads items from a Timeline.
 * @param <T> the item type
</T> */
class TimelineDelegate<T : Tweet> internal constructor(
        val context: Context,
        internal val timeline: Timeline<T>,
        var itemList: MutableList<T> = mutableListOf(),
        private val timelineStateHolder: TimelineStateHolder = TimelineStateHolder(),
        private var toggleSheetMenuListener: BaseFragment.ToggleSheetMenuListener? = null,
        val tweetRepository: TweetRepository = TweetRepository(
                Handler(Looper.getMainLooper()),
                TwitterCore.getInstance().sessionManager
        )
) : DataSetObservable() {

    private var selectionList: MutableList<String> = mutableListOf()

    companion object {
        internal val CAPACITY = 200L
    }


    /**
     * Triggers loading the latest items and calls through to the developer callback. If items are
     * received, they replace existing items.
     */
    fun refresh(developerCb: Callback<TimelineResult<T>>) {
        // reset scrollStateHolder cursors to be null, loadNext will get latest items
        timelineStateHolder.resetCursors()
        // load latest timeline items and replace existing items
        loadNext(timelineStateHolder.positionForNext(),
                RefreshCallback(developerCb, timelineStateHolder))
    }

    /**
     * Triggers loading next items and calls through to the developer callback.
     */
    fun next(developerCb: Callback<TimelineResult<T>>) {
        loadNext(timelineStateHolder.positionForNext(),
                NextCallback(developerCb, timelineStateHolder))
    }

    /**
     * Triggers loading previous items.
     */
    private fun previous() {
        loadPrevious(timelineStateHolder.positionForPrevious(),
                PreviousCallback(timelineStateHolder))
    }

    fun getItem(position: Int): T {
        if (isLastPosition(position)) {
            previous()
        }
        return itemList[position]
    }

    /**
     * Returns true if the queueList size is below the MAX_ITEMS capacity, false otherwise.
     */
    internal fun withinMaxCapacity(): Boolean = itemList.size < CAPACITY

    /**
     * Returns true if the position is for the last item in queueList, false otherwise.
     */
    internal fun isLastPosition(position: Int): Boolean = position == itemList.size - 1

    /**
     * Checks the capacity and sets requestInFlight before calling timeline.next.
     */
    internal fun loadNext(minPosition: Long?, cb: Callback<TimelineResult<T>>) {
        if (withinMaxCapacity()) {
            if (timelineStateHolder.startTimelineRequest()) {
                timeline.next(minPosition, cb)
            } else {
                cb.failure(TwitterException("Request already in flight"))
            }
        } else {
            cb.failure(TwitterException("Max capacity reached"))
        }
    }

    /**
     * Checks the capacity and sets requestInFlight before calling timeline.previous.
     */
    internal fun loadPrevious(maxPosition: Long?, cb: Callback<TimelineResult<T>>) {
        if (withinMaxCapacity()) {
            if (timelineStateHolder.startTimelineRequest()) {
                timeline.previous(maxPosition, cb)
            } else {
                cb.failure(TwitterException("Request already in flight"))
            }
        } else {
            cb.failure(TwitterException("Max capacity reached"))
        }
    }

    /**
     * TimelineDelegate.DefaultCallback is a Callback which handles setting requestInFlight to
     * false on both success and failure and calling through to a wrapped developer Callback.
     * Subclass methods must call through to the parent method after their custom implementation.
     */
    internal open inner class DefaultCallback(val developerCallback: Callback<TimelineResult<T>>?,
                                              val timelineStateHolder: TimelineStateHolder) : Callback<TimelineResult<T>>() {

        override fun success(result: Result<TimelineResult<T>>) {
            timelineStateHolder.finishTimelineRequest()
            developerCallback?.success(result)
        }

        override fun failure(exception: TwitterException) {
            timelineStateHolder.finishTimelineRequest()
            developerCallback?.failure(exception)
        }
    }

    /**
     * Handles receiving next timeline items. Prepends received items to listItems, updates the
     * scrollStateHolder nextCursor, and calls notifyDataSetChanged.
     */
    internal open inner class NextCallback(developerCb: Callback<TimelineResult<T>>,
                                           timelineStateHolder: TimelineStateHolder) : DefaultCallback(developerCb, timelineStateHolder) {

        override fun success(result: Result<TimelineResult<T>>) {
            if (result.data.items.size > 0) {
                val receivedItems = ArrayList(result.data.items)
                receivedItems.addAll(itemList)
                itemList = receivedItems
                notifyChanged()
                timelineStateHolder.setNextCursor(result.data.timelineCursor)
            }
            // do nothing when zero items are received. Subsequent 'next' call does not change.
            super.success(result)
        }
    }

    /**
     * Handles receiving latest timeline items. If timeline items are received, clears listItems,
     * sets received items, updates the scrollStateHolder nextCursor, and calls
     * notifyDataSetChanged. If the results have no items, does nothing.
     */
    internal inner class RefreshCallback(developerCb: Callback<TimelineResult<T>>,
                                         timelineStateHolder: TimelineStateHolder) : NextCallback(developerCb, timelineStateHolder) {

        override fun success(result: Result<TimelineResult<T>>) {
            if (result.data.items.size > 0) {
                itemList.clear()
            }
            super.success(result)
        }
    }

    /**
     * Handles appending listItems and updating the scrollStateHolder previousCursor.
     */
    internal inner class PreviousCallback(timelineStateHolder: TimelineStateHolder) : DefaultCallback(null, timelineStateHolder) {

        override fun success(result: Result<TimelineResult<T>>) {
            if (result.data.items.size > 0) {
                itemList.addAll(result.data.items)
                notifyChanged()
                timelineStateHolder.setPreviousCursor(result.data.timelineCursor)
            }
            // do nothing when zero items are received. Subsequent 'next' call does not change.
            super.success(result)
        }
    }

    fun selectionToggle(item: T) {
        if (selectionList.isSelected(item)) {
            selectionList.remove(item.idStr)
        } else
            selectionList.add(item.idStr)

        afterSelectionToggleAction()
        notifyChanged()
    }

    fun isSelected(item: T): Boolean = selectionList.isSelected(item)

    private fun MutableList<String>.isSelected(item: T): Boolean = item.id.toString().let(this::contains)


    fun selectAll(checked: Boolean) {
        val deleteQueue = AndroTweetApp.instance.deleteQueue
        if (checked)
            itemList
                    .filter { item ->
                        selectionList.contains(item.idStr).not() && deleteQueue.contains(item.idStr).not()
                    }
                    .forEach { item ->
                        selectionList.add(item.idStr)
                    }
        else
            selectionList.clear()

        afterSelectionToggleAction()

        notifyChanged()
    }

    fun addAll() {
        val activeUserId = TwitterCore.getInstance().sessionManager.activeSession.userId.toString()
        firebaseService.apply {
            val selectedObjects = HashMap<String, DeleteTweetObject>()
            selectionList.forEach {
                selectedObjects[it] = DeleteTweetObject(it, auth.currentUser?.uid!!, activeUserId)
            }

            DELETION_QUEUE?.update(
                    selectedObjects,
                    DatabaseReference.CompletionListener { dbError, _ ->
                        if (dbError == null) {
                            selectionList.clear()
                            afterSelectionToggleAction()
                            if (itemList.size <= 0)
                                previous()
                        }
                        notifyChanged()
                    }
            )
        }
    }

    private fun afterSelectionToggleAction() {
        toggleSheetMenuListener?.onToggle(selectionList.size)

    }

}
