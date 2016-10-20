package com.abct.tljr.kline;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.graphics.Color;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

public class FiveLineView implements OptionsItemSelected {
	private Handler handler;
	private String market, code;
	private LineChart chart;
	private ArrayList<OneData> datas = new ArrayList<OneData>();
	private BarChart barChart;
	private int start, end;
	private String TAG = "FiveLineView";

	/*
	 * 五日线
	 */
	public FiveLineView(Handler handler, LineChart chart, String market, String code) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.chart = chart;
		this.market = market;
		this.code = code;
		initLineChat();
	}

	public void setMarCo(String market, String code) {
		this.market = market;
		this.code = code;
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

	private void initLineChat() {
		// MyYAxisRender yMyRender = new
		// MyYAxisRender(chart.getViewPortHandler(),
		// chart.getAxisLeft(), chart.getTransformer(AxisDependency.LEFT));
		// chart.setRendererLeftYAxis(yMyRender);
		//
		// MyXAxisRender xMyRender = new
		// MyXAxisRender(chart.getViewPortHandler(),
		// chart.getXAxis(), chart.getTransformer(AxisDependency.LEFT));
		// chart.setXAxisRenderer(xMyRender);

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
	}

	public void initData() {
		if (OneGuActivity.fiveDatas.size() != 0) {
			datas.clear();
			datas.addAll(OneGuActivity.fiveDatas);
			reflushData();
			return;
		}
		handler.sendEmptyMessage(1);
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/5minutes",
				"market=" + market + "&code=" + code, new NetResult() {
					@Override
					public void result(String msg) {
						// TODO Auto-generated method stub
						datas.clear();
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 200) {
								JSONArray array = object.getJSONArray("result");
								readArray(array);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	}

	private void readArray(JSONArray array) throws JSONException {
		// ArrayList<OneData> list = new ArrayList<OneData>();
		// long time = 0;
		datas.clear();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			OneData data = new OneData();
			data.setTime(obj.optLong("time"));
			// if (data.getTime() < time) {
			// datas.addAll(0, list);
			// list = null;
			// readArray(array, i);
			// return;
			// }
			// time = data.getTime();
			data.setDate(Util.getDateOnlyDay(data.getTime()));
			// data.setDate(Util.getDateOnlyHour(data.getTime()));
			data.setEndPrice((float) obj.optDouble("last"));
			data.setChangesnow(obj.optLong("volume"));
			// if (i != 0
			// && obj.optLong("volume") >= array.getJSONObject(i -
			// 1).optLong("volume")) {
			// data.setChanges(obj.optLong("volume")
			// - array.getJSONObject(i - 1).optLong("volume"));
			data.setChanges(obj.optLong("volume"));
			datas.add(data);
			if (i == array.length() - 1) {
				OneGuActivity.fiveDatas.clear();
				OneGuActivity.fiveDatas.addAll(datas);
				handler.sendEmptyMessage(2);
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						reflushData();
					}
				});
			}
		}
	}

	public void initKData() {
		handler.sendEmptyMessage(1);
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/5minutes",
				"market=" + market + "&code=" + code, new NetResult() {
					@Override
					public void result(String msg) {
						// TODO Auto-generated method stub
						datas.clear();
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 200) {
								JSONArray array = object.getJSONArray("result");
								readArray1(array, 0);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	private void readArray1(JSONArray array, int index) throws JSONException {
		// ArrayList<OneData> list = new ArrayList<OneData>();
		// long time = 0;
		datas.clear();
		for (int i = index; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			OneData data = new OneData();
			data.setTime(obj.optLong("time"));
			// if (data.getTime() < time) {
			// datas.addAll(0, list);
			// list = null;
			// readArray(array, i);
			// return;
			// }
			// time = data.getTime();
			data.setDate(Util.getDateOnlyDay(data.getTime()));
			// data.setDate(Util.getDateOnlyHour(data.getTime()));
			data.setEndPrice((float) obj.optDouble("last"));
			data.setChangesnow(obj.optLong("volume"));
			data.setChanges(obj.optLong("volume"));
			datas.add(data);
			if (i == array.length() - 1) {
				OneGuActivity.fiveDatas.clear();
				OneGuActivity.fiveDatas.addAll(datas);
				handler.sendEmptyMessage(2);
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						initLineForNum(0, datas.size());
					}
				});
			}
		}
	}

	private void reflushData() {
		if (datas.size() == 0) {
			return;
		}
		int prog = datas.size();

		ArrayList<String> xVals = new ArrayList<String>();
		String temp = "";
		for (int i = 0; i < prog; i++) {
			if (!temp.equals(datas.get(i).getDate())) {
				temp = datas.get(i).getDate();
				xVals.add(temp);
				LogUtil.e("addXVals", temp);
			} else {
				xVals.add("");
			}
			// xVals.add(datas.get(i).getDate());
		}
		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		int PriceSmallerOne = 1;
		double count = 0;
		for (int i = 0; i < prog; i++) {
			count += datas.get(i).getEndPrice();
			vals1.add(new Entry(datas.get(i).getEndPrice(), i));
			vals2.add(new Entry(Float.parseFloat(Util.df.format(count / (i + 1))), i));
			yVals2.add(new BarEntry(datas.get(i).getChanges(), i, Color.rgb(255, 70, 41)));
			if (datas.get(i).getEndPrice() < 1 && PriceSmallerOne == 1 || datas.get(i).getEndPrice() >= 10000) {
				PriceSmallerOne = 2;
			}
		}
		ValueFormatter formatter = new MyValueFormatter(PriceSmallerOne);
		chart.getAxisLeft().setValueFormatter(formatter);

		LineDataSet set1 = new LineDataSet(vals1, "Data Set 1");
		set1.setDrawCubic(true);
		set1.setCubicIntensity(0.2f);
		set1.setDrawFilled(true);
		set1.setDrawCircles(false);
		set1.setLineWidth(1f);
		set1.setCircleSize(5f);
		set1.setHighLightColor(Color.rgb(244, 117, 117));
		// set1.setColor(Color.rgb(104, 241, 175));
		set1.setColor(Color.rgb(78, 128, 172));
		set1.setFillColor(ColorTemplate.getHoloBlue());
		dataSets.add(set1);

		LineDataSet set2 = new LineDataSet(vals2, "Data Set 2");
		set2.setDrawCubic(true);
		set2.setCubicIntensity(0.2f * xVals.size() / vals2.size());
		set2.setDrawCircles(false);
		set2.setLineWidth(1f);
		set2.setCircleSize(5f);
		set2.setColor(Color.parseColor("#e7ba10"));
		dataSets.add(set2);

		LineData data = new LineData(xVals, dataSets);
		data.setValueTextSize(9f);
		data.setDrawValues(false);
		chart.setData(data);
		chart.invalidate();

		BarDataSet barSet = new BarDataSet(yVals2, "DataSet");
		barSet.setBarSpacePercent(35f);
		barSet.setDrawValues(false);
		barSet.resetColors();
		barSet.addColor(Color.GREEN);
		barSet.addColor(Color.RED);

		BarData data1 = new BarData(xVals, barSet);
		// data.setValueFormatter(new MyValueFormatter());
		data1.setValueTextSize(10f);
		if (barChart != null) {
			barChart.setData(data1);
			barChart.invalidate();
		}
	}

	public void initLineForNum(int start, int end) {
		this.start = start;
		this.end = end;
		if (datas.size() == 0 || start == end) {
			return;
		}
		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		int m = 0;

		String temp = "";
		int PriceSmallerOne = 1;
		double count = 0;
		for (int i = start; i < end; i++, m++) {
			if (!temp.equals(datas.get(i).getDate())) {
				temp = datas.get(i).getDate();
				xVals.add(temp);
			} else {
				xVals.add("");
			}
			// xVals.add(datas.get(i).getDate());
			count += datas.get(i).getEndPrice();
			vals1.add(new Entry(datas.get(i).getEndPrice(), m));
			vals2.add(new Entry(Float.parseFloat(Util.df.format(count / (m + 1))), m));
			yVals2.add(new BarEntry(datas.get(i).getChanges(), m, Color.rgb(255, 70, 41)));
			if (datas.get(i).getEndPrice() < 1 && PriceSmallerOne == 1 || datas.get(i).getEndPrice() >= 10000) {
				PriceSmallerOne = 2;
			}
		}
		ValueFormatter formatter = new MyValueFormatter(PriceSmallerOne);
		chart.getAxisLeft().setValueFormatter(formatter);

		LineDataSet set1 = new LineDataSet(vals1, "Data Set 1");
		set1.setDrawCubic(true);
		set1.setCubicIntensity(0.2f);
		set1.setDrawFilled(true);
		set1.setDrawCircles(false);
		set1.setLineWidth(1f);
		set1.setCircleSize(5f);
		set1.setHighLightColor(Color.rgb(244, 117, 117));
		set1.setColor(Color.rgb(78, 128, 172));
		set1.setFillColor(ColorTemplate.getHoloBlue());
		dataSets.add(set1);
		LineDataSet set2 = new LineDataSet(vals2, "Data Set 2");
		set2.setDrawCubic(true);
		set2.setCubicIntensity(0.2f * xVals.size() / vals2.size());
		set2.setDrawCircles(false);
		set2.setLineWidth(1f);
		set2.setCircleSize(5f);
		set2.setColor(Color.parseColor("#e7ba10"));
		dataSets.add(set2);
		LineData data = new LineData(xVals, dataSets);
		data.setValueTextSize(9f);
		data.setDrawValues(false);
		chart.setData(data);
		chart.invalidate();
		BarDataSet barSet = new BarDataSet(yVals2, "DataSet");
		barSet.setBarSpacePercent(35f);
		barSet.setDrawValues(false);
		barSet.resetColors();
		barSet.addColor(Color.GREEN);
		barSet.addColor(Color.RED);

		BarData data1 = new BarData(xVals, barSet);
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
		case R.id.actionToggleValues: {
			for (DataSet<?> set : chart.getData().getDataSets())
				set.setDrawValues(!set.isDrawValuesEnabled());

			chart.invalidate();
			break;
		}
		case R.id.actionToggleHighlight: {
			if (chart.isHighlightEnabled())
				chart.setHighlightEnabled(false);
			else
				chart.setHighlightEnabled(true);
			chart.invalidate();
			break;
		}
		case R.id.actionToggleFilled: {

			ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) chart.getData().getDataSets();

			for (LineDataSet set : sets) {
				if (set.isDrawFilledEnabled())
					set.setDrawFilled(false);
				else
					set.setDrawFilled(true);
			}
			chart.invalidate();
			break;
		}
		case R.id.actionToggleCircles: {
			ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) chart.getData().getDataSets();

			for (LineDataSet set : sets) {
				if (set.isDrawCirclesEnabled())
					set.setDrawCircles(false);
				else
					set.setDrawCircles(true);
			}
			chart.invalidate();
			break;
		}
		case R.id.actionToggleCubic: {
			ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) chart.getData().getDataSets();

			for (LineDataSet set : sets) {
				if (set.isDrawCubicEnabled())
					set.setDrawCubic(false);
				else
					set.setDrawCubic(true);
			}
			chart.invalidate();
			break;
		}
		case R.id.actionToggleStartzero: {
			chart.getAxisLeft().setStartAtZero(!chart.getAxisLeft().isStartAtZeroEnabled());
			chart.getAxisRight().setStartAtZero(!chart.getAxisRight().isStartAtZeroEnabled());
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
		case R.id.actionToggleFilter: {

			// the angle of filtering is 35°
			Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 35);

			if (!chart.isFilteringEnabled()) {
				chart.enableFiltering(a);
			} else {
				chart.disableFiltering();
			}
			chart.invalidate();
			break;
		}
		case R.id.actionSave: {
			if (chart.saveToPath("title" + System.currentTimeMillis(), "")) {
				Toast.makeText(chart.getContext().getApplicationContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT)
						.show();
			} else
				Toast.makeText(chart.getContext().getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show();

			// chart.saveToGallery("title"+System.currentTimeMillis())
			break;
		}
		}
	}
}
