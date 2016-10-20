package com.abct.tljr.kline.gegu.view;

import com.qh.common.util.LogUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class OneguListView extends ListView {

	private boolean selfScroll = true;

	public OneguListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub
	}

	public OneguListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public OneguListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public OneguListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private float lastY = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getY();
		// LogUtil.e("first", ""+this.getChildAt(0).getTop());

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:

			getParent().requestDisallowInterceptTouchEvent(true);// 自己处理

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			getParent().requestDisallowInterceptTouchEvent(false);// 父亲处理
			break;
		}
		lastY = y;

		return super.onTouchEvent(event);
	}

}
