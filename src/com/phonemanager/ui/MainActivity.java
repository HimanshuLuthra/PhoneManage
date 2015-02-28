package com.phonemanager.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.phonemanager.R;
import com.phonemanager.service.ActivityDataPair;
import com.phonemanager.service.LogService;
import com.phonemanager.service.LogService.LocalBinder;
import com.phonemanager.service.PMConstants;

public class MainActivity extends Activity implements ServiceConnection,
		OnPageChangeListener {
	private String TAG = getClass().getSimpleName();
	private HashMap<String, Long> dailyDataMap;
	private HashMap<String, Long> weeklyDataMap;
	private HashMap<String, Long> monthlyDataMap;
	private Intent serviceIntent;
	boolean isService;
	private LogService serviceObj;
	private boolean isDataReadOnCreate = false;
	private boolean isServiceConnected = false;
	private PagerState pagerState; // for current ui state
	private ViewPager mainPager;
	private ListPagerAdapter mainPagerAdapterList;
	private GraphPagerAdapter mainPagerAdapterGraph;
	public HashMap<Integer, Long> timeMap;
	public HashMap<Integer, HashMap<String, Long>> dataMap;
	public HashSet<String> rejectedPackageList;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "TEST1 onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		rejectedPackageList = new HashSet<String>();
		isDataReadOnCreate = true;
		dailyDataMap = new HashMap<String, Long>();
		weeklyDataMap = new HashMap<String, Long>();
		monthlyDataMap = new HashMap<String, Long>();
		timeMap = new HashMap<Integer, Long>();
		for (int i = 0; i < 3; i++)
			timeMap.put(i, Util.getTimeSlotStart(i));

		dataMap = new HashMap<Integer, HashMap<String, Long>>();
		dataMap.put(0, dailyDataMap);
		dataMap.put(1, weeklyDataMap);
		dataMap.put(2, monthlyDataMap);

		// initialize rejectedPackageList
		rejectPackages();
		isService = false;
		isService = LogService.isServiceInstance();
		serviceIntent = new Intent(this,
				com.phonemanager.service.LogService.class);

		if (!isService) {
			startService(serviceIntent);
		}
		// lastValidTime = CURRENT_DAY_TIME_STAMP + Util.timeZoneCorrection();
		isService = bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);

		mainPagerAdapterList = new ListPagerAdapter(this);
		mainPagerAdapterGraph = new GraphPagerAdapter(this);
		mainPager = (ViewPager) findViewById(R.id.main_pager);
		mainPager.setDrawingCacheEnabled(true);
		mainPager.setOffscreenPageLimit(PMConstants.MAIN_PAGE_ADAPTER_COUNT);
		mainPager.setOnPageChangeListener(this);
		// PagerTabStrip pagerTitleStrip = (PagerTabStrip)
		// findViewById(R.id.pager_title_strip);
		pagerState = PagerState.LIST_STATE;
		
	/*	  if (savedInstanceState != null) pagerState =
		  PagerState.getValue((Integer) savedInstanceState
		  .get(PMConstants.PAGER_STATE_RESTORE));*/
		 

		if (pagerState == PagerState.LIST_STATE)
			mainPager.setAdapter(mainPagerAdapterList);
		else
			mainPager.setAdapter(mainPagerAdapterGraph);
	
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(PMConstants.PAGER_STATE_RESTORE, pagerState.getValue());
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_flip:
			int currentPosition = mainPager.getCurrentItem();

			if (pagerState == PagerState.LIST_STATE) {
				mainPager.setAdapter(mainPagerAdapterGraph);
				pagerState = PagerState.GRAPH_STATE;
			} else {
				mainPager.setAdapter(mainPagerAdapterList);
				pagerState = PagerState.LIST_STATE;
			}

			mainPager.setCurrentItem(currentPosition);
			return true;

		case R.id.action_settings:
			Util.toast("Settings action", this);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		isServiceConnected = true;
		serviceObj = ((LocalBinder) service).getService();
		buildMap();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "TEST1 onResume");
		super.onResume();

		if (isServiceConnected && isDataReadOnCreate != true) {
			Log.d(TAG, "RESUME FUNCTION IF CONDITION");
			buildMap();
		}
		isDataReadOnCreate = false;
		//setAlarm();
	}
	private void setAlarm(){
		Intent mIntent = new Intent(this,
				com.phonemanager.service.LogService.class);
		mIntent.putExtra("noti", true);
		PendingIntent mPendingIntent = PendingIntent.getService(getApplicationContext(),0, mIntent,0);
		Calendar c2 = new GregorianCalendar();
		c2.set(Calendar.HOUR_OF_DAY,23);
		c2.set(Calendar.MINUTE, 59);
		c2.set(Calendar.DATE, c2.get(Calendar.DATE));
		c2.set(Calendar.MONTH, c2.get(Calendar.MONTH));
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, Util.getTimeSlotStart(Util.DAILY),Util.notificationRepetionTime, mPendingIntent);
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		isServiceConnected = false;
		serviceObj = null;
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "Test1 onPause");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Test1 onDestroy");
		//timeMap.clear();
		super.onDestroy();
		unbindService(this);
	}

	protected void buildMap() {
		String activityName;
		Long currentStartTime;
		Long currentEndTime;
		Long activityTotalTime = 0L;

		int position = mainPager.getCurrentItem();
		ArrayList<ActivityDataPair> queryResult;

		long slotTimeStart = Util.getTimeSlotStart(position);
		Log.d("TEST:::", "Util true??? " + Util.packageRemoved);
		if (Util.packageRemoved) {
			Log.d("TEST:::", "Util.packageRemoved-> " + Util.packageRemoved);
			// Util.packageRemoved = false;
			int size = dailyDataMap.size();
			for (int i = 0; i < size; i++) {
				Log.d(TAG, "datamap " + i + " -> " + dailyDataMap.get(i));
			}
			if (dataMap.get(position)
					.containsKey(Util.getPackageToRemoveName())) {
				dataMap.get(position).remove(Util.getPackageToRemoveName());
				for (int i = 0; i < dataMap.get(position).size(); i++) {
					Log.d(TAG, "dataMap activityName "
							+ dataMap.get(position).values());
				}
				Log.d(TAG, "removedpackagename" + Util.getPackageToRemoveName());
			}

		}
		if (slotTimeStart > timeMap.get(position)) {
			Log.d(TAG, "Test slottimestart>timeMap.get(position)");
			timeMap.clear();
			queryResult = serviceObj.getData(slotTimeStart);
		}

		else {
			Log.d(TAG, "Test else case of slottimestart>timeMap.get(position)");
			// queryResult = serviceObj.getData(slotTimeStart);
			queryResult = serviceObj.getData(timeMap.get(position));
		}
		if (queryResult == null || queryResult.size() == 0) {
			Log.d(TAG, "query result = null");
			return;
		}

		for (int i = 0; i < queryResult.size(); i++) {

			activityName = queryResult.get(i).getActivityName();
			currentStartTime = Util.normaliseTime(queryResult.get(i)
					.getStartTime());
			currentEndTime = Util
					.normaliseTime(queryResult.get(i).getEndTime());

			/* if (!activityName.equalsIgnoreCase(PMConstants.SCREEN_LOCKED)) */
			if (!rejectedPackageList.contains(activityName)) {
				Log.d("TEST", activityName);
				if (dataMap.get(position).containsKey(activityName)) {
					activityTotalTime = dataMap.get(position).get(activityName);
					activityTotalTime += currentEndTime - currentStartTime;
					dataMap.get(position).put(activityName, activityTotalTime);
				} else {
					activityTotalTime = currentEndTime - currentStartTime;
					dataMap.get(position).put(activityName, activityTotalTime);
				}
			}
		}

		timeMap.put(position, queryResult.get(queryResult.size() - 1)
				.getEndTime());
		
		Iterator<Map.Entry<String, Long>> iterator = dataMap.get(position)
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Long> test = iterator.next();
			Log.d("TEST", test.getKey() + " :: " + test.getValue());
			// You can remove elements while iterating.

		}
		mainPagerAdapterList.updateData(position, dataMap.get(position));
			
	}

	
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		if (serviceObj != null) {
			buildMap();

		}
	}

	private enum PagerState {
		LIST_STATE(0), GRAPH_STATE(1);
		private int value;

		PagerState(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	private void rejectPackages() {
		rejectedPackageList.add("com.android.systemui");
		//TODO: to add more package names 
		
		//Remove launcher packages
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo info : resolveInfo) {
			rejectedPackageList.add(info.activityInfo.packageName);
			Log.d("TEST", "rejected package Name "
					+ info.activityInfo.packageName);
		}
	}
}
