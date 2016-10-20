package com.abct.tljr.hangqing.zixuan;

import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.dialog.SearchDialog;
import com.abct.tljr.hangqing.adapter.ZiXuanGuAdapter;
import com.abct.tljr.hangqing.database.ZiXuanGu;
import com.abct.tljr.hangqing.database.ZiXuanGuDataBaseImpl;
import com.abct.tljr.hangqing.util.DividerItemDecoration;
import com.abct.tljr.hangqing.util.ParseJson;
import com.abct.tljr.utils.Util;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.volley.Response;
import com.qh.common.volley.VolleyError;
import com.qh.common.volley.toolbox.StringRequest;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class tljr_zixuan_gu_recyclerview extends Fragment implements OnRefreshListener, OnClickListener {
	private View view = null;
	private static RecyclerView mRecyclerView = null;
	public static SwipeRefreshLayout mSwipeRefreshLayout = null;
	public static List<ZiXuanGu> listZiXuanGu = null;
	public static ZiXuanGuAdapter adapter = null;
	public static UpdateZiXuanGu mUpdateZiXuanGu = null;
	private static int freshTime = Constant.FlushTime;
	private static int lastVisibleItem=0;
	private static LinearLayoutManager manager=null;
	public static LinearLayout login_in=null;
	private static Context context=null;
	public static LinearLayout addgushow=null;;
	private ImageView nowprice_arrow=null;
	private ImageView zhangdiefu_arrow=null;
	
	private RelativeLayout zixuangu_price_relative;
	private RelativeLayout zixuangu_zhangdiefu_relative;
	
	private int nowprice=0;
	private int zhangdiefu=0;
	
	private String host="http://120.24.235.202:8080/ZhiLiYinHang/ZiXuanServlet";
	
	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		context=getActivity();
		view = inflater.inflate(R.layout.tljr_zixuan_gu_layout,container,false);
		nowprice_arrow=(ImageView)view.findViewById(R.id.zixuangu_nowprice_arrow);
		zhangdiefu_arrow=(ImageView)view.findViewById(R.id.zixuangu_zhangdiefu_arrow);
		
		zixuangu_price_relative=(RelativeLayout)view.findViewById(R.id.zixuangu_nowprice_relative);
		zixuangu_zhangdiefu_relative=(RelativeLayout)view.findViewById(R.id.zixuangu_zhangdiefu_relative);
		zixuangu_price_relative.setOnClickListener(this);
		zixuangu_zhangdiefu_relative.setOnClickListener(this);
		
		mRecyclerView = (RecyclerView) view.findViewById(R.id.foreign_zixuangu);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
		manager=new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(manager);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.foreign_swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
	    login_in=(LinearLayout)view.findViewById(R.id.zixuan_login_lin);
	    addgushow=(LinearLayout)view.findViewById(R.id.addgutip);
	    login_in.setOnClickListener(this);
	    addgushow.setOnClickListener(this);
		IntentFilter intentFilter = new IntentFilter("com.zixuan.updatezixuan");
		mUpdateZiXuanGu = new UpdateZiXuanGu();
		getActivity().registerReceiver(mUpdateZiXuanGu, intentFilter);
		mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
					mSwipeRefreshLayout.post(new Runnable() {
						@Override
						public void run() {
							mSwipeRefreshLayout.setRefreshing(true);
						}
					});
				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = manager.findLastVisibleItemPosition();
			}
		});
		if(MyApplication.getInstance().self==null){
			mSwipeRefreshLayout.setVisibility(View.GONE);
		}else{
			login_in.setVisibility(View.GONE);
			mSwipeRefreshLayout.post(new Runnable() {
				@Override
				public void run() {
					mSwipeRefreshLayout.setRefreshing(true);
				}
			});
			getZiXuanGu();
		}
		return view;
	}

	public static void unlogin(){
		addgushow.setVisibility(View.GONE);
		mRecyclerView.setVisibility(View.GONE);
		listZiXuanGu.clear();
		adapter.notifyDataSetChanged();
		login_in.setVisibility(View.VISIBLE);
	}
	
	public static void getZiXuanGu() {
		try {
			if(MyApplication.getInstance().self!=null){
				mSwipeRefreshLayout.setVisibility(View.VISIBLE);
				login_in.setVisibility(View.GONE);
				RealmResults<ZiXuanGu> mRealmResults= ZiXuanGuDataBaseImpl.SelectAllZiXuanGu(context);
				if (mRealmResults != null && !mRealmResults.isEmpty()) {
					listZiXuanGu=new ArrayList<>();
					for(int i=0;i<mRealmResults.size();i++){
						listZiXuanGu.add(ChangeCode(mRealmResults.get(i)));
					}
					sendMessage(null,2);
				} else {
					getZiXuanGuOfNet();
				}
			}else{
				mSwipeRefreshLayout.setVisibility(View.GONE);
				login_in.setVisibility(View.VISIBLE);
				addgushow.setVisibility(View.GONE);
				if(listZiXuanGu!=null){
					listZiXuanGu.clear();
					adapter.notifyDataSetChanged();
				}
			}
		} catch (Exception e) {
		}
	}

	public static ZiXuanGu ChangeCode(ZiXuanGu gu){
		ZiXuanGu temp=new ZiXuanGu();
		temp.setCode(gu.getCode());
		temp.setId(gu.getId());
		temp.setMarket(gu.getMarket());
		temp.setName(gu.getName());
		temp.setPrice(gu.getPrice());
		temp.setChange(gu.getChange());
		temp.setLocation(gu.getLocation());
		temp.setStatus("--");
		temp.setYclose(-1);
		return temp;
	}
	
	// 得到股票列表
	public static void getZiXuanGuOfNet() {
		try {
			String getListUrl = "http://120.24.235.202:8080/ZhiLiYinHang/ZiXuanServlet?method=getMyList&uid="
					+ MyApplication.getInstance().self.getId()+ "&token="+ Configs.token;
			MyApplication.requestQueue.add(new StringRequest(getListUrl,new Response.Listener<String>() {
						@Override
						public void onResponse(String response){
							LogUtil.e("zixuanguresponse",response);
							listZiXuanGu = ParseJson.ParseZiXuanData(response);
							if (listZiXuanGu != null&&listZiXuanGu.size()>0){
								ZiXuanGuDataBaseImpl.saveZiXuanGu(listZiXuanGu);
								sendMessage(null,2);
							}else{
								addgushow.setVisibility(View.VISIBLE);
								listZiXuanGu=new ArrayList<>();
								adapter = new ZiXuanGuAdapter(context,R.layout.tljr_zixuangu_item,listZiXuanGu);
								mRecyclerView.setAdapter(adapter);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError response) {
							sendMessage(null,1);
						}
					}));
		} catch (Exception e) {

		}
	}

	// 下来刷新
	@Override
	public void onRefresh() {
		flush(true);
	}

	public static void sendMessage(String info,int msg) {
		Message message = Message.obtain();
		message.what = msg;
		if(info!=null){
			Bundle bundle=new Bundle();
			bundle.putString("msg",info);
			message.setData(bundle);
		}
		mHandler.sendMessage(message);
	}

	final static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mSwipeRefreshLayout.setRefreshing(false);
				break;
			case 2:
				login_in.setVisibility(View.GONE);
				if(listZiXuanGu!=null&&listZiXuanGu.size()<=0){
					mSwipeRefreshLayout.setVisibility(View.GONE);
					addgushow.setVisibility(View.VISIBLE);
				}else{
					addgushow.setVisibility(View.GONE);
				}
				mSwipeRefreshLayout.post(new Runnable(){
					@Override
					public void run() {
						mSwipeRefreshLayout.setRefreshing(false);
					}
				});
				adapter = new ZiXuanGuAdapter(context,R.layout.tljr_zixuangu_item,listZiXuanGu);
				mRecyclerView.setAdapter(adapter);
				flush(true);
				break;
			case 3:
				adapter = new ZiXuanGuAdapter(context,R.layout.tljr_zixuangu_item,listZiXuanGu);
				mRecyclerView.setAdapter(adapter);
				break;
			case 4:
				freshGridMapData(msg.getData().getString("msg"),true);
				break;
			}
		};
	};

	private static int lastFlushItem;
	
	public static void flush(final boolean... isWait) {
		if(listZiXuanGu==null||listZiXuanGu.size()<=0){
			mSwipeRefreshLayout.setRefreshing(false);
			return ;
		}
		if (isWait.length > 0)
			freshTime = Constant.FlushTime;
		freshTime++;
		if (freshTime<(Constant.netType.equals("WIFI")?5:((Constant.FlushTime==0?9999:Constant.FlushTime)))){
			return;
		}
		if (listZiXuanGu.size() == 0 || mRecyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
			mSwipeRefreshLayout.setRefreshing(false);
			return;
		}
		lastFlushItem = lastVisibleItem;
		freshTime = 0;
		int m = 0;
		String parm = "list=";
		int first = manager.findFirstVisibleItemPosition() > 0 ? manager.findFirstVisibleItemPosition() : 0;
		for (int i = lastFlushItem; i >= first; i--) {
			if(i>listZiXuanGu.size()-1){
				break;
			}
			ZiXuanGu mZiXuanGu=listZiXuanGu.get(i);
			if (m == 0) {
				parm += (mZiXuanGu.getMarket() + "|" + mZiXuanGu.getCode());
			} else {
				parm += ("," + mZiXuanGu.getMarket() + "|" + mZiXuanGu.getCode());
			}
			m++;
		}
		Util.getRealInfo(parm, new NetResult() {
			@Override
			public void result(final String msg) {
				String testMsg=msg;
				if (!msg.equals("")) {
					freshGridMapData(msg,isWait);
				} else {
					mSwipeRefreshLayout.setRefreshing(false);
				}
			}
		}, true);
	}

	private static void freshGridMapData(final String msg, boolean... isWait) {
		try {
			final org.json.JSONObject object = new org.json.JSONObject(msg);
			if (object.getInt("code") != 1) {
				return;
			}
			try {
				org.json.JSONArray arr = object.getJSONArray("result");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					if (i < listZiXuanGu.size()) {
						JSONArray array = obj.getJSONArray("data");
						ZiXuanGu mZiXuanGu = listZiXuanGu.get(lastFlushItem - i);
						mZiXuanGu.setPrice((float) array.optDouble(0));
						mZiXuanGu.setChange((float) array.optDouble(9, 0));
						mZiXuanGu.setYclose((float)array.optDouble(1, 0));
						mZiXuanGu.setStatus(array.optString(16,""));
					}
				}
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}
				if (isWait.length > 0) {
					mSwipeRefreshLayout.setRefreshing(false);
				}
			} catch (org.json.JSONException e) {
				e.printStackTrace();
			}
		} catch (org.json.JSONException e) {
			e.printStackTrace();
		}
	}
	
	class UpdateZiXuanGu extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getStringExtra("action");
			if (action.equals("add")) {
				ZiXuanGu mZiXuanGu = new ZiXuanGu();
				mZiXuanGu.setCode(intent.getStringExtra("code"));
				LogUtil.e("addId",intent.getStringExtra("id"));
				mZiXuanGu.setId(intent.getStringExtra("id"));
				mZiXuanGu.setMarket(intent.getStringExtra("market"));
				mZiXuanGu.setName(intent.getStringExtra("name"));
				mZiXuanGu.setPrice(intent.getFloatExtra("price", 0.0f));
				mZiXuanGu.setChange(intent.getFloatExtra("p_change",0.0f));
				mZiXuanGu.setStatus(intent.getStringExtra("status"));
				mZiXuanGu.setLocation(0);
				listZiXuanGu.add(0,mZiXuanGu);
				adapter.notifyDataSetChanged();
				for(int i = 0; i < listZiXuanGu.size();i++){
					listZiXuanGu.get(i).setLocation(i);
				}
				if(addgushow.getVisibility()==View.VISIBLE){
					mSwipeRefreshLayout.setVisibility(View.VISIBLE);
					addgushow.setVisibility(View.GONE);
				}
				addgushow.setVisibility(View.GONE);
				ZiXuanGuDataBaseImpl.saveZiXuanGu(tljr_zixuan_gu_recyclerview.listZiXuanGu);
				
				JSONArray array=new JSONArray();
				if(tljr_zixuan_gu_recyclerview.listZiXuanGu.size()>0){
					for(int i = 0; i < tljr_zixuan_gu_recyclerview.listZiXuanGu.size();i++){
						tljr_zixuan_gu_recyclerview.listZiXuanGu.get(i).setLocation(i);
						array.put(Integer.valueOf(tljr_zixuan_gu_recyclerview.listZiXuanGu.get(i).getId()));
					}
					ZiXuanGuDataBaseImpl.saveZiXuanGu(tljr_zixuan_gu_recyclerview.listZiXuanGu);
				}
				NetUtil.sendGet(host+"?method=update&uid="+MyApplication.getInstance().self.getId()
						+"&token="+Configs.token+"&ids="+array.toString(),new NetResult() {
					@Override
					public void result(String response) {
						try{
							LogUtil.e("response",response);
						}catch(Exception e){
							LogUtil.e("DeleteZiXian",e.getMessage());
						}
					}
			   });
				
			}
		}
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.zixuan_login_lin:
			Intent intent=new Intent(context,WXEntryActivity.class);
			context.startActivity(intent);
			break;
		case R.id.addgutip:
			new SearchDialog(getActivity()).show(null);
			break;
		case R.id.zixuangu_nowprice_relative:
			if(MyApplication.getInstance().self==null){
				break ;
			}
			if(listZiXuanGu!=null&&listZiXuanGu.size()>0){
				if(zhangdiefu_arrow.getVisibility()==View.VISIBLE){
					zhangdiefu=0;
					zhangdiefu_arrow.setVisibility(View.INVISIBLE);
				}
				if(nowprice==0){
					SortZiXuanGuForNowprice(0);
					nowprice=1;
					nowprice_arrow.setVisibility(View.VISIBLE);
					nowprice_arrow.setImageResource(R.drawable.img_arraw_up);
				}else if(nowprice==1){
					SortZiXuanGuForNowprice(1);
					nowprice=2;
					nowprice_arrow.setImageResource(R.drawable.img_arraw_down);
				}else{
					SortZiXuanGuForNowprice(2);
					nowprice=0;
					nowprice_arrow.setVisibility(View.INVISIBLE);
				}
			}
			break;
		case R.id.zixuangu_zhangdiefu_relative:	
			if(MyApplication.getInstance().self==null){
				break ;
			}
			if(listZiXuanGu!=null&&listZiXuanGu.size()>0){
				if(nowprice_arrow.getVisibility()==View.VISIBLE){
					nowprice=0;
					nowprice_arrow.setVisibility(View.INVISIBLE);
				}
				if(zhangdiefu==0){
					SortZiXuanGuForZhangDieFu(0);
					zhangdiefu=1;
					zhangdiefu_arrow.setVisibility(View.VISIBLE);
					zhangdiefu_arrow.setImageResource(R.drawable.img_arraw_up);
				}else if(zhangdiefu==1){
					SortZiXuanGuForZhangDieFu(1);
					zhangdiefu=2;
					zhangdiefu_arrow.setImageResource(R.drawable.img_arraw_down);
				}else{
					SortZiXuanGuForZhangDieFu(2);
					zhangdiefu=0;
					zhangdiefu_arrow.setVisibility(View.INVISIBLE);
				}
			}
			break;
		}
	}

	//按当前价排序
	public void SortZiXuanGuForNowprice(int status){
		if(status==0){
			for(int i=0;i<listZiXuanGu.size();i++){
				for(int j = 0; j < listZiXuanGu.size() - i - 1; j++){
					if(listZiXuanGu.get(j).getPrice()<listZiXuanGu.get(j+1).getPrice()){
						ZiXuanGu tempgu=listZiXuanGu.get(j);
						listZiXuanGu.set(j,listZiXuanGu.get(j+1));
						listZiXuanGu.set(j+1,tempgu);
					}
				}
			}
		}else if(status==1){
			for(int i=0;i<listZiXuanGu.size();i++){
				for(int j = 0; j < listZiXuanGu.size() - i - 1; j++){
					if(listZiXuanGu.get(j).getPrice()>listZiXuanGu.get(j+1).getPrice()){
						ZiXuanGu tempgu=listZiXuanGu.get(j);
						listZiXuanGu.set(j,listZiXuanGu.get(j+1));
						listZiXuanGu.set(j+1,tempgu);
					}
				}
			}
		}else{
			for(int i=0;i<listZiXuanGu.size();i++){
				for(int j = 0; j < listZiXuanGu.size() - i - 1; j++){
					if(listZiXuanGu.get(j).getLocation()>listZiXuanGu.get(j+1).getLocation()){
						ZiXuanGu tempgu=listZiXuanGu.get(j);
						listZiXuanGu.set(j,listZiXuanGu.get(j+1));
						listZiXuanGu.set(j+1,tempgu);
					}
				}
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	public void SortZiXuanGuForZhangDieFu(int status){
		if(status==0){
			for(int i=0;i<listZiXuanGu.size();i++){
				for(int j = 0; j < listZiXuanGu.size() - i - 1; j++){
					if(listZiXuanGu.get(j).getChange()<listZiXuanGu.get(j+1).getChange()){
						ZiXuanGu tempgu=listZiXuanGu.get(j);
						listZiXuanGu.set(j,listZiXuanGu.get(j+1));
						listZiXuanGu.set(j+1,tempgu);
					}
				}
			}
		}else if(status==1){
			for(int i=0;i<listZiXuanGu.size();i++){
				for(int j = 0; j < listZiXuanGu.size() - i - 1; j++){
					if(listZiXuanGu.get(j).getChange()>listZiXuanGu.get(j+1).getChange()){
						ZiXuanGu tempgu=listZiXuanGu.get(j);
						listZiXuanGu.set(j,listZiXuanGu.get(j+1));
						listZiXuanGu.set(j+1,tempgu);
					}
				}
			}
		}else{
			for(int i=0;i<listZiXuanGu.size();i++){
				for(int j = 0; j < listZiXuanGu.size() - i - 1; j++){
					if(listZiXuanGu.get(j).getLocation()>listZiXuanGu.get(j+1).getLocation()){
						ZiXuanGu tempgu=listZiXuanGu.get(j);
						listZiXuanGu.set(j,listZiXuanGu.get(j+1));
						listZiXuanGu.set(j+1,tempgu);
					}
				}
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	
}
