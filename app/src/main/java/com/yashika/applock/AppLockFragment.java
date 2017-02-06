package com.yashika.applock;

import android.support.v4.app.Fragment;

/**
 * This is the base fragment that all fragmentMap will extend.
 *
 * @author yashika.
 */

public class AppLockFragment extends Fragment {

    /**
     * This is the function that returns the AppLockActivity.
     *
     * @return An object of AppLockActivity.
     */
    public AppLockActivity getAppLockActivity() {
        return (AppLockActivity) getActivity();
    }
}
