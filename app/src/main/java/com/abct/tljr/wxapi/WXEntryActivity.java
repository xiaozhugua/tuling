package com.abct.tljr.wxapi;

import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.BwManager;
import com.qh.common.login.Configs;
import com.qh.common.login.login.LoginCallBack;
import com.qh.common.login.util.ErrorCode;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WXEntryActivity extends BaseActivity implements LoginCallBack {
	private EditText name, pwd;

	private View scmm;
	private RelativeLayout rl_bottom;
	private TextView cancel, youxiang, phone;
	public static Activity other;
	public boolean LoginStatus=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BwManager.getInstance().initLogin(this);
		setContentView(R.layout.activity_login);
		initUI();
	}

	private void initUI() {
		rl_bottom = (RelativeLayout) findViewById(R.id.rl);
		cancel = (TextView) findViewById(R.id.cancel);
		youxiang = (TextView) findViewById(R.id.youxiang);
		phone = (TextView) findViewById(R.id.phone);
		rl_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				arg0.setVisibility(View.GONE);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				rl_bottom.setVisibility(View.GONE);
			}
		});
		youxiang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				findpwd(true);
				rl_bottom.setVisibility(View.GONE);
			}
		});
		phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				findpwd(false);
				rl_bottom.setVisibility(View.GONE);
			}
		});
		findViewById(R.id.tljr_txt_wjmm).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wanJiMiMa();
			}
		});

		findViewById(R.id.tljr_btn_lfanhui).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.tljr_img_lweixin).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProgressDlgUtil.showProgressDlg("", WXEntryActivity.this);
				other = WXEntryActivity.this;
				BwManager.getInstance().loginWeiXin();
			}
		});
		findViewById(R.id.tljr_img_lqq).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProgressDlgUtil.showProgressDlg("", WXEntryActivity.this);
				BwManager.getInstance().loginQQ();
			}
		});
		findViewById(R.id.tljr_img_lweibo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProgressDlgUtil.showProgressDlg("", WXEntryActivity.this);
				BwManager.getInstance().loginSina();
			}
		});
		findViewById(R.id.tljr_img_regiest).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WXEntryActivity.this, RegiestActivity.class);
				startActivity(intent);
				finish();
			}
		});
		name = (EditText) findViewById(R.id.tljr_et_lname);
		pwd = (EditText) findViewById(R.id.tljr_et_lpwd);
		scmm = findViewById(R.id.tljr_img_scmm);
		pwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0) {
					scmm.setVisibility(View.VISIBLE);
				} else {
					scmm.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		scmm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pwd.setText("");
				scmm.setVisibility(View.GONE);
			}
		});
		findViewById(R.id.tljr_btn_ldenglu).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (name.getText().toString().trim().equals("") || pwd.getText().toString().trim().equals("")) {
					showToast("请输入用户名或密码");
				} else {
					if(LoginStatus==true){
						LoginStatus=false;
						ProgressDlgUtil.showProgressDlg("", WXEntryActivity.this);
						BwManager.getInstance().loginQh(name.getText().toString().trim(), pwd.getText().toString().trim(),
								getResources().getString(R.string.app_name));
					}else{
						Toast.makeText(WXEntryActivity.this,"正在登陆中",Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		if (PreferenceUtils.getInstance().getUserName() != null) {
			name.setText(PreferenceUtils.getInstance().getUserName());
		}

	}

	private JSONObject objfindPwd;

	protected void wanJiMiMa() {
		final String userName = name.getText().toString().trim();
		final String method = "email";
		if (userName.length() <= 0) {
			showToast("请输入你要找回密码的图灵帐号");
			name.requestFocus();
			return;
		}
		ProgressDlgUtil.showProgressDlg("", this);
		LogUtil.e("Text", "url :" + UrlUtil.URL_getbindinfo + "&uname=" + userName);
		NetUtil.sendGet(UrlUtil.URL_getbindinfo + "&uname=" + userName, new NetResult() {
			@Override
			public void result(String msg) {
				try{
					LogUtil.e("Text", "msg :" + msg);
					ProgressDlgUtil.stopProgressDlg();
					if (msg.length() == 0) {
						showToast("请求失败,请检查网络重试");
						return;
					}
					JSONObject result =new JSONObject(msg);
					int code = result.optInt("code");
					if (code == 1) {
						JSONObject obj = result.getJSONObject("result");
						objfindPwd = result.getJSONObject("result");
						if (obj.has("email") && obj.has("phone")) {
							if (obj.getBoolean("emailVerify") && obj.getBoolean("phoneVerify")) {
								rl_bottom.setVisibility(View.VISIBLE);
							} else if (obj.getBoolean("emailVerify") && !obj.getBoolean("phoneVerify")) {
								findpwd(true);
							} else if (!obj.getBoolean("emailVerify") && obj.getBoolean("phoneVerify")) {
								findpwd(false);
							} else {
								// 未验证
								new PromptDialog(WXEntryActivity.this, "您的邮箱和手机皆未通过验证,无法找回密码", new Complete() {
									@Override
									public void complete(){}
								}).showNoCancel();
							}
						} else if (obj.has("email")) {
							findpwd(true);
						} else if (obj.has("phone")) {
							findpwd(false);
						} else {
							new PromptDialog(WXEntryActivity.this, "未绑定邮箱、手机或者三方登录,如需找回密码,请联系客服!", new Complete() {
								@Override
								public void complete() {

								}
							}).showNoCancel();
						}
					} else {
						showToast(ErrorCode.getErrorMessage(code));
					}
				}catch(Exception e){
					LogUtil.e("Exception",e.getMessage());
				}
			}
		});

	}

	public void findpwd(boolean isemail) {
		try{
			if (isemail) {
				String email = objfindPwd.getString("email");
				if (objfindPwd.getBoolean("emailVerify")) {
					Intent intent = new Intent(WXEntryActivity.this, FindPassword.class);
					intent.putExtra("userName", name.getText().toString().trim());
					intent.putExtra("method", "email");
					intent.putExtra("email", email);
					startActivity(intent);
				} else {
					// 未验证
					showDialog("您的邮箱未通过验证,无法找回密码");
				}
			} else {
				String phone = objfindPwd.getString("phone");
				if (objfindPwd.getBoolean("phoneVerify")) {
					Intent intent = new Intent(WXEntryActivity.this, FindPassword.class);
					intent.putExtra("userName", name.getText().toString().trim());
					intent.putExtra("method", "phone");
					intent.putExtra("phone", phone);
					startActivity(intent);
				} else {
					// 未验证
					showDialog("您的手机未通过验证,无法找回密码");
				}
			}
		}catch(Exception e){
			
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		BwManager.getInstance().onActivityResult(requestCode, resultCode, data);
	}

	public void showDialog(final String msg) {
		// post(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		AlertDialog.Builder builder = new AlertDialog.Builder(WXEntryActivity.this);
		builder.setTitle("提示");
		builder.setMessage(msg);
		builder.setNegativeButton("确定", null);
		builder.show();
	}

	@Override
	public void login(String s) {
		try{
			Thread.sleep(500);
			LoginStatus=true;
		}catch(Exception e){
			
		}
		Intent intent = new Intent(Configs.serviceName);
		intent.putExtra("type", "login");
		intent.putExtra("msg", s);
		sendBroadcast(intent);
		ProgressDlgUtil.stopProgressDlg();
		finish();
		if (other != null)
			other.finish();
	}

	@Override
	public void logout() {
		Intent intent = new Intent(Configs.serviceName);
		intent.putExtra("type", "logout");
		sendBroadcast(intent);
		finish();
	}

	@Override
	public void error(String s) {
		try{
			Thread.sleep(500);
			LoginStatus=true;
		}catch(Exception e){
		}
		showToast(s);
		ProgressDlgUtil.stopProgressDlg();
	}
}
