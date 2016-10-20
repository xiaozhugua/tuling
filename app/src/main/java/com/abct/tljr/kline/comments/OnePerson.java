package com.abct.tljr.kline.comments;
 
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.kline.comments.XScrollView.IXScrollViewListener;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
 
public class OnePerson extends BaseActivity {
 
   private int page = 0;
   private String TAG = "OnePerson";
   private XScrollView mScrollView;
   private boolean isupdata = false;
   private ArrayList<CommentsBean> arraylist;
   private ArrayList<View> views;
   private LayoutInflater inflater;
   private String name = "";
   private LinearLayout linearlist;
   private TextView load;
   private int presize = 0;
   private boolean havemore = false;
   public static boolean islive = false;
 
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       // TODO Auto-generated method stub
       super.onCreate(savedInstanceState);
       setContentView(R.layout.tljr_activity_comment_oneperson);
       islive = true;
       findview();
   }
 
   private void findview() {
       findViewById(R.id.tljr_img_futures_join_back).setOnClickListener(
               onclick);
       load = (TextView) findViewById(R.id.load);
       load.setOnClickListener(onclick);
       linearlist = (LinearLayout) findViewById(R.id.list);
       arraylist = new ArrayList<CommentsBean>();
       views = new ArrayList<View>();
       inflater = LayoutInflater.from(this);
       name = getIntent().getStringExtra("name");
 
       mScrollView = (XScrollView) findViewById(R.id.tljr_sy_sc);
       mScrollView.initWithContext(OnePerson.this);
       mScrollView.setPullRefreshEnable(true);
       mScrollView.setPullLoadEnable(true);
       mScrollView.setAutoLoadEnable(false);
       mScrollView.setRefreshTime(Util.getNowTime());
       // mScrollView.startLoadMore();
       mScrollView.setIXScrollViewListener(new IXScrollViewListener() {
           @Override
           public void onRefresh() {
               isupdata = true;
               getlist(false);
           }
 
           @Override
           public void onLoadMore() {
               if (load.getVisibility() == View.VISIBLE) {
                   getlist(true);
               }
           }
       });
 
       getlist(false);
   }
 
   private void initview(boolean isload) {
       if (isload) {
       } else {
           linearlist.removeAllViews();
           // views.clear();
       }
 
       for (int i = presize; i < arraylist.size(); i++) {
 
           View view = inflater.inflate(
                   R.layout.tljr_activity_comment_onepersonitem, null);
           CommentsBean bean = arraylist.get(i);
 
           if (i == 0) {
               ((TextView) findViewById(R.id.name)).setText(bean.getUname());
               StartActivity.imageLoader.displayImage(bean.getAvatar(),
                       (ImageView) findViewById(R.id.im));
           }
 
           ((TextView) view.findViewById(R.id.time)).setText(Util
                   .getDateMY(bean.getTime()));
           String contents = "";
           if (bean.getComment_content() != null) {
               try{
            	   JSONArray arr =new JSONArray(bean.getComment_content());
                   for (int j = 0; j < arr.length(); j++) {
                       JSONObject ob = arr.getJSONObject(j);
                       if (j == 0) {
                           ((TextView) view.findViewById(R.id.content3))
                                   .setText(ob.getString("uname") + "#" + name
                                           + "#" + ob.getString("content"));
                       } else {
                           contents = "||" + ob.getString("uname") + ":"
                                   + ob.getString("content");
                       }
                   }
                   JSONObject ob1 = arr.getJSONObject(0);
               }catch(Exception e){
            	   
               }
               // ((TextView)view.findViewById(R.id.content3)).setText(bean.getComment_content());
           } else {
               view.findViewById(R.id.becontent).setVisibility(View.GONE);
           }
           ((TextView) view.findViewById(R.id.content)).setText(bean
                   .getContent() + contents);
           linearlist.addView(view);
           // views.add(view);
       }
 
       if (havemore) {
           load.setVisibility(View.VISIBLE);
       } else {
           load.setVisibility(View.GONE);
       }
       // 结束刷新
       if (isupdata) {
           mScrollView.setRefreshTime(Util.getNowTime());
           mScrollView.stopRefresh();
           isupdata = false;
       }
   }
 
   private void getlist(final boolean isload) {
	   
	   
	   NetUtil.sendPost(UrlUtil.URL_Comments_getbyoid, "uid="+MyApplication.getInstance().self.getId()+"&cid="+getIntent().getStringExtra("cid")+"&page="+page, new NetResult() {

				@Override
				public void result(final String msg) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							 try{
		                    	  // TODO Auto-generated method stub
		                          if (!msg.equals("error")) {
		                              JSONObject ob =new JSONObject(msg);
		                              if (ob.optBoolean("isok")) {
		                                  JSONArray arr = ob.getJSONArray("datas");
		                                  if (!isload) {
		                                      arraylist.clear();
		                                      presize = 0;
		                                  } else {
		                                      presize = arraylist.size();
		                                  }
		                                  if (ob.optInt("page") > 0) {
		                                      page = ob.optInt("page");
		                                      havemore = true;
		                                  } else {
		                                      havemore = false;
		                                  }
		                                  for (int i = 0; i < arr.length(); i++) {
		                                      JSONObject ob1 = arr.getJSONObject(i);
		                                      CommentsBean bean = new CommentsBean();
		                                      bean.setAvatar(ob1.getString("avatar"));
		                                      bean.setCid(ob1.getString("cid"));
		                                      bean.setContent(ob1.getString("content"));
		                                      bean.setId(ob1.getString("id"));
		                                      bean.setPraise(ob1.optInt("praise"));
		                                      bean.setTime(ob1.optLong("time"));
		                                      bean.setTread(ob1.optInt("tread"));
		                                      bean.setIspraise(ob1.optBoolean("ispraise"));
		                                      bean.setIstread(ob1.optBoolean("istread"));
		                                      bean.setUid(ob1.getString("uid"));
		                                      bean.setUname(ob1.getString("uname"));
		                                      if (ob1.getString("comment_values") != null) {
		                                          bean.setComment_content(ob1.getString("comment_values"));
		                                      }
		                                      // arraylist.add(bean);
		                                      arraylist.add(0, bean);
		                                  }
		                                  post(new Runnable() {
		                                      public void run() {
		                                          if(islive){
		                                              initview(isload);
		                                          }
		                                      }
		                                  });
		                              }
		                          }
		                      }catch(Exception e){
		                    	  
		                      }
						}
					});

				}
			});
		   
//       RequestParams params = new RequestParams();
//       params.addBodyParameter("cid", getIntent().getStringExtra("cid"));
//       params.addBodyParameter("uid", getIntent().getStringExtra("uid"));
//       params.addBodyParameter("page", page + "");
//       XUtilsHelper.sendPost(UrlUtil.URL_Comments_getbyoid, params,
//               new HttpCallback() {
//                   @Override
//                   public void callback(String data) {
//                    
//                	   try{
//                    	  // TODO Auto-generated method stub
//                          if (!data.equals("error")) {
//                              JSONObject ob =new JSONObject(data);
//                              if (ob.optBoolean("isok")) {
//                                  JSONArray arr = ob.getJSONArray("datas");
//                                  if (!isload) {
//                                      arraylist.clear();
//                                      presize = 0;
//                                  } else {
//                                      presize = arraylist.size();
//                                  }
//                                  if (ob.optInt("page") > 0) {
//                                      page = ob.optInt("page");
//                                      havemore = true;
//                                  } else {
//                                      havemore = false;
//                                  }
//                                  for (int i = 0; i < arr.length(); i++) {
//                                      JSONObject ob1 = arr.getJSONObject(i);
//                                      CommentsBean bean = new CommentsBean();
//                                      bean.setAvatar(ob1.getString("avatar"));
//                                      bean.setCid(ob1.getString("cid"));
//                                      bean.setContent(ob1.getString("content"));
//                                      bean.setId(ob1.getString("id"));
//                                      bean.setPraise(ob1.optInt("praise"));
//                                      bean.setTime(ob1.optLong("time"));
//                                      bean.setTread(ob1.optInt("tread"));
//                                      bean.setIspraise(ob1.optBoolean("ispraise"));
//                                      bean.setIstread(ob1.optBoolean("istread"));
//                                      bean.setUid(ob1.getString("uid"));
//                                      bean.setUname(ob1.getString("uname"));
//                                      if (ob1.getString("comment_values") != null) {
//                                          bean.setComment_content(ob1.getString("comment_values"));
//                                      }
//                                      // arraylist.add(bean);
//                                      arraylist.add(0, bean);
//                                  }
//                                  post(new Runnable() {
//                                      public void run() {
//                                          if(islive){
//                                              initview(isload);
//                                          }
//                                      }
//                                  });
//                              }
//                          }
//                      }catch(Exception e){
//                    	  
//                      }
//                   }
//               });
   }
 
   private OnClickListener onclick = new OnClickListener() {
 
       @Override
       public void onClick(View arg0) {
           // TODO Auto-generated method stub
           switch (arg0.getId()) {
           case R.id.tljr_img_futures_join_back:
               finish();
               break;
           case R.id.load:
               getlist(true);
               break;
           default:
               break;
           }
       }
   };
 
   @Override
   protected void onDestroy() {
       // TODO Auto-generated method stub
       islive = false;
       super.onDestroy();
   }
   
 
}