package com.yashika.applock;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.yashika.applock.utils.Permissions;
import com.yashika.applock.utils.SessionPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * this class is used to set view for All applications
 *
 * @author yashika.
 */

class AllAppsRecyclerAdapter extends RecyclerView.Adapter<AllAppsRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<ApplicationInfo> appsList;
    private LayoutInflater inflater;
    private List<String> savedAppsList;
    private Permissions permissions;

    AllAppsRecyclerAdapter(Context context) {
        this.context = context;
        appsList = new ArrayList<>();
        permissions = new Permissions((Activity) context);
        savedAppsList = SessionPreferences.INSTANCE.getLockedApps();
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.apps_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(!appsList.isEmpty() && appsList.get(position) != null) {
            final ApplicationInfo applicationInfo = appsList.get(position);

            if(!TextUtils.isEmpty(applicationInfo.packageName)) {
                try {
                    Drawable icon = context.getPackageManager().getApplicationIcon(applicationInfo.packageName);
                    holder.appIconImageView.setImageDrawable(icon);
                } catch(PackageManager.NameNotFoundException ignored) {
                    // silent catch
                }
            }

            if(!TextUtils.isEmpty(context.getPackageManager().getApplicationLabel(applicationInfo))) {
                holder.appNameTextView.setText(context.getPackageManager().getApplicationLabel(applicationInfo));
            }

            if(!savedAppsList.isEmpty() && !TextUtils.isEmpty(applicationInfo.packageName)
                    && savedAppsList.contains(applicationInfo.packageName)) {
                holder.appSwitch.setChecked(true);
            } else {
                holder.appSwitch.setChecked(false);
            }

            holder.appSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!permissions.checkPermissionForBoot()) {
                        permissions.requestPermissionForBoot();
                    } else {
                        if(b) {
                            // checked
                            SessionPreferences.INSTANCE.addLockedApp(applicationInfo.packageName);
                        } else {
                            // unchecked
                            SessionPreferences.INSTANCE.removeApp(applicationInfo.packageName);
                        }
                    }

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return appsList.size();
    }

    /**
     * This method sets all apps data in Adapter
     *
     * @param appsList List of Applications Information
     */
    void setAppsList(List<ApplicationInfo> appsList) {
        this.appsList.clear();
        this.appsList.addAll(appsList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.app_icon_image_view)
        ImageView appIconImageView;
        @BindView(R.id.app_name_text_view)
        TextView appNameTextView;
        @BindView(R.id.app_switch)
        Switch appSwitch;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
