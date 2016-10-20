package com.abct.tljr.chart;
 
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.abct.tljr.R;
import com.abct.tljr.utils.Util;
 
public class FutureAskViewAdapter extends BaseAdapter{
   Context context;
   ArrayList<AskBean> array;
   public static HashMap<String,AskBean > map;//点击回答所用的数据
   
   public FutureAskViewAdapter(Context context,ArrayList<AskBean> array){
       this.context = context;
       this.array = array;
       map = new HashMap<String,AskBean >();
   }
   
   @Override
   public int getCount() {
       // TODO Auto-generated method stub
       return array.size();
   }
 
   @Override
   public Object getItem(int arg0) {
       // TODO Auto-generated method stub
       return array.get(arg0);
   }
 
   @Override
   public long getItemId(int arg0) {
       // TODO Auto-generated method stub
       return arg0;
   }
 
   @Override
   public View getView(int position, View v, ViewGroup arg2) {
       // TODO Auto-generated method stub
       ViewHolder  holder = null;
       if(v == null){
           holder = new ViewHolder();
           v = View.inflate(context, R.layout.activity_chart_futrueitem, null);
           holder.name = (TextView) v.findViewById(R.id.name);
           holder.content = (TextView) v.findViewById(R.id.content);
           holder.time = (TextView) v.findViewById(R.id.time);
           holder.name2 = (TextView) v.findViewById(R.id.name2);
           holder.content2 = (TextView) v.findViewById(R.id.content2);
           holder.time2 = (TextView) v.findViewById(R.id.time2);
           holder.rl = (RelativeLayout) v.findViewById(R.id.rl_blew);
           v.setTag(holder);
       }else{
           holder = (ViewHolder) v.getTag();
       }
       if(position<array.size()){
    	   
    	   AskBean ask = array.get(position);
    	   ask.position = position;
    	   holder.name.setText(ask.nickname);
//       holder.time.setText(Util.getDateOnlyHour(ask.date));
    	   holder.time.setText(Util.getDateOnlyDat(System.currentTimeMillis()).equals(Util.getDateOnlyDat(ask.date))
    			   ?Util.getDateOnlyHour(ask.date) : Util.getDateOnlyDat(ask.date));
    	   holder.content.setText(ask.msg);
//        Log.d("ChartActivity", "answers :"+ask.answers);
    	  try{
    		  JSONArray arr =new JSONArray(ask.answers);
       	   if(arr!=null&&arr.length()>0){
       		   holder.rl.setVisibility(View.VISIBLE);
       		   JSONObject ob = arr.getJSONObject(0);
       		   AskBean a = new AskBean();
       		   a.id = ob.getString("id");
       		   a.isfocus = ob.getBoolean("isfocus");
       		   a.msg = ob.getString("msg");
       		   a.nickname = ob.getString("nickname");
       		   a.focus = ob.getString("focus");
       		   a.avatar = ob.getString("avatar");
       		   a.unfocus = ob.getString("step");
       		   a.uid = ob.getString("uid");
       		   a.isunfocus = ob.getBoolean("isstep");
       		   map.put(ask.id, a);
       		   holder.name2.setText(ob.getString("nickname"));
       		   holder.content2.setText(ob.getString("msg"));
//              holder.time2.setText(Util.getDateOnlyHour(ob.getLongValue("date")));
       		   holder.time2.setText(Util.getDateOnlyDat(System.currentTimeMillis()).equals(Util.getDateOnlyDat(ob.getLong("date")*1000))
       				   ?Util.getDateOnlyHour(ob.getLong("date")*1000) : Util.getDateOnlyDat(ob.getLong("date")*1000));  
    		   
       	   }else{
    		   holder.rl.setVisibility(View.GONE);
    	   }
    	  }catch(Exception e){
    		  
    	  }
    	   v.findViewById(R.id.ask).setTag(ask);
    	   v.findViewById(R.id.ask).setOnClickListener(new OnClickListener() {
    		   @Override
    		   public void onClick(View v) {
    			   // TODO Auto-generated method stub
    			   Intent i = new Intent(context, OneAskActivity.class);
    			   i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                OneAskActivity.id = ((AskBean) v.getTag()).id;
    			   i.putExtra("msg", ((AskBean) v.getTag()).msg);
    			   i.putExtra("answers", ((AskBean) v.getTag()).answers);
    			   i.putExtra("id", ((AskBean) v.getTag()).id);
    			   i.putExtra("nickname", ((AskBean) v.getTag()).nickname);
    			   i.putExtra("focus", ((AskBean) v.getTag()).focus);
    			   i.putExtra("date", ((AskBean) v.getTag()).date);
    			   i.putExtra("isfocus", ((AskBean) v.getTag()).isfocus);
    			   i.putExtra("position", ((AskBean) v.getTag()).position);
    			   i.putExtra("uid", ((AskBean) v.getTag()).uid);
    			   context.startActivity(i);
    		   }
    	   });
    	   holder.rl.setTag(ask);
    	   holder.rl.setOnClickListener(new OnClickListener() {
    		   
    		   @Override
    		   public void onClick(View v) {
    			   // TODO Auto-generated method stub
    			   Intent i = new Intent(context,OneAnswer.class);
//                i.putExtra("answers",JSONArray.parseArray(((AskBean) v.getTag()).answers).get(0).toString());
    			   i.putExtra("title", ((AskBean) v.getTag()).msg);
    			   i.putExtra("id", ((AskBean) v.getTag()).id);
    			   i.putExtra("type", "FutureAskViewAdapter");
    			   context.startActivity(i);
    		   }
    	   });
       }else{
    	   return null;
       }
//        holder.name2
       return v;
   }
   
   public final class ViewHolder{
       TextView name;
       TextView time;
       TextView content;
       TextView name2;
       TextView time2;
       TextView content2;
       RelativeLayout rl;
   }
   public static class AskBean{
       String answers;
       long date;
       String focus;
       String unfocus;
       String id ;
       String msg;
       String nickname;
       String type ;
       String uid ;
       boolean isfocus;
       boolean isunfocus;
       String avatar;
       int position;
   }
}