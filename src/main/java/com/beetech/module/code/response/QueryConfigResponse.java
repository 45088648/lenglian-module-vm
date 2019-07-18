package com.beetech.module.code.response;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import com.beetech.module.code.BaseResponse;
import com.beetech.module.utils.ByteUtilities;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

public class QueryConfigResponse extends BaseResponse {

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
	private String gwId; // 网关序列号(小模块网关编号 )
	private Date inputTime;

    public QueryConfigResponse(){}
    public QueryConfigResponse(byte[] buf) {
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
		byte[] hardVerBytes = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;
		this.hardVer =  ByteUtilities.asHex(hardVerBytes);
		byte[] softVerBytes = new byte[]{buf[start], buf[start+1]}; start = start + 2;
		this.softVer = ByteUtilities.asHex(softVerBytes);
		byte[] customerBytes = new byte[]{buf[start], buf[start+1]}; start = start + 2;
		this.customer = ByteUtilities.asHex(customerBytes);
		this.debug = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.category = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.interval = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		try {
			this.calendar = dateFromat.parse(ByteUtilities.bcd2Str(ByteUtilities.subBytes(buf, start, 6))); start = start + 6;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.pattern = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.bps = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.channel = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.ramData = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.front = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.rear = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.pflashLength = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.sendOk = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;

		byte gwVoltageByte0 = buf[start];
		byte gwVoltageByte1 = buf[start+1];
		BigDecimal gwVoltageBd = new BigDecimal((ByteUtilities.toUnsignedInt(gwVoltageByte0)*256+ByteUtilities.toUnsignedInt(gwVoltageByte1))*1.0/4/1023);
		this.gwVoltage = gwVoltageBd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); start = start + 2;
		this.crc  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.end  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		
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

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}
}
