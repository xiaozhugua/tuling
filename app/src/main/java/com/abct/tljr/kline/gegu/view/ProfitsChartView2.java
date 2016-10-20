package com.abct.tljr.kline.gegu.view;

import java.text.DecimalFormat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import android.graphics.Color;

/**
 * Created by Administrator on 2016/4/22.
 */
public class ProfitsChartView2 {
	private CombinedChart combinedChart;
	private BarChart barChart;
	private CombinedData combinedData;
	private BarData barData;
	private int textColor = Color.rgb(0x9a, 0x9a, 0x9a);

	public ProfitsChartView2(CombinedChart combinedChart, BarChart barChart, CombinedData combinedData,
			BarData barData) {
		this.combinedChart = combinedChart;
		this.barChart = barChart;
		this.combinedData = combinedData;
		this.barData = barData;
	}

	public void initChart() {
		initCombinedChart();
		initBarChart();
	}

	private void initBarChart() {
		barChart.setDescription("");
		barChart.setScaleEnabled(false);
		barChart.setTouchEnabled(false);
		barChart.setPinchZoom(false);
		XAxis xAxis = barChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setTextColor(textColor);
		xAxis.setAxisLineColor(textColor);
		YAxis leftAxis = barChart.getAxisLeft();
		YAxis rightAxis = barChart.getAxisRight();
		leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		leftAxis.setDrawGridLines(false);
		leftAxis.setDrawLabels(true);
		//leftAxis.setAxisMaxValue(barData.getYMax());
		leftAxis.setAxisMinValue(barData.getYMin());
	/*	leftAxis.setAxisMaxValue(barData.getYMax());
		leftAxis.setAxisMinValue(-barData.getYMax());*/
		
		leftAxis.setValueFormatter(new MyYAxisValueFormatter("百万", true));
		leftAxis.setSpaceTop(5f);
		leftAxis.setTextColor(textColor);
		
		// leftAxis.setZeroLineColor(textColor);
		rightAxis.setValueFormatter(new MyYAxisValueFormatter("%", false));
		rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		
		leftAxis.setStartAtZero(false);
		rightAxis.setStartAtZero(false);
		
		rightAxis.setDrawGridLines(false);
		rightAxis.setDrawLabels(true);
		rightAxis.setAxisMaxValue(combinedData.getYMax());
		rightAxis.setAxisMinValue(combinedData.getYMin());
		rightAxis.setSpaceTop(5f);
		rightAxis.setTextColor(Color.TRANSPARENT);
		// rightAxis.setZeroLineColor(Color.TRANSPARENT);
		Legend l = barChart.getLegend();
		l.setXEntrySpace(50);
		l.setForm(Legend.LegendForm.SQUARE);
		l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
		barChart.animateXY(1500, 1500);
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
		xAxis.setTextColor(Color.TRANSPARENT);
		xAxis.setAxisLineColor(textColor);
		YAxis leftAxis = combinedChart.getAxisLeft();
		YAxis rightAxis = combinedChart.getAxisRight();
		rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		rightAxis.setDrawGridLines(false);
		rightAxis.setDrawLabels(true);
		rightAxis.setSpaceTop(5f);
		rightAxis.setAxisMinValue(combinedData.getYMin() - combinedData.getYMin() * 0.1f);
		rightAxis.setAxisMaxValue(combinedData.getYMax() + combinedData.getYMax() * 0.1f);
		rightAxis.setTextColor(textColor);
		// rightAxis.setZeroLineColor(textColor);
		//rightAxis.set
		rightAxis.setValueFormatter(new MyYAxisValueFormatter("%", false));
		leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		leftAxis.setDrawGridLines(false);
		leftAxis.setDrawLabels(true);
		leftAxis.setSpaceTop(5f);
		leftAxis.setAxisMinValue(barData.getYMin());
		leftAxis.setAxisMaxValue(barData.getYMax());
		leftAxis.setTextColor(Color.TRANSPARENT);
		// leftAxis.setZeroLineColor(Color.TRANSPARENT);
		leftAxis.setValueFormatter(new MyYAxisValueFormatter("百万", true));
		Legend l = combinedChart.getLegend();
		l.setXEntrySpace(50);
		l.setForm(Legend.LegendForm.CIRCLE);
		l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
		combinedChart.animateXY(1500, 1500);
		combinedChart.setData(combinedData);

	}

	private class MyYAxisValueFormatter implements ValueFormatter {
	    private DecimalFormat mFormat;
	    private String unit;
	    private boolean isInt;

	    public MyYAxisValueFormatter(String unit,boolean isInt) {
	        mFormat = new DecimalFormat(isInt?"###,###,###,##0":"###,###,###,##0.0");
	        this.unit = unit;
	    }
	 /*   @Override
	    public String getFormattedValue(float value, YAxis yAxis) {
	        return mFormat.format(value) + unit;
	    }*/
		@Override
		public String getFormattedValue(float value) {
			// TODO Auto-generated method stub
			 return mFormat.format(value) + unit;
		}
	}

}
