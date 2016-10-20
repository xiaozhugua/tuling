package com.abct.tljr.ranking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.RoundProgressBar;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import io.realm.Realm;

/**
 * @author xbw
 * @version 创建时间：2015年8月8日 下午2:21:21
 */
public class OneRankInfoActivity extends BaseActivity implements OnClickListener {
	private String name, zhname;
	private String uid, zhid;
	private LineChart chart;
	private LinearLayout own;
	private ScrollView sv;
	private LinearLayout grp;
	private ArrayList<OneGu> ownList = new ArrayList<OneGu>();
	private ArrayList<ArrayList<OneHistory>> list = new ArrayList<ArrayList<OneHistory>>();
	private View check;
	private ImageView onepersoninfo_show;
	private String showTag;
	private int ShowTagStatus = 0;
	private TextView onepersoninfo_footer;
	private TextView onepersoninfo_footer_now;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		uid = i.getStringExtra("uid");
		zhid = i.getStringExtra("zhid");
		name = i.getStringExtra("name");
		zhname = i.getStringExtra("zhname");
		setContentView(R.layout.tljr_activity_rank_onepersoninfos);
		onepersoninfo_show = (ImageView) findViewById(R.id.onepersoninfo_show);
		onepersoninfo_show.setOnClickListener(this);
		((TextView) findViewById((R.id.tljr_txt_onepersoninfo_title))).setText(zhname);
		findViewById(R.id.tljr_img_onepersoninfo_fanhui).setOnClickListener(this);
		findViewById(R.id.tljr_onepersoninfo_own).setOnClickListener(this);
		findViewById(R.id.tljr_onepersoninfo_history).setOnClickListener(this);
		findViewById(R.id.tljr_img_onepersoninfo_tianjia).setOnClickListener(this);
		check = findViewById(R.id.tljr_onepersoninfo_check);
		sv = (ScrollView) findViewById(R.id.tljr_sv_onepersoninfo_history);
		onepersoninfo_footer = (TextView) findViewById(R.id.onepersoninfo_footer2);
		onepersoninfo_footer_now = (TextView) findViewById(R.id.onepersoninfo_footer1);
		grp = (LinearLayout) findViewById(R.id.tljr_grp_onepersoninfo_history);

		own = (LinearLayout) findViewById(R.id.tljr_grp_onepersoninfo_own);
		initChart();
		initData();
		submitCode = new HashMap<String, Integer>();
		if (zhname.indexOf("跟投") != 0)
			zhname = "跟投" + zhname;

	}

	private void initChart() {
		chart = (LineChart) findViewById(R.id.tljr_onepersoninfo_chart);
		chart.setDescription("");
		chart.setMaxVisibleValueCount(60);
		chart.setPinchZoom(false);
		XAxis xAxis = chart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));
		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.setLabelCount(5);
		leftAxis.setStartAtZero(false);
		leftAxis.setTextColor(Color.rgb(139, 148, 153));
		chart.getAxisRight().setEnabled(false);
		chart.setDrawGridBackground(false);
		chart.getLegend().setEnabled(false);
	}

	static class ViewHolder {
		TextView time;
		TextView name;
		TextView value;
		TextView gain;
	}

	private void initData() {
		NetUtil.sendPost(UrlUtil.URL_rankinginfo, "uid=" + uid + "&zhid=" + zhid, new NetResult() {
			@Override
			public void result(String msg) {
				try {
					final JSONObject jsonObject = new JSONObject(msg);
					if (jsonObject.getInt("status") == 1) {
						final JSONObject object = jsonObject.getJSONObject("msg");
						JSONArray ownArray = object.getJSONArray("chicang");
						for (int i = 0; i < ownArray.length(); i++) {
							JSONObject ownObj = ownArray.getJSONObject(i);
							OneGu gu = new OneGu();
							gu.setCode(ownObj.optString("code"));
							gu.setMarket(ownObj.optString("market"));
							gu.setId(ownObj.optString("id"));
							gu.setPercent((int) (ownObj.optDouble("percent") * 100));
							gu.setFirst(ownObj.optDouble("price", 0.0));
							gu.setTime(ownObj.optLong("time"));
							gu.setKey(gu.getMarket() + gu.getCode());
							gu.setName("");
							gu.setTag(ownObj.optString("tag", "暂无备注"));
							ownList.add(gu);
						}
						JSONArray historyArray = object.getJSONArray("tiaocang");
						for (int i = 0; i < historyArray.length(); i++) {
							JSONObject historyObj = historyArray.getJSONObject(i);
							long time = historyObj.getLong("time");
							JSONArray arrays = new JSONArray(historyObj.getString("tiaocang"));
							ArrayList<OneHistory> hList = new ArrayList<OneHistory>();
							for (int j = 0; j < arrays.length(); j++) {
								JSONObject hisObject = arrays.getJSONObject(j);
								OneHistory history = new OneHistory();
								history.setName(hisObject.optString("id"));
								history.setSrcP((float) hisObject.optDouble("srcPercent"));
								history.setToP((float) hisObject.optDouble("toPercent"));
								history.setTime(time);
								history.setMarket(hisObject.optString("market"));
								history.setCode(hisObject.optString("code"));
								if (hisObject.has("market") && hisObject.has("code"))
									hList.add(history);
							}
							if (hList.size() > 0)
								list.add(hList);
						}
						reflushDP();
						post(new Runnable() {
							@Override
							public void run() {
								try {
									initHistoryData();
									initUi(object.getJSONObject("zhinfo"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						post(new Runnable() {
							@Override
							public void run() {
								showToast(jsonObject.optString("msg"));
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initUi(JSONObject object) throws JSONException {
		if (object.optLong("date", 0) != 0) {
			((TextView) findViewById(R.id.tljr_txt_onepersoninfo_name))
					.setText(name + "创建(" + Util.getDateOnlyDay(object.optLong("date")) + ")");
		} else {
			((TextView) findViewById(R.id.tljr_txt_onepersoninfo_name)).setText(name);
		}
		showTag = object.optString("tag", "");
		if (showTag.equals("")) {
			findViewById(R.id.onepersoninfo_beizhu).setVisibility(View.GONE);
		} else if (showTag.length() > 16) {
			((TextView) findViewById(R.id.tljr_txt_onepersoninfo_tag)).setText(showTag.substring(0, 15) + "...");
		} else {
			((TextView) findViewById(R.id.tljr_txt_onepersoninfo_tag)).setText(showTag);
			((ImageView) findViewById(R.id.onepersoninfo_show)).setVisibility(View.GONE);
		}

		// ((TextView) findViewById(R.id.tljr_txt_onepersoninfo_tag))
		// .setText(object.optString("tag", "无"));
		((TextView) findViewById(R.id.tljr_txt_onepersoninfo_jz)).setText(object.getString("jingzhi"));
		((TextView) findViewById(R.id.tljr_txt_onepersoninfo_day))
				.setText(Util.df.format(object.getDouble("rishouyi") * 100) + "%");
		((TextView) findViewById(R.id.tljr_txt_onepersoninfo_month))
				.setText(Util.df.format(object.getDouble("yueshouyi") * 100) + "%");
		((TextView) findViewById(R.id.tljr_txt_onepersoninfo_all))
				.setText(Util.df.format(object.getDouble("zongshouyi") * 100) + "%");
		((TextView) findViewById(R.id.tljr_txt_onepersoninfo_beta)).setText(object.optString("beta"));
		((TextView) findViewById(R.id.tljr_txt_onepersoninfo_sharpe)).setText(object.optString("sharpe"));
		((TextView) findViewById(R.id.tljr_txt_onepersoninfo_alpha)).setText(object.optString("alpha"));
		((TextView) findViewById(R.id.tljr_txt_onepersoninfo_zdhc))
				.setText(Util.df.format(object.getDouble("maxdrawdown") * 100) + "%");
		reflushChart(object.getJSONArray("jingzhiliebiao"));
	}

	private void initHistoryData() {
		post(new Runnable() {
			@Override
			public void run() {
				grp.removeAllViews();
				Realm realm = Realm.getDefaultInstance();
				for (int i = 0; i < list.size(); i++) {
					ArrayList<OneHistory> l = list.get(i);
					View v = View.inflate(OneRankInfoActivity.this, R.layout.tljr_item_showtiaocangitem, null);
					if (l.size() > 0) {
						long time = l.get(0).getTime();
						((TextView) v.findViewById(R.id.showtiaocangitem_time)).setText(Util.getDateOnlyDat(time));
						((TextView) v.findViewById(R.id.tljr_zx_xiangxicangwei)).setText(Util.getDateOnlyHour(time));
					}
					LinearLayout layout = (LinearLayout) v.findViewById(R.id.tiaocangtiem);
					layout.setVisibility(View.VISIBLE);
					for (int j = 0; j < l.size(); j++) {
						OneHistory history = l.get(j);
						View view = View.inflate(OneRankInfoActivity.this, R.layout.tljr_item_tiaocangitem, null);
						OneGu gu = realm.where(OneGu.class).equalTo("market", history.getMarket())
								.equalTo("code", history.getCode()).findFirst();
						if (gu != null) {
							((TextView) view.findViewById(R.id.tljr_zx_tiaocangname)).setText(gu.getName());
						} else {
							((TextView) view.findViewById(R.id.tljr_zx_tiaocangname)).setText(history.getName());
						}
						((TextView) view.findViewById(R.id.tljr_zx_srcpercenti))
								.setText(Util.df.format(history.getSrcP() * 100) + "%");
						((TextView) view.findViewById(R.id.tljr_zx_topercent))
								.setText(Util.df.format(history.getToP() * 100) + "%");
						layout.addView(view);

					}
					grp.addView(v);
				}

				realm.close();
				// Log.e("childCount1",grp.getChildCount()+"");
				if (grp.getChildCount() == 0) {
					// Log.e("childCount",grp.getChildCount()+"");
					// grp.setVisibility(View.GONE);
					onepersoninfo_footer.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * 
	 */
	private void initOwn() {
		own.removeAllViews();
		Realm realm = Realm.getDefaultInstance();
		for (int i = 0; i < ownList.size(); i++) {
			View v = View.inflate(OneRankInfoActivity.this, R.layout.tljr_item_rank_own, null);
			OneGu gu = ownList.get(i);
			((TextView) v.findViewById(R.id.tljr_item_rank_own_tag)).setText(gu.getTag());
			((TextView) v.findViewById(R.id.tljr_item_rank_own_price)).setText(Util.df.format(gu.getFirst()));
			if (gu.getFirst() == 0) {
				((TextView) v.findViewById(R.id.tljr_item_rank_own_price)).setText("--");
			}
			((TextView) v.findViewById(R.id.tljr_item_rank_own_value)).setText(gu.getPercent() + "%");
			RoundProgressBar mRoundProgressBar = (RoundProgressBar) v.findViewById(R.id.onePerson_RoundProgressBar);
			mRoundProgressBar.setCricleProgressColor(getResources().getColor(R.color.testred));
			mRoundProgressBar.setTextColor(getResources().getColor(R.color.testred));
			mRoundProgressBar.setProgress(gu.getPercent());
			mRoundProgressBar.setDefaultText("100%");
			mRoundProgressBar.setRoundWidth(14);
			mRoundProgressBar.setTextSize(30);
			mRoundProgressBar.setMax(100);

			((TextView) v.findViewById(R.id.tljr_item_rank_own_now)).setText(Util.df.format(gu.getNow()));
			((TextView) v.findViewById(R.id.tljr_item_rank_own_now))
					.setTextColor(gu.getNow() > gu.getFirst() ? ColorUtil.red : ColorUtil.green);
			((TextView) v.findViewById(R.id.tljr_item_rank_own_change)).setText((gu.getNow() == 0 || gu.getFirst() == 0)
					? "--" : (Util.df.format((gu.getNow() / gu.getFirst() - 1) * 100) + "%"));
			((TextView) v.findViewById(R.id.tljr_item_rank_own_change))
					.setTextColor(gu.getNow() > gu.getFirst() ? ColorUtil.red : ColorUtil.green);
			OneGu gu1 = realm.where(OneGu.class).equalTo("market", gu.getMarket()).equalTo("code", gu.getCode())
					.findFirst();
			if (gu1 != null) {
				gu.setName(gu1.getName());
				((TextView) v.findViewById(R.id.tljr_item_rank_own_name)).setText(gu.getName() + "  " + gu.getCode());
			} else {
				((TextView) v.findViewById(R.id.tljr_item_rank_own_name)).setText(gu.getMarket() + "  " + gu.getCode());
			}
			final int m = i;
			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					OneGu gu = ownList.get(m);
					Intent intent = new Intent(OneRankInfoActivity.this, OneGuActivity.class);
					// 用Bundle携带数据
					Bundle bundle = new Bundle();
					// 传递name参数为tinyphp
					bundle.putString("code", gu.getCode());
					bundle.putString("market", gu.getMarket());
					bundle.putString("name", gu.getName());
					bundle.putString("key", gu.getKey());
					bundle.putSerializable("onegu", gu);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			own.addView(v);
		}
		realm.close();
		if (own.getChildCount() == 0) {
			onepersoninfo_footer_now.setVisibility(View.VISIBLE);
		}

	}

	private void showOwn(boolean isShowOwn) {
		own.setVisibility(isShowOwn ? View.VISIBLE : View.GONE);
		sv.setVisibility(isShowOwn ? View.GONE : View.VISIBLE);
		((TextView) findViewById(R.id.tljr_onepersoninfo_own))
				.setTextColor(isShowOwn ? ColorUtil.red : getResources().getColor(R.color.gray));
		((TextView) findViewById(R.id.tljr_onepersoninfo_history))
				.setTextColor(isShowOwn ? getResources().getColor(R.color.gray) : ColorUtil.red);
		LayoutParams params = (RelativeLayout.LayoutParams) check.getLayoutParams();
		Animation animation = null;
		int width = getWindowManager().getDefaultDisplay().getWidth();
		if (isShowOwn) {
			// params.removeRule(RelativeLayout.RIGHT_OF);
			// params.addRule(RelativeLayout.LEFT_OF,R.id.tljr_onepersoninfo_view);
			animation = new TranslateAnimation(width / 2, 0, 0, 0);
		} else {
			animation = new TranslateAnimation(0, width / 2, 0, 0);
			// params.removeRule(RelativeLayout.LEFT_OF);
			// params.addRule(RelativeLayout.RIGHT_OF,R.id.tljr_onepersoninfo_view);
		}
		animation.setFillAfter(true);
		animation.setDuration(300);
		check.startAnimation(animation);
		// check.setLayoutParams(params);
	}

	public void reflushDP() {
		int m = 0;
		String parm = "list=";
		for (int i = 0; i < ownList.size(); i++) {
			if (m == 0) {
				parm += (ownList.get(i).getMarket() + "|" + ownList.get(i).getCode());
			} else {
				parm += ("," + ownList.get(i).getMarket() + "|" + ownList.get(i).getCode());
			}
			m++;
		}
		Util.getRealInfo(parm, new NetResult() {
			@Override
			public void result(final String msg) {
				freshData(msg);
			}
		}, true);
	}

	private void freshData(final String msg) {
		try {
			final org.json.JSONObject object = new org.json.JSONObject(msg);
			if (object.getInt("code") != 1) {
				return;
			}
			try {
				org.json.JSONArray arr = object.getJSONArray("result");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String key = obj.getString("market") + obj.getString("code");
					for (int j = 0; j < ownList.size(); j++) {
						if (ownList.get(j).getKey().equals(key)) {
							OneGu gu = ownList.get(j);
							JSONArray array = obj.getJSONArray("data");
							gu.setNow(array.optDouble(0));
							if (gu.getNow() == 0) {
								gu.setNow(array.optDouble(1));
							}
							gu.setKaipanjia(array.optDouble(2));
							j = ownList.size();
						}
					}
				}
			} catch (org.json.JSONException e) {
				e.printStackTrace();
			}
			post(new Runnable() {
				@Override
				public void run() {
					initOwn();
				}
			});
		} catch (org.json.JSONException e) {
			e.printStackTrace();
		}
	}

	private void reflushChart(JSONArray array) throws JSONException {
		chart.getXAxis().setTextSize(8f);
		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<Entry> vals = new ArrayList<Entry>();
		boolean isHide = array.length() > 9;
		int index = array.length() / 10;
		int m = 0;
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			if (!isHide || m > index) {
				m = 0;
				xVals.add(Util.getDateOnlyDay(object.getLong("time")));
			} else {
				xVals.add("");
			}
			m++;
			vals.add(new Entry((float) object.getDouble("jingzhi"), i));
		}
		LineDataSet set = new LineDataSet(vals, "Data Set 1");
		// set.setDrawCubic(true);
		set.setCubicIntensity(0.2f * xVals.size() / vals.size());
		set.setDrawCircles(false);
		set.setLineWidth(1f);
		set.setCircleSize(5f);
		set.setHighLightColor(Color.rgb(244, 117, 117));
		set.setColor(Color.rgb(78, 128, 172));

		LineData data = new LineData(xVals, set);
		data.setValueTextSize(9f);
		data.setDrawValues(false);
		chart.setData(data);
		chart.invalidate();

	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null || listAdapter.getCount() == 0) {
			return;
		}
		// int totalHeight = 0;
		// for (int i = 0; i < listAdapter.getCount(); i++) { //
		// // listAdapter.getCount()返回数据项的数目
		// View listItem = listAdapter.getView(i, null, listView);
		// listItem.measure(0, 0); // 计算子项View 的宽高
		// totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		// }
		int totalHeight = listAdapter.getCount() * Util.HEIGHT / 12;
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tljr_img_onepersoninfo_fanhui:
			finish();
			break;
		case R.id.tljr_onepersoninfo_own:
			showOwn(true);
			break;
		case R.id.tljr_onepersoninfo_history:
			showOwn(false);
			break;
		case R.id.tljr_img_onepersoninfo_tianjia:
			new AlertDialog.Builder(this).setTitle("添加分组").setMessage("您要将此分组拷贝到我的组合吗？")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							addShareZu();
						}
					}).setNegativeButton("否", null).show();
			break;
		case R.id.onepersoninfo_show:
			if (ShowTagStatus == 0) {
				((TextView) findViewById(R.id.tljr_txt_onepersoninfo_tag)).setText(showTag);
				ShowTagStatus = 1;
				openItem((ImageView) findViewById(R.id.onepersoninfo_show));
			} else {
				((TextView) findViewById(R.id.tljr_txt_onepersoninfo_tag)).setText(showTag.substring(0, 15) + "...");
				ShowTagStatus = 0;
				closeItem((ImageView) findViewById(R.id.onepersoninfo_show));
			}

			break;
		default:
			break;
		}
	}

	public void closeItem(ImageView image) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(image, "rotation", 90f, 0f);
		animator.setDuration(300);
		animator.start();
	}

	public void openItem(ImageView image) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(image, "rotation", 0f, 90f);
		animator.setDuration(300);
		animator.start();
	}

	private void addShareZu() {
		if (ZiXuanUtil.fzMap.containsKey(zhname)) {
			LogUtil.e("addShareZu", "name1");
			// zu = ZiXuanUtil.fzMap.get(zhname);
			showTip("该组合已添加");
		} else {
			OneFenZu zu;
			ArrayList<OneGu> list;
			LogUtil.e("addShareZu", "name2");
			zu = ZiXuanUtil.addNewFenzu(zhname, false);
			list = zu.getList();
			HashMap<String, OneGu> map = new HashMap<String, OneGu>();
			for (int i = 0; i < list.size(); i++) {
				map.put(list.get(i).getMarket() + list.get(i).getCode() + list.get(i).getName(), list.get(i));
			}
			for (int j = 0; j < ownList.size(); j++) {
				OneGu gu = ownList.get(j);
				gu.setTime(System.currentTimeMillis());
				gu.setFirst(gu.getNow() == 0 ? 0 : gu.getNow());
				if (!map.containsKey(gu.getMarket() + gu.getCode() + gu.getName())) {
					ZiXuanUtil.addStock(gu, zu.getName());
				}
			}
			map = null;
			zu.setList(list);
			if (MyApplication.getInstance().getMainActivity() != null) {
				MainActivity activity = MyApplication.getInstance().getMainActivity();
				if (activity.hangQing.mTljrZuHe != null)
					activity.hangQing.mTljrZuHe.addNewFenZu(zu.getName());
				if (MyApplication.getInstance().self != null) {
					ZiXuanUtil.sendActions(MyApplication.getInstance().self, activity, new Complete() {
						@Override
						public void complete() {
							try {
								sendPost(ZiXuanUtil.fzMap.get(zhname));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
			showToast("加入到我的自选成功");
		}
	}

	// 调整仓位
	public void sendPost(final OneFenZu zu) throws JSONException {
		String jsonResult = getJsonPost(zu.getList());
		LogUtil.e("OneRankSendPost", jsonResult);
		JSONArray array = new JSONArray(jsonResult);

		if (array.length() != 0) {
			NetUtil.sendPost(UrlUtil.URL_tc, "action=tiaocang&uid=" + MyApplication.getInstance().self.getId()
					+ "&zhid=" + zu.getId() + "&tiaocang=" + jsonResult, new NetResult() {
						@SuppressLint("SimpleDateFormat")
						@Override
						public void result(String msg) {
							try {
								JSONObject object = new JSONObject(msg);
								if (object.getInt("status") == 1) {
									Intent intent = null;
									// 发送广播
									for (int i = 0; i < zu.getList().size(); i++) {
										intent = new Intent(zu.getId() + "." + zu.getList().get(i).getCode());
										intent.putExtra("percent", zu.getList().get(i).getPercent());
										sendBroadcast(intent);
									}

									Message message = new Message();
									message.what = 2;
									handler.sendMessage(message);

								} else {
									showTip(object.getString("msg"));
								}
							} catch (Exception e) {
								// Log.e("sendPostError2",e.getMessage());
								// showTip("获取调仓失败");
							}
						}
					});
		}
	}

	public void showTip(String tip) {
		Message message = new Message();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("msg", tip);
		message.setData(bundle);
		handler.sendMessage(message);
	}

	private boolean submitStatus = false;
	private Map<String, Integer> submitCode;

	@Override
	public void handleMsg(Message msg) {
		super.handleMsg(msg);
		switch (msg.what) {
		case 1:
			showToast(msg.getData().getString("msg"));
			break;
		case 2:
			submitStatus = true;
			ReturnResult();
			// 股票初始化
			showTip("调仓成功");
			finish();
			break;
		default:
			break;
		}

	};

	// 得到发送请求的json
	public String getJsonPost(ArrayList<OneGu> listGu) {
		try {
			JSONArray array = new JSONArray();
			JSONObject object;
			for (int i = 0; i < listGu.size(); i++) {
				for (int j = 0; j < ownList.size(); j++) {
					if ((ownList.get(j).getMarket() + ownList.get(j).getCode())
							.equals(listGu.get(i).getMarket() + listGu.get(i).getCode())) {
						listGu.get(i).setKaipanjia(ownList.get(j).getKaipanjia());
						listGu.get(i).setPercent(ownList.get(j).getPercent());
					}
				}
				if (listGu.get(i).getKaipanjia() == 0.0) {
					continue;
				}
				if (!(listGu.get(i).getMarket().equals("sh")) && !(listGu.get(i).getMarket().equals("sz"))) {
					continue;
				}
				object = new JSONObject();
				float src = 0.0f;
				float to = Float.valueOf(String.valueOf(listGu.get(i).getPercent() / 100f + ""));
				if (src != to) {
					object.put("id", listGu.get(i).getId());
					object.put("srcPercent", Float.valueOf(src));
					object.put("toPercent", Float.valueOf(to));
					object.put("code", listGu.get(i).getCode());
					object.put("market", listGu.get(i).getMarket());
					submitCode.put(listGu.get(i).getCode(), listGu.get(i).getPercent());
					array.put(object);
				}

			}
			return array.toString();
		} catch (Exception e) {
			showTip("获取本地数据出错");
			return null;
		}
	}

	public void ReturnResult() {
		Intent intent = new Intent();
		intent.putExtra("submit_Result", submitStatus);
		List<Map<String, Integer>> params = new ArrayList<Map<String, Integer>>();
		params.add(submitCode);
		intent.putExtra("params", (Serializable) params);
		setResult(RESULT_OK, intent);
	}

}
