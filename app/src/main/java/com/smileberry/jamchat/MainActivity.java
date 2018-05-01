package com.smileberry.jamchat;

import android.content.Context;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.smileberry.jamchat.application.CustomApplicationContext;
import com.smileberry.jamchat.db.DBUtils;
import com.smileberry.jamchat.db.DataBaseHelper;
import com.smileberry.jamchat.fragments.ChatListFragment;
import com.smileberry.jamchat.fragments.MapFragment;
import com.smileberry.jamchat.model.Message;
import com.smileberry.jamchat.service.Handler;
import com.smileberry.jamchat.service.MessageSender;
import com.smileberry.jamchat.service.ReceivedMessageHandler;
import com.smileberry.jamchat.ui.SlidingTabLayout;
import com.smileberry.jamchat.utils.CoordinateUtils;
import com.smileberry.jamchat.utils.MessageUtils;
import com.smileberry.jamchat.utils.Preferences;

import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;

import java.sql.SQLException;
import java.util.HashMap;

import static com.smileberry.jamchat.utils.CoordinateUtils.getRandomLatitude;
import static com.smileberry.jamchat.utils.CoordinateUtils.getRandomLatitudeForRadius;
import static com.smileberry.jamchat.utils.CoordinateUtils.getRandomLongitude;
import static com.smileberry.jamchat.utils.CoordinateUtils.getRandomLongitudeForRadius;
import static com.smileberry.jamchat.utils.MapUtils.moveCameraOnMapToLatLng;
import static com.smileberry.jamchat.utils.Preferences.getHideMeRange;

public class MainActivity extends BaseDrawerActivity implements MessageHandler, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();
    public static final String ALERT = "alert";

    private static final String ON_MAP = "Map";
    private static final String LIST = "List";
    private static final int ONE_MINUTE = 1000 * 60;
    private static final int TWO_MINUTES_IN_MILLISECONDS = 120000;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Location currentLocation;
    private GoogleApiClient googleApiClient;
    private DataBaseHelper databaseHelper = null;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private MapFragment mapFragment;
    private ChatListFragment chatListFragment;

    private LocationRequest locationRequest;
    private LocationListener locationListener;

    private CustomApplicationContext customApplicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_main_layout);
        super.onCreateDrawer();

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        buildGoogleApiClient();
        googleApiClient.connect();
        createLocationRequest();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mapFragment = new MapFragment();
        chatListFragment = new ChatListFragment();

        final EditText editText = (EditText) findViewById(R.id.editText);
        final RelativeLayout sendBtnL = (RelativeLayout) findViewById(R.id.sendBtnL);
        final ImageButton sendBtn = (ImageButton) findViewById(R.id.sendBtn);

        editText.getBackground().setColorFilter(getResources().getColor(R.color.primaryColorDark), PorterDuff.Mode.SRC_ATOP);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    sendBtn.setBackgroundResource(R.drawable.ic_send_primary_36dp);
                } else {
                    sendBtn.setBackgroundResource(R.drawable.ic_send_grey600_36dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        customApplicationContext = (CustomApplicationContext) getApplicationContext();

        View.OnClickListener sendOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText() != null && editText.getText().toString().trim().length() > 0) {
                    final Message message = new Message();
                    generateAndSetCoordinatesToMessage(message, customApplicationContext, getCurrentLocation());
                    message.setMessage(editText.getText().toString());
                    String deviceId = customApplicationContext.getDeviceId();
                    message.setGcmId(deviceId != null ? deviceId : "lorem");
                    message.setTime(System.currentTimeMillis());

                    if (customApplicationContext.getDeviceToken() != null) {
                        message.setDeviceToken(customApplicationContext.getDeviceToken());
                    }

                    MessageSender sender = new MessageSender(message, new Handler() {
                        @Override
                        public void taskFinished() {
/*
                            try {
                                DBUtils.AddMessageTask addMessageTask = new DBUtils.AddMessageTask(customApplicationContext.getDataBaseHelper().getMessageDAO(), message);
                                addMessageTask.execute();
                            } catch (SQLException e) {
                                Log.e(TAG, "Can not get DB");
                            }
*/
                        }
                    });
                    sender.execute();
                    editText.setText("");
                }
            }
        };
        sendBtnL.setOnClickListener(sendOnClickListener);
        sendBtn.setOnClickListener(sendOnClickListener);


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));

        // Give the SlidingTabLayout the ViewPager, this must be
        // done AFTER the ViewPager has had it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);


        final ImageButton switchViewButton = (ImageButton) findViewById(R.id.switchViewButton);
        final ImageButton myLocation = (ImageButton) findViewById(R.id.myLocationButton);
        myLocation.setVisibility(View.VISIBLE);
        switchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currItem = mViewPager.getCurrentItem();
                if (currItem == 0) {
                    mViewPager.setCurrentItem(1);
                    switchViewButton.setBackground(getResources().getDrawable(R.drawable.switch_to_map_button));
                    myLocation.setVisibility(View.GONE);
                } else {
                    mViewPager.setCurrentItem(0);
                    myLocation.setVisibility(View.VISIBLE);
                    switchViewButton.setBackground(getResources().getDrawable(R.drawable.switch_to_list_button));
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            ReceivedMessageHandler mapHandler = mapFragment.getMapReceiveMessageHandler();
            ReceivedMessageHandler chatHandler = chatListFragment.getChatReceiveMessageHandler();
            DBUtils.FindMessagesForChatListTask findMessagesTask = new DBUtils.FindMessagesForChatListTask(customApplicationContext.getDataBaseHelper().getMessageDAO(), mapHandler, chatHandler, getCurrentLocation());
            findMessagesTask.execute();
        } catch (SQLException e) {
            Log.e(TAG, "Cannot load messages");
        }


//        buildGoogleApiClient();
//        googleApiClient.connect();
//        if (!googleApiClient.isConnected()) {
//            googleApiClient.connect();
//        }
//        createLocationRequest();
    }

    @Override
    public void onStop() {
        CoordinateUtils.saveLastKnownLocation(MainActivity.this, currentLocation);
        super.onStop();
        googleApiClient.disconnect();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
/*
        customApplicationContext.getRegistration().unregister(customApplicationContext, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Devise was unregistered OK");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Unable to unregister " + e.toString());
            }
        });
*/
    }


    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
        RegistrarManager.registerMainThreadHandler(this);
    }

    @Override
    public void onPause() {
        CoordinateUtils.saveLastKnownLocation(MainActivity.this, currentLocation);
        super.onPause();
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        RegistrarManager.unregisterMainThreadHandler(this);
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {

    }

    @Override
    public void onMessage(Context context, Bundle bundle) {
        Message message = MessageUtils.createMessageFromBundle(bundle);
//        if (!message.isTheSameDevice(((CustomApplicationContext) getApplicationContext()).getDeviceId())) {
        try {
            DBUtils.AddMessageTask addMessageTask = new DBUtils.AddMessageTask(customApplicationContext.getDataBaseHelper().getMessageDAO(), message);
            addMessageTask.execute();
        } catch (SQLException e) {
            Log.e(TAG, "Cannot save message");
        }
//        }

        Log.i(TAG, "Device token in message is " + message.getDeviceToken());

        mapFragment.updateMap(message);
        chatListFragment.updateChatList(message);

        AsyncTask<Void, Void, Void> play = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                ringtone.play();
                return null;
            }
        };
        play.execute();

    }

    @Override
    public void onError() {
    }


    @Override
    public void onConnected(Bundle bundle) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (getMap() != null) {
            moveCameraOnMapToLatLng(getMap(), getCurrentLocation(), getApplicationContext());
        }
        startLocationUpdates();
    }

    public void startLocationUpdates() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                if (getMap() != null) {
                    moveCameraOnMapToLatLng(getMap(), getCurrentLocation(), getApplicationContext());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (getGoogleApiClient().isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(getGoogleApiClient(), locationRequest, (com.google.android.gms.location.LocationListener) locationListener);
        }
    }

    private void stopLocationUpdates() {
        if (getGoogleApiClient().isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(getGoogleApiClient(), (LocationCallback) locationListener);
        }
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(TWO_MINUTES_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public LatLng getCurrentLocation() {
        if (currentLocation == null) {
            return CoordinateUtils.getLastKnownLocation(MainActivity.this);
        }
        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        HashMap<Integer, Fragment> cachedFragmentHashMap = new HashMap<Integer, Fragment>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            switch (position) {
                case 0:
                    title = ON_MAP;
                    break;
                case 1:
                    title = LIST;
                    break;
                default:
                    title = ON_MAP;
                    break;
            }

            return title;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = mapFragment;
                    break;
                case 1:
                    fragment = chatListFragment;
                    break;
                default:
                    fragment = mapFragment;
                    break;
            }
            cachedFragmentHashMap.put(position, fragment);
            return fragment;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            cachedFragmentHashMap.remove(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
*/

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
*/
/*
        switch (item.getItemId()) {
            case R.id.action_my_location:
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(CoordinateUtils.getCurrentLatLng(currentLocation), 18);
                mMap.animateCamera(cameraUpdate);
                return true;
            case R.id.action_settings :
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
*//*

        return super.onOptionsItemSelected(item);
    }
*/

    public GoogleMap getMap() {
        return mapFragment.getMapFromFragment();
    }

    public HashMap<String, Marker> getMarkers() {
        return mapFragment.getMarkersFromFragment();
    }

    public void updateViewWhenRedirectingFromChat(Message message) {
        //Switch view to map and prepare buttons
        mViewPager.setCurrentItem(0);
        final ImageButton switchViewButton = (ImageButton) findViewById(R.id.switchViewButton);
        final ImageButton myLocation = (ImageButton) findViewById(R.id.myLocationButton);
        myLocation.setVisibility(View.VISIBLE);
        switchViewButton.setBackground(getResources().getDrawable(R.drawable.switch_to_list_button));
        //move camera to the map
        moveCameraOnMapToLatLng(getMap(), new LatLng(Double.parseDouble(message.getLat()), Double.parseDouble(message.getLng())), getApplicationContext());
        //open info marker content
        Marker marker = getMarkers().get(message.getMarkerId());
        marker.showInfoWindow();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public CustomApplicationContext getCustomContext() {
        return customApplicationContext;
    }

    private static void generateAndSetCoordinatesToMessage(Message message, Context context, LatLng currentLocation) {
        String selectedHideMeRangeRadio = getHideMeRange(context);
        double currentLatitude = currentLocation.latitude;
        double currentLongitude = currentLocation.longitude;

        double latitude;
        double longitude;
        if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.DONT_HIDE))) {
            latitude = currentLatitude;
            longitude = currentLongitude;
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_100))) {
            latitude = getRandomLatitudeForRadius(currentLatitude, Preferences.HideMeRange.HIDE_100.getRadius());
            longitude = getRandomLongitudeForRadius(currentLatitude, currentLongitude, Preferences.HideMeRange.HIDE_100.getRadius());
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_500))) {
            latitude = getRandomLatitudeForRadius(currentLatitude, Preferences.HideMeRange.HIDE_500.getRadius());
            longitude = getRandomLongitudeForRadius(currentLatitude, currentLongitude, Preferences.HideMeRange.HIDE_500.getRadius());
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_1000))) {
            latitude = getRandomLatitudeForRadius(currentLatitude, Preferences.HideMeRange.HIDE_1000.getRadius());
            longitude = getRandomLongitudeForRadius(currentLatitude, currentLongitude, Preferences.HideMeRange.HIDE_1000.getRadius());
        } else if (selectedHideMeRangeRadio.equals(String.valueOf(Preferences.HideMeRange.HIDE_TRAVEL_WORLD))) {
            latitude = getRandomLatitude();
            longitude = getRandomLongitude();
        } else {
            //Default value is 50m
            latitude = getRandomLatitudeForRadius(currentLatitude, Preferences.HideMeRange.HIDE_50.getRadius());
            longitude = getRandomLongitudeForRadius(currentLatitude, currentLongitude, Preferences.HideMeRange.HIDE_50.getRadius());
        }
        message.setLat(Double.toString(latitude));
        message.setLng(Double.toString(longitude));
    }

}
