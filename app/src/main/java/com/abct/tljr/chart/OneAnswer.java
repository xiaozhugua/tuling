package com.abct.tljr.chart;

import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.FutureAskViewAdapter.AskBean;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.CircularImage;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.wxapi.OtherPerson;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class OneAnswer extends BaseActivity {

	ImageView imagezan,imagecai;
	AskBean ob;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart_oneanswer);
		// getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.toumin));

		findView();
	}

	private void findView() {
		findViewById(R.id.tljr_img_futures_join_back).setOnClickListener(
				onclick);
		((TextView) findViewById(R.id.title)).setText(getIntent()
				.getStringExtra("title"));
		((TextView) findViewById(R.id.title2)).setText(getIntent()
				.getStringExtra("title"));
		imagezan = (ImageView) findViewById(R.id.zan);
		imagecai = (ImageView) findViewById(R.id.cai);
		imagezan.setOnClickListener(onclick);
		imagecai.setOnClickListener(onclick);

		if (getIntent().getStringExtra("type").equals("FutureAskViewAdapter")) {
			ob = FutureAskViewAdapter.map.get(getIntent().getStringExtra("id"));
		} else {
			ob = OneAskActivity.map.get(getIntent().getStringExtra("id"));
		}
		((TextView) findViewById(R.id.name)).setText(ob.nickname);
		((TextView) findViewById(R.id.zan_number)).setText(ob.focus);
		((TextView) findViewById(R.id.content)).setText(ob.msg);
		((TextView) findViewById(R.id.cai_number)).setText(ob.unfocus);
		StartActivity.imageLoader.displayImage(ob.avatar,
				(CircularImage) findViewById(R.id.im));
		findViewById(R.id.im).setOnClickListener(onclick);
		if (ob.isfocus) {
			imagezan.setBackgroundResource(R.drawable.zan_blue);
		}
		if(ob.isunfocus){
			imagecai.setBackgroundResource(R.drawable.cai_blue);
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
			case R.id.zan:
				Ask(ob.id, !ob.isfocus);
				break;
			case R.id.cai:
				Askcai(ob.id,!ob.isunfocus);
				break;
			case R.id.im:
				Intent i = new Intent(OneAnswer.this,OtherPerson.class);
				i.putExtra("id", ob.uid);
				i.putExtra("name", ob.nickname);
				i.putExtra("avatar", ob.avatar);
				startActivity(i);
				break;
			default:
				break;
			}
		}
	};

	// 点赞
	public void Ask(String id, final boolean zanorno) {
		ProgressDlgUtil.showProgressDlg("", this);
		try {
			// &mobile= &avatar=
			String url = UrlUtil.Url_235 + "8080/TLChat/rest/userqa/";
			if (zanorno) {
				url += "focus";
			} else {
				url += "unfocus";
			}
			NetUtil.sendGet(url,
					"uid=" + MyApplication.getInstance().self.getId() + "&id="
							+ id, new NetResult() {
						@Override
						public void result(String msg) {
							try{
								// TODO Auto-generated method stub
								if (!msg.equals("error")) {
									JSONObject js =new JSONObject(msg);
									if (js!=null&&js.getInt("status") == 1) {
										ob.isfocus = zanorno;
										handler.sendEmptyMessage(1);
										// handler.sendEmptyMessage(9);
									} else {
										ProgressDlgUtil.stopProgressDlg();
									}
								} else {
									ProgressDlgUtil.stopProgressDlg();
								}
							}catch(Exception e){
								
							}
						}
					});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 点赞
		public void Askcai(String id, final boolean caiorno) {
			ProgressDlgUtil.showProgressDlg("", this);
			try {
				// &mobile= &avatar=
				String url = UrlUtil.Url_235 + "8080/TLChat/rest/userqa/";
				if (caiorno) {
					url += "step";
				} else {
					url += "unStep";
				}
				NetUtil.sendGet(url,
						"uid=" + MyApplication.getInstance().self.getId() + "&id="
								+ id, new NetResult() {
							@Override
							public void result(String msg) {
								try{
									// TODO Auto-generated method stub
									if (!msg.equals("error")) {
										JSONObject js =new JSONObject(msg);
										if (js!=null&&js.getInt("status") == 1) {
											ob.isunfocus = caiorno;
											handler.sendEmptyMessage(2);
											// handler.sendEmptyMessage(9);
										} else {
											ProgressDlgUtil.stopProgressDlg();
										}
									} else {
										ProgressDlgUtil.stopProgressDlg();
									}
								}catch(Exception e){
									
								}
							}
						});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				int i;
				if (ob.isfocus) {
					imagezan.setBackgroundResource(R.drawable.zan_blue);
					i = Integer
							.parseInt(((TextView) findViewById(R.id.zan_number))
									.getText().toString()) + 1;
				} else {
					imagezan.setBackgroundResource(R.drawable.zan);
					i = Integer
							.parseInt(((TextView) findViewById(R.id.zan_number))
									.getText().toString()) - 1;
				}
				if (i < 0) {
					i = 0;
				}
				((TextView) findViewById(R.id.zan_number)).setText(i + "");
				ob.focus = i + "";
				setResult(1);
				ProgressDlgUtil.stopProgressDlg();
				break;
			case 2:
				int cai;
				if (ob.isunfocus) {
					imagecai.setBackgroundResource(R.drawable.cai_blue);
					cai = Integer
							.parseInt(((TextView) findViewById(R.id.cai_number))
									.getText().toString()) + 1;
				} else {
					imagecai.setBackgroundResource(R.drawable.cai_grey_c);
					cai = Integer
							.parseInt(((TextView) findViewById(R.id.cai_number))
									.getText().toString()) - 1;
				}
				if (cai < 0) {
					cai = 0;
				}
				((TextView) findViewById(R.id.cai_number)).setText(cai + "");
				ob.unfocus = cai + "";
				setResult(1);
				ProgressDlgUtil.stopProgressDlg();
				break;
			default:
				break;
			}
		};
	};
}
