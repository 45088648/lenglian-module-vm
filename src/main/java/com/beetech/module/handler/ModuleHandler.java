package com.beetech.module.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.code.request.UpdateConfigRequest;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.ByteUtilities;
import com.beetech.module.utils.DeleteHistoryDataUtils;
import com.beetech.module.utils.ModuleFreeUtils;
import com.beetech.module.utils.ModuleInitUtils;
import com.beetech.module.utils.QueryConfigUtils;
import com.beetech.module.utils.ReadNextUtils;
import com.beetech.module.utils.SetDataBeginTimeUtils;
import com.beetech.module.utils.SetTimeUtils;
import com.beetech.module.utils.UpdateSSParamUtils;
import com.rscja.deviceapi.Module;

public class ModuleHandler extends Handler {
    private final static String TAG = ModuleHandler.class.getSimpleName();
    private MyApplication myApp;

    public ModuleHandler(Context context, Looper looper){
        super(looper);
        myApp = (MyApplication)context.getApplicationContext();
    }

    @Override
    public void handleMessage(Message msg) {
        String threadName = Thread.currentThread().getName();
        Log.d(TAG, threadName + ", 收到消息, "+msg.what);

        switch (msg.what){

            case -1: // 释放
                Log.d(TAG, threadName + ", ModuleFreeUtils.moduleFree");
                try{
                    ModuleFreeUtils.moduleFree(myApp);
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "ModuleFreeUtils.moduleFree 异常", e);
                }
                break;

            case 0: // 上电
                Log.d(TAG, threadName + ", ModuleInitUtils.moduleInit");
                try{
                    ModuleInitUtils.moduleInit(myApp);
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "ModuleInitUtils.moduleInit 异常", e);
                }
                break;


            case 1:// 查询本地配置
                Log.d(TAG, threadName + ", QueryConfigUtils.queryConfig");
                try{
                    final boolean sendResult = QueryConfigUtils.queryConfig(myApp);
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "QueryConfigUtils.queryConfig 异常", e);
                }

                break;

            case 3:
                Log.d(TAG, "DeleteHistoryDataUtils.deleteHistoryData");
                try {
                    DeleteHistoryDataUtils.deleteHistoryData(myApp);
                    myApp.appLogSDDao.save("删除模块历史数据");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "DeleteHistoryDataUtils.deleteHistoryData 异常", e);
                }
                break;

            case 4:// 授时
                Log.d(TAG, threadName + ", SetTimeUtils.setTime");
                try{
                    SetTimeUtils.setTime(myApp);
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "SetTimeUtils.setTime 异常", e);
                }
                break;

            case 7:
                Log.d(TAG, threadName + ", ReadNextUtils.readNext");
                try{
                    ReadNextUtils.readNext(myApp);
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "ReadNextUtils.readNext 异常", e);
                }
                break;

            case 9: //设置数据开始时间
                Log.d(TAG, threadName + ", SetDataBeginTimeUtils.setDataBeginTime");
                try {
                    SetDataBeginTimeUtils.setDataBeginTime(myApp);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "SetDataBeginTimeUtils.setDataBeginTime 异常", e);
                }
                break;

            case 84:
                try {
                    QueryConfigRealtime queryConfigRealtime = myApp.queryConfigRealtimeSDDao.queryLast();
                    if (queryConfigRealtime == null) {
                        return;
                    }

                    UpdateConfigRequest updateConfigRequest = new UpdateConfigRequest(queryConfigRealtime);
                    if (!TextUtils.isEmpty(myApp.customer)) {
                        updateConfigRequest.setCustomer(myApp.customer);
                    }
                    updateConfigRequest.setPattern(myApp.pattern);
                    updateConfigRequest.setBps(myApp.bps);
                    updateConfigRequest.setChannel(myApp.channel);
                    updateConfigRequest.pack();
                    byte[] buf = updateConfigRequest.getBuf();
                    Log.d(TAG, "updateConfigRequest.buf="+ ByteUtilities.asHex(buf).toUpperCase());

                    Module module = myApp.module;
                    if (module != null && myApp.initResult) {
                        boolean sendResult = module.send(buf);
                        myApp.lastWriteTime = System.currentTimeMillis();

                        if (Constant.IS_SAVE_MODULE_LOG) {
                            try {
                                myApp.moduleBufSDDao.save(buf, 0, updateConfigRequest.getCmd(), sendResult); // 保存串口通信数据
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "修改模块参数异常", e);
                }
                break;

            case 0x9c:

                try {
                    UpdateSSParamUtils.updateSSParam(myApp);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "设置SS时间参数", e);
                }
                break;
        }
    }

}

