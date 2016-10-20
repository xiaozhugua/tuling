package com.abct.tljr.wxapi;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfirmCodeActivity extends BaseActivity implements OnClickListener {
	private SmsObserver smsObserver;
	private Uri SMS_INBOX = Uri.parse("content://sms/");
	String key, phone;
	EditText et_confirm;
	TextView txt_confirm_phone, txt_confirm;
	int num = 60;
	TextView[] views = new TextView[6];
	LinearLayout grp_confirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		key = getIntent().getExtras().getString("key");
		phone = getIntent().getExtras().getString("phone");
		setContentView(R.layout.tljr_activity_confirmcode);
		grp_confirm = (LinearLayout) findViewById(R.id.tljr_grp_confirm);
		et_confirm = (EditText) findViewById(R.id.tljr_et_confirm);
		txt_confirm_phone = (TextView) findViewById(R.id.tljr_txt_confirm_phone);
		txt_confirm = (TextView) findViewById(R.id.tljr_txt_confirm);
		DecimalFormat mFormat = new DecimalFormat();
		mFormat.setGroupingSize(4);
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		dfs.setGroupingSeparator('-');
		mFormat.setDecimalFormatSymbols(dfs);
		txt_confirm_phone.setText("+86 " + mFormat.format(Long.parseLong(phone)));
		findViewById(R.id.tljr_img_confirm_back).setOnClickListener(this);
		post(runnable);
		for (int i = 0; i < views.length; i++) {
			views[i] = new TextView(this);
			views[i].setTextColor(getResources().getColor(R.color.gray));
			views[i].setTextSize(30);
			views[i].setGravity(Gravity.CENTER);
			views[i].setLayoutParams(
					new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
			grp_confirm.addView(views[i]);
		}
		et_confirm.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				String s = arg0.toString().trim();
				for (int i = 0; i < views.length; i++) {
					if (i < s.length()) {
						views[i].setText(s.charAt(i) + "");
					} else {
						views[i].setText("");
					}
				}
				if (arg0.length() == 6) {
					comfirmCode(arg0.toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
		smsObserver = new SmsObserver(this, handler);
		getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);
	}

	public void getSmsFromPhone() {
		try {
			ContentResolver cr = getContentResolver();
			String[] projection = new String[] { "body", "address", "person" };// "_id",
																				// "address",
			// "person",, "date",
			// "type
			String where = " date >  " + (System.currentTimeMillis() - 10 * 60 * 1000);
			Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
			if (null == cur)
				return;
			if (cur.moveToNext()) {
				String number = cur.getString(cur.getColumnIndex("address"));// 手机号
				String name = cur.getString(cur.getColumnIndex("person"));// 联系人姓名列表
				String body = cur.getString(cur.getColumnIndex("body"));

				final String s = getDynamicPassword(body);
				for (int i = 0; i < views.length; i++) {
					if (i < s.length()) {
						views[i].setText(s.charAt(i) + "");
					} else {
						views[i].setText("");
					}
				}
				postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						et_confirm.setText(s);
					}
				}, 500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void comfirmCode(final String code) {
		NetUtil.sendPost(UrlUtil.URL_matchcode, "code=" + code + "&id=" + key + "&method=regist", new NetResult() {

			@Override
			public void result(String msg) {
				// TODO Auto-generated method stub
				ProgressDlgUtil.stopProgressDlg();
				// {"code":"-1021"}
				// {"code":1,"result":"55691a10e224548e3a00000c"}
				try {
					JSONObject object = new JSONObject(msg);
					if (object.optInt("code") == 1) {
						String key = object.optString("result");
						Intent intent = new Intent(ConfirmCodeActivity.this, RegiestLastActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("key", key);
						bundle.putString("code", code);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					} else {
						showError(object.optInt("code"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showError(0);
				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tljr_img_confirm_back:// 返回
			Intent intent = new Intent(ConfirmCodeActivity.this, RegiestActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
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
			msg = "验证码验证失败";
			break;
		}
		showToast(msg);
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			postDelayed(runnable, 1000);
			if (num > 0) {
				num--;
				post(new Runnable() {

					@Override
					public void run() {
						if (num > 0) {
							txt_confirm.setText("接收短信大约需要" + num + "秒");
						} else {
							txt_confirm.setText("接收短信超时，请重试");
						}
					}
				});
			}
		}
	};

	/**
	 * 从字符串中截取连续6位数字 用于从短信中获取动态密码
	 * 
	 * @param str
	 *            短信内容
	 * @return 截取得到的6位动态密码
	 */
	public static String getDynamicPassword(String str) {
		Pattern continuousNumberPattern = Pattern.compile("[0-9\\.]+");
		Matcher m = continuousNumberPattern.matcher(str);
		String dynamicPassword = "";
		while (m.find()) {
			if (m.group().length() == 6) {
				dynamicPassword = m.group();
			}
		}

		return dynamicPassword;
	}

	class SmsObserver extends ContentObserver {

		public SmsObserver(Context context, Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// 每当有新短信到来时，使用我们获取短消息的方法
			getSmsFromPhone();
		}
	}
}
