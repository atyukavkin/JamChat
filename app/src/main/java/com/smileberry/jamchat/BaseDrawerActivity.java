package com.smileberry.jamchat;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smileberry.jamchat.adapters.DrawerAdapter;
import com.smileberry.jamchat.ui.ScrimInsetsFrameLayout;
import com.smileberry.jamchat.utils.UIUtils;

import java.util.Arrays;

public class BaseDrawerActivity extends ActionBarActivity implements ScrimInsetsFrameLayout.OnInsetsCallback {

    private final static String[] titles = {"Settings", "About"};
    private ActionBarDrawerToggle toggle;

    private ListView navigationDrawer;
    private DrawerLayout drawerLayout;
    private ScrimInsetsFrameLayout scrimInsetsFrameLayout;

    protected void onCreateDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);

        navigationDrawer = (ListView) findViewById(R.id.left_drawer);
        navigationDrawer.setAdapter(new DrawerAdapter(this, Arrays.asList(titles)));
        navigationDrawer.setOnItemClickListener(new DrawerItemClickListener());

        scrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);
        scrimInsetsFrameLayout.setOnInsetsCallback(this);
        //Added as a solution to the issue when onClickListener didn't work
        scrimInsetsFrameLayout.bringToFront();
        scrimInsetsFrameLayout.requestLayout();


        UIUtils.setStatusBarColor(this, findViewById(R.id.statusBarBackground));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Intent intent = new Intent(this, DrawerItemActivity.class);
        intent.putExtra("title", titles[position]);
        startActivity(intent);

        // update selected item and title, then close the drawer
        navigationDrawer.setItemChecked(position, true);
        setTitle(titles[position]);
        drawerLayout.closeDrawer(scrimInsetsFrameLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onInsetsChanged(Rect insets) {
    }

}
