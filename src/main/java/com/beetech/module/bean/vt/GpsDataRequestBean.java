package com.beetech.module.bean.vt;

import com.beetech.module.bean.GpsDataBean;

public class GpsDataRequestBean extends VtRequestBean{
	private GpsDataRequestBeanBody body;

	public GpsDataRequestBean() {
		setCmd("GPSDATA");
	}

	public GpsDataRequestBean(GpsDataBean gpsDataBean) {
		this();
		setId(gpsDataBean.get_id());
		this.body = new GpsDataRequestBeanBody(gpsDataBean);
	}

	public GpsDataRequestBeanBody getBody() {
		return body;
	}

	public void setBody(GpsDataRequestBeanBody body) {
		this.body = body;
	}
}