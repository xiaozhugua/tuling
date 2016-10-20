package com.abct.tljr.news.mark;

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

public class MarkTitleBar {
	TextView[] views;
	ViewPager vpContent;
	ImageView cursor;
	View titlebar;
	final int barCount = 2;

	public MarkTitleBar(ViewPager vp, View bar) {
		vpContent = vp;
		titlebar = bar;
		initTextView(titlebar);
		initImageView(titlebar);
	}

	private void initTextView(View v) {
		views = new TextView[barCount];
		views[0] = (TextView) v.findViewById(R.id.tljr_txt_my);
		views[1] = (TextView) v.findViewById(R.id.tljr_txt_all);
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tljr_txt_my:
					vpContent.setCurrentItem(0);
					break;
				case R.id.tljr_txt_all:
					vpContent.setCurrentItem(1);
					break;
				}
			}
		};
		for (int i = 0; i < views.length; i++) {
			views[i].setOnClickListener(listener);
		}
		views[0].setTextColor(ColorUtil.blue);
	}

	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private int offsetX = 0;
	private int w = Util.WIDTH / barCount / 2;

	private void initImageView(View v) {
		cursor = (ImageView) v.findViewById(R.id.tljr_check);
		offset = Util.WIDTH / barCount;
		bmpW = offset;// UtilUI.dpToPixel(80);
		// cursor.getLayoutParams().width = offset;
		// offsetX = (Util.WIDTH / barCount - bmpW) / 2;
		cursor.getLayoutParams().width = offset - w;
		offsetX = (Util.WIDTH / barCount - bmpW) / 2 + w / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, offsetX);
		cursor.setImageMatrix(matrix);
		onPageSelected(0);
	}

	public void onPageSelected(int selectInd) {

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
