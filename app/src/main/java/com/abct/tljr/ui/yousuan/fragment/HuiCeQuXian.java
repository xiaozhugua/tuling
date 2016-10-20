package com.abct.tljr.ui.yousuan.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.linechar.MyValueFormatter2;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.yousuan.activity.YouSuanItem;
import com.abct.tljr.ui.yousuan.model.HuiCeQuXianModel;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
import com.abct.tljr.ui.yousuan.model.YouSuanRecordModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class HuiCeQuXian extends Fragment implements OnClickListener{

	private View huice=null;
	private LineChart huicequxian=null;
	private List<Entry> huiceEntry=null; 
	private List<String> xVals=null;
	private LineData huicedata=null;
	private int page=1;
	private String HuiCeQuXianUrl=UrlUtil.URL_YS+"gentou/getWithdraw";
	public YouSuanItemModel model=null;
	private List<HuiCeQuXianModel> listModel=null;
	private RelativeLayout huicequxian_eare;
	private Button huicequxian_refresh;	
	private SimpleDateFormat mSimpleDateFormat=new SimpleDateFormat("MM/yy");
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		huice=inflater.inflate(R.layout.huicequxian,container,false);
		huicequxian=(LineChart)huice.findViewById(R.id.huicequxian_linechart);
		huicequxian_eare=(RelativeLayout)huice.findViewById(R.id.huicequxian_eare);
		huicequxian_refresh=(Button)huice.findViewById(R.id.huicequxian_refresh);
		huicequxian_refresh.setOnClickListener(this);
		model=((YouSuanItem)getActivity()).model;
        if(model==null){
            this.model=((YouSuanItem)getActivity()).itemModel.getGetTou();
        }
		initLineChart();
		getNetData();
		return huice;
	}
	
	public void initLineChart(){
		listModel=new ArrayList<HuiCeQuXianModel>();
		huicequxian.setDescription("");
		huicequxian.setMaxVisibleValueCount(60);
		huicequxian.setPinchZoom(true);
		huicequxian.setDrawGridBackground(true);
		huicequxian.setNoDataTextDescription("");
		
		XAxis xAxis =huicequxian.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setAdjustXLabels(true);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));
		xAxis.setTextSize(7);
		xAxis.setAvoidFirstLastClipping(true);
		
		YAxis leftAxis =huicequxian.getAxisLeft();
		leftAxis.setLabelCount(5);
		leftAxis.setStartAtZero(false);
		leftAxis.setTextColor(Color.rgb(139, 148, 153));
		leftAxis.setTextSize(7.5f);
		
		YAxis rightAxis=huicequxian.getAxisRight();
		rightAxis.setEnabled(false);
		
		huicequxian.animateY(1000);
		
		huicequxian.setDrawGridBackground(false);	
		huicequxian.getLegend().setEnabled(true);
		
	}
	
	public void getNetData(){
		String params="name="+model.getKey();
		NetUtil.sendGet(HuiCeQuXianUrl,params,new NetResult() {
			@Override
			public void result(String response) {
				if(ZhiYanParseJson.ParseHuiCeQuXian(listModel,response)){
					if(listModel.size()>0){
						initData(listModel);
					}else{
						showToast("对不起,没有数据");
						hideTop();
					}
				}else{
					showToast("对不起,没有数据");
					hideTop();
				}
			}
		});
	}

	public void showToast(String msg){
		Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
	}
	
	public void hideTop(){
		huicequxian_eare.setVisibility(View.VISIBLE);
		huicequxian.setVisibility(View.GONE);
	}
	
	public void showTip(){
		huicequxian_eare.setVisibility(View.GONE);
		huicequxian.setVisibility(View.VISIBLE);
	}
	
	public void initData(List<HuiCeQuXianModel> listModels){
		huiceEntry=new ArrayList<Entry>();
		xVals=new ArrayList<String>();
		for(int i=0;i<listModels.size();i++){
			huiceEntry.add(new Entry(listModels.get(i).getPoint(),i));
			xVals.add(mSimpleDateFormat.format(new Date(listModels.get(i).getDate()*1000))+"");
		}
		LineDataSet lineDataQuanyi=new LineDataSet(huiceEntry,"回测曲线");
		lineDataQuanyi.setDrawCircles(false);
		lineDataQuanyi.setDrawCubic(true);
		lineDataQuanyi.setCubicIntensity(0.2f);
		lineDataQuanyi.setDrawFilled(true);
		lineDataQuanyi.setHighLightColor(Color.rgb(244, 117, 117));
		lineDataQuanyi.setColor(Color.rgb(249, 203, 156));
		lineDataQuanyi.setFillColor(Color.rgb(249, 203, 156));
		huicedata=new LineData(xVals,lineDataQuanyi);
		
		initView();
	}
	
	public void initView(){
		showTip();
		huicequxian.setData(huicedata);
		huicequxian.invalidate();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.huicequxian_refresh:
			if(huiceEntry!=null)
				huiceEntry.clear(); 
			if(xVals!=null)
				xVals.clear();
			getNetData();
			break;
		}
	}
	
}
