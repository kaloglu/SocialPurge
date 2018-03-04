package com.zsk.androtweet2.components

import android.database.DataSetObserver

/**
 * Created by kaloglu on 4.03.2018.
 */
abstract class ListObserver<T> : DataSetObserver() {
    /**
     * This method is called when the entire data set has changed,
     * most likely through a call to [Cursor.requery] on a [Cursor].
     */
    open fun onItemAdded(position: T) {
        onChanged()
    }

    /**
     * This method is called when the entire data becomes invalid,
     * most likely through a call to [Cursor.deactivate] or [Cursor.close] on a
     * [Cursor].
     */
    open fun onItemRemoved(position: T) {
        onChanged()
    }
}
