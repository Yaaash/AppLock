package com.yashika.applock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //hide action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        switchToFragment(new SetPinFragment());
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
