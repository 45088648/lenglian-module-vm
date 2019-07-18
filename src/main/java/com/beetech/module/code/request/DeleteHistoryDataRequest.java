package com.beetech.module.code.request;

import com.beetech.module.code.BaseRequest;
import com.beetech.module.utils.ByteUtilities;

import java.util.Arrays;

public class DeleteHistoryDataRequest extends BaseRequest{
	private int front;
	private int rear;

	public DeleteHistoryDataRequest(String gwId) {
		super();
		setPackLen(Integer.valueOf("09", 16));
		setCmd(Integer.valueOf("03", 16));
		this.front = 0;
		this.rear = 0;
		setGwId(gwId);
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
		byte[] gwIdBytes = ByteUtilities.asByteArray(getGwId());
		for (byte b : gwIdBytes) {
			Arrays.fill(buf, start, ++start, b);
		}
		ByteUtilities.intToNetworkByteOrder(getFront() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getRear() , buf , start, 2); start = start+2;
		
		ByteUtilities.intToNetworkByteOrder(getCrc() , buf , start, 2); start = start+2;
		ByteUtilities.intToNetworkByteOrder(getEnd() , buf , start, 2);
		
	}

	public int getFront() {
		return front;
	}

	public void setFront(int front) {
		this.front = front;
	}

	public int getRear() {
		return rear;
	}

	public void setRear(int rear) {
		this.rear = rear;
	}
}
