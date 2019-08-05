package com.beetech.module.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import com.beetech.module.R;
import com.beetech.module.fragment.HistoryAppLogFragment;
import com.beetech.module.fragment.HistoryGpsDataFragment;
import com.beetech.module.fragment.HistoryModuleBufFragment;
import com.beetech.module.fragment.HistoryReadDataFragment;
import com.beetech.module.fragment.HistoryVtSocketLogFragment;
import com.beetech.module.fragment.RunStateFragment;

public class MainActivity extends AppCompatActivity {

	private final static String TAG = MainActivity.class.getSimpleName();

	private FragmentTabHost mTabHost;
	private FragmentManager fm;

    private int refreshDelayedInMs = 1000; //发送传感器数据线程启动延时
    private int refreshIntervalInMs = 1000*60; //发送传感器数据线程启动间隔

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕唤醒
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		fm = getSupportFragmentManager();
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, fm, R.id.realtabcontent);

		mTabHost.addTab(mTabHost.newTabSpec("identification").setIndicator("运行状态"), RunStateFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("history").setIndicator("温度数据"), HistoryReadDataFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("gps").setIndicator("gps数据"), HistoryGpsDataFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("bufHistory").setIndicator("串口通信"), HistoryModuleBufFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("appHistory").setIndicator("APP日志"), HistoryAppLogFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("vtSocketHistory").setIndicator("冷链网关通信"), HistoryVtSocketLogFragment.class, null);

//		appLogSDDao.save("MainActivity onCreate");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
//        appLogSDDao.save("MainActivity onResume");
	}

}
