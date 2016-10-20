package com.abct.tljr.wxapi;

import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.model.Code;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindPassword extends BaseActivity implements OnClickListener {

	private ImageView code;
	private String getCode = null; // 获取验证码的值
	private String userName = null;// 用户名
	private String method = null;// 类型
	private EditText edit;
	private String getemailorphone;
	private RelativeLayout rl_phone;
	private EditText ed_phone1, ed_phone2, edit_code;
	private TextView tv_updata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpassword);
		findView();
	}

	private void findView() {
		ed_phone1 = (EditText) findViewById(R.id.edit_pwd1);
		ed_phone2 = (EditText) findViewById(R.id.edit_pwd2);
		edit_code = (EditText) findViewById(R.id.edit_code);
		tv_updata = (TextView) findViewById(R.id.updata);
		code = (ImageView) findViewById(R.id.tljr_txt_futures_join_confirm);
		edit = (EditText) findViewById(R.id.tljr_et_futures_join_confirm);
		rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
		code.setImageBitmap(Code.getInstance().getBitmap());
		getCode = Code.getInstance().getCode();
		userName = getIntent().getStringExtra("userName");
		method = getIntent().getStringExtra("method");
		if (method.equals("email")) {
			((TextView) findViewById(R.id.you)).setText("您已绑定的邮箱 :");
			((TextView) findViewById(R.id.sure)).setText("通过邮箱找回");
			getemailorphone = getIntent().getStringExtra("email");
		} else if (method.equals("phone")) {
			((TextView) findViewById(R.id.you)).setText("您已绑定的手机 :");
			((TextView) findViewById(R.id.sure)).setText("通过手机找回");
			getemailorphone = getIntent().getStringExtra("phone");
		}
		((TextView) findViewById(R.id.number)).setText(getemailorphone);
		findViewById(R.id.sure).setOnClickListener(this);
		findViewById(R.id.tljr_per_btn_lfanhui).setOnClickListener(this);
		findViewById(R.id.tljr_txt_futures_join_confirm).setOnClickListener(
				this);
		findViewById(R.id.sure_phone).setOnClickListener(this);
		tv_updata.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tljr_per_btn_lfanhui:
			finish();
			break;
		case R.id.sure:
			LogUtil.i("DATA", "getCode :" + getCode + "   edi :"
					+ edit.getText().toString());
			if (!edit.getText().toString().equals(getCode)) {
				showToast("验证码输入错误!");
				return;
			}
			if (rl_phone.getVisibility() == View.GONE && edit.getText().toString().equals(getCode)) {
				findpassword();
			}
			break;
		case R.id.tljr_txt_futures_join_confirm:
			code.setImageBitmap(Code.getInstance().getBitmap());
			getCode = Code.getInstance().getCode();
			break;
		case R.id.sure_phone:
			findpasswordphone();
			break;
		case R.id.updata:
			if (time <= 0) {
				findpassword2();
				time = 30;
				tv_updata.setText("（" + time + "）秒后可重新发送短信验证码");
				tv_updata.setTextColor(Color.GRAY);
				handler.postDelayed(run, 1000);
			}
			break;
		default:
			break;
		}
	}

	String resultStr = "";

	private void findpassword() {
		// 通过邮箱/手机找回
		NetUtil.sendPost(UrlUtil.URL_forgotpwd, "uname=" + userName
				+ "&method=" + method, new NetResult() {
			@Override
			public void result(String msg) {
				LogUtil.i("DATA", "findpass :" + msg);
				if (msg.length() == 0) {
					showToast("连接服务器失败，请重试");
					return;
				} else {
					try{
						JSONObject result =new JSONObject(msg);
						int code = result.optInt("code");
						if (method.equals("email")) {
							if (code == 1) {
								resultStr = result.optString("result");
								handler.sendEmptyMessage(0);
							} else {
								handler.sendEmptyMessage(1);
							}
						} else {
							if (code == 1) {
								resultStr = result.optString("result");
								handler.sendEmptyMessage(2);
							} else {
								handler.sendEmptyMessage(1);
							}
						}
					}catch(Exception e){
						
					}
				}
			}
		});
	}

	private void findpassword2() {
		// 通过邮箱/手机找回
		NetUtil.sendPost(UrlUtil.URL_forgotpwd, "uname=" + userName
				+ "&method=" + method, new NetResult() {
			@Override
			public void result(String msg) {
			}
		});
	}

	private void findpasswordphone() {
		String pwd1 = ed_phone1.getText().toString();
		String pwd2 = ed_phone2.getText().toString();
		String code = edit_code.getText().toString();
		if (!pwd1.equals("") || !pwd2.equals("") || !code.equals("")) {
			if (pwd1.length() > 5) {
				if (pwd1.equals(pwd2)) {
					NetUtil.sendPost(UrlUtil.URL_changepwd, "id=" + resultStr
							+ "&upass=" + pwd1 + "&repass=" + pwd2 + "&code="
							+ code, new NetResult() {
						@Override
						public void result(String msg) {
							// TODO Auto-generated method stub
							LogUtil.i("DATA", "phone :" + msg);
							try{
								JSONObject ob =new JSONObject(msg);
								if (ob.optInt("code") == 1) {
									handler.sendEmptyMessage(6);
								}
							}catch(Exception e){
								
							}
						}
					});
				} else {
					Toast.makeText(this, "密码填写不一致", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "密码最少6位数哦！", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "输入框不能未空", Toast.LENGTH_SHORT).show();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				showDialog(resultStr);
				break;
			case 1:
				Toast.makeText(FindPassword.this, "找回密码失败", Toast.LENGTH_SHORT)
						.show();
				break;
			case 2:
				rl_phone.setVisibility(View.VISIBLE);
				handler.sendEmptyMessage(3);
				findViewById(R.id.sure).setVisibility(View.GONE);
				break;
			case 3:
				int y = (int) rl_phone.getHeight() + (int) rl_phone.getY();
				Animation animation = new TranslateAnimation(0, 0, -y, 0);
				animation.setFillAfter(true);
				animation.setDuration(500);
				rl_phone.startAnimation(animation);
				time = 30;
				tv_updata.setText("（" + time + "）秒后可重新发送短信验证码");
				tv_updata.setTextColor(Color.GRAY);
				handler.postDelayed(run, 1000);
				break;
			case 4:
				handler.post(run);
				break;
			case 5:
				tv_updata.setText("获取短信验证码");
				tv_updata.setTextColor(ColorUtil.green);
				break;
			case 6:
				showDialog("修改成功");
				break;
			default:
				break;
			}
		}
	};

	int time = 0;
	Runnable run = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (time > 0) {
				tv_updata.setText("（" + time + "）秒后可重新发送短信验证码");
				time--;
				handler.sendEmptyMessageDelayed(4, 1000);
			} else {
				handler.sendEmptyMessage(5);
			}
		}
	};

	public void showDialog(final String msg) {
		post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FindPassword.this);
				builder.setTitle("提示");
				builder.setMessage(msg);
				builder.setNegativeButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								finish();
							}
						});
				builder.show();
			}
		});

	}
}
