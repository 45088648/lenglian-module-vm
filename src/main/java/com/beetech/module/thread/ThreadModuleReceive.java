package com.beetech.module.thread;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.code.BaseResponse;
import com.beetech.module.code.ResponseFactory;
import com.beetech.module.code.request.SetDataBeginTimeRequest;
import com.beetech.module.code.response.DeleteHistoryDataResponse;
import com.beetech.module.code.response.QueryConfigResponse;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.code.response.SetDataBeginTimeResponse;
import com.beetech.module.code.response.SetTimeResponse;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.ByteUtilities;
import com.beetech.module.utils.ReadNextUtils;
import com.rscja.deviceapi.Module;

/**
 * 读取串口模块数据
 */
public class ThreadModuleReceive extends Thread {
    private final static String TAG = ThreadModuleReceive.class.getSimpleName();

    public static int INTERVAL = 1000;
    public static long instanceTime;
    public static long runTime;
    private static ThreadModuleReceive instance;
    private MyApplication myApp;

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

            Log.d(TAG, "moduleReceive");
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
                } else {
                    try {
                        SystemClock.sleep(INTERVAL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "moduleReceive 异常", e);
            }


        }
    }

    //解析读取到的串口数据
    private void unpackReceiveBuf(byte[] readBuf) {
        Log.d(TAG, "bufHex="+ ByteUtilities.asHex(readBuf).toUpperCase());
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
            if(response instanceof ReadDataResponse){
                ReadDataResponse readDataResponse = (ReadDataResponse)response;
                myApp.readDataResponseError = readDataResponse.getError();
                myApp.readDataResponseWaitSentSize1 = readDataResponse.getWaitSentSize1(); // 待发1, Sensor RAM队列中待发数据的数量为26条。
                myApp.readDataResponseWaitSentSize2 = readDataResponse.getWaitSentSize2(); // 待发2, Sensor Flash队列中待发数据的数量为0条。
                myApp.readDataResponseErrorcode = readDataResponse.getErrorcode(); // Errorcode, 记录flash发送错误的次数

                if(myApp.readDataResponseError == 0){
                    try {
                        myApp.readDataSDDao.save(readDataResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "保存温度数据异常", e);
                        myApp.appLogSDDao.save(e.getMessage());
                    }

                    try {
                        myApp.readDataRealtimeSDDao.updateRealtime(readDataResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "更新实时数据异常", e);
                    }
                }

                //读取下一条
                myApp.gwId = readDataResponse.getGwId();
                if(myApp.readDataResponseError == 0){
                    myApp.serialNo = readDataResponse.getSerialNo();
                    myApp.readDataResponseTime = System.currentTimeMillis();
                    Log.d(TAG, "myApp.readDataResponseTime="+myApp.readDataResponseTime);
                    Log.d(TAG, "ReadNextUtils.readNext " + myApp.serialNo);
                    try{
                        ReadNextUtils.readNext(myApp);
                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e(TAG, "ReadNextUtils.readNext异常", e);
                    }
                }


            }


            if (response instanceof QueryConfigResponse){
                QueryConfigResponse queryConfigResponse = (QueryConfigResponse)response;

                try {
                    myApp.queryConfigRealtimeSDDao.update(queryConfigResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
            }

            if(response instanceof SetTimeResponse){
                SetTimeResponse setTimeResponse = (SetTimeResponse)response;
            }
        }


        if(Constant.IS_SAVE_MODULE_LOG){
            try{
                myApp.moduleBufSDDao.save(readBuf, 1, cmd, true); // 保存串口通信数据
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }
}
