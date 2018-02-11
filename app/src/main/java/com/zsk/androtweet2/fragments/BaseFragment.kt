package com.zsk.androtweet2.fragments

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import com.zsk.androtweet2.R
import com.zsk.androtweet2.helpers.utils.Enums
import com.zsk.androtweet2.helpers.utils.Enums.FragmentArguments.*
import kotlinx.android.synthetic.main.actions_bottom_sheet.*
import kotlinx.android.synthetic.main.actions_bottom_sheet.view.*

abstract class BaseFragment : Fragment() {
    abstract val layoutId: Int
    abstract val fab: FloatingActionButton
    abstract val bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>?
    var toggleSheetMenuListener: ToggleSheetMenuListener? = object : ToggleSheetMenuListener {
        override fun onToggle(selectedCount: Int) {
            toggleSheetMenu(selectedCount > 0)
            setInfoText(selectedCount)
        }
    }

    private fun setInfoText(selectedCount: Int) {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatCount = 1
        anim.repeatMode = Animation.REVERSE

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {
                var infoText: String

                if (selectedCount > 0)
                    bottom_sheet?.info?.text = String.format(getString(R.string.selection_info), selectedCount)
                else {
                    bottom_sheet?.info?.text = String.format(getString(R.string.selection_info), "No")
                    bottom_sheet?.select_all_icon?.isChecked = false
                }
            }
        })


        bottom_sheet?.info?.startAnimation(anim)
    }

    interface ToggleSheetMenuListener {
        fun onToggle(selectedCount: Int)
    }

    private var type: String? = "default"

    open var TAG = this.javaClass.simpleName!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(layoutId, container, false)

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

    override fun onResume() {
        super.onResume()
        setInfoText(0)
//        toggleSheetMenuListener?.onToggle(0)
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
        var scale = 0f
        val animate = fab.animate()
        with(this) {
            state = when {
                show || this.state == BottomSheetBehavior.STATE_COLLAPSED -> {
                    scale = 1F
                    BottomSheetBehavior.STATE_EXPANDED
                }
                else -> {
                    BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
        animate
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(200)
                .setInterpolator(LinearInterpolator()).start()
    }

}
