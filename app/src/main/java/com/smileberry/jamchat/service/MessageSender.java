package com.smileberry.jamchat.service;

import android.os.AsyncTask;

import com.smileberry.jamchat.application.CustomApplicationContext;
import com.smileberry.jamchat.model.Constants;
import com.smileberry.jamchat.model.Message;

import org.jboss.aerogear.unifiedpush.DefaultPushSender;
import org.jboss.aerogear.unifiedpush.PushSender;
import org.jboss.aerogear.unifiedpush.message.MessageResponseCallback;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;

public class MessageSender extends AsyncTask<String, Void, String> {

    private static final String TAG = MessageSender.class.getName();

    private static final int ONE_DAY = 60 * 60 * 24;
    private static final int SECOND_DAY = 60 * 60 * 24;
    public static final String APPLICATION_ID = "";
    public static final String MASTER_SECRET = "";
    private Message message;
    private Handler handler;

    public MessageSender(Message message, Handler handler) {
        this.message = message;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(String... params) {
        String msg = "";

        final PushSender sender =
                DefaultPushSender.withRootServerURL(CustomApplicationContext.UNIFIED_SERVER_URL)
                        .pushApplicationId(APPLICATION_ID)
                        .masterSecret(MASTER_SECRET)
                        .build();

        final UnifiedMessage unifiedMessage = UnifiedMessage.
                withMessage()
                .alert(message.getMessage())
                .userData(Constants.LAT_FIELD, message.getLat())
                .userData(Constants.LNG_FIELD, message.getLng())
                .userData(Constants.GCM_ID_FIELD, message.getGcmId())
                .userData(Constants.TIME_FIELD, String.valueOf(message.getTime()))
                .userData(Constants.DEVICE_TOKEN, message.getDeviceToken())
                .build();


        sender.send(unifiedMessage, new MessageResponseCallback() {
            @Override
            public void onComplete() {
                handler.taskFinished();
            }
        });
        return msg;
    }

    @Override
    protected void onPostExecute(String s) {

    }
}
