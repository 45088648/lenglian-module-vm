package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.client.ClientConnectManager;
import org.apache.mina.core.session.IoSession;

public class CheckSessionUtils {
    private final static String TAG = CheckSessionUtils.class.getSimpleName();

    public static void checkSession(Context context){
        long beginTimeInMills = System.currentTimeMillis();
        try{
            MyApplication myApp = (MyApplication)context.getApplicationContext();
            IoSession session = myApp.session;
            if (session == null || !session.isConnected()) {
                ClientConnectManager.getInstance(myApp).connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkSession 异常 ", e);

        } finally {
            Log.d(TAG, "checkSession 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }
    }
}
