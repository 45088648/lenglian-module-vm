package com.beetech.module.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换工具
 */
public class DateUtils {
   
	public static final String C_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss SSS";
	public static final String C_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public static final String C_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	
	public static final String C_YYYY_MM = "yyyy-MM";
	
	public static final String C_YYYY_MM_DD = "yyyy-MM-dd";

	public static final String C_YYYYMMDD = "yyyyMMdd";

	public static final String C_HHMMSS = "HH:mm:ss";

	public static final String C_YYYY = "yyyy";
	
	public static final String C_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String C_YYMMDDHHMMSS = "yyMMddHHmmss";

	public static final String[] C_WEAK = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

	

	/*************************************************
	 * 获得date类型的当前日期
	 * @return Date类型的当前日期
	 **************************************************/
	public static Date getCurrentDate() {
		/*Calendar cal = Calendar.getInstance();
		Date currDate = cal.getTime();
		return currDate;*/
		long now = System.currentTimeMillis();
        java.sql.Date vDate = new java.sql.Date(now);
        return vDate;
	}
	/*************************************************
	 * 使用Calendar.getInstance().getTime()获得date类型的当前日期
	 * @return Date类型的当前日期
	 **************************************************/
	public static Date getCurrentDate2(){
		return new Date();
	}
	/*************************************************
	 * 根据时间值获得时间对象
	 * @return Date类型的日期对象
	 **************************************************/
	public static Date getLongToDate(Long value){
        java.sql.Date vDate = new java.sql.Date(value);
        return vDate;
	}
	/***************************************************
	 * 获得字符串类型的当前日期，默认格式yyyy-MM-dd
	 * @return String类型的当前日期
	 **************************************************/
	public static String getCurrentDateStr() {
		Calendar cal = Calendar.getInstance();
		Date currDate = cal.getTime();

		return parseDateToString(currDate,C_YYYY_MM_DD);
	}
	
	/***************************************************
	 * 获得字符类型的当前日期，日期格式为strFormat
	 * @param strFormat 格式日期字符串
	 * @return String类型的当前日期
	 **************************************************/
	public static String getCurrentDateStr(String strFormat) {
		Calendar cal = Calendar.getInstance();
		Date currDate = cal.getTime();

		return parseDateToString(currDate, strFormat);
	}

	/***************************************************
	 * 转换String类型的日期到Date类型，格式yyyy-mm-dd
	 * @param dateValue 字符类型日期
	 * @return date类型日期
	 **************************************************/
	public static Date parseStringToDate(String dateValue) {
		return parseStringToDate(dateValue,C_YYYY_MM_DD);
	}

	/***************************************************
	 * 转换String类型的日期到Date类型，格式为strFormat，默认格式yyyy-mm-dd
	 * @param strFormat 格式字符串
	 * @param dateValue String类型的日期
	 * @return Date 获得指定格式的日期对象，如果出现异常返回null
	 **************************************************/
	public static Date parseStringToDate(String dateValue, String strFormat) {
		if (dateValue == null)
			return null;
		if (strFormat == null)
			strFormat = C_YYYY_MM_DD;

		SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
		Date newDate = null;

		try {
			newDate = dateFormat.parse(dateValue);
		} catch (ParseException pe) {
			newDate = null;
		}
		return newDate;
	}

	/*****************************************************
	 * 将date类型的日期转换为系统参数定义的格式的字符串,格式yyyy-mm-dd。
	 * @param dateValue  需要转换的日期。
	 * @return 转换后符合给定格式的日期字符串
	 ****************************************************/
	public static String parseDateToString(Date dateValue) {
		return parseDateToString(dateValue, C_YYYY_MM_DD);
	}

	/*****************************************************
	 * 将Date类型的日期转换为系统参数定义的格式的字符串,格式strFormat。
	 * @param dateValue 需要转换的日期
	 * @param strFormat 格式字符串
	 * @return 指定字符格式的日期字符串
	 ****************************************************/

	public static String parseDateToString(Date dateValue, String strFormat) {
		if (dateValue == null || strFormat == null)
			return null;

		SimpleDateFormat dateFromat = new SimpleDateFormat();
		dateFromat.applyPattern(strFormat);

		return dateFromat.format(dateValue);
	}

	/*****************************************************
	 * 取得指定日期N天后的日期
	 * @param date 指定时间
	 * @param days 天数
	 * @return 指定时间days天后的日期对象
	 ****************************************************/ 
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	/*****************************************************
	 * 计算两个日期之间相差的天数
	 * @param date1
	 * @param date2
	 * @return 两个日期相差的天数
	 ****************************************************/
	public static int daysBetween(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / 86400000;//(1000 * 3600 * 24 = 86400000);

		return Integer.parseInt(String.valueOf(between_days));
	}
	
	/*****************************************************
	 * 根据指定的日期获得对应的星期字符串
	 * @param vdate 指定的日期字符，格式yyyy-MM-dd或yyyy-MM-dd hh:mm:ss
	 * @return 日期字符串，如果给定的日期字符串不符合要求返回null
	 ****************************************************/
	public static String getWeek(String vdate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = parseStringToDate(vdate, C_YYYY_MM_DD);
			if(date == null){
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int i = calendar.get(calendar.DAY_OF_WEEK) - 1;			
		return C_WEAK[i];
	}
	/*****************************************************
	 * 
	 * 
	 * @return 日期字符串及星期字符串
	 ****************************************************/
	public static String getDateAndWeekString(String dateFormat) {
		String rtn = "";
		if(dateFormat == null || dateFormat.equals("")){
			dateFormat = C_YYYY_MM_DD;
		}

		Calendar calendar = Calendar.getInstance();
		Date currDate = calendar.getTime();
		int i = calendar.get(calendar.DAY_OF_WEEK) - 1;
		try{
			SimpleDateFormat dateFromat = new SimpleDateFormat();
			dateFromat.applyPattern(dateFormat);
			rtn = dateFromat.format(currDate);
		}catch(Exception e){
			e.printStackTrace();
		}
		return rtn + " " + C_WEAK[i];

	}
	public static String getDateAndWeekString2(String dateFormat) {
		String rtn = "";
		if(dateFormat == null || dateFormat.equals("")){
			dateFormat = C_YYYY_MM_DD;
		}

		Calendar calendar = Calendar.getInstance();
		Date currDate = calendar.getTime();
		int i = calendar.get(calendar.DAY_OF_WEEK) - 1;
		try{
			SimpleDateFormat dateFromat = new SimpleDateFormat();
			dateFromat.applyPattern(dateFormat);
			rtn = dateFromat.format(currDate);
		}catch(Exception e){
			e.printStackTrace();
		}
		return rtn;

	}
	/*****************************************************
	 * 比较当前日期和指定日期，如果指定日期晚于当前日期返回true
	 * @return 如果指定日期晚于当前日期返回true，指定日期早于当前日期返回false，指定日期为null返回false；
	 ****************************************************/
	public static boolean compareDate(Date vDate){
		if(vDate == null) return false;
		Date cDate = getCurrentDate();
		return  cDate.before(vDate);
	}
	/*******************************************************
	 * 比较当前日期是否在指定的起始日期和截止日期内（报考等于起始日期或截止日期）
	 * 如果在指定的日期之内返回true，如果不在指定的日期内返回false
	 * @param beginDate 起始日期，长格式日期
	 * @param endDate   截止日期  长格式日期
	 ********************************************************/
	public static boolean compareDate(Date beginDate,Date endDate){
		boolean retn ;
		Date cDate = getCurrentDate();
		if(cDate.after(beginDate) && cDate.before(endDate)){
			retn = true;
		}else{
			retn = false;
		}
		return retn;
	}
	/*******************************************************
	 * 比较当前日期是否在指定的起始日期和截止日期内（报考等于起始日期或截止日期）
	 * 如果在指定的日期之内返回true，如果不在指定的日期内返回false
	 * @param beginDate 起始日期，短格式日期
	 * @param endDate   截止日期  短格式日期
	 ********************************************************/
	public static boolean compareShortDate(Date beginDate,Date endDate){
		boolean retn ;
		if(beginDate == null || endDate == null){
			retn = false;
		}else{
			Date cDate = getCurrentDate();
			if(cDate.after(beginDate) && cDate.before(addDays(endDate,1))){
				retn = true;
			}else{
				retn = false;
			}
		}
		return retn;
	}
	/********************************************************
	 * 此处插入方法说明。 创建日期：(2002-7-24 22:29:20)
	 * @return java.lang.String
	 ********************************************************/
	public static final String nullToKong(Date date) {
		if (date == null || date.toString().equals("null"))
			return "";
		else
			return parseDateToString(date,C_YYYY_MM_DD_HH_MM_SS);
	}
	/**
	 * 功能：格式化日期。 创建日期：(2002-8-18 下午 09:47:08)
	 * 
	 * @param vDate  :(日期对象java.util.Date)
	 * @param i      :  1 则返回YYYY-MM-DD格式的日期字符。 i = 2
	 *                                   则返回YYYY-MM-DD HH:MM:SS格式的日期字符。 i = 3
	 *                                   则返回YYYY格式的日期字符。 否则返回YYYY-MM-DD格式的日期字符。
	 * @return java.lang.String
	 */
	public static final String formatDateTime(Date vDate, int i) {
		if (vDate == null)
			return null;
		SimpleDateFormat formatter;
		if (i == 1)
			formatter = new SimpleDateFormat("yyyy-MM-dd");
		else if (i == 2) {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if (i == 3) {
			formatter = new SimpleDateFormat("yyyy");
		} else  if (i == 4) {
			formatter = new SimpleDateFormat("yyyy年MM月dd日");
		}else{
			formatter = new SimpleDateFormat("yyyy-MM-dd");
		}

		String dateString = formatter.format(vDate);
		return dateString;
	}
	
	public static final String getCurrentYear(){
		return new Integer(Calendar.getInstance().get(Calendar.YEAR)).toString();
	}
	
	/**
	 * 上个月开始时间
	 * @return
	 */
	public static Date getLastMothFirstTime(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 上个月结束时间
	 * @param time
	 * @return
	 */
	public static Date getLastMonthLastTime(Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		int month = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}
	
	/**
	 *  如果数据采集年份为2000年，将当前年份设置给数据时间
	 * @param devTempDatetime
	 * @return
	 */
	public static Date checkYear(Date devTempDatetime) {
		if(devTempDatetime != null){
			Calendar cal = Calendar.getInstance();
			int nowYear= cal.get(Calendar.YEAR);// 当前年份
			
			cal.setTime(devTempDatetime);
			int year = cal.get(Calendar.YEAR);
			if(year == 2000){
				cal.set(Calendar.YEAR, nowYear);
				devTempDatetime = cal.getTime();
			}
		}
		return devTempDatetime;
	}
	
	public static void main(String[] args) {
//		System.out.println(DateUtils.getCurrentDateStr("yyyy-MM-dd HH:mm:ss")+"----------");
//		String date = "2008-12-10 10:51:30";
//		System.out.println("当前日期date：" + DateUtils.getCurrentDate());
//		Calendar startCalendar = Calendar.getInstance();
//		System.out.println("当前日期date：" + Calendar.getInstance().getTime());//startCalendar.getTime());
//		System.out.println("当前日期date：" + compareDate(DateUtils.parseStringToDate(date, DateUtils.C_YYYY_MM_DD_HH_MM_SS)));
//		System.out.println("当前日期date：" + DateUtils.parseStringToDate(DateUtils.getCurrentDate().toString(), DateUtils.C_YYYY_MM_DD_HH_MM_SS));
//		System.out.println("当前日期date：" + DateUtils.getCurrentDateStr());
		
//		Date time = DateUtils.getLastMothFirstTime(new Date());
//		System.out.println(DateUtils.parseDateToString(time ,DateUtils.C_YYYY_MM_DD_HH_MM_SS));
//
//		Date strDateTo = DateUtils.getLastMonthLastTime(new Date());  
//		System.out.println(DateUtils.parseDateToString(strDateTo,DateUtils.C_YYYY_MM_DD_HH_MM_SS));
		
		Date devTempDatetime = parseStringToDate("20000423101129", C_YYYYMMDDHHMMSS);
		System.out.println(checkYear(devTempDatetime));
	}

}
