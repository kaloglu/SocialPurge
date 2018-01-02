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

package com.zsk.androtweet2.components.twitter

import com.twitter.sdk.android.core.models.Tweet
import retrofit2.Call
import retrofit2.http.*

interface TimelineService {

    /**
     * Returns a collection of the most recent tweets posted by the user indicated by the
     * screen_name or user_id parameters.
     *
     *
     * User timelines belonging to protected users may only be requested when the authenticated user
     * either "owns" the timeline or is an approved follower of the owner.
     *
     *
     * The timeline returned is the equivalent of the one seen when you view a user's profile on
     * twitter.com.
     *
     *
     * The Twitter REST API goes back up to 3,200 of a user's most recent tweets.
     * Native retweets of other statuses by the user is included in this total, regardless of
     * whether include_rts is set to false when requesting this resource.
     *
     *
     * Always specify either an user_id or screen_name when requesting a user timeline.
     *
     * @param userId (optional) The ID of the user for whom to return results for.
     * @param screenName (optional) The screen name of the user for whom to return results for.
     * @param count (optional) Specifies the number of tweets to try and retrieve, up to a maximum
     * of 200. The value of count is best thought of as a limit to the number of tweets
     * to return because suspended or deleted content is removed after the count has
     * been applied. We include retweets in the count, even if include_rts is not
     * supplied. It is recommended you always send include_rts=1 when using this API
     * method.
     * @param sinceId (optional) Returns results with an ID greater than (that is, more recent than)
     * the specified ID. There are limits to the number of tweets which can be
     * accessed through the API. If the limit of tweets has occurred since the
     * since_id, the since_id will be forced to the oldest ID available.
     * @param maxId (optional) Returns results with an ID less than (that is, older than) or equal
     * to the specified ID.
     * @param trimUser (optional) When set to either true, t or 1, each Tweet returned in a timeline
     * will include a user object including only the status authors numerical ID.
     * Omit this parameter to receive the complete user object.
     * @param excludeReplies (optional) This parameter will prevent replies from appearing in the
     * returned timeline. Using exclude_replies with the count parameter will
     * mean you will receive up-to count tweets — this is because the count
     * parameter retrieves that many tweets before filtering out retweets and
     * replies. This parameter is only supported for JSON and XML responses.
     * @param contributeDetails (optional) This parameter enhances the contributors element of the
     * status response to include the screen_name of the contributor. By
     * default only the user_id of the contributor is included.
     * @param includeRetweets (optional) When set to false, the timeline will strip any native
     * retweets (though they will still count toward both the maximal length
     * of the timeline and the slice selected by the count parameter).
     * Note: If you're using the trim_user parameter in conjunction with
     * include_rts, the retweets will still contain a full user object.
     */
    @GET("/1.1/statuses/user_timeline.json?" + "tweet_mode=compact&include_cards=false&cards_platform=TwitterKit-13")
    fun userTimeline(@Query("user_id") userId: Long?,
                     @Query("screen_name") screenname: String?,
                     @Query("since_id") sinceId: Long?,
                     @Query("max_id") maxId: Long?,
                     @Query("count") maxCount: Int? = 50,
                     @Query("trim_user") trimUser: Boolean? = false,
                     @Query("exclude_replies") excludeReplies: Boolean? = false,
                     @Query("contributor_details") contributeDetails: Boolean? = true,
                     @Query("include_rts") includeRetweets: Boolean? = true): Call<List<Tweet>>

    /**
     * Destroys the status specified by the required ID parameter. The authenticating user must be
     * the author of the specified status. Returns the destroyed status if successful.
     *
     * @param id (required) The numerical ID of the desired Tweet.
     * @param trimUser (optional) When set to either true, t or 1, each Tweet returned in a timeline
     * will include a user object including only the status authors numerical ID.
     * Omit this parameter to receive the complete user object.
     */
    @FormUrlEncoded
    @POST("/1.1/statuses/destroy/{id}.json?" + "tweet_mode=compact&include_cards=false&cards_platform=TwitterKit-13")
    fun destroy(@Path("id") id: Long?,
                @Field("trim_user") trimUser: Boolean?): Call<Tweet>

    /**
     * Destroys the retweet specified by the required source Tweet's ID parameter. Returns the
     * source Tweet if successful.
     *
     * @param id (required) The numerical ID of the source Tweet.
     * @param trimUser (optional) When set to either true, t or 1, each Tweet returned in a timeline
     * will include a user object including only the status authors numerical ID.
     * Omit this parameter to receive the complete user object.
     */
    @FormUrlEncoded
    @POST("/1.1/statuses/unretweet/{id}.json?" + "tweet_mode=compact&include_cards=false&cards_platform=TwitterKit-13")
    fun unretweet(@Path("id") id: Long?,
                  @Field("trim_user") trimUser: Boolean?): Call<Tweet>
}
