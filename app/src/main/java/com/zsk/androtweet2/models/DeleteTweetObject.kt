package com.zsk.androtweet2.models

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName


/**
 * Created by kaloglu on 12/11/2017.
 */
class DeleteTweetObject : FirebaseObject {
    @SerializedName("id")
    @Exclude
    override var id: String
    var list: MutableList<String>? = null


    constructor(id: String) {
        this.id = id
    }

    constructor(selectionList: MutableList<String>, uid: String, userId: String) : this(userId + "__" + uid) {
        this.list = selectionList
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result.put("id", id)

        return result
    }


}
