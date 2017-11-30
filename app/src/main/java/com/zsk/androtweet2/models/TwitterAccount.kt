package com.zsk.androtweet2.models

import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.models.User

/**
 * Created by kaloglu on 12/11/2017.
 */
class TwitterAccount : FirebaseObject {
    lateinit var name: String
    lateinit var realname: String
    lateinit var profilePic: String
    var authToken: CustomAuthToken? = null

    constructor() : super()

    constructor(id: Long,
                username: String = "",
                fullName: String = "",
                profilePic: String = "",
                authToken: CustomAuthToken = CustomAuthToken()
    ) : super(id) {
        this.name = username
        this.realname = fullName
        this.profilePic = profilePic
        this.authToken = authToken
    }

    constructor(user: User, authToken: TwitterAuthToken) :
            this(user.id, "@" + user.screenName, user.name, user.profileImageUrl, CustomAuthToken(authToken))


    class CustomAuthToken {
        lateinit var token: String
        lateinit var secret: String
        var expired = false

        constructor()
        constructor(authToken: TwitterAuthToken) {
            this.token = authToken.token
            this.secret = authToken.secret
            this.expired = authToken.isExpired
        }

    }

}
