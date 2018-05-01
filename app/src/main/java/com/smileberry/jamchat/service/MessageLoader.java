package com.smileberry.jamchat.service;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.smileberry.jamchat.model.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageLoader extends AsyncTaskLoader<List<Message>> {

    private List<Message> messages;
    private Dao<Message, Integer> dao;
    private LatLng latLng;

    private MessageObserver messageObserver;

    public MessageLoader(Context context) {
        super(context);
    }

    public MessageLoader(Context context, Dao<Message, Integer> dao, LatLng latLng) {
        super(context);
        this.dao = dao;
        this.latLng = latLng;
    }

    @Override
    public List<Message> loadInBackground() {
        List<Message> results = new ArrayList<>();
        String latitude = String.valueOf(latLng.latitude);
        String longitude = String.valueOf(latLng.longitude);
        String[] args = {latitude, latitude, longitude, longitude};
        try {
            if (dao != null) {
                String query = "select m.id, m.gcm_id, m.message, m.lat, m.lng, m.time, m.readStatus, ((? - m.lat)*(? - m.lat) + (? - m.lng)*(? - m.lng)) as dist from message m order by dist desc limit 100";
                GenericRawResults<String[]> rawResults =
                        dao.queryRaw(
                                query, new RawRowMapper<String[]>() {

                                    @Override
                                    public String[] mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                                        return resultColumns;
                                    }
                                }, args);

                for (String[] resRow : rawResults.getResults()) {
                    int id = Integer.parseInt(resRow[0]);
                    String gcmId = resRow[1];
                    String message = resRow[2];
                    String lat = resRow[3];
                    String lng = resRow[4];
                    long time = Long.parseLong(resRow[5]);
                    boolean read = "1".equals(resRow[6]);
                    results.add(new Message(id, gcmId, message, lat, lng, time, read));
                }
            }
        } catch (Exception e) {
            Log.e("utils", "Can't save results", e);
        }
        return results;
    }


    @Override
    public void deliverResult(List<Message> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<Message> oldData = messages;
        messages = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (messages != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(messages);
        }

        // Register the observers that will notify the Loader when changes are made.
        if (messageObserver == null) {
            messageObserver = new MessageObserver(this);
        }

        if (takeContentChanged() || messages == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // The Loader is being reset, so we should stop monitoring for changes.
        if (messageObserver != null) {
            getContext().unregisterReceiver(messageObserver);
            messageObserver = null;
        }

        // At this point we can release the resources associated with 'messages'.
        if (messages != null) {
            releaseResources(messages);
            messages = null;
        }

    }

    @Override
    public void onCanceled(List<Message> data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(List<Message> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }
}
