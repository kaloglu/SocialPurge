package com.zsk.androtweet2.components

import android.database.Observable

/**
 * Created by kaloglu on 4.03.2018.
 */
open class ListObservable<T> : Observable<ListObserver<T>>() {

    fun notifyItemAdded(element: T) {
        synchronized(mObservers) {
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (i in mObservers.indices.reversed()) {
                mObservers[i].onItemAdded(element)
            }
        }
    }
    fun notifyItemRemoved(element: T) {
        synchronized(mObservers) {
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (i in mObservers.indices.reversed()) {
                mObservers[i].onItemRemoved(element)
            }
        }
    }

    fun notifyInvalidated() {
        synchronized(mObservers) {
            for (i in mObservers.indices.reversed()) {
                mObservers[i].onInvalidated()
            }
        }
    }
}
