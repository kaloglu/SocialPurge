package com.zsk.androtweet2

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by kaloglu on 11/11/2017.
 */
class FirebaseService {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? = firebaseAuth!!.currentUser

    val USERS: String? = "users"
    val TWITTER_ACCOUNTS: String? = "twitter_accounts"
    val PREFERENCES: String? = "preferences"
    val STATUSES: String? = "statuses"
    val LIKES: String? = "likes"
    val RETWEETS: String? = "retweets"
    val TWEET_DETAILS: String? = "tweet_details"


    fun String?.getDatabaseReference(): DatabaseReference =
            firebaseDatabase.getReference(this)

    fun String?.update(id: String, value: Any): Task<Void>? =
            this.getDatabaseReference().child(currentUser!!.uid).child(id).setValue(value)

}
