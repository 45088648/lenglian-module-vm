package com.beetech.module.bean.vt;

import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.constant.Constant;

import java.io.Serializable;

public class StateData implements Serializable{
	private static final long serialVersionUID = 1588352472877616362L;

	private Long ids;
	private String rfId;
	private int rssi;
	private String time;
	
	public StateData() {
	}
	public StateData(ReadDataRealtime readDataRealtime) {
		this.ids = readDataRealtime.get_id();
		this.rfId = readDataRealtime.getSensorId();
		this.rssi = readDataRealtime.getRssi();
		this.time = Constant.dateFormat.format(readDataRealtime.getSensorDataTime());
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
}
