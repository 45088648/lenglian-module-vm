package com.beetech.module.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.vt.VtSocketLog;
import com.beetech.module.bean.vt.VtStateRequestBean;
import com.beetech.module.bean.vt.VtStateRequestBeanUtils;
import com.beetech.module.client.ClientConnectManager;
import com.beetech.module.client.ConnectUtils;
import com.beetech.module.code.CommonBase;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.BaseSDDaoUtils;
import com.beetech.module.utils.AppStateUtils;
import com.beetech.module.utils.DateUtils;
import com.beetech.module.utils.DeleteHistoryDataUtils;
import com.beetech.module.utils.ModuleUtils;
import com.beetech.module.utils.NodeParamUtils;
import com.chainway.libs.mylibrary.CwSpecialFunc;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.proxy.utils.ByteUtilities;

import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {
    private final static String TAG = SmsReceiver.class.getSimpleName();

    private ModuleUtils moduleUtils;
    private BaseSDDaoUtils baseSDDaoUtils;
    private MyApplication myApp;
    private VtStateRequestBeanUtils vtStateRequestBeanUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        moduleUtils = new ModuleUtils(context);
        myApp = (MyApplication)context.getApplicationContext();

        vtStateRequestBeanUtils = new VtStateRequestBeanUtils(context);
        baseSDDaoUtils = new BaseSDDaoUtils(myApp);

        //[1]获取发短信送的号码  和内容
        //[2]获取smsmessage实例
        android.telephony.SmsMessage smsMessage;
        if (Build.VERSION.SDK_INT >= 19) { //KITKAT
            android.telephony.SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            smsMessage = msgs[0];
        } else {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            String pduHex = ByteUtilities.asHex((byte[]) pdus[0]);
            Log.v(TAG, "pduHex="+pduHex);
            smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
        }
        //[3]获取发送短信的内容
        String body = smsMessage.getMessageBody();
        Date date = new Date(smsMessage.getTimestampMillis());//时间

        //[4]获取发送者
        String address = smsMessage.getOriginatingAddress();
        String receiveTime = DateUtils.parseDateToString(date, DateUtils.C_YYYY_MM_DD_HH_MM_SS);
        String logContent = "短信:" + body + "，" + address+"，"+receiveTime;
        Log.v(TAG, logContent);

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

    public void parseSmsContent(String smsContent, Context context){
        Log.d(TAG, "parseSmsContent begin, "+smsContent);
        try {
            //  st:19952200225|N|aaaaaa|0|0|0|0|0|gtw1.wendu114.com|8088
            if(smsContent.startsWith("st:")){
                myApp.queryConfigRealtimeSDDao.updateBySmsSt(smsContent);
                myApp.appLogSDDao.save("短信初始化：host="+ ConnectUtils.HOST+", port="+ConnectUtils.PORT);
                ClientConnectManager.getInstance(context).connect();

            } else if(smsContent.startsWith("isSetDataBeginTimeByBoot")){ // isSetDataBeginTimeByBoot|[true|false]|20200410113255
                if (TextUtils.isEmpty(smsContent)) {
                    return;
                }
                String[] stParamStrArr = smsContent.split("\\|");
                String cmdStr = stParamStrArr[0];
                String isSetDataBeginTimeByBootStr = stParamStrArr[1];
                String timeStr = stParamStrArr[2];
                boolean isSetDataBeginTimeByBoot = Boolean.valueOf(isSetDataBeginTimeByBootStr);
                Log.d(TAG, "isSetDataBeginTimeByBoot="+isSetDataBeginTimeByBoot);
                myApp.queryConfigRealtimeSDDao.updateByIsSetDataBeginTimeByBoot(isSetDataBeginTimeByBoot);

            } else if("queryConfig".equals(smsContent)){

                Message msg = new Message();
                msg.what = CommonBase.CMD_QUERY_CONFIG;
                myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
                myApp.appLogSDDao.save("短信查询本地配置");

            }  else if(smsContent.startsWith("updateConfig")){
                Log.v(TAG, "updateConfig");
                String[] paramStrArr = smsContent.substring("updateConfig".length()+1).split("\\|");
                String customer = paramStrArr[0];
                int debug = Integer.valueOf(paramStrArr[1]);
                int category = Integer.valueOf(paramStrArr[2]);
                int pattern = Integer.valueOf(paramStrArr[3]);
                int bps = Integer.valueOf(paramStrArr[4]);
                int channel = Integer.valueOf(paramStrArr[5]);
                int txPower = Integer.valueOf(paramStrArr[6]);
                int forwardFlag = Integer.valueOf(paramStrArr[7]);
                Log.d(TAG, "customer=" + customer + ", debug=" + debug+ ", category=" + category + ", pattern=" + pattern + ", bps=" + bps + ", channel=" + channel + ", txPower=" + txPower + ", forwardFlag=" + forwardFlag);

                if(!TextUtils.isEmpty(customer)) {
                    myApp.customer = customer;
                    myApp.pattern = pattern;
                    myApp.bps = bps;
                    myApp.channel = channel;
                    myApp.txPower = txPower;
                    myApp.forwardFlag = forwardFlag;
                    myApp.debug = debug;
                    myApp.category = category;

                    Message msg = new Message();
                    msg.what = CommonBase.CMD_UPDATE_CONFIG;
                    myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
                    String contentLog = "更新本地配置，customer=" + customer + ", pattern=" + pattern + ", bps=" + bps +", txPower=" +txPower+", channel=" + channel+ ", forwardFlag=" + forwardFlag+", debug="+debug;
                    Log.v(TAG, contentLog);
                    myApp.appLogSDDao.save(contentLog);
                }

            } else if("ResetSystomOperation".equals(smsContent)){

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "CwSpecialFunc.getInstance().rebootDevice");
                        try {
                            CwSpecialFunc.getInstance().rebootDevice();//重启设备
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "btnRebootDevice_OnClick 异常", e);
                        }
                    }
                }).start();
                myApp.appLogSDDao.save("重启设备");

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

            }  else if("trancateLog".equals(smsContent)){ // 清空日志

                baseSDDaoUtils.trancateLog();
                myApp.appLogSDDao.save("短信指令清空日志");

            }  else if("saveLogOn".equals(smsContent)){

                Constant.IS_SAVE_MODULE_LOG = true;
                Constant.IS_SAVE_SOCKET_LOG = true;
                myApp.appLogSDDao.save("短信开启记录日志");

            }   else if("saveLogOff".equals(smsContent)){

                Constant.IS_SAVE_MODULE_LOG = false;
                Constant.IS_SAVE_SOCKET_LOG = false;
                myApp.appLogSDDao.save("短信关闭记录日志");

            }   else if("saveAndUpModuleLogOn".equals(smsContent)){

                Constant.IS_SAVE_MODULE_LOG = true;
                Constant.IS_UP_MODULE_LOG = true;
                myApp.appLogSDDao.save("短信开启记录并上传模块日志");

            }   else if("saveAndUpModuleLogOff".equals(smsContent)){

                Constant.IS_SAVE_MODULE_LOG = false;
                Constant.IS_UP_MODULE_LOG = false;
                myApp.appLogSDDao.save("短信关闭记录并上传模块日志");

            }   else if("upAppLogOn".equals(smsContent)){

                Constant.IS_UP_APP_LOG = true;
                myApp.appLogSDDao.save("短信开启上传APP日志");

            }   else if("upAppLogOff".equals(smsContent)){

                Constant.IS_UP_APP_LOG = false;
                myApp.appLogSDDao.save("短信关闭上传APP日志");

            }  else if("gwlast".equals(smsContent)){

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