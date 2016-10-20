package com.abct.tljr.hangqing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class GrapeGridview extends GridView {
	public GrapeGridview(Context context) {
		super(context);
	}

	public GrapeGridview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GrapeGridview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 重写dispatchTouchEvent方法禁止GridView滑动
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}
}