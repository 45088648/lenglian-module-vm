package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.ReadDataRealtimeSDDao;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class SoundLedAlarmUtils {
    private final static String TAG = CheckSessionUtils.class.getSimpleName();

    public static void checkAlarm(Context context){
        long beginTimeInMills = System.currentTimeMillis();

        try {
            final MyApplication myApp = (MyApplication) context.getApplicationContext();
            ReadDataRealtimeSDDao readDataRealtimeSDDao = new ReadDataRealtimeSDDao(myApp);
            List<ReadDataRealtime> readDataRealtimeList = readDataRealtimeSDDao.queryAll();
            List<ReadDataRealtime> readDataRealtimeListAlarm = new ArrayList<>();

            if (readDataRealtimeList != null) {
                for (ReadDataRealtime readDataRealtime : readDataRealtimeList) {
                    double temp = readDataRealtime.getTemp();
                    double tempLower = readDataRealtime.getTempLower();
                    double tempHight = readDataRealtime.getTempHight();
                    if (tempLower != 0 && tempHight != 0 && (temp > tempHight || temp < tempLower)) {
                        readDataRealtimeListAlarm.add(readDataRealtime);
                    }
                }
            }

            int alarmSize = readDataRealtimeListAlarm.size();
            Log.d(TAG, "alarmSize=" + alarmSize);
            if (alarmSize > 0 && Constant.alarmLightFlag) {
                try {
                    myApp.powerLED.on();
                    Log.d(TAG, "LED on");
                    myApp.mMediaPlayer.start();

                    //延迟3秒关闭
                    myApp.timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                myApp.powerLED.off();
                                Log.d(TAG, "LED off");
                                myApp.mMediaPlayer.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "LED 关灯异常", e);
                                myApp.mMediaPlayer.stop();
                            }
                        }
                    }, 3 * 1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "LED 开灯异常", e);
                }

            } else {
                try {
                    myApp.powerLED.off();
                    Log.d(TAG, "LED off");
                    if (myApp.mMediaPlayer.isPlaying()) {
                        myApp.mMediaPlayer.stop();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "LED 关灯异常", e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkAlarm 异常 ", e);

        } finally {
            Log.d(TAG, "checkAlarm 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }
    }
}
