package com.beetech.module.bean.vt;

public class VtRequestBean {
	private Long id = 0L;
	private String v = "1.0";
	private String cmd = "SHTRF";

	public VtRequestBean() {}

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
	
}
