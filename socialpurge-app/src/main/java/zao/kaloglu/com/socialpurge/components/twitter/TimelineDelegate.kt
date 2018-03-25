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

package zao.kaloglu.com.socialpurge.components.twitter

import android.content.Context
import android.database.DataSetObservable
import android.os.Handler
import android.os.Looper
import android.support.v7.util.DiffUtil
import com.google.firebase.database.DatabaseReference
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.TweetBuilder
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import zao.kaloglu.com.socialpurge.adapters.TimelineAdapter
import zao.kaloglu.com.socialpurge.fragments.BaseFragment
import zao.kaloglu.com.socialpurge.helpers.bases.BaseActivity.Companion.firebaseService
import zao.kaloglu.com.socialpurge.models.DeleteTweetObject
import java.util.*

/**
 * TimelineDelegate manages timeline data items and loads items from a Timeline.
 * @param <Tweet> the item type
</T> */
class TimelineDelegate internal constructor(
        val context: Context,
        internal val timeline: Timeline<Tweet>,
        var tweetList: MutableList<Tweet> = mutableListOf(),
        private val timelineStateHolder: zao.kaloglu.com.socialpurge.components.twitter.TimelineStateHolder = zao.kaloglu.com.socialpurge.components.twitter.TimelineStateHolder(),
        private var toggleSheetMenuListener: BaseFragment.ToggleSheetMenuListener? = null,
        val tweetRepository: zao.kaloglu.com.socialpurge.helpers.utils.twitter.components.others.TweetRepository = zao.kaloglu.com.socialpurge.helpers.utils.twitter.components.others.TweetRepository(
                Handler(Looper.getMainLooper()),
                TwitterCore.getInstance().sessionManager
        )
) : DataSetObservable() {

    lateinit var adapter: TimelineAdapter
    private var selectionList: MutableList<String> = mutableListOf()

    companion object {
        const val ITEMS_PER_AD: Int = 4
        internal val CAPACITY = 200L
    }

    private val queueListObserver = object : zao.kaloglu.com.socialpurge.components.ListObserver<String>() {

        override fun onItemAdded(item: String) {
            val indexOfFirst = tweetList.indexOfFirst { it.idStr == item }
//            dispatch()
            adapter.notifyItemChanged(indexOfFirst)
        }

        override fun onItemRemoved(item: String) {
            val newList = mutableListOf<Tweet>()
//            tweetList.adBanner(false)
            newList.addAll(tweetList)
            val indexOfFirst = newList.indexOfFirst { it.idStr == item }
            if (indexOfFirst==-1)
                return
            newList.removeAt(indexOfFirst)
            dispatch(newList)
//            adapter.notifyItemRemoved(indexOfFirst)
        }
    }
    private val deleteQueue = zao.kaloglu.com.socialpurge.SocialPurgeApp.instance.deleteQueue

    init {
        deleteQueue.registerObserver(queueListObserver)
    }


    private fun dispatch(newTweetList: MutableList<Tweet>? = null) {
        val tweetDiffCallback = TweetDiffCallBack(newTweetList, tweetList, selectionList, deleteQueue)
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(tweetDiffCallback)
        newTweetList?.let {
            tweetList = newTweetList
        }
//        tweetList.adBanner(false)
//        tweetList.adBanner()
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun MutableList<Tweet>.adBanner(showAds: Boolean = true) {
        val iterate = this.listIterator()
        var index = 0
        if (showAds)
            iterate.add(TweetBuilder().build())
        while (iterate.hasNext()) {
            index++
            val tweet = iterate.next()
            if (showAds && index % TimelineDelegate.ITEMS_PER_AD == 0)
                iterate.add(TweetBuilder().build())
            else if (showAds.not() && tweet.id == Tweet.INVALID_ID)
                iterate.remove()
        }
    }

    private val TAG: String? = this::class.java.simpleName

    /**
     * Triggers loading the latest items and calls through to the developer callback. If items are
     * received, they replace existing items.
     */
    fun refresh(newAdapter: TimelineAdapter) {
        this.adapter = newAdapter
        // reset scrollStateHolder cursors to be null, loadNext will get latest items
        timelineStateHolder.resetCursors()
        // load latest timeline items and replace existing items
        loadNext(timelineStateHolder.positionForNext(), RefreshCallback(timelineStateHolder))
    }

    /**
     * Triggers loading next items and calls through to the developer callback.
     */
    fun next(developerCb: Callback<TimelineResult<Tweet>>? = null) {
        loadNext(timelineStateHolder.positionForNext(), NextCallback(timelineStateHolder, developerCb))
    }

    /**
     * Triggers loading previous items.
     */
    fun previous() {
        loadPrevious(timelineStateHolder.positionForPrevious(), PreviousCallback(timelineStateHolder))
    }

    fun getItem(position: Int): Tweet {
        if (isLastPosition(position)) {
            previous()
        }
        return tweetList[position]
    }

    /**
     * Returns true if the queueList size is below the MAX_ITEMS capacity, false otherwise.
     */
    internal fun withinMaxCapacity(): Boolean = tweetList.size < CAPACITY

    /**
     * Returns true if the position is for the last item in queueList, false otherwise.
     */
    internal fun isLastPosition(position: Int): Boolean = position == tweetList.size - 1

    /**
     * Checks the capacity and sets requestInFlight before calling timeline.next.
     */
    internal fun loadNext(minPosition: Long?, cb: Callback<TimelineResult<Tweet>>? = null) {
        if (withinMaxCapacity()) {
            if (timelineStateHolder.startTimelineRequest()) {
                timeline.next(minPosition, cb)
            } else {
                cb?.failure(TwitterException("Request already in flight"))
            }
        } else {
            cb?.failure(TwitterException("Max capacity reached"))
        }
    }

    /**
     * Checks the capacity and sets requestInFlight before calling timeline.previous.
     */
    internal fun loadPrevious(maxPosition: Long?, cb: Callback<TimelineResult<Tweet>>) {
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
    internal open inner class DefaultCallback(
            val timelineStateHolder: zao.kaloglu.com.socialpurge.components.twitter.TimelineStateHolder,
            val developerCallback: Callback<TimelineResult<Tweet>>? = null
    ) : Callback<TimelineResult<Tweet>>() {

        override fun success(result: Result<TimelineResult<Tweet>>) {
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
    internal open inner class NextCallback(
            timelineStateHolder: zao.kaloglu.com.socialpurge.components.twitter.TimelineStateHolder,
            developerCb: Callback<TimelineResult<Tweet>>? = null
    ) : DefaultCallback(timelineStateHolder, developerCb) {

        override fun success(result: Result<TimelineResult<Tweet>>) {
            if (result.data.items.size > 0) {
                val newTweetList = ArrayList(result.data.items)
//                tweetList.adBanner(false)
                newTweetList.addAll(tweetList)
                dispatch(newTweetList)
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
    internal inner class RefreshCallback(
            timelineStateHolder: zao.kaloglu.com.socialpurge.components.twitter.TimelineStateHolder,
            developerCb: Callback<TimelineResult<Tweet>>? = null
    ) : NextCallback(timelineStateHolder, developerCb) {

        override fun success(result: Result<TimelineResult<Tweet>>) {
            if (result.data.items.size > 0) {
                tweetList.clear()
            }
            super.success(result)
        }
    }

    /**
     * Handles appending listItems and updating the scrollStateHolder previousCursor.
     */
    internal inner class PreviousCallback(timelineStateHolder: zao.kaloglu.com.socialpurge.components.twitter.TimelineStateHolder) : DefaultCallback(timelineStateHolder) {

        override fun success(result: Result<TimelineResult<Tweet>>) {
            if (result.data.items.size > 0) {
                val newTweetList = mutableListOf<Tweet>()
//                tweetList.adBanner(false)
                newTweetList.addAll(tweetList)
                newTweetList.addAll(ArrayList(result.data.items))
                dispatch(newTweetList)
//                notifyChanged()
                timelineStateHolder.setPreviousCursor(result.data.timelineCursor)
            }
            // do nothing when zero items are received. Subsequent 'next' call does not change.
            super.success(result)
        }
    }

    fun selectionToggle(position: Int = -1, item: Tweet) {
        if (selectionList.isSelected(item)) {
            selectionList.remove(item.idStr)
        } else
            selectionList.add(item.idStr)

        afterSelectionToggleAction()
        adapter.notifyItemChanged(position)
    }

    fun isSelected(item: Tweet): Boolean = selectionList.isSelected(item)

    private fun MutableList<String>.isSelected(item: Tweet): Boolean = item.id.toString().let(this::contains)


    fun selectAll(checked: Boolean) {
        val deleteQueue = zao.kaloglu.com.socialpurge.SocialPurgeApp.instance.deleteQueue
        if (checked)
            tweetList
                    .filter { item ->
                        item.idStr.isNullOrEmpty().not() && selectionList.contains(item.idStr).not() && deleteQueue.contains(item.idStr).not()
                    }
                    .forEachIndexed { index, item ->
                        selectionList.add(item.idStr)
                        adapter.notifyItemChanged(index)
                    }
        else {
            selectionList.clear()
            adapter.notifyItemRangeChanged(0, tweetList.size)
        }

        afterSelectionToggleAction()

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
                            if (tweetList.size <= 0)
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

    class TweetDiffCallBack(
            newList: MutableList<Tweet>? = null,
            private val oldTweetList: MutableList<Tweet>,
            private val selectionList: MutableList<String>,
            private val deleteQueue: zao.kaloglu.com.socialpurge.components.List<String>
    ) : DiffUtil.Callback() {

        private var newTweetList = newList

        init {
            if (newTweetList == null) newTweetList = oldTweetList
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val isTheSameItems = oldTweetList[oldItemPosition].id == newTweetList!![newItemPosition].id
//            val isTheSameSelections = checkSelection(oldTweetList[oldItemPosition].idStr) && checkSelection(newTweetList!![newItemPosition].idStr)
//            val inTheQueue = checkQueue(oldTweetList[oldItemPosition].idStr)
            return isTheSameItems //&& isTheSameSelections && inTheQueue
        }

        private fun checkQueue(idStr: String) = deleteQueue.contains(idStr)
        private fun checkSelection(idStr: String) = selectionList.contains(idStr)


        override fun getOldListSize(): Int = oldTweetList.size

        override fun getNewListSize(): Int = newTweetList?.size ?: oldListSize

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldTweetList[oldItemPosition] == newTweetList?.get(newItemPosition)

    }

}
