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
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

public class YueKView implements OptionsItemSelected {
	private Handler handler;
	private String market, code;
	private CandleStickChart chart;
	private ArrayList<OneData> datas = new ArrayList<OneData>();
	private ArrayList<ArrayList<OneData>> dataList = new ArrayList<ArrayList<OneData>>();
	private BarChart barChart;
	private int start, end;

	public YueKView(Handler handler, CandleStickChart chart, String market,
			String code) {
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
		leftAxis.setTextColor(Color.rgb(139, 148, 153));

		YAxis rightAxis = chart.getAxisRight();
		rightAxis.setLabelCount(5);
		rightAxis.setStartAtZero(false);
		rightAxis.setTextColor(Color.rgb(139, 148, 153));

	}

	public void initData() {
		if (OneGuActivity.datayue.size() != 0) {
			datas.clear();
			datas.addAll(OneGuActivity.datayue);
			getZhouArray();
			reflushData();
			return;
		}
		handler.sendEmptyMessage(1);
		NetUtil.sendPost(UrlUtil.Url_apicavacn
				+ "tools/stock/quotation/0.2/days", "market=" + market + "&code="
				+ code, new NetResult() {

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
							data.setDate(Util.getDateOnlyMonth(data.getTime()));
							data.setStartPrice((float) obj.optDouble("open"));
							data.setHighPrice((float) obj.optDouble("high"));
							data.setLowPrice((float) obj.optDouble("low"));
							data.setEndPrice((float) obj.optDouble("close"));
							data.setChanges(obj.optLong("volume"));
							datas.add(data);
							OneGuActivity.datayue.add(data);
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
		Date a = new Date((Long) datas.get(0).getTime());
		Date b = new Date((Long) datas.get(datas.size() - 1).getTime());
		dataList.clear();
		getDateInterval(a, b);
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
					&& cal_begin.get(Calendar.MONTH) == cal_end
							.get(Calendar.MONTH)) {
				addOneArray(cal_begin, cal_end);
				changeArray();
				break;
			}
			addOneArray(cal_begin, getMonthEnd(cal_begin));
			cal_begin.add(Calendar.MONTH, 1);
			cal_begin.set(Calendar.DAY_OF_MONTH, 1);
		}
	}

	private void addOneArray(Calendar begin, Calendar end) {
		ArrayList<OneData> list = new ArrayList<OneData>();
		for (int i = 0; i < datas.size(); i++) {
			if (datas.get(i).getTime() >= begin.getTime().getTime()
					&& datas.get(i).getTime() <= end.getTime().getTime()) {
				list.add(datas.get(i));
			}
		}
		datas.removeAll(list);
		dataList.add(list);
	}

	/**
	 * 取得指定月份的最后一天date
	 * 
	 * @param strdate
	 *            String
	 * @return String
	 */
	private Calendar getMonthEnd(Calendar c) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(c.getTime());
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return calendar;
	}

	private void changeArray() {
		datas.clear();
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(i).size() != 0) {
				OneData data = new OneData();
				for (int j = 0; j < dataList.get(i).size(); j++) {
					if (j == 0) {
						data.setLowPrice(dataList.get(i).get(0).getLowPrice());
					}
					data.setHighPrice(Math.max(data.getHighPrice(), dataList
							.get(i).get(j).getHighPrice()));
					data.setLowPrice(Math.min(data.getLowPrice(),
							dataList.get(i).get(j).getLowPrice()));
					data.setChanges(dataList.get(i).get(j).getChanges()
							+ data.getChanges());
				}
				data.setTime(dataList.get(i).get(0).getTime());
				data.setDate(dataList.get(i).get(0).getDate());
				data.setStartPrice(dataList.get(i).get(0).getStartPrice());
				data.setEndPrice(dataList.get(i)
						.get(dataList.get(i).size() - 1).getEndPrice());
				data.setChanges(data.getChanges());
				datas.add(data);
			}
		}
	}

	public void initKData() {
		handler.sendEmptyMessage(1);
		NetUtil.sendPost(UrlUtil.Url_apicavacn
				+ "tools/stock/quotation/0.2/days", "market=" + market + "&code="
				+ code, new NetResult() {

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
							data.setDate(Util.getDateOnlyMonth(data.getTime()));
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

	@SuppressWarnings("unchecked")
	private void reflushData() {
		if (datas.size() == 0) {
			return;
		}
		int prog = datas.size();

		chart.resetTracking();

		ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();

		for (int i = 0; i < prog; i++) {
			yVals1.add(new CandleEntry(i, datas.get(i).getHighPrice(), datas
					.get(i).getLowPrice(), datas.get(i).getStartPrice(), datas
					.get(i).getEndPrice()));
		}

		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		int PriceSmallerOne = 1;
		boolean havetime = true;
		double count1 = 0;
		for (int i = 0; i < prog; i++) {
			OneData data = datas.get(i);
			count1 += data.getEndPrice();
			if (i > 4) {
				OneData data1 = datas.get(-5 + i);
				count1 -= data1.getEndPrice();
				vals1.add(new Entry((float) (count1 / 5), i));
			} else {
				vals1.add(new Entry(data.getEndPrice(), i));
			}
			if (havetime) {
				xVals.add(data.getDate());
				havetime = false;
			} else {
				xVals.add("");
				havetime = true;
			}
			yVals2.add(new BarEntry(data.getChanges(), i, data.getEndPrice()
					- data.getStartPrice() > 0 ? Color.rgb(255, 70, 41) : Color
					.rgb(30, 133, 16)));
			if (data.getEndPrice() < 1 && PriceSmallerOne == 1) {
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
		LineData linedata = new LineData(xVals, dataSets);
		data.setValueTextSize(9f);
		data.setDrawValues(false);
		chart.setLineData(linedata);
		chart.invalidate();

		BarDataSet set2 = new BarDataSet(yVals2, "DataSet");
		set2.setBarSpacePercent(35f);
		set2.resetColors();
		set2.addColor(Color.GREEN);
		set2.addColor(Color.RED);
		set2.setDrawValues(false);

		BarData data1 = new BarData(xVals, set2);
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
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		int m = 0;
		int PriceSmallerOne = 1;
		double count1 = 0;
		for (int i = start - 1; i >= 0; i--) {
			if (i >= start - 5) {
				count1 += datas.get(i).getEndPrice();
			}
		}
		for (int i = start; i < end; i++, m++) {
			if (i < datas.size()) {
				OneData data = datas.get(i);
				count1 += data.getEndPrice();
				if (i > 4) {
					OneData data1 = datas.get(i - 5);
					count1 -= data1.getEndPrice();
					vals1.add(new Entry(Float.parseFloat(Util.df
							.format(count1 / 5)), m));
				} else {
					vals1.add(new Entry(data.getEndPrice(), m));
				}
				yVals1.add(new CandleEntry(m, data.getHighPrice(), data
						.getLowPrice(), data.getStartPrice(), data
						.getEndPrice()));
				xVals.add(data.getDate());
				yVals2.add(new BarEntry(data.getChanges(), m, data
						.getEndPrice() - data.getStartPrice() > 0 ? Color.rgb(
						255, 70, 41) : Color.rgb(30, 133, 16)));
				if (data.getEndPrice() < 1 && PriceSmallerOne == 1) {
					PriceSmallerOne = 2;
				}
			}
		}
		ValueFormatter formatter = new MyValueFormatter(PriceSmallerOne);
		chart.getAxisLeft().setValueFormatter(formatter);

		CandleDataSet set = new CandleDataSet(yVals1, "Data Set");
		set.setAxisDependency(AxisDependency.LEFT);
		// set.setColor(Color.rgb(80, 80, 80));
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

		LineData linedata = new LineData(xVals, dataSets);
		data.setValueTextSize(9f);
		data.setDrawValues(false);
		chart.setLineData(linedata);
		chart.invalidate();

		BarDataSet set2 = new BarDataSet(yVals2, "DataSet");
		// set2.setBarSpacePercent(35f);

		set2.setBarSpacePercent(set.getBodySpace() * 100);
		set2.resetColors();
		set2.addColor(Color.GREEN);
		set2.addColor(Color.RED);
		set2.setValueTextColor(Color.rgb(139, 148, 153));
		set2.setDrawValues(false);
		BarData data1 = new BarData(xVals, set2);
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
			chart.getAxisLeft().setStartAtZero(
					!chart.getAxisLeft().isStartAtZeroEnabled());
			chart.getAxisRight().setStartAtZero(
					!chart.getAxisRight().isStartAtZeroEnabled());
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
				Toast.makeText(chart.getContext().getApplicationContext(),
						"Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(chart.getContext().getApplicationContext(),
						"Saving FAILED!", Toast.LENGTH_SHORT).show();
			break;
		}
		}
	}
}
