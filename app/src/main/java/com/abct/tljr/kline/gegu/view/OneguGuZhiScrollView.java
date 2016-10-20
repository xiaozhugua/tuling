package com.abct.tljr.kline.gegu.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.abct.tljr.kline.gegu.inter.MyScrollViewListener;

/**
 * momo写的用于onegu估值的ScrollView
 */
public class OneguGuZhiScrollView extends ScrollView {

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public OneguGuZhiScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub

	}

	public OneguGuZhiScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub

	}

	public OneguGuZhiScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	public OneguGuZhiScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}

	private MyScrollViewListener scrollViewListener = null;

	public void setScrollViewListener(MyScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

}
