package com.abct.tljr.service;

import java.util.List;

import com.abct.tljr.MyApplication;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.util.LogUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * 
 * @author xbw 程序后台一段时间自动关掉程序
 */
public class KillService extends Service {
	private ActivityManager activityManager;
	private String packageName;
	private boolean flag = true;
	private int i = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (thread == null) {
			activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
			packageName = this.getPackageName();
			thread = new MyThread();
			new Thread(thread).start();
			IntentFilter filter = new IntentFilter();
			filter.addAction("screenOn");
			registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					flag = true;
				}
			}, filter);
			IntentFilter filter1 = new IntentFilter();
			filter1.addAction("screenOFF");
			registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					flag = false;
				}
			}, filter1);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	MyThread thread;

	class MyThread implements Runnable {
		public void run() {
			try {
				while (true) {
					Thread.sleep(1000);
					if (!isAppOnForeground() || !flag) {
						i += 1;
						if (i > 60 || Util.WIDTH == 0 || MainActivity.activity == null) {
							LogUtil.e("退出程序", "退出");
							LoginResultReceiver.sendOnlineStop();
							MyApplication.getInstance().exit();
							android.os.Process.killProcess(android.os.Process.myPid());
							System.exit(0);
						}
					} else {
						i = 0;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

}
