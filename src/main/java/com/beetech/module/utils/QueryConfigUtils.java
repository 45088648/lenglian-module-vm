package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.code.request.QueryConfigRequest;
import com.beetech.module.constant.Constant;
import com.rscja.deviceapi.Module;

public class QueryConfigUtils {
    private final static String TAG = QueryConfigUtils.class.getSimpleName();

    public static boolean queryConfig(Context context){
        long runTime = System.currentTimeMillis();
        boolean sendResult = false;
        try{
            MyApplication myApp = (MyApplication) context.getApplicationContext();

            QueryConfigRequest queryConfigRequest = new QueryConfigRequest();
            byte[] buf = queryConfigRequest.getBuf();
            Log.d(TAG, "queryConfigRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase());

            Module module = myApp.module;
            if (module != null && myApp.initResult) {
                sendResult = module.send(buf);
                myApp.lastWriteTime = System.currentTimeMillis();
                if(Constant.IS_SAVE_MODULE_LOG) {
                    try{
                        myApp.moduleBufSDDao.save(buf, 0, queryConfigRequest.getCmd(), sendResult); // 保存串口通信数据
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "queryConfig 异常", e);

        } finally {
            Log.d(TAG, "queryConfig 耗时：" + (System.currentTimeMillis()-runTime));
        }
        return sendResult;
    }
}
