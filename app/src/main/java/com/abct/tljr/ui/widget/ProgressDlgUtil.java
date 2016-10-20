package com.abct.tljr.ui.widget;

import com.abct.tljr.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration.Status;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ProgressDlgUtil extends Activity {
	private static ProgressDlgUtil instance;
	private static boolean isShow = false;
	public static boolean Status=true;
	
	public static void showProgressDlg(String msg, Context context) {
		if (!isShow) {
			context.startActivity(new Intent(context, ProgressDlgUtil.class));
		}
		isShow = true;
		
	}

	public static void stopProgressDlg() {
		if (instance != null) {
			instance.finish();
			instance.overridePendingTransition(0, 0);
			Status=true;
		}
		isShow = false;
	}

	private RelativeLayout layout;
	private LoadingLayout mLoadingLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (!isShow) {
			finish();
			return;
		}
		instance = this;
		layout = new RelativeLayout(this);
		setContentView(layout);
		mLoadingLayout = new LoadingLayout(this);
		RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mLoadingLayout.setLayoutParams(params);
		layout.addView(mLoadingLayout);
		mLoadingLayout.setVisibility(View.VISIBLE);
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Status){
					stopProgressDlg();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isShow = false;
	}
}

class LoadingLayout extends RelativeLayout {

	private View mCircleView;
	private ImageView mBottomView;
	private float mAnimTransValueRec[];
	private float mRotationValue[];
	private float mScaleX[];
	private float mScaleY[];
	private AnimatorSet mCircleAnim;
	private Animator.AnimatorListener mCircleListener;

	private boolean isAnim = false;

	public LoadingLayout(Context context) {
		this(context, null);
	}

	public LoadingLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(getContext());
		initValue();
		initAnim();
	}

	private void initAnim() {
		mCircleListener = new CircleAnimListener();
	}

	private void initView(Context context) {
		/* 固定这几个图片的大小为28个 dp 值 */
		int viewSize = (int) (40 * getResources().getDisplayMetrics().density + .5f);
		/* 创建一个 显示圆形图片的View */
		mCircleView = new View(context);
		/* 设置参数 */
		RelativeLayout.LayoutParams circleParams = new LayoutParams(viewSize, viewSize);
		/* 让他水平居中显示 */
		circleParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		mCircleView.setLayoutParams(circleParams);
		/* 设置背景图片 */
		mCircleView.setBackgroundResource(R.drawable.img_donghua1);
		/* 设置 id，这里的作用，是为了下面阴影的排列，需要用此View 作为参考对象 */
		mCircleView.setId(R.id.action_bar_root);

		/* 创建一个显示底部阴影图片的ImageView */
		mBottomView = new ImageView(context);
		RelativeLayout.LayoutParams bottomParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		/* 设置水平居中 */
		bottomParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		/* 设置在圆形图片的下方 */
		bottomParams.addRule(RelativeLayout.BELOW, R.id.action_bar_root);
		mBottomView.setLayoutParams(bottomParams);
		mBottomView.setBackgroundResource(R.drawable.loading_bottom);
		/* 整个Layout 中的View 居中显示 */
		setGravity(Gravity.CENTER);
		/* 添加View */
		addView(mCircleView);
		addView(mBottomView);
	}

	private void initValue() {
		mAnimTransValueRec = new float[3];
		mAnimTransValueRec[0] = -150f;
		mAnimTransValueRec[1] = 10f;
		mAnimTransValueRec[2] = -150f;
		mScaleX = new float[11];
		mScaleX[0] = .9f;
		mScaleX[1] = .5f;
		mScaleX[2] = .2f;
		mScaleX[3] = .1f;
		mScaleX[4] = .05f;
		mScaleX[5] = .1f;
		mScaleX[6] = .2f;
		mScaleX[7] = .3f;
		mScaleX[8] = .5f;
		mScaleX[9] = .7f;
		mScaleX[10] = .9f;
		mScaleY = new float[11];
		mScaleY[0] = 1f;
		mScaleY[1] = 1f;
		mScaleY[2] = 1f;
		mScaleY[3] = 1f;
		mScaleY[4] = .7f;
		mScaleY[5] = .5f;
		mScaleY[6] = .7f;
		mScaleY[7] = .9f;
		mScaleY[8] = 1f;
		mScaleY[9] = 1f;
		mScaleY[10] = 1f;
		mRotationValue = new float[2];
		mRotationValue[0] = 0;
		mRotationValue[1] = 120.0f;
	}

	private void startAnim() {
		isAnim = true;
		/* 圆形图片的动画集合 */
		mCircleAnim = new AnimatorSet();
		/* 设置执行时长800ms */
		mCircleAnim.setDuration(1000l);
		/* 这里设置播放动画的个数，移动动画和底部阴影放缩动画 */
		mCircleAnim.playTogether(translationAnim(mCircleView), circleAnim(mCircleView), bottomAnim(mBottomView));
		/* 开始动画 */
		mCircleAnim.start();
		/* 设置动画监听事件 */
		mCircleAnim.addListener(mCircleListener);

	}

	private Animator bottomAnim(Object object) {
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(object, "scaleX", mScaleX);
		return objectAnimator;
	}

	private Animator translationAnim(Object object) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(object, "translationY", mAnimTransValueRec);
		return animator;
	}

	private Animator circleAnim(Object object) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(object, "scaleY", mScaleY);
		return animator;
	}

	private void stopAnim() {

		if (mCircleAnim != null) {
			mCircleAnim.end();
			mCircleAnim.removeAllListeners();
		}
		isAnim = false;
	}

	private class CircleAnimListener extends AnimatorListenerAdapter {

		@Override
		public void onAnimationEnd(Animator animation) {
			startAnim();
		}
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == VISIBLE) {
			if (!isAnim)
				startAnim();
		} else {
			stopAnim();
		}
	}
}