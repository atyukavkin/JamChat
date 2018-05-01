package com.smileberry.jamchat.utils;

import android.os.Bundle;
import android.util.Log;

import com.smileberry.jamchat.MainActivity;
import com.smileberry.jamchat.model.Constants;
import com.smileberry.jamchat.model.Message;

public class MessageUtils {

    private static final String TAG = MessageUtils.class.getName();

    private MessageUtils() {
    }


    public static Message createMessageFromBundle(Bundle bundle) {
        Message message = new Message();
        message.setMessage(bundle.getString(MainActivity.ALERT));
        message.setLat(bundle.getString(Constants.LAT_FIELD));
        message.setLng(bundle.getString(Constants.LNG_FIELD));
        try {
            message.setTime(Long.parseLong(bundle.getString(Constants.TIME_FIELD)));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Cannot parse timestamp");
        }
        message.setGcmId(bundle.getString(Constants.GCM_ID_FIELD));
        message.setDeviceToken(bundle.getString(Constants.DEVICE_TOKEN));
        return message;
    }
}
