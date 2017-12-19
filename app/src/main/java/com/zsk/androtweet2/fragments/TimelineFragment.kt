package com.zsk.androtweet2.fragments

import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentItemTypes.LIST
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
open class TimelineFragment : BaseFragment() {

    fun setInstance(@FragmentTypes fragment_type: Long, @FragmentContentTypes content_type: Long) =
            super.setInstance(fragment_type, content_type, LIST)

}

