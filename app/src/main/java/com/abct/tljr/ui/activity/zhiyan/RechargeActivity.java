package com.abct.tljr.ui.activity.zhiyan;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.AppInfo;
import com.qh.common.model.User;
import com.qh.common.pay.AliPay;
import com.qh.common.pay.PayCallBack;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RechargeActivity extends BaseActivity implements OnClickListener, IWXAPIEventHandler, PayCallBack {
	private TextView yue, chong, fan;
	private EditText money;
	private float crash, ratio;
	private GridView gv;
	private RadioButton cb;
	private ArrayList<Gv> list = new ArrayList<Gv>();
	private int[] moneys = { 10, 30, 50, 100, 500, 1000 };
	private int payMoney;
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
		setContentView(R.layout.tljr_activity_recharge);
		Intent intent = getIntent();
		crash = intent.getFloatExtra("crash", 0);
		ratio = intent.getFloatExtra("ratio", 0);
		initView();
		api = WXAPIFactory.createWXAPI(this, Configs.WeiXinAppId);
	}

	private void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.recharge).setOnClickListener(this);
		cb = (RadioButton) findViewById(R.id.cb);
		cb.setChecked(true);
		gv = (GridView) findViewById(R.id.gv);
		yue = (TextView) findViewById(R.id.yue);
		yue.setText("￥" + crash / 100 + "元");
		chong = (TextView) findViewById(R.id.chong);
		fan = (TextView) findViewById(R.id.fan);
		money = (EditText) findViewById(R.id.money);
		GradientDrawable drawable = (GradientDrawable) money.getBackground();
		drawable.setStroke(1, ColorUtil.lightGray);
		for (int i = 0; i < moneys.length; i++) {
			Gv gv = new Gv(this, moneys[i]);
			list.add(gv);
		}
		gv.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				return list.get(position);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}
		});
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
				for (int i = 0; i < list.size(); i++) {
					list.get(i).check(i == p);
				}
				payMoney = list.get(p).getMoney();
				check();
			}
		});
		money.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					payMoney = Integer.parseInt(s.toString());
					if (payMoney > 1000) {
						payMoney = 1000;
						money.setText("1000");
						money.setSelection(money.getText().length());
					}
				} else {
					payMoney = 0;
				}
				check();
			}
		});
		for (int i = 0; i < list.size(); i++) {
			list.get(i).check(i == 0);
		}
		payMoney = list.get(0).getMoney();
		check();
	}

	private void check() {
		 
		chong.setText("充" + payMoney + "元");
		fan.setText("返" + (int) (payMoney * ratio) + "元");
	}

	private void pay() {
		if (!cb.isChecked()) {
			showToast("请先阅读并同意《充值返现活动协议》");
			return;
		}
		if (payMoney <= 0 || payMoney > 1000) {
			showToast("请输入正确充值金额，最多1000元");
			return;
		}
		if (User.getUser() == null) {
			showToast("请先登录!");
			return;
		}
		startActivityForResult(
				new Intent(this, PayActivity.class).putExtra("onlyPay", true).putExtra("money", (float) payMoney), 1);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == 1) {
			final String type = data.getStringExtra("type");
			LogUtil.e("rechargeurl", "uid=" + User.getUser().getId() + "&token=" + Configs.token + "&money=" + payMoney
					+ "&type=" + type);
			NetUtil.sendPost(UrlUtil.URL_ZR + "user/recharge", "uid=" + User.getUser().getId() + "&token="
					+ Configs.token + "&money=" + payMoney + "&type=" + type, new NetResult() {

						@Override
						public void result(String msg) {
							LogUtil.e("recharge", msg);
							try {
								JSONObject object = new JSONObject(msg);
								if (object.getInt("status") == 1) {
									if (type.equals("1")) {
										AliPay.getInstance().init(RechargeActivity.this).pay(object.getString("msg"));
									} else if (type.equals("3")) {
										PayReq req = new PayReq();
										JSONObject json = object.getJSONObject("msg");
										req.appId = json.getString("appid");
										req.partnerId = json.getString("partnerid");
										req.prepayId = json.getString("prepayid");
										req.nonceStr = json.getString("noncestr");
										req.timeStamp = json.getString("timestamp");
										req.packageValue = json.getString("package");
										req.sign = json.getString("sign");
										req.extData = "app data"; // optional
										api.sendReq(req);
									}
								} else {
									showToast(object.getString("msg"));
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								ProgressDlgUtil.stopProgressDlg();
								e.printStackTrace();
							}

						}
					});
		}
	}

	@Override
	public void payerror(String arg0) {
		showToast(arg0);
	}

	@Override
	public void paysuccess(String arg0) {
		showToast(arg0);
		initYue();
	}
	@Override
	public void onReq(BaseReq arg0) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d("ReSerahActivity", "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			showToast(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
			initYue();
		}
	}

	private void initYue() {
		NetUtil.sendGet(UrlUtil.URL_ZR + "user/getQuan", "uid=" + User.getUser().getId() + "&token=" + Configs.token,
				new NetResult() {

					@Override
					public void result(final String msg) {
						ProgressDlgUtil.stopProgressDlg();
						try {
							org.json.JSONObject object = new org.json.JSONObject(msg);
							if (object.getInt("status") == 1) {
								crash = (float) object.getDouble("msg");
								yue.setText("￥" + crash / 100 + "元");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initYue();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.recharge:
			pay();
			break;

		default:
			break;
		}
	}
}

class Gv extends RelativeLayout {
	private TextView tv;
	private ImageView check;
	private int money;

	public Gv(Context context, int money) {
		super(context);
		this.money = money;
		init();
	}

	public int getMoney() {
		return money;
	}

	private void init() {
		setBackground(getContext().getResources().getDrawable(R.drawable.shape_rectangle_bj));
		setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, AppInfo.dp2px(getContext(), 60)));
		GradientDrawable drawable = (GradientDrawable) getBackground();
		drawable.setStroke(1, ColorUtil.lightGray);
		tv = new TextView(getContext());
		tv.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(16);
		tv.setText("充" + money + "元");
		addView(tv);
		check = new ImageView(getContext());
		check.setImageResource(R.drawable.img_zhifuxuanzhong);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(54, 54);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		check.setLayoutParams(params);
		addView(check);
	}

	public void check(boolean isCheck) {
		check.setVisibility(isCheck ? View.VISIBLE : View.INVISIBLE);
		tv.setTextColor(isCheck ? ColorUtil.gray : ColorUtil.lightGray);
	}
}