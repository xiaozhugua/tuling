package com.abct.tljr.ui.yousuan.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.yousuan.activity.YouSuanItem;
import com.abct.tljr.ui.yousuan.model.DuoKongYinKuiModel;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
import com.abct.tljr.ui.yousuan.util.DuoKongItemValueFormatter;
import com.abct.tljr.ui.yousuan.util.DuoKongValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

public class DuoKongYinkui extends Fragment implements OnClickListener {

	private View duokong = null;
	private BarChart duokongBarChart = null;
	private ArrayList<BarEntry> yVals1 = null;
	private ArrayList<BarEntry> yVals2 = null;
	private List<String> xVals = null;
	private BarData data = null;
	private ArrayList<BarDataSet> dataSets = null;
	private String DuokongyinkuiUrl=UrlUtil.URL_YS+"gentou/getDuoKong";
	public YouSuanItemModel model=null;
	private List<DuoKongYinKuiModel> listModel=null;
	private RelativeLayout duokongyinkui_eare;
	private DecimalFormat decimalFormat=new DecimalFormat("0.0000");
	private Button refresh;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		duokong = inflater.inflate(R.layout.duokongyinkui, container, false);
		duokongBarChart = (BarChart) duokong.findViewById(R.id.duokongyinkui_barchart);
		initBarChart();
		getNetData();
		return duokong;
	}
	
	public void initBarChart() {
		model=((YouSuanItem)getActivity()).model;
        if(model==null){
            this.model=((YouSuanItem)getActivity()).itemModel.getGetTou();
        }
		duokongyinkui_eare=(RelativeLayout)duokong.findViewById(R.id.duokongyinkui_eare);
		refresh=(Button)duokong.findViewById(R.id.duokong_refresh);
		refresh.setOnClickListener(this);
		listModel=new ArrayList<DuoKongYinKuiModel>();
		duokongBarChart.setDescription("");
		duokongBarChart.setMaxVisibleValueCount(60);
		duokongBarChart.setPinchZoom(false);
		duokongBarChart.setDrawBarShadow(false);
		duokongBarChart.setDrawGridBackground(false);

		XAxis xAxis = duokongBarChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(0);
		xAxis.setDrawGridLines(false);
		
		YAxis leftAxis =duokongBarChart.getAxisLeft();
		leftAxis.setDrawGridLines(false);
		DuoKongValueFormatter custom = new DuoKongValueFormatter();
		leftAxis.setValueFormatter(custom);
		
		duokongBarChart.getAxisRight().setEnabled(false);
		duokongBarChart.animateY(2500);

		dataSets = new ArrayList<BarDataSet>();
	}

	public void getNetData(){
		String params="name="+model.getKey();
		NetUtil.sendGet(DuokongyinkuiUrl,params,new NetResult() {
			@Override
			public void result(String response){
				if(ZhiYanParseJson.ParseDuoKongYinKui(listModel,response)){
					initData(listModel);
				}else{
					hideTop();
					showToast("对不起，没有数据");
				}
			}
		});
	}
	
	public int DealData(double data){
		return (int)(Double.valueOf(decimalFormat.format(data))*10000);
	}
	
	public void showToast(String msg){
		Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
	}
	
	public void hideTop(){
		duokongyinkui_eare.setVisibility(View.VISIBLE);
		duokongBarChart.setVisibility(View.GONE);
	}
	
	public void showTip(){
		duokongyinkui_eare.setVisibility(View.GONE);
		duokongBarChart.setVisibility(View.VISIBLE);
	}
	
	public void initData(List<DuoKongYinKuiModel> listModel){
		yVals1 = new ArrayList<BarEntry>();
		yVals2 = new ArrayList<BarEntry>();
		xVals = new ArrayList<String>();
		for(int i=0;i<listModel.size();i++){
			yVals1.add(new BarEntry(DealData(listModel.get(i).getDuo()),i));
			yVals2.add(new BarEntry(DealData(listModel.get(i).getKong()),i));
			xVals.add(listModel.get(i).getType()+ "");
		}
	
		BarDataSet set1 = new BarDataSet(yVals1,"多头");
        set1.setValueFormatter(new DuoKongItemValueFormatter());
		set1.setColor(Color.rgb(149, 206, 255));
		set1.setDrawValues(true);
		
		BarDataSet set2 = new BarDataSet(yVals2,"空头");
        set2.setValueFormatter(new DuoKongItemValueFormatter());
		set2.setColor(Color.rgb(247, 163, 92));
		set2.setDrawValues(true);

		dataSets.add(set1);
		dataSets.add(set2);
		
		buildView();
	}
	
	public void buildView() {
		showTip();
		data = new BarData(xVals, dataSets);
		duokongBarChart.setData(data);
		duokongBarChart.invalidate();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.duokong_refresh:
			if(yVals1!=null)
				yVals1.clear();
			if(yVals2!=null)
				yVals2.clear();	
			if(xVals!=null)
				xVals.clear();
			getNetData();
			break;
		}
	}

}
