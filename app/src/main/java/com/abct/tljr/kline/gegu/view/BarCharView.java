package com.abct.tljr.kline.gegu.view;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;



/**
 * Created by Administrator on 2016/4/20.
 */
public class BarCharView {
    private BarChart barChart;
    private BarData data;
    private int textColor = Color.rgb(0x9a,0x9a,0x9a);
    private String mUnit;
    private boolean isLegend;

    public BarCharView(BarChart barChart, BarData data,String unit,boolean isLegend) {
        this.barChart = barChart;
        this.data = data;
        this.isLegend=isLegend;
        mUnit = unit;
    }
    public void initChar() {
        barChart.setDescription("");
        barChart.setScaleEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.setNoDataText("没有数据");
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(textColor);
        xAxis.setAxisLineColor(textColor);
        YAxis leftAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();
        MyYAxisValueFormatter formatter = new MyYAxisValueFormatter(mUnit,false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setValueFormatter(formatter);
       // leftAxis.setDrawZeroLine(true);
        leftAxis.setSpaceTop(5f);
        leftAxis.setTextColor(textColor);
        //leftAxis.setZeroLineColor(textColor);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        Legend legend = barChart.getLegend();
        legend.setEnabled(isLegend);
        legend.setXEntrySpace(100);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        barChart.setData(data);
        barChart.animateXY(1500, 1500);
    }
    
    private class MyYAxisValueFormatter implements ValueFormatter {
        private DecimalFormat mFormat;
        private String unit;
        private boolean isInt;

        public MyYAxisValueFormatter(String unit,boolean isInt) {
            mFormat = new DecimalFormat(isInt?"###,###,###,##0":"###,###,###,##0.0");
            this.unit = unit;
        }
       /* @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value) + unit;
        }*/

		@Override
		public String getFormattedValue(float value) {
			// TODO Auto-generated method stub
			return mFormat.format(value) + unit+"%";
		}
		
    }


}
