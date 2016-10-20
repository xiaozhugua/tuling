package com.abct.tljr.wxapi;

import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.model.Options;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class OtherPerson extends BaseActivity {
	String id, name, avatar, level, company;
	int jifen = 0;
	String TAG = "OtherPerson";
	double pro = 0;
	int ValidStatus = 0, ValidStatusDv = 0;
	boolean isValid = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otherperson);
		findView();
	}

	private void findView() {
		id = getIntent().getStringExtra("id");
		name = getIntent().getStringExtra("name");
		avatar = getIntent().getStringExtra("avatar");
		((TextView) findViewById(R.id.id)).setText(id);
		((TextView) findViewById(R.id.name)).setText(name);
		StartActivity.imageLoader.displayImage(avatar, ((ImageView) findViewById(R.id.avatar)),
				Options.getCircleListOptions());
		Util.setImage(avatar, findViewById(R.id.info), handler);
		findViewById(R.id.back).setOnClickListener(onclick);

		getPersonMsg();
	}

	private OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.back:
				finish();
				break;
			default:
				break;
			}
		}
	};

	private void getPersonMsg() {
		String url = UrlUtil.URL_online + "/" + id;
		LogUtil.i(TAG, "url :" + url);
		NetUtil.sendPost(url, "", new NetResult() {
			@Override
			public void result(String msg) {
				// TODO Auto-generated method stub
				LogUtil.i(TAG, "getperson :" + msg);
				try{
					JSONObject json =new JSONObject(msg);
					if (json.optInt("code") == 200) {
						JSONObject js = json.getJSONObject("result");
						// ((TextView)findViewById(R.id.lv)).setText("等级\n"+js.getString("level"));
						level = js.getString("level");
						pro = (js.getDouble("needtotal") - js.getDouble("need")) / js.getDouble("needtotal") * 100;
						handler.sendEmptyMessage(0);
					}
				}catch(Exception e){
					
				}
			}
		});
		NetUtil.sendGet(UrlUtil.URL_online + "/" + id + "/event", "", new NetResult() {

			@Override
			public void result(String msg) {
				LogUtil.e("getUserEvent", msg);
				try {
					JSONObject object =new JSONObject(msg).getJSONObject("result");
					// MyApplication.getInstance().self
					// .setIntegral(object.getInt("number"));
					jifen = object.optInt("number");
					handler.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		// 实名检测
		NetUtil.sendGet(UrlUtil.URL_valid + "/me", "user=" + id, new NetResult() {

			@Override
			public void result(String msg) {
				LogUtil.e("getUserValid", msg);
				try {
					JSONObject object =new JSONObject(msg);
					if (object.optInt("code") == 200) {
						JSONObject obj = object.getJSONObject("result");
						// if (MyApplication.getInstance().self != null) {
						// MyApplication.getInstance().self
						// .setIdName(obj
						// .getString("idName"));
						// MyApplication.getInstance().self
						// .setIdNumber(obj
						// .getString("idNumber"));
						// MyApplication.getInstance().self.setIdentityId(obj
						// .getString("identityId"));
						// MyApplication.getInstance().self
						// .setValid(obj
						// .getBooleanValue("isValid"));
						// MyApplication.getInstance().self
						// .setValidStatus(obj
						// .getIntValue("status"));
						ValidStatus = obj.optInt("status");
						handler.sendEmptyMessage(2);
						// }
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

		// 大v检测
		NetUtil.sendPost(UrlUtil.URL_validDv + "/" + id, "", new NetResult() {

			@Override
			public void result(String msg) {
				LogUtil.e("getUserValidDv", msg);
				try {
					JSONObject object =new JSONObject(msg);
					if (object.optInt("code") == 200) {
						JSONObject obj = object.getJSONObject("result");
						// if (MyApplication.getInstance().self != null) {
						// MyApplication.getInstance().self
						// .setCompany(obj
						// .getString("company"));
						// MyApplication.getInstance().self.setIdentityDvId(obj
						// .getString("identityId"));
						// MyApplication.getInstance().self
						// .setValidDv(obj
						// .getBooleanValue("isValid"));
						// MyApplication.getInstance().self
						// .setIdentityStatus(obj
						// .getIntValue("status"));
						isValid = obj.optBoolean("isValid");
						ValidStatusDv = obj.optInt("status");
						company = obj.getString("company");
						handler.sendEmptyMessage(3);
						// }
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		super.handleMsg(msg);
		switch (msg.what) {
		case 0:
			((TextView) findViewById(R.id.lv)).setText("等级\nLv:" + level);
			((TextView) findViewById(R.id.lvInfo)).setText("经验值\n" + Util.df.format(pro) + "%");
			break;
		case 1:
			((TextView) findViewById(R.id.jifen)).setText("积分\n" + jifen);
			break;
		case 2:
			String str = "未认证";
			if (ValidStatus == 0) {
				str = "未认证";
			} else if (ValidStatus == 1) {
				str = "认证中";
			} else {
				str = "已验证";
			}
			((TextView) findViewById(R.id.nameauth)).setText(str);
			break;
		case 3:
			if (isValid) {
				((TextView) findViewById(R.id.vauth)).setText(company);
				((CheckBox) findViewById(R.id.vauthcb1)).setChecked(true);
				((CheckBox) findViewById(R.id.vauthcb)).setChecked(true);
				((TextView) findViewById(R.id.vauth)).setText("已认证");
			} else {
				((CheckBox) findViewById(R.id.vauthcb1)).setChecked(false);
				((CheckBox) findViewById(R.id.vauthcb)).setChecked(false);
				if (ValidStatusDv == 0) {
					((TextView) findViewById(R.id.vauth)).setText("未认证");
				} else {
					((TextView) findViewById(R.id.vauth)).setText("认证中");
				}
			}
			break;
		default:
			break;
		}
	}
}