package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import java.util.Calendar;

public class DeleteGpsDataOldUtils {
    private final static String TAG = DeleteGpsDataOldUtils.class.getSimpleName();

    public static void deleteGpsDataOld(Context context){
        long beginTimeInMills = System.currentTimeMillis();

        try{
            MyApplication myApp = (MyApplication)context.getApplicationContext();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -30);
            myApp.gpsDataSDDao.deleteOld(cal.getTimeInMillis());
            myApp.appLogSDDao.save("定时删除历史GPS数据，"+ DateUtils.parseDateToString(cal.getTime(), DateUtils.C_YYYY_MM_DD_HH_MM_SS));

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "deleteGpsDataOld 异常 ", e);

        } finally {
            Log.d(TAG, "deleteGpsDataOld 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }

    }
}
