package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.code.request.SetTimeRequest;
import com.beetech.module.constant.Constant;
import com.rscja.deviceapi.Module;

public class SetTimeUtils {
    private final static String TAG = SetTimeUtils.class.getSimpleName();

    public static void setTime(Context context){
        long runTime = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication)context.getApplicationContext();
            long ntpTimeInMills = System.currentTimeMillis();
            if(ntpTimeInMills < 1559318400000L){
                Log.e(TAG, "ntpTimeInMills < 2019-6-1, ntpTimeInMills="+ntpTimeInMills);
                return;
            }
            Log.d(TAG, "ntpTimeInMills = "+ntpTimeInMills);

            Module module = myApp.module;
            if (module != null && myApp.initResult) {
                SetTimeRequest setTimeRequest = new SetTimeRequest(myApp.gwId, ntpTimeInMills);
                byte[] buf = setTimeRequest.getBuf();
                Log.d(TAG, Thread.currentThread().getName() + ", setTimeRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase());
                boolean sendResult = module.send(buf);
                myApp.lastWriteTime = System.currentTimeMillis();
                if(Constant.IS_SAVE_MODULE_LOG) {
                    try{
                        myApp.moduleBufSDDao.save(buf, 0, setTimeRequest.getCmd(), sendResult); // 保存串口通信数据
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setTime 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "setTime 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }
}
