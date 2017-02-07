package com.yashika.applock;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yashika.applock.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This fragment displays all the applications installed on phone and provide feature to lock the
 * selected apps
 *
 * @author yashika.
 */
public class AllAppsFragment extends AppLockFragment {

    @BindView(R.id.apps_recycler_view)
    RecyclerView appsRecyclerView;
    private List<ApplicationInfo> allAppsList;
    private AllAppsRecyclerAdapter allAppsRecyclerAdapter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case Constants.BOOT_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // do nothing

                } else {

                    // permission denied
                    // request again
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allAppsList = new ArrayList<>();
        allAppsRecyclerAdapter = new AllAppsRecyclerAdapter(getAppLockActivity());
        getAllInstalledApps();
    }

    private void getAllInstalledApps() {
        if(getAppLockActivity() != null) {
            PackageManager packageManager = getAppLockActivity().getPackageManager();
            allAppsList.clear();
            allAppsList.addAll(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_installed_apps, container, false);
        ButterKnife.bind(this, view);
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        appsRecyclerView.setLayoutManager(new LinearLayoutManager(getAppLockActivity()));
        appsRecyclerView.setAdapter(allAppsRecyclerAdapter);
        allAppsRecyclerAdapter.setAppsList(allAppsList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getAppLockActivity() != null && getAppLockActivity().getSupportActionBar() != null) {
            getAppLockActivity().getSupportActionBar().show();
            getAppLockActivity().getSupportActionBar().setTitle(R.string.all_apps);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // TODO: 06-02-2017 add search and refresh option
    }
}
