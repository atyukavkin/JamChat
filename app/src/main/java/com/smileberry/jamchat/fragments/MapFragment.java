package com.smileberry.jamchat.fragments;


import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smileberry.jamchat.MainActivity;
import com.smileberry.jamchat.R;
import com.smileberry.jamchat.application.CustomApplicationContext;
import com.smileberry.jamchat.ballon.ui.BalloonFactory;
import com.smileberry.jamchat.model.Message;
import com.smileberry.jamchat.service.MessageLoader;
import com.smileberry.jamchat.service.ReceivedMessageHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static com.smileberry.jamchat.utils.MapUtils.moveCameraOnMapToLatLng;
import static com.smileberry.jamchat.utils.Preferences.isShowTraffic;

public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Message>> {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private BalloonFactory balloonFactory;
    private HashMap<String, Message> messageMarkers = new HashMap<>();
    private HashMap<String, Marker> markers = new HashMap<>();

    public static final int ONE_MINUTE = 1000 * 60;
    private static final String TAG = MainActivity.class.getName();

    private ReceivedMessageHandler mapReceiveMessageHandler;

    public MapFragment() {
        mapReceiveMessageHandler = new ReceivedMessageHandler() {
            @Override
            public void taskFinished(List<Message> messages, String error) {
                for (Message message : messages) {
                    if (!messageMarkers.containsValue(message)) {
                        processMessage(message, getBalloonFactory(), mMap);
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        setUpMapIfNeeded(savedInstanceState);
        moveCameraOnMapToLatLng(mMap, getCurrentLocation(), getActivity().getApplicationContext());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        moveCameraOnMapToLatLng(mMap, getCurrentLocation(), getActivity().getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void processMessage(Message message, BalloonFactory balloonFactory, GoogleMap mMap) {
        addIcon(balloonFactory, message, mMap);
    }

    private void addIcon(BalloonFactory iconFactory, Message message, GoogleMap mMap) {
        if (message == null || message.getLat() == null || message.getLng() == null) {
            return;
        }
        LatLng position = new LatLng(Double.parseDouble(message.getLat()), Double.parseDouble(message.getLng()));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
                .snippet(message.getMessage());

        if (message.getReadStatus()) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_message_read));
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_message));
        }

        Marker marker = mMap.addMarker(markerOptions);
        message.setMarkerId(marker.getId());
        messageMarkers.put(marker.getId(), message);
        markers.put(marker.getId(), marker);
    }

    @Override
    public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
        Loader<List<Message>> messageLoader = null;
        CustomApplicationContext customApplicationContext = (CustomApplicationContext) getActivity().getApplicationContext();
        try {
            messageLoader = new MessageLoader(customApplicationContext, customApplicationContext.getDataBaseHelper().getMessageDAO(), getCurrentLocation());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Message>> loader, List<Message> messages) {
        for (Message message : messages) {
            processMessage(message, getBalloonFactory(), mMap);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Message>> loader) {

    }

    public void updateMap(Message message) {
        if (message.isTheSameDevice(((CustomApplicationContext) getActivity().getApplicationContext()).getDeviceId())) {
            moveCameraOnMapToLatLng(mMap, new LatLng(Double.parseDouble(message.getLat()), Double.parseDouble(message.getLng())), getActivity().getApplicationContext());
        }

        processMessage(message, getBalloonFactory(), mMap);
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded(Bundle savedInstanceState) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) (getChildFragmentManager().findFragmentById(R.id.map))).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MapsInitializer.initialize(getActivity());
                    if (mMap != null) {
                        setUpMap();
                    }
                }
            });
        }
        mMap.setTrafficEnabled(isShowTraffic(getActivity()));
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        getBalloonFactory().setColor(Color.CYAN);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_message_read));

                final Message message = messageMarkers.get(marker.getId());
                final CustomApplicationContext customApplicationContext = (CustomApplicationContext) getActivity().getApplicationContext();

                if (!message.getReadStatus()) {
                    message.setReadStatus(true);
                    Runnable updateReadStatus = new Runnable() {
                        public void run() {
                            try {
                                customApplicationContext.getDataBaseHelper().getMessageDAO().update(message);
                            } catch (SQLException e) {
                                Log.e(TAG, "Cannot update read status");
                            }
                        }
                    };
                    updateReadStatus.run();
                }

                View v = getActivity().getLayoutInflater().inflate(R.layout.marker, null);
                TextView info = (TextView) v.findViewById(R.id.info);
                info.setText(message.getMessage());
                TextView timeAddedView = (TextView) v.findViewById(R.id.time_added);
                long last = System.currentTimeMillis() - message.getTime();
                String now = "added just now";
                if (last > ONE_MINUTE) {
                    now = "added " + String.valueOf((int) ((int) last / 1000.0) / 60) + " minutes ago";
                }
                timeAddedView.setText(now);
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker pointMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });

        ImageButton myLocation = (ImageButton) getActivity().findViewById(R.id.myLocationButton);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = mMap.getMyLocation();
                if (location != null) {
                    LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition position = mMap.getCameraPosition();
                    CameraPosition.Builder builder = new CameraPosition.Builder();
                    builder.zoom(15);
                    builder.target(target);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                }
            }
        });
    }

    public LatLng getCurrentLocation() {
        return ((MainActivity) getActivity()).getCurrentLocation();
    }

    public GoogleMap getMapFromFragment() {
        return mMap;
    }

    public HashMap<String, Marker> getMarkersFromFragment() {
        return markers;
    }

    public ReceivedMessageHandler getMapReceiveMessageHandler() {
        return mapReceiveMessageHandler;
    }

    public BalloonFactory getBalloonFactory() {
        if (balloonFactory == null) {
            balloonFactory = new BalloonFactory(getActivity());
        }
        return balloonFactory;
    }
}
