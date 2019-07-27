package com.beetech.module.handler;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.beetech.module.R;
import com.beetech.module.activity.PrintActivity;
import com.beetech.module.service.BlueToothService;


/**
 * 连接打印机提示Handler
 * 
 * @author zcs
 * 
 */
public class BlueToothHandler extends Handler {
	private PrintActivity printActivity;

	public BlueToothHandler(PrintActivity printActivity) {
		this.printActivity = printActivity;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case BlueToothService.MESSAGE_STATE_CHANGE:// 蓝牙连接状态
			switch (msg.arg1) {
			case BlueToothService.STATE_CONNECTED:// 已经连接
				break;
			case BlueToothService.STATE_CONNECTING:// 正在连接
				Toast.makeText(printActivity, "正在连接蓝牙打印机", Toast.LENGTH_SHORT).show();
				break;
			case BlueToothService.STATE_LISTEN:
			case BlueToothService.STATE_NONE:
				break;
			case BlueToothService.SUCCESS_CONNECT:
				Toast.makeText(printActivity, printActivity.getResources().getString(R.string.str_succonnect), Toast.LENGTH_SHORT).show();
				printActivity.print();
				break;
			case BlueToothService.FAILED_CONNECT:
				Toast.makeText(printActivity, printActivity.getResources().getString(R.string.str_faileconnect), Toast.LENGTH_SHORT).show();
				break;
			case BlueToothService.LOSE_CONNECT:
				Toast.makeText(printActivity, printActivity.getResources().getString(R.string.str_lose), Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		case BlueToothService.MESSAGE_READ:
			// sendFlag = false;//缓冲区已满
			break;
		case BlueToothService.MESSAGE_WRITE:// 缓冲区未满
			// sendFlag = true;
			break;
		}
	}
}