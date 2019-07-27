package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.code.request.SetDataBeginTimeRequest;
import com.beetech.module.constant.Constant;
import com.rscja.deviceapi.Module;

public class SetDataBeginTimeUtils {
    private final static String TAG = SetDataBeginTimeUtils.class.getSimpleName();

    public static void setDataBeginTime(Context context){
        long runTime = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication)context.getApplicationContext();

            Module module = myApp.module;
            if (module != null && myApp.initResult) {
                SetDataBeginTimeRequest setDataBeginTimeRequest = new SetDataBeginTimeRequest(myApp.gwId);
                byte[] buf = setDataBeginTimeRequest.getBuf();
                Log.d(TAG, Thread.currentThread().getName() + ", SetDataBeginTimeRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase());

                boolean sendResult = module.send(buf);
                myApp.lastWriteTime = System.currentTimeMillis();
                if(Constant.IS_SAVE_MODULE_LOG) {
                    try{
                        myApp.moduleBufSDDao.save(buf, 0, setDataBeginTimeRequest.getCmd(), sendResult); // 保存串口通信数据
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
