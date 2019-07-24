package com.beetech.module.bean.vt;

public class SysResponseBody{

	// 扩大十倍
	/**
	 * temperature hight
	 */
	private Integer th;
	/**
	 * temperature light
	 */
	private Integer tl;
	/**
	 * humidity hight
	 */
	private Integer hh;
	/**
	 * humidity light
	 */
	private Integer hl;
	
	// 服务器 ip和端口    主备
	private String ip1;
	private Integer port1;
	private String ip2;
	private Integer port2;
	
	private String time;
	/**
	 * upload cycle
	 */
	private Integer uc;
	
	/**
	 * Sim Card Number
	 */
	private String num;
	
	/**
	 * 电量低阀值
	 */
	private Integer btl;
	/**
	 * 报警周期 分钟
	 */
	private Integer ac;
	
	/**
	 * 温度是否报警配置
	 */
	private Integer ta;
	/**
	 * 湿度是否报警配置
	 */
	private Integer ha;
	
	/**
	 * 设备电量是否报警配置
	 */
	private Integer pa;
	/**
	 * 设备外接电源是否报警配置
	 */
	private Integer epa;
	
	public SysResponseBody() {

	}

	public Integer getTh() {
		return th;
	}

	public void setTh(Integer th) {
		this.th = th;
	}

	public Integer getTl() {
		return tl;
	}

	public void setTl(Integer tl) {
		this.tl = tl;
	}

	public Integer getHh() {
		return hh;
	}

	public void setHh(Integer hh) {
		this.hh = hh;
	}

	public Integer getHl() {
		return hl;
	}

	public void setHl(Integer hl) {
		this.hl = hl;
	}

	public String getIp1() {
		return ip1;
	}

	public void setIp1(String ip1) {
		this.ip1 = ip1;
	}

	public Integer getPort1() {
		return port1;
	}

	public void setPort1(Integer port1) {
		this.port1 = port1;
	}

	public String getIp2() {
		return ip2;
	}

	public void setIp2(String ip2) {
		this.ip2 = ip2;
	}

	public Integer getPort2() {
		return port2;
	}

	public void setPort2(Integer port2) {
		this.port2 = port2;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getUc() {
		return uc;
	}

	public void setUc(Integer uc) {
		this.uc = uc;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Integer getBtl() {
		return btl;
	}

	public void setBtl(Integer btl) {
		this.btl = btl;
	}

	public Integer getAc() {
		return ac;
	}

	public void setAc(Integer ac) {
		this.ac = ac;
	}

	public Integer getTa() {
		return ta;
	}

	public void setTa(Integer ta) {
		this.ta = ta;
	}

	public Integer getHa() {
		return ha;
	}

	public void setHa(Integer ha) {
		this.ha = ha;
	}

	public Integer getPa() {
		return pa;
	}

	public void setPa(Integer pa) {
		this.pa = pa;
	}

	public Integer getEpa() {
		return epa;
	}

	public void setEpa(Integer epa) {
		this.epa = epa;
	}
}
