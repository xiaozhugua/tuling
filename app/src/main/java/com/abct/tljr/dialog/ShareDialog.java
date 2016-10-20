package com.abct.tljr.dialog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xbw
 * @version 创建时间：2015-6-2 上午11:04:17
 */
public class ShareDialog extends Dialog implements OnClickListener {
	private Handler handler = new Handler();
	Activity activity;
	Object object;
	TextView txt_share_title;
	EditText et_share_id, et_share_msg;
	Button btn_share;

	public ShareDialog(Activity activity, Object object) {
		super(activity, R.style.dialog);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.object = object;
		setContentView(R.layout.tljr_dialog_share);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		txt_share_title = (TextView) findViewById(R.id.tljr_txt_share_title);
		et_share_id = (EditText) findViewById(R.id.tljr_et_share_id);
		et_share_msg = (EditText) findViewById(R.id.tljr_et_share_msg);
		btn_share = (Button) findViewById(R.id.tljr_btn_share);
		findViewById(R.id.tljr_img_share_fanhui).setOnClickListener(this);
		btn_share.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tljr_img_share_fanhui:
			this.dismiss();
			break;
		case R.id.tljr_btn_share:
			share();
			break;

		default:
			break;
		}
	}

	private void share() {
		String s = et_share_id.getText().toString();
		if (s.equals("")) {
			Toast.makeText(activity.getApplicationContext(), "请输入ID",
					Toast.LENGTH_SHORT).show();
			return;
		}
		// if (s.equals(MyApplication.getInstance().self
		// .getId())) {
		// activity.showMessage("不支持给自己分享");
		// return;
		// }
		ShareDialog.this.dismiss();
		if (object instanceof OneFenZu) {
			MobclickAgent.onEvent(activity, "shareGroup");
			shareGroup(handler, activity, (OneFenZu) object, s, et_share_msg
					.getText().toString());
		} else {
			MobclickAgent.onEvent(activity, "shareStock");
			shareStock(handler, activity, (OneGu) object, s, et_share_msg
					.getText().toString());
		}
	}

	public static void shareGroup(final Handler handler,
			final Activity activity, OneFenZu zu, String id, String msg) {
		ProgressDlgUtil.showProgressDlg("", activity);
		try {
			JSONObject obj = new JSONObject();
			obj.put("msg", msg);
			obj.put("name", zu.getName());
			obj.put("tag", zu.getTag());
			obj.put("time", zu.getTime());
			JSONArray array = new JSONArray();
			ArrayList<OneGu> list = zu.getList();
			for (int i = 0; i < list.size(); i++) {
				OneGu gu = list.get(i);
				JSONObject object = new JSONObject();
				object.put("code", gu.getCode());
				object.put("market", gu.getMarket());
				object.put("name", gu.getName());
				object.put("now", gu.getNow());
				// object.put("tag", gu.getTag());
				// object.put("price", gu.getFirst());
				// object.put("time", gu.getTime());
				// object.put("top", gu.getTop());
				// object.put("changeP", gu.getP_change());
				object.put("key", gu.getKey());
				array.put(object);
			}
			obj.put("stocks", array);
			NetUtil.sendPost(UrlUtil.URL_share,
					"reqType=share&type=g&info=" + obj + "&sourceId="
							+ MyApplication.getInstance().self.getId()
							+ "&toId=" + id + "&name=" + zu.getName(),
					new NetResult() {

						@Override
						public void result(final String msg) {
							// TODO Auto-generated method stub
							LogUtil.e("shareGroup", msg);
							ProgressDlgUtil.stopProgressDlg();
							handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										JSONObject object = new JSONObject(msg);
										if (object.getInt("status") == 1) {
											Toast.makeText(
													activity.getApplicationContext(),
													object.getString("msg"),
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(
													activity.getApplicationContext(),
													"分享失败", Toast.LENGTH_SHORT)
													.show();
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							});
						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void shareStock(final Handler handler,
			final Activity activity, OneGu gu, String id, String msg) {
		ProgressDlgUtil.showProgressDlg("", activity);
		try {
			JSONObject object = new JSONObject();
			object.put("msg", msg);
			object.put("code", gu.getCode());
			object.put("market", gu.getMarket());
			object.put("name", gu.getName());
			object.put("now", gu.getNow());
			// object.put("tag", gu.getTag());
			// object.put("price", gu.getFirst());
			// object.put("time", gu.getTime());
			// object.put("top", gu.getTop());
			// object.put("changeP", gu.getP_change());
			object.put("key", gu.getKey());
			NetUtil.sendPost(UrlUtil.URL_share,
					"reqType=share&type=s&info=" + object + "&sourceId="
							+ MyApplication.getInstance().self.getId()
							+ "&toId=" + id + "&name=" + gu.getName(),
					new NetResult() {

						@Override
						public void result(final String msg) {
							// TODO Auto-generated method stub
							LogUtil.e("shareStock", msg);
							ProgressDlgUtil.stopProgressDlg();
							handler.post(new Runnable() {
								@Override
								public void run() {
									try {
										JSONObject object = new JSONObject(msg);
										if (object.getInt("status") == 1) {
											Toast.makeText(
													activity.getApplicationContext(),
													object.getString("msg"),
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(
													activity.getApplicationContext(),
													"分享失败", Toast.LENGTH_SHORT)
													.show();
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});

						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
