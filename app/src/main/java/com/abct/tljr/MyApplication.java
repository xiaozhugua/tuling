package com.abct.tljr;

import java.util.Iterator;
import java.util.Map.Entry;
import com.abct.tljr.data.Constant;
import com.abct.tljr.gt.GTApp;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.Common;
import com.abct.tljr.model.Options;
import com.abct.tljr.service.KillService;
import com.abct.tljr.service.UmengPushReceiver;
import com.abct.tljr.service.UmengPushServer;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.CrashHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.pgyersdk.crash.PgyCrashManager;
import com.qh.common.CommonApplication;
import com.qh.common.login.BwManager;
import com.qh.common.volley.RequestQueue;
import com.qh.common.volley.toolbox.Volley;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.Handler;
import android.util.Log;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class MyApplication extends CommonApplication {
	public static ImageLoader imageLoader;
	public static DisplayImageOptions options;
	public static boolean isupdate = false;
	private static MyApplication instance;
	public PushAgent mPushAgent;
	public UmengPushReceiver receiver;
	public UmengPushServer umengPushServer;
	public static RequestQueue requestQueue;

	private GTApp gt;

	@Override
	public void onCreate() {
		super.onCreate();
		CommonApplication.StatusBarColor = R.drawable.img_biaotibeijing;
		common.addCommon(getPackageName(), new Common());
		BwManager.getInstance().init(this).setQQId("1104538121").setWxId("wx5a0730d6cfb207bf").setSinaId("3059335908");
		instance = this;
		requestQueue = Volley.newRequestQueue(this);
		// 设置默认的Reanlm配置
		RealmConfiguration realmconfig = new RealmConfiguration.Builder(this).name("tljr_Default.realm").build();
		try {
			Realm.setDefaultConfiguration(realmconfig);
		} catch (RealmMigrationNeededException e) {
			try {
				Realm.deleteRealm(realmconfig);
				// Realm file has been deleted.
				Realm.setDefaultConfiguration(realmconfig);
			} catch (Exception ex) {
				// No Realm file to remove.
			}
		}
		// Realm.setDefaultConfiguration(realmconfig);
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		PgyCrashManager.register(this);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			Constant.appVersion = pi.versionName;
			Constant.packageName = getPackageName();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		StartActivity.imageLoader = ImageLoader.getInstance();
		StartActivity.options = Options.getListOptions();
		imageLoader = StartActivity.imageLoader;
		options = StartActivity.options;
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.memoryCacheExtraOptions(480, 800)
				// max width, max height，即保存的每个缓存文件的最大长宽
				// .discCacheExtraOptions(480, 800, CompressFormat.PNG, 75,
				// null)
				// Can slow ImageLoader, use it carefully (Better don't use
				// it)/设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(2)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 3).denyCacheImageMultipleSizesInMemory()
				// .memoryCache(new WeakMemoryCache())
				.memoryCache(new LruMemoryCache(8 * 1024 * 1024))

				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(9 * 1024 * 1024).discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .discCacheFileCount(200)
				// 缓存的文件数量
				// .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
				.imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout
																										// (5
																										// s),
																										// readTimeout
																										// (30
																										// s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();// 开始构建

		StartActivity.imageLoader.init(config);

		// StartActivity.imageLoader.init(ImageLoaderConfiguration
		// .createDefault(getApplicationContext()));
		mPushAgent = PushAgent.getInstance(this);
		// mPushAgent.setDebugMode(true);// 发布时删除该语句
		mPushAgent.setMuteDurationSeconds(1);
		/**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
		 */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
			@Override
			public void handleMessage(Context arg0, final UMessage msg) {
				// TODO Auto-generated method stub
				new Handler(getMainLooper()).postDelayed(new Runnable() {

					@Override
					public void run() {
						Intent intent = new Intent("android.intent.action.umengPushReceiver");
						intent.putExtra("msg", msg.extra.toString());

						for (Entry<String, String> entry : msg.extra.entrySet()) {
							String key = entry.getKey();
							String value = entry.getValue();
							if (key.equals("type")) {
								Log.i("type", "custom-mpush:" + key);
								intent.putExtra("text", msg.text);
								intent.putExtra("title", msg.title);
								intent.putExtra("msg", msg.extra.get("info"));
								intent.putExtra("type", msg.extra.get("type"));
							} else {
								Log.i("mpush", "custom-mpush:" + key);
								intent.putExtra("mymsg", value);
							}
							// intent.putExtra("data", value);
						}

						sendBroadcast(intent);

					}
				}, 300);
				super.handleMessage(arg0, msg);
			}

			@Override
			public void autoUpdate(Context arg0, UMessage arg1) {
				// TODO Auto-generated method stub
				super.autoUpdate(arg0, arg1);
			}

			@Override
			public void openActivity(Context arg0, UMessage arg1) {
				// TODO Auto-generated method stub
				super.openActivity(arg0, arg1);
			}

			@Override
			public void dismissNotification(Context arg0, UMessage arg1) {
				// TODO Auto-generated method stub
				super.dismissNotification(arg0, arg1);
			}

			@Override
			public void openUrl(Context arg0, UMessage arg1) {
				// TODO Auto-generated method stub
				super.openUrl(arg0, arg1);
			}

			@Override
			public void launchApp(Context arg0, UMessage arg1) {
				// TODO Auto-generated method stub
				// super.launchApp(arg0, arg1);
			}

			@Override
			public void dealWithCustomAction(Context context, final UMessage msg) {
				// TODO Auto-generated method stub
				super.dealWithCustomAction(context, msg);
			}

			//
			// @Override
			// public void handleMessage(Context context, UMessage msg) {
			// // TODO Auto-generated method stub
			// super.handleMessage(context, msg);
			// Intent intent = new Intent(
			// "android.intent.action.umengPushReceiver");
			// intent.putExtra("text", msg.text);
			// intent.putExtra("title", msg.title);
			// intent.putExtra("msg", msg.extra.get("info"));
			// intent.putExtra("type", msg.extra.get("type"));
			// sendBroadcast(intent);
			// }
		};
		mPushAgent.setNotificationClickHandler(notificationClickHandler);

		// UmengMessageHandler messageHandler = new UmengMessageHandler() {
		//
		// @Override
		// public void dealWithNotificationMessage(Context context, final
		// UMessage msg) {
		// // TODO Auto-generated method stub
		// super.dealWithNotificationMessage(context, msg);
		// new Handler(getMainLooper()).postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// Intent intent = new Intent(
		// "android.intent.action.umengPushReceiver");
		// intent.putExtra("msg",msg.extra.toString() );
		//
		// for (Entry<String, String> entry : msg.extra.entrySet())
		// {
		// String key = entry.getKey();
		// String value = entry.getValue();
		// if (key!=null){
		// Log.i("mpush", "custom-mpush:"+key);
		// intent.putExtra("mymsg", value);
		// }
		// // intent.putExtra("data", value);
		// }
		//
		// sendBroadcast(intent);
		//
		// }
		// }, 300);
		//
		// }
		//
		// @Override
		// public void dealWithCustomMessage(Context context, final UMessage
		// msg) {
		// // TODO Auto-generated method stub
		// super.dealWithCustomMessage(context, msg);
		//
		//
		// }
		//
		// // @Override
		// // public void handleMessage(Context context, UMessage msg) {
		// // // TODO Auto-generated method stub
		// // super.handleMessage(context, msg);
		// // System.err.println("handleMessage:" + msg.text);
		// // System.err.println("handleMessage:" + msg.title);
		// // System.err.println("handleMessage:" + msg.extra.toString());
		// // Intent intent = new Intent(
		// // "android.intent.action.umengPushReceiver");
		// // intent.putExtra("text", msg.text);
		// // intent.putExtra("title", msg.title);
		// // intent.putExtra("msg", msg.extra.get("info"));
		// // intent.putExtra("type", msg.extra.get("type"));// share:分享才处理
		// // sendBroadcast(intent);
		// // }
		// };
		// mPushAgent.setMessageHandler(messageHandler);

		receiver = new UmengPushReceiver(this);
		umengPushServer = new UmengPushServer(this);
		registerReceiver(receiver3, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

		gt = new GTApp();
		gt.start();
		gt.setRunning(true);
	}

	BroadcastReceiver receiver3 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra("reason");
				if (reason != null) {
					if (reason.equals("homekey")) {
						Intent intent1 = new Intent(MyApplication.getInstance(), KillService.class);
						MyApplication.getInstance().startService(intent1);
					} else if (reason.equals("recentapps")) {
					}
				}
			}
		}
	};

	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	public MainActivity getMainActivity() {
		return (MainActivity) activityMap.get(Constant.packageName + ".main.MainActivity");
	}

	@Override
	public Activity getNowActivity() {
		// TODO Auto-generated method stub
		try {
			return super.getNowActivity();
		} catch (Exception e) {
			return getMainActivity();
		}
	}

	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityMap.values()) {
			activity.finish();
			activity = null;
		}
		activityMap.clear();
		gt.setRunning(false);
	}

	public void closeOtherActivity() {
		Iterator<String> it = activityMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!key.equals(Constant.packageName + ".main.MainActivity")) {
				activityMap.get(key).finish();
			}
		}
	}

	public static long all = 0;
	private ApplicationInfo ai;

	public void getUidByte() {
		try {
			if (ai == null) {
				PackageManager pm = getPackageManager();
				ai = pm.getApplicationInfo(Constant.packageName, PackageManager.GET_ACTIVITIES);
			}
			if (all == 0) {
				all = TrafficStats.getUidRxBytes(ai.uid) + TrafficStats.getUidTxBytes(ai.uid);
			}
			Constant.Liuliang = (TrafficStats.getUidRxBytes(ai.uid) + TrafficStats.getUidTxBytes(ai.uid) - all) / 1024;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void resultandLogin() {
		((MainActivity) activityMap.get(Constant.packageName + ".main.MainActivity")).mHandler.sendEmptyMessage(201);
	}

	private static int sTheme = 1;

	public final static int THEME_DEFAULT = 0;
	public final static int THEME_RED = 1;
	public final static int THEME_BLUE = 2;
	public static int StatusBarColor;

	public static void changeToTheme(Activity activity, int theme) {
		sTheme = theme;
		activity.finish();
		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	@SuppressWarnings("deprecation")
	public static void onActivityCreateSetTheme(Activity activity) {
		switch (sTheme) {
		case THEME_DEFAULT:
			StatusBarColor = activity.getResources().getColor(R.color.tljr_balck);
			activity.setTheme(R.style.MyThemeDefault);
			break;
		case THEME_RED:
			StatusBarColor = activity.getResources().getColor(R.color.redtitlebj);
			activity.setTheme(R.style.MyTheme);
			break;
		default:
			break;
		}
	}

}