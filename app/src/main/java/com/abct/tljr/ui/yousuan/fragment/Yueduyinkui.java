package com.abct.tljr.ui.yousuan.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.yousuan.activity.YouSuanItem;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
import com.abct.tljr.ui.yousuan.model.YueDuYinkuiModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
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

public class Yueduyinkui extends Fragment implements OnClickListener{

	private View yuedu=null;
	private BarChart yueduyinkui=null;
	private ArrayList<BarEntry> yVals = null;
	private List<String> xVals=null;
	private String Yueduyinkui=UrlUtil.URL_YS+"gentou/getMoonProfit";
	private YouSuanItemModel model=null;
	private List<YueDuYinkuiModel> listModel=null;
	private int page=1;
	private int size=10;
	private BarData data=null;
	private Button refeshing=null;
	private RelativeLayout yueduyinkui_eare;
	private SimpleDateFormat mSimpleDateFormat=null;
	private ArrayList<Integer> QuanyiColors = new ArrayList<Integer>();
	int yellow = Color.parseColor("#EBAB41");
	int red = Color.parseColor("#EC4F40");
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		yuedu=inflater.inflate(R.layout.yueduyinkui,container,false);
		yueduyinkui=(BarChart)yuedu.findViewById(R.id.yueduyinkui_barchart);
		initBarChart();
		getNetData();
		return yuedu;
	}

	public void getNetData(){
		String params="name="+model.getKey()+"&page="+page+"&size="+size;
        LogUtil.e("getNetDataUrl",Yueduyinkui+"?"+params);
		NetUtil.sendGet(Yueduyinkui,params,new NetResult() {
			@Override
			public void result(String response) {
				if(ZhiYanParseJson.ParseYueDuYinKui(listModel,response)){
					if(listModel.size()>0){
						initData(listModel);
					}else{
						showToast("对不起，没有数据");
						nodataTip();
					}
				}else{
					showToast("对不起，没有数据");
					nodataTip();
				}
			}
		});
	}
	
	public void showToast(String msg){
		Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
	}
	
	public void nodataTip(){
		yueduyinkui_eare.setVisibility(View.VISIBLE);
		yueduyinkui.setVisibility(View.GONE);
	}
	
	public void hasdateTip(){
		yueduyinkui_eare.setVisibility(View.GONE);
		yueduyinkui.setVisibility(View.VISIBLE);
	}
	
	public void initBarChart(){
		listModel=new ArrayList<YueDuYinkuiModel>();
		refeshing=(Button)yuedu.findViewById(R.id.barchart_refresh);
		refeshing.setOnClickListener(this);
		yueduyinkui_eare=(RelativeLayout)yuedu.findViewById(R.id.yueduyinkui_eare);
		model=((YouSuanItem)getActivity()).model;
        if(model==null){
            this.model=((YouSuanItem)getActivity()).itemModel.getGetTou();
        }
		mSimpleDateFormat=new SimpleDateFormat("MM");
		yueduyinkui.setDescription("");
		yueduyinkui.setMaxVisibleValueCount(60);
		yueduyinkui.setPinchZoom(false);
		yueduyinkui.setDrawBarShadow(false);
		yueduyinkui.setDrawGridBackground(false);

        XAxis xAxis = yueduyinkui.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);
       
        YAxis mYAxis=yueduyinkui.getAxisLeft();
        mYAxis.setDrawGridLines(false);
        mYAxis.setLabelCount(5);
        mYAxis.setStartAtZero(false);
        
        yueduyinkui.getAxisRight().setEnabled(false);
        
        yueduyinkui.animateY(1000);
        yueduyinkui.getLegend().setEnabled(false);
	}
	
	public void initData(List<YueDuYinkuiModel> listModel){
		yVals = new ArrayList<BarEntry>();
		xVals=new ArrayList<String>(); 
		for(int i=0;i<listModel.size();i++){
			if(listModel.get(i).getProfit()>0){
				QuanyiColors.add(red);
			}else{
				QuanyiColors.add(yellow);
			}
	        yVals.add(new BarEntry(listModel.get(i).getProfit(), i));
	        xVals.add(mSimpleDateFormat.format(new Date(listModel.get(i).getDate()*1000))+"");
		}
		BarDataSet set1 = new BarDataSet(yVals,"月度盈亏");
        set1.setColors(QuanyiColors);
        set1.setDrawValues(false);
        ArrayList<BarDataSet> dataSets=new ArrayList<BarDataSet>();
        dataSets.add(set1);
        data = new BarData(xVals, dataSets);
        buildView();
	}
	
	public void buildView(){
		hasdateTip();
		yueduyinkui.setData(data);
	    yueduyinkui.invalidate();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.barchart_refresh:
			if(xVals!=null)
			xVals.clear();
			if(yVals!=null)
			yVals.clear();
			getNetData();
			break;
		}
	}
	
}
