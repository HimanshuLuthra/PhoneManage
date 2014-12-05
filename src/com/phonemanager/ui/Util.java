package com.phonemanager.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.widget.Toast;

public class Util {

	public static String PACKAGE_NAME_ERROR = "(unknown)";
	public static String TODAY = "Today";
	public static String WEEKLY = "Weekly";
	public static String MONTHLY = "Monthly";
	public static boolean packageRemoved = false;
	private static String packageToRemoveName;
	public static int noOfApplicationsToShowToday = 5;
	public static int noOfApplicationsToShowWeekly = 10;
	public static int noOfApplicationsToShowMonthly = 15;
	public static long normaliseTime(long currentData) {
		return (currentData / 1000L);
	}

	public static long timeZoneCorrection() {
		Calendar c = Calendar.getInstance();
		TimeZone z = c.getTimeZone();
		long offset = z.getRawOffset();

		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}
		return offset;
	}

	public static void toast(String text, Context context) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static long getTimeSlotStart(int position) {
		switch (position) {
		case 1: //return week start
			return (((new Date()).getTime()) / 86400000L) * 86400000L - 7 * 86400000L;
		case 2: //return month start
			return (((new Date()).getTime()) / 86400000L) * 86400000L - 30 * 86400000L;
		default: //by default return day start
			return (((new Date()).getTime()) / 86400000L) * 86400000L;
		}
	}

	public static String getPackageToRemoveName() {
		return packageToRemoveName;
	}

	public static void setPackageToRemoveName(String packageToRemoveName) {
		Util.packageToRemoveName = packageToRemoveName;
	}

}
