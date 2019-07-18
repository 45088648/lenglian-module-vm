package com.beetech.module.bean.vt;

import android.text.TextUtils;

import com.beetech.module.bean.QueryConfigRealtime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StateRequestBean {
    public static SimpleDateFormat dateFromat = new SimpleDateFormat("yyyyMMddHHmmss");

	private static final long serialVersionUID = -5980465058119958498L;

	/**
	 * mre 版本
	 */
	private String var;
	/**
	 * IMEI / SN
	 */
	private String imei;

	/**
	 * 当前电量
	 */
	private Integer bt;
	/**
	 * "20180403141317" 格式时间
	 */
	private String time;

	/**
	 * 网关状态 0:关闭 1、正常 2：等待
	 */
	private Integer gwstate;
	/**
	 * 连接数
	 */
	private Integer ljs;
	/**
	 * 信号强度
	 */
	private Integer csq;
	/**
	 * 未使用的磁盘空间
	 */
	private Integer uf;
	/**
	 * 文件中存放的数据数量
	 */
	private Long datafile;
	/**
	 * 未上传的数据个数
	 */
	private Long unup;

	private String iccid;

	/**
	 * 外接电源是否接入
	 */
	private Integer power;

	private List<StateData> data;
	
	/**
	 * 网关编号  Hex字符串  9A8B3C1A
	 */
	private String gwid;
	/**
	 * 客户码    Hex字符串  转 int   4位的十进制字符串转 int
	 */
	private Integer cst;
	/**
	 * 工作模式  Hex字符串 转int   1位的十进制 转int    1 2 3 4 5
	 */
	private Integer ptn;
	/**
	 * 传输速率  Hex字符串 转 int   1位的十进制 转int    1 2 3 4 5
	 */
	private Integer bps;
	
	/**
	 * 基站信息 lac
	 */
	private Integer lac;
	/**
	 * 基站信息 cid
	 */
	private Integer ci;
	/**
	 * 基站信息 mnc
	 */
	private Integer mnc;
	/**
	 * 基站信息 lac
	 */
	private Integer lac1;
	/**
	 * 基站信息 cid
	 */
	private Integer ci1;
	/**
	 * 基站信息 mnc
	 */
	private Integer mnc1;
	/**
	 * 基站信息 lac
	 */
	private Integer lac2;
	/**
	 * 基站信息 cid
	 */
	private Integer ci2;
	/**
	 * 基站信息 mnc
	 */
	private Integer mnc2;

	private String appState;

	public StateRequestBean() {
	}

	public StateRequestBean(QueryConfigRealtime queryConfigRealtime) {
		this.gwstate = 1;
		try{
			String customer = queryConfigRealtime.getCustomer();
			if (!TextUtils.isEmpty(customer)) {
				this.cst = Integer.valueOf(customer, 16);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		this.bps = queryConfigRealtime.getBps();
		this.ptn = queryConfigRealtime.getPattern();
		this.gwid = queryConfigRealtime.getImei();
		this.imei = queryConfigRealtime.getImei();
		this.time = dateFromat.format(new Date());
		this.csq = 0;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
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

	public Integer getGwstate() {
		return gwstate;
	}

	public void setGwstate(Integer gwstate) {
		this.gwstate = gwstate;
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

	public Integer getUf() {
		return uf;
	}

	public void setUf(Integer uf) {
		this.uf = uf;
	}

	public Integer getPower() {
		return power;
	}

	public void setPower(Integer power) {
		this.power = power;
	}

	public List<StateData> getData() {
		return data;
	}

	public void setData(List<StateData> data) {
		this.data = data;
	}

	public Long getDatafile() {
		return datafile;
	}

	public void setDatafile(Long datafile) {
		this.datafile = datafile;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public Long getUnup() {
		return unup;
	}

	public void setUnup(Long unup) {
		this.unup = unup;
	}

	public String getGwid() {
		return gwid;
	}

	public void setGwid(String gwid) {
		this.gwid = gwid;
	}

	public Integer getCst() {
		return cst;
	}

	public void setCst(Integer cst) {
		this.cst = cst;
	}

	public Integer getPtn() {
		return ptn;
	}

	public void setPtn(Integer ptn) {
		this.ptn = ptn;
	}

	public Integer getBps() {
		return bps;
	}

	public void setBps(Integer bps) {
		this.bps = bps;
	}

	public Integer getLac() {
		return lac;
	}

	public void setLac(Integer lac) {
		this.lac = lac;
	}

	public Integer getCi() {
		return ci;
	}

	public void setCi(Integer ci) {
		this.ci = ci;
	}

	public Integer getMnc() {
		return mnc;
	}

	public void setMnc(Integer mnc) {
		this.mnc = mnc;
	}

	public Integer getLac1() {
		return lac1;
	}

	public void setLac1(Integer lac1) {
		this.lac1 = lac1;
	}

	public Integer getCi1() {
		return ci1;
	}

	public void setCi1(Integer ci1) {
		this.ci1 = ci1;
	}

	public Integer getMnc1() {
		return mnc1;
	}

	public void setMnc1(Integer mnc1) {
		this.mnc1 = mnc1;
	}

	public Integer getLac2() {
		return lac2;
	}

	public void setLac2(Integer lac2) {
		this.lac2 = lac2;
	}

	public Integer getCi2() {
		return ci2;
	}

	public void setCi2(Integer ci2) {
		this.ci2 = ci2;
	}

	public Integer getMnc2() {
		return mnc2;
	}

	public void setMnc2(Integer mnc2) {
		this.mnc2 = mnc2;
	}

	public String getAppState() {
		return appState;
	}

	public void setAppState(String appState) {
		this.appState = appState;
	}
}
