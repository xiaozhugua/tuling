package com.abct.tljr.model;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.qh.common.listener.CommonMethod;
import com.qh.common.listener.Complete;
import com.qh.common.util.LogUtil;
import com.ryg.dynamicload.DLBasePluginActivity;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Common extends CommonMethod {

	@Override
	public void hideProgress() {
		// TODO Auto-generated method stub
		LogUtil.e("hideProgress", "hideProgress");
		ProgressDlgUtil.stopProgressDlg();
	}

	@Override
	public void login(boolean... noShowPrompt) {
		// TODO Auto-generated method stub
		LogUtil.e("login", "login");
		Toast.makeText(MyApplication.getInstance().getNowActivity(), "请先登录", Toast.LENGTH_SHORT).show();
//		if (noShowPrompt.length == 0) {
//			new PromptDialog(MyApplication.getInstance().getNowActivity(), "登录获得更多功能，是否立刻登录？", new Complete() {
//
//				@Override
//				public void complete() {
//					Intent intent = new Intent(MyApplication.getInstance().getNowActivity(), WXEntryActivity.class);
//					MyApplication.getInstance().startActivity(intent);
//					MyApplication.getInstance().getNowActivity().overridePendingTransition(R.anim.move_left_in_activity,
//							R.anim.move_right_out_activity);
//				}
//			}).show();
//			return;
//		}
//		MobclickAgent.onEvent(MyApplication.getInstance().getNowActivity(), "Login");
//		Intent intent = new Intent(MyApplication.getInstance().getNowActivity(), WXEntryActivity.class);
//		MyApplication.getInstance().startActivity(intent);
//		MyApplication.getInstance().getNowActivity().overridePendingTransition(R.anim.move_left_in_activity,
//				R.anim.move_right_out_activity);
	}

	@Override
	public void loginSuccess() {
		// TODO Auto-generated method stub
		System.err.println("loginSuccess");

	}

	@Override
	public void showProgress(String msg, Context context) {
		// TODO Auto-generated method stub
		LogUtil.e("showProgress", "showProgress");
//		ProgressDlgUtil.showProgressDlg(msg, MyApplication.getInstance().getNowActivity());
	}

}
