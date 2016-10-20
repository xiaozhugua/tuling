package com.abct.tljr.ui.fragments;

import java.io.File;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.UserCrowd;
import com.abct.tljr.model.UserCrowdUser;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.activity.zhiyan.PayActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.DownloadProUtil;
import com.abct.tljr.utils.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.pay.AliPay;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ComputerFragment extends BaseFragment {

	private RelativeLayout title;
	private RecyclerView lv;
	private int lastVisibleItem;
	private MyAdapter adapter;
	private ArrayList<ZhongchouBean> list = new ArrayList<ZhongchouBean>();
	private ArrayList<ZhongchouBean> mList = new ArrayList<ZhongchouBean>();
	private LinearLayoutManager manager;

	private boolean isFlush = false;
	private int page = 0;
	private int size = 10;
	
	public ZhongchouBean getBean() {
		if (adapter == null)
			return null;
		return adapter.paybean;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baseView = new LinearLayout(activity);
	}
	
	@SuppressWarnings("deprecation")
	public void init() {
		if (title != null) {
			return;
		}
		((LinearLayout) baseView).setOrientation(LinearLayout.VERTICAL);
		baseView.setBackgroundColor(getResources().getColor(R.color.tljr_bj));
//		title = (RelativeLayout) View.inflate(activity, R.layout.tljr_view_activity_title, null);
//		title.findViewById(R.id.back).setVisibility(View.GONE);
//		((LinearLayout) baseView).addView(title,new LinearLayout.LayoutParams(
//				android.widget.LinearLayout.LayoutParams.FILL_PARENT,AppInfo.dp2px(activity, MyApplication.TitleHeight)));
//		((TextView) title.findViewById(R.id.name)).setText("优算");
		lv = new RecyclerView(getActivity());
		RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// lv.setLayoutParams(params);
		((LinearLayout) baseView).addView(lv, params);
		lv.setAdapter(adapter);
		manager = new LinearLayoutManager(getContext());
		lv.setLayoutManager(manager);
		adapter = new MyAdapter(getContext(), list);
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
		page = 0;
		list.clear();
		isFlush = false;
		loadMore();
	}

	private void loadMore() {
		if (isFlush) {
			lv.stopScroll();
			return;
		}
		isFlush = true;
		getData();
	}
	
	private void getData() {
		LogUtil.e("getData", "获取数据。。、。。。");
		page++;
		String parms = "page=" + page + "&size=" + size;
		if (User.getUser() != null)
			parms += ("&uid=" + User.getUser().getId());
		ProgressDlgUtil.showProgressDlg("", getActivity());
		NetUtil.sendGet(UrlUtil.URL_ZR + "computer/getList", parms, new NetResult() {
			@Override
			public void result(String msg) {
				LogUtil.e("getData", msg);
				ProgressDlgUtil.stopProgressDlg();
				try {
					final JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						final org.json.JSONArray array = object.getJSONArray("msg");
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							ZhongchouBean bean = new ZhongchouBean();
							bean.setCreateDate(obj.optLong("date"));
							bean.setFocus(obj.optInt("focus"));
							bean.setIconurl(obj.optString("icon"));
							bean.setId(obj.optString("id"));
							bean.setLook(obj.optBoolean("isPay"));
							bean.setRewardMoney(obj.optInt("rewardMoney"));
							bean.setType(obj.optString("name"));
							bean.setTypedesc(obj.optString("desc"));
							bean.setSection(obj.optString("pdfURL"));
							JSONArray ar2 = obj.optJSONArray("items");
							ArrayList<UserCrowd> list = new ArrayList();
							for (int x = 0; x < ar2.length(); x++) {
								JSONObject o = ar2.getJSONObject(x);
								UserCrowd crowd = new UserCrowd();
								crowd.setCcrowdId(o.optString("computerId"));
								crowd.setCdate(o.optLong("date"));
								crowd.setCid(o.optString("uid"));
								JSONObject object1 = o.getJSONObject("user");
								UserCrowdUser u = new UserCrowdUser();
								u.setUavata(object1.optString("avatar"));
								u.setUdata(object1.optLong("date"));
								u.setUlevel(object1.optInt("level"));
								u.setUuid(object1.optString("uid"));
								u.setUnickname(object1.optString("nickName"));
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
							MyApplication.getInstance().getMainActivity().mYouSuanBaseFragment
									.celuechi.setText("策略池("+list.size()+")");
							isFlush = false;
						} else {
							lv.stopScroll();
						}
					} else {
						lv.stopScroll();
						isFlush = false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void paySuccess(String msg) {
		((MainActivity) getActivity()).showToast(msg);
		initData();
	}

	public void onpay(final String type, float quan, String couponId) {
		float money = (float) adapter.paybean.getRewardMoney() / 100;
		String parms = "id=" + adapter.paybean.getId() + "&uid=" + 
				MyApplication.getInstance().self.getId() + "&money="
				+ money + "&type=" + type + "&token=" + Configs.token;
		parms += ("&quan=" + quan);
		if (couponId != null)
			parms += ("&couponId=" + couponId);
		parms += ("&payType=3");
		//String testUrl=UrlUtil.URL_ZR + "crowd/reward"+parms;
		NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/reward", parms, new NetResult() {
			@Override
			public void result(final String msg) {
				LogUtil.e("Test", "Launch onpay :" + msg);
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
							req.extData = "app data"; // optional
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
}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

	private Context context;
	private ArrayList<ZhongchouBean> list;
	public ZhongchouBean paybean;

	public MyAdapter(Context context, ArrayList<ZhongchouBean> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(
				R.layout.fragment_zhiyan_computer_item, parent, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final ZhongchouBean bean = list.get(position);
		holder.title.setText(bean.getType());
		holder.desc.setText(bean.getTypedesc());
		UserCrowdUser user = bean.getUserCrowdList().get(0).getUser();
		holder.name.setText(user.getUnickname());
		holder.level.setText("Lv:" + user.getUlevel());
		holder.date.setText("于" + DateUtil.getDateNoss(user.getUdata() * 1000) + "发起");
		holder.haspay.setVisibility(bean.isLook() ? View.VISIBLE : View.GONE);
		ImageLoader.getInstance().displayImage(user.getUavata(), holder.avatar, Options.getCircleListOptions());
		ImageLoader.getInstance().displayImage(bean.getIconurl(), holder.im, Options.getListOptions());

		holder.itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (User.getUser() == null) {
					Toast.makeText(context, "请先登录！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!bean.isLook()) {
					new PromptDialog(context, "首次阅读需要支付" + (float) bean.getRewardMoney() / 100 + "元,是否支付？",
							new Complete() {
						@Override
						public void complete() {
							paybean = new ZhongchouBean();
							paybean.setRewardMoney(bean.getRewardMoney());
							paybean.setMarket(bean.getMarket());
							paybean.setCode(bean.getCode());
							paybean.setId(bean.getId());
							((Activity) context).startActivityForResult(new Intent(context, PayActivity.class)
									.putExtra("money", (float) bean.getRewardMoney() / 100).putExtra("canuse", false),3);
						}
					}).show();
					return;
				}
				final String name="."+bean.getId()+".pdf";
				File file = new File(Util.fileDirPath+"/"+name);
				if (file.exists()) {
					openPdf(file);
				} else {
					downLoadFile(bean.getSection(),"附件",name, new Complete() {
						@Override
						public void complete() {
							openPdf(new File(Util.fileDirPath+"/"+name));
						}
					});
				}
			}
		});
	}

	private void openPdf(File file) {
		try {
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(file);
			intent.setDataAndType(uri, "application/pdf");// 文档格式
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "未找到打开方式", Toast.LENGTH_SHORT).show();
		}
	}

	private void downLoadFile(final String url, final String title, final String apkName, final Complete complete) {
		if (!Constant.netType.equals("WIFI")) {
			new AlertDialog.Builder(context).setTitle("图灵金融").setMessage("当前为" + Constant.netType + "网络，下载会消耗流量，确认下载？")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							DownloadProUtil.showProgressDlg(title, url, apkName, (Activity) context, true, complete);
						}
					}).setNegativeButton("否", null).show();
		} else {
			DownloadProUtil.showProgressDlg(title, url, apkName, (Activity) context, true, complete);
		}
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public final class ViewHolder extends RecyclerView.ViewHolder {
		private TextView title, desc, name, level, date;
		private ImageView avatar, im, haspay;

		public ViewHolder(View v) {
			super(v);
			title = (TextView) v.findViewById(R.id.title);
			desc = (TextView) v.findViewById(R.id.info);
			name = (TextView) v.findViewById(R.id.personname);
			level = (TextView) v.findViewById(R.id.lv_person);
			date = (TextView) v.findViewById(R.id.persenbuy);
			avatar = (ImageView) v.findViewById(R.id.im22);
			im = (ImageView) v.findViewById(R.id.im);
			haspay = (ImageView) v.findViewById(R.id.haspay);
		}
	}
	
}