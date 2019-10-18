package com.beetech.module.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.beetech.module.application.MyApplication;

/**
 * 功能描述：在系统即将关闭时发出的广播的接收器
 */
public class ShutdownBroadcastReceiver extends BroadcastReceiver {
 
    private static final String TAG = ShutdownBroadcastReceiver.class.getSimpleName();
    
	private static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.i(TAG, "Shut down this system, ShutdownBroadcastReceiver onReceive()");
	    
		if (intent.getAction().equals(ACTION_SHUTDOWN)) {
		    Log.i(TAG, "ShutdownBroadcastReceiver onReceive(), Do thing!");

		    try {
				MyApplication myApp = (MyApplication) context.getApplicationContext();
				if (myApp.appLogSDDao != null) {
					myApp.appLogSDDao.save("系统关机");
				}
			} catch (Exception e){
		    	e.printStackTrace();
			}
		}
	}
}