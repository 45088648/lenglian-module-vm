package com.beetech.module.utils;

public final class ByteUtils {
	// 高位在前，低位在后
	public static byte[] int2bytes(int num) {
		byte[] result = new byte[2];
		// result[0] = (byte)((num >>> 24) & 0xff);//说明一
		// result[1] = (byte)((num >>> 16)& 0xff );
		// result[2] = (byte)((num >>> 8) & 0xff );
		// result[3] = (byte)((num >>> 0) & 0xff );
		result[0] = (byte) ((num >>> 8) & 0xff);// 说明一
		result[1] = (byte) ((num >>> 0) & 0xff);
		return result;
	}

	// 高位在前，低位在后
	public static int bytes2int(byte[] bytes) {
		int result = 0;
		if (bytes.length == 2) {
			// int a = (bytes[0] & 0xff) << 24;//说明二
			// int b = (bytes[1] & 0xff) << 16;
			// int c = (bytes[2] & 0xff) << 8;
			// int d = (bytes[3] & 0xff);
			// result = a | b | c | d;
			int a = (bytes[0] & 0xff) << 8;// 说明二
			int b = (bytes[1] & 0xff);
			result = a | b;
		}
		return result;
	}
}
