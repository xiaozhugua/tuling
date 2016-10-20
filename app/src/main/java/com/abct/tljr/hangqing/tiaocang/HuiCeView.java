package com.abct.tljr.hangqing.tiaocang;
  
 import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.linechar.HuiceAll;
import com.abct.tljr.hangqing.util.CalendarImpl;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.qh.common.volley.Response;
import com.qh.common.volley.VolleyError;
import com.qh.common.volley.toolbox.StringRequest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
  
 @SuppressLint({ "InflateParams", "SimpleDateFormat" })
 public class HuiCeView extends BaseActivity implements OnClickListener {
  
     // http://120.24.235.202:8080/ZhiLiYinHang/BackTestServlet?zuid=zh04772&startTime=2015-07-10&endTime=2015-11-16
  
     private SimpleDateFormat dateFormat;
     private TextView startTime;
     private TextView endTime;
     public LineData lineData;
     private View fanghui;
     private Button sure;
     private String zhid;
     private HuiceAll huiceAll;
     private ProgressDialog Progressdialog = null;
     private TextView huicename;
  
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.tljr_huice_view);
         // 初始化变量
         initView();
         // String StartText=CalendarImpl.getBeforeMonthDay(new Date());
         String EndText = dateFormat.format(new Date());
         getJingZhiLieBiao(zhid, "", "");
         startTime.setText(EndText);
         endTime.setText(EndText);
         showDateDialog(startTime.getText().toString(), 1, this);
     }
  
     // 初始化变量
     public void initView() {
         zhid = getIntent().getStringExtra("zhid");
         fanghui = (View) findViewById(R.id.tljr_huice_fanhui);
         fanghui.setOnClickListener(this);
  
         startTime = (TextView) findViewById(R.id.huice_start);
         startTime.setOnClickListener(this);
         endTime = (TextView) findViewById(R.id.huice_end);
         endTime.setOnClickListener(this);
  
         sure = (Button) findViewById(R.id.huice_sure);
         sure.setOnClickListener(this);
  
         dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  
         startTime.setText(dateFormat.format(new Date()));
         endTime.setText(dateFormat.format(new Date()));
  
         huicename = (TextView) findViewById(R.id.huice_view_name);
         huicename.setText(getIntent().getStringExtra("zuname"));
     }
  
     @Override
     public void onClick(View v) {
         switch (v.getId()) {
         case R.id.tljr_huice_fanhui:
             finish();
             break;
         case R.id.huice_start:
             showDateDialog(startTime.getText().toString(), 1, this);
             break;
         case R.id.huice_end:
             showDateDialog(endTime.getText().toString(), 2, this);
             break;
         case R.id.huice_sure:
             try {
                 if (lineData != null) {
                     lineData.clearValues();
                 }
                 getJingZhiLieBiao(zhid, startTime.getText().toString(), endTime
                         .getText().toString());
             } catch (Exception e) {
                 // Log.e("huice_message",e.getMessage());
             }
             break;
         default:
             break;
         }
     }
  
     // 修改日期
     public void showDateDialog(String time, final int i, final Context context) {
         try {
             View view = LayoutInflater.from(this).inflate(
                     R.layout.huiceview_datedialog, null);
             final DatePicker datePicker = (DatePicker) view
                     .findViewById(R.id.huice_datedialog);
  
             Date textDate = dateFormat.parse(time);
             Calendar calendar = Calendar.getInstance();
             calendar.setTime(textDate);
             datePicker.setMinDate(dateFormat.parse("2000-01-01").getTime());
             datePicker.setMaxDate(new Date().getTime());
             datePicker.updateDate(calendar.get(Calendar.YEAR),
                     calendar.get(Calendar.MONTH),
                     calendar.get(Calendar.DAY_OF_MONTH));
  
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("请选择时间");
             // builder.setMessage("结束时间要大于开始时间，否则修改不了");
             builder.setView(view);
             builder.setPositiveButton("确定",
                     new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             String TimeString = datePicker.getYear() + "-"
                                     + (datePicker.getMonth() + 1) + "-"
                                     + datePicker.getDayOfMonth();
                             if (i == 1) {
                                 if (checkStartTime(TimeString)) {
                                     startTime.setText(TimeString);
                                 } else {
                                     showToast("开始时间大于等于结束时间,修改不了日期");
                                 }
                             } else if (i == 2) {
                                 if (checkEndTime(TimeString)) {
                                     endTime.setText(TimeString);
                                 } else {
                                     showToast("结束时间小于等于开始时间,修改不了日期");
                                 }
                             }
                         }
                     });
             builder.setNegativeButton("取消", null);
             builder.create().show();
         } catch (Exception e) {
         }
     }
  
     // 判断开始是否超过今天
     public boolean checkStartTime(String time) {
         try {
             // 现在开始的日期
             Date NewDate = dateFormat.parse(time);
             // 显示结束的日期
             Date endDate = dateFormat.parse(endTime.getText().toString());
             if (NewDate.getTime() >= endDate.getTime()) {
                 return false;
             } else {
                 return true;
             }
         } catch (Exception e) {
             return false;
         }
     }
  
     // 判断结束时候小于开始
     public boolean checkEndTime(String time) {
         try {
             // 现在结束的时间
             Date EndDate = dateFormat.parse(time);
             // 显示开始的时间
             Date startDate = dateFormat.parse(startTime.getText().toString());
             if (EndDate.getTime() <= startDate.getTime()) {
                 return false;
             } else {
                 return true;
             }
         } catch (Exception e) {
             return false;
         }
     }
  
     // 显示提示
     public void showToast(String toast) {
         Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
     }
  
     // 根据参数初始化linechar
     public void getJingZhiLieBiao(String zhid, String startTime, String endTime) {
         Progressdialog = ProgressDialog.show(this, "", "获取数据中", false, true,
                 new OnCancelListener() {
                     public void onCancel(DialogInterface dialog) {
                    	 NetUtil.mQueue.cancelAll(this);
                     }
                 });
         if (!startTime.equals("") && !endTime.equals("")) {
             // 生成新的url
             String url = UrlUtil.HuiceUrl + "?zhid=" + zhid + "&startTime=" + startTime
                     + "&endTime=" + endTime;
             // 发送网络请求
             NetUtil.mQueue.add(new StringRequest(url,
                     new Response.Listener<String>() {
                         @Override
                         public void onResponse(String response) {
                             // 解析数据
                             ParseJsonResponse(response);
                         }
                     }, new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError error) {
                             Progressdialog.dismiss();
                             ParseJsonResponse("HuiceNetError");
                             Message message = new Message();
                             message.what = 2;
                             Bundle bundle = new Bundle();
                             bundle.putString("msg", "网络或服务器访问异常");
                             message.setData(bundle);
                             handler.sendMessage(message);
                         }
                     }));
         } else {
             ParseJsonResponse("HuiceNetError");
             Message message = new Message();
             message.what = 2;
             Bundle bundle = new Bundle();
             bundle.putString("msg", "请输入日期");
             message.setData(bundle);
             handler.sendMessage(message);
         }
     }
  
     // 解析json数据
     public void ParseJsonResponse(String response) {
         try {
             // 初始化装载数据的list
             List<Entry> ListJingzhi = new ArrayList<Entry>();
             List<Entry> ListHuShen = new ArrayList<Entry>();
             List<String> ListXValues = new ArrayList<String>();
  
             if (response.equals("HuiceNetError")) {
                 // 只装载一个数据
                 ListJingzhi.add(new Entry((float) (0), 1));
                 ListHuShen.add(new Entry((float) (0), 1));
                 ListXValues.add(dateFormat.format(dateFormat.parse(CalendarImpl.getBeforeMonthDay(new Date()))));
  
             } else {
                 // 获取第一个jsonObject
                 JSONObject firstJson = new JSONObject(response);
                 // 查看返回状态，正常则返回1
                 if (firstJson.getInt("status") == 1) {
                     // 获取msg的json对象
                     JSONObject msgObject = firstJson.getJSONObject("msg");
                     // 获取jingzhiliebiao的jsonObject
                     JSONArray jingzhiliebiao = new JSONArray(
                             msgObject.getString("jingzhiliebiao"));
                     JSONObject tempObject;
                     // 装载数据
                     for (int i = 0; i < jingzhiliebiao.length(); i++) {
                         tempObject = jingzhiliebiao.getJSONObject(i);
                         ListJingzhi.add(new Entry((float) (tempObject
                                 .getDouble("jingzhi") - 1), i));
                         ListHuShen.add(new Entry((float) (tempObject
                                 .getDouble("hs")), i));
                         ListXValues.add(dateFormat.format(Long
                                 .valueOf(tempObject.getString("time"))));
                     }
                 } else {
                     // 只装载一个数据
                     ListJingzhi.add(new Entry((float) (0), 1));
                     ListHuShen.add(new Entry((float) (0), 1));
                     ListXValues
                             .add(dateFormat.format(dateFormat
                                     .parse(CalendarImpl
                                             .getBeforeMonthDay(new Date()))));
                     Message message = new Message();
                     message.what = 2;
                     Bundle bundle = new Bundle();
                     bundle.putString("msg", firstJson.getString("msg"));
                     message.setData(bundle);
                     handler.sendMessage(message);
                 }
             }
  
             // 指数
             LineDataSet hushen = new LineDataSet(ListHuShen, "沪深指数");
             hushen.setColor(Color.argb(255, 25, 98, 175));
             hushen.setCircleColor(Color.argb(255, 25, 98, 175));
             hushen.setLineWidth(2f);
             hushen.setCircleSize(2f);
             hushen.setFillAlpha(65);
             hushen.setDrawValues(false);
             hushen.setHighLightColor(Color.rgb(244, 117, 117));
             hushen.setDrawCircleHole(false);
             hushen.setDrawCircles(false);
  
             // 净值
             LineDataSet jingzhi = new LineDataSet(ListJingzhi, "净值");
             jingzhi.setColor(Color.argb(255, 177, 45, 85));
             jingzhi.setCircleColor(Color.argb(255, 177, 45, 85));
             jingzhi.setLineWidth(2f);
             jingzhi.setCircleSize(2f);
             jingzhi.setFillAlpha(65);
             jingzhi.setDrawValues(false);
             jingzhi.setHighLightColor(Color.rgb(244, 117, 117));
             jingzhi.setDrawCircleHole(false);
             jingzhi.setDrawCircles(false);
  
             lineData = new LineData(ListXValues, hushen);
             lineData.setValueTextColor(Color.BLACK);
             if (hushen != null)
                 lineData.addDataSet(jingzhi);
             Message message = new Message();
             message.what = 1;
             handler.sendMessage(message);
         } catch (Exception e) {
         }
     }
  
     // 创建LineChart图表
     public void buildLineChart(LineData lineData) {
         if (lineData.getDataSetCount() != 0
                 && (lineData.getYValCount() <= (lineData.getXValCount() * 2))) {
             huiceAll = new HuiceAll(
                     (LineChart) findViewById(R.id.huice_linechart), lineData);
             huiceAll.ShowView();
         } else {
             showToast("没有数据");
         }
     }
  
     public void handleMsg(Message msg) {
         super.handleMsg(msg);
         switch (msg.what) {
         case 1:
             buildLineChart(lineData);
             Progressdialog.dismiss();
             break;
         case 2:
             showToast(msg.getData().getString("msg"));
             break;
         default:
             break;
         }
     };
  
     @Override
     protected void onDestroy(){
         lineData.clearValues();
         System.gc();
         super.onDestroy();
     }
     
 } 