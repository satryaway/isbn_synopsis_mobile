package com.samstudio.isbnsynopsis;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.samstudio.isbnsynopsis.utils.CommonConstants;

/**
 * Created by satryaway on 10/17/2015.
 * Singleton for application
 */
public class ISBNSynopsisApplication extends Application {
    private static ISBNSynopsisApplication instance;
    private SharedPreferences preferences;

    public synchronized static ISBNSynopsisApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferences = getSharedPreferences(CommonConstants.ISBN_APP, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences(){
        return preferences;
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(CommonConstants.IS_LOGGED_IN, false);
    }
}
