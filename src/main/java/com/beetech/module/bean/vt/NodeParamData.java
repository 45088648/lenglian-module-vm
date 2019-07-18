package com.beetech.module.bean.vt;

public class NodeParamData {
	
	private String num;//设备号
	private String name;//标识名
	private String gn;//分组名
	/**
	 * temperature hight
	 */
	private String th;
	/**
	 * temperature light
	 */
	private String tl;
	/**
	 * humidity hight
	 */
	private String hh;
	/**
	 * humidity light
	 */
	private String hl;
	
	private String sc; // 采集周期，单位：秒
	private Integer af; // 报警标志

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGn() {
		return gn;
	}

	public void setGn(String gn) {
		this.gn = gn;
	}

	public String getTh() {
		return th;
	}

	public void setTh(String th) {
		this.th = th;
	}

	public String getTl() {
		return tl;
	}

	public void setTl(String tl) {
		this.tl = tl;
	}

	public String getHh() {
		return hh;
	}

	public void setHh(String hh) {
		this.hh = hh;
	}

	public String getHl() {
		return hl;
	}

	public void setHl(String hl) {
		this.hl = hl;
	}

	public String getSc() {
		return sc;
	}

	public void setSc(String sc) {
		this.sc = sc;
	}

	public Integer getAf() {
		return af;
	}

	public void setAf(Integer af) {
		this.af = af;
	}
}
