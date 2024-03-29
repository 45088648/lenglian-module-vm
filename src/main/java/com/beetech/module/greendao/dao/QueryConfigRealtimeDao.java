package com.beetech.module.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.beetech.module.bean.QueryConfigRealtime;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "QUERY_CONFIG_REALTIME".
*/
public class QueryConfigRealtimeDao extends AbstractDao<QueryConfigRealtime, Long> {

    public static final String TABLENAME = "QUERY_CONFIG_REALTIME";

    /**
     * Properties of entity QueryConfigRealtime.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property HardVer = new Property(1, String.class, "hardVer", false, "HARD_VER");
        public final static Property SoftVer = new Property(2, String.class, "softVer", false, "SOFT_VER");
        public final static Property Customer = new Property(3, String.class, "customer", false, "CUSTOMER");
        public final static Property Debug = new Property(4, int.class, "debug", false, "DEBUG");
        public final static Property Category = new Property(5, int.class, "category", false, "CATEGORY");
        public final static Property Interval = new Property(6, int.class, "interval", false, "INTERVAL");
        public final static Property Calendar = new Property(7, java.util.Date.class, "calendar", false, "CALENDAR");
        public final static Property Pattern = new Property(8, int.class, "pattern", false, "PATTERN");
        public final static Property Bps = new Property(9, int.class, "bps", false, "BPS");
        public final static Property Channel = new Property(10, int.class, "channel", false, "CHANNEL");
        public final static Property TxPower = new Property(11, int.class, "txPower", false, "TX_POWER");
        public final static Property ForwardFlag = new Property(12, int.class, "forwardFlag", false, "FORWARD_FLAG");
        public final static Property RamData = new Property(13, int.class, "ramData", false, "RAM_DATA");
        public final static Property Front = new Property(14, int.class, "front", false, "FRONT");
        public final static Property Rear = new Property(15, int.class, "rear", false, "REAR");
        public final static Property PflashLength = new Property(16, int.class, "pflashLength", false, "PFLASH_LENGTH");
        public final static Property SendOk = new Property(17, int.class, "sendOk", false, "SEND_OK");
        public final static Property GwVoltage = new Property(18, double.class, "gwVoltage", false, "GW_VOLTAGE");
        public final static Property BindCount = new Property(19, int.class, "bindCount", false, "BIND_COUNT");
        public final static Property GwId = new Property(20, String.class, "gwId", false, "GW_ID");
        public final static Property Imei = new Property(21, String.class, "imei", false, "IMEI");
        public final static Property DevServerIp = new Property(22, String.class, "devServerIp", false, "DEV_SERVER_IP");
        public final static Property DevNum = new Property(23, String.class, "devNum", false, "DEV_NUM");
        public final static Property DevServerPort = new Property(24, int.class, "devServerPort", false, "DEV_SERVER_PORT");
        public final static Property DevEncryption = new Property(25, String.class, "devEncryption", false, "DEV_ENCRYPTION");
        public final static Property UpdateTime = new Property(26, java.util.Date.class, "updateTime", false, "UPDATE_TIME");
        public final static Property MonitorState = new Property(27, int.class, "monitorState", false, "MONITOR_STATE");
        public final static Property BeginMonitorTime = new Property(28, java.util.Date.class, "beginMonitorTime", false, "BEGIN_MONITOR_TIME");
        public final static Property EndMonitorTime = new Property(29, java.util.Date.class, "endMonitorTime", false, "END_MONITOR_TIME");
        public final static Property DevName = new Property(30, String.class, "devName", false, "DEV_NAME");
        public final static Property UserName = new Property(31, String.class, "userName", false, "USER_NAME");
        public final static Property DevSendCycle = new Property(32, String.class, "devSendCycle", false, "DEV_SEND_CYCLE");
        public final static Property DevAutosend = new Property(33, String.class, "devAutosend", false, "DEV_AUTOSEND");
        public final static Property TempLower = new Property(34, String.class, "tempLower", false, "TEMP_LOWER");
        public final static Property TempHight = new Property(35, String.class, "tempHight", false, "TEMP_HIGHT");
        public final static Property BatteryLower = new Property(36, String.class, "batteryLower", false, "BATTERY_LOWER");
        public final static Property SysDatetime = new Property(37, String.class, "sysDatetime", false, "SYS_DATETIME");
        public final static Property RhLower = new Property(38, String.class, "rhLower", false, "RH_LOWER");
        public final static Property RhHight = new Property(39, String.class, "rhHight", false, "RH_HIGHT");
        public final static Property TempAlarmFlag = new Property(40, String.class, "tempAlarmFlag", false, "TEMP_ALARM_FLAG");
        public final static Property RhAlarmFlag = new Property(41, String.class, "rhAlarmFlag", false, "RH_ALARM_FLAG");
        public final static Property BatteryAlarmFlag = new Property(42, String.class, "batteryAlarmFlag", false, "BATTERY_ALARM_FLAG");
        public final static Property ExtPowerAlarmFlag = new Property(43, String.class, "extPowerAlarmFlag", false, "EXT_POWER_ALARM_FLAG");
        public final static Property UnqualifyRecordFlag = new Property(44, String.class, "unqualifyRecordFlag", false, "UNQUALIFY_RECORD_FLAG");
        public final static Property NextUpdateFlag = new Property(45, String.class, "nextUpdateFlag", false, "NEXT_UPDATE_FLAG");
        public final static Property UpdateUrl = new Property(46, String.class, "updateUrl", false, "UPDATE_URL");
        public final static Property Destination = new Property(47, String.class, "destination", false, "DESTINATION");
        public final static Property OrderNo = new Property(48, String.class, "orderNo", false, "ORDER_NO");
        public final static Property Receiver = new Property(49, String.class, "receiver", false, "RECEIVER");
        public final static Property Company = new Property(50, String.class, "company", false, "COMPANY");
        public final static Property DevTypeFlag = new Property(51, String.class, "devTypeFlag", false, "DEV_TYPE_FLAG");
        public final static Property AlarmInterval = new Property(52, String.class, "alarmInterval", false, "ALARM_INTERVAL");
        public final static Property EquipType = new Property(53, String.class, "equipType", false, "EQUIP_TYPE");
        public final static Property IsSetDataBeginTimeByBoot = new Property(54, boolean.class, "isSetDataBeginTimeByBoot", false, "IS_SET_DATA_BEGIN_TIME_BY_BOOT");
        public final static Property AlarmFlag = new Property(55, boolean.class, "alarmFlag", false, "ALARM_FLAG");
    }


    public QueryConfigRealtimeDao(DaoConfig config) {
        super(config);
    }
    
    public QueryConfigRealtimeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"QUERY_CONFIG_REALTIME\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"HARD_VER\" TEXT," + // 1: hardVer
                "\"SOFT_VER\" TEXT," + // 2: softVer
                "\"CUSTOMER\" TEXT," + // 3: customer
                "\"DEBUG\" INTEGER NOT NULL ," + // 4: debug
                "\"CATEGORY\" INTEGER NOT NULL ," + // 5: category
                "\"INTERVAL\" INTEGER NOT NULL ," + // 6: interval
                "\"CALENDAR\" INTEGER," + // 7: calendar
                "\"PATTERN\" INTEGER NOT NULL ," + // 8: pattern
                "\"BPS\" INTEGER NOT NULL ," + // 9: bps
                "\"CHANNEL\" INTEGER NOT NULL ," + // 10: channel
                "\"TX_POWER\" INTEGER NOT NULL ," + // 11: txPower
                "\"FORWARD_FLAG\" INTEGER NOT NULL ," + // 12: forwardFlag
                "\"RAM_DATA\" INTEGER NOT NULL ," + // 13: ramData
                "\"FRONT\" INTEGER NOT NULL ," + // 14: front
                "\"REAR\" INTEGER NOT NULL ," + // 15: rear
                "\"PFLASH_LENGTH\" INTEGER NOT NULL ," + // 16: pflashLength
                "\"SEND_OK\" INTEGER NOT NULL ," + // 17: sendOk
                "\"GW_VOLTAGE\" REAL NOT NULL ," + // 18: gwVoltage
                "\"BIND_COUNT\" INTEGER NOT NULL ," + // 19: bindCount
                "\"GW_ID\" TEXT," + // 20: gwId
                "\"IMEI\" TEXT," + // 21: imei
                "\"DEV_SERVER_IP\" TEXT," + // 22: devServerIp
                "\"DEV_NUM\" TEXT," + // 23: devNum
                "\"DEV_SERVER_PORT\" INTEGER NOT NULL ," + // 24: devServerPort
                "\"DEV_ENCRYPTION\" TEXT," + // 25: devEncryption
                "\"UPDATE_TIME\" INTEGER," + // 26: updateTime
                "\"MONITOR_STATE\" INTEGER NOT NULL ," + // 27: monitorState
                "\"BEGIN_MONITOR_TIME\" INTEGER," + // 28: beginMonitorTime
                "\"END_MONITOR_TIME\" INTEGER," + // 29: endMonitorTime
                "\"DEV_NAME\" TEXT," + // 30: devName
                "\"USER_NAME\" TEXT," + // 31: userName
                "\"DEV_SEND_CYCLE\" TEXT," + // 32: devSendCycle
                "\"DEV_AUTOSEND\" TEXT," + // 33: devAutosend
                "\"TEMP_LOWER\" TEXT," + // 34: tempLower
                "\"TEMP_HIGHT\" TEXT," + // 35: tempHight
                "\"BATTERY_LOWER\" TEXT," + // 36: batteryLower
                "\"SYS_DATETIME\" TEXT," + // 37: sysDatetime
                "\"RH_LOWER\" TEXT," + // 38: rhLower
                "\"RH_HIGHT\" TEXT," + // 39: rhHight
                "\"TEMP_ALARM_FLAG\" TEXT," + // 40: tempAlarmFlag
                "\"RH_ALARM_FLAG\" TEXT," + // 41: rhAlarmFlag
                "\"BATTERY_ALARM_FLAG\" TEXT," + // 42: batteryAlarmFlag
                "\"EXT_POWER_ALARM_FLAG\" TEXT," + // 43: extPowerAlarmFlag
                "\"UNQUALIFY_RECORD_FLAG\" TEXT," + // 44: unqualifyRecordFlag
                "\"NEXT_UPDATE_FLAG\" TEXT," + // 45: nextUpdateFlag
                "\"UPDATE_URL\" TEXT," + // 46: updateUrl
                "\"DESTINATION\" TEXT," + // 47: destination
                "\"ORDER_NO\" TEXT," + // 48: orderNo
                "\"RECEIVER\" TEXT," + // 49: receiver
                "\"COMPANY\" TEXT," + // 50: company
                "\"DEV_TYPE_FLAG\" TEXT," + // 51: devTypeFlag
                "\"ALARM_INTERVAL\" TEXT," + // 52: alarmInterval
                "\"EQUIP_TYPE\" TEXT," + // 53: equipType
                "\"IS_SET_DATA_BEGIN_TIME_BY_BOOT\" INTEGER NOT NULL ," + // 54: isSetDataBeginTimeByBoot
                "\"ALARM_FLAG\" INTEGER NOT NULL );"); // 55: alarmFlag
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"QUERY_CONFIG_REALTIME\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, QueryConfigRealtime entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String hardVer = entity.getHardVer();
        if (hardVer != null) {
            stmt.bindString(2, hardVer);
        }
 
        String softVer = entity.getSoftVer();
        if (softVer != null) {
            stmt.bindString(3, softVer);
        }
 
        String customer = entity.getCustomer();
        if (customer != null) {
            stmt.bindString(4, customer);
        }
        stmt.bindLong(5, entity.getDebug());
        stmt.bindLong(6, entity.getCategory());
        stmt.bindLong(7, entity.getInterval());
 
        java.util.Date calendar = entity.getCalendar();
        if (calendar != null) {
            stmt.bindLong(8, calendar.getTime());
        }
        stmt.bindLong(9, entity.getPattern());
        stmt.bindLong(10, entity.getBps());
        stmt.bindLong(11, entity.getChannel());
        stmt.bindLong(12, entity.getTxPower());
        stmt.bindLong(13, entity.getForwardFlag());
        stmt.bindLong(14, entity.getRamData());
        stmt.bindLong(15, entity.getFront());
        stmt.bindLong(16, entity.getRear());
        stmt.bindLong(17, entity.getPflashLength());
        stmt.bindLong(18, entity.getSendOk());
        stmt.bindDouble(19, entity.getGwVoltage());
        stmt.bindLong(20, entity.getBindCount());
 
        String gwId = entity.getGwId();
        if (gwId != null) {
            stmt.bindString(21, gwId);
        }
 
        String imei = entity.getImei();
        if (imei != null) {
            stmt.bindString(22, imei);
        }
 
        String devServerIp = entity.getDevServerIp();
        if (devServerIp != null) {
            stmt.bindString(23, devServerIp);
        }
 
        String devNum = entity.getDevNum();
        if (devNum != null) {
            stmt.bindString(24, devNum);
        }
        stmt.bindLong(25, entity.getDevServerPort());
 
        String devEncryption = entity.getDevEncryption();
        if (devEncryption != null) {
            stmt.bindString(26, devEncryption);
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(27, updateTime.getTime());
        }
        stmt.bindLong(28, entity.getMonitorState());
 
        java.util.Date beginMonitorTime = entity.getBeginMonitorTime();
        if (beginMonitorTime != null) {
            stmt.bindLong(29, beginMonitorTime.getTime());
        }
 
        java.util.Date endMonitorTime = entity.getEndMonitorTime();
        if (endMonitorTime != null) {
            stmt.bindLong(30, endMonitorTime.getTime());
        }
 
        String devName = entity.getDevName();
        if (devName != null) {
            stmt.bindString(31, devName);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(32, userName);
        }
 
        String devSendCycle = entity.getDevSendCycle();
        if (devSendCycle != null) {
            stmt.bindString(33, devSendCycle);
        }
 
        String devAutosend = entity.getDevAutosend();
        if (devAutosend != null) {
            stmt.bindString(34, devAutosend);
        }
 
        String tempLower = entity.getTempLower();
        if (tempLower != null) {
            stmt.bindString(35, tempLower);
        }
 
        String tempHight = entity.getTempHight();
        if (tempHight != null) {
            stmt.bindString(36, tempHight);
        }
 
        String batteryLower = entity.getBatteryLower();
        if (batteryLower != null) {
            stmt.bindString(37, batteryLower);
        }
 
        String sysDatetime = entity.getSysDatetime();
        if (sysDatetime != null) {
            stmt.bindString(38, sysDatetime);
        }
 
        String rhLower = entity.getRhLower();
        if (rhLower != null) {
            stmt.bindString(39, rhLower);
        }
 
        String rhHight = entity.getRhHight();
        if (rhHight != null) {
            stmt.bindString(40, rhHight);
        }
 
        String tempAlarmFlag = entity.getTempAlarmFlag();
        if (tempAlarmFlag != null) {
            stmt.bindString(41, tempAlarmFlag);
        }
 
        String rhAlarmFlag = entity.getRhAlarmFlag();
        if (rhAlarmFlag != null) {
            stmt.bindString(42, rhAlarmFlag);
        }
 
        String batteryAlarmFlag = entity.getBatteryAlarmFlag();
        if (batteryAlarmFlag != null) {
            stmt.bindString(43, batteryAlarmFlag);
        }
 
        String extPowerAlarmFlag = entity.getExtPowerAlarmFlag();
        if (extPowerAlarmFlag != null) {
            stmt.bindString(44, extPowerAlarmFlag);
        }
 
        String unqualifyRecordFlag = entity.getUnqualifyRecordFlag();
        if (unqualifyRecordFlag != null) {
            stmt.bindString(45, unqualifyRecordFlag);
        }
 
        String nextUpdateFlag = entity.getNextUpdateFlag();
        if (nextUpdateFlag != null) {
            stmt.bindString(46, nextUpdateFlag);
        }
 
        String updateUrl = entity.getUpdateUrl();
        if (updateUrl != null) {
            stmt.bindString(47, updateUrl);
        }
 
        String destination = entity.getDestination();
        if (destination != null) {
            stmt.bindString(48, destination);
        }
 
        String orderNo = entity.getOrderNo();
        if (orderNo != null) {
            stmt.bindString(49, orderNo);
        }
 
        String receiver = entity.getReceiver();
        if (receiver != null) {
            stmt.bindString(50, receiver);
        }
 
        String company = entity.getCompany();
        if (company != null) {
            stmt.bindString(51, company);
        }
 
        String devTypeFlag = entity.getDevTypeFlag();
        if (devTypeFlag != null) {
            stmt.bindString(52, devTypeFlag);
        }
 
        String alarmInterval = entity.getAlarmInterval();
        if (alarmInterval != null) {
            stmt.bindString(53, alarmInterval);
        }
 
        String equipType = entity.getEquipType();
        if (equipType != null) {
            stmt.bindString(54, equipType);
        }
        stmt.bindLong(55, entity.getIsSetDataBeginTimeByBoot() ? 1L: 0L);
        stmt.bindLong(56, entity.getAlarmFlag() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, QueryConfigRealtime entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String hardVer = entity.getHardVer();
        if (hardVer != null) {
            stmt.bindString(2, hardVer);
        }
 
        String softVer = entity.getSoftVer();
        if (softVer != null) {
            stmt.bindString(3, softVer);
        }
 
        String customer = entity.getCustomer();
        if (customer != null) {
            stmt.bindString(4, customer);
        }
        stmt.bindLong(5, entity.getDebug());
        stmt.bindLong(6, entity.getCategory());
        stmt.bindLong(7, entity.getInterval());
 
        java.util.Date calendar = entity.getCalendar();
        if (calendar != null) {
            stmt.bindLong(8, calendar.getTime());
        }
        stmt.bindLong(9, entity.getPattern());
        stmt.bindLong(10, entity.getBps());
        stmt.bindLong(11, entity.getChannel());
        stmt.bindLong(12, entity.getTxPower());
        stmt.bindLong(13, entity.getForwardFlag());
        stmt.bindLong(14, entity.getRamData());
        stmt.bindLong(15, entity.getFront());
        stmt.bindLong(16, entity.getRear());
        stmt.bindLong(17, entity.getPflashLength());
        stmt.bindLong(18, entity.getSendOk());
        stmt.bindDouble(19, entity.getGwVoltage());
        stmt.bindLong(20, entity.getBindCount());
 
        String gwId = entity.getGwId();
        if (gwId != null) {
            stmt.bindString(21, gwId);
        }
 
        String imei = entity.getImei();
        if (imei != null) {
            stmt.bindString(22, imei);
        }
 
        String devServerIp = entity.getDevServerIp();
        if (devServerIp != null) {
            stmt.bindString(23, devServerIp);
        }
 
        String devNum = entity.getDevNum();
        if (devNum != null) {
            stmt.bindString(24, devNum);
        }
        stmt.bindLong(25, entity.getDevServerPort());
 
        String devEncryption = entity.getDevEncryption();
        if (devEncryption != null) {
            stmt.bindString(26, devEncryption);
        }
 
        java.util.Date updateTime = entity.getUpdateTime();
        if (updateTime != null) {
            stmt.bindLong(27, updateTime.getTime());
        }
        stmt.bindLong(28, entity.getMonitorState());
 
        java.util.Date beginMonitorTime = entity.getBeginMonitorTime();
        if (beginMonitorTime != null) {
            stmt.bindLong(29, beginMonitorTime.getTime());
        }
 
        java.util.Date endMonitorTime = entity.getEndMonitorTime();
        if (endMonitorTime != null) {
            stmt.bindLong(30, endMonitorTime.getTime());
        }
 
        String devName = entity.getDevName();
        if (devName != null) {
            stmt.bindString(31, devName);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(32, userName);
        }
 
        String devSendCycle = entity.getDevSendCycle();
        if (devSendCycle != null) {
            stmt.bindString(33, devSendCycle);
        }
 
        String devAutosend = entity.getDevAutosend();
        if (devAutosend != null) {
            stmt.bindString(34, devAutosend);
        }
 
        String tempLower = entity.getTempLower();
        if (tempLower != null) {
            stmt.bindString(35, tempLower);
        }
 
        String tempHight = entity.getTempHight();
        if (tempHight != null) {
            stmt.bindString(36, tempHight);
        }
 
        String batteryLower = entity.getBatteryLower();
        if (batteryLower != null) {
            stmt.bindString(37, batteryLower);
        }
 
        String sysDatetime = entity.getSysDatetime();
        if (sysDatetime != null) {
            stmt.bindString(38, sysDatetime);
        }
 
        String rhLower = entity.getRhLower();
        if (rhLower != null) {
            stmt.bindString(39, rhLower);
        }
 
        String rhHight = entity.getRhHight();
        if (rhHight != null) {
            stmt.bindString(40, rhHight);
        }
 
        String tempAlarmFlag = entity.getTempAlarmFlag();
        if (tempAlarmFlag != null) {
            stmt.bindString(41, tempAlarmFlag);
        }
 
        String rhAlarmFlag = entity.getRhAlarmFlag();
        if (rhAlarmFlag != null) {
            stmt.bindString(42, rhAlarmFlag);
        }
 
        String batteryAlarmFlag = entity.getBatteryAlarmFlag();
        if (batteryAlarmFlag != null) {
            stmt.bindString(43, batteryAlarmFlag);
        }
 
        String extPowerAlarmFlag = entity.getExtPowerAlarmFlag();
        if (extPowerAlarmFlag != null) {
            stmt.bindString(44, extPowerAlarmFlag);
        }
 
        String unqualifyRecordFlag = entity.getUnqualifyRecordFlag();
        if (unqualifyRecordFlag != null) {
            stmt.bindString(45, unqualifyRecordFlag);
        }
 
        String nextUpdateFlag = entity.getNextUpdateFlag();
        if (nextUpdateFlag != null) {
            stmt.bindString(46, nextUpdateFlag);
        }
 
        String updateUrl = entity.getUpdateUrl();
        if (updateUrl != null) {
            stmt.bindString(47, updateUrl);
        }
 
        String destination = entity.getDestination();
        if (destination != null) {
            stmt.bindString(48, destination);
        }
 
        String orderNo = entity.getOrderNo();
        if (orderNo != null) {
            stmt.bindString(49, orderNo);
        }
 
        String receiver = entity.getReceiver();
        if (receiver != null) {
            stmt.bindString(50, receiver);
        }
 
        String company = entity.getCompany();
        if (company != null) {
            stmt.bindString(51, company);
        }
 
        String devTypeFlag = entity.getDevTypeFlag();
        if (devTypeFlag != null) {
            stmt.bindString(52, devTypeFlag);
        }
 
        String alarmInterval = entity.getAlarmInterval();
        if (alarmInterval != null) {
            stmt.bindString(53, alarmInterval);
        }
 
        String equipType = entity.getEquipType();
        if (equipType != null) {
            stmt.bindString(54, equipType);
        }
        stmt.bindLong(55, entity.getIsSetDataBeginTimeByBoot() ? 1L: 0L);
        stmt.bindLong(56, entity.getAlarmFlag() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public QueryConfigRealtime readEntity(Cursor cursor, int offset) {
        QueryConfigRealtime entity = new QueryConfigRealtime( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // hardVer
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // softVer
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // customer
            cursor.getInt(offset + 4), // debug
            cursor.getInt(offset + 5), // category
            cursor.getInt(offset + 6), // interval
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // calendar
            cursor.getInt(offset + 8), // pattern
            cursor.getInt(offset + 9), // bps
            cursor.getInt(offset + 10), // channel
            cursor.getInt(offset + 11), // txPower
            cursor.getInt(offset + 12), // forwardFlag
            cursor.getInt(offset + 13), // ramData
            cursor.getInt(offset + 14), // front
            cursor.getInt(offset + 15), // rear
            cursor.getInt(offset + 16), // pflashLength
            cursor.getInt(offset + 17), // sendOk
            cursor.getDouble(offset + 18), // gwVoltage
            cursor.getInt(offset + 19), // bindCount
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // gwId
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // imei
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // devServerIp
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // devNum
            cursor.getInt(offset + 24), // devServerPort
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // devEncryption
            cursor.isNull(offset + 26) ? null : new java.util.Date(cursor.getLong(offset + 26)), // updateTime
            cursor.getInt(offset + 27), // monitorState
            cursor.isNull(offset + 28) ? null : new java.util.Date(cursor.getLong(offset + 28)), // beginMonitorTime
            cursor.isNull(offset + 29) ? null : new java.util.Date(cursor.getLong(offset + 29)), // endMonitorTime
            cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30), // devName
            cursor.isNull(offset + 31) ? null : cursor.getString(offset + 31), // userName
            cursor.isNull(offset + 32) ? null : cursor.getString(offset + 32), // devSendCycle
            cursor.isNull(offset + 33) ? null : cursor.getString(offset + 33), // devAutosend
            cursor.isNull(offset + 34) ? null : cursor.getString(offset + 34), // tempLower
            cursor.isNull(offset + 35) ? null : cursor.getString(offset + 35), // tempHight
            cursor.isNull(offset + 36) ? null : cursor.getString(offset + 36), // batteryLower
            cursor.isNull(offset + 37) ? null : cursor.getString(offset + 37), // sysDatetime
            cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38), // rhLower
            cursor.isNull(offset + 39) ? null : cursor.getString(offset + 39), // rhHight
            cursor.isNull(offset + 40) ? null : cursor.getString(offset + 40), // tempAlarmFlag
            cursor.isNull(offset + 41) ? null : cursor.getString(offset + 41), // rhAlarmFlag
            cursor.isNull(offset + 42) ? null : cursor.getString(offset + 42), // batteryAlarmFlag
            cursor.isNull(offset + 43) ? null : cursor.getString(offset + 43), // extPowerAlarmFlag
            cursor.isNull(offset + 44) ? null : cursor.getString(offset + 44), // unqualifyRecordFlag
            cursor.isNull(offset + 45) ? null : cursor.getString(offset + 45), // nextUpdateFlag
            cursor.isNull(offset + 46) ? null : cursor.getString(offset + 46), // updateUrl
            cursor.isNull(offset + 47) ? null : cursor.getString(offset + 47), // destination
            cursor.isNull(offset + 48) ? null : cursor.getString(offset + 48), // orderNo
            cursor.isNull(offset + 49) ? null : cursor.getString(offset + 49), // receiver
            cursor.isNull(offset + 50) ? null : cursor.getString(offset + 50), // company
            cursor.isNull(offset + 51) ? null : cursor.getString(offset + 51), // devTypeFlag
            cursor.isNull(offset + 52) ? null : cursor.getString(offset + 52), // alarmInterval
            cursor.isNull(offset + 53) ? null : cursor.getString(offset + 53), // equipType
            cursor.getShort(offset + 54) != 0, // isSetDataBeginTimeByBoot
            cursor.getShort(offset + 55) != 0 // alarmFlag
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, QueryConfigRealtime entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setHardVer(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSoftVer(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCustomer(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDebug(cursor.getInt(offset + 4));
        entity.setCategory(cursor.getInt(offset + 5));
        entity.setInterval(cursor.getInt(offset + 6));
        entity.setCalendar(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setPattern(cursor.getInt(offset + 8));
        entity.setBps(cursor.getInt(offset + 9));
        entity.setChannel(cursor.getInt(offset + 10));
        entity.setTxPower(cursor.getInt(offset + 11));
        entity.setForwardFlag(cursor.getInt(offset + 12));
        entity.setRamData(cursor.getInt(offset + 13));
        entity.setFront(cursor.getInt(offset + 14));
        entity.setRear(cursor.getInt(offset + 15));
        entity.setPflashLength(cursor.getInt(offset + 16));
        entity.setSendOk(cursor.getInt(offset + 17));
        entity.setGwVoltage(cursor.getDouble(offset + 18));
        entity.setBindCount(cursor.getInt(offset + 19));
        entity.setGwId(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setImei(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setDevServerIp(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setDevNum(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setDevServerPort(cursor.getInt(offset + 24));
        entity.setDevEncryption(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setUpdateTime(cursor.isNull(offset + 26) ? null : new java.util.Date(cursor.getLong(offset + 26)));
        entity.setMonitorState(cursor.getInt(offset + 27));
        entity.setBeginMonitorTime(cursor.isNull(offset + 28) ? null : new java.util.Date(cursor.getLong(offset + 28)));
        entity.setEndMonitorTime(cursor.isNull(offset + 29) ? null : new java.util.Date(cursor.getLong(offset + 29)));
        entity.setDevName(cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30));
        entity.setUserName(cursor.isNull(offset + 31) ? null : cursor.getString(offset + 31));
        entity.setDevSendCycle(cursor.isNull(offset + 32) ? null : cursor.getString(offset + 32));
        entity.setDevAutosend(cursor.isNull(offset + 33) ? null : cursor.getString(offset + 33));
        entity.setTempLower(cursor.isNull(offset + 34) ? null : cursor.getString(offset + 34));
        entity.setTempHight(cursor.isNull(offset + 35) ? null : cursor.getString(offset + 35));
        entity.setBatteryLower(cursor.isNull(offset + 36) ? null : cursor.getString(offset + 36));
        entity.setSysDatetime(cursor.isNull(offset + 37) ? null : cursor.getString(offset + 37));
        entity.setRhLower(cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38));
        entity.setRhHight(cursor.isNull(offset + 39) ? null : cursor.getString(offset + 39));
        entity.setTempAlarmFlag(cursor.isNull(offset + 40) ? null : cursor.getString(offset + 40));
        entity.setRhAlarmFlag(cursor.isNull(offset + 41) ? null : cursor.getString(offset + 41));
        entity.setBatteryAlarmFlag(cursor.isNull(offset + 42) ? null : cursor.getString(offset + 42));
        entity.setExtPowerAlarmFlag(cursor.isNull(offset + 43) ? null : cursor.getString(offset + 43));
        entity.setUnqualifyRecordFlag(cursor.isNull(offset + 44) ? null : cursor.getString(offset + 44));
        entity.setNextUpdateFlag(cursor.isNull(offset + 45) ? null : cursor.getString(offset + 45));
        entity.setUpdateUrl(cursor.isNull(offset + 46) ? null : cursor.getString(offset + 46));
        entity.setDestination(cursor.isNull(offset + 47) ? null : cursor.getString(offset + 47));
        entity.setOrderNo(cursor.isNull(offset + 48) ? null : cursor.getString(offset + 48));
        entity.setReceiver(cursor.isNull(offset + 49) ? null : cursor.getString(offset + 49));
        entity.setCompany(cursor.isNull(offset + 50) ? null : cursor.getString(offset + 50));
        entity.setDevTypeFlag(cursor.isNull(offset + 51) ? null : cursor.getString(offset + 51));
        entity.setAlarmInterval(cursor.isNull(offset + 52) ? null : cursor.getString(offset + 52));
        entity.setEquipType(cursor.isNull(offset + 53) ? null : cursor.getString(offset + 53));
        entity.setIsSetDataBeginTimeByBoot(cursor.getShort(offset + 54) != 0);
        entity.setAlarmFlag(cursor.getShort(offset + 55) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(QueryConfigRealtime entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(QueryConfigRealtime entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(QueryConfigRealtime entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
