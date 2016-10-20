package com.abct.tljr.kline.gegu.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.kline.BarView;
import com.abct.tljr.kline.FiveLineView;
import com.abct.tljr.kline.KLineGraphActivity;
import com.abct.tljr.kline.KView;
import com.abct.tljr.kline.LineView;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.kline.WeekKView;
import com.abct.tljr.kline.YueKView;
import com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.OneGuConstance;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.model.AppInfo;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * momo写的onegu估值的Fragment
 */
public class OneguGuzhiFragment extends BaseFragment {

    /**
     * onegu首页的 根布局
     */
    private View rootView;

    private String code, name, market, zuName;
    /**
     * 这个gukey是private String
     */
    private String guKey;
    /**
     * isJJ的初始值是false
     */
    private boolean isJJ = false;
    private String  key  = "minute";

    private ArrayList<String> tabs = new ArrayList<String>();

    /**
     * 特别注意这个gukey是public static与另外一个有所区别
     */
    public static String       gukey;
    private       BarView      barView;
    private       LineView     lineView;
    private       FiveLineView fiveLineView;
    private       KView        kView;
    private       WeekKView    weekKView;
    private       YueKView     yueKView;

    private JSONObject marketInfo;

    private TextView stockJinKai;

    private TextView stockZuoShou;

    private TextView stockZueiDi;

    private TextView stockZueiGao;

    private TextView stockShiJinLv;

    private TextView stockShiYinLv;

    private TextView stockChengJiaoLiang;

    private TextView stockChengJiaoE;

    private TextView stockZongShiZhi;

    private TextView stockLiouTongShiZhi;

    private OneGuActivity oneGuActivity;

    private OneguGuZhiScrollView scrollView;

    private boolean rootViewFloat = false;

    public  Timer        timer;
    private LinearLayout noDataLinear;


    /**
     * 标题栏
     */
    // private RelativeLayout topColumn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = View.inflate(getActivity(), R.layout.fragment_onegu_guzhi, null);
        oneGuActivity = (OneGuActivity) OneguGuzhiFragment.this.getActivity();
        // topColumn = oneGuActivity.getTopColumn();
        initData();
        initRootView();
        return rootView;
    }

    /**
     * 对根布局进行初始化
     */
    public void initRootView() {
        if (rootViewFloat) {
            return;
        }
        rootViewFloat = true;

        initBaseView();
        getNetData();
    }

    /**
     * 从网络获取数据
     */
    private void getNetData() {
        if (Constant.marketInfo == null) {
            Constant.getMarketInfo(new Complete() {
                @Override
                public void complete() {
                    initMarket();
                }
            });
        } else {
            initMarket();
        }
    }

    private void initMarket() {

        marketInfo = Constant.marketInfo.get(market.toLowerCase());

        oneGuActivity.post(runnable);

        initView();
        lineView.setArray(marketInfo);
        lineView.initData();
        barView.getChart().setVisibility(marketInfo.optBoolean("volumes") ? View.VISIBLE : View.GONE);

    }

    private TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            oneGuActivity.handler.sendMessage(message);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        run = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        run = false;
    }

    boolean  run      = true;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 要做的事情
            if (run) {
                // oneGuActivity.postDelayed(this, 30000);
                // reflushDP();
                // 用计时器间隔6秒就访问一次后台数据
                timer = new Timer(true);
                timer.schedule(task, 0, 6000); // 延时0ms后执行，6s执行一次
                if (!key.equals("minute")) {
                    return;
                }
                lineView.ShiShi();
            }
        }
    };

    private View[]  radios;
    private TabHost tabHost;

    private void initView() {
        // 这是所个图表切换的
        tabHost = (TabHost) rootView.findViewById(R.id.tljr_tabhost);
        tabHost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gotoDetail();
            }
        });
        JSONArray array = marketInfo.optJSONArray("lines");
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<Integer> views = new ArrayList<Integer>();
        for (int i = 0; i < array.length(); i++) {
            keys.add(array.optString(i));
        }
        if (keys.contains("minutes")) {
            tabs.add("分时");
            views.add(R.id.tljr_hq_chart3);
        }
        if (keys.contains("5minutes")) {
            tabs.add("五日");
            views.add(R.id.tljr_hq_chart6);
        }
        if (keys.contains("days")) {
            tabs.add("日K");
            views.add(R.id.tljr_hq_chart4);
        }
        if (keys.contains("weeks")) {
            tabs.add("周K");
            views.add(R.id.tljr_hq_chart5);
        }
        if (keys.contains("months")) {
            tabs.add("月K");
            views.add(R.id.tljr_hq_chart1);
        }
        tabHost.setup();
        radios = new View[tabs.size()];
        for (int i = 0; i < tabs.size(); i++) {
            RadioButton view = (RadioButton) LayoutInflater.from(oneGuActivity).inflate(R.layout.tljr_item_tabtitle,
                    null);
            view.setText(tabs.get(i));
            view.setTag(tabs.get(i));
            tabHost.addTab(tabHost.newTabSpec(tabs.get(i)).setIndicator(view).setContent(views.get(i)));
            radios[i] = view;
        }

        // //判断tab的个数
        if (tabs.size() == 1) {
            radios[0].setLayoutParams(
                    new LinearLayout.LayoutParams(Util.dip2px(oneGuActivity, 100), Util.dip2px(oneGuActivity, 40)));
            ((RadioButton) radios[0]).setChecked(true);
            radios[0].setEnabled(false);
            rootView.findViewById(R.id.tljr_arrow1).setVisibility(View.INVISIBLE);
        } else {

            tabChangedArrow(rootView.findViewById(R.id.tljr_arrow1), 0, tabs.size());
        }
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for (int i = 0; i < radios.length; i++) {
                    if (((String) radios[i].getTag()).equals(tabId)) {
                        ((RadioButton) radios[i]).setChecked(true);
                        tabChangedArrow(rootView.findViewById(R.id.tljr_arrow1), i, tabs.size());
                    } else {
                        ((RadioButton) radios[i]).setChecked(false);
                    }
                }

                if (tabId.equals("分时")) {
                    key = "minute";
                    lineView.initData();
                    lineView.getChart().setVisibility(View.VISIBLE);
                    kView.getChart().setVisibility(View.GONE);
                    yueKView.getChart().setVisibility(View.GONE);
                    fiveLineView.getChart().setVisibility(View.GONE);
                    weekKView.getChart().setVisibility(View.GONE);
                } else if (tabId.equals("五日")) {
                    key = "fiveday";
                    fiveLineView.initData();
                    fiveLineView.getChart().setVisibility(View.VISIBLE);
                    kView.getChart().setVisibility(View.GONE);
                    weekKView.getChart().setVisibility(View.GONE);
                    lineView.getChart().setVisibility(View.GONE);
                    yueKView.getChart().setVisibility(View.GONE);
                } else if (tabId.equals("日K")) {
                    key = "diary";
                    kView.initData();
                    kView.getChart().setVisibility(View.VISIBLE);
                    lineView.getChart().setVisibility(View.GONE);
                    yueKView.getChart().setVisibility(View.GONE);
                    fiveLineView.getChart().setVisibility(View.GONE);
                    weekKView.getChart().setVisibility(View.GONE);
                } else if (tabId.equals("周K")) {
                    key = "week";
                    weekKView.initData();
                    weekKView.getChart().setVisibility(View.VISIBLE);
                    yueKView.getChart().setVisibility(View.GONE);
                    lineView.getChart().setVisibility(View.GONE);
                    kView.getChart().setVisibility(View.GONE);
                    fiveLineView.getChart().setVisibility(View.GONE);
                } else if (tabId.equals("月K")) {
                    key = "mouth";
                    yueKView.initData();
                    yueKView.getChart().setVisibility(View.VISIBLE);
                    lineView.getChart().setVisibility(View.GONE);
                    kView.getChart().setVisibility(View.GONE);
                    fiveLineView.getChart().setVisibility(View.GONE);
                    weekKView.getChart().setVisibility(View.GONE);
                }
            }
        });

        kView = new KView(oneGuActivity.handler, (CandleStickChart) rootView.findViewById(R.id.tljr_hq_chart1), market,
                code);
        kView.getChart().getAxisRight().setEnabled(false);
        kView.getChart().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // gotoDetail();
                return false;
            }
        });
        lineView = new LineView(oneGuActivity.handler, (LineChart) rootView.findViewById(R.id.tljr_hq_chart3), market,
                code);
        lineView.getChart().getAxisRight().setEnabled(false);
        lineView.getChart().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // gotoDetail();
                return false;
            }
        });
        fiveLineView = new FiveLineView(oneGuActivity.handler, (LineChart) rootView.findViewById(R.id.tljr_hq_chart6),
                market, code);
        fiveLineView.getChart().getAxisRight().setEnabled(false);
        fiveLineView.getChart().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // gotoDetail();
                return false;
            }
        });
        yueKView = new YueKView(oneGuActivity.handler, (CandleStickChart) rootView.findViewById(R.id.tljr_hq_chart4),
                market, code);
        yueKView.getChart().getAxisRight().setEnabled(false);
        yueKView.getChart().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // gotoDetail();
                return false;
            }
        });
        weekKView = new WeekKView(oneGuActivity.handler, (CandleStickChart) rootView.findViewById(R.id.tljr_hq_chart5),
                market, code);
        weekKView.getChart().getAxisRight().setEnabled(false);
        weekKView.getChart().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // gotoDetail();
                return false;
            }
        });
        // 设置扁平图表
        barView = new BarView((BarChart) rootView.findViewById(R.id.tljr_hq_chart2));
        barView.getChart().getAxisRight().setEnabled(false);
        barView.getChart().getXAxis().setEnabled(false);
        barView.getChart().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // gotoDetail();
                return false;
            }
        });
        kView.setBarChart(barView.getChart());
        lineView.setBarChart(barView.getChart());
        fiveLineView.setBarChart(barView.getChart());
        weekKView.setBarChart(barView.getChart());
        yueKView.setBarChart(barView.getChart());

        kView.getChart().setOtherChart(barView.getChart());
        barView.getChart().setOtherChart(kView.getChart());

        fiveLineView.getChart().setOtherChart(barView.getChart());
        barView.getChart().setOtherChart(fiveLineView.getChart());

        weekKView.getChart().setOtherChart(barView.getChart());
        barView.getChart().setOtherChart(weekKView.getChart());

        yueKView.getChart().setOtherChart(barView.getChart());
        barView.getChart().setOtherChart(yueKView.getChart());

        lineView.getChart().setOtherChart(barView.getChart());
        barView.getChart().setOtherChart(lineView.getChart());

        // name2.setText(code);
    }

    private void gotoDetail() {
        Intent intent = new Intent(oneGuActivity, KLineGraphActivity.class);
        // 用Bundle携带数据
        Bundle bundle = new Bundle();
        // 传递name参数为tinyphp
        bundle.putString("code", code);
        bundle.putString("name", name);
        bundle.putString("key", key);
        bundle.putString("market", market);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void tabChangedArrow(View v, int to, int size) {
        if (v.getTag() == null)
            initImageView(v);
        int from = (Integer) v.getTag();

        int offset = (AppInfo.WIDTH - AppInfo.dp2px(oneGuActivity, 20)) / size;// 间距为屏幕宽减左右间隔除以5
        // int offset = (AppInfo.WIDTH ) / size;// 间距为屏幕宽减左右间隔除以5
        int offsetX = AppInfo.dp2px(oneGuActivity, 10) + offset / 2 - AppInfo.dp2px(oneGuActivity, 84) / 2;// 第0个居左x为：居左间隔+间距/5个-箭头宽度/2
        Animation animation = null;
        animation = new TranslateAnimation(from * offset + offsetX, to * offset + offsetX, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(200);
        v.startAnimation(animation);
        v.setTag(to);
    }

    /**
     * 设置箭头宽度
     */
    private void initImageView(View cursor) {
        cursor.getLayoutParams().width = AppInfo.dp2px(oneGuActivity, 84);
        cursor.setTag(1);
    }


    private void failUi() {
        noDataLinear.setVisibility(View.VISIBLE);
       /* LinearLayout noDataContainer = (LinearLayout) rootView.findViewById(R.id.header_no_data_container);
        noDataContainer.setVisibility(View.VISIBLE);
        View errorView = View.inflate(oneGuActivity, R.layout.layout_hqsb, null);

        Button button = (Button) errorView.findViewById(R.id.layout_hqsb_chongzai);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                reflushDP();
            }
        });


        noDataContainer.addView(errorView);*/
        rootView.findViewById(R.id.header_data_container).setVisibility(View.GONE);
        TextView tv = new TextView(oneGuActivity);
        tv.setText("获取数据失败");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        noDataLinear.addView(tv);


    }

    private void jiazaiUi() {
        noDataLinear.setVisibility(View.VISIBLE);
        noDataLinear.removeAllViews();
        TextView noDataText = new TextView(oneGuActivity);

        noDataText.setText("加载数据中");
        noDataText.setGravity(Gravity.CENTER_HORIZONTAL);
        noDataLinear.addView(noDataText);
    }

    private void successUi() {
        noDataLinear.setVisibility(View.GONE);
        LinearLayout noDataContainer = (LinearLayout) rootView.findViewById(R.id.header_no_data_container);
        noDataContainer.setVisibility(View.GONE);
        rootView.findViewById(R.id.header_data_container).setVisibility(View.VISIBLE);
    }

    private boolean isShowProgressDialogAtFirst = true;

    public void reflushDP() {
        if (isShowProgressDialogAtFirst) {
            ProgressDlgUtil.showProgressDlg("", oneGuActivity);
            jiazaiUi();
        }


        //000000

        Util.getRealInfo("market=" + market + "&code=" + code, new NetResult() {
            @Override
            public void result(final String msg) {
                LogUtil.e("reflushDP123", msg);
                oneGuActivity.post(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @SuppressWarnings("deprecation")
                    @Override
                    public void run() {
                        oneGuActivity.mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            if (isShowProgressDialogAtFirst) {

                                ProgressDlgUtil.stopProgressDlg();
                                isShowProgressDialogAtFirst = false;
                            }


                            if (msg == null || "".equals(msg)) {
                                failUi();
                                return;
                            }
                            org.json.JSONObject object = new org.json.JSONObject(msg);
                            if (object.getInt("code") != 1) {
                                failUi();
                                return;
                            }

                            if (object.getInt("code") == 1) {

                                final org.json.JSONArray array = object.getJSONArray("result");

                                successUi();
                                scrollView.smoothScrollTo(0, 0);

                                setStyle(stockJinKai, array.optString(2), "今开", false);
                                setStyle(stockZuoShou, array.optString(1).replace("null", "--"), "昨收", false);

                                setStyle(stockZueiDi, array.optString(7), "最低", true);
                                setStyle(stockZueiGao, array.optString(6), "最高", true);
                                setStyle(stockShiYinLv, array.optString(11).replace("null", "--"), "市盈率", true);
                                setStyle(stockShiJinLv, array.optString(12).replace("null", "--"), "市净率", true);
                                setStyle(stockChengJiaoLiang, (array.optString(3).equals("null") ? "--"
                                        : df.format(array.optDouble(3) / 10000)) + "万手", "成交量", true);
                                setStyle(stockChengJiaoE, (array.optString(4).equals("null") ? "--"
                                        : df.format(array.optDouble(4) / 10000)) + "亿", "成交额", true);
                                setStyle(stockZongShiZhi, (array.optString(13).equals("null") ? "--"
                                        : (df.format(array.optDouble(13)) + "亿")), "总市值", true);
                                setStyle(stockLiouTongShiZhi, array.optString(14).equals("null") ? "--"
                                        : (df.format(array.optDouble(14)) + "亿"), "流通市值", true);

                                if (stockHeader != null) {
                                    if (Double.isNaN(array.optDouble(0))) {
                                    } else {
                                        if (array.optDouble(0) >= 100000000000l) {
                                            stockValue.setText(Util.df.format(array.optDouble(0) / 1000000000000l) + "万亿");
                                        } else if (array.optDouble(0) >= 10000000) {
                                            stockValue.setText(Util.df.format(array.optDouble(0) / 100000000) + "亿");
                                        } else {
                                            stockValue.setText(array.optDouble(0) == 0 ? "--" : (array.optDouble(0) + ""));
                                        }
                                    }


                                    if (Double.isNaN(array.optDouble(8)) || Double.isNaN(array.optDouble(9))) {
                                    } else {
                                        double change = array.optDouble(8);
                                        double changeP = array.optDouble(9);

                                        if (Math.abs(change) >= 100000000000l) {
                                            stockChange.setText((changeP > 0 ? "+" : "")
                                                    + (Util.df.format(change / 1000000000000l) + "万亿"));
                                            stockPercent.setText((changeP > 0 ? "+" : "") + Util.df.format(changeP) + "%");
                                        } else if (Math.abs(change) >= 10000000) {
                                            stockChange.setText(
                                                    (changeP > 0 ? "+" : "") + (Util.df.format(change / 100000000) + "亿"));
                                            stockPercent.setText((changeP > 0 ? "+" : "") + Util.df.format(changeP) + "%");
                                        } else if (Math.abs(change) >= 1) {
                                            stockChange.setText((changeP > 0 ? "+" : "") + change);
                                            stockPercent.setText((changeP > 0 ? "+" : "") + Util.df.format(changeP) + "%");
                                        } else {
                                            stockChange.setText((changeP > 0 ? "+" : "") + Util.getUsedNum(change, 2));

                                            stockPercent
                                                    .setText((changeP > 0 ? "+" : "") + Util.getUsedNum(changeP, 2) + "%");
                                        }
                                    }


                                    stockInfo.setText(array.optString(16).replace("null", "") + "(" + Util.getCurrentTime() + "更新)");

                                    if (array.optDouble(9) > 0) {
                                        stockHeader
                                                .setBackground(getResources().getDrawable(R.drawable.img_zhangbeijing));
                                        oneGuActivity.topColumn
                                                .setBackground(getResources().getDrawable(R.drawable.img_zhangbeijing));
                                    } else if (array.optDouble(9) < 0) {
                                        // 给设置颜色
                                        stockHeader
                                                .setBackground(getResources().getDrawable(R.drawable.img_diebeijing));
                                        oneGuActivity.topColumn
                                                .setBackground(getResources().getDrawable(R.drawable.img_diebeijing));
                                    } else {
                                        stockHeader.setBackground(
                                                getResources().getDrawable(R.drawable.img_buzhangbudiebeijing));
                                        oneGuActivity.topColumn.setBackground(
                                                getResources().getDrawable(R.drawable.img_buzhangbudiebeijing));
                                    }

                                    if (array.optString(16).replace("null", "").equals("停牌")) {
                                        stockHeader.setBackground(
                                                getResources().getDrawable(R.drawable.img_tingpaibeijing));
                                        oneGuActivity.topColumn.setBackground(
                                                getResources().getDrawable(R.drawable.img_tingpaibeijing));
                                    }

                                    stockCode.setText(code);
                                }


                            }
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
    }

    DecimalFormat df = new DecimalFormat("0.00");

    private BarChart everyStockChart;

    private LinearLayout ybpContainer;

    private TextView tv1;

    private TextView tv2;

    private TextView tv3;

    private TextView tv4;

    /**
     * 仪表盘的控件
     */
    //private View ybpView;

    private ScrollView stockScrollView;

    private LinearLayout stockHeader;

    private TextView stockName;

    private TextView stockCode;

    private TextView stockValue;

    private TextView stockChange;

    private TextView stockPercent;

    private TextView stockInfo;

    private ImageView zhizheng1;

    private ImageView zhizheng2;

    private ImageView zhizheng3;

    private ImageView zhizheng4;

    private void setStyle(TextView tv, String str, String pa, boolean shown) {
        SpannableStringBuilder style = new SpannableStringBuilder(
                pa + (shown ? "\n" : "  ") + str.replace("null", "0.0").replace("NaN", "0.0"));
        style.setSpan(new ForegroundColorSpan(ColorUtil.lightGray), 0, pa.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }

    /**
     * 初始化一些基本的控件
     */
    private void initBaseView() {
        // TODO Auto-generated method stub
        // mSwipeRefreshLayout = (SwipeRefreshLayout)
        // rootView.findViewById(R.id.onegu_swiperefresh);
        noDataLinear = (LinearLayout) rootView.findViewById(R.id.fragment_onegu_guzhi_no_data_ll);//

        // noDataText = (TextView) rootView.findViewById(R.id.fragment_onegu_guzhi_no_data_tv);//

        scrollView = (com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView) rootView.findViewById(R.id.tljr_scrollView1);//

		/*
         * scrollView.setScrollViewListener(new MyScrollViewListener() {
		 * 
		 * 
		 * @Override public void onScrollChanged(OneguGuZhiScrollView
		 * scrollView, int x, int y, int oldx, int oldy) { LogUtil.e("y", y +
		 * ""); if (y == 0.0) { oneGuActivity.sw.setEnabled(true); } else {
		 * oneGuActivity.sw.setEnabled(false); } } });
		 */
        scrollView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:

                        LogUtil.e("zzzzz", "" + scrollView.getScrollY());
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

        // scrollView的控件
        // stockScrollView = (ScrollView)
        // rootView.findViewById(R.id.tljr_scrollView1);

        stockJinKai = (TextView) rootView.findViewById(R.id.tljr_txt_hq_jinkai);// 今开
        stockZuoShou = (TextView) rootView.findViewById(R.id.tljr_txt_hq_zuoshou);// 昨收
        stockZueiDi = (TextView) rootView.findViewById(R.id.tljr_txt_hq_zuidi);// 最低

        stockZueiGao = (TextView) rootView.findViewById(R.id.tljr_txt_hq_zuigao);// 最高

        stockShiJinLv = (TextView) rootView.findViewById(R.id.tljr_txt_hq_52zhoudi);// 市净率
        stockShiYinLv = (TextView) rootView.findViewById(R.id.tljr_txt_hq_52zhougao);// 市盈率
        stockChengJiaoLiang = (TextView) rootView.findViewById(R.id.tljr_txt_hq_changes);// 成交量
        stockChengJiaoE = (TextView) rootView.findViewById(R.id.tljr_txt_hq_chengjiaoe);// 成交额
        stockZongShiZhi = (TextView) rootView.findViewById(R.id.tljr_txt_hq_shizhi);// 总市值
        stockLiouTongShiZhi = (TextView) rootView.findViewById(R.id.tljr_txt_hq_chengjiaojia);// 流通市值

        // 每股收益图表
        everyStockChart = (BarChart) rootView.findViewById(R.id.fragment_onegu_guzhi_meigushouyi_chart);//

        ybpContainer = (LinearLayout) rootView.findViewById(R.id.fragment_onegu_guzhi_ybp_container);// 四个仪表盘的容器

        // 线性布局的部分

        stockHeader = (LinearLayout) rootView.findViewById(R.id.bj);// 上面的线性布局

        // hight= topColumn.getMeasuredHeight()+stockHeader.getMeasuredHeight();
        stockName = (TextView) rootView.findViewById(R.id.tljr_txt_hq_name1);// 名字
        stockName.setText(name);
        stockCode = (TextView) rootView.findViewById(R.id.tljr_txt_hq_name2);// 股票名字右边的号码
        stockValue = (TextView) rootView.findViewById(R.id.tljr_txt_hq_now);// 字体比较大的数值
        stockChange = (TextView) rootView.findViewById(R.id.tljr_txt_hq_change);// 小数点
        stockPercent = (TextView) rootView.findViewById(R.id.tljr_txt_hq_changep);// 百分比
        stockInfo = (TextView) rootView.findViewById(R.id.tljr_txt_hq_info);// 收盘情况

        stockInfo.setText(isJJ ? ("基金" + code) : (market + code));

        if (isShenZhenOrShangHai(market)) {

            getEveryStockData();// 获取每股图表数据
            getYbpContainerData();// 获取仪表盘数据

            rootView.findViewById(R.id.fragment_onegu_guzhi_meigushouyi_ll).setVisibility(View.VISIBLE);
            everyStockChart.setVisibility(View.VISIBLE);
            ybpContainer.setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.fragment_onegu_guzhi_meigushouyi_ll).setVisibility(View.GONE);
            everyStockChart.setVisibility(View.GONE);
            ybpContainer.setVisibility(View.GONE);

        }
        //
    }

    /**
     * 判断market是否是深圳或者上海
     */
    private boolean isShenZhenOrShangHai(String market) {
        if ("sz".equals(market) || "sh".equals(market)) {

            return true;
        }
        return false;
    }

    private String param = "";

    private void getParamsFromIntent() {


        JSONObject jo = new JSONObject();
        try {
            jo.put("market", market);
            jo.put("code", code);
            param = jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 对仪表盘的容器操作，获取仪表盘数据
     */
    private void getYbpContainerData() {

        //ybpView = View.inflate(oneGuActivity, R.layout.layout_onegu_guzhi_yibiaopang, null);


        // ybpView.findViewById(R.id.)
        // gc1 = (GaugeChart01View) ybpView.findViewById(R.id.gc1);
        zhizheng1 = (ImageView) rootView.findViewById(R.id.zhizheng1);
        zhizheng2 = (ImageView) rootView.findViewById(R.id.zhizheng2);
        zhizheng3 = (ImageView) rootView.findViewById(R.id.zhizheng3);
        zhizheng4 = (ImageView) rootView.findViewById(R.id.zhizheng4);

		/*
         * gc2 = (GaugeChart01View) ybpView.findViewById(R.id.gc2); gc3 =
		 * (GaugeChart01View) ybpView.findViewById(R.id.gc3); gc4 =
		 * (GaugeChart01View) ybpView.findViewById(R.id.gc4);
		 */

        tv1 = (TextView) rootView.findViewById(R.id.tv1);
        tv2 = (TextView) rootView.findViewById(R.id.tv2);
        tv3 = (TextView) rootView.findViewById(R.id.tv3);
        tv4 = (TextView) rootView.findViewById(R.id.tv4);
        String url = OneGuConstance.getURL("HybjImpf", "hybj", param);
        // LogUtil.e("仪表盘", url);
        ProgressDlgUtil.showProgressDlg("", oneGuActivity);
        NetUtil.sendGet(url, new NetResult() {
            @Override
            public void result(String result) {
                ProgressDlgUtil.stopProgressDlg();
                if (result == null || "".equals(result)) {
                    LogUtil.e("momo", "getYbpContainerData==空");
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int i = jsonObject.optInt("status");
                    if (i != 1) {
                        Toast.makeText(getActivity(), "获取数据有异常", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    analysisJsonObject(jsonObject.optJSONObject("result"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 解析仪表盘数据
     */
    private void analysisJsonObject(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        if (jsonObject == null) {
            return;
        }

        try {
            int total = jsonObject.optInt("num");
            int zsz = jsonObject.optInt("zsz_num");
            int sjl = jsonObject.optInt("sjl_num");

            int syl = jsonObject.optInt("jtsyl_num");
            int jzc = jsonObject.optInt("roe_num");

            tv1.setText("市净率排名:" + sjl + "/" + total);
            tv2.setText("总市值排名:" + zsz + "/" + total);
            tv3.setText("市盈率排名:" + syl + "/" + total);
            tv4.setText("净资产收益率排名:" + jzc + "/" + total);

			/*
             * gc1.setAngle(180 * sjl / total); gc1.chartRender();
			 * gc1.invalidate();
			 */
            float gc1Degrees = (float) 180 * (float) sjl / (float) total;
            float gc2Degrees = (float) 180 * (float) zsz / (float) total;
            float gc3Degrees = (float) 180 * (float) syl / (float) total;
            float gc4Degrees = (float) 180 * (float) jzc / (float) total;
            setDerees(gc1Degrees, zhizheng1);
            setDerees(gc2Degrees, zhizheng2);
            setDerees(gc3Degrees, zhizheng3);
            setDerees(gc4Degrees, zhizheng4);

			/*
             * gc2.setAngle(180 * zsz / total); gc2.chartRender();
			 * gc2.invalidate();
			 * 
			 * gc3.setAngle(180 * syl / total); gc3.chartRender();
			 * gc3.invalidate();
			 * 
			 * gc4.setAngle(180 * jzc / total); gc4.chartRender();
			 * gc4.invalidate();
			 */

            // 把仪表盘的控件添加到仪表盘容器中
           // ybpContainer.addView(ybpView);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 设置角度
     */
    private void setDerees(float gc1Degrees, ImageView zhizheng) {
        float gc1Degrees_ = -90.0f;
        if (gc1Degrees <= 90.0f) {
            gc1Degrees_ = -(90.0f - gc1Degrees);

        } else if (gc1Degrees > 90.0f) {
            gc1Degrees_ = 90.0f - (180.0f - gc1Degrees);

        }

        RotateAnimation animation = new RotateAnimation(0, gc1Degrees_, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        zhizheng.startAnimation(animation);
    }

    /**
     * 获取每股收益的数据
     */
    private void getEveryStockData() {

        String url = OneGuConstance.getURL("MgsyImpf", "mgsy", param);
        LogUtil.e("每股收益2", "url==" + url);
        ProgressDlgUtil.showProgressDlg("", oneGuActivity);
        NetUtil.sendPost(url, new NetResult() {

            @Override
            public void result(String result) {
                ProgressDlgUtil.stopProgressDlg();

                if (result == null || "".equals(result)) {
                    LogUtil.e("momo", "getEveryStockData==空");
                    return;
                }
                try {
                    JSONObject resultJsonObject = new JSONObject(result);

                    int i = resultJsonObject.optInt("status");
                    if (i != 1) {
                        Toast.makeText(getActivity(), "获取数据异常", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (i == 1) {
                        // 获取数据成功
                        JSONArray jsonArray = resultJsonObject.optJSONArray("result");
                        if (jsonArray.length() == 0 || jsonArray == null) {
                            return;
                        }

                        int newRed = Color.parseColor("#fe0017");
                        int newGreen = Color.parseColor("#008e32");

                        List<Integer> colors = new ArrayList<Integer>();
                        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();// y值
                        ArrayList<String> xVals = new ArrayList<String>();// X值
                        for (int j = jsonArray.length() - 1; j >= 0; j--) {
                            JSONObject object = jsonArray.optJSONObject(j);
                            // object.getLong("eNDDATE");// 获取X轴时间
                            Date date = new Date(object.optLong("eNDDATE"));
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
                            String dateString = formatter.format(date);
                            xVals.add(dateString);
                            // object.getDouble("mgsy");// 获取对应Y轴数据
                            yVals1.add(new BarEntry((float) (object.optDouble("mgsy") * 100), j));
                            LogUtil.e("每股收益", object.optDouble("mgsy") + "");
                            // 设置颜色
                            if ((float) object.optDouble("mgsy") > 0) {
                                colors.add(newRed);
                            } else {
                                colors.add(newGreen);
                            }

                        }

                        BarDataSet set1 = new BarDataSet(yVals1, "Data Set");
                        // set1.setColor(Color.parseColor("#EB5041"));
                        set1.setColors(colors);

                        set1.setDrawValues(true);
                        set1.setBarSpacePercent(60f);
                        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
                        dataSets.add(set1);

                        BarData barData = new BarData(xVals, dataSets);
                        initEveryStockChart(barData);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    /**
     * 在每股收益的图表上加载数据显示出来
     *
     * @param barData
     */
    private void initEveryStockChart(BarData barData) {
        // TODO Auto-generated method stub
        everyStockChart.setDescription("");
        // everyStockChart.setNoDataTextDescription("还没有数据哦");
        everyStockChart.setBackgroundColor(Color.WHITE);
        everyStockChart.setDrawGridBackground(false);
        everyStockChart.setDrawBarShadow(false);
        everyStockChart.setHighlightEnabled(false);
        everyStockChart.setDrawValueAboveBar(true);

        // everyStockChart.setVisibleXRange(1);

        YAxis rightAxis = everyStockChart.getAxisRight();
        rightAxis.setEnabled(false);// 右边的Y轴不显示
        // 设置左边Y轴
        YAxis leftAxis = everyStockChart.getAxisLeft();
        leftAxis.setEnabled(true);// 设置左边Y轴显示
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#CDCDCD"));
        leftAxis.setTextColor(Color.parseColor("#CDCDCD"));
        leftAxis.setStartAtZero(false);
        leftAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float arg0) {
                // TODO Auto-generated method stub
                DecimalFormat mFormat = new DecimalFormat("0.0#");
                return mFormat.format(arg0) + "%";
            }
        });
        // 设置X轴
        XAxis xAxis = everyStockChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setEnabled(true);// 显示X轴
        xAxis.setDrawGridLines(true);// 画网格
        xAxis.setGridColor(Color.parseColor("#F5F5F5"));

        // 设置比例图标示
        Legend mLegend = everyStockChart.getLegend();

        mLegend.setEnabled(false);
        everyStockChart.setData(barData);
        everyStockChart.animateX(1000);

    }

    /**
     * 获取从上一个页面彻底过来的数据
     */
    private void initData() {
        // 新页面接收数据
        Bundle bundle = getActivity().getIntent().getExtras();
        code = bundle.getString("code");// 我武生物右边的代码
        name = bundle.getString("name");// 我武生物
        market = bundle.getString("market");
        guKey = bundle.getString("key");
        isJJ = guKey.substring(0, 2).equals("jj");
        getParamsFromIntent();
    }

}
