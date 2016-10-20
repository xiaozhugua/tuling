package com.abct.tljr.chart;
 
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.WeSeeAdapter.WeseeBean;
import com.abct.tljr.dialog.EditDialog;
import com.qh.common.listener.CompleteStr;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class WeSeeView implements OnRefreshListener {
   Activity activity;
   View RealTimeView;
   ListView listview;
   public ArrayList<WeseeBean> array;
   private WeSeeAdapter adapter;
   private RelativeLayout rl_bedown;
//   private EditText edit;
//   private TextView send;
   private TextView bedown_number;
   private String TAG = "WeSeeView";
   private long uptime = 0;
   private int loadingsize = 10;
   private int position = 0;
   private boolean isloading = false;
   private String tempid = "";
   private int noshownumber = 0;	
   private SwipeRefreshLayout mSwipeRefreshLayout=null;
   
   
   public WeSeeView(Activity activity) {
       this.activity = activity;
       RealTimeView = activity.getLayoutInflater().inflate(R.layout.tljr_chart_edit2, null);
       mSwipeRefreshLayout=(SwipeRefreshLayout)RealTimeView.findViewById(R.id.wuseeview_refresh);
       mSwipeRefreshLayout.setOnRefreshListener(this);
	   mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
       findView();
//        initData(true);
       Ask(10,"",1,false,false);
   }
 
   public View getView() {
       return RealTimeView;
   }
 
   
   //获得列表
      public void Ask(int size,String id,int direction,final boolean updatanews,final boolean isaddhistory){
          try {
               String param = "size="+size+"&type=1";
               if(!id.equals("")){
                   param += "&id="+id+"&direction="+direction;
               }
               if(isaddhistory){
                   if(!tempid.equals("")){
                       if(tempid.equals(id)){
                           return;
                       }
                   }else{
                       tempid = id;
                   }
               }
                NetUtil.sendGet(UrlUtil.Url_chat_getchatlist, 
                       param
                       ,new NetResult() {
                   @Override
                   public void result(String msg) {
                	   try{
                		   if(msg.equals("error")){
                               if(!isaddhistory && !updatanews){
                                   handler.sendEmptyMessage(7);
                               }
                           }else{
                               JSONObject js =new JSONObject(msg);
                               if(js!=null&&js.getInt("status") == 1){
                                   JSONArray arr = js.getJSONArray("msg");
                                   ArrayList<WeseeBean> l = new ArrayList<WeSeeAdapter.WeseeBean>();
                                   if(isaddhistory){
                                       position = array.size();
                                       for(int i = arr.length()-1; i>=0;i--){
                                            JSONObject ob = arr.getJSONObject(i);
                                            WeseeBean we = new WeseeBean();
                                            we.uid = ob.getString("uid");
                                            we.nickname = ob.getString("nickname");
                                            we.content = ob.getString("msg");
                                            we.time = ob.getLong("date");
                                            we.avatar = ob.getString("avatar");
                                            we.id = ob.getString("id");
                                            l.add(we);
                                        }
                                       array.addAll(0, l);
                                       handler.sendEmptyMessage(8);
                                   }else{
                                       if(updatanews){
                                           for(int i = arr.length()-1; i>=0;i--){
                                               JSONObject ob = arr.getJSONObject(i);
                                               WeseeBean we = new WeseeBean();
                                               we.uid = ob.getString("uid");
                                               we.nickname = ob.getString("nickname");
                                               we.content = ob.getString("msg");
                                               we.time = ob.getLong("date");
                                               we.avatar = ob.getString("avatar");
                                               we.id = ob.getString("id");
                                               l.add(we);
                                           }
                                           if(l.size()>0){
                                               if(!l.get(l.size()-1).id.equals(array.get(array.size()-1).id)){
                                                   array.addAll(l);
                                                   if(array.size()>100){
                                                       array.remove(0);
                                                   }
                                                   noshownumber += arr.length();
                                                   handler.sendEmptyMessage(1);
                                               }
                                           }
                                       }else{
                                           for(int i = arr.length()-1; i>=0;i--){
                                               JSONObject ob = arr.getJSONObject(i);
                                               WeseeBean we = new WeseeBean();
                                               we.uid = ob.getString("uid");
                                               we.nickname = ob.getString("nickname");
                                               we.content = ob.getString("msg");
                                               we.time = ob.getLong("date");
                                               we.avatar = ob.getString("avatar");
                                               we.id = ob.getString("id");
                                               l.add(we);
                                           }
                                           array.clear();
                                           array.addAll(l);
                                           handler.sendEmptyMessage(0);
                                       }
                                   }
                               }
                           }   
                	   }catch(Exception e){
                		   
                	   }
                   }
               });
           } catch (Exception e) {
               // TODO Auto-generated catch block
               handler.sendEmptyMessage(7);
               e.printStackTrace();
           }
       }
      
    //发表
      public void send(String msg){
           try {
               
        	   NetUtil.sendPost(UrlUtil.Url_chat_sendchat, "uid="+MyApplication.getInstance().self.getId()+"&msg="+msg+"&type=1"+"&token="+Configs.token, new NetResult() {

   				@Override
   				public void result(final String msg) {

   					handler.post(new Runnable() {

   						@Override
   						public void run() {
   						    if(msg.equals("error")){
                            }else if(msg.equals("")){
//                                 handler.sendEmptyMessage(3);
                            }else{
                               try{
                             	  JSONObject js =new  JSONObject(msg);
                                   if(js!=null&&js.getInt("status") == 1){
                                       handler.sendEmptyMessage(9);
                                   }else{
                                   }
                               }catch(Exception e){
                             	  
                               }
                                
                            }
   						}
   					});

   				}
   			});
        	   
//               RequestParams params = new RequestParams();
//                  params.addBodyParameter("token", Configs.token);
//                  params.addBodyParameter("uid", MyApplication.getInstance().self.getId());
//                  params.addBodyParameter("msg", msg);
//                  params.addBodyParameter("type", "1");
//                  XUtilsHelper.sendPost(UrlUtil.Url_chat_sendchat,
//                          params, new HttpCallback() {
//                           @Override
//                           public void callback(String data) {
//                               // TODO Auto-generated method stub
//                               if(data.equals("error")){
//                               }else if(data.equals("")){
////                                    handler.sendEmptyMessage(3);
//                               }else{
//                                  try{
//                                	  JSONObject js =new  JSONObject(data);
//                                      if(js!=null&&js.getInt("status") == 1){
//                                          handler.sendEmptyMessage(9);
//                                      }else{
//                                      }	
//                                  }catch(Exception e){
//                                	  
//                                  }
//                                   
//                               }
//                           }
//                       });
           } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
       }
   
   
 
   String path;
 
   private void findView() {
//        RealTimeView.findViewById(R.id.im_send_image).setVisibility(View.GONE);
//        RealTimeView.findViewById(R.id.im_more).setVisibility(View.GONE);
//        RealTimeView.findViewById(R.id.im_audio).setVisibility(View.GONE);
       
       rl_bedown = (RelativeLayout) RealTimeView.findViewById(R.id.bedown);
       bedown_number = (TextView) RealTimeView.findViewById(R.id.bedown_unumber);
       rl_bedown.setOnClickListener(onclick);
       
//       edit = (EditText) RealTimeView.findViewById(R.id.im_send_text);
//       edit.addTextChangedListener(warcher);
//       send = (TextView) RealTimeView.findViewById(R.id.im_send_btn);
//       send.setOnClickListener(onclick);
       RealTimeView.findViewById(R.id.write).setOnClickListener(onclick);
       listview = (ListView) RealTimeView.findViewById(R.id.list);
      
       array = new ArrayList<WeseeBean>();
//        adapter = new WeSeeAdapter(activity, array,handler);
//        listview.setAdapter(adapter);
       
       listview.setOnScrollListener(new OnScrollListener() {
 
           @Override
           public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
               // TODO Auto-generated method stub
               if(arg1+arg2 == arg3){
                   rl_bedown.setVisibility(View.GONE);
                   noshownumber = 0;
               }
           }
 
           @Override
           public void onScrollStateChanged(AbsListView arg0, int arg1) {
               // TODO Auto-generated method stub
               
           }
       });
       
       // array = new ArrayList<WeSeeBean>();
       // adapter = new WeSeeAdapter(activity, array);
       // listview.setAdapter(adapter);
   }
 
   final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
 
   private OnClickListener onclick = new OnClickListener() {
 
       @Override
       public void onClick(View arg0) {
           // TODO Auto-generated method stub
           switch (arg0.getId()) {
           case R.id.write:
//               send(edit.getText().toString());
//               edit.setText("");
        	   new EditDialog(activity, "大家看大盘","", new CompleteStr() {
				
				@Override
				public void completeStr(String msg) {
					// TODO Auto-generated method stub
					if(!msg.equals("")){
						send(msg);
					}
				}
			}).show();
               break;
           case R.id.bedown:
               listview.setSelection(listview.FOCUS_DOWN);
               rl_bedown.setVisibility(View.GONE);
               noshownumber = 0;
               break;
           default:
               break;
           }
       }
   };
 
   public Handler handler = new Handler() {
       public void handleMessage(android.os.Message msg) {
           if(ChartActivity.islive){
               switch (msg.what) {
               case 0:
                   //第一次更新
                   adapter = new WeSeeAdapter(activity, array,handler);
                   listview.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
                   listview.requestFocus();
                   listview.setSelection(listview.getBottom());
                   
                   RealTimeView.findViewById(R.id.jiazai).setVisibility(View.GONE);
//                handler.sendEmptyMessageDelayed(4, 1000);
                   break;
               case 1:
                   //新消息来的处理
                   adapter.notifyDataSetChanged();
                   if(listview.getFirstVisiblePosition()>=array.size()-7){
                       listview.setSelection(listview.FOCUS_DOWN);
                   }else{
                       rl_bedown.setVisibility(View.VISIBLE);
                       bedown_number.setText(noshownumber+"");
                   }
//                listview.setSelection(listview.FOCUS_DOWN);
                   break;
//            case 2:
//                RealTimeView.findViewById(R.id.im_ll_record).setVisibility(
//                        View.GONE);
//                RealTimeView.findViewById(R.id.im_key).setVisibility(View.GONE);
//                RealTimeView.findViewById(R.id.im_audio).setVisibility(
//                        View.VISIBLE);
//                break;
               case 3:
                   adapter.notifyDataSetChanged();
                   break;
               case 4:
                   listview.requestFocus();
                   listview.setSelection(listview.getBottom());
//                listview.setSelection(listview.FOCUS_DOWN);
                   break;
               case 7:
                   //加载失败
                   ((TextView)RealTimeView.findViewById(R.id.jiazai)).setText("加载失败,重新加载中...");
                   break;
               case 8:
                   adapter.notifyDataSetChanged();
//                   listview.onRefreshComplete();
                   mSwipeRefreshLayout.setRefreshing(false);
                   listview.setSelection(array.size()-position);
                   break;
               case 9:
                   Ask(10, array.get(array.size()-1).id, 1, true,false);
                   break;
               default:
                   break;
               }
           }
       };
   };


   @Override
   public void onRefresh() {
	   loadingsize = loadingsize + 10;
       isloading = true;
       if(array.size()>90){
           Toast.makeText(activity, "聊天信息过多,不能显示更多聊天信息", Toast.LENGTH_LONG).show();
           //listview.onRefreshComplete();
           mSwipeRefreshLayout.setRefreshing(false);
       }else{
           Ask(10, array.get(0).id, -1, true, true);
       }
   }
 
//   private TextWatcher warcher = new TextWatcher() {
// 
//       @Override
//       public void onTextChanged(CharSequence arg0, int arg1, int arg2,
//               int arg3) {
//           // TODO Auto-generated method stub
//           if (edit.getText().toString().equals("")) {
//               send.setVisibility(View.GONE);
//           } else {
//               send.setVisibility(View.VISIBLE);
//           }
//       }
// 
//       @Override
//       public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//               int arg3) {
//           // TODO Auto-generated method stub
// 
//       }
// 
//       @Override
//       public void afterTextChanged(Editable arg0) {
//           // TODO Auto-generated method stub
// 
//       }
//   };
   
}
