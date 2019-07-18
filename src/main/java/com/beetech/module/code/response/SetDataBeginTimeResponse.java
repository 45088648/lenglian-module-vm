package com.beetech.module.code.response;

import com.beetech.module.code.BaseResponse;
import com.beetech.module.utils.ByteUtilities;

import java.text.ParseException;
import java.util.Date;

public class SetDataBeginTimeResponse extends BaseResponse {
	private int readOrWriteFlag; // R/W标识
	private int error; // 数据标识
	private Date dataBeginTime; // 数据开始的日期和时间，东8区，BCD码格式，"YY MM DD HH mm SS"，即“年、月、日、时、分、秒”

	Date inputTime;

	public SetDataBeginTimeResponse(byte[] buf) {
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
		this.readOrWriteFlag = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.error = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;

		try {
			this.dataBeginTime = dateFromat.parse(ByteUtilities.bcd2Str(ByteUtilities.subBytes(buf, start, 6))); start = start + 6;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.crc  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.end  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
	}

	public int getReadOrWriteFlag() {
		return readOrWriteFlag;
	}

	public void setReadOrWriteFlag(int readOrWriteFlag) {
		this.readOrWriteFlag = readOrWriteFlag;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public Date getDataBeginTime() {
		return dataBeginTime;
	}

	public void setDataBeginTime(Date dataBeginTime) {
		this.dataBeginTime = dataBeginTime;
	}

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}
}
