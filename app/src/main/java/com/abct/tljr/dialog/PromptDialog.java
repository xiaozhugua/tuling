package com.abct.tljr.dialog;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.service.LoginResultReceiver;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015-6-2 上午11:04:17
 */
public class PromptDialog extends Dialog implements OnClickListener {
	private Context context;
	private Complete complete;
	private Complete cancelComplete;
	private NetResult msg;
	private EditText et;

	public PromptDialog(Context context) {
		super(context, R.style.dialog);
		init("确定退出图灵金融 ？");
	}

	public PromptDialog(Context context, String msg, Complete complete) {
		super(context, R.style.dialog);
		this.complete = complete;
		init(msg);
	}

	public PromptDialog(Context context, String msg, Complete complete, Complete cancelComplete) {
		super(context, R.style.dialog);
		this.complete = complete;
		this.cancelComplete = cancelComplete;
		init(msg);
	}

	private void init(String msg) {
		setContentView(R.layout.tljr_dialog_exit);
		setCanceledOnTouchOutside(false);
		Window dialogWindow = getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (Util.WIDTH * 0.8);
		p.height = p.width * 2 / 3;
		dialogWindow.setAttributes(p);
		((TextView) findViewById(R.id.tljr_dialog_exit_msg)).setText(msg);
		et = (EditText) findViewById(R.id.tljr_dialog_exit_et);
		findViewById(R.id.tljr_dialog_exit_cancel).setOnClickListener(this);
		findViewById(R.id.tljr_dialog_exit_ok).setOnClickListener(this);
		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (cancelComplete != null) {
					cancelComplete.complete();
				}
			}
		});
	}

	public void showNoCancel() {
		findViewById(R.id.tljr_dialog_exit_cancel).setVisibility(View.GONE);
		findViewById(R.id.tljr_dialog_exit_fgx).setVisibility(View.GONE);
		show();
	}

	public void showEdit(String title, NetResult revMsg) {
		((TextView) findViewById(R.id.tljr_txt_share_title)).setText(title);
		findViewById(R.id.tljr_dialog_exit_msg).setVisibility(View.GONE);
		et.setVisibility(View.VISIBLE);
		this.msg = revMsg;
		show();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tljr_dialog_exit_cancel:
			if (cancelComplete != null) {
				cancelComplete.complete();
			}
			this.dismiss();
			break;
		case R.id.tljr_dialog_exit_ok:
			this.dismiss();
			if (complete != null) {
				complete.complete();
				return;
			}
			if (msg != null) {
				msg.result(et.getText().toString());
				return;
			}
			LoginResultReceiver.sendOnlineStop();
			try {
				Constant.dataStorage();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (context instanceof Activity) {
				((Activity) context).finish();
				MobclickAgent.onKillProcess(context);
			}
			// if (Warn.needpublicWarn) {
			// Constant.preference.edit().putBoolean("Mainislive", false)
			// .commit();
			// MyApplication.getInstance().startService(
			// new Intent(MyApplication.getInstance(),
			// WarnService.class));
			// }
			MyApplication.getInstance().exit();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
			break;

		default:
			break;
		}
	}
}
