package com.zsk.androtweet2.models

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.models.User


/**
 * Created by kaloglu on 12/11/2017.
 */
class TwitterAccount : AccountObject {
    @SerializedName("id", alternate = ["accountId"])
    @Exclude
    override var id: Long = 0

    var name: String = ""
    var realname: String = ""
    var profilePic: String = ""
    var token: String = ""
    var secret: String = ""

    constructor() : super()

    constructor(accountId: Long,
                username: String = "",
                fullName: String = "",
                profilePic: String = "",
                token: String,
                secret: String
    ) {
        this.id = accountId
        this.name = username
        this.realname = fullName
        this.profilePic = profilePic
        this.token = token
        this.secret = secret
    }

    constructor(user: User, authToken: TwitterAuthToken) :
            this(user.id, "@" + user.screenName, user.name, user.profileImageUrl, authToken.token, authToken.secret)

    constructor(twitterAccount: TwitterAccount) : this(
            twitterAccount.id,
            twitterAccount.name,
            twitterAccount.realname,
            twitterAccount.profilePic,
            twitterAccount.token,
            twitterAccount.secret
    )


    fun TwitterAccount.twitterAuth(): TwitterAuthToken = TwitterAuthToken(token, secret)

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result.put("name", name)
        result.put("realname", realname)
        result.put("profilePic", profilePic)
        result.put("token", token)
        result.put("secret", secret)

        return result
    }

}
