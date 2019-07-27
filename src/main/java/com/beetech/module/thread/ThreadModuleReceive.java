package com.beetech.module.thread;

import android.content.Context;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import com.beetech.module.application.MyApplication;
import com.beetech.module.constant.Constant;
import com.rscja.deviceapi.Module;

/**
 * 读取串口模块数据
 */
public class ThreadModuleReceive extends HandlerThread {
    private final static String TAG = ThreadModuleReceive.class.getSimpleName();

    public static int INTERVAL = 1000;
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
                    myApp.moduleHandler.sendEmptyMessage(-2);

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
}
