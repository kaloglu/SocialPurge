package com.zsk.androtweet2.fragments

import com.zsk.androtweet2.R
import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentItemTypes.LIST
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
abstract class TimelineFragment : BaseFragment() {

    fun getInstance(@FragmentTypes fragment_type: Long, @FragmentContentTypes content_type: Long) =
            super.getInstance(fragment_type, content_type, LIST)

}

