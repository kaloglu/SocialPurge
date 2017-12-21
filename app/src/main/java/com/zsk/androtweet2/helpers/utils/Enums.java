package com.zsk.androtweet2.helpers.utils;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.zsk.androtweet2.helpers.utils.Enums.DrawItemTypes.ADD_TWITTER_ACCOUNT;
import static com.zsk.androtweet2.helpers.utils.Enums.DrawItemTypes.LOGOUT;
import static com.zsk.androtweet2.helpers.utils.Enums.DrawItemTypes.MANAGE_ACCOUNTS;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentArguments.CONTENT_TYPE;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentArguments.FRAGMENT_TYPE;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentArguments.ITEM_TYPE;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.FAVORITE;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.MENTION;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.RETWEET;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentContentTypes.TWEET;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentItemTypes.ITEM;
import static com.zsk.androtweet2.helpers.utils.Enums.FragmentItemTypes.LIST;
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

    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({LIST, ITEM})
    @Target(ElementType.PARAMETER)
    public @interface FragmentItemTypes {
        long ITEM = 0;
        long LIST = 50;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TWEET, MENTION, RETWEET, FAVORITE})
    @Target(ElementType.PARAMETER)
    public @interface FragmentContentTypes {
        long TWEET = 0;
        long MENTION = 1;
        long RETWEET = 2;
        long FAVORITE = 3;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TWITTER, FACEBOOK, INSTAGRAM})
    @Target(ElementType.PARAMETER)
    public @interface FragmentTypes {
        long TWITTER = 0;
        long FACEBOOK = 1;
        long INSTAGRAM = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({FRAGMENT_TYPE, CONTENT_TYPE, ITEM_TYPE})
    @Target(ElementType.PARAMETER)
    public @interface FragmentArguments {
        String FRAGMENT_TYPE = "fragment_type";
        String CONTENT_TYPE = "content_type";
        String ITEM_TYPE = "item_type";
    }

}
