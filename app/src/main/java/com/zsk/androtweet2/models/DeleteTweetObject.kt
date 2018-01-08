package com.zsk.androtweet2.models

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName
import com.twitter.sdk.android.core.models.Tweet


/**
 * Created by kaloglu on 12/11/2017.
 */
class DeleteTweetObject : FirebaseObject {
    @SerializedName("id", alternate = ["tweetId"])
    override var id: String

    var addedTime: String
    var userId: String
    var uId: String = ""

    constructor(tweetId: String,
                addedTime: String,
                userId: String,
                uId: String
    ) {
        this.id = tweetId
        this.addedTime = addedTime
        this.userId = userId
        this.uId = uId
    }

    constructor(tweet: Tweet, firebaseUser: FirebaseUser) :
            this(
                    tweet.id.toString(),
                    System.currentTimeMillis().toString(),
                    tweet.user.id.toString(),
                    firebaseUser.uid
            )


    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result.put("tweetId", id)
        result.put("addedTime", addedTime)
        result.put("userId", userId)
        result.put("uId", uId)

        return result
    }

}
