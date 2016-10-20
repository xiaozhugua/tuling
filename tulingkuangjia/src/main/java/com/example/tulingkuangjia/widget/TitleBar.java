package com.example.tulingkuangjia.widget;

import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.abct.tljr.R;
import com.abct.tljr.utils.ViewUtil;
import com.qh.common.util.LogUtil;

public class TitleBar {
	TextView[] views;
	ViewPager vpContent;
	ImageView cursor;
	View titlebar;
	int barCount = 4;
	Animation animation = null;
	
	public TitleBar(ViewPager vp, View bar,TextView[] titleTextviews) {
		vpContent = vp;
		titlebar = bar;
		views = titleTextviews;
		barCount = views.length;
		initTextView();
		initImageView(titlebar);
	}

	private void initTextView() {
//		views = new TextView[barCount];
//		views[0] = (TextView) v.findViewById(R.id.tv1);
//		views[1] = (TextView) v.findViewById(R.id.tv2);
//		views[2] = (TextView) v.findViewById(R.id.tv3);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				vpContent.setCurrentItem(Integer.parseInt(v.getTag().toString()));
				onPageSelected_out(Integer.parseInt(v.getTag().toString()));
			}
		};
		for (int i = 0; i < views.length; i++) {
			views[i].setTag(i);
			views[i].setOnClickListener(listener);
		}
		views[0].setTextColor(Color.RED);
		
		vpContent.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				 onPageSelected_out(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});;
		
	}

	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private int offsetX = 0;
//	private int w = Util.WIDTH / barCount/2;

	private void initImageView(View v) {
		 cursor = (ImageView) v.findViewById(R.id.tljr_check);
		offset = ViewUtil.WIDTH / barCount;
		bmpW = offset;
//		cursor.getLayoutParams().width = offset - w;
		cursor.getLayoutParams().width = offset;
		LogUtil.e("Text","in initImageView width :"+offset);
//		offsetX = (Util.WIDTH / barCount - bmpW) / 2 + w / 2;
		offsetX = (ViewUtil.WIDTH / barCount - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, offsetX);
		cursor.setImageMatrix(matrix);
		onPageSelected_out(0);
	}

	public void onPageSelected_out(int selectInd) {
		animation = new TranslateAnimation(currIndex * offset +offsetX,selectInd * offset + offsetX, 0, 0);
		currIndex = selectInd;
		animation.setFillAfter(true);
		animation.setDuration(0);
		cursor.startAnimation(animation);
		for (int i = 0; i < views.length; i++) {
			views[i].setTextColor(Color.parseColor("#969696"));
			views[i].setTextSize(14);
			views[i].getPaint().setFakeBoldText(false);
		}
		views[selectInd].setTextColor(Color.RED);
	}

}
