package com.beetech.module.receiver;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.beetech.module.application.MyApplication;
import com.beetech.module.dao.AppLogSDDao;
import com.beetech.module.handler.ModuleHandler;
import com.beetech.module.thread.ThreadModuleReceive;
import com.beetech.module.thread.ThreadTimeTask;

/**
 * 灰色保活手段唤醒广播
 */
public class WakeReceiver extends BroadcastReceiver {

    private final static String TAG = WakeReceiver.class.getSimpleName();
    private final static int WAKE_SERVICE_ID = -1111;
    public final static String ACTION = "com.beetech.module.receiver.Wake";
    private AppLogSDDao appLogSDDao;
    private MyApplication myApp;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION.equals(action)) {
            Log.i(TAG, "onReceive");
            appLogSDDao = new AppLogSDDao(context);

            Intent wakeIntent = new Intent(context, WakeNotifyService.class);
            context.startService(wakeIntent);

            myApp = (MyApplication)context.getApplicationContext();

            // 监测进程状态
            if(myApp.moduleHandlerThread == null){
                myApp.moduleHandlerThread = new HandlerThread("moduleHandlerThread");
                myApp.moduleHandlerThread.start();
                myApp.moduleHandler = new ModuleHandler(myApp, myApp.moduleHandlerThread.getLooper());
            }

            //定时任务
            if(myApp.threadTimeTask == null){ // 如果线程为空，重新创建初始化启动
                myApp.threadTimeTask = ThreadTimeTask.getInstance();
                myApp.threadTimeTask.init(context);
                myApp.threadTimeTask.start();
                appLogSDDao.save("threadTimeTask is null, restart threadTimeTask = "+myApp.threadTimeTask);
            }
            Log.d(TAG, "threadTimeTask = "+myApp.threadTimeTask);

            //读串口
            ThreadModuleReceive threadModuleReceive = myApp.threadModuleReceive;
            if(threadModuleReceive == null){ // 如果线程为空，重新创建初始化启动
                myApp.threadModuleReceive = ThreadModuleReceive.getInstance();
                myApp.threadModuleReceive.init(context);
                myApp.threadModuleReceive.start();
                appLogSDDao.save("threadModuleReceive is null, restart threadModuleReceive = "+myApp.threadModuleReceive);
            }
            Log.d(TAG, "threadModuleReceive = "+myApp.threadModuleReceive);
        }
    }

    /**
     * 用于其他进程来唤醒UI进程用的Service
     */
    public static class WakeNotifyService extends Service {

        @Override
        public void onCreate() {
            Log.i(TAG, "WakeNotifyService->onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(TAG, "WakeNotifyService->onStartCommand");
            if (Build.VERSION.SDK_INT < 18) {
                startForeground(WAKE_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
            } else {
                Intent innerIntent = new Intent(this, WakeGrayInnerService.class);
                startService(innerIntent);
                startForeground(WAKE_SERVICE_ID, new Notification());
            }
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "WakeNotifyService->onDestroy");
            super.onDestroy();
        }
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class WakeGrayInnerService extends Service {

        @Override
        public void onCreate() {
            Log.i(TAG, "InnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(TAG, "InnerService -> onStartCommand");
            startForeground(WAKE_SERVICE_ID, new Notification());
            //stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "InnerService -> onDestroy");
            super.onDestroy();
        }
    }
}
