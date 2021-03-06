package zao.kaloglu.com.socialpurge.components

import android.database.DataSetObserver

/**
 * Created by kaloglu on 4.03.2018.
 */
abstract class ListObserver<in T> : DataSetObserver() {
    /**
     * This method is called when the entire data set has changed,
     * most likely through a call to [Cursor.requery] on a [Cursor].
     */
    open fun onItemAdded(item: T) {
        onChanged()
    }

    /**
     * This method is called when the entire data becomes invalid,
     * most likely through a call to [Cursor.deactivate] or [Cursor.close] on a
     * [Cursor].
     */
    open fun onItemRemoved(item: T) {
        onChanged()
    }
}
