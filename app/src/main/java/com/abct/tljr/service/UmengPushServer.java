package com.abct.tljr.service;

import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.abct.tljr.MyApplication;
import com.abct.tljr.data.Constant;
import com.abct.tljr.dialog.ReceiveDialog;

/**
 * @author xbw
 * @version 创建时间：2015-6-3 上午10:21:09
 */
public class UmengPushServer implements Runnable {
	MyApplication application;
	ReceiveDialog dialog;
	Handler handler = new Handler();

	public UmengPushServer(MyApplication application) {
		this.application = application;
		new Thread(this).start();
	}

	@Override
	public void run() {
		handler.postDelayed(this, 1000);
		if (!MyApplication.getInstance().receiver.isNull()&& null != getNowActivity()
				&& !getNowActivity().getClass().getName().equals(Constant.packageName + ".StartActivity")
				&& (dialog == null || !dialog.isShowing())) {
			dialog = new ReceiveDialog(getNowActivity(),
					(Intent) MyApplication.getInstance().receiver.intents.pop());
			dialog.show();
		}
	}

	public String getTopActivity() {
		ActivityManager activityManager = (ActivityManager) 
				application.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
		RunningTaskInfo currentActivity;
		currentActivity = forGroundActivity.get(0);
		return currentActivity.topActivity.getClassName();
	}

	public Activity getNowActivity() {
		return MyApplication.getInstance().activityMap.get(getTopActivity());
	}

}
