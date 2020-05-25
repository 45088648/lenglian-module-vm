package com.beetech.module.activity;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.beetech.module.R;
import com.beetech.module.adapter.ReadDataRealtimeRvAdapter;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.AppLogSDDao;
import com.beetech.module.dao.BaseSDDaoUtils;
import com.beetech.module.dao.ReadDataRealtimeSDDao;
import com.beetech.module.fragment.GridSpacingItemDecoration;
import com.beetech.module.listener.BatteryListener;
import com.beetech.module.listener.PhoneStatListener;
import com.beetech.module.service.JobProtectService;
import com.beetech.module.service.ModuleService;
import com.beetech.module.service.RemoteService;
import com.beetech.module.utils.BeginMonitorUtils;
import com.beetech.module.utils.DateUtils;
import com.beetech.module.utils.DevStateUtils;
import com.beetech.module.utils.EndMonitorUtils;
import com.beetech.module.utils.NetUtils;
import com.beetech.module.utils.ServiceAliveUtils;
import com.beetech.module.utils.SoundLedAlarmUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RealtimeMonitorActivity extends AppCompatActivity {
    private final static String TAG = RealtimeMonitorActivity.class.getSimpleName();
    private int refreshInterval = 1000*30*1; //刷新数据间隔

    private AppLogSDDao appLogSDDao;
    private BatteryListener listener;

    public TelephonyManager mTelephonyManager;
    public PhoneStatListener mListener;

    @ViewInject(R.id.rvReadDataRealtimeData)
    private RecyclerView rvReadDataRealtime;

    private ReadDataRealtimeSDDao readDataRealtimeSDDao;
    private List<ReadDataRealtime> readDataRealtimeListRefresh = new ArrayList<>();

    private ReadDataRealtimeRvAdapter readDataRealtimeRvAdapter;
    int spanCount = 1;
    int spacing = 5;

    //定位
    @ViewInject(R.id.bmapView)
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private MyApplication myApp;

    @ViewInject(R.id.tvImei)
    TextView tvImei;

    @ViewInject(R.id.tvDevNum)
    TextView tvDevNum;
    @ViewInject(R.id.tvNetState)
    TextView tvNetState;
    @ViewInject(R.id.tvMonitorState)
    TextView tvMonitorState;
    @ViewInject(R.id.tvRefreshTime)
    TextView tvRefreshTime;
    @ViewInject(R.id.tvBeginMonitorTime)
    TextView tvBeginMonitorTime;
    @ViewInject(R.id.tvEndMonitorTime)
    TextView tvEndMonitorTime;

    //按钮
    @ViewInject(R.id.btn_beginMonitor)
    private Button btnBeginMonitor;

    @ViewInject(R.id.btn_endMonitor)
    private Button btnEndMonitor;

    @ViewInject(R.id.btn_print)
    private Button btnPrint;

    private BaseSDDaoUtils baseSDDaoUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_realtime_monitor);
        ViewUtils.inject(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD, WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);//开机不锁屏 设置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //屏幕唤醒
        PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "StartupReceiver:");//最后的参数是LogCat里用的Tag
        wl.acquire();

        //屏幕解锁
        KeyguardManager km= (KeyguardManager) getBaseContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("StartupReceiver");//参数是LogCat里用的Tag
        kl.disableKeyguard();

        if(appLogSDDao == null){
            appLogSDDao = new AppLogSDDao(this);
        }

//        appLogSDDao.save(TAG + " onCreate");
        startModuleService();

        myApp = (MyApplication) getApplication();
        baseSDDaoUtils = new BaseSDDaoUtils(myApp);

        //定位
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        mMapView.showScaleControl(false); //地图上比例尺
        mMapView.showZoomControls(false); // 隐藏缩放控件

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

        if(readDataRealtimeSDDao == null){
            readDataRealtimeSDDao = new ReadDataRealtimeSDDao(this);
        }

        rvReadDataRealtime.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        readDataRealtimeRvAdapter = new ReadDataRealtimeRvAdapter(readDataRealtimeListRefresh);
        readDataRealtimeRvAdapter.setOnItemLongClickListener(new ReadDataRealtimeRvAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                ReadDataRealtime readDataRealtime = readDataRealtimeListRefresh.get(position);
                Intent intent=new  Intent(RealtimeMonitorActivity.this, TempLineActivity.class);
                intent.putExtra("sensorId",readDataRealtime.getSensorId());
                startActivity(intent);
            }
        });

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, spanCount);
        mGridLayoutManager.setOrientation(LinearLayout.VERTICAL);
        rvReadDataRealtime.setLayoutManager(mGridLayoutManager);
        rvReadDataRealtime.setAdapter(readDataRealtimeRvAdapter);

        //定时刷新
        handlerRefresh.removeCallbacks(runnableRefresh);
        handlerRefresh.postDelayed(runnableRefresh, 0);
    }

    public void startModuleService(){
        /*如果服务正在运行，直接return*/
//        if (!ServiceAliveUtils.isServiceRunning(this, Constant.className_GPSService)){
//            Log.d(TAG, "startService GPSService");
//            startService(new Intent(this, GPSService.class));
//        }

        /*如果服务正在运行，直接return*/
        if (!ServiceAliveUtils.isServiceRunning(this, Constant.className_moduleService)){
            /* 启动串口通信服务 */
            startService(new Intent(this, ModuleService.class));

            //开启守护线程 aidl
            startService(new Intent(this, RemoteService.class));

            //创建唤醒定时任务
            try {
                //获取JobScheduler 他是一种系统服务
                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancelAll();
                JobInfo.Builder builder = new JobInfo.Builder(1024, new ComponentName(getPackageName(), JobProtectService.class.getName()));

                if(Build.VERSION.SDK_INT >= 24) {
                    //android N之后时间必须在15分钟以上
                    //            builder.setMinimumLatency(10 * 1000);
                    builder.setPeriodic(1000 * 60 * 15);
                }else{
                    builder.setPeriodic(1000 * 60 * 15);
                }
                builder.setPersisted(true);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
                int schedule = jobScheduler.schedule(builder.build());
                if (schedule <= 0) {
                    Log.w(TAG, "schedule error！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshReadDataRealtimeRv(){
        readDataRealtimeRvAdapter.notifyDataSetChanged();
        refreshState();
    }

    //刷新左侧状态
    public void refreshState(){
        tvImei.setText(Constant.imei);
        tvDevNum.setText(Constant.devNum);

        try {
            int dbm = 0;
            myApp.netWorkType = NetUtils.getNetworkState(myApp);
            if (myApp.netWorkType == NetUtils.NETWORK_WIFI) {
                dbm = DevStateUtils.getWifiRssi(myApp);
            }else if(myApp.netWorkType == NetUtils.NETWORK_2G || myApp.netWorkType == NetUtils.NETWORK_3G || myApp.netWorkType == NetUtils.NETWORK_4G ){
                dbm = DevStateUtils.getMobileDbm(myApp);
            }
            myApp.signalStrength = dbm;
            if(myApp.netWorkType == NetUtils.NETWORK_NONE){
                tvNetState.setText(NetUtils.network_type_name.get(myApp.netWorkType));
                tvNetState.setTextColor(Color.RED);
            } else {
                tvNetState.setText(NetUtils.network_type_name.get(myApp.netWorkType) +"  信号: "+myApp.signalStrength+"dbm");
                tvNetState.setTextColor(Color.BLUE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(myApp.monitorState == 0){
            tvMonitorState.setText("未监控");
            tvMonitorState.setTextColor(Color.RED);
        } else {
            tvMonitorState.setText("监控中");
            tvMonitorState.setTextColor(Color.BLUE);
        }

        tvRefreshTime.setText(DateUtils.parseDateToString(new Date(), DateUtils.C_YYYY_MM_DD_HH_MM_SS));
        if(myApp.beginMonitorTime != null){
            tvBeginMonitorTime.setText(DateUtils.parseDateToString(myApp.beginMonitorTime, DateUtils.C_YYYY_MM_DD_HH_MM_SS));
            tvBeginMonitorTime.setTextColor(Color.BLUE);
        }else {
            tvBeginMonitorTime.setText(null);
        }
        if(myApp.endMonitorTime != null){
            tvEndMonitorTime.setText(DateUtils.parseDateToString(myApp.endMonitorTime, DateUtils.C_YYYY_MM_DD_HH_MM_SS));
            tvEndMonitorTime.setTextColor(Color.BLUE);
        } else {
            tvEndMonitorTime.setText(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handlerRefresh.removeCallbacks(runnableRefresh);
        handlerRefresh.postDelayed(runnableRefresh, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            listener.unregister();
        }

//        new ModuleUtils(this).free(); //我们的应用要一直运行
        appLogSDDao.save(TAG+" onDestroy");
    }

    //定时刷新
    private Handler handlerRefresh = new Handler(){};
    Runnable runnableRefresh = new Runnable() {
        @Override
        public void run() {

            RefreshAsyncTask refreshAsyncTask = new RefreshAsyncTask();
            refreshAsyncTask.execute();

            handlerRefresh.postDelayed(this, refreshInterval);
        }
    };

    class RefreshAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {}

        @Override
        protected Integer doInBackground(String... params) {
            try{
                List<ReadDataRealtime> readDataRealtimeList = readDataRealtimeSDDao.queryAll();
                readDataRealtimeListRefresh.clear();
                readDataRealtimeListRefresh.addAll(readDataRealtimeList);
            } catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, "刷新数据异常");
            }

            if(myApp.alarmFlag && myApp.monitorState == 1){
                try{
                    SoundLedAlarmUtils.checkAlarm(myApp);
                } catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "监测报警异常");
                }
            }

            try{
                locRefresh();
            } catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, "位置刷新异常");
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            refreshReadDataRealtimeRv();
        }
    }

    private void locRefresh(){
        Log.d(TAG, "locRefresh");

        BDLocation location = myApp.location;
        if (location == null) {
            return;
        }
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.loc);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.clear();
        mBaiduMap.addOverlay(option);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
    }

    @OnClick(R.id.btn_beginMonitor)
    public void btn_beginMonitor_onClick(View v) {
        if(myApp.monitorState == 1){
            Toast.makeText(RealtimeMonitorActivity.this, "当前状态已是监控中", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setMessage("开始监控会清除标签历史数据，确定要开始监控吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try{
                    BeginMonitorUtils.beginMonitor(myApp);
                    refreshState();
                    Toast.makeText(RealtimeMonitorActivity.this, "开始监控", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "开始监控异常", e);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @OnClick(R.id.btn_endMonitor)
    public void btn_endMonitor_onClick(View v) {
        if(myApp.monitorState == 0){
            Toast.makeText(RealtimeMonitorActivity.this, "当前状态已是未监控", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("结束监控会停止记录标签数据，确定要结束监控吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try{
                    EndMonitorUtils.endMonitor(myApp);
                    Toast.makeText(RealtimeMonitorActivity.this, "结束监控", Toast.LENGTH_SHORT).show();

                    refreshState();

                    dialog.dismiss();
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "结束监控异常", e);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void endMonitor(){

    }

    @OnClick(R.id.btn_print)
    public void btn_print_onClick(View v) {
        try{
            Intent intent = new Intent(RealtimeMonitorActivity.this, QueryDataAllActivity.class);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            handlerRefresh.removeCallbacks(runnableRefresh);//切换到背景不更新

//            moveTaskToBack(true);
            Intent intent = new Intent();
            intent.setClass(RealtimeMonitorActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private Handler handlerToast = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Object toastMsg = msg.obj;
            if(toastMsg != null){
                Toast.makeText(RealtimeMonitorActivity.this, toastMsg.toString(), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };
}