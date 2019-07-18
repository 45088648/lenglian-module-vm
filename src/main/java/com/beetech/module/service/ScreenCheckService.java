package com.beetech.module.service;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.beetech.module.utils.ScreenManager;
import com.beetech.module.utils.ScreenReceiverUtil;
import android.app.Service;
import android.util.Log;
import android.support.annotation.Nullable;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenCheckService extends Service {
    public static final int NOTICE_ID = 100;
    private static final String TAG = ScreenCheckService.class.getSimpleName();
    private DownloadBinder mDownloadBinder;
    private NotificationCompat.Builder mBuilderProgress;
    private NotificationManager mNotificationManager;

    private ScreenReceiverUtil mScreenListener;
    private ScreenManager mScreenManager;
    private Timer mRunTimer;

    private int mTimeSec;
    private int mTimeMin;
    private int mTimeHour;

    private ScreenReceiverUtil.SreenStateListener mScreenListenerer = new ScreenReceiverUtil.SreenStateListener() {
        @Override
        public void onSreenOn() {
            mScreenManager.finishActivity();
            Log.d(TAG, "关闭了1像素Activity");
        }

        @Override
        public void onSreenOff() {
            mScreenManager.startActivity();
            Log.d(TAG, "打开了1像素Activity");
        }

        @Override
        public void onUserPresent() {
        }
    };
    private OnTimeChangeListener mOnTimeChangeListener;

    @Override
    public void onCreate() {
        super.onCreate();

//        注册锁屏广播监听器
        mScreenListener = new ScreenReceiverUtil(this);
        mScreenManager = ScreenManager.getInstance(this);
        mScreenListener.setScreenReceiverListener(mScreenListenerer);
        
        mDownloadBinder = new DownloadBinder();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        startRunTimer();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mDownloadBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mManager == null) {
            return;
        }
        mManager.cancel(NOTICE_ID);
        stopRunTimer();
//        mScreenListener.stopScreenReceiverListener();
    }


    private void startRunTimer() {
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                mTimeSec++;
                if (mTimeSec == 60) {
                    mTimeSec = 0;
                    mTimeMin++;
                }
                if (mTimeMin == 60) {
                    mTimeMin = 0;
                    mTimeHour++;
                }
                if (mTimeHour == 24) {
                    mTimeSec = 0;
                    mTimeMin = 0;
                    mTimeHour = 0;
                }
                String time = "时间为：" + mTimeHour + " : " + mTimeMin + " : " + mTimeSec;
                if (mOnTimeChangeListener != null) {
                    mOnTimeChangeListener.showTime(time);
                }
                Log.d(TAG, time);
            }
        };
        mRunTimer = new Timer();
        // 每隔1s更新一下时间
        mRunTimer.schedule(mTask, 1000, 1000);
    }

    private void stopRunTimer() {
        if (mRunTimer != null) {
            mRunTimer.cancel();
            mRunTimer = null;
        }
        mTimeSec = 0;
        mTimeMin = 0;
        mTimeHour = 0;
//        Log.d(TAG, "时间为：" + mTimeHour + " : " + mTimeMin + " : " + mTimeSec);
    }

    public interface OnTimeChangeListener {
        void showTime(String time);
    }

    public class DownloadBinder extends Binder {
        public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
            mOnTimeChangeListener = onTimeChangeListener;
        }
    }
}
