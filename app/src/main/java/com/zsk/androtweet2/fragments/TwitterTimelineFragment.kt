package com.zsk.androtweet2.fragments

import com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes
import com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes.TWITTER

/**
 * Created by kaloglu on 16.12.2017.
 */

//TODO: update for using.
class TwitterTimelineFragment() : TimelineFragment() {

    fun setInstance(@FragmentContentTypes content_type: Long) = super.setInstance(TWITTER, content_type)
}
