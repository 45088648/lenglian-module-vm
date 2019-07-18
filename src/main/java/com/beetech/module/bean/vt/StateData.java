package com.beetech.module.bean.vt;

import com.beetech.module.bean.ReadDataRealtime;
import java.io.Serializable;

public class StateData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1588352472877616362L;
	private Long ids;
	private String rfId;
	
	public StateData() {
	}
	public StateData(ReadDataRealtime readDataRealtime) {
		this.ids = readDataRealtime.get_id();
		this.rfId = readDataRealtime.getSensorId();
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
}
