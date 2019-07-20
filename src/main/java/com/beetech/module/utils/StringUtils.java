package com.beetech.module.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to peform common String manipulation algorithms.
 */
public class StringUtils {
	public StringUtils() {}

	/********************************************************************
	 * 检查字符串是否是空字符串或者是null，空格作为空字符串处理
	 * 
	 * @param str 需要检查的字符串
	 * @return 如果要检查的字符串是一个空字符串""或是是null返回true，否则返回false
	 ********************************************************************/
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/*********************************************************************
	 * 判断某字符串是否为null或空字符串或由空白符构成，
	 * 对于制表符、换行符、换页符和回车符StringUtils.isBlank()均识为空白</p>
	 * 
	 * @param str 要检查的字符串
	 * @return 如果字符串是空null，或者是空字符""，或者用空白字符组成返回true，否则返回false
	 ********************************************************************/
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}
	/*********************************************************************
	 * 去掉字符串两端的控制符(control characters, char <= 32),如果输入为null则返回null
	 * 
	 * @param str  需要处理的字符串
	 * @return 去掉两端的控制字符串的字符，如果输入为null，返回null
	 ********************************************************************/
	public static String trim(String str) {
		return str == null ? null : str.trim();
	}
    /*********************************************************************
     *从字符串中删除全部空白字符
     *
     * @param str  需要处理的字符的，可以是null
     * @return 返回没用空白字符的字符串，输入null，返回null
     ********************************************************************/
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }
	/********************************************************
	 * 此处插入方法说明。 创建日期：(2002-7-24 22:29:20)
	 * @param str  ：java.lang.String
	 * @return java.lang.String
	 ********************************************************/
	public static final String nullToKong(String str) {
		if (str == null || str.equals("null"))
			return "";
		else
			return str;
	}
	/********************************************************
	 * 通过对传入的输入值判断是否有非法字符
	 * 
	 * @param InputString 传入的字符串的值 对“<>"' ”进行Html转换防止sql错误，或者页面错误
	 * @return returnValse returnValse builder 李志东 build on 2006/03/15
	 *********************************************************/
	public static String escapeForSQL(String InputString) {
		if (InputString == null) {
			return null;
		}
		char ch;
		int i = 0;
		int last = 0;
		char[] input = InputString.toCharArray();
		int len = input.length;
		StringBuffer out = new StringBuffer((int) (len * 1.3));
		for (; i < len; i++) {
			ch = input[i];
			if (ch > '>') {
				continue;
			} else if (ch == '<') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(LT_ENCODE);
			} else if (ch == '&') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(AMP_ENCODE);
			} else if (ch == '"' || ch == '\'') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(QUOTE_ENCODE);
			} else if (ch == ' ') {
				if (i > last) {
					out.append(input, last, i - last);
				}
				last = i + 1;
				out.append(SPACE_ENCODE);
			}

		}
		if (last == 0) {
			return InputString;
		}
		if (i > last) {
			out.append(input, last, i - last);
		}
		return out.toString();
	}
	/*****************************************************************
	 * 转换一个字符数组为16进制字符串
	 * 
	 * @param bytes ：需要转换的字节数据
	 * @return String ：16进制字符串
	 ***************************************************************/
	public static String encodeHex(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**********************************************************************
	 * 转换一个16进制的字符串成为字节数组
	 * 
	 * @param hex 16进制编码字符串.
	 * @return 
	 **********************************************************************/
	public static byte[] decodeHex(String hex) {
		char[] chars = hex.toCharArray();
		byte[] bytes = new byte[chars.length / 2];
		int byteCount = 0;
		for (int i = 0; i < chars.length; i += 2) {
			byte newByte = 0x00;
			newByte |= hexCharToByte(chars[i]);
			newByte <<= 4;
			newByte |= hexCharToByte(chars[i + 1]);
			bytes[byteCount] = newByte;
			byteCount++;
		}
		return bytes;
	}

	// *********************************************************************
	// * Base64 - a simple base64 encoder and decoder.
	// *
	// * Copyright (c) 1999, Bob Withers - bwit@pobox.com
	// *
	// * This code may be freely used for any purpose, either personal
	// * or commercial, provided the authors copyright notice remains
	// * intact.
	// *********************************************************************

	/****************************************
	 * 编码一个字符串为base64字符串
	 * 
	 * @param data 需要编码的字符串
	 * @return 一个base64编码的字符串.
	 *****************************************/
	public static String encodeBase64(String data) {
		return encodeBase64(data.getBytes());
	}

	/********************************************
	 * 编码一个字符串为base64字符串
	 * 
	 * @param data 需要编码的字节数组
	 * @return 一个base64字符串.
	 *********************************************/
	public static String encodeBase64(byte[] data) {
		int c;
		int len = data.length;
		StringBuffer ret = new StringBuffer(((len / 3) + 1) * 4);
		for (int i = 0; i < len; ++i) {
			c = (data[i] >> 2) & 0x3f;
			ret.append(cvt.charAt(c));
			c = (data[i] << 4) & 0x3f;
			if (++i < len)
				c |= (data[i] >> 4) & 0x0f;

			ret.append(cvt.charAt(c));
			if (i < len) {
				c = (data[i] << 2) & 0x3f;
				if (++i < len)
					c |= (data[i] >> 6) & 0x03;

				ret.append(cvt.charAt(c));
			} else {
				++i;
				ret.append((char) fillchar);
			}

			if (i < len) {
				c = data[i] & 0x3f;
				ret.append(cvt.charAt(c));
			} else {
				ret.append((char) fillchar);
			}
		}
		return ret.toString();
	}

	/*****************************************
	 * 解码一个base64字符串.
	 * 
	 * @param data 需要解码的base64字符串
	 * @return String 解码字符串
	 *****************************************/
	public static String decodeBase64(String data) {
		return decodeBase64(data.getBytes());
	}

	/****************************************
	 * 解码一个base64字符串.
	 * 
	 * @param data 需要解码的Base64字节数组
	 * @return String 解码字符串
	 *****************************************/
	public static String decodeBase64(byte[] data) {
		int c, c1;
		int len = data.length;
		StringBuffer ret = new StringBuffer((len * 3) / 4);
		for (int i = 0; i < len; ++i) {
			c = cvt.indexOf(data[i]);
			++i;
			c1 = cvt.indexOf(data[i]);
			c = ((c << 2) | ((c1 >> 4) & 0x3));
			ret.append((char) c);
			if (++i < len) {
				c = data[i];
				if (fillchar == c)
					break;

				c = cvt.indexOf((char) c);
				c1 = ((c1 << 4) & 0xf0) | ((c >> 2) & 0xf);
				ret.append((char) c1);
			}

			if (++i < len) {
				c1 = data[i];
				if (fillchar == c1)
					break;

				c1 = cvt.indexOf((char) c1);
				c = ((c << 6) & 0xc0) | c1;
				ret.append((char) c);
			}
		}
		return ret.toString();
	}
    /*********************************************************
     * 生成一个指定长度随机字符串，随机字符串由阿拉伯数字和英文字母组成
     *
     * @param count  随机字符串长度
     * @return 指定长度的随机字符串
     **********************************************************/
    public static String randomAlphanumeric(int count) {
        return random(count, 0, 0, true, true, null, RANDOM);
    }
    /*********************************************************
     * 生成一个指定长度随机字符串，随机字符串由阿拉伯数字组成
     *
     * @param count  随机字符串长度
     * @return 指定长度的随机字符串
     **********************************************************/
    public static String randomNumeric(int count) {
        return random(count, 0, 0, false, true, null, RANDOM);
    }
    /*********************************************************
     * 生成一个指定长度随机字符串，随机字符串由英文字母组成
     *
     * @param count  随机字符串长度
     * @return 指定长度的随机字符串
     **********************************************************/
    public static String randomAlphabetic(int count) {
        return random(count, 0, 0, true, false, null, RANDOM);
    }

	/*******************************************************
	 * 页面到数据库调用char2Gb2312
	 ******************************************************/ 
	public static String char2Gb2312(String strInString) {
		String str = "";
		try {
			if (strInString == null || strInString.equals(""))
				return strInString;
			str = new String(strInString.getBytes("UTF-8"), "gb2312");
			return str;
		} catch (Exception e) {
			return "char2Gb2312转换错误！";
		}
	}
	
	/*******************************************************
	 * 页面到数据库调用char2Gb2312
	 ******************************************************/ 
	public static String IsoToUTF(String strInString) {
		String str = "";
		try {
			if (strInString == null || strInString.equals(""))
				return strInString;
			str = new String(strInString.getBytes("iso8859-1"),"UTF-8");
			return str;
		} catch (Exception e) {
			return "IsoToUTF转换错误！";
		}
	}
	/******************************************************
	 *  数据库到页面调用char2ISO
	 ******************************************************/
	public static String char2ISO(String strInString) {
		String str = "";
		try {
			if (strInString == null || strInString.equals(""))
				return strInString;
			str = new String(strInString.getBytes("gbk"), "ISO8859-1");
			return str;
		} catch (Exception e) {
			return "char2Gb2312转换错误！";
		}
	}
	/***********************************************************
	 * 更具字节长度截取字符串,如果字符串中有汉字不截断汉字，要保留完整汉字
	 * @param str 需要截取的字符串
	 * @param btyeLength 需要截取的字节长度
	 * @return 截取后的子字符串，如果输入为空字符串或者null，返回空字符串""
	 ************************************************************/
	public static String getSubString(String str,int btyeLength){
		if(isEmpty(str)) return "";
		StringBuffer retn = new StringBuffer();
		char[] ch = str.toCharArray();
		int n = ch.length;
		int count = 0;// 记录字节数
		for(int i = 0 ; i < n ; i++){
			if(count>=btyeLength){
				break;
			}else{
				retn.append(ch[i]);
			}
			count = count + String.valueOf(ch[i]).getBytes().length;
		}
		return retn.toString();
	}
	//-------------------------------------------------------private---------------------------------------------------------------
	/*************************************************************************************
	 * Returns the the byte value of a hexadecmical char (0-f). It's assumed
	 * that the hexidecimal chars are lower case as appropriate.
	 * 
	 * @param ch
	 *            a hexedicmal character (0-f)
	 * @return the byte value of the character (0x00-0x0F)
	 */
	private static final byte hexCharToByte(char ch) {
		switch (ch) {
		case '0':
			return 0x00;
		case '1':
			return 0x01;
		case '2':
			return 0x02;
		case '3':
			return 0x03;
		case '4':
			return 0x04;
		case '5':
			return 0x05;
		case '6':
			return 0x06;
		case '7':
			return 0x07;
		case '8':
			return 0x08;
		case '9':
			return 0x09;
		case 'a':
			return 0x0A;
		case 'b':
			return 0x0B;
		case 'c':
			return 0x0C;
		case 'd':
			return 0x0D;
		case 'e':
			return 0x0E;
		case 'f':
			return 0x0F;
		}
		return 0x00;
	}
	private static final int fillchar = '=';

	private static final String cvt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "abcdefghijklmnopqrstuvwxyz" + "0123456789+/";
	// constants used by escapeHTMLTags
	private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();

	private static final char[] AMP_ENCODE = "&amp;".toCharArray();

	private static final char[] LT_ENCODE = "&lt;".toCharArray();

	private static final char[] GT_ENCODE = "&gt;".toCharArray();

	private static final char[] APOS_ENCODE = "&apos;".toCharArray();

	private static final char[] SPACE_ENCODE = "&nbsp;".toCharArray();

	/************************************************************************************
	 * Initialization lock for the whole class. Init's only happen once per
	 * class load so this shouldn't be a bottleneck.
	 */
	private static Object initLock = new Object();
	
	private static final Random RANDOM = new Random();
	
    /***********************************************************************************
     * <p>Creates a random string based on a variety of options, using
     * supplied source of randomness.</p>
     *
     * <p>If start and end are both <code>0</code>, start and end are set
     * to <code>' '</code> and <code>'z'</code>, the ASCII printable
     * characters, will be used, unless letters and numbers are both
     * <code>false</code>, in which case, start and end are set to
     * <code>0</code> and <code>Integer.MAX_VALUE</code>.
     *
     * <p>If set is not <code>null</code>, characters between start and
     * end are chosen.</p>
     *
     * <p>This method accepts a user-supplied {@link Random}
     * instance to use as a source of randomness. By seeding a single 
     * {@link Random} instance with a fixed seed and using it for each call,
     * the same random sequence of strings can be generated repeatedly
     * and predictably.</p>
     *
     * @param count  the length of random string to create
     * @param start  the position in set of chars to start at
     * @param end  the position in set of chars to end before
     * @param letters  only allow letters?
     * @param numbers  only allow numbers?
     * @param chars  the set of chars to choose randoms from.
     *  If <code>null</code>, then it will use the set of all chars.
     * @param random  a source of randomness.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     *  <code>(end - start) + 1</code> characters in the set array.
     * @throws IllegalArgumentException if <code>count</code> &lt; 0.
     * @since 2.0
     */
    public static String random(int count, int start, int end, boolean letters, boolean numbers,
                                char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if ((start == 0) && (end == 0)) {
            end = 'z' + 1;
            start = ' ';
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        char[] buffer = new char[count];
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if ((letters && Character.isLetter(ch))
                || (numbers && Character.isDigit(ch))
                || (!letters && !numbers)) 
            {
                if(ch >= 56320 && ch <= 57343) {
                    if(count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if(ch >= 55296 && ch <= 56191) {
                    if(count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if(ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }
    /**
	 * Replaces all instances of oldString with newString in line. The count
	 * Integer is updated with number of replaces.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * 
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString,
			String newString, int[] count) {
		if (line == null) {
			return "";
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			int counter = 0;
			counter++;
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				counter++;
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			count[0] = counter;
			return buf.toString();
		}
		return line;
	}
    /**
     * double28 判断字符是奇数还是偶数
     * @param number 
     * @return 返回 even 或 odd，如果不是数字等其它情况 返回 null
     */
    public static String getParity(String number){
    	if(null == number) return null;
    	int testNumber ;
    	try{
    		testNumber = Integer.parseInt(number.trim());    		
    	    return testNumber % 2 == 0?"even":"odd"; 
    	}catch(Exception e){
    		return null;
    	}
    	
    }
    /**
     * 试题分析，1248@替换成abcde
     * @param args
     */
    public static String getABCDE(String args){
    	Map<String,String> tempMap = new HashMap<String,String>();
    	tempMap.put("1", "A");
    	tempMap.put("2", "B");
    	tempMap.put("4", "C");
    	tempMap.put("8", "D");
    	tempMap.put("@", "E");
    	return tempMap.containsKey(args) ? tempMap.get(args):"";
    }
    
	/**
	 *  验证必须为数字（整数或小数）
	 */
	public static boolean isNumber(String numStr) {
		String pattern = "(-)?[0-9]+(.[0-9]+)?";
		// 对()的用法总结：将()中的表达式作为一个整体进行处理，必须满足他的整体结构才可以。
		// (.[0-9]+)? ：表示()中的整体出现一次或一次也不出现
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(numStr);
		return m.matches();
	}
	
    public static void main(String args[]){
    	System.out.println(StringUtils.randomAlphanumeric(100));
    	System.out.println(StringUtils.randomAlphabetic(100));
    	System.out.println(StringUtils.randomNumeric(100));
    }

    
}
