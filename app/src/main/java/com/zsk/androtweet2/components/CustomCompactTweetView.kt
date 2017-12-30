package com.zsk.androtweet2.components

import android.annotation.SuppressLint
import android.content.Context
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.CompactTweetView
import com.zsk.androtweet2.R

@SuppressLint("ViewConstructor")
/**
 * Created by kaloglu on 30.12.2017.
 */
class CustomCompactTweetView(
        context: Context?,
        tweet: Tweet?,
        styleResId: Int
) : CompactTweetView(context, tweet, styleResId) {

    override fun getLayout(): Int = R.layout.tw__tweet_compact

}
