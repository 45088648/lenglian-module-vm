package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.constant.Constant;
import java.util.Calendar;

public class DeleteReadDataOldUtils {
    private final static String TAG = DeleteReadDataOldUtils.class.getSimpleName();

    public static void deleteReadDataOld(Context context){
        long runTime = System.currentTimeMillis();

        try{
            MyApplication myApp = (MyApplication) context.getApplicationContext();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -30);
            long sensorDataTimeEndInMills = cal.getTimeInMillis();
            myApp.readDataSDDao.deleteOld(sensorDataTimeEndInMills);
            myApp.appLogSDDao.save("定时删除历史温湿度数据，"+ Constant.sdf.format(cal.getTime()));

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "deleteReadDataOld 异常", e);

        } finally {
            Log.d(TAG, "deleteReadDataOld 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }

}
