package com.phonemanager.service;

import android.util.Log;

public class ActivityDataPair {
	//here activityName is refered as packageName
    private String  activityName;
	private Long 	startTime;
	private Long 	endTime;



	public ActivityDataPair(){
		this("",0L);
	}

	public ActivityDataPair(String activityName, Long startTime){
		this.activityName = activityName;
		this.startTime = startTime;
		this.endTime = startTime;
		
	}

	public void setActivityName(String activityName){
		Log.d("DUMMY2",activityName);
		this.activityName = activityName;
	}

	public void setStartTime(Long startTime){
		this.startTime = startTime;
	}
		
	public void setEndTime(Long endTime){
		if(endTime > this.endTime)
		this.endTime = endTime;
		else{
			//error;
		}
	}
	
	public String getActivityName(){
		return activityName;
	}

	public Long getStartTime(){
		return startTime;
	}
	
	public Long getEndTime(){
		return endTime;
	}
	
}
