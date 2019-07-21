package com.beetech.module.bean.vt;

public class SysRequestBody {

	/**
	 * IMEI /SN
	 */
	private String imei;
	/**
	 * Sim Card Number
	 */
	private String num;
	/**
	 * "20180402113036"
	 */
	private String time;

	/**
	 * 连接数
	 */
	private Integer ljs;
	/**
	 * 信号强度
	 */
	private Integer csq;
	/**
	 * mre版本号
	 */
	private String var;
	/**
	 * 类型
	 */
	private Integer type;
	/**
	 * Sim Card iccid
	 */
	private String iccid;

	public SysRequestBody() {

	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public Integer getLjs() {
		return ljs;
	}

	public void setLjs(Integer ljs) {
		this.ljs = ljs;
	}

	public Integer getCsq() {
		return csq;
	}

	public void setCsq(Integer csq) {
		this.csq = csq;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}
}
