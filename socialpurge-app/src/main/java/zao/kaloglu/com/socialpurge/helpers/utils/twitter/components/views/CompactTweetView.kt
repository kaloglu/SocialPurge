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

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.twitter.sdk.android.core.models.MediaEntity
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.tw__tweet_compact.view.*
import zao.kaloglu.com.socialpurge.components.twitter.TimelineDelegate

@SuppressLint("ViewConstructor")
class CompactTweetView(
        context: Context,
        tweet: Tweet,
        timelineDelegate: TimelineDelegate
) : zao.kaloglu.com.socialpurge.helpers.utils.twitter.components.views.BaseTweetView(context, tweet, timelineDelegate) {

    override fun getLayout(): Int = zao.kaloglu.com.socialpurge.R.layout.tw__tweet_compact_card

    override fun render() {
        super.render()
        if (tweet.idStr == null)
            return

        // Redraw screen name on recycle, because TextView doesn't resize when text length changes
        screenNameView.requestLayout()
        isSelected = timelineDelegate.isSelected(tweet)
//        val color = if (isSelected) R.color.md_blue_50 else R.color.tweet_background_color
//        tweet_view?.setBackgroundColor(ContextCompat.getColor(context, color))

        this.alpha = 1f
        this.isEnabled = true
        tw__tweet_inqueue.visibility = View.GONE

        if (zao.kaloglu.com.socialpurge.SocialPurgeApp.instance.deleteQueue.contains(tweet.idStr)) {
            this.alpha = 0.5f
            this.isEnabled = false
            tw__tweet_inqueue.visibility = View.VISIBLE
        }
        applyStyles()
    }



    override fun applyStyles() {
        super.applyStyles()

        val mediaViewRadius = resources.getDimensionPixelSize(zao.kaloglu.com.socialpurge.R.dimen.tw__media_view_radius)
        tweetMediaView.setRoundedCornersRadii(mediaViewRadius, mediaViewRadius,
                mediaViewRadius, mediaViewRadius)
    }

    /**
     * Returns the desired aspect ratio of the Tweet media entity according to "sizes" metadata
     * and the aspect ratio display rules.
     *
     * @param photoEntity the first
     * @return the target image and bitmap width to height aspect ratio
     */
    override fun getAspectRatio(photoEntity: MediaEntity): Double {
        val ratio = super.getAspectRatio(photoEntity)
        return when {
            ratio <= SQUARE_ASPECT_RATIO -> // portrait (tall) photos should be cropped to be square aspect ratio
                SQUARE_ASPECT_RATIO
            ratio > MAX_LANDSCAPE_ASPECT_RATIO -> // the widest landscape photos allowed are 3:1
                MAX_LANDSCAPE_ASPECT_RATIO
            ratio < MIN_LANDSCAPE_ASPECT_RATIO -> // the tallest landscape photos allowed are 4:3
                MIN_LANDSCAPE_ASPECT_RATIO
            else -> // landscape photos between 3:1 to 4:3 present the original width to height ratio
                ratio
        }
    }

    /**
     * Returns the desired aspect ratio for Tweet that contains photo entities
     *
     * @param photoCount total count of photo entities
     * @return the target image and bitmap width to height aspect ratio
     */
    override fun getAspectRatioForPhotoEntity(photoCount: Int): Double =
            DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER

    companion object {
        private val SQUARE_ASPECT_RATIO = 1.0
        private val MAX_LANDSCAPE_ASPECT_RATIO = 3.0
        private val MIN_LANDSCAPE_ASPECT_RATIO = 4.0 / 3.0
        private val DEFAULT_ASPECT_RATIO_MEDIA_CONTAINER = 16.0 / 10.0
    }
}
