package com.beetech.module.thread;

import android.content.Context;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.CheckSessionUtils;
import com.beetech.module.utils.DeleteReadDataOldUtils;
import com.beetech.module.utils.LocationServiceUtils;
import com.beetech.module.utils.SendGpsDataUtils;
import com.beetech.module.utils.SendShtrfNoResponseUtils;
import com.beetech.module.utils.SendShtrfUtils;
import com.beetech.module.utils.SendVtStateUtils;
import com.beetech.module.utils.SetSysTimeUtils;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTimeTask extends HandlerThread {
    private final static String TAG = ThreadTimeTask.class.getSimpleName();

    public final static int INTERVAL = 1000*1;
    public static long instanceTime;
    public static long runTime;
    public ExecutorService executor;
    private static ThreadTimeTask instance;
    private ThreadTimeTask() {
        super(TAG, android.os.Process.THREAD_PRIORITY_DEFAULT);
    }

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
                if(num % (60*1) == 0){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            myApp.moduleHandler.sendEmptyMessage(0);
                        }
                    });
                }

                //授时
                if(num != 0 && second == 31){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "SetSysTimeUtils.setSysTime");
                            try {
                                SetSysTimeUtils.setSysTime(myApp);
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "SetSysTimeUtils.setSysTime 异常", e);
                            }
                            myApp.moduleHandler.sendEmptyMessage(4);
                        }
                    });
                }

                //向串口发读数据报文，从第35秒开始读取
                if(num != 0 && second == 35){
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            myApp.moduleHandler.sendEmptyMessage(7);
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

                if (num % 30 == 0) {
                    if(num == 0 && myApp.monitorState == 1){
                        myApp.locationService.start();
                    }
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "LocationServiceUtils.checkLocationService run");
                            try {
                                LocationServiceUtils.checkLocationService(myApp);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "LocationServiceUtils.checkLocationService异常", e);
                            }
                        }
                    });
                }

                if(num % 53 == 0) {
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "SendGpsDataUtils.sendGpsData run");
                            try {
                                SendGpsDataUtils.sendGpsData(myApp);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "SendGpsDataUtils.sendGpsData异常", e);
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
