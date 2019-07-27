package com.beetech.module.utils;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.beetech.module.application.MyApplication;

public class SetSysTimeUtils {
    private final static String TAG = SetSysTimeUtils.class.getSimpleName();

    public static void setSysTime(Context context){
        long runTime = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication)context.getApplicationContext();
            long ntpTimeInMills = NtpUtils.getTimeInMills();
            if(ntpTimeInMills < 1559318400000L){
                Log.e(TAG, "ntpTimeInMills < 2019-6-1, ntpTimeInMills="+ntpTimeInMills);
                return;
            }
            boolean setCurrentTimeMillisRet = SystemClock.setCurrentTimeMillis(ntpTimeInMills);
            Log.d(TAG, "ntpTimeInMills = "+ntpTimeInMills);
            Log.d(TAG, "setCurrentTimeMillisRet = " + setCurrentTimeMillisRet);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setSysTime 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "setSysTime 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }
}
