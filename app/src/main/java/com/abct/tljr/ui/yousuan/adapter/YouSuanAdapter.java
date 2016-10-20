package com.abct.tljr.ui.yousuan.adapter;


import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class YouSuanAdapter extends FragmentPagerAdapter{

	private List<Fragment> listFragments=null;
	
	public YouSuanAdapter(FragmentManager fm,List<Fragment> listFragments) {
		super(fm);
		this.listFragments=listFragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		if(listFragments!=null){
			return listFragments.get(arg0);
		}
		return null;
	}

	@Override
	public int getCount() {
		return listFragments.size();
	}
	
	
	
}
	
	

	
	
