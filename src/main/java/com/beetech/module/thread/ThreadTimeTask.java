package com.beetech.module.thread;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.CheckSessionUtils;
import com.beetech.module.utils.DeleteReadDataOldUtils;
import com.beetech.module.utils.ModuleInitUtils;
import com.beetech.module.utils.ReadDataUtils;
import com.beetech.module.utils.SendShtrfNoResponseUtils;
import com.beetech.module.utils.SendShtrfUtils;
import com.beetech.module.utils.SendVtStateUtils;
import com.beetech.module.utils.SetTimeUtils;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTimeTask extends Thread {
    private final static String TAG = ThreadTimeTask.class.getSimpleName();

    public final static int INTERVAL = 1000*1;
    public static long instanceTime;
    public static long runTime;
    public ExecutorService executor;
    private static ThreadTimeTask instance;

    public synchronized static ThreadTimeTask getInstance() {
        if (null == instance) {
            synchronized(ThreadTimeTask.class){
                if (null == instance) {
                    instance = new ThreadTimeTask();
                    instanceTime = System.currentTimeMillis();
                }
            }
        }
        return instance;
    }

    private MyApplication myApp;

    public void init(Context mContext){
        myApp = (MyApplication) mContext.getApplicationContext();
        executor = Executors.newCachedThreadPool();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG,"未捕获异常", e);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void run() {
        while(true){
            runTime = System.currentTimeMillis();
            long num =  Constant.NUM.getAndIncrement();
            final String threadName = Thread.currentThread().getName();
            Log.d(TAG, threadName  + ", num:" + num);


            Calendar cal = Calendar.getInstance();
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            try{

                //模块上电
                if(num % (60*5) == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "ModuleInitUtils.moduleInit");
                            try{
                                ModuleInitUtils.moduleInit(myApp);
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "ModuleInitUtils.moduleInit 异常", e);
                            }
                        }
                    });
                }


                //授时
                if(num != 0 && num % 55 == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "SetTimeUtils.setTime");
                            try{
                                SetTimeUtils.setTime(myApp);
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "SetTimeUtils.setTime 异常", e);
                            }
                        }
                    });
                }

                //向串口发读数据报文
                if(num != 0 && num % 5 == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "ReadDataUtils.readData");
                            try{
                                ReadDataUtils.readData(myApp);
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "ReadDataUtils.readData 异常", e);
                            }
                        }
                    });
                }

                //服务器连接
                if(num % 60 == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "CheckSessionUtils.checkSession");
                            try{
                                CheckSessionUtils.checkSession(myApp);
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "CheckSessionUtils.checkSession 异常", e);
                            }
                        }
                    });
                }


                //发温湿度数据
                if(num % 13 == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "SendShtrfUtils.sendShtrf");
                            try{
                                SendShtrfUtils.sendShtrf(myApp);
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "SendShtrfUtils.sendShtrf 异常", e);
                            }
                        }
                    });
                }

                //补发温湿度数据
                if(num % 65 == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "SendShtrfNoResponseUtils.sendShtrfNoResponse");
                            try{
                                SendShtrfNoResponseUtils.sendShtrfNoResponse(myApp);
                            } catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "SendShtrfNoResponseUtils.sendShtrfNoResponse 异常", e);
                            }
                        }
                    });
                }

                if(num % (60*5) == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "SendVtStateUtils.sendVtState run");
                            try {
                                SendVtStateUtils.sendVtState(myApp);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "SendVtStateUtils.sendVtState 异常", e);
                            }
                        }
                    });
                }

                if(num > 0 && num % 9999 == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "DeleteReadDataOldUtils.deleteReadDataOld run");
                            try {
                                DeleteReadDataOldUtils.deleteReadDataOld(myApp);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "DeleteReadDataOldUtils.deleteReadDataOld 异常", e);
                            }
                        }
                    });
                }

            }catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "run 异常", e);
            }

            try {
                SystemClock.sleep(INTERVAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
