package com.abct.tljr.kline.comments;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class EditActivity extends BaseActivity {

	EditText edit;
	String content, code;
	int type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_comments_edit);

		findview();
	}

	private void findview() {
		code = getIntent().getStringExtra("code");
		type = getIntent().getIntExtra("type", 0);
		findViewById(R.id.tljr_img_futures_join_back).setOnClickListener(onclick);
		findViewById(R.id.send).setOnClickListener(onclick);
		edit = (EditText) findViewById(R.id.edit);
	
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) edit.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(edit, 0);
			}

		}, 300);
	}

	// private void Comment() {
	// ProgressDlgUtil.showProgressDlg("", EditActivity.this);
	// content = edit.getText().toString();
	// if (!content.equals("")) {
	// RequestParams params = new RequestParams();
	// params.addBodyParameter("cid", "symbol_" + code);
	// params.addBodyParameter("uid",
	// MyApplication.getInstance().self.getId());
	// params.addBodyParameter("uname",
	// MyApplication.getInstance().self.getNickName());
	// params.addBodyParameter("content", content);
	// if (type == 1) {
	// params.addBodyParameter("comment",
	// getIntent().getStringExtra("id"));
	// if (getIntent().getStringExtra("oid") != null) {
	// params.addBodyParameter("oid",
	// getIntent().getStringExtra("oid"));
	// } else {
	// params.addBodyParameter("oid",
	// getIntent().getStringExtra("id"));
	// }
	// }
	// XUtilsHelper.sendPost(UrlUtil.URL_Comments_comment, params,
	// new HttpCallback() {
	// @Override
	// public void callback(String data) {
	// // TODO Auto-generated method stub
	// JSONObject ob = JSONObject.parseObject(data);
	// if (ob.getBooleanValue("isok")) {
	// JSONObject js = ob.getJSONObject("datas");
	// Intent i = new Intent();
	// i.putExtra("datas", ob.getString("datas"));
	// setResult(1, i);
	// ProgressDlgUtil.stopProgressDlg();
	// post(new Runnable() {
	//
	// @Override
	// public void run() {
	// finish();
	// }
	// });
	// } else {
	// ProgressDlgUtil.stopProgressDlg();
	// }
	// }
	// });
	// } else {
	// Toast.makeText(this, "亲，您还没填写评论内容喔！", Toast.LENGTH_SHORT).show();
	// }
	// }
	private void Comment() {
		ProgressDlgUtil.showProgressDlg("", EditActivity.this);
		content = edit.getText().toString();
		if (!content.equals("")) {
			String params = "cid=symbol_" + code + "&uid=" + User.getUser().getId() + "&uname="
					+ User.getUser().getNickName() + "&content=" + content;
			if (type == 1) {
				params += ("&comment=" + getIntent().getStringExtra("id"));
				if (getIntent().getStringExtra("oid") != null) {
					params += ("&oid=" + getIntent().getStringExtra("oid"));
				} else {
					params += ("&oid=" + getIntent().getStringExtra("id"));
				}
			}
			LogUtil.e("Comment", UrlUtil.URL_Comments_comment+"?"+params);
			NetUtil.sendPost(UrlUtil.URL_Comments_comment, params, new NetResult() {

				@Override
				public void result(String data) {
					try{
						JSONObject ob =new JSONObject(data);
						if (ob!=null&&ob.getBoolean("isok")) {
							Intent i = new Intent();
							i.putExtra("datas", ob.getString("datas"));
							setResult(1, i);
							ProgressDlgUtil.stopProgressDlg();
							post(new Runnable() {

								@Override
								public void run() {
									finish();
								}
							});
						} else {
							ProgressDlgUtil.stopProgressDlg();
							showToast("评论失败，请稍后重试！");
						}
					}catch(Exception e){
						
					}
				}
			});
		} else {
			Toast.makeText(this, "亲，您还没填写评论内容喔！", Toast.LENGTH_SHORT).show();
		}
	}

	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.tljr_img_futures_join_back:
				finish();
				break;
			case R.id.send:
				Comment();
				break;
			default:
				break;
			}
		}
	};

}
