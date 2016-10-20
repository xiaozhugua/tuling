package com.abct.tljr.ui.activity.zhiyan;

import java.util.ArrayList;

//import net.sourceforge.simcpux.PayActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.kline.FiveLineView;
import com.abct.tljr.kline.KLineGraphActivity;
import com.abct.tljr.kline.KView;
import com.abct.tljr.kline.LineView;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.kline.WeekKView;
import com.abct.tljr.kline.YueKView;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.widget.XScrollView;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.BwManager;
import com.qh.common.login.Configs;
import com.qh.common.login.util.ShareContent;
import com.qh.common.model.User;
import com.qh.common.pay.AliPay;
import com.qh.common.pay.PayCallBack;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import io.realm.Realm;

/**
 * Created by Administrator on 2016/2/16.
 */
public class LaunchReSearchActivity extends BaseActivity implements PayCallBack{

	private TextView txt_hq_now, txt_hq_change, txt_hq_jinkai, txt_hq_zuoshou, txt_hq_changes, txt_hq_shizhi,
			txt_hq_zuigao, txt_hq_zuidi, txt_hq_chengjiaoe, txt_hq_chengjiaojia, txt_hq_52zhougao, txt_hq_52zhoudi;
	private String market, code;
	private XScrollView mScrollView;

	private LineView lineView;
	private FiveLineView fiveLineView;
	private KView kView;
	private WeekKView weekKView;
	private YueKView yueKView;

	private TextView sure;
	private String key = "minute";
	private TabHost tabHost;
	private View[] radios;
	private ArrayList<String> tabs = new ArrayList<String>();
	private JSONObject marketInfo;
	private EditText edit, edit_title;
	ListView list;
	ArrayList<OneGu> arrayList;
	Realm realm;
	private IWXAPI api;
	private String msg = "";
	private String name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhiyan_launchresearch);
		findview();
		BwManager.getInstance().initShare(this);
	}

	public void findview() {
		// final IWXAPI msgApi =
		// WXAPIFactory.createWXAPI(LaunchReSearchActivity.this, null);
		// // 将该app注册到微信
		// msgApi.registerApp(Configs.WeiXinAppId);
		api = WXAPIFactory.createWXAPI(this, Configs.WeiXinAppId);

		realm = Realm.getDefaultInstance();
		market = getIntent().getStringExtra("market");
		code = getIntent().getStringExtra("code");
		OneGu oneGu = realm.where(OneGu.class).equalTo("code", code).equalTo("market", market).findFirst();
		((TextView) findViewById(R.id.title)).setText(oneGu.getName() + "(" + code + ")");
		name = oneGu.getName();
		findViewById(R.id.rl3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
			}
		});
		edit_title = (EditText) findViewById(R.id.edit);
		edit_title.addTextChangedListener(watcher);
		list = (ListView) findViewById(R.id.list);
		arrayList = new ArrayList<OneGu>();

		list.setAdapter(adapter);

		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		sure = (TextView) findViewById(R.id.btn_ensure_support);
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (User.getUser() != null) {
					if (((EditText) findViewById(R.id.et)).getText().toString().length() == 0) {
						showToast("请输入发起理由!");
						return;
					}
					msg = ((EditText) findViewById(R.id.et)).getText().toString();
					float money = Float.parseFloat(edit.getText().toString());
					startActivityForResult(
							new Intent(LaunchReSearchActivity.this, PayActivity.class).putExtra("money", money), 1);
				} else {
					login();
				}
			}
		});

		edit = (EditText) findViewById(R.id.et_support_num);
		edit.addTextChangedListener(watcher2);
		findViewById(R.id.btn_reduce).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int num = Integer.parseInt(edit.getText().toString());
				if (num > 10)
					num -= 10;
				edit.setText(num + "");
			}
		});
		findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int num = Integer.parseInt(edit.getText().toString());
				num += 10;
				edit.setText(num + "");
			}
		});

		mScrollView = (XScrollView) findViewById(R.id.tljr_scrollView1);
		// mScrollView.setOnTouchListener(this);
		// mScrollView.setOnSrcollViewListener(this);
		mScrollView.initWithContext(this);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(false);
		mScrollView.setAutoLoadEnable(false);
		mScrollView.setRefreshTime(Util.getNowTime());
		mScrollView.setIXScrollViewListener(new XScrollView.IXScrollViewListener() {
			@Override
			public void onRefresh() {
				reflushDP();
			}

			@Override
			public void onLoadMore() {
			}
		});
		tabHost = (TabHost) findViewById(R.id.tljr_tabhost);
		tabHost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gotoDetail();
			}
		});

		if (Constant.marketInfo == null) {
			Constant.getMarketInfo(new Complete() {
				@Override
				public void complete() {
					initMarket();
				}
			});
		} else {
			initMarket();
		}
	}

	private void initMarket() {
		post(runnable);
		LogUtil.e("Market====", market + "");
		marketInfo = Constant.marketInfo.get(market);
		initView();
		lineView.setArray(marketInfo);
		lineView.initData();
		// barView.getChart().setVisibility(
		// marketInfo.optBoolean("volumes") ? View.VISIBLE : View.GONE);

	}

	private void initView() {
		tabHost = (TabHost) findViewById(R.id.tljr_tabhost);
		tabHost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gotoDetail();
			}
		});
		JSONArray array = marketInfo.optJSONArray("lines");
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Integer> views = new ArrayList<Integer>();
		for (int i = 0; i < array.length(); i++) {
			keys.add(array.optString(i));
		}
		if (keys.contains("minutes")) {
			tabs.add("分时");
			views.add(R.id.tljr_hq_chart3);
		}
		if (keys.contains("5minutes")) {
			tabs.add("五日");
			views.add(R.id.tljr_hq_chart6);
		}
		if (keys.contains("days")) {
			tabs.add("日K");
			views.add(R.id.tljr_hq_chart4);
		}
		if (keys.contains("weeks")) {
			tabs.add("周K");
			views.add(R.id.tljr_hq_chart5);
		}
		if (keys.contains("months")) {
			tabs.add("月K");
			views.add(R.id.tljr_hq_chart1);
		}
		tabHost.setup();
		radios = new View[tabs.size()];
		for (int i = 0; i < tabs.size(); i++) {
			RadioButton view = (RadioButton) LayoutInflater.from(this).inflate(R.layout.tljr_item_tabtitle, null);
			view.setText(tabs.get(i));
			view.setTag(tabs.get(i));
			tabHost.addTab(tabHost.newTabSpec(tabs.get(i)).setIndicator(view).setContent(views.get(i)));
			radios[i] = view;
		}
		tabChangedArrow(findViewById(R.id.tljr_arrow1), 0);
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				for (int i = 0; i < radios.length; i++) {
					if (((String) radios[i].getTag()).equals(tabId)) {
						((RadioButton) radios[i]).setChecked(true);
						tabChangedArrow(findViewById(R.id.tljr_arrow1), i);
					} else {
						((RadioButton) radios[i]).setChecked(false);
					}
				}
				if (tabId.equals("分时")) {
					key = "minute";
					lineView.initData();
					lineView.getChart().setVisibility(View.VISIBLE);
					kView.getChart().setVisibility(View.GONE);
					yueKView.getChart().setVisibility(View.GONE);
					fiveLineView.getChart().setVisibility(View.GONE);
					weekKView.getChart().setVisibility(View.GONE);
				} else if (tabId.equals("五日")) {
					key = "fiveday";
					fiveLineView.initData();
					fiveLineView.getChart().setVisibility(View.VISIBLE);
					kView.getChart().setVisibility(View.GONE);
					weekKView.getChart().setVisibility(View.GONE);
					lineView.getChart().setVisibility(View.GONE);
					yueKView.getChart().setVisibility(View.GONE);
				} else if (tabId.equals("日K")) {
					key = "diary";
					kView.initData();
					kView.getChart().setVisibility(View.VISIBLE);
					lineView.getChart().setVisibility(View.GONE);
					yueKView.getChart().setVisibility(View.GONE);
					fiveLineView.getChart().setVisibility(View.GONE);
					weekKView.getChart().setVisibility(View.GONE);
				} else if (tabId.equals("周K")) {
					key = "week";
					weekKView.initData();
					weekKView.getChart().setVisibility(View.VISIBLE);
					yueKView.getChart().setVisibility(View.GONE);
					lineView.getChart().setVisibility(View.GONE);
					kView.getChart().setVisibility(View.GONE);
					fiveLineView.getChart().setVisibility(View.GONE);
				} else if (tabId.equals("月K")) {
					key = "mouth";
					yueKView.initData();
					yueKView.getChart().setVisibility(View.VISIBLE);
					lineView.getChart().setVisibility(View.GONE);
					kView.getChart().setVisibility(View.GONE);
					fiveLineView.getChart().setVisibility(View.GONE);
					weekKView.getChart().setVisibility(View.GONE);
				}
			}
		});
		// gukey = guKey;
		// txt_hq_info = (TextView) findViewById(R.id.tljr_txt_hq_info);
		// txt_hq_info.setText(isJJ ? ("基金" + code) : (market + code));
		txt_hq_now = (TextView) findViewById(R.id.tljr_txt_hq_now);
		txt_hq_change = (TextView) findViewById(R.id.tljr_txt_hq_change);
		// txt_hq_changep = (TextView) findViewById(R.id.tljr_txt_hq_changep);
		txt_hq_jinkai = (TextView) findViewById(R.id.tljr_txt_hq_jinkai);
		txt_hq_zuoshou = (TextView) findViewById(R.id.tljr_txt_hq_zuoshou);
		txt_hq_changes = (TextView) findViewById(R.id.tljr_txt_hq_changes);
		txt_hq_shizhi = (TextView) findViewById(R.id.tljr_txt_hq_shizhi);
		txt_hq_zuigao = (TextView) findViewById(R.id.tljr_txt_hq_zuigao);
		txt_hq_zuidi = (TextView) findViewById(R.id.tljr_txt_hq_zuidi);
		txt_hq_chengjiaoe = (TextView) findViewById(R.id.tljr_txt_hq_chengjiaoe);
		txt_hq_chengjiaojia = (TextView) findViewById(R.id.tljr_txt_hq_chengjiaojia);
		txt_hq_52zhougao = (TextView) findViewById(R.id.tljr_txt_hq_52zhougao);
		txt_hq_52zhoudi = (TextView) findViewById(R.id.tljr_txt_hq_52zhoudi);

		kView = new KView(handler, (CandleStickChart) findViewById(R.id.tljr_hq_chart1), market, code);
		kView.getChart().getAxisRight().setEnabled(false);
		kView.getChart().setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// gotoDetail();
				return false;
			}
		});
		lineView = new LineView(handler, (LineChart) findViewById(R.id.tljr_hq_chart3), market, code);
		lineView.getChart().getAxisRight().setEnabled(false);
		lineView.getChart().setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// gotoDetail();
				return false;
			}
		});
		fiveLineView = new FiveLineView(handler, (LineChart) findViewById(R.id.tljr_hq_chart6), market, code);
		fiveLineView.getChart().getAxisRight().setEnabled(false);
		fiveLineView.getChart().setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// gotoDetail();
				return false;
			}
		});
		yueKView = new YueKView(handler, (CandleStickChart) findViewById(R.id.tljr_hq_chart4), market, code);
		yueKView.getChart().getAxisRight().setEnabled(false);
		yueKView.getChart().setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// gotoDetail();
				return false;
			}
		});
		weekKView = new WeekKView(handler, (CandleStickChart) findViewById(R.id.tljr_hq_chart5), market, code);
		weekKView.getChart().getAxisRight().setEnabled(false);
		weekKView.getChart().setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// gotoDetail();
				return false;
			}
		});
	}

	public static String buyId;
	private JSONObject shareObject;

	private void showShare() {
		NetUtil.sendGet(UrlUtil.URL_ZR + "crowd/getUserShareId",
				"id=" + buyId + "&uid=" + User.getUser().getId() + "&token=" + Configs.token, new NetResult() {

					@Override
					public void result(String arg0) {
						try {
							JSONObject object = new JSONObject(arg0);
							if (object.getInt("status") == 1) {
								JSONObject obj = object.getJSONObject("msg");
								shareObject = obj;
								new PromptDialog(LaunchReSearchActivity.this, obj.optString("name") + "，是否立刻分享给好友？",
										new Complete() {

									@Override
									public void complete() {
										showPopupView();
									}
								}).show();
							} else {
								showToast(object.getString("msg"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	private PopupWindow popupWindow;

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void showPopupView() {
		if (popupWindow == null) {
			// 一个自定义的布局，作为显示的内容
			View contentView = LayoutInflater.from(this).inflate(R.layout.tljr_dialog_share_news, null);
			contentView.findViewById(R.id.btn_cancle).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
				}
			});
			LinearLayout v = (LinearLayout) contentView.findViewById(R.id.ly1);
			for (int i = 0; i < v.getChildCount(); i++) {
				final int m = i;
				v.getChildAt(i).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						share(m);
						popupWindow.dismiss();
					}
				});
			}
			popupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
			popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setOutsideTouchable(true);
			popupWindow.setAnimationStyle(R.style.AnimationPreview);
			popupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					setAlpha(1f);
				}
			});
		}

		setAlpha(0.8f);
		int[] location = new int[2];
		View v = findViewById(R.id.bar_bottom);
		v.getLocationOnScreen(location);
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]);
	}// type 0:QQ 1微信 2新浪微博 3朋友圈

	private void setAlpha(float f) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = f;
		lp.dimAmount = f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	private void share(int type) {
		ShareContent.actionUrl = shareObject.optString("shareUrl");
		ShareContent.content = shareObject.optString("desc");
		switch (type) {
		case 0:
			BwManager.getInstance().shareQQ();
			break;
		case 1:
			BwManager.getInstance().shareWeiXin();
			break;
		case 2:
			BwManager.getInstance().shareSina();
			break;
		case 3:
			BwManager.getInstance().shareWeiXinPyq();
			break;
		default:
			break;
		}
	}

	private void onpay(final String type, float quan, String couponId) {
		// LogUtil.e("Test","Launch onpay url :"+UrlUtil.URL_ZR +
		// "crowd/pay?uid=" + MyApplication.getInstance().self.getId()
		// + "&money=" + edit.getText().toString() + "&type=" + type
		// + "&market=" + market + "&code=" + code + "&token=" + Configs.token);
		int money = Integer.parseInt(edit.getText().toString());
		String parms = "uid=" + MyApplication.getInstance().self.getId() + "&money=" + money + "&type=" + type
				+ "&market=" + market + "&code=" + code + "&token=" + Configs.token + "&msg=" + msg;
		if (quan != 0)
			parms += ("&quan=" + quan);
		if (couponId != null)
			parms += ("&couponId=" + couponId);
		LogUtil.e("Test", UrlUtil.URL_ZR + "crowd/pay?" + parms);
		NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/pay", parms, new NetResult() {
			@Override
			public void result(final String msg) {
				LogUtil.e("Test", "Launch onpay :" + msg);
				post(new Runnable() {

					@Override
					public void run() {
						try {
							JSONObject object = new JSONObject(msg);
							buyId = object.optString("buyId");
							if (object.getInt("status") == 1) {
								if (type.equals("0")) {
									paysuccess(object.getString("msg"));
								} else if (type.equals("1")) {
									LogUtil.e("Test", "in 1");
									AliPay.getInstance().init(LaunchReSearchActivity.this).pay(object.getString("msg"));
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
									Toast.makeText(LaunchReSearchActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
									// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
									LogUtil.e("Test", "in 3");
									api.sendReq(req);
								} else {
									Intent intent = new Intent(LaunchReSearchActivity.this, WebActivity.class);
									intent.putExtra("url", object.getString("msg"));
									startActivity(intent);
								}
							} else {
								showToast(object.getString("msg"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							ProgressDlgUtil.stopProgressDlg();
							e.printStackTrace();
						}

					}
				});
			}
		});
	}

	private void reflushDP() {
		LogUtil.e("Test", "in reflushDP :" + "market=" + market + "&code=" + code);
		ProgressDlgUtil.showProgressDlg("", this);
		Util.getRealInfo("market=" + market + "&code=" + code, new NetResult() {
			@Override
			public void result(final String msg) {
				LogUtil.e("Test", "reflushDP msg:" + msg);
				ProgressDlgUtil.stopProgressDlg();
				post(new Runnable() {
					@Override
					public void run() {
						mScrollView.setRefreshTime(Util.getNowTime());
						mScrollView.stopRefresh();
						try {
							org.json.JSONObject object = new org.json.JSONObject(msg);
							if (object.getInt("code") == 1) {
								LogUtil.e("Test", "reflushDP msg 111");
								final org.json.JSONArray array = object.getJSONArray("result");
								LogUtil.e("Test", "reflushDP msg 222");
								if (array.optDouble(0) >= 100000000000l) {
									txt_hq_now.setText(
											"当前价：" + Util.df.format(array.optDouble(0) / 1000000000000l) + "万亿");
								} else if (array.optDouble(0) >= 10000000) {
									txt_hq_now.setText("当前价：" + Util.df.format(array.optDouble(0) / 100000000) + "亿");
								} else {
									txt_hq_now.setText(
											"当前价：" + (array.optDouble(0) == 0 ? "--" : (array.optDouble(0) + "")));
								}
								LogUtil.e("Test", "reflushDP msg 333");
								double change = array.optDouble(8);
								double changeP = array.optDouble(9);
								if (Math.abs(change) >= 100000000000l) {
									txt_hq_change
											.setText("涨跌幅：" + (changeP > 0 ? "+" : "")
													+ (Util.df.format(change / 1000000000000l) + "万亿" + " , "
															+ (changeP > 0 ? "+" : "") + Util.df.format(changeP)
															+ "%"));
								} else if (Math.abs(change) >= 10000000) {
									txt_hq_change.setText("涨跌幅：" + (changeP > 0 ? "+" : "")
											+ (Util.df.format(change / 100000000) + "亿") + " , "
											+ (changeP > 0 ? "+" : "") + Util.df.format(changeP) + "%");
								} else if (Math.abs(change) >= 1) {
									txt_hq_change.setText("涨跌幅：" + (changeP > 0 ? "+" : "") + change + " , "
											+ (changeP > 0 ? "+" : "") + Util.df.format(changeP) + "%");
								} else {
									txt_hq_change.setText("涨跌幅：" + (changeP > 0 ? "+" : "") + Util.getUsedNum(change, 2)+" , "
											+ " " + (changeP > 0 ? "+" : "") + Util.getUsedNum(changeP, 2) + "%");
								}
								setStyle(txt_hq_jinkai, array.optString(2), "今开", false);
								setStyle(txt_hq_zuoshou, array.optString(1).replace("null", "--"), "昨收", false);

								setStyle(txt_hq_zuidi, array.optString(7), "最低", true);
								setStyle(txt_hq_zuigao, array.optString(6), "最高", true);
								setStyle(txt_hq_52zhougao, array.optString(11).replace("null", "--"), "市盈率", true);
								setStyle(txt_hq_52zhoudi, array.optString(12).replace("null", "--"), "市净率", true);
								setStyle(txt_hq_changes, (array.optString(3).equals("null") ? "--"
										: Util.df.format(array.optDouble(3) / 10000)) + "万手", "成交量", true);
								setStyle(txt_hq_chengjiaoe, (array.optString(4).equals("null") ? "--"
										: Util.df.format(array.optDouble(4) / 10000)) + "亿", "成交额", true);
								setStyle(txt_hq_shizhi, (array.optString(13).equals("null") ? "--"
										: (Util.df.format(array.optDouble(13)) + "亿")), "总市值", true);
								setStyle(txt_hq_chengjiaojia, array.optString(14).equals("null") ? "--"
										: (Util.df.format(array.optDouble(14)) + "亿"), "流通市值", true);
								// txt_hq_info.setText(array.optString(16)
								// .replace("null", "")
								// + "("
								// + Util.getCurrentTime() + "更新)");

								if (array.optDouble(9) > 0) {
									findViewById(R.id.rl2)
											.setBackgroundColor(getResources().getColor(R.color.tljr_hq_zx_up));

								} else {
									findViewById(R.id.rl2)
											.setBackgroundColor(getResources().getColor(R.color.tljr_hq_zx_down));
								}
								// name2.setText(code);
							}
						} catch (org.json.JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	private void setStyle(TextView tv, String str, String pa, boolean shown) {
		SpannableStringBuilder style = new SpannableStringBuilder(
				pa + (shown ? "\n" : "  ") + str.replace("null", "0.0").replace("NaN", "0.0"));
		style.setSpan(new ForegroundColorSpan(ColorUtil.lightGray), 0, pa.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(style);
	}

	private void gotoDetail() {
		Intent intent = new Intent(LaunchReSearchActivity.this, KLineGraphActivity.class);
		// 用Bundle携带数据
		Bundle bundle = new Bundle();
		// 传递name参数为tinyphp
		bundle.putString("code", code);
		bundle.putString("name", name);
		bundle.putString("key", key);
		bundle.putString("market", market);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void tabChangedArrow(View v, int to) {
		if (v.getTag() == null)
			initImageView(v);
		int from = (Integer) v.getTag();
		int offset = (Util.WIDTH - Util.dp2px(this, 20)) / tabs.size();// 间距为屏幕宽减左右间隔除以5
		int offsetX = Util.dp2px(this, 10) + offset / 2 - Util.dp2px(this, 10) / 2;// 第0个居左x为：居左间隔+间距/5个-箭头宽度/2
		Animation animation = null;
		animation = new TranslateAnimation(from * offset + offsetX, to * offset + offsetX, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(200);
		v.startAnimation(animation);
		v.setTag(to);
	}

	private void initImageView(View cursor) {
		cursor.getLayoutParams().width = Util.dp2px(this, 10);
		cursor.setTag(1);
	}

	private BaseAdapter adapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(LaunchReSearchActivity.this, R.layout.fragment_zhiyan_searchlist, null);
			}
			((TextView) convertView.findViewById(R.id.name))
					.setText(arrayList.get(position).getName() + " " + arrayList.get(position).getCode());
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// if(){
					// artview.search(arrayList.get(position).getCode());
					// }
					// Intent i = new Intent(LaunchReSearchActivity.this,
					// LaunchReSearchActivity.class);
					// i.putExtra("market",
					// arrayList.get(position).getMarket());
					// i.putExtra("code", arrayList.get(position).getCode());
					// edit.setText(arrayList.get(position).getCode());
					market = arrayList.get(position).getMarket();
					code = arrayList.get(position).getCode();
					edit_title.setText(code);
					OneGu oneGu = realm.where(OneGu.class).equalTo("code", code).equalTo("market", market).findFirst();
					if (oneGu != null)
						((TextView) findViewById(R.id.title)).setText(oneGu.getName() + "(" + code + ")");
					reflushDP();
					arrayList.clear();
					adapter.notifyDataSetInvalidated();
					findViewById(R.id.rl3).setVisibility(View.GONE);
					lineView.setMarCo(market, code);
					fiveLineView.setMarCo(market, code);
					kView.setMarCo(market, code);
					weekKView.setMarCo(market, code);
					yueKView.setMarCo(market, code);
					LogUtil.e("Test", "i :" + tabHost.getCurrentTab());
					if (tabHost.getCurrentTab() == 0) {
						lineView.initData();
					} else if (tabHost.getCurrentTab() == 1) {
						fiveLineView.initData();
					} else if (tabHost.getCurrentTab() == 2) {
						kView.initData();
					} else if (tabHost.getCurrentTab() == 3) {
						weekKView.initData();
					} else if (tabHost.getCurrentTab() == 4) {
						yueKView.initData();
					}
					InputMethodManager imm = (InputMethodManager) getSystemService(
							LaunchReSearchActivity.this.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						// 如果开启
						imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
						// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
					}
				}
			});
			return convertView;
		}

	};

	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			findViewById(R.id.rl3).setVisibility(View.VISIBLE);
			// list.setVisibility(View.VISIBLE);
			if (!edit_title.getText().toString().equals("")) {
				// if(edit.getText().toString().length() == 6){
				// arrayList.clear();
				// arrayList.add(realm.where(OneGu.class).equalTo("code",edit.getText().toString()).findFirst());
				// }else{
				ArrayList<OneGu> temp = new ArrayList<OneGu>();
				temp.addAll(realm.where(OneGu.class).contains("code", edit_title.getText().toString()).findAll());
				arrayList.clear();
				if (temp.size() > 7) {
					arrayList.addAll(temp.subList(0, 7));
				} else {
					arrayList.addAll(temp);
				}
			}
			adapter.notifyDataSetInvalidated();
			// }
		}
	};

	private TextWatcher watcher2 = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			sure.setText("发起研究(" + edit.getText().toString() + ")");
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		run = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		run = false;
	}

	boolean run = true;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 要做的事情
			if (run) {
				postDelayed(this, 30000);
				reflushDP();
				// if (!key.equals("minute")) {
				// return;
				// }
				// lineView.ShiShi();
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OneGuActivity.dataday.clear();
		OneGuActivity.dataweek.clear();
		OneGuActivity.datayue.clear();
		OneGuActivity.fiveDatas.clear();
		realm.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == 1) {
			String type = data.getStringExtra("type");
			float quan = data.getFloatExtra("quan", 0);
			String couponId = data.getStringExtra("couponId");
			LogUtil.e("Test", "type :" + type);
			LogUtil.e("Test", "quan :" + quan);
			LogUtil.e("Test", "couponId :" + couponId);
			onpay(type, quan, couponId);
		}
	}

	@Override
	public void payerror(String arg0) {
		showToast(arg0);

	}

	@Override
	public void paysuccess(String arg0) {
		showToast(arg0);
		if(MyApplication.getInstance().getMainActivity().zhiyanFragment!=null&&
				MyApplication.getInstance().getMainActivity().zhiyanFragment.artzhongchou!=null){
			MyApplication.getInstance().getMainActivity().zhiyanFragment.artzhongchou.initData();
		}
		showShare();
	}

}
