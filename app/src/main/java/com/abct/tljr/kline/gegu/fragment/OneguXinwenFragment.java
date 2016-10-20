package com.abct.tljr.kline.gegu.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.model.AppInfo;
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
 * onegu的新闻fragment
 */
public class OneguXinwenFragment extends BaseFragment {

    private View                 rootView;
    // private SwipeRefreshLayout mSwipeRefreshLayout;
    // private LinearLayout layoutContainer;
    private OneGuActivity oneGuActivity;
    private String        code;
    private String        name;
    private String        market;
    private String        guKey;
    private boolean       isJJ;
    private JSONObject    marketInfo;
    //private LinearLayout         companeyNewsContainer;
    // private LinearLayout businessNewsContainer;
    private ScrollView    scrollView;
    // private ListView companeyNewsListView;
    private ArrayList<View> companeyNewsViewList = new ArrayList<View>();
    private TextView companeyNewsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        rootView = View.inflate(getActivity(), R.layout.fragment_onegu_xinwen, null);
        oneGuActivity = (OneGuActivity) this.getActivity();

        // initRootView();
        return rootView;
    }

    private boolean rootViewFloat = false;
    //private LinearLayout businessNewsContainer;
    private TextView businessNewsText;
    /**
     * 记录的是scrollView的竖直滑动的距离
     */
    private static int scrollY = 0;

    private static int scrollY2 = 0;

    @Override
    public void onStop() {
        scrollY2 = scrollY;

        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * 初始化根布局
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initRootView() {
        // TODO Auto-generated method stub

        if (rootViewFloat) {
            return;
        }
        rootViewFloat = true;

        // 公司新闻容器
        //companeyNewsContainer = (LinearLayout) rootView.findViewById(R.id.fragment_onegu_xinwen_layout_companey_news);//
        // 公司新闻的listView

        // 行业新闻容器
        //businessNewsContainer = (LinearLayout) rootView.findViewById(R.id.fragment_onegu_xinwen_layout_business_news);

        businessNewsText = (TextView) rootView.findViewById(R.id.fragment_onegu_xinwen_business_tv);// 行业新闻的加载状况
        companeyNewsText = (TextView) rootView.findViewById(R.id.fragment_onegu_xinwen_layout_companey_news_tv);//公司新闻加载状况

        scrollView = (ScrollView) rootView
                .findViewById(R.id.fragment_onegu_xinwen_scrollView);
   /*     scrollView = (com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView) rootView
                .findViewById(R.id.fragment_onegu_xinwen_scrollView);*///

       /* scrollView.setScrollViewListener(new MyScrollViewListener() {
            @Override
            public void onScrollChanged(OneguGuZhiScrollView scrollView, int x, int y, int oldx, int oldy) {
                scrollY = y;
            }
        });*/


        // 用第二种方法来做
        scrollView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:

                        // LogUtil.e("zzzzz", "" + scrollView.getScrollY());
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

        initData();
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


    public void initMarket() {
        // TODO Auto-generated method stub
        marketInfo = Constant.marketInfo.get(market.toLowerCase());
        try {
            getData();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 从上一个页面彻底过来的数据
     */
    public void initData() {
        // 新页面接收数据
        Bundle bundle = oneGuActivity.getIntent().getExtras();
        code = bundle.getString("code");// 我武生物右边的代码
        name = bundle.getString("name");// 我武生物
        market = bundle.getString("market");
        guKey = bundle.getString("key");
        isJJ = guKey.substring(0, 2).equals("jj");
    }

    /**
     * 获取网络数据
     */
    public void getData() throws JSONException {
        ProgressDlgUtil.showProgressDlg("", oneGuActivity);
        String url = UrlUtil.Url_235 + "8080/QhOtherServer/stock_url/get?" + "market=" + market + "&code=" + code;
        LogUtil.e("xinwen-url==", url);
        NetUtil.sendPost(UrlUtil.Url_235 + "8080/QhOtherServer/stock_url/get", "market=" + market + "&code=" + code,
                new NetResult() {

                    @Override
                    public void result(final String msg) {
                        ProgressDlgUtil.stopProgressDlg();
                        oneGuActivity.post(new Runnable() {

                            @Override
                            public void run() {
                                // mSwipeRefreshLayout.setRefreshing(false);
                                if (msg == null || "".equals(msg)) {

                                    companeyNewsText.setText("沒有获取数据");
                                    businessNewsText.setText("没有获取数据");
                                    return;

                                } else {
                                    try {
                                        JSONObject object = new JSONObject(msg);

                                        int itemHigh = AppInfo.dp2px(oneGuActivity, 44);

                                        // 行业新闻
                                        JSONArray inewsJsonArray = object.optJSONArray("inews");
                                        if (inewsJsonArray == null || inewsJsonArray.length() == 0) {
                                            businessNewsText.setText("行业新闻没有数据");
                                        } else {

                                            ListView businessListView = (ListView) rootView.findViewById(R.id.fragment_onegu_xinwen_business_lv);
                                            businessListView.setVisibility(View.VISIBLE);
                                            if (inewsJsonArray.length()!=0){
                                                businessNewsText.setVisibility(View.GONE);
                                            }
                                            ArrayList<View> businessList = new ArrayList<View>();
                                            for (int i = 0; i < inewsJsonArray.length(); i++) {

                                                JSONObject inewsObj = inewsJsonArray.optJSONObject(i);

                                                View inewsView = getOneInewsView(inewsObj.optString("title"),
                                                        inewsObj.optLong("time"), name, inewsObj.optString("url"));
                                                businessList.add(inewsView);

                                            }
                                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                    LayoutParams.MATCH_PARENT,
                                                    itemHigh * inewsJsonArray.length() + (inewsJsonArray.length() - 1)
                                                            * businessListView.getDividerHeight());
                                            businessListView.setAdapter(new MyAdapter(businessList));
                                            businessListView.setLayoutParams(layoutParams);
                                        }

                                        JSONArray tabs = marketInfo.optJSONArray("tabs");
                                        for (int j = 0; j < tabs.length(); j++) {
                                            String s = tabs.optJSONObject(j).optString("key");
                                            if (s == null || "".equals(s)) {
                                                LogUtil.e("momo", "s==null或者是为空字符串");

                                                continue;

                                            }

                                            //公司新闻
                                            if (object.has(s) && "news".equals(s)) {
                                                JSONArray array = object.optJSONArray(s);
                                                View v = null;
                                                if (array.length() == 0 || array == null) {
                                                    companeyNewsText.setText("没有数据");
                                                } else {
                                                    ListView companeyListView = (ListView) rootView.findViewById(R.id.fragment_onegu_xinwen_companey_news_lv);
                                                    companeyListView.setVisibility(View.VISIBLE);
                                                    if (array.length()!=0){
                                                        companeyNewsText.setVisibility(View.GONE);
                                                    }
                                                    for (int i = 0; i < array.length(); i++) {

                                                        JSONObject obj = array.optJSONObject(i);
                                                        v = getOneXw(obj.optString("title"), obj.optString("datetime"),
                                                                name, obj.optString("url"));

                                                        companeyNewsViewList.add(v);
                                                    }
                                                    // 给listview设置适配器
                                                    companeyListView.setAdapter(new MyAdapter(companeyNewsViewList));
                                                    // 设置listview的宽高
                                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                            LayoutParams.MATCH_PARENT,
                                                            itemHigh * array.length() + (array.length() - 1)
                                                                    * companeyListView.getDividerHeight());
                                                    companeyListView.setLayoutParams(layoutParams);
                                                }

                                                scrollView.smoothScrollTo(0, 0);
                                            }

                                        }

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                    }
                });


    }

    private void showErrorDialog(int num) {
        View view = View.inflate(oneGuActivity, R.layout.layout_hqsb, null);
        Button chongzaiButton = (Button) view.findViewById(R.id.layout_hqsb_chongzai);//
        chongzaiButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    getData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        switch (num) {
            case 1:


                break;
            case 2:

                break;
            default:
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {
        private ArrayList<View> list;

        @SuppressWarnings("unused")
        public MyAdapter(ArrayList<View> companeyNewsViewList) {
            // TODO Auto-generated constructor stub
            this.list = companeyNewsViewList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (list != null) {
                return list.get(position);
            }
            return null;
        }

    }

    /**
     * 返回行业新闻的条目
     */
    @SuppressWarnings("unused")
    private View getOneInewsView(String title, long time, final String name, final String url) {
        View v = View.inflate(oneGuActivity, R.layout.tljr_item_syxw2, null);
        ((TextView) v.findViewById(R.id.tljr_txt_syxw)).setText(title);

        v.findViewById(R.id.tljr_txt_syxw_time).setVisibility(View.VISIBLE);

        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        ((TextView) v.findViewById(R.id.tljr_txt_syxw_time)).setText(dateFormater.format(date));

        v.setPadding(0, 10, 0, 10);
        if (!url.equals("")) {
            v.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    LogUtil.e("getOneInewsView", "url==" + url);
                    Intent intent = new Intent(oneGuActivity, WebActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    bundle.putString("name", name);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
        return v;
    }

    /**
     * 返回条目
     */
    public View getOneXw(final String title, final String time, final String name, final String url) {
        View v = View.inflate(oneGuActivity, R.layout.tljr_item_syxw2, null);
        ((TextView) v.findViewById(R.id.tljr_txt_syxw)).setText(title);
        // TextView comefrom = (TextView)
        // v.findViewById(R.id.tljr_txt_syxw_laiyuan);// 新闻来源
        // comefrom.setText(source);
        if (time != null) {
            v.findViewById(R.id.tljr_txt_syxw_time).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tljr_txt_syxw_time)).setText(time);
        }
        v.setPadding(0, 10, 0, 10);
        if (!url.equals("")) {
            v.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // LogUtil.e("getOneInewsView", "url=="+url);
                    Intent intent = new Intent(oneGuActivity, WebActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    bundle.putString("name", name);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
        return v;
    }

}
