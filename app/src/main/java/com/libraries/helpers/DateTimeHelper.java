package com.libraries.helpers;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateTimeHelper {
	
	public static String getStringDateFromTimeStamp(long timeStamp, String format) {
		// "ddd MM, yyyy"
		DateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date netDate = (new Date(timeStamp * 1000));
        return sdf.format(netDate);
	}
	
	public static String formatDate(String date, String format) {
		// "ddd MM, yyyy"
		DateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		try {
			Date parsedDate = sdf.parse(date);
			return parsedDate.toString();
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "";
	}

	public static Date getDateFromTimeStamp(long timeStamp) {
        Date netDate = new Date(timeStamp * 1000);
        return netDate;
	}

	public static long getCurrentDateInMillis() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        Timestamp timeStampDate = new Timestamp(date.getTime());
        return timeStampDate.getTime();
	}

	public static Timestamp getTimeStampFromCurrentDate() {
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		return ts;
	}

	public static String formateDateFromString(String inputFormat, String outputFormat, String inputDate){
		Date parsed = null;
		String outputDate = "";
		SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
		SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
		try {
			parsed = df_input.parse(inputDate);
			outputDate = df_output.format(parsed);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return outputDate;
	}

	public static String formateDateFromStringUTC(String inputFormat, String outputFormat, String inputDate){
		Date parsed = null;
		String outputDate = "";
		SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
		df_input.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
		try {
			parsed = df_input.parse(inputDate);
			outputDate = df_output.format(parsed);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return outputDate;
	}

	public static String formatDateFromDate(String inputFormat, String outputFormat, Date inputDate){
		String outputDate = "";
		SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
		try {
			outputDate = df_output.format(inputDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputDate;
	}

	public static Date formatDateFromStringToDate(String inputFormat, String inputDate){
		Date parsed = null;
		String outputDate = "";
		SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
		try {
			parsed = df_input.parse(inputDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return parsed;
	}
}
