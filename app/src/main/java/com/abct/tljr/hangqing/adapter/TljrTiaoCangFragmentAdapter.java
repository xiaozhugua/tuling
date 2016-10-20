package com.abct.tljr.hangqing.adapter;

import com.abct.tljr.hangqing.tiaocang.fragment.ChiCangPercent;
import com.abct.tljr.hangqing.tiaocang.fragment.Movements;
import com.abct.tljr.hangqing.tiaocang.fragment.TiaoCangRecord;
import com.abct.tljr.hangqing.tiaocang.fragment.TiaocangGu;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TljrTiaoCangFragmentAdapter extends FragmentPagerAdapter{

	String[] title={"股票","持仓比","调仓记录","走势"};
	private TiaoCangRecord mTiaoCangRecord=null;
	private TiaocangGu mTiaoCangGu=null;
	private Movements mMovements=null;
	private ChiCangPercent mChiCangPercent=null;
	
	public TljrTiaoCangFragmentAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int item) {
		switch(item){
		case 0:
			if(mTiaoCangGu==null){
				mTiaoCangGu=new TiaocangGu();
			}
			return mTiaoCangGu;
		case 1:
			if(mChiCangPercent==null){
				mChiCangPercent=new ChiCangPercent();
			}
			return mChiCangPercent;
		case 2:
			if(mTiaoCangRecord==null){
				mTiaoCangRecord=new TiaoCangRecord();
			}
			return mTiaoCangRecord;
		case 3:
			if(mMovements==null){
				mMovements=new Movements();
			}
			return mMovements;
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return title.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return title[position];
	}
	
}
