package com.abct.tljr.hangqing.tiaocang.fragment;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.tiaocang.TljrChangePercent;
import com.abct.tljr.hangqing.tiaocang.TljrTiaoCangView;
import com.abct.tljr.model.OneGu;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class ChiCangPercent extends Fragment implements OnClickListener {

	private View mView = null;
	private PieChart pieChart = null;
	private List<OneGu> listGu = null;
	private Map<String, Integer> BuildPieChartData;
	private Map<String, Double> tempCodeKaipanjia;
	private Map<String, Integer> tempPercent;
	private Map<String, String> tempCodeId;
	public static TiaoCangUpdatePieChart mTiaoCangUpdatePieChart=null;
	public static TiaoCangUpdateDelete mTiaoCangUpdateDelete=null;
	public static UpdateZuId mUpdateZuId=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.tljr_page_zh_chicangpercent,container, false);
		pieChart = (PieChart) mView.findViewById(R.id.tljr_zh_changepercent_piechart);
		listGu = ((TljrChangePercent) getActivity()).listGus;
		BuildPieChartData = new HashMap<String, Integer>();
		tempPercent = new HashMap<String, Integer>();
		tempCodeId = new HashMap<String, String>();
		tempCodeKaipanjia = new HashMap<String, Double>();
		pieChart.setOnClickListener(this);
		//调仓更新百分比
		mTiaoCangUpdatePieChart = new TiaoCangUpdatePieChart();
		IntentFilter intentFilter = new IntentFilter("tiaocang_update_piechart");
		getActivity().registerReceiver(mTiaoCangUpdatePieChart, intentFilter);
		//删除更新百分比
		mTiaoCangUpdateDelete=new TiaoCangUpdateDelete();
		IntentFilter intentFilter2=new IntentFilter("tljr_tiaocang_changeupdate");
		getActivity().registerReceiver(mTiaoCangUpdateDelete,intentFilter2);
		
		mUpdateZuId=new UpdateZuId();
		IntentFilter intentFilter3=new IntentFilter("tljr_tiaocang_updateZuId");
		getActivity().registerReceiver(mUpdateZuId,intentFilter3);
		//更新组合id
		getZuData(getActivity().getIntent().getStringExtra("zuid"),getActivity().getIntent().getStringExtra("key"));
		getZuId(getActivity().getIntent().getStringExtra("key"));
		
		return mView;
	}

	// 得到组合的百分比再跳到调仓界面
		public void getZuData(final String paramkey, final String name) {
			if (MyApplication.getInstance().self != null) {
				NetUtil.sendPost(UrlUtil.URL_tc, "action=getzh&uid="
						+ MyApplication.getInstance().self.getId() + "&zhid="
						+ paramkey, new NetResult() {
					@Override
					public void result(String msg) {
						try {
							JSONObject object = new JSONObject(msg);
							DecimalFormat df = new DecimalFormat("#.00");
							int status = object.getInt("status");
							if (status == 1) {
								JSONArray array = object.getJSONArray("msg");
								if (!array.toString().equals("[]")) {
									String code;
									for (int i = 0; i < array.length(); i++) {
										JSONObject json = array.getJSONObject(i);
										code = json.getString("code");
										tempCodeKaipanjia.put(code, Double.valueOf(df.format(json.getDouble("open"))));
										tempPercent.put(code,Math.round((Float.valueOf(json.getString("percent")) * 100)));
										tempCodeId.put(code, json.getString("id"));
									}
									for (String keys : tempPercent.keySet()) {
										for (int i = 0; i < listGu.size(); i++) {
											if (listGu.get(i).getCode().equals(keys)) {
												listGu.get(i).setPercent(tempPercent.get(keys));
												listGu.get(i).setId(tempCodeId.get(keys));
												listGu.get(i).setKaipanjia(tempCodeKaipanjia.get(keys));
											}
										}
									}
								} else {
								}
							} else if (status == 2) {
								JSONArray array = object.getJSONArray("msg");
								String code;
								for (int i = 0; i < array.length(); i++) {
									JSONObject json = array.getJSONObject(i);
									code = json.getString("code");
									tempCodeKaipanjia.put(code, Double.valueOf(df.format(json.getDouble("open"))));
									tempCodeId.put(code, json.getString("id"));
								}
								for (String keys : tempPercent.keySet()) {
									for (int i = 0; i < listGu.size(); i++) {
										if (listGu.get(i).getCode().equals(keys)) {
											listGu.get(i).setId(tempCodeId.get(keys));
											listGu.get(i).setKaipanjia(tempCodeKaipanjia.get(keys));
										}
									}
								}
							}
							// 构建饼图
							initPieChart();
						} catch (Exception e) {
						}
					}
				});
			}
		}
	
	
	//得到刷新组合Id
	public void getZuId(final String paramkey) {
		LogUtil.e("geZuDataUrl",UrlUtil.URL_tc+"?action=getzh&uid="+MyApplication.getInstance().self.getId()+"&zhid="+paramkey);
		if (MyApplication.getInstance().self != null) {
			NetUtil.sendPost(UrlUtil.URL_dmg,"id="+MyApplication.getInstance().self.getId()+
					"&operaList=[]"+"&type=get&v=1"+"&zhid="+paramkey,new NetResult() {
				@Override
				public void result(String msg) {
					try {
						LogUtil.e("getZuIdMsg",msg);
						parseZuHeIdData(msg);
					} catch (Exception e) {
					}
				}
			});
		}
	}

	public void parseZuHeIdData(String msg) {
		try {
			List<Map<String, String>> ListParam = new ArrayList<Map<String, String>>();
			Map<String, String> param = null;
			JSONObject object = new JSONObject(msg);
			if (object.getInt("status") == 1) {
				JSONArray array = object.getJSONArray("msg");
				JSONObject obj = array.getJSONObject(0);
				JSONArray stocks = obj.getJSONArray("stocks");
				JSONObject stock = null;
				for (int i = 0; i < stocks.length(); i++) {
					stock = stocks.getJSONObject(i);
					param = new HashMap<String, String>();
					param.put("code", stock.getString("code"));
					param.put("id", stock.getString("id"));
					ListParam.add(param);
				}
			} else {
			}
			for (Map<String, String> map : ListParam) {
				for (int j = 0; j < listGu.size(); j++) {
					if (map.get("code").equals(listGu.get(j).getCode())) {
						listGu.get(j).setId(map.get("id"));
					}
				}
			}
		} catch (Exception e) {
		}
	}
	
	// 加载饼图参数
	public void initPieChart() {
		pieChart.setOnClickListener(this);
		pieChart.setUsePercentValues(false);
		pieChart.setDescription("");
		pieChart.setDrawHoleEnabled(true);
		pieChart.setHoleColorTransparent(true);
		pieChart.setHoleRadius(55f);
		pieChart.setTransparentCircleRadius(0f);
		pieChart.setRotationAngle(0);
		pieChart.setRotationEnabled(false);
		pieChart.setTouchEnabled(false);
		pieChart.setNoDataText("没有数据");
		pieChart.setDrawSliceText(false);
		pieChart.setDrawCenterText(true);
        pieChart.animateXY(1000, 1000);
		if(listGu.size()>0){
			pieChart.setCenterText("点击可调仓");
		}
		pieChart.setCenterTextSize(15); 
		pieChart.setCenterTextColor(Color.BLACK);
        Legend mLegend = pieChart.getLegend(); // 设置比例图
		mLegend.setPosition(LegendPosition.BELOW_CHART_LEFT); // 最右边显示
		mLegend.setForm(LegendForm.SQUARE); // 设置比例图的形状，默认是方形
		mLegend.setXEntrySpace(7f);
		mLegend.setYEntrySpace(5f);
		initPieChartData();
	}

	// 组装化饼图的数据
	public void initPieChartData() {
		for (OneGu gu : listGu) {
			BuildPieChartData.put(gu.getName(), gu.getPercent());
		}
		if (BuildPieChartData.size() != 0) {
			if (BuildPieChartData.size() == 1) {
				for (String key : BuildPieChartData.keySet()) {
					if (BuildPieChartData.get(key) == 0) {
						BuildPieChartData.put("现金", 100);
					}
				}
			} else {
				int num = 0;
				for (String key : BuildPieChartData.keySet()) {
					if (BuildPieChartData.get(key) == 0) {
						num += 1;
					}
				}
				if (BuildPieChartData.size() == num) {
					BuildPieChartData.put("现金", 100);
				}
			}
		} else {
			BuildPieChartData.put("现金", 100);
		}
		setPieChartData(BuildPieChartData);
	}

	// 把Data放在ChartData
	public void setPieChartData(Map<String, Integer> params) {
		try {
			List<com.github.mikephil.charting.data.Entry> yVals1 = new ArrayList<com.github.mikephil.charting.data.Entry>();
			ArrayList<String> xVals = new ArrayList<>();
			int percent = 0;
			int i = 0;
			for (String key : params.keySet()) {
				if (Float.valueOf(params.get(key)) != 0.0) {
                    Float vy=Float.valueOf(params.get(key));
					yVals1.add(new com.github.mikephil.charting.data.Entry(vy, i));
					if (key.length() >= 5) {
						String temp = key.substring(0,4) + "...";
						xVals.add(temp+"("+vy+")");
					} else {
						xVals.add(key+"("+vy+")");
					}
					i += 0;
					percent += params.get(key);
				}
			}
			if (percent < 100 && percent > 0) {
				yVals1.add(new com.github.mikephil.charting.data.Entry(Float.valueOf(100 - percent), i + 1));
				xVals.add("现金");
			}

			PieDataSet dataSet = new PieDataSet(yVals1, "");
            dataSet.setValueTextColor(Color.parseColor("#00000000"));
			dataSet.setSliceSpace(3f);
			dataSet.setSelectionShift(5f);
			ArrayList<Integer> colors = new ArrayList<Integer>();
			for (int c : ColorTemplate.JOYFUL_COLORS)
				colors.add(c);
			for (int c : ColorTemplate.PASTEL_COLORS)
				colors.add(c);
			for (int c : ColorTemplate.VORDIPLOM_COLORS)
				colors.add(c);
			for (int c : ColorTemplate.LIBERTY_COLORS)
				colors.add(c);
			for (int c : ColorTemplate.COLORFUL_COLORS)
				colors.add(c);
			colors.add(ColorTemplate.getHoloBlue());
			dataSet.setColors(colors);
			PieData data = new PieData(xVals, dataSet);
			data.setValueTextSize(12f);
			pieChart.setData(data);
			pieChart.highlightValues(null);
			pieChart.invalidate();
		} catch (Exception e) {
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tljr_zh_changepercent_piechart:
			if(listGu.size()>0){
				Intent intent = new Intent(getActivity(),TljrTiaoCangView.class);
				intent.putExtra("zulist", (Serializable) listGu);
				intent.putExtra("zuid",getActivity().getIntent().getStringExtra("zuid"));
				intent.putExtra("zuName",((TljrChangePercent)getActivity()).ZuName.getText());
				intent.putExtra("action",1);
				getActivity().startActivity(intent);
			}else{
				Toast.makeText(getActivity(),"没有股票可以调仓",Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	class TiaoCangUpdatePieChart extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (pieChart != null) {
				@SuppressWarnings("unchecked")
				List<Map<String, Integer>> param = (ArrayList<Map<String, Integer>>) intent.getSerializableExtra("params");
				Map<String, Integer> replace = param.get(0);
				for (String key : replace.keySet()) {
					for (int i = 0; i < listGu.size(); i++) {
						if (listGu.get(i).getCode().equals(key)) {
							listGu.get(i).setPercent(replace.get(key));
						}
					}
				}
				BuildPieChartData.clear();
				for (OneGu gu : listGu) {
					BuildPieChartData.put(gu.getName(), gu.getPercent());
				}
				setPieChartData(BuildPieChartData);
			}
		}
	}

	class TiaoCangUpdateDelete extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String code = intent.getStringExtra("code");
			String market=intent.getStringExtra("market");
			for (int i = 0; i < listGu.size(); i++) {
				if (listGu.get(i).getKey().equals(market+code)) {
					listGu.remove(i);
				}
			}
			BuildPieChartData.clear();
			for (OneGu gu : listGu) {
				BuildPieChartData.put(gu.getName(), gu.getPercent());
			}
			
			if(BuildPieChartData.size()<=0){
				BuildPieChartData.put("现金",100);
				pieChart.setCenterText("");
			}
			
			setPieChartData(BuildPieChartData);
		}
	}

	class UpdateZuId extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			getZuId(getActivity().getIntent().getStringExtra("key"));
		}
	}
	
	final Handler mHandler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 1:
				break;
			}
		};
	};
	
}
