package com.phonemanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;

public class ActivityDataManager {
	public   ArrayList<ActivityDataPair> dataCache;
	private  DBManager mDBHandler;
	private  Timer flushTimer;
	private  CountDownTimerTask flushTask;
	private  HashSet<String> rejectionSet;
	private  ActivityDataPair dataForNextDataCache;
	private  boolean dbCall = false;

	public ActivityDataManager(Context mContext){
		dataCache = new ArrayList<ActivityDataPair>();
		mDBHandler = DBManager.getInstance(mContext);
		flushTask = new CountDownTimerTask();
		rejectionSet = new HashSet<String>();
		rejectionSet.add("com.phonemanager");
		dataForNextDataCache = new ActivityDataPair();
		//dummy addition
		
		resetTimer();
	}

	private void resetTimer(){
		if(flushTimer != null)
			flushTimer.cancel();
		flushTask.cancel();
		flushTask = new CountDownTimerTask();
		flushTimer = new Timer();
		flushTimer.scheduleAtFixedRate(flushTask, 0, PMConstants.MAX_TTL_FLUSH_TASK);
	}

	public void addData(final ActivityDataPair newDataPair){
		Log.d("ActivityDataManager","inside addData1");

		if(!rejectionSet.contains(newDataPair.getActivityName()))
		{
			Log.d("ActivityDataManager","inside addData2");
			if(dataCache.size()!=0)
				dataCache.get(dataCache.size()-1).setEndTime(newDataPair.getStartTime());
			Log.d("ActivityDataManager","inside addData3");

			dataCache.add(newDataPair);
			checkForDump();
		}
	}

	public ArrayList<ActivityDataPair> getData(Long queryTime){
		flush();
		ArrayList<ActivityDataPair> resultData = mDBHandler.read(queryTime);


		return resultData;
	}

	private void checkForDump(){
		if(dataCache.size() >= PMConstants.MAX_FLUSH_LIMIT){
			saveData();
			resetTimer();
		}
	}

	public void saveData(){
		if (dataCache.size() > 1) {
			int lastIndex = dataCache.size() - 1;
			long currentTime = ((new Date()).getTime());
			if (dbCall) {
			//	long nextStartTime = currentTime;
				
				dataCache.get(lastIndex).setEndTime(currentTime);
				//dataCache.get(lastIndex).setStartTime(nextStartTime);
				dataForNextDataCache = dataCache.get(lastIndex);
				dataForNextDataCache.setStartTime(currentTime);
				
			} else {
				dataForNextDataCache = dataCache.get(lastIndex);
				//dataForNextDataCache.setStartTime(currentTime);
				dataCache.remove(lastIndex);
			}

			mDBHandler.write(dataCache);
			dataCache.clear();
			dataCache.add(dataForNextDataCache);

		}
	}

	public void flush(){
		dbCall = true;
		saveData();
		resetTimer();
	}

	private class CountDownTimerTask extends TimerTask{
		@Override
		public void run(){
			saveData();
		}
	}
}
