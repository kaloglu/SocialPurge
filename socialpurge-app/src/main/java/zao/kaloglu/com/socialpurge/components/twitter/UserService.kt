package zao.kaloglu.com.socialpurge.components.twitter

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.models.User
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by kaloglu on 1.01.2018.
 */
interface UserService {
    /**
     * Customized via: @kaloglu
     * Returns a variety of information about the user specified by the required user_id or screen_name parameter.
     * The author’s most recent Tweet will be returned inline when possible.
     *
     *
     * GET users / lookup is used to retrieve a bulk collection of user objects.
     * You must be following a protected user to be able to see their most recent Tweet.
     *
     *
     * If you don’t follow a protected user, the users Tweet will be removed.
     * A Tweet will not always be returned in the current_status field.
     *
     * @param userId          (optional) The ID of the user for whom to return results for.
     * @param screenName      (optional) The screen name of the user for whom to return results for.
     * @param includeEntities (optional) The entities node will be omitted when set to false.
     * @param cb              The callback to invoke when the request completes.
     */
    @GET("/1.1/users/show.json?" + "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    fun show(@Query("user_id") userId: Long?,
             @Query("screen_name") screenName: String,
             @Query("include_entities") includeEntities: Boolean?,
             cb: Callback<User>)

    @GET("/1.1/users/lookup.json?" + "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    fun lookup(@Query("user_id") userId: String,
               @Query("include_entities") includeEntities: Boolean?,
               cb: Callback<List<User>>)

}
