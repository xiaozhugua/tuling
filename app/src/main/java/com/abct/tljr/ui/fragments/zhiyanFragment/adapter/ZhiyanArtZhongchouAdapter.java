package com.abct.tljr.ui.fragments.zhiyanFragment.adapter;

import io.realm.Realm;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.ImageOptions;
import com.abct.tljr.dialog.SpeakDialog;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.UserCrowd;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.activity.zhiyan.PayActivity;
import com.abct.tljr.ui.activity.zhiyan.ReSearchActivity;
import com.abct.tljr.ui.widget.DefaultIconDrawable;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.widget.RoundProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.AppInfo;
import com.qh.common.model.User;
import com.qh.common.pay.AliPay;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class ZhiyanArtZhongchouAdapter extends RecyclerView.Adapter<ZhiyanArtZhongchouAdapter.MyViewHolder> {

	private Context context;
	private ArrayList<ZhongchouBean> list;
	public static ArrayList<UserCrowd> userCrowdList;
	private ZhongchouBean paybean;
	private IWXAPI api;
	
	
	public ZhiyanArtZhongchouAdapter(Context context, ArrayList<ZhongchouBean> list) {
		this.context = context;
		this.list = list;
		api = WXAPIFactory.createWXAPI(context, Configs.WeiXinAppId);                
	}

	@Override
	public ZhiyanArtZhongchouAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		MyViewHolder holder = new MyViewHolder(
				LayoutInflater.from(context).inflate(R.layout.fragment_zhiyan_artificial_listitem, parent, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(ZhiyanArtZhongchouAdapter.MyViewHolder holder, int position) {
		final int tempposition=position;
		final ZhongchouBean bean = list.get(position);
		Realm realm = Realm.getDefaultInstance();
		OneGu gu = realm.where(OneGu.class).equalTo("code", bean.getCode()).equalTo("market", bean.getMarket()).findFirst();
		if (gu != null)
			holder.name.setText(gu.getName());
		realm.close();
		int fen = bean.getUserCrowdList().get(0).getCmoney() / bean.getPrice();
		String str = "于" + DateUtil.getDate(bean.getUserCrowdList().get(0).getCdate() * 1000,
				"MM月dd日hh:mm发起已认购" + fen)+ "份(" + bean.getUserCrowdList().get(0).getCmoney() / 100 + "元)";
		holder.persenbuy.setText(str);
		holder.code.setText(bean.getCode());
		holder.type.setText(bean.getType());
		final int persen = bean.getHasMoney() * 100 / bean.getTotalMoney();
		holder.persen.setText(persen + "/100");
		if (persen >= 0)
			holder.bar.setProgress(persen);
		holder.line.getLayoutParams().width = AppInfo.WIDTH*persen/100;
		holder.personname.setText(bean.getUserCrowdList().get(0).getUser().getUnickname());
		holder.lv_person.setText("Lv " + bean.getUserCrowdList().get(0).getUser().getUlevel());
		holder.focus.setText(" " + (bean.isIsfocus() ? "已" : "") + "关注(" + bean.getFocus() + ")");
		holder.remark.setText(" 点评(" + bean.getRemark() + ")");
		holder.ds.setText(" 打赏(" + bean.getReward() + ")");
		String[] s = bean.getSection().split("-");
		for (int i = 0; i < holder.tvs.length; i++) {
			if (i < s.length && !s[i].equals("")) {
				holder.tvs[i].setVisibility(View.VISIBLE);
				holder.tvs[i].setText(s[i]);
			} else {
				holder.tvs[i].setVisibility(View.GONE);
			}
		}
		holder.rl_xianqing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, ReSearchActivity.class);
				i.putExtra("market", bean.getMarket());
				i.putExtra("code", bean.getCode());
				i.putExtra("id", bean.getId());
				i.putExtra("url", bean.getIconurl());
				i.putExtra("type", bean.getType());
				i.putExtra("typedesc", bean.getTypedesc());
				i.putExtra("persen", persen);
				i.putExtra("focus", bean.getFocus());
				i.putExtra("remark", bean.getRemark());
				i.putExtra("isfocus", bean.isIsfocus());
				i.putExtra("section", bean.getSection());
				i.putExtra("tempposition", tempposition);
				userCrowdList = bean.getUserCrowdList();
				MyApplication.getInstance().getMainActivity().zhiyanFragment.artzhongchou.updatebean=
						MyApplication.getInstance().getMainActivity().zhiyanFragment.artzhongchou.list.get(tempposition);
				context.startActivity(i);
			}
		});
		holder.ds.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (User.getUser() == null) {
					Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
				} else {
					paybean = bean;
					((Activity) context).startActivityForResult(new Intent(context, PayActivity.class).putExtra("money", 
									(float) bean.getRewardMoney() / 100).putExtra("canuse", false),1);
				}
			}
		});
		holder.focus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (User.getUser() == null) {
					Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
				} else {
					focus(tempposition,(TextView)v,bean);
				}
			}
		});
		holder.remark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (User.getUser() == null) {
					Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
				} else {
					new SpeakDialog(context, UrlUtil.URL_ZR + "crowd/remark","id=" + bean.getId() + 
							"&uid=" + User.getUser().getId() + "&token=" + Configs.token,
							new Complete() {
						@Override
						public void complete() {
							bean.setRemark(bean.getRemark() + 1);
							((TextView) v).setText(" 点评(" + bean.getRemark() + ")");
						}
					}).show();
				}
			}
		});
		if (holder.im.getTag() == null || !holder.im.getTag().equals(bean.getId())) {
			holder.im.setTag(bean.getId());
			LogUtil.e("bean.getIconurl()", bean.getIconurl() + "");
			if (bean.getIconurl().equals("http://www.baidu.com")) {
				holder.im.setImageDrawable(new DefaultIconDrawable(holder.im.getLayoutParams().height,
						holder.name.getText().toString(), true));
			} else {
				ImageLoader.getInstance().displayImage(bean.getIconurl(), holder.im, Options.getCircleListOptions());
			}
		}

		if (holder.im22.getTag() == null || !holder.im22.getTag().equals(bean.getId())) {
			holder.im22.setTag(bean.getId());
			ImageLoader.getInstance().displayImage(bean.getUserCrowdList().get(0).getUser().getUavata(), holder.im22,
					ImageOptions.getCircleListOptions());
		}
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	int[] colors = { ColorUtil.red, ColorUtil.green, ColorUtil.blue };

	public final class MyViewHolder extends RecyclerView.ViewHolder {
		TextView name;
		TextView code;
		TextView type;
		TextView persen;
		RoundProgressBar bar;
		View line;
		TextView personname;
		TextView lv_person;
		TextView persenbuy;
		ImageView im;
		ImageView im22;
		View rl_xianqing;
		TextView ds;
		TextView focus;
		TextView remark;
		TextView[] tvs = new TextView[3];

		public MyViewHolder(View itemView) {
			super(itemView);
			name = (TextView) itemView.findViewById(R.id.name);
			code = (TextView) itemView.findViewById(R.id.code);
			type = (TextView) itemView.findViewById(R.id.level);
			persen = (TextView) itemView.findViewById(R.id.persen);
			bar = (RoundProgressBar) itemView.findViewById(R.id.seekbar);
			line = itemView.findViewById(R.id.line);
			personname = (TextView) itemView.findViewById(R.id.personname);
			lv_person = (TextView) itemView.findViewById(R.id.lv_person);
			persenbuy = (TextView) itemView.findViewById(R.id.persenbuy);
			im = (ImageView) itemView.findViewById(R.id.im);
			im22 = (ImageView) itemView.findViewById(R.id.im22);
			ds = (TextView) itemView.findViewById(R.id.tv_DS);
			focus = (TextView) itemView.findViewById(R.id.tv_guanzhu);
			remark = (TextView) itemView.findViewById(R.id.tv_pinglun);
			rl_xianqing = (View) itemView.findViewById(R.id.rl_xianqing);
			tvs[0] = (TextView) itemView.findViewById(R.id.tv1);
			tvs[1] = (TextView) itemView.findViewById(R.id.tv2);
			tvs[2] = (TextView) itemView.findViewById(R.id.tv3);
			((GradientDrawable) type.getBackground()).setStroke(2, colors[0]);
			for (int i = 0; i < tvs.length; i++) {
				tvs[i].setTextColor(colors[i]);
				GradientDrawable gd = (GradientDrawable) tvs[i].getBackground();
				gd.setStroke(2, colors[i]);
			}
		}
	}

	private void focus(final int position,final TextView v, final ZhongchouBean bean) {
		String a = v.getText().toString();
		int type = a.contains("已") ? 0 : 1;
		ProgressDlgUtil.showProgressDlg("", (Activity) context);
		NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/focus",
				"id=" + bean.getId() + "&uid=" + User.getUser().getId() + "&token=" + Configs.token + "&type=" + type,
				new NetResult() {
					@Override
					public void result(String msg) {
						LogUtil.e("focus", msg);
						ProgressDlgUtil.stopProgressDlg();
						try {
							JSONObject jsonObject = new JSONObject(msg);
							if (jsonObject.getInt("status") == 1) {
								Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
								String a = v.getText().toString();
								int type = a.contains("已") ? 0 : 1;
								if (type == 0) {
									bean.setIsfocus(false);
									bean.setFocus(bean.getFocus() - 1);
									v.setText(" " + "关注(" + bean.getFocus() + ")");
									List<ZhongchouBean> listbean=MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean;
									String key=list.get(position).getMarket()+list.get(position).getCode();
									for(int i=0;i<listbean.size();i++){
										if(key.equals(listbean.get(i).getMarket()+listbean.get(i).getCode())){
											listbean.remove(i);
											break;
										}
									}
									MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
									MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum=
											MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum-1;
									MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton.setText(
											"我的("+(MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)+")");
									
								} else {
									bean.setIsfocus(true);
									bean.setFocus(bean.getFocus() + 1);
									v.setText(" " + "已关注(" + bean.getFocus() + ")");
									list.get(position).setFinishstatus(true);
									for(int i=0;i<MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.size();i++){
										if(MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.get(i).getStatus()==0){
											MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.get(i).setFinishstatus(false);
										}
									}
									MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.add(0,list.get(position));
									MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
									MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum=
											MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum+1;
									MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton.setText(
											"我的("+(MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)+")");
								}
							} else {
								Toast.makeText(context, "服务器连接失败！", Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Toast.makeText(context, "服务器连接失败！", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public void onpay(final String type, float quan, String couponId) {
		float money = (float) paybean.getRewardMoney() / 100;
		LogUtil.e("money1", paybean.getRewardMoney() + "");
		LogUtil.e("money", money + "");
		String parms = "id=" + paybean.getId() + "&uid=" + MyApplication.getInstance().self.getId() + "&money=" + money
				+ "&type=" + type + "&token=" + Configs.token;
		if (quan != 0)
			parms += ("&quan=" + quan);
		if (couponId != null)
			parms += ("&couponId=" + couponId);
		parms += ("&payType=1");
		LogUtil.e("Test", UrlUtil.URL_ZR + "crowd/reward?" + parms);
		NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/reward", parms, new NetResult() {
			@Override
			public void result(final String msg) {
				LogUtil.e("Test", "Launch onpay :" + msg);
				try {
					JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						if (type.equals("0")) {
							((MainActivity) context).showToast(object.getString("msg"));
							((MainActivity) context).zhiyanFragment.artzhongchou.initData();
						} else if (type.equals("1")) {
							LogUtil.e("Test", "in 1");
							AliPay.getInstance().init((Activity) context).pay(object.getString("msg"));
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
							api.sendReq(req);
						} else {
							Intent intent = new Intent(context, WebActivity.class);
							intent.putExtra("url", object.getString("msg"));
							context.startActivity(intent);
						}
					} else {
						((MainActivity) context).showToast(object.getString("msg"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					ProgressDlgUtil.stopProgressDlg();
					e.printStackTrace();
				}

			}
		});
	}

}