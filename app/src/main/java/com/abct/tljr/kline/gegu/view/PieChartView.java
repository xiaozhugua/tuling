package com.abct.tljr.kline.gegu.view;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;

import android.graphics.Color;

/**
 * Created by Administrator on 2016/4/21.
 */
public class PieChartView {
	private PieChart mChart;
	private PieData data;

	public PieChartView(PieChart mChart, PieData data) {
		this.mChart = mChart;
		this.data = data;
	}

	public void initChart() {
		mChart.setUsePercentValues(true);
		mChart.setDescription("");
		mChart.setTouchEnabled(false);
		//mChart.setExtraOffsets(5, 10, 5, 5);
		//mChart.setDragDecelerationFrictionCoef(0.95f);
		// mChart.setCenterText(generateCenterSpannableText());
		mChart.setDrawHoleEnabled(true);
		mChart.setHoleColor(Color.WHITE);
		//mChart.setTransparentCircleColor(Color.WHITE);
		//mChart.setTransparentCircleAlpha(110);
		
		mChart.setHoleRadius(58f);
		mChart.setTransparentCircleRadius(61f);
		mChart.setDrawSliceText(false);//不写饼图上的行业名字
		//mChart.setDrawCenterText(true);
		//mChart.set
		mChart.setRotationAngle(0);
		// enable rotation of the chart by touch
		mChart.setRotationEnabled(false);
		//mChart.setHighlightPerTapEnabled(true);
		// mChart.setUnit(" €");
		// mChart.setDrawUnitsInChart(true);
		// add a selection listener
		mChart.animateY(1400);
		// mChart.spin(2000, 0, 360);
		Legend l = mChart.getLegend();
		l.setEnabled(false);
		/*l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
		l.setXEntrySpace(13.0f);
		l.setYEntrySpace(15.0f);
		l.setYOffset(11.0f);*/
		mChart.setData(data);
	}
}
