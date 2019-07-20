package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;

public class LocationServiceUtils {
    private final static String TAG = LocationServiceUtils.class.getSimpleName();

    public static void checkLocationService(Context context){
        long beginTimeInMills = System.currentTimeMillis();

        try{
            MyApplication myApp = (MyApplication)context.getApplicationContext();

            if (!myApp.locationService.isStart() && myApp.monitorState == 1) {
                myApp.locationService.getClient().requestLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "checkLocationService 异常 ", e);

        } finally {
            Log.d(TAG, "checkLocationService 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }
    }
}
