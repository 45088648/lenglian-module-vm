package com.beetech.module.bean.vt;

import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.utils.DateUtils;

import java.io.Serializable;

public class StateData implements Serializable{
	private static final long serialVersionUID = 1588352472877616362L;

	private Long ids;
	private String rfId;
	private int rssi;
	private String time;
	private String gwtt; //	GW传输时间
	private String sstt; //	SS传输时间
	private int wait2;
	
	public StateData() {
	}
	public StateData(ReadDataRealtime readDataRealtime) {
		this.ids = readDataRealtime.get_id();
		this.rfId = readDataRealtime.getSensorId();
		this.rssi = readDataRealtime.getRssi();
		this.time = DateUtils.parseDateToString(readDataRealtime.getSensorDataTime(), DateUtils.C_YYYYMMDDHHMMSS);
		this.gwtt = DateUtils.parseDateToString(readDataRealtime.getGwTime(), DateUtils.C_YYYYMMDDHHMMSS);
		this.sstt = DateUtils.parseDateToString(readDataRealtime.getSsTransfTime(), DateUtils.C_YYYYMMDDHHMMSS);
		this.wait2 = readDataRealtime.getWaitSentSize2();
	}
	
	public Long getIds() {
		return ids;
	}
	public void setIds(Long ids) {
		this.ids = ids;
	}
	public String getRfId() {
		return rfId;
	}
	public void setRfId(String rfId) {
		this.rfId = rfId;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getGwtt() {
		return gwtt;
	}

	public void setGwtt(String gwtt) {
		this.gwtt = gwtt;
	}

	public String getSstt() {
		return sstt;
	}

	public void setSstt(String sstt) {
		this.sstt = sstt;
	}

	public int getWait2() {
		return wait2;
	}

	public void setWait2(int wait2) {
		this.wait2 = wait2;
	}
}
