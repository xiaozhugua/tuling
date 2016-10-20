package com.abct.tljr.ui.activity.zhiyan;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.shouye.FerdlsActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import io.realm.Realm;

public class NullSearchActivity extends BaseActivity {
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhiyan_search);
		findview();
		edit = (EditText) findViewById(R.id.edit);
		findViewById(R.id.chaxun).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				serch();
			}
		});
	}

	public void findview() {
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void serch() {
		String input = edit.getText().toString();
		if (input.length() == 0 || input == null || input.equals("")) {
			showToast("请输入搜索的股票");
			return;
		}
		Realm realm = Realm.getDefaultInstance();
		if (realm.isEmpty()) {
			realm.close();
			showToast("查询股票失败，请修改查询条件！");
			return;
		}
		OneGu gu;
		if (input.matches("[+-]?[0-9]+[0-9]*(\\.[0-9]+)?")) {
			gu = realm.where(OneGu.class).beginsWith("code", input).findFirst();
		} else if (input.matches("[a-zA-Z]+")) {
			gu = realm.where(OneGu.class).beginsWith("pyName", input.toLowerCase()).findFirst();
		} else if (input.matches("[\\u4e00-\\u9fa5]+")) {
			gu = realm.where(OneGu.class).beginsWith("name", input).findFirst();
		} else {
			showToast("查询股票失败，请修改查询条件！");
			realm.close();
			return;
		}
		realm.close();
		if (gu == null) {
			showToast("查询股票失败，请修改查询条件！");
			return;
		}
		serch(gu);
	}

	private void serch(final OneGu gu) {
		LogUtil.e("serchCode", UrlUtil.URL_ZR + "crowd/getCrowListByCode?code=" + gu.getCode());
		ProgressDlgUtil.showProgressDlg("", this);
		NetUtil.sendGet(UrlUtil.URL_ZR + "crowd/getCrowListByCode?code=" + gu.getCode(), new NetResult() {

			@Override
			public void result(String arg0) {
				LogUtil.e("serchCode", arg0);
				ProgressDlgUtil.stopProgressDlg();
				try {
					JSONObject jsonObject = new JSONObject(arg0);
					if (jsonObject.getInt("status") == 1) {
						JSONObject object = jsonObject.getJSONArray("msg").getJSONObject(0);
						if (object.getInt("status") == 1) {// 已完成
							more(gu.getMarket(), gu.getCode());
						} else {// 正在众筹
							startLanchCode(gu.getMarket(), gu.getCode());
						}
					} else {
						showToast(jsonObject.getString("msg"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					startLanchCode(gu.getMarket(), gu.getCode());
				}
			}
		});
	}

	private void startLanchCode(String market, String code) {
		Intent i = new Intent(this, LaunchReSearchActivity.class);
		i.putExtra("market", market);
		i.putExtra("code", code);
		startActivity(i);
	}

	private void more(String market, String code) {
		NetUtil.sendPost(UrlUtil.Url_235 + "8080/StockDataService/ferdls", "oper=1&code=" + code + "&market=" + market,
				new NetResult() {

					@Override
					public void result(String arg0) {
						try {
							JSONObject jsonObject = new JSONObject(arg0);
							if (jsonObject.getInt("status") == 1) {
								Intent intent = new Intent(NullSearchActivity.this, FerdlsActivity.class);
								intent.putExtra("info", jsonObject.getString("result"));
								startActivity(intent);
							} else {
								showToast("获取数据失败！");
							}
						} catch (JSONException e) {
							e.printStackTrace();
							showToast("获取数据失败！");
						}
					}
				});
	}

}
