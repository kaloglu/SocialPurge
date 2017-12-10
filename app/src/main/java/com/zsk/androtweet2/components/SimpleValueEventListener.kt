package com.zsk.androtweet2.components

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * Created by kaloglu on 30.11.2017.
 */
interface SimpleValueEventListener : ValueEventListener {
    override fun onCancelled(databaseError: DatabaseError?) = Unit
    override fun onDataChange(dataSnapshot: DataSnapshot?) = Unit
}
