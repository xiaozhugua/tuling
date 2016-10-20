package com.abct.tljr.kline;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.MySuperBaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.kline.gegu.fragment.OneguCaiwuFragment;
import com.abct.tljr.kline.gegu.fragment.OneguGaikuangFragment;
import com.abct.tljr.kline.gegu.fragment.OneguGuzhiFragment;
import com.abct.tljr.kline.gegu.fragment.OneguTulingZhiyanFrgment;
import com.abct.tljr.kline.gegu.fragment.OneguXinwenFragment;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.utils.Util;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * momo写的onegu的页面
 */

public class OneGuActivity extends MySuperBaseActivity implements OnRefreshListener, OnClickListener {
	public static ArrayList<OneData> dataday = new ArrayList<OneData>();
	public static ArrayList<OneData> dataweek = new ArrayList<OneData>();
	public static ArrayList<OneData> datayue = new ArrayList<OneData>();
	public static ArrayList<OneData> fiveDatas = new ArrayList<OneData>();

	public static String gukey;
	public static ArrayList<String> addZuName = new ArrayList<String>();
	public static ArrayList<String> removeZuName = new ArrayList<String>();
	private ArrayList<String> hasCode;
	private ArrayList<String> noHasCode;
	private ArrayList<String> listCode = new ArrayList<String>();

	public static boolean isScroll = false;

	/**
	 * 带有Viewpager的主布局
	 */
	private View onegu2View;
	private ViewPager oneguViewPager;
	private ArrayList<Fragment> list;
	private LinearLayout buts;
	public String code;
	public String name;
	public String market;
	private String guKey;

	private boolean isJJ = false;
	/**
	 * 顶部返回按钮，标题，键入组合的一栏
	 */
	public RelativeLayout topColumn;
	private TextView topTitle;
	private TextView combination;
	private OneguTitleBar oneguTitleBar;
	private String zuName;
	private JSONObject marketInfo;
	private Timer timer;
	public SwipeRefreshLayout mSwipeRefreshLayout;
	public int UpOrDowm = 0;
	public OneguTulingZhiyanFrgment mOneguTulingZhiyanFrgment = null;
	public SwipeRefreshLayout sw;
	public int hight;
	private OneGu addOneGu;
	private Context context = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		onegu2View = View.inflate(this, R.layout.activity_onegu2, null);
		mOneguTulingZhiyanFrgment = new OneguTulingZhiyanFrgment();
		setContentView(onegu2View);
		context = this;
		sw = (SwipeRefreshLayout) onegu2View.findViewById(R.id.activity_onegu2_sr);
		initData();
		initView();
	}

	/**
	 * 接收从上一个页面传递过来的数据
	 */
	private void initData() {
		// 新页面接收数据
		Bundle bundle = this.getIntent().getExtras();
		code = bundle.getString("code");// 我武生物右边的代码
		name = bundle.getString("name");// 我武生物
		market = bundle.getString("market");
		guKey = bundle.getString("key");
		isJJ = guKey.substring(0, 2).equals("jj");
		for (String key : ZiXuanUtil.fzMap.keySet()) {
			for (int i = 0; i < ZiXuanUtil.fzMap.get(key).getList().size(); i++) {
				for (OneGu gu : ZiXuanUtil.fzMap.get(key).getList()) {
					if (gu.getKey().equals(guKey)) {
						addOneGu = gu;
						break;
					}
				}
			}
		}
		if (addOneGu == null) {
			addOneGu = (OneGu) getIntent().getExtras().getSerializable("onegu");
		}
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		topColumn = (RelativeLayout) onegu2View.findViewById(R.id.tljr_grp_hq_top);// 顶部栏
		mSwipeRefreshLayout = (SwipeRefreshLayout) onegu2View.findViewById(R.id.activity_onegu2_sr);// 把SwipeRefreshLayout
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
		// 返回按钮
		onegu2View.findViewById(R.id.tljr_img_hq_fanhui).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OneGuActivity.this.finish();
			}
		});
		// 顶部栏的标题
		topTitle = (TextView) onegu2View.findViewById(R.id.tljr_txt_hq_name);

		topTitle.setText(name);
		/* stockInfo.setText(isJJ ? ("基金" + code) : (market + code)); */
		// 加入组合按钮
		combination = (TextView) onegu2View.findViewById(R.id.tljr_txt_tjzx);

		// 这里处理点击顶部栏的组合按钮的情况
		combination.setOnClickListener(this);

		// 设置viewpager和下面按钮关联
		setViewPagerAndButton();
		// 从网络获取数据
		getNetData();

	}

	/**
	 * 判断market是否是深圳或者上海
	 */
	private boolean isShenZhenOrShangHai(String market) {
		if ("sz".equals(market) || "sh".equals(market)) {

			return true;
		}
		return false;
	}

	/**
	 * 从网络获取数据
	 */
	private void getNetData() {
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

		marketInfo = Constant.marketInfo.get(market.toLowerCase());

		/*
		 * // 用计时器间隔6秒就访问一次后台数据 timer = new Timer(true); timer.schedule(task, 0,
		 * 6000); // 延时0ms后执行，6s执行一次
		 */
	}

	private TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};
	private OneguGuzhiFragment oneguGuzhiFragment;
	private OneguCaiwuFragment oneguCaiwuFragment;
	private OneguXinwenFragment oneguXinwenFragment;
	private OneguGaikuangFragment oneguGaikuangFragment;
	private OneguTulingZhiyanFrgment oneguTulingZhiyanFrgment;

	/**
	 * 处理消息
	 */
	public void handleMsg(Message msg) {
		if (msg.what == 1) {
			oneguGuzhiFragment.reflushDP();
		}
	};

	/**
	 * 刷新固定部分
	 */
	public void reflushGuding() {
		Util.getRealInfo("market=" + market + "&code=" + code, new NetResult() {
			@Override
			public void result(final String msg) {
				post(new Runnable() {
					@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						try {
							sw.setRefreshing(false);
							org.json.JSONObject object = new org.json.JSONObject(msg);
							if (object.getInt("code") == 1) {
								final org.json.JSONArray array = object.getJSONArray("result");
								if (array.optDouble(9) > 0) {
									UpOrDowm = 1;
									mOneguTulingZhiyanFrgment.changeBackground();
									// stockHeader.setBackground(getResources().getDrawable(R.drawable.img_zhangbeijing));
									if (topColumn != null) {
										topColumn.setBackground(getResources().getDrawable(R.drawable.img_zhangbeijing));
									}
									// hq_header.setBackground(getResources().getDrawable(R.drawable.img_zhangbeijing));
								} else {
									// 给设置颜色
									UpOrDowm = 2;
									mOneguTulingZhiyanFrgment.changeBackground();
									// stockHeader.setBackground(getResources().getDrawable(R.drawable.img_diebeijing));
									if (topColumn != null) {
										topColumn.setBackground(getResources().getDrawable(R.drawable.img_diebeijing));
									}
									// hq_header.setBackground(getResources().getDrawable(R.drawable.img_diebeijing));
								}
								// stockCode.setText(code);
							}
						} catch (org.json.JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	private void setViewPagerAndButton() {
		list = new ArrayList<Fragment>();
		oneguGuzhiFragment = new OneguGuzhiFragment();
		oneguCaiwuFragment = new OneguCaiwuFragment();
		oneguXinwenFragment = new OneguXinwenFragment();
		oneguGaikuangFragment = new OneguGaikuangFragment();
		oneguTulingZhiyanFrgment = new OneguTulingZhiyanFrgment();
		if (isShenZhenOrShangHai(market)) {

			list.add(oneguGuzhiFragment);// onegu的估值
			list.add(oneguCaiwuFragment);// onegu的财务
			list.add(oneguXinwenFragment);// onegu的新闻
			list.add(oneguGaikuangFragment);// onegu的概况
			list.add(mOneguTulingZhiyanFrgment);// onegu的图灵智研
		} else {
			list.add(oneguGuzhiFragment);// onegu的估值
			list.add(mOneguTulingZhiyanFrgment);// onegu的图灵智研
		}
		oneguViewPager = (ViewPager) onegu2View.findViewById(R.id.activity_onegu2_viewpager);
		buts = (LinearLayout) onegu2View.findViewById(R.id.activity_onegu2_ll_buts);

		oneguViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				if (list != null) {
					return list.size();
				}
				return 0;
			}

			@Override
			public Fragment getItem(int position) {
				// TODO Auto-generated method stub
				if (list != null && list.size() != 0) {
					return list.get(position);

				}
				return null;
			}
		});
		oneguViewPager.setOffscreenPageLimit(list.size());
		// 处理viewpager和按钮切换的效果
		oneguTitleBar = new OneguTitleBar(oneguViewPager, buts, list, isShenZhenOrShangHai(market));
	}

	/**
	 * 获取顶部栏
	 */
	public RelativeLayout getTopColumn() {
		if (topColumn != null) {
			return topColumn;
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dataday.clear();
		dataweek.clear();
		datayue.clear();
		fiveDatas.clear();
		if (oneguTitleBar != null) {
			oneguTitleBar = null;
		}
		/* timer.cancel(); */
		if (oneguGuzhiFragment.timer != null) {

			// 关闭计时器
			oneguGuzhiFragment.timer.cancel();
		}

	}

	/**
	 * 拖动刷新
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		reflushGuding();

		if (isShenZhenOrShangHai(market)) {

			switch (oneguViewPager.getCurrentItem()) {
			case 0:
				oneguGuzhiFragment.reflushDP();
				break;
			case 1:
				oneguCaiwuFragment.getAllChartData();
				break;
			case 2:
				try {
					oneguXinwenFragment.getData();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				oneguGaikuangFragment.getAllCompaneyData();
				break;
			default:
				break;
			}
		} else {
			// 不是深圳或者上海的股票数据处理

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tljr_txt_tjzx:
			if (MyApplication.getInstance().self != null) {
				if (combination.getText().toString().equals("删除自选")) {
					showBuildDialog(false);
				} else {
					showBuildDialog(true);
				}
			} else {
				Intent intent = new Intent(context, WXEntryActivity.class);
				context.startActivity(intent);
			}
			break;
		}
	}

	public void showBuildDialog(final boolean isAdd) {
		if (hasCode == null) {
			hasCode = new ArrayList<String>();
			noHasCode = new ArrayList<String>();
			for (OneFenZu zu : ZiXuanUtil.fzMap.values()) {
				if (!ZiXuanUtil.isHas(zu.getName(), gukey)) {
					noHasCode.add(zu.getName());
				}
				if (ZiXuanUtil.isHas(zu.getName(), gukey)) {
					hasCode.add(zu.getName());
				}
			}
		}
		listCode.clear();
		if (isAdd) {
			listCode.addAll(noHasCode);
		} else {
			listCode.addAll(hasCode);
		}
		if (isAdd == true && noHasCode.size() == 0) {
			Toast.makeText(getBaseContext(), "对不起,没有新的组合可以加入自选该股", Toast.LENGTH_SHORT).show();
		} else if (isAdd == false && hasCode.size() == 0) {
			Toast.makeText(getBaseContext(), "对不起,没有组合含有该股票", Toast.LENGTH_SHORT).show();
		} else {
			// 初始化要输入的组的数据
			final String[] arrayItems = new String[listCode.size()];
			final boolean[] checkedItems = new boolean[listCode.size()];
			for (int i = 0; i < arrayItems.length; i++) {
				arrayItems[i] = listCode.get(i);
				checkedItems[i] = false;
			}
			checkedItems[0] = true;
			// 初始化并显示Dialog
			AlertDialog alertDialog = new AlertDialog.Builder(OneGuActivity.this)
					.setTitle("请选择" + (isAdd ? "添加" : "删除") + "股票的分组")
					.setMultiChoiceItems(arrayItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							// 点击时各组数据的变化
							checkedItems[which] = isChecked;
						}
					}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							boolean selectstatus = true;
							int count = 0;
							for (int i = 0; i < checkedItems.length; i++) {
								if (checkedItems[i]) {
									selectstatus = false;
									count++;
									if (isAdd) {
										hasCode.add(arrayItems[i]);
										noHasCode.remove(arrayItems[i]);
										// 添加股份
										addGu(arrayItems[i]);
									} else {
										hasCode.remove(arrayItems[i]);
										noHasCode.add(arrayItems[i]);
										// 删除股份
										deleteGu(arrayItems[i]);
									}
								}
							}
							if (isAdd && count > 0) {
								combination.setText("删除自选");
							}
							if (!isAdd && count > 0) {
								combination.setText("加入自选");
							}
							if (selectstatus) {
								Toast.makeText(getBaseContext(), "对不起,没有选择股票", Toast.LENGTH_SHORT).show();
							}
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 取消按钮
						}
					}).create();
			alertDialog.show();
		}

	}

	public void deleteGu(String zuName) {
		GuDealImpl.deleteGu(context, code, market, guKey, zuName);
	}

	public void addGu(String zuName) {
		if (addOneGu != null || getIntent().getStringExtra("key") != null) {
			// 添加股票
			Intent intent = new Intent("com.tljr.mainActivityResult");
			if (addOneGu != null) {
				intent.putExtra("code", addOneGu.getKey());
			} else {
				intent.putExtra("code", getIntent().getStringExtra("key"));
			}
			intent.putExtra("zuName", zuName);
			intent.putExtra("zuid", ZiXuanUtil.fzMap.get(zuName).getId());
			sendBroadcast(intent);
		} else {
			Toast.makeText(getBaseContext(), "没有股票数据", Toast.LENGTH_SHORT).show();
		}
	}

}
