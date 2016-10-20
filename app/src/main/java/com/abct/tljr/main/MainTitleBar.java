package com.abct.tljr.main;

import com.abct.tljr.R;
import com.qh.common.util.ColorUtil;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainTitleBar {
	TextView[] views;
	TextView[] views1;
	ImageView[] img;
	ViewPager vpContent;
	int barCount;

	public MainTitleBar(ViewPager vp, LinearLayout v) {
		this.vpContent = vp;
		initTextView(v);
		onPageSelected(0);
	}

	private void initTextView(LinearLayout grps) {
		img = new ImageView[grps.getChildCount()];
		views = new TextView[grps.getChildCount()];
		views1 = new TextView[grps.getChildCount()];
		for (int i = 0; i < grps.getChildCount(); i++) {
			ViewGroup layout = (ViewGroup) grps.getChildAt(i);
			final int m = i;
			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					vpContent.setCurrentItem(m);
				}
			});
			img[i] = (ImageView) layout.getChildAt(0);
			views[i] = (TextView) layout.getChildAt(1);
			views1[i] = layout.getChildCount() > 1 ? (TextView) layout.getChildAt(2) : null;
		}
	}

	public void onPageSelected(int selectInd) {
		for (int i = 0; i < views.length; i++) {
			views[i].setTextColor(ColorUtil.gray);
			if (views1[i] != null)
				views1[i].setTextColor(ColorUtil.gray);
		}
		((ImageView) ((View) vpContent.getParent()).findViewById(R.id.logo)).setImageResource(R.drawable.img_dibulogo);
		views[selectInd].setTextColor(ColorUtil.red);
		if (views1[selectInd] != null)
			views1[selectInd].setTextColor(ColorUtil.red);
		img[0].setImageResource(R.drawable.img_iconhangqing1);
		img[1].setImageResource(R.drawable.img_iconxinwen1);
		// img[2].setImageResource(R.drawable.img_dibulogo);
		img[3].setImageResource(R.drawable.img_iconzhiyan1);
		img[4].setImageResource(R.drawable.img_yousuan1);
		switch (selectInd) {
		case 0:
			img[0].setImageResource(R.drawable.img_iconhangqing2);
			break;
		case 1:
			img[1].setImageResource(R.drawable.img_iconxinwen2);
			break;
		case 2:
			// img[2].setImageResource(R.drawable.img_dibulogo1);
			break;
		case 3:
			img[3].setImageResource(R.drawable.img_iconzhiyan2);
			break;
		case 4:
			img[4].setImageResource(R.drawable.img_yousuan2);
			break;
		default:
			break;
		}
	}
}
