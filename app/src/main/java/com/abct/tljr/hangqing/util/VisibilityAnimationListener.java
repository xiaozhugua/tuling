package com.abct.tljr.hangqing.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class VisibilityAnimationListener implements AnimationListener {

	private View mVisibilityView;

	public VisibilityAnimationListener(View view) {
		mVisibilityView = view;
	}

	public void setVisibilityView(View view) {
		mVisibilityView = view;
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (mVisibilityView != null) {
			mVisibilityView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (mVisibilityView != null) {
			mVisibilityView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	
}