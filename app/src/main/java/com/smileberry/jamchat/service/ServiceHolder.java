package com.smileberry.jamchat.service;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.smileberry.jamchat.db.DataBaseHelper;

public class ServiceHolder extends Fragment {

    private static final String TAG = ServiceHolder.class.getName();

    private DataBaseHelper databaseHelper = null;

    public ServiceHolder() {
    }

    public static ServiceHolder findOrCreateServiceHolder(FragmentManager fm) {
        // Check to see if we have retained the worker fragment.
        ServiceHolder mRetainFragment = (ServiceHolder) fm.findFragmentByTag(TAG);

        // If not retained (or first time running), we need to create and add it.
        if (mRetainFragment == null) {
            mRetainFragment = new ServiceHolder();
            fm.beginTransaction().add(mRetainFragment, TAG).commit();
        }

        return mRetainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure this Fragment is retained over a configuration change
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
