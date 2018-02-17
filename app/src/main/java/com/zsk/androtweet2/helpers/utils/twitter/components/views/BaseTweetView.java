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

package com.zsk.androtweet2.helpers.utils.twitter.components.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.zsk.androtweet2.R;
import com.zsk.androtweet2.components.twitter.TimelineDelegate;
import com.zsk.androtweet2.components.twitter.utils.ColorUtils;
import com.zsk.androtweet2.components.twitter.utils.TweetDateUtils;
import com.zsk.androtweet2.helpers.utils.twitter.components.others.TweetUtils;
import com.zsk.androtweet2.helpers.utils.twitter.components.others.Utils;

import static com.zsk.androtweet2.R.styleable.tw__TweetView_tw__tweet_id;

@SuppressLint("PrivateResource")
public abstract class BaseTweetView extends AbstractTweetView {

    TextView retweetedByView;
    ImageView twitterLogoView;
    TextView timestampView;
    ImageView avatarView;
    ViewGroup quoteTweetHolder;
    QuoteTweetView quoteTweetView;
    View bottomSeparator;

    // color values
    int containerBgColor;
    int birdLogoResId;
    int retweetIconResId;
    // styled drawables for images
    ColorDrawable avatarMediaBg;

    /**
     * Constructs a view from the given Tweet.
     * @param context the context of the view
     * @param tweet a Tweet object
     */
    BaseTweetView(Context context, Tweet tweet, TimelineDelegate<Tweet> timelineDelegate) {
        this(context, tweet, DEFAULT_STYLE,timelineDelegate);
    }

    /**
     * Constructs a view from the given Tweet using the given dependency provider
     * @param context the context of the view
     * @param tweet a Tweet object
     * @param styleResId resource id of the Tweet view style
     */
    BaseTweetView(Context context, Tweet tweet, int styleResId, TimelineDelegate<Tweet> timelineDelegate) {
        super(context, null, styleResId, timelineDelegate);

        initAttributes(styleResId);
        applyStyles();
        setTweet(tweet);
    }

    /* XML Constructors */

    /**
     * Constructs a view with data retrieved from the API, for the XML defined tweetId.
     * @param context the context of the view
     * @param attrs the attributes of the XML tag that is inflating the TweetView.
     * @throws IllegalArgumentException if the Tweet id is invalid.
     */
    public BaseTweetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Performs inflation from XML and apply a class-specific base style.
     * @param context the context of the view
     * @param attrs the attributes of the XML tag that is inflating the TweetView.
     * @param defStyle An attribute in the current theme that contains a reference to a style
     *                 resource to apply to this view. If 0, no default style will be applied.
     * @throws IllegalArgumentException if the Tweet id is invalid.
     */
    public BaseTweetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle,null);

        initXmlAttributes(context, attrs);
        applyStyles();
    }

    /**
     * Initializes attributes needed before view inflation. This initializer should be called by the
     * programmatic constructors. For programmatic Tweet views, the style is passed as a constructor
     * argument.
     */
    private void initAttributes(int styleResId) {
        this.styleResId = styleResId;
        final TypedArray a = getContext().getTheme().obtainStyledAttributes(styleResId,
                R.styleable.tw__TweetView);
        try {
            setStyleAttributes(a);
        } finally {
            a.recycle();
        }
    }

    /**
     * Initializes XML attributes needed before view inflation. This initializer should be called
     * by the XML constructor. For XML Tweet views, the style is obtained from XML attrs.
     * @param context the context of the view
     * @param attrs set of raw XML attributes associated with the view's XML tag
     * @throws IllegalArgumentException if the tw__tweet_id XML attribute is invalid
     */
    private void initXmlAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        // parse the xml attributes by resolving resource references
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.tw__TweetView, 0, 0);
        try {
            setXmlDataAttributes(a);
            setStyleAttributes(a);
        } finally {
            a.recycle();
        }
    }

    /**
     * Parses and sets the Tweet data XML attributes. Must be called before view inflation.
     * @param a A TypedArray holding the attribute values obtained from the XML attributes
     * @throws IllegalArgumentException if the tw__tweet_id XML attribute is invalid
     */
    private void setXmlDataAttributes(TypedArray a) {
         final long tweetId = Utils.numberOrDefault(a.getString(tw__TweetView_tw__tweet_id), INVALID_ID);
        if (tweetId <= 0) {
            throw new IllegalArgumentException("Invalid tw__tweet_id");
        }
        // XML special case. The screen_name is not known yet. A permalink can be constructed and
        // followed. Permalink should be updated once the loadTweet call receives the Tweet.
        this.tweet = new TweetBuilder().setId(tweetId).build();
    }

    /**
     * Parses and sets style attributes. Must be called before view inflation. Defaults style
     * attributes to the light style values.
     * @param a A TypedArray holding style-related attribute values.
     */
    private void setStyleAttributes(TypedArray a) {
        // Styled via attributes
        containerBgColor = a.getColor(R.styleable.tw__TweetView_tw__container_bg_color,
                getResources().getColor(R.color.tw__tweet_light_container_bg_color));
        primaryTextColor = a.getColor(R.styleable.tw__TweetView_tw__primary_text_color,
                getResources().getColor(R.color.tw__tweet_light_primary_text_color));
        actionColor = a.getColor(
                R.styleable.tw__TweetView_tw__action_color,
                getResources().getColor(R.color.tw__tweet_action_color));
        actionHighlightColor = a.getColor(
                R.styleable.tw__TweetView_tw__action_highlight_color,
                getResources().getColor(R.color.tw__tweet_action_light_highlight_color));
        tweetActionsEnabled =
                a.getBoolean(R.styleable.tw__TweetView_tw__tweet_actions_enabled, false);

        // Calculated colors
        final boolean isLightBg = ColorUtils.isLightColor(containerBgColor);

        if (isLightBg) {
            photoErrorResId = R.drawable.tw__ic_tweet_photo_error_light;
            birdLogoResId = R.drawable.tw__ic_logo_blue;
            retweetIconResId = R.drawable.tw__ic_retweet_light;
        } else {
            photoErrorResId = R.drawable.tw__ic_tweet_photo_error_dark;
            birdLogoResId = R.drawable.tw__ic_logo_white;
            retweetIconResId = R.drawable.tw__ic_retweet_dark;
        }

        // offset from white when background is light
        secondaryTextColor = ColorUtils.calculateOpacityTransform(
                isLightBg ? SECONDARY_TEXT_COLOR_LIGHT_OPACITY : SECONDARY_TEXT_COLOR_DARK_OPACITY,
                isLightBg ? Color.WHITE : Color.BLACK,
                primaryTextColor
        );

        // offset from black when background is light
        mediaBgColor = ColorUtils.calculateOpacityTransform(
                isLightBg ? MEDIA_BG_LIGHT_OPACITY : MEDIA_BG_DARK_OPACITY,
                isLightBg ? Color.BLACK : Color.WHITE,
                containerBgColor
        );

        avatarMediaBg = new ColorDrawable(mediaBgColor);
    }

    /**
     * Find and hold subview references for quick lookup.
     */
    @Override
    void findSubviews() {
        super.findSubviews();

        avatarView = findViewById(R.id.tw__tweet_author_avatar);
        timestampView = findViewById(R.id.tw__tweet_timestamp);
        twitterLogoView = findViewById(R.id.tw__twitter_logo);
        retweetedByView = findViewById(R.id.tw__tweet_retweeted_by);
        quoteTweetHolder = findViewById(R.id.quote_tweet_holder);
        bottomSeparator = findViewById(R.id.bottom_separator);
    }

    /**
     * Render the Tweet by updating the subviews. For any data that is missing from the Tweet,
     * invalidate the subview value (e.g. text views set to empty string) for view recycling.
     * Do not call with render true until inflation has completed.
     */
    @Override
    void render() {
        super.render();

        final Tweet displayTweet = TweetUtils.getDisplayTweet(tweet);
        setProfilePhotoView(displayTweet);
        setTimestamp(displayTweet);
        showRetweetedBy(tweet);
        setQuoteTweet(tweet);

    }

    void setQuoteTweet(Tweet tweet) {
        quoteTweetView = null;
        quoteTweetHolder.removeAllViews();
        if (tweet != null && TweetUtils.showQuoteTweet(tweet)) {
            quoteTweetView = new QuoteTweetView(getContext());
            quoteTweetView.setStyle(primaryTextColor, secondaryTextColor, actionColor,
                    actionHighlightColor, mediaBgColor, photoErrorResId);
            quoteTweetView.setTweet(tweet.quotedStatus);
            quoteTweetHolder.setVisibility(VISIBLE);
            quoteTweetHolder.addView(quoteTweetView);
        } else {
            quoteTweetHolder.setVisibility(GONE);
        }
    }

    /**
     * Toggles display of "Retweeted by" text based on status from the API.
     * @param tweet The status from the API, if it is a retweet show the "retweeted by" text
     */
    void showRetweetedBy(Tweet tweet) {
        if (tweet == null || tweet.retweetedStatus == null) {
            retweetedByView.setVisibility(GONE);
        } else {
            retweetedByView.setText(
                    getResources().getString(R.string.tw__retweeted_by_format, tweet.user.name));
            retweetedByView.setVisibility(VISIBLE);
        }
    }

    /**
     * Apply the style attributes to the Tweet subviews. Must be called after view inflation and
     * findSubviews.
     */
    protected void applyStyles() {
        if (isSelected())
            setBackgroundColor(ContextCompat.getColor(getContext(),R.color.tw__tweet_action_dark_highlight_color));
        else
            setBackgroundColor(containerBgColor);
        fullNameView.setTextColor(primaryTextColor);
        screenNameView.setTextColor(secondaryTextColor);
        contentView.setTextColor(primaryTextColor);
        tweetMediaView.setMediaBgColor(mediaBgColor);
        tweetMediaView.setPhotoErrorResId(photoErrorResId);
        avatarView.setImageDrawable(avatarMediaBg);
        timestampView.setTextColor(secondaryTextColor);
        twitterLogoView.setImageResource(birdLogoResId);
        retweetedByView.setTextColor(secondaryTextColor);
    }

    /**
     * Set the timestamp if data from the Tweet is available. If timestamp cannot be determined,
     * set the timestamp to an empty string to handle view recycling.
     */
    private void setTimestamp(Tweet displayTweet) {
        final String formattedTimestamp;
        if (displayTweet != null && displayTweet.createdAt != null &&
                TweetDateUtils.isValidTimestamp(displayTweet.createdAt)) {
            final Long createdAtTimestamp
                    = TweetDateUtils.apiTimeToLong(displayTweet.createdAt);
            final String timestamp = TweetDateUtils.getRelativeTimeString(getResources(),
                    System.currentTimeMillis(),
                    createdAtTimestamp);
            formattedTimestamp = TweetDateUtils.dotPrefix(timestamp);
        } else {
            formattedTimestamp = EMPTY_STRING;
        }

        timestampView.setText(formattedTimestamp);
    }

    /**
     * Can be null if run before TweetUi#doInBackground completes
     */
    Picasso getImageLoader() {
        return TweetUi.getInstance().getImageLoader();
    }
    
    /**
     * Sets the profile photo. If the profile photo url is available from the Tweet, sets the the
     * default avatar background and attempts to load the image. If the url is not available, just
     * sets the default avatar background. Setting the default background upfront handles view
     * recycling.
     */
    void setProfilePhotoView(Tweet displayTweet) {
        final Picasso imageLoader = getImageLoader();

        if (imageLoader == null) return;

        final String url;
        if (displayTweet == null || displayTweet.user == null) {
            url = null;
        } else {
            url = UserUtils.getProfileImageUrlHttps(displayTweet.user,
                    UserUtils.AvatarSize.REASONABLY_SMALL);
        }

        imageLoader.load(url).placeholder(avatarMediaBg).into(avatarView);
    }
}
