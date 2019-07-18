package com.beetech.module.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.vt.VtSocketLog;
import com.beetech.module.bean.vt.VtStateRequestBean;
import com.beetech.module.bean.vt.VtStateRequestBeanUtils;
import com.beetech.module.client.ClientConnectManager;
import com.beetech.module.client.ConnectUtils;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.AppStateUtils;
import com.beetech.module.utils.DeleteHistoryDataUtils;
import com.beetech.module.utils.ModuleUtils;
import com.beetech.module.utils.NodeParamUtils;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {
    private final static String TAG = SmsReceiver.class.getSimpleName();

    private ModuleUtils moduleUtils;
    private MyApplication myApp;
    private VtStateRequestBeanUtils vtStateRequestBeanUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        moduleUtils = new ModuleUtils(context);
        myApp = (MyApplication)context.getApplicationContext();

        vtStateRequestBeanUtils = new VtStateRequestBeanUtils(context);

        //[1]获取发短信送的号码  和内容
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu : objects) {
            //[2]获取smsmessage实例
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            //[3]获取发送短信的内容
            String body = smsMessage.getMessageBody();
            Date date = new Date(smsMessage.getTimestampMillis());//时间
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //[4]获取发送者
            String address = smsMessage.getOriginatingAddress();
            String receiveTime = format.format(date);
            String logContent = "短信:" + body + "，" + address+"，"+receiveTime;
            Log.e(TAG, logContent);
            myApp.appLogSDDao.save(logContent);

            final String smsContent = body.replaceAll("【.+?】", "").trim();
            if(TextUtils.isEmpty(smsContent)){
                return;
            }

            new Thread(){
                @Override
                public void run() {
                    parseSmsContent(smsContent, myApp);
                }
            }.start();

        }
    }

    public void parseSmsContent(String smsContent, Context context){
        Log.d(TAG, "parseSmsContent begin, "+smsContent);
        try {
            //  st:19952200225|N|aaaaaa|0|0|0|0|0|gtw1.wendu114.com|8088
            if(smsContent.startsWith("st:")){
                myApp.queryConfigRealtimeSDDao.updateBySmsSt(smsContent);
                myApp.appLogSDDao.save("短信初始化：host="+ ConnectUtils.HOST+", port="+ConnectUtils.PORT);
                ClientConnectManager.getInstance(context).connect();

            } else if("ResetSystomOperation".equals(smsContent)){

                moduleUtils.init();
                myApp.manualStopModuleFlag = 0;
                myApp.appLogSDDao.save("模块重新上电初始化");

            } else if("stop module".equals(smsContent)){

                moduleUtils.free();
                myApp.manualStopModuleFlag = 1;
                Log.d(TAG, "========== myApp.manualStopModuleFlag = "+myApp.manualStopModuleFlag);
                myApp.appLogSDDao.save("手动模块释放");

            } else if("start module".equals(smsContent)){

                moduleUtils.init();
                myApp.manualStopModuleFlag = 0;
                Log.d(TAG, "========== myApp.manualStopModuleFlag = "+myApp.manualStopModuleFlag);
                myApp.appLogSDDao.save("手动模块释放重新上电");

            } else if("deleteReadDataOld".equals(smsContent)){ // 短信指令删除历史温湿度数据
                long sensorDataTimeEndInMills = System.currentTimeMillis();
                myApp.readDataSDDao.deleteOld(sensorDataTimeEndInMills);
                myApp.appLogSDDao.save("短信指令删除历史温湿度数据");

            }  else if("deleteHistoryData".equals(smsContent)){ // 短信指令删除标签模块历史数据
                long sensorDataTimeEndInMills = System.currentTimeMillis();
                DeleteHistoryDataUtils.deleteHistoryData(context);
                myApp.appLogSDDao.save("短信指令删除标签模块历史数据");

            } else if("gwlast".equals(smsContent)){

                IoSession mSession = myApp.session;
                if(mSession != null && mSession.isConnected()){
                    VtStateRequestBean vtStateRequestBean = vtStateRequestBeanUtils.getMessage();

                    if(vtStateRequestBean != null) {
                        StringBuffer stateSb = AppStateUtils.getState(myApp);
                        vtStateRequestBean.getBody().setAppState(stateSb.toString());

                        if (Constant.IS_SAVE_SOCKET_LOG){
                            //保存日志
                            try {
                                VtSocketLog vtSocketLog = new VtSocketLog(JSON.toJSONString(vtStateRequestBean), 0, 0L, Thread.currentThread().getName());
                                myApp.vtSocketLogSDDao.save(vtSocketLog);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        WriteFuture writeResult= mSession.write(JSON.toJSONString(vtStateRequestBean));

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
                    }
                }

            } else if("requestNodeParam".equals(smsContent)){
                NodeParamUtils.requestNodeParam(myApp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "解析短信异常", e);
        } finally {
            Log.d(TAG, "parseSmsContent end");
        }
    }
}