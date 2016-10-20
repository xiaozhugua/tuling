package com.abct.tljr.ui.widget;

import java.util.Date;

import com.abct.tljr.R;
import com.abct.tljr.utils.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * The header view for {@link com.markmao.pulltorefresh.widget.XListView} and
 * {@link com.markmao.pulltorefresh.widget.XScrollView}
 * 
 * @author markmjw
 * @date 2013-10-08
 */
public class XHeaderView extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	private final int ROTATE_ANIM_DURATION = 180;

	private LinearLayout mContainer;

	// private ImageView mArrowImageView;
	//
	// private ProgressBar mProgressBar;
	//
	// private TextView mHintTextView;

	private int mState = STATE_NORMAL;

	// private Animation mRotateUpAnim;
	// private Animation mRotateDownAnim;

	private boolean mIsFirst;

	private int currentState;
	private Context context;

	public XHeaderView(Context context) {
		super(context);
		initView(context);
		this.context = context;
	}

	public XHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		this.context = context;
	}

	private void initView(Context context)
	{
		// Initial set header view height 0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);

		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.vw_header, null);
		// mContainer.setVisibility(View.GONE);
		setWillNotDraw(false); // 需要设置 才可重写LinearLayout的onDraw
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		// mArrowImageView = (ImageView) findViewById(R.id.header_arrow);
		// mHintTextView = (TextView) findViewById(R.id.header_hint_text);
		// mProgressBar = (ProgressBar) findViewById(R.id.header_progressbar);
		//
		// mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
		// Animation.RELATIVE_TO_SELF, 0.5f,
		// Animation.RELATIVE_TO_SELF, 0.5f);
		// mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		// mRotateUpAnim.setFillAfter(true);
		//
		// mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
		// Animation.RELATIVE_TO_SELF, 0.5f,
		// Animation.RELATIVE_TO_SELF, 0.5f);
		// mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		// mRotateDownAnim.setFillAfter(true);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL);
		mPointColor = 0xffeb5041;
		mPointRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, context.getResources()
				.getDisplayMetrics());
		mCircleRadius = mPointRadius * 3.5f;
		mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources()
				.getDisplayMetrics());
		lastRefreshTime = Util.format10.format(new Date(System.currentTimeMillis()));

	}

	public int getDp(int num)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, num, context.getResources()
				.getDisplayMetrics());
	}

	private Paint mPaint;
	private int mPointColor;
	private int mPice = 6;
	private int mTime = 0;
	private float mPointRadius = 0;
	private float mCircleRadius = 0;
	private int mHeight = 0;
	private float PI = (float) Math.PI;
	private String lastRefreshTime;

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int right = getRight();
		int left = getLeft();
		int bottom = getBottom();
		int top = getTop();
		boolean more = false;
		final int width = right - left;
		final int Mwidth = (right / 2);
		final int height = mHeight;
		final int offset = bottom - top;

		if (currentState == STATE_READY || currentState == STATE_NORMAL)
		{

			mPaint.setColor(mPointColor);
			for (int i = 0; i < mPice; i++)
			{
				int angleParam;
				if (offset < height * 3 / 4)
				{
					angleParam = offset * 16 / height - 3;// 每1%转0.16度;
				} else
				{
					angleParam = offset * 300 / height - 217;// 每1%转3度;
				}
				float angle = -(i * (360 / mPice) - angleParam) * PI / 180;
				float radiusParam = 1;
				 if (offset <= height)
				 {
				 radiusParam = offset / (float) height;
				 radiusParam = 1 - radiusParam;
				 radiusParam *= radiusParam;
				 radiusParam = 1 - radiusParam;
				 } else
				 {
				 radiusParam = 1;
				 }
				float radius = Mwidth / 2 - radiusParam * (Mwidth / 2 - mCircleRadius);
				float x = (float) (Mwidth / 2 + radius * Math.cos(angle));
				float y = (float) (offset / 2 + radius * Math.sin(angle));
				canvas.drawCircle(x, y + top, mPointRadius, mPaint);
			}

			mPaint.setTextSize(Util.getFontSize(30));
			mPaint.setColor(0xff969696);
			if (offset > 120)
			{
				canvas.drawText("松开刷新", (right / 2) - getDp(25), (offset / 2) - getDp(5), mPaint);
			} else
			{
				canvas.drawText("下拉刷新", (right / 2) - getDp(25), (offset / 2) - getDp(5), mPaint);
			}
			mPaint.setTextSize(Util.getFontSize(25));
			canvas.drawText("上次刷新时间 : " + lastRefreshTime, (right / 2) - getDp(65), (offset / 2) + getDp(15), mPaint);
		}

		if (currentState == STATE_REFRESHING)
		{
			// show progress

			mPaint.setColor(mPointColor);
			mPaint.setTextSize(Util.getFontSize(30));
			for (int i = 0; i < mPice; i++)
			{
				int angleParam = mTime * 5;
				float angle = -(i * (360 / mPice) - angleParam) * PI / 180;
				float radius = mCircleRadius;
				float x = (float) (Mwidth / 2 + radius * Math.cos(angle));
				float y;
				if (offset < height)
				{
					y = (float) (offset - height / 2 + radius * Math.sin(angle));
				} else
				{
					y = (float) (offset / 2 + radius * Math.sin(angle));
				}
				canvas.drawCircle(x, y + top, mPointRadius, mPaint);
			}

			mPaint.setTextSize(Util.getFontSize(30));
			mPaint.setColor(0xff969696);

			canvas.drawText(" 刷新中", (right / 2) - getDp(25), (offset / 2) - getDp(5), mPaint);

			mPaint.setTextSize(Util.getFontSize(25));
			canvas.drawText("上次刷新时间 : " + lastRefreshTime, (right / 2) - getDp(65), (offset / 2) + getDp(15), mPaint);

			mTime++;
			lastRefreshTime = Util.format10.format(new Date(System.currentTimeMillis()));
			if (currentState == STATE_REFRESHING)
			{
				postInvalidate();
				return;
			}
	
		}

	}

	public void setState(int state)
	{
		currentState = state;

		// if (state == mState && mIsFirst) {
		// mIsFirst = true;
		// return;
		// }
		//
		// if (state == STATE_REFRESHING) {
		// // show progress
		// mArrowImageView.clearAnimation();
		// mArrowImageView.setVisibility(View.INVISIBLE);
		// mProgressBar.setVisibility(View.VISIBLE);
		// } else {
		// // show arrow image
		// mArrowImageView.setVisibility(View.VISIBLE);
		// mProgressBar.setVisibility(View.INVISIBLE);
		// }
		//
		// switch (state) {
		// case STATE_NORMAL:
		// if (mState == STATE_READY) {
		// mArrowImageView.startAnimation(mRotateDownAnim);
		// }
		//
		// if (mState == STATE_REFRESHING) {
		// mArrowImageView.clearAnimation();
		// }
		//
		// mHintTextView.setText(R.string.header_hint_refresh_normal);
		// break;
		//
		// case STATE_READY:
		// if (mState != STATE_READY) {
		// mArrowImageView.clearAnimation();
		// mArrowImageView.startAnimation(mRotateUpAnim);
		// mHintTextView.setText(R.string.header_hint_refresh_ready);
		// }
		// break;
		//
		// case STATE_REFRESHING:
		// mHintTextView.setText(R.string.header_hint_refresh_loading);
		// break;

		// default:
		// break;
		// }
		//
		// mState = state;
	}

	/**
	 * Set the header view visible height.
	 * 
	 * @param height
	 */
	public void setVisibleHeight(int height)
	{
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	/**
	 * Get the header view visible height.
	 * 
	 * @return
	 */
	public int getVisibleHeight()
	{
		return mContainer.getHeight();
	}

}