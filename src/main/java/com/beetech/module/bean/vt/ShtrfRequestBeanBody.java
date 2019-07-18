package com.beetech.module.bean.vt;

import com.beetech.module.code.response.ReadDataResponse;
import java.util.ArrayList;
import java.util.List;

public class ShtrfRequestBeanBody {
	/**
	 * 网关IMEI SN
	 */
	private String imei;
	/**
	 * 20180101031212
	 */
	private String time;
	private String var = "1.6";
	private Integer bt;

	/**
	 * 未上传数量， debug
	 */
	private Integer unup;

	private List<ShtrfData> data;

	public ShtrfRequestBeanBody() {}
	public ShtrfRequestBeanBody(ReadDataResponse readDataResponse) {
	    this.imei = readDataResponse.getSensorId();
        this.data = new ArrayList<>();
        ShtrfData shtrfData = new ShtrfData(readDataResponse);
        this.data.add(shtrfData);
    }

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Integer getUnup() {
		return unup;
	}

	public void setUnup(Integer unup) {
		this.unup = unup;
	}

	public List<ShtrfData> getData() {
		return data;
	}

	public void setData(List<ShtrfData> data) {
		this.data = data;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public Integer getBt() {
		return bt;
	}

	public void setBt(Integer bt) {
		this.bt = bt;
	}
}
