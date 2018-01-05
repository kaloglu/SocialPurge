package com.zsk.androtweet2.models

import com.google.firebase.database.Exclude
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.models.Tweet


/**
 * Created by kaloglu on 12/11/2017.
 */
class DeleteTweetObject : FirebaseObject {
    var addedTime: Long = 0
    var userId: Long = 0

    constructor() : super()

    constructor(id: Long,
                addedTime: Long,
                userId: Long
    ) : super(id) {
        this.addedTime = addedTime
        this.userId = userId
    }

    constructor(tweet: Tweet) :
            this(tweet.id, System.currentTimeMillis(), tweet.user.id)


    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result.put("tweetId", id)
        result.put("addedTime", addedTime)
        result.put("userId", userId)

        return result
    }

}
