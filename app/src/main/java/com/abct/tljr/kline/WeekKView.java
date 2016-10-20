package com.abct.tljr.kline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.R;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.listener.NetResult;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

public class WeekKView implements OptionsItemSelected {
	private Handler handler;
	private String market, code;
	private CandleStickChart chart;
	private ArrayList<OneData> datas = new ArrayList<OneData>();
	private ArrayList<ArrayList<OneData>> dataList = new ArrayList<ArrayList<OneData>>();
	private BarChart barChart;
	private int start, end;
	private String TAG = "WeekKView";

	public WeekKView(Handler handler, CandleStickChart chart, String market, String code) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.chart = chart;
		this.market = market;
		this.code = code;
		initChar();
	}
	public void setMarCo(String market, String code) {
		this.market = market;
		this.code = code;
	}
	public ArrayList<OneData> getDatas() {
		return datas;
	}

	public CandleStickChart getChart() {
		return chart;
	}

	public void setBarChart(BarChart barChart) {
		this.barChart = barChart;
	}

	private void initChar() {
		// MyYAxisRender yMyRender = new
		// MyYAxisRender(chart.getViewPortHandler(),
		// chart.getAxisLeft(), chart.getTransformer(AxisDependency.LEFT));
		// chart.setRendererLeftYAxis(yMyRender);
		//
		// MyXAxisRender xMyRender = new
		// MyXAxisRender(chart.getViewPortHandler(),
		// chart.getXAxis(), chart.getTransformer(AxisDependency.LEFT));
		// chart.setXAxisRenderer(xMyRender);

		// if more than 60 entries are displayed in the chart, no values will be
		// drawn
		chart.setMaxVisibleValueCount(60);

		// scaling can now only be done on x- and y-axis separately
		chart.setPinchZoom(false);
		chart.setDrawGridBackground(false);
		chart.setDescription("");
		chart.setDuplicateParentStateEnabled(true);

		// setting data

		chart.getLegend().setEnabled(false);
		XAxis xAxis = chart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));
		xAxis.setAdjustXLabels(true);

		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.setLabelCount(5);
		leftAxis.setStartAtZero(false);
		leftAxis.setTextColor(Color.rgb(139, 148, 153));

		YAxis rightAxis = chart.getAxisRight();
		rightAxis.setLabelCount(5);
		rightAxis.setStartAtZero(false);
		rightAxis.setTextColor(Color.rgb(139, 148, 153));

	}

	public void initData() {
		if (OneGuActivity.dataweek.size() != 0) {
			datas.clear();
			datas.addAll(OneGuActivity.dataweek);
			getZhouArray();
			reflushData();
			return;
		}
		handler.sendEmptyMessage(1);
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/days", "market=" + market + "&code=" + code,
				new NetResult() {

					@Override
					public void result(String msg) {
						// TODO Auto-generated method stub
						datas.clear();
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 200) {
								JSONArray array = object.getJSONArray("result");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									OneData data = new OneData();
									data.setTime(obj.optLong("time"));
									data.setDate(Util.getDateOnlyDat(data.getTime()));
									data.setStartPrice((float) obj.optDouble("open"));
									data.setHighPrice((float) obj.optDouble("high"));
									data.setLowPrice((float) obj.optDouble("low"));
									data.setEndPrice((float) obj.optDouble("close"));
									data.setChanges(obj.optLong("volume"));
									datas.add(data);
									OneGuActivity.dataweek.add(data);
								}
								getZhouArray();
								handler.sendEmptyMessage(2);
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										reflushData();
									}
								});
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	private void getZhouArray() {
		// long data = array.getJSONArray(0).getLong(0);
		if (datas.size() == 0) {
			return;
		}

		LogUtil.e("开始计算周K", datas.size() + "");
		Date a = new Date((Long) datas.get(0).getTime());
		Date b = new Date((Long) datas.get(datas.size() - 1).getTime());
		LogUtil.e("开始计算周K",
				"从" + DateUtil.getDateNoss(a.getTime()) + "开始，从" + DateUtil.getDateNoss(b.getTime()) + "结束");
		dataList.clear();
		getDateInterval(a, b);
		LogUtil.e("计算周K完毕", datas.size() + "");
	}

	private void getDateInterval(Date begin, Date end) {
		// 开始日期不能大于结束日期
		if (!begin.before(end)) {
			return;
		}
		Calendar cal_begin = Calendar.getInstance();
		cal_begin.setTime(begin);
		Calendar cal_end = Calendar.getInstance();
		cal_end.setTime(end);
		while (true) {
			if (cal_begin.get(Calendar.YEAR) == cal_end.get(Calendar.YEAR)
					&& cal_begin.get(Calendar.MONTH) == cal_end.get(Calendar.MONTH)
					&& cal_begin.get(Calendar.WEEK_OF_YEAR) == cal_end.get(Calendar.WEEK_OF_YEAR)) {
				addOneArray(cal_begin, cal_end);
				changeArray();
				break;
			}
			Calendar weekEnd = getWeekEnd(cal_begin);
			if (weekEnd.getTime().getTime() > cal_end.getTime().getTime()) {
				addOneArray(cal_begin, cal_end);
				changeArray();
				break;
			}
			addOneArray(cal_begin, weekEnd);
			cal_begin.add(Calendar.WEEK_OF_MONTH, 1);
			cal_begin.set(Calendar.DAY_OF_WEEK, 2);
		}
	}

	private void addOneArray(Calendar begin, Calendar end) {

		LogUtil.e("开始加入时间", DateUtil.getDateNoss(begin.getTime().getTime()) + "结束:"
				+ DateUtil.getDateNoss(end.getTime().getTime()));
		ArrayList<OneData> list = new ArrayList<OneData>();
		for (int i = 0; i < 7; i++) {
			if (datas.size() > i && datas.get(i).getTime() >= begin.getTime().getTime()
					&& datas.get(i).getTime() <= end.getTime().getTime()) {
				list.add(datas.get(i));
			}
		}
		datas.removeAll(list);
		dataList.add(list);

	}

	/**
	 * 取得指定星期的最后一天date
	 * 
	 * @param strdate
	 *            String
	 * @return String
	 */
	private Calendar getWeekEnd(Calendar c) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(c.getTime());
		calendar.add(Calendar.WEEK_OF_MONTH, 1);
		calendar.set(Calendar.DAY_OF_WEEK, 1);
		return calendar;
	}

	private void changeArray() {
		datas.clear();
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(i).size() != 0) {
				OneData data = new OneData();
				for (int j = 0; j < dataList.get(i).size(); j++) {
					LogUtil.e("值", DateUtil.getDateNoss(dataList.get(i).get(j).getTime()));
					if (j == 0) {
						data.setLowPrice(dataList.get(i).get(0).getLowPrice());
					}
					data.setHighPrice(Math.max(data.getHighPrice(), dataList.get(i).get(j).getHighPrice()));
					data.setLowPrice(Math.min(data.getLowPrice(), dataList.get(i).get(j).getLowPrice()));
					data.setChanges(dataList.get(i).get(j).getChanges() + data.getChanges());
				}
				data.setTime(dataList.get(i).get(0).getTime());
				data.setDate(dataList.get(i).get(0).getDate());
				data.setStartPrice(dataList.get(i).get(0).getStartPrice());
				data.setEndPrice(dataList.get(i).get(dataList.get(i).size() - 1).getEndPrice());
				data.setChanges(data.getChanges());
				datas.add(data);
			}
		}
	}

	public void initKData() {
		handler.sendEmptyMessage(1);
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/days", "market=" + market + "&code=" + code,
				new NetResult() {

					@Override
					public void result(String msg) {
						// TODO Auto-generated method stub
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 200) {
								final JSONArray array = object.getJSONArray("result");
								// mSeekBarX.setMax(array.length() - 1);
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									OneData data = new OneData();
									data.setTime(obj.optLong("time"));
									data.setDate(Util.getDateOnlyDat(data.getTime()));
									data.setStartPrice((float) obj.optDouble("open"));
									data.setHighPrice((float) obj.optDouble("high"));
									data.setLowPrice((float) obj.optDouble("low"));
									data.setEndPrice((float) obj.optDouble("close"));
									data.setChanges(obj.optLong("volume"));
									datas.add(data);
								}
								getZhouArray();
								handler.sendEmptyMessage(2);
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										initForNum(0, datas.size());
									}
								});
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	private void reflushData() {
		if (datas.size() == 0) {
			return;
		}
		LogUtil.e("开始显示周K", datas.size() + "");
		int prog = datas.size();

		chart.resetTracking();

		ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();
		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		int PriceSmallerOne = 1;
		// String temp = datas.get(0).getDate().substring(0, 7);
		// boolean havetime = true;
		double count1 = 0;
		double count2 = 0;
		for (int i = 0; i < prog; i++) {
			OneData data = datas.get(i);
			count1 += data.getEndPrice();
			count2 += data.getEndPrice();
			if (i > 4) {
				OneData data1 = datas.get(-5 + i);
				count1 -= data1.getEndPrice();
				vals1.add(new Entry((float) (count1 / 5), i));
			} else {
				vals1.add(new Entry(data.getEndPrice(), i));
			}
			if (i > 9) {
				OneData data1 = datas.get(-10 + i);
				count2 -= data1.getEndPrice();
				vals2.add(new Entry((float) (count2 / 10), i));
			} else {
				vals2.add(new Entry(data.getEndPrice(), i));
			}
			yVals1.add(new CandleEntry(i, data.getHighPrice(), data.getLowPrice(), data.getStartPrice(),
					data.getEndPrice()));
			// if (temp.equals(data.getDate().substring(0, 7))) {
			// xVals.add("");
			// } else {
			// temp = data.getDate().substring(0, 7);
			// if (havetime) {
			// xVals.add(temp);
			// havetime = false;
			// } else {
			// xVals.add("");
			// havetime = true;
			// }
			// }
			// xVals.add(datas.get(i).getDate().substring(0, 7));
			xVals.add(datas.get(i).getDate());
			yVals2.add(new BarEntry(datas.get(i).getChanges(), i,
					datas.get(i).getEndPrice() - datas.get(i).getStartPrice() > 0 ? Color.rgb(255, 70, 41)
							: Color.rgb(30, 133, 16)));
			if (datas.get(i).getEndPrice() < 1 && PriceSmallerOne == 1) {
				PriceSmallerOne = 2;
			}
		}
		ValueFormatter formatter = new MyValueFormatter(PriceSmallerOne);
		chart.getAxisLeft().setValueFormatter(formatter);

		CandleDataSet set = new CandleDataSet(yVals1, "Data Set");
		set.setAxisDependency(AxisDependency.LEFT);
		// set1.setColor(Color.rgb(80, 80, 80));
		set.setShadowColor(Color.DKGRAY);
		set.setShadowWidth(0.7f);
		set.setDecreasingColor(Color.rgb(30, 133, 16));
		set.setDecreasingPaintStyle(Paint.Style.FILL);
		set.setIncreasingColor(Color.rgb(255, 70, 41));
		set.setIncreasingPaintStyle(Paint.Style.FILL);
		set.setDrawValues(false);

		CandleData data = new CandleData(xVals, set);

		chart.setData(data);

		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		LineDataSet set1 = new LineDataSet(vals1, "M5");
		set1.setDrawCubic(true);
		set1.setCubicIntensity(0.2f * xVals.size() / vals1.size());
		set1.setDrawCircles(false);
		set1.setLineWidth(1f);
		set1.setCircleSize(5f);
		set1.setColor(Color.parseColor("#000000"));
		dataSets.add(set1);

		LineDataSet set2 = new LineDataSet(vals2, "M10");
		set2.setDrawCubic(true);
		set2.setCubicIntensity(0.2f * xVals.size() / vals2.size());
		set2.setDrawCircles(false);
		set2.setLineWidth(1f);
		set2.setCircleSize(5f);
		set2.setColor(Color.parseColor("#e7ba10"));
		dataSets.add(set2);

		LineData linedata = new LineData(xVals, dataSets);
		data.setValueTextSize(9f);
		data.setDrawValues(false);
		chart.setLineData(linedata);
		chart.invalidate();

		BarDataSet barSet = new BarDataSet(yVals2, "DataSet");
		barSet.setBarSpacePercent(35f);
		barSet.resetColors();
		barSet.addColor(Color.GREEN);
		barSet.addColor(Color.RED);
		barSet.setDrawValues(false);

		BarData data1 = new BarData(xVals, barSet);
		// data.setValueFormatter(new MyValueFormatter());
		data1.setValueTextSize(10f);
		if (barChart != null) {
			barChart.setData(data1);
			barChart.invalidate();
		}
	}

	public void initForNum(int start, int end) {
		this.start = start;
		this.end = end;
		if (datas.size() == 0 || start == end) {
			return;
		}
		chart.resetTracking();

		ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();

		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		int PriceSmallerOne = 1;
		int m = 0;
		// String temp = datas.get(start).getDate().substring(0, 7);
		double count1 = 0;
		double count2 = 0;
		for (int i = start - 1; i >= 0; i--) {
			if (i >= start - 5) {
				count1 += datas.get(i).getEndPrice();
			}
			if (i >= start - 10) {
				count2 += datas.get(i).getEndPrice();
			}
		}
		for (int i = start; i < end; i++, m++) {
			if (i < datas.size()) {
				OneData data = datas.get(i);
				count1 += data.getEndPrice();
				count2 += data.getEndPrice();
				if (i > 4) {
					OneData data1 = datas.get(i - 5);
					count1 -= data1.getEndPrice();
					vals1.add(new Entry(Float.parseFloat(Util.df.format(count1 / 5)), m));
				} else {
					vals1.add(new Entry(data.getEndPrice(), m));
				}
				if (i > 9) {
					OneData data1 = datas.get(i - 10);
					count2 -= data1.getEndPrice();
					vals2.add(new Entry(Float.parseFloat(Util.df.format(count2 / 10)), m));
				} else {
					vals2.add(new Entry(data.getEndPrice(), m));
				}
				yVals1.add(new CandleEntry(m, data.getHighPrice(), data.getLowPrice(), data.getStartPrice(),
						data.getEndPrice()));
				// if (temp.equals(data.getDate().substring(0, 7))) {
				// xVals.add("");
				// } else {
				// temp = data.getDate().substring(0, 7);
				// xVals.add(temp);
				// }
				xVals.add(datas.get(i).getDate().substring(0, 7));
				yVals2.add(new BarEntry(data.getChanges(), m, data.getEndPrice() - data.getStartPrice() > 0
						? Color.rgb(255, 70, 41) : Color.rgb(30, 133, 16)));
				if (data.getEndPrice() < 1 && PriceSmallerOne == 1) {
					PriceSmallerOne = 2;
				}
			}
		}
		ValueFormatter formatter = new MyValueFormatter(PriceSmallerOne);
		chart.getAxisLeft().setValueFormatter(formatter);

		CandleDataSet set = new CandleDataSet(yVals1, "Data Set");
		set.setAxisDependency(AxisDependency.LEFT);
		// set1.setColor(Color.rgb(80, 80, 80));
		set.setShadowColor(Color.DKGRAY);
		set.setShadowWidth(0.5f);
		set.setDecreasingColor(Color.rgb(30, 133, 16));
		set.setDecreasingPaintStyle(Paint.Style.FILL);
		set.setIncreasingColor(Color.rgb(255, 70, 41));
		set.setIncreasingPaintStyle(Paint.Style.FILL);
		set.setValueTextColor(Color.rgb(139, 148, 153));
		set.setDrawValues(false);

		CandleData data = new CandleData(xVals, set);

		chart.setData(data);
		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		LineDataSet set1 = new LineDataSet(vals1, "M5");
		set1.setDrawCubic(true);
		set1.setCubicIntensity(0.2f * xVals.size() / vals1.size());
		set1.setDrawCircles(false);
		set1.setLineWidth(1f);
		set1.setCircleSize(5f);
		set1.setColor(Color.parseColor("#000000"));
		dataSets.add(set1);

		LineDataSet set2 = new LineDataSet(vals2, "M10");
		set2.setDrawCubic(true);
		set2.setCubicIntensity(0.2f * xVals.size() / vals2.size());
		set2.setDrawCircles(false);
		set2.setLineWidth(1f);
		set2.setCircleSize(5f);
		set2.setColor(Color.parseColor("#e7ba10"));
		dataSets.add(set2);

		LineData linedata = new LineData(xVals, dataSets);
		data.setValueTextSize(9f);
		data.setDrawValues(false);
		chart.setLineData(linedata);
		chart.invalidate();

		BarDataSet barSet = new BarDataSet(yVals2, "DataSet");
		// set2.setBarSpacePercent(35f);

		barSet.setBarSpacePercent(set.getBodySpace() * 100);
		barSet.resetColors();
		barSet.addColor(Color.GREEN);
		barSet.addColor(Color.RED);
		barSet.setValueTextColor(Color.rgb(139, 148, 153));
		barSet.setDrawValues(false);

		BarData data1 = new BarData(xVals, barSet);
		// data.setValueFormatter(new MyValueFormatter());
		data1.setValueTextSize(10f);
		if (barChart != null) {
			barChart.setData(data1);
			barChart.invalidate();
		}
	}

	@Override
	public void optionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.actionToggleHighlight: {
			if (chart.isHighlightEnabled())
				chart.setHighlightEnabled(false);
			else
				chart.setHighlightEnabled(true);
			chart.invalidate();
			break;
		}
		case R.id.actionTogglePinch: {
			if (chart.isPinchZoomEnabled())
				chart.setPinchZoom(false);
			else
				chart.setPinchZoom(true);

			chart.invalidate();
			break;
		}
		case R.id.actionToggleStartzero: {
			chart.getAxisLeft().setStartAtZero(!chart.getAxisLeft().isStartAtZeroEnabled());
			chart.getAxisRight().setStartAtZero(!chart.getAxisRight().isStartAtZeroEnabled());
			chart.invalidate();
			break;
		}
		case R.id.animateX: {
			chart.animateX(3000);
			break;
		}
		case R.id.animateY: {
			chart.animateY(3000);
			break;
		}
		case R.id.animateXY: {

			chart.animateXY(3000, 3000);
			break;
		}
		case R.id.actionSave: {
			if (chart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
				Toast.makeText(chart.getContext().getApplicationContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT)
						.show();
			} else
				Toast.makeText(chart.getContext().getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();
			break;
		}
		}
	}
}
