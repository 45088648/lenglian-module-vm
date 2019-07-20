package com.beetech.module.bean;

import com.beetech.module.code.response.QueryConfigResponse;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 本地配置实时数据
 */
@Entity
public class QueryConfigRealtime {

    @Id(autoincrement = true)
    private Long _id;
    private String hardVer; // 硬件版本
    private String softVer; // 软件版本
    private String customer; //客户码
    private int debug; // debug固定值
    private int category; // 分类码
    private int interval; // 时间间隔
    private Date calendar; // 采集时间,BCD码，格式：“16年05月20日17时12分46秒
    private int pattern; //工作模式
    private int bps; // 传输速率
    private int channel; // 频段
    private int ramData; // RAM数据
    private int front; // pflash 循环队列的读指针，最大值是1023
    private int rear; // pflash 循环队列的写指针，最大值是1023
    private int pflashLength; // pflash 循环队列中已存数据的数目，最大值是1023。
    private int sendOk; // 数据包发送成功标识位： 0 = 失败； 1 = 成功； other = 未定义
    private double gwVoltage; //计算公式：U = x*4/1023, 单位：V，其中，x = byte1*256+byte2
    public String gwId; // 网关序列号(小模块网关编号 )
    public String imei;

    private String devServerIp;
    private String devNum;
    private int devServerPort;
    private String devEncryption;
    private Date updateTime;
    private int monitorState;
    private Date beginMonitorTime;
    private Date endMonitorTime;

    //平台参数
    private String devName;
    private String userName; // 设备负责人登陆用户名
    private String devSendCycle; // 采集周期
    private String devAutosend; // 上报周期
    private String tempLower; // 低温阀值
    private String tempHight; // 高温阀值
    private String batteryLower; // 电量阀值
    private String sysDatetime; // 系统时间 yyyyMMddHHmmss

    private String rhLower; // 低湿度阀值
    private String rhHight; // 高湿度阀值
    private String tempAlarmFlag; // 温度是否报警
    private String rhAlarmFlag; // 湿度是否报警
    private String batteryAlarmFlag; // 电量是否报警
    private String extPowerAlarmFlag; // 外接电源是否报警
    private String unqualifyRecordFlag; // 温度超温后是否1分钟记录一次
    private String nextUpdateFlag; // 下次是否自动升级0:否,1:是,2:强制升级

    private String updateUrl; // 升级地址
    private String destination; // 送至目的地
    private String orderNo; // 单号
    private String receiver; // 收件人
    private String company; // 公司
    private String devTypeFlag; // 设备类型 0温度 |1温湿度
    private String alarmInterval; //报警时间间隔： -1 不限制
    private String equipType;//设备型号

    public QueryConfigRealtime(){}

    public void update(QueryConfigResponse queryConfigResponse) {
        this.hardVer = queryConfigResponse.getHardVer();
        this.softVer = queryConfigResponse.getSoftVer();
        this.customer = queryConfigResponse.getCustomer();
        this.debug = queryConfigResponse.getDebug();
        this.category = queryConfigResponse.getCategory();
        this.interval = queryConfigResponse.getInterval();
        this.calendar = queryConfigResponse.getCalendar();
        this.pattern = queryConfigResponse.getPattern();
        this.bps = queryConfigResponse.getBps();
        this.channel = queryConfigResponse.getChannel();
        this.ramData = queryConfigResponse.getRamData();
        this.front = queryConfigResponse.getFront();
        this.rear = queryConfigResponse.getRear();
        this.pflashLength = queryConfigResponse.getPflashLength();
        this.sendOk = queryConfigResponse.getSendOk();
        this.gwVoltage = queryConfigResponse.getGwVoltage();
        this.gwId = queryConfigResponse.getGwId();
    }

    @Generated(hash = 1696840765)
    public QueryConfigRealtime(Long _id, String hardVer, String softVer,
            String customer, int debug, int category, int interval, Date calendar,
            int pattern, int bps, int channel, int ramData, int front, int rear,
            int pflashLength, int sendOk, double gwVoltage, String gwId,
            String imei, String devServerIp, String devNum, int devServerPort,
            String devEncryption, Date updateTime, int monitorState,
            Date beginMonitorTime, Date endMonitorTime, String devName,
            String userName, String devSendCycle, String devAutosend,
            String tempLower, String tempHight, String batteryLower,
            String sysDatetime, String rhLower, String rhHight,
            String tempAlarmFlag, String rhAlarmFlag, String batteryAlarmFlag,
            String extPowerAlarmFlag, String unqualifyRecordFlag,
            String nextUpdateFlag, String updateUrl, String destination,
            String orderNo, String receiver, String company, String devTypeFlag,
            String alarmInterval, String equipType) {
        this._id = _id;
        this.hardVer = hardVer;
        this.softVer = softVer;
        this.customer = customer;
        this.debug = debug;
        this.category = category;
        this.interval = interval;
        this.calendar = calendar;
        this.pattern = pattern;
        this.bps = bps;
        this.channel = channel;
        this.ramData = ramData;
        this.front = front;
        this.rear = rear;
        this.pflashLength = pflashLength;
        this.sendOk = sendOk;
        this.gwVoltage = gwVoltage;
        this.gwId = gwId;
        this.imei = imei;
        this.devServerIp = devServerIp;
        this.devNum = devNum;
        this.devServerPort = devServerPort;
        this.devEncryption = devEncryption;
        this.updateTime = updateTime;
        this.monitorState = monitorState;
        this.beginMonitorTime = beginMonitorTime;
        this.endMonitorTime = endMonitorTime;
        this.devName = devName;
        this.userName = userName;
        this.devSendCycle = devSendCycle;
        this.devAutosend = devAutosend;
        this.tempLower = tempLower;
        this.tempHight = tempHight;
        this.batteryLower = batteryLower;
        this.sysDatetime = sysDatetime;
        this.rhLower = rhLower;
        this.rhHight = rhHight;
        this.tempAlarmFlag = tempAlarmFlag;
        this.rhAlarmFlag = rhAlarmFlag;
        this.batteryAlarmFlag = batteryAlarmFlag;
        this.extPowerAlarmFlag = extPowerAlarmFlag;
        this.unqualifyRecordFlag = unqualifyRecordFlag;
        this.nextUpdateFlag = nextUpdateFlag;
        this.updateUrl = updateUrl;
        this.destination = destination;
        this.orderNo = orderNo;
        this.receiver = receiver;
        this.company = company;
        this.devTypeFlag = devTypeFlag;
        this.alarmInterval = alarmInterval;
        this.equipType = equipType;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getHardVer() {
        return hardVer;
    }


    public void setHardVer(String hardVer) {
        this.hardVer = hardVer;
    }


    public String getSoftVer() {
        return softVer;
    }


    public void setSoftVer(String softVer) {
        this.softVer = softVer;
    }


    public String getCustomer() {
        return customer;
    }


    public void setCustomer(String customer) {
        this.customer = customer;
    }


    public int getDebug() {
        return debug;
    }


    public void setDebug(int debug) {
        this.debug = debug;
    }


    public int getCategory() {
        return category;
    }


    public void setCategory(int category) {
        this.category = category;
    }


    public int getInterval() {
        return interval;
    }


    public void setInterval(int interval) {
        this.interval = interval;
    }


    public Date getCalendar() {
        return calendar;
    }


    public void setCalendar(Date calendar) {
        this.calendar = calendar;
    }


    public int getPattern() {
        return pattern;
    }


    public void setPattern(int pattern) {
        this.pattern = pattern;
    }


    public int getBps() {
        return bps;
    }


    public void setBps(int bps) {
        this.bps = bps;
    }


    public int getChannel() {
        return channel;
    }


    public void setChannel(int channel) {
        this.channel = channel;
    }


    public int getRamData() {
        return ramData;
    }


    public void setRamData(int ramData) {
        this.ramData = ramData;
    }


    public int getFront() {
        return front;
    }


    public void setFront(int front) {
        this.front = front;
    }


    public int getRear() {
        return rear;
    }


    public void setRear(int rear) {
        this.rear = rear;
    }


    public int getPflashLength() {
        return pflashLength;
    }


    public void setPflashLength(int pflashLength) {
        this.pflashLength = pflashLength;
    }


    public int getSendOk() {
        return sendOk;
    }


    public void setSendOk(int sendOk) {
        this.sendOk = sendOk;
    }


    public double getGwVoltage() {
        return gwVoltage;
    }


    public void setGwVoltage(double gwVoltage) {
        this.gwVoltage = gwVoltage;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDevServerIp() {
        return devServerIp;
    }

    public void setDevServerIp(String devServerIp) {
        this.devServerIp = devServerIp;
    }

    public String getDevNum() {
        return devNum;
    }

    public void setDevNum(String devNum) {
        this.devNum = devNum;
    }

    public int getDevServerPort() {
        return devServerPort;
    }

    public void setDevServerPort(int devServerPort) {
        this.devServerPort = devServerPort;
    }

    public String getDevEncryption() {
        return devEncryption;
    }

    public void setDevEncryption(String devEncryption) {
        this.devEncryption = devEncryption;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getMonitorState() {
        return monitorState;
    }

    public void setMonitorState(int monitorState) {
        this.monitorState = monitorState;
    }

    public Date getBeginMonitorTime() {
        return beginMonitorTime;
    }

    public void setBeginMonitorTime(Date beginMonitorTime) {
        this.beginMonitorTime = beginMonitorTime;
    }

    public Date getEndMonitorTime() {
        return endMonitorTime;
    }

    public void setEndMonitorTime(Date endMonitorTime) {
        this.endMonitorTime = endMonitorTime;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDevSendCycle() {
        return devSendCycle;
    }

    public void setDevSendCycle(String devSendCycle) {
        this.devSendCycle = devSendCycle;
    }

    public String getDevAutosend() {
        return devAutosend;
    }

    public void setDevAutosend(String devAutosend) {
        this.devAutosend = devAutosend;
    }

    public String getTempLower() {
        return tempLower;
    }

    public void setTempLower(String tempLower) {
        this.tempLower = tempLower;
    }

    public String getTempHight() {
        return tempHight;
    }

    public void setTempHight(String tempHight) {
        this.tempHight = tempHight;
    }

    public String getBatteryLower() {
        return batteryLower;
    }

    public void setBatteryLower(String batteryLower) {
        this.batteryLower = batteryLower;
    }

    public String getSysDatetime() {
        return sysDatetime;
    }

    public void setSysDatetime(String sysDatetime) {
        this.sysDatetime = sysDatetime;
    }

    public String getRhLower() {
        return rhLower;
    }

    public void setRhLower(String rhLower) {
        this.rhLower = rhLower;
    }

    public String getRhHight() {
        return rhHight;
    }

    public void setRhHight(String rhHight) {
        this.rhHight = rhHight;
    }

    public String getTempAlarmFlag() {
        return tempAlarmFlag;
    }

    public void setTempAlarmFlag(String tempAlarmFlag) {
        this.tempAlarmFlag = tempAlarmFlag;
    }

    public String getRhAlarmFlag() {
        return rhAlarmFlag;
    }

    public void setRhAlarmFlag(String rhAlarmFlag) {
        this.rhAlarmFlag = rhAlarmFlag;
    }

    public String getBatteryAlarmFlag() {
        return batteryAlarmFlag;
    }

    public void setBatteryAlarmFlag(String batteryAlarmFlag) {
        this.batteryAlarmFlag = batteryAlarmFlag;
    }

    public String getExtPowerAlarmFlag() {
        return extPowerAlarmFlag;
    }

    public void setExtPowerAlarmFlag(String extPowerAlarmFlag) {
        this.extPowerAlarmFlag = extPowerAlarmFlag;
    }

    public String getUnqualifyRecordFlag() {
        return unqualifyRecordFlag;
    }

    public void setUnqualifyRecordFlag(String unqualifyRecordFlag) {
        this.unqualifyRecordFlag = unqualifyRecordFlag;
    }

    public String getNextUpdateFlag() {
        return nextUpdateFlag;
    }

    public void setNextUpdateFlag(String nextUpdateFlag) {
        this.nextUpdateFlag = nextUpdateFlag;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDevTypeFlag() {
        return devTypeFlag;
    }

    public void setDevTypeFlag(String devTypeFlag) {
        this.devTypeFlag = devTypeFlag;
    }

    public String getAlarmInterval() {
        return alarmInterval;
    }

    public void setAlarmInterval(String alarmInterval) {
        this.alarmInterval = alarmInterval;
    }

    public String getEquipType() {
        return equipType;
    }

    public void setEquipType(String equipType) {
        this.equipType = equipType;
    }
}
