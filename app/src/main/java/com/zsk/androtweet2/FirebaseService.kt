package com.zsk.androtweet2

import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.zsk.androtweet2.components.SimpleChildEventListener
import com.zsk.androtweet2.models.FirebaseObject

/**
 * Created by kaloglu on 11/11/2017.
 */
class FirebaseService {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val auth: FirebaseAuth? = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? = auth!!.currentUser

    val CONSUMERS: String? = "consumers"
    val USERS: String? = "users"
    val TWITTER_ACCOUNTS: String? = "twitter_accounts"
    val PREFERENCES: String? = "preferences"
    val STATUSES: String? = "statuses"
    val LIKES: String? = "likes"
    val RETWEETS: String? = "retweets"
    val TWEET_DETAILS: String? = "tweet_details"

    private fun String?.getDBRef(vararg longArr: Long): DatabaseReference? = this.getDBRef(longArr.asSequence().toString())

    /**
     * return db path.
     * eg.:
     *  "table".getDBRef("subtable1","subtable2","subtable3")
     *
     * result:
     *  "table/subtable1/subtable2/subtable3"
     */
    fun String?.getDBRef(vararg strArr: String): DatabaseReference {
        var ref = this.getDBRef().child(currentUser!!.uid)
        strArr.asSequence().filter { it != "" }.forEach { ref = ref.child(it) }
        return ref
    }


    fun String?.getDBRef(): DatabaseReference = database.getReference(this)

    fun <T : FirebaseObject> String?.update(valueObj: T): Task<Void>? = getDBRef(valueObj.id!!)!!.setValue(valueObj)

    fun <T : FirebaseObject> String?.remove(valueObj: T): Task<Void>? = getDBRef(valueObj.id!!)!!.removeValue()

    fun String?.putValueEventListener(
            valueEventListener: ValueEventListener,
            vararg childArray: String
    ) = getDBRef(*childArray).addValueEventListener(valueEventListener)


    fun String?.putChildEventListener(
            childEventListener: SimpleChildEventListener,
            vararg childArray: String
    ) = getDBRef(*childArray).addChildEventListener(childEventListener)

    fun String?.orderByKey(): Query = getDBRef().orderByKey()


    fun isSignedIn(): Boolean = FirebaseAuth.getInstance().currentUser != null

    fun <T> recyclerOptions(receiver: Class<T>, activity: BaseActivity, query: Query?): FirebaseRecyclerOptions<T> {
        return FirebaseRecyclerOptions.Builder<T>()
                .setQuery(query, receiver)
                .setLifecycleOwner(activity)
                .build()
    }

}

