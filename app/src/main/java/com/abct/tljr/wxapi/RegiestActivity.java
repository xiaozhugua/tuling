package com.abct.tljr.wxapi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegiestActivity extends BaseActivity implements OnClickListener {
	EditText et_regiest_phone;
	Button btn_regiest_regiest;
	TextView txt_login_regiest, txt_login_coderegiest, txt_regiest_normal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_regiest);
		et_regiest_phone = (EditText) findViewById(R.id.tljr_et_regiest_phone);
		btn_regiest_regiest = (Button) findViewById(R.id.tljr_btn_regiest_regiest);
		txt_regiest_normal = (TextView) findViewById(R.id.tljr_txt_regiest_normal);
		findViewById(R.id.tljr_img_regiest_back).setOnClickListener(this);
		btn_regiest_regiest.setOnClickListener(this);
		txt_regiest_normal.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tljr_img_regiest_back:// 返回
			finish();
			break;
		case R.id.tljr_txt_regiest_normal:// 下一步
			Intent intent = new Intent(RegiestActivity.this,RegisterActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.tljr_btn_regiest_regiest:// 下一步
			sendRegiest();
			break;

		default:
			break;
		}
	}

	private void sendRegiest() {
		String phone = et_regiest_phone.getText().toString().trim();
		if (phone.length() == 0) {
			showToast("请输入手机号码");
			return;
		}
		if (!isValidMobile(phone)) {
			showToast("请输入正确的手机号码");
			return;
		}
		ProgressDlgUtil.showProgressDlg("", RegiestActivity.this);
		isExist(phone);
	}

	private boolean isValidMobile(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();

	}

	private void isExist(final String phone) {
		NetUtil.sendPost(UrlUtil.URL_isexist, "uname=" + phone
				+ "&platform=PHONE", new NetResult() {
			@Override
			public void result(String msg) {
				try {
					JSONObject object = new JSONObject(msg);
					if (object.optInt("code") == 1) {
						showError(0);
						ProgressDlgUtil.stopProgressDlg();
					} else {
						getKey(phone);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					ProgressDlgUtil.stopProgressDlg();
					showError(1);
				}
			}
		});
	}

	private void getKey(final String phone) {
		NetUtil.sendPost(UrlUtil.URL_sendsms, "phone=" + phone,
				new NetResult() {

					@Override
					public void result(String msg) {
						ProgressDlgUtil.stopProgressDlg();
						// {"code":1,"result":"556912bfe224548e3a000006"}
						// {"code":""}
						try {
							JSONObject object = new JSONObject(msg);
							if (object.optInt("code") == 1) {
								String key = object.optString("result");
								Intent intent = new Intent(RegiestActivity.this,ConfirmCodeActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("key", key);
								bundle.putString("phone", phone);
								intent.putExtras(bundle);
								startActivity(intent);
								finish();
							} else {
								showError(object.optInt("code"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showError(0);
						}
					}
				});
	}

	private void showError(int code) {
		String msg = "";
		switch (code) {
		case 1:
			msg = "手机注册失败";
			break;
		case -1021:
			msg = "短信未找到";
			break;
		case -1022:
			msg = "发送短信失败";
			break;
		default:
			msg = "用户名已存在";
			break;
		}
		showToast(msg);
	}
}
