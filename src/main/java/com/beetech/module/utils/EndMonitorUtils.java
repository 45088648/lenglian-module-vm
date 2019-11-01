package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;

import java.util.Date;

public class EndMonitorUtils {
    private final static String TAG = EndMonitorUtils.class.getSimpleName();

    public static void endMonitor(Context context){
        long beginTimeInMills = System.currentTimeMillis();
        final MyApplication myApp = (MyApplication)context.getApplicationContext();

        try{
            myApp.endMonitorTime = new Date();
            myApp.monitorState = 0;
            QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
            if(queryConfigRealtime != null){
                queryConfigRealtime.setMonitorState(myApp.monitorState);
                queryConfigRealtime.setEndMonitorTime(myApp.endMonitorTime);
                myApp.queryConfigRealtimeSDDao.update(queryConfigRealtime);
            }

            myApp.locationService.stop();
            myApp.appLogSDDao.save("结束监控, "+ DateUtils.parseDateToString(myApp.endMonitorTime, DateUtils.C_YYYY_MM_DD_HH_MM_SS));

            //发送SHUTDOWN报文
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "ShutdownRequestUtils.requestShutdown");
                    try {
                        ShutdownRequestUtils.requestShutdown(myApp);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "ShutdownRequestUtils.requestShutdown 异常", e);
                    }
                }
            }).start();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "beginMonitor 异常", e);

        } finally {
            Log.d(TAG, "beginMonitor 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }
    }
}
