package com.beetech.module.code.request;

import com.beetech.module.bean.QueryConfigRealtime;
import com.beetech.module.code.BaseRequest;
import com.beetech.module.utils.ByteUtilities;
import java.util.Arrays;

public class UpdateConfigRequest extends BaseRequest {

	private String newGwId;
	private String hardVer; // 硬件版本
	private String customer = "FFFFFFFF"; //客户码
	private int debug = 0; // debug固定值
	private int category = 2; // 分类码
	private int interval = 180; // 时间间隔
	private int pattern = 1; //工作模式
	private int bps = 1; // 传输速率
	private int channel = 4;
	private byte[] reserved = new byte[]{0, 0, 0, 0};

	public UpdateConfigRequest(QueryConfigRealtime queryConfigRealtime) {
		super();
		setPackLen(Integer.valueOf("1B", 16));
		setCmd(Integer.valueOf("84", 16));
		setGwId(queryConfigRealtime.getGwId());
		this.newGwId = queryConfigRealtime.getGwId();
		this.hardVer = queryConfigRealtime.getHardVer();
		this.customer = queryConfigRealtime.getCustomer();
//		this.debug = queryConfigRealtime.getDebug();
		this.debug = 02;
		this.category = queryConfigRealtime.getCategory();
		this.interval = queryConfigRealtime.getInterval();
		this.pattern = queryConfigRealtime.getPattern();
		this.bps = queryConfigRealtime.getBps();
		this.channel = queryConfigRealtime.getChannel();
	}

	public UpdateConfigRequest(String gwId) {
		super();
		setPackLen(Integer.valueOf("12", 16));
		setCmd(Integer.valueOf("02", 16));
		setGwId(gwId);
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
		byte[] newGwIdBytes = ByteUtilities.asByteArray(newGwId);
		for (byte b : newGwIdBytes) {
			Arrays.fill(buf, start, ++start, b);
		}
		byte[] hardVerBytes = ByteUtilities.asByteArray(hardVer);
		for (byte b : hardVerBytes) {
			Arrays.fill(buf, start, ++start, b);
		}
		byte[] customerBytes = ByteUtilities.asByteArray(customer);
		for (byte b : customerBytes) {
			Arrays.fill(buf, start, ++start, b);
		}
		ByteUtilities.intToNetworkByteOrder(debug , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(category, buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(interval, buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(pattern, buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(bps, buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(channel, buf , start, 1); start = start+1;
		for (byte b : reserved) {
			Arrays.fill(buf, start, ++start, b);
		}
		ByteUtilities.intToNetworkByteOrder(getCrc() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getEnd() , buf , start, 2);
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

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}
}
