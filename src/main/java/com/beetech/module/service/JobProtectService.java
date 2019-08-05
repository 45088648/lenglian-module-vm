package com.beetech.module.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.AppLogSDDao;
import com.beetech.module.utils.ServiceAliveUtils;

public class JobProtectService extends Service {
    private final static String TAG = JobProtectService.class.getSimpleName();
    private AppLogSDDao appLogSDDao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appLogSDDao = new AppLogSDDao(this);
//        Toast.makeText(getApplicationContext(), TAG + "schedule ", Toast.LENGTH_SHORT).show();

        /*如果服务正在运行，直接return*/
        if (!ServiceAliveUtils.isServiceRunning(JobProtectService.this, Constant.className_moduleService)){
            /* 启动串口通信服务 */
            startService(new Intent(this, ModuleService.class));
            appLogSDDao.save(TAG + " schedule start ModuleService ");
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
