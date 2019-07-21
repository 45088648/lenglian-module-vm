package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.bean.vt.SysRequestBean;
import com.beetech.module.constant.Constant;
import org.apache.mina.core.session.IoSession;

public class SysRequestUtils {
    private final static String TAG = SysRequestUtils.class.getSimpleName();

    public static int requestSys(Context context){
        long runTime = System.currentTimeMillis();

        try{
            MyApplication myApp = (MyApplication)context.getApplicationContext();
            QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
            if(queryConfigRealtime == null){
                return -1;
            }

            SysRequestBean sysRequestBean = new SysRequestBean(queryConfigRealtime, myApp);
            IoSession mSession = myApp.session;
            if(mSession == null || !mSession.isConnected()){
                return -2;
            }
            String inText = JSON.toJSONString(sysRequestBean);
            mSession.write(inText);
            if(Constant.IS_SAVE_SOCKET_LOG){
                try{
                    myApp.vtSocketLogSDDao.save(inText, 0, 0L, Thread.currentThread().getName());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "requestSys 异常", e);
        } finally {
            Log.d(TAG, "requestSys 耗时：" + (System.currentTimeMillis()-runTime));
        }
        return 0;
    }
}
