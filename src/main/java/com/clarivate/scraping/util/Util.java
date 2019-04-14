package com.clarivate.scraping.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	public static boolean isNull(String str) {
		if (str == null) {
			return true;
		} else if (str.trim().length() <= 0) {
			return true;
		} else if ("null".equalsIgnoreCase(str)) {
			return true;
		}
		return false;
	}

	public static boolean isNull(Date dt) {
		if (dt == null) {
			return true;
		} else if (String.valueOf(dt) == null) {
			return true;
		} else if (String.valueOf(dt).trim().length() <= 0) {
			return true;
		}
		return false;
	}

	public static Date getTime(String str) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		try {
			return sdf.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
