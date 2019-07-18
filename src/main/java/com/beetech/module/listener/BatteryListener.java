package com.beetech.module.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import com.beetech.module.application.MyApplication;

public class BatteryListener {
    private final static String TAG = "BatteryListener";
    private Context mContext;

    private BatteryBroadcastReceiver receiver;

    public BatteryListener(Context context) {
        mContext = context;
        receiver = new BatteryBroadcastReceiver();
    }

    public void register() {
        if (receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(Intent.ACTION_BATTERY_LOW);
            filter.addAction(Intent.ACTION_BATTERY_OKAY);
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            mContext.registerReceiver(receiver, filter);
        }
    }

    public void unregister() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyApplication myApp = (MyApplication) mContext.getApplicationContext();

            if (intent != null) {
                String acyion = intent.getAction();
                switch (acyion) {
                    case Intent.ACTION_BATTERY_CHANGED://电量发生改变
//                        Log.e(TAG, "BatteryBroadcastReceiver --> onReceive--> ACTION_BATTERY_CHANGED");
                        int current = intent.getExtras().getInt("level");// 获得当前电量
                        int total = intent.getExtras().getInt("scale");// 获得总电量
                        int percent = current * 100 / total;
//                        Toast.makeText(mContext, "电量发生改变, current="+current+", total="+total, Toast.LENGTH_SHORT).show();
                        myApp.batteryPercent = percent;
                        break;

                    case Intent.ACTION_BATTERY_LOW://电量低
//                        Log.e(TAG, "BatteryBroadcastReceiver --> onReceive--> ACTION_BATTERY_LOW");
                        break;

                    case Intent.ACTION_BATTERY_OKAY://电量充满
//                        Log.e(TAG, "BatteryBroadcastReceiver --> onReceive--> ACTION_BATTERY_OKAY");
                        break;

                    case Intent.ACTION_POWER_CONNECTED://接通电源
//                        Log.e("zhang", "BatteryBroadcastReceiver --> onReceive--> ACTION_POWER_CONNECTED");
                        myApp.power = 1;
                        break;

                    case Intent.ACTION_POWER_DISCONNECTED://拔出电源
//                        Log.e(TAG, "BatteryBroadcastReceiver --> onReceive--> ACTION_POWER_DISCONNECTED");
                        myApp.power = 0;
                        break;
                }
            }
        }
    }
}