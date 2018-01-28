package com.zsk.androtweet2.helpers.utils

import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.zsk.androtweet2.components.SimpleChildEventListener
import com.zsk.androtweet2.helpers.bases.BaseActivity
import com.zsk.androtweet2.models.AccountObject
import com.zsk.androtweet2.models.FirebaseObject

/**
 * Created by kaloglu on 11/11/2017.
 */
class FirebaseService {
    internal val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val auth: FirebaseAuth? = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser = auth!!.currentUser!!

    val PROFILES: String? = "profiles"
    val DELETION_QUEUE: String? = "delete_queue"
    val DEVICE_TOKENS: String? = "device_tokens"

    internal fun String?.getDBRefWithUID(vararg longArr: Long): DatabaseReference? =
            this.getDBRef(true, longArr.asSequence().joinToString(","))

    internal fun String?.getDBRef(hasUID: Boolean = false, vararg longArr: Long): DatabaseReference? =
            this.getDBRef(hasUID, longArr.asSequence().joinToString(","))

    /**
     * return db path.
     * eg.:
     *  "table".getDBRef("subtable1","subtable2","subtable3")
     *
     * result:
     *  "table/subtable1/subtable2/subtable3"
     */
    fun String?.getDBRefWithUID(vararg strArr: String): DatabaseReference = getDBRef(true, *strArr)

    fun String?.getDBRef(hasUID: Boolean = false, vararg strArr: String): DatabaseReference {
        var ref = this.getDBRef()
        if (hasUID)
            ref = ref.child(currentUser.uid)
        strArr.filter { it != "" }.forEach { ref = ref.child(it) }
        return ref
    }


    fun String?.getDBRef(hasUID: Boolean): DatabaseReference {
        var ref = this.getDBRef()
        if (hasUID)
            ref = ref.child(currentUser.uid)

        return ref
    }
    fun String?.getDBRef(): DatabaseReference = database.getReference(this)

    @JvmOverloads
    fun String?.updateWithUID(
            valueObj: FirebaseObject,
            completionListener: DatabaseReference.CompletionListener? = null
    ) = update(valueObj, completionListener, true)

    @JvmOverloads
    fun String?.updateWithUID(
            valueObj: AccountObject,
            completionListener: DatabaseReference.CompletionListener? = null
    ) = update(valueObj, completionListener, true)

    @JvmOverloads
    fun String?.updateWithUID(
            valueObj: Map<String, *>,
            completionListener: DatabaseReference.CompletionListener? = null
    ) = update(valueObj, completionListener, true)

    @JvmOverloads
    fun String?.update(
            valueObj: FirebaseObject,
            completionListener: DatabaseReference.CompletionListener? = null,
            hasUID: Boolean = false
    ) = getDBRef(hasUID, valueObj.id).setValue(valueObj, completionListener)

    fun String?.removeWitUID(valueObj: FirebaseObject): Task<Void>? = remove(valueObj, true)

    @JvmOverloads
    fun String?.update(
            valueObj: AccountObject,
            completionListener: DatabaseReference.CompletionListener? = null,
            hasUID: Boolean = false
    ) = getDBRef(hasUID, valueObj.id)?.setValue(valueObj, completionListener)

    fun String?.removeWitUID(valueObj: AccountObject): Task<Void>? = remove(valueObj, true)

    @JvmOverloads
    fun <T : FirebaseObject> String?.remove(
            valueObj: T,
            hasUID: Boolean = false
    ): Task<Void>? = getDBRef(hasUID, valueObj.id).removeValue()

    @JvmOverloads
    fun <T : AccountObject> String?.remove(
            valueObj: T,
            hasUID: Boolean = false
    ): Task<Void>? = getDBRef(hasUID, valueObj.id)?.removeValue()

    @JvmOverloads
    fun String?.update(
            valueObj: Map<String, *>,
            completionListener: DatabaseReference.CompletionListener? = null,
            hasUID: Boolean = false
    ) = getDBRef(hasUID).updateChildren(valueObj, completionListener)

    fun String?.putValueEventListener(
            valueEventListener: ValueEventListener,
            vararg childArray: String
    ) = this.putValueEventListener(false, valueEventListener, *childArray)

    fun String?.putValueEventListener(
            justOnce: Boolean = false,
            valueEventListener: ValueEventListener,
            vararg childArray: String
    ) {
        if (justOnce)
            getDBRefWithUID(*childArray).addListenerForSingleValueEvent(valueEventListener)
        else
            getDBRefWithUID(*childArray).addValueEventListener(valueEventListener)
    }


    fun String?.putChildEventListener(childEventListener: SimpleChildEventListener, vararg childArray: String)
            = getDBRefWithUID(*childArray).addChildEventListener(childEventListener)

    fun Query.putChildEventListener(childEventListener: SimpleChildEventListener)
            = this.addChildEventListener(childEventListener)

    fun String?.orderByKey(): Query {
        val ref = if (this.equals(PROFILES)) getDBRefWithUID("") else getDBRef()

        return ref.orderByKey()
    }

    fun String?.orderByChild(string: String): Query = getDBRef().orderByChild(string)


    fun isSignedIn(): Boolean = FirebaseAuth.getInstance().currentUser != null

    fun <T> recyclerOptions(receiver: Class<T>, activity: BaseActivity, query: Query?): FirebaseRecyclerOptions<T> {
        return FirebaseRecyclerOptions.Builder<T>()
                .setQuery(query, receiver)
                .setLifecycleOwner(activity)
                .build()
    }

}

