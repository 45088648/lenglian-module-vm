package com.beetech.module.code.request;

import com.beetech.module.code.BaseRequest;
import com.beetech.module.utils.ByteUtilities;

public class ReadDataRequest extends BaseRequest {
	public int readIdleTime; //指示GW向Master发送一个含有SS数据的包后，需要等待的超时时间，超时即判定此数据包上传失败。取值范围：32 <= X <= 32000，单位：ms； 其中，X = 256*MSB+LSB
	public int error; // 0 = 收到数据；1 = 数据错误，需要重发；other = 未定义；
	public int serialNo; // 确认序列号：用来告知GW其上一个数据包（与确认序列号一致）已经发送成功。当处等待读取的状态时，GW如果收到一个确认序列号为0x0000的读取指令，那么就会开始上传数据。
	
	public ReadDataRequest(){
		super();
		setPackLen(Integer.valueOf("0A", 16));
		setCmd(Integer.valueOf("07", 16));
		setReadIdleTime(4096);
		setError(Integer.valueOf("00", 16));
		setSerialNo(Integer.valueOf("0000", 16));
	}

	public ReadDataRequest(String gwId, int error, int serialNo){
		this();
		setGwId(gwId);
		setError(error);
		setSerialNo(serialNo);
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
		ByteUtilities.intToNetworkByteOrder(getError() , buf , start, 1); start = start+1;
		ByteUtilities.intToNetworkByteOrder(getSerialNo() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getReadIdleTime() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getCrc() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getEnd() , buf , start, 2);
	}

	public int getReadIdleTime() {
		return readIdleTime;
	}

	public void setReadIdleTime(int readIdleTime) {
		this.readIdleTime = readIdleTime;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	
}
