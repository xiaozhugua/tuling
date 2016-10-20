package com.abct.tljr.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.kline.MyValueFormatter;
import com.abct.tljr.utils.TulingDataContant;
import com.abct.tljr.utils.ViewUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.listener.NetResult;
import com.qh.common.ui.widget.ProgressDlgUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 从main的fragment点击图灵数据部分前几个跳到这里，单独展示图表
 */
public class TulingChartActivity extends BaseActivity implements OnClickListener {

    /**
     * 装下图表的容器
     */
    private FrameLayout mLayout;
    /**
     * 图表的名字
     */
    private TextView    mName;
    private Intent      mIntent;
    private Chart       chart0, chart1, chart2, chart3, chart4, chart5, chart6;
    private int screenWith;
    private int screenHight;
    private View             tljr_data_fanhui = null;
    private SimpleDateFormat format           = new SimpleDateFormat("MM.dd");
    private LinearLayout noDataLinear;
    private TextView     noDataTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tulingchart);
        //LogUtil.i("momo", "跳跃到TulingChartActivity");
        tljr_data_fanhui = (View) findViewById(R.id.tljr_data_fanhui);
        mLayout = (FrameLayout) findViewById(R.id.layout_showChart);//
        mName = (TextView) findViewById(R.id.layout_chartName);// 图表的名字

        screenWith = ViewUtil.getScreenWidth(this);// 当前屏幕宽度
        screenHight = ViewUtil.getScreenHeight(this);// 获取屏幕高度
        tljr_data_fanhui.setOnClickListener(this);
        mIntent = getIntent();
        int chartNumber = mIntent.getIntExtra("chartNumber", -1);
        noDataLinear = (LinearLayout) findViewById(R.id.activity_tulingchart_no_data_ll);
        noDataTv = (TextView) findViewById(R.id.tulingchart_nodata_tv);


        switchNumber(chartNumber);
    }


    /**
     * 没有数据的UI视图
     */
    private void noDataUi() {

        noDataLinear.setVisibility(View.VISIBLE);
        noDataTv.setText("没有数据显示");
        mLayout.setVisibility(View.GONE);

    }

    /**
     * 加载中的UI视图
     */
    private void jiazaiUi() {
        noDataLinear.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.GONE);
        noDataTv.setText("正在加载中······");

    }


    /**
     * 成功获取数据显示
     */
    private void successUi() {
        noDataLinear.setVisibility(View.GONE);
        mLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示土司提示
     */
    private void showMessage(String message) {
        Toast.makeText(TulingChartActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 根据号码来区别显示图表
     */
    private void switchNumber(int chartNumber) {
        // TODO Auto-generated method stub
        if (chartNumber == -1) {
            showMessage("传入的数据有问题");
            return;
        }

        switch (chartNumber) {
            case 0:

                initChart0();
                break;
            case 1:
                initChart1();
                break;
            case 2:
                initChart2();
                break;
            case 3:
                initChart3();
                break;
            case 4:
                initChart4();
                break;
            case 5:
                initChart5();
                break;
            case 6:
                initChart6();
                break;

            default:
                break;
        }

    }

    private ValueFormatter custom  = new MyValueFormatter(4);
    private ValueFormatter custom1 = new MyValueFormatter(1);
    private ValueFormatter custom2 = new MyValueFormatter(5);

    private void initChart6() {
        // TODO Auto-generated method stub

        chart6 = new LineChart(this);
        chart6.setLayoutParams(new LayoutParams(screenWith, screenHight * 1 / 2));
        initGzData();
    }

    private void initChart5() {
        // TODO Auto-generated method stub

        chart5 = new LineChart(this);
        chart5.setLayoutParams(new LayoutParams(screenWith, screenHight * 1 / 2));
        initPjData();
    }

    private void initChart4() {
        // TODO Auto-generated method stub

        chart4 = new LineChart(this);
        chart4.setLayoutParams(new LayoutParams(screenWith, screenHight * 1 / 2));
        initSylData();
    }

    private void initChart3() {
        // TODO Auto-generated method stub

      // chart3 = new BarChart(this);
        chart3=new CombinedChart(this);
        chart3.setLayoutParams(new LayoutParams(screenWith, screenHight * 1 / 2));

        initKHData();
    }

    private void initChart2() {
        // TODO Auto-generated method stub

        chart2 = new LineChart(this);
        chart2.setLayoutParams(new LayoutParams(screenWith, screenHight * 1 / 2));
        initQhData();
    }

    private void initChart1() {
        // TODO Auto-generated method stub

        chart1 = new BarChart(this);
        chart1.setLayoutParams(new LayoutParams(screenWith, screenHight * 1 / 2));
        initYzData();
    }

    private void initChart0() {
        // TODO Auto-generated method stub
        LogUtil.i("momo", "initChart0()");
        chart0 = new LineChart(this);
        chart0.setLayoutParams(new LayoutParams(screenWith, screenHight * 1 / 2));
        initLrData();

    }

    private String[]  names = {"两融数据", "银证转账信息", "期权成交量", "新增开户数", "市盈率", "票据信息", "银行间固定利率国债收益率曲线(年)"};
    private Integer[] bjs   = {R.drawable.img_huangtiao, R.drawable.img_lvtiao, R.drawable.img_hongtiao,
            R.drawable.img_lantiao, R.drawable.img_huangtiao, R.drawable.img_lvtiao, R.drawable.img_hongtiao};

    /**
     * 把图表添加到容器中
     */
    private void addChart(Chart chart, int index) {
        mName.setText(names[index]);
        // ((ImageView)
        // title.findViewById(R.id.tljr_view_title_img)).setBackgroundResource(bjs[index]);
        // 添加头标签
        // layout.addView(title);
        // 添加图标
        /*
         * if (chart.getParent() != null) { ((LinearLayout)
		 * chart.getParent()).removeView(chart); }
		 */
        mLayout.removeAllViews();// 移除所有的视图控件
        mLayout.addView(chart);
    }

    private void showDialog() {

        ProgressDlgUtil.showProgressDlg("", this);

    }

    private void dissDialog() {
        ProgressDlgUtil.stopProgressDlg();
    }

    /**
     * 加载固定利率收益曲线的数据
     */
    private void initGzData() {
        showDialog();
        jiazaiUi();
        // 新接口
        NetUtil.sendPost(TulingDataContant.getUrl("d_ny"), new NetResult() {

            @Override
            public void result(String arg0) {
                // TODO Auto-generated method stub
                dissDialog();
                successUi();
                if (arg0 == null || "".equals(arg0)) {
                    showErrorDialog(6);
                } else {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(arg0);

                        if (json.optInt("status") != 1) {
                            showMessage("接收数据异常");
                            noDataUi();
                            return;
                        }
                        // TODO Auto-generated method stub
                        initGzUI(json.optJSONArray("result").toString(), (LineChart) chart6);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    /**
     * 固定收益率曲线
     */
    private void initGzUI(String msg, LineChart mChart) {
        addChart(mChart, 6);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.resetTracking();
        mChart.setNoDataText("没有数据");


        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.rgb(150, 150, 150));
        YAxis yLabels = mChart.getAxisLeft();
        yLabels.setValueFormatter(custom2);
        yLabels.setTextColor(Color.rgb(150, 150, 150));
        yLabels.setStartAtZero(false);
        mChart.getAxisRight().setEnabled(false);
        ArrayList<String> xVals = new ArrayList<String>();
        try {
            JSONArray array = new JSONArray(msg);
            ArrayList<Entry> values0 = new ArrayList<Entry>();
            for (int i = array.length() - 1; i > 0; i--) {
                JSONObject object = array.getJSONObject(i);
                xVals.add(format.format(new Date(object.optLong("timestamp"))));
                values0.add(new Entry((float) array.getJSONObject(i).getDouble("yield") / 100, array.length() - i - 1));
            }
            LineDataSet d0 = new LineDataSet(values0, "银行间固定利率国债收益率曲线(年)");
            d0.setColor(Color.parseColor("#0e5eab"));
            d0.setLineWidth(2);
            d0.setDrawCircles(false);
            LineData data = new LineData(xVals, d0);
            data.setDrawValues(false);
            mChart.setData(data);
            mChart.animateX(1000);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 加载票据信息的数据
     */
    private void initPjData() {
        showDialog();
        jiazaiUi();
        //http://120.24.235.202:8080/ResearchReportService/ReceiptService?msg_types=1
        //http://news.tuling.me/ResearchReportService/ReceiptService
        //	LogUtil.e("initPjData", "url=="+UrlUtil.URL_pj+"?msg_types=1");
        //	String   url="http://news.tuling.me/ResearchReportService/ReceiptService";
        NetUtil.sendPost(UrlUtil.URL_pj, "msg_types=1", new NetResult() {
            //	NetUtil.sendPost(url, "msg_types=1", new NetResult() {
            @Override
            public void result(final String msg) {
                LogUtil.e("pi123", "票据信息" + msg);
                post(new Runnable() {

                    @Override
                    public void run() {
                        dissDialog();
                        successUi();
                        if (msg == null || "".equals(msg)) {
                            showErrorDialog(5);
                        } else {

                            // TODO Auto-generated method stub
                            initPjUI(msg, (LineChart) chart5);
                            // initGzData(completes);
                        }
                    }
                });
            }
        });


    }

    /**
     * 票据信息
     */
    private void initPjUI(String msg, LineChart mChart) {

        addChart(mChart, 5);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.resetTracking();
        mChart.setNoDataText("没有数据");
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.rgb(150, 150, 150));
        YAxis yLabels = mChart.getAxisLeft();
        yLabels.setValueFormatter(custom2);
        yLabels.setTextColor(Color.rgb(150, 150, 150));
        yLabels.setStartAtZero(false);
        mChart.getAxisRight().setEnabled(false);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        try {
            JSONArray array = new JSONArray(msg);
            ArrayList<Entry> values0 = new ArrayList<Entry>();
            ArrayList<Entry> values1 = new ArrayList<Entry>();
            ArrayList<Entry> values2 = new ArrayList<Entry>();
            ArrayList<Entry> values3 = new ArrayList<Entry>();
            int m = 0;
            for (int i = array.length() - 1; i > 0; i -= 8) {
                JSONObject object = array.getJSONObject(i);
                xVals.add(format.format(new Date(object.optLong("timestamp"))));
                values0.add(new Entry((float) array.getJSONObject(i - 7).getDouble("averageInterestRate") / 100, m));
                values1.add(new Entry((float) array.getJSONObject(i - 6).getDouble("averageInterestRate") / 100, m));
                values2.add(new Entry((float) array.getJSONObject(i - 3).getDouble("averageInterestRate") / 100, m));
                values3.add(new Entry((float) array.getJSONObject(i - 2).getDouble("averageInterestRate") / 100, m));
                m++;
            }
            LineDataSet d0 = new LineDataSet(values0, "电子票据买入");
            d0.setColor(Color.parseColor("#0e5eab"));
            d0.setLineWidth(2);
            d0.setDrawCircles(false);
            LineDataSet d1 = new LineDataSet(values1, "电子票据卖出");
            d1.setColor(Color.parseColor("#e0422f"));
            d1.setLineWidth(2);
            d1.setDrawCircles(false);
            LineDataSet d2 = new LineDataSet(values2, "纸质票据买入");
            d2.setColor(Color.parseColor("#46943a"));
            d2.setLineWidth(2);
            d2.setDrawCircles(false);
            LineDataSet d3 = new LineDataSet(values3, "纸质票据卖出");
            d3.setColor(Color.parseColor("#e7ba10"));
            d3.setLineWidth(2);
            d3.setDrawCircles(false);
            dataSets.add(d0);
            dataSets.add(d1);
            dataSets.add(d2);
            dataSets.add(d3);
            // make the first DataSet dashed
            // dataSets.get(0).enableDashedLine(10, 10, 0);
            // dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
            // dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);
            LineData data = new LineData(xVals, dataSets);
            data.setDrawValues(false);
            mChart.setData(data);
            mChart.animateX(1000);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 加载市盈率的数据
     */
    private void initSylData() {
        showDialog();
        jiazaiUi();
        // 新接口
        NetUtil.sendPost(TulingDataContant.getUrl("peps"), new NetResult() {

            @Override
            public void result(String arg0) {
                // TODO Auto-generated method stub
                dissDialog();
                successUi();
                if (arg0 == null || "".equals(arg0)) {
                    showErrorDialog(4);
                } else {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(arg0);

                        if (json.optInt("status") != 1) {
                            showMessage("接收数据有异常");
                            noDataUi();
                            return;
                        }
                        // TODO Auto-generated method stub
                        initSylUI(json.optJSONArray("result").toString(), (LineChart) chart4);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * 市盈律
     */
    private void initSylUI(String msg, LineChart mChart) {
        addChart(mChart, 4);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setTouchEnabled(false);
        mChart.setPinchZoom(false);
        mChart.resetTracking();
        mChart.setNoDataText("没有数据");


        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.rgb(150, 150, 150));
        YAxis yLabels = mChart.getAxisLeft();
        yLabels.setValueFormatter(custom);
        yLabels.setTextColor(Color.rgb(150, 150, 150));
        yLabels.setStartAtZero(false);
        yLabels.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return value + "%";
            }
        });
        mChart.getAxisRight().setEnabled(false);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        try {
            JSONArray array = new JSONArray(msg);
            ArrayList<Entry> values0 = new ArrayList<Entry>();
            ArrayList<Entry> values1 = new ArrayList<Entry>();
            ArrayList<Entry> values2 = new ArrayList<Entry>();
            ArrayList<Entry> values3 = new ArrayList<Entry>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(array.length() - i - 1);
                JSONArray array2 = object.getJSONArray("pers");
                for (int j = 0; j < array2.length(); j++) {
                    JSONObject object2 = array2.getJSONObject(j);
                    if (j == 0) {
                        xVals.add(format.format(new Date(object2.optLong("timestamp"))));
                    }
                    if (object2.getString("plate_name").equals("上海A股")) {
                        values0.add(new Entry(Float.parseFloat(object2.optString("newest_pe_r")), i));
                    } else if (object2.getString("plate_name").equals("深圳A股")) {
                        values1.add(new Entry(Float.parseFloat(object2.optString("newest_pe_r")), i));
                    } else if (object2.getString("plate_name").equals("沪深A股")) {
                        values2.add(new Entry(Float.parseFloat(object2.optString("newest_pe_r")), i));
                    } else if (object2.getString("plate_name").equals("深市A股")) {
                        values3.add(new Entry(Float.parseFloat(object2.optString("newest_pe_r")), i));
                    }

                }
            }
            LineDataSet d0 = new LineDataSet(values0, "上海A股");
            d0.setColor(Color.parseColor("#0e5eab"));
            d0.setLineWidth(2);
            d0.setDrawCircles(false);
            LineDataSet d1 = new LineDataSet(values1, "深圳A股");
            d1.setColor(Color.parseColor("#e0422f"));
            d1.setLineWidth(2);
            d1.setDrawCircles(false);
            LineDataSet d2 = new LineDataSet(values2, "沪深A股");
            d2.setColor(Color.parseColor("#46943a"));
            d2.setLineWidth(2);
            d2.setDrawCircles(false);
            LineDataSet d3 = new LineDataSet(values3, "深市A股");
            d3.setColor(Color.parseColor("#e7ba10"));
            d3.setLineWidth(2);
            d3.setDrawCircles(false);
            dataSets.add(d0);
            dataSets.add(d1);
            dataSets.add(d2);
            dataSets.add(d3);
            // make the first DataSet dashed
            // dataSets.get(0).enableDashedLine(10, 10, 0);
            // dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
            // dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);
            LineData data = new LineData(xVals, dataSets);
            data.setDrawValues(false);
            mChart.setData(data);
            mChart.animateX(1000);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 加载新增开户数的数据
     */
    private void initKHData() {
        showDialog();
        jiazaiUi();
        // 新接口
        NetUtil.sendPost(TulingDataContant.getUrl("D_ACCOUNT_KEY"), new NetResult() {

            @Override
            public void result(String arg0) {
                // TODO Auto-generated method stub
                dissDialog();
                if (arg0 == null || "".equals(arg0)) {
                    successUi();
                    showErrorDialog(3);
                } else {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(arg0);

                        if (json.optInt("status") != 1) {
                            showMessage("接收数据有误");
                            noDataUi();
                            return;
                        }
                        successUi();
                       // initKhUI(json.optJSONObject("result").toString(), (BarChart) chart3);
                        initKhUI2(json.optJSONObject("result").toString(), (CombinedChart) chart3);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // initSylData(completes);
                }

            }
        });

    }

    /**
     * 新的新增开户数的解析方法
     * 折线图*/
    private   void   initKhUI2(String msg, CombinedChart  mChart){
        addChart(mChart, 3);
          try {


              ArrayList<String> xVals = new ArrayList<String>();

              JSONArray array = new JSONObject(msg).getJSONArray("data");
              ArrayList<Entry> values0 = new ArrayList<Entry>();
              for (int i = 0; i < array.length(); i++) {
                  JSONObject object = array.getJSONObject(array.length() - i - 1);
                  String date = object.optString("time");
                  if (date.length() > 5)
                      date = date.substring(date.length() - 5, date.length());
                  xVals.add(date);
                  values0.add(new Entry((float) object.optDouble("val") * 10000, i));
              }
              LineDataSet khSet = new LineDataSet(values0, "新增开户数");
              khSet.setAxisDependency(AxisDependency.LEFT);
               int newRed  = Color.parseColor("#fe0017");
              khSet.setColor(newRed);
              //khSet.setCircleColor(newRed);
              khSet.setDrawValues(false);
              khSet.setDrawCircles(false);
              LineData lineData = new LineData();
              lineData.addDataSet(khSet);

              CombinedData data = new CombinedData(xVals);
              BarData barData1 = new BarData();
              barData1.setGroupSpace(0.0f);
              data.setData(barData1);
              data.setData(lineData);

              mChart.setDrawGridBackground(false);
              mChart.setDescription("");
              mChart.setPinchZoom(true);
              mChart.setDrawBarShadow(false);
              mChart.setTouchEnabled(true);
              mChart.setNoDataText("没有数据");

              XAxis xAxis = mChart.getXAxis();
              xAxis.setPosition(XAxisPosition.BOTTOM);
              xAxis.setTextSize(8f);
              xAxis.setTextColor(Color.rgb(150, 150, 150));
              YAxis yLabels = mChart.getAxisLeft();
              yLabels.setValueFormatter(custom);
              yLabels.setTextColor(Color.rgb(150, 150, 150));
              mChart.getAxisRight().setEnabled(false);


              mChart.setData(data);
              mChart.animateXY(1500, 1500);

          }catch (Exception e) {
              e.printStackTrace();
          }
    }



    /**
     * 新增开户数
     */
    private void initKhUI(String msg, BarChart mChart) {
        addChart(mChart, 3);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setPinchZoom(true);
        mChart.setDrawBarShadow(false);
        mChart.setTouchEnabled(true);
        mChart.setNoDataText("没有数据");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.rgb(150, 150, 150));
        YAxis yLabels = mChart.getAxisLeft();
        yLabels.setValueFormatter(custom);
        yLabels.setTextColor(Color.rgb(150, 150, 150));
        mChart.getAxisRight().setEnabled(false);
        mChart.resetTracking();
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        try {
            JSONArray array = new JSONObject(msg).getJSONArray("data");
            ArrayList<BarEntry> values0 = new ArrayList<BarEntry>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(array.length() - i - 1);
                String date = object.optString("time");
                if (date.length() > 5)
                    date = date.substring(date.length() - 5, date.length());
                xVals.add(date);
                values0.add(new BarEntry((float) object.optDouble("val") * 10000, i));
            }
            BarDataSet set1 = new BarDataSet(values0, "新增开户数");
            set1.setColor(Color.parseColor("#e0422f"));
            set1.setDrawValues(false);
            dataSets.add(set1);
            set1.setValueTextSize(8);
            BarData data = new BarData(xVals, dataSets);
            // data.setGroupSpace(80f);
            mChart.setData(data);
            mChart.animateX(1000);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 加载期权转账的数据
     */
    private void initQhData() {
        showDialog();
        // LogUtil.i("momo", "UrlUtil.URL_qq==" + UrlUtil.URL_qq);
        jiazaiUi();
        // 旧的接口
        NetUtil.sendGet(UrlUtil.URL_qq, "", new NetResult() {

            @Override
            public void result(final String msg) {
                // TODO Auto-generated method stub
                post(new Runnable() {

                    @Override
                    public void run() {
                        dissDialog();
                        successUi();
                        if (msg == null || "".equals(msg)) {
                            showErrorDialog(2);
                        } else {

                            // TODO Auto-generated method stub
                            initQhUI(msg, (LineChart) chart2);
                            // initKHData(completes);
                        }
                    }
                });
            }
        });

    }

    /**
     * 初始化期权成交量的UI界面
     */
    private void initQhUI(String msg, LineChart mChart) {
        addChart(mChart, 2);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setTouchEnabled(true);
        // mChart.setStartAtZero(true);
        mChart.setPinchZoom(true);
        mChart.resetTracking();
        mChart.setNoDataText("没有数据");
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.rgb(150, 150, 150));
        YAxis yLabels = mChart.getAxisLeft();
        yLabels.setValueFormatter(custom);
        yLabels.setTextColor(Color.rgb(150, 150, 150));
        mChart.getAxisRight().setEnabled(false);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        try {
            JSONArray array = new JSONArray(msg);
            ArrayList<Entry> values0 = new ArrayList<Entry>();
            ArrayList<Entry> values1 = new ArrayList<Entry>();
            ArrayList<Entry> values2 = new ArrayList<Entry>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(array.length() - i - 1);
                xVals.add(format.format(new Date(object.optLong("timestamp"))));
                values0.add(new Entry(Float.parseFloat(object.optString("put_volume").replaceAll(",", "")), i));
                values1.add(new Entry(Float.parseFloat(object.optString("leaves_call_qty").replaceAll(",", "")), i));
                values2.add(new Entry(Float.parseFloat(object.optString("total_volume").replaceAll(",", "")), i));
            }
            LineDataSet d0 = new LineDataSet(values0, "认购成交量");
            d0.setColor(Color.parseColor("#e0422f"));
            d0.setCircleColor(Color.parseColor("#e0422f"));
            d0.setLineWidth(3);
            d0.setCircleSize(4);
            LineDataSet d1 = new LineDataSet(values1, "未平仓合约");
            d1.setColor(Color.parseColor("#0e5eab"));
            d1.setCircleColor(Color.parseColor("#0e5eab"));
            d1.setLineWidth(3);
            d1.setCircleSize(4);
            LineDataSet d2 = new LineDataSet(values2, "总成交量");
            d2.setColor(Color.parseColor("#e7ba10"));
            d2.setCircleColor(Color.parseColor("#e7ba10"));
            d2.setLineWidth(3);
            d2.setCircleSize(4);
            dataSets.add(d0);
            dataSets.add(d1);
            dataSets.add(d2);
            // make the first DataSet dashed
            // dataSets.get(0).enableDashedLine(10, 10, 0);
            // dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
            // dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);
            LineData data = new LineData(xVals, dataSets);
            data.setDrawValues(false);
            mChart.setData(data);
            mChart.animateX(1000);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 加载银证转帐信息的数据
     */
    private void initYzData() {
        showDialog();
        jiazaiUi();
        NetUtil.sendPost(UrlUtil.URL_yz, "type=1", new NetResult() {

            @Override
            public void result(final String msg) {
                // TODO Auto-generated method stub
                post(new Runnable() {

                    @Override
                    public void run() {
                        dissDialog();
                        successUi();
                        if (msg == null || "".equals(msg)) {

                            showErrorDialog(1);
                        } else {

                            // TODO Auto-generated method stub
                            initYzUI(msg, (BarChart) chart1);
                            // initQhData(completes);
                        }
                    }
                });
            }
        });
        /*
		 * NetUtil.sendPost(UrlUtil.URL_yz, "type=1", new HttpRevMsg() {
		 * 
		 * @Override public void revMsg(final String msg) { // TODO
		 * Auto-generated method stub post(new Runnable() {
		 * 
		 * @Override public void run() { dissDialog(); // TODO Auto-generated
		 * method stub initYzUI(msg, (BarChart) chart1); //
		 * initQhData(completes); } }); } });
		 */
    }

    /**
     * 银证转账信息
     */
    private void initYzUI(String msg, BarChart mChart) {
        addChart(mChart, 1);
        mChart.setDescription("");
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setTouchEnabled(true);
        mChart.setDrawSpace(true);
        mChart.setNoDataText("没有数据");
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.rgb(150, 150, 150));

        YAxis yLabels = mChart.getAxisLeft();
        yLabels.setValueFormatter(custom);
        yLabels.setTextColor(Color.rgb(150, 150, 150));
        mChart.getAxisRight().setEnabled(false);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        try {
            JSONArray array = new JSONArray(msg);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(array.length() - i - 1);
                String date = object.optString("date");
                if (date.length() > 11)
                    date = date.substring(date.length() - 11, date.length());
                xVals.add(date);
                yVals1.add(new BarEntry(new float[]{Float.parseFloat(object.optString("in").replaceAll(",", "")),
                        Float.parseFloat(object.optString("out").replaceAll(",", "")),
                        Float.parseFloat(object.optString("last_date").replaceAll(",", ""))}, i));
            }
            BarDataSet set1 = new BarDataSet(yVals1, "");
            set1.setColors(getColors());
            set1.setValueTextSize(8);
            set1.setStackLabels(new String[]{"银证转入", "银证转出", "资金余额"});
            ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(xVals, dataSets);
            data.setValueFormatter(custom);
            mChart.setData(data);
            mChart.animateX(1000);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private int[] getColors() {
        int[] colors = {Color.parseColor("#e0422f"), Color.parseColor("#46943a"), Color.parseColor("#e7ba10")};
        // for (int i = 0; i < stacksize; i++) {
        // colors[i] = ColorTemplate.VORDIPLOM_COLORS[i];
        // }
        return colors;
    }

    /**
     * 初始化两融数据的数据
     */
    private void initLrData() {
        // LogUtil.i("momo", "initLrData()");
        showDialog();
        // LogUtil.i("momo", "TLUrl.URL_lr==" + UrlUtil.URL_lr);
        jiazaiUi();
        NetUtil.sendPost(TulingDataContant.getUrl("dealing_newest_key"), new NetResult() {

            @Override
            public void result(final String msg) {
                // TODO Auto-generated method stub
                post(new Runnable() {

                    @Override
                    public void run() {
                        // LogUtil.i("momo", "dissDialog();");
                        dissDialog();
                        if (msg == null || "".equals(msg)) {
                            successUi();
                            showErrorDialog(0);
                        } else {

                            try {
                                JSONObject obj = new JSONObject(msg);

                                if (obj.optInt("status") != 1) {
                                    showMessage("服务器数据有问题");
                                    noDataUi();
                                    return;
                                }
                                successUi();
                                initLrUI(obj.optJSONArray("result"), (LineChart) chart0);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            // initYzData(completes);
                        }
                    }
                });
            }
        });

    }

    /**
     * 两融数据的UI界面
     *
     * @param array
     */
    private void initLrUI(JSONArray array, LineChart mChart) {
        addChart(mChart, 0);

        mChart.setDescription("");
        mChart.setTouchEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setNoDataText("没有数据");
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.rgb(150, 150, 150));

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(custom1);
        leftAxis.setStartAtZero(false);
        leftAxis.setTextColor(Color.rgb(150, 150, 150));
        // leftAxis.setGridColor(Color.rgb(253, 246, 238));
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setValueFormatter(custom1);
        rightAxis.setStartAtZero(false);
        rightAxis.setTextColor(Color.rgb(150, 150, 150));
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> vals1 = new ArrayList<Entry>();
        ArrayList<Entry> vals2 = new ArrayList<Entry>();
        try {
            // JSONArray array = new JSONArray(msg);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(array.length() - i - 1);
                xVals.add(format.format(new Date(object.optLong("timestamp"))));
                vals1.add(new Entry(object.getLong("rzye"), i));
                vals2.add(new Entry(object.getLong("rqylje"), i));
            }
            LineDataSet set1 = new LineDataSet(vals1, "融资余额(左轴)");
            set1.setAxisDependency(AxisDependency.LEFT);
            set1.setDrawCircles(false);
            // set1.setCubicIntensity(0.2f * xVals.size() / vals1.size());
            // set1.setCircleColor(Color.rgb(253, 224, 132));
            // set1.setCircleSize(3f);
            set1.setLineWidth(2f);
            set1.setDrawCubic(true);
            set1.setColor(Color.rgb(251, 109, 108));

            LineDataSet set2 = new LineDataSet(vals2, "融劵余额(右轴)");
            set2.setAxisDependency(AxisDependency.RIGHT);
            set2.setDrawCircles(false);
            // set2.setCubicIntensity(0.2f * xVals.size() / vals2.size());
            // set2.setCircleColor(Color.rgb(251, 109, 108));
            // set2.setCircleSize(3f);
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
            mChart.animateX(1000);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 网络请求数据失败的时候的处理
     */
    private void showErrorDialog(final int num) {
        View view = View.inflate(TulingChartActivity.this, R.layout.layout_hqsb, null);
        Button congzai = (Button) view.findViewById(R.id.layout_hqsb_chongzai);//
        mLayout.removeAllViews();
        mLayout.addView(view);
        congzai.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mLayout.removeAllViews();
                switch (num) {
                    case 0:
                        initLrData();
                        break;
                    case 1:
                        initYzData();
                        break;
                    case 2:
                        initQhData();
                        break;
                    case 3:
                        initKHData();
                        break;
                    case 4:
                        initSylData();
                        break;
                    case 5:
                        initPjData();
                        break;
                    case 6:
                        initGzData();
                        break;

                    default:
                        break;
                }

            }
        });

    }

    public void post(Runnable r) {
        if (handler != null)
            handler.post(r);
    }

    public void handleMsg(Message msg) {
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            handleMsg(msg);
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.tljr_data_fanhui) {
            finish();
        }
    }

}
