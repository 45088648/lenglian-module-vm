package com.beetech.module.application;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.beetech.module.R;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.client.ConnectUtils;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.AppLogSDDao;
import com.beetech.module.dao.GpsDataSDDao;
import com.beetech.module.dao.ModuleBufSDDao;
import com.beetech.module.dao.QueryConfigRealtimeSDDao;
import com.beetech.module.dao.ReadDataRealtimeSDDao;
import com.beetech.module.dao.ReadDataSDDao;
import com.beetech.module.dao.VtSocketLogSDDao;
import com.beetech.module.gps.GpsContent;
import com.beetech.module.greendao.dao.DaoMaster;
import com.beetech.module.greendao.dao.DaoSession;
import com.beetech.module.handler.CrashHandler;
import com.beetech.module.handler.ModuleHandler;
import com.beetech.module.handler.ToastHandler;
import com.beetech.module.service.LocationService;
import com.beetech.module.thread.ThreadModuleReceive;
import com.beetech.module.thread.ThreadTimeTask;
import com.beetech.module.utils.APKVersionCodeUtils;
import com.beetech.module.utils.AppStateUtils;
import com.beetech.module.utils.MobileInfoUtil;
import com.beetech.module.utils.ModuleUtils;
import com.beetech.module.utils.PhoneInfoUtils;
import com.beetech.module.utils.StringUtils;
import com.chainway.libs.mylibrary.CwSpecialFunc;
import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;
import com.rscja.deviceapi.Module;
import com.rscja.deviceapi.PowerLED;

import org.apache.mina.core.session.IoSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;
import java.util.Timer;

public class MyApplication extends Application {
    private final static String TAG = MyApplication.class.getSimpleName();
    public PowerLED powerLED;
    public static int powerLEDFlag = 0; //0 关闭；1 开启
    public Module module;
    public boolean initResult; //模块上电初始化结果
    public int manualStopModuleFlag = 0; // 手动模块断电释放标记：0：未断电 1：断电
    public long createTime;
    public long initTime;
    public long lastReadTime;
    public long lastWriteTime;
    public long setRtcTime;

    public int batteryPercent = 0; // 电量百分比
    public int power = 1;// 0断开  1接通
    public boolean usbConnected; //USB 接通
    public int netWorkType = 0;// 网络类型
    public int signalStrength = 0;// 信号强度

    public String gwId = "00000000";
    public static String customer;
    public int debug = 0; // debug
    public int category = 0; // 分类码
    public int pattern = 3; //工作模式
    public int bps = 0; // 传输速率
    public int channel = 4; // 频段
    public int txPower = 15; // 发射功率
    public int forwardFlag = 0; // 转发策略
    public int serialNo = 0;

    public long readDataResponseTime;
    public long moduleReceiveDataTime;
    public Date setDataBeginTime;
    public Integer frontDeleteResponse;
    public Integer rearDeleteResponse;
    public Integer pflashLengthDeleteResponse;

    public ThreadModuleReceive threadModuleReceive;
    public ThreadTimeTask threadTimeTask;
    public HandlerThread moduleHandlerThread;
    public ModuleHandler moduleHandler;
    public ToastHandler toastHandler;

    public IoSession session;
    //定位
    public LocationService locationService;
    public BDAbstractLocationListener locationListener;
    public BDLocation location;
    public Vibrator mVibrator;

    //北斗定位
    public GpsContent gpsContent = new GpsContent();

    //db操作对象
    public DaoMaster.DevOpenHelper devOpenHelper;
    public SQLiteDatabase database;
    public DaoMaster daoMaster;
    public DaoSession daoSession;

    public ReadDataSDDao readDataSDDao;
    public ReadDataRealtimeSDDao readDataRealtimeSDDao;
    public ModuleBufSDDao moduleBufSDDao;
    public GpsDataSDDao gpsDataSDDao;
    public AppLogSDDao appLogSDDao;
    public QueryConfigRealtimeSDDao queryConfigRealtimeSDDao;
    public VtSocketLogSDDao vtSocketLogSDDao;

    public CrashHandler appException;
    public PhoneInfoUtils phoneInfoUtils;
    public ModuleUtils moduleUtils;
    public Timer timer;

    public int monitorState;
    public Date beginMonitorTime;
    public Date endMonitorTime;

    public MediaPlayer mMediaPlayer;

    public static BluetoothAdapter adapter;

    public boolean isSetDataBeginTimeByBoot = false; //开机自启动监控，设置数据开始时间
    public boolean alarmFlag = true; //是否报警
    public static boolean alarmSoundFlag = true; //是否声音报警
    public static boolean alarmLightFlag = true; //是否灯光报警

    @Override
    public void onCreate() {
        super.onCreate();


        String processName = getProcessName(this, android.os.Process.myPid());
        Log.d(TAG, "processName="+processName);
        if (!TextUtils.isEmpty(processName) && processName.equals(Constant.REAL_PACKAGE_NAME)) {
            initApp();
        }
    }

    /**
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public void initApp(){
        try{
            CwSpecialFunc.getInstance().init(this); //初始化
            CwSpecialFunc.getInstance().setOnInitFinish(new CwSpecialFunc.OnInitFinish() {
                @Override
                public void ready() {
                    try {
                        CwSpecialFunc.getInstance().setDownStatusEnable(false);//禁止下拉菜单
                        CwSpecialFunc.getInstance().setHomeKeyEnable(false);   //禁止HOME按键
                        CwSpecialFunc.getInstance().setMenuKeyEnable(false);   //禁止菜单按键
                        String packageName = getPackageName();
                        String className = packageName.concat(".activity.RealtimeMonitorActivity");
                        Log.d(TAG, "开机默认启动Activity，packageName = "+packageName+", className = "+className);//开机默认启动Activity
                        Log.d(TAG, "set config of CwSpecialFunc");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void free() {
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
        toastHandler = new ToastHandler(this);
        appException = CrashHandler.getInstance();
        appException.init(getApplicationContext());
        mMediaPlayer = MediaPlayer.create(this, R.raw.alarm);

        new ANRWatchDog().setANRListener(new ANRWatchDog.ANRListener() {
            @Override
            public void onAppNotResponding(ANRError error) {
                // Handle the error. For example, log it to HockeyApp:
                appException.saveCrashInfo2File(error);
            }
        }).start();

        phoneInfoUtils = new PhoneInfoUtils(this);
        //Android Pad VT VM
        Constant.verName = "APVV"+ APKVersionCodeUtils.getVerName(this);
//        Constant.imei = MobileInfoUtil.getIMEI(this);
//        Constant.devNum = phoneInfoUtils.getNativePhoneNumber();
//        Constant.iccid = phoneInfoUtils.getIccid();


        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

        installDb();
        readDataSDDao = new ReadDataSDDao(this);
        readDataRealtimeSDDao = new ReadDataRealtimeSDDao(this);
        moduleBufSDDao = new ModuleBufSDDao(this);
        appLogSDDao = new AppLogSDDao(this);
        gpsDataSDDao = new GpsDataSDDao(this);
        queryConfigRealtimeSDDao = new QueryConfigRealtimeSDDao(this);
        vtSocketLogSDDao = new VtSocketLogSDDao(this);

        moduleUtils = new ModuleUtils(this);
        timer = new Timer();

        initConfig();

        Constant.IS_DEBUGGABLE = AppStateUtils.isDebuggable(this);
//        Constant.IS_SAVE_MODULE_LOG = AppStateUtils.isDebuggable(this);
//        Constant.IS_SAVE_SOCKET_LOG = AppStateUtils.isDebuggable(this);

        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void initConfig(){
        try {
            String imei = null;
            do{
                imei = MobileInfoUtil.getIMEI(this);
                Log.d(TAG, "imei = " + imei);
                SystemClock.sleep(10);
            } while (TextUtils.isEmpty(imei) || !TextUtils.isDigitsOnly(imei));
            Log.d(TAG, "imei = " + imei);

            Constant.imei = imei;
//            Constant.phoneNumber = phoneInfoUtils.getNativePhoneNumber();
            String iccid = phoneInfoUtils.getIccid();
            if(!StringUtils.isBlank(iccid)) {
                Constant.iccid = iccid.toUpperCase();
            }

            QueryConfigRealtime queryConfigRealtime = queryConfigRealtimeSDDao.queryLast();
            if(queryConfigRealtime == null){
                queryConfigRealtime = new QueryConfigRealtime();
                queryConfigRealtime.setImei(imei);
                queryConfigRealtime.setDevServerIp(ConnectUtils.HOST);
                queryConfigRealtime.setDevServerPort(ConnectUtils.PORT);
                queryConfigRealtime.setIsSetDataBeginTimeByBoot(false);
                queryConfigRealtime.setAlarmFlag(true);
                queryConfigRealtimeSDDao.save(queryConfigRealtime);

            } else {

                ConnectUtils.HOST = queryConfigRealtime.getDevServerIp();
                ConnectUtils.PORT = queryConfigRealtime.getDevServerPort();
                String gwId = queryConfigRealtime.getGwId();
                if(!TextUtils.isEmpty(gwId)){
                    this.gwId = gwId;
                }

                Log.d(TAG, "HOST = " + ConnectUtils.HOST +", PORT = " + ConnectUtils.PORT);

                monitorState = queryConfigRealtime.getMonitorState();
                beginMonitorTime = queryConfigRealtime.getBeginMonitorTime();
                endMonitorTime = queryConfigRealtime.getEndMonitorTime();
                Constant.devNum = queryConfigRealtime.getDevNum();
                Constant.devName = queryConfigRealtime.getDevName();
                isSetDataBeginTimeByBoot = queryConfigRealtime.isSetDataBeginTimeByBoot();
                alarmFlag = queryConfigRealtime.getAlarmFlag();
                Log.d(TAG, "isSetDataBeginTimeByBoot="+isSetDataBeginTimeByBoot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installDb() {
        devOpenHelper = new DaoMaster.DevOpenHelper(this, Constant.DATABASE_NAME, null);
        //获取可写数据库
        database = devOpenHelper.getWritableDatabase();
        //获取数据库对象
        daoMaster = new DaoMaster(database);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();

        //控制台打印SQL语句日志
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public DaoMaster.DevOpenHelper getDevOpenHelper() {
        return devOpenHelper;
    }

    public void setDevOpenHelper(DaoMaster.DevOpenHelper devOpenHelper) {
        this.devOpenHelper = devOpenHelper;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public void setDaoMaster(DaoMaster daoMaster) {
        this.daoMaster = daoMaster;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
    }
}
