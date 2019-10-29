package com.beetech.module.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.AppLog;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.bean.vt.StateRequestBean;
import com.beetech.module.bean.vt.VtSocketLog;
import com.beetech.module.bean.vt.VtStateRequestBean;
import com.beetech.module.constant.Constant;

import org.apache.mina.core.session.IoSession;

import java.util.Date;
import java.util.List;

public class SendAppLogUtils {
    private final static String TAG = ModuleInitUtils.class.getSimpleName();
    private final static int queryCount = 100;

    public static void sendAppLog(Context context){
        long beginTimeInMills = System.currentTimeMillis();

        try {
            MyApplication myApp = (MyApplication)context.getApplicationContext();
            IoSession mSession = myApp.session;
            if(mSession == null || !mSession.isConnected()){
                return;
            }
            List<AppLog> dataList = myApp.appLogSDDao.queryForSend(queryCount, 0);
            if(dataList == null || dataList.isEmpty()){
                return;
            }
            QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();

            for (AppLog appLog : dataList){
                final Long id = appLog.get_id();
                String content = appLog.getContent();
                Date inputTime = appLog.getInputTime();
                VtStateRequestBean vtStateRequestBean = null;
                try {
                    if(queryConfigRealtime != null){
                        vtStateRequestBean = new VtStateRequestBean(queryConfigRealtime);
                        StateRequestBean body = vtStateRequestBean.getBody();
                        body.setBt(myApp.batteryPercent);
                        body.setPower(myApp.power);
                        body.setGwstate(myApp.monitorState);
                        body.setSt(2);
                        body.setMit(myApp.initTime);
                        long newId = 999000000+id;
                        vtStateRequestBean.setId(newId);
                        body.setAppState(content);
                        body.setBft(inputTime.getTime());
                        if (Constant.IS_SAVE_SOCKET_LOG){
                            //保存日志
                            try {
                                VtSocketLog vtSocketLog = new VtSocketLog(JSON.toJSONString(vtStateRequestBean), 0, 0L, Thread.currentThread().getName());
                                myApp.vtSocketLogSDDao.save(vtSocketLog);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mSession.write(JSON.toJSONString(vtStateRequestBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "sendModuleBuf 异常 ", e);

        } finally {
            Log.d(TAG, "sendModuleBuf 耗时 "+(System.currentTimeMillis() - beginTimeInMills));
        }

    }
}
