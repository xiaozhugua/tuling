package com.abct.tljr.news.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class NewsDetailsViewPagerAdapter extends FragmentPagerAdapter{
	
	private List<Fragment> fragments; 
	
	public NewsDetailsViewPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragments = fragments;
		
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub 
		return fragments.get(arg0);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}
	
	 
	 

}
