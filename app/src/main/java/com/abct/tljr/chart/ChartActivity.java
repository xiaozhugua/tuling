package com.abct.tljr.chart;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ChartActivity extends BaseActivity {

	ViewPager viewpager;
	ChartChangeBar Chartchangebar;
	RealTimeView realtimeview;
	WeSeeView weseeview;
	FutureAskView futureaskview;
	private TextView txt_islogin, hu, hupercent, huchange, sz, 
					szpercent, szchange, zhong, zhongpercent, zhongchange,
					chuang,chuangpercent,chuangchange;
	private String TAG = "ChartActivity";
	private ArrayList<View> list = new ArrayList<View>();
	public static String YunZhiToken = "";
	public static boolean islive = false;
	private HashMap<String, OneGu> map;
	public boolean iskaishi = true;
	public View hushenview;
	public View zhongchuanview;
	private ViewFlipper zx_header_viewFlipper;
	private ReceiveBroadCast receiveBroadCast; // 广播实例
	private TextView bottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_chart);
		if (User.getUser() == null) {
			showToast("请先登录!");
			finish();
			return;
		}
		islive = true;
		// getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.tljr_bj_tp));
		overridePendingTransition(R.anim.vp_top_in_activity, R.anim.vp_top_out_activity);
		// this.getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
		// |WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		// | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
		// | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		findView();
		// android:windowSoftInputMode="stateVisible|adjustResize"

		// 注册广播接收
		receiveBroadCast = new ReceiveBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("Tip_ChartActivity"); // 只有持有相同的action的接受者才能接收此广播
		getApplicationContext().registerReceiver(receiveBroadCast, filter);
	}

	@SuppressLint("WrongViewCast")
	private void findView() {
		map = new HashMap<String, OneGu>();
		bottom = (TextView) findViewById(R.id.bottom);
		findViewById(R.id.tljr_img_futures_join_back).setOnClickListener(onclick);
		inheader();
		viewpager = (ViewPager) findViewById(R.id.tljr_vp);
		realtimeview = new RealTimeView(this, handler);
		weseeview = new WeSeeView(this);
		futureaskview = new FutureAskView(this);
		list.add(realtimeview.getView());
		list.add(futureaskview.getView());
		list.add(weseeview.getView());

		viewpager.setAdapter(pagerAdapter);
		viewpager.setOnPageChangeListener(PagerChangeL);

		Chartchangebar = new ChartChangeBar(viewpager, this);
	}

	private void inheader() {
		// 初始化轮播
		zx_header_viewFlipper = (ViewFlipper) findViewById(R.id.header).findViewById(R.id.zx_header_viewFlipper);
		// 沪深view
		hushenview = (View) LayoutInflater.from(this).inflate(R.layout.tljr_chart_header, null);

		((ImageView) hushenview.findViewById(R.id.tljr_zixuan_leftimage)).setImageResource(R.drawable.img_fengtiao1);
		((ImageView) hushenview.findViewById(R.id.tljr_zixuan_rightimage)).setImageResource(R.drawable.img_fengtiao2);
		
		// 创业,中小板view
		zhongchuanview = (View) LayoutInflater.from(this).inflate(R.layout.tljr_chart_header, null);
		((ImageView) zhongchuanview.findViewById(R.id.tljr_zixuan_leftimage)).setImageResource(R.drawable.img_fengtiao4);
		((ImageView) zhongchuanview.findViewById(R.id.tljr_zixuan_rightimage)).setImageResource(R.drawable.img_fengtiao3);
		
		// 装载view
		zx_header_viewFlipper.addView(hushenview);
		zx_header_viewFlipper.addView(zhongchuanview);
		// 设置动画开始滚动
		zx_header_viewFlipper.setInAnimation(this, R.anim.vp_bottom_in_activity);
		zx_header_viewFlipper.setOutAnimation(this, R.anim.vp_bottom_out_activity);
		zx_header_viewFlipper.startFlipping();
		// 沪指
		hu = (TextView) hushenview.findViewById(R.id.tljr_hq_zx_hu_price);
		hupercent = (TextView) hushenview.findViewById(R.id.tljr_hq_zx_hu_changpercent);
		huchange = (TextView) hushenview.findViewById(R.id.tljr_hq_zx_hu_change);
		hushenview.findViewById(R.id.ll1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChartActivity.this, OneGuActivity.class);
				// 用Bundle携带数据
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				OneGu zixuan = new OneGu();
				zixuan.setCode("000001");
				zixuan.setMarket("sh");
				zixuan.setName("上证指数");
				zixuan.setKey("sh000001");
				bundle.putString("code", zixuan.getCode());
				bundle.putString("market", zixuan.getMarket());
				bundle.putString("name", zixuan.getName());
				bundle.putString("key", zixuan.getKey());
				bundle.putSerializable("onegu", zixuan);

				intent.putExtras(bundle);
				ChartActivity.this.startActivity(intent);
			}
		});
		// 深指
		sz = (TextView) hushenview.findViewById(R.id.tljr_hq_zx_sz_price);
		szpercent = (TextView) hushenview.findViewById(R.id.tljr_hq_zx_sz_changpercent);
		szchange = (TextView) hushenview.findViewById(R.id.tljr_hq_zx_sz_change);
		hushenview.findViewById(R.id.ll2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChartActivity.this, OneGuActivity.class);
				// 用Bundle携带数据
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				OneGu zixuan = new OneGu();
				zixuan.setCode("399001");
				zixuan.setMarket("sz");
				zixuan.setName("深证成指");
				zixuan.setKey("sz399001");
				bundle.putString("code", zixuan.getCode());
				bundle.putString("market", zixuan.getMarket());
				bundle.putString("name", zixuan.getName());
				bundle.putString("key", zixuan.getKey());
				bundle.putSerializable("onegu", zixuan);

				intent.putExtras(bundle);
				ChartActivity.this.startActivity(intent);
			}
		});
		// 中小板
		zhong = (TextView) zhongchuanview.findViewById(R.id.tljr_hq_zx_hu_price);
		zhongpercent = (TextView) zhongchuanview.findViewById(R.id.tljr_hq_zx_hu_changpercent);
		zhongchange = (TextView) zhongchuanview.findViewById(R.id.tljr_hq_zx_hu_change);
		zhongchuanview.findViewById(R.id.ll2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChartActivity.this, OneGuActivity.class);
				// 用Bundle携带数据
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				OneGu zixuan = new OneGu();
				zixuan.setCode("399005");
				zixuan.setMarket("sz");
				zixuan.setName("中小板指");
				zixuan.setKey("sz399005");
				bundle.putString("code", zixuan.getCode());
				bundle.putString("market", zixuan.getMarket());
				bundle.putString("name", zixuan.getName());
				bundle.putString("key", zixuan.getKey());
				bundle.putSerializable("onegu", zixuan);

				intent.putExtras(bundle);
				ChartActivity.this.startActivity(intent);
			}
		});
		// 创业板
		chuang = (TextView) zhongchuanview.findViewById(R.id.tljr_hq_zx_sz_price);
		chuangpercent = (TextView) zhongchuanview.findViewById(R.id.tljr_hq_zx_sz_changpercent);
		chuangchange = (TextView) zhongchuanview.findViewById(R.id.tljr_hq_zx_sz_change);
		zhongchuanview.findViewById(R.id.ll1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChartActivity.this, OneGuActivity.class);
				// 用Bundle携带数据
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				OneGu zixuan = new OneGu();
				zixuan.setCode("399006");
				zixuan.setMarket("sz");
				zixuan.setName("创业板指");
				zixuan.setKey("sz399006");
				bundle.putString("code", zixuan.getCode());
				bundle.putString("market", zixuan.getMarket());
				bundle.putString("name", zixuan.getName());
				bundle.putString("key", zixuan.getKey());
				bundle.putSerializable("onegu", zixuan);

				intent.putExtras(bundle);
				ChartActivity.this.startActivity(intent);
			}
		});
		reflushDP("sh", "000001", hushenview);
		reflushDP("sz", "399001", hushenview);
		reflushDP("sz", "399006", zhongchuanview);
		reflushDP("sz", "399005", zhongchuanview);
	}

	// 刷新沪深的股指
	public void reflushDP(final String market, final String code, final View view) {
		Util.getRealInfo("market=" + market + "&code=" + code + "", new NetResult() {
			@Override
			public void result(final String msg) {
				try {
					// Log.e("reflushDP_msg",msg);
					org.json.JSONObject object = new org.json.JSONObject(msg);
					if (object.getInt("code") == 1) {
						if (handler != null) {
							final org.json.JSONArray array = object.getJSONArray("result");
							handler.post(new Runnable() {
								@Override
								public void run() {
									try {
										if (code.equals("000001")) {
											hu.setText((float) array.getDouble(0) + "");
											hupercent.setText((float) array.getDouble(9) > 0
													? "+" + (float) array.getDouble(9) + "%"
													: (float) array.getDouble(9) + "%");
											huchange.setText((float) array.optDouble(8, 0) > 0
													? "+" + (float) array.optDouble(8, 0) + ""
													: (float) array.optDouble(8, 0) + "");
											hu.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
											hupercent.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
											huchange.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
										} else if (code.equals("399006")) {
											zhong.setText((float) array.getDouble(0) + "");
											zhongpercent.setText((float) array.getDouble(9) > 0
													? "+" + (float) array.getDouble(9) + "%"
													: (float) array.getDouble(9) + "%");
											zhongchange.setText((float) array.optDouble(8, 0) > 0
													? "+" + (float) array.optDouble(8, 0) + ""
													: (float) array.optDouble(8, 0) + "");
											zhong.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
											zhongpercent.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
											zhongchange.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
										} else if (code.equals("399001")) {
											sz.setText((float) array.getDouble(0) + "");
											szpercent.setText((float) array.getDouble(9) > 0
													? "+" + (float) array.getDouble(9) + "%"
													: (float) array.getDouble(9) + "%");
											szchange.setText((float) array.optDouble(8, 0) > 0
													? "+" + (float) array.optDouble(8, 0) + ""
													: (float) array.optDouble(8, 0) + "");
											sz.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
											szpercent.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
											szchange.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
										} else if (code.equals("399005")) {
											chuang.setText((float) array.getDouble(0) + "");
											chuangpercent.setText((float) array.getDouble(9) > 0
													? "+" + (float) array.getDouble(9) + "%"
													: (float) array.getDouble(9) + "%");
											chuangchange.setText((float) array.optDouble(8, 0) > 0
													? "+" + (float) array.optDouble(8, 0) + ""
													: (float) array.optDouble(8, 0) + "");
											chuang.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
											chuangpercent.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
											chuangchange.setTextColor((float) array.optDouble(8, 0) > 0 ? ColorUtil.red
													: ColorUtil.green);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}
					}
				} catch (org.json.JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	int time = 0;
	private PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(list.get(position));
			if (position == 2 && time == 0) {
				time = 1;
				weseeview.handler.sendEmptyMessage(4);
			}
			return list.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
	};
	private OnPageChangeListener PagerChangeL = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			Chartchangebar.onPageSelected(position);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}
	};

	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.tljr_img_futures_join_back:
				finish();
				overridePendingTransition(0, R.anim.vp_bottom_out_activity);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			overridePendingTransition(R.anim.vp_top_in_activity, R.anim.vp_bottom_out_activity);
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// 获取云之讯token
	public static void getYunZhiToken(String uid, String nickName, String mobile, String avatar) {
		// &mobile= &avatar=
		NetUtil.sendPost(UrlUtil.Url_235 + "8080/TLChat/rest/user/login",
				"uid=" + uid + "&nickname=" + nickName + "&mobile=13800000000" + "&avatar=" + avatar, new NetResult() {
					@Override
					public void result(String msg) {
						// TODO Auto-generated method stub
						try {
							JSONObject js = new JSONObject(msg);
							if (js.getInt("status") == 1) {
								JSONObject oj = js.getJSONObject("msg");
								String token = oj.optString("token");
								if (token.length() > 0) {
									Constant.preference.edit().putString("YunZhiToken", token);
									YunZhiToken = token;
									if(User.getUser()!=null){
										User.getUser().setSSID(token);
										User.getUser().setIsadmin(oj.optBoolean("isadmin"));
									}
								
								}
							
								// MyApplication.getInstance().getMainActivity().mHandler.sendEmptyMessage(97);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		super.handleMsg(msg);
		switch (msg.what) {
		case 0:
			break;
		case 8:
			bottom.setVisibility(View.GONE);
			break;
		case 9:
			bottom.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		realtimeview.handler.postDelayed(realtimeview.run, 5000);
		super.onResume();
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		realtimeview.handler.removeCallbacks(realtimeview.run);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// TooltipMgr.getInstance().onResume();
		islive = false;
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		// | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
		// | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		super.onDestroy();
		getApplicationContext().unregisterReceiver(receiveBroadCast);
	}

	public class ReceiveBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 得到广播中得到的数据，并显示出来
			if (intent.getStringExtra("data").equals("updateFuture")){
				futureaskview.adapter.notifyDataSetChanged();
			} else if (intent.getStringExtra("data").equals("updateFutureposition")) {
				int position = intent.getIntExtra("position", 0);
				futureaskview.array.remove(position);
				futureaskview.adapter.notifyDataSetChanged();
			}
		}
	}

}
