package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.code.request.ReadDataRequest;
import com.beetech.module.constant.Constant;
import com.rscja.deviceapi.Module;

public class ReadDataUtils {

    private final static String TAG = ReadDataUtils.class.getSimpleName();

    public static void readData(Context context){
        long runTime = System.currentTimeMillis();

        try{
            MyApplication myApp = (MyApplication) context.getApplicationContext();

            long currentTimeMillis = System.currentTimeMillis();
            long responseTimeInterval = currentTimeMillis - myApp.readDataResponseTime; // 接收传感器数据时间和当前时间间隔
            //首次或接收传感器数据时间超时
            if(myApp.readDataResponseTime == 0 || responseTimeInterval > Constant.readDataResponseTimeOut){
                ReadDataRequest readDataRequest = new ReadDataRequest(myApp.gwId, 0, myApp.serialNo);
                byte[] buf = readDataRequest.getBuf();
                Log.d(TAG, Thread.currentThread().getName() + ", readDataRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase()+", myApp.serialNo="+myApp.serialNo+", responseTimeInterval="+responseTimeInterval);

                Module module = myApp.module;
                if (module != null && myApp.initResult) {
                    boolean sendResult = module.send(buf);
                    myApp.lastWriteTime = System.currentTimeMillis();
                    if(Constant.IS_SAVE_MODULE_LOG) {
                        try{
                            myApp.moduleBufSDDao.save(buf, 0, readDataRequest.getCmd(), sendResult); // 保存串口通信数据
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
//                        myApp.appLogSDDao.save("发送读第一条传感器数据指令, responseTimeInterval="+responseTimeInterval);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "readData 异常", e);

        } finally {
            Log.d(TAG, "readData 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }
}
