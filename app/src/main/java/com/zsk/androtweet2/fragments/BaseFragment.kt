package com.zsk.androtweet2.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zsk.androtweet2.helpers.utils.Enums
import com.zsk.androtweet2.helpers.utils.Enums.FragmentArguments.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BaseFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BaseFragment.getInstance] factory method to
 * create an instance of this fragment.
 */
abstract class BaseFragment : Fragment() {
    abstract val layoutId: Int

    private var type: String? = "default"
    private var mListener: OnFragmentInteractionListener? = null

    open var TAG = this.javaClass.simpleName!!

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater!!.inflate(layoutId, container, false)

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) mListener!!.onFragmentInteraction(uri)
    }

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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        when (context) {
            is OnFragmentInteractionListener -> mListener = context
            else -> throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

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

}
