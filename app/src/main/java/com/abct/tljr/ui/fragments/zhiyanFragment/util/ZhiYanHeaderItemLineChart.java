package com.abct.tljr.ui.fragments.zhiyanFragment.util;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.ZhiyanFinishiAdapter.finishview_info;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanFinishHeader;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class ZhiYanHeaderItemLineChart {

	private LineChart mLineChart=null;
	private LineData ChartData=null;
	private List<Entry> SousuoEntry=null;
	private List<Entry> MeitiEntry=null;
	private List<Entry> QingxuEntry=null;
	private List<String> ChartxVals=null;
	private Context context=null;
	private ZhiYanFinishHeader header=null;
	private finishview_info holderView=null;
	
	public ZhiYanHeaderItemLineChart(Context context,finishview_info holderView,ZhiYanFinishHeader header){
		this.mLineChart=holderView.mLineChart;
		this.context=context;
		this.holderView=holderView;
		this.header=header;
		SousuoEntry=new ArrayList<Entry>();
		MeitiEntry=new ArrayList<Entry>();
		QingxuEntry=new ArrayList<Entry>();
		ChartxVals=new ArrayList<String>();
		
		initData();
		initView();
	}
	
	public void initData(){
		if(header!=null){
			for(int i=0;i<header.getListItem().size();i++){
					SousuoEntry.add(new Entry(header.getListItem().get(i).getSerach(),i));
					MeitiEntry.add(new Entry(header.getListItem().get(i).getMedia(),i));
					QingxuEntry.add(new Entry(header.getListItem().get(i).getEmation(),i));
					ChartxVals.add(header.getListItem().get(i).getTime());
			}						
						
			holderView.meitiitem1.setText(header.getMediaRanking()+"");
			holderView.meitiitem2.setText(header.getMediaRate()+"%");
			holderView.meitiitem3.setText(header.getMedia()+"");
						
			holderView.sousouitme1.setText(header.getSearchRanking()+"");
			holderView.sousouitme2.setText(header.getSearchRate()+"%");
			holderView.sousouitme3.setText(header.getSerach()+"");
						
			holderView.qingxuitem1.setText(header.getEmationRanking()+"");
			holderView.qingxuitem2.setText(header.getEmationRate()+"%");
			holderView.qingxuitem3.setText(header.getEmation()+"");
						
			holderView.finishName.setText(header.getName()+"");
						
			initChart();
			initView();
		}
		
	}
	
   @SuppressWarnings("deprecation")
   public void initChart(){
	   LineDataSet lineDataSousuo = new LineDataSet(SousuoEntry,"");
	   LineDataSet lineDataMeiti = new LineDataSet(MeitiEntry,"");
	   LineDataSet lineDataQingxu = new LineDataSet(QingxuEntry,"");
	
	   lineDataSousuo.setDrawValues(false);
	   lineDataMeiti.setDrawValues(false);
	   lineDataQingxu.setDrawValues(false);
	
	   lineDataSousuo.setColor(context.getResources().getColor(R.color.zhiyan_finish_item3));
	   lineDataMeiti.setColor(context.getResources().getColor(R.color.zhiyan_finish_item1));
	   lineDataQingxu.setColor(context.getResources().getColor(R.color.red));
	
	   lineDataSousuo.setCircleColor(context.getResources().getColor(R.color.zhiyan_finish_item3));
	   lineDataMeiti.setCircleColor(context.getResources().getColor(R.color.zhiyan_finish_item1));
	   lineDataQingxu.setCircleColor(context.getResources().getColor(R.color.red));
	
	   lineDataSousuo.setCircleSize(3);
	   lineDataMeiti.setCircleSize(3);
	   lineDataQingxu.setCircleSize(3);
	
	   ChartData=new LineData(ChartxVals,lineDataSousuo);
	   ChartData.addDataSet(lineDataMeiti);
	   ChartData.addDataSet(lineDataQingxu);
	}
	
	@SuppressWarnings("deprecation")
	public void initView(){
		mLineChart.getAxisRight().setEnabled(false);
		mLineChart.getLegend().setEnabled(false);
//		mLineChart.animateX(800);
		mLineChart.setDescription("");
		mLineChart.setPinchZoom(true);
		mLineChart.setBackgroundColor(context.getResources().getColor(R.color.white));
		mLineChart.setBackgroundResource(R.color.white);
		mLineChart.setDrawGridBackground(false);
		
		mLineChart.getAxisLeft().setTextColor(context.getResources().getColor(R.color.lightgray));
		
		XAxis xAxis =mLineChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setAdjustXLabels(true);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));
		xAxis.setTextSize(7);
		xAxis.setAvoidFirstLastClipping(true);
		xAxis.setTextColor(context.getResources().getColor(R.color.lightgray));
		
		mLineChart.setData(ChartData);
		mLineChart.invalidate();
	}
	
}
