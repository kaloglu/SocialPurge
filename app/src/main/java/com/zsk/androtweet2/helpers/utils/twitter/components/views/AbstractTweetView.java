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

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.internal.VineCardUtils;
import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.ImageValue;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetMediaClickListener;
import com.twitter.sdk.android.tweetui.internal.AspectRatioFrameLayout;
import com.twitter.sdk.android.tweetui.internal.MediaBadgeView;
import com.twitter.sdk.android.tweetui.internal.SpanClickHandler;
import com.twitter.sdk.android.tweetui.internal.TweetMediaUtils;
import com.twitter.sdk.android.tweetui.internal.TweetMediaView;
import com.zsk.androtweet2.R;
import com.zsk.androtweet2.components.twitter.TimelineDelegate;
import com.zsk.androtweet2.components.twitter.utils.TweetDateUtils;
import com.zsk.androtweet2.helpers.utils.twitter.components.others.FormattedTweetText;
import com.zsk.androtweet2.helpers.utils.twitter.components.others.TweetTextLinkifier;
import com.zsk.androtweet2.helpers.utils.twitter.components.others.TweetUtils;
import com.zsk.androtweet2.helpers.utils.twitter.components.others.Utils;
import com.zsk.androtweet2.helpers.utils.twitter.intefaces.LinkClickListener;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


abstract class AbstractTweetView<T extends Tweet> extends RelativeLayout {
    static final String TAG = AbstractTweetView.class.getSimpleName();
    public static final int DEFAULT_STYLE = R.style.tw__TweetDarkStyle_;
    static final String EMPTY_STRING = "";
    static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;

    static final double SECONDARY_TEXT_COLOR_LIGHT_OPACITY = 0.4;
    static final double SECONDARY_TEXT_COLOR_DARK_OPACITY = 0.35;
    static final double MEDIA_BG_LIGHT_OPACITY = 0.08;
    static final double MEDIA_BG_DARK_OPACITY = 0.12;

    static final long INVALID_ID = -1L;
    protected final TimelineDelegate<T> timelineDelegate;

    T tweet;
    T parent;

    // for testing
    int styleResId;
    boolean tweetActionsEnabled;
    //    private LinkClickListener linkClickListener;
//    TweetLinkClickListener tweetLinkClickListener;
    TweetMediaClickListener tweetMediaClickListener;
    private Uri permalinkUri;
    // layout views
    TextView fullNameView;
    TextView screenNameView;
    AspectRatioFrameLayout mediaContainer;
    TweetMediaView tweetMediaView;
    TextView contentView;
    MediaBadgeView mediaBadgeView;

    // color values
    int primaryTextColor;
    int secondaryTextColor;
    int actionColor;
    int actionHighlightColor;
    int mediaBgColor;
    // resource id's
    int photoErrorResId;

    /**
     * Performs inflation from XML and apply a class-specific base style with the given dependency
     * provider.
     *
     * @param context  the context of the view
     * @param attrs    the attributes of the XML tag that is inflating the TweetView
     * @param defStyle An attribute in the current theme that contains a reference to a style
     *                 resource to apply to this view. If 0, no default style will be applied.
     * @throws IllegalArgumentException if the Tweet id is invalid.
     */
    AbstractTweetView(Context context, AttributeSet attrs, int defStyle, TimelineDelegate<T> timelineDelegate) {
        super(context, attrs, defStyle);

        inflateView(context);
        findSubviews();
        this.timelineDelegate = timelineDelegate;
    }

    /**
     * Inflate the TweetView using the layout that has been set.
     *
     * @param context The Context the view is running in.
     */
    private void inflateView(Context context) {
        LayoutInflater.from(context).inflate(getLayout(), this, true);
    }

    /**
     * Find and hold subview references for quick lookup.
     */
    void findSubviews() {
        // Tweet attribution (avatar, name, screen name, etc.)
        fullNameView = findViewById(R.id.tw__tweet_author_full_name);
        screenNameView = findViewById(R.id.tw__tweet_author_screen_name);
        mediaContainer =
                findViewById(R.id.tw__aspect_ratio_media_container);
        tweetMediaView = findViewById(R.id.tweet_media_view);
        contentView = findViewById(R.id.tw__tweet_text);
        mediaBadgeView = findViewById(R.id.tw__tweet_media_badge);
    }

    /*
     * It's up to the extending class to determine what layout id to use
     */
    abstract int getLayout();

    /**
     * Set the Tweet to be displayed and update the subviews. For any data that is missing from
     * the Tweet, invalidate the subview value (e.g. text views set to empty string) for view
     * recycling. Cannot be called before inflation has completed.
     *
     * @param tweet Tweet data
     */

    public void setTweet(T tweet) {
        setTweet(tweet, null);
    }

    public void setTweet(T tweet, T parent) {
        this.tweet = tweet;
        this.parent = parent;
        render();
    }

    /**
     * @return the Tweet of the TweetView
     */
    public T getTweet() {
        return tweet;
    }

    /**
     * Render the Tweet by updating the subviews. For any data that is missing from the Tweet,
     * invalidate the subview value (e.g. text views set to empty string) for view recycling.
     * Do not call with render true until inflation has completed.
     */
    void render() {
        final T displayTweet = (T)TweetUtils.getDisplayTweet(tweet);

        setName(displayTweet);
        setScreenName(displayTweet);
        setTweetMedia(displayTweet);
        setText(displayTweet);
        setContentDescription(displayTweet);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isEnabled())
                    timelineDelegate.selectionToggle((parent == null) ? tweet : parent);
            }
        });

    }

    /**
     * Sets the Tweet author name. If author name is unavailable, resets to empty string.
     */
    private void setName(T displayTweet) {
        if (displayTweet != null && displayTweet.user != null) {
            fullNameView.setText(displayTweet.user.name);
        } else {
            fullNameView.setText(EMPTY_STRING);
        }
    }

    /**
     * Sets the Tweet author screen name. If screen name is unavailable, resets to empty string.
     */
    private void setScreenName(T displayTweet) {
        if (displayTweet != null && displayTweet.user != null) {
            screenNameView.setText(UserUtils.formatScreenName(displayTweet.user.screenName));
        } else {
            screenNameView.setText(EMPTY_STRING);
        }
    }

    /**
     * @param displayTweet The unformatted Tweet
     * @return The linkified text with display url's subbed for t.co links
     */
    protected CharSequence getLinkifiedText(T displayTweet) {
        FormattedTweetText formattedText = null;
        if (timelineDelegate != null)
            formattedText = timelineDelegate.getTweetRepository().formatTweetText(displayTweet);

        final boolean stripVineCard = displayTweet.card != null
                && VineCardUtils.isVine(displayTweet.card);

        final boolean stripQuoteTweet = TweetUtils.showQuoteTweet(displayTweet);

        return TweetTextLinkifier.linkifyUrls(formattedText, new LinkClickListener() {
                    @Override
                    public void onUrlClicked(String url) {
                        AbstractTweetView.this.callOnClick();
                    }
                }, actionColor,
                actionHighlightColor, stripQuoteTweet, stripVineCard);
    }

    /**
     * Sets the Tweet text. If the Tweet text is unavailable, resets to empty string.
     */
    private void setText(T displayTweet) {
        contentView.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
//        final CharSequence tweetText = displayTweet.text;
        final CharSequence tweetText = Utils.charSeqOrEmpty(getLinkifiedText(displayTweet));
        SpanClickHandler.enableClicksOnSpans(contentView);
        if (!TextUtils.isEmpty(tweetText)) {
            contentView.setText(tweetText);
            contentView.setVisibility(VISIBLE);
        } else {
            contentView.setText(EMPTY_STRING);
            contentView.setVisibility(GONE);
        }
    }


    final void setTweetMedia(T displayTweet) {
        clearTweetMedia();

        if (displayTweet == null) {
            return;
        }

        if (displayTweet.card != null && VineCardUtils.isVine(displayTweet.card)) {
            final Card card = displayTweet.card;
            final ImageValue imageValue = VineCardUtils.getImageValue(card);
            final String playerStreamUrl = VineCardUtils.getStreamUrl(card);
            // Make sure we have required bindings for Vine card
            if (imageValue != null && !TextUtils.isEmpty(playerStreamUrl)) {
                setViewsForMedia(getAspectRatio(imageValue));
                tweetMediaView.setVineCard(displayTweet);
                mediaBadgeView.setVisibility(View.VISIBLE);
                mediaBadgeView.setCard(card);
            }
        } else if (TweetMediaUtils.hasSupportedVideo(displayTweet)) {
            final MediaEntity mediaEntity = TweetMediaUtils.getVideoEntity(displayTweet);
            setViewsForMedia(getAspectRatio(mediaEntity));
            tweetMediaView.setTweetMediaEntities(tweet, Collections.singletonList(mediaEntity));
            mediaBadgeView.setVisibility(View.VISIBLE);
            mediaBadgeView.setMediaEntity(mediaEntity);
        } else if (TweetMediaUtils.hasPhoto(displayTweet)) {
            final List<MediaEntity> mediaEntities = TweetMediaUtils.getPhotoEntities(displayTweet);
            setViewsForMedia(getAspectRatioForPhotoEntity(mediaEntities.size()));
            tweetMediaView.setTweetMediaEntities(displayTweet, mediaEntities);
            tweetMediaView.setTweetMediaClickListener(new TweetMediaClickListener() {
                @Override
                public void onMediaEntityClick(Tweet tweet, MediaEntity entity) {
                    AbstractTweetView.this.callOnClick();
                }
            });
            mediaBadgeView.setVisibility(View.GONE);
        }
    }

    void setViewsForMedia(double aspectRatio) {
        mediaContainer.setVisibility(ImageView.VISIBLE);
        mediaContainer.setAspectRatio(aspectRatio);
        tweetMediaView.setVisibility(View.VISIBLE);
    }

    protected double getAspectRatio(MediaEntity photoEntity) {
        if (photoEntity == null || photoEntity.sizes == null || photoEntity.sizes.medium == null ||
                photoEntity.sizes.medium.w == 0 || photoEntity.sizes.medium.h == 0) {
            return DEFAULT_ASPECT_RATIO;
        }

        return (double) photoEntity.sizes.medium.w / photoEntity.sizes.medium.h;
    }

    protected double getAspectRatio(ImageValue imageValue) {
        if (imageValue == null || imageValue.width == 0 || imageValue.height == 0) {
            return DEFAULT_ASPECT_RATIO;
        }

        return (double) imageValue.width / imageValue.height;
    }

    protected abstract double getAspectRatioForPhotoEntity(int photoCount);

    protected void clearTweetMedia() {
        mediaContainer.setVisibility(ImageView.GONE);
    }

    void setContentDescription(T displayTweet) {
        if (!resolvableTweet(displayTweet))
            return;

        final String tweetText = displayTweet.text;

        final long createdAt = TweetDateUtils.apiTimeToLong(displayTweet.createdAt);
        String timestamp = DateFormat.getDateInstance().format(new Date(createdAt));

        setContentDescription(getResources().getString(R.string.tw__tweet_content_description,
                displayTweet.user.name, tweetText, timestamp));
    }

    private boolean resolvableTweet(T displayTweet) {
        return displayTweet.text != null && displayTweet.user != null && displayTweet.createdAt != null;
    }

}
