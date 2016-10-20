package com.abct.tljr.kline;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.kline.SeekBarPressure.OnSeekBarChangeListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class KLineGraphActivity extends BaseActivity implements
		OnSeekBarChangeListener {
	private TextView txt_kname;
	private KView kView;
	private YueKView yueKView;
	private WeekKView weekKView;
	private LineView lineView;
	private FiveLineView fiveLineView;
	private BarView barView;
	private SeekBarPressure mSeekBarX;
	private String code, name, market, key;
	private String TAG = "KLineGraphActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.tljr_activity_kline);
		initData();
		initView();
		post(runnable);
	}

	private void initData() {
		Bundle bundle = this.getIntent().getExtras();
		name = bundle.getString("name");
		code = bundle.getString("code");
		key = bundle.getString("key");
		market = bundle.getString("market");
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.tljr_img_kfanhui).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						KLineGraphActivity.this.finish();
					}
				});
		txt_kname = (TextView) findViewById(R.id.tljr_txt_kname);
		txt_kname.setText(name + "(" + code + ")");
		mSeekBarX = (SeekBarPressure) findViewById(R.id.tljr_seekBar1);
		mSeekBarX.setOnSeekBarChangeListener(this);
		barView = new BarView((BarChart) findViewById(R.id.tljr_chart2));
		barView.getChart().setScaleYEnabled(false);
		// barView.getChart().setOnChartValueSelectedListener(this);
		barView.getChart().getAxisRight().setEnabled(false);
		barView.getChart().getXAxis().setEnabled(false);
		barView.getChart().setDrawValueAboveBar(false);
		if (key.equals("minute")) {
			lineView = new LineView(handler,
					(LineChart) findViewById(R.id.tljr_chart3), market, code);
			lineView.getChart().setVisibility(View.VISIBLE);
			lineView.setBarChart(barView.getChart());
			lineView.getChart().getAxisRight().setEnabled(false);
			lineView.initKData();
			MyMarkerView m = new MyMarkerView(this, R.layout.tljr_marker_view);
			lineView.getChart().setMarkerView(m);
			MyMarkerView.datas = lineView.getDatas();
			lineView.getChart().setOtherChart(barView.getChart());
			barView.getChart().setOtherChart(lineView.getChart());
			lineView.getChart().getXAxis().setAdjustXLabels(true);
		} else if (key.equals("diary")) {
			kView = new KView(handler,
					(CandleStickChart) findViewById(R.id.tljr_chart1), market,
					code);
			kView.getChart().setVisibility(View.VISIBLE);
			kView.getChart().setScaleYEnabled(false);
			kView.getChart().getAxisRight().setEnabled(false);
			// kView.getChart().setDoubleTapToZoomEnabled(false);
			kView.setBarChart(barView.getChart());
			kView.initKData();
			MyMarkerView m = new MyMarkerView(this, R.layout.tljr_marker_view);
			kView.getChart().setMarkerView(m);
			MyMarkerView.datas = kView.getDatas();
			kView.getChart().setOtherChart(barView.getChart());
			barView.getChart().setOtherChart(kView.getChart());
		} else if (key.equals("mouth")) {
			yueKView = new YueKView(handler,
					(CandleStickChart) findViewById(R.id.tljr_chart4), market,
					code);
			yueKView.getChart().setVisibility(View.VISIBLE);
			yueKView.getChart().setScaleYEnabled(false);
			yueKView.getChart().getAxisRight().setEnabled(false);
			// kView.getChart().setDoubleTapToZoomEnabled(false);
			yueKView.setBarChart(barView.getChart());
			yueKView.initKData();
			MyMarkerView m = new MyMarkerView(this, R.layout.tljr_marker_view);
			yueKView.getChart().setMarkerView(m);
			MyMarkerView.datas = yueKView.getDatas();
			yueKView.getChart().setOtherChart(barView.getChart());
			barView.getChart().setOtherChart(yueKView.getChart());
		} else if (key.equals("week")) {
			weekKView = new WeekKView(handler,
					(CandleStickChart) findViewById(R.id.tljr_chart5), market,
					code);
			weekKView.getChart().setVisibility(View.VISIBLE);
			weekKView.getChart().setScaleYEnabled(false);
			weekKView.getChart().getAxisRight().setEnabled(false);
			// kView.getChart().setDoubleTapToZoomEnabled(false);
			weekKView.setBarChart(barView.getChart());
			weekKView.initKData();
			MyMarkerView m = new MyMarkerView(this, R.layout.tljr_marker_view);
			weekKView.getChart().setMarkerView(m);
			MyMarkerView.datas = weekKView.getDatas();
			weekKView.getChart().setOtherChart(barView.getChart());
			barView.getChart().setOtherChart(weekKView.getChart());
		} else {
			fiveLineView = new FiveLineView(handler,
					(LineChart) findViewById(R.id.tljr_chart6), market, code);
			fiveLineView.getChart().setVisibility(View.VISIBLE);
			fiveLineView.setBarChart(barView.getChart());
			fiveLineView.getChart().getAxisRight().setEnabled(false);
			fiveLineView.initKData();
			MyMarkerView m = new MyMarkerView(this, R.layout.tljr_marker_view);
			fiveLineView.getChart().setMarkerView(m);
			MyMarkerView.datas = fiveLineView.getDatas();
			fiveLineView.getChart().setOtherChart(barView.getChart());
			barView.getChart().setOtherChart(fiveLineView.getChart());
		}
	}

	@Override
	public void onProgressChanged(SeekBarPressure seekBar, double progressLow,
			double progressHigh) {
		if (key.equals("minute")) {
			lineView.initLineForNum((int) (progressLow / 100 * lineView
					.getDatas().size()), (int) (progressHigh / 100 * lineView
					.getDatas().size()));
			MyMarkerView.start = (int) (progressLow / 100 * lineView.getDatas()
					.size());
		} else if (key.equals("diary")) {
			kView.initForNum(
					(int) (progressLow / 100 * kView.getDatas().size()),
					(int) (progressHigh / 100 * kView.getDatas().size()));
			MyMarkerView.start = (int) (progressLow / 100 * kView.getDatas()
					.size());
		} else if (key.equals("mouth")) {
			yueKView.initForNum((int) (progressLow / 100 * yueKView.getDatas()
					.size()), (int) (progressHigh / 100 * yueKView.getDatas()
					.size()));
			MyMarkerView.start = (int) (progressLow / 100 * yueKView.getDatas()
					.size());
		} else if (key.equals("week")) {
			weekKView.initForNum((int) (progressLow / 100 * weekKView
					.getDatas().size()), (int) (progressHigh / 100 * weekKView
					.getDatas().size()));
			MyMarkerView.start = (int) (progressLow / 100 * weekKView
					.getDatas().size());
		} else {
			fiveLineView
					.initLineForNum((int) (progressLow / 100 * fiveLineView
							.getDatas().size()),
							(int) (progressHigh / 100 * fiveLineView.getDatas()
									.size()));
			MyMarkerView.start = (int) (progressLow / 100 * fiveLineView
					.getDatas().size());
		}
	}

	protected void onResume() {
		super.onResume();
		run = true;
	};

	protected void onPause() {
		super.onPause();
		run = false;
	};

	boolean run = true;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (run) {
				// 要做的事情
				postDelayed(this, 30000);
				if (!key.equals("minute")) {
					return;
				}
				if (lineView != null)
					lineView.ShiShi1();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.candle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (kView != null)
			kView.optionsItemSelected(item);
		if (lineView != null)
			lineView.optionsItemSelected(item);
		if (yueKView != null)
			yueKView.optionsItemSelected(item);
		if (weekKView != null)
			weekKView.optionsItemSelected(item);
		barView.optionsItemSelected(item);
		return true;
	}

	// @Override
	// public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
	// // TODO Auto-generated method stub
	// // if (e == null)
	// // return;
	// // RectF bounds = barView.getChart().getBarBounds((BarEntry) e);
	// // PointF position = barView.getChart()
	// // .getPosition(e, AxisDependency.LEFT);
	// // Log.i("bounds", bounds.toString());
	// // Log.i("position", position.toString());
	// }

	// @Override
	// public void onNothingSelected() {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void onProgressBefore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressAfter() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event != null
				&& event.getRepeatCount() == 0) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
