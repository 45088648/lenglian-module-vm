package com.beetech.module.bean.vt;

import java.util.Date;

public class ShutdownRequestBody {

	private String imei;
	private Integer bt;
	private String time;
	private Date formatTime;

	public ShutdownRequestBody() {
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Integer getBt() {
		return bt;
	}

	public void setBt(Integer bt) {
		this.bt = bt;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Date getFormatTime() {
		return formatTime;
	}

	public void setFormatTime(Date formatTime) {
		this.formatTime = formatTime;
	}
}
