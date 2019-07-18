package com.beetech.module.code.response;

import com.beetech.module.code.BaseResponse;
import com.beetech.module.utils.ByteUtilities;
import java.util.Date;

public class DeleteHistoryDataResponse extends BaseResponse {
	private int front;
	private int rear;
	private int pflashLength;

	private Date inputTime;

	public DeleteHistoryDataResponse(byte[] buf) {
		super(buf);
		inputTime = new Date();
	}

	@Override
	public void unpack() {
		if(buf == null || buf.length == 0) {
			return;
		}

		int start = 0;
		this.begin  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.packLen  = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		this.cmd = ByteUtilities.toUnsignedInt(buf[start]); start = start + 1;
		byte[] gwIdBytes = new byte[]{buf[start], buf[start+1], buf[start+2], buf[start+3]}; start = start + 4;
		this.gwId = ByteUtilities.asHex(gwIdBytes);
		this.front = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.rear = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.pflashLength = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.crc  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
		this.end  = ByteUtilities.makeIntFromByte2(buf, start); start = start + 2;
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

	public int getPflashLength() {
		return pflashLength;
	}

	public void setPflashLength(int pflashLength) {
		this.pflashLength = pflashLength;
	}
}
