package com.abct.tljr.kline.gegu.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.abct.tljr.R;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.kline.gegu.entity.LdblEntity;
import com.abct.tljr.kline.gegu.entity.LxfgbsEntity;
import com.abct.tljr.kline.gegu.entity.ProfitsEntity;
import com.abct.tljr.kline.gegu.entity.RkdpBase;
import com.abct.tljr.kline.gegu.entity.RkdpEntity;
import com.abct.tljr.kline.gegu.entity.SxjylEntity;
import com.abct.tljr.kline.gegu.entity.YYSRBEntity;
import com.abct.tljr.kline.gegu.util.ChartUtils;
import com.abct.tljr.kline.gegu.util.DataListener;
import com.abct.tljr.kline.gegu.util.NetUtils;
import com.abct.tljr.kline.gegu.view.BarCharView;
import com.abct.tljr.kline.gegu.view.CombinedChartView;
import com.abct.tljr.kline.gegu.view.LineCharView;
import com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView;
import com.abct.tljr.kline.gegu.view.PieChartView;
import com.abct.tljr.kline.gegu.view.ProfitsChartView;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.utils.OneGuConstance;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * onegu的财务fragment
 */
public class OneguCaiwuFragment extends BaseFragment {
    /**
     * 根布局
     */
    private View                 rootView;
    private OneGuActivity        oneGuActivity;
    private String               code;
    private String               name;
    private String               market;
    private String               guKey;
    private boolean              isJJ;
    private String               param;
    private BarChart             profitsBarChart;
    private CombinedChart        profitsLineChart;
    private BarChart             ldblChart;
    private BarChart             sdblChart;
    private BarChart             lxfgbsChart;
    private OneguGuZhiScrollView scrollView;


    /**
     * 红色：fe0017
     * 蓝色0059b2
     * 绿色：008e32
     */

    private int newRed  = Color.parseColor("#fe0017");
    private int newBlue = Color.parseColor("#0059b2");
    private int newGreen=Color.parseColor("#008e32");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = View.inflate(getActivity(), R.layout.fragment_onegu_caiwu, null);
        oneGuActivity = (OneGuActivity) OneguCaiwuFragment.this.getActivity();
        return rootView;
    }

    /**
     * 接收从上一个页面传递过来的数据
     */
    private void initData() {
        // 新页面接收数据
        Bundle bundle = oneGuActivity.getIntent().getExtras();
        code = bundle.getString("code");// 我武生物右边的代码
        name = bundle.getString("name");// 我武生物
        market = bundle.getString("market");
        guKey = bundle.getString("key");
        isJJ = guKey.substring(0, 2).equals("jj");

    }

    private void showMessage(String message) {
        Toast.makeText(oneGuActivity, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 添加总收益的图表数据
     */
    void addProfitsCombinedBar(ArrayList<ProfitsEntity> list) {

        if (!isListUseable(list))
            return;

        boolean flat = false;
        boolean baifenFlat = false;
        ArrayList<Integer> lrColors = new ArrayList<Integer>();
        ArrayList<Integer> zsyColors = new ArrayList<Integer>();

        ArrayList<String> xVals = new ArrayList<String>();// X值
        ArrayList<BarEntry> zsyvalues = new ArrayList<BarEntry>();// 总收益Y值
        ArrayList<BarEntry> lrvalues = new ArrayList<BarEntry>();// 利润Y值
        ArrayList<Entry> sybvalues = new ArrayList<Entry>();// 收益Y值
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        for (int i = list.size() - 1; i >= 0; i--) {
            ProfitsEntity entity = list.get(i);
            zsyvalues.add(new BarEntry((float) entity.getZsr() / 100000000, list.size() - 1 - i));
            LogUtil.e("zsyvalues", entity.getZsr() / 1000000 + "");
            // 利润值
            lrvalues.add(new BarEntry(entity.getKfjlr() / 1000000, list.size() - 1 - i));
            // 设置颜色
            if (entity.getZsr() > 0) {
                zsyColors.add(newRed);
            } else {
                zsyColors.add(newGreen);
            }
            if (entity.getKfjlr() > 0) {
                lrColors.add(newRed);
            } else {
                lrColors.add(newGreen);
            }
            sybvalues.add(new Entry(entity.getKfjlrbzsr() * 100, list.size() - 1 - i));
            xVals.add(format.format(new Date(entity.geteNDDATE())));
        }
        LineDataSet sybSet = new LineDataSet(sybvalues, "收益比(左)");
        sybSet.setAxisDependency(AxisDependency.LEFT);
        sybSet.setColor(newBlue);
        sybSet.setCircleColor(newBlue);
        LineData lineData = new LineData();
        lineData.addDataSet(sybSet);
        BarDataSet lrSet = new BarDataSet(lrvalues, "利润(左)");

        lrSet.setBarSpacePercent(60.0f);
        lrSet.setColors(lrColors);

        BarData lrBarData = new BarData(xVals);
        lrBarData.addDataSet(lrSet);

        BarDataSet zsySet = new BarDataSet(zsyvalues, "总收益(左)");
        zsySet.setBarSpacePercent(60.0f);
        zsySet.setColors(zsyColors);

        BarData zsyBarData = new BarData(xVals);
        zsyBarData.addDataSet(zsySet);

        CombinedData data = new CombinedData(xVals);
        BarData barData1 = new BarData();
        barData1.setGroupSpace(0.0f);
        data.setData(barData1);
        data.setData(lineData);

        ProfitsChartView view = new ProfitsChartView(profitsLineChart, profitsBarChart, lrBarChart, data, zsyBarData,
                lrBarData);
        view.initChart();

    }

    private <T> boolean isListUseable(ArrayList<T> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        return true;
    }
    /**
     * 红色：fe0017
     * 蓝色:0059b2
     * 绿色：008e32
     */
    private int[] colors = new int[]{Color.parseColor("#fe0017"), Color.parseColor("#0059b2"),
            Color.parseColor("#008e32")};
    private CombinedChart combinedChart;
    private PieChart      pieChart;
    private LineChart     lineChart;

    /**
     * 添加流动速率和速动速率的合成图表
     */
    void addLsdblBarChar(ArrayList<LdblEntity> list) {
        if (!isListUseable(list))
            return;
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        for (int i = list.size() - 1; i >= 0; i--) {
            xVals.add(format.format(new Date(list.get(i).geteNDDATE())));
            yVals1.add(new BarEntry(Float.valueOf(list.get(i).getLdbl() + "")*100.0f, list.size() - 1 - i));
            yVals2.add(new BarEntry(Float.valueOf(list.get(i).getSdbl() + "")*100.0f, list.size() - 1 - i));
            /**
             *
             *  yVals1.add(new BarEntry(Float.valueOf(list.get(i).getLdbl() + ""), list.size() - 1 - i));
             yVals2.add(new BarEntry(Float.valueOf(list.get(i).getSdbl() + ""), list.size() - 1 - i));
             */
            LogUtil.e("yVals2", list.get(i).getSdbl() + "");
        }
        BarDataSet set1 = new BarDataSet(yVals1, "流动比率");
        set1.setBarSpacePercent(0.0f);
        set1.setColor(newRed);
        dataSets.add(set1);
        BarDataSet set2 = new BarDataSet(yVals2, "速动比率");
        set2.setBarSpacePercent(0.0f);
        set2.setColor(newBlue);
        dataSets.add(set2);
        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setGroupSpace(90f);
        ldblChart.setHighlightEnabled(false);
        BarCharView view = new BarCharView(ldblChart, data, "", true);
        view.initChar();

    }

  /*  private int colorLine1 = Color.parseColor("#6BC0FF");
    private int colorLine2 = Color.parseColor("#EC4F40");*/
/*    private int colorLine3 = Color.parseColor("#EBAB41");*/



    private BarChart lrBarChart;




    /**
     * 添加估值比率的图表的数据
     */
    private void addSxjylLineChar(ArrayList<SxjylEntity> list) {
        if (!isListUseable(list))
            return;


        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        ArrayList<Entry> sjlvalues = new ArrayList<Entry>();
        ArrayList<Entry> sxlvalues = new ArrayList<Entry>();
        ArrayList<Entry> sylvalues = new ArrayList<Entry>();
        ArrayList<BarEntry> barYVals = new ArrayList<BarEntry>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        for (int i = list.size() - 1; i >= 0; i--) {
            SxjylEntity entity = list.get(i);
            sjlvalues.add(new Entry(Float.valueOf(entity.getSJL()), list.size() - 1 - i));
            sxlvalues.add(new Entry(Float.valueOf(entity.getSXL()), list.size() - 1 - i));
            sylvalues.add(new Entry(Float.valueOf(entity.getJTSYL()), list.size() - 1 - i));
            LogUtil.e("sxlvalues", Float.valueOf(entity.getJTSYL()) + "");

            barYVals.add(new BarEntry(0.0f, list.size() - 1 - i));
            xVals.add(format.format(list.get(i).getENDDATE()));
        }
        LineDataSet sjlSet = new LineDataSet(sjlvalues, "市净率(左)");
        sjlSet.setColor(newBlue);
        sjlSet.setCircleColor(newBlue);
        sjlSet.setDrawValues(false);
        LineDataSet sxlSet = new LineDataSet(sxlvalues, "市销率(左)");
        sxlSet.setColor(newRed);
        sxlSet.setCircleColor(newRed);
        sxlSet.setDrawValues(false);
        LineDataSet sylSet = new LineDataSet(sylvalues, "市盈率(右)");
        sylSet.setColor(newGreen);
        sylSet.setCircleColor(newGreen);
        sylSet.setAxisDependency(AxisDependency.RIGHT);
        sylSet.setDrawValues(false);
        LineData lineData = new LineData();
        lineData.addDataSet(sxlSet);
        lineData.addDataSet(sjlSet);
        lineData.addDataSet(sylSet);
        CombinedData data = new CombinedData(xVals);
        data.setData(lineData);
        BarData bardata = new BarData();
        BarDataSet barDataSet = new BarDataSet(barYVals, "");
        barDataSet.setDrawValues(false);
        barDataSet.setColor(Color.parseColor("#00000000"));
        bardata.addDataSet(barDataSet);
        // bardata.setGroupSpace(0f);
        // data.setData(bardata);
        data.setData(new BarData());
        CombinedChartView view = new CombinedChartView(combinedChart, data);
        view.initchar();

    }

    /**
     * 添加营业收入比重图标的数据
     */
    private void addYYSRBChart(ArrayList<YYSRBEntity> list) {
        if (!isListUseable(list))
            return;
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        // float countAll = 0.0f;
        for (int i = 0; i < list.size(); i++) {
            YYSRBEntity entity = list.get(i);
            if (!entity.getiTEM_NAME().equals("合计")) {
                xVals.add(entity.getiTEM_NAME());
                yVals1.add(new Entry(Float.valueOf(entity.getiNCOME() + ""), i));
            }
        }
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        /**
         * 环形图专用的十种颜色
         * 蓝1：0071b8
         蓝2：00acee
         蓝3：6dcff4
         绿1：01652f
         绿2：00a551
         绿3：76be41
         黄1：fff794
         黄2：fee600
         黄3：ffcb05
         黄4：ef701e

         红色：ee584e

         黄色：eece4e

         绿色：46b35d

         蓝色：23b7ee

         粉色：ff6ddb*/

          int[] cricleColors  = {
                Color.parseColor("#0071b8"),
                Color.parseColor("#00acee"),
                Color.parseColor("#6dcff4"),
                Color.parseColor("#01652f"),
                Color.parseColor("#00a551"),
                Color.parseColor("#76be41"),
                Color.parseColor("#fff794"),
                Color.parseColor("#fee600"),
                Color.parseColor("#ffcb05"),
                Color.parseColor("#ef701e"),

                  Color.parseColor("#ee584e"),
                  Color.parseColor("#eece4e"),
                  Color.parseColor("#46b35d"),
                  Color.parseColor("#23b7ee"),
                  Color.parseColor("#ff6ddb")



        };

        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
            colors.add(ColorTemplate.getHoloBlue());


            */
        for (int  i:cricleColors){
            colors.add(i);
        }


        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter((ValueFormatter) new PercentFormatter());

        data.setValueTextSize(11.0f);
        data.setValueTextColor(Color.BLACK);

        PieChartView view = new PieChartView(pieChart, data);
        view.initChart();
        // 设置下面的标签
        ChartUtils.setBelowLables(xVals, yVals1, colors, data.getYValueSum(), perLables, oneGuActivity);

    }

    private class PercentFormatter implements ValueFormatter {
        protected DecimalFormat mFormat;

        public PercentFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0");
        }

        public PercentFormatter(DecimalFormat format) {
            this.mFormat = format;
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value) + " %";
        }
    }

    /**
     * 添加利息覆盖倍数的图表数据
     */
    private void addLxfgbsChart(ArrayList<LxfgbsEntity> lxfgbsEntities) {
        if (!isListUseable(lxfgbsEntities)) {
            return;
        }
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        for (int i = lxfgbsEntities.size() - 1; i >= 0; i--) {
            xVals.add(format.format(new Date(lxfgbsEntities.get(i).geteNDDATE())));
            yVals1.add(
                    new BarEntry(Float.valueOf(lxfgbsEntities.get(i).getLxfgbs() + ""), lxfgbsEntities.size() - 1 - i));
        }
        BarDataSet set1 = new BarDataSet(yVals1, "");
        set1.setBarSpacePercent(60f);
        set1.setColor(newRed);
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        lxfgbsChart.setHighlightEnabled(false);
        BarCharView view = new BarCharView(lxfgbsChart, data, "", false);
        view.initChar();
    }

    /**
     * 添加走势对比图表的数据
     */
    private void addRkdpChart(ArrayList<RkdpEntity> list) {
        if (!isListUseable(list)) {
            return;
        }

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        int minSize = list.get(0).getResult().size();
        for (RkdpEntity entity : list) {
            if (entity.getResult().size() < minSize) {
                minSize = entity.getResult().size();
            }
        }
        for (int i = 0; i < list.size(); i++) {
            RkdpEntity entity = list.get(i);
            ArrayList<Entry> values = new ArrayList<Entry>();
            for (int j = minSize - 1; j >= 0; j--) {
                RkdpBase data = entity.getResult().get(j);
                values.add(new Entry(data.getRate(), j));
                if (i == 0)
                    xVals.add(new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getTime())));
            }
            LineDataSet set1 = new LineDataSet(values, entity.getName());
            set1.setDrawCircles(false);
            set1.setColor(colors[i]);
            dataSets.add(set1);
        }
        Collections.reverse(xVals);
        LineData data = new LineData(xVals, dataSets);
        LineCharView view = new LineCharView(lineChart, data);
        view.initChar();
    }

    private boolean rootViewFloat = false;
    private LinearLayout perLables;

    /**
     * 初始化根布局
     */
    public void initRootView() {
        if (rootViewFloat) {
            return;
        }
        rootViewFloat = true;
        initData();
        scrollView = (OneguGuZhiScrollView) rootView.findViewById(R.id.fragment_onegu_caiwu_sv);
        // 用另外一种方式来处理滚动
        scrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                        if (scrollView.getScrollY() > 0) {
                            oneGuActivity.sw.setEnabled(false);
                        } else {
                            oneGuActivity.sw.setEnabled(true);
                        }
                        break;
                }
                return false;
            }
        });

        // 总收益
        profitsBarChart = (BarChart) rootView.findViewById(R.id.profitsBarChart);

        // 利润
        lrBarChart = (BarChart) rootView.findViewById(R.id.lrBarChart);

        // 收益比
        profitsLineChart = (CombinedChart) rootView.findViewById(R.id.profitsLineChart);

        // 流动比率和速动比率合在一起的图表
        ldblChart = (BarChart) rootView.findViewById(R.id.ldblChart);

        // 利息覆盖倍数图表
        lxfgbsChart = (BarChart) rootView.findViewById(R.id.lxfgbsChart);
        // 估值比率的图表
        combinedChart = (CombinedChart) rootView.findViewById(R.id.combinedChart);
        // 营业收入比重
        pieChart = (PieChart) rootView.findViewById(R.id.pieChart);
        // 走势对比
        lineChart = (LineChart) rootView.findViewById(R.id.lineChart);

        perLables = (LinearLayout) rootView.findViewById(R.id.fragment_caiwu_piechart_lables);// 营业收入比重下面的小标签容器

        JSONObject jo = new JSONObject();

        try {
            jo.put("market", market);
            jo.put("code", code);

            param = jo.toString();
            getAllChartData();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取所有的图表数据
     */

    public void getAllChartData() {

        try {
            // 获取总收入图表的数据
            getprofitsData();

            // 获取估值比率图表的数据
            getSxjylData();

            // 获取流动比率和速动比率合成图表的数据
            getldblChartData();

            // 获取利息覆盖倍速的数据
            getLxfgbsData();
            // 获取营业收入比重的数据
            getYYSRB();
            // 获取走势对比图表的数据
            getRkdpData();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取走势对比图表的数据
     */
    private void getRkdpData() {
        // TODO Auto-generated method stub
        String url = OneGuConstance.getURL("Rkdp2Impf", "rkdp2", param);
        LogUtil.e("gh123", "走势对比" + url);
        File file = NetUtils.getAbsoluteFile(NetUtils.rkdpFileName, oneGuActivity, name);
        NetUtils.getNeedData(url, file, oneGuActivity, new DataListener() {

            @Override
            public void addChartData(JSONArray jsonArray) throws JSONException {
                // 添加走势对比图表的数据
                addRkdpChart(NetUtils.getRkdpListByJsonArray(jsonArray));
            }
        });

    }

    /**
     * 获取营业收入比重的数据
     */
    private void getYYSRB() {
        // TODO Auto-generated method stub
        String url = OneGuConstance.getURL("YysrbzImpf", "yysrbz", param);
        LogUtil.e("gh123", "营业收入" + url);
        File file = NetUtils.getAbsoluteFile(NetUtils.yYSRBFileName, oneGuActivity, name);
        NetUtils.getNeedData(url, file, oneGuActivity, new DataListener() {
            @Override
            public void addChartData(JSONArray jsonArray) throws JSONException {
                // 添加营业收入比重图标的数据
                addYYSRBChart(NetUtils.getYYSRBListByJsonArray(jsonArray));
            }
        });
    }

    /**
     * 获取利息覆盖倍速的数据
     */
    private void getLxfgbsData() {
        // TODO Auto-generated method stub
        String url = OneGuConstance.getURL("LxfgbsImpf", "lxfgbs", param);
        File file = NetUtils.getAbsoluteFile(NetUtils.lxfgbsFileName, oneGuActivity, name);
        NetUtils.getNeedData(url, file, oneGuActivity, new DataListener() {
            @Override
            public void addChartData(JSONArray jsonArray) throws JSONException {
                LogUtil.e("gh124", "覆盖倍数-- " + jsonArray.toString());
                addLxfgbsChart(NetUtils.getLxfgbsListByJson(jsonArray));

            }
        });

    }

    /**
     * 获取流动比率和速动比率合成图表的数据
     */
    private void getldblChartData() {
        // TODO Auto-generated method stub
        String url = OneGuConstance.getURL("LsdblImpf", "lsdbl", param);
        LogUtil.e("gh123", "流动比率和速动比率" + url);
        File file = NetUtils.getAbsoluteFile(NetUtils.ldblChartFileName, oneGuActivity, name);
        NetUtils.getNeedData(url, file, oneGuActivity, new DataListener() {
            @Override
            public void addChartData(JSONArray jsonArray) throws JSONException {
                LogUtil.e("gh124", "流动-- " + jsonArray.toString());
                addLsdblBarChar(NetUtils.getldblListByJsonArray(jsonArray));

            }
        });

    }

    /**
     * 获取估值比率图表的数据
     *
     * @throws UnsupportedEncodingException
     */
    private void getSxjylData() throws UnsupportedEncodingException {
        // TODO Auto-generated method stub
        String url = OneGuConstance.getURL("SxjylImpf", "sxjyl", param);
        LogUtil.e("gh123", "估值比率" + url);
        File file = NetUtils.getAbsoluteFile(NetUtils.sxjylFileName, oneGuActivity, name);
        NetUtils.getNeedData(url, file, oneGuActivity, new DataListener() {

            @Override
            public void addChartData(JSONArray jsonArray) throws JSONException {
                addSxjylLineChar(NetUtils.getSxjylListByJsonArray(jsonArray));

            }
        });
    }

    /**
     * 获取总收入图表的数据
     *
     * @throws UnsupportedEncodingException
     */
    private void getprofitsData() throws UnsupportedEncodingException {
        // TODO Auto-generated method stub
        File file = NetUtils.getAbsoluteFile(NetUtils.profitsFileName, oneGuActivity, name);

        String url = OneGuConstance.getURL("KfjlrbzsrImpf", "kfjlrbzsr", param);
        LogUtil.e("gh123", "总收益" + url);
        // 本地获取总收入图表的数据,如果不可以从本地获取数据就网络获取
        NetUtils.getNeedData(url, file, oneGuActivity, new DataListener() {

            @Override
            public void addChartData(JSONArray jsonArray) throws JSONException {
                addProfitsCombinedBar(NetUtils.getProfitsListByJsonArray(jsonArray));

            }
        });
    }

}
