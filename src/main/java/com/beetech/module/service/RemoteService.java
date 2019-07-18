package com.beetech.module.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.app.Service;
import android.util.Log;
import android.app.Notification;
import com.beetech.module.KeepAliveConnection;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.ServiceAliveUtils;
import android.support.annotation.Nullable;

/**
 * 守护进程 双进程通讯
 *
 * @author zhangcs
 * @time Created by 2019-1-9 17:43:17
 */
public class RemoteService extends Service {
    private final static String TAG = GuardService.class.getSimpleName();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "GuardService:建立链接");
            boolean isServiceRunning = ServiceAliveUtils.isServiceRunning(RemoteService.this, Constant.className_screenCheckService);
            if (!isServiceRunning) {
                Intent i = new Intent(RemoteService.this, ScreenCheckService.class);
                startService(i);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // 断开链接
            if (!ServiceAliveUtils.isServiceRunning(RemoteService.this, Constant.className_moduleService)) {
                startService(new Intent(RemoteService.this, ModuleService.class));
            }
            // 重新绑定
            bindService(new Intent(RemoteService.this, ModuleService.class), mServiceConnection, Context.BIND_IMPORTANT);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new KeepAliveConnection.Stub() {
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, new Notification());
        // 绑定建立链接
        bindService(new Intent(this, ModuleService.class), mServiceConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

}