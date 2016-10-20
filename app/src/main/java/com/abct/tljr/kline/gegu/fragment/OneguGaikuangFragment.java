package com.abct.tljr.kline.gegu.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.kline.gegu.activity.MoreNoticeActivity;
import com.abct.tljr.kline.gegu.entity.TenManJavaBean;
import com.abct.tljr.kline.gegu.util.ChartUtils;
import com.abct.tljr.kline.gegu.util.DataListener;
import com.abct.tljr.kline.gegu.util.NetUtils;
import com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView;
import com.abct.tljr.kline.gegu.view.OneguListView;
import com.abct.tljr.kline.gegu.view.PieChartView;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.OneGuConstance;
import com.abct.tljr.utils.ViewUtil;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.model.AppInfo;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * onegu的概况fragmentf
 */
public class OneguGaikuangFragment extends BaseFragment {

	private View rootView;
	private LinearLayout noticeMore;
	private LinearLayout threeNoticeSimple;
	/*
	 * private LinearLayout belongIndustry; private LinearLayout
	 * relevanceIndustry;
	 */
	// private LinearLayout belongTheme;

	private OneGuActivity oneGuActivity;
	private String code;
	private String name;
	private String market;
	private String guKey;
	private boolean isJJ;
	private JSONObject marketInfo;
	private String param;

	private TextView introduceText;
	private TextView workPlaceText;
	private OneguGuZhiScrollView scrollView;
	private TextView noDataText;
	/**
	 * 环形图专用的十种颜色
	 * 蓝1：0071b8
	 蓝2：00acee
	 蓝3：6dcff4
	 绿1：01652f
	 绿2：00a551
	 绿3：76be41
	 黄1：fff794
	 黄2：fee600
	 黄3：ffcb05
	 黄4：ef701e

	 红色：ee584e

	 黄色：eece4e

	 绿色：46b35d

	 蓝色：23b7ee

	 粉色：ff6ddb*/



	private final int[] cricleColors         = {
			Color.parseColor("#0071b8"),
			Color.parseColor("#00acee"),
			Color.parseColor("#6dcff4"),
			Color.parseColor("#01652f"),
			Color.parseColor("#00a551"),
			Color.parseColor("#76be41"),
			Color.parseColor("#fff794"),
			Color.parseColor("#fee600"),
			Color.parseColor("#ffcb05"),
			Color.parseColor("#ef701e"),

			Color.parseColor("#ee584e"),
			Color.parseColor("#ff6ddb")


	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		rootView = View.inflate(getActivity(), R.layout.fragment_onegu_gaikuang, null);
		oneGuActivity = (OneGuActivity) this.getActivity();
		

		// initRootView();
		return rootView;
	}

	private boolean rootViewFloat = false;

	/**
	 * 初始化根布局
	 */
	public void initRootView() {
		// TODO Auto-generated method stub

		if (rootViewFloat) {
			return;
		}
		rootViewFloat = true;
		initData();
		initBaseView();

		if (Constant.marketInfo == null) {
			Constant.getMarketInfo(new Complete() {
				@Override
				public void complete() {
					initMarket();
				}
			});
		} else {
			initMarket();
		}

	}

	public void initMarket() {
		// TODO Auto-generated method stub
		marketInfo = Constant.marketInfo.get(market.toLowerCase());

		getAllCompaneyData();

	}

	public void getAllCompaneyData() {
		try {
			// 获取新闻公告的数据
			getNoticeData();
			// 获取主题的数据
			getCompaneyThemeData();
			// 获取公司信息的数据
			getCompaneyInformationData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private TextView nameText;
	private TextView themeText;
	private TextView relevanceIndustryText;
	private TextView belongIndustryText;
	private TextView establishDayText;
	private TextView provinceText;
	private TextView cityText;
	private TextView secretaryText;
	private TextView speakPersonText;
	private int screenWith;
	private int screenHight;

	/**
	 * 获取公司信息的数据
	 */
	private void getCompaneyInformationData() {
		// TODO Auto-generated method stub
		String url = OneGuConstance.getURL("ComDataImpf", "comdata", param);
		// LogUtil.e("gh2", "公司信息url==" + url);
		NetUtil.sendPost(url, new NetResult() {

			@Override
			public void result(String result) {

				LogUtil.e("Companey", result);
				// TODO Auto-generated method stub
				if (result == null || "".equals(result)) {
					showMessage("没有数据");
					return;
				}
				try {
					JSONObject resJson = new JSONObject(result);
					if (resJson.optInt("status") != 1) {
						showMessage("服务器出问题");
						return;
					}
					JSONObject json = resJson.optJSONObject("result");

					if (json==null) {
						return;
					}
					// organizationName.addView(getCompaneyItemTextView(json.optString("CNAME")));//机构名字
					long buildDate = json.optLong("BUILD_DATE");

					Date date = new Date(buildDate);
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					// ----------------------------------------------------------
					// setTextViewWith(nameText, json.optString("CNAME") + "");
					nameText.setText(json.optString("CNAME") + "");
					// setTextViewWith(establishDayText,
					// simpleDateFormat.format(date));
					establishDayText.setText(simpleDateFormat.format(date));
					// setTextViewWith(workPlaceText,
					// json.optString("OFFICE_ADDR") + "");
					workPlaceText.setText(json.optString("OFFICE_ADDR") + "");
					// setTextViewWith(introduceText,
					// json.optString("COM_BRIEF") + "");
					introduceText.setText(json.optString("COM_BRIEF") + "");
					// setTextViewWith(provinceText,
					// json.optString("PROVINCE"));
					provinceText.setText(json.optString("PROVINCE"));

					// setTextViewWith(cityText, json.optString("CITY"));
					cityText.setText(json.optString("CITY"));
					// setTextViewWith(secretaryText,
					// json.optString("BOARD_SECTRY"));
					secretaryText.setText(json.optString("BOARD_SECTRY"));
					// setTextViewWith(speakPersonText, json.optString("REPR"));
					speakPersonText.setText(json.optString("REPR"));
					// setTextViewWith(belongIndustryText,
					// json.optString("GICS_INDU_3"));
					belongIndustryText.setText(json.optString("GICS_INDU_3"));
					// setTextViewWith(relevanceIndustryText,
					// json.getString("GICS_INDU_2") + ", " +
					// json.getString("GICS_INDU_1"));
					relevanceIndustryText.setText(json.getString("GICS_INDU_2") + ", " + json.getString("GICS_INDU_1"));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 设置textview 的控件宽度和文本内容
	 */
	private void setTextViewWith(TextView tv, String text) {
		screenWith = ViewUtil.getScreenWidth(activity);// 当前屏幕宽度
		// screenHight = ViewUtil.getScreenHeight(activity);// 获取屏幕高度
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWith / 10 * 7,
				LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(layoutParams);
		tv.setPadding(AppInfo.dp2px(oneGuActivity, 10), 0, 0, 0);
		tv.setText(text);

	}

	/**
	 * 获取主题的数据
	 */
	private void getCompaneyThemeData() {

		// TODO Auto-generated method stub
		// String param = "";
		// 测试
		/*
		 * JSONObject jo = new JSONObject(); try { jo.put("market", "sz");
		 * jo.put("code", "000002");
		 * 
		 * param = jo.toString();
		 * 
		 * } catch (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		String url = OneGuConstance.getURL("ThemeImpf", "theme", param);
		LogUtil.e("gh2", "公司主题url==" + url);
		NetUtil.sendPost(url, new NetResult() {

			@Override
			public void result(String result) {
				// TODO Auto-generated method stub
				if (result == null || "".equals(result)) {
					// showMessage("没有数据");
					return;
				}
				JSONObject json;
				try {
					json = new JSONObject(result);
					int i = json.optInt("status");
					if (i != 1) {
						return;
					}
					// LogUtil.e("momo11", "有数据");

					JSONObject resObj = json.optJSONObject("result");
					if (resObj.optInt("code") != 200) {
						// LogUtil.e("gh", "获取主题code==200");
						return;
					}

					JSONArray statusArray = resObj.optJSONArray("result");

					StringBuffer sb = new StringBuffer("");
					for (int j = 0; j < statusArray.length(); j++) {
						String themeName = statusArray.optJSONObject(j).optString("themeName");
						if (j != statusArray.length() - 1) {
							sb.append(" " + themeName + ",");
						} else {
							sb.append(" " + themeName);
						}
					}

					// themeText.setText(sb.toString());
					setTextViewWith(themeText, sb.toString());// 所属主题
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

	}

	private void showMessage(String message) {
		Toast.makeText(oneGuActivity, message, Toast.LENGTH_SHORT).show();
	}

	private JSONArray noticeJsonArray = null;
	private PieChart tenChairMansChart;
	private LinearLayout lables;

	private LinearLayout managerHeader;
	private LinearLayout managerListViewContainer;

	/**
	 * 获取公告数据
	 */
	private void getNoticeData() throws JSONException {

		noDataText=      (TextView)rootView.findViewById(R.id.layout_onegu_gk_gsgg_list_tv);//


		// TODO Auto-generated method stub
		ProgressDlgUtil.showProgressDlg("", oneGuActivity);
		NetUtil.sendPost(UrlUtil.Url_235 + "8080/QhOtherServer/stock_url/get", "market=" + market + "&code=" + code,
				new NetResult() {

					@Override
					public void result(final String msg) {
						// TODO Auto-generated method stub
						ProgressDlgUtil.stopProgressDlg();
						oneGuActivity.post(new Runnable() {

							@Override
							public void run() {
								try {
									if (msg==null  ||  "".equals(msg)){

										noDataText.setText("没有数据");
										return;
									}
									JSONObject object = new JSONObject(msg);
									JSONArray tabs = marketInfo.optJSONArray("tabs");
									for (int j = 0; j < tabs.length(); j++) {
										String s = tabs.optJSONObject(j).optString("key");
										LogUtil.e("ceshi", "这个s是==" + s);
										if (s == null || "".equals(s)) {
											LogUtil.e("momo", "s==null或者是为空字符串");
											noDataText.setText("没有数据");
											continue;

										}
										// 公司公告数据
										if (object.has(s) && "notices".equals(s)) {
											JSONArray array = object.optJSONArray(s);
											noticeJsonArray = array;

											// LogUtil.e("ceshi",
											// array.toString());
											int number = 0;
											if (array.length() >= 3) {
												number = 3;
											} else if (array.length() < 3 && array.length() > 0) {
												number = array.length();
											} else {
												Toast.makeText(oneGuActivity, "", Toast.LENGTH_SHORT).show();
												noDataText.setText("没有数据");
												return;
											}
											for (int i = 0; i < number; i++) {
												JSONObject obj = array.optJSONObject(i);
												if (i == 0) {
													threeNoticeSimple.removeAllViews();

												}
												threeNoticeSimple.addView(getOneXw(obj.optString("title"),
														obj.optString("datetime"), name, obj.optString("url")));
											}

										}

									}

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
				});
	}

	/**
	 * 获取公告数据 （可以获取数据源的）
	 */
	private void getNoticeData2() throws JSONException {
		// ProgressDlgUtil.showProgressDlg("", oneGuActivity);
		String url = UrlUtil.Url_235 + "8080/QhOtherServer/stock_url/get?code=" + code + "&market=" + market
				+ "&sourth=true";
		NetUtil.sendPost(url, new NetResult() {

			@Override
			public void result(String msg) {
				// TODO Auto-generated method stub
				try {
					JSONObject msgJsonObject = new JSONObject(msg);

					JSONArray array = msgJsonObject.optJSONArray("notices");
					noticeJsonArray = array;
					int number = 0;
					if (array.length() >= 3) {
						number = 3;
					} else if (array.length() < 3 && array.length() > 0) {
						number = array.length();
					} else {
						Toast.makeText(oneGuActivity, "", Toast.LENGTH_SHORT).show();
						return;
					}
					for (int i = 0; i < number; i++) {
						JSONObject obj = array.optJSONObject(i);
						if (i == 0) {
							threeNoticeSimple.removeAllViews();

						}
						threeNoticeSimple.addView(getOneXw(obj.optString("title"), obj.optString("datetime"), name,
								obj.optString("url")));
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 返回公司公告的条目
	 */
	public View getOneXw(final String title, final String time, final String name, final String url) {
		View v = View.inflate(oneGuActivity, R.layout.tljr_item_syxw2, null);
		((TextView) v.findViewById(R.id.tljr_txt_syxw)).setText(title);
		// TextView comefrom = (TextView)
		// v.findViewById(R.id.tljr_txt_syxw_laiyuan);// 新闻来源
		if (time != null) {
			v.findViewById(R.id.tljr_txt_syxw_time).setVisibility(View.VISIBLE);
			((TextView) v.findViewById(R.id.tljr_txt_syxw_time)).setText(time);
		}
		v.setPadding(0, 10, 0, 10);
		if (!url.equals("")) {
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(oneGuActivity, WebActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("url", url);
					bundle.putString("name", name);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
		}
		return v;
	}

	public void initData() {
		// 新页面接收数据
		Bundle bundle = oneGuActivity.getIntent().getExtras();
		code = bundle.getString("code");// 我武生物右边的代码
		name = bundle.getString("name");// 我武生物
		market = bundle.getString("market");
		guKey = bundle.getString("key");
		isJJ = guKey.substring(0, 2).equals("jj");

		JSONObject jo = new JSONObject();
		try {
			jo.put("market", market);
			jo.put("code", code);

			param = jo.toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 记录的是scrollView的竖直滑动的距离*/
	private   int   scrollY=0;

	/**
	 * 找出根布局中 的各个控件id号
	 */
	private void initBaseView() {
		// TODO Auto-generated method stub

		scrollView = (OneguGuZhiScrollView) rootView.findViewById(R.id.fragment_onegu_gaikuang_sv);

		scrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_UP:

					LogUtil.e("zzzzz", "" + scrollView.getScrollY());
					if (scrollView.getScrollY() > 0) {
						oneGuActivity.sw.setEnabled(false);
					} else {
						oneGuActivity.sw.setEnabled(true);
					}
					break;
				}

				return false;
			}
		});

		// 公司公告部分的控件
		noticeMore = (LinearLayout) rootView.findViewById(R.id.layout_onegu_gk_gsgg_more); // 更多公告按钮

		noticeMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jumpToNoticeMore();
			}

		});

		threeNoticeSimple = (LinearLayout) rootView.findViewById(R.id.layout_onegu_gk_gsgg_list_ll);// 前三个公告例子的容器

		// textView
		nameText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxx_jgmc_text);// 机构名字text
		establishDayText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxx_clrq_text);// 成立日期的text
		workPlaceText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxx_bgdz_text);// 办公地址的text
		introduceText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxx_jgjj_text);// 机构简介的text
		provinceText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxx_sssf_text);// 省份的text
		cityText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxx_sscs_text);// 城市text
		secretaryText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxx_dshms_text);// 秘书text
		speakPersonText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxx_zjswdb_text);// 事务代表text

		themeText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxg_sszt_text);// 所属主题text
		belongIndustryText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxg_sshy_text);// 所属行业的text
		relevanceIndustryText = (TextView) rootView.findViewById(R.id.layout_onegu_gk_gsxg_xghy_text);// 相关行业的text

		setTextViewWith(nameText, "");
		setTextViewWith(establishDayText, "");
		setTextViewWith(workPlaceText, "");
		setTextViewWith(introduceText, "");
		setTextViewWith(provinceText, "");
		setTextViewWith(cityText, "");
		setTextViewWith(secretaryText, "");

		setTextViewWith(speakPersonText, "");
		setTextViewWith(belongIndustryText, "");
		setTextViewWith(relevanceIndustryText, "");

		setTextViewWith(themeText, "");// 所属主题

		// 十大股东
		tenChairMansChart = (PieChart) rootView.findViewById(R.id.layout_ten_big_pieChart);
		lables = (LinearLayout) rootView.findViewById(R.id.layout_ten_big_piechart_lables);
		// 获取十大股东数据
		getTenBigData();

		// 高管列表的表头
		// managerHeader=
		// (LinearLayout)rootView.findViewById(R.id.layout_manager_list_header);
		// 获取高管列表的数据
		getManagerListData();

	}

	/**
	 * 获取高管列表的数据
	 */
	private void getManagerListData() {
		// http://ys.tuling.me/YSServer/YSServlet?class=GglbImpf&package=me.tuling.ys.gglb.&params={"market":"sz","code":"000001"}
		String url = OneGuConstance.getURL("GglbImpf", "gglb", param);
		// managerListView
		NetUtil.sendPost(url, new NetResult() {

			@Override
			public void result(String arg0) {
				LogUtil.e("getManagerListData", arg0);
				if (arg0 == null || "".equals(arg0)) {
					return;
				}
				JSONObject json = null;
				try {

					json = new JSONObject(arg0);
					if (json.optInt("status") != 1) {
						return;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				JSONArray msgArray = json.optJSONArray("result");
				if (msgArray == null || msgArray.length() == 0) {
					return;
				}

				// 高管列表容器
				managerListViewContainer = (LinearLayout) rootView.findViewById(R.id.fragment_onegu_gk_manager_list_ll);

				View managerView = View.inflate(oneGuActivity, R.layout.layout_manage_list, null);
				OneguListView managerListView = new OneguListView(oneGuActivity);

				final ArrayList<View> list = new ArrayList<View>();
				// 每一个条目的高度35dp
				int itemHight = AppInfo.dp2px(oneGuActivity, 35);

				for (int i = 0; i < msgArray.length(); i++) {
					View view = View.inflate(oneGuActivity, R.layout.item_manager_lv, null);
					AbsListView.LayoutParams params = new AbsListView.LayoutParams(
							AbsListView.LayoutParams.MATCH_PARENT, itemHight);
					view.setLayoutParams(params);
					JSONObject msgObject = msgArray.optJSONObject(i);
					((TextView) view.findViewById(R.id.item_manager_name)).setText(msgObject.optString("name"));
					((TextView) view.findViewById(R.id.item_manager_age)).setText(msgObject.optString("age"));
					((TextView) view.findViewById(R.id.item_manager_sex)).setText(msgObject.optString("sex"));
					((TextView) view.findViewById(R.id.item_manager_education))
							.setText(msgObject.optString("education"));
					((TextView) view.findViewById(R.id.item_manager_post)).setText(msgObject.optString("post"));
					list.add(view);
				}
				LinearLayout.LayoutParams lvParams = null;
				if (msgArray.length() <= 7) {
					lvParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, msgArray.length() * itemHight
							+ (msgArray.length() - 1) * managerListView.getDividerHeight());
				} else {
					lvParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							7 * itemHight + 6 * managerListView.getDividerHeight());
				}
				managerListView.setLayoutParams(lvParams);
				managerListView.setAdapter(new BaseAdapter() {

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						return list.get(position);
					}

					@Override
					public long getItemId(int position) {
						// TODO Auto-generated method stub
						return position;
					}

					@Override
					public Object getItem(int position) {
						// TODO Auto-generated method stub
						return position;
					}

					@Override
					public int getCount() {
						// TODO Auto-generated method stub
						return list.size();
					}
				});
				managerListViewContainer.addView(managerView);
				managerListViewContainer.addView(managerListView);
			}

		});

	}

	/**
	 * 获取十大股东数据
	 */
	private void getTenBigData() {
		// TODO Auto-generated method stub
		String url = OneGuConstance.getURL("SdgdImpf", "sdgd", param);
		File file = NetUtils.getAbsoluteFile(NetUtils.tenBigFileName, oneGuActivity, name);
		NetUtils.getNeedData(url, file, oneGuActivity, new DataListener() {

			@Override
			public void addChartData(JSONArray jsonArray) throws JSONException {
				// 添加十大股东图表的数据
				addTenBigChart(NetUtils.getTenManListByJsonArray(jsonArray));
			}
		});
		scrollView.smoothScrollTo(0,0);

	}

	/**
	 * 跳到更多公告的页面
	 */
	private void jumpToNoticeMore() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(oneGuActivity, MoreNoticeActivity.class);
		if (noticeJsonArray == null) {
			intent.putExtra("noticeJsonArray", "null");
		} else {

			intent.putExtra("noticeJsonArray", noticeJsonArray.toString());
		}
		intent.putExtra("name", name);
		startActivity(intent);
	}

	private <T> boolean isListUseable(ArrayList<T> list) {
		if (list == null || list.size() == 0) {
			return false;
		}
		return true;
	}





	/**
	 * 添加十大股东图标的数据
	 */
	private void addTenBigChart(ArrayList<TenManJavaBean> list) {
		if (!isListUseable(list))
			return;
		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		// float countAll = 0.0f;
		TenManJavaBean anthorEntity = null;// 其余的
		for (int i = 0; i < list.size(); i++) {
			TenManJavaBean entity = list.get(i);
			if (!entity.getName().equals("合计")) {
				xVals.add(entity.getName());
				// 设置Y值

				yVals1.add(new Entry(Float.parseFloat(entity.getRate().replaceAll("%", "")), i));

			} else {
				xVals.add("其余");
				yVals1.add(new Entry(100.0f - Float.parseFloat(entity.getRate().replaceAll("%", "")), i));
			}
		}
		PieDataSet dataSet = new PieDataSet(yVals1, "");
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);
		// dataSet.setValueTypeface(tf);
		ArrayList<Integer> colors = new ArrayList<Integer>();
		/*for (int c : ColorTemplate.VORDIPLOM_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.JOYFUL_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.COLORFUL_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.LIBERTY_COLORS)
			colors.add(c);
		for (int c : ColorTemplate.PASTEL_COLORS)
			colors.add(c);
		colors.add(ColorTemplate.getHoloBlue());*/
		for (int  i:cricleColors){
			colors.add(i);
		}

		dataSet.setColors(colors);

		PieData data = new PieData(xVals, dataSet);
		// data.setValueFormatter((ValueFormatter) new PercentFormatter());
		// LogUtil.e("getYValueSum", data.getYValueSum()+" "+countAll);
		//data.setDrawValues(false);// 不在图表上画图

		data.setValueTextSize(11.0f);
		data.setValueTextColor(Color.WHITE);
		
		PieChartView view = new PieChartView(tenChairMansChart, data);
		view.initChart();
		// 设置下面的标签
		ChartUtils.setBelowLables(xVals, yVals1, colors, data.getYValueSum(), lables, oneGuActivity);

	}

}
