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

package com.zsk.androtweet2.helpers.utils.tweetui;

import com.twitter.sdk.android.core.models.Tweet;

/**
 * Interface to be invoked when URL is clicked.
 */
public interface TweetLinkClickListener {
    /**
     * Called when URL clicked.
     * @param tweet The Tweet that was clicked.
     * @param url The URL that was clicked.
     */
    void onLinkClick(Tweet tweet, String url);
}