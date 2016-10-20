package com.abct.tljr.hangqing.jingwu;
  
 import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.adapter.JingWuAdapter;
import com.abct.tljr.hangqing.adapter.JingWuItemAdapter;
import com.abct.tljr.hangqing.model.JingWuItemModel;
import com.abct.tljr.hangqing.model.JingWuModel;
import com.abct.tljr.hangqing.model.RankViewDataModel;
import com.abct.tljr.hangqing.util.CalendarImpl;
import com.abct.tljr.hangqing.widget.datepicker.cons.DPMode;
import com.abct.tljr.hangqing.widget.datepicker.views.DatePicker;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.UrlUtil;
import com.qh.common.volley.Response;
import com.qh.common.volley.VolleyError;
import com.qh.common.volley.toolbox.StringRequest;
  
 public class JingWuActivity extends BaseActivity implements OnClickListener {
  
     private RelativeLayout time;
     private GridView mGridView;
     private List<JingWuModel> listGrid;
     private JingWuAdapter jingWuAdapter;
     //private ZrcListView jingWuListView;
     private List<JingWuItemModel> listItem;
     private JingWuItemAdapter jingwuitemAdapter;
     private View tljr_jingwu_fanhui;
     private List<RankViewDataModel> ListRankView;
     private TextView date;
     private int index = 0;
     private UpdateCheckItem mUpdateCheckItem;
     private SimpleDateFormat mSimpleDateFormat;
     private String lastTime;
     List<RankViewDataModel> selectlistModel = null;
     private int parseStatus = 0;
     private DecimalFormat df;
     private DatePicker mDatePicker;
     private Context context;
     private Calendar mCalendar;
     private SharedPreferences mSharedPreferences=null;
     private ImageView imageback=null;
     
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.jingwuselect);
         this.context=this;
         mCalendar=Calendar.getInstance();
         checkFirst();
         //初始化各个参数
         initView();
         //初始化顶部的GridView
         initGridView(ListRankView, 1);
         //加载数据第一次数据
         LoadMore();
     }
  
     @SuppressWarnings("unchecked")
     @SuppressLint("SimpleDateFormat")
     public void initView() {
         time = (RelativeLayout) findViewById(R.id.tljr_jingwu_time);
         time.setOnClickListener(this);
         mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
         mGridView = (GridView) findViewById(R.id.tljr_rankview_jingwu_gridview);
         listGrid = new ArrayList<JingWuModel>();
         listItem = new ArrayList<JingWuItemModel>();
         tljr_jingwu_fanhui = (View) findViewById(R.id.tljr_jingwu_fanhui);
         tljr_jingwu_fanhui.setOnClickListener(this);
         df = new DecimalFormat("00");
         date = (TextView) findViewById(R.id.jingwu_content);
//       date.setText(getIntent().getStringExtra("date"));
         ListRankView = (ArrayList<RankViewDataModel>) getIntent().getSerializableExtra("jingwudata");
  
         IntentFilter updateIntent = new IntentFilter();
         updateIntent.addAction("com.example.jingwuchange");
         mUpdateCheckItem = new UpdateCheckItem();// 更新个股
         registerReceiver(mUpdateCheckItem, updateIntent);
  
         lastTime = getIntent().getStringExtra("lastTime");
         selectlistModel = new ArrayList<RankViewDataModel>();
  
         mDatePicker=(DatePicker)findViewById(R.id.jingwu_datepicker);
         mDatePicker.setDate(mCalendar.get(Calendar.YEAR),(mCalendar.get(Calendar.MONTH)+1));
         mDatePicker.setMode(DPMode.SINGLE);
         
         mDatePicker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
			@Override
			public void onDatePicked(String date){
		       List<String> listDate=getDate(date);
		       String StrMonth=null; 
		       int month=Integer.valueOf(listDate.get(1));
		       if(month<10){
		    	   StrMonth="0"+month;
		       }else{
		    	   StrMonth=""+month;
		       }
		       String StrDay=null;
		       int day=Integer.valueOf(listDate.get(2));
		       if(day<10){
		    	   StrDay="0"+day;
		       }else{
		    	   StrDay=""+day;
		       }
		       selectTime(listDate.get(0)+"-"+ StrMonth+"-"+ StrDay);
			}
		});
         
     }
  
     public void checkFirst(){
    	 mSharedPreferences=getSharedPreferences("jingwuactivityfirst",MODE_PRIVATE);
    	 boolean first=mSharedPreferences.getBoolean("first",false);
    	 if(!first){
    		 //是第一次登录
    		 imageback=((ImageView)findViewById(R.id.jingwuhelp));
    		 imageback.setVisibility(View.VISIBLE);
    		 imageback.setOnClickListener(this);
    	 }
     }
     
     public void initGridView(List<RankViewDataModel> ListRankViews, int status) {
         listGrid.clear();
         JingWuModel model = null;
         RankViewDataModel RankViewModel = null;
         for (int i = 0; i < ListRankViews.size() + 1; i++) {
             if (i == 0) {
                 String action = null;
                 List<Integer> listint = CalendarImpl.getListTime(ListRankViews.get(0).getTimes());
                 int actionstatus=CalendarImpl.getActionStatus(ListRankViews.get(0).getTimes());
                 if(actionstatus==1){
                     action = "交易中";
                 }else if(actionstatus==2){
                     action = "停市中";
                 }else if(actionstatus==4||actionstatus==6){
                     action = "已闭市";
                 }else if(actionstatus==3||actionstatus==5){
                	 action="将开市";
                 }
                 model = new JingWuModel(CalendarImpl.getWeekDay(ListRankViews
                         .get(0).getTimes()), action, listint.get(0) + "",
                         listint.get(2) + "年" + listint.get(1) + "月", "", 1, "","");
             } else {
                 RankViewModel = ListRankViews.get(i - 1);
                 model = new JingWuModel("--", "--", "--",
                         RankViewModel.getName(), RankViewModel.getIconUrl(), 2,RankViewModel.getCode(),RankViewModel.getMarket());
             }
             listGrid.add(model);
         }
  
         date.setText(RankViewModel.getTime() + "推荐");
         updateFlushData(ListRankViews);
  
         if (status == 1) {
             jingWuAdapter = new JingWuAdapter(this,R.layout.tljr_rankview_item, listGrid);
             mGridView.setAdapter(jingWuAdapter);
         }
     }
  
     public void updateFlushData(List<RankViewDataModel> ListRankViews) {
         // 获取股票列表参数
         String parm = "list=";
         for (int i = 0; i < ListRankViews.size(); i++) {
             if (i == 0) {
                 parm += ListRankViews.get(i).getMarket() + "|"
                         + ListRankViews.get(i).getCode();
             } else {
                 parm += "," + ListRankViews.get(i).getMarket() + "|"
                         + ListRankViews.get(i).getCode();
             }
         }
         
         // 网上获取最新数据
         Util.getRealInfo(parm, new NetResult() {
             @Override
             public void result(final String msg) {
                 try {
                     final org.json.JSONObject object = new org.json.JSONObject(msg);
                     if (object.getInt("code") == 1) {
                         JSONArray arr = object.getJSONArray("result");
                         for (int i = 0; i < arr.length(); i++) {
                             JSONObject obj = arr.getJSONObject(i);
                             String code = obj.optString("code");
                             JSONArray array = obj.getJSONArray("data");
                             for (int j = 0; j < listGrid.size(); j++) {
                                 if (listGrid.get(j).code.equals(code)) {
                                     listGrid.get(j).setActionornum((float) array.getDouble(8) + "");
                                     listGrid.get(j).setTimeormoney((float) array.getDouble(0) + "");
                                     listGrid.get(j).setDataorpercent((float) array.getDouble(9) + "");
                                 }
                             }
                         }
                         Message message = new Message();
                         message.what = 1;
                         handler.sendMessage(message);
                     }
                 } catch (Exception e) {
                 }
             }
         },true);
     }
  
     @Override
     public void onClick(View v) {
         switch (v.getId()) {
         case R.id.tljr_jingwu_time:
             showTimeDialog();
             break;
         case R.id.tljr_jingwu_fanhui:
             finish();
             break;
         case R.id.jingwuhelp:
        	 if(imageback!=null&&mSharedPreferences!=null){
        		 imageback.setVisibility(View.GONE);
        		 mSharedPreferences.edit().putBoolean("first",true).commit();
        	 }
        	 break;
         default:
             break;
         }
     }
  
     public void showTimeDialog() {
         View view = LayoutInflater.from(this).inflate(
                 R.layout.huiceview_datedialog, null);
         try {
//             final DatePicker datePicker = (DatePicker) view
//                     .findViewById(R.id.huice_datedialog);
//             datePicker.setMinDate(mSimpleDateFormat.parse(lastTime).getTime());
//             datePicker.setMaxDate(new Date().getTime());
//             AlertDialog.Builder builder = new AlertDialog.Builder(this);
//             builder.setTitle("请选择时间");
//             builder.setView(view);
//             builder.setPositiveButton("确定",
//                     new DialogInterface.OnClickListener() {
//                         @Override
//                         public void onClick(DialogInterface dialog, int which) {
//                             selectTime(datePicker.getYear() + "-"
//                                     + df.format(datePicker.getMonth() + 1)
//                                     + "-"
//                                     + df.format(datePicker.getDayOfMonth()));
//                         }
//                     });
//             builder.setNegativeButton("取消", null);
//             builder.create().show();
         } catch (Exception e) {
  
         }
     }
  
     public void selectTime(String time) {
         MyApplication.requestQueue.add(new StringRequest(UrlUtil.URl_JingWu_load
                 + "?time=" + time, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 ParseSelectTime(response);
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
             }
         }));
     }
  
     public void ParseSelectTime(String json) {
         try {
             JSONObject object = new JSONObject(json);
             if (object.getInt("code") == 1) {
                 JSONArray array = object.getJSONArray("data");
                 JSONObject tempObject = null;
                 RankViewDataModel model = null;
                 selectlistModel.clear();
                 for (int i = 0; i < array.length(); i++) {
                     tempObject = array.getJSONObject(i);
                     model = new RankViewDataModel(tempObject.getString("code"),
                             tempObject.optString("grade"),
                             tempObject.optString("iconUrl"),
                             tempObject.optString("name"),
                             tempObject.optString("reason"),
                             tempObject.optString("time"), mSimpleDateFormat
                                     .parse(tempObject.optString("time"))
                                     .getTime(), tempObject.optString("market"));
                     selectlistModel.add(model);
                 }
                 Message message = Message.obtain();
                 message.what = 2;
                 handler.sendMessage(message);
             } else {
                 Message message = Message.obtain();
                 message.what = 3;
                 Bundle bundle = new Bundle();
                 // bundle.putString("msg",object.getString("msg"));
                 bundle.putString("msg", "没有这一天的数据");
                 message.setData(bundle);
                 handler.sendMessage(message);
             }
         } catch (Exception e) {
  
         }
  
     }
  
     public void LoadMore() {
         if(index!=-1){
             String param = "index=" + index + "&need=5";
 //            Log.e("Url_Param",TLUrl.URl_JingWu_load+"?"+param);
             // 网上获取最新数据
             MyApplication.requestQueue.add(new StringRequest(UrlUtil.URl_JingWu_load
                     + "?" + param, new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                     new updateData().execute(response);
                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                 }
             }));
         }else{
             Toast.makeText(getBaseContext(),"已经是最后一条记录了",Toast.LENGTH_SHORT);
         }
         
     }
  
     //解析网络加载下来的数据
     class updateData extends AsyncTask<String, Void, Integer> {
         @Override
         protected Integer doInBackground(String... params) {
             return parseJson(params[0]);
         }
  
         @Override
         protected void onPostExecute(Integer result) {
             if (result == 1) {
                 parseStatus = 1;
                // jingwuitemAdapter = new JingWuItemAdapter(getBaseContext(),R.layout.tljr_rankview_time_list, listItem);
                 //jingWuListView.setAdapter(jingwuitemAdapter);
             } else {
                 //jingwuitemAdapter.notifyDataSetChanged();
                 //jingWuListView.setLoadMoreSuccess();
                // jingWuListView.startLoadMore();
             }
         }
     }
  
     //具体的解析方法
     public int parseJson(String json) {
         try {
             JSONObject firstObject = new JSONObject(json);
             index = firstObject.getInt("index");
             if (firstObject.getInt("code") == 1) {
                 JSONArray array = firstObject.getJSONArray("data");
                 JSONArray tempArray = null;
                 JSONObject tempObject = null;
                 JingWuItemModel mJingWuItemModel;
                 RankViewDataModel model = null;
                 for (int i = 0; i < array.length(); i++) {
                     tempArray = array.getJSONArray(i);
                     List<RankViewDataModel> listModel = new ArrayList<RankViewDataModel>();
                     for (int j = 0; j < tempArray.length(); j++) {
                         tempObject = tempArray.getJSONObject(j);
                         model = new RankViewDataModel(
                                 tempObject.getString("code"),
                                 tempObject.getString("grade"),
                                 tempObject.getString("iconUrl"),
                                 tempObject.getString("name"),
                                 tempObject.getString("reason"),
                                 tempObject.getString("time"), mSimpleDateFormat
                                         .parse(tempObject.optString("time"))
                                         .getTime(),
                                 tempObject.getString("market"));
                         listModel.add(model);
                     }
                     mJingWuItemModel = new JingWuItemModel(listModel.get(0).getTime(), listModel);
                     listItem.add(mJingWuItemModel);
                 }
             }
             
             if (parseStatus == 0) {
                 return 1;
             } else {
                 return 2;
             }
         } catch (Exception e) {
             return 3;
         }
     }
  
     
     public void handleMsg(android.os.Message msg) {
         switch (msg.what) {
         case 1:
             jingWuAdapter.notifyDataSetChanged();
             break;
         case 2:
             initGridView(selectlistModel, 2);
             break;
         case 3:
             Toast.makeText(getBaseContext(), msg.getData().getString("msg"),
                     Toast.LENGTH_SHORT).show();
             break;
         }
     };
  
     // 注册广播
     class UpdateCheckItem extends BroadcastReceiver {
         @Override
         public void onReceive(Context context, Intent intent) {
             int position = intent.getIntExtra("position", 0);
             initGridView(listItem.get(position).getListitem(), 2);
         }
     }
  
     @Override
     protected void onDestroy() {
         super.onDestroy();
         unregisterReceiver(mUpdateCheckItem);
     }
     
     public List<String> getDate(String date){
    	 try{
    		 List<String> listDate=new ArrayList<String>();
    		 int last=date.lastIndexOf("-");
    		 int first=date.indexOf("-");
    		 listDate.add(date.substring(0,first));
    		 listDate.add(date.substring(first+1,last));
    		 listDate.add(date.substring(last+1,date.length()));
    		 return listDate;
    	 }catch(Exception e){
    		 return null;
    	 }
     }
  
 } 