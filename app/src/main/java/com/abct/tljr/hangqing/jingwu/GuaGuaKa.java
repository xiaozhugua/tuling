package com.abct.tljr.hangqing.jingwu;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.R;
import com.abct.tljr.hangqing.model.RankViewDataModel;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class GuaGuaKa extends View {

	/**
	 * 绘制线条的Paint,即用户手指绘制Path
	 */
	private Paint mOutterPaint = new Paint();
	/**
	 * 记录用户绘制的Path
	 */
	private Path mPath = new Path();
	/**
	 * 内存中创建的Canvas
	 */
	private Canvas mCanvas;
	/**
	 * mCanvas绘制内容在其上
	 */
	private Bitmap mBitmap;

	/**
	 * 以下是奖区的一些变量
	 */

	private boolean isComplete;

	private Paint mBackPint = new Paint();
	private Rect mTextBound = new Rect();
	private String mText = "0000";
	private int mLastX;
	private int mLastY;
	private BitmapFactory.Options opt;
	// private Bitmap mBackBitmap;
	private int status = 0;
	private ViewFlipper mViewFlipper;
	private Context context;
	private List<RankViewDataModel> ListDataModel;
	private int num;
	private ViewPager mViewPager;
	private long times;
	private GridView v;
	private View gridview;
	private String url = null;
	private OneGu mOneGu = null;
	private SharedPreferences.Editor editor = null;
	private String name;

	private View rankviewitem;

	public GuaGuaKa(Context context) {
		this(context, null);
	}

	public GuaGuaKa(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GuaGuaKa(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setViewFlipper(ViewFlipper mViewFlipper, Context context,
			List<RankViewDataModel> ListDataModel, int i, ViewPager mViewPager,
			long times, View view, String name) {
		this.mViewFlipper = mViewFlipper;
		this.context = context;
		this.ListDataModel = ListDataModel;
		this.num = i;
		this.mViewPager = mViewPager;
		this.times = times;
		this.gridview = view;
		this.name = name;
	}

	public void setUrl(String url, String code) {
		this.url = url;
		this.mText = code;
		invalidate();
	}

	/**
	 * 初始化canvas的绘制用的画笔
	 */
	private void setUpBackPaint() {
		mBackPint.setStyle(Style.FILL);
		mBackPint.setColor(Color.DKGRAY);
		mBackPint.setTextSize(40);
		mBackPint.getTextBounds(mText, 0, mText.length(), mTextBound);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inSampleSize = 1;
		setUpOutPaint();
		setUpBackPaint();

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		// 初始化bitmap
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);
		mCanvas = new Canvas(mBitmap);
		// 绘制遮盖层
		mOutterPaint.setStyle(Paint.Style.FILL);
		mCanvas.drawRoundRect(new RectF(0, 0, width, height), 30, 30,
				mOutterPaint);
		mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.img_guayigua), null, new RectF(0, 0, width, height),
				null);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawBitmap(mBackBitmap,0,0,null);
		canvas.drawText(mText, getWidth() / 2 - mTextBound.width() / 2,
				getHeight() / 2 + mTextBound.height() / 2, mBackPint);
		// 绘制奖项
		if (!isComplete) {
			drawPath();
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
	}

	/**
	 * 设置画笔的一些参数
	 */
	private void setUpOutPaint() {
		// 设置画笔
		// mOutterPaint.setAlpha(0);
		mOutterPaint.setColor(Color.parseColor("#c0c0c0"));
		mOutterPaint.setAntiAlias(true);
		mOutterPaint.setDither(true);
		mOutterPaint.setStyle(Paint.Style.STROKE);
		mOutterPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
		mOutterPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角
		// 设置画笔宽度
		mOutterPaint.setStrokeWidth(40);
	}

	/**
	 * 绘制线条
	 */
	private void drawPath() {
		mOutterPaint.setStyle(Paint.Style.STROKE);
		mOutterPaint
				.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		mCanvas.drawPath(mPath, mOutterPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			mPath.moveTo(mLastX, mLastY);
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = Math.abs(x - mLastX);
			int dy = Math.abs(y - mLastY);
			if (dx > 3 || dy > 3)
				mPath.lineTo(x, y);
			mLastX = x;
			mLastY = y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_CANCEL:
			status += 1;
			if (status == 3) {
				editor = getContext().getSharedPreferences("jingwuopen",getContext().MODE_PRIVATE).edit();
				rankviewitem = LayoutInflater.from(this.context).inflate(R.layout.tljr_rankview_item_view, null);

				rankviewitem.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mOneGu != null && mOneGu.getCode() != null
								&& mOneGu.getMarket() != null
								&& mOneGu.getName() != null
								&& mOneGu.getKey() != null) {
							Intent intent = new Intent(getContext(),OneGuActivity.class);
							// 用Bundle携带数据
							Bundle bundle = new Bundle();
							if(mOneGu.getName()!=null){
								Log.e("GuaGuaName",mOneGu.getName());
								bundle.putString("code", mOneGu.getCode());
								bundle.putString("market", mOneGu.getMarket());
								bundle.putString("name", mOneGu.getName());
								bundle.putString("key", mOneGu.getKey());
								bundle.putSerializable("onegu", mOneGu);
								intent.putExtras(bundle);
								getContext().startActivity(intent);
							}
						} else {
							Toast.makeText(getContext(), "没数据，暂时进不了个股信息",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
				if (ListDataModel.size() > num) {
					((TextView) rankviewitem.findViewById(R.id.rankview_code)).setText(ListDataModel.get(num).getCode());
					((TextView) rankviewitem.findViewById(R.id.rankview_code)).setText(ListDataModel.get(num).getName());
					StartActivity.imageLoader.displayImage(ListDataModel.get(num).getIconUrl(),
							((ImageView) rankviewitem.findViewById(R.id.rankview_img)),StartActivity.options);
				}

				MainJingWuView.MapView.put(ListDataModel.get(num).getMarket() + "|"
						+ ListDataModel.get(num).getCode(), rankviewitem);

				mViewFlipper.addView(rankviewitem, 1);
				mViewFlipper.showNext();
				mViewFlipper.removeViewAt(0);
				mBitmap.recycle();
				getReflushData();
				System.gc();
			}
			break;
		}
		invalidate();
		return true;
	}

	public void getReflushData() {
		v = (GridView) mViewPager.getChildAt(mViewPager.getCurrentItem());
		if (v.getChildCount() == 0) {
			return;
		}
		// 获取股票列表参数
		String parm = "market=" + ListDataModel.get(num).getMarket() +"&code="+ListDataModel.get(num).getCode();
		// 网上获取最新数据
		Util.getRealInfo(parm, new NetResult() {
			@Override
			public void result(final String msg) {
				try {
					JSONObject object = new JSONObject(msg);
					String code = ListDataModel.get(num).getCode();
					String market = ListDataModel.get(num).getMarket();
					JSONArray array = object.getJSONArray("result");
					Message message = new Message();
					message.what = 1;
					Bundle bundle = new Bundle();
					bundle.putString("code", code);
					bundle.putString("now", (float) array.optDouble(0) + "");
					bundle.putString("num", (float) array.optDouble(8) + "");
					bundle.putString("parent", (float) array.optDouble(9) + "");
					mOneGu = new OneGu();
					mOneGu.setCode(code);
					mOneGu.setNow(array.optDouble(0));
					mOneGu.setName(name);
					mOneGu.setMarket(market);
					mOneGu.setKey(market + code);
					mOneGu.setYclose(array.optDouble(1));
					mOneGu.setKaipanjia(array.optDouble(2));
					mOneGu.setP_changes(((float) array.optDouble(9, 0) > 0 ? "+": "")
								+ (float) array.optDouble(9, 0) + "%");
					mOneGu.setChange((float) array.optDouble(9, 0));
					editor.putString(times + code, code).commit();
					message.setData(bundle);
					mHandel.sendMessage(message);
					editor.commit();
				} catch (JSONException e) {
				}
			}
		});
	}

	@SuppressLint("HandlerLeak")
	final Handler mHandel = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Bundle bundle = msg.getData();
				ViewFlipper viewFlipper = (ViewFlipper) gridview
						.findViewById(R.id.tljr_rankview_viewflipper);
				View view = viewFlipper.getChildAt(0);
				((TextView) view.findViewById(R.id.rankview_now))
						.setText(bundle.getString("now"));
				((TextView) view.findViewById(R.id.rankview_num))
						.setText(bundle.getString("num"));
				TextView percent = ((TextView) view
						.findViewById(R.id.rankview_percent));
				percent.setText(bundle.getString("parent") + "%");
				float num = 0;
				if (!bundle.getString("num").equals("")) {
					num = Float.valueOf(bundle.getString("num"));
				}
				if (num > 0) {
//					percent.setTextColor(getContext().getResources().getColor(
//							R.color.red));
//					((RelativeLayout) view.findViewById(R.id.rankview_header))
//							.setBackgroundColor(getContext().getResources()
//									.getColor(R.color.red));
					  ((RelativeLayout) view.findViewById(R.id.tljr_rankview_item))
					  						.setBackgroundResource(R.drawable.img_tuijian02);
					
				} else {
//					percent.setTextColor(getContext().getResources().getColor(
//							R.color.tljr_c_green));
//					((RelativeLayout) view.findViewById(R.id.rankview_header))
//							.setBackgroundColor(getContext().getResources()
//									.getColor(R.color.tljr_c_green));
					  ((RelativeLayout) view.findViewById(R.id.tljr_rankview_item))
					  						.setBackgroundResource(R.drawable.img_tuijian01);
				}

				break;
			}
		};
	};

	// private static Bitmap big(Bitmap bitmap) {
	// Matrix matrix = new Matrix();
	// matrix.postScale(2.5f,2.5f); //长和宽放大缩小的比例
	// Bitmap resizeBmp =
	// Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
	// return resizeBmp;
	// }

}