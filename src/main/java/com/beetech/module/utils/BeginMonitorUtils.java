package com.beetech.module.utils;

import android.content.Context;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;

import java.util.Date;

public class BeginMonitorUtils {
    private final static String TAG = BeginMonitorUtils.class.getSimpleName();

    public static void beginMonitor(Context context){
        long beginTimeInMills = System.currentTimeMillis();
        final MyApplication myApp = (MyApplication)context.getApplicationContext();

        try{
            //设置数据开始时间
            Message msg = new Message();
            msg.what = 9;
            myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);

            myApp.beginMonitorTime = new Date();
            myApp.endMonitorTime = null;
            myApp.monitorState = 1;
            QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
            if(queryConfigRealtime != null){
                queryConfigRealtime.setMonitorState(myApp.monitorState);
                queryConfigRealtime.setBeginMonitorTime(myApp.beginMonitorTime);
                queryConfigRealtime.setEndMonitorTime(myApp.endMonitorTime);
                myApp.queryConfigRealtimeSDDao.update(queryConfigRealtime);
            }
            if(!myApp.locationService.isStart()){
                myApp.locationService.start();
            }
            myApp.appLogSDDao.save("开始监控"+DateUtils.parseDateToString(myApp.beginMonitorTime, DateUtils.C_YYYY_MM_DD_HH_MM_SS));

            //发送SYS报文
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "SysRequestUtils.requestSys");
                    try {
                        SysRequestUtils.requestSys(myApp, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "SysRequestUtils.requestSys 异常", e);
                    }
                }
            }).start();

            SystemClock.sleep(200);
            //发送SYS报文
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "NodeParamUtils.requestNodeParam");
                    try {
                        NodeParamUtils.requestNodeParam(myApp);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "NodeParamUtils.requestNodeParam 异常", e);
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

    public static void beginMonitorByBoot(Context context){
        long beginTimeInMills = System.currentTimeMillis();
        final MyApplication myApp = (MyApplication)context.getApplicationContext();

        try{
            myApp.monitorState = 1;
            QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
            if(queryConfigRealtime != null){
                Date beginMonitorTime = queryConfigRealtime.getBeginMonitorTime();
                if(beginMonitorTime == null){
                    myApp.beginMonitorTime = new Date();
                    queryConfigRealtime.setBeginMonitorTime(myApp.beginMonitorTime);
                } else {
                    myApp.beginMonitorTime = beginMonitorTime;
                }
                queryConfigRealtime.setMonitorState(myApp.monitorState);
                myApp.queryConfigRealtimeSDDao.update(queryConfigRealtime);
            }
            if(!myApp.locationService.isStart()){
                myApp.locationService.start();
            }

            //发送SYS报文
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "SysRequestUtils.requestSys");
                    try {
                        SysRequestUtils.requestSys(myApp, 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "SysRequestUtils.requestSys 异常", e);
                    }
                }
            }).start();

            //发送SYS报文
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "NodeParamUtils.requestNodeParam");
                    try {
                        NodeParamUtils.requestNodeParam(myApp);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "NodeParamUtils.requestNodeParam 异常", e);
                    }
                }
            }).start();

            myApp.appLogSDDao.save("开机开始监控"+DateUtils.parseDateToString(myApp.beginMonitorTime, DateUtils.C_YYYY_MM_DD_HH_MM_SS));
            Toast.makeText(context, "开机开始监控", Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "beginMonitorByBoot 异常", e);

        } finally {
            Log.d(TAG, "beginMonitorByBoot 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }
    }


}
