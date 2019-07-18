package com.beetech.module.code.response;

import com.beetech.module.code.BaseResponse;
import com.beetech.module.utils.ByteUtilities;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

public class UpdateConfigResponse extends BaseResponse {

	private Long _id;
	private String newGwId; // 新网关序列号(小模块网关编号 )
	private String hardVer; // 硬件版本
	private String customer; //客户码
	private int debug; // debug固定值
	private int category; // 分类码
	private int interval; // 时间间隔
	private int pattern; //工作模式
	private int bps; // 传输速率
	private int channel; // 频段
	private byte[] reserved;
	private Date inputTime;

    public UpdateConfigResponse(){}
    public UpdateConfigResponse(byte[] buf) {
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

		byte[] newGwIdBytes = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;
		this.newGwId = ByteUtilities.asHex(newGwIdBytes);

		byte[] hardVerBytes = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;
		this.hardVer =  ByteUtilities.asHex(hardVerBytes);

		byte[] customerBytes = new byte[]{buf[start], buf[start+1]}; start = start + 2;
		this.customer = ByteUtilities.asHex(customerBytes);

		this.debug = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.category = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.interval = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;

		this.pattern = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.bps = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.channel = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;

		reserved = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;

		this.crc  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.end  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
	}

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

	public String getNewGwId() {
		return newGwId;
	}

	public void setNewGwId(String newGwId) {
		this.newGwId = newGwId;
	}

	public String getHardVer() {
		return hardVer;
	}


	public void setHardVer(String hardVer) {
		this.hardVer = hardVer;
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

	public String getGwId() {
		return gwId;
	}

	public void setGwId(String gwId) {
		this.gwId = gwId;
	}

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}
}
