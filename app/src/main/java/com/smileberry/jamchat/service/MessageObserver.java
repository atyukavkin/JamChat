package com.smileberry.jamchat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.jboss.aerogear.android.unifiedpush.MessageHandler;

public class MessageObserver extends BroadcastReceiver implements MessageHandler {

    public static final String MESSAGE_ADDED = "MESSAGE_ADDED";
    private MessageLoader messageLoader;

    public MessageObserver(MessageLoader messageLoader) {
        this.messageLoader = messageLoader;
        IntentFilter filter = new IntentFilter(MESSAGE_ADDED);
        this.messageLoader.getContext().registerReceiver(this, filter);
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {

    }

    @Override
    public void onMessage(Context context, Bundle bundle) {
        this.onReceive(context, new Intent().putExtras(bundle));

    }

    @Override
    public void onError() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        messageLoader.onContentChanged();
    }
}
