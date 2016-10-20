package com.abct.tljr.kline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

public class KView implements OptionsItemSelected {
	private Handler handler;
	private String market, code;
	private CandleStickChart chart;
	private ArrayList<OneData> datas = new ArrayList<OneData>();
	private BarChart barChart;
	private int start, end;
	private String TAG = "KView";

	public KView(Handler handler, CandleStickChart chart, String market, String code) {
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
		chart.setMaxVisibleValueCount(60);

		// scaling can now only be done on x- and y-axis separately
		chart.setPinchZoom(false);
		chart.setDrawGridBackground(false);
		chart.setDescription("");
		chart.setDuplicateParentStateEnabled(true);
		// setting data

		chart.getLegend().setEnabled(false);
		XAxis xAxis = chart.getXAxis();
		xAxis.setAdjustXLabels(true);
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));
		xAxis.setTextSize(9f);

		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.setLabelCount(5);
		leftAxis.setStartAtZero(false);
		// leftAxis.setDrawGridLines(false);
		leftAxis.setTextColor(Color.rgb(139, 148, 153));

		YAxis rightAxis = chart.getAxisRight();
		rightAxis.setLabelCount(5);
		rightAxis.setStartAtZero(false);
		rightAxis.setTextColor(Color.rgb(139, 148, 153));

	}

	public void initData() {
		if (OneGuActivity.dataday.size() != 0) {
			datas.clear();
			datas.addAll(OneGuActivity.dataday);
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
								String mouth = "";
								int space = 0;
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									OneData data = new OneData();
									data.setTime(obj.optLong("time"));
									data.setDate(getDateOnlyMonth(data.getTime()));
									data.setStartPrice((float) obj.optDouble("open"));
									data.setHighPrice((float) obj.optDouble("high"));
									data.setLowPrice((float) obj.optDouble("low"));
									data.setEndPrice((float) obj.optDouble("close"));
									data.setChanges(obj.optLong("volume"));
									datas.add(data);
									OneGuActivity.dataday.add(data);
								}
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

	private SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

	public String getDateOnlyMonth(long time) {
		Date date = new Date(time);
		return format.format(date);
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
								String mouth = "";
								int space = 0;
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									// [1416553200000,21.3,21.98,21.25,21.8,159636]
									// 时间戳，开盘价，最高价，最低价，收盘价，成交量
									OneData data = new OneData();
									data.setTime(obj.optLong("time"));
									data.setDate(getDateOnlyMonth(data.getTime()));
									data.setStartPrice((float) obj.optDouble("open"));
									data.setHighPrice((float) obj.optDouble("high"));
									data.setLowPrice((float) obj.optDouble("low"));
									data.setEndPrice((float) obj.optDouble("close"));
									data.setChanges(obj.optLong("volume"));
									datas.add(data);
								}
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
		chart.getXAxis().setDrawGridLines(true);
		int prog = datas.size() > 59 ? 59 : datas.size();
		chart.resetTracking();

		ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();
		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		ArrayList<Entry> vals3 = new ArrayList<Entry>();
		ArrayList<Entry> vals4 = new ArrayList<Entry>();
		int PriceSmallerOne = 1;
		double count1 = 0;
		double count2 = 0;
		double count3 = 0;
		double count4 = 0;
		int a = datas.size() - prog;
		// float b = 0;
		for (int i = a - 1; i >= 0; i--) {
			// b += datas.get(i).getEndPrice();
			// if (i == a - 5) {
			// count1 += b;
			// }
			// if (i == a - 9) {
			// count2 += b;
			// }
			// if (i == a - 19) {
			// count3 += b;
			// }
			// if (i == a - 29) {
			// count4 += b;
			// }
			if (i >= a - 5) {
				count1 += datas.get(i).getEndPrice();
			}
			if (i >= a - 10) {
				count2 += datas.get(i).getEndPrice();
			}
			if (i >= a - 20) {
				count3 += datas.get(i).getEndPrice();
			}
			if (i >= a - 30) {
				count4 += datas.get(i).getEndPrice();
			}
		}
		LogUtil.e("a=-----", a + "");
		for (int i = 0; i < prog; i++) {
			OneData data = datas.get(a + i);
			count1 += data.getEndPrice();
			count2 += data.getEndPrice();
			count3 += data.getEndPrice();
			count4 += data.getEndPrice();
			LogUtil.e("a + i=-----", a + i + "");
			if (a + i > 4) {
				OneData data1 = datas.get(a - 5 + i);
				count1 -= data1.getEndPrice();
				LogUtil.e("count1=-----", count1 + "");
				vals1.add(new Entry(Float.parseFloat(Util.df.format(count1 / 5)), i));
			} else {
				vals1.add(new Entry(data.getEndPrice(), i));
			}
			if (a + i > 9) {
				OneData data1 = datas.get(a - 10 + i);
				count2 -= data1.getEndPrice();
				LogUtil.e("count2=-----", count2 + "");
				vals2.add(new Entry(Float.parseFloat(Util.df.format(count2 / 10)), i));
			} else {
				vals2.add(new Entry(data.getEndPrice(), i));
			}
			if (a + i > 19) {
				OneData data1 = datas.get(a - 20 + i);
				count3 -= data1.getEndPrice();
				LogUtil.e("count3=-----", count3 + "");
				vals3.add(new Entry(Float.parseFloat(Util.df.format(count3 / 20)), i));
			} else {
				vals3.add(new Entry(data.getEndPrice(), i));
			}
			if (a + i > 29) {
				OneData data1 = datas.get(a - 30 + i);
				count4 -= data1.getEndPrice();
				LogUtil.e("count4=-----", count4 + "");
				vals4.add(new Entry(Float.parseFloat(Util.df.format(count4 / 30)), i));
			} else {
				vals4.add(new Entry(data.getEndPrice(), i));
			}
			yVals1.add(new CandleEntry(i, data.getHighPrice(), data.getLowPrice(), data.getStartPrice(),
					data.getEndPrice()));
			xVals.add(data.getDate());
			yVals2.add(new BarEntry(data.getChanges(), i,
					data.getEndPrice() - data.getStartPrice() > 0 ? Color.rgb(255, 70, 41) : Color.rgb(30, 133, 16)));
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

		LineDataSet set3 = new LineDataSet(vals3, "M20");
		set3.setDrawCubic(true);
		set3.setCubicIntensity(0.2f * xVals.size() / vals3.size());
		set3.setDrawCircles(false);
		set3.setLineWidth(1f);
		set3.setCircleSize(5f);
		set3.setColor(Color.parseColor("#e0422f"));
		dataSets.add(set3);

		LineDataSet set4 = new LineDataSet(vals4, "M30");
		set4.setDrawCubic(true);
		set4.setCubicIntensity(0.2f * xVals.size() / vals4.size());
		set4.setDrawCircles(false);
		set4.setLineWidth(1f);
		set4.setCircleSize(5f);
		set4.setColor(Color.parseColor("#46943a"));
		dataSets.add(set4);

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
		ArrayList<Entry> vals3 = new ArrayList<Entry>();
		ArrayList<Entry> vals4 = new ArrayList<Entry>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		int m = 0;
		int PriceSmallerOne = 1;
		double count1 = 0;
		double count2 = 0;
		double count3 = 0;
		double count4 = 0;
		for (int i = start - 1; i >= 0; i--) {
			if (i >= start - 5) {
				count1 += datas.get(i).getEndPrice();
			}
			if (i >= start - 10) {
				count2 += datas.get(i).getEndPrice();
			}
			if (i >= start - 20) {
				count3 += datas.get(i).getEndPrice();
			}
			if (i >= start - 30) {
				count4 += datas.get(i).getEndPrice();
			}
		}
		for (int i = start; i < end; i++, m++) {
			if (i < datas.size()) {
				OneData data = datas.get(i);
				count1 += data.getEndPrice();
				count2 += data.getEndPrice();
				count3 += data.getEndPrice();
				count4 += data.getEndPrice();
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
				if (i > 19) {
					OneData data1 = datas.get(i - 20);
					count3 -= data1.getEndPrice();
					vals3.add(new Entry(Float.parseFloat(Util.df.format(count3 / 20)), m));
				} else {
					vals3.add(new Entry(data.getEndPrice(), m));
				}
				if (i > 29) {
					OneData data1 = datas.get(i - 30);
					count4 -= data1.getEndPrice();
					vals4.add(new Entry(Float.parseFloat(Util.df.format(count4 / 30)), m));
				} else {
					vals4.add(new Entry(data.getEndPrice(), m));
				}
				yVals1.add(new CandleEntry(m, data.getHighPrice(), data.getLowPrice(), data.getStartPrice(),
						data.getEndPrice()));
				xVals.add(data.getDate());
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

		LineDataSet set3 = new LineDataSet(vals3, "M20");
		set3.setDrawCubic(true);
		set3.setCubicIntensity(0.2f * xVals.size() / vals3.size());
		set3.setDrawCircles(false);
		set3.setLineWidth(1f);
		set3.setCircleSize(5f);
		set3.setColor(Color.parseColor("#e0422f"));
		dataSets.add(set3);

		LineDataSet set4 = new LineDataSet(vals4, "M30");
		set4.setDrawCubic(true);
		set4.setCubicIntensity(0.2f * xVals.size() / vals4.size());
		set4.setDrawCircles(false);
		set4.setLineWidth(1f);
		set4.setCircleSize(5f);
		set4.setColor(Color.parseColor("#46943a"));
		dataSets.add(set4);
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
