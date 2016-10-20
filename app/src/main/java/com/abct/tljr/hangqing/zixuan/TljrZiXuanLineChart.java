package com.abct.tljr.hangqing.zixuan;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.R;
import com.abct.tljr.chart.ChartActivity;
import com.abct.tljr.chart.RealTimeView;
import com.abct.tljr.hangqing.shuoshuo.ShouShouView;
import com.abct.tljr.shouye.SYChart;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

/**
 * Created by Administrator on 2016/2/14.
 */

public class TljrZiXuanLineChart {

    public static SYChart mSYChart=null;
    private LinearLayout mLinearLayout=null;
	private TextView zongtext=null;
	private TextView zongxiangqing=null;
	private TextView zongtime=null;
	private SimpleDateFormat data=null;
	private String msg1=null;
	private String msg2=null;
	private long time=0L;
    
    public TljrZiXuanLineChart(final Activity activity,View mView){
        mSYChart=new SYChart(activity,mView);
        mLinearLayout=(LinearLayout)mView.findViewById(R.id.tljr_page_zixuan_linechart);
        mLinearLayout.addView(mSYChart.getView());
        //mLinearLayout.addView(new ShouShouView(activity).getView());
		zongtext=(TextView)mView.findViewById(R.id.zongtext1);
		zongxiangqing=(TextView)mView.findViewById(R.id.zongxiangqing);
		zongtime=(TextView)mView.findViewById(R.id.zongtime);
		data=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		zongtime.setText(data.format(new Date()));
		mView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (User.getUser() == null) {
	                Toast.makeText(activity,"请先登录",Toast.LENGTH_SHORT).show();
	            }else{
	            	Intent intent = new Intent(activity,ChartActivity.class);
		            RealTimeView.redpointisshow = false;
		            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            activity.startActivity(intent);
	            }
			}
		});
		initView();
        
    }

    public void initView(){
		getZongData(1);
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
