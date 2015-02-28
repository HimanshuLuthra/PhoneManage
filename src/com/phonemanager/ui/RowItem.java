package com.phonemanager.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.phonemanager.R;

public class RowItem {

	private Context context;
	private Long time;
	private String activity;
	private Drawable icon;
	private String packageName;
	private String timeToDisplay;

	public RowItem(Context context, String activity, Long time) {
		this.context = context;
		this.time = time;
		this.activity = activity;
		this.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
	}

	public RowItem(Context context) {
		this(context,"",0L);
	}

	public String getActivity() {
		return activity;
	}
	
	public Long getTime() {
		return time;
	}

	//time in seconds
	public String getTimeInString(Long time) {
		String finalTime = "";
		Long hours, minutes;

		hours = time / 3600;

		if (hours != 0) {
			finalTime = hours.toString();
			finalTime = finalTime.concat("h:");
		}
		minutes = time % 3600;

		minutes = minutes / 60;
		finalTime = finalTime.concat(minutes.toString() + "m");

		return finalTime;
	}	
	public Drawable getIcon() {
		return this.icon;
	}

	public boolean setActivity(String packageName) {
		this.packageName = packageName;
		ApplicationInfo mApplicationInfo;
		try {
			mApplicationInfo = context.getPackageManager().getApplicationInfo(
					packageName, 0);
			this.activity = (String) context.getPackageManager()
					.getApplicationLabel(mApplicationInfo);
			setIcon(packageName);
			return true;
		} catch (NameNotFoundException e) {
			mApplicationInfo = null;
			this.activity = Util.PACKAGE_NAME_ERROR;
			return false;
		}

	}
	public String getPackageName()
	{
		return this.packageName;
	}


	public void setTime(Long time) {
		this.time = time;
	}

	private void setIcon(String packageName) {
		try {
			this.icon = context.getPackageManager().getApplicationIcon(
					packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}


}
