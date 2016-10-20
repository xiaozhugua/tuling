package com.abct.tljr.hangqing.linechar;

import java.util.ArrayList;
import java.util.List;
import com.abct.tljr.kline.OptionsItemSelected;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.graphics.Color;
import android.view.MenuItem;

public class AllGuAll implements OptionsItemSelected{

	private LineChart mLineChart;
	private LineData data;
	
	public AllGuAll(LineChart mLineChart,LineData data){
		this.data = data;
		this.mLineChart=mLineChart;
		initLineChat();
	}
	
	private void initLineChat() {
		mLineChart.setDescription("");
		mLineChart.setMaxVisibleValueCount(60);
		mLineChart.setPinchZoom(false);
		mLineChart.setDrawGridBackground(false);
		mLineChart.setNoDataTextDescription("");
		mLineChart.setTouchEnabled(true);
		mLineChart.setDragEnabled(true);// 是否可以拖拽    
        mLineChart.setScaleEnabled(true);
		
		XAxis xAxis =mLineChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setAdjustXLabels(true);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));
		xAxis.setTextSize(7);
		xAxis.setAvoidFirstLastClipping(true);
		
		YAxis leftAxis =mLineChart.getAxisLeft();
		leftAxis.setLabelCount(5);
		leftAxis.setStartAtZero(false);
		leftAxis.setTextColor(Color.rgb(139, 148, 153));
		MyValueFormatter2 custom = new MyValueFormatter2();
		leftAxis.setValueFormatter(custom);
		leftAxis.setTextSize(7.5f);
		leftAxis.setLabelCount(5);
		
		YAxis rightAxis=mLineChart.getAxisRight();
		rightAxis.setEnabled(false);
		
		mLineChart.setDrawGridBackground(false);	
		mLineChart.getLegend().setEnabled(true);
		
	    data.setValueFormatter(new DataValue());
	    mLineChart.setData(data);
	    
	}
	
	public void updateLineChart(ArrayList<LineDataSet> DataSet,List<String> xVals){
		 LineData data = new LineData(xVals, DataSet);
	     data.setValueTextColor(Color.BLACK);
	     data.setValueTextSize(9f);
	     data.setValueFormatter(new DataValue());
	     mLineChart.setData(data);
	}
	
	public void ShowView(){
	     mLineChart.invalidate();
	}
	
	public LineChart getChart(){
		return this.mLineChart;
	}
	
	@Override
	public void optionsItemSelected(MenuItem item) {
		
	}

}
