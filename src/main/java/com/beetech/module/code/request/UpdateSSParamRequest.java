package com.beetech.module.code.request;

import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.code.BaseRequest;
import com.beetech.module.utils.ByteUtilities;

import java.util.Arrays;

/**
 * 修改SS的时间和阈值
 * CACA 3B 9C 00000000 01020304 52017137 ABCD 01 000F 003C 0078 0078 20
 * 65 03 1388 65 03 0000
 * 66 03 2328 66 02 03E8
 * 65 01 0FA0 65 00 0000
 * 66 01 251C 66 00 0000
 * 9876 0000 ACAC
 *
 * 起始位2|长度1|命令1|GWID4|保留位4|SSID4|TID2|pattern1|采集间隔2|正常间隔2|预警间隔2|报警间隔2|负载长度1|
 * 数据类型1|阈值类型1|温度预警上限2|数据类型1|阈值类型1|温度预警下限2|
 * 数据类型1|阈值类型1|湿度预警上限2|数据类型1|阈值类型1|湿度预警下限2|
 * 数据类型1|阈值类型1|温度阈值上限2|数据类型1|阈值类型1|温度阈值下限2|
 * 数据类型1|阈值类型1|湿度阈值上限2|数据类型1|阈值类型1|湿度阈值下限2|
 * 协议保留位2|CRC16 2|结束位2|
 *
 * TID：随机数，判断重复数据返回
 */
public class UpdateSSParamRequest extends BaseRequest {

	private byte[] reserved = new byte[]{0, 0, 0, 0};

	private String sensorId; // 标签ID
	private int pattern = 4; //工作模式
	private int tid = 0xABCD; // 随机数，判断重复数据返回
	private int dataInterval = 60; //采集间隔
	private int normalInterval = 60; // 正常间隔
	private int preAlarmInterval = 60; // 预警间隔
	private int alarmInterval = 60; // 报警间隔
	private int loadLen = 32; //负载长度1

	private int preAlarmTempHight = 8; // 温度预警上限
	private int preAlarmTempLower = 2; // 温度预警下限
	private int preAlarmRhHight = 75; // 湿度预警上限
	private int preAlarmRhLower = 35; // 湿度预警下限

	private int tempHight = 8; // 温度阈值上限
	private int tempLower = 2; // 温度阈值下限
	private int rhHight = 75; // 湿度阈值上限
	private int rhLower = 35; // 湿度阈值下限

	private byte[] protocalReserved = new byte[]{0, 0};

	public UpdateSSParamRequest(QueryConfigRealtime queryConfigRealtime, ReadDataRealtime readDataRealtime) {
		super();
		setPackLen(Integer.valueOf("3B", 16));
		setCmd(Integer.valueOf("9C", 16));
		setGwId(queryConfigRealtime.getGwId());
		this.sensorId = readDataRealtime.getSensorId();
		pack();
	}

	public UpdateSSParamRequest(String gwId, ReadDataRealtime readDataRealtime) {
		super();
		setPackLen(Integer.valueOf("3B", 16));
		setCmd(Integer.valueOf("9C", 16));
		setGwId(gwId);
		this.sensorId = readDataRealtime.getSensorId();
		pack();
	}

	@Override
	public void pack() {
		int bufferLen = 2+1+getPackLen()+2+2; // 2起始位+1长度+长度+2CRC16+2结束位
		this.buf = new byte[bufferLen];
		int start = 0;
		ByteUtilities.intToNetworkByteOrder(getBegin() , buf , start, 2); start = start+2;		
		ByteUtilities.intToNetworkByteOrder(getPackLen() , buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(getCmd() , buf , start, 1); start = start+1;

		byte[] gwIdBytes = ByteUtilities.asByteArray(getGwId());
		for (byte b : gwIdBytes) {
			Arrays.fill(buf, start, ++start, b);
		}
		for (byte b : reserved) {
			Arrays.fill(buf, start, ++start, b);
		}
		byte[] newGwIdBytes = ByteUtilities.asByteArray(sensorId);
		for (byte b : newGwIdBytes) {
			Arrays.fill(buf, start, ++start, b);
		}
		ByteUtilities.intToNetworkByteOrder(tid, buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(pattern, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(dataInterval, buf, start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(normalInterval, buf, start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(preAlarmInterval, buf, start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(alarmInterval, buf, start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(loadLen, buf, start, 1); start = start+1;

		//温度预警上限
		ByteUtilities.intToNetworkByteOrder(0x65, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(0x03 , buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(preAlarmTempHight*100, buf, start, 2); start = start+2;

		//温度预警下限
		ByteUtilities.intToNetworkByteOrder(0x65, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(0x02 , buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(preAlarmTempLower*100, buf, start, 2); start = start+2;

		//湿度预警上限
		ByteUtilities.intToNetworkByteOrder(0x66, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(0x03 , buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(preAlarmRhHight*100, buf, start, 2); start = start+2;

		//湿度预警下限
		ByteUtilities.intToNetworkByteOrder(0x66, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(0x02 , buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(preAlarmRhLower*100, buf, start, 2); start = start+2;

		//温度阈值上限
		ByteUtilities.intToNetworkByteOrder(0x65, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(0x01 , buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(tempHight*100, buf, start, 2); start = start+2;

		//温度阈值下限
		ByteUtilities.intToNetworkByteOrder(0x65, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(0x00 , buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(tempLower*100, buf, start, 2); start = start+2;

		//湿度阈值上限
		ByteUtilities.intToNetworkByteOrder(0x66, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(0x01 , buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(rhHight*100, buf, start, 2); start = start+2;

		//湿度阈值下限
		ByteUtilities.intToNetworkByteOrder(0x66, buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(0x00 , buf, start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(rhLower*100, buf, start, 2); start = start+2;

		//协议保留位
		for (byte b : protocalReserved) {
			Arrays.fill(buf, start, ++start, b);
		}
		ByteUtilities.intToNetworkByteOrder(getCrc() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getEnd() , buf , start, 2);
	}

	public byte[] getReserved() {
		return reserved;
	}

	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public int getPattern() {
		return pattern;
	}

	public void setPattern(int pattern) {
		this.pattern = pattern;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getDataInterval() {
		return dataInterval;
	}

	public void setDataInterval(int dataInterval) {
		this.dataInterval = dataInterval;
	}

	public int getNormalInterval() {
		return normalInterval;
	}

	public void setNormalInterval(int normalInterval) {
		this.normalInterval = normalInterval;
	}

	public int getPreAlarmInterval() {
		return preAlarmInterval;
	}

	public void setPreAlarmInterval(int preAlarmInterval) {
		this.preAlarmInterval = preAlarmInterval;
	}

	public int getAlarmInterval() {
		return alarmInterval;
	}

	public void setAlarmInterval(int alarmInterval) {
		this.alarmInterval = alarmInterval;
	}

	@Override
	public int getLoadLen() {
		return loadLen;
	}

	@Override
	public void setLoadLen(int loadLen) {
		this.loadLen = loadLen;
	}

	public int getPreAlarmTempHight() {
		return preAlarmTempHight;
	}

	public void setPreAlarmTempHight(int preAlarmTempHight) {
		this.preAlarmTempHight = preAlarmTempHight;
	}

	public int getPreAlarmTempLower() {
		return preAlarmTempLower;
	}

	public void setPreAlarmTempLower(int preAlarmTempLower) {
		this.preAlarmTempLower = preAlarmTempLower;
	}

	public int getPreAlarmRhHight() {
		return preAlarmRhHight;
	}

	public void setPreAlarmRhHight(int preAlarmRhHight) {
		this.preAlarmRhHight = preAlarmRhHight;
	}

	public int getPreAlarmRhLower() {
		return preAlarmRhLower;
	}

	public void setPreAlarmRhLower(int preAlarmRhLower) {
		this.preAlarmRhLower = preAlarmRhLower;
	}

	public int getTempHight() {
		return tempHight;
	}

	public void setTempHight(int tempHight) {
		this.tempHight = tempHight;
	}

	public int getTempLower() {
		return tempLower;
	}

	public void setTempLower(int tempLower) {
		this.tempLower = tempLower;
	}

	public int getRhHight() {
		return rhHight;
	}

	public void setRhHight(int rhHight) {
		this.rhHight = rhHight;
	}

	public int getRhLower() {
		return rhLower;
	}

	public void setRhLower(int rhLower) {
		this.rhLower = rhLower;
	}

	public byte[] getProtocalReserved() {
		return protocalReserved;
	}

	public void setProtocalReserved(byte[] protocalReserved) {
		this.protocalReserved = protocalReserved;
	}
}
