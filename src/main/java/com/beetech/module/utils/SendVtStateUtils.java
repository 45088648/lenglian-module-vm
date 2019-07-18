package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.vt.VtStateRequestBean;
import com.beetech.module.bean.vt.VtStateRequestBeanUtils;
import com.beetech.module.constant.Constant;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

public class SendVtStateUtils {
    private final static String TAG = SendVtStateUtils.class.getSimpleName();

    public static void sendVtState(Context context){
        long runTime = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication)context.getApplicationContext();

            IoSession mSession = myApp.session;
            if(mSession == null || !mSession.isConnected()){
                return;
            }

            VtStateRequestBeanUtils vtStateRequestBeanUtils = new VtStateRequestBeanUtils(myApp);
            VtStateRequestBean vtStateRequestBean = vtStateRequestBeanUtils.getMessage();
            if(vtStateRequestBean == null){
                return;
            }
            String inText = JSON.toJSONString(vtStateRequestBean);
            if(inText == null ){
                return;
            }
            //保存日志
            try {
                if(Constant.IS_SAVE_SOCKET_LOG) {
                    myApp.vtSocketLogSDDao.save(inText, 0, 0L, Thread.currentThread().getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            WriteFuture writeResult= mSession.write(inText);

            writeResult.addListener(new IoFutureListener() {
                public void operationComplete(IoFuture future) {
                    WriteFuture wfuture = (WriteFuture) future;
                    // 写入成功  
                    if (wfuture.isWritten()) {
                        return;
                    }
                    // 写入失败，自行进行处理  
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "sendVtState 异常", e);
            throw e;

        } finally {
            Log.d(TAG, "sendVtState 耗时：" + (System.currentTimeMillis()-runTime));
        }
    }
}
