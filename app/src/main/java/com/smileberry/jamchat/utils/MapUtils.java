package com.smileberry.jamchat.utils;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import static com.smileberry.jamchat.utils.CoordinateUtils.getLatLngBoundsForRadius;
import static com.smileberry.jamchat.utils.Preferences.getHideMeRange;

public class MapUtils {


    private MapUtils() {
    }


    public static CameraUpdate prepareCameraUpdateForMoving(LatLng latLng, Context context) {
        final double makeRadiusWider = 0.1;
        String selectedHideMeRangeRadio = getHideMeRange(context);
        double radius = 0.05;

        if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_100))) {
            radius = 0.1;
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_500))) {
            radius = 0.5;
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_1000))) {
            radius = 1;
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_TRAVEL_WORLD))) {
            radius = 2000;
        }

        return CameraUpdateFactory.newLatLngBounds(getLatLngBoundsForRadius(latLng.latitude, latLng.longitude, radius + makeRadiusWider), 0);
    }

    public static void moveCameraOnMapToLatLng(final GoogleMap mMap, LatLng latLng, Context context) {
        if (mMap == null) {
            return;
        }
        final CameraUpdate cameraUpdate = MapUtils.prepareCameraUpdateForMoving(latLng, context);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(cameraUpdate);
            }
        });
    }
}
