package com.abct.tljr.hangqing.util;

import com.abct.tljr.R;
import com.abct.tljr.utils.Util;
import com.qh.common.util.ColorUtil;

import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class HQtitleBar {
	TextView[] views;
	ViewPager vpContent;
	ImageView cursor;
	View titlebar;
	final int barCount = 4;
	Animation animation = null;
	
	public HQtitleBar(ViewPager vp, View bar) {
		vpContent = vp;
		titlebar = bar;
		initTextView(titlebar);
		initImageView(titlebar);
	}

	private void initTextView(View v) {
		views = new TextView[barCount];
//		views[0] = (TextView) v.findViewById(R.id.tljr_txt_paihang);
//		views[1] = (TextView) v.findViewById(R.id.tljr_txt_zixuan);
//		views[2] = (TextView) v.findViewById(R.id.tljr_txt_zuhe);
//		views[3] = (TextView) v.findViewById(R.id.tljr_txt_guzhi);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
//				case R.id.tljr_txt_paihang:
//					vpContent.setCurrentItem(0);
//					break;
//				case R.id.tljr_txt_zixuan:
//					vpContent.setCurrentItem(1);
//					break;
//				case R.id.tljr_txt_zuhe:
//					vpContent.setCurrentItem(2);
//					break;
//				case R.id.tljr_txt_guzhi:
//					vpContent.setCurrentItem(3);
//					break;
				}
			}
		};
		for (int i = 0; i < views.length; i++) {
			views[i].setOnClickListener(listener);
		}
		views[0].setTextColor(ColorUtil.red);
	}

	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private int offsetX = 0;
//	private int w = Util.WIDTH / barCount/2;

	private void initImageView(View v) {
		//cursor = (ImageView) v.findViewById(R.id.tljr_img_hq_check);
		offset = Util.WIDTH / barCount;
		bmpW = offset;
//		cursor.getLayoutParams().width = offset - w;
		cursor.getLayoutParams().width = offset;
//		offsetX = (Util.WIDTH / barCount - bmpW) / 2 + w / 2;
		offsetX = (Util.WIDTH / barCount - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, offsetX);
		cursor.setImageMatrix(matrix);
		onPageSelected(0);
	}

	public void onPageSelected(int selectInd) {
		animation = new TranslateAnimation(currIndex * offset +offsetX,selectInd * offset + offsetX, 0, 0);
		currIndex = selectInd;
		animation.setFillAfter(true);
		animation.setDuration(0);
		cursor.startAnimation(animation);
		for (int i = 0; i < views.length; i++) {
			views[i].setTextColor(Color.BLACK);
			views[i].setTextSize(14);
			views[i].getPaint().setFakeBoldText(false);
		}
		
		TabAnimator(views[selectInd]);
	}
	
	public void TabAnimator(TextView View){
		View.setTextColor(ColorUtil.red);
		//View.setTextSize(16);
		//TextPaint tp=View.getPaint();
		//tp.setFakeBoldText(true);
	}
	
}
