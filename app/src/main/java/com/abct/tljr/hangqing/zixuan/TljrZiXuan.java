package com.abct.tljr.hangqing.zixuan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.hangqing.database.OneFenZuModel;
import com.abct.tljr.hangqing.database.OneFenZuRealmImpl;
import com.abct.tljr.hangqing.database.ZiXuanGuRealmImpl;
import com.abct.tljr.hangqing.model.ZiXuanOneGu;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.qh.common.model.User;

/**
 * Created by Administrator on 2016/2/14.
 */

public class TljrZiXuan extends Fragment implements OnRefreshListener {

    private static View zixuan=null;
    private static Context context=null;
    public static Map<String,ZiXuanOneGu> ZiXuanGu=null;
    public SwipeRefreshLayout mSwipeRefreshLayout=null;
    public static Refresh mRefresh=null;
    public String msg1;
    public String msg2;
    
	@SuppressWarnings("static-access")
	@Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
    	zixuan=inflater.from(getActivity()).inflate(R.layout.tljr_page_zixuan,container,false);
    	this.context=getActivity();
        IntentFilter intentFilter=new IntentFilter("com.tljr.zixuan");
        mRefresh=new Refresh();
        context.registerReceiver(mRefresh,intentFilter);
        //加载自选视图
        if(context!=null){
            this.zixuan= LayoutInflater.from(context).inflate(R.layout.tljr_page_zixuan,null);
            ZiXuanGu=new HashMap<String,ZiXuanOneGu>();
            mSwipeRefreshLayout=(SwipeRefreshLayout)zixuan.findViewById(R.id.zixuan_swiperefresh);
    		mSwipeRefreshLayout.setOnRefreshListener(this);
    		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
    		if(MyApplication.getInstance().self != null ){
       		   mSwipeRefreshLayout.post(new Runnable() {
       			@Override
       			public void run() {
       				mSwipeRefreshLayout.setRefreshing(true);
       			}
       		   });
           }
        }
        if(User.getUser()!=null){
        	((RelativeLayout)zixuan.findViewById(R.id.zixuan_login)).setVisibility(View.GONE);
            ((RelativeLayout)zixuan.findViewById(R.id.zixuan_view)).setVisibility(View.VISIBLE);
        }else{
        	((RelativeLayout)zixuan.findViewById(R.id.zixuan_login)).setVisibility(View.VISIBLE);
            ((RelativeLayout)zixuan.findViewById(R.id.zixuan_view)).setVisibility(View.GONE);
            ((RelativeLayout)zixuan.findViewById(R.id.zixuan_login)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(context,WXEntryActivity.class);
					context.startActivity(intent);
				}
			});
        }
    	return zixuan;
    }

    public static void initZiXuan(){
    		//初始化linechart和自选股
    		ZiXuanOneGu gu=null;
            ZiXuanGu.clear();
            List<OneFenZuModel> listOneZu=new ArrayList<OneFenZuModel>();
            OneFenZuModel model=null;
            for (OneFenZu oneFenZu:ZiXuanUtil.fzMap.values()){
            	model=new OneFenZuModel();
            	model.setBeizu(oneFenZu.getTag());
            	model.setName(oneFenZu.getName());
            	model.setTime(oneFenZu.getTime()+"");
            	model.setZuid(oneFenZu.getId());
            	listOneZu.add(model);
                List<OneGu> l=oneFenZu.getList();
                for (int j=0;j<l.size();j++) {
                	gu=GuDealImpl.getZiXuanGu(l.get(j));
                    ZiXuanGu.put(gu.getCode(),gu);
                }
            }
            if(MyApplication.getInstance().self!=null){
            	OneFenZuRealmImpl.addUpdateZuHe(listOneZu);
                ZiXuanGuRealmImpl.saveAndUpdateZiXuanGu(ZiXuanGu);
            }
            if(zixuan!=null){
                new TljrZiXuanGu(context,zixuan);
            }
    }
    
    //获取视图
	public View getView(){
        return this.zixuan;
    }

    //调用下拉刷新
	@Override
	public void onRefresh() {
		try{
			TljrZiXuanGu.reflush(mHandler);
		}catch(Exception e){
		
		}
	}

	class Refresh extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			sendMessage(1);
		}
	}
	
	public void sendMessage(int msg){
		Message message=Message.obtain();
		message.what=msg;
		mHandler.sendMessage(message);
	}
	
	@SuppressLint("HandlerLeak")
	final Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				if(mSwipeRefreshLayout!=null){
					mSwipeRefreshLayout.post(new Runnable() {
		    			@Override
		    			public void run() {
		    				mSwipeRefreshLayout.setRefreshing(false);
		    			}
		    		});
				}
				break;
			case 2:
				mSwipeRefreshLayout.setRefreshing(false);
				break;
			case 3:
				
				break;
			case 4:
				
				break;
			}
		};
	};

}
