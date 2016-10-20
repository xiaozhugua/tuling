package com.abct.tljr.ui.fragments.zhiyanFragment.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanFinishHeader;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;

@SuppressWarnings("serial")
public class ZhiYanFinishItemLineChart implements Serializable{

	private LineChart mLineChart = null;
	private LineData ChartData = null;
	private List<Entry> SousuoEntry = null;
	private List<Entry> MeitiEntry = null;
	private List<Entry> QingxuEntry = null;
	private List<String> ChartxVals = null;

	private Context context = null;

	private TextView sousuo1 = null;
	private TextView sousuo2 = null;
	private TextView sousuo3 = null;
	private TextView meiti1 = null;
	private TextView meiti2 = null;
	private TextView meiti3 = null;
	private TextView qingxu1 = null;
	private TextView qingxu2 = null;
	private TextView qingxu3 = null;

	private String BaseUrl = "http://news.tuling.me/DataApi/index/stock?";
	private String type = null;
	private String code = null;
	private String market = null;

	public ZhiYanFinishHeader header = null;

	public ZhiYanFinishItemLineChart(Context context,View view,String type,String code, String market) {
		this.mLineChart = (LineChart) view.findViewById(R.id.zhiyan_finish_item_linechart);
		this.sousuo1 = (TextView) view.findViewById(R.id.sousuodiwu);
		this.sousuo2 = (TextView) view.findViewById(R.id.sousuodiwu2);
		this.sousuo3 = (TextView) view.findViewById(R.id.sousuodiwu3);
		this.meiti1 = (TextView) view.findViewById(R.id.meitidiwu);
		this.meiti2 = (TextView) view.findViewById(R.id.meitidiwu2);
		this.meiti3 = (TextView) view.findViewById(R.id.meitidiwu3);
		this.qingxu1 = (TextView) view.findViewById(R.id.qingxudiwu);
		this.qingxu2 = (TextView) view.findViewById(R.id.qingxudiwu2);
		this.qingxu3 = (TextView) view.findViewById(R.id.qingxudiwu3);

		this.context = context;
		this.type = type;
		this.code = code;
		this.market = market;

		SousuoEntry = new ArrayList<Entry>();
		MeitiEntry = new ArrayList<Entry>();
		QingxuEntry = new ArrayList<Entry>();
		ChartxVals = new ArrayList<String>();
		initData();
	}

	public void UpdateView(View view) {
		LogUtil.e("ZhiYanUpdateView","UpdateView");
        if(header==null){
            return ;
        }
		this.mLineChart = (LineChart) view.findViewById(R.id.zhiyan_finish_item_linechart);

		this.sousuo1 = (TextView) view.findViewById(R.id.sousuodiwu);
		this.sousuo2 = (TextView) view.findViewById(R.id.sousuodiwu2);
		this.sousuo3 = (TextView) view.findViewById(R.id.sousuodiwu3);
		this.meiti1 = (TextView) view.findViewById(R.id.meitidiwu);
		this.meiti2 = (TextView) view.findViewById(R.id.meitidiwu2);
		this.meiti3 = (TextView) view.findViewById(R.id.meitidiwu3);
		this.qingxu1 = (TextView) view.findViewById(R.id.qingxudiwu);
		this.qingxu2 = (TextView) view.findViewById(R.id.qingxudiwu2);
		this.qingxu3 = (TextView) view.findViewById(R.id.qingxudiwu3);

		buildView();
	}

	public void initData() {
		String param = "type=" + type + "&code=" + code + "&market=" + market;
		LogUtil.e("initDataParam", param);
        LogUtil.e("initdataurl",BaseUrl + param);
		NetUtil.sendGet(BaseUrl + param, new NetResult() {
			@Override
			public void result(String response) {
				LogUtil.e("initDataResponse", response);
				if (!response.equals("")) {
					new UpdateLineChart().execute(response);
				} else {

				}
			}
		});
	}

	public void initView() {
		mLineChart.getAxisRight().setEnabled(false);
		mLineChart.getLegend().setEnabled(false);
		mLineChart.setDescription("");
		mLineChart.setPinchZoom(true);
		mLineChart.setBackgroundColor(context.getResources().getColor(R.color.white));
		mLineChart.setBackgroundResource(R.color.white);
		mLineChart.setDrawGridBackground(false);
		mLineChart.setTouchEnabled(true);
		mLineChart.getAxisLeft().setTextColor(context.getResources().getColor(R.color.lightgray));

		XAxis xAxis = mLineChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setAdjustXLabels(true);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));
		xAxis.setTextSize(7);
		xAxis.setAvoidFirstLastClipping(true);
		xAxis.setTextColor(context.getResources().getColor(R.color.lightgray));
	}

	public void BuildData() {
		LogUtil.e("buildData","BuildData1");
		if (header != null) {
			LogUtil.e("buildData","BuildData2");
			for (int i = 0; i < header.getListItem().size(); i++) {
				SousuoEntry.add(new Entry(header.getListItem().get(i).getSerach(), i));
				MeitiEntry.add(new Entry(header.getListItem().get(i).getMedia(), i));
				QingxuEntry.add(new Entry(header.getListItem().get(i).getEmation(), i));
				ChartxVals.add(header.getListItem().get(i).getTime());
			}
			LineDataSet lineDataSousuo = new LineDataSet(SousuoEntry, "");
			LineDataSet lineDataMeiti = new LineDataSet(MeitiEntry, "");
			LineDataSet lineDataQingxu = new LineDataSet(QingxuEntry, "");

			lineDataSousuo.setDrawValues(false);
			lineDataMeiti.setDrawValues(false);
			lineDataQingxu.setDrawValues(false);

			lineDataSousuo.setColor(context.getResources().getColor(R.color.zhiyan_finish_item1));
			lineDataMeiti.setColor(context.getResources().getColor(R.color.red));
			lineDataQingxu.setColor(context.getResources().getColor(R.color.zhiyan_finish_item3));

			lineDataSousuo.setCircleColor(context.getResources().getColor(R.color.zhiyan_finish_item1));
			lineDataMeiti.setCircleColor(context.getResources().getColor(R.color.red));
			lineDataQingxu.setCircleColor(context.getResources().getColor(R.color.zhiyan_finish_item3));

			lineDataSousuo.setCircleSize(3);
			lineDataMeiti.setCircleSize(3);
			lineDataQingxu.setCircleSize(3);

			ChartData = new LineData(ChartxVals, lineDataSousuo);
			ChartData.addDataSet(lineDataMeiti);
			ChartData.addDataSet(lineDataQingxu);

		}
	}

	class UpdateLineChart extends AsyncTask<String, Void, Integer> {
		@SuppressWarnings("deprecation")
		@Override
		protected Integer doInBackground(String... params) {
			try {
                String test=params[0];
				header = ZhiYanParseJson.parseZhiYanHeaderData(params[0]);
                if(header==null){
                    return 3;
                }else{
                    BuildData();
                    return 1;
                }
			} catch (Exception e) {
				LogUtil.e("UpdateLineChartException", e.getMessage());
				return 2;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 1) {
				buildView();
			}
		}
	}

	public void buildView(){
		initView();
		sousuo1.setText(header.getSearchRanking()+"");
		sousuo2.setText(header.getSearchRate()+"%");
		sousuo3.setText(header.getSerach()+"");

		meiti1.setText(header.getMediaRanking()+"");
		meiti2.setText(header.getMediaRate()+"%");
		meiti3.setText(header.getMedia()+"");

		qingxu1.setText(header.getEmationRanking()+"");
		qingxu2.setText(header.getEmationRate()+"%");
		qingxu3.setText(header.getEmation()+"");

		mLineChart.setData(ChartData);
		mLineChart.invalidate();
	}
	
}
