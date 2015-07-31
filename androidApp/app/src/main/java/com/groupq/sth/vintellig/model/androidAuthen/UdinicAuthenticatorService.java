package com.groupq.sth.vintellig.model.androidAuthen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by sth on 6/17/15.
 */
public class UdinicAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        VgAuthenticator authenticator = new VgAuthenticator(this);
        return authenticator.getIBinder();
    }
}