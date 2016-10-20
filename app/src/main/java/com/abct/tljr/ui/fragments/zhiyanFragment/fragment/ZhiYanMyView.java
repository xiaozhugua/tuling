package com.abct.tljr.ui.fragments.zhiyanFragment.fragment;

import java.util.ArrayList;
import com.abct.tljr.R;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.MyViewAdapter;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ZhiYanMyView implements OnClickListener, OnRefreshListener {

	private Context context=null;
	private View myView;
	private RecyclerView mRecyclerView=null;
	private LinearLayoutManager manager;
	private int lastVisibleItem;
	public MyViewAdapter adapter=null;
	private int size=1000;
	private int page = 1;
	public ArrayList<ZhongchouBean> listBean=null;
	private RelativeLayout zhiyan_my_denglu;
	private SwipeRefreshLayout zhiyan_myview_refresh=null;
	private RelativeLayout myview_reset=null;
	private Button zhiyan_myview_rebtn=null;
	private Boolean ResRefreshStatus=true;
	private RelativeLayout nogupiao_tip=null;
	
	public ZhiYanMyView(Context context){
		this.context=context;
		myView=LayoutInflater.from(context).inflate(R.layout.tljr_zhiyan_myview,null);
		initView();
		initData();
	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public void initView(){
		listBean=new ArrayList<ZhongchouBean>();
		mRecyclerView=(RecyclerView)myView.findViewById(R.id.myview_recyclerview);
		zhiyan_my_denglu=(RelativeLayout)myView.findViewById(R.id.zhiyan_my_denglu);
		myview_reset=(RelativeLayout)myView.findViewById(R.id.myview_reset);
		zhiyan_myview_rebtn=(Button)myView.findViewById(R.id.zhiyan_myview_rebtn);
		zhiyan_myview_rebtn.setOnClickListener(this);
		zhiyan_myview_refresh=(SwipeRefreshLayout)myView.findViewById(R.id.zhiyan_myview_refresh);
		zhiyan_myview_refresh.setOnRefreshListener(this);
		zhiyan_myview_refresh.setColorSchemeResources(android.R.color.holo_red_light);
		zhiyan_my_denglu.setOnClickListener(this);
		nogupiao_tip=(RelativeLayout)myView.findViewById(R.id.nogupiao_tip);
		manager = new LinearLayoutManager(context);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if(adapter!=null){
					if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
						//loadMore();
					}
				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = manager.findLastVisibleItemPosition();
			}
		});	
		adapter=new MyViewAdapter(context,listBean);
	}
	
	public void initData(){
		if(User.getUser()==null||Configs.token==null){
			listBean.clear();
			if(adapter!=null){
				adapter=new MyViewAdapter(context,listBean);
				mHandler.sendEmptyMessage(0);	
			}
			zhiyan_my_denglu.setVisibility(View.VISIBLE);
			zhiyan_myview_refresh.setVisibility(View.GONE);
			return ;
		}
		myview_reset.setVisibility(View.GONE);
		zhiyan_my_denglu.setVisibility(View.GONE);
		zhiyan_myview_refresh.setVisibility(View.VISIBLE);
//		String testUrl=UrlUtil.URL_ZR+"crowd/getMyCrowdList"+"?"+"uid="+User.getUser().getId()
//				+"&token="+Configs.token+"&page="+page+"&size="+size;
		NetUtil.sendGet(UrlUtil.URL_ZR+"crowd/getMyCrowdList","uid="+User.getUser().getId()
				+"&token="+Configs.token+"&page="+page+"&size="+size,new NetResult() {
				@Override
				public void result(String msg) {
					listBean.clear();
					if(!msg.equals("")){
						boolean status=ZhiYanParseJson.ParseMyViewJson(listBean,msg);
						if(status==true&&listBean.size()>0){
							sortListBean(listBean);
							adapter=new MyViewAdapter(context,listBean);
							mHandler.sendEmptyMessage(0);	
						}else{
							myview_reset.setVisibility(View.GONE);
							zhiyan_my_denglu.setVisibility(View.GONE);
							zhiyan_myview_refresh.setVisibility(View.GONE);
							nogupiao_tip.setVisibility(View.VISIBLE);
						}
					}else{
						//没有网络的时候
						myview_reset.setVisibility(View.VISIBLE);
						zhiyan_myview_refresh.setVisibility(View.GONE);
						nogupiao_tip.setVisibility(View.GONE);
						Toast.makeText(context,"数据加载失败",Toast.LENGTH_SHORT).show();
					}
					ResRefreshStatus=true;
				}
		});
	}

	public void loadMore(){
		NetUtil.sendGet(UrlUtil.URL_ZR+"crowd/getMyCrowdList","uid="+User.getUser().getId()
				+"&token="+Configs.token+"&page="+page+"&size="+size,new NetResult() {
				@Override
				public void result(String msg) {
					boolean status=ZhiYanParseJson.ParseMyViewJson(listBean,msg);
					if(status==true&&listBean.size()>0){
						sortListBean(listBean);
						mHandler.sendEmptyMessage(1);	
						page++;
					}
				}
		});
	}
	
	public void sortListBean(ArrayList<ZhongchouBean> listBeans){
		for(int i=0;i<listBeans.size();i++){
			for(int j = 0; j <listBeans.size() - i - 1; j++){
				if(listBeans.get(j).getStatus()>listBeans.get(j+1).getStatus()){
					ZhongchouBean tempforign=listBeans.get(j);
					listBeans.set(j,listBeans.get(j+1));
					listBeans.set(j+1,tempforign);
				}
			}
		}
	}
	
	final Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				if(adapter!=null){
					mRecyclerView.setAdapter(adapter);
					nogupiao_tip.setVisibility(View.GONE);
					zhiyan_myview_refresh.setRefreshing(false);
				}
				break;
			case 1:
				adapter.notifyDataSetChanged();	
				zhiyan_myview_refresh.setRefreshing(false);
				break;
			}
		};
	};
	
	public View getView(){
		return myView;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.zhiyan_my_denglu:
			Intent intent=new Intent(context,WXEntryActivity.class);
			context.startActivity(intent);
			break;
		case R.id.zhiyan_myview_rebtn:
			if(ResRefreshStatus){
				ResRefreshStatus=false;
				initData();
			}
			break;
		}
	}

	@Override
	public void onRefresh() {
		initData();
	}
	
	public void refreshAdapterView(){
		myview_reset.setVisibility(View.GONE);
		nogupiao_tip.setVisibility(View.GONE);
		zhiyan_my_denglu.setVisibility(View.GONE);
		zhiyan_myview_refresh.setVisibility(View.VISIBLE);
	}
	
}

