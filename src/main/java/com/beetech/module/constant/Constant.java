package com.beetech.module.constant;

import android.graphics.Color;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Constant {

    public final static String REAL_PACKAGE_NAME = "com.beetech.module";

    public final static int module = 4;
    public final static int baudrate = 115200;
    public final static int beginReadSecond = 35; // 从第几秒开始读数据

    public static boolean isReadNextTime(){
        boolean isReadNextTime = true;
        Calendar cal = Calendar.getInstance();
        int second = cal.get(Calendar.SECOND);
        if(second > 0 && second < Constant.beginReadSecond){ // 0 - beginReadSecond 给模块和标签通信用
            isReadNextTime = false;
        }
        Log.d("Constant", "isReadNextTime: "+isReadNextTime+", second="+second);
        return isReadNextTime;
    }

    /**
     * 定时唤醒的时间间隔
     */
    public final static int ALARM_INTERVAL = 1000 * 60;
    public final static int WAKE_REQUEST_CODE = 6666;
    public final static int GRAY_SERVICE_ID = -1001;


    // 监测点背景设
    public static final int SENSOR_COLOR = Color.parseColor("#0673B4");
    public static final int SENSOR_COLOR1 = Color.parseColor("#DE5F50");
    public static final int DEFAULT_COLOR = Color.parseColor("#DFDFDF");
    public static final int DEFAULT_DARKEN_COLOR = Color.parseColor("#DDDDDD");
    public static final int COLOR_BLUE = Color.parseColor("#33B5E5");
    public static final int COLOR_VIOLET = Color.parseColor("#AA66CC");
    public static final int COLOR_GREEN = Color.parseColor("#99CC00");
    public static final int COLOR_ORANGE = Color.parseColor("#FFBB33");
    public static final int COLOR_RED = Color.parseColor("#FF4444");
    public static final int COLOR_WHITE = Color.parseColor("#FFFFFF");
    public static final int COLOR_BLACK = Color.parseColor("#000000");
//    public static final int[] COLORS = new int[]{SENSOR_COLOR, SENSOR_COLOR1, COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, COLOR_ORANGE, COLOR_RED};
    public static final int[] COLORS = new int[]{COLOR_BLUE, COLOR_BLUE, COLOR_BLUE, COLOR_BLUE, COLOR_BLUE, COLOR_BLUE, COLOR_BLUE};
    public static int RED_THEME = Color.parseColor("#eb2127");
    public static final String PACKAGE_NAME = "";

    public static String verName;
    public static String imei;
    public static String devNum;
    public static String devName;
    public static String devEncryption;
    public static String iccid;
    public static String phoneNumber;
    public static String DATABASE_NAME = "module.db";

    public static AtomicLong NUM_RECEIVE = new AtomicLong();
    public static AtomicLong NUM = new AtomicLong();
    public static AtomicInteger readFlag = new AtomicInteger();
    public static long moduleReceiveTimeOutForReInit = 1000*60*30; // 接收网关模块数据超时，模块重新上电初始化依据
    public static long readDataResponseTimeOut = 1000*5; // 接收网关模块温度数据超时

    //记录日志标志位
    public static boolean IS_SAVE_MODULE_LOG = false; //是否记录串口日志
    public static boolean IS_SAVE_SOCKET_LOG = false; //是否记录SOCKET日志
    public static boolean IS_UP_MODULE_LOG = false; //是否上传串口日志
    public static boolean IS_UP_APP_LOG = true; //是否上传APP日志
    public static boolean IS_DEBUGGABLE = false; //是否调试模式
    public static boolean IS_TTSS_TOAST = false; //百度语音是否toast
    public static boolean IS_READ_NEXT = true; //是否读取下一条数据

    public final static String className_GPSService = "com.beetech.module.service.GPSService";
    public final static String className_moduleService = "com.beetech.module.service.ModuleService";
    public final static String className_guardService = "com.beetech.module.service.GuardService";
    public final static String className_screenCheckService = "com.beetech.module.service.ScreenCheckService";

//    public final static String[] printTimeIntvalItems = { "5分钟", "4分钟", "3分钟", "2分钟", "1分钟"};
//    public final static String[] colSizeSpinItems = { "两组", "一组", "三组"};


    public static final String VERSION_DOWNLOAD_URL = "http://app.wendu114.com/apk/v600/t/lenglian-module-vm.apk"; // 下载地址
    public static final String DOWNLOAD_NAME = "lenglian-module-vm.apk"; // 下载存储的文件名
}
