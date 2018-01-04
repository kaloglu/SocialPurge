package com.zsk.androtweet2.helpers.utils

import android.database.DataSetObservable
import android.database.DataSetObserver
import com.zsk.androtweet2.helpers.utils.interfaces.Identifiable
import java.util.*

/**
 * SelectionDelegate manages items data items and loads items from a Timeline.
 *
 * @param <T> the item type
</T> */
public class SelectionDelegate<in T : Identifiable>(observable: DataSetObservable?) {
    private var items: ArrayList<T> = ArrayList()
    private val listObservable: DataSetObservable = observable ?: DataSetObservable()

    class selectionObserver : DataSetObserver() {

        override fun onChanged() {
            var a = 0
            super.onChanged()
        }

    }

    init {
        listObservable.registerObserver(selectionObserver)
    }

    fun setItemById(item: T) {
        items.indices
                .asSequence()
                .filter { item.id == items[it].id }
                .forEach { items[it] = item }
        notifyDataSetChanged()
    }

    /**
     * Returns true if the items size is below the MAX_ITEMS capacity, false otherwise.
     */
    internal fun withinMaxCapacity(): Boolean = items.size < CAPACITY

    /**
     * Returns true if the position is for the last item in items, false otherwise.
     */
    internal fun isLastPosition(position: Int): Boolean = position == items.size - 1

    /**
     * Registers an observer that is called when changes happen to the managed data items.
     *
     * @param observer The object that will be notified when the data set changes.
     */
    fun registerDataSetObserver(observer: DataSetObserver) {
        listObservable.registerObserver(observer)
    }

    /**
     * Unregister an observer that has previously been registered via
     * registerDataSetObserver(DataSetObserver).
     *
     * @param observer The object to unregister.
     */
    fun unregisterDataSetObserver(observer: DataSetObserver) {
        listObservable.unregisterObserver(observer)
    }

    /**
     * Notifies the attached observers that the underlying data has been changed and any View
     * reflecting the data set should refresh itself.
     */
    fun notifyDataSetChanged() {
        listObservable.notifyChanged()
    }

    /**
     * Notifies the attached observers that the underlying data is not longer valid or available.
     * Once invoked, this adapter is no longer valid and should not report further data set changes.
     */
    fun notifyDataSetInvalidated() {
        listObservable.notifyInvalidated()
    }

    companion object {
        // once capacity is exceeded, additional items will not be loaded
        internal val CAPACITY = 50L
    }
}
