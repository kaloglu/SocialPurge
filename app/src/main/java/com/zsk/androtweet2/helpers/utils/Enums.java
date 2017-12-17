package com.zsk.androtweet2.helpers.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.zsk.androtweet2.helpers.utils.Enums.DrawItemTypes.ADD_TWITTER_ACCOUNT;
import static com.zsk.androtweet2.helpers.utils.Enums.DrawItemTypes.LOGOUT;
import static com.zsk.androtweet2.helpers.utils.Enums.DrawItemTypes.MANAGE_ACCOUNTS;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.FAVORITES;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.MENTIONS;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.RETWEETS;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes.FACEBOOK;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes.INSTAGRAM;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentTypes.TWITTER;
import static com.zsk.androtweet2.helpers.utils.Enums.RequestCodes.RC_SIGN_IN;

/**
 * Created by kaloglu on 17.12.2017.
 */

public class Enums {
    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({RC_SIGN_IN})
    @Target(ElementType.PARAMETER)
    public @interface RequestCodes {
        int RC_SIGN_IN = 100;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOGOUT, ADD_TWITTER_ACCOUNT, MANAGE_ACCOUNTS})
    @Target(ElementType.PARAMETER)
    public @interface DrawItemTypes {
        long LOGOUT = 1;
        long ADD_TWITTER_ACCOUNT = 2;
        long MANAGE_ACCOUNTS = 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MENTIONS, RETWEETS, FAVORITES})
    @Target(ElementType.PARAMETER)
    public @interface FragmentContentTypes {
        long MENTIONS = 1;
        long RETWEETS = 2;
        long FAVORITES = 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TWITTER, FACEBOOK, INSTAGRAM})
    @Target(ElementType.PARAMETER)
    public @interface FragmentTypes {
        long TWITTER = 0;
        long FACEBOOK = 1;
        long INSTAGRAM = 2;
    }


}
