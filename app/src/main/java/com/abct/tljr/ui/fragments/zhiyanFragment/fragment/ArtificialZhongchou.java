package com.abct.tljr.ui.fragments.zhiyanFragment.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.UserCrowd;
import com.abct.tljr.model.UserCrowdUser;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.ZhiyanArtZhongchouAdapter;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

/**
 * Created by Administrator on 2016/1/27.
 */
public class ArtificialZhongchou implements OnClickListener {
	Context context;
	View v;
	public ZhiyanArtZhongchouAdapter adapter;
	public ArrayList<ZhongchouBean> list = new ArrayList<ZhongchouBean>();
	private ArrayList<ZhongchouBean> mList = new ArrayList<ZhongchouBean>();
	private RecyclerView lv;
	private LinearLayoutManager manager;
	private int lastVisibleItem;
	private int page = 1;
	private int size = 10;
	private boolean isFlush = false;
	private View artificaialview=null;
	private RelativeLayout artificial_reset=null;
	private Button artificial_rebtn=null;
	public ZhongchouBean updatebean=null;
	
	public View getView() {
		return artificaialview;
	}

	@SuppressWarnings("deprecation")
	public ArtificialZhongchou(Context context) {
		this.context = context;
		artificaialview=LayoutInflater.from(context).inflate(R.layout.fragment_artificial,null);
		artificial_reset=(RelativeLayout)artificaialview.findViewById(R.id.artificial_reset);
		artificial_rebtn=(Button)artificaialview.findViewById(R.id.zhiyan_artificial_rebtn);
		artificial_rebtn.setOnClickListener(this);
		lv =(RecyclerView)artificaialview.findViewById(R.id.artificalrecycerview);
		list = new ArrayList<ZhongchouBean>();
		adapter = new ZhiyanArtZhongchouAdapter(context, list);
		manager = new LinearLayoutManager(context);
		lv.setLayoutManager(manager);
		lv.setItemAnimator(new DefaultItemAnimator());
		lv.setAdapter(adapter);
		lv.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
					loadMore();
				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = manager.findLastVisibleItemPosition();
			}
		});
		initData();
	}

	public void initData() {
		if (lv == null) {
			return;
		}
		page = 1;
		list.clear();
		isFlush = false;
		loadMore();
	}

	public void loadMore() {
		if (isFlush) {
			lv.stopScroll();
			return;
		}
		isFlush = true;
		String url;
		if (User.getUser() == null) {
			url = UrlUtil.URL_ZR + "crowd/getList?status=0&page=" + page + "&size=" + size;
		} else {
			url = UrlUtil.URL_ZR + "crowd/getList?status=0&page=" + page + "&size=" + size + 
					"&uid="+ User.getUser().getId();
		}
//		String urltest=url;
		ProgressDlgUtil.showProgressDlg("", (Activity) context);
		NetUtil.sendGet(url, new NetResult() {
			@Override
			public void result(String msg) {
				ProgressDlgUtil.stopProgressDlg();
				if (msg != null && !msg.equals("")) {
					try {
						page++;
						artificial_reset.setVisibility(View.GONE);
						lv.setVisibility(View.VISIBLE);
						JSONObject object = new JSONObject(msg);
						if (object.getInt("status") == 1){
							JSONArray arr = object.getJSONArray("msg");
							for (int i = 0; i < arr.length(); i++) {
								JSONObject ob = arr.getJSONObject(i);
								ZhongchouBean bean = new ZhongchouBean();
								bean.setCode(ob.optString("code"));
								bean.setCount(ob.optInt("count"));
								bean.setCreateDate(ob.optLong("createDate"));
								bean.setEndDate(ob.optLong("endDate"));
								bean.setHasMoney(ob.optInt("hasMoney"));
								bean.setIconurl(ob.optString("icon"));
								bean.setId(ob.optString("id"));
								bean.setMarket(ob.optString("market"));
								bean.setPrice(ob.optInt("price"));
								bean.setStatus(ob.optInt("status"));
								bean.setTotalMoney(ob.optInt("totalMoney"));
								bean.setType(ob.optString("type"));
								bean.setTypedesc(ob.optString("typedesc"));
								bean.setFocus(ob.optInt("focus"));
								bean.setRemark(ob.optInt("remark"));
								bean.setIsfocus(ob.optBoolean("isfocus"));
								bean.setReward(ob.optInt("reward"));
								bean.setRewardMoney(ob.optInt("rewardMoney"));
								bean.setSection(ob.optString("section"));
								JSONArray ar2 = ob.getJSONArray("userCrowd");
								ArrayList<UserCrowd> list = new ArrayList<UserCrowd>();
								for (int x = 0; x < ar2.length(); x++) {
									JSONObject o = ar2.getJSONObject(x);
									UserCrowd crowd = new UserCrowd();
									crowd.setCcrowdId(o.getString("crowdId"));
									crowd.setCdate(o.getLong("date"));
									crowd.setCid(o.getString("id"));
									crowd.setCinit(o.getBoolean("init"));
									crowd.setCmoney(o.getInt("money"));
									crowd.setAllMoney(o.getInt("allMoney"));
									crowd.setFocs(o.optBoolean("isFocus"));
									crowd.setMsg(o.optString("msg"));
									JSONObject object1 = o.getJSONObject("user");
									UserCrowdUser u = new UserCrowdUser();
									u.setUavata(object1.getString("avatar"));
									u.setUdata(object1.getLong("date"));
									u.setUlevel(object1.getInt("level"));
									u.setUuid(object1.getString("uid"));
									u.setUnickname(object1.getString("nickName"));
									JSONObject object2 = object1.getJSONObject("userInfo");
									u.setAllMoney(object2.getInt("allMoney"));
									u.setCount(object2.getInt("count"));
									crowd.setUser(u);
									list.add(crowd);
								}
								bean.setUserCrowdList(list);
								mList.add(bean);
							}
							if (mList.size() > 0) {
								list.addAll(mList);
								mList.clear();
								lv.stopScroll();
								adapter.notifyDataSetChanged();
								isFlush = false;
							} else {
								lv.stopScroll();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						isFlush = false;
					}
				}else{
					//没有网络时的处理
					artificial_reset.setVisibility(View.VISIBLE);
					lv.setVisibility(View.GONE);
				}
			}
		});
	}

	public void UpdateMyView(){
		 if(updatebean!=null){
			 boolean status=true; 
			  List<ZhongchouBean> listBean= MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean;
			  for(int y=0;y<listBean.size();y++){
				  if(listBean.get(y).getCode().equals(updatebean.getCode())){
					  status=false;
					  break;
				  }
			  } 
			  if(status){
				  updatebean.setFinishstatus(true);
					for(int i=0;i<MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.size();i++){
						if(MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.get(i).getStatus()==0){
							MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.get(i).setFinishstatus(false);
						}
					}
					MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.add(0,updatebean);
					MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
					MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum=
							MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum+1;
					MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton.setText(
							"我的("+(MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)+")");
			  }
		 }
	   }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.zhiyan_artificial_rebtn:
			initData();
			break;
		}
	}

}