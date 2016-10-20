package com.abct.tljr.ui.yousuan.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.yousuan.activity.YouSuanItem;
import com.abct.tljr.ui.yousuan.model.QiYuXianModel;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
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
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Quanyiquxian extends Fragment implements OnClickListener{

	private View quanyiview=null;
	private LineChart quanyiquxian=null;
	private LineData quanyidata=null;
	private List<Entry> quangyi=null;
	private List<String> xVals=null;
	private String QuanyiquxianUrl=UrlUtil.URL_YS+"gentou/getJingZhi";
	public YouSuanItemModel model=null;
	private int size=1000;
	private int page=1;
	private List<QiYuXianModel> listModel=null;
	private Button quanyi_refresh;
	private RelativeLayout quanyiquxian_eare;
	private SimpleDateFormat mSimpleDateFormat=null;
	private int TempData=0;
	private ArrayList<Integer> QuanyiColors = new ArrayList<Integer>();
	
	@Nullable
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		quanyiview=inflater.inflate(R.layout.quanyiquxian,container,false);
		quanyiquxian=(LineChart)quanyiview.findViewById(R.id.quanyiquxian_linechart);
		model=((YouSuanItem)getActivity()).model;
        if(model==null){
            this.model=((YouSuanItem)getActivity()).itemModel.getGetTou();
        }
		initLineChart();
		getNetData();
		
		return quanyiview;
	}
	
	public void getNetData(){
		String params="name="+model.getKey()+"&page="+page+"&size="+size;
		NetUtil.sendGet(QuanyiquxianUrl,params,new NetResult() {
			@Override
			public void result(String response){
				if(!response.equals("")){
					ZhiYanParseJson.ParseQuanyiquxian(listModel,response);
					if(listModel.size()>0){
						initData(listModel);
					}else{
						hideTop();
						showToast("对不起,没有数据");
					}
				}else{
					hideTop();
					showToast("对不起,没有数据");
				}
			}
		});
	}
	
	public void showToast(String msg){
		Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
	}
	
	public void hideTop(){
		quanyiquxian_eare.setVisibility(View.VISIBLE);
		quanyiquxian.setVisibility(View.GONE);
	}
	
	public void showTip(){
		quanyiquxian_eare.setVisibility(View.GONE);
		quanyiquxian.setVisibility(View.VISIBLE);
	}
	
	public void initLineChart(){
		listModel=new ArrayList<QiYuXianModel>();
		quanyi_refresh=(Button)quanyiview.findViewById(R.id.quanyi_refresh);
		quanyi_refresh.setOnClickListener(this);
		quanyiquxian_eare=(RelativeLayout)quanyiview.findViewById(R.id.quanyiquxian_eare);
		mSimpleDateFormat=new SimpleDateFormat("MM/dd");
		quanyiquxian.setDescription("");
		quanyiquxian.setMaxVisibleValueCount(60);
		quanyiquxian.setPinchZoom(true);
		quanyiquxian.setDrawGridBackground(true);
		quanyiquxian.setNoDataTextDescription("");
		quanyiquxian.setTouchEnabled(true);
		quanyiquxian.setDragEnabled(true);// 是否可以拖拽    
		quanyiquxian.setScaleEnabled(true);
		
		XAxis xAxis =quanyiquxian.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setAdjustXLabels(true);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));
		xAxis.setTextSize(7);
		xAxis.setAvoidFirstLastClipping(true);
		
		YAxis leftAxis =quanyiquxian.getAxisLeft();
		leftAxis.setLabelCount(5);
		leftAxis.setStartAtZero(false);
		leftAxis.setTextColor(Color.rgb(139, 148, 153));
		leftAxis.setTextSize(7.5f);
		
		YAxis rightAxis=quanyiquxian.getAxisRight();
		rightAxis.setEnabled(false);
		
		quanyiquxian.animateY(1000);
		quanyiquxian.setDrawGridBackground(true);	
		quanyiquxian.getLegend().setEnabled(true);
		
	}
	
	public void initData(List<QiYuXianModel> listModel){
		quangyi=new ArrayList<Entry>();
		xVals=new ArrayList<String>();
		for(int i=0;i<listModel.size();i++){
			TempData+=listModel.get(i).getJingZhi();
			if(TempData>0){
				QuanyiColors.add(Color.rgb(255,185,185));
			}else{
				QuanyiColors.add(Color.rgb(204,234,156));
			}
			quangyi.add(new Entry(TempData,i));
			xVals.add(mSimpleDateFormat.format(new Date(listModel.get(i).getData()*1000))+"");
		}
		LineDataSet lineDataQuanyi = new LineDataSet(quangyi, "权益曲线");
		lineDataQuanyi.setDrawCircles(false);
		lineDataQuanyi.setHighLightColor(Color.rgb(255, 183, 183));
		lineDataQuanyi.setDrawCubic(true);
		lineDataQuanyi.setColors(QuanyiColors);
		lineDataQuanyi.setCubicIntensity(0.2f);
		lineDataQuanyi.setDrawFilled(true);
		lineDataQuanyi.setColor(Color.rgb(255, 183, 183));
		lineDataQuanyi.setHighLightColor(Color.rgb(255, 183, 183));
		lineDataQuanyi.setFillColor(ColorTemplate.getHoloBlue());
		quanyidata=new LineData(xVals,lineDataQuanyi);
		
		initView();
	}
	
	public void initView(){
		showTip();
		quanyiquxian.setData(quanyidata);
		quanyiquxian.invalidate();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.quanyi_refresh:
			if(quangyi!=null)
			  quangyi.clear();
			if(xVals!=null)
			  xVals.clear();	
			getNetData();
			break;
		}
	}
	
}
