package com.beetech.module.code.response;

import com.beetech.module.code.BaseResponse;
import com.beetech.module.utils.ByteUtilities;
import java.util.Date;

/**
 * 反馈：修改SS时间和阈值参数
 * ACAC 10 9C 01020304 52017137 ABCD 00 00000000 0000 CACA
 * 起始位2|长度1|命令1|GWID4|SSID4|TID1|Error1|保留位4|CRC16|结束位|
 */
public class UpdateSSParamResponse extends BaseResponse {

	private String sensorId; // 标签ID
	private int tid; // 随机数，判断重复数据返回
	private int error; //Error
	private byte[] reserved;
	private Date inputTime;

    public UpdateSSParamResponse(){}
    public UpdateSSParamResponse(byte[] buf) {
        super(buf);
        inputTime = new Date();
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

		byte[] sensorIdBytes = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;
		this.sensorId =  ByteUtilities.asHex(sensorIdBytes);

		this.tid = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.error = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		reserved = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;

		this.crc  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.end  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public byte[] getReserved() {
		return reserved;
	}

	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}
}
