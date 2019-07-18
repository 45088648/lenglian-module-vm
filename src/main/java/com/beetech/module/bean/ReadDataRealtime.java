package com.beetech.module.bean;

import com.beetech.module.code.response.ReadDataResponse;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * 传感器实时数据
 */
@Entity(indexes = {
        @Index(value = "sensorId")
})
public class ReadDataRealtime {

    @Id(autoincrement = true)
    private Long _id;

    public int error; // 0 = 收到数据；1 = 数据错误，需要重发；other = 未定义；
    private int gwType; //网关类型, 网关设备类型
    public int serialNo; // 确认序列号：每次传输，此序列号递增，最大65535，达到最大值后从1开始
    private Date gwTime; // BCD码，格式：“年 月 日 时 分 秒”
    private double gwVoltage; //GW电压， U = (MSB*256+LSB)。计算方法：电池电压 = U/1000，单位：V
    private String sensorId; //传感器ID, 传感器编号
    private int ssStatus; // 0 = 正常数据；1 = 预警数据；2 = 警告数据；other = 未定义；
    private int ssFun; // SS功能, 固定值
    private int ssType; //SS类型, 51/5D: M1/M1_Beetech
    private int protocolVer; // 协议版本
    private int loadLen; //负载长度, 固定值
    private int type1; //类型, 固定值
    private int ssSerialNo; // SS序列号,无线温湿度记录仪上传数据序列号,每次传输+1。
    private int type2; //类型, 固定值
    private double chipTemp; // 片内温度, 有符号整形数，表示CC1310内部温度，单位：℃。
    private int type3;  //类型, 固定值
    private double ssVoltage; // SS电压, U= (MSB*256+LSB)/1000，单位：V。
    private int type4;  //类型, 固定值
    private Date sensorDataTime; // 本条数据的采集时间，BCD码，格式：“年 月 日 时 分 秒”。
    private int type5;  //类型, 固定值
    private Date ssTransfTime; // 本条数据传输到网关的时间，BCD码，格式：“年 月 日 时 分 秒”。
    private int type6;  //类型, 固定值
    private int rssi; // 有符号整数，转为十进制后减去256，单位：dBm
    private int type7;  //类型, 固定值
    private double temp; // 有符号数，X = MSB*256+LSB，当X>=0x8000时，T = (X-65536)/100，单位：℃；当X<0x8000时，T = X/100，单位：℃；
    private int type8;  //类型, 固定值
    private double rh; // 湿度, T = (MSB*256+LSB)/100，单位：%；
    private int isNewFlag; // L/H，表明Sensor目前传输的数据是否是最新的；0 = 最新数据；1 = 历史数据；other = 未定义；
    private int waitSentSize1; // 待发1, Sensor RAM队列中待发数据的数量为26条。
    private int waitSentSize2; // 待发2, Sensor Flash队列中待发数据的数量为0条。
    private int errorcode; // Errorcode, 记录flash发送错误的次数
    private Date inputTime;
    private Date updateTime;
    //阈值 =========begin, 从t平台获取

    private String devName;
    private String devGroupName;
    private double tempLower;
    private double tempHight;

    private double rhLower;
    private double rhHight;

    private double voltageLower;
    private int devSendCycle;
    private int alarmFlag; // 报警标志
    //阈值  =========end

    public ReadDataRealtime(){}


    @Generated(hash = 325781452)
    public ReadDataRealtime(Long _id, int error, int gwType, int serialNo, Date gwTime, double gwVoltage,
            String sensorId, int ssStatus, int ssFun, int ssType, int protocolVer, int loadLen, int type1,
            int ssSerialNo, int type2, double chipTemp, int type3, double ssVoltage, int type4,
            Date sensorDataTime, int type5, Date ssTransfTime, int type6, int rssi, int type7, double temp,
            int type8, double rh, int isNewFlag, int waitSentSize1, int waitSentSize2, int errorcode,
            Date inputTime, Date updateTime, String devName, String devGroupName, double tempLower,
            double tempHight, double rhLower, double rhHight, double voltageLower, int devSendCycle,
            int alarmFlag) {
        this._id = _id;
        this.error = error;
        this.gwType = gwType;
        this.serialNo = serialNo;
        this.gwTime = gwTime;
        this.gwVoltage = gwVoltage;
        this.sensorId = sensorId;
        this.ssStatus = ssStatus;
        this.ssFun = ssFun;
        this.ssType = ssType;
        this.protocolVer = protocolVer;
        this.loadLen = loadLen;
        this.type1 = type1;
        this.ssSerialNo = ssSerialNo;
        this.type2 = type2;
        this.chipTemp = chipTemp;
        this.type3 = type3;
        this.ssVoltage = ssVoltage;
        this.type4 = type4;
        this.sensorDataTime = sensorDataTime;
        this.type5 = type5;
        this.ssTransfTime = ssTransfTime;
        this.type6 = type6;
        this.rssi = rssi;
        this.type7 = type7;
        this.temp = temp;
        this.type8 = type8;
        this.rh = rh;
        this.isNewFlag = isNewFlag;
        this.waitSentSize1 = waitSentSize1;
        this.waitSentSize2 = waitSentSize2;
        this.errorcode = errorcode;
        this.inputTime = inputTime;
        this.updateTime = updateTime;
        this.devName = devName;
        this.devGroupName = devGroupName;
        this.tempLower = tempLower;
        this.tempHight = tempHight;
        this.rhLower = rhLower;
        this.rhHight = rhHight;
        this.voltageLower = voltageLower;
        this.devSendCycle = devSendCycle;
        this.alarmFlag = alarmFlag;
    }

    public void update(ReadDataResponse readDataResponse) {
        this.error = readDataResponse.getError();
        this.gwType = readDataResponse.getGwType();
        this.serialNo = readDataResponse.getSerialNo();
        this.gwTime = readDataResponse.getGwTime();
        this.gwVoltage = readDataResponse.getGwVoltage();
        this.sensorId = readDataResponse.getSensorId();
        this.ssStatus = readDataResponse.getSsStatus();
        this.ssFun = readDataResponse.getSsFun();
        this.ssType = readDataResponse.getSsType();
        this.protocolVer = readDataResponse.getProtocolVer();
        this.loadLen = readDataResponse.getLoadLen();
        this.type1 = readDataResponse.getType1();
        this.ssSerialNo = readDataResponse.getSsSerialNo();
        this.type2 = readDataResponse.getType2();
        this.chipTemp = readDataResponse.getChipTemp();
        this.type3 = readDataResponse.getType3();
        this.ssVoltage = readDataResponse.getSsVoltage();
        this.type4 = readDataResponse.getType4();
        this.sensorDataTime = readDataResponse.getSensorDataTime();
        this.type5 = readDataResponse.getType5();
        this.ssTransfTime = readDataResponse.getSsTransfTime();
        this.type6 = readDataResponse.getType6();
        this.rssi = readDataResponse.getRssi();
        this.type7 = readDataResponse.getType7();
        this.temp = readDataResponse.getTemp();
        this.type8 = readDataResponse.getType8();
        this.rh = readDataResponse.getRh();
        this.isNewFlag = readDataResponse.getIsNewFlag();
        this.waitSentSize1 = readDataResponse.getWaitSentSize1();
        this.waitSentSize2 = readDataResponse.getWaitSentSize2();
        this.errorcode = readDataResponse.getErrorcode();
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getGwType() {
        return gwType;
    }

    public void setGwType(int gwType) {
        this.gwType = gwType;
    }

    public Date getGwTime() {
        return gwTime;
    }

    public void setGwTime(Date gwTime) {
        this.gwTime = gwTime;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public double getGwVoltage() {
        return gwVoltage;
    }

    public void setGwVoltage(double gwVoltage) {
        this.gwVoltage = gwVoltage;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public int getSsStatus() {
        return ssStatus;
    }

    public void setSsStatus(int ssStatus) {
        this.ssStatus = ssStatus;
    }

    public int getSsFun() {
        return ssFun;
    }

    public void setSsFun(int ssFun) {
        this.ssFun = ssFun;
    }

    public int getSsType() {
        return ssType;
    }

    public void setSsType(int ssType) {
        this.ssType = ssType;
    }

    public int getProtocolVer() {
        return protocolVer;
    }

    public void setProtocolVer(int protocolVer) {
        this.protocolVer = protocolVer;
    }

    public int getLoadLen() {
        return loadLen;
    }

    public void setLoadLen(int loadLen) {
        this.loadLen = loadLen;
    }

    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }

    public int getSsSerialNo() {
        return ssSerialNo;
    }

    public void setSsSerialNo(int ssSerialNo) {
        this.ssSerialNo = ssSerialNo;
    }

    public int getType2() {
        return type2;
    }

    public void setType2(int type2) {
        this.type2 = type2;
    }

    public double getChipTemp() {
        return chipTemp;
    }

    public void setChipTemp(double chipTemp) {
        this.chipTemp = chipTemp;
    }

    public double getSsVoltage() {
        return ssVoltage;
    }

    public void setSsVoltage(double ssVoltage) {
        this.ssVoltage = ssVoltage;
    }

    public int getType3() {
        return type3;
    }

    public void setType3(int type3) {
        this.type3 = type3;
    }

    public Date getSensorDataTime() {
        return sensorDataTime;
    }

    public void setSensorDataTime(Date sensorDataTime) {
        this.sensorDataTime = sensorDataTime;
    }

    public int getType4() {
        return type4;
    }

    public void setType4(int type4) {
        this.type4 = type4;
    }

    public Date getSsTransfTime() {
        return ssTransfTime;
    }

    public void setSsTransfTime(Date ssTransfTime) {
        this.ssTransfTime = ssTransfTime;
    }

    public int getType5() {
        return type5;
    }

    public void setType5(int type5) {
        this.type5 = type5;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getType6() {
        return type6;
    }

    public void setType6(int type6) {
        this.type6 = type6;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getType7() {
        return type7;
    }

    public void setType7(int type7) {
        this.type7 = type7;
    }

    public double getRh() {
        return rh;
    }

    public void setRh(double rh) {
        this.rh = rh;
    }

    public int getType8() {
        return type8;
    }

    public void setType8(int type8) {
        this.type8 = type8;
    }

    public int getIsNewFlag() {
        return isNewFlag;
    }

    public void setIsNewFlag(int isNewFlag) {
        this.isNewFlag = isNewFlag;
    }

    public int getWaitSentSize1() {
        return waitSentSize1;
    }

    public void setWaitSentSize1(int waitSentSize1) {
        this.waitSentSize1 = waitSentSize1;
    }

    public int getWaitSentSize2() {
        return waitSentSize2;
    }

    public void setWaitSentSize2(int waitSentSize2) {
        this.waitSentSize2 = waitSentSize2;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public Date getInputTime() {
        return inputTime;
    }

    public void setInputTime(Date inputTime) {
        this.inputTime = inputTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevGroupName() {
        return devGroupName;
    }

    public void setDevGroupName(String devGroupName) {
        this.devGroupName = devGroupName;
    }

    public double getTempLower() {
        return tempLower;
    }

    public void setTempLower(double tempLower) {
        this.tempLower = tempLower;
    }

    public double getTempHight() {
        return tempHight;
    }

    public void setTempHight(double tempHight) {
        this.tempHight = tempHight;
    }

    public double getRhLower() {
        return rhLower;
    }

    public void setRhLower(double rhLower) {
        this.rhLower = rhLower;
    }

    public double getRhHight() {
        return rhHight;
    }

    public void setRhHight(double rhHight) {
        this.rhHight = rhHight;
    }

    public double getVoltageLower() {
        return voltageLower;
    }

    public void setVoltageLower(double voltageLower) {
        this.voltageLower = voltageLower;
    }

    public int getDevSendCycle() {
        return devSendCycle;
    }

    public void setDevSendCycle(int devSendCycle) {
        this.devSendCycle = devSendCycle;
    }

    public int getAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(int alarmFlag) {
        this.alarmFlag = alarmFlag;
    }
}
