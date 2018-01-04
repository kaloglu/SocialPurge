package com.zsk.androtweet2.fragments

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.zsk.androtweet2.helpers.utils.Enums
import com.zsk.androtweet2.helpers.utils.Enums.FragmentArguments.*

abstract class BaseFragment : Fragment() {
    abstract val layoutId: Int
    abstract val bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>?
    var toggleSheetMenuListener: ToggleSheetMenuListener? = object : ToggleSheetMenuListener {
        override fun onToggle(show: Boolean) {
            toggleSheetMenu(show)
        }
    }

    interface ToggleSheetMenuListener {
        fun onToggle(show: Boolean)
    }

    private var type: String? = "default"

    open var TAG = this.javaClass.simpleName!!

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater!!.inflate(layoutId, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        onFragmentCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated() called with: savedInstanceState = [$savedInstanceState]")
        super.onActivityCreated(savedInstanceState)
    }

    private fun onFragmentCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onFragmentCreated() called with: savedInstanceState = [$savedInstanceState]")
        initializeScreenObjects()
        designScreen()
    }

    abstract fun designScreen()

    abstract fun initializeScreenObjects()

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fragment_type @link
     * @param content_type Parameter 2.
     * @param item_type Parameter 3.
     * @return A new instance of fragment BaseFragment.
     */

    internal fun getInstance(@Enums.FragmentTypes fragment_type: Long, @Enums.FragmentContentTypes content_type: Long, @Enums.FragmentItemTypes item_type: Long): BaseFragment {
        this.arguments = Bundle().apply {
            putLong(FRAGMENT_TYPE, fragment_type)
            putLong(CONTENT_TYPE, content_type)
            putLong(ITEM_TYPE, item_type)
        }

        return this
    }

    fun toggleSheetMenu(show: Boolean = false) {
        bottomSheetBehavior?.toggleSheetState(show)
    }

    private fun BottomSheetBehavior<*>.toggleSheetState(show: Boolean = false) {
        with(this) {
            state = when {
                show || this.state == BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
                else -> BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

}
