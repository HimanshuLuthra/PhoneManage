package com.phonemanager.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonemanager.R;
import com.phonemanager.service.PMConstants;

public class AppListAdapter extends ArrayAdapter<RowItem> {
	private final Context context;
	private ArrayList<RowItem> packageList;
	private ArrayList<RowItem> itemsToShow;
	private RowItemComparator mComparator;
	private String TAG = getClass().getSimpleName();
	private int position;

	public AppListAdapter(Context context) {
		super(context, R.layout.app_list_row);
		this.context = context;
		mComparator = new RowItemComparator();
	}

	public void updatePackageList(int position,
			final HashMap<String, Long> newDataMap) {
		if (newDataMap == null)
			return;

		this.position = position;
		Log.d("TEST:::", "UTIL IS STILL??? " + Util.packageRemoved
				+ "packageToRemove is " + Util.getPackageToRemoveName());
		if (Util.packageRemoved) {
			Util.packageRemoved = false;
		}
		if (packageList == null)
			packageList = new ArrayList<RowItem>();
		if (itemsToShow == null){
			itemsToShow = new ArrayList<RowItem>();
		}
		boolean modificationFlag = false;
		HashMap<String, Long> newDataMapCopy = new HashMap<String, Long>(
				newDataMap);
		Log.d(TAG, "Start Hash map size :" + newDataMapCopy.toString());

		for (int i = 0; i < packageList.size(); i++) {
			String key = packageList.get(i).getPackageName();
			Long value = packageList.get(i).getTime();
			Log.d("TEST:::", "packageList.get(i) "
					+ packageList.get(i).getPackageName()+"time is "+value);
			if (key.equals(Util.getPackageToRemoveName())) {
				Log.d("TEST:::",
						"REMOVED PACKAGE IS " + Util.getPackageToRemoveName());
				packageList.remove(i);
				continue;
			}
			if (newDataMapCopy.containsKey(key)) {

				if (newDataMapCopy.get(key) != value) {
					modificationFlag = true;
					packageList.get(i).setTime(newDataMapCopy.get(key));

				}
				newDataMapCopy.remove(key);
			}
		}

		Iterator<Entry<String, Long>> mapIterator = newDataMapCopy.entrySet()
				.iterator();
		while (mapIterator.hasNext()) {
			modificationFlag = true;
			Entry<String, Long> dataPair = mapIterator.next();

			RowItem item = new RowItem(context);

			if (item.setActivity((String) dataPair.getKey())) {
				item.setTime((Long) dataPair.getValue());
				Log.d("Test::::::","time is "+dataPair.getValue());
				if(dataPair.getValue()>PMConstants.MINIMUM_TIME_FOR_DISPLAY_IN_SECONDS){
					packageList.add(item);	
				}
			}
		}

		if (modificationFlag) {
			Collections.sort(packageList, mComparator);
			//cutdata();
			notifyDataSetChanged();
			//return;
		}
		cutdata();

	}

	private void cutdata() {
		itemsToShow.clear();
		for (int i = 0; i < getApplicationShowSize() && i<packageList.size(); i++){
			itemsToShow.add(packageList.get(i));
		}
	}

	private int getApplicationShowSize() {
		switch (position) {
		case 0:
			return Util.noOfApplicationsToShowToday;
		case 1:
			return Util.noOfApplicationsToShowWeekly;
		case 2:
			return Util.noOfApplicationsToShowMonthly;
		default:
			return Util.noOfApplicationsToShowToday;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AppRecordHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.app_list_row, parent, false);

			holder = new AppRecordHolder();
			holder.appIcon = (ImageView) row.findViewById(R.id.appIcon);
			holder.appName = (TextView) row.findViewById(R.id.appName);
			holder.appRunTime = (TextView) row.findViewById(R.id.appRunTime);

			row.setTag(holder);
		} else {
			holder = (AppRecordHolder) row.getTag();
		}

		 RowItem packageObject = packageList.get(position);
		//RowItem packageObject = itemsToShow.get(position);

		holder.appName.setText(packageObject.getActivity());
		//holder.appRunTime.setText("" + packageObject.getTime());
		holder.appRunTime.setText(packageObject.getTimeInString(packageObject.getTime()));
		holder.appIcon.setImageDrawable(packageObject.getIcon());

		return row;
	}

	private static class AppRecordHolder {
		ImageView appIcon;
		TextView appName;
		TextView appRunTime;
	}

	@Override
	public int getCount() {
		if(itemsToShow==null)
		Log.d("Himanshu ","itemstoshow is null");
		else
		Log.d("Himanshu "," itemsToShow is not null ");
		return itemsToShow == null ? 0 : itemsToShow.size();
	}

	class RowItemComparator implements Comparator<Object> {

		public int compare(Object arg0, Object arg1) {
			RowItem user0 = (RowItem) arg0;
			RowItem user1 = (RowItem) arg1;

			Long i = user0.getTime();
			Long ii = user1.getTime();

			return (int) (ii - i);
		}

	}

}
