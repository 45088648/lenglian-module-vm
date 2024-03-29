package com.beetech.module.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.beetech.module.activity.RealtimeMonitorActivity;
import com.beetech.module.application.MyApplication;
import com.beetech.module.utils.BeginMonitorUtils;

//开机自启动广播接受
public class AutoStartBroadcastReceiver extends BroadcastReceiver {
    static final String action_boot ="android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)){
            Intent sayHelloIntent=new Intent(context,RealtimeMonitorActivity.class);
            sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(sayHelloIntent);

            try{
                MyApplication myApp = (MyApplication)context.getApplicationContext();
                BeginMonitorUtils.beginMonitorByBoot(myApp);
                if(myApp.appLogSDDao != null){
                    myApp.appLogSDDao.save("系统开机");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}