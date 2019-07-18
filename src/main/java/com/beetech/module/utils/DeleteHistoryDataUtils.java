package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.code.request.DeleteHistoryDataRequest;
import com.beetech.module.constant.Constant;
import com.rscja.deviceapi.Module;

public class DeleteHistoryDataUtils {
    private final static String TAG = DeleteHistoryDataUtils.class.getSimpleName();

    public static void deleteHistoryData(Context context) {
        long runTime = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication) context.getApplicationContext();
            Log.d(TAG, "gwId = "+myApp.gwId);
            DeleteHistoryDataRequest deleteHistoryDataRequest = new DeleteHistoryDataRequest(myApp.gwId);
            byte[] buf = deleteHistoryDataRequest.getBuf();
            Log.d(TAG, "deleteHistoryDataRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase());

            Module module = myApp.module;
            if (module != null && myApp.initResult) {
                boolean sendResult = module.send(buf);
                myApp.lastWriteTime = System.currentTimeMillis();
                if(Constant.IS_SAVE_MODULE_LOG) {
                    try{
                        myApp.moduleBufSDDao.save(buf, 0, deleteHistoryDataRequest.getCmd(), sendResult); // 保存串口通信数据
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "deleteHistoryData 异常", e);

        } finally {
            Log.d(TAG, "deleteHistoryData 耗时：" + (System.currentTimeMillis() - runTime));
        }
    }

}
