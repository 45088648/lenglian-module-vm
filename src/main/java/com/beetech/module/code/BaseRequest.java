package com.beetech.module.code;

public abstract class BaseRequest extends CommonBase {

	public BaseRequest() {
		super();
		setBegin(Integer.valueOf("CACA", 16));
		setEnd(Integer.valueOf("ACAC", 16));
		setCrc(Integer.valueOf("0000", 16));
	}
	

	public abstract void pack();
}
