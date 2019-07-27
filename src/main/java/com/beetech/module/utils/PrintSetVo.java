package com.beetech.module.utils;

public class PrintSetVo {
	private int rhFlag = 1; /* 是否打印湿度 0 打印 1不打印 */
	private int printStats = 0; /* 打印统计数据最大值、最小值、平均值 0 打印 1 不打印 */
	private Integer colSize = 1; // 单排数据 双排数据
	private int printTimeInterval = 5;
	public PrintSetVo() {

	}

	public void setRhFlag(int rhFlag) {
		this.rhFlag = rhFlag;
	}

	public int getPrintStats() {
		return printStats;
	}

	public void setPrintStats(int printStats) {
		this.printStats = printStats;
	}

	public int getRhFlag() {
		return rhFlag;
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
}
