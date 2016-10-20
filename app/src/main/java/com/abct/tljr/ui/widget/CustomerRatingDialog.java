package com.abct.tljr.ui.widget;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.RatingBar.OnRatingChangeListener;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.InputTools;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;

public class CustomerRatingDialog extends Dialog implements OnClickListener {
	private EditText et;
	private Context activity;
	private String url;
	private String parms;
	private Complete complete;
	private Handler handler = new Handler();
	private long PUBLISH_COMMENT_TIME;

	private RatingBar ratingBar;
	private int currentStar=0;
	public CustomerRatingDialog(Context activity, String url, String parms, Complete complete) {
		super(activity, R.style.dialog);
		this.activity = activity;
		this.url = url;
		this.parms = parms;
		this.complete = complete;
		setContentView(R.layout.tljr_dialog_rating_speak);
		setCanceledOnTouchOutside(false);
		init();
		windowDeploy();
		
	}

	// 设置窗口显示
	public void windowDeploy() {
		Window win = getWindow(); // 得到对话框
		win.setWindowAnimations(R.style.speakdialog_bottom); // 设置窗口弹出动画
	//	win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		win.getDecorView().setPadding(0, 0, 0, 0); // 宽度占满，因为style里面本身带有padding
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.FILL_PARENT;
		win.setAttributes(lp);

	}

	private void init() {

		ratingBar = (RatingBar) findViewById(R.id.ratingbar);
		
		ratingBar.setOnRatingChangeListener(new OnRatingChangeListener() {
			@Override
			public void onRatingChange(int var1) {
				// TODO Auto-generated method stub
				LogUtil.i("star", "星星 ："+var1);
				currentStar =var1;
			}
		});
		findViewById(R.id.tljr_img_speak_fanhui).setOnClickListener(this);
		findViewById(R.id.tljr_btn_speak).setOnClickListener(this);
		et = (EditText) findViewById(R.id.tljr_et_speak_msg);
		// et.setFocusable(true);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				InputTools.ShowKeyboard(et);
			}
		}, 400);

	}

	private void speak() {
		if (System.currentTimeMillis() - PUBLISH_COMMENT_TIME < 15 * 1000) { // 15秒发言时间间隔
			showToast("太快了，休息下吧");
			return;
		}
		String s = et.getText().toString().trim();
		if (s.equals("")) {
			showToast("请输入评论内容");
			return;
		}
		if (MyApplication.getInstance().self == null) {
			showToast("请先登录");
			return;
		}
		parms += ("&msg=" + s);
		if(currentStar!=0){
			parms += ("&flower="+currentStar);
		}
		LogUtil.e("Speak", url + "?" + parms);
		ProgressDlgUtil.showProgressDlg("", (Activity) activity);
		NetUtil.sendPost(url, parms, new NetResult() {
			@Override
			public void result(String msg) {
				ProgressDlgUtil.stopProgressDlg();
				LogUtil.e("Speak", msg);
				try {
					JSONObject object = new JSONObject(msg);
					showToast(object.getString("msg"));
					dismiss();
					InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					PUBLISH_COMMENT_TIME = System.currentTimeMillis();
					if (complete != null)
						complete.complete();
				} catch (JSONException e) {
					e.printStackTrace();
					showToast("发送失败，请稍后再试");
				}
			}
		});
	}

	private void showToast(String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tljr_img_speak_fanhui:
			InputTools.HideKeyboard(et);
			this.dismiss();
			break;
		case R.id.tljr_btn_speak:
			speak();
			break;
		default:
			break;
		}
	}

}
