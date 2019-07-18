package com.beetech.module.utils;

import java.io.UnsupportedEncodingException;

public class EncryStrUtils {

	public static String printType = "encrypt"; // 印机类型，加密/非加密,
												// 打适合RunToFree，否则适合老的打印机
												// jbm-141

	/**
	 * 送打印机之前的字符加密码
	 */
	public static int printer_encrypt_byte[] = { 0x00, 0x08, 0x03, 0x02, 0x0C, 0x0B, 0x0E, 0x09, 0x01, 0x07, 0x0D, 0x05, 0x04, 0x0A, 0x06, 0x0F, 0x80, 0x88, 0x83, 0x82, 0x8C, 0x8B, 0x8E, 0x89, 0x81, 0x87, 0x8D, 0x85, 0x84, 0x8A, 0x86, 0x8F, 0x30, 0x38, 0x33, 0x32, 0x3C, 0x3B, 0x3E, 0x39, 0x31, 0x37, 0x3D, 0x35, 0x34, 0x3A, 0x36, 0x3F, 0x20, 0x28, 0x23, 0x22, 0x2C, 0x2B, 0x2E, 0x29, 0x21, 0x27, 0x2D, 0x25, 0x24, 0x2A, 0x26, 0x2F, 0xC0, 0xC8, 0xC3, 0xC2, 0xCC, 0xCB, 0xCE, 0xC9, 0xC1, 0xC7, 0xCD, 0xC5, 0xC4, 0xCA, 0xC6, 0xCF, 0xB0, 0xB8, 0xB3, 0xB2, 0xBC, 0xBB, 0xBE, 0xB9, 0xB1, 0xB7, 0xBD, 0xB5, 0xB4, 0xBA, 0xB6, 0xBF, 0xE0, 0xE8, 0xE3, 0xE2, 0xEC, 0xEB, 0xEE, 0xE9, 0xE1, 0xE7, 0xED, 0xE5, 0xE4, 0xEA, 0xE6, 0xEF, 0x90, 0x98, 0x93, 0x92, 0x9C, 0x9B, 0x9E, 0x99, 0x91, 0x97, 0x9D, 0x95, 0x94,
			0x9A, 0x96, 0x9F, 0x10, 0x18, 0x13, 0x12, 0x1C, 0x1B, 0x1E, 0x19, 0x11, 0x17, 0x1D, 0x15, 0x14, 0x1A, 0x16, 0x1F, 0x70, 0x78, 0x73, 0x72, 0x7C, 0x7B, 0x7E, 0x79, 0x71, 0x77, 0x7D, 0x75, 0x74, 0x7A, 0x76, 0x7F, 0xD0, 0xD8, 0xD3, 0xD2, 0xDC, 0xDB, 0xDE, 0xD9, 0xD1, 0xD7, 0xDD, 0xD5, 0xD4, 0xDA, 0xD6, 0xDF, 0x50, 0x58, 0x53, 0x52, 0x5C, 0x5B, 0x5E, 0x59, 0x51, 0x57, 0x5D, 0x55, 0x54, 0x5A, 0x56, 0x5F, 0x40, 0x48, 0x43, 0x42, 0x4C, 0x4B, 0x4E, 0x49, 0x41, 0x47, 0x4D, 0x45, 0x44, 0x4A, 0x46, 0x4F, 0xA0, 0xA8, 0xA3, 0xA2, 0xAC, 0xAB, 0xAE, 0xA9, 0xA1, 0xA7, 0xAD, 0xA5, 0xA4, 0xAA, 0xA6, 0xAF, 0x60, 0x68, 0x63, 0x62, 0x6C, 0x6B, 0x6E, 0x69, 0x61, 0x67, 0x6D, 0x65, 0x64, 0x6A, 0x66, 0x6F, 0xF0, 0xF8, 0xF3, 0xF2, 0xFC, 0xFB, 0xFE, 0xF9, 0xF1, 0xF7, 0xFD, 0xF5, 0xF4, 0xFA, 0xF6, 0xFF };

	/**
	 * 将字符串编码成16进制
	 */
	public static String convertToHex(String str) {
		/*
		 * 16进制数字字符集
		 */
		String hexString = "0123456789ABCDEF";
		StringBuilder sb = null;
		// 根据默认编码获取字节数组
		byte[] bytes;
		try {
			bytes = str.getBytes("GB2312");
			sb = new StringBuilder(bytes.length * 2);
			// 将字节数组中每个字节拆解成2位16进制整数
			for (int i = 0; i < bytes.length; i++) {
				sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
				sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 返回加密后的打印字符串
	 */
	public static byte[] encryStrToByte(String str) {
		int len = str.length();
		byte[] retBytes = new byte[len / 2];

		int start = 0;
		int step = 2;
		int end = start + step;

		int i = 0;
		while (true) {

			if (start >= len) {
				break;
			}
			String temp = str.substring(start, end);
			retBytes[i++] = (byte) doData(temp);

			start = end;
			end = end + step;

			if (end >= len) {
				end = len;
			}
		}
		return retBytes;
	}

	// private static int doData(String subStr) {
	// int index = Integer.parseInt(subStr, 16);
	// return printType.equals("noencrypt") ? index :
	// printer_encrypt_byte[index];
	// }

	private static int doData(String subStr) {
		int index = -1;

		char highChar = subStr.charAt(0);
		char lowChar = subStr.charAt(1);

		boolean highLetter = new String("ABCDEF").contains(String.valueOf(highChar));
		boolean lowLetter = new String("ABCDEF").contains(String.valueOf(lowChar));

		// Log.e(TAG, "doData++++++++++++++++highLetter , lowLetter :   "
		// + highLetter + " , " + lowLetter);

		if (highLetter && !lowLetter) { // eg: c8
			switch (highChar) {
			case 'A':
				index = (10 * 16 + Integer.valueOf(String.valueOf(lowChar)));
				break;
			case 'B':
				index = (11 * 16 + Integer.valueOf(String.valueOf(lowChar)));
				break;
			case 'C':
				index = (12 * 16 + Integer.valueOf(String.valueOf(lowChar)));
				break;
			case 'D':
				index = (13 * 16 + Integer.valueOf(String.valueOf(lowChar)));
				break;
			case 'E':
				index = (14 * 16 + Integer.valueOf(String.valueOf(lowChar)));
				break;
			case 'F':
				index = (15 * 16 + Integer.valueOf(String.valueOf(lowChar)));
				break;
			}
		} else if (!highLetter && lowLetter) { // eg: 8c
			switch (lowChar) {
			case 'A':
				index = (Integer.valueOf(String.valueOf(highChar)) * 16 + 10);
				break;
			case 'B':
				index = (Integer.valueOf(String.valueOf(highChar)) * 16 + 11);
				break;
			case 'C':
				index = (Integer.valueOf(String.valueOf(highChar)) * 16 + 12);
				break;
			case 'D':
				index = (Integer.valueOf(String.valueOf(highChar)) * 16 + 13);
				break;
			case 'E':
				index = (Integer.valueOf(String.valueOf(highChar)) * 16 + 14);
				break;
			case 'F':
				index = (Integer.valueOf(String.valueOf(highChar)) * 16 + 15);
				break;
			}
		} else if (highLetter && lowLetter) { // eg: cc
			int highNumber = -1;
			int lowNumber = -2;
			switch (highChar) {
			case 'A':
				highNumber = 10;
				break;
			case 'B':
				highNumber = 11;
				break;
			case 'C':
				highNumber = 12;
				break;
			case 'D':
				highNumber = 13;
				break;
			case 'E':
				highNumber = 14;
				break;
			case 'F':
				highNumber = 15;
				break;
			}
			switch (lowChar) {
			case 'A':
				lowNumber = 10;
				break;
			case 'B':
				lowNumber = 11;
				break;
			case 'C':
				lowNumber = 12;
				break;
			case 'D':
				lowNumber = 13;
				break;
			case 'E':
				lowNumber = 14;
				break;
			case 'F':
				lowNumber = 15;
				break;
			}
			index = highNumber * 16 + lowNumber;

		} else { // eg: 88
			index = Integer.valueOf(String.valueOf(highChar)) * 16 + Integer.valueOf(String.valueOf(lowChar));
		}
		if (printType.equals("noencrypt")) {
			return index; // 非加密的打印机
		} else
			return printer_encrypt_byte[index]; // 加密的打印机
	}

	/**
	 * 加密文字
	 */
	public static byte[] encryptStr(String printStr) {
		String strToHex = convertToHex(printStr);
		byte[] encryStrToBytes = encryStrToByte(strToHex);
		return encryStrToBytes;
	}
}
