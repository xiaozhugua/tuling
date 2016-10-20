package com.abct.tljr.wxapi.xunzhang;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.qh.common.CommonApplication;
import com.qh.common.model.AppInfo;
import com.qh.common.model.User;

public class MyTitleBar {
	private int count = 0;
	private Context context;
	private int width = 10;
	private int index = -1;
	private int sideWidth = 0;
	private int[] color;
	private View[] views;
	ViewPager viewpager;
	View cursor;

	public MyTitleBar(Context context, ViewPager vp, ViewGroup layout, View cursor) {
		this(context, vp, layout);
		this.cursor = cursor;
		tabChangedArrow(0);
	}

	public MyTitleBar(Context context, ViewPager vp, ViewGroup layout) {
		this.context = context;
		viewpager = vp;
		initView(layout);
	}

	private void initView(ViewGroup layout) {
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (layout.getChildAt(i).getClass() == layout.getChildAt(0).getClass()) {
				count++;
			}
		}
		views = new View[count];
		count = 0;
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (layout.getChildAt(i).getClass() == layout.getChildAt(0).getClass()) {
				views[count] = layout.getChildAt(i);
				final int m = count;
				views[count].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (m == index && User.getUser() == null) {
							CommonApplication.getInstance().common.login();
						}
						viewpager.setCurrentItem(m);
						if (cursor != null)
							tabChangedArrow(m);
					}
				});
				count++;
			}
		}
		width = AppInfo.WIDTH/ count;
	}

	public void setNeedLogin(int index) {
		this.index = index;
	}

	/**
	 * 设置选中条目的宽度
	 */
	public void setCursorWidth(int width) {
		this.width = AppInfo.dp2px(context, width);
		initImageView();
	}

	/**
	 * 设置选中条目的宽度
	 */
	public void setCursorWidth1(int width) {
		this.width = width;
		initImageView();
	}

	public void setColor(int[] color) {
		this.color = color;
	}

	/**
	 * 设置左右两边间距
	 *
	 *
	 * @param sideWidth
	 */
	public void setSideWidth(int sideWidth) {
		this.sideWidth = sideWidth;
	}

	public void tabChangedArrow(int to) {
		if (cursor.getTag() == null)
			initImageView();
		int from = (Integer) cursor.getTag();
		int offset = (AppInfo.WIDTH - AppInfo.dp2px(context, sideWidth)) / count;
		int offsetX = AppInfo.dp2px(context, sideWidth) + offset / 2 - width / 2;// 第0个居左x为：居左间隔+间距/5个-箭头宽度/2
		Animation animation = null;
		animation = new TranslateAnimation(from * offset + offsetX, to * offset + offsetX, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(200);
		cursor.startAnimation(animation);
		cursor.setTag(to);
		if (to < views.length && views[to] instanceof CompoundButton) {
			((CompoundButton) views[to]).setChecked(true);
		}
		if (color != null && views[0] instanceof TextView) {
			for (int i = 0; i < views.length; i++) {
				((TextView) views[i]).setTextColor(color[0]);
			}
			((TextView) views[to]).setTextColor(color[1]);
		}
	}

	private void initImageView() {
		cursor.getLayoutParams().width = width;
		cursor.setTag(0);
	}
}