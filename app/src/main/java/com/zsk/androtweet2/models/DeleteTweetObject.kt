package com.zsk.androtweet2.models

import com.google.firebase.database.Exclude


/**
 * Created by kaloglu on 12/11/2017.
 */
class DeleteTweetObject : FirebaseObject {
    @get:Exclude
    override lateinit var id: String
    lateinit var uid: String
    lateinit var userId: String
    var queueDate: Long = System.currentTimeMillis()

    @JvmOverloads
    constructor(
            id: String,
            uid: String,
            userId: String,
            queueDate: Long = System.currentTimeMillis()
    ) : this() {
        this.id = id
        this.uid = uid
        this.userId = userId
        this.queueDate=queueDate
    }

    constructor()

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result["id"] = id
        result["uid"] = uid
        result["userId"] = userId
        result["queueDate"] = queueDate

        return result
    }


}
