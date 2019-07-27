package com.beetech.module.utils;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.constant.Constant;

import java.util.Date;

public class ModuleInitUtils {
    private final static String TAG = ModuleInitUtils.class.getSimpleName();

    public static void moduleInit(Context context){
        long runTime = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication) context.getApplicationContext();

            Log.d(TAG, "========== myApp.manualStopModuleFlag = "+myApp.manualStopModuleFlag);
            //手动停止模块不重新启动
            if(myApp.manualStopModuleFlag == 0) {
                long currentTimeMillis = System.currentTimeMillis();
                long responseTimeInterval = currentTimeMillis - myApp.moduleReceiveDataTime; // 接收传感器数据时间和当前时间间隔
                //接收传感器数据长时间超时
                if (responseTimeInterval > Constant.moduleReceiveTimeOutForReInit) {
                    if(myApp.initResult){
                        myApp.moduleUtils.free();
                        SystemClock.sleep(3000);
                    }

                    boolean result = myApp.moduleUtils.init();
                    myApp.appLogSDDao.save("模块上电 "+result+", readDataResponseTime="+Constant.sdf.format(new Date(myApp.moduleReceiveDataTime)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "moduleInit 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "moduleInit 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }

}
