package com.beetech.module.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.beetech.module.receiver.ConnectReceiver;

public class GuardService extends Service {
    private final static String TAG = GuardService.class.getSimpleName();

    public GuardService() {
    }
 
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
 
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
 
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        return START_STICKY;
    }
 
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        Intent intent = new Intent(ConnectReceiver.ACTION);
        sendBroadcast(intent);
        Log.d(TAG, "sendBroadcast[" + ConnectReceiver.ACTION + "]");
    }

 
}
