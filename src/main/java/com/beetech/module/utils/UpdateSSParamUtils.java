package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.code.request.UpdateSSParamRequest;
import com.beetech.module.constant.Constant;
import com.rscja.deviceapi.Module;
import java.util.List;

public class UpdateSSParamUtils {
    private final static String TAG = UpdateSSParamUtils.class.getSimpleName();

    public static int updateSSParam(Context context){
        long runTime = System.currentTimeMillis();

        try{
            MyApplication myApp = (MyApplication)context.getApplicationContext();
            List<ReadDataRealtime> readDataRealtimeList = myApp.readDataRealtimeSDDao.queryAll();
            if(readDataRealtimeList == null || readDataRealtimeList.isEmpty()){
                return -1;
            }
            Module module = myApp.module;
            if (module == null || !myApp.initResult) {
                return -2;
            }

            for (ReadDataRealtime readDataRealtime : readDataRealtimeList) {
                UpdateSSParamRequest updateSSParamRequest = new UpdateSSParamRequest(myApp.gwId, readDataRealtime);
                byte[] buf = updateSSParamRequest.getBuf();
                Log.d(TAG, "updateSSParamRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase());
                boolean sendResult = module.send(buf);
                myApp.lastWriteTime = System.currentTimeMillis();
                if(Constant.IS_SAVE_MODULE_LOG) {
                    try{
                        myApp.moduleBufSDDao.save(buf, 0, updateSSParamRequest.getCmd(), sendResult); // 保存串口通信数据
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "updateSSParam 异常", e);
        } finally {
            Log.d(TAG, "updateSSParam 耗时：" + (System.currentTimeMillis()-runTime));
        }
        return 0;
    }
}
