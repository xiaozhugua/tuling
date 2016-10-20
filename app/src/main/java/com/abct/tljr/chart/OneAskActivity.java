package com.abct.tljr.chart;
 
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.FutureAskViewAdapter.AskBean;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.CircularImage;
import com.abct.tljr.utils.Util;
import com.abct.tljr.wxapi.OtherPerson;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
 
public class OneAskActivity extends BaseActivity {
 
   public static String id = "";
   private String msg, nickname, focus, answers,uid;
   private boolean isfocus = false;
   private TextView zan_bt;
   private ListView list;
   private ArrayList<AskBean> arrlist;
   private int page = 1, position;
   public static HashMap<String, AskBean> map;// 点击回答所用的数据
   public static int HaveNewAnswer = 9999;
   private String TAG = "OneAskActivity";
 
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       // TODO Auto-generated method stub
       super.onCreate(savedInstanceState);
       setContentView(R.layout.tljr_activity_oneask);
       // getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.toumin));
       findview();
   }
 
   private void findview() {
       map = new HashMap<String, FutureAskViewAdapter.AskBean>();
       findViewById(R.id.tljr_img_futures_join_back).setOnClickListener(
               onclick);
       findViewById(R.id.addanswer).setOnClickListener(onclick);
       zan_bt = (TextView) findViewById(R.id.zan_bt);
       zan_bt.setOnClickListener(onclick);
       id = getIntent().getStringExtra("id");
       msg = getIntent().getStringExtra("msg");
       nickname = getIntent().getStringExtra("nickname");
       focus = getIntent().getStringExtra("focus");
       answers = getIntent().getStringExtra("answers");
       isfocus = getIntent().getBooleanExtra("isfocus", false);
       position = getIntent().getIntExtra("position", 0);
       uid = getIntent().getStringExtra("uid");
       if (isfocus) {
           zan_bt.setText("取消关注");
           ((GradientDrawable) zan_bt.getBackground()).setColor(Color.GRAY);
       } else {
           zan_bt.setText("关注");
           ((GradientDrawable) zan_bt.getBackground()).setColor(ColorUtil.green);
       }
       ((TextView) findViewById(R.id.name)).setText(nickname);
       ((TextView) findViewById(R.id.time)).setText(Util
               .getDateOnlyHour(getIntent().getLongExtra("date", 0)));
       ((TextView) findViewById(R.id.content)).setText(msg);
       ((TextView) findViewById(R.id.zannumber)).setText(focus);
       
       //问题删除
//       LogUtil.i(TAG, "self uid :"+uid);
//       if(MyApplication.getInstance().self.getId().equals(uid)){
//    	   findViewById(R.id.delete).setVisibility(View.VISIBLE);
//    	   findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				new PromptDialog(OneAskActivity.this, "是否要删除此问题？", new Complete() {
//					@Override
//					public void complete() {
//						// TODO Auto-generated method stub
//						Intent intent = new Intent(); // Itent就是我们要发送的内容
//                                           intent.putExtra("data",
//                                                   "updateFutureposition");
//                                           intent.putExtra("position", position);
//                                           intent.setAction("Tip_ChartActivity"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
//                                           sendBroadcast(intent); // 发送广播
//						finish();
//					}
//				}).show();
//			}
//		});
//       }
       
       arrlist = new ArrayList<FutureAskViewAdapter.AskBean>();
       list = (ListView) findViewById(R.id.list);
       list.setAdapter(new BaseAdapter() {
 
           @Override
           public View getView(int position, View v, ViewGroup arg2) {
               // TODO Auto-generated method stub
               Holder holder;
               AskBean ask = arrlist.get(position);
               if (v == null) {
                   holder = new Holder();
                   v = View.inflate(OneAskActivity.this,
                           R.layout.activity_chart_askitem, null);
                   // holder.touxiang
                   holder.name = ((TextView) v.findViewById(R.id.name));
                   holder.time = ((TextView) v.findViewById(R.id.time));
                   holder.content = ((TextView) v.findViewById(R.id.content));
                   holder.zan = ((TextView) v.findViewById(R.id.zan));
                   holder.image = (CircularImage) v
                           .findViewById(R.id.touxiang);
                   v.setTag(holder);
               } else {
                   holder = (Holder) v.getTag();
               }
               //删除评论
//               if(MyApplication.getInstance().self.getId().equals(ask.uid)){
//            	   v.findViewById(R.id.delete).setVisibility(View.VISIBLE);
//            	   v.findViewById(R.id.delete).setTag(position);
//            	   v.findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(final View arg0) {
//						// TODO Auto-generated method stub
//						new PromptDialog(OneAskActivity.this, "是否要删除此回答？", new Complete() {
//							@Override
//							public void complete() {
//								// TODO Auto-generated method stub
//								arrlist.remove(Integer.parseInt(arg0.getTag().toString()));
//								((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
//							}
//						}).show();
//					}
//				});
//               }else{
//            	   v.findViewById(R.id.delete).setVisibility(View.GONE);
//               }
               map.put(ask.id, ask);
               holder.content.setText(ask.msg);
               holder.zan.setText(ask.focus);
               StartActivity.imageLoader
                       .displayImage(ask.avatar, holder.image);
               if (Util.getDateOnlyDat(System.currentTimeMillis()).equals(
                       Util.getDateOnlyDat(ask.date))) {
                   holder.time.setText(Util.getDateOnlyHour(ask.date));
               } else {
                   holder.time.setText(Util.getDateOnlyDat(ask.date));
               }
               holder.name.setText(ask.nickname);
               holder.id = ask.id;
               holder.image.setTag(position);
               holder.image.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						AskBean bean = arrlist.get(Integer.parseInt(arg0.getTag().toString()));
						Intent i = new Intent(OneAskActivity.this,OtherPerson.class);
						i.putExtra("id", bean.uid);
						i.putExtra("name", bean.nickname);
						i.putExtra("avatar", bean.avatar);
						startActivity(i);
					}
			   });
               holder.content.setTag(holder);
               holder.content.setOnClickListener(new OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       // TODO Auto-generated method stub
                       Intent i = new Intent(OneAskActivity.this,
                               OneAnswer.class);
                       i.putExtra("type", "OneAskActivity");
                       i.putExtra("id", ((Holder) v.getTag()).id);
                       i.putExtra("title", msg);
                       // startActivity(i);
                       startActivityForResult(i, 1);
                   }
               });
               return v;
           }
 
           @Override
           public long getItemId(int arg0) {
               // TODO Auto-generated method stub
               return arg0;
           }
 
           @Override
           public Object getItem(int arg0) {
               // TODO Auto-generated method stub
               return arrlist.get(arg0);
           }
 
           @Override
           public int getCount() {
               // TODO Auto-generated method stub
               return arrlist.size();
           }
       });
       GetanswersList(true);
   }
 
   private OnClickListener onclick = new OnClickListener() {
 
       @Override
       public void onClick(View arg0) {
           // TODO Auto-generated method stub
           switch (arg0.getId()) {
           case R.id.tljr_img_futures_join_back:
               finish();
               break;
           case R.id.addanswer:
               EditDialog dialog = new EditDialog(OneAskActivity.this, handler);
               dialog.show();
               break;
           case R.id.zan_bt:
               if (isfocus) {
                   Ask(id, false);
               } else {
                   Ask(id, true);
               }
               break;
           default:
               break;
           }
       }
   };
 
   // 获取回答列表
   public void GetanswersList(final boolean isupdata) {
       try {
           // &mobile= &avatar=
           NetUtil.sendGet(UrlUtil.URL_Ask_getList,
                   "page=" + page + "&size=10" + "&id=" + id + "&uid="
                           + MyApplication.getInstance().self.getId(),
                   new NetResult() {
                       @Override
                       public void result(String msg) {
                          try{
                        	  if (!msg.equals("error")) {
                                  JSONObject js =new JSONObject(msg);
                                  if (js!=null&&js.getInt("status") == 1) {
                                      JSONArray jr =new JSONArray(js
                                              .getString("msg"));
                                      ArrayList<AskBean> list = new ArrayList<AskBean>();
                                      for (int i = 0; i < jr.length(); i++) {
                                          JSONObject ob = jr.getJSONObject(i);
                                          AskBean ask = new AskBean();
                                          ask.date = ob.getLong("date") * 1000;
                                          ask.isfocus = ob
                                                  .getBoolean("isfocus");
                                          ask.nickname = ob.getString("nickname");
                                          ask.id = ob.getString("id");
                                          ask.msg = ob.getString("msg");
                                          ask.focus = ob.getString("focus");
                                          ask.avatar = ob.getString("avatar");
                                          ask.isunfocus = ob.getBoolean("isstep");
                                          ask.unfocus = ob.getString("step");
                                          ask.uid = ob.getString("uid");
                                          list.add(ask);
                                      }
                                      if (HaveNewAnswer == 9998
                                              && arrlist.size() == 0) {
                                          if (list.size() > 0) {
                                              FutureAskView.array.get(position).answers=jr.toString();
                                              HaveNewAnswer = 9999;
                                              Intent intent = new Intent(); // Itent就是我们要发送的内容
                                              intent.putExtra("data","updateFuture");
                                              intent.setAction("Tip_ChartActivity"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
                                              sendBroadcast(intent); // 发送广播
                                          }
                                      } else {
                                          HaveNewAnswer = 9999;
                                      }
                                      if (isupdata) {
                                          arrlist.clear();
                                      }
                                      arrlist.addAll(list);
                                      if(handler != null){
                                   	   handler.sendEmptyMessage(0);
                                      }
                                  }
                              }
                          }catch(Exception e){
                        	  
                          }
                       }
                   });
       } catch (Exception e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
   }
 
   // 关注
   public void Ask(String id, final boolean zanorno) {
       try {
           // &mobile= &avatar=
           String url = UrlUtil.Url_chat_focus;
           if (zanorno) {
               url += "focus";
           } else {
               url += "unfocus";
           }
           NetUtil.sendGet(url,
                   "uid=" + MyApplication.getInstance().self.getId() + "&id="
                           + id, new NetResult() {
                       @Override
                       public void result(String msg) {
                           // TODO Auto-generated method stub
                          try{
                        	  if (!msg.equals("error")) {
                                  JSONObject js =new JSONObject(msg);
                                  if (js!=null&&js.getInt("status") == 1) {
                                      isfocus = zanorno;
                                      if(handler != null){
                                   	   handler.sendEmptyMessage(1);
                                   	   handler.sendEmptyMessage(9);
                                      }
                                  } else {
                                  }
                              }
                          }catch(Exception e){
                        	  
                          }
                       }
                   });
       } catch (Exception e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
   }
 
   public void handleMsg(Message msg) {
       // TODO Auto-generated method stub
       super.handleMsg(msg);
       switch (msg.what) {
       case 0:
           ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
           break;
       case 1:
           int i;
           if (isfocus) {
               zan_bt.setText("取消关注");
               i = Integer.parseInt(((TextView) findViewById(R.id.zannumber))
                       .getText().toString()) + 1;
               ((GradientDrawable) zan_bt.getBackground())
                       .setColor(Color.GRAY);
           } else {
               zan_bt.setText("关注");
               i = Integer.parseInt(((TextView) findViewById(R.id.zannumber))
                       .getText().toString()) - 1;
               ((GradientDrawable) zan_bt.getBackground())
                       .setColor(ColorUtil.green);
           }
           ((TextView) findViewById(R.id.zannumber)).setText(i + "");
           FutureAskView.array.get(position).focus = i + "";
           FutureAskView.array.get(position).isfocus = isfocus;
           break;
       case 9:
           // 重新更新(将清除list)
           page = 1;
           GetanswersList(true);
           break;
       case 10:
           // EditDialog的更新
           page = 1;
           GetanswersList(true);
           if (arrlist.size() == 0) {
               HaveNewAnswer = 9998;
           }
           break;
       default:
           break;
       }
   };
 
   public final class Holder {
       ImageView touxiang;
       TextView zan;
       TextView name;
       TextView time;
       TextView content;
       String id;
       CircularImage image;
   }
 
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // TODO Auto-generated method stub
       if (requestCode == 1 && resultCode == 1) {
           ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
       }
   }
 
}