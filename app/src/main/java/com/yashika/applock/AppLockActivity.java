package com.yashika.applock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yashika.applock.service.AlarmReceiver;
import com.yashika.applock.service.AppLockService;
import com.yashika.applock.utils.Permissions;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is the main activity of AppLock application
 *
 * @author Yashika
 */
public class AppLockActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Permissions permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.bind(this);
        setUpToolBar();
        setUpServices();
        permissions = new Permissions(this);
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        //hide action bar.
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void setUpServices() {
        startService(new Intent(AppLockActivity.this, AppLockService.class));
        try {
            Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 999, alarmIntent, 0);
            int interval = (86400 * 1000) / 4;
            if(manager != null) {
                manager.cancel(pendingIntent);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            }
        } catch(Exception ignored) {
            // silent catch
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // We need to enter pin every time activity hides from user.
        switchToFragment(new SetPinFragment());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(!permissions.checkPermissionForUsage()) {
//                    permissions.requestPermissionForUsageStats();
//                }
//            }
//        }).start();
    }

    /**
     * Does not add fragment to backStack and sends in a null bundle.
     *
     * @param targetFragment A fragment to be opened.
     */
    public void switchToFragment(Fragment targetFragment) {
        switchToFragment(targetFragment, false);
    }

    /**
     * Adds fragment to backStack based on the addToBackStack flag and sends null bundle.
     *
     * @param targetFragment A fragment to be opened.
     * @param addToBackStack A boolean that is used to say if the fragment is to be added to the
     *                       BackStack.
     */
    public void switchToFragment(Fragment targetFragment, boolean addToBackStack) {
        switchToFragment(targetFragment, addToBackStack, null);
    }

    /**
     * Adds fragment to backStack based on the addToBackStack flag and sends the bundle as an
     * argument.
     *
     * @param targetFragment A fragment to be opened.
     * @param addToBackStack A boolean that is used to say if the fragment is to be added to the
     *                       backStack.
     * @param bundle         A bundle that holds the information to be passed to the fragment.
     */
    public void switchToFragment(Fragment targetFragment, boolean addToBackStack, Bundle bundle) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(bundle != null) {
            targetFragment.setArguments(bundle);
        } else {
            if(targetFragment.getArguments() != null) {
                targetFragment.getArguments().clear();
            }
            targetFragment.setArguments(null);
        }
        fragmentTransaction.replace(R.id.fragment_container, targetFragment);

        if(addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }
}
