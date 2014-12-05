package com.phonemanager.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	private final String ACTION = "android.intent.action.BOOT_COMPLETED";
	private Intent serviceIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			serviceIntent = new Intent(context,
					com.phonemanager.service.LogService.class);
			context.startService(serviceIntent);
		}
	}
}
