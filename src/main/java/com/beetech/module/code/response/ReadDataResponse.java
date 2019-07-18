package com.beetech.module.code.response;

import com.beetech.module.code.BaseResponse;
import com.beetech.module.utils.ByteUtilities;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
		@Index(value = "sensorId"),
		@Index(value = "sensorDataTime"),
		@Index(value = "sendFlag"),
		@Index(value = "responseFlag"),
		@Index(value = "writeTime")
})
public class ReadDataResponse extends BaseResponse {

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
	private int sendFlag = 0; // 发送到gtw1.wendu114.com 36002状态：0待发送，1已发送
	private Date writeTime;
	private Date writtenTime;
	private int responseFlag = 0; // 响应状态：0待响应，1已响应
	private Date responseTime;
	private Date inputTime;

	public ReadDataResponse(){}
	public ReadDataResponse(Date sensorDataTime, double temp){
		this.sensorDataTime = sensorDataTime;
		this.temp = temp;
	}
	public ReadDataResponse(byte[] buf) {
		super(buf);
		inputTime = new Date();
	}
	@Generated(hash = 1049612656)
	public ReadDataResponse(Long _id, int error, int gwType, int serialNo, Date gwTime, double gwVoltage, String sensorId, int ssStatus,
			int ssFun, int ssType, int protocolVer, int loadLen, int type1, int ssSerialNo, int type2, double chipTemp, int type3, double ssVoltage,
			int type4, Date sensorDataTime, int type5, Date ssTransfTime, int type6, int rssi, int type7, double temp, int type8, double rh,
			int isNewFlag, int waitSentSize1, int waitSentSize2, int errorcode, int sendFlag, Date writeTime, Date writtenTime, int responseFlag,
			Date responseTime, Date inputTime) {
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
		this.sendFlag = sendFlag;
		this.writeTime = writeTime;
		this.writtenTime = writtenTime;
		this.responseFlag = responseFlag;
		this.responseTime = responseTime;
		this.inputTime = inputTime;
	}
	@Override
	public void unpack() {
		if(buf == null || buf.length == 0) {
			return;
		}
		
		int start = 0;
		this.begin  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.packLen  = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.cmd = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		byte[] gwIdBytes = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;
		this.gwId = ByteUtilities.asHex(gwIdBytes);
		this.error = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		
		if(error == 0) {
			this.gwType = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.serialNo = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
			try {
				this.gwTime = dateFromat.parse(ByteUtilities.bcd2Str(ByteUtilities.subBytes(buf, start, 6))); start = start + 6;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			BigDecimal gwVoltageBd = new BigDecimal(((ByteUtilities.toUnsignedInt(buf[start])*256+ByteUtilities.toUnsignedInt(buf[start+1])))*1.0/1000);
			this.gwVoltage = gwVoltageBd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); start = start + 2;

			byte[] sensorIdBytes = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;
			this.sensorId = ByteUtilities.asHex(sensorIdBytes);

			this.ssStatus = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.ssFun = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.ssType = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.protocolVer = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.loadLen = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.type1 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.ssSerialNo = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
			this.type2 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.chipTemp = buf[start]; start = start + 1;
			this.type3 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			BigDecimal ssVoltageBd = new BigDecimal(((ByteUtilities.toUnsignedInt(buf[start])*256+ByteUtilities.toUnsignedInt(buf[start+1])))*1.0/1000);
			this.ssVoltage = ssVoltageBd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); start = start + 2;
			this.type4 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			try {
				this.sensorDataTime = dateFromat.parse(ByteUtilities.bcd2Str(ByteUtilities.subBytes(buf, start, 6))); start = start + 6;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			this.type5 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			try {
				this.ssTransfTime = dateFromat.parse(ByteUtilities.bcd2Str(ByteUtilities.subBytes(buf, start, 6))); start = start + 6;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.type6 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;

			this.rssi = ByteUtilities.toUnsignedInt(buf[start])-256; start = start + 1;
			this.type7 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			
			//X = MSB*256+LSB，当X>=0x8000时，T = (X-65536)/100，单位：℃；当X<0x8000时，T = X/100，单位：℃；
			int x = ByteUtilities.toUnsignedInt(buf[start])*256+ByteUtilities.toUnsignedInt(buf[start+1]); start = start + 2;
			if(x > Integer.valueOf("8000", 16)) {
				BigDecimal tempVoltageBd = new BigDecimal((x-65536)*1.0/100);
				this.temp = tempVoltageBd.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {
				BigDecimal tempVoltageBd = new BigDecimal(x*1.0/100);
				this.temp = tempVoltageBd.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			this.type8 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			
			int y = ByteUtilities.toUnsignedInt(buf[start])*256+ByteUtilities.toUnsignedInt(buf[start+1]); start = start + 2;
			BigDecimal tempVoltageBd = new BigDecimal(y*1.0/100);
			this.rh = tempVoltageBd.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			
			this.isNewFlag =  ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.waitSentSize1 = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
			this.waitSentSize2 = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
			this.errorcode = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		}
		
		this.crc  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.end  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		
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

    public int getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(int sendFlag) {
        this.sendFlag = sendFlag;
    }

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}

	public Date getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(Date writeTime) {
		this.writeTime = writeTime;
	}

	public Date getWrittenTime() {
		return writtenTime;
	}

	public void setWrittenTime(Date writtenTime) {
		this.writtenTime = writtenTime;
	}

	public int getResponseFlag() {
		return responseFlag;
	}

	public void setResponseFlag(int responseFlag) {
		this.responseFlag = responseFlag;
	}

	public Date getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}
}
