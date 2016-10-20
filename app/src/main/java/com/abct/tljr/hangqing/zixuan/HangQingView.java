package com.abct.tljr.hangqing.zixuan;

import com.abct.tljr.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HangQingView extends Fragment{

	private View hangqingview=null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		hangqingview=inflater.inflate(R.layout.tljr_hangqing_view,container,false);
		new TljrZiXuanLineChart(getActivity(),hangqingview);
		return hangqingview;
	}
	
	
}
