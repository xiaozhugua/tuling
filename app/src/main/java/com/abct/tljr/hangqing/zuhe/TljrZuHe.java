package com.abct.tljr.hangqing.zuhe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.hangqing.adapter.TljrZuheAdapter;
import com.abct.tljr.hangqing.model.ZuHeModel;
import com.abct.tljr.hangqing.tiaocang.TljrChangePercent;
import com.abct.tljr.hangqing.util.ParseJson;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.qh.common.util.UrlUtil;
import com.qh.common.volley.Response;
import com.qh.common.volley.VolleyError;
import com.qh.common.volley.toolbox.StringRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/2/15.
 */
public class TljrZuHe extends Fragment implements OnRefreshListener,OnItemClickListener, OnClickListener {

	private View tljrzuhe = null;
	private static GridView zuheView = null;
	public static List<ZuHeModel> listzuhe = null;
	public static TljrZuheAdapter zuheAdapter = null;
	private static Context context = null;
	private static List<OneFenZu> listZu = null;
	private static SwipeRefreshLayout mSwipeRefreshLayout = null;
	private static LinearLayout page_zuhe = null;
	private Button page_zuhe_bu=null;
	private static SharedPreferences.Editor editor=null;
	private SharedPreferences GetTime=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		this.context = getActivity();
		editor=getActivity().getSharedPreferences("zuheupdatetime",getActivity().MODE_PRIVATE).edit();
		GetTime=getActivity().getSharedPreferences("zuheupdatetime",getActivity().MODE_PRIVATE);
		tljrzuhe = LayoutInflater.from(context).inflate(R.layout.tljr_page_zuhe, null);
		page_zuhe=(LinearLayout)tljrzuhe.findViewById(R.id.tljr_page_zuhe_linear);
		zuheView = (GridView) tljrzuhe.findViewById(R.id.tljr_page_zuhe_gridview);
		page_zuhe_bu=(Button)tljrzuhe.findViewById(R.id.tljr_page_zuhe_linear_bu);
		zuheView.setOnItemClickListener(this);
		page_zuhe_bu.setOnClickListener(this);
		listzuhe = new ArrayList<ZuHeModel>();
		mSwipeRefreshLayout = (SwipeRefreshLayout) tljrzuhe.findViewById(R.id.zuhe_swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
		if (MyApplication.getInstance().self != null) {
			mSwipeRefreshLayout.post(new Runnable() {
				@Override
				public void run() {
					mSwipeRefreshLayout.setRefreshing(true);
				}
			});
		}else{
			mSwipeRefreshLayout.setVisibility(View.GONE);
			page_zuhe.setVisibility(View.VISIBLE);
		}
		return tljrzuhe;
	}

	public static void initView(boolean status) {
		if(MyApplication.getInstance().self != null){
			//假如有登录
			page_zuhe.setVisibility(View.GONE);
			mSwipeRefreshLayout.setVisibility(View.VISIBLE);
			String params = null;
			listZu = new ArrayList<OneFenZu>(ZiXuanUtil.fzMap.values());
			if(listZu.size()<=0){
				zuheAdapter = new TljrZuheAdapter(context,R.layout.tljr_page_zuhe_item, listzuhe);
				zuheView.setAdapter(zuheAdapter);
				//刚注册登录没有组合就加入默认组合
				ZiXuanUtil.addNewFenzu(MyApplication.getInstance().getResources().getString(R.string.morenfenzu), false);
				TljrZuHe.addNewFenZu(MyApplication.getInstance().getResources().getString(R.string.morenfenzu));
				MainActivity.AddZuHuStatus = 1;
				ZiXuanUtil.emitNowFenZu(MyApplication.getInstance().getResources().getString(R.string.morenfenzu));
				if (MyApplication.getInstance().self != null) {
					ZiXuanUtil.sendActions(MyApplication.getInstance().self,MyApplication.getInstance().getMainActivity(), null);
				}
			}else{
				//加载用户组合
				for (int i = 0; i < listZu.size(); i++){
					if(status){
						ZuHeModel zuhe = new ZuHeModel(listZu.get(i).getId(), listZu.get(i).getName(), 
									"", listZu.get(i).getTag(), "", "", listZu.get(i).getId());
						listzuhe.add(zuhe);
					}
					if (i == 0) {
						params = listZu.get(i).getId();
					} else{
						params = params + "," + listZu.get(i).getId();
					}
				}
				MyApplication.requestQueue.add(new StringRequest(UrlUtil.URL_tc+"?action=getnewzhinfo&uid="
					+ MyApplication.getInstance().self.getId()+"&zhid="+params+"&updateDate="+System.currentTimeMillis(),
					new Response.Listener<String>() {
						public void onResponse(String response){
					           try{
					            	JSONObject object = new JSONObject(response);
					            	editor.putString("updateTime",object.getString("updateTime"));
					            	editor.commit();
									if(object.getInt("status")!=0){
										listzuhe =ParseJson.ParseInitZuHe(listzuhe,object);
										if (listzuhe!=null&&listzuhe.size()>0) {
											Message message = Message.obtain();
											message.what = 1;
											mHandler.sendMessage(message);
										}	
									}else{
										Toast.makeText(context,"已是最新",Toast.LENGTH_SHORT).show();
										 if(mSwipeRefreshLayout!=null){
											   mSwipeRefreshLayout.setRefreshing(false);
										 }
									}
					            }catch(Exception e){
					            	
					            }
							}
						}, new Response.ErrorListener() {
						   public void onErrorResponse(VolleyError error) {
							   if(mSwipeRefreshLayout!=null){
								   mSwipeRefreshLayout.setRefreshing(false);
							   }
						}
				}));
			}
		}else{
			//假如没有登录
			page_zuhe.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setVisibility(View.GONE);
			if(zuheAdapter!=null){
				zuheAdapter.clear();
				listzuhe.clear();
			}
		}
	}

	public static final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				zuheAdapter = new TljrZuheAdapter(context,R.layout.tljr_page_zuhe_item, listzuhe);
				zuheView.setAdapter(zuheAdapter);
				mSwipeRefreshLayout.post(new Runnable() {
					@Override
					public void run() {
						mSwipeRefreshLayout.setRefreshing(false);
					}
				});
				break;
			case 6:
				Toast.makeText(context, msg.getData().getString("msg"),Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public View getView() {
		return this.tljrzuhe;
	}

	public static void sendMessage(int Msg,String msg) {
		Message message = Message.obtain();
		message.what = Msg;
		if(msg!=""){
			Bundle bundle=new Bundle();
			bundle.putString("msg",msg);
			message.setData(bundle);
		}
		mHandler.sendMessage(message);
	}

	// 组合下拉刷新
	@Override
	public void onRefresh() {
		if(MyApplication.getInstance().self!=null){
			String time=GetTime.getString("updateTime","");
			if(!time.equals("")){
				if(new Date().getTime()>Long.valueOf(time)){
					initView(false);
				}else{
					mSwipeRefreshLayout.setRefreshing(false);
					Toast.makeText(getContext(),"已是最新",Toast.LENGTH_SHORT).show();
				}
			}else{
				initView(false);
			}
		}else{
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		ZuHeModel zu = (ZuHeModel) view.getTag(R.id.tiaocang_into_info);
		final Intent intent = new Intent(context, TljrChangePercent.class);
		intent.putExtra("zuName",zu.getName());
		intent.putExtra("zuid", zu.getId());
		intent.putExtra("TagName", zu.getName());
		intent.putExtra("TagTime", zu.getTime());
		intent.putExtra("key", zu.getKey());
		intent.putExtra("jsonData", zu.getJsonData());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void addNewFenZu(String name){
		ZuHeModel zuhe = new ZuHeModel("", name, "", "", "", "", "");
		if (listzuhe != null && zuheAdapter != null) {
			listzuhe.add(listzuhe.size(), zuhe);
			zuheAdapter.notifyDataSetChanged();
		}
	}

	public static void removeFenZu(String ZuName){
		for(int i=0;i<listzuhe.size();i++){
			if(listzuhe.get(i).getName().equals(ZuName)){
				listzuhe.remove(i);
			}
		}
		zuheAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tljr_page_zuhe_linear_bu:
			Intent intent=new Intent(getActivity(),WXEntryActivity.class);
			getActivity().startActivity(intent);
			break;
		}
	}
	
	
}
