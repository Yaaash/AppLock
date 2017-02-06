package com.yashika.applock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

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
     * @param pin
     */
    public void setPin(String pin) {
        sharedPreferencesEditor.putString(Constants.PIN, pin);
        sharedPreferencesEditor.apply();
    }
}
