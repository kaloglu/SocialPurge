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
import android.database.DataSetObserver
import com.google.firebase.database.DatabaseReference
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Identifiable
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import com.zsk.androtweet2.fragments.BaseFragment
import com.zsk.androtweet2.helpers.bases.BaseActivity.Companion.firebaseService
import com.zsk.androtweet2.models.DeleteTweetObject
import java.util.*

/**
 * TimelineDelegate manages timeline data items and loads items from a Timeline.
 * @param <T> the item type
</T> */
class TimelineDelegate<T : Identifiable>
internal constructor(
        val context: Context,
        internal val timeline: Timeline<T>,
        observer: DataSetObservable? = null,
        var itemList: MutableList<T> = ArrayList(),
        private val timelineStateHolder: TimelineStateHolder = TimelineStateHolder(),
        private var toggleSheetMenuListener: BaseFragment.ToggleSheetMenuListener? = null
) {
    private var selectionList: MutableList<T> = ArrayList()
    private var listAdapterObservable: DataSetObservable = observer ?: DataSetObservable()

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
     * Sets all items in the itemList with the item id to be item. If no items with the same id
     * are found, no changes are made.
     * @param item the updated item to set in the itemList
     */
    fun setItemById(item: T) {
        itemList.indices
                .asSequence()
                .filter { item.id == itemList[it].id }
                .forEach { itemList[it] = item }
        notifyDataSetChanged()
    }

    /**
     * Returns true if the itemList size is below the MAX_ITEMS capacity, false otherwise.
     */
    internal fun withinMaxCapacity(): Boolean = itemList.size < CAPACITY

    /**
     * Returns true if the position is for the last item in itemList, false otherwise.
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
                notifyDataSetChanged()
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
                notifyDataSetChanged()
                timelineStateHolder.setPreviousCursor(result.data.timelineCursor)
            }
            // do nothing when zero items are received. Subsequent 'next' call does not change.
            super.success(result)
        }
    }

/* Support Adapter DataSetObservers, based on BaseAdapter */

    /**
     * Registers an observer that is called when changes happen to the managed data items.
     * @param observer The object that will be notified when the data set changes.
     */
    fun registerDataSetObserver(observer: DataSetObserver) {
        listAdapterObservable.registerObserver(observer)
    }

    /**
     * Unregister an observer that has previously been registered via
     * registerDataSetObserver(DataSetObserver).
     * @param observer The object to unregister.
     */
    fun unregisterDataSetObserver(observer: DataSetObserver) {
        listAdapterObservable.unregisterObserver(observer)
    }

    /**
     * Notifies the attached observers that the underlying data has been changed and any View
     * reflecting the data set should refresh itself.
     */
    fun notifyDataSetChanged() {
        listAdapterObservable.notifyChanged()
    }

    /**
     * Notifies the attached observers that the underlying data is not longer valid or available.
     * Once invoked, this adapter is no longer valid and should not report further data set changes.
     */
    fun notifyDataSetInvalidated() {
        listAdapterObservable.notifyInvalidated()
    }

    fun selectionToggle(item: T) {
        if (selectionList.isSelected(item)) {
            selectionList.remove(item)
        } else
            selectionList.add(item)

        afterSelectionToggleAction()
        notifyDataSetChanged()
    }

    fun isSelected(item: T): Boolean = selectionList.isSelected(item)

    private fun MutableList<T>.isSelected(item: T): Boolean = item.let(this::contains)


    fun selectAll(checked: Boolean) {
        if (checked)
            itemList.filter { selectionList.contains(it).not() }.forEach { selectionList.add(it) }
        else
            selectionList.clear()

        afterSelectionToggleAction()

        notifyDataSetChanged()
    }

    fun addAll() {
        itemList.filter { selectionList.contains(it) }.forEach {
            firebaseService.apply {
                DELETION_QUEUE?.update(DeleteTweetObject(it as Tweet,currentUser), DatabaseReference.CompletionListener { dbError, _ ->
                    if (dbError == null) {
                        selectionList.remove(it)
                        itemList.remove(it)
                        afterSelectionToggleAction()

                        notifyDataSetChanged()
                    }
                })
            }
        }

    }


    private fun afterSelectionToggleAction() {
        toggleSheetMenuListener?.onToggle(selectionList.size)

    }

}
