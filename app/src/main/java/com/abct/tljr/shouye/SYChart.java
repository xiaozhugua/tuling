package com.abct.tljr.shouye;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.kline.LineView;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.LineChart;
import com.qh.common.listener.NetResult;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.model.User;
import com.qh.common.ui.widget.ProgressDlgUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import io.realm.Case;
import io.realm.Realm;

public class SYChart {

	private View view, view_chart;
	private Activity activity;
	private HorizontalScrollView viewpager = null;
	private LinearLayout layout = null;
	// 记录code market name
	public static ArrayList<Beanchart> arrlist;
	// 记录now updown updownpersen key:code
	private HashMap<String, Beanchart> map;
	private ImageView[] img = null;
	private String TAG = "SYChart";
	private ArrayList<View> viewlist;

	private LineChart linechart;
	private LineView lineview;
	private int preposition = 0;
	private boolean creatdone = false;
	private boolean canclick = true;
	private boolean isfirstsetposition = false;
	private JSONArray arr = new JSONArray();
	public String currentMonthCode;
	public String nextMonthCode;
	public String currentSeason;
	public String nextSeason;
	// private TextView tv_maktop,tv2,tv3;

	public View getView() {
		return view_chart;
	}

	public SYChart(Activity activity, View view) {
		this.activity = activity;
		view_chart = activity.getLayoutInflater().inflate(R.layout.shouye_chart, null);
		viewlist = new ArrayList<View>();
		preposition = 0;
		reset();
		this.view = view;
		linechart = (LineChart) view_chart.findViewById(R.id.chart);
		lineview = new LineView(handler, linechart, "sh", "000001");
		viewpager = (HorizontalScrollView) view.findViewById(R.id.tljr_hot_viewpager);
		layout = (LinearLayout) viewpager.getChildAt(0);
		arrlist = new ArrayList<Beanchart>();
		map = new HashMap<String, Beanchart>();

		Beanchart bean = new Beanchart();
		bean.setMarket("sh");
		bean.setCode("000001");
		bean.setName("上证指数");
		arrlist.add(bean);
		Beanchart bean2 = new Beanchart();
		bean2.setMarket("sz");
		bean2.setCode("399001");
		bean2.setName("深证指数");
		arrlist.add(bean2);
		Beanchart bean3 = new Beanchart();
		bean3.setMarket("sz");
		bean3.setCode("399006");
		bean3.setName("创业板指");
		arrlist.add(bean3);
		
		initView();
	}

	public void gettitlelist() {
		arrlist.clear();
		handler.sendEmptyMessage(15);
		LogUtil.e(TAG, UrlUtil.URL_titlelist + User.getUser().getId());
//		String url=UrlUtil.URL_titlelist + User.getUser().getId();
		NetUtil.sendGet(UrlUtil.URL_titlelist + User.getUser().getId(), new NetResult() {
			@Override
			public void result(String msg) {
				LogUtil.e(TAG, "gettritlelist msg :" + msg);
				if (msg.length() == 0) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							gettitlelist();
						}
					}, 1000);
					return;
				}
				ArrayList<Beanchart> temp = new ArrayList<SYChart.Beanchart>();
				try {
					JSONObject ob = new JSONObject(msg);
					if (ob.getInt("code") == 200) {
						creatdone = false;
						JSONArray arr = ob.getJSONArray("result");
						for (int i = 0; i < arr.length(); i++) {
							LogUtil.i(TAG, "gettritlelist arr :" + i);
							JSONObject ob1 = arr.getJSONObject(i);
							JSONObject obj = ob1.getJSONObject("product");
							if (obj != null) {
								Beanchart bean = new Beanchart();
								bean.setId(obj.getString("productId"));
								bean.setCode(obj.getString("code"));
								bean.setName(obj.getString("name"));
								JSONObject obj1 = obj.getJSONObject("type");
								bean.setMarket(obj1.getString("market"));
								temp.add(bean);
							}
						}
						arrlist.clear();
						arrlist.addAll(temp);
						handler.sendEmptyMessage(15);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	int width = 0;
	int hight = 0;
	boolean inlongclick = false, top_visible = false;
	int checkposition = 0;
	int longtopposition = 0;
	public static boolean isonclick = false;
	boolean isinitview = false;

	public void unlogin(){
		if(arrlist==null){
			arrlist=new ArrayList<>();
		}else{
			arrlist.clear();
		}
		Beanchart bean = new Beanchart();
		bean.setMarket("sh");
		bean.setCode("000001");
		bean.setName("上证指数");
		arrlist.add(bean);
		Beanchart bean2 = new Beanchart();
		bean2.setMarket("sz");
		bean2.setCode("399001");
		bean2.setName("深证指数");
		arrlist.add(bean2);
		Beanchart bean3 = new Beanchart();
		bean3.setMarket("sz");
		bean3.setCode("399006");
		bean3.setName("创业板指");
		arrlist.add(bean3);
		
		initView();
		init(0);
		viewpager.scrollTo(0,0);
	}
	
	public void initView() {
		isinitview = true;
		layout.removeAllViews();
		viewlist.clear();
		width = Util.WIDTH / 3;

		for (int i = 0; i < arrlist.size(); i++) {
			View view = activity.getLayoutInflater().inflate(R.layout.shouye_chart_item, null);
			if (i == preposition) {
				view.findViewById(R.id.onclick).setVisibility(View.VISIBLE);
			}
			view.findViewById(R.id.name).getLayoutParams().width = width;
			view.findViewById(R.id.onclick).getLayoutParams().width = width;
			view.findViewById(R.id.top_ll).getLayoutParams().width = width;
			((TextView) view.findViewById(R.id.name)).setText(arrlist.get(i).getName());
			view.setTag(i);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (inlongclick) {
						inlongclick = false;
						LogUtil.e("onclick", "a");
					} else {
						if (top_visible) {
							top_visible = false;
							viewlist.get(longtopposition).findViewById(R.id.top_ll).setVisibility(View.GONE);
						} else {
							if (canclick) {
								int position = Integer.parseInt(v.getTag().toString());
								if (preposition == position) {
									LogUtil.e("onclick", "e");
									Beanchart be = arrlist.get(position);
									String key = be.getMarket().toLowerCase() + be.getCode().toLowerCase();
									LogUtil.e("onclickkey", key);
									Realm realm = Realm.getDefaultInstance();
									OneGu gu1 = realm.where(OneGu.class).equalTo("marketCode", key, Case.INSENSITIVE)
											.findFirst();
									realm.close();
									if (gu1 != null) {
										OneGu gu = Constant.cloneGu(gu1);
										Intent intent = new Intent(activity, OneGuActivity.class);
										Bundle bundle = new Bundle();
										bundle.putString("code", gu.getCode());
										bundle.putString("name", gu.getName());
										bundle.putString("market", gu.getMarket());
										bundle.putString("key", gu.getKey());
										bundle.putSerializable("onegu", gu);
										intent.putExtras(bundle);
										activity.startActivity(intent);
									}
									return;
								}
								ProgressDlgUtil.showProgressDlg("", activity);
								isonclick = true;
								checkposition = position;
								LogUtil.i("LiW", "SY preposition :" + preposition + "  view.size :" + viewlist.size()
										+ "  pso :" + position);
								if (preposition < viewlist.size()) {
									viewlist.get(preposition).findViewById(R.id.onclick).setVisibility(View.GONE);
									LogUtil.i("LiW", "SY 1");
									if (isfirstsetposition)
										viewlist.get(0).findViewById(R.id.onclick).setVisibility(View.GONE);
								}
								viewlist.get(position).findViewById(R.id.onclick).setVisibility(View.VISIBLE);
								preposition = position;
								init(position);
								handler.sendEmptyMessage(16);
							} else {
								Toast.makeText(activity, "亲，请勿操作过快喔", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}
			});
			view.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (MyApplication.getInstance().self != null) {
						viewlist.get(longtopposition).findViewById(R.id.top_ll).setVisibility(View.GONE);
						inlongclick = true;
						top_visible = true;
						longtopposition = Integer.parseInt(v.getTag().toString());
						v.findViewById(R.id.top_ll).setVisibility(View.VISIBLE);
						v.findViewById(R.id.maketop).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								LogUtil.i("moveNest", "onlick :" + longtopposition);
								Beanchart b = arrlist.get(longtopposition);
								maketop(b.getId());
								arrlist.remove(longtopposition);
								arrlist.add(0, b);
								initView();
								top_visible = false;
								//viewlist.get(longtopposition).findViewById(R.id.top_ll).setVisibility(View.GONE);
							}
						});
						v.findViewById(R.id.deletetop).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								LogUtil.i("moveNest", "onlick 2");
								remove();
								top_visible = false;
								viewlist.get(longtopposition).findViewById(R.id.top_ll).setVisibility(View.GONE);
							}
						});
					}
					return false;
				}
			});

			layout.addView(view);
			viewlist.add(view);
		}
		View viewadd = activity.getLayoutInflater().inflate(R.layout.shouye_chart_itemadd, null);
		viewadd.findViewById(R.id.width).getLayoutParams().width = width;
		viewadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (MyApplication.getInstance().self != null) {
					Intent intent = new Intent(activity, SearchActivity.class);
					activity.startActivityForResult(intent, 0);
				} else {
					((MainActivity) activity).mHandler.sendEmptyMessage(93);
				}
			}
		});
		layout.addView(viewadd, 0);

		int pagenum = layout.getChildCount() / 3 + (layout.getChildCount() % 3 == 0 ? 0 : 1);
		img = new ImageView[pagenum];
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.tljr_hot_viewGroup);
		layout.getLayoutParams().height = (int) (Util.HEIGHT * 35 / Util.IMAGEHEIGTH);
		layout.removeAllViews();
		for (int i = 0; i < img.length; i++) {
			img[i] = new ImageView(activity);
			if (0 == i) {
				img[i].setBackgroundResource(R.drawable.img_yuandian1);
			} else {
				img[i].setBackgroundResource(R.drawable.img_yuandian2);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.leftMargin = 15;
			params.width = 15;
			params.height = 15;
			layout.addView(img[i], params);
		}
		viewpager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_UP:
					scrollX = viewpager.getScrollX();
					detectScrollX();
					break;
				}
				return false;
			}
		});
		reflushDP();
		creatdone = true;
		if (temppage >= img.length) {
			temppage--;
		}
		if (temppage != 0) {
			check(temppage);
		}
		handler.sendEmptyMessage(17);
	}

	private void remove() {
		ProgressDlgUtil.showProgressDlg("", activity);
		String url = UrlUtil.URL_addremove + MyApplication.getInstance().self.getId();
		url = url + "/remove/" + arrlist.get(longtopposition).getId();
		HttpRequest.sendPost(url, "", new HttpRevMsg() {
			@Override
			public void revMsg(String msg) {
				try {
					JSONObject ob = new JSONObject(msg);
					if (ob.getInt("code") == 200) {
						handler.sendEmptyMessage(18);
					} else {
						handler.sendEmptyMessage(19);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private int scrollX;
	private int temppage = 0;

	private void detectScrollX() {
		view.postDelayed(new Runnable() {
			@Override
			public void run() {
				int tempScrollX = viewpager.getScrollX();
				if (tempScrollX != scrollX) {
					scrollX = tempScrollX;
					detectScrollX();
				} else {
					int page = 0;
					if (tempScrollX > Util.WIDTH / 6) {
						page = tempScrollX / Util.WIDTH + 1;
					}
					viewpager.smoothScrollTo(
							((int) (tempScrollX + Util.WIDTH / 6) / (Util.WIDTH / 3)) * (Util.WIDTH / 3), 0);
					if (page >= img.length) {
						page--;
					}
					temppage = page;
					check(page);
				}
				LogUtil.i("moveNest", "tempx :" + scrollX);
			}
		}, 200);
	}

	private void check(int page) {
		for (int i = 0; i < img.length; i++) {
			if (page == i) {
				img[i].setBackgroundResource(R.drawable.img_yuandian1);
			} else {
				img[i].setBackgroundResource(R.drawable.img_yuandian2);
			}
		}
	}

	public void init(int position) {
		if (position < arrlist.size()) {
			Beanchart be = arrlist.get(position);
			LogUtil.i(TAG, "markey :" + be.getMarket() + "  code :" + be.getCode());
			lineview.setMarCo(be.getMarket(),be.getCode());
			String key = "";
			String k = arrlist.get(position).getCode();
			if ((k.substring(2)).equalsIgnoreCase("CURRENT-SEASON")) {
				key = (k.substring(0, 2)) + currentSeason;
			} else if ((k.substring(2)).equalsIgnoreCase("current-Month")) {
				key = (k.substring(0, 2)) + currentMonthCode;
			} else if ((k.substring(2)).equalsIgnoreCase("next-Month")) {
				key = (k.substring(0, 2)) + nextMonthCode;
			} else if ((k.substring(2)).equalsIgnoreCase("next-Season")) {
				key = (k.substring(0, 2)) + nextSeason;
			} else {
				key = arrlist.get(position).getMarket().toLowerCase() + k;
			}
			if (map.get(key) != null) {
				be = map.get(key);
				lineisred = be.updown >= 0;
				lineview.setlinecolor(lineisred ? 1 : -1);
				if(lineview.custom!=null){
					lineview.custom.setStartvalue(be.getStart());
					lineview.initData();
				}else{
					lineview.initLineChat();
					lineview.custom.setStartvalue(be.getStart());
					lineview.initData();
				}
				LogUtil.i(TAG, "start :" + be.getStart());
				//lineview.initData();
				LogUtil.i("XXX", "in init(position) pre :" + be.getStart());
			}
		}
	}

	// chart
	public void reflushDP() {
		String url = UrlUtil.URL_real;
		for (int i = temppage * 3 - 3; i < temppage * 3 + 3; i++) {
			if (i >= 0 && i < arrlist.size()) {
				Beanchart bean = arrlist.get(i);
				if (i == 0) {
					url += (bean.getMarket() + "|" + bean.getCode());
				} else {
					url += ("," + bean.getMarket() + "|" + bean.getCode());
				}
			}
		}
		LogUtil.i(TAG, "url :" + url);
		HttpRequest.sendPost(url, "", new HttpRevMsg() {
			@Override
			public void revMsg(final String msg) {
				LogUtil.e("SYChart_reflush", msg);
				freshGridMapData(msg);
			}
		});
	}

	public void maketop(String productid) {
		String url = UrlUtil.URL_titlelist + MyApplication.getInstance().self.getId() + "/rivet/" + productid;
		LogUtil.i("moveNest", "make Top url :" + url);
		HttpRequest.sendPost(url, "", new HttpRevMsg() {
			@Override
			public void revMsg(String msg) {
				// TODO Auto-generated method stub
				LogUtil.i("moveNest", "make Top :" + msg);
			}
		});
	}

	int firsttime = 0;
	boolean lineisred = true;

	private void freshGridMapData(final String msg) {
		try {
			JSONObject object = new JSONObject(msg);
			if (object.getInt("code") != 200) {
				return;
			}
			JSONArray arr = object.getJSONArray("result");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				// JSONArray arritem = obj.getJSONArray("data");
				Beanchart bean = new Beanchart();
				if (obj.has("close")) {
					bean.setNow(obj.optDouble("close"));
				} else {
					bean.setNow(obj.optDouble("last"));
				}
				bean.setStart((float) obj.optDouble("preClose"));
				bean.setUpdown(obj.optDouble("ud"));
				bean.setUpdownpersen(obj.optDouble("upRate"));
				if (firsttime == 0) {
					if (checkposition == i) {
						lineisred = bean.updown >= 0;
						lineview.setlinecolor(lineisred ? 1 : -1);
						if(lineview.custom!=null&&bean!=null){
							lineview.custom.setStartvalue(bean.getStart());
							handler.sendEmptyMessage(88);
						}else{
							LogUtil.e("data","noObject");
							lineview.initLineChat();
							lineview.custom.setStartvalue(bean.getStart());
							handler.sendEmptyMessage(88);
						}
						LogUtil.i(TAG, "SYchart up :" + i);
						if (i > 0) {
							firsttime = 1;
						}
					}
				}

				// 记录颜色
				if (checkposition == i) {
					if (!(lineisred && bean.updown >= 0)) {
						LogUtil.i("XXX", "lineischange");
					}
					lineisred = bean.updown >= 0;
					lineview.setlinecolor(lineisred ? 1 : -1);
					handler.sendEmptyMessage(20);
				}
				map.put(obj.getString("market").toLowerCase() + obj.getString("code"), bean);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handler.sendEmptyMessage(10);
	}

	private void updatadata() {
		int size = arrlist.size();
		for (int i = 0; i < size; i++) {
			String key = "";
			if ((arrlist.get(i).getCode().substring(2)).equalsIgnoreCase("CURRENT-SEASON")) {
				key = (arrlist.get(i).getCode().substring(0, 2)) + currentSeason;
			} else if ((arrlist.get(i).getCode().substring(2)).equalsIgnoreCase("current-Month")) {
				key = (arrlist.get(i).getCode().substring(0, 2)) + currentMonthCode;
			} else if ((arrlist.get(i).getCode().substring(2)).equalsIgnoreCase("next-Month")) {
				key = (arrlist.get(i).getCode().substring(0, 2)) + nextMonthCode;
			} else if ((arrlist.get(i).getCode().substring(2)).equalsIgnoreCase("next-Season")) {
				key = (arrlist.get(i).getCode().substring(0, 2)) + nextSeason;
			} else {
				key = arrlist.get(i).getMarket().toLowerCase() + arrlist.get(i).getCode();
			}
			Beanchart bean = map.get(key);
			if (bean != null && bean.getNow() != null && i < viewlist.size()) {
				((TextView) viewlist.get(i).findViewById(R.id.now)).setText("" + bean.getNow());
				((TextView) viewlist.get(i).findViewById(R.id.updown))
						.setText((bean.getUpdown() >= 0 ? "+" : "") + bean.getUpdown());
				((TextView) viewlist.get(i).findViewById(R.id.updownpersen))
						.setText((bean.getUpdown() >= 0 ? "+" : "") + bean.getUpdownpersen() + "%");

				((TextView) viewlist.get(i).findViewById(R.id.now))
						.setTextColor((bean.getUpdown() >= 0 ? Util.c_red : Util.c_green));
				((TextView) viewlist.get(i).findViewById(R.id.updown))
						.setTextColor((bean.getUpdown() >= 0 ? Util.c_red : Util.c_green));
				((TextView) viewlist.get(i).findViewById(R.id.updownpersen))
						.setTextColor((bean.getUpdown() >= 0 ? Util.c_red : Util.c_green));
				if (i == preposition) {
					viewlist.get(i).findViewById(R.id.onclick).setVisibility(View.VISIBLE);
				}
			}
		}
		layout.postInvalidate();
	}

	private int time = 0;

	// private boolean first = true;
	public void runforupdata() {
		if (arrlist.size() > viewlist.size()) {
			return;
		}
		if (!creatdone) {
			return;
		}
		time++;
		if (time % 3 == 0) {
			reflushDP();
		} else {
			return;
		}
		if (time >= 30) {
			handler.sendEmptyMessage(11);
			time = 0;
		}
	}

	public void getpreposition() {
		String poStr = PreferenceUtils.getInstance().preferences.getString("titlechartposition", "");
		if (!poStr.equals("")) {
			String[] strs = poStr.split(",");
			if (strs[0].equals(MyApplication.getInstance().self.getId())) {
				preposition = Integer.parseInt(strs[1]);
				isfirstsetposition = true;
				checkposition = preposition;
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 10:
				// 更新
				updatadata();
				if (isinitview) {
					init(checkposition);
					isinitview = false;
				}
				break;
			case 11:
				// 更新chart
				lineview.initData();
				break;
			case 12:
				canclick = true;
				break;
			case 13:
				lineview.initData();
				break;
			case 14:
				// Toast.makeText(activity, "获取不到指数数据...",
				// Toast.LENGTH_SHORT).show();
				break;
			case 15:
				LogUtil.i(TAG, "in 15");
				// LogUtil.i("move", "hight :"+view_chart.getHeight());
				// ((MainActivity)activity).shouYe.mScrollView.setchangemoveHight(view_chart.getHeight());
				initView();
				break;
			case 16:
				if (MyApplication.getInstance().self != null) {
					String saveposition = MyApplication.getInstance().self.getId() + "," + preposition;
					PreferenceUtils.getInstance().preferences.edit().putString("titlechartposition", saveposition)
							.commit();
				}
				break;
			case 17:
				if (isfirstsetposition && preposition >= 2) {
					// int x = preposition ;
					viewpager.scrollTo(Util.WIDTH / 3 * (preposition - 1), 0);
					isfirstsetposition = false;
					int check = (preposition + 1) / 3;
					temppage = check;
					check(check);
				}
				break;
			case 18:
				arrlist.remove(longtopposition);
				longtopposition = 0;
				initView();
				ProgressDlgUtil.stopProgressDlg();
				break;
			case 19:
				ProgressDlgUtil.stopProgressDlg();
				break;
			case 20:
				lineview.reflushData1();
				break;
			case 88:
				// linechart.invalidate();
				lineview.reflushData1();
				break;
			default:
				break;
			}
		};
	};

	public void reset() {
		Date date = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar = (calendar == null) ? Calendar.getInstance() : calendar;
		int week = calendar.get(4);
		int day = calendar.get(7);
		if ((week >= 3)) {
			if (week > 3) {
				calendar.set(2, calendar.get(2) + 1);
			} else if (day == 7) {
				calendar.set(2, calendar.get(2) + 1);
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		SimpleDateFormat formatyear = new SimpleDateFormat("yy");
		SimpleDateFormat formatMM = new SimpleDateFormat("MM");

		String currentMonth = format.format(calendar.getTime());

		int year = Integer.parseInt(formatyear.format(calendar.getTime()));
		int MM = Integer.parseInt(formatMM.format(calendar.getTime()));
		String nextMonth = "";
		String currentSeason = "";
		String nextSeason = "";
		if ((MM + 1) <= 12) {
			nextMonth = (year * 100 + (MM + 1)) + "";
		} else {
			nextMonth = (year + 1) + "01";
		}
		int ys = MM % 3;
		currentSeason = (year * 100 + (MM + 3 - ys)) + "";
		if ((MM + 3 - ys + 3) > 12) {
			nextSeason = ((year + 1) * 100) + "03";
		} else {
			nextSeason = (year * 100 + (MM + 3 - ys + 3)) + "";
		}

		// int ys = calendar.get(2) % 3;
		//
		// calendar.set(2, calendar.get(2) + 1);
		// String nextMonth = format.format(calendar.getTime());
		// calendar.set(2, calendar.get(2) + 3 - ys - 2);
		// String currentSeason = format.format(calendar.getTime());
		// calendar.set(2, calendar.get(2) + 3);
		// String nextSeason = format.format(calendar.getTime());

		this.currentMonthCode = currentMonth;
		this.currentSeason = currentSeason;
		this.nextMonthCode = nextMonth;
		this.nextSeason = nextSeason;
		// this.events.onChange(this);
	}

	public static class Beanchart {
		String market;
		String code;
		String name;
		Double now;
		Double updown;
		Double updownpersen;
		String Id;
		float start;

		public float getStart() {
			return start;
		}

		public void setStart(float start) {
			this.start = start;
		}

		public String getId() {
			return Id;
		}

		public void setId(String id) {
			Id = id;
		}

		public Double getNow() {
			return now;
		}

		public void setNow(Double now) {
			this.now = now;
		}

		public Double getUpdown() {
			return updown;
		}

		public void setUpdown(Double updown) {
			this.updown = updown;
		}

		public Double getUpdownpersen() {
			return updownpersen;
		}

		public void setUpdownpersen(Double updownpersen) {
			this.updownpersen = updownpersen;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMarket() {
			return market;
		}

		public void setMarket(String market) {
			this.market = market;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

	}

}
