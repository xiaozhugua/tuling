package com.abct.tljr.kline;

import java.text.ParseException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
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
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.graphics.Color;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

public class LineView implements OptionsItemSelected {
	private Handler handler;
	private String market, code;
	private LineChart chart;
	private ArrayList<OneData> datas = new ArrayList<OneData>();
	private BarChart barChart;
	private int start, end;
	private JSONObject object;
	public MyValueFormatter custom;

	/*
	 * 实时线
	 */
	public LineView(Handler handler, LineChart chart, String market, String code) {
		this.handler = handler;
		this.chart = chart;
		this.market = market;
		this.code = code;
		initLineChat();
		if (object == null){
			if (Constant.marketInfo == null) {
				Constant.getMarketInfo(new Complete() {
					@Override
					public void complete() {
						object = Constant.marketInfo.get(LineView.this.market.toLowerCase());
					}
				});
				return;
			} else {
				object = Constant.marketInfo.get(market.toLowerCase());
			}
		}
	}

	public void setMarCo(final String market, String code) {
		this.market = market;
//		object = Constant.marketInfo.get(market.toLowerCase());
		if (Constant.marketInfo == null) {
			Constant.getMarketInfo(new Complete() {
				@Override
				public void complete() {
					object = Constant.marketInfo.get(market.toLowerCase());
				}
			});
		} else {
			object = Constant.marketInfo.get(market.toLowerCase());
		}
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

	public void setArray(JSONObject object) {
		this.object = object;
	}

	private int linecolorisred = 0;

	public void setlinecolor(int linecolorisred) {
		this.linecolorisred = linecolorisred;
	}

	public void initLineChat() {
		// MyYAxisRender yMyRender = new
		// MyYAxisRender(chart.getViewPortHandler(),
		// chart.getAxisLeft(), chart.getTransformer(AxisDependency.LEFT));
		// chart.setRendererLeftYAxis(yMyRender);
		//
		// MyXAxisRender xMyRender = new
		// MyXAxisRender(chart.getViewPortHandler(),
		// chart.getXAxis(), chart.getTransformer(AxisDependency.LEFT));
		// chart.setXAxisRenderer(xMyRender);

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
		chart.setDrawGridBackground(false);
		chart.getLegend().setEnabled(false);

		YAxis rightAxis = chart.getAxisRight();
		rightAxis.setLabelCount(5);
		rightAxis.setStartAtZero(false);
		rightAxis.setTextColor(Color.rgb(139, 148, 153));
		rightAxis.setValueFormatter(custom);
		rightAxis.setAxisLineColor(Color.GRAY);
		rightAxis.setEnabled(true);
	}

	public void initData() {
		if (datas == null || datas.size() == 0)
			ProgressDlgUtil.showProgressDlg("", chart.getContext());
		handler.sendEmptyMessage(1);
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/minutes",
				"market=" + market + "&code=" + code, new NetResult() {
					@Override
					public void result(String msg) {
						// TODO Auto-generated method stub
						datas.clear();
						ProgressDlgUtil.stopProgressDlg();
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 200) {
								JSONArray array = object.getJSONArray("result");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									OneData data = new OneData();
									data.setTime(obj.optLong("time"));
									data.setDate(Util.getDateOnlyHour(data.getTime()));
									data.setEndPrice((float) obj.optDouble("last"));
									data.setChangesnow(obj.optLong("volume"));
									data.setChanges(obj.optLong("volume"));
									datas.add(data);
								}
								if (datas.size() > 0) {
									endtime = datas.get(datas.size() - 1).getTime();
									handler.sendEmptyMessage(2);
									handler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											reflushData1();
										}
									});
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	}

	public void initKData() {
		handler.sendEmptyMessage(1);
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/minutes",
				"market=" + market + "&code=" + code, new NetResult() {
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
									data.setDate(Util.getDateOnlyHour(data.getTime()));
									data.setEndPrice((float) obj.optDouble("last"));
									data.setChangesnow(obj.optLong("volume"));
									data.setChanges(obj.optLong("volume"));
									datas.add(data);
								}
								if (datas.size() == 0) {
									return;
								}
								endtime = datas.get(datas.size() - 1).getTime();
								handler.sendEmptyMessage(2);
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										initLineForNum(0, datas.size());
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

	// 加入11:30到13:00之间的数据
	// private void addDatas() {
	// for (int i = 0; i < datas.size(); i++) {
	// if (Util.getDateOnlyHour(datas.get(i).getTime()).equals("11:30")
	// && i < datas.size() - 2
	// && Util.getDateOnlyHour(datas.get(i + 1).getTime()).equals(
	// "13:00")) {
	// addNoDataDatas(i);
	// break;
	// }
	// }
	// }
	//
	// private void addNoDataDatas(int index) {
	// OneData da = datas.get(index);
	// ArrayList<OneData> list = new ArrayList<OneData>();
	// for (long i = 1; i <= 120; i++) {
	// OneData data = new OneData();
	// data.setTime(da.getTime() + i*60000);
	// data.setDate(Util.getDateOnlyHour(data.getTime()));
	// data.setEndPrice(da.getEndPrice());
	// data.setChangesnow(0);
	// data.setChanges(0);
	// list.add(data);
	// }
	// datas.addAll(index, list);
	// }

	// private ArrayList<String> getXVals(long time) {
	// String day = Util.getDateOnlyDat(time);
	// try {
	// ArrayList<String> xVals = new ArrayList<String>();
	// long dayFirst = Util.format.parse(day + " 09:30:00").getTime();
	// long dayEnd = Util.format.parse(day + " 15:00:00").getTime();
	// long a = Util.format.parse(day + " 11:30:00").getTime();
	// long b = Util.format.parse(day + " 13:00:00").getTime();
	// // long dayEnd = Util.format.parse(day + " 18:30:00").getTime();
	// for (long i = dayFirst; i <= dayEnd; i += 60000) {
	// if (i <= a || i >= b)
	// xVals.add(Util.getDateOnlyHour(i));
	// }
	// return xVals;
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }

	/**
	 * 分隔时间，形成以半小时为分隔的ArrayList
	 * <String> 使用此方法分隔必须XAxis.setAdjustXLabels(false)；自适应开启将影响Label的显示。
	 * 
	 * @param start
	 * @param end
	 * @param isNoMove
	 * @return
	 */
	private ArrayList<String> getXVals(long start, long end, boolean isNoMove) {
		String day = Util.getDateOnlyDat(start);
		try {
			ArrayList<String> xVals = new ArrayList<String>();
			// long dayFirst = Util.format.parse(day + " 09:30:00").getTime();
			// long dayEnd = Util.format.parse(day + " 15:00:00").getTime();
			long a = Util.format.parse(day + " 11:30:00").getTime();
			long b = Util.format.parse(day + " 13:00:00").getTime();
			// long dayEnd = Util.format.parse(day + " 18:30:00").getTime();
			if (market.equals("hk") || market.equals("hkfe")) {
				a = Util.format.parse(day + " 12:00:00").getTime();
				b = Util.format.parse(day + " 13:00:00").getTime();
				if (isNoMove)
					end = Util.format.parse(day + " 16:00:00").getTime();
			} else if (market.equals("us")) {
				b = a = (start - 1);
				if (isNoMove)
					end = Util.format.parse(day + " 16:00:00").getTime();
			} else if (market.equals("cboe")) {
				b = a = (start - 1);
				if (isNoMove)
					end = Util.format.parse(day + " 15:00:00").getTime();
			} else if (market.equals("nasdaq")) {
				b = a = (start - 1);
				if (isNoMove)
					end = Util.format.parse(day + " 17:40:00").getTime();
			} else if (market.equals("idealpro")) {
				b = a = (start - 1);
				if (isNoMove)
					end = Util.format.parse(day + " 23:59:59").getTime();
			} else if (market.equals("sgx")) {
				a = Util.format.parse(day + " 16:00:00").getTime();
				b = Util.format.parse(day + " 16:40:00").getTime();
				if (isNoMove)
					end = Util.format.parse(day + " 23:59:59").getTime();
			} else {
				if (isNoMove)
					end = Util.format.parse(day + " 15:00:00").getTime();
			}
			for (long i = start; i <= end; i += 60000) {
				if (i <= a || i >= b) {
					int sub = 30;
					if (market.equals("us") || market.equals("hk") || market.equals("ecbot")
							|| market.equals("nasdaq")) {
						sub = 60;
					} else if (market.equals("idealpro") || market.equals("sgx")) {
						sub = 120;
					}
					if (i / 60000 % sub == 0) {
						if (i == a) {
							if (market.equals("hkfe")) {
								xVals.add(Util.getDateOnlyHour(i).substring(0, 2) + "/" + Util.getDateOnlyHour(b));
							} else {
								xVals.add(Util.getDateOnlyHour(i) + "/" + Util.getDateOnlyHour(b));
							}
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

	public void reflushData1() {
		chart.getXAxis().setTextSize(8f);

		if (datas.size() == 0) {
			return;
		}
		int prog = datas.size() - 1;

		ArrayList<String> xVals = new ArrayList<String>();
		try{
		  if(object!=null)
			xVals = Util.getXVals(Util.format3, object.optJSONArray("sections"), object.optLong("interval"));
		}catch(Exception e){
			
		}
		// xVals = getXVals(datas.get(0).getTime(), datas.get(datas.size() - 1).getTime(), true);
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		int PriceSmallerOne = 1;
		double count = 0;
		int tempposition = 0;
		for (int i = 0; i < prog; i++) {
			if (custom!=null&&tempposition == 0&&custom.getStartvalue() == 0) {
				custom.setStartvalue(datas.get(i).getEndPrice());
			}
			tempposition = i;
			count += datas.get(i).getEndPrice();
			vals1.add(new Entry(datas.get(i).getEndPrice(), i));
			// vals2.add(new Entry(Float.parseFloat(Util.df.format(count/(i+1))), i));
			vals2.add(new Entry((float) (count / (i + 1)), i));
			if (datas.get(i).getEndPrice() < 1 && PriceSmallerOne == 1 || datas.get(i).getEndPrice() >= 100000) {
				PriceSmallerOne = 2;
			} else if (datas.get(i).getEndPrice() > 10000 && datas.get(i).getEndPrice() < 100000) {
				PriceSmallerOne = 3;
			}
		}
		ValueFormatter formatter = new MyValueFormatter(PriceSmallerOne);
		chart.getAxisLeft().setValueFormatter(formatter);

		if (vals1.size() > xVals.size()) {
			vals1.subList(xVals.size(), vals1.size()).clear();
		}
		if (vals2.size() > xVals.size()) {
			vals2.subList(xVals.size(), vals2.size()).clear();
		}
		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		LineDataSet set1 = new LineDataSet(vals1, "Data Set 1");
		set1.setDrawCubic(true);
		// set1.setCubicIntensity(0.2f * xVals.size() / vals1.size());
		set1.setDrawFilled(true);
		set1.setDrawCircles(false);
		set1.setLineWidth(1f);
		set1.setCircleSize(5f);
		set1.setHighLightColor(Color.rgb(244, 117, 117));
		// set1.setColor(Color.rgb(104, 241, 175));
		set1.setColor(Color.rgb(78, 128, 172));
		set1.setFillColor(ColorTemplate.getHoloBlue());
		if (custom.getStartvalue() != 0) {
			// set1.setColor(nowdatas.get(nowdatas.size()-1).getEndPrice()>=custom.getStartvalue()
			if (linecolorisred > 0) {
				set1.setColor(Util.c_red);
				set1.setDrawFilled(false);
			} else if (linecolorisred < 0) {
				set1.setColor(Util.c_green);
				set1.setDrawFilled(false);
			}
		}
		dataSets.add(set1);
		LineDataSet set2 = new LineDataSet(vals2, "Data Set 2");
		set2.setDrawCubic(true);
		// set2.setCubicIntensity(0.2f * xVals.size() / vals2.size());
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

		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

		for (int i = 0; i < prog; i++) {
			yVals2.add(new BarEntry(datas.get(i).getChanges(), i, Color.rgb(255, 70, 41)));
		}
		if (yVals2.size() > xVals.size()) {
			yVals2.subList(xVals.size(), yVals2.size()).clear();
		}
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
		end = (end > datas.size() ? datas.size() : end);
		chart.getXAxis().setTextSize(10f);
		this.start = start;
		this.end = end;
		if (datas.size() == 0 || start == end) {
			return;
		}
		ArrayList<String> xVals = new ArrayList<String>();
		// xVals = getXVals(datas.get(start).getTime(), datas.get(end - 1)
		// .getTime(), false);
		// for (int i = 0; i < prog; i++) {
		// xVals.add(datas.get(i).getDate());
		// }
		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
		int m = 0;
		int PriceSmallerOne = 1;
		double count = 0;
		for (int i = start; i < end; i++, m++) {
			xVals.add(datas.get(i).getDate());
			count += datas.get(i).getEndPrice();
			vals1.add(new Entry(datas.get(i).getEndPrice(), m));
			// vals2.add(new Entry(Float.parseFloat(Util.df.format(count / (m +
			// 1))), m));
			vals2.add(new Entry((float) (count / (m + 1)), m));
			yVals2.add(new BarEntry(datas.get(i).getChanges(), m, Color.rgb(255, 70, 41)));
			if (datas.get(i).getEndPrice() < 1 && PriceSmallerOne == 1 || datas.get(i).getEndPrice() >= 100000) {
				PriceSmallerOne = 2;
			} else if (datas.get(i).getEndPrice() > 10000 && datas.get(i).getEndPrice() < 100000) {
				PriceSmallerOne = 3;
			}
		}
		ValueFormatter formatter = new MyValueFormatter(PriceSmallerOne);
		chart.getAxisLeft().setValueFormatter(formatter);

		LineDataSet set1 = new LineDataSet(vals1, "Data Set 1");
		set1.setDrawCubic(true);
		// set1.setCubicIntensity(0.2f);
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
		// set2.setCubicIntensity(0.2f * xVals.size() / vals2.size());
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

	private long endtime = 0;

	public void ShiShi() {
		if (endtime == 0) {
			return;
		}
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/minutes",
				"market=" + market + "&code=" + code + "&begin=" + endtime, new NetResult() {
					@Override
					public void result(final String msg) {
						// TODO Auto-generated method stub
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									JSONObject object = new JSONObject(msg);
									if (object.getInt("code") == 200) {
										JSONArray array = object.getJSONArray("result");
										for (int i = 0; i < array.length(); i++) {
											JSONObject obj = array.getJSONObject(i);
											OneData data = new OneData();
											data.setTime(obj.optLong("time"));
											data.setDate(Util.getDateOnlyHour(data.getTime()));
											data.setEndPrice((float) obj.optDouble("last"));
											data.setChangesnow(obj.optLong("volume"));
											data.setChanges(obj.optLong("volume"));
											if (data.getTime() > datas.get(datas.size() - 1).getTime())
												datas.add(data);
										}
										endtime = datas.get(datas.size() - 1).getTime();
										reflushData1();
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

	public void ShiShi1() {
		if (endtime == 0) {
			return;
		}
		NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/minutes",
				"market=" + market + "&code=" + code + "&begin=" + endtime, new NetResult() {
					@Override
					public void result(final String msg) {
						// TODO Auto-generated method stub
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									JSONObject object = new JSONObject(msg);
									if (object.getInt("code") == 200 && object.getJSONArray("result").length() > 0) {
										JSONArray array = object.getJSONArray("result");
										for (int i = 0; i < array.length(); i++) {
											JSONObject obj = array.getJSONObject(i);
											OneData data = new OneData();
											data.setTime(obj.optLong("time"));
											data.setDate(Util.getDateOnlyHour(data.getTime()));
											data.setEndPrice((float) obj.optDouble("last"));
											data.setChangesnow(obj.optLong("volume"));
											data.setChanges(obj.optLong("volume"));
											if (data.getTime() > datas.get(datas.size() - 1).getTime())
												datas.add(data);
										}
										endtime = datas.get(datas.size() - 1).getTime();
										handler.sendEmptyMessage(2);
										initLineForNum(start, end + 1);
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