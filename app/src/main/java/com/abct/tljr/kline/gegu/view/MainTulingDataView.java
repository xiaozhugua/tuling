package com.abct.tljr.kline.gegu.view;

import java.io.File;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneItem;
import com.abct.tljr.ui.activity.TulingChartActivity;
import com.abct.tljr.utils.DownloadProUtil;
import com.abct.tljr.utils.SuperGridView;
import com.abct.tljr.utils.Util;
import com.abct.tljr.utils.ViewUtil;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.ryg.utils.DLUtils;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * xiaomo——mainfragment中的图灵数据九宫格部分
 */
public class MainTulingDataView {

	/**
	 * 总布局
	 */
	private View mTulingDataView;
	/**
	 * 添加九宫格的容器
	 */
	private LinearLayout mContantView;
	/**
	 * 九宫格的列数
	 */
	private int countNums = 4;// 九宫格的列数
	/**
	 * 屏幕宽度
	 */
	private int screenWith;
	private MainActivity activity;
	/**
	 * 固定的5个小图标
	 */
	private int[] secondIcons = { R.drawable.abh, R.drawable.abh, R.drawable.etf, R.drawable.basis, R.drawable.build };// ,
																														// R.drawable.mood,
	private String[] secondNames = { "A/B分级基金", "A/H差价 ", "沪港通数据", "基差", "自建指数" };// "新闻热度/情绪",
	private String[] toActivitys = { Constant.packageName + ".ui.activity.tools.AbDataActivity",
			Constant.packageName + ".ui.activity.tools.AhDataActivity",
			Constant.packageName + ".ui.activity.tools.AbhActivity",//沪港通数据
			Constant.packageName + ".ui.activity.tools.BasisActivity",
			Constant.packageName + ".ui.activity.tools.BuildListActivity" };

	/**
	 * 先加固定的图表按钮的小图
	 */
	private int[] firstIcons = { R.drawable.img_baojia, R.drawable.img_dabaogao, R.drawable.img_gonggao,
			R.drawable.img_jingjishuju, R.drawable.img_jishugonglve, R.drawable.img_quanqiuzhibo,
			R.drawable.img_yanjiu };//
	private String[] firstNames = { "两融数据", "银证转账信息", "期权成交量", "新增开户数", "市盈率", "票据信息", "银行间固定利率国债收益率曲线(年)" };
	/**
	 * 屏幕高度
	 */
	private int screenHight;
	/**
	 * 九宫格
	 */
	private GridView mTulingGridView;

	/**
	 * 九宫格的适配器
	 */
	private MyGridViewAdapter myGridViewAdapter;

	/**
	 * 九宫格的数组列表
	 */
	private ArrayList<View> list;

	public MainTulingDataView(MainActivity activity) {
		this.activity = activity;
		mTulingDataView = LayoutInflater.from(activity).inflate(R.layout.layout_mian_tuling_data, null);
		mContantView = (LinearLayout) mTulingDataView.findViewById(R.id.layout_tuling_data);
		LogUtil.i("momo", "setInfo这里");
		// mTulingGridView = (GridView) View.inflate(activity,
		// R.layout.layout_main_grieview, null);// 准备好的九宫格
		mTulingGridView = new SuperGridView(activity);
		mTulingGridView.setNumColumns(countNums);
		mTulingGridView.setBackgroundColor(Color.WHITE);
		screenWith = ViewUtil.getScreenWidth(activity);// 当前屏幕宽度
		screenHight = ViewUtil.getScreenHeight(activity);// 获取屏幕高度

		initGridView();

	}

	private void initGridView() {
		View view = null;
		list = new ArrayList<View>();
		// 先加固定的7个图表按钮
		for (int i = 0; i < firstIcons.length; i++) {
			view = getOneXinxi(firstIcons[i], firstNames[i], i);
			initAppLine(view, i);
			list.add(view);

		}
		// 添加固定的5个按钮
		for (int i = 0; i < secondIcons.length; i++) {
			view = getOneXinxi(secondIcons[i], secondNames[i], toActivitys[i]);
			initAppLine(view, i + firstIcons.length);
			list.add(view);
		}
		refresh(list);// 刷新操作
		setInfo(1);
		//setInfo(2);
		// 把九宫格添加到容器中
		mContantView.addView(mTulingGridView);

	}

	private void refresh(ArrayList<View> list) {
		// TODO Auto-generated method stub
		if (list.size() == 0 || list == null) {
			return;
		}
		int num = list.size() / countNums + (list.size() % countNums == 0 ? 0 : 1);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		params.height = (int) (num * screenHight / 6.0f);// 记住要添加这句，一定要动态计算高度
		mTulingGridView.setLayoutParams(params);// 记住要添加这句
		/*
		 * mTulingGridView.setVerticalSpacing(1);
		 * mTulingGridView.setHorizontalSpacing(1);
		 */
		mTulingGridView.invalidate();
		setAdapter(mTulingGridView, list);

	}

	/**
	 * 设置适配器
	 */
	private void setAdapter(GridView view, final ArrayList<View> list) {
		view.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// view.setNumColumns(countNums);

		myGridViewAdapter = new MyGridViewAdapter(list);
		view.setAdapter(myGridViewAdapter);

	}

	class MyGridViewAdapter extends BaseAdapter {

		private ArrayList<View> list;

		public MyGridViewAdapter(ArrayList<View> list) {
			// TODO Auto-generated constructor stub
			MyGridViewAdapter.this.list = list;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

	}

	/**
	 * 设置九宫格条目之间的间距
	 */
	private void initAppLine(View view, int i) {
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.tljr_grp_app_di);
		layout.setBackgroundColor(Color.WHITE);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
		switch (i % 4) {
		case 0:
		case 1:
		case 2:
			params.setMargins(0, 0, 1, 1);
			break;

		case 3:
			params.setMargins(0, 0, 0, 1);
			break;
		default:
			break;
		}
	}

	/**
	 * 第一部分固定图表按钮——确定item条目
	 */
	public View getOneXinxi(int icon, String title, final int number) {

		LinearLayout view = getItemView(icon, title);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity, TulingChartActivity.class);
				// 传递参数
				intent.putExtra("chartNumber", number);
				LogUtil.i("momo", "chartNumber=" + number);
				activity.startActivity(intent);
				LogUtil.i("momo", "跳跃页面之后");

			}
		});

		return view;
	}

	/**
	 * 第二部分固定的5个按钮——根据图片标题和跳到的页面来确定条目View
	 */
	public View getOneXinxi(int icon, String title, final String toActivity) {
		/*
		 * final LinearLayout view = (LinearLayout) View.inflate(activity,
		 * R.layout.tljr_item_app, null);
		 * view.findViewById(R.id.tljr_img_app_type).setVisibility(View.GONE);
		 * view.setLayoutParams(new AbsListView.LayoutParams(screenWith /
		 * countNums, (int) (screenHight / 6.67f))); ((ImageView)
		 * view.findViewById(R.id.tljr_img_app_icon)).setBackgroundResource(icon
		 * ); ((TextView)
		 * view.findViewById(R.id.tljr_txt_app_name)).setText(title);
		 */
		LinearLayout view = getItemView(icon, title);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				ComponentName cn = new ComponentName(activity, toActivity);
				intent.setComponent(cn);
				activity.startActivity(intent);
			}
		});
		return view;
	}

	/**
	 * 获取条目控件
	 */
	private LinearLayout getItemView(int icon, String title) {
		final LinearLayout view = (LinearLayout) View.inflate(activity, R.layout.tljr_item_app, null);
		view.findViewById(R.id.tljr_img_app_type).setVisibility(View.GONE);
		//view.setLayoutParams(new AbsListView.LayoutParams(screenWith / countNums, (int) (screenHight / 6.67f)));
		view.setLayoutParams(new AbsListView.LayoutParams(screenWith / countNums, (int) (screenHight / 6.0f)));
		((ImageView) view.findViewById(R.id.tljr_img_app_icon)).setBackgroundResource(icon);
		((TextView) view.findViewById(R.id.tljr_txt_app_name)).setText(title);
		return view;
	}

	/**
	 * 获取控件
	 */
	public View getView() {
		// TODO Auto-generated method stub
		return mTulingDataView;
	}

	public void setInfo(final int type) {
		//LogUtil.i("momo", "NetUtil.sendGet");
		String  specialParam="v=2&reqType=appInfo&category=" + type;
		LogUtil.e("momo42", UrlUtil.URL_canyu+specialParam);
		NetUtil.sendGet(UrlUtil.URL_canyu, specialParam, new NetResult() {
			@Override
			public void result(final String msg) {
				// TODO Auto-generated method stub
				if (msg.equals("")) {
					activity.postDelayed(new Runnable() {
						@Override
						public void run() {
							setInfo(type);
						}
					}, 2000);
					return;
				}
				activity.post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("status") != 1) {
								((MainActivity) activity).showMessage(object.getString("msg"));
								return;
							}	
							JSONArray array = object.getJSONArray("msg");
							// ArrayList<View> list = new ArrayList<View>();
							for (int i = 0; i < array.length(); i++) {
								JSONObject obj = array.getJSONObject(i);
								LogUtil.e("momo41", obj.optString("name")+"    packageName=="+obj.optString("packageName"));
								OneItem item = new OneItem(obj.optInt("appType"), obj.getString("appId"),
										obj.getString("url"), obj.optString("appName"), obj.optString("packageName"),
										obj.optString("icon"), obj.optString("name"), obj.optString("version"),
										obj.optInt("type"), obj.optInt("useCount"), obj.optInt("level"),
										obj.optString("desc"));
								View view = getOneXinxi(item);
								initAppLine(view, i);
								list.add(view);
							}
							if (type == 1)
								/*
								 * 注意这里改动了 list.add(getOneXinxi(R.drawable.mood,
								 * "新闻热度/情绪", Constant.packageName +
								 * ".zhiku.tools.MoodActivity"));
								 */
								list.add(getOneXinxi(R.drawable.mood, "新闻热度/情绪",
										Constant.packageName + ".ui.activity.tools.MoodActivity"));
							// 刷新列表数据
							refresh(list);
							// myGridViewAdapter.notifyDataSetChanged();
							LogUtil.i("momo", "setInfo这里");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

			}
		});

	}
	/**
	 * 刷新列表数据
	 */
	/*
	 * private void refresh(ArrayList<View> list, int type) { if (list.size() ==
	 * 0) { return; } GridView view = (GridView) View.inflate(activity,
	 * R.layout.tljr_item_gridview_bigdata, null);
	 * view.setBackgroundColor(Color.parseColor("#fafafa"));
	 * view.setNumColumns(countNums); view.invalidate(); int num = list.size() /
	 * countNums + (list.size() % countNums == 0 ? 0 : 1); LayoutParams params =
	 * new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 * params.height = num * Util.WIDTH / countNums;
	 * view.setLayoutParams(params); setAdapter(view, list); View title =
	 * View.inflate(activity, R.layout.tljr_view_title, null); ((TextView)
	 * title.findViewById(R.id.tljr_view_title_name)) .setText(type == 1 ?
	 * "官方工具" : "社区工具"); // ((ImageView)
	 * title.findViewById(R.id.tljr_view_title_img)) //
	 * .setBackgroundResource(type == 1 ? R.drawable.img_hongtiao // :
	 * R.drawable.img_lvtiao); ((ImageView)
	 * title.findViewById(R.id.tljr_view_title_img)) .setVisibility(View.GONE);
	 * layout.addView(title); layout.addView(view); }
	 */

	
	private   void   showMessage(String    text){
		Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
	}
	boolean    isTest=true;
	
	/**
	 * 根据OneItem来获取控件
	 */
	public View getOneXinxi(final OneItem item) {
		final LinearLayout view = (LinearLayout) View.inflate(activity, R.layout.tljr_item_app, null);
		// view.setPadding(5, 5, 5, 5);
		// StartActivity.imageLoader.displayImage(item.getAvatar(),
		// ((ImageView) view.findViewById(R.id.tljr_img_app_icon)),
		// StartActivity.options);
		/*
		 * view.setLayoutParams(new AbsListView.LayoutParams(screenWith /
		 * countNums, screenWith / countNums));
		 */
		//view.setLayoutParams(new AbsListView.LayoutParams(screenWith / countNums, (int) (screenHight / 6.67f)));
		view.setLayoutParams(new AbsListView.LayoutParams(screenWith / countNums, (int) (screenHight / 6.0f)));
		
		Util.setImage(item.getAvatar(), ((ImageView) view.findViewById(R.id.tljr_img_app_icon)), activity.handler);
		((TextView) view.findViewById(R.id.tljr_txt_app_name)).setSingleLine(true);
		((TextView) view.findViewById(R.id.tljr_txt_app_name)).setText(item.getTitle());
		((TextView) view.findViewById(R.id.tljr_txt_app_users)).setText(item.getUsers() + "次使用");
		final View type = view.findViewById(R.id.tljr_img_app_type);
		final String apkName = item.getAppName() + ".apk";
		File file = new File(Util.fileDirPath + "/" + apkName);
		if (file.exists()) {
			PackageInfo info = DLUtils.getPackageInfo(activity, file.getAbsolutePath());
			if (Float.parseFloat(item.getVersion()) > Float.parseFloat(info.versionName)) {
				type.setBackgroundResource(R.drawable.img_xinbanben);
			} else {
				type.setVisibility(View.GONE);
			}
		}
	//	Log.i("momo", "setOnClickListener");
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			/*	if (isTest) {
					//工程里面的插件的例子Util.startApp(activity, "YGSM.apk");
					//测试
					//LogUtil.e("getPackageName", item.getPackageName());
				//	showMessage(item.getPackageName());
					
					Util.startApp(activity, "MA.apk");
					return;
				}
			*/
				
				
				File file = new File(Util.fileDirPath + "/" + apkName);
				if (file.exists()) {
					PackageInfo info = DLUtils.getPackageInfo(activity, file.getAbsolutePath());
					if (Float.parseFloat(item.getVersion()) > Float.parseFloat(info.versionName)) {
						//版本过时从新下载
						downLoadApk(item, apkName, type);
					} else {
						//版本没有过时，直接打开插件
						type.setVisibility(View.GONE);
						Util.lanchApp(activity, apkName);
						NetUtil.sendPost(UrlUtil.URL_canyu, "reqType=clickApp&appId=" + item.getId(), new NetResult() {
							@Override
							public void result(String arg0) {
								
							}
						});
					}
				} else {
					downLoadApk(item, apkName, type);
				}
//			    Util.start360App(activity,"ETF.apk");
				
//				try {
//					final File file = new File(Util.filePath + "/" +"ETF.apk");
//					PluginManager.getInstance().installPackage(file.getAbsolutePath(),0);
//					PackageManager pm = activity.getPackageManager();
//				    Intent intent = pm.getLaunchIntentForPackage("com.qh.turingfinance.etf.activity");
//				    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				    activity.startActivity(intent);
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
				
			}
		});
		return view;
	}

	private void downLoadApk(final OneItem item, final String apkName, final View type) {
		// 注意这里的Constent被momo该成了Constant
		LogUtil.i("momo", "Constant.netType.equals...——>"+Constant.netType.equals("WIFI"));
		if (!Constant.netType.equals("WIFI")){
			new AlertDialog.Builder(activity).setTitle("图灵金融").setMessage("当前为" + Constant.netType + "网络，下载会消耗流量，确认下载？")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//LogUtil.i("momo", "点击进入了downLoadApk");
							DownloadProUtil.showProgressDlg(item.getTitle(), item.getUrl(), apkName, activity,new Complete() {
								@Override
								public void complete() {
									type.setVisibility(View.GONE);
									File file = new File(Util.fileDirPath + "/" + apkName);
									PackageInfo info = DLUtils.getPackageInfo(activity, file.getAbsolutePath());
									item.setVersion(info.versionName);
								}
							});
						}
					}).setNegativeButton("否", null).show();

		} else {
			//显示进度条
//			LogUtil.e("momo", "MainTulingDataView--->jkl");
//			LogUtil.e("momo", "MainTulingDataView--->jkl");
			DownloadProUtil.showProgressDlg(item.getTitle(), item.getUrl(), apkName, activity, new Complete() {
				@Override
				public void complete() {
					type.setVisibility(View.GONE);
					File file = new File(Util.fileDirPath + "/" + apkName);
					PackageInfo info = DLUtils.getPackageInfo(activity, file.getAbsolutePath());
					if (info != null)
						item.setVersion(info.versionName);
				}
			});
		}

	}

	private boolean flag = true;

	/**
	 * 提供外部ScrollView滑倒底部的时候调用
	 */
	public void refreshScrollView() {
		if (flag) {
			setInfo(1);
			setInfo(2);
		}
		flag = false;
	}

}
