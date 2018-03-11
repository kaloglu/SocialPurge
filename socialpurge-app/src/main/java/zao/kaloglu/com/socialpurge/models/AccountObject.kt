package zao.kaloglu.com.socialpurge.models

import com.google.firebase.database.Exclude

/**
 * Created by kaloglu on 11/11/2017.
 */
interface AccountObject {
    @get:Exclude
    var id: Long
    @get:Exclude
    val idStr: String
        get() = id.toString()
}
