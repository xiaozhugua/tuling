package com.abct.tljr.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.login.Configs;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.model.User;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xbw
 * @version 创建时间：2015-6-2 上午11:04:17
 */
public class EnterBindSecurityDialog extends Dialog {
	private LinearLayout grp_confirm;
	private TextView lastTime;
	private TextView tv;
	private Button btn;
	private EditText pwd;
	private int time = 60;
	private Handler handler = new Handler(	);
	private TextView[] views = new TextView[6];
	private String orderId;
	private Context context;
	private String newPhone=null;
	
	public EnterBindSecurityDialog(final Context context, final String phone,String orderId, TextView tv,String newPhone) {
		super(context, R.style.dialog);
		this.orderId = orderId;
		this.context = context;
		this.tv = tv;
		this.newPhone=newPhone;
		setContentView(R.layout.occft_dialog_entersecurity);
		setCanceledOnTouchOutside(false);
		Window dialogWindow = getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (Util.WIDTH * 0.9); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(p);
		findViewById(R.id.dialog_exit).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						EnterBindSecurityDialog.this.dismiss();
					}
				});

		((TextView) findViewById(R.id.dialog_entersecurity_title))
				.setText("验证码已发往绑定手机" + phone + "，请注意查收"); 
		lastTime = (TextView) findViewById(R.id.dialog_entersecurity_tip);
		btn = (Button) findViewById(R.id.dialog_entersecurity_btn);
		grp_confirm = (LinearLayout) findViewById(R.id.tljr_grp_confirm);
		for (int i = 0; i < views.length; i++) {
			views[i] = new TextView(context);
			views[i].setTextColor(context.getResources().getColor(R.color.gray));
			views[i].setTextSize(30);
			views[i].setGravity(Gravity.CENTER);
			views[i].setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
			grp_confirm.addView(views[i]);
		}
		pwd = (EditText) findViewById(R.id.tljr_et_confirm);
		pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before,
					int count) {
				final String s = arg0.toString().trim();
				for (int i = 0; i < views.length; i++) {
					if (i < s.length()) {
						views[i].setText(s.charAt(i) + "");
					} else {
						views[i].setText("");
					}
				}
				if (arg0.length() == 6) {
					pwd.postDelayed(new Runnable() {
						@Override
						public void run() {
							pwd.setText("");
							ComfirmCode(EnterBindSecurityDialog.this.orderId, s);
						}
					}, 200);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String geturl = UrlUtil.URL_user + "bind?iou=1";
				String param = "token=" + Configs.token + "&type=phone"
						+ "&value=" + phone;
				HttpRequest.sendPost(geturl, param, new HttpRevMsg() {
					@Override
					public void revMsg(final String msg) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								try {
									JSONObject jsonObject = new JSONObject(msg);
									if (jsonObject.getInt("code") == 1) {
										EnterBindSecurityDialog.this.orderId = jsonObject.getString("result");
										time = 60;
										handler.post(runnable);
									} else {
										Toast.makeText(context, "获取失败，请重试",Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
			}
		});
		handler.post(runnable);
	}

	private void ComfirmCode(String id, String code) {
		String geturl = UrlUtil.URL_user + "bind/phoneverify?iou=1";
		String param = "user=" + MyApplication.getInstance().self.getBindeId()+ "&id=" + id + "&code=" + code;
		ProgressDlgUtil.showProgressDlg("验证中", (Activity) context);
		HttpRequest.sendPost(geturl, param, new HttpRevMsg() {
			@Override
			public void revMsg(final String msg) {
				ProgressDlgUtil.stopProgressDlg();
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject object = new JSONObject(msg);
							String code = object.getString("code");
							if (code.equals("1")) {
								Toast.makeText(context,object.getString("result"),Toast.LENGTH_SHORT).show();
								dismiss();
								tv.setText(dealPhone(newPhone)+" (已验证)");
								User.getUser().setPhone(dealPhone(newPhone));
								User.getUser().setInvailPhone(true);
								Util.ChangePhoneEmailFinish(context);
							} else {
								Toast.makeText(context,"验证码错误，请重试",Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	public String dealPhone(String phone){
		String first=phone.substring(0,3);
		String end=phone.substring(phone.length()-4,phone.length());
		return first+"****"+end;
	}
	
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (time > 0) {
				handler.postDelayed(runnable, 1000);
				time--;
				btn.setVisibility(View.GONE);
				lastTime.setVisibility(View.VISIBLE);
				lastTime.setText("重新获取(" + time + ")");
			} else {
				lastTime.setVisibility(View.GONE);
				btn.setVisibility(View.VISIBLE);
			}
		}
	};
	
	
}
