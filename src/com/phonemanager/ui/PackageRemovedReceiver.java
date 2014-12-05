package com.phonemanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.phonemanager.service.DBManager;

public class PackageRemovedReceiver extends BroadcastReceiver {
	private DBManager mDbManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		mDbManager = DBManager.getInstance(context);
		if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			String packageName = intent.getData().toString();
			if (packageName.contains("package:")) {
				packageName = packageName.replace("package:", "");
			}
			Util.packageRemoved = true;
			mDbManager.deletePackageEntry(packageName);
			Log.d("TEST", "Removed package Name " + packageName);
			Util.packageRemoved = true;
			Util.setPackageToRemoveName(packageName);
		}
	};

}
