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

package zao.kaloglu.com.socialpurge.components

/**
 * TimelineDelegate manages timeline data items and loads items from a Timeline.
 * @param <T> the item type
</T> */
class List<T> internal constructor(
        var queueList: MutableList<T> = mutableListOf()
) : zao.kaloglu.com.socialpurge.components.ListObservable<T>() {

    companion object {
        internal val CAPACITY = 200L
    }

    fun getItem(position: Int): T = queueList[position]

    /**
     * Returns true if the queueList size is below the MAX_ITEMS capacity, false otherwise.
     */
    internal fun withinMaxCapacity(): Boolean = queueList.size < zao.kaloglu.com.socialpurge.components.List.Companion.CAPACITY

    /**
     * Returns true if the position is for the last item in queueList, false otherwise.
     */
    internal fun isLastPosition(position: Int): Boolean = position == queueList.size - 1

    fun add(element: T): Boolean {
        val add = queueList.add(element)
            notifyItemAdded(element)
        return add
    }

    fun remove(element: T): Boolean {

        val remove = queueList.remove(element)
            notifyItemRemoved(element)
        return remove
    }

    fun contains(element: T): Boolean {
        val contains = queueList.contains(element)
        notifyInvalidated()
        return contains
    }

    fun size(): Int {
        val size = queueList.size
        notifyInvalidated()
        return size
    }


}
