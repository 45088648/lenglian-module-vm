package com.beetech.module.utils;

public class PrintSetVo {
	private boolean printStats; /* 打印统计数据最高、最低 */
	private Integer colSize = 1; // 单排数据 双排数据
	private int printTimeInterval = 5;
	private String plateNumber;	// 车牌号

	public PrintSetVo() {

	}

	public boolean isPrintStats() {
		return printStats;
	}

	public void setPrintStats(boolean printStats) {
		this.printStats = printStats;
	}

	public Integer getColSize() {
		return colSize;
	}

	public void setColSize(Integer colSize) {
		this.colSize = colSize;
	}

	public int getPrintTimeInterval() {
		return printTimeInterval;
	}

	public void setPrintTimeInterval(int printTimeInterval) {
		this.printTimeInterval = printTimeInterval;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
}
