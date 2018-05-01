package com.smileberry.jamchat.application;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.smileberry.jamchat.db.DBUtils;
import com.smileberry.jamchat.db.DataBaseHelper;
import com.smileberry.jamchat.utils.Preferences;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.gcm.AeroGearGCMPushConfiguration;
import org.jboss.aerogear.android.unifiedpush.gcm.AeroGearGCMPushRegistrar;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.UUID;

public class CustomApplicationContext extends Application {

    private static final String TAG = CustomApplicationContext.class.getName();

    public final String VARIANT_ID = "";
    public final String SECRET = "";
    public static final String GCM_SENDER_ID = "";
    public static final String UNIFIED_SERVER_URL = "";

    public static final int DELETE_DELAY = 1000 * 60 * 3;

    private PushRegistrar registration;

    private DataBaseHelper databaseHelper = null;

    private android.os.Handler deleteMessageHandler = new android.os.Handler();

    private TelephonyManager tm;

    private String deviceToken;

    @Override
    public void onCreate() {
        super.onCreate();

        tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        try {
            final AeroGearGCMPushConfiguration config = new AeroGearGCMPushConfiguration()
                    .addSenderId(GCM_SENDER_ID)
                    .setVariantID(VARIANT_ID)
                    .setSecret(SECRET)
                    .setPushServerURI(new URI(UNIFIED_SERVER_URL));
            registration = config.asRegistrar();
            registration.register(this, new Callback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setDeviceToken(config.getDeviceToken());
                    Log.i(TAG, "Device token is " + deviceToken);
                    Log.i(TAG, "Registration id is " + ((AeroGearGCMPushRegistrar) registration).getRegistrationId(CustomApplicationContext.this));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Unable to register " + e.toString());
                }
            });

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Runnable deleteThread = new Runnable() {
            @Override
            public void run() {
                try {
                    DBUtils.DeleteObsoleteMessagesTask deleteObsoleteMessagesTask = new DBUtils.DeleteObsoleteMessagesTask(getDataBaseHelper().getMessageDAO());
                    deleteObsoleteMessagesTask.execute();
                } catch (SQLException e) {
                    Log.e(TAG, "Can't delete messages inside ApplicationContext");
                }
                deleteMessageHandler.postDelayed(this, DELETE_DELAY);
            }
        };
        deleteMessageHandler.postDelayed(deleteThread, DELETE_DELAY);
    }

    public String getDeviceId() {
        String deviceIdString = Preferences.getSavedDeviceId(this);
        if (deviceIdString.trim().length() == 0) {
            final String macAddr, androidId;

            WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();

            macAddr = wifiInf.getMacAddress();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());

            Preferences.saveDeviceId(this, deviceUuid.toString());
        }
        return deviceIdString;
    }

    public PushRegistrar getRegistration() {
        return registration;
    }

    public DataBaseHelper getDataBaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(CustomApplicationContext.this, DataBaseHelper.class);
        }
        return databaseHelper;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
