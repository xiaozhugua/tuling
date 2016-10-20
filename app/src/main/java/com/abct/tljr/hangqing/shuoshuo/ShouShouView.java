package com.abct.tljr.hangqing.shuoshuo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.chart.ChartActivity;
import com.abct.tljr.chart.RealTimeView;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ShouShouView {

	private TextView zongtext=null;
	private TextView zongxiangqing=null;
	private TextView zongtime=null;
	private SimpleDateFormat data=null;
	private View zong=null;
	private String msg1=null;
	private String msg2=null;
	private long time=0L;
	
	public ShouShouView(final Context context){
		zong=(View)LayoutInflater.from(context).inflate(R.layout.zixuanshuoshuo,null);
		zongtext=(TextView)zong.findViewById(R.id.zongtext1);
		zongxiangqing=(TextView)zong.findViewById(R.id.zongxiangqing);
		zongtime=(TextView)zong.findViewById(R.id.zongtime);
		data=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		zongtime.setText(data.format(new Date()));
		zong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (User.getUser() == null) {
	                Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
	            }else{
	            	Intent intent = new Intent(context,ChartActivity.class);
		            RealTimeView.redpointisshow = false;
		            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            context.startActivity(intent);
	            }
			}
		});
		initView();
	}
	
	public void initView(){
		getZongData(1);
		
	}
	
	public View getView(){
		return this.zong;
	}
	
	public void getZongData(int i) {
	       String param = "size=" + i;
	       NetUtil.sendGet(UrlUtil.Url_chat_getchatlist, param, new NetResult() {
	           @Override
	           public void result(String msg) {
	               try {
	                   JSONObject object = new JSONObject(msg);
	                   if (object.getInt("status") == 1) {
	                       JSONArray array = object.getJSONArray("msg");
	                       if (array.length() == 1) {
	                           msg1 = array.getJSONObject(0).getString("msg");
	                           time=array.getJSONObject(0).getLong("date");
	                       } else if (array.length() == 2) {
	                           msg1 = array.getJSONObject(0).getString("msg");
	                           msg2 = array.getJSONObject(1).getString("msg");
	                       }
	                       zongtext.setText("        "+msg1);
	                       zongtime.setText(data.format(new Date(time)));
	                   } else {
	                   }
	               } catch (Exception e) {
	 
	               }
	           }
	       });
	   }
	
}
