package com.smileberry.jamchat.service;

import android.os.AsyncTask;
import android.util.Log;

import com.smileberry.jamchat.application.CustomApplicationContext;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;

public class Registrar extends AsyncTask<String, Void, String> {

    private CustomApplicationContext context;

    public Registrar(CustomApplicationContext context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        PushRegistrar push = context.getRegistration();
        push.register(context, new Callback<Void>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSuccess(Void ignore) {
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("MainActivity", exception.getMessage(), exception);
            }
        });

        return null;
    }
}
