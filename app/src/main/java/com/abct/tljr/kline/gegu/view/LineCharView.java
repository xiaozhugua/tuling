package com.abct.tljr.kline.gegu.view;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;

import java.text.DecimalFormat;


/**
 * Created by Administrator on 2016/4/20.
 */
public class LineCharView {
    private LineChart mChart;
    private LineData data;
    private int textColor = Color.rgb(0x9a,0x9a,0x9a);

    public LineCharView(LineChart lineChart, LineData data) {
        this.mChart = lineChart;
        this.data = data;
    }

    public void initChar(){
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setDrawBorders(false);
        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setNoDataText("没有数据");
//        mChart.setAutoScaleMinMaxEnabled(true);
      
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(textColor);
        xAxis.setAxisLineColor(textColor);
        
        xAxis.setDrawLabels(true);
        YAxis leftAxis = mChart.getAxisLeft();
        YAxis rightAxis = mChart.getAxisRight();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(true);
        leftAxis.setAxisMinValue(data.getYMin());
        leftAxis.setAxisMaxValue(data.getYMax());
        leftAxis.setValueFormatter(new MyYAxisValueFormatter("%",false));
        leftAxis.setSpaceTop(5f);
        leftAxis.setStartAtZero(false);
       // leftAxis.setDrawZeroLine(true);
        leftAxis.setTextColor(textColor);
       // leftAxis.setZeroLineColor(textColor);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setTextColor(textColor);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setXEntrySpace(40);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mChart.animateXY(1500,1500);
        mChart.setData(data);
    }
    private class MyYAxisValueFormatter implements com.github.mikephil.charting.utils.ValueFormatter {
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
			 return mFormat.format(value) + unit;
		}
    }
    
    
}
