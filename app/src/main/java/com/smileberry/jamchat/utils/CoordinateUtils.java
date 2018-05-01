package com.smileberry.jamchat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Random;

public class CoordinateUtils {

    public static final String LAST_KNOWN_LAT = "LAT";
    public static final String LAST_KNOWN_LNG = "LNG";
    public static final double DEFAUL_LATITUDE = 55.960070;
    public static final double DEFAULT_LONGITUDE = 37.888244;

    public static double getRandomLatitudeForRadius(double currentLatitude, double radius) {
        double deltaLat = radius / 111.1;
        double minLat = currentLatitude - deltaLat;
        double maxLat = currentLatitude + deltaLat;

        return minLat + (maxLat - minLat) * new Random().nextDouble();
    }

    public static double getRandomLongitudeForRadius(double currentLatitude, double currentLongitude, double radius) {
        double kmInLongitudeDegree = 111.320 * Math.cos(currentLatitude / 180.0 * Math.PI);
        double deltaLong = radius / kmInLongitudeDegree;
        double minLong = currentLongitude - deltaLong;
        double maxLong = currentLongitude + deltaLong;

        return minLong + (maxLong - minLong) * new Random().nextDouble();
    }

    public static double getRandomLatitude() {
        double minLat = 0;
        double maxLat = 90;

        return minLat + (maxLat - minLat) * new Random().nextDouble();
    }

    public static double getRandomLongitude() {
        double minLong = -180;
        double maxLong = 180;

        return minLong + (maxLong - minLong) * new Random().nextDouble();
    }

    public static LatLngBounds getLatLngBoundsForRadius(double currentLatitude, double currentLongitude, double radius) {
        double deltaLat = radius / 111.1;
        double minLat = currentLatitude - deltaLat;
        double maxLat = currentLatitude + deltaLat;
        double kmInLongitudeDegree = 111.320 * Math.cos(currentLatitude / 180.0 * Math.PI);
        double deltaLong = radius / kmInLongitudeDegree;
        double minLong = currentLongitude - deltaLong;
        double maxLong = currentLongitude + deltaLong;

        return new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong));
    }

    /**
     * Gets LatLng for current Location if it's available, otherwise returns some hardcoded value
     * <p/>
     * @return LatLng for current location
     */
    public static LatLng getCurrentLatLng(Location currentLocation) {
        if (currentLocation != null) {
            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }

        return new LatLng(DEFAUL_LATITUDE, DEFAULT_LONGITUDE);
    }

    public static void saveLastKnownLocation(Context context, Location lastLocation) {
        if (lastLocation == null) {
            return;
        }
        SharedPreferences sharedSettings = context.getSharedPreferences(Preferences.SHARED_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putFloat(LAST_KNOWN_LAT, (float) lastLocation.getLatitude());
        editor.putFloat(LAST_KNOWN_LNG, (float) lastLocation.getLongitude());
        editor.commit();
    }

    public static LatLng getLastKnownLocation(Context context) {
        SharedPreferences sharedSettings = context.getSharedPreferences(Preferences.SHARED_PREF, Activity.MODE_PRIVATE);
        Double latitude = (double) sharedSettings.getFloat(LAST_KNOWN_LAT, (float) DEFAUL_LATITUDE);
        Double longitude = (double) sharedSettings.getFloat(LAST_KNOWN_LNG, (float) DEFAULT_LONGITUDE);
        return new LatLng(latitude, longitude);

    }
}
