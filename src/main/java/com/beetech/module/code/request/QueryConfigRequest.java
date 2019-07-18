package com.beetech.module.code.request;

import com.beetech.module.code.BaseRequest;
import com.beetech.module.utils.ByteUtilities;

public class QueryConfigRequest extends BaseRequest {

	public QueryConfigRequest() {
		super();
		setPackLen(Integer.valueOf("05", 16));
		setCmd(Integer.valueOf("01", 16));
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
		ByteUtilities.intToNetworkByteOrder(getCrc() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getEnd() , buf , start, 2);
	}
	
	

}
