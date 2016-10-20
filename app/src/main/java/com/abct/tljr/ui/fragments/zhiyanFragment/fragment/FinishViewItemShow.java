package com.abct.tljr.ui.fragments.zhiyanFragment.fragment;

import java.util.ArrayList;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.ZhiyanFinishiAdapter;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanFinishHeader;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FinishViewItemShow extends Fragment implements OnClickListener {
	private String FirstItemUrl="http://news.tuling.me/DataApi/index/genre";
	private View FinishViewItem = null;
	private String position = "0";
	public ArrayList<ZhongchouBean> listBean = null;
	private RecyclerView mRecyclerView = null;
	private int size = 10;
	private int page = 1;
	private LinearLayoutManager manager=null;
	private int lastVisibleItem=0;
	public ZhiyanFinishiAdapter adapter;
	private int CountItem=0;
	private int ClickPosition=0;
	private RelativeLayout finishview_reset=null;
	private Button resBtn=null;
	private boolean ResRefreshStatus=true;
	boolean firstinit=true;
	private ZhiYanFinishHeader header=null;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
            if(args!=null){
                this.position = args.getString("position");
		        this.CountItem=args.getInt("countItem");
		        this.ClickPosition=args.getInt("clickPosition");
        }
		listBean = new ArrayList<ZhongchouBean>();
    }

//    public FinishViewItemShow(String position, int countItem, int clickPosition) {
//		this.position = position;
//		this.CountItem=countItem;
//		this.ClickPosition=clickPosition;
//		listBean = new ArrayList<ZhongchouBean>();
//	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		FinishViewItem = inflater.inflate(R.layout.finishviewitemshow, null);
		mRecyclerView = (RecyclerView) FinishViewItem.findViewById(R.id.finishitemshow);
		finishview_reset=(RelativeLayout)FinishViewItem.findViewById(R.id.finishview_reset);
		resBtn=(Button)FinishViewItem.findViewById(R.id.zhiyan_finishview_rebtn);
		resBtn.setOnClickListener(this);
		manager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if(adapter!=null){
					if (newState == RecyclerView.SCROLL_STATE_IDLE&& lastVisibleItem + 1 == adapter.getItemCount()) {
						if(CountItem!=adapter.getItemCount()){
							loadMore();
						}
					}
				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = manager.findLastVisibleItemPosition();
			}
		});
		getCrowdData();
		return FinishViewItem;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void getCrowdData() {
	 ProgressDlgUtil.showProgressDlg("",getActivity());	
	 String url = "";
		if (MyApplication.getInstance().self == null) {
			url = UrlUtil.URL_ZR + "crowd/getListByType?page=" + page+ "&size=" + size + "&type=" + position;
		} else {
			url = UrlUtil.URL_ZR + "crowd/getListByType?page=" + page+ "&size=" + size + "&uid="+ 
												MyApplication.getInstance().self.getId() + "&type="+ position;
		}
		NetUtil.sendGet(url, new NetResult() {
			@Override   
			public void result(String msg) {
				if (!msg.equals("")) {
					page++;
					ZhiYanParseJson.parseZhiYanCrowd(listBean,msg);
					if(listBean.size()<=0){
						return ;
					}
					if(adapter==null){
						adapter=new ZhiyanFinishiAdapter(getActivity(),listBean,ClickPosition,position);
						mHandler.sendEmptyMessage(0);
					}else{
						mHandler.sendEmptyMessage(1);
					}
				} else {
					//获取数据失败
					finishview_reset.setVisibility(View.VISIBLE);
					mRecyclerView.setVisibility(View.GONE);
					ProgressDlgUtil.stopProgressDlg();
					Toast.makeText(getActivity(),"数据加载失败",Toast.LENGTH_SHORT).show();
				}
				ResRefreshStatus=true;	
			}
		});
	}

	public void loadMore(){
		getCrowdData();
	}
	
	final Handler mHandler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				mRecyclerView.setAdapter(adapter);
				ProgressDlgUtil.stopProgressDlg();
				finishview_reset.setVisibility(View.GONE);
				mRecyclerView.setVisibility(View.VISIBLE);
				ZhiYanHeaderUpdate();
				break;
			case 1:
				adapter.notifyDataSetChanged();
				ProgressDlgUtil.stopProgressDlg();
				finishview_reset.setVisibility(View.GONE);
				mRecyclerView.setVisibility(View.VISIBLE);
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.zhiyan_finishview_rebtn:
				if(ResRefreshStatus){
					ResRefreshStatus=false;
					getCrowdData();
				}
				break;
		}
	}
	
	public void ZhiYanHeaderUpdate(){
		String url=FirstItemUrl+"?type="+position;
		NetUtil.sendGet(url,new NetResult() {
			@Override
			public void result(String response) {
				LogUtil.e("initData_Response",response);
				if(!response.equals("")){
					//网络访问成功,获取数据成功
					header=ZhiYanParseJson.parseZhiYanHeaderData(response);
					if(header!=null){
						ZhongchouBean first=new ZhongchouBean();
						first.setFirst(1);
						first.setCode("");
						listBean.add(0,first);
						adapter.setZhiYanFinishHeader(header);
						adapter.notifyDataSetChanged();
					}
				}else{
					//网络访问失败
					Toast.makeText(getActivity(),"网络访问失败",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	
}
