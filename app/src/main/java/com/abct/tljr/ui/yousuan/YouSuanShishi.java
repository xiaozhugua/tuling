package com.abct.tljr.ui.yousuan;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.yousuan.adapter.YouSuanItemAdapter;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.pay.AliPay;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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

public class YouSuanShishi extends Fragment implements OnClickListener {

	private View yousuanshishi;
	private RecyclerView yousuanlist = null;
	public YouSuanItemAdapter adapter = null;
	public int page = 1;
	private String shishi=UrlUtil.Url_235+"8080/yousuan/rest/gentou/getGenTouList";
	public List<YouSuanItemModel> listModel;
	private RelativeLayout resBtnRelativeLayout;
	private Button resBtn;
	private boolean RefreshStatus = true;
	private LinearLayoutManager mLinearLayoutManager = null;
	private int lastVisibleItem = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		yousuanshishi = inflater.inflate(R.layout.yousuanshishi, container,false);
		initView();
		LodeMore();
		initRadioButton();
		return yousuanshishi;
	}

	@SuppressWarnings("deprecation")
	public void initView() {
		listModel = new ArrayList<YouSuanItemModel>();
		yousuanlist = (RecyclerView) yousuanshishi.findViewById(R.id.yousuanshishi);
		resBtnRelativeLayout = (RelativeLayout) yousuanshishi.findViewById(R.id.yousuanshishi_reset);
		resBtn = (Button) yousuanshishi.findViewById(R.id.yousuan_rebtn);
		resBtn.setOnClickListener(this);
		mLinearLayoutManager = new LinearLayoutManager(getActivity());
		yousuanlist.setLayoutManager(mLinearLayoutManager);
		yousuanlist.setItemAnimator(new DefaultItemAnimator());
		yousuanlist.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView,int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (adapter != null) {
					if (newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==adapter.getItemCount()) {
						LodeMore();
					}
				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView,int dx,int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
			}
		});
	}

	public void initData() {
		LodeMore();
	}

	public void LodeMore() {
		ProgressDlgUtil.showProgressDlg("", getActivity());
		String param = "";
		if (MyApplication.getInstance().self != null) {
			param = "uid=" + MyApplication.getInstance().self.getId()+ "&page=" + page + "&size=" + 10;
		} else {
			param = "page=" + page + "&size=" + 10;
		}
        LogUtil.e("shishiurl",shishi+"?"+param);
		NetUtil.sendGet(shishi,param,new NetResult() {
			@Override
			public void result(String msg) {
				try {
					RefreshStatus = true;
					if (!msg.equals("")){
						resBtnRelativeLayout.setVisibility(View.GONE);
						yousuanlist.setVisibility(View.VISIBLE);
						if (ZhiYanParseJson.parseYouSuanShishi(listModel,msg)) {
							// 数据获取解析成功
							page++;
							if (listModel.size() > 0) {
								if (adapter == null) {
									adapter = new YouSuanItemAdapter(getActivity(),R.layout.yousuanshishiitem,listModel);
									mHandler.sendEmptyMessage(0);
								} else {
									mHandler.sendEmptyMessage(1);
								}
							}
						} else {
							// 数据获取解析失败
							Toast.makeText(getActivity(),"没有更多数据了",Toast.LENGTH_SHORT).show();
						}
					} else {
						// 没有获取数据
						resBtnRelativeLayout.setVisibility(View.VISIBLE);
						yousuanlist.setVisibility(View.GONE);
						Toast.makeText(getActivity(),"获取数据失败",Toast.LENGTH_SHORT).show();
					}
					ProgressDlgUtil.stopProgressDlg();
				} catch (Exception e) {
					// 出错
				}
			}
		});
	}

	public void onpay(final String type, float quan, String couponId){
		float money = (float) adapter.paybean.getRewardMoney()/100;
		String parms ="id="+adapter.paybean.getId()+"&uid="+ MyApplication.getInstance().self.getId()
				+"&money="+money+"&type="+type+"&token="+Configs.token;
		parms += ("&quan=" + quan);
		if (couponId != null)
			parms += ("&couponId=" + couponId);
		parms += ("&payType=3");
//		LogUtil.e("textYoutSuan",UrlUtil.URL_ZR+"crowd/reward"+parms);
		NetUtil.sendPost(UrlUtil.Url_235+"8080/yousuan/rest/gentou/pay",parms, new NetResult() {
			@Override
			public void result(final String msg) {
				LogUtil.e("Test","Launch onpay:"+msg);
				try {
					JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						if (type.equals("0")) {
							paySuccess(object.optString("msg"));
						} else if (type.equals("1")) {
							LogUtil.e("Test", "in 1");
							AliPay.getInstance().init(getActivity()).pay(object.getString("msg"));
						} else if (type.equals("3")) {
							LogUtil.e("Test", "in 3");
							PayReq req = new PayReq();
							// req.appId = "wxf8b4f85f3a794e77";
							// // 测试用appId
							JSONObject json = object.getJSONObject("msg");
							req.appId = json.getString("appid");
							req.partnerId = json.getString("partnerid");
							req.prepayId = json.getString("prepayid");
							req.nonceStr = json.getString("noncestr");
							req.timeStamp = json.getString("timestamp");
							req.packageValue = json.getString("package");
							req.sign = json.getString("sign");
							req.extData = "app data"; //optional
							// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
							LogUtil.e("Test", "in 3");
							MyApplication.getInstance().getMainActivity().api.sendReq(req);
						} else {
							Intent intent = new Intent(getActivity(), WebActivity.class);
							intent.putExtra("url", object.getString("msg"));
							getActivity().startActivity(intent);
						}
					} else {
						((MainActivity) getActivity()).showToast(object.getString("msg"));
					}
				} catch (JSONException e) {
					ProgressDlgUtil.stopProgressDlg();
					e.printStackTrace();
				}

			}
		});
	}
	
	public void paySuccess(String msg){
		Toast.makeText(getActivity(),"支付成功",Toast.LENGTH_SHORT).show();
	}
	
	public void initRadioButton() {
		MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment.gengtou.setText("机器人(" + listModel.size() + ")");
	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				yousuanlist.setAdapter(adapter);
				initRadioButton();
				break;
			case 1:
				adapter.notifyDataSetChanged();
				initRadioButton();
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.yousuan_rebtn:
			if (RefreshStatus) {
				RefreshStatus = false;
				LodeMore();
			}
			break;
		}
	}

}
