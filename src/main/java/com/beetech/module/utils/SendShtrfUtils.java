package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.vt.ShtrfRequestBean;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.constant.Constant;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import java.util.ArrayList;
import java.util.List;

public class SendShtrfUtils {
    private final static String TAG = SendShtrfUtils.class.getSimpleName();

    private final static int queryCount = 200;

    public static void sendShtrf(Context context) throws Exception{
        long runTime = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication)context.getApplicationContext();

            IoSession mSession = myApp.session;
            if(mSession == null || !mSession.isConnected()){
                return;
            }

            List<ReadDataResponse> readDataResponseList = myApp.readDataSDDao.queryForSend(queryCount, 0);
            if(readDataResponseList == null || readDataResponseList.isEmpty()){
               return;
            }

            List<Long> ids_update_list = new ArrayList<>();
            for (ReadDataResponse readDataResponse : readDataResponseList){
                final Long id = readDataResponse.get_id();

                ShtrfRequestBean requestBean = new ShtrfRequestBean(readDataResponse);

                String inText = JSON.toJSONString(requestBean);
                //保存日志
                try {
                    if(Constant.IS_SAVE_SOCKET_LOG){
                        myApp.vtSocketLogSDDao.save(inText, 0, id, Thread.currentThread().getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                WriteFuture writeResult= mSession.write(inText);

                ids_update_list.add(id);
                Log.d(TAG, "shtrf, write, id="+id);

                writeResult.addListener(new IoFutureListener() {
                    public void operationComplete(IoFuture future) {
                        WriteFuture wfuture = (WriteFuture) future;
                        // 写入成功  
                        if (wfuture.isWritten()) {
                            Log.d(TAG, "shtrf, written, id="+id);

                            return;
                        }
                        // 写入失败，自行进行处理  
                    }
                });

            }

            myApp.readDataSDDao.updateSendFlagList(ids_update_list);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "sendShtrf 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "sendShtrf 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }
}
