package com.beetech.module.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.beetech.module.application.MyApplication;

public class ToastHandler extends Handler {
    private MyApplication myApp;

    public ToastHandler(Context context){
        super(Looper.getMainLooper());
        myApp = (MyApplication)context.getApplicationContext();
    }

    @Override
    public void handleMessage(Message msg) {
        Object toastMsg = msg.obj;
        if(toastMsg != null){
            Toast.makeText(myApp.getApplicationContext(), toastMsg.toString(), Toast.LENGTH_SHORT).show();
        }
        super.handleMessage(msg);
    }
}
