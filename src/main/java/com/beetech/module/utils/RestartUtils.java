package com.beetech.module.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class RestartUtils {

    public static void restartApplication(Context context) {

        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            //与正常页面跳转一样可传递序列化数据,在Launch页面内获得
            intent.putExtra("REBOOT", "reboot");
            PendingIntent restartIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
            android.os.Process.killProcess(android.os.Process.myPid());

//            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            //与正常页面跳转一样可传递序列化数据,在Launch页面内获得
//            intent.putExtra("REBOOT","reboot");
//            context.startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
