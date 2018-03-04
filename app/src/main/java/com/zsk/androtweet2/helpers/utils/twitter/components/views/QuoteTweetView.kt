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

package com.zsk.androtweet2.helpers.utils.twitter.components.views

import android.annotation.SuppressLint
import android.content.Context
import com.twitter.sdk.android.core.models.MediaEntity
import com.twitter.sdk.android.core.models.Tweet
import com.zsk.androtweet2.R
import com.zsk.androtweet2.components.twitter.TimelineDelegate

@SuppressLint("ViewConstructor")
internal class QuoteTweetView
(context: Context, timelineDelegate: TimelineDelegate<Tweet>) :
        AbstractTweetView(
                context,
                null,
                0,
                timelineDelegate
        ) {

    fun setStyle(primaryTextColor: Int, secondaryTextColor: Int, actionColor: Int,
                 actionHighlightColor: Int, mediaBgColor: Int, photoErrorResId: Int) {
        this.primaryTextColor = primaryTextColor
        this.secondaryTextColor = secondaryTextColor
        this.actionColor = actionColor
        this.actionHighlightColor = actionHighlightColor
        this.mediaBgColor = mediaBgColor
        this.photoErrorResId = photoErrorResId

        applyStyles()
    }

    override fun getLayout(): Int = R.layout.tw__tweet_quote

    internal override fun render() {
        super.render()

        // Redraw screen name on recycle
        screenNameView.requestLayout()
    }

    protected fun applyStyles() {
        val mediaViewRadius = resources.getDimensionPixelSize(R.dimen.tw__media_view_radius)
        tweetMediaView.setRoundedCornersRadii(0, 0, mediaViewRadius, mediaViewRadius)

        setBackgroundResource(R.drawable.tw__quote_tweet_border)
        fullNameView.setTextColor(primaryTextColor)
        screenNameView.setTextColor(secondaryTextColor)
        contentView.setTextColor(primaryTextColor)
        tweetMediaView.setMediaBgColor(mediaBgColor)
        tweetMediaView.setPhotoErrorResId(photoErrorResId)
    }

    /**
     * Returns the desired aspect ratio of the Tweet media entity according to "sizes" metadata
     * and the aspect ratio display rules.
     * @param photoEntity the first
     * @return the target image and bitmap width to height aspect ratio
     */
    override fun getAspectRatio(photoEntity: MediaEntity): Double {
        val ratio = super.getAspectRatio(photoEntity)
        return if (ratio <= SQUARE_ASPECT_RATIO) {
            // portrait (tall) photos should be cropped to be square aspect ratio
            SQUARE_ASPECT_RATIO
        } else if (ratio > MAX_LANDSCAPE_ASPECT_RATIO) {
            // the widest landscape photos allowed are 3:1
            MAX_LANDSCAPE_ASPECT_RATIO
        } else if (ratio < MIN_LANDSCAPE_ASPECT_RATIO) {
            // the tallest landscape photos allowed are 4:3
            MIN_LANDSCAPE_ASPECT_RATIO
        } else {
            // landscape photos between 3:1 to 4:3 present the original width to height ratio
            ratio
        }
    }

    /**
     * Returns the desired aspect ratio for Tweet that contains photo entities
     *
     * @param photoCount total count of photo entities
     * @return the target image and bitmap width to height aspect ratio
     */
    override fun getAspectRatioForPhotoEntity(photoCount: Int): Double {
        return DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER
    }

    companion object {
        private val VIEW_TYPE_NAME = "quote"
        private val SQUARE_ASPECT_RATIO = 1.0
        private val MAX_LANDSCAPE_ASPECT_RATIO = 3.0
        private val MIN_LANDSCAPE_ASPECT_RATIO = 4.0 / 3.0
        private val DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER = 16.0 / 10.0
    }

}
