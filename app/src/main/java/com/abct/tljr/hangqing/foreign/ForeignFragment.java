package com.abct.tljr.hangqing.foreign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.hangqing.HangQing;
import com.abct.tljr.hangqing.adapter.ForeignAdapter;
import com.abct.tljr.hangqing.model.Foreign;
import com.abct.tljr.hangqing.util.DividerItemDecoration;
import com.abct.tljr.hangqing.util.ParseJson;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.ui.widget.ProgressDlgUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ForeignFragment extends Fragment implements OnRefreshListener, OnClickListener{
	private View view = null;
	private RecyclerView ForeignListView = null;
	private ForeignAdapter adapter = null;
	private List<Foreign> listForeign = null;
	private int page = 1;
	private int size = 10;
	private int lastVisibleItem;
	private LinearLayoutManager manager;
	private int freshTime = Constant.FlushTime;
	public SwipeRefreshLayout mSwipeRefreshLayout = null;
	public int num = 0;
	public static String[] title = null;
	public static String[] temptitle = null;
	public boolean once = true;
	public DisplayMetrics dm = null;
	private int lastFlushItem;
	private String url=UrlUtil.Url_apicavacn+"tools/index/0.2/qlist";
	public Map<String,String> tabmenu=new HashMap<String,String>();
	private UpdateForignFragment mUpdateForignFragment=null;
	private RelativeLayout forign_nowprice_relative;
	private RelativeLayout forign_zhangdiefu_relative;
	private ImageView forign_nowprice;
	private ImageView forign_zhangdiefu;
	private int pricestatus=0;
	private int changdiefustatus=0;
	private RelativeLayout forign_reset;
	private Button forign_rebtn;
	private String type="";
	private boolean ResbtnStatus=true;
	
	//类HangQing初始化的时候会调用ForeignFragment初始化里面的变量
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.tljr_page_hq_foreign,container, false);
		listForeign = new ArrayList<Foreign>();
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.foreign_swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
		forign_nowprice_relative=(RelativeLayout)view.findViewById(R.id.forign_nowprice_relative);
		forign_zhangdiefu_relative=(RelativeLayout)view.findViewById(R.id.forign_zhangdiefu_relative);
		forign_nowprice=(ImageView)view.findViewById(R.id.forign_nowprice_arrow);
		forign_zhangdiefu=(ImageView)view.findViewById(R.id.forign_zhangdiefu_arrow);
		forign_reset=(RelativeLayout)view.findViewById(R.id.forign_reset);
		forign_rebtn=(Button)view.findViewById(R.id.forign_rebtn);
		forign_rebtn.setOnClickListener(this);
		forign_nowprice_relative.setOnClickListener(this);
		forign_zhangdiefu_relative.setOnClickListener(this);
		dm = getActivity().getResources().getDisplayMetrics();
		initTabTitle();
		
		return view;
	}

	//加载titleTab
	public void initTabTitle() {
		if(HangQing.HangQingTab==null){
			return ;
		}
		title=new String[HangQing.HangQingTab.size() + 3];
		temptitle=new String[HangQing.HangQingTab.size() + 3];
		title[0]="行情";
		title[1]="自选";
		title[2]="组合";
		temptitle[0]="行情";
		temptitle[1]="自选";
		temptitle[2]="组合";
		int i = 3;
		for (String name:HangQing.HangQingTab.keySet()) {
			title[i]=name;
			temptitle[i]=name;
			i+=1;
		}
	}

	//当滑到对应的ForeignFragment会调用initView获取数据显示
	@SuppressWarnings("deprecation")
	public void initView(final String type, int num) {
		if (once) {
			once = false;
			this.num = num;
			this.type=type;
			CheckTitle(type);
			if(mSwipeRefreshLayout!=null){
			mSwipeRefreshLayout.post(new Runnable() {
				@Override
				public void run() {
					mSwipeRefreshLayout.setRefreshing(true);
				}
			});}
			ForeignListView = (RecyclerView) view.findViewById(R.id.foreign_listview);
			ForeignListView.setItemAnimator(new DefaultItemAnimator());
			ForeignListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
			adapter = new ForeignAdapter(getActivity(),R.layout.tljr_page_hq_foreign_item, listForeign);
			ForeignListView.setAdapter(adapter);
			manager = new LinearLayoutManager(getContext());
			ForeignListView.setLayoutManager(manager);
			ForeignListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
						getList(type);
					}
				}
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
					super.onScrolled(recyclerView, dx, dy);
					lastVisibleItem = manager.findLastVisibleItemPosition();
				}
			});
			getList(type);
		}
	}

	//title选项因为美股比较特殊，用这个检测CheckTitle
	public void CheckTitle(String type){
		if(type.equals("MG")){
			mUpdateForignFragment=new UpdateForignFragment();
			IntentFilter intentFilter=new IntentFilter("com.forignfragement.mg");
			getActivity().registerReceiver(mUpdateForignFragment,intentFilter);
			NetUtil.sendGet(url+"/"+type.toLowerCase(),new NetResult() {
				@Override
				public void result(String response) {
					if(!response.equals("")){
						tabmenu=ParseJson.ParseHangQingTab(response);
						tabmenu.put("美股","MG");
					}	
				}
			});
		}
	}
	
	//得到股票数据
	private void getList(String type) {
//		String testurl=UrlUtil.Url_apicavacn+"tools/stock/quotation/0.2/type/list"+"?"+"type="+type+"&pn="+page+"&ps="+size;
		NetUtil.sendGet(UrlUtil.Url_apicavacn+"tools/stock/quotation/0.2/type/list",
				"type="+type+"&pn="+page+"&ps="+size,new NetResult() {
					@Override
					public void result(String arg0) {
						try {
							if(!arg0.equals("")){
								page++;
								forign_reset.setVisibility(View.GONE);
								mSwipeRefreshLayout.setVisibility(View.VISIBLE);
								JSONObject jsonObject = new JSONObject(arg0);
								if (jsonObject.getInt("code") == 200) {
									JSONArray array = jsonObject.getJSONArray("result");
									for (int i = 0; i < array.length(); i++) {
										JSONObject object = array.getJSONObject(i);
										Foreign mForeign = new Foreign();
										mForeign.setName(object.getString("name"));
										mForeign.setMarket(object.getString("market"));
										mForeign.setCode(object.getString("code"));
										mForeign.setYclose(-1);
										mForeign.setStatus("--");
										listForeign.add(mForeign);
									}
									ForeignListView.stopScroll();
									UpdateTtitle(num);
									adapter.notifyDataSetChanged();
									mSwipeRefreshLayout.postDelayed(new Runnable() {
										@Override
										public void run() {
											mSwipeRefreshLayout.setRefreshing(false);
										}
									}, 1000);
								}
							}else{
								forign_reset.setVisibility(View.VISIBLE);
								mSwipeRefreshLayout.setVisibility(View.GONE);
								Toast.makeText(getActivity(),"数据加载失败",Toast.LENGTH_SHORT).show();
							}
							ResbtnStatus=true;
							ProgressDlgUtil.stopProgressDlg();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				},5000);
	}

	//刷新数据
	public void flush(final boolean... isWait) {
		if (isWait.length > 0)
			freshTime = Constant.FlushTime;
			freshTime++;
		if (freshTime < (Constant.netType.equals("WIFI") ? 5:((Constant.FlushTime == 0 ? 9999 : Constant.FlushTime)))) {
			return;
		}
		if (listForeign.size() == 0 || ForeignListView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
			mSwipeRefreshLayout.setRefreshing(false);
			return;
		}
		lastFlushItem = lastVisibleItem;
		freshTime = 0;
		int m = 0;
		String parm = "list=";
		int first = manager.findFirstVisibleItemPosition() > 0 ? manager.findFirstVisibleItemPosition() : 0;
		for (int i = lastFlushItem; i >= first; i--) {
			Foreign foreign = listForeign.get(i);
			if (m == 0) {
				parm += (foreign.getMarket() + "|" + foreign.getCode());
			} else {
				parm += ("," + foreign.getMarket() + "|" + foreign.getCode());
			}
			m++;
		}
		Util.getRealInfo(parm, new NetResult() {
			@Override
			public void result(final String msg) {
				if (!msg.equals("")) {
					freshGridMapData(msg, isWait);
				} else {
					mSwipeRefreshLayout.setRefreshing(false);
				}
			}
		}, true);
	}

	private void freshGridMapData(final String msg, boolean... isWait) {
		try {
			LogUtil.e("forignfragment_msg",msg);
			final org.json.JSONObject object = new org.json.JSONObject(msg);
			if (object.getInt("code") != 1) {
				return;
			}
			try {
				org.json.JSONArray arr = object.getJSONArray("result");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					if (i < listForeign.size()) {
						JSONArray array = obj.getJSONArray("data");
						Foreign foreign = listForeign.get(lastFlushItem - i);
						foreign.setPrice((float) array.optDouble(0));
						foreign.setChange((float) array.optDouble(9, 0));
						foreign.setYclose((float)array.optDouble(1,0));
						foreign.setStatus(array.optString(16,"--"));
					}
				}
				adapter.notifyDataSetChanged();
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

	@Override
	public void onRefresh() {
		flush(true);
	}
	
	//当股票数据个数改变的时候更新
	// { "自选", "组合", "指数", "港股", "美股", "外汇", "商品", "黄金白银", "国债","期货"}
	public void UpdateTtitle(int num) {
		title[num] = temptitle[num] + "(" + listForeign.size() + ")";
		HangQing.adapter.setTitle(title);
		HangQing.mPagerSlidingTabStrip.setTextSize((int) 
				TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, dm));
		HangQing.mPagerSlidingTabStrip.setTabPaddingLeftRight(40);
		HangQing.mPagerSlidingTabStrip.setPadding(5, 0, 5, 0);
		HangQing.mPagerSlidingTabStrip.setUnderlineHeight(0);
		HangQing.adapter.notifyDataSetChanged();
		HangQing.mPagerSlidingTabStrip.notifyDataSetChanged();
	}
	
	class UpdateForignFragment extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String key=intent.getStringExtra("key");
			mSwipeRefreshLayout.setRefreshing(true);
			if(tabmenu!=null){
				for(String name:tabmenu.keySet()){
					if(tabmenu.get(name).equals(key)){
						title[num]=name;
						temptitle[num]=name;
						break;
					}
				}
				HangQing.mPagerSlidingTabStrip.notifyDataSetChanged();
				listForeign.clear();
				page=0;
				size=10;
				getList(key);
			}else{
				mSwipeRefreshLayout.setRefreshing(false);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.forign_nowprice_relative:
			if(listForeign!=null&&listForeign.size()>0){
				if(forign_zhangdiefu.getVisibility()==View.VISIBLE){
					changdiefustatus=0;
					forign_zhangdiefu.setVisibility(View.INVISIBLE);
				}
				if(pricestatus==0){
					SortForignForNowprice(0);
					pricestatus=1;
					forign_nowprice.setVisibility(View.VISIBLE);
					forign_nowprice.setImageResource(R.drawable.img_arraw_up);
				}else if(pricestatus==1){
					SortForignForNowprice(1);
					pricestatus=2;
					forign_nowprice.setImageResource(R.drawable.img_arraw_down);
				}else{
					SortForignForNowprice(2);
					pricestatus=0;
					forign_nowprice.setVisibility(View.INVISIBLE);
				}
			}
			break;
		case R.id.forign_zhangdiefu_relative:
			if(listForeign!=null&&listForeign.size()>0){
				if(forign_nowprice.getVisibility()==View.VISIBLE){
					pricestatus=0;
					forign_nowprice.setVisibility(View.INVISIBLE);
				}
				if(changdiefustatus==0){
					SortForignForChangdiefu(0);
					forign_zhangdiefu.setVisibility(View.VISIBLE);
					forign_zhangdiefu.setImageResource(R.drawable.img_arraw_up);
					changdiefustatus=1;
				}else if(changdiefustatus==1){
					SortForignForChangdiefu(1);
					forign_zhangdiefu.setImageResource(R.drawable.img_arraw_down);
					changdiefustatus=2;
				}else{
					SortForignForChangdiefu(2);
					changdiefustatus=0;
					forign_zhangdiefu.setVisibility(View.INVISIBLE);
				}
			}
			break;
		case R.id.forign_rebtn:
			ProgressDlgUtil.showProgressDlg("",getActivity());
			if(ResbtnStatus){
				ResbtnStatus=false;
				getList(type);
			}
			break;
		}
	}
	
	//按当前价排序
	public void SortForignForNowprice(int status){
		if(status==0){
			for(int i=0;i<listForeign.size();i++){
				for(int j = 0; j <listForeign.size() - i - 1; j++){
					if(listForeign.get(j).getPrice()<listForeign.get(j+1).getPrice()){
						Foreign tempforign=listForeign.get(j);
						listForeign.set(j,listForeign.get(j+1));
						listForeign.set(j+1,tempforign);
					}
				}
			}
		}else if(status==1){
			for(int i=0;i<listForeign.size();i++){
				for(int j = 0; j < listForeign.size() - i - 1; j++){
					if(listForeign.get(j).getPrice()>listForeign.get(j+1).getPrice()){
						Foreign tempforeign=listForeign.get(j);
						listForeign.set(j,listForeign.get(j+1));
						listForeign.set(j+1,tempforeign);
					}
				}
			}
		}else{
			for(int i=0;i<listForeign.size();i++){
				for(int j = 0; j < listForeign.size() - i - 1; j++){
					if(listForeign.get(j).getYclose()>listForeign.get(j+1).getYclose()){
						Foreign tempgu=listForeign.get(j);
						listForeign.set(j,listForeign.get(j+1));
						listForeign.set(j+1,tempgu);
					}
				}
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	

	//按当前价排序
	public void SortForignForChangdiefu(int status){
		if(status==0){
			for(int i=0;i<listForeign.size();i++){
				for(int j = 0; j <listForeign.size() - i - 1; j++){
					if(listForeign.get(j).getChange()<listForeign.get(j+1).getChange()){
						Foreign tempforign=listForeign.get(j);
						listForeign.set(j,listForeign.get(j+1));
						listForeign.set(j+1,tempforign);
					}
				}
			 }
			}else if(status==1){
				for(int i=0;i<listForeign.size();i++){
					for(int j = 0; j < listForeign.size() - i - 1; j++){
						if(listForeign.get(j).getChange()>listForeign.get(j+1).getChange()){
							Foreign tempforeign=listForeign.get(j);
							listForeign.set(j,listForeign.get(j+1));
							listForeign.set(j+1,tempforeign);
						}
					}
				}
			}else{
				for(int i=0;i<listForeign.size();i++){
					for(int j = 0; j < listForeign.size() - i - 1; j++){
						if(listForeign.get(j).getYclose()>listForeign.get(j+1).getYclose()){
							Foreign tempgu=listForeign.get(j);
							listForeign.set(j,listForeign.get(j+1));
							listForeign.set(j+1,tempgu);
						}
					}
				}
			}
			adapter.notifyDataSetChanged();
		}
}
