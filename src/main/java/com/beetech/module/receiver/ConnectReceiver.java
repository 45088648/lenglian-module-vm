package com.beetech.module.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.beetech.module.constant.Constant;
import com.beetech.module.service.GuardService;
import com.beetech.module.service.ModuleService;
import com.beetech.module.utils.ServiceAliveUtils;

public class ConnectReceiver extends BroadcastReceiver {
 
    private final static String TAG = ConnectReceiver.class.getSimpleName();
    public final static String ACTION = "com.beetech.module.receiver.CONNECT_SERVICE";

    public ConnectReceiver() {
    }
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (!ServiceAliveUtils.isServiceRunning(context, Constant.className_moduleService)) {
            Intent mIntent = new Intent();
            mIntent.setClass(context, ModuleService.class);
            context.startService(mIntent);
            Log.d(TAG, "start ModuleService");
        }

        if (!ServiceAliveUtils.isServiceRunning(context, Constant.className_guardService)) {
            Intent intent1 = new Intent();
            intent1.setClass(context, GuardService.class);
            context.startService(intent1);
            Log.d(TAG, "start GuardService");
        }
    }
}
