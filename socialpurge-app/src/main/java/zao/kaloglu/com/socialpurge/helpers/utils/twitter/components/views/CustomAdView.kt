/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package zao.kaloglu.com.socialpurge.helpers.utils.twitter.components.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout


abstract class CustomAdView
/**
 * Performs inflation from XML and apply a class-specific base style with the given dependency
 * provider.
 *
 * @param context  the context of the view
 * @param attrs    the attributes of the XML tag that is inflating the TweetView
 * @param defStyle An attribute in the current theme that contains a reference to a style
 * resource to apply to this view. If 0, no default style will be applied.
 * @throws IllegalArgumentException if the Tweet id is invalid.
 */
@JvmOverloads internal constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {

    /*
     * It's up to the extending class to determine what layout id to use
     */
    internal abstract val layout: Int

    init {

        inflateView(context)
        this.findSubviews()
    }

    /**
     * Inflate the TweetView using the layout that has been set.
     *
     * @param context The Context the view is running in.
     */
    private fun inflateView(context: Context) {
        LayoutInflater.from(context).inflate(layout, this, true)
    }

    /**
     * Find and hold subview references for quick lookup.
     */
    internal abstract fun findSubviews()

    /**
     * Render the Tweet by updating the subviews. For any data that is missing from the Tweet,
     * invalidate the subview value (e.g. text views set to empty string) for view recycling.
     * Do not call with render true until inflation has completed.
     */
    internal abstract fun render()

}
