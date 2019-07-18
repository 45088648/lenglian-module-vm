package com.beetech.module.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.client.ConnectUtils;
import com.beetech.module.cockroach.Cockroach;
import com.beetech.module.constant.Constant;
import com.beetech.module.dao.AppLogSDDao;
import com.beetech.module.dao.ModuleBufSDDao;
import com.beetech.module.dao.QueryConfigRealtimeSDDao;
import com.beetech.module.dao.ReadDataRealtimeSDDao;
import com.beetech.module.dao.ReadDataSDDao;
import com.beetech.module.dao.VtSocketLogSDDao;
import com.beetech.module.greendao.dao.DaoMaster;
import com.beetech.module.greendao.dao.DaoSession;
import com.beetech.module.handler.CrashHandler;
import com.beetech.module.thread.ThreadModuleReceive;
import com.beetech.module.thread.ThreadTimeTask;
import com.beetech.module.utils.APKVersionCodeUtils;
import com.beetech.module.utils.AppStateUtils;
import com.beetech.module.utils.MobileInfoUtil;
import com.beetech.module.utils.ModuleUtils;
import com.beetech.module.utils.PhoneInfoUtils;
import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;
import com.rscja.deviceapi.Module;
import org.apache.mina.core.session.IoSession;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.Date;
import com.beetech.module.cockroach.ExceptionHandler;
import java.util.Timer;

public class MyApplication extends Application {
    private final static String TAG = MyApplication.class.getSimpleName();

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
    public String customer;
    public int pattern; //工作模式
    public int bps; // 传输速率
    public int channel; // 频段
    public int serialNo = 0;
    public int readDataResponseError = 0;
    public int readDataResponseWaitSentSize1; // 待发1, Sensor RAM队列中待发数据的数量为26条。
    public int readDataResponseWaitSentSize2; // 待发2, Sensor Flash队列中待发数据的数量为0条。
    public int readDataResponseErrorcode; // Errorcode, 记录flash发送错误的次数
    public long readDataResponseTime;
    public Date setDataBeginTime;
    public Integer frontDeleteResponse;
    public Integer rearDeleteResponse;
    public Integer pflashLengthDeleteResponse;

    public ThreadModuleReceive threadModuleReceive;
    public ThreadTimeTask threadTimeTask;

    public IoSession session;

    //db操作对象
    public DaoMaster.DevOpenHelper devOpenHelper;
    public SQLiteDatabase database;
    public DaoMaster daoMaster;
    public DaoSession daoSession;

    public ReadDataSDDao readDataSDDao;
    public ReadDataRealtimeSDDao readDataRealtimeSDDao;
    public ModuleBufSDDao moduleBufSDDao;
    public AppLogSDDao appLogSDDao;
    public QueryConfigRealtimeSDDao queryConfigRealtimeSDDao;
    public VtSocketLogSDDao vtSocketLogSDDao;

    public CrashHandler appException;
    public PhoneInfoUtils phoneInfoUtils;
    public ModuleUtils moduleUtils;
    public Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        appException = CrashHandler.getInstance();
        appException.init(getApplicationContext());

        new ANRWatchDog().setANRListener(new ANRWatchDog.ANRListener() {
            @Override
            public void onAppNotResponding(ANRError error) {
                // Handle the error. For example, log it to HockeyApp:
                appException.saveCrashInfo2File(error);
            }
        }).start();

        phoneInfoUtils = new PhoneInfoUtils(this);
        //Android Pad VT
        Constant.verName = "APV"+APKVersionCodeUtils.getVerName(this);
        Constant.imei = MobileInfoUtil.getIMEI(this);
        Constant.devNum = phoneInfoUtils.getNativePhoneNumber();
        Constant.iccid = phoneInfoUtils.getIccid();


        installDb();
        readDataSDDao = new ReadDataSDDao(this);
        readDataRealtimeSDDao = new ReadDataRealtimeSDDao(this);
        moduleBufSDDao = new ModuleBufSDDao(this);
        appLogSDDao = new AppLogSDDao(this);
        queryConfigRealtimeSDDao = new QueryConfigRealtimeSDDao(this);
        vtSocketLogSDDao = new VtSocketLogSDDao(this);

        moduleUtils = new ModuleUtils(this);
        timer = new Timer();

        initConfig();
        install();

        Constant.IS_DEBUGGABLE = AppStateUtils.isDebuggable(this);
        Constant.IS_SAVE_MODULE_LOG = AppStateUtils.isDebuggable(this);
        Constant.IS_SAVE_SOCKET_LOG = AppStateUtils.isDebuggable(this);
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
            Constant.phoneNumber = phoneInfoUtils.getNativePhoneNumber();
            Constant.iccid = phoneInfoUtils.getIccid();

            QueryConfigRealtime queryConfigRealtime = queryConfigRealtimeSDDao.queryLast();
            if(queryConfigRealtime == null){
                queryConfigRealtime = new QueryConfigRealtime();
                queryConfigRealtime.setImei(imei);
                queryConfigRealtime.setDevServerIp(ConnectUtils.HOST);
                queryConfigRealtime.setDevServerPort(ConnectUtils.PORT);
                queryConfigRealtimeSDDao.save(queryConfigRealtime);

            } else {

                ConnectUtils.HOST = queryConfigRealtime.getDevServerIp();
                ConnectUtils.PORT = queryConfigRealtime.getDevServerPort();
                String gwId = queryConfigRealtime.getGwId();
                if(!TextUtils.isEmpty(gwId)){
                    this.gwId = gwId;
                }
                customer = queryConfigRealtime.getCustomer();
                pattern = queryConfigRealtime.getPattern();
                bps = queryConfigRealtime.getBps();
                channel = queryConfigRealtime.getChannel();
                Log.d(TAG, "HOST = " + ConnectUtils.HOST +", PORT = " + ConnectUtils.PORT);
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

    private void install() {
        final Thread.UncaughtExceptionHandler sysExcepHandler = Thread.getDefaultUncaughtExceptionHandler();
        Cockroach.install(this.getApplicationContext(), new ExceptionHandler() {
            @Override
            protected void onUncaughtExceptionHappened(Thread thread, Throwable throwable) {
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:" + thread + "<---", throwable);
                appException.saveCrashInfo2File(throwable);
            }

            @Override
            protected void onBandageExceptionHappened(Throwable throwable) {
                throwable.printStackTrace();//打印警告级别log，该throwable可能是最开始的bug导致的，无需关心
            }

            @Override
            protected void onEnterSafeMode() {

            }

            @Override
            protected void onMayBeBlackScreen(Throwable e) {
                Thread thread = Looper.getMainLooper().getThread();
                Log.e("AndroidRuntime", "--->onUncaughtExceptionHappened:" + thread + "<---", e);
                //黑屏时建议直接杀死app
                sysExcepHandler.uncaughtException(thread, new RuntimeException("black screen"));
            }

        });

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
