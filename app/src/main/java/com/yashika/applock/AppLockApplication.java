package com.yashika.applock;

import android.app.Application;

import com.yashika.applock.utils.SessionPreferences;

/**
 * This is the class that acts as the application for the whole app.
 *
 * @author yashika.
 */

public class AppLockApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SessionPreferences.INSTANCE.init(this);
    }
}
