package com.yashika.applock.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Use this class to store all non-secure information about the user.
 *
 * @author yashika.
 */
public enum SessionPreferences {

    INSTANCE;
    private Context ctx;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    @SuppressLint("CommitPrefEdits")
    public void init(final Context ctx) {
        this.ctx = ctx;
        this.sharedPreferences = ctx.getSharedPreferences(Preferences.class.getName(), Context.MODE_PRIVATE);
        this.sharedPreferencesEditor = this.sharedPreferences.edit();
    }

    /**
     * Get user pin information
     */
    public String getPin() {
        return sharedPreferences.getString(Constants.PIN, Constants.INVALID_PIN);
    }

    /**
     * Save user pin
     *
     * @param pin String object
     */
    public void setPin(String pin) {
        sharedPreferencesEditor.putString(Constants.PIN, pin);
        sharedPreferencesEditor.apply();
    }

    /**
     * This method adds a new locked app in List of saved locked apps
     *
     * @param appName Name of Application to be saved, String
     */
    public void addLockedApp(String appName) {
        List<String> appsList = getLockedApps();
        appsList.add(appName);
        saveLockedApps(appsList);
    }

    /**
     * This method saves all the locked apps in Shared preferences
     *
     * @param lockedAppsList List Object
     */
    public void saveLockedApps(List<String> lockedAppsList) {
        Gson gson = new Gson();
        String jsonLockedApp = gson.toJson(lockedAppsList);
        sharedPreferencesEditor.putString(Constants.SAVE_LOCKED_APPS, jsonLockedApp);
        sharedPreferencesEditor.apply();
    }

    /**
     * This method gets all the Locked apps saved in shared preferences
     *
     * @return ArrayList of Locked Apps
     */
    public List<String> getLockedApps() {
        List<String> appsList = new ArrayList<>();
        if(sharedPreferences.contains(Constants.SAVE_LOCKED_APPS)) {
            String jsonLocked = sharedPreferences.getString(Constants.SAVE_LOCKED_APPS, null);
            Gson gson = new Gson();
            String[] lockedItems = gson.fromJson(jsonLocked, String[].class);
            appsList.addAll(Arrays.asList(lockedItems));
        }
        return appsList;
    }

    /**
     * This method removes an app from all the saved Locked apps
     *
     * @param appName Name of Application to be removed, String
     */
    public void removeApp(String appName) {
        List<String> appsList = getLockedApps();
        if(!appsList.isEmpty() && appsList.contains(appName)) {
            appsList.remove(appName);
            saveLockedApps(appsList);
        }
    }
}
