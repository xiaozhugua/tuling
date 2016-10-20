package com.abct.tljr.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.abct.tljr.MyApplication;
import com.abct.tljr.data.Constant;
import com.abct.tljr.utils.Util;

/**
 * @author xbw
 * @version 创建时间：2015-6-4 下午5:41:56 网络变化监听
 */
public class NetChangeReceiver extends BroadcastReceiver {
	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.v("网络变化", Constant.netType);
		if (Constant.netType.equals("未知")&&MyApplication.getInstance().getMainActivity() != null) {
			MyApplication.getInstance().getMainActivity().mHandler.sendEmptyMessage(1000);
		}
		Constant.netType = Util.getAPNType(MyApplication.getInstance());
	}

}
