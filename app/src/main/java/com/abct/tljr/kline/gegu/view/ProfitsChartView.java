package com.abct.tljr.kline.gegu.view;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/4/22.
 */
public class ProfitsChartView {
	private CombinedChart combinedChart;
	private BarChart barChart;
	private CombinedData combinedData;
	private BarData barData;
	private BarData lrBarData;
	private BarChart lrBarChart;
	private int textColor = Color.rgb(0x9a, 0x9a, 0x9a);

	public ProfitsChartView(CombinedChart combinedChart, BarChart barChart, BarChart lrBarChart,
			CombinedData combinedData, BarData barData, BarData lrBarData) {
		this.combinedChart = combinedChart;
		this.barChart = barChart;
		this.combinedData = combinedData;
		this.barData = barData;
		this.lrBarData = lrBarData;
		this.lrBarChart = lrBarChart;

	}

	public void initChart() {
		initlrChart();//利润
		initCombinedChart();//收益比
		initBarChart();//总收益
	}

	/**
	 * 加载利润图表数据
	 */
	private void initlrChart() {
		// TODO Auto-generated method stub
		lrBarChart.setDescription("");
		lrBarChart.setScaleEnabled(false);
		lrBarChart.setTouchEnabled(false);
		XAxis xAxis = lrBarChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setTextColor(textColor);
		xAxis.setAxisLineColor(textColor);
		YAxis leftAxis = lrBarChart.getAxisLeft();
		YAxis rightAxis = lrBarChart.getAxisRight();

		rightAxis.setEnabled(false);
		leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		leftAxis.setDrawGridLines(true);
		leftAxis.setDrawLabels(true);
		leftAxis.setValueFormatter(new LrYAxisValueFormatter("百万", false));
		leftAxis.setSpaceTop(5f);
		leftAxis.setTextColor(textColor);
		leftAxis.setStartAtZero(false);

		/*
		 * rightAxis.setDrawGridLines(false); rightAxis.setDrawLabels(true);
		 * rightAxis.setAxisMaxValue(combinedData.getYMax());
		 * rightAxis.setAxisMinValue(combinedData.getYMin());
		 * rightAxis.setSpaceTop(5f); rightAxis.setTextColor(Color.TRANSPARENT);
		 */
		// rightAxis.setZeroLineColor(Color.TRANSPARENT);
		Legend l = lrBarChart.getLegend();
		l.setEnabled(false);

		/*
		 * l.setXEntrySpace(50); l.setForm(Legend.LegendForm.SQUARE);
		 * l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
		 */

		lrBarChart.setData(lrBarData);
		lrBarChart.animateX(1000);

	}

	private void initBarChart() {
		barChart.setDescription("");
		barChart.setScaleEnabled(false);
		barChart.setTouchEnabled(false);
		XAxis xAxis = barChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setTextColor(textColor);
		xAxis.setAxisLineColor(textColor);
		YAxis leftAxis = barChart.getAxisLeft();
		YAxis rightAxis = barChart.getAxisRight();

		rightAxis.setEnabled(false);
		leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		leftAxis.setDrawGridLines(true);
		leftAxis.setDrawLabels(true);

		// leftAxis.setAxisMaxValue(barData.getYMax());
		// leftAxis.setAxisMinValue(barData.getYMin());
		leftAxis.setValueFormatter(new MyYAxisValueFormatter("亿", true));
		leftAxis.setSpaceTop(5f);
		leftAxis.setTextColor(textColor);

		// rightAxis.setEnabled(false);
		// leftAxis.setZeroLineColor(textColor);
		// rightAxis.setValueFormatter(new MyYAxisValueFormatter("%", false));
		// rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		leftAxis.setStartAtZero(false);

		/*
		 * rightAxis.setDrawGridLines(false); rightAxis.setDrawLabels(true);
		 * rightAxis.setAxisMaxValue(combinedData.getYMax());
		 * rightAxis.setAxisMinValue(combinedData.getYMin());
		 * rightAxis.setSpaceTop(5f); rightAxis.setTextColor(Color.TRANSPARENT);
		 */
		// rightAxis.setZeroLineColor(Color.TRANSPARENT);
		Legend l = barChart.getLegend();
		l.setEnabled(false);

		/*
		 * l.setXEntrySpace(50); l.setForm(Legend.LegendForm.SQUARE);
		 * l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
		 */

		barChart.animateX(1000);
		barChart.setData(barData);
	}

	private void initCombinedChart() {
		combinedChart.setDrawGridBackground(false);
		combinedChart.setDescription("");
		combinedChart.setDrawBorders(false);
		combinedChart.setScaleEnabled(false);
		combinedChart.setTouchEnabled(false);
		combinedChart.setDrawOrder(
				new CombinedChart.DrawOrder[] { CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR });
		XAxis xAxis = combinedChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setTextColor(textColor);
		xAxis.setAxisLineColor(textColor);

		YAxis leftAxis = combinedChart.getAxisLeft();
		YAxis rightAxis = combinedChart.getAxisRight();
		rightAxis.setEnabled(false);
		/*
		 * rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		 * rightAxis.setDrawGridLines(false); rightAxis.setDrawLabels(true);
		 * rightAxis.setSpaceTop(5f);
		 * rightAxis.setAxisMinValue(combinedData.getYMin() -
		 * combinedData.getYMin() * 0.1f);
		 * rightAxis.setAxisMaxValue(combinedData.getYMax() +
		 * combinedData.getYMax() * 0.1f); rightAxis.setTextColor(textColor);
		 */
		// rightAxis.setZeroLineColor(textColor);
		// rightAxis.set
		// rightAxis.setValueFormatter(new MyYAxisValueFormatter("%", false));

		// leftAxis.setEnabled(false);
		leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		leftAxis.setDrawGridLines(false);
		leftAxis.setDrawLabels(true);
		leftAxis.setSpaceTop(5f);
		leftAxis.setTextColor(textColor);
		// leftAxis.setAxisMinValue(barData.getYMin());
		// leftAxis.setAxisMaxValue(barData.getYMax());
		// leftAxis.setTextColor(Color.TRANSPARENT);
		leftAxis.setStartAtZero(false);
		// leftAxis.setZeroLineColor(Color.TRANSPARENT);
		// leftAxis.setValueFormatter(new MyYAxisValueFormatter("百万", true));
		leftAxis.setValueFormatter(new MyYAxisValueFormatter("%", false));

		Legend l = combinedChart.getLegend();
		l.setXEntrySpace(50);
		l.setForm(Legend.LegendForm.CIRCLE);
		l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
		combinedChart.animateXY(1500, 1500);
		combinedChart.setData(combinedData);

	}

	/**
	 * 总收益的Y轴单位规范
	 */
	private class MyYAxisValueFormatter implements ValueFormatter {
		private DecimalFormat mFormat;
		private String unit;
		private boolean isInt;

		public MyYAxisValueFormatter(String unit, boolean isInt) {
			//mFormat = new DecimalFormat(isInt ? "###,###,###,##0" : "###,###,###,##0.0");
			mFormat = new DecimalFormat("######0.000");
			
			this.unit = unit;
		}

		/*
		 * @Override public String getFormattedValue(float value, YAxis yAxis) {
		 * return mFormat.format(value) + unit; }
		 */
		@Override
		public String getFormattedValue(float value) {
			// TODO Auto-generated method stub
			return mFormat.format(value) + unit;
		}
	}

	/**
	 * 利润的Y轴单位规范
	 */
	private class LrYAxisValueFormatter implements ValueFormatter {
		private DecimalFormat mFormat;
		private String unit;
		private boolean isInt;

		public LrYAxisValueFormatter(String unit, boolean isInt) {
			//mFormat = new DecimalFormat(isInt ? "###,###,###,##0" : "###,###,###,##0.0");
			mFormat = new DecimalFormat("######0.00");
			this.unit = unit;
		}

		/*
		 * @Override public String getFormattedValue(float value, YAxis yAxis) {
		 * return mFormat.format(value) + unit; }
		 */
		@Override
		public String getFormattedValue(float value) {
			// TODO Auto-generated method stub
			return mFormat.format(value) + unit;
		}
	}

}
