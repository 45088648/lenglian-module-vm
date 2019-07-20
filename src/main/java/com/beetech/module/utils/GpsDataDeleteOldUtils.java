package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import java.util.Calendar;

public class GpsDataDeleteOldUtils {
    private final static String TAG = GpsDataDeleteOldUtils.class.getSimpleName();

    public static void deleteOld(Context context){
        long beginTimeInMills = System.currentTimeMillis();

        try{
            MyApplication myApp = (MyApplication)context.getApplicationContext();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -90);
            myApp.gpsDataSDDao.deleteOld(cal.getTimeInMillis());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "deleteOld 异常 ", e);

        } finally {
            Log.d(TAG, "deleteOld 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }

    }
}
