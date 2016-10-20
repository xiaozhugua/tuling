package com.abct.tljr.ui.activity.zhiyan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年12月2日 上午10:54:34
 */
public class PayActivity extends BaseActivity implements OnClickListener {
	private int money;
	private LinearLayout layout;
	private ImageView rdyue;
	private RadioButton rdQuan;
	private TextView quan, yue, othermoney;
	private String quanId;
	private int quanMoney, yueMoney;
	private int crash;
	private int item=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.e("PayActivity", getIntent().getFloatExtra("money", 0) + "");
		money = (int) (getIntent().getFloatExtra("money", 0) * 100);
		setContentView(R.layout.tljr_activity_pay);
		layout = (LinearLayout) findViewById(R.id.layout);
		item=getIntent().getIntExtra("item",0);
		((TextView) findViewById(R.id.money)).setText("￥"+getIntent().getFloatExtra("money", 0));
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.juan).setVisibility(getIntent().getBooleanExtra("canuse", true) ? View.VISIBLE : View.GONE);
		if (getIntent().getBooleanExtra("onlyPay", false)) {
			findViewById(R.id.juan).setVisibility(View.GONE);
			findViewById(R.id.layout_need).setVisibility(View.GONE);
			findViewById(R.id.layout_yue).setVisibility(View.GONE);
		}
		findViewById(R.id.juan).setOnClickListener(this);
		findViewById(R.id.pay).setOnClickListener(this);
		rdyue = (ImageView) findViewById(R.id.rd_yue);
		rdyue.setTag(false);
		rdyue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rdyue.setTag(!(Boolean) rdyue.getTag());
				LogUtil.e("click", crash + "" + rdyue.getTag());
				rdyue.setImageResource((Boolean) rdyue.getTag() 
						? R.drawable.img_gouxuan1 : R.drawable.img_gouxuan2);
				if (crash != 0) {
					yueMoney = (Boolean) rdyue.getTag() ? crash : 0;
					initOtherMoney();
				}
			}
		});
		rdQuan = (RadioButton) findViewById(R.id.rd_juan);
		rdQuan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				quanMoney = 0;
				quanId = null;
				quan.setText("暂无");
				initOtherMoney();
				rdQuan.setChecked(false);
				rdQuan.setClickable(false);
			}
		});
		quan = (TextView) findViewById(R.id.tv_juan);
		yue = (TextView) findViewById(R.id.xianjin);
		othermoney = (TextView) findViewById(R.id.othermoney);

		initPayWay();
		initCrash();
	}

	private void initCrash() {
		ProgressDlgUtil.showProgressDlg("", this);
		NetUtil.sendGet(UrlUtil.URL_ZR+"user/getQuan","uid="+User.getUser().getId()+"&token="+Configs.token,
				new NetResult() {
					@Override
					public void result(final String msg) {
						ProgressDlgUtil.stopProgressDlg();
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("status") == 1) {
								crash = object.getInt("msg");
								rdyue.setTag(true);
								rdyue.setImageResource(R.drawable.img_gouxuan1);
								yue.setText("￥" + (float) crash / 100);
								yueMoney = crash;
								initOtherMoney();
							} else {
								showToast("获取用户现金失败，请联系客服!");
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showToast("获取用户现金失败，请联系客服!");
						}

					}
				});
	}

	private void initPayWay() {
		ProgressDlgUtil.showProgressDlg("", this);
		NetUtil.sendPost(UrlUtil.Url_zsl+"/finance/PayWayServlet","product=zy",new NetResult() {
			@Override
			public void result(final String msg) {
				ProgressDlgUtil.stopProgressDlg();
				try {
					JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						showPayWay(object.getJSONArray("msg"));
					} else {
						showToast("获取支付方式失败，请联系客服!");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showToast("获取支付方式失败，请联系客服!");
				}

			}
		});
	}

	private String payType;

	private void showPayWay(JSONArray jsonArray) throws JSONException {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			View v = View.inflate(this, R.layout.occft_item_pay, null);
			v.setLayoutParams(new LinearLayout.LayoutParams(Util.WIDTH, Util.dp2px(this, 60)));
			if (object.optString("iconurl").length() > 0)
				StartActivity.imageLoader.displayImage(object.optString("iconurl"),
						(ImageView) v.findViewById(R.id.icon), StartActivity.options);
			RadioButton rd = (RadioButton) v.findViewById(R.id.rd);
			String type = object.getString("type");
			if (i == 0) {
				payType = type;
				rd.setChecked(true);
				rd.setClickable(false);
			}
			rd.setVisibility(View.VISIBLE);
			rd.setTag(type);
			rd.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((CompoundButton) v).isChecked()) {
						payType = (String) v.getTag();
						hideOther();
						v.setClickable(false);
						((CompoundButton) v).setChecked(true);
					}
				}
			});
			v.findViewById(R.id.arrow).setVisibility(View.GONE);
			((TextView) v.findViewById(R.id.name)).setText(object.getString("name"));
			((TextView) v.findViewById(R.id.desc)).setText(object.getString("desc"));
			layout.addView(v);
			if (i != jsonArray.length() - 1) {
				View line = new View(this);
				line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
						(int) getResources().getDimension(R.dimen.img_space_hight)));
				line.setBackground(getResources().getDrawable(R.drawable.img_shape_line));
				layout.addView(line);
			}
		}
	}

	private void hideOther() {
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (layout.getChildAt(i) instanceof LinearLayout) {
				((RadioButton) layout.getChildAt(i).findViewById(R.id.rd)).setChecked(false);
				((RadioButton) layout.getChildAt(i).findViewById(R.id.rd)).setClickable(true);
			}
		}
	}

	private void initOtherMoney() {
		int a = money - quanMoney - yueMoney;
		a = (a < 0 ? 0 : a);
		othermoney.setText("￥"+(float)a/100+"元");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			rdQuan.setClickable(true);
			rdQuan.setChecked(true);
			quanMoney = data.getIntExtra("money", 0);
			quan.setText(quanMoney / 100 + "元");
			quanId = data.getStringExtra("id");
			initOtherMoney();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.pay:
			int paymoney = yueMoney;
			LogUtil.e("yueMoney", yueMoney + "");
			LogUtil.e("quanMoney", quanMoney + "");
			LogUtil.e("money", money + "");
			if (yueMoney + quanMoney >= money&&!getIntent().getBooleanExtra("onlyPay", false)) {
				payType = "0";
				paymoney = money - quanMoney;
			}
			LogUtil.e("paymoney", paymoney + "");
			setResult(1, new Intent().putExtra("type", payType).putExtra(
					"quan", (float) paymoney / 100).putExtra("couponId", quanId).putExtra("item",item));
			finish();
			break;
		case R.id.juan:
			startActivityForResult(new Intent(this,YhjActivity.class), 1);
			break;
		default:
			break;
		}
	}

}