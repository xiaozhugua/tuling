package com.abct.tljr.chart;

import com.abct.tljr.R;
import com.abct.tljr.utils.Util;
import com.qh.common.util.ColorUtil;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class ChartChangeBar {
	ViewPager vpContent;
	Activity activity;
	TextView[] views;
	final int barCount = 3;
	ImageView cursor;
	private String TAG = "ChartChangeBar";
	
	public ChartChangeBar(ViewPager vp,Activity activity){
		vpContent = vp;
		this.activity = activity;
		initTextView();
		initImageView();
	}
	
	
	int time = 0;
	public void initTextView(){
		views = new TextView[barCount];
		views[0] = (TextView) activity.findViewById(R.id.tv1);
		views[1] = (TextView) activity.findViewById(R.id.tv3);
		views[2] = (TextView) activity.findViewById(R.id.tv2);
		
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv1:
					onPageSelected(0);
					vpContent.setCurrentItem(0);
					break;
				case R.id.tv3:
					onPageSelected(1);
					vpContent.setCurrentItem(1);
					break;
				case R.id.tv2:
					onPageSelected(2);
					vpContent.setCurrentItem(2);
					if(time ==0){
						time = 1;
						((ChartActivity)activity).weseeview.handler.sendEmptyMessage(4);
					}
					break;
				}
			}
		};
		for (int i = 0; i < views.length; i++) {
			views[i].setOnClickListener(listener);
		}
//		views[0].setTextColor(Util.c_blue);
	}
	
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private int offsetX = 0;
	private int w = Util.WIDTH/ barCount/3;
	private void initImageView() {
		cursor = (ImageView) activity.findViewById(R.id.im);
		cursor.setBackgroundColor(activity.getResources().getColor(R.color.c_red_text));
		offset = Util.WIDTH/ barCount;
		bmpW = offset;// UtilUI.dpToPixel(80);
		offsetX = (Util.WIDTH/ barCount - bmpW) / 2 + w / 2;
		cursor.getLayoutParams().width = offset - w;
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, offsetX);
		cursor.setImageMatrix(matrix);
		onPageSelected(0);
	}
	
	public void onPageSelected(int selectInd) {
		cursor.getLayoutParams().width = offset - w;
		Animation animation = null;
		animation = new TranslateAnimation(currIndex * offset + offsetX,
				selectInd * offset + offsetX, 0, 0);
		currIndex = selectInd;
		animation.setFillAfter(true);
		animation.setDuration(300);
		cursor.startAnimation(animation); 
		for (int i = 0; i < views.length; i++) {
			views[i].setTextColor(Color.parseColor("#555555"));
		}
		views[selectInd].setTextColor(ColorUtil.red);
	}
	
}
