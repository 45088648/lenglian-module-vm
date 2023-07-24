package com.beetech.module.thread;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.code.BaseResponse;
import com.beetech.module.code.CommonBase;
import com.beetech.module.code.ResponseFactory;
import com.beetech.module.code.response.DeleteHistoryDataResponse;
import com.beetech.module.code.response.QueryConfigResponse;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.code.response.SetDataBeginTimeResponse;
import com.beetech.module.code.response.SetTimeResponse;
import com.beetech.module.code.response.UpdateConfigResponse;
import com.beetech.module.code.response.UpdateSSParamResponse;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.ByteUtilities;
import com.beetech.module.utils.DateUtils;
import com.rscja.deviceapi.Module;

import java.util.Date;

/**
 * 读取串口模块数据
 */
public class ThreadModuleReceive extends HandlerThread {
    private final static String TAG = ThreadModuleReceive.class.getSimpleName();

    public static int INTERVAL = 200;
    public static long instanceTime;
    public static long runTime;
    private static ThreadModuleReceive instance;
    private MyApplication myApp;

    private ThreadModuleReceive() {
        super(TAG, android.os.Process.THREAD_PRIORITY_DEFAULT);
    }

    public synchronized static ThreadModuleReceive getInstance() {
        if (null == instance) {
            synchronized(ThreadModuleReceive.class) {
                instance = new ThreadModuleReceive();
                instanceTime = System.currentTimeMillis();
            }
        }
        return instance;
    }

    public void init(Context context){
        myApp = (MyApplication) context.getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG,"未捕获异常", e);
                e.printStackTrace();
            }
        });
    };

    @Override
    public void run() {
        while(true){
            runTime = System.currentTimeMillis();
            long num =  Constant.NUM_RECEIVE.getAndIncrement();
            Log.d(TAG, " run " + num);
            try {
                Module module = myApp.module;
                if (module != null && myApp.initResult) {
                    try {
                        byte[] buf = module.receive();
                        myApp.lastReadTime = System.currentTimeMillis();
                        if (buf != null && buf.length > 0) {
                            unpackReceiveBuf(buf);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "读串口数据异常", e);
                    }

                    try {
                        SystemClock.sleep(INTERVAL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        SystemClock.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "myApp.moduleHandler.sendEmptyMessage 异常", e);
            }
        }
    }

    //解析读取到的串口数据
    private void unpackReceiveBuf(byte[] readBuf) {
        Log.d(TAG, Thread.currentThread().getName() + ", unpackReceiveBuf.bufHex="+ ByteUtilities.asHex(readBuf).toUpperCase());
        myApp.moduleReceiveDataTime = System.currentTimeMillis();
        StringBuffer toastSb = new StringBuffer();
        int cmd = 0;
        int bufLen = readBuf.length;
        int index = 0;
        while(index < bufLen) {
            byte beginByte0 = readBuf[index++];
            byte beginByte1 = readBuf[index++];
            byte dataLenByte = readBuf[index++];

            int dataLen = ByteUtilities.toUnsignedInt(dataLenByte);

            byte[] packBuf = new byte[ 2 + 1+ dataLen + 2 + 2];
            int packIndex = 0;
            packBuf[packIndex++] = beginByte0;
            packBuf[packIndex++] = beginByte1;
            packBuf[packIndex++] = dataLenByte;

            byte cmdByte = readBuf[index++];
            cmd = ByteUtilities.toUnsignedInt(cmdByte);

            packBuf[packIndex++] = cmdByte;

            for (int i = 0; i < dataLen - 1; i++) {
                byte packDataByte = readBuf[index++];
                packBuf[packIndex++] = packDataByte;
            }

            byte check0 = readBuf[index++];
            byte check1 = readBuf[index++];
            byte end0 = readBuf[index++];
            byte end1 = readBuf[index++];

            packBuf[packIndex++] = check0;
            packBuf[packIndex++] = check1;
            packBuf[packIndex++] = end0;
            packBuf[packIndex++] = end1;

            Log.d(TAG, "packBuf="+ ByteUtilities.asHex(packBuf).toUpperCase());

            BaseResponse response = ResponseFactory.unpack(packBuf);
            if(response != null) {
                String gwid = response.getGwId();
                if(!TextUtils.isEmpty(gwid) && !gwid.equals(myApp.gwId)) {
                    myApp.gwId =gwid;
                }
            }
            if(response instanceof ReadDataResponse){
                ReadDataResponse readDataResponse = (ReadDataResponse)response;
                int readDataResponseError = readDataResponse.getError();

                if(readDataResponseError == 0){
                    if(isMontorData(readDataResponse)) {
                        try {
                            myApp.readDataSDDao.save(readDataResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "保存温度数据异常", e);
                            myApp.appLogSDDao.save(e.getMessage());
                        }
                    }

                    try {
                        myApp.readDataRealtimeSDDao.updateRealtime(readDataResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "更新实时数据异常", e);
                    }
                }

                /*
                   读到数据，继续读取下一条；读到空，停止读取，下一次35继续读取
                 */
                myApp.gwId = readDataResponse.getGwId();
                myApp.readDataResponseTime = System.currentTimeMillis();
                Log.d(TAG, "myApp.readDataResponseTime="+myApp.readDataResponseTime);

                if(readDataResponseError == 0){
                    myApp.serialNo = readDataResponse.getSerialNo();
                    if(Constant.isReadNextTime() && Constant.IS_READ_NEXT) {
                        Log.d(TAG, "readNext，serialNo = " + myApp.serialNo);
                        myApp.moduleHandler.sendEmptyMessageDelayed(7, 0);
                    }
                } else {
                    Log.d(TAG, "readNext，serialNo = " + myApp.serialNo + ", error = " + readDataResponseError);
                }
            }

            if (response instanceof QueryConfigResponse){
                QueryConfigResponse queryConfigResponse = (QueryConfigResponse)response;
                try {
                    myApp.queryConfigRealtimeSDDao.update(queryConfigResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String timeStr = DateUtils.parseDateToString(queryConfigResponse.getCalendar(), DateUtils.C_YYYY_MM_DD_HH_MM_SS);
                toastSb.append("查询本地配置：").append(timeStr).append("\n");
                toastSb.append("客户码：").append(queryConfigResponse.getCustomer().toUpperCase()).append("\n");
                toastSb.append("Debug：").append(queryConfigResponse.getDebug()).append("\n");
                toastSb.append("分类码：").append(queryConfigResponse.getCategory()).append("\n");
                toastSb.append("工作模式：").append(queryConfigResponse.getPattern()).append("\n");
                toastSb.append("传输速率：").append(queryConfigResponse.getBps()).append("\n");
                toastSb.append("频段：").append(queryConfigResponse.getChannel()).append("\n");
                toastSb.append("发射功率：").append(queryConfigResponse.getTxPower()).append("\n");
                toastSb.append("转发策略：").append(queryConfigResponse.getForwardFlag()).append("\n");
            }

            if(response instanceof UpdateConfigResponse){ // 更新完查询本地配置
                Log.v(TAG, "UpdateConfigResponse");
                UpdateConfigResponse updateConfigResponse = (UpdateConfigResponse)response;
                toastSb.append("修改模块配置：").append("\n");
                toastSb.append("客户码：").append(updateConfigResponse.getCustomer().toUpperCase()).append("\n");
                toastSb.append("Debug：").append(updateConfigResponse.getDebug()).append("\n");
                toastSb.append("分类码：").append(updateConfigResponse.getCategory()).append("\n");
                toastSb.append("工作模式：").append(updateConfigResponse.getPattern()).append("\n");
                toastSb.append("传输速率：").append(updateConfigResponse.getBps()).append("\n");
                toastSb.append("频段：").append(updateConfigResponse.getChannel()).append("\n");
                toastSb.append("发射功率：").append(updateConfigResponse.getTxPower()).append("\n");
                toastSb.append("转发策略：").append(updateConfigResponse.getForwardFlag()).append("\n");

                //查询本地配置
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG, "查询本地配置");
                        try {
                            SystemClock.sleep(1*1000);
                            Message msg = new Message();
                            msg.what = CommonBase.CMD_QUERY_CONFIG;
                            myApp.moduleHandler.sendMessageAtFrontOfQueue(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "查询本地配置 异常", e);
                        }
                    }
                }).start();
            }

            if(response instanceof DeleteHistoryDataResponse){
                DeleteHistoryDataResponse deleteHistoryDataResponse = (DeleteHistoryDataResponse)response;
                myApp.frontDeleteResponse = deleteHistoryDataResponse.getFront();
                myApp.rearDeleteResponse = deleteHistoryDataResponse.getRear();
                myApp.pflashLengthDeleteResponse = deleteHistoryDataResponse.getPflashLength();
            }
            if(response instanceof SetDataBeginTimeResponse){
                SetDataBeginTimeResponse setDataBeginTimeResponse = (SetDataBeginTimeResponse)response;
                myApp.setDataBeginTime = setDataBeginTimeResponse.getDataBeginTime();
                toastSb.append("数据开始时间：").append(DateUtils.parseDateToString(myApp.setDataBeginTime, DateUtils.C_YYYY_MM_DD_HH_MM_SS)+"~"+setDataBeginTimeResponse.getError());
            }

            if(response instanceof SetTimeResponse){
                SetTimeResponse setTimeResponse = (SetTimeResponse)response;
            }

            if(response instanceof UpdateSSParamResponse){
                UpdateSSParamResponse updateSSParamResponse = (UpdateSSParamResponse)response;
                if(toastSb.length()==0){
                    toastSb.append("修改SS时间参数：");
                }
                toastSb.append(updateSSParamResponse.getSensorId()).append("~").append(updateSSParamResponse.getError()).append(" ");
            }
        }

        if(Constant.IS_SAVE_MODULE_LOG){
            try{
                myApp.moduleBufSDDao.save(readBuf, 1, cmd, true); // 保存串口通信数据
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(!TextUtils.isEmpty(toastSb.toString())){
            try {
                Message toastMsg = new Message();
                toastMsg.obj = toastSb.toString();
                myApp.toastHandler.sendMessage(toastMsg);
                myApp.appLogSDDao.save(toastSb.toString());
            }catch (Exception e){
                e.printStackTrace();
                Log.e("提示消息异常", e.getMessage());
            }
        }
    }

    /**
     * 监控中或未监控，但采集时间是监控期间的数据
     */
    public boolean isMontorData(ReadDataResponse readDataResponse){
        Date sensorDataTime = readDataResponse.getSensorDataTime();
        if(myApp.monitorState == 1){
            return true;

        }else if(myApp.monitorState == 0){

            if(myApp.beginMonitorTime == null || myApp.endMonitorTime == null){
                return false;
            }
            if(sensorDataTime.equals(myApp.beginMonitorTime) ){
                return true;

            }else if(sensorDataTime.after(myApp.beginMonitorTime) && sensorDataTime.before(myApp.endMonitorTime)) {
                return true;

            }else if(sensorDataTime.equals(myApp.endMonitorTime)){
                return true;
            }
        }
        return false;
    }
}
