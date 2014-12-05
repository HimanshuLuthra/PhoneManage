package com.phonemanager.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListPagerAdapter extends MainPagerAdapter implements
		OnItemClickListener {

	public ListPagerAdapter(Context context) {
		super(context);
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		ListView appList = new ListView(getContext());	
		appList.setAdapter(getMainAdapter().get(position));
		appList.setOnItemClickListener(this);
		((ViewPager) collection).addView(appList, 0);
		return appList;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Util.toast("Click position = " + position, getContext());
	}

}
