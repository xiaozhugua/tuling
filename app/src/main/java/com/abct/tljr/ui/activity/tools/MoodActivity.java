package com.abct.tljr.ui.activity.tools;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.kline.MyValueFormatter;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * @author xbw
 * @version 创建时间：2015年10月9日 下午5:18:54
 * 首页的新闻热度情绪
 */
public class MoodActivity extends BaseActivity {
	private EditText editText;
	private LineChart moodChart;
	private ValueFormatter custom = new MyValueFormatter(2);

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_mood);
		editText = (EditText) findViewById(R.id.tljr_tools_mood_et);
		moodChart = (LineChart) findViewById(R.id.tljr_tools_mood);
		moodChart.setNoDataText("");
		moodChart.setLayoutParams(new LinearLayout.LayoutParams(Util.WIDTH, Util.HEIGHT * 1 / 2));
		findViewById(R.id.tljr_tools_mood_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchCode(editText.getText().toString());
			}
		});
		findViewById(R.id.pe_img_jzjl_fanhui).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	};

	private void searchCode(String code) {
		Realm realm = Realm.getDefaultInstance();
		if (code.matches("[+-]?[0-9]+[0-9]*(\\.[0-9]+)?")) {
			OneGu gu = realm.where(OneGu.class).equalTo("code", code).findFirst();
			realm.close();
			if (gu != null) {
				LogUtil.e("searchCode", "gu != null  1"+(gu != null));
				initMoodData(code);
				return;
			}
			// } else if (input.matches("[a-zA-Z]+")) {
			// type = 2;
		} else if (code.matches("[\\u4e00-\\u9fa5]+")) {
			OneGu gu = realm.where(OneGu.class).equalTo("name", code).findFirst();
			realm.close();
			if (gu != null) {
				LogUtil.e("searchCode", "gu != null  2"+(gu != null));
				initMoodData(gu.getCode());
				return;
			}
		}
		showToast("请输入正确的股票代码/名称");
	}

	private void initMoodData(final String code) {
		editText.setText("");
		ProgressDlgUtil.showProgressDlg("", this);
	//	LogUtil.e("initMoodData", "url=="+UrlUtil.URL_mood+"?"+"code=" + code);
		String   url="http://news.tuling.me/QhWebNewsServer/DailyNewsAll?code="+code;
		NetUtil.sendPost(url, new  NetResult() {
			
			@Override
			public void result(final String msg) {
				ProgressDlgUtil.stopProgressDlg();
				post(new Runnable() {

					@Override
					public void run() {
						
						Realm realm = Realm.getDefaultInstance();
						OneGu gu = realm.where(OneGu.class).equalTo("code", code).findFirst();
						realm.close();
						if (gu != null) {
							initLrUI(msg, moodChart, gu.getName());
						}
					}
				});
			}
		});
		/*HttpRequest.sendPost(UrlUtil.URL_mood, "code=" + code, new HttpRevMsg() {

			@Override
			public void revMsg(final String msg) {
				LogUtil.e("initMoodData", msg);
				ProgressDlgUtil.stopProgressDlg();
				post(new Runnable() {

					@Override
					public void run() {
						
						Realm realm = Realm.getDefaultInstance();
						OneGu gu = realm.where(OneGu.class).equalTo("code", code).findFirst();
						realm.close();
						if (gu != null) {
							initLrUI(msg, moodChart, gu.getName());
						}
					}
				});
			}
		});*/
	}

	private void initLrUI(String msg, LineChart mChart, String name) {
		mChart.setDescription("");
		TextView    textView=              (TextView)findViewById(R.id.news_hot_name_tv);
		textView.setText(name);
		mChart.setNoDataText("没有数据");
		mChart.setTouchEnabled(false);
		mChart.setDrawGridBackground(false);
		XAxis xAxis = mChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setTextSize(8f);
		xAxis.setTextColor(Color.rgb(150, 150, 150));

		YAxis leftAxis = mChart.getAxisLeft();
		leftAxis.setValueFormatter(custom);
		leftAxis.setStartAtZero(false);
		leftAxis.setTextColor(Color.rgb(150, 150, 150));
		// leftAxis.setGridColor(Color.rgb(253, 246, 238));
		YAxis rightAxis = mChart.getAxisRight();
		rightAxis.setValueFormatter(custom);
		rightAxis.setStartAtZero(false);
		rightAxis.setTextColor(Color.rgb(150, 150, 150));
		ArrayList<String> xVals = new ArrayList<String>();
		ArrayList<Entry> vals1 = new ArrayList<Entry>();
		ArrayList<Entry> vals2 = new ArrayList<Entry>();
		try {
			JSONArray array = new JSONArray(msg);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(array.length() - i - 1);
				xVals.add(object.optString("newsPublishDate"));
				String   heatIndexString=           object.optString("heatIndex");
				String   sentimentIndexString=          object.optString("sentimentIndex");
				Double.parseDouble(heatIndexString);
				//vals1.add(new Entry((float) object.getDouble("heatIndex"), i));
				//vals2.add(new Entry((float) object.getDouble("sentimentIndex"), i));
				//LogUtil.e("initLrUI", ""+);
				vals1.add(new Entry((float) Double.parseDouble(heatIndexString), i));
				vals2.add(new Entry((float) Double.parseDouble(sentimentIndexString), i));
				
				
			}
			LineDataSet set1 = new LineDataSet(vals1, "新闻热度(左轴)");
			set1.setAxisDependency(AxisDependency.LEFT);
			set1.setDrawCircles(false);
			set1.setLineWidth(2f);
			set1.setDrawCubic(true);
			set1.setColor(Color.rgb(251, 109, 108));

			LineDataSet set2 = new LineDataSet(vals2, "新闻情绪(右轴)");
			set2.setAxisDependency(AxisDependency.RIGHT);
			set2.setDrawCircles(false);
			set2.setLineWidth(2f);
			set1.setDrawCubic(true);
			set2.setColor(Color.parseColor("#46943a"));

			ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
			dataSets.add(set1);
			dataSets.add(set2);
			LineData data = new LineData(xVals, dataSets);
			data.setValueTextSize(9f);
			data.setDrawValues(false);
			mChart.setData(data);
			mChart.invalidate();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
