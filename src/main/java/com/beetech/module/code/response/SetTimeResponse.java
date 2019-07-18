package com.beetech.module.code.response;

import java.text.ParseException;
import java.util.Date;
import com.beetech.module.code.BaseResponse;
import com.beetech.module.utils.ByteUtilities;

public class SetTimeResponse extends BaseResponse {
	private int dataType; // 数据类型
	private Date masterTime; // Master的日期和时间，东8区，BCD码格式，"YY MM DD HH mm SS"，即“年、月、日、时、分、秒”

	Date inputTime;

	public SetTimeResponse(byte[] buf) {
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
		this.gwId = Integer.toHexString(ByteUtilities.makeIntFromByte4(buf, start)); start = start + 4;
		this.loadLen = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.dataType = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.dataLen = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;

		try {
			this.masterTime = dateFromat.parse(ByteUtilities.bcd2Str(ByteUtilities.subBytes(buf, start, 6))); start = start + 6;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.crc  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.end  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public Date getMasterTime() {
		return masterTime;
	}

	public void setMasterTime(Date masterTime) {
		this.masterTime = masterTime;
	}

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}
}
