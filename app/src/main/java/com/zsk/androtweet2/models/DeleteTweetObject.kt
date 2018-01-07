package com.zsk.androtweet2.models

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName
import com.twitter.sdk.android.core.models.Tweet


/**
 * Created by kaloglu on 12/11/2017.
 */
class DeleteTweetObject : FirebaseObject {
    override fun getId(): Long = tweetId.toLong()

    @SerializedName("id")
    var tweetId:String=""
    var addedTime: Long = 0
    var userId: Long = 0

    constructor(tweetId: String,
                addedTime: Long,
                userId: Long
    ){
        this.tweetId= tweetId
        this.addedTime = addedTime
        this.userId = userId
    }

    constructor(tweet: Tweet) :
            this(tweetId = tweet.id.toString(), addedTime = System.currentTimeMillis(), userId = tweet.user.id)


    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result.put("tweetId", tweetId)
        result.put("addedTime", addedTime)
        result.put("userId", userId)

        return result
    }

}
