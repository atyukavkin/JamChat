package com.smileberry.jamchat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.smileberry.jamchat.fragments.AboutFragment;
import com.smileberry.jamchat.fragments.SettingsFragment;
import com.smileberry.jamchat.utils.Preferences;
import com.smileberry.jamchat.utils.UIUtils;

public class DrawerItemActivity extends ActionBarActivity {

    public static final String SETTINGS_TITLE = "Settings";
    public static final String ABOUT_TITLE = "About";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_main);

        UIUtils.setStatusBarColor(this, findViewById(R.id.statusBarBackground));

        Toolbar toolbar = (Toolbar) findViewById(R.id.drawerItemToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Fragment fragment;
        if (SETTINGS_TITLE.equals(intent.getStringExtra("title"))) {
            fragment = new SettingsFragment();
            setTitle(SETTINGS_TITLE);
        } else {
            fragment = new AboutFragment();
            setTitle(ABOUT_TITLE);
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerForFragments, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DrawerItemActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onShowTrafficCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        Preferences.setShowTraffic(this, checked);
    }

    public void onHideMeRadioClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioDontHide:
                if (checked) {
                    Preferences.setHideMeRange(this, Preferences.HideMeRange.DONT_HIDE);
                }
                break;
            case R.id.radioHide50:
                if (checked) {
                    Preferences.setHideMeRange(this, Preferences.HideMeRange.HIDE_50);
                }
                break;
            case R.id.radioHide100:
                if (checked) {
                    Preferences.setHideMeRange(this, Preferences.HideMeRange.HIDE_100);
                }
                break;
            case R.id.radioHide500:
                if (checked) {
                    Preferences.setHideMeRange(this, Preferences.HideMeRange.HIDE_500);
                }
                break;
            case R.id.radioHide1000:
                if (checked) {
                    Preferences.setHideMeRange(this, Preferences.HideMeRange.HIDE_1000);
                }
                break;
            case R.id.radioHideTravelTheWorld:
                if (checked) {
                    Preferences.setHideMeRange(this, Preferences.HideMeRange.HIDE_TRAVEL_WORLD);
                }
                break;
        }

    }
}
