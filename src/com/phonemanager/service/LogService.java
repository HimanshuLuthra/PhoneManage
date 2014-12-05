package com.phonemanager.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class LogService extends Service

{
	private static LogService instance = null;
	private TimerTask checkActivity;
	private Timer timer;
	private String lastActivity;
	private DBManager mDBManager;
	private ActivityDataManager mActivityDataManager;
	private KeyguardManager mKeyguardManager;
	private ActivityManager mActivityManager; 
	private final IBinder mBinder = new LocalBinder(); 
	private String TAG = getClass().getSimpleName();
	private Intent restartServiceIntent;
	public class LocalBinder extends Binder
	{

		public LogService getService()
		{
			//return instance of this class to UI
			return LogService.this;
		}
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
		//system initializations
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mActivityManager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		mDBManager = DBManager.getInstance(this);
		mActivityDataManager = new ActivityDataManager(this.getBaseContext());

		Log.d(TAG,"onCreate");
		//local reference initializations
		instance = this;
		checkActivity = new MyTimerTask();
		timer = new Timer();
		timer.scheduleAtFixedRate(checkActivity, 0,PMConstants.TIME_FREQUENCY);
		lastActivity = "";


	}
	public ArrayList<ActivityDataPair> getData(Long time)
	{
		return mActivityDataManager.getData(time);
	}

	//to delete the data table
	public void clearDb()
	{
		mDBManager.clear();
	}

	public class DataQueue
	{
		int i=0,size=0;
		ArrayList<ActivityDataPair> currentData;
		Long entry1;
		Long entry2;
    	ActivityDataPair localObj; 


		public DataQueue()
		{
			currentData = new ArrayList<ActivityDataPair>();
			localObj 	= new ActivityDataPair();
		}

		/*public void addData(ActivityDataPair newData)
		{
			Log.d("LogService","Inside addData");
			currentData.add(newData);


		}*/


		/*	public void maintainArrayList()
		{
			Log.d("LogService","Inside maintainArrayList");
			while(true)
			{
				entry1=currentData.get(0).getStartTime();
				if((currentData.size()==1))
				{
					Log.d("LogService","Inside if 1");
					break;
				}
				else
				{
				Log.d("LogService","Inside else");
				entry2=currentData.get(1).getStartTime();
				currentSysytemTimeStamp=Util.normaliseTime((new Date()).getTime());
				threeDaysBeforeTimeStamp = currentSysytemTimeStamp - PMConstants.THREE_DAY_SECOND;
					if(entry1<threeDaysBeforeTimeStamp)
					{
						Log.d("LogService","Inside if 2");

						if(entry2<=threeDaysBeforeTimeStamp)
						{
							Log.d("LogService","Inside if 3");

						//remove activity at index 0
						currentData.remove(0);
						continue;
						}

						else
						{
							//reset time of the index 0 activity
							localObj = currentData.get(0);
							localObj.setStartTime(currentSysytemTimeStamp);
							currentData.add(localObj);
							break;
						}
					}
					else 
					break;
				}
			}
		}
		 */
	}




	private class MyTimerTask extends TimerTask
	{

		public void run() 
		{


			Log.d("LogService->","lastActivity = "+lastActivity);
			String[] splitResultSet = getTopActivityStackTimeStampAndName().split(PMConstants.DATA_DELIMITER, 2); 

			// split result set will contain
			// [0] -> Time stamp on the activity
			// [1] -> Name of the activity

			long timeStamp = Long.parseLong(splitResultSet[0]);
		//	timeStamp = Util.normaliseTime(timeStamp); //removing the milliseconds for easier calculations
			String currentActivity = splitResultSet[1];

			if(mKeyguardManager.isKeyguardLocked())
				currentActivity = PMConstants.SCREEN_LOCKED; 

			if(!lastActivity.equalsIgnoreCase(currentActivity)){
				Log.d("LogService", "Inside Run Activity Switched "+currentActivity);
				//if(!rejectedPackageList.contains(currentActivity)){
				ActivityDataPair newData = new ActivityDataPair(currentActivity, timeStamp);
				mActivityDataManager.addData(newData);
				//}
				lastActivity = currentActivity;

			}			
		}
	}


	public String getTopActivityStackTimeStampAndName() 
	{
		long timeStamp;
		String packageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
		timeStamp = (new Date()).getTime();
		Log.d("ActivityTimeFunction | Activity = ",packageName);
		return timeStamp + PMConstants.DATA_DELIMITER + packageName;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//timer.scheduleAtFixedRate(checkActivity, 0, PMConstants.TIME_FREQUENCY);
		Log.d(TAG, "Received start id " + startId + ": " + intent+"inside onStartCommand");
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}


	@Override
	public IBinder onBind(Intent intent) 
	{

		return mBinder;
	}

	@Override
	public void onDestroy()
	{
		mActivityDataManager.saveData();
		timer.cancel();
		restartServiceIntent = new Intent(this,LogService.class);
		startService(restartServiceIntent);


	}


	public static boolean isServiceInstance()
	{
		return instance != null;

	}
	public void deleteFromDb(String packageName) {
		mDBManager.deletePackageEntry(packageName);
		
	}
}


