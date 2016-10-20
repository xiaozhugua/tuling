package com.abct.tljr.ui.fragments.zhiyanFragment.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.ZhiYanLeftBeanAdapter;
import com.abct.tljr.ui.fragments.zhiyanFragment.fragment.FinishViewItemShow;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanLeftBean;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ZhiYanFinishView implements OnItemClickListener, OnClickListener {

	private View ZhiYanFinishView = null;
	private Context context = null;
	private ListView listTitle = null;
	public List<ZhiYanLeftBean> listLeftBeans;
	private ZhiYanLeftBeanAdapter leftAdapter = null;
	public static int leftPosition = 0;
	public FragmentManager fragmentManager = null;
	public static boolean first = true;
	public int nowSection = 0;
	public Map<Integer, Integer> saveSection = null;
	private RelativeLayout finish_reset = null;
	private Button zhiyan_finish_rebtn = null;
	private boolean ResRefreshStatus = true;
	//private String TitleUrl = "http://news.tuling.me/DataApi/index/rank";

	public ZhiYanFinishView() {
		super();
	}

	public ZhiYanFinishView(Context context) {
		this.context = context;
		ZhiYanFinishView = LayoutInflater.from(context).inflate(R.layout.zhiyanfinishview, null);
		initView();
		initTitle();
		// initLeftTitle();
	}

	public void initView() {
		listTitle = (ListView) ZhiYanFinishView.findViewById(R.id.zhiyan_list_item);
		zhiyan_finish_rebtn = (Button) ZhiYanFinishView.findViewById(R.id.zhiyan_finish_rebtn);
		finish_reset = (RelativeLayout) ZhiYanFinishView.findViewById(R.id.finish_reset);
		zhiyan_finish_rebtn.setOnClickListener(this);
		listLeftBeans = new ArrayList<ZhiYanLeftBean>();
		saveSection = new HashMap<Integer, Integer>();
		fragmentManager = ((FragmentActivity) context).getFragmentManager();
		listTitle.setOnItemClickListener(this);
	}

	public void initTitle() {
		//String test=UrlUtil.URL_ZR+"crowd/getTypeList";
		NetUtil.sendGet(UrlUtil.URL_ZR + "crowd/getTypeList", new NetResult() {
			@Override
			public void result(String msg) {
				try {
					if (!msg.equals("")) {
						if (finish_reset.getVisibility() == View.VISIBLE) {
							finish_reset.setVisibility(View.GONE);
						}
						listLeftBeans = ZhiYanParseJson.parseZhiYanTitle(msg);
						if (listLeftBeans != null) {
							leftAdapter = new ZhiYanLeftBeanAdapter(context,
									R.layout.fragment_zhiyan_finishleftitem,listLeftBeans);
							mHandler.sendEmptyMessage(0);
						}
					} else {
						// 获取网络数据失败
						finish_reset.setVisibility(View.VISIBLE);
						Toast.makeText(context, "数据加载失败", Toast.LENGTH_SHORT).show();
					}
					ResRefreshStatus = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				listTitle.setAdapter(leftAdapter);
				setLeftSection(0);
				break;
			}
		}
	};

	// 登录和登出
	public void LogoutInOut() {
		if (listLeftBeans.size() > 0) {
			for (int i = 0; i < listLeftBeans.size(); i++) {
				if (listLeftBeans.get(i).getFinishViewItemShow() != null) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					transaction.remove(listLeftBeans.get(i)
							.getFinishViewItemShow());
					transaction.commitAllowingStateLoss();
					listLeftBeans.get(i).setFinishViewItemShow(null);
				}
			}
			setLeftSection(0);
		}
	}

	public View getView() {
		return ZhiYanFinishView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent,View view,int position,long id) {
		nowSection = position;
		setLeftSection(position);
	}

	// 点击item切换fragment
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setLeftSection(int position) {
		saveSection.put(position, position);
		resetBtn(position);
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragment(transaction);
		if (listLeftBeans.get(position).getFinishViewItemShow() == null) {
//			listLeftBeans.get(position).setFinishViewItemShow(
//				new FinishViewItemShow(listLeftBeans.get(position).getType(),listLeftBeans.get(position).getCount(),position));
			listLeftBeans.get(position).setFinishViewItemShow(newInstance(
                    listLeftBeans.get(position).getType(),listLeftBeans.get(position).getCount(),position));
			transaction.add(R.id.zhiyanfinishView, listLeftBeans.get(position).getFinishViewItemShow());
		} else {
			transaction.show(listLeftBeans.get(position).getFinishViewItemShow());
		}
		transaction.commitAllowingStateLoss();
	}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static FinishViewItemShow newInstance(String position, int countItem, int clickPosition){
        FinishViewItemShow finishViewItemShow=new FinishViewItemShow();
        Bundle bundle=new Bundle();
        bundle.putString("position",position);
        bundle.putInt("countItem",countItem);
        bundle.putInt("clickPosition",clickPosition);
        finishViewItemShow.setArguments(bundle);
        return finishViewItemShow;
    }


	public void hideFragment(FragmentTransaction transaction) {
		for (int i = 0; i < listLeftBeans.size(); i++) {
			if (listLeftBeans.get(i).getFinishViewItemShow() != null) {
				transaction.hide(listLeftBeans.get(i).getFinishViewItemShow());
			}
		}
	}

	public void resetBtn(int position) {
		for (int i = 0; i < listLeftBeans.size(); i++) {
			listLeftBeans.get(i).setPosition(-1);
		}
		listLeftBeans.get(position).setPosition(1);
		leftAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.zhiyan_finish_rebtn:
			if (ResRefreshStatus) {
				ResRefreshStatus = false;
				initTitle();
			}
			break;
		}
	}

}
