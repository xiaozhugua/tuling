package com.abct.tljr.ui.activity.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.adapter.AHDataAdapter;
import com.abct.tljr.utils.OneGuConstance;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xbw
 * @version 创建时间：2015年10月9日 下午4:45:42 AH差价
 */
public class AhDataActivity extends BaseActivity {
	private View v;
	private Activity activity;
	private ViewPager viewPager;
	// private ImageView imageView;
	private List<View> lists = new ArrayList<View>();
	List<TextView> listView = new ArrayList<TextView>();
	// List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	JSONArray array = new JSONArray();
	int count = 0;
	ProgressDialog pd = null;
	private View ah_view;
	private View errorView;
	private LinearLayout noDataContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.activity = this;
		v = View.inflate(this, R.layout.tljr_activity_abdata, null);
		setContentView(v);

		errorView = View.inflate(this, R.layout.layout_hqsb, null);
		ah_view = View.inflate(this, R.layout.activity_ah, null);

		noDataContainer =           (LinearLayout)v.findViewById(R.id.activity_ah_no_data_ll);

		((TextView) v.findViewById(R.id.pe_main_title)).setText("A/H数据");
		v.findViewById(R.id.abdatagrp).setVisibility(View.GONE);
		v.findViewById(R.id.img_lanxian).setVisibility(View.GONE);

		lists.add(setAHView());

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

		findViewById(R.id.pe_img_jzjl_fanhui).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

/**
 * 成功视图*/
	private   void    successUi(){
		noDataContainer.setVisibility(View.GONE);
	}


	/**
	 * 失败视图*/
	private   void   failui(final String url, final String param, final int layoutId, final View view){
		noDataContainer.setVisibility(View.VISIBLE);
		View     inflateView=View.inflate(this,R.layout.layout_hqsb,null);
		Button      button=            (Button)inflateView.findViewById(R.id.layout_hqsb_chongzai);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updatelistview(url,param,layoutId,view);
			}
		});
		noDataContainer.addView(inflateView);
	}
	/**
	 * 加载中视图*/
	private   void   jiazaiUi(){
		noDataContainer.setVisibility(View.VISIBLE);
		TextView    tv=new TextView(this);
		tv.setText("数据加载中······");
		tv.setTextColor(Color.parseColor("#000000"));
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		noDataContainer.addView(tv);
	}


	private ListView ahListView;

	// 更新listview
	public void updatelistview(final String url, final String param, final int layoutId, final View view) {
		jiazaiUi();
		count++;
		try {
			// list = new ArrayList<Map<String, String>>();
			// LogUtil.e("updatelistview", "url=="+url+"?"+ param);
			String superUrl = OneGuConstance.AHDataAddress+"QhWebNewsServer/AhRatio";
			NetUtil.sendPost(superUrl + "?" + param, new NetResult() {

				@Override
				public void result(final String msg) {
					try {
						// array=new JSONArray(msg);
						post(new Runnable() {

							@Override
							public void run() {
								try {
									successUi();
									ahListView = (ListView) ah_view.findViewById(R.id.ah);

									if (msg == null || "".equals(msg)) {
										if (pd.isShowing()) {
											pd.dismiss();
										}
                                        //没有数据
                                        failui(url,param,layoutId,view);

										return;

									}

									array = new JSONObject(msg).getJSONArray("data");

									ahListView.setAdapter(new AHDataAdapter(array, activity));

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

			// http://news.tuling.me/QhWebNewsServer/AhRatio?
			/*
			 * HttpRequest.sendGet(url+"?"+ param, new HttpRevMsg() {
			 * 
			 * @Override public void revMsg(String msg) {
			 * LogUtil.e("updatelistview", "ah=="+msg); try { array =new
			 * JSONObject(msg) .getJSONArray("data"); post(new Runnable() {
			 * 
			 * @Override public void run() { try {
			 * 
			 * JSONObject json = null; Map<String, String> maps = new
			 * HashMap<String, String>();
			 * 
			 * List<Map<String, String>> list = new ArrayList<Map<String,
			 * String>>(); for (int i = 0; i < array.length(); i++) { json =
			 * array.getJSONObject(i); maps = new HashMap<String, String>();
			 * Iterator<String> paramsList=json.keys();
			 * while(paramsList.hasNext()){
			 * maps.put(paramsList.next(),json.getString(paramsList.next())); }
			 * list.add(maps); }
			 * 
			 * MySimpleCursorAdapter adapter = new MySimpleCursorAdapter(
			 * activity, list, id, ColumnNames, ids);
			 * adapter.notifyDataSetChanged();
			 * 
			 * ListView lv = (ListView) view .findViewById(lv1);
			 * lv.setAdapter(adapter); if (pd.isShowing()) { pd.dismiss(); } }
			 * catch (Exception e) { e.printStackTrace(); } } }); } catch
			 * (Exception e) { e.printStackTrace(); } } });
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 添加视图
	public View addView(int id, String url, String param, int layoutId) {
		// View view = activity.getLayoutInflater().inflate(id, null);// 就是当前的页面
		updatelistview(url, param, layoutId, ah_view);
		return ah_view;
	}

	// 设置AH视图
	public View setAHView() {
		try {
			/*
			 * String[] ColumnNames = { "a_code", "name", "h_code", "a_price",
			 * "a_inc", "h_price", "h_price_rmb", "h_inc", "ha_ratio" };
			 */

			/*
			 * String[] names = { "A股代码", "名称", "H股编码", "A股价格", "A股涨跌幅",
			 * "H股价格(港元)", "H股价格(元)", "H股涨跌幅", "比价" };
			 */
			/*
			 * int[] ids = new int[] { R.id.ah_code, R.id.ah_name, R.id.h_code,
			 * R.id.a_price, R.id.a_inc, R.id.h_price, R.id.h_price_rmb,
			 * R.id.h_inc, R.id.ha_ratio };
			 */
			return addView(R.layout.activity_ah, UrlUtil.Url_235 + "8080/QhWebNewsServer/AhRatio", "",
					R.layout.ah_view_layout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new View(activity);
	}
}
