package com.abct.tljr.ui.activity.tools;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.ui.widget.ObservableScrollView;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.widget.ScrollViewListener;
import com.abct.tljr.utils.TulingDataContant;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xbw
 * @version 创建时间：2015年10月9日 下午4:45:42
 */
public class AbhActivity extends BaseActivity {
	private View v;
	private Activity activity;
	private ViewPager viewPager;
	// private ImageView imageView;
	private List<View> lists = new ArrayList<View>();
	private TextView textView1;
	private TextView textView2;
	List<TextView> listView = new ArrayList<TextView>();
	private ArrayList<JSONObject> a = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> h = new ArrayList<JSONObject>();
	JSONArray array = new JSONArray();
	int count = 0;
	ProgressDialog pd = null;
	ImageView img = null;
	int nums[] = { 1, 3 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.activity = this;
		v = View.inflate(this, R.layout.tljr_activity_abhdata, null);
		setContentView(v);
		textView1 = (TextView) v.findViewById(R.id.agsj);
		textView2 = (TextView) v.findViewById(R.id.bgsj);
		img = (ImageView) v.findViewById(R.id.img_lanxian);
		WindowManager wm = getWindowManager();
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		LayoutParams params = new LayoutParams(
				width / 4, 3);
		int left = width * nums[0] / 4 - width / 4 / 2;
		params.setMargins(left, 0, 0, 0);
		img.setLayoutParams(params);
		findViewById(R.id.pe_img_jzjl_fanhui).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
		initData();
	}

	private void initData() {
		ProgressDlgUtil.showProgressDlg("", activity);
	/*	
		//旧接口
		HttpRequest.sendGet(UrlUtil.Url_235 + "8080/StockDataService/ahc",
				 new HttpRevMsg() {

					@Override
					public void revMsg(String msg) {
						try {
							JSONArray array = new JSONArray(msg);
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								if (object.has("a")) {
									a.add(object.getJSONObject("a"));
								}
								if (object.has("h")) {
									h.add(object.getJSONObject("h"));
								}
							}
							post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									initUI();
								}
							});
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});*/
		//新接口
		NetUtil.sendPost(TulingDataContant.getUrl("ahc_interval_data"),new NetResult() {
			
			@Override
			public void result(String arg0) {
				try {
					if (arg0==null  ||   "".equals(arg0)) {
						showMessage("获取数据异常");
						return ;
					}
					JSONObject    json=new   JSONObject(arg0);
					if (json.optInt("status")!=1) {
						showMessage("获取数据异常");
						return ;
					}
					JSONArray array = json.optJSONArray("result");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						if (object.has("a")) {
							a.add(object.getJSONObject("a"));
						}
						if (object.has("h")) {
							h.add(object.getJSONObject("h"));
						}
					}
					post(new Runnable() {

						@Override
						public void run() {
							initUI();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private   void     showMessage(String    message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@SuppressWarnings("deprecation")
	private void initUI() {
		lists.add(getView(a));
		lists.add(getView(h));
		viewPager = (ViewPager) v.findViewById(R.id.viewPager);
		viewPager.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		viewPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() { // 获得size
				// TODO Auto-generated method stub
				return lists.size();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(View view, int position, Object object) {
				((ViewPager) view).removeView(lists.get(position));
			}

			@Override
			public Object instantiateItem(View view, int position) // 实例化Item
			{
				((ViewPager) view).addView(lists.get(position), 0);
				return lists.get(position);
			}
		});
		// viewpage滑动事件
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				Constant.addClickCount();
				updateTextColor(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		setClick(textView1, 0);
		setClick(textView2, 1);
		ProgressDlgUtil.stopProgressDlg();
	}

	public View getView(ArrayList<JSONObject> list) {
		final View view = View.inflate(activity, R.layout.activity_abh, null);
		final ObservableScrollView tljr_sv_forex_titles = (ObservableScrollView) view
				.findViewById(R.id.tljr_sv_forex_title);
		final ObservableScrollView tljr_sv_forex_items = (ObservableScrollView) view
				.findViewById(R.id.tljr_sv_forex_items);
		tljr_sv_forex_titles.setScrollViewListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int x,
					int y, int oldx, int oldy) {
				// TODO Autogenerated method stub
				tljr_sv_forex_items.scrollTo(x, y);
			}
		});
		tljr_sv_forex_items.setScrollViewListener(new ScrollViewListener() {

			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int x,
					int y, int oldx, int oldy) {
				// TODO Autogenerated method stub
				tljr_sv_forex_titles.scrollTo(x, y);
			}
		});
		final LinearLayout items = (LinearLayout) tljr_sv_forex_items
				.getChildAt(0);
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = list.get(i);
			View v = View.inflate(activity, R.layout.abh_view_layout, null);
			((TextView) v.findViewById(R.id.abh_date)).setText(object
					.optString("date"));
			((TextView) v.findViewById(R.id.abh_in)).setTextColor(object
					.optLong("in") >= 0 ? ColorUtil.red : ColorUtil.green);
			((TextView) v.findViewById(R.id.abh_in)).setText(Util.df
					.format(object.optLong("in") / 100000000) + "亿");
			((TextView) v.findViewById(R.id.abh_todayBalance)).setText(Util.df
					.format(object.optLong("todayBalance") / 100) + "亿");
			((TextView) v.findViewById(R.id.abh_allBalance)).setText(Util.df
					.format(object.optLong("allBalance") / 100) + "亿");

			((TextView) v.findViewById(R.id.abh_turnoverRealBuy))
					.setTextColor(object.optLong("turnoverRealBuy") >= 0 ? ColorUtil.red
							: ColorUtil.green);
			((TextView) v.findViewById(R.id.abh_turnoverRealBuy))
					.setText(Util.df.format(object.optLong("turnoverRealBuy") / 100)
							+ "亿");
			((TextView) v.findViewById(R.id.abh_buyTurnover)).setText(Util.df
					.format(object.optLong("buyTurnover") / 100) + "亿");
			((TextView) v.findViewById(R.id.abh_sellTurnover)).setText(Util.df
					.format(object.optLong("sellTurnover") / 100) + "亿");
			((TextView) v.findViewById(R.id.abh_upMaxStock)).setText(object
					.optString("upMaxStock")
					+ "("
					+ object.optString("upMaxStockCode") + ")");

			((TextView) v.findViewById(R.id.abh_upMaxStockRatio))
					.setText(object.optString("upMaxStockRatio"));
			v.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, Util.HEIGHT / 20));
			items.addView(v);
		}
		return view;
	}

	// 设置TextView的点击事件
	public void setClick(TextView view, final int num) {
		listView.add(view);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(num);
				updateTextColor(num);
			}
		});
	}

	// 修改颜色
	public void updateTextColor(final int num) {
		if (num > -1) {
			int color = 0;
			for (int i = 0; i < listView.size(); i++) {
				if (i == num) {
					color = ColorUtil.red;
				} else {
					color = Color.rgb(0, 0, 0);
				}
				listView.get(i).setTextColor(color);
			}

			// img.setPadding(nums[num], 0, 0, 0);
			WindowManager wm = activity.getWindowManager();
			@SuppressWarnings("deprecation")
			int width = wm.getDefaultDisplay().getWidth();
			LayoutParams params = new LayoutParams(
					width / 4, 5);
			int left = width * nums[num] / 4 - img.getWidth() / 2;
			params.setMargins(left, 0, 0, 0);
			img.setLayoutParams(params);
		}
	}
}
