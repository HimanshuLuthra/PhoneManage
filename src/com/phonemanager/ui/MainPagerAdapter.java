package com.phonemanager.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.phonemanager.service.PMConstants;

public class MainPagerAdapter extends PagerAdapter {

	private Context context;
	private static ArrayList<AppListAdapter> mainAdapterArray;

	protected MainPagerAdapter(Context context) {
		if (this.context == null)
			this.context = context;

		if (mainAdapterArray == null) {
			mainAdapterArray = new ArrayList<AppListAdapter>();
			for (int i = 0; i < getCount(); i++)
				mainAdapterArray.add(new AppListAdapter(this.context));
		}
	}

	@Override
	public int getCount() {
		return PMConstants.MAIN_PAGE_ADAPTER_COUNT;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch(position)
		{
		case 0:
			return Util.TODAY;
		case 1:
			return Util.WEEKLY;
		case 2:
			return Util.MONTHLY;
		default:
			return null;
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	protected ArrayList<AppListAdapter> getMainAdapter() {
		return mainAdapterArray;
	}


	public void updateData(int position, final HashMap<String, Long> newDataMap) {
		AppListAdapter appAdapter = mainAdapterArray.get(position);
		appAdapter.updatePackageList(position,newDataMap);
	}
	/*
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}*/
	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) { 

	}

	/*public int getAdapterCount(int position){
		return mainAdapterArray.get(position) == null ? 0 : mainAdapterArray.get(position).getCount();
	}
	 */
	protected Context getContext() {
		return context;
	}

}
