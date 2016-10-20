package com.abct.tljr.shouye;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.kline.MyValueFormatter;
import com.abct.tljr.kline.OneData;
import com.abct.tljr.kline.OptionsItemSelected;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.listener.Complete;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;
import com.ryg.utils.LOG;

import android.graphics.Color;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

public class LineView{
	private Handler handler;
	private String market, code;
	private LineChart chart;
	private ArrayList<OneData> datas = new ArrayList<OneData>();
	private BarChart barChart;
	private int start, end;
	public MyValueFormatter custom;
	public boolean isred = true;
	private JSONObject object;

	/*
	 * 实时线
	 */
	
	public LineView(Handler handler, LineChart chart, String market, String code) {
		this.handler = handler;
		this.chart = chart;
		this.market = market;
		this.code = code;
		initLineChat();
		if (Constant.marketInfo == null){
			Constant.getMarketInfo(new Complete() {
				@Override
				public void complete() {
					object = Constant.marketInfo.get(LineView.this.market.toLowerCase());
				}
			});
		} else {
			object = Constant.marketInfo.get(market.toLowerCase());
		}
	}

	public ArrayList<OneData> getDatas() {
		return datas;
	}

	public LineChart getChart() {
		return chart;
	}

	public void setBarChart(BarChart barChart) {
		this.barChart = barChart;
	}

	public void setArray(JSONObject object) {
		this.object = object;
	}

	private void initLineChat() {
		custom = new MyValueFormatter(5);

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
		leftAxis.setAxisLineColor(Color.GRAY);

		YAxis rightAxis = chart.getAxisRight();
		rightAxis.setLabelCount(5);
		rightAxis.setStartAtZero(false);
		rightAxis.setTextColor(Color.rgb(139, 148, 153));
		rightAxis.setValueFormatter(custom);
		rightAxis.setAxisLineColor(Color.GRAY);
		rightAxis.setEnabled(true);

		chart.setDrawGridBackground(false);
		chart.getLegend().setEnabled(false);
	}

	public void setMKCode(String market, String code) {
		this.market = market;
		this.code = code;
	}

	public void initData() {
		String url = UrlUtil.URL_minutes + market + "/" + code;
		HttpRequest.sendPost(url, "", new HttpRevMsg() {
			@Override
			public void revMsg(String msg) {
				if (msg.equals("")) {
					handler.sendEmptyMessage(14);
				}
				try {
					JSONObject object = new JSONObject(msg);
					if (object.getInt("code") == 200) {
						JSONArray array = object.getJSONArray("result");
						ArrayList<OneData> temp = new ArrayList<OneData>();
						for (int i = 0; i < array.length(); i++) {
							JSONObject ob = array.getJSONObject(i);
							// [1429147800000,32.03,8741]
							// 时间戳，收盘价，成交量
							OneData data = new OneData();
							data.setTime(ob.getLong("time"));
							data.setDate(Util.getDateOnlyHour(data.getTime()));
							data.setEndPrice((float) ob.getDouble("last"));
							temp.add(data);
						}
						if (temp.size() == 0) {
							return;
						}
						datas.clear();
						datas.addAll(temp);
						handler.post(new Runnable() {
							@Override
							public void run() {
								reflushData1();
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private boolean linecolorisred = true;

	public void setlinecolor(boolean linecolorisred) {
		this.linecolorisred = linecolorisred;
	}

	// public boolean have
	public void reflushData1() {
		chart.getXAxis().setTextSize(8f);
		ArrayList<OneData> nowdatas = new ArrayList<OneData>();
		nowdatas.addAll(datas);

		if (nowdatas.size() == 0) {
			return;
		}
//		if (market.equals("CFFEX")) {
//			nowdatas = setnulldata();
//		}
		int prog = nowdatas.size() - 1;

		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		// xVals = getXVals(nowdatas.get(0).getTime(),
		// nowdatas.get(nowdatas.size() - 1).getTime(), true);
		xVals = Util.getXVals(Util.format3, object.optJSONArray("sections"), object.optLong("interval"));
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		int PriceSmallerOne = 1;
		double count = 0;
		double counnum = 0;
		int tempposition = 0;
		// com.tencent.mm.sdk.platformtools.Log.d("addmove",
		// "custom.getStartvalue() :"+custom.getStartvalue());
		for (int i = 0; i < prog; i++) {
			if (nowdatas.get(i) != null) {
				if (tempposition == 0 && custom.getStartvalue() == 0) {
					custom.setStartvalue(nowdatas.get(i).getEndPrice());
				}
				// LogUtil.i("lineview222", "time :"+nowdatas.get(i).getDate()+"
				// end :"+nowdatas.get(i).getEndPrice());
				tempposition = i;
				counnum++;
				count += nowdatas.get(i).getEndPrice();
				vals1.add(new Entry(nowdatas.get(i).getEndPrice(), i));
				vals2.add(new Entry(Float.parseFloat(Util.df3.format(count / counnum)), i));
				if (nowdatas.get(i).getEndPrice() < 1 && PriceSmallerOne == 1
						|| nowdatas.get(i).getEndPrice() >= 100000) {
					PriceSmallerOne = 2;
				} else if (nowdatas.get(i).getEndPrice() > 10000 && nowdatas.get(i).getEndPrice() < 100000) {
					PriceSmallerOne = 3;
				}
			} else {
				counnum++;
				count += nowdatas.get(tempposition).getEndPrice();
				vals1.add(new Entry(nowdatas.get(tempposition).getEndPrice(), i));
				vals2.add(new Entry(Float.parseFloat(Util.df.format(count / counnum)), i));
			}
		}
		ValueFormatter formatter = new MyValueFormatter(PriceSmallerOne);
		chart.getAxisLeft().setValueFormatter(formatter);

		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		if (vals1.size() > xVals.size()) {
			vals1.subList(xVals.size(), vals1.size()).clear();
		}
		LineDataSet set1 = new LineDataSet(vals1, "Data Set 1");
		set1.setDrawCubic(true);
		// set1.setCubicIntensity(0.2f * xVals.size() / vals1.size());
		set1.setCubicIntensity(0);
		set1.setDrawFilled(false);
		set1.setDrawCircles(false);
		set1.setLineWidth(1f);
		set1.setCircleSize(5f);
		set1.setHighLightColor(Color.rgb(244, 117, 117));
		set1.setColor(Color.rgb(104, 241, 175));
		set1.setColor(Util.c_red);
		set1.setFillColor(ColorTemplate.getHoloBlue());
		if (custom.getStartvalue() != 0) {
			// set1.setColor(nowdatas.get(nowdatas.size()-1).getEndPrice()>=custom.getStartvalue()
			set1.setColor(linecolorisred ? Util.c_red : Util.c_green);
		}
		dataSets.add(set1);
		if (vals2.size() > xVals.size()) {
			vals2.subList(xVals.size(), vals2.size()).clear();
		}
		LineDataSet set2 = new LineDataSet(vals2, "Data Set 2");
		set2.setDrawCubic(true);
		// set2.setCubicIntensity(0.2f * xVals.size() / vals2.size());
		set2.setCubicIntensity(0);
		set2.setDrawCircles(false);
		set2.setLineWidth(1f);
		set2.setCircleSize(5f);
		set2.setHighLightColor(Color.rgb(244, 117, 117));
		set2.setColor(Color.parseColor("#e7ba10"));
		dataSets.add(set2);
		LineData data = new LineData(xVals, dataSets);
		data.setValueTextSize(9f);
		data.setDrawValues(false);
		chart.setData(data);
		chart.invalidate();

		handler.sendEmptyMessage(12);
	}

}
