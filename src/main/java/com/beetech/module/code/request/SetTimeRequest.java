package com.beetech.module.code.request;

import java.util.Arrays;
import java.util.Date;
import com.beetech.module.code.BaseRequest;
import com.beetech.module.utils.ByteUtilities;

public class SetTimeRequest extends BaseRequest{
	private int dataType; // 数据类型
	private Date masterTime; // Master的日期和时间，东8区，BCD码格式，"YY MM DD HH mm SS"，即“年、月、日、时、分、秒”
	
	public SetTimeRequest() {
		super();
		setPackLen(Integer.valueOf("0E", 16));
		setCmd(Integer.valueOf("04", 16));
		setLoadLen(Integer.valueOf("08", 16));
		setDataLen(Integer.valueOf("06", 16));
		setDataType(Integer.valueOf("15", 16));
	}
	
	public SetTimeRequest(String gwId) {
		this();
		setGwId(gwId);
		masterTime = new Date();
		pack();
	}
	public SetTimeRequest(String gwId, long timeInMills) {
		this();
		setGwId(gwId);
		masterTime = new Date(timeInMills);
		pack();
	}

	@Override
	public void pack() {
		int bufferLen = 2+1+getPackLen()+2+2; // 2起始位+1长度+长度+2CRC16+2结束位
		this.buf = new byte[bufferLen];
		int start = 0;
		ByteUtilities.intToNetworkByteOrder(getBegin() , buf , start, 2); start = start+2;		
		ByteUtilities.intToNetworkByteOrder(getPackLen() , buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(getCmd() , buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(Integer.valueOf(getGwId(), 16) , buf , start, 4); start = start+4;
		ByteUtilities.intToNetworkByteOrder(getLoadLen() , buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(getDataType() , buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(getDataLen() , buf , start, 1); start = start+1;
		masterTime = new Date();
		byte[] timeBcd = ByteUtilities.str2Bcd(dateFromat.format(masterTime));
		for (byte b : timeBcd) {
			Arrays.fill(buf, start, ++start, b);
		}
		
		ByteUtilities.intToNetworkByteOrder(getCrc() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getEnd() , buf , start, 2);
		
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public Date getMasterTime() {
		return masterTime;
	}

	public void setMasterTime(Date masterTime) {
		this.masterTime = masterTime;
	}
}
