package com.abct.tljr.hangqing;

import io.realm.Realm;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.hangqing.foreign.ForeignFragment;
import com.abct.tljr.hangqing.hqGridView.HQGridView;
import com.abct.tljr.hangqing.hqGridView.UpdateHQGridViewService;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.hangqing.util.PagerSlidingTabStrip;
import com.abct.tljr.hangqing.util.ParseJson;
import com.abct.tljr.hangqing.zixuan.DeleteZiXuanGu;
import com.abct.tljr.hangqing.zixuan.HangQingView;
import com.abct.tljr.hangqing.zixuan.TljrZiXuanLineChart;
import com.abct.tljr.hangqing.zixuan.UpdateZiXuanService;
import com.abct.tljr.hangqing.zixuan.tljr_zixuan_gu_recyclerview;
import com.abct.tljr.hangqing.zuhe.TljrZuHe;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import com.qh.common.util.FileUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import com.abct.tljr.utils.Util;
import com.abct.tljr.wxapi.WXEntryActivity;

public class HangQing extends BaseFragment implements OnClickListener {
	public static MainActivity activity;
	public View view;
	public static ViewPager viewpager = null;
	public static HQGridView hqGridView = null;
	public static TljrZuHe mTljrZuHe = null;
	public HangQingView mHangQingView=null;
	public static tljr_zixuan_gu_recyclerview zixuan=null;
	
	private ImageView hq_chazhao;
	private TextView txt_hq_tongbutime;
	private boolean isInit = false;
	private Calendar now = Calendar.getInstance();
	private Calendar today = Calendar.getInstance();
	// 早上刷新时间段
	private long morningstart = 0L;
	private long morningend = 0L;
	// 下午刷新时间段
	private long afternoonstart = 0L;
	private long afternoonend = 0L;
	// 现在的时间
	private long nowtime = 0L;
	private TextView hangqingtitle;
	private int ZiXuanUpdateStatus =2;
	private int ZiXuanUpdateTime = 0;

	public MainActivityResult mainActivityResult;
	public static PagerSlidingTabStrip mPagerSlidingTabStrip;

	public HangQing context = null;
	public DisplayMetrics dm = null;

	public static int addStatus = 0;
	public static HangQingFragmentAdapter adapter = null;
	private RelativeLayout zixuan_shanchu;

	public static Map<String,String> HangQingTab = null;
	public List<String> TabTitle = new ArrayList<String>();
	public List<ForeignFragment> ListObject = new ArrayList<ForeignFragment>();
	
	public static ImageView xialaicon=null;
	public LinearLayout headertitle;
	public View hangqingmenu;
	public String nowTitle="";
	public PopupWindow mPopupWindow=null;
	public int nowitem=0;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		this.context = this;
		activity = (MainActivity) getActivity();
		dm = activity.getResources().getDisplayMetrics();
		view = getActivity().getLayoutInflater().inflate(R.layout.tljr_fragment_hangqing, null);
		hangqingtitle = (TextView) view.findViewById(R.id.tljr_title);
		zixuan_shanchu = (RelativeLayout) view.findViewById(R.id.tljr_img_hqzixuan_shanchu);
		viewpager = (ViewPager) view.findViewById(R.id.tljr_hq_viewpager);
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.fragment_hq_tabs);
		hangqingtitle.setText("行情");
		zixuan_shanchu.setVisibility(View.GONE);
		xialaicon=(ImageView)view.findViewById(R.id.tljr_xialaicon);
		headertitle=(LinearLayout)view.findViewById(R.id.hangqing_headertitle);
		mainActivityResult = new MainActivityResult();
		IntentFilter intentFilter = new IntentFilter("com.tljr.mainActivityResult");
		activity.registerReceiver(mainActivityResult, intentFilter);
		headertitle.setOnClickListener(this);
		if(HangQingTab!=null){
			try{
				initView();
			}catch(Exception e){
			}
		}else{
			String url=UrlUtil.Url_apicavacn+"tools/index/0.2/qlist";
			NetUtil.sendGet(url,"",new NetResult() {
				@Override
				public void result(String msg) {
					if(!msg.equals("")){
						HangQing.HangQingTab=ParseJson.ParseHangQingTab(msg);
						initView();
					}else{
						HangQing.HangQingTab=ParseJson.ParseHangQingTab(FileUtil.getFromAssets("hangqing.properties"));
						initView();
					}
				}
			});	
		}
		return view;
	}

	public void initTabData() {
		// 初始化tab名字,对象
		TabTitle.add("行情");
		TabTitle.add("自选");
		TabTitle.add("组合");
		if(HangQingTab!=null&&!HangQingTab.isEmpty()){
			for (String name : HangQingTab.keySet()) {
				TabTitle.add(name);
				ListObject.add(new ForeignFragment());
			}
		}
	}
	public void show() {
		if (!isInit) {
			isInit = true;
		}
	}

	@SuppressWarnings("deprecation")
	public void initView() {
		initTabData();
		ProgressDlgUtil.showProgressDlg("", activity);
		if (viewpager != null) {
			adapter = new HangQingFragmentAdapter(getActivity().getSupportFragmentManager());
			viewpager.setAdapter(adapter);
			viewpager.setOffscreenPageLimit(TabTitle.size());
			mPagerSlidingTabStrip.setViewPager(viewpager);
			// 设置Tab的分割线是透明的
			mPagerSlidingTabStrip.setDividerColor(getContext().getResources().getColor(R.color.bj));
			// 设置Tab底部线的高度
			mPagerSlidingTabStrip.setUnderlineHeight(0);
			// 设置Tab Indicator的高度
			mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
			// 设置Tab标题文字的大小
			mPagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16, dm));
			mPagerSlidingTabStrip.setTextColor(getContext().getResources().getColor(R.color.drakgray));
			//设置TabIndicator的颜色
			mPagerSlidingTabStrip.setSelectedTextColor(Color.parseColor("#F05A3C"));
			mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#F05A3C"));
			mPagerSlidingTabStrip.setBackgroundColor(getContext().getResources().getColor(R.color.white));
			mPagerSlidingTabStrip.setTabPaddingLeftRight(20);
			//页面活动的监听器
			mPagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
						@Override
						public void onPageSelected(int page) {
							Constant.addClickCount();
							nowTitle=TabTitle.get(page);
							nowitem=page;
							if(TabTitle.get(page).equals("美股")){
								xialaicon.setVisibility(View.VISIBLE);
								headertitle.setClickable(true);
							}else{
								xialaicon.setVisibility(View.GONE);
								headertitle.setClickable(false);
							}
							switch (page) {
							case 0:
								hangqingtitle.setText(TabTitle.get(page));
								addStatus =10;
								hq_chazhao.setVisibility(View.GONE);
								zixuan_shanchu.setVisibility(View.GONE);
								zixuan_shanchu.setVisibility(View.GONE);
								break;
							case 1:
								hangqingtitle.setText(TabTitle.get(page));
								addStatus = 0;
								zixuan_shanchu.setVisibility(View.VISIBLE);
								hq_chazhao.setVisibility(View.VISIBLE);
								zixuan_shanchu.setVisibility(View.VISIBLE);
								break;
							case 2:
								hangqingtitle.setText(TabTitle.get(page));
								addStatus = 1;
								hq_chazhao.setVisibility(View.GONE);
								hq_chazhao.setVisibility(View.VISIBLE);
								zixuan_shanchu.setVisibility(View.GONE);
								break;
							default:
								hangqingtitle.setText(TabTitle.get(page));
								ListObject.get(page-3).initView(HangQingTab.get(TabTitle.get(page)),page);
								addStatus = page;
								hq_chazhao.setVisibility(View.GONE);
								break;
							}
						}
						@Override
						public void onPageScrolled(int arg0, float arg1,int arg2) {
						}
						@Override
						public void onPageScrollStateChanged(int arg0) {
						}
					});

			hq_chazhao = (ImageView) view.findViewById(R.id.tljr_img_hq_chazhao);
			hq_chazhao.setVisibility(View.GONE);
			txt_hq_tongbutime = (TextView) view.findViewById(R.id.tljr_txt_hq_tongbutime);
			//refreshSynTime();
			hq_chazhao.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (addStatus == 0) {
						activity.showSouSuo(null);
					} else if (addStatus == 1) {
						if (User.getUser() == null) {
							Intent intent = new Intent(getActivity(),WXEntryActivity.class);
							getActivity().startActivity(intent);
						} else {
							showBuilder("请输入分组名");
						}
					}
				}
			});
			zixuan_shanchu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (User.getUser() == null) {
						Intent intent = new Intent(getActivity(),WXEntryActivity.class);
						getActivity().startActivity(intent);
					} else {
						Intent intent = new Intent(getActivity(),DeleteZiXuanGu.class);
						getActivity().startActivity(intent);
					}
				}
			});
		}
	}

	public class HangQingFragmentAdapter extends FragmentPagerAdapter {
		// public String[] title = { "自选", "组合", "指数", "港股", "美股", "外汇", "商品","国债","期货"};
		public String[] title = new String[TabTitle.size()];
		public HangQingFragmentAdapter(FragmentManager fm) {
			super(fm);
			for (int i = 0; i < TabTitle.size(); i++) {
				title[i] = TabTitle.get(i);
			}
		}
		@Override
		public Fragment getItem(int page) {
			switch (page) {
			case 0:
				if(mHangQingView==null){
					mHangQingView=new HangQingView();
				}
				return mHangQingView;
			case 1:
				if(zixuan==null){
					zixuan=new tljr_zixuan_gu_recyclerview();
				}
				return zixuan;
			case 2:
				if (mTljrZuHe == null) {
					mTljrZuHe = new TljrZuHe();
				}
				return mTljrZuHe;
			default:
				ForeignFragment mForeignFragment = ListObject.get(page - 3);
				if (mForeignFragment == null) {
					mForeignFragment = new ForeignFragment();
				}
				return mForeignFragment;
			}
		}

		@Override
		public int getCount() {
			return title.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return title[position];
		}

		public void setTitle(String[] title) {
			this.title = title;
		}
	}

	// 刷新同步时间
	public void refreshSynTime() {
		if (txt_hq_tongbutime != null) {
			txt_hq_tongbutime.setVisibility(MyApplication.getInstance().self==null?View.GONE:View.VISIBLE);
			txt_hq_tongbutime.setText("上次同步时间:" + Constant.lastSynTime);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setCurrentItem(int i) {
		viewpager.setCurrentItem(i);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	int time = 0;

	public void oneSecAction() {
		switch (viewpager.getCurrentItem()) {
		case 0:
			if (TljrZiXuanLineChart.mSYChart != null) {
				TljrZiXuanLineChart.mSYChart.runforupdata();
			}
			break;
		case 1:
			//if (checkTime()) {
				ZiXuanUpdateTime++;
				if (ZiXuanUpdateStatus == ZiXuanUpdateTime) {
					Intent intent2 = new Intent(activity,UpdateZiXuanService.class);
					activity.startService(intent2);
					ZiXuanUpdateTime = 0;
				}
			//}
			break;
		case 2:
			Intent intent = new Intent(activity, UpdateHQGridViewService.class);
			activity.startService(intent);
			break;
		case 3:
			break;
		default:
			break;
		}
		if(((FragmentPagerAdapter) viewpager.getAdapter())!=null){
			if (((FragmentPagerAdapter) viewpager.getAdapter()).getItem(viewpager.getCurrentItem()) instanceof ForeignFragment) {
				((ForeignFragment) ((FragmentPagerAdapter) viewpager.getAdapter()).getItem(viewpager.getCurrentItem())).flush();
			}
		}
	}

	public void setZiXuanNum(int num) {

	}

	public void setZuHeNum(int num) {

	}

	// 查看是不是应该刷新
	public boolean checkTime() {
		// 早上开始时间
		today.set(Calendar.HOUR_OF_DAY, 9);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		morningstart = today.getTime().getTime();
		// 早上结束时间
		today.set(Calendar.HOUR_OF_DAY, 11);
		today.set(Calendar.MINUTE, 29);
		today.set(Calendar.SECOND, 59);
		today.set(Calendar.MILLISECOND, 999);
		morningend = today.getTime().getTime();
		// 上午开始时间
		today.set(Calendar.HOUR_OF_DAY, 13);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		afternoonstart = today.getTime().getTime();
		// 下午结束时间
		today.set(Calendar.HOUR_OF_DAY, 15);
		today.set(Calendar.MINUTE, 29);
		today.set(Calendar.SECOND, 59);
		today.set(Calendar.MILLISECOND, 0);
		afternoonend = today.getTime().getTime();
		// 当前时间
		nowtime = now.getTime().getTime();
		// 判断时间区域
		if ((morningstart < nowtime && nowtime < morningend)
				|| (afternoonstart < nowtime && nowtime < afternoonend)) {
			return true;
		} else {
			return false;
		}
	}

	// 添加股票接受广播
	class MainActivityResult extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ProgressDlgUtil.stopProgressDlg();
			final Context tempcontext = context;
			final String zuid = intent.getStringExtra("zuid");
			final String key = intent.getStringExtra("code");
			final String zhName = intent.getStringExtra("zuName");
			Realm realm = Realm.getDefaultInstance();
			realm.close();
			if (!key.equals("")) {
				if (zhName == null || zhName.equals("")) {
					GuDealImpl.addGuInfo(tempcontext, key, ZiXuanUtil.nowFenZu,zuid);
				} else {
					GuDealImpl.addGuInfo(tempcontext, key, zhName,zuid);
				}
			} else {
			}
		}
	}

	
	public void showBuilder(String title) {
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.tljr_zx_addzuview,null);
		final EditText edittext=(EditText)view.findViewById(R.id.addzu_content);
		final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setView(view);
		builder.create();
		final AlertDialog dialog=builder.show();
		((Button)view.findViewById(R.id.addzu_quxiao)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		((Button)view.findViewById(R.id.addzu_quedin)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = edittext.getText().toString();
				if (name.equals("")) {
					showMessage("请输入分组名称");
					return;
				}
				if (ZiXuanUtil.fzMap.containsKey(name)) {
					showMessage("此分组已存在");
					return;
				}
				ZiXuanUtil.addNewFenzu(name, false);
				TljrZuHe.addNewFenZu(name);
				MainActivity.AddZuHuStatus = 1;
				ZiXuanUtil.emitNowFenZu(name);
				if (MyApplication.getInstance().self != null) {
					ZiXuanUtil.sendActions(MyApplication.getInstance().self,(MainActivity)getActivity(), null);
				}
				dialog.dismiss();
			}
		});
	}
	public void showMessage(String msg) {
		Message message = Message.obtain();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("msg", msg);
		message.setData(bundle);
		mHandler.sendMessage(message);
	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getActivity(), msg.getData().getString("msg"),Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.hangqing_headertitle:
			if(nowTitle.equals("美股")){
				showPopulWindow("");
			}
			break;
		}
	}

	@SuppressWarnings("deprecation")
	public void showPopulWindow(String type){
		try{
			final ForeignFragment mForeignFragment=((ForeignFragment)((FragmentPagerAdapter)viewpager.getAdapter()).getItem(nowitem));
			final View views = LayoutInflater.from(getActivity()).inflate(R.layout.tljr_hangqing_forign_populwindow, null);
			mPopupWindow = new PopupWindow(views,Util.WIDTH/2,Util.WIDTH/2);
			mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();  
		    lp.alpha = 0.7f;  
		    getActivity().getWindow().setAttributes(lp);  
			int[] location = new int[2];  
		    view.getLocationOnScreen(location);
		    mPopupWindow.showAsDropDown(view.findViewById(R.id.tljr_grp_hq_title),Util.WIDTH/4,0);
		    mPopupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();  
				    lp.alpha = 1f;  
				    getActivity().getWindow().setAttributes(lp); 
				}
			});
		    views.findViewById(R.id.meigu).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent=new Intent("com.forignfragement.mg");
					intent.putExtra("key",mForeignFragment.tabmenu.get(
							((TextView)views.findViewById(R.id.meigu)).getText().toString()));
					getActivity().sendBroadcast(intent);
					mPopupWindow.dismiss();
				}
			});
		    views.findViewById(R.id.zhonggaigu).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent=new Intent("com.forignfragement.mg");
					intent.putExtra("key",mForeignFragment.tabmenu.get(
							((TextView)views.findViewById(R.id.zhonggaigu)).getText().toString()));
					getActivity().sendBroadcast(intent);
					mPopupWindow.dismiss();
				}
			});
		    views.findViewById(R.id.biaopu).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent=new Intent("com.forignfragement.mg");
					intent.putExtra("key",mForeignFragment.tabmenu.get(
							((TextView)views.findViewById(R.id.biaopu)).getText().toString()));
					getActivity().sendBroadcast(intent);
					mPopupWindow.dismiss();
				}
			});
		    views.findViewById(R.id.mingxing).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent=new Intent("com.forignfragement.mg");
					intent.putExtra("key",mForeignFragment.tabmenu.get(
							((TextView)views.findViewById(R.id.mingxing)).getText().toString()));
					getActivity().sendBroadcast(intent);
					mPopupWindow.dismiss();
				}
			});
		}catch(Exception e){
			
		}
	}
	
}
