package com.guudint.clickargo.clictruck.sage.service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class SageUtil {

	/**
	 * 
	 * @param date
	 * @return like:2023 07 13 00:00:00
	 * @throws ParseException
	 */
	public Date getYesterdayBeginDate(Date date) throws ParseException {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1); // yesterday
 		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();

	}
	/**
	 * 
	 * @param date
	 * @return like:2023 07 13 00:00:00
	 * @throws ParseException
	 */
	public Date getBeginDate(Date date) throws ParseException {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();

	}

	/**
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public Date getEndDate(Date date) throws ParseException {

		Date bDate = this.getBeginDate(date);

		Calendar cal = Calendar.getInstance();
		cal.setTime(bDate);
		cal.add(Calendar.DAY_OF_YEAR, 1);

		return cal.getTime();
	}
	
	public Date getYeterdayLastMMilliSecond(Date date) throws ParseException {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		//minus 1 millonsecond
		cal.add(Calendar.MILLISECOND, -1);

		return cal.getTime();
	}
	
	

}
