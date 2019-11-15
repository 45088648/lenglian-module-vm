package com.beetech.module.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.beetech.module.activity.QueryDataAllActivity;
import com.beetech.module.application.MyApplication;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlueToothService {
	private String TAG = BlueToothService.class.getSimpleName();

	private MyApplication myApp;
	private Context context;
	private QueryDataAllActivity queryDataAllActivity;

	private static final String NAME = "ble_anquan365";
	public static int scanState = 0;
	private BluetoothLeScanner scanner = null;
	public Map<String, BluetoothDevice> printerMap = new HashMap<>();

	public BlueToothService(QueryDataAllActivity queryDataAllActivity) {
		this.context = queryDataAllActivity;
		this.queryDataAllActivity = queryDataAllActivity;
		this.myApp = (MyApplication) queryDataAllActivity.getApplicationContext();
		scanner = myApp.adapter.getBluetoothLeScanner();
	}

	public boolean isOpen() {
		synchronized (this) {
			if (myApp.adapter.isEnabled()) {
				return true;
			}

			myApp.adapter.enable();
			Toast.makeText(queryDataAllActivity, "开启蓝牙", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	// 扫描蓝牙设备
	public void scanDevice() {
		if(isOpen()){
		}
		if (myApp.adapter.isDiscovering()) {
			myApp.adapter.cancelDiscovery();
		}
		queryDataAllActivity.getPrinterList().clear();
		printerMap.clear();
		queryDataAllActivity.getPrinterListViewAdapter().notifyDataSetChanged();
		queryDataAllActivity.showLoading();

		startScanLe();
	}

	ScanCallback leCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			super.onScanResult(callbackType, result);
			BluetoothDevice device = result.getDevice();
			String name = device.getName();
			String address = device.getAddress();
			Log.d(TAG, "BLE name=================="+name);
			if (NAME.equals(name)) {

				String printer = name+"("+address+")";
				queryDataAllActivity.getPrinterList().add(printer);
				queryDataAllActivity.getPrinterListViewAdapter().notifyDataSetChanged();
				printerMap.put(printer, device);
                stopScanLe();
				queryDataAllActivity.hideLoading();
			}
		}

		@Override
		public void onScanFailed(int errorCode) {
			super.onScanFailed(errorCode);
			Log.d(TAG,"搜索失败");
		}
	};

	public void startScanLe() {
		if(scanState == 0) {
			scanner.startScan(leCallback);
			scanState = 1;
		}
	}

	public void stopScanLe() {
		if(scanState == 1){
			scanner.stopScan(leCallback);
			scanState = 0;
		}
	}
	BluetoothDevice device = null;
	BluetoothGatt bluetoothGatt = null;

	public void print(String printer){

		device = printerMap.get(printer);
		if(device != null){

			stopScanLe();
			BluetoothGatt bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback);
			Log.d(TAG, "bluetoothGatt=========="+bluetoothGatt);
			if(bluetoothGatt == null){
				queryDataAllActivity.toast("连接创建失败，请重试");
				queryDataAllActivity.hideLoading();
				return;
			}
			BluetoothGattService bluetoothGattServicePrint = null;
			BluetoothGattCharacteristic bluetoothGattCharacteristicPrint = null;
			SystemClock.sleep(1000);
			List<BluetoothGattService> bluetoothGattServices = null;
			int tryTimes = 0;

			do {
				SystemClock.sleep(1000);
				bluetoothGattServices = bluetoothGatt.getServices();
				Log.d(TAG, "bluetoothGattServices=========="+bluetoothGattServices.size());
				tryTimes++;
			} while((bluetoothGattServices == null || bluetoothGattServices.isEmpty()) && tryTimes < 5);

			if(bluetoothGattServices == null || bluetoothGattServices.isEmpty()){
				queryDataAllActivity.toast("未查询到打印服务，请重试");
				queryDataAllActivity.hideLoading();
				return;
			}
			for (BluetoothGattService bluetoothGattService : bluetoothGattServices) {
				String uuid = bluetoothGattService.getUuid().toString();
				int mServiceType = bluetoothGattService.getType();

				if(mServiceType == BluetoothGattService.SERVICE_TYPE_PRIMARY){
					bluetoothGattServicePrint = bluetoothGattService;
					Log.d(TAG, "=====================uuid_service="+uuid+", mServiceType="+mServiceType);

					List<BluetoothGattCharacteristic> characteristicses = bluetoothGattService.getCharacteristics();
					if(characteristicses != null && !characteristicses.isEmpty()){
						for (final BluetoothGattCharacteristic characteristic : characteristicses) {
							String uuid_characteristics = characteristic.getUuid().toString();
							int mProperties = characteristic.getProperties();
							int mPermissions = characteristic.getPermissions();

							if (!((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) == 0
									&& (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0)){ // 可写
								bluetoothGattCharacteristicPrint = characteristic;
								Log.d(TAG, "======uuid_characteristics="+uuid_characteristics+", uuid="+uuid+", mProperties="+mProperties+", mPermissions="+mPermissions);
							}
						}
					}
				}
			}

			if(bluetoothGattCharacteristicPrint == null) {
				queryDataAllActivity.toast("未查询到打印服务，请重试");
				queryDataAllActivity.hideLoading();
				return;
			}
			new PrintThread(bluetoothGatt, bluetoothGattCharacteristicPrint).start();
		}
	}

	public static int batchWriteSize = 10;
	class PrintThread extends Thread {
		BluetoothGatt bluetoothGatt = null;
		BluetoothGattCharacteristic bluetoothGattCharacteristicPrint = null;

		public PrintThread(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristicPrint){
			this.bluetoothGatt = bluetoothGatt;
			this.bluetoothGattCharacteristicPrint = bluetoothGattCharacteristicPrint;
		}

		@Override
		public void run() {
			//测试发数据 begin
			// String content = "中华人民共和国万岁，中国人民万岁，世界人民万岁\n\n\n";
			String content = queryDataAllActivity.getPrintStr();
			if(TextUtils.isEmpty(content)){
				return;
			}

			//分批发送
			int len = content.length();
			int page = len % batchWriteSize == 0 ? len / batchWriteSize : (len / batchWriteSize + 1);
			for (int i = 1; i <= page; i++) {
				int beginIndex = (i-1) * batchWriteSize;
				int endIndex = i * batchWriteSize;
				String contentPage = "";
				if(endIndex > len -1){
					contentPage = content.substring(beginIndex);
				} else {
					contentPage = content.substring(beginIndex, endIndex);
				}
//                Log.d(TAG, "contentPage========================"+contentPage);
				bluetoothGattCharacteristicPrint.setValue(contentPage.getBytes(Charset.forName("GB2312")));
				bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicPrint);
				SystemClock.sleep(40);
			}
			bluetoothGattCharacteristicPrint.setValue(new byte[]{0x0a});
			bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicPrint);
			queryDataAllActivity.hideLoading();
			try {
				if (bluetoothGatt != null) {
					bluetoothGatt.disconnect();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			//测试发数据 end
		}
	}

	BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
			    queryDataAllActivity.toast("连接成功，搜索打印服务");
				gatt.discoverServices();//连接成功，开始搜索服务，一定要调用此方法，否则获取不到服务
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			Log.d(TAG,gatt.getDevice().getName() + " write successfully. "+status);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			Log.d(TAG,gatt.getDevice().getName() + " recieved ");
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			Log.d(TAG,  "The response is ");
		}

	};
}