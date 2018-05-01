package com.smileberry.jamchat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public final static String SHOW_TRAFFIC_KEY = "show_traffic";
    public final static String HIDE_ME_RANGE_KEY = "hide_me_range";
    public final static String SHARED_PREF = "SETTINGS";
    public static final String DEVICE_ID = "device_id";

    private Preferences() {
    }

    public static boolean isShowTraffic(Context context) {
        SharedPreferences sharedSettings = context.getSharedPreferences(SHARED_PREF, Activity.MODE_PRIVATE);
        return sharedSettings.getBoolean(SHOW_TRAFFIC_KEY, false);
    }

    public static void setShowTraffic(Context context, boolean isShowTraffic) {
        SharedPreferences sharedSettings = context.getSharedPreferences(Preferences.SHARED_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putBoolean(SHOW_TRAFFIC_KEY, isShowTraffic);
        editor.commit();
    }

    public static String getHideMeRange(Context context) {
        SharedPreferences sharedSettings = context.getSharedPreferences(SHARED_PREF, Activity.MODE_PRIVATE);
        return sharedSettings.getString(HIDE_ME_RANGE_KEY, String.valueOf(HideMeRange.HIDE_50));

    }

    public static void setHideMeRange(Context context, HideMeRange hideMeRange) {
        SharedPreferences sharedSettings = context.getSharedPreferences(Preferences.SHARED_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putString(HIDE_ME_RANGE_KEY, String.valueOf(hideMeRange));
        editor.commit();
    }

    public enum HideMeRange {
        DONT_HIDE {
            @Override
            public double getRadius() {
                return 0;
            }
        }, HIDE_50 {
            @Override
            public double getRadius() {
                return 0.05;
            }
        }, HIDE_100 {
            @Override
            public double getRadius() {
                return 0.1;
            }
        }, HIDE_500 {
            @Override
            public double getRadius() {
                return 0.5;
            }
        }, HIDE_1000 {
            @Override
            public double getRadius() {
                return 1;
            }
        }, HIDE_TRAVEL_WORLD {
            @Override
            public double getRadius() {
                return 0;
            }
        };

        abstract public double getRadius();
    }

    public static void saveDeviceId(Context context, String deviceId) {
        if (deviceId == null) {
            return;
        }
        SharedPreferences sharedSettings = context.getSharedPreferences(Preferences.SHARED_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putString(DEVICE_ID, deviceId);
        editor.commit();
    }

    public static String getSavedDeviceId(Context context) {
        SharedPreferences sharedSettings = context.getSharedPreferences(Preferences.SHARED_PREF, Activity.MODE_PRIVATE);
        return sharedSettings.getString(DEVICE_ID, "");

    }

}
