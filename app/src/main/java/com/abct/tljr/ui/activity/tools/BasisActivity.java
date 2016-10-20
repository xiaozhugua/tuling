package com.abct.tljr.ui.activity.tools;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.kline.MyValueFormatter;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.listener.NetResult;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年11月11日 下午4:01:27
 * 基差的页面
 */
public class BasisActivity extends BaseActivity {
	private static final String url = "http://tt.tuling.me:8888";
	private TextView now, change, changep;
	private CheckBox cb;
	private JSONObject[] types;
	private ArrayList<HashMap<String, ArrayList<OneBasis>>> list;
	private JSONObject[] titles;
	private LinearLayout tljr_titleGroup;
	private ViewPager vpContent;
	private ArrayList<View> list1 = new ArrayList<View>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_basis);
		findViewById(R.id.tljr_activity_basis_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		tljr_titleGroup = (LinearLayout) findViewById(R.id.tljr_titleGroup);
		vpContent = (ViewPager) findViewById(R.id.tljr_basis_vp);
		now = (TextView) findViewById(R.id.tljr_activity_basis_now);
		change = (TextView) findViewById(R.id.tljr_activity_basis_change);
		changep = (TextView) findViewById(R.id.tljr_activity_basis_changep);
		cb = (CheckBox) findViewById(R.id.tljr_activity_basis_cb);
		getList();
	}

	@SuppressWarnings("deprecation")
	private void initTitle() {
		for (int i = 0; i < tljr_titleGroup.getChildCount(); i++) {
			if (i != 0) {
				tljr_titleGroup.removeViewAt(i);
			}
		}
		list = new ArrayList<HashMap<String, ArrayList<OneBasis>>>();
		titles = new JSONObject[types.length];
		barCount = types.length;
		for (int i = 0; i < types.length; i++) {
			HashMap<String, ArrayList<OneBasis>> map = new HashMap<String, ArrayList<OneBasis>>();
			list.add(map);
			ScrollView view = new ScrollView(this);
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			view.addView(layout);
			list1.add(view);
			TextView t = new TextView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			params.gravity = Gravity.CENTER;
			params.weight = 1;
			t.setGravity(Gravity.CENTER);
			t.setTextColor(getResources().getColor(R.color.tljr_white));
			t.setTextSize(14);
			t.setText(types[i].optString("name"));
			t.setLayoutParams(params);
			final int m = i;
			t.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					vpContent.setCurrentItem(m);
				}
			});
			tljr_titleGroup.addView(t);
		}
		final View v = findViewById(R.id.tljr_activity_basis_back);
		v.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						allW = Util.WIDTH - v.getMeasuredWidth();
						initImage();
						v.getViewTreeObserver().removeGlobalOnLayoutListener(
								this);
					}
				});
		vpContent.setAdapter(new PagerAdapter() {

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(list1.get(position));
				return list1.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(list1.get(position));
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return list1.size();
			}
		});
		vpContent.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int page, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int page) {
				Constant.addClickCount();
				onPageSelect(page);
				getData(page);
			}
		});
	}

	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private int offsetX = 0;

	ImageView cursor;
	int barCount = 3;
	int allW;

	private void initImage() {
		int w = allW / barCount / 2;
		cursor = (ImageView) findViewById(R.id.tljr_check);
		offset = allW / barCount;
		bmpW = offset;
		cursor.getLayoutParams().width = offset - w;
		offsetX = (allW / barCount - bmpW) / 2 + w / 2 + (Util.WIDTH - allW);
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, offsetX);
		cursor.setImageMatrix(matrix);
		onPageSelect(0);
	}

	public void onPageSelect(int selectInd) {
		if (cursor != null) {
			Animation animation = null;
			animation = new TranslateAnimation(currIndex * offset + offsetX,
					selectInd * offset + offsetX, 0, 0);
			currIndex = selectInd;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}
	}

	// private void showPopupView() {
	// if (types == null || types.length == 0) {
	// return;
	// }
	// if (popupWindow == null) {
	// typeSerch = new LinearLayout(this);
	// typeSerch.setOrientation(LinearLayout.VERTICAL);
	// typeSerch.setBackgroundColor(getResources().getColor(
	// R.color.tljr_white));
	// typeSerch.setLayoutParams(new LinearLayout.LayoutParams(
	// LayoutParams.FILL_PARENT, types.length
	// * (Util.HEIGHT / 10 + Util.dp2px(this, 1))));
	// for (int i = 0; i < types.length; i++) {
	// TextView t = new TextView(this);
	// t.setLayoutParams(new LinearLayout.LayoutParams(Util.WIDTH,
	// Util.HEIGHT / 10));
	// t.setText(types[i].optString("name"));
	// t.setGravity(Gravity.CENTER);
	// t.setTextSize(18);
	// typeSerch.addView(t);
	// View v = new View(this);
	// v.setLayoutParams(new LinearLayout.LayoutParams(Util.WIDTH,
	// Util.dp2px(this, 1)));
	// v.setBackground(getResources().getDrawable(R.drawable.img_shape_space));
	// typeSerch.addView(v);
	// }
	// for (int i = 0; i < typeSerch.getChildCount(); i++) {
	// if (typeSerch.getChildAt(i) instanceof TextView) {
	// typeSerch.getChildAt(i).setOnClickListener(
	// new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// String s = ((TextView) v).getText()
	// .toString();
	// if (!s.equals(title.getText().toString())) {
	// for (int j = 0; j < types.length; j++) {
	// if ((s.equals(types[j]
	// .optString("name")))) {
	// getData(types[j]
	// .optString("type"));
	// title.setText(types[j]
	// .optString("name"));
	// popupWindow.dismiss();
	// }
	// }
	// }
	//
	// }
	// });
	// }
	// }
	// // popupWindow = new PopupWindow(contentView, Util.WIDTH / 2,
	// // Util.HEIGHT / 2, true);
	// popupWindow = new PopupWindow(typeSerch, LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT, true);
	// popupWindow.setBackgroundDrawable(new BitmapDrawable());
	// popupWindow.setOutsideTouchable(true);
	// popupWindow.setOnDismissListener(new OnDismissListener() {
	//
	// @Override
	// public void onDismiss() {
	// // TODO Auto-generated method stub
	// findViewById(R.id.im_right).setRotation(0);
	// setAlpha(1f);
	// }
	// });
	// }
	// // popupWindow.showAsDropDown(findViewById(R.id.tljr_grp_rank_type), 0,
	// // 0,
	// // Gravity.CENTER_HORIZONTAL);
	// findViewById(R.id.im_right).setRotation(180);
	// popupWindow
	// .showAsDropDown(findViewById(R.id.tljr_activity_basis_grp_title));
	// setAlpha(0.8f);
	// }
	//
	// private void setAlpha(float f) {
	// WindowManager.LayoutParams lp = getWindow().getAttributes();
	// lp.alpha = f;
	// lp.dimAmount = f;
	// getWindow().setAttributes(lp);
	// getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	//
	// }

	private void getList() {
		ProgressDlgUtil.showProgressDlg("", this);
		HttpRequest.sendPost(UrlUtil.Url_apicavacn + "tools/index/0.2/basis/list", "", new HttpRevMsg() {

			@Override
			public void revMsg(String msg) {
				LogUtil.e("getList", msg);
				try {
					JSONArray array = new org.json.JSONObject(msg).getJSONArray("result");
					types = new JSONObject[array.length()];
					for (int i = 0; i < array.length(); i++) {
						types[i] = array.getJSONObject(i);
					}
					if (types.length > 0) {
						post(new Runnable() {

							@Override
							public void run() {
								initTitle();
								getData(0);
							}
						});
					} else {
						ProgressDlgUtil.stopProgressDlg();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ProgressDlgUtil.stopProgressDlg();
				}
			}
		});
	}

	private void getData(final int index) {
		final String type = types[index].optString("type");
		if (list.get(index).size() > 0) {
			showUi(index);
			return;
		}
		ProgressDlgUtil.showProgressDlg("", this);
		HttpRequest.sendGet(UrlUtil.Url_apicavacn + "tools/index/0.2/" + type, new HttpRevMsg() {

			@Override
			public void revMsg(String msg) {
				LogUtil.e("getData", msg);
				ProgressDlgUtil.stopProgressDlg();
				if (msg.equals("")) {
					postDelayed(new Runnable() {

						@Override
						public void run() {
							getData(index);
						}
					}, 2000);
					return;
				}
				try {
					//这里注意要判断list
					if (list==null ||  list.size()==0 || list.get(index)==null) {
						return;
					}
					HashMap<String, ArrayList<OneBasis>> map = list.get(index);
					JSONObject jsonObject = new JSONObject(msg).getJSONObject("result");
					getOneNowData(jsonObject.optString("market"),
							jsonObject.optString("code"), now, change, changep,
							cb, index);
					JSONArray array = jsonObject.getJSONArray("result");
					map.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.optJSONObject(i);
						if (object == null) {
							continue;
						}
						JSONArray arr = object.getJSONArray("result");
						ArrayList<OneBasis>	list = new ArrayList<OneBasis>();
						map.put(object.optString("name"), list);
						for (int j = 0; j < arr.length(); j++) {
							JSONObject obj = arr.optJSONObject(j);
							if (obj == null) {
								continue;
							}
							OneBasis data = new OneBasis();
							data.setBasis((float) obj.optDouble("last"));
							data.setTime(obj.optLong("time"));
							data.setVolume(obj.optLong("volume"));
							list.add(data);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showUi(index);
			}
		});
	}

	private void sort(String[] score) {
		for (int i = 0; i < score.length - 1; i++) { // 最多做n-1趟排序
			for (int j = 0; j < score.length - i - 1; j++) { // 对当前无序区间score[0......length-i-1]进行排序(j的范围很关键，这个范围是在逐步缩小的)
				if (Integer.parseInt(score[j].substring(2, score[j].length())) > Integer
						.parseInt(score[j + 1].substring(2, score[j].length()))) { // 把小的值交换到后面
					String temp = score[j];
					score[j] = score[j + 1];
					score[j + 1] = temp;
				}
			}
		}
	}

	private void showUi(final int index) {
		post(new Runnable() {

			@Override
			public void run() {
				HashMap<String, ArrayList<OneBasis>> map = list.get(index);
				LinearLayout layout = (LinearLayout) ((ScrollView) list1
						.get(index)).getChildAt(0);
				if (layout.getChildCount() > 0) {
					try {
						showTitle(titles[index], now, change, changep, cb);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
				String[] a = new String[map.keySet().size()];
				Iterator<String> it = map.keySet().iterator();
				int count = 0;
				while (it.hasNext()) {
					a[count] = it.next();
					count++;
				}
				sort(a);
				layout.removeAllViews();
				for (int i = 0; i < a.length; i++) {
					String key = a[i];
					ArrayList<OneBasis> datas = map.get(key);
					if (datas.size() > 0) {
						View v = View.inflate(BasisActivity.this,
								R.layout.tljr_item_basis, null);
						ViewHolder holder = new ViewHolder();
						holder.dif = (TextView) v
								.findViewById(R.id.tljr_item_basis_dif);
						holder.now = (TextView) v
								.findViewById(R.id.tljr_item_basis_now);
						holder.change = (TextView) v
								.findViewById(R.id.tljr_item_basis_change);
						holder.changep = (TextView) v
								.findViewById(R.id.tljr_item_basis_changep);
						holder.name = (TextView) v
								.findViewById(R.id.tljr_item_basis_name);
						holder.lineChart = (LineChart) v
								.findViewById(R.id.tljr_item_basisc_chart1);
						initLineChart(holder.lineChart);
						holder.fiveLineChart = (LineChart) v
								.findViewById(R.id.tljr_item_basisc_chart2);
						initLineChart(holder.fiveLineChart);
						holder.stickChart = (CandleStickChart) v
								.findViewById(R.id.tljr_item_basisc_chart3);
						holder.name.setText(key);
						OneBasis basis = datas.get(datas.size() - 1);
						holder.dif.setText(basis.getBasis() * -1 + "");
						// holder.now.setText(basis.getClose() + "");
						getOneNowData(key.substring(0, 2).toLowerCase(),
								key.substring(2, key.length()), holder.now,
								holder.change, holder.changep);
						initItem(v, holder);
						v.setTag(holder);
						showLineChart(holder.lineChart, datas);
						layout.addView(v);
					}
				}
			}
		});
	}

	private void showLineChart(final LineChart chart,
			final ArrayList<OneBasis> datas) {
		post(new Runnable() {

			@Override
			public void run() {
				chart.getXAxis().setTextSize(8f);
				if (datas.size() == 0) {
					return;
				}
				int prog = datas.size() - 1;

				ArrayList<String> xVals = new ArrayList<String>();
				xVals = getXVals(datas.get(0).getTime(),
						datas.get(datas.size() - 1).getTime(), false);
				// for (int i = 0; i < prog; i++) {
				// xVals.add(datas.get(i).getDate());
				// }
				ArrayList<Entry> vals1 = new ArrayList<Entry>();
				ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
				for (int i = 0; i < prog; i++) {
					vals1.add(new Entry(datas.get(i).getBasis() * -1, i));
					yVals2.add(new BarEntry(datas.get(i).getVolume(), i, Color
							.rgb(255, 70, 41)));
				}
				ValueFormatter formatter = new MyValueFormatter(4);
				chart.getAxisLeft().setValueFormatter(formatter);
				LineDataSet set1 = new LineDataSet(vals1, "Data Set 1");
				set1.setDrawCubic(true);
				// set1.setCubicIntensity(0.2f * xVals.size() / vals1.size());
				set1.setDrawFilled(true);
				set1.setDrawCircles(false);
				set1.setLineWidth(1f);
				// set1.setCircleSize(5f);
				set1.setHighLightColor(Color.rgb(244, 117, 117));
				set1.setColor(Color.rgb(78, 128, 172));
				set1.setFillColor(ColorTemplate.getHoloBlue());

				LineData data = new LineData(xVals, set1);
				data.setValueTextSize(9f);
				data.setDrawValues(false);
				chart.setData(data);
				chart.invalidate();

			}
		});
	}

	private int showNum = 0;

	private void initItem(View v, final ViewHolder holder) {
		RadioGroup rg = (RadioGroup) v.findViewById(R.id.tljr_item_basis_rg);
		for (int i = 0; i < rg.getChildCount(); i++) {
			if (rg.getChildAt(i).getVisibility() == View.VISIBLE)
				showNum++;
		}
		final View check = v.findViewById(R.id.tljr_item_basis_arrow);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tljr_item_basis_rb1:
					holder.lineChart.setVisibility(View.VISIBLE);
					holder.fiveLineChart.setVisibility(View.GONE);
					holder.stickChart.setVisibility(View.GONE);
					tabChangedArrow(check, 0);
					break;
				case R.id.tljr_item_basis_rb2:
					holder.lineChart.setVisibility(View.GONE);
					holder.fiveLineChart.setVisibility(View.VISIBLE);
					holder.stickChart.setVisibility(View.GONE);
					tabChangedArrow(check, 1);
					break;
				case R.id.tljr_item_basis_rb3:
					holder.lineChart.setVisibility(View.GONE);
					holder.fiveLineChart.setVisibility(View.GONE);
					holder.stickChart.setVisibility(View.VISIBLE);
					tabChangedArrow(check, 2);
					break;
				default:
					break;
				}
			}
		});
		tabChangedArrow(check, 0);
	}

	private void tabChangedArrow(View v, int to) {
		if (v.getTag() == null)
			initImageView(v);
		int from = (Integer) v.getTag();
		int offset = (Util.WIDTH - Util.dp2px(this, 20)) / showNum;
		int offsetX = Util.dp2px(this, 10) + offset / 2 - Util.dp2px(this, 10)
				/ 2;
		Animation animation = null;
		animation = new TranslateAnimation(from * offset + offsetX, to * offset
				+ offsetX, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(200);
		v.startAnimation(animation);
		v.setTag(to);
	}

	private void initImageView(View cursor) {
		cursor.getLayoutParams().width = Util.dp2px(this, 10);
		cursor.setTag(1);
	}

	private void initLineChart(LineChart chart) {
		chart.setDescription("");
		chart.setMaxVisibleValueCount(60);
		chart.setPinchZoom(false);

		XAxis xAxis = chart.getXAxis();
		xAxis.setAdjustXLabels(false);
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));

		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.setLabelCount(5);
		leftAxis.setStartAtZero(false);
		leftAxis.setTextColor(Color.rgb(139, 148, 153));
		chart.setDrawGridBackground(false);
		chart.getLegend().setEnabled(false);
		chart.getAxisRight().setEnabled(false);
	}

	/**
	 * 分隔时间，形成以半小时为分隔的ArrayList<String>
	 * 使用此方法分隔必须XAxis.setAdjustXLabels(false)；自自适应开启将影响Label的显示。
	 * 
	 * @param start
	 * @param end
	 * @param isNoMove
	 * @return
	 */
	public static ArrayList<String> getXVals(long start, long end,
			boolean isNoMove) {
		String day = Util.getDateOnlyDat(start);
		try {
			ArrayList<String> xVals = new ArrayList<String>();
			// long dayFirst = Util.format.parse(day + " 09:30:00").getTime();
			// long dayEnd = Util.format.parse(day + " 15:00:00").getTime();
			long a = Util.format.parse(day + " 11:30:00").getTime();
			long b = Util.format.parse(day + " 13:00:00").getTime();
			// long dayEnd = Util.format.parse(day + " 18:30:00").getTime();
			if (isNoMove)
				end = Util.format.parse(day + " 15:00:00").getTime();
			for (long i = start; i <= end; i += 60000) {
				if (i <= a || i >= b) {
					int sub = 30;
					if (i / 60000 % sub == 0) {
						if (i == a) {
							xVals.add(Util.getDateOnlyHour(i) + "/"
									+ Util.getDateOnlyHour(b));
						} else if (i == b) {
							xVals.add("");
						} else {
							xVals.add(Util.getDateOnlyHour(i));
						}
					} else {
						xVals.add("");
					}
				}
				// if (i <= a || i >= b)
				// xVals.add(Util.getDateOnlyHour(i));
			}
			return xVals;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void getOneNowData(String market, String code, final TextView now,
			final TextView change, final TextView changep) {
		Util.getRealInfo("market=" + market + "&code="
				+ code + "", new NetResult() {

			@Override
			public void result(final String msg) {
				// TODO Auto-generated method stub
				post(new Runnable() {

					@Override
					public void run() {
						try {
							showTitle(new JSONObject(msg), now, change,
									changep, cb);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

			}
		});
	}

	private void getOneNowData(String market, String code, final TextView now,
			final TextView change, final TextView changep, final CheckBox cb,
			final int index) {
		Util.getRealInfo("market=" + market + "&code="
				+ code + "", new NetResult() {

			@Override
			public void result(final String msg) {
				// TODO Auto-generated method stub
				post(new Runnable() {

					@Override
					public void run() {
						try {
							titles[index] = new JSONObject(msg);
							showTitle(titles[index], now, change, changep, cb);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

			}
		});
	}

	private void showTitle(JSONObject object, TextView now,
			TextView change, TextView changep, CheckBox... cb)
			throws JSONException {
		if (object != null && object.getInt("code") == 1) {
			final JSONArray array = object.getJSONArray("result");
			now.setText(array.optString(0).replace("null", "--"));
			change.setText((array.optDouble(9) > 0 ? "+" : "")
					+ array.optString(8).replace("null", "0.0"));// 涨跌
			changep.setText((array.optDouble(9) > 0 ? "+" : "")
					+ array.optString(9).replace("null", "0.0") + "%");// 涨跌幅
			if (array.optDouble(9) > 0) {
				int red = getResources().getColor(R.color.tljr_hq_zx_up);
				now.setTextColor(red);
				change.setTextColor(red);
				changep.setTextColor(red);
				for (CheckBox cheb : cb) {
					cheb.setChecked(true);
				}
			} else {
				int green = getResources().getColor(R.color.tljr_hq_zx_down);
				now.setTextColor(green);
				change.setTextColor(green);
				changep.setTextColor(green);
				for (CheckBox cheb : cb) {
					cheb.setChecked(false);
				}
			}
		}

	}

	static class ViewHolder {
		TextView dif, now, change, changep, name;
		LineChart lineChart, fiveLineChart;
		CandleStickChart stickChart;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		types = null;
		list = null;
		titles = null;
		list1 = null;
	}
}
