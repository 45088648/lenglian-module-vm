package com.beetech.module.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.beetech.module.KeepAliveConnection;
import com.beetech.module.application.MyApplication;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.AppLogSDDao;
import com.beetech.module.handler.ModuleHandler;
import com.beetech.module.listener.BatteryListener;
import com.beetech.module.listener.PhoneStatListener;
import com.beetech.module.receiver.ConnectReceiver;
import com.beetech.module.receiver.SmsReceiver;
import com.beetech.module.receiver.WakeReceiver;
import com.beetech.module.thread.ThreadModuleReceive;
import com.beetech.module.thread.ThreadTimeTask;
import com.beetech.module.utils.ServiceAliveUtils;
import com.rscja.deviceapi.PowerLED;

public class ModuleService extends Service {
    private final static String TAG = ModuleService.class.getSimpleName();
    public final static ConnectReceiver conncetReceiver = new ConnectReceiver();

    private AppLogSDDao appLogSDDao;
    private MyApplication myApp;

    private SmsReceiver mSmsReceiver;
    private BatteryListener listener;
    public TelephonyManager mTelephonyManager;
    public PhoneStatListener mListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new KeepAliveConnection.Stub() {};
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "---->onCreate");

        //  服务保活 start
        startGuardService();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(conncetReceiver , filter);

        Intent innerIntent = new Intent(this, GrayInnerService.class);
        Notification notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startService(innerIntent);
        startForeground(Constant.GRAY_SERVICE_ID, notification);

        //  服务保活 end

        if(appLogSDDao == null){
            appLogSDDao = new AppLogSDDao(this);
        }
        appLogSDDao.save(TAG + " onCreate");

        myApp = (MyApplication)getApplicationContext();

        if(myApp.moduleHandlerThread == null){
            myApp.moduleHandlerThread = new HandlerThread("moduleHandlerThread");
            myApp.moduleHandlerThread.start();
            myApp.moduleHandler = new ModuleHandler(myApp, myApp.moduleHandlerThread.getLooper());
        }
        //定时任务
        if(myApp.threadTimeTask == null){
            myApp.threadTimeTask = ThreadTimeTask.getInstance();
            myApp.threadTimeTask.init(this);
            myApp.threadTimeTask.start();
        }

        handler.postDelayed(runnable, 4000);
        handler.postDelayed(runnableWakerUpAlarm, 60*1000);

        //电量和插拔电源状态广播监听
        if(listener == null){
            listener = new BatteryListener(this);
            listener.register();
        }

        //获取telephonyManager, 监听信号强度
        if(mListener == null){
            mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mListener = new PhoneStatListener(this);
            mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
        }

        //注册短信广播接收者
        if(mSmsReceiver == null){
            IntentFilter smsFilter = new IntentFilter();
            smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            mSmsReceiver = new SmsReceiver();
            registerReceiver(mSmsReceiver, smsFilter);
        }
        detectUsbWithBroadcast();

        if(myApp.powerLED == null){
            myApp.powerLED = PowerLED.getInstance();
            Log.d(TAG, "myApp.powerLED getInstance");
        }
    }

    private void detectUsbWithBroadcast() {
        Log.d(TAG, "listenUsb: register");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        filter.addAction("android.hardware.usb.action.USB_STATE");

        registerReceiver(mUsbStateChangeReceiver, filter);
        Log.d(TAG, "listenUsb: registered");
    }

    private BroadcastReceiver mUsbStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                myApp.usbConnected = bundle.getBoolean("connected");
            }
            Log.d(TAG, "usb state onReceive: " + intent.getAction()+", connected="+myApp.usbConnected);
            Toast.makeText(getBaseContext(), "USB " + (myApp.usbConnected ? "接通": "断开"), Toast.LENGTH_SHORT).show();
        }
    };


    private Runnable runnableWakerUpAlarm = new Runnable() {
        @Override
        public void run() {
            //发送唤醒广播来促使挂掉的UI进程重新启动起来
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent();
            alarmIntent.setAction(WakeReceiver.ACTION);
            alarmIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent operation = PendingIntent.getBroadcast(ModuleService.this, Constant.WAKE_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constant.ALARM_INTERVAL, operation);
        }
    };

    private Handler handler = new Handler(){};
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //接收串口数据
            if(myApp.threadModuleReceive == null) {
                myApp.threadModuleReceive = ThreadModuleReceive.getInstance();
                myApp.threadModuleReceive.init(ModuleService.this);
                myApp.threadModuleReceive.start();
            }
        }
    };

    public void startGuardService() {
        Intent intent = new Intent();
        intent.setClass(this, GuardService.class);
        startService(intent);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "StepService:建立链接");
            boolean isServiceRunning = ServiceAliveUtils.isServiceRunning(ModuleService.this, "com.beetech.module.service.ScreenCheckService");
            if (!isServiceRunning) {
                Intent i = new Intent(ModuleService.this, ScreenCheckService.class);
                startService(i);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 断开链接
            startService(new Intent(ModuleService.this, RemoteService.class));
            // 重新绑定
            bindService(new Intent(ModuleService.this, RemoteService.class), mServiceConnection, Context.BIND_IMPORTANT);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        appLogSDDao.save(TAG+" onStartCommand");

        // 绑定建立链接
        bindService(new Intent(this, RemoteService.class), mServiceConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "---->onDestroy");
        appLogSDDao.save(TAG+" onDestroy");
        super.onDestroy();

        Intent intent = new Intent(ConnectReceiver.ACTION);
        sendBroadcast(intent);
        unregisterReceiver( conncetReceiver );
        Log.d(TAG, "sendBroadcast[" + ConnectReceiver.ACTION + "]");
    }



    private Handler handlerToast = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Object toastMsg = msg.obj;
            if(toastMsg != null){
                Toast.makeText(getApplicationContext(), toastMsg.toString(), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public void onCreate() {
            Log.i(TAG, "InnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i(TAG, "InnerService -> onStartCommand");
            Notification notification = new Notification();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
            startForeground(Constant.GRAY_SERVICE_ID, notification);
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
