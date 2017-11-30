package com.zsk.androtweet2.models

/**
 * Created by kaloglu on 11/11/2017.
 */
abstract class FirebaseObject {
    var id: Long = 0

    constructor()

    constructor(id: Long) {
        this.id = id
    }
}
