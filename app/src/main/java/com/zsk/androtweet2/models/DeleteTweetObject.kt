package com.zsk.androtweet2.models

import com.google.firebase.database.Exclude


/**
 * Created by kaloglu on 12/11/2017.
 */
class DeleteTweetObject(
        @get:Exclude override var id: String,
        var uid: String,
        var userId: Long,
        var queueDate: Long = System.currentTimeMillis()
) : FirebaseObject {

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
