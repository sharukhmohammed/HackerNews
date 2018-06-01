package com.getomnify.hackernews;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Sharukh Mohammed on 29/05/18 at 12:55.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());

    }
}
