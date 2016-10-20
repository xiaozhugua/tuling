package com.abct.tljr;

import com.abct.tljr.data.Constant;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.wxapi.WXEntryActivity;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.qh.common.listener.Complete;
import com.qh.common.util.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * momo用于onegu页面
 */
public class MySuperBaseActivity extends AppCompatActivity {
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			handleMsg(msg);
		}
	};

	public void handleMsg(Message msg) {
	}

	public void post(Runnable r) {
		if (handler != null)
			handler.post(r);
	}

	public void postDelayed(final Runnable r, long time) {
		if (handler != null)
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (handler != null)
						r.run();
				}
			}, time);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		MyApplication.onActivityCreateSetTheme(this);
		super.onCreate(arg0);
		MyApplication.getInstance().addActivity(this);
		initSystemBar();
		Constant.addClickCount();
	}

	public void initSystemBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(false);
			// tintManager.setStatusBarTintColor(MyApplication.StatusBarColor);
		}
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();

		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	

	public void showToast(final String msg) {
		post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void login(boolean... noShowPrompt) {
		if (noShowPrompt!=null&&noShowPrompt.length == 0) {
			new PromptDialog(this, "登录获得更多功能，是否立刻登录？", new Complete() {
				@Override
				public void complete() {
					MobclickAgent.onEvent(MySuperBaseActivity.this, "Login");
					Intent intent = new Intent(MySuperBaseActivity.this, WXEntryActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
				}
			}).show();
			return;
		}
		MobclickAgent.onEvent(MySuperBaseActivity.this, "Login");
		Intent intent = new Intent(MySuperBaseActivity.this,WXEntryActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().removeActivity(this);
		View v = new View(this);
		setContentView(v);
		super.onDestroy();
		v = null;
		handler = null;
		//PgyFeedbackShakeManager.unregister();
		//ProgressDlgUtil.stopProgressDlg();
	}

}
