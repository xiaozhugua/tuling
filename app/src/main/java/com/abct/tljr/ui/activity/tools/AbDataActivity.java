package com.abct.tljr.ui.activity.tools;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.adapter.ABHaAdapter;
import com.abct.tljr.adapter.ABHbAdapter;
import com.abct.tljr.data.Constant;
import com.abct.tljr.utils.OneGuConstance;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xbw
 * @version 创建时间：2015年10月9日 下午4:45:42 AB分级基金
 */
public class AbDataActivity extends BaseActivity {
	private View v;
	private Activity activity;
	private ViewPager viewPager;
	// private ImageView imageView;
	private List<View> lists = new ArrayList<View>();
	private TextView textView1;
	private TextView textView2;
	List<TextView> listView = new ArrayList<TextView>();
	// List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	ProgressDialog pd = null;
	ImageView img = null;
	int nums[] = { 1, 3 };
	private ListView aStockListView;
	private View abhA_view;
	private View abhB_view;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.activity = this;
		v = View.inflate(this, R.layout.tljr_activity_abdata, null);
		setContentView(v);

		abhA_view = View.inflate(this, R.layout.activity_a, null);

		abhB_view = View.inflate(this, R.layout.activity_b, null);

		v.findViewById(R.id.ahsj1).setVisibility(View.GONE);
		v.findViewById(R.id.ahsj).setVisibility(View.GONE);
		textView1 = (TextView) v.findViewById(R.id.agsj);
		textView2 = (TextView) v.findViewById(R.id.bgsj);
		img = (ImageView) v.findViewById(R.id.img_lanxian);
		WindowManager wm = getWindowManager();
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		LayoutParams params = new LayoutParams(width / 4, 3);
		int left = width * nums[0] / 4 - width / 4 / 2;
		params.setMargins(left, 0, 0, 0);
		img.setLayoutParams(params);

		lists.add(setAView());
		lists.add(setBView());

		viewPager = (ViewPager) v.findViewById(R.id.viewPager);
		viewPager.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
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
		findViewById(R.id.pe_img_jzjl_fanhui).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	// 更新listview
	public void updatelistview(final String url, final String param, final View view) {
		try {
			LogUtil.e("updatelistview", "url==" + url + "?" + param);
			// http://news.tuling.me
			String superUrlString = OneGuConstance.AHDataAddress+"QhWebNewsServer/ABJi";
			HttpRequest.sendGet(superUrlString + "?" + param, new HttpRevMsg() {
				@Override
				public void revMsg(final String msg) {
					try {
						LogUtil.e("updatelistview", "ab==" + msg);

						post(new Runnable() {

							@Override
							public void run() {
								try {
									removeAllViewInLinearLayout();
									if (msg == null || "".equals(msg)) {
										if (pd.isShowing()) {
											pd.dismiss();
										}
										showErrorUi(url, param, view);
										return;

									}
									showUi();
									final JSONArray array = new JSONObject(msg).getJSONArray("data");

									/*
									 * MySimpleCursorAdapter adapter = new
									 * MySimpleCursorAdapter(activity, list, id,
									 * ColumnNames, ids);
									 * adapter.notifyDataSetChanged();
									 * 
									 * ListView lv = (ListView)
									 * view.findViewById(lv1);
									 * lv.setAdapter(adapter);
									 */

									if ("type=a".equals(param)) {
										// 设置A股数据
										setAStockData(array, view);
									} else {
										// 设置B股数据
										setBStockData(array, view);
									}

									if (pd.isShowing()) {
										pd.dismiss();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载数据成功时控件显示
	 */
	private void showUi() {
		LinearLayout showUi = (LinearLayout) v.findViewById(R.id.abh_activity_show_ui);
		showUi.setVisibility(View.VISIBLE);

	}

	/**
	 * 显示加载失败时候的UI图片
	 * 
	 * @param param
	 * @param url
	 * @param view
	 */
	private void showErrorUi(final String url, final String param, final View view) {
		// TODO Auto-generated method stub

		View errorView = View.inflate(this, R.layout.layout_hqsb, null);

		Button button = (Button) errorView.findViewById(R.id.layout_hqsb_chongzai);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                  setAView();
                  setBView();
			}
		});

		LinearLayout errorUi = (LinearLayout) v.findViewById(R.id.abh_activity_show_error_ui);//
		errorUi.addView(errorView);
		errorUi.setVisibility(View.VISIBLE);
		LinearLayout showUi = (LinearLayout) v.findViewById(R.id.abh_activity_show_ui);
		showUi.setVisibility(View.GONE);

	}

	private void removeAllViewInLinearLayout() {
		LinearLayout errorUi = (LinearLayout) v.findViewById(R.id.abh_activity_show_error_ui);//
		errorUi.removeAllViews();
	}

	/**
	 * 设置A股数据
	 * 
	 * @param view
	 */
	private void setAStockData(JSONArray array, View view) {
		// TODO Auto-generated method stub
		aStockListView = (ListView) abhA_view.findViewById(R.id.lv);
		aStockListView.setAdapter(new ABHaAdapter(array, this));
	}

	/**
	 * 设置B股数据
	 * 
	 * @param view
	 */
	private void setBStockData(JSONArray array, View view) {
		// TODO Auto-generated method stub
		ListView bStockListView = (ListView) abhB_view.findViewById(R.id.lv1);
		bStockListView.setAdapter(new ABHbAdapter(array, this));

	}

	// 添加视图
	public View addView(int id, String url, String param) {
         if (id==R.layout.activity_a) {
        	 updatelistview(url, param, abhA_view);
			
        	 return abhA_view;
		}else {
			 updatelistview(url, param, abhB_view);
				
        	 return abhB_view;
		}
	}

	// 设置A视图
	public View setAView() {
		try {
			pd = new ProgressDialog(activity);
			pd.setMessage("正在加载...");
			pd.show();
			/*
			 * String[] ColumnNames = { "funda_id", "funda_name",
			 * "funda_current_price", "funda_coupon_next",
			 * "funda_base_est_dis_rt", "funda_amount_increase_rt",
			 * "funda_profit_rt_next" };
			 */

			/*
			 * String[] names = { "代码", "名称", "现价", "下期利率", "整体溢价率", "份额增长",
			 * "下期利润" };
			 */
			/*
			 * int[] ids = new int[] { R.id.funda_id, R.id.funda_name,
			 * R.id.funda_current_price, R.id.funda_coupon_next,
			 * R.id.funda_base_est_dis_rt, R.id.funda_amount_increase_rt,
			 * R.id.funda_profit_rt_next };
			 */
			// return addView(R.layout.activity_a, UrlUtil.Url_235 +
			// "8080/QhWebNewsServer/ABJi", "type=a");
			return addView(R.layout.activity_a, UrlUtil.Url_235 + "8080/QhWebNewsServer/ABJi", "type=a");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new View(activity);
	}

	// 设置B视图
	public View setBView() {
		try {
			/*
			 * String[] ColumnNames = { "fundb_id", "fundb_name",
			 * "fundb_current_price", "b_est_val", "fundb_base_est_dis_rt",
			 * "fundb_discount_rt", "fundb_price_leverage_rt" };
			 * 
			 * String[] names = { "代码", "名称", "现价", "估值", "整体溢价率", "溢价率", "价格杠杆"
			 * }; int[] ids = new int[] { R.id.fundb_id, R.id.fundb_name,
			 * R.id.fundb_current_price, R.id.b_est_val,
			 * R.id.fundb_base_est_dis_rt, R.id.fundb_discount_rt,
			 * R.id.fundb_price_leverage_rt };
			 */
			return addView(R.layout.activity_b, UrlUtil.Url_235 + "8080/QhWebNewsServer/ABJi", "type=b");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new View(activity);
	}

	// 设置TextView的点击事件
	public void setClick(TextView view, final int num) {
		listView.add(view);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(num);
				// switch (num) {
				// case 0:
				// setAView();
				// break;
				// case 1:
				// setBView();
				// break;
				// }
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
			LayoutParams params = new LayoutParams(width / 4, 3);
			int left = width * nums[num] / 4 - img.getWidth() / 2;
			params.setMargins(left, 0, 0, 0);
			img.setLayoutParams(params);
		}
	}
}
