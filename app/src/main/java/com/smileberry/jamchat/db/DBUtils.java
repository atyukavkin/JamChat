package com.smileberry.jamchat.db;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.smileberry.jamchat.model.Constants;
import com.smileberry.jamchat.model.Message;
import com.smileberry.jamchat.service.ReceivedMessageHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    public static final int LIFE_TIME_ONE_HOUR = 1000 * 60 * 60;
    public static final int LIFE_TIME_ONE_DAY = 86400000;
    public static final int NUMBER_OF_DISPLAYED_MESSAGES = 100;
    public static final String READ = "1";

    private DBUtils() {
    }

    public static class AddMessageTask extends AsyncTask<String, String, Integer> {

        private Dao<Message, Integer> dao;
        private Message message;

        public AddMessageTask(Dao<Message, Integer> dao, Message message) {
            this.dao = dao;
            this.message = message;
        }

        public Integer doInBackground(String... parameters) {
            try {
                if (dao != null) {
                    dao.create(message);
                }
            } catch (Exception e) {
                Log.e("utils", "Can't save message", e);
            }
            return 0;
        }

        public void onPostExecute(Integer success) {
        }
    }

    public static class FindMessagesTask extends AsyncTask<String, String, List<Message>> {

        private Dao<Message, Integer> dao;
        private ReceivedMessageHandler mapHandler;
        private ReceivedMessageHandler chatHandler;

        public FindMessagesTask(Dao<Message, Integer> dao, ReceivedMessageHandler mapHandler, ReceivedMessageHandler chatHandler) {
            this.dao = dao;
            this.mapHandler = mapHandler;
            this.chatHandler = chatHandler;
        }

        public List<Message> doInBackground(String... parameters) {
            List<Message> results = new ArrayList<>();
            try {
                if (dao != null) {
                    results = dao.queryForAll();
                }
            } catch (Exception e) {
                Log.e("utils", "Can't save results", e);
            }
            return results;
        }

        public void onPostExecute(List<Message> results) {
            if (dao != null) {
                mapHandler.taskFinished(results, "");
                chatHandler.taskFinished(results, "");
            }
        }
    }

    public static class FindMessagesForChatListTask extends AsyncTask<String, String, List<Message>> {

        private Dao<Message, Integer> dao;
        private ReceivedMessageHandler mapHandler;
        private ReceivedMessageHandler chatHandler;
        private LatLng latLng;

        public FindMessagesForChatListTask(Dao<Message, Integer> dao, ReceivedMessageHandler mapHandler, ReceivedMessageHandler chatHandler, LatLng latLng) {
            this.dao = dao;
            this.mapHandler = mapHandler;
            this.chatHandler = chatHandler;
            this.latLng = latLng;
        }

        public List<Message> doInBackground(String... parameters) {
            List<Message> results = new ArrayList<>();
            String latitude = String.valueOf(latLng.latitude);
            String longitude = String.valueOf(latLng.longitude);
            String[] args = {latitude, latitude, longitude, longitude};
            try {
                if (dao != null) {
                    String query = "select m.id, m.gcm_id, m.message, m.lat, m.lng, m.time, m.readStatus, ((? - m.lat)*(? - m.lat) + (? - m.lng)*(? - m.lng))" +
                            " as dist from message m order by dist desc limit " + String.valueOf(NUMBER_OF_DISPLAYED_MESSAGES);
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
                        boolean read = READ.equals(resRow[6]);
                        results.add(new Message(id, gcmId, message, lat, lng, time, read));
                    }
                }
            } catch (Exception e) {
                Log.e("utils", "Can't save results", e);
            }
            return results;
        }

        public void onPostExecute(List<Message> results) {
            if (dao != null) {
                mapHandler.taskFinished(results, "");
                chatHandler.taskFinished(results, "");
            }
        }
    }

    public static class DeleteObsoleteMessagesTask extends AsyncTask<String, String, Void> {

        private Dao<Message, Integer> dao;

        public DeleteObsoleteMessagesTask(Dao<Message, Integer> dao) {
            this.dao = dao;
        }

        public Void doInBackground(String... parameters) {
            try {
                if (dao != null) {
                    DeleteBuilder<Message, Integer> deleteBuilder = dao.deleteBuilder();
                    deleteBuilder.where().ge(Constants.TIME_FIELD, System.currentTimeMillis() - LIFE_TIME_ONE_DAY);
                    deleteBuilder.delete();
                }
            } catch (Exception e) {
                Log.e("utils", "Can't delete messages", e);
            }
            return null;
        }

        public void onPostExecute(Void v) {
        }
    }
}
