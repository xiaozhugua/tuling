package com.abct.tljr.ui.fragments.zhiyanFragment.adapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.ImageOptions;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.shouye.FerdlsActivity;
import com.abct.tljr.shouye.SpeakActiviy;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.activity.zhiyan.PayActivity;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanFinishHeader;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanFinishItemLineChart;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanHeaderItemLineChart;
import com.abct.tljr.ui.widget.DefaultIconDrawable;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.pay.AliPay;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import io.realm.Realm;

/**
 * Created by Administrator on 2016/1/31.
 */

public class ZhiyanFinishiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context context;
	private ArrayList<ZhongchouBean> list;
	public ZhongchouBean paybean;
	public int item = 0;
	NumberFormat numFormat = NumberFormat.getNumberInstance();
	int[] colors = { ColorUtil.red, ColorUtil.green, ColorUtil.blue };
	private String type = "";
	private ZhiYanFinishHeader header = null;
	private int paybeanPosition=-1;
	
	public ZhiyanFinishiAdapter(Context context, ArrayList<ZhongchouBean> list,int item, String type) {
		this.context = context;
		this.list = list;
		this.item = item;
		this.type = type;
	}

	public void setZhiYanFinishHeader(ZhiYanFinishHeader header) {
		this.header = header;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		if (viewType == 1) {
			return new finishview_info(LayoutInflater.from(context).inflate(
					R.layout.fragment_zhiyan_finish_info, parent, false));
		} else {
			return new ViewHolder(LayoutInflater.from(context).inflate(
					R.layout.fragment_zhiyan_finishadpter, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewholder,int position) {
		final ZhongchouBean bean = list.get(position);
		final int itemposition=position;
		if (viewholder instanceof ViewHolder) {
			final ViewHolder holder = (ViewHolder) viewholder;
			final int tempposition = position;
			Realm realm = Realm.getDefaultInstance();
			OneGu gu = realm.where(OneGu.class).equalTo("code", bean.getCode())
					.equalTo("market", bean.getMarket()).findFirst();
			if (gu != null) {
				holder.name.setText(gu.getName());
			} else {
				holder.name.setText("");
			}
			realm.close();

			holder.code.setText(bean.getCode());
			holder.type.setText(bean.getType());
			holder.focus.setText(" " + (bean.isIsfocus() ? "已" : "") + "关注("
					+ bean.getFocus() + ")");
			holder.remark.setText(" 点评(" + bean.getRemark() + ")");
			holder.haslook.setVisibility(bean.isLook() ? View.VISIBLE
					: View.GONE);
			holder.name.setTextColor(bean.isLook() ? ColorUtil.lightGray
					: ColorUtil.gray);

			numFormat.setMaximumFractionDigits(1);
			holder.flower.setText(numFormat.format(bean.getCustomerRating())+ "");
			holder.star.setText(bean.getPrivateStar() + "");

			holder.bar_star.setProgress((int) (100 * ((double) bean.getPrivateStar() / 5)));
			holder.bar_flower.setProgress((int) (100 * ((double) bean.getCustomerRating() / 5)));

			String[] s = bean.getSection().split("-");
			for (int i = 0; i < holder.tvs.length; i++) {
				if (i < s.length && !s[i].equals("")) {
					holder.tvs[i].setVisibility(View.VISIBLE);
					holder.tvs[i].setText(s[i]);
				} else {
					holder.tvs[i].setVisibility(View.GONE);
				}
			}

			holder.rl.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pay(bean,itemposition);
				}
			});

			holder.rl_xiangqing.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pay(bean,itemposition);
				}
			});

			holder.context.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pay(bean,itemposition);
				}
			});

			holder.focus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (User.getUser() == null) {
						Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT)
								.show();
					} else {
						focus(tempposition, (TextView) v, bean);
					}
				}
			});

			holder.remark.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					if (User.getUser() == null) {
						Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
					} else {
						Intent intent=new Intent(context,SpeakActiviy.class);
						intent.putExtra("id", bean.getId());
						intent.putExtra("action",1);
						intent.putExtra("item",itemposition);
						intent.putExtra("leftposition",item);
						context.startActivity(intent);
					}
				}
			});

			holder.personname.setText(bean.getUserCrowdList().get(0).getUser().getUnickname());
			holder.lv_person.setText("Lv："+ bean.getUserCrowdList().get(0).getUser().getUlevel());

			if (holder.im.getTag() == null|| !holder.im.getTag().equals(bean.getId())) {
				holder.im.setTag(bean.getId());
				if (bean.getIconurl().equals("http://www.baidu.com")) {
					holder.im.setImageDrawable(new DefaultIconDrawable(
							holder.im.getLayoutParams().height, holder.name.getText().toString(), true));
				} else {
					ImageLoader.getInstance().displayImage(bean.getIconurl(),
							holder.im, Options.getListOptions());
				}
			}

			if (holder.im22.getTag() == null
					|| !holder.im22.getTag().equals(bean.getId())) {
				holder.im22.setTag(bean.getId());
				ImageLoader.getInstance().displayImage(
						bean.getUserCrowdList().get(0).getUser().getUavata(),
						holder.im22, ImageOptions.getCircleListOptions());
			}

			if (bean.isFinishstatus() == true) {
				holder.context.setVisibility(View.VISIBLE);
				if (bean.getzLineChart() != null) {
					bean.getzLineChart().UpdateView(holder.context);
				}
			} else {
				holder.context.setVisibility(View.GONE);
			}

			holder.jiantou.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (holder.context.getVisibility() == View.GONE) {
						holder.context.setVisibility(View.VISIBLE);
						OpenIcon(holder.jiantou);
						bean.setFinishstatus(true);
						bean.setzLineChart(new ZhiYanFinishItemLineChart(context,
								holder.context, type, bean.getCode(),bean.getMarket()));
					} else {
						CloseIcon(holder.jiantou);
						bean.setFinishstatus(false);
						holder.context.setVisibility(View.GONE);
					}
				}
			});
		} else {
			// 加载第一项的时候
			final finishview_info infoViewHolder = (finishview_info) viewholder;
			new ZhiYanHeaderItemLineChart(context, infoViewHolder, header);
			if (bean.isFinishstatus() == true) {
				infoViewHolder.finishitem_content.setVisibility(View.GONE);
			} else {
				infoViewHolder.finishitem_content.setVisibility(View.VISIBLE);
			}
			infoViewHolder.menu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (infoViewHolder.finishitem_content.getVisibility() == View.GONE) {
						infoViewHolder.finishitem_content.setVisibility(View.VISIBLE);
						OpenIcon(infoViewHolder.menu);
						bean.setFinishstatus(false);
					} else {
						infoViewHolder.finishitem_content.setVisibility(View.GONE);
						CloseIcon(infoViewHolder.menu);
						bean.setFinishstatus(true);
					}
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	@Override
	public int getItemViewType(int position) {
		return list.get(position).getFirst();
	}

	public final class ViewHolder extends RecyclerView.ViewHolder {
		TextView name;
		TextView code;
		TextView type;
		TextView personname;
		TextView lv_person;
		ImageView im;
		ImageView im22;
		RelativeLayout rl_xiangqing;
		TextView focus;
		TextView remark;
		View rl;
		TextView[] tvs = new TextView[3];
		View haslook;

		ProgressBar bar_star;
		ProgressBar bar_flower;
		TextView star;
		TextView flower;

		ImageView jiantou = null;
		View context = null;
		LineChart mLineChart = null;

		ZhiYanFinishItemLineChart zhiyanlinechart = null;

		public ViewHolder(View convertView) {
			super(convertView);
			name = (TextView) convertView.findViewById(R.id.name);
			code = (TextView) convertView.findViewById(R.id.code);
			type = (TextView) convertView.findViewById(R.id.level);
			flower = (TextView) convertView.findViewById(R.id.flower);
			star = (TextView) convertView.findViewById(R.id.star);
			bar_star = (ProgressBar) convertView
					.findViewById(R.id.progress_star);
			bar_flower = (ProgressBar) convertView
					.findViewById(R.id.progress_flower);
			personname = (TextView) convertView.findViewById(R.id.personname);
			lv_person = (TextView) convertView.findViewById(R.id.lv_person);
			im = (ImageView) convertView.findViewById(R.id.im);
			im22 = (ImageView) convertView.findViewById(R.id.im22);
			focus = (TextView) convertView.findViewById(R.id.tv_guanzhu);
			remark = (TextView) convertView.findViewById(R.id.tv_pinglun);
			rl_xiangqing = (RelativeLayout) convertView
					.findViewById(R.id.rl_xiangqing);
			mLineChart = (LineChart) convertView
					.findViewById(R.id.zhiyan_finish_item_linechart);
			rl = convertView.findViewById(R.id.rl);
			haslook = convertView.findViewById(R.id.haslook);
			tvs[0] = (TextView) itemView.findViewById(R.id.tv1);
			tvs[1] = (TextView) itemView.findViewById(R.id.tv2);
			tvs[2] = (TextView) itemView.findViewById(R.id.tv3);

			jiantou = (ImageView) itemView
					.findViewById(R.id.finish_itemview_jiantou);
			context = (View) itemView
					.findViewById(R.id.zhiyan_finishitem_content);
			((GradientDrawable) type.getBackground()).setStroke(2, colors[0]);
			for (int i = 0; i < tvs.length; i++) {
				tvs[i].setTextColor(colors[i]);
				GradientDrawable gd = (GradientDrawable) tvs[i].getBackground();
				gd.setStroke(2, colors[i]);
			}

		}

	}

	public final class finishview_info extends RecyclerView.ViewHolder {
		public TextView sousouitme1;
		public TextView sousouitme2;
		public TextView sousouitme3;
		public TextView meitiitem1;
		public TextView meitiitem2;
		public TextView meitiitem3;
		public TextView qingxuitem1;
		public TextView qingxuitem2;
		public TextView qingxuitem3;
		public LineChart mLineChart;
		public ImageView menu;
		public RelativeLayout finishitem_content;
		public TextView finishName;

		public finishview_info(View itemView) {
			super(itemView);
			sousouitme1 = (TextView) itemView.findViewById(R.id.sousuodiwu);
			sousouitme2 = (TextView) itemView.findViewById(R.id.sousuodiwu2);
			sousouitme3 = (TextView) itemView.findViewById(R.id.sousuodiwu3);
			meitiitem1 = (TextView) itemView.findViewById(R.id.meitidiwu);
			meitiitem2 = (TextView) itemView.findViewById(R.id.meitidiwu2);
			meitiitem3 = (TextView) itemView.findViewById(R.id.meitidiwu3);
			qingxuitem1 = (TextView) itemView.findViewById(R.id.qingxudiwu);
			qingxuitem2 = (TextView) itemView.findViewById(R.id.qingxudiwu2);
			qingxuitem3 = (TextView) itemView.findViewById(R.id.qingxudiwu3);
			mLineChart = (LineChart) itemView
					.findViewById(R.id.zhiyan_finish_item_linechart);
			menu = (ImageView) itemView.findViewById(R.id.finish_action_image);
			finishitem_content = (RelativeLayout) itemView
					.findViewById(R.id.finishitem_content);
			finishName = (TextView) itemView
					.findViewById(R.id.fragment_zhiyan_finish_name);
		}

	}

	public void pay(final ZhongchouBean bean,final int... position) {
		if (User.getUser() == null) {
			Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!bean.isLook()) {
			new PromptDialog(context, "首次阅读需要支付"
					+ (float) bean.getRewardMoney() / 100 + "元,是否支付？",
					new Complete() {
						@Override
						public void complete() {
							if(position.length>0){
								paybeanPosition=position[0];
							}
							paybean = new ZhongchouBean();
							paybean.setRewardMoney(bean.getRewardMoney());
							paybean.setMarket(bean.getMarket());
							paybean.setCode(bean.getCode());
							paybean.setId(bean.getId());
							((Activity) context).startActivityForResult(new Intent(context, PayActivity.class)
										.putExtra("money",(float)bean.getRewardMoney() / 100)
										.putExtra("canuse",false)
										.putExtra("item",item),2);
						}
					}).show();
			return;
		}
		if(position.length>0){
			more(bean,position[0]);
		}else{
			more(bean);
		}
		
	}

	public void paySuccess(String msg) {
		((MainActivity) context).showToast(msg);
		((MainActivity) context).zhiyanFragment.artzhongchou.initData();
		((MainActivity) context).zhiyanFragment.artzhongchou.UpdateMyView();
		if(paybeanPosition==-1){
			more(paybean);
		}else{
			more(list.get(paybeanPosition),paybeanPosition);
		}
	}

	private void more(final ZhongchouBean bean,final int... count) {
		LogUtil.e("bean", bean.getMarket());
		NetUtil.sendPost(UrlUtil.Url_235 + "8080/StockDataService/ferdls",
				"oper=1&code=" + bean.getCode() + "&market=" + bean.getMarket()
						+ "&crowdId=" + bean.getId() + "&uid="+ User.getUser().getId(), new NetResult() {
					@Override
					public void result(String arg0) {
						try {
							JSONObject jsonObject = new JSONObject(arg0);
							if (jsonObject.getInt("status") == 1) {
								for (int i = 0; i < list.size(); i++) {
									if (list.get(i).getCode()
											.equals(bean.getCode())) {
										list.get(i).setLook(true);
									}
								}
								notifyDataSetChanged();
								if (MyApplication.getInstance()
										.getMainActivity().zhiyanFragment.mMyView.listBean != null) {
									ArrayList<ZhongchouBean> list1 = MyApplication
											.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean;
									if (list1 != null) {
										for (int i = 0; i < list1.size(); i++) {
											if (list1.get(i).getCode().equals(bean.getCode())) {
												list1.get(i).setLook(true);
											}
										}
									}
								}
								if (MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter != null) {
									MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
								}
								Intent intent = new Intent(context,FerdlsActivity.class);
								intent.putExtra("info",jsonObject.getString("result"));
								intent.putExtra("id", bean.getId());
								intent.putExtra("item", item);
								if(count.length>0 ){
									intent.putExtra("position",count[0]);
								}
								context.startActivity(intent);
							} else {
								Toast.makeText(context, "获取数据失败！",Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							Toast.makeText(context, "获取数据失败！",
									Toast.LENGTH_SHORT).show();
						}
						paybean = null;
					}
				});
	}

	public void onpay(final String type, float quan, String couponId) {
		float money = (float) paybean.getRewardMoney() / 100;
		LogUtil.e("money1", paybean.getRewardMoney() + "");
		LogUtil.e("money", money + "");
		String parms = "id=" + paybean.getId() + "&uid="
				+ MyApplication.getInstance().self.getId() + "&money=" + money
				+ "&type=" + type + "&token=" + Configs.token;
		parms += ("&quan=" + quan);
		if (couponId != null)
			parms += ("&couponId=" + couponId);
		parms += ("&payType=2");
		NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/reward", parms,
				new NetResult() {
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
									AliPay.getInstance()
											.init((Activity) context)
											.pay(object.getString("msg"));
								} else if (type.equals("3")) {
									LogUtil.e("Test", "in 3");
									PayReq req = new PayReq();
									// req.appId = "wxf8b4f85f3a794e77";
									// // 测试用appId
									JSONObject json = object
											.getJSONObject("msg");
									req.appId = json.getString("appid");
									req.partnerId = json.getString("partnerid");
									req.prepayId = json.getString("prepayid");
									req.nonceStr = json.getString("noncestr");
									req.timeStamp = json.getString("timestamp");
									req.packageValue = json
											.getString("package");
									req.sign = json.getString("sign");
									req.extData = "app data"; // optional
									// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
									LogUtil.e("Test", "in 3");
									MyApplication.getInstance()
											.getMainActivity().api.sendReq(req);
								} else {
									Intent intent = new Intent(context,WebActivity.class);
									intent.putExtra("url",object.getString("msg"));
									context.startActivity(intent);
								}
							} else {
								((MainActivity) context).showToast(object
										.getString("msg"));
							}
						} catch (JSONException e) {
							ProgressDlgUtil.stopProgressDlg();
							e.printStackTrace();
						}

					}
				});
	}

	public void OpenIcon(ImageView mImageView) {
		ObjectAnimator mValueAnimator = ObjectAnimator.ofFloat(mImageView,"rotation",90f,0f);
		mValueAnimator.setDuration(500);
		mValueAnimator.start();
	}

	public void CloseIcon(ImageView mImageView) {
		ObjectAnimator mValueAnimator = ObjectAnimator.ofFloat(mImageView,"rotation",0f,90f);
		mValueAnimator.setDuration(500);
		mValueAnimator.start();
	}

	private void focus(final int positon,final TextView v,final ZhongchouBean bean) {
		String a = v.getText().toString();
		int type = a.contains("已") ? 0 : 1;
		ProgressDlgUtil.showProgressDlg("", (Activity) context);
		NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/focus", "id=" + bean.getId()
				+ "&uid=" + User.getUser().getId() + "&token=" + Configs.token
				+ "&type=" + type, new NetResult() {
			@Override
			public void result(String msg) {
				LogUtil.e("focus", msg);
				ProgressDlgUtil.stopProgressDlg();
				try {
					JSONObject jsonObject = new JSONObject(msg);
					if (jsonObject.getInt("status") == 1) {
						Toast.makeText(context, jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
						String a = v.getText().toString();
						int type = a.contains("已") ? 0 : 1;
						if (type == 0) {
							bean.setFocus(bean.getFocus() - 1);
							bean.setIsfocus(false);
							v.setText(" "+"关注("+bean.getFocus()+")");
							List<ZhongchouBean> listbean = MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean;
							String key = list.get(positon).getMarket()+ list.get(positon).getCode();
							for (int i = 0; i < listbean.size(); i++) {
								if (key.equals(listbean.get(i).getMarket()+ listbean.get(i).getCode())) {
									if (i == 0) {
										listbean.get(1).setFinishstatus(true);
									}
									listbean.remove(i);
									break;
								}
							}
							
							MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
							MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum = MyApplication
									.getInstance().getMainActivity().zhiyanFragment.myNum - 1;
							MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton
									.setText("我的("+ (MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)+ ")");
						} else {
							bean.setFocus(bean.getFocus() + 1);
							bean.setIsfocus(true);
							
							v.setText(" "+"已关注("+bean.getFocus()+")");
							list.get(positon).setFinishstatus(true);
							for (int i = 0; i < MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.size(); i++) {
								if (MyApplication.getInstance()
										.getMainActivity().zhiyanFragment.mMyView.listBean
										.get(i).getStatus() == 1) {
									MyApplication.getInstance()
											.getMainActivity().zhiyanFragment.mMyView.listBean
											.get(i).setFinishstatus(false);
								}
							}
							list.get(positon).setFinishstatus(true);
							MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.add(0, list.get(positon));
							MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
							MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.refreshAdapterView();
							MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum = MyApplication
									.getInstance().getMainActivity().zhiyanFragment.myNum + 1;
							MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton
									.setText("我的("+ (MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)+ ")");
						}
					} else {
						Toast.makeText(context, "服务器连接失败！", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(context, "服务器连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
