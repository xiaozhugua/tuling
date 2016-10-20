package com.abct.tljr.ranking;
  
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.jingwu.JingWuActivity;
import com.abct.tljr.hangqing.jingwu.JingWuView;
import com.abct.tljr.shouye.SYFerdls;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

 /**
  * @author xbw
  * @version 创建时间：2015年8月8日 下午2:21:21
  */
  
 public class RankView implements OnClickListener {
    public static boolean isGet = false;
    private Activity activity;
    private View v;
    private LinearLayout layout;
    private Handler handler = new Handler();
    private SharedPreferences rankPreference;
    private String RANKKEY = "RANK";
    private JSONArray jsonArray;
    private boolean isInit = false;
    private int time=0;
    private SYFerdls ferdls;
    private JingWuView jingwu;
    
    public View getView() {
         return v;
    }
  
     @SuppressLint("SimpleDateFormat")
    public RankView(Activity activity) {
            this.activity = activity;
            ProgressDlgUtil.showProgressDlg("", this.activity);
            v = View.inflate(activity, R.layout.tljr_activity_rank, null);
            v.findViewById(R.id.tljr_grp_rank_title).setVisibility(View.GONE);
            layout = (LinearLayout) v.findViewById(R.id.tljr_grp_rank_list);
     }
     
     public void initFooterView(){
    	 isInit = true;
         rankPreference = activity.getSharedPreferences("RANKING",Application.MODE_PRIVATE);
         if (!rankPreference.getString(RANKKEY,"").equals(""))
             try {
                 flushUi(new JSONArray(rankPreference.getString(RANKKEY, "")));
             } catch (JSONException e) {
                 e.printStackTrace();
             }
         if (!isGet)
             flushList();
     }
     
     public void init() {
    	 layout.removeAllViews();
         if (isInit) {
             return;
         }
         jingwu=new JingWuView(activity);
         jingwu.setHandler(mHandler);
         layout.addView(jingwu.getView(),0);
     }
  
     private void flushList() {
         ProgressDlgUtil.showProgressDlg("加载数据中", activity);
         NetUtil.sendPost(UrlUtil.URL_ranking,"method=getranking&page=0&size=0&v=3", new NetResult() {
                     @Override
                     public void result(String msg) {
                         ProgressDlgUtil.stopProgressDlg();
                         try {
                             final JSONObject jsonObject = new JSONObject(msg);
                             if (jsonObject.getInt("status")==1) {
                                 handler.post(new Runnable() {
                                     @Override
                                     public void run() {
                                         try {
                                             flushUi(jsonObject.getJSONArray("msg"));
                                             rankPreference.edit().putString(RANKKEY,jsonArray.toString()).commit();
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 });
                             }
                         } catch (JSONException e) {
                             e.printStackTrace();
                             handler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     flushList();
                                 }
                             }, 2000);
                         }
                     }
                 });
     }
  
    @SuppressLint({ "Recycle", "InflateParams" })
     private void flushUi(JSONArray jsonArray) throws JSONException {
         isGet = true;
         this.jsonArray = jsonArray;
         TypedArray arrayId = activity.getResources().obtainTypedArray(R.array.tljr_dot);
         for (int i = 0; i < jsonArray.length(); i++) {
             JSONObject jsonObject = jsonArray.getJSONObject(i);
             View rank = View.inflate(activity, R.layout.tljr_item_onerank, null);
             ((TextView) rank.findViewById(R.id.tljr_item_onerank_title))
                     .setText(jsonObject.getString("name"));
             int m = i % 4;
             rank.findViewById(R.id.tljr_item_onerank_icon).setBackgroundResource(
                     arrayId.getResourceId(m,R.drawable.img_yanjiusuoicon));
             LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                     LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
             params.topMargin = 10;
             rank.setLayoutParams(params);
             LinearLayout l = (LinearLayout) rank.findViewById(R.id.tljr_item_onerank_grp);
             if (jsonObject.getInt("layout") != 2) {
                 rank.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                 l.addView(getGrid(jsonObject.getInt("layout"), jsonObject));
             } else {
                 JSONArray array = jsonObject.getJSONArray("items");
                 for (int j = 0; j < array.length(); j++) {
                     final JSONObject object = array.getJSONObject(j);
                     View v = View.inflate(activity,R.layout.tljr_item_onerank_item, null);
                     ((TextView) v.findViewById(R.id.tljr_rank_name)).setText(object.getString("name"));
                     ((TextView) v.findViewById(R.id.tljr_rank_desc)).setText(object.getString("desc"));
                     if (object.has("first")) {
                         TextView tv = (TextView)v.findViewById(R.id.tljr_rank_high);
                         tv.setVisibility(View.VISIBLE);
                         tv.setText(object.getString("tag"));
                         TextView tv1 = (TextView)v.findViewById(R.id.tljr_rank_high1);
                         tv1.setVisibility(View.VISIBLE);
                         tv1.setText((object.optDouble("high") > 0 ? "+" : "")
                                 + (object.getInt("percent") == 0 ? Util.df
                                         .format(object.optDouble("first"))
                                         : (Util.df.format(object.optDouble("first") * 100) + "%")));
                     }
                     if (!object.optString("iconurl").equals("")) {
                         Util.setImage(object.optString("iconurl"),
                                 v.findViewById(R.id.tljr_rank_icon), handler);
                     }
                     v.setOnClickListener(new OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             try {
                                 startOne(object.getString("name"),
                                         object.getString("reqkey"),
                                         object.getInt("percent"),
                                         object.getString("key"),
                                         object.optString("time"),
                                         object.optInt("layout"));
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                         }
                     });
                     l.addView(v);
                 }
             }
             layout.addView(rank);
         }
         Message message=Message.obtain();
         message.what=3;
         mHandler.sendMessage(message);
     }
  
     // type=1,2个，2，3个
     private GridView getGrid(int type, JSONObject jsonObject) throws JSONException {
         int numcolumns = type == 1 ? 2 : 3;
         GridView gv = new GridView(activity);
         gv.setBackgroundColor(activity.getResources().getColor(R.color.tljr_white));
         gv.setNumColumns(numcolumns);
         gv.setVerticalScrollBarEnabled(false);
         gv.setAdapter(new MyBaseAdapter(jsonObject.getJSONArray("items"), type));
         int count = jsonObject.getJSONArray("items").length();
         count = (count % numcolumns == 0 ? count / numcolumns : (count
                 / numcolumns + 1));
         LinearLayout.LayoutParams params;
         int spacing;
         if (type == 1) {
             spacing = Util.WIDTH * 9 / Util.IMAGEWIDTH;
             params = new LinearLayout.LayoutParams(Util.WIDTH,
                     (int) ((Util.HEIGHT / 4.5f + spacing) * count) + spacing);
         } else {
             spacing = Util.WIDTH * 9 / Util.IMAGEWIDTH;
             params = new LinearLayout.LayoutParams(Util.WIDTH,
                     (int) ((Util.HEIGHT / 4.5f + spacing) * count) + spacing);
         }
         gv.setLayoutParams(params);
         gv.setPadding(spacing, spacing, spacing, spacing);
         // gv.setPadding(0, 0, 0, 0);
         gv.setHorizontalSpacing(spacing);
         gv.setVerticalSpacing(spacing);
         return gv;
     }
  
     private ArrayList<View> getGridView(JSONArray gird, int type)throws JSONException {
         ArrayList<View> list = new ArrayList<View>();
         for (int i = 0; i < gird.length(); i++) {
             final JSONObject object = gird.getJSONObject(i);
             View v;
             AbsListView.LayoutParams params;
             int spacing;
             if (type == 1) {
                 spacing = Util.WIDTH * 9 / Util.IMAGEWIDTH;
                 v = View.inflate(activity,R.layout.tljr_item_onerankgrid_item1, null);
                 ((TextView) v.findViewById(R.id.tljr_rank_nickname))
                         .setText("创建人:" + object.optString("creator"));
                 ((TextView) v.findViewById(R.id.tljr_rank_time)).setText("成立"
                         + ((System.currentTimeMillis() - object
                                 .optLong("createdate")) / 86400000l + 1) + "天");
                 params = new AbsListView.LayoutParams(Util.WIDTH / 2 - spacing,
                         (int) (Util.HEIGHT / 4.5f));
             } else {
                 spacing = Util.WIDTH * 9 / Util.IMAGEWIDTH;
                 v = View.inflate(activity,
                         R.layout.tljr_item_onerankgrid_item3, null);
                 ((TextView) v.findViewById(R.id.tljr_rank_nickname))
                         .setText(object.optString("desc"));
                 params = new AbsListView.LayoutParams(Util.WIDTH / 3 - spacing,
                         (int) (Util.HEIGHT / 4.5f));
             }
             v.setLayoutParams(params);
             if (!object.optString("bgurl").equals("")) {
                 Util.setImage(object.optString("bgurl"),
                         v.findViewById(R.id.tljr_rank_grp), handler);
             }
             ((TextView) v.findViewById(R.id.tljr_rank_name)).setText(object.optString("name"));
             ((TextView) v.findViewById(R.id.tljr_rank_high)).setText(object.optString("tag").replace(":", ""));
             ((TextView) v.findViewById(R.id.tljr_rank_high1)).setText(object.optInt("percent") == 0 ? Util.df
                             .format(object.optDouble("first", 0)) : (Util.df
                             .format(object.optDouble("first", 0) * 100) + "%"));
             v.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     try {
                         startOne(object.getString("name"),
                                 object.getString("reqkey"),
                                 object.getInt("percent"),
                                 object.getString("key"),
                                 object.optString("time"),
                                 object.optInt("layout"));
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             });
             list.add(v);
         }
         return list;
     }
  
     class MyBaseAdapter extends BaseAdapter {
         private ArrayList<View> list;
  
         public MyBaseAdapter(JSONArray gird, int type) throws JSONException {
             this.list = getGridView(gird, type);
         }
  
         @Override
         public View getView(int position, View convertView, ViewGroup parent) {
             return list.get(position);
         }
  
         @Override
         public int getCount() {
             return list.size();
         }
  
         @Override
         public Object getItem(int position) {
             return null;
         }
  
         @Override
         public long getItemId(int position) {
             return 0;
         }
  
     }
  
     private String[] title = { "总收益率", "总收益率", "胜率", "最小回撤率", "最大风险", "周收益率",
             "月收益率", "总决赛收益率" };
  
     private String[] key = { "securities", "zongshouyi", "winrate",
             "bigBackWithdraw", "sharperatio", "zhoushouyi", "yueshouyi",
             "nianshouyi" };
  
     private void startOne(String name, String reqkey, int percent, String key, String time, int layout) {
         activity.startActivity(new Intent(activity, OneRankActivity.class)
                 .putExtra("key", key).putExtra("percent", percent)
                 .putExtra("title", name).putExtra("reqkey", reqkey)
                 .putExtra("time", time).putExtra("layout", layout)
                 .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
     }
  
  
  
     @Override
     public void onClick(View v) {
         switch (v.getId()) {
         case R.id.tljr_rankview_more:
             Intent intent = new Intent(activity, JingWuActivity.class);
             activity.startActivity(intent);
             break;
         default:
             break;
         }
     }
  
     public void oneSecAction() {
         try {
             time++;
             if (time > 3) {
                 ferdls.reflushData();
                 jingwu.flush();
                 time = 0;
             }
         } catch (Exception e) {
         }
     }
     
     final Handler mHandler=new Handler(){
    	 public void handleMessage(android.os.Message msg) {
    		 switch(msg.what){
    		 case 1:
    			  ferdls = new SYFerdls(activity);
    			  ferdls.setHandler(mHandler);
    		      layout.addView(ferdls.getView(),1);
    			 break;
    		 case 2:
    			 initFooterView();
    			 break;
    		 case 3:
    			 ProgressDlgUtil.stopProgressDlg();
    			 break;
    		 }
    	 };
    	 
     };
     
 }
