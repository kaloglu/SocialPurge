import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;

/**
 * Created by kaloglu on 05/11/2017.
 */

public class test {

    public RecyclerView.Adapter create(Context context,
                                       Timeline<Tweet> timeline,
                                       Class<? extends RecyclerView.Adapter> tClass) {
        if (tClass == TweetTimelineRecyclerViewAdapter.class)
            return new TweetTimelineRecyclerViewAdapter(context, timeline);

        return null;
    }

}
