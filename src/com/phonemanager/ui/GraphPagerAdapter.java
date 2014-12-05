package com.phonemanager.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebView;

public class GraphPagerAdapter extends MainPagerAdapter {

	private String[] charts = { "file:///android_asset/abc.html", "file:///android_asset/new2.html",
	"file:///android_asset/pie.html" };
	
	public GraphPagerAdapter(Context context){
		super(context);
	}
	
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public Object instantiateItem(View collection, int position) {
		WebView view = new WebView(getContext());
		view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		view.getSettings().setJavaScriptEnabled(true);
		view.loadUrl(charts[position]);
		((ViewPager) collection).addView(view, 0);
		return view;
	}

	
}
