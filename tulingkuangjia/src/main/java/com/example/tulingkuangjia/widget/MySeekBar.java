package com.example.tulingkuangjia.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.SeekBar;

import com.abct.tljr.R;
import com.qh.common.model.AppInfo;

public class MySeekBar extends SeekBar {
	private boolean mIsDragging;
	private float mTouchDownY;
	private int mScaledTouchSlop;
	private boolean isInScrollingContainer = false;
	private Bitmap other;
	private Paint mPaint;

	public boolean isInScrollingContainer() {
		return isInScrollingContainer;
	}

	public void setInScrollingContainer(boolean isInScrollingContainer) {
		this.isInScrollingContainer = isInScrollingContainer;
	}

	/**
	 * On touch, this offset plus the scaled value from the position of the
	 * touch will form the progress value. Usually 0.
	 */
	float mTouchProgressOffset;

	public MySeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		init();
	}

	public MySeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MySeekBar(Context context) {
		super(context);
		init();
	}

	private void init() {
		other = BitmapFactory.decodeResource(getResources(), R.drawable.img_zhangdiefuzhizhen1);
		Matrix matrix = new Matrix();
		matrix.postScale(0.5f, 0.5f); // 长和宽放大缩小的比例
		other = Bitmap.createBitmap(other, 0, 0, other.getWidth(), other.getHeight(), matrix, true);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.WHITE);
		mPaint.setTextSize(AppInfo.dp2px(getContext(), 12));
	}

	public void setOther(int id) {
		other = BitmapFactory.decodeResource(getResources(), id);
		Matrix matrix = new Matrix();
		matrix.postScale(0.5f, 0.5f); // 长和宽放大缩小的比例
		other = Bitmap.createBitmap(other, 0, 0, other.getWidth(), other.getHeight(), matrix, true);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(h, w, oldh, oldw);

	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight() + AppInfo.dp2px(getContext(), 30), getMeasuredWidth());
	}

	private String s;

	public void setProText(String s) {
		this.s = s;
	}

	private int a = AppInfo.dp2px(getContext(), 17);
	private int b = AppInfo.dp2px(getContext(), 16);
	private int c = AppInfo.dp2px(getContext(), 24);
	private int d = AppInfo.dp2px(getContext(), 1);
	private int e = AppInfo.dp2px(getContext(), 5);
	private int f = AppInfo.dp2px(getContext(), -7);

	// private int a = AppInfo.dp2px(getContext(), 50);
	// private int b = AppInfo.dp2px(getContext(), 40);
	// private int c = AppInfo.dp2px(getContext(), 66);
	// private int d = AppInfo.dp2px(getContext(), 12);
	// private int e = AppInfo.dp2px(getContext(), 20);
	// private int f = AppInfo.dp2px(getContext(), -14);
	@SuppressLint("NewApi")
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		canvas.drawBitmap(other, a, getMeasuredHeight() - getThumb().getBounds().left - b, mPaint);
		canvas.drawText(s == null ? (getProgress() + "") : s, c, getMeasuredHeight() - getThumb().getBounds().left - d,
				mPaint);
		canvas.rotate(-90);
		canvas.translate(-getHeight(), e);
		canvas.scale(1, 0.1f);
		getProgressDrawable().draw(canvas);
		canvas.scale(1, 10);
		canvas.translate(0, f);
		getThumb().draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isInScrollingContainer()) {

				mTouchDownY = event.getY();
			} else {
				setPressed(true);

				invalidate();
				onStartTrackingTouch();
				trackTouchEvent(event);
				attemptClaimDrag();

				onSizeChanged(getWidth(), getHeight(), 0, 0);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (mIsDragging) {
				trackTouchEvent(event);

			} else {
				final float y = event.getY();
				if (Math.abs(y - mTouchDownY) > mScaledTouchSlop) {
					setPressed(true);

					invalidate();
					onStartTrackingTouch();
					trackTouchEvent(event);
					attemptClaimDrag();

				}
			}
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			break;

		case MotionEvent.ACTION_UP:
			if (mIsDragging) {
				trackTouchEvent(event);
				onStopTrackingTouch();
				setPressed(false);

			} else {
				// Touch up when we never crossed the touch slop threshold
				// should
				// be interpreted as a tap-seek to that location.
				onStartTrackingTouch();
				trackTouchEvent(event);
				onStopTrackingTouch();

			}
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			// ProgressBar doesn't know to repaint the thumb drawable
			// in its inactive state when the touch stops (because the
			// value has not apparently changed)
			invalidate();
			break;
		}
		return true;

	}

	private void trackTouchEvent(MotionEvent event) {
		final int height = getHeight();
		final int top = getPaddingTop();
		final int bottom = getPaddingBottom();
		final int available = height - top - bottom;

		int y = (int) event.getY();

		float scale;
		float progress = 0;

		if (y > height - bottom) {
			scale = 0.0f;
		} else if (y < top) {
			scale = 1.0f;
		} else {
			scale = (float) (available - y + top) / (float) available;
			progress = mTouchProgressOffset;
		}

		final int max = getMax();
		progress += scale * max;

		setProgress((int) progress);

	}

	/**
	 * This is called when the user has started touching this widget.
	 */
	void onStartTrackingTouch() {
		mIsDragging = true;
	}

	/**
	 * This is called when the user either releases his touch or the touch is
	 * canceled.
	 */
	void onStopTrackingTouch() {
		mIsDragging = false;
	}

	private void attemptClaimDrag() {
		ViewParent p = getParent();
		if (p != null) {
			p.requestDisallowInterceptTouchEvent(true);
		}
	}

	@Override
	public synchronized void setProgress(int progress) {

		super.setProgress(progress);
		onSizeChanged(getWidth(), getHeight(), 0, 0);

	}

	@Override
	public void setThumb(Drawable thumb) {
		thumb = rotateBitmap(thumb);
		super.setThumb(thumb);
	}

	private static Bitmap drawableToBitmap(Drawable drawable)// drawable
																// 转换成bitmap
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	private static BitmapDrawable rotateBitmap(Drawable drawable) {

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		matrix.setRotate(90, width / 2, height / 2);
		matrix.postScale(2, 2);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
		return new BitmapDrawable(newbmp);
	}

}