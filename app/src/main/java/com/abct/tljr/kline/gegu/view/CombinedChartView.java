package com.abct.tljr.kline.gegu.view;

import android.graphics.Color;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;




/**
 * Created by Administrator on 2016/4/21.
 */
public class CombinedChartView {
    private CombinedChart mChart;
    private CombinedData data;
    private int textColor = Color.rgb(0x9a,0x9a,0x9a);

    public CombinedChartView(CombinedChart mChart, CombinedData data ) {
        this.mChart = mChart;
        this.data = data;
    }

    public void initchar() {
       // mChart.setDrawGridBackground(false);
    	mChart.setDrawGridBackground(true);
    	mChart.setGridBackgroundColor(Color.WHITE);
        mChart.setDescription("");
        mChart.setDrawBorders(false);
        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setNoDataText("没有数据");
        //mChart.setMaxVisibleValueCount(6);
        //这里调换一下位置
        mChart.setDrawOrder(new CombinedChart.DrawOrder[] { CombinedChart.DrawOrder.LINE,CombinedChart.DrawOrder.BAR });
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(textColor);
        xAxis.setAxisLineColor(textColor);
        YAxis leftAxis = mChart.getAxisLeft();
        YAxis rightAxis = mChart.getAxisRight();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);
       // leftAxis.setSpaceTop(5f);
        //leftAxis.setAxisMinValue(0f);
        leftAxis.setAxisMaxValue(data.getYMax(YAxis.AxisDependency.LEFT)+1.0f);
        leftAxis.setTextColor(textColor);
        leftAxis.setStartAtZero(false);
       // leftAxis.setLabelCount(10);
       // leftAxis.setZeroLineColor(textColor);
      //  leftAxis.set
        rightAxis.setStartAtZero(false);
        rightAxis.setDrawLabels(true);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setTextColor(textColor);
        Legend l = mChart.getLegend();
        l.setXEntrySpace(30);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mChart.animateXY(1500,1500);
        mChart.setData(data);
    }


}
