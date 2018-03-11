package zao.kaloglu.com.socialpurge.components

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

/**
 * Created by kaloglu on 30.11.2017.
 */
interface SimpleChildEventListener : ChildEventListener {
    override fun onCancelled(databaseError: DatabaseError?) = Unit

    override fun onChildMoved(dataSnapShot: DataSnapshot?, p1: String?) = Unit

    override fun onChildChanged(dataSnapShot: DataSnapshot?, p1: String?) = Unit

    override fun onChildAdded(dataSnapShot: DataSnapshot?, p1: String?) = Unit

    override fun onChildRemoved(dataSnapShot: DataSnapshot?) = Unit
}
