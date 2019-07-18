package com.beetech.module.bean.vt;

import com.beetech.module.bean.QueryConfigRealtime;

public class VtStateRequestBean {
	private Long id = 0L;
	private String v = "1.0";
	private String cmd = "STATE";
	private StateRequestBean body ;

	public VtStateRequestBean() {}
	public VtStateRequestBean(QueryConfigRealtime queryConfigRealtime) {
		this.id = queryConfigRealtime.get_id();
		this.body = new StateRequestBean(queryConfigRealtime);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public StateRequestBean getBody() {
		return body;
	}

	public void setBody(StateRequestBean body) {
		this.body = body;
	}
	
}
