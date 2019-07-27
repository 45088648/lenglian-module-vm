package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.code.request.ReadDataRequest;
import com.beetech.module.constant.Constant;
import com.rscja.deviceapi.Module;

public class ReadNextUtils {
    private final static String TAG = ReadNextUtils.class.getSimpleName();

    public static void readNext(Context context){
        long runTime = System.currentTimeMillis();
        MyApplication myApp = (MyApplication) context.getApplicationContext();
        Log.d(TAG, "readNext run " + myApp.serialNo);

        try{
            ReadDataRequest readDataRequest = new ReadDataRequest(myApp.gwId, 0, myApp.serialNo);
            readDataRequest.pack();
            byte[] buf = readDataRequest.getBuf();
            Log.d(TAG, Thread.currentThread().getName() + ", readDataRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase()+", myApp.serialNo="+myApp.serialNo);

            Module module = myApp.module;
            boolean sendResult = module.send(buf);
            myApp.lastWriteTime = System.currentTimeMillis();
            if(Constant.IS_SAVE_MODULE_LOG) {
                try{
                    myApp.moduleBufSDDao.save(buf, 0, readDataRequest.getCmd(), sendResult); // 保存串口通信数据
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "readNext 异常", e);

        } finally {
            Log.d(TAG, "readNext 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }
}
