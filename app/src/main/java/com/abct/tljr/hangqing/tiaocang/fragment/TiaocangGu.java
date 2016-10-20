package com.abct.tljr.hangqing.tiaocang.fragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.tiaocang.ChangePercent_ListViewAdapter;
import com.abct.tljr.hangqing.tiaocang.TljrChangePercent;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.hangqing.util.ParseJson;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TiaocangGu extends Fragment{
	private View viewgu;
	private ListView changepercent_listview=null;
	private List<OneGu> listGu=null;
	private ChangePercent_ListViewAdapter adapter=null;
	public  static TiaocangGuListView mTiaocangGuListView=null;
	public  static TiaocangGuAdd mTiaocangGuAdd=null;
	public static TiaocangUpdateId updateId;
	private ProgressDialog Progressdialog = null;
	private LinearLayout tiaocanggu_content=null;
	private TextView tiaocanggu_tip=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		viewgu=inflater.inflate(R.layout.tljr_zh_tiaocanggu,container,false);
		changepercent_listview=(ListView)viewgu.findViewById(R.id.tiaocanggu_list);
		tiaocanggu_content=(LinearLayout)viewgu.findViewById(R.id.tiaocanggu_content);
		tiaocanggu_tip=(TextView)viewgu.findViewById(R.id.tiaocang_tip);
		listGu=((TljrChangePercent)getActivity()).listGus;
		//删除
		IntentFilter intentFilter=new IntentFilter("tljr_TiaocangGu_update");
		mTiaocangGuListView=new TiaocangGuListView();
		getActivity().registerReceiver(mTiaocangGuListView,intentFilter);
		
		//添加
		IntentFilter intentFilter2=new IntentFilter("tljr_TiaocangGu_updateadd");
		mTiaocangGuAdd=new TiaocangGuAdd();
		getActivity().registerReceiver(mTiaocangGuAdd,intentFilter2);
		
		//更新股ID
		IntentFilter intentFilter3=new IntentFilter("com.example.updateGuId");
		updateId=new TiaocangUpdateId();
		getActivity().registerReceiver(updateId,intentFilter3);
		
		initView();
		return viewgu;
	}

	
	public void initView(){
		adapter=new ChangePercent_ListViewAdapter(
				getActivity(),R.layout.chengepercent_listview_item,listGu,"");
		changepercent_listview.setAdapter(adapter);
		refresh();
		new Timer().schedule(new UpdateTiaoCangGu(),3000,3000);
		if(listGu.size()<=0){
			tiaocanggu_content.setVisibility(View.GONE);
			tiaocanggu_tip.setVisibility(View.VISIBLE);
		}
	}
	
	class TiaocangGuListView extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent){
			String code=intent.getStringExtra("code");
			String market=intent.getStringExtra("market");
			for(int i=0;i<listGu.size();i++){
				if(listGu.get(i).getKey().equals(market+code)){
					listGu.remove(i);
					break;
				}
			}
			adapter.notifyDataSetChanged();
			if(listGu.size()<=0){
				tiaocanggu_content.setVisibility(View.GONE);
				tiaocanggu_tip.setVisibility(View.VISIBLE);
			}
		}
	}
	
	class TiaocangGuAdd extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(tiaocanggu_content.getVisibility()==View.GONE){
				tiaocanggu_content.setVisibility(View.VISIBLE);
				tiaocanggu_tip.setVisibility(View.GONE);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	class TiaocangUpdateId extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Progressdialog = ProgressDialog.show(getActivity(),"","获取数据中", true,false);
			GuDealImpl.getZhGuId(getActivity().getIntent().getStringExtra("zuid"),listGu,mHandler,2);
		}
	}
	
    class UpdateTiaoCangGu extends TimerTask{
		@Override
		public void run() {
			refresh();
		}
    };
	
    public void refresh(){
    	 // 获取股票列表参数
	      String param = "list=";
	      int i=0;	
	      for(OneGu mOneGu:listGu){
	    	  if(i==0){
	    		  param+=mOneGu.getMarket()+"|"+mOneGu.getCode();
	    		  i=1;
	    	  }else{
	    		  param+=","+mOneGu.getMarket()+"|"+mOneGu.getCode();
	    	  }
	      }
	      // 网上获取最新数据
	      Util.getRealInfo(param, new NetResult() {
	          @Override
	          public void result(final String msg) {
	              try {
	            	  ParseJson.ParseJsonListGuInfo(listGu,msg);
	            	  Message message=new Message();
	            	  message.what=1;
	            	  mHandler.sendMessage(message);
	              } catch (Exception e) {
	              }
	          }
	      },true);
    }
    
    final Handler mHandler=new Handler(){
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case 1:
    			adapter.notifyDataSetChanged();
    			break;
    		case 2:
    			Progressdialog.dismiss();
    			break;
    		}
    	};
    };
    
}
