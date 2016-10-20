package com.abct.tljr.kline.gegu.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class OneguViewpager extends ViewPager {

	public OneguViewpager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public OneguViewpager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/*@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean intercepted = false;

		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			intercepted = false; 
			break;
		case MotionEvent.ACTION_MOVE:
            
			break;
		case MotionEvent.ACTION_UP:
			intercepted = false;
			break;

		default:
			break;
		}
		return intercepted;
	}*/

}
