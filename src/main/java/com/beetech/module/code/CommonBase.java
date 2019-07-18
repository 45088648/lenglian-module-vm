package com.beetech.module.code;

import java.text.SimpleDateFormat;

public abstract class CommonBase {
	
	public static SimpleDateFormat dateFromat = new SimpleDateFormat("yyMMddHHmmss");
	public int begin; // 起始位
	public int packLen; // 长度
	public int loadLen; // 负载长度
	public int dataLen; // 数据长度
	public int cmd; // 命令
	public String gwId; // 网关序列号(小模块网关编号 )
	public int crc; // CRC16, 保留位
	public int end; // 结束位
	public byte[] buf;

	public CommonBase() {
		super();
		setGwId("00000000");
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getPackLen() {
		return packLen;
	}

	public void setPackLen(int length) {
		this.packLen = length;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public int getCrc() {
		return crc;
	}

	public void setCrc(int crc) {
		this.crc = crc;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getGwId() {
		return gwId;
	}

	public void setGwId(String gwId) {
		this.gwId = gwId;
	}

	public byte[] getBuf() {
		return buf;
	}

	public void setBuf(byte[] buf) {
		this.buf = buf;
	}

	public int getLoadLen() {
		return loadLen;
	}

	public void setLoadLen(int loadLen) {
		this.loadLen = loadLen;
	}

	public int getDataLen() {
		return dataLen;
	}

	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}

}
