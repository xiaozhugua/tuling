package com.abct.tljr.ui.yousuan;

import java.util.ArrayList;
import java.util.List;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.yousuan.adapter.YouSuanMyAdapter;
import com.abct.tljr.ui.yousuan.model.YouSuanMyItem;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class YouSuanMy extends Fragment implements OnClickListener{

	private View yousuan=null;
	private ListView YousuanList=null;
	private YouSuanMyAdapter adapter=null;
	public List<YouSuanMyItem> listYouSuan=null;
	private String YouSuanMyUrl=UrlUtil.Url_235+"8080/yousuan/rest/gentou/getMyGenTouList";
	private int page=1;
	private RelativeLayout messagetip=null;
	private RelativeLayout messageLogin=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		yousuan=inflater.inflate(R.layout.occft_fragment_yousuan,container,false);
		initView();
		return yousuan;
	}
	
	public void initView(){
		YousuanList=(ListView)yousuan.findViewById(R.id.yousuan_list);
		listYouSuan=new ArrayList<YouSuanMyItem>();
		messagetip=(RelativeLayout)yousuan.findViewById(R.id.yousuan_nulljilu);
		messageLogin=(RelativeLayout)yousuan.findViewById(R.id.yousuan_login);
	}
	
	public void initData(){
		if(User.getUser()==null){
			loginTip();
			return ;
		}
		String params="uid="+User.getUser().getId()+"&token="+Configs.token+"&page="+page+"&size=10";
        LogUtil.e("YouSuanMyUrl",YouSuanMyUrl+"?"+params);
		NetUtil.sendGet(YouSuanMyUrl,params,new NetResult() {
			@Override
			public void result(String response) {
				ZhiYanParseJson.parseYouSuanMyData(response,listYouSuan);
				if(listYouSuan.size()>0){
					adapter=new YouSuanMyAdapter(getActivity(),R.layout.yousuan_listitem,listYouSuan);
					mHandler.sendEmptyMessage(1);
				}else{
					notDataTip();
				}
			}
		});
	}
	
	public void loginTip(){
		YousuanList.setVisibility(View.GONE);
		messagetip.setVisibility(View.GONE);
		messageLogin.setVisibility(View.VISIBLE);
	}
	
	public void notDataTip(){
		YousuanList.setVisibility(View.GONE);
		messagetip.setVisibility(View.VISIBLE);
		messageLogin.setVisibility(View.GONE);
	}
	
	public void ZhangDataTip(){
		YousuanList.setVisibility(View.VISIBLE);
		messagetip.setVisibility(View.GONE);
		messageLogin.setVisibility(View.GONE);
	}
	
	final Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				ZhangDataTip();
				YousuanList.setAdapter(adapter);
				initRadioButton();
				break;
			}
		};
	};
	
	public void initRadioButton(){
		MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment.wode.setText("我的("+listYouSuan.size()+")");
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.zhiyanmy_denglu:
			Intent intent=new Intent(getActivity(),WXEntryActivity.class);
			getActivity().startActivity(intent);
			break;
		}
	}
	
}
