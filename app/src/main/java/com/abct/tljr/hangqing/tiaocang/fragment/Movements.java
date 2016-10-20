package com.abct.tljr.hangqing.tiaocang.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.linechar.AllGuAll;
import com.abct.tljr.hangqing.linechar.AllGuThreeMonth;
import com.abct.tljr.hangqing.tiaocang.TljrChangePercent;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Movements extends Fragment implements OnClickListener {

	private View viewMovements;
	private LineChart ThreeLineChart = null;
	private LineChart AllLineChart = null;
	private TextView threeText = null;
	private TextView allText = null;
	private RelativeLayout threeRelative = null;
	private RelativeLayout allRelative = null;
	private AllGuThreeMonth allGuThreeMonth = null;
	private AllGuAll allGuAll=null;
	private SimpleDateFormat formats;
	private List<String> xVals = null;
	private List<String> xVals2 = null;
	private LineData data;
	private LineData data1;
	private int status = 0;
	private String jingzhiliebiao = null;

	@SuppressLint("SimpleDateFormat")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		viewMovements = inflater.inflate(R.layout.tljr_zh_movements,container,false);
		formats = new SimpleDateFormat("MM-dd");
		xVals = new ArrayList<String>();
		xVals2 = new ArrayList<String>();
		initLineChart();

		return viewMovements;
	}

	public void initLineChart() {
		ThreeLineChart = (LineChart) viewMovements.findViewById(R.id.threelinechart);
		AllLineChart = (LineChart) viewMovements.findViewById(R.id.alllinechart);
		threeText = (TextView) viewMovements.findViewById(R.id.tljr_zh_changepercent_threetext);
		allText = (TextView) viewMovements.findViewById(R.id.tljr_zh_changepercent_alltext);
		threeRelative = (RelativeLayout) viewMovements.findViewById(R.id.tljr_zh_changepercent_threemonth);
		allRelative = (RelativeLayout) viewMovements.findViewById(R.id.tljr_zh_changepercent_allmonth);

		threeRelative.setOnClickListener(this);
		allRelative.setOnClickListener(this);

		jingzhiliebiao = ((TljrChangePercent)getActivity()).jingzhiliebiao;
		getThreeMonthGuShuoYiLu(jingzhiliebiao);
	}

	// 得到三个月的数据
	public void getThreeMonthGuShuoYiLu(String TempArray) {
		try {
			if (TempArray != null) {
				// 处理三个月的数据
				DealThreeData(TempArray);
			} else {

			}
		} catch (Exception e) {
		}
	}

	// 处理三个月的数据
	public void DealThreeData(String TempArray) {
		try {
			List<Entry> yValsJingzhi = new ArrayList<Entry>();
			List<Entry> yValsHushen = new ArrayList<Entry>();
			LineDataSet hushen = null;
			// 没有数据
			if (TempArray.equals("[]") || TempArray == null) {
				yValsJingzhi.add(new Entry((float) 0, 0));
				yValsHushen.add(new Entry((float) 0, 0));
				xVals.add(formats.format(new Date()));
			} else {
				// 有数据的时候
				JSONArray array = new JSONArray(TempArray);
				JSONObject object = null;
				int day = 0;
				if (array.length() > 90) {
					day = 90;
				} else if (array.length() <= 90 && array.length() > 0) {
					day = array.length();
				}
				double jingzhi = 0;
				double hs = 0;
				for (int i = 0; i < day; i++) {
					object = array.getJSONObject(i);
					String time = formats.format(object.getLong("time"));
					jingzhi = object.getDouble("jingzhi");
					hs = object.getDouble("hs");
					yValsJingzhi.add(new Entry((float) (jingzhi - 1), i));
					yValsHushen.add(new Entry((float) (hs), i));
					xVals.add(time);
				}
			}
			// 为沪深画线准备数据
			hushen = new LineDataSet(yValsHushen, "上证指数");
			hushen.setColor(Color.argb(255, 25, 98, 175));
			hushen.setCircleColor(Color.argb(255, 25, 98, 175));
			hushen.setLineWidth(2f);
			hushen.setCircleSize(2f);
			hushen.setFillAlpha(65);
			hushen.setDrawValues(false);
			hushen.setHighLightColor(Color.rgb(244, 117, 117));
			hushen.setDrawCircleHole(false);
			hushen.setDrawCircles(false);
			// 为我的组合画线准备数据
			LineDataSet zhuhe = new LineDataSet(yValsJingzhi, "我的组合");
			zhuhe.setColor(Color.argb(255, 177, 45, 85));
			zhuhe.setCircleColor(Color.argb(255, 177, 45, 85));
			zhuhe.setLineWidth(2f);
			zhuhe.setCircleSize(2f);
			zhuhe.setDrawValues(false);
			zhuhe.setFillAlpha(65);
			zhuhe.setHighLightColor(Color.rgb(244, 117, 117));
			zhuhe.setDrawCircleHole(false);
			zhuhe.setDrawCircles(false);
			data = new LineData(xVals,zhuhe);
			data.setValueTextColor(Color.BLACK);
			if (hushen != null)
				data.addDataSet(hushen);
			Message message = new Message();
			message.what = 1;
			mHandler.sendMessage(message);
		} catch (Exception e) {
			showTip("三月份数据处理出错");
		}
	}

	public void showTip(String tip) {
		Message message = Message.obtain();
		message.what = 3;
		Bundle bundle = new Bundle();
		bundle.putString("msg", tip);
		message.setData(bundle);
		mHandler.sendMessage(message);
	}

	@SuppressWarnings("deprecation")
	public void btnchange(int i) {
		if (i == 0) {
			// 点击全部
			threeText.setTextColor(getActivity().getResources().getColor(R.color.black));
			allText.setTextColor(getActivity().getResources().getColor(R.color.white));
			threeRelative.setBackground(null);
			allRelative.setBackgroundResource(R.drawable.img_dangqianxuanzhong);
			ThreeLineChart.setVisibility(View.GONE);
			AllLineChart.setVisibility(View.VISIBLE);
		} else {
			// 点击三个月
			threeText.setTextColor(getActivity().getResources().getColor(R.color.white));
			allText.setTextColor(getActivity().getResources().getColor(R.color.black));
			threeRelative.setBackgroundResource(R.drawable.img_dangqianxuanzhong);
			allRelative.setBackground(null);
			ThreeLineChart.setVisibility(View.VISIBLE);
			AllLineChart.setVisibility(View.GONE);
		}
	}

	// 得到全部的数据
	public void getAllMonthGuShuoYiLu() {
		if (jingzhiliebiao != null) {
			DealAllData(jingzhiliebiao);
		} else {

		}
	}

	// 处理所有数据
	public void DealAllData(String tempArray) {
		try {
			List<Entry> yValsJingzhi = new ArrayList<Entry>();
			List<Entry> yValsHushen = new ArrayList<Entry>();

			LineDataSet hushen = null;

			if (tempArray.equals("[]") || tempArray == null) {
				yValsJingzhi.add(new Entry((float) 0, 0));
				yValsHushen.add(new Entry((float) 0, 0));
				xVals2.add(formats.format(new Date()));
				showTip("暂时没有收益率数据");
			} else {
				JSONArray array = new JSONArray(tempArray);
				JSONObject object = null;

				for (int i = 0; i < array.length(); i++) {
					object = array.getJSONObject(i);
					String time = formats.format(object.getLong("time"));
					double jingzhi = object.getDouble("jingzhi");
					double hs = object.getDouble("hs");
					yValsJingzhi.add(new Entry(
							(Float.valueOf(jingzhi + "") - 1), i));
					yValsHushen.add(new Entry(Float.valueOf((hs) + ""), i));
					xVals2.add(time);
				}
			}

			// 为沪深画线准备数据
			hushen = new LineDataSet(yValsHushen, "上证指数");
			hushen.setColor(Color.argb(255, 25, 98, 175));
			hushen.setCircleColor(Color.argb(255, 25, 98, 175));
			hushen.setLineWidth(2f);
			hushen.setCircleSize(2f);
			hushen.setFillAlpha(65);
			hushen.setDrawValues(false);
			hushen.setHighLightColor(Color.rgb(244, 117, 117));
			hushen.setDrawCircleHole(false);
			hushen.setDrawCircles(false);

			// 为我的组合画线准备数据
			LineDataSet zhuhe = new LineDataSet(yValsJingzhi, "我的组合");
			zhuhe.setColor(Color.argb(255, 177, 45, 85));
			zhuhe.setCircleColor(Color.argb(255, 177, 45, 85));
			zhuhe.setLineWidth(2f);
			zhuhe.setCircleSize(2f);
			zhuhe.setFillAlpha(65);
			zhuhe.setDrawValues(false);
			zhuhe.setHighLightColor(Color.rgb(244, 117, 117));
			zhuhe.setDrawCircleHole(false);
			zhuhe.setDrawCircles(false);

			data1 = new LineData(xVals2, zhuhe);
			data1.setValueTextColor(Color.BLACK);
			if (hushen != null)
				data1.addDataSet(hushen);

			Message message = new Message();
			message.what = 2;
			mHandler.sendMessage(message);

		} catch (Exception e) {
			showTip("全部的数据处理出错");
		}
	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg){
			switch (msg.what) {
			case 1:
				if (data.getDataSetCount() != 0 && xVals.size() != 0) {
					allGuThreeMonth = new AllGuThreeMonth((LineChart) getActivity().findViewById(R.id.threelinechart), data);
					allGuThreeMonth.ShowView();
				} else {
					showTip("没有三月份的数据");
				}
				break;
			case 2:
				 if (data1.getDataSetCount() != 0 && xVals2.size() != 0) {
		               allGuAll = new AllGuAll((LineChart) getActivity().findViewById(R.id.alllinechart), data1);
		               allGuAll.ShowView();
		           } else {
		               showTip("没有全部的数据");
		           }
				break;
			case 3:
				Toast.makeText(getContext(), msg.getData().getString("msg"),Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tljr_zh_changepercent_threemonth:
			btnchange(1);
			break;
		case R.id.tljr_zh_changepercent_allmonth:
			if (status == 0) {
				getAllMonthGuShuoYiLu();
				status+=1;
			}
			btnchange(0);
			break;
		}
	}

}
