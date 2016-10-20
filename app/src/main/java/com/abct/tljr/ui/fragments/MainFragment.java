package com.abct.tljr.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.hangqing.jingwu.MainJingWuView;
import com.abct.tljr.kline.gegu.inter.MyScrollViewListener;
import com.abct.tljr.kline.gegu.view.MainTulingDataView;
import com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.Options;
import com.abct.tljr.service.LoginResultReceiver;
import com.abct.tljr.ui.fragments.zhiyanFragment.zhiyanFragment;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.abct.tljr.utils.ViewUtil;
import com.abct.tljr.wxapi.PersonalActivity;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class MainFragment extends BaseFragment implements View.OnClickListener {
	private DecimalFormat df = new DecimalFormat("0.00");
	private LinearLayout mLinearLayout = null;
	private MainJingWuView jingwu;
	private String msg1 = null;
	private String msg2 = null;
	private SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd");
	private MainFragment mainFragment;

	private MainTulingDataView mTulingDataView = null;// 图灵数据九宫格部分
	private com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView scrollView;
	private MainActivity mainActivity;

	private RelativeLayout kaihujiaoyi=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainFragment = this;
		ViewUtil.init();
		baseView = View.inflate(activity, R.layout.fragment_main, null);
		mainActivity = (MainActivity) getActivity();
		init();
	}

	public void init() {
		if (mLinearLayout == null) {

			kaihujiaoyi=(RelativeLayout)findViewById(R.id.kaihujiaoyi);
			kaihujiaoyi.setOnClickListener(this);
			
			findViewById(R.id.login).setOnClickListener(this);
			// findViewById(R.id.zongzhixun).setOnClickListener(this);
			findViewById(R.id.avatar).setOnClickListener(this);

			findViewById(R.id.simu).setOnClickListener(this);
			findViewById(R.id.licai).setOnClickListener(this);
			findViewById(R.id.setting).setOnClickListener(this);

			mLinearLayout = (LinearLayout) baseView.findViewById(R.id.fragment_main_linear);

			jingwu = new MainJingWuView(getActivity());

			mLinearLayout.addView(jingwu.getView());
			getZongData(2);

			// 添加图灵数据的九宫格部分
			mTulingDataView = new MainTulingDataView((MainActivity) getActivity());
			mLinearLayout.addView(mTulingDataView.getView());

			/*
			 * com.abct.tljr.ui.MySuperScrollView mScrollView =
			 * (com.abct.tljr.ui.MySuperScrollView) baseView
			 * .findViewById(R.id.fragment_main_super_sv);
			 */
			/*
			 * mScrollView.setOnScrollToBottomLintener(new
			 * OnScrollToBottomListener() {
			 * 
			 * @Override public void onScrollBottomListener(boolean isBottom) {
			 * // TODO Auto-generated method stub // 加载更多的数据
			 * 
			 * //mTulingDataView.refreshScrollView(); } });
			 */

			// mainDownWidget = new MainDownWidget(mainFragment,
			// (RelativeLayout) baseView);
			//
			// findViewById(R.id.arrow).setOnClickListener(this);

			scrollView = (com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView) baseView
					.findViewById(R.id.fragment_main_super_sv);// scrollview
			mainActivity.setMainFragmentScrollView(scrollView);
			scrollView.setScrollViewListener(new MyScrollViewListener() {

				@Override
				public void onScrollChanged(OneguGuZhiScrollView scrollView, int x, int y, int oldx, int oldy) {
					// TODO Auto-generated method stub

					LogUtil.e("取回焦点", "x==" + x + "    y==" + y);
					//mainActivity.currentX = x;
					mainActivity.currentY = y;

				}
			});

			initUser();
		}
	}

	public Handler handler = new Handler();

	public void initAvatar() {
		if (User.getUser() != null)
			ImageLoader.getInstance().displayImage(User.getUser().getAvatarUrl(), (ImageView) findViewById(R.id.imm),
					Options.getCircleListOptions());
	}

	public void initUser() {
		if (mLinearLayout == null)
			return;
		if (User.getUser() == null) {
			// 没有登录
			findViewById(R.id.money).setVisibility(View.INVISIBLE);

			findViewById(R.id.login).setVisibility(View.VISIBLE);
			findViewById(R.id.avatar).setVisibility(View.GONE);
			findViewById(R.id.info).setVisibility(View.GONE);
		} else {
			// 已经登录
			getUserCash();
			findViewById(R.id.money).setVisibility(View.VISIBLE);

			findViewById(R.id.login).setVisibility(View.GONE);
			findViewById(R.id.avatar).setVisibility(View.VISIBLE);
			findViewById(R.id.info).setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(User.getUser().getAvatarUrl(), (ImageView) findViewById(R.id.imm),
					Options.getCircleListOptions());
			((TextView) findViewById(R.id.tv_name)).setText(User.getUser().getNickName());
			((TextView) findViewById(R.id.id)).setText(User.getUser().getId());
			LoginResultReceiver.getUserInfo(new Complete() {
				@Override
				public void complete() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							((TextView) findViewById(R.id.jifen_number)).setText(User.getUser().getIntegral() + "");
							((TextView) findViewById(R.id.tv_numebr)).setText(User.getUser().getLevel() + "");
							((TextView) findViewById(R.id.time)).setText(User.getUser().getLevelTotal() + "小时");
							double pro = (User.getUser().getLevelNeedTotal() - User.getUser().getLevelNeed())
									/ User.getUser().getLevelNeedTotal() * 100;
							if (!Double.isNaN(pro)) {
								((TextView) findViewById(R.id.jingyan)).setText(df.format(pro) + "%");
								((ProgressBar) findViewById(R.id.seekbar)).setProgress((int) pro);
							}
							// 智研模块与服务器互通

							zhiyanFragment.Loginzhiyan();
							if (User.getUser().isValid()) {
								((TextView) findViewById(R.id.tv_person)).setText("已认证");
								((ImageView) findViewById(R.id.im_person))
										.setImageResource(R.drawable.img_shenfenrenzheng1);
							} else {
								((TextView) findViewById(R.id.tv_person))
										.setText(User.getUser().getValidStatus() == 0 ? "未认证" : "认证中");
								((ImageView) findViewById(R.id.im_person))
										.setImageResource(R.drawable.img_shenfenrenzheng2);
							}
							if (User.getUser().isValidDv()) {
								((TextView) findViewById(R.id.tv_dv)).setText("已认证");
								((ImageView) findViewById(R.id.im_dv)).setImageResource(R.drawable.img_dav1);
							} else {
								((TextView) findViewById(R.id.tv_dv))
										.setText(User.getUser().getValidStatus() == 0 ? "未认证" : "认证中");
								((ImageView) findViewById(R.id.im_dv)).setImageResource(R.drawable.img_dav2);
							}
						}
					});
				}
			});

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			startActivity(new Intent(activity, WXEntryActivity.class));
			break;
		case R.id.avatar:
		case R.id.setting:
			if (User.getUser() == null) {
				Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
				return;
			}
			startActivity(new Intent(activity, PersonalActivity.class));
			break;

		case R.id.simu:
			if (User.getUser() == null) {
				Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
				return;
			}
			Util.startApp(activity, "YGSM.apk");
			break;
		case R.id.licai:

			if (User.getUser() == null) {
				Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
				return;
			}
			Util.startApp(activity, "Occft.apk");
			break;
		case R.id.kaihujiaoyi:
			Util.start360App(activity,"TuringFinance.apk");
			break;
		// case R.id.zongzhixun:
		// if (User.getUser() == null) {
		// Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
		// return;
		// }
		// Intent intent = new Intent(activity, ChartActivity.class);
		// RealTimeView.redpointisshow = false;
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// activity.startActivity(intent);
		// break;

		}
	}

	private int freshTime = Constant.FlushTime;

	public void oneSecAction() {
		freshTime++;
		if (freshTime < (Constant.netType.equals("WIFI") ? 5
				: ((Constant.FlushTime == 0 ? 9999 : Constant.FlushTime)))) {
			return;
		}
		freshTime = 0;
		if (jingwu != null)
			jingwu.flush();
	}


	@Override
	public void onStart() {
		//LogUtil.e("取回焦点",   "   currentY==" + mainActivity.currentY);

		super.onStart();
		LogUtil.e("mwj","onStart");
	}

	/**
	 * 获取焦点的时候*/
	@Override
	public void onResume() {
		//scrollView.smoothScrollTo(0,mainActivity.currentY2);
		super.onResume();
		LoginResultReceiver.updateOnlineEvent(new Complete() {
			@Override
			public void complete() {
				ProgressDlgUtil.stopProgressDlg();
				if (User.getUser() != null && User.getUser().getIntegral() > 0) {
					((TextView) findViewById(R.id.jifen_number)).setText(User.getUser().getIntegral() + "");
				}
			}
		});

		LogUtil.e("mwj","onResume");
		scrollView.setEnabled(true);
	}





	/**
	 * 失去焦点的时候调用
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		LogUtil.e("mwj","onPause");

		super.onPause();
	
	}
	/**
	 * 看不见得时候调用*/
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mainActivity.currentY2 = mainActivity.currentY;
		LogUtil.e("取回焦点", "mainActivity.currentY2=="+mainActivity.currentY2);
		LogUtil.e("mwj","onStop");
		scrollView.setEnabled(false);
		
	}

	public void getZongData(int i) {
		String param = "size=" + i;
		NetUtil.sendGet(UrlUtil.Url_chat_getchatlist, param, new NetResult() {
			@Override
			public void result(String msg) {
				try {
					JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						JSONArray array = object.getJSONArray("msg");
						if (array.length() == 1) {
							msg1 = array.getJSONObject(0).getString("msg");
							// msg2=array.getJSONObject(1).getString("msg");
						} else if (array.length() == 2) {
							msg1 = array.getJSONObject(0).getString("msg");
							msg2 = array.getJSONObject(1).getString("msg");
						}
						Message message = Message.obtain();
						message.what = 1;
						mHandler.sendMessage(message);
					} else {

					}
				} catch (Exception e) {

				}
			}
		});
	}

	public void getUserCash() {
		LogUtil.e("getUserCash", "method=getUserInfo&uid=" + User.getUser().getId() + "&token=" + Configs.token);
		HttpRequest.sendPost(UrlUtil.Url_235 + "8080/finance/UserServlet",
				"method=getUserInfo&uid=" + User.getUser().getId() + "&token=" + Configs.token, new HttpRevMsg() {

					@Override
					public void revMsg(String msg) {
						LogUtil.e("getUserCash", msg);
						if (msg.equals("") && User.getUser() != null) {
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									getUserCash();
								}
							}, 2000);
							return;
						}
						try {
							JSONObject jsonObject = new JSONObject(msg);
							if (jsonObject.getInt("status") == 1) {
								JSONObject object = jsonObject.getJSONObject("msg");
								User.getUser().setAllEarnings((float) object.getDouble("allEarnings"));
								User.getUser().setEarningsYesterday((float) object.getDouble("earningsYesterday"));
								User.getUser().setInvestCount(object.getInt("investCount"));
								User.getUser().setTotalAssets((float) object.getDouble("totalAssets"));
								User.getUser().setTotalInvest((float) object.getDouble("totalInvest"));
								// {"msg":{"allEarnings":86,"createDate":1445774262045,"earningsYesterday":29,"investCount":4,"totalAssets":121686,"totalInvest":121600,"uid":"10017"},"status":1}
								handler.post(new Runnable() {

									@Override
									public void run() {

										((TextView) findViewById(R.id.money)).setText(
												"(¥" + Util.df.format(User.getUser().getTotalAssets() / 100) + ")");
										// Investmoney.setText(Util.df.format(User.getUser().getTotalAssets()
										// / 100));
										// zuori.setText(Util.df.format(User.getUser().getEarningsYesterday()
										// / 100));
										// leiji.setText(Util.df.format(User.getUser().getAllEarnings()
										// / 100));
										// ((TextView)
										// view.findViewById(R.id.yitoujinqian))
										// .setText(User.getUser().getTotalInvest()
										// / 100 + "元");
									}
								});
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	final Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// case 1:
			// zongtext1.setText(" 1." + msg1);
			// break;
			}
		};
	};

}
