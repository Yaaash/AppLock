package com.yashika.applock.service;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yashika.applock.R;
import com.yashika.applock.utils.SessionPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppLockService extends Service {

    public static String currentApp = "";
    public static String previousApp = "";
    ImageView imageView;
    private List<String> lockedAppList;
    private WindowManager windowManager;
    private Dialog dialog;
    private Context context;
    private Timer timer;
    private TimerTask showUITask = new TimerTask() {
        @Override
        public void run() {
            lockedAppList = SessionPreferences.INSTANCE.getLockedApps();

            if(isAppRunningInForeground()) {
                if(imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            if(!currentApp.matches(previousApp)) {
                                showDialog();
                                previousApp = currentApp;
                            }
                        }
                    });
                }
            } else {
                if(imageView != null) {
                    imageView.post(new Runnable() {
                        public void run() {
                            hideUnlockDialog();
                        }
                    });
                }
            }
        }
    };

    public AppLockService() {
        lockedAppList = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        lockedAppList.addAll(SessionPreferences.INSTANCE.getLockedApps());
        setUpWindowManager();
        setUpTimer();
    }

    private void setUpWindowManager() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageView = new ImageView(this);
        imageView.setVisibility(View.GONE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = ((getApplicationContext().getResources().getDisplayMetrics().widthPixels) / 2);
        params.y = ((getApplicationContext().getResources().getDisplayMetrics().heightPixels) / 2);
        windowManager.addView(imageView, params);

    }

    private void setUpTimer() {
        timer = new Timer(AppLockService.class.getSimpleName());
        timer.schedule(showUITask, 1000L, 1000L);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer = null;
        if(imageView != null) {
            windowManager.removeView(imageView);
        }
        try {
            if(dialog != null) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch(Exception ignored) {
            // silent catch
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isAppRunningInForeground() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        if(manager != null && manager.getRunningAppProcesses() != null
                && manager.getRunningAppProcesses().get(0) != null) {
            String packageName = manager.getRunningAppProcesses().get(0).processName;

            Log.e("Package Name: ", packageName);
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
            if(stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<>();
                for(UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if(runningTask.isEmpty()) {
                    // empty
                    packageName = "";
                } else {
                    // not empty
                    packageName = runningTask.get(runningTask.lastKey()).getPackageName();
                    Log.e("New Package Name: ", packageName);
                }
            }

            for(String lockedPackageName : lockedAppList) {
                if(!TextUtils.isEmpty(lockedPackageName) && !TextUtils.isEmpty(packageName) &&
                        lockedPackageName.equals(packageName)) {
                    currentApp = lockedPackageName;
                    return true;
                }
            }
        }
        return false;
    }

    void showDialog() {
        if(context == null) {
            context = getApplicationContext();
        }

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptsView = layoutInflater.inflate(R.layout.fragment_set_pin, null);
        ViewHolder viewHolder = new ViewHolder(promptsView, context);
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(dialog.getWindow() != null) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setContentView(promptsView);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
        viewHolder.statusTextView.setText("");
        viewHolder.statusSubtitleTextView.setText(context.getString(R.string.enter_pin));

        if(!TextUtils.isEmpty(currentApp)) {
            try {
                Drawable icon = context.getPackageManager().getApplicationIcon(currentApp);
                viewHolder.appIconImageView.setImageDrawable(icon);
                viewHolder.appIconImageView.setVisibility(View.VISIBLE);
            } catch(PackageManager.NameNotFoundException ignored) {
                // silent catch
            }
        }
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
                return true;
            }
        });
        dialog.show();
    }

    public void hideUnlockDialog() {
        previousApp = "";
        try {
            if(dialog != null) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    class ViewHolder {
        private static final int PIN_LENGTH = 6;
        @BindView(R.id.app_icon_image_view)
        ImageView appIconImageView;
        @BindView(R.id.status_text_view)
        TextView statusTextView;
        @BindView(R.id.status_subtitle_text_view)
        TextView statusSubtitleTextView;
        @BindView(R.id.pin_edit_text)
        EditText pinEditText;
        private StringBuilder inputPin;
        private Context context;

        ViewHolder(View view, Context context) {
            ButterKnife.bind(this, view);
            inputPin = new StringBuilder();
            pinEditText.addTextChangedListener(new PinTextWatcher());
        }

        @OnClick({ R.id.delete_image_view, R.id.one_button, R.id.two_button,
                R.id.three_button, R.id.four_button, R.id.five_button, R.id.six_button,
                R.id.seven_button, R.id.eight_button, R.id.nine_button, R.id.zero_button })
        public void onClick(View view) {
            if(context != null) {
                switch(view.getId()) {
                    case R.id.delete_image_view:
                        deletePin();
                        break;
                    case R.id.one_button:
                        appendText(context.getString(R.string.one_text));
                        break;
                    case R.id.two_button:
                        appendText(context.getString(R.string.two_text));
                        break;
                    case R.id.three_button:
                        appendText(context.getString(R.string.three_text));
                        break;
                    case R.id.four_button:
                        appendText(context.getString(R.string.four_text));
                        break;
                    case R.id.five_button:
                        appendText(context.getString(R.string.five_text));
                        break;
                    case R.id.six_button:
                        appendText(context.getString(R.string.six_text));
                        break;
                    case R.id.seven_button:
                        appendText(context.getString(R.string.seven_text));
                        break;
                    case R.id.eight_button:
                        appendText(context.getString(R.string.eight_text));
                        break;
                    case R.id.nine_button:
                        appendText(context.getString(R.string.nine_text));
                        break;
                    case R.id.zero_button:
                        appendText(context.getString(R.string.zero_text));
                        break;
                }
            }
        }

        private void appendText(String input) {
            if(inputPin.length() < PIN_LENGTH) {
                inputPin.append(input);
                pinEditText.setText(inputPin);
                pinEditText.setSelection(pinEditText.getText().length());
            }
        }

        private void deletePin() {
            if(!TextUtils.isEmpty(inputPin)) {
                inputPin.deleteCharAt(inputPin.length() - 1);
                String pinEditTextString = pinEditText.getText().toString();
                if(!TextUtils.isEmpty(pinEditTextString)) {
                    pinEditTextString = pinEditTextString.substring(0, pinEditTextString.length() - 1);
                    pinEditText.setText(pinEditTextString);
                }
            }
            pinEditText.setSelection(pinEditText.getText().length());
        }

        class PinTextWatcher implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() == PIN_LENGTH) {
                    if(context != null && !TextUtils.isEmpty(pinEditText.getText().toString()) &&
                            !TextUtils.isEmpty(SessionPreferences.INSTANCE.getPin()) &&
                            SessionPreferences.INSTANCE.getPin().equals(pinEditText.getText().toString())) {
                        hideUnlockDialog();
                    } else {
                        statusSubtitleTextView.setText(R.string.pin_mismatch);
                        pinEditText.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }
    }
}
