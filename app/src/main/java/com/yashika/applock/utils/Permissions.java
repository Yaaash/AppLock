package com.yashika.applock.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * @author yashika.
 */

public class Permissions {

    private static final String BOOT_PERMISSION = "Boot permission is needed. Please allow access in App Settings.";
    private static final String USAGE_PERMISSION = "System permission is needed. Please allow access in App Settings.";
    private Activity activity;

    public Permissions(Activity activity) {
        this.activity = activity;
    }

    /**
     * This method checks if permission is needed
     *
     * @return boolean
     */
    public boolean checkPermissionForBoot() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * This method checks if permission is needed
     *
     * @return boolean
     */
    public boolean checkPermissionForUsage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.PACKAGE_USAGE_STATS);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    /**
     * This method requests the permission we need
     */
    public void requestPermissionForBoot() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
            AppLockUtils.showMessageAlertDialog(BOOT_PERMISSION, activity);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.RECEIVE_BOOT_COMPLETED },
                                              Constants.BOOT_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * This method requests the permission we need
     */
    public void requestPermissionForUsageStats() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.PACKAGE_USAGE_STATS)) {
            AppLockUtils.showMessageAlertDialog(USAGE_PERMISSION, activity);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.PACKAGE_USAGE_STATS },
                                              Constants.USAGE_PERMISSION_REQUEST_CODE);
        }
    }
}
