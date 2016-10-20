package com.abct.tljr.hangqing.tiaocang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.adapter.TiaoCangAdapter;
import com.abct.tljr.hangqing.model.CodePercent;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class TljrTiaoCangView extends BaseActivity implements OnClickListener {

	private View back;
	private TextView reset;
	private TextView sure;
	private ListView listData = null;
	private List<CodePercent> listCode;
	private int goumai = 0;
	private List<OneGu> listGu = null;
	private TextView progressbar_status;
	private ProgressBar progressbar_shengyu;
	private TiaoCangAdapter adapter = null;
	private TextView percent = null;
	private Map<String, Integer> submitCode;
	private ProgressDialog Progressdialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_tiaocang_view);
		IntentFilter intentFilter = new IntentFilter("com.changpercent.xianjin");
		updateSeekBar mUpdateSeekBar = new updateSeekBar();
		registerReceiver(mUpdateSeekBar, intentFilter);
		initView();
	}

	@SuppressWarnings("unchecked")
	public void initView() {
		back = (View) findViewById(R.id.tljr_tiaocang_fanhui);
		reset = (TextView) findViewById(R.id.tljr_tiaocang_reset);
		sure = (TextView) findViewById(R.id.tljr_tiaocang_sure);
		percent = (TextView) findViewById(R.id.tljr_tiaocang_progressbar_status);
		progressbar_shengyu = (ProgressBar) findViewById(R.id.tljr_tiaocang_progressbar);
		progressbar_status = (TextView) findViewById(R.id.tljr_tiaocang_progressbar_status);
		
		back.setOnClickListener(this);
		reset.setOnClickListener(this);
		sure.setOnClickListener(this);
		submitCode = new HashMap<String, Integer>();
		listGu = (ArrayList<OneGu>) getIntent().getSerializableExtra("zulist");
		listData = (ListView) findViewById(R.id.tljr_tiaocang_status_listview);
		listCode = new ArrayList<CodePercent>();

		if(getIntent().getIntExtra("action",0)==1){
			if(listGu.size()>0){
				Progressdialog = ProgressDialog.show(this,"","获取股票数据中",true,true);
				GuDealImpl.RefreshKaiPangJia(listGu,getIntent().getStringExtra("zuName"), mHandler,5);
			}
		}else{
			if(listGu.size()>0){
				Progressdialog = ProgressDialog.show(this,"","获取股票数据中",true,true);
				GuDealImpl.getZhPercent(getIntent().getStringExtra("zuid"), listGu,mHandler,3);
			}
		}
		
	}

	public void buildListView() {
		// 初始化listview的数据
		CodePercent code;
		for (OneGu gu : listGu) {
			code = new CodePercent(getIntent().getStringExtra("zuname"),
					gu.getName(), gu.getNow(), gu.getPercent(), gu.getMarket(),
					gu.getKaipanjia(), gu.getCode(), gu.getKey(),gu.getP_change());
			listCode.add(code);
			goumai += gu.getPercent();
		}
		adapter = new TiaoCangAdapter(this, R.layout.tijr_tiaocang_viewitem,listCode);
		listData.setAdapter(adapter);
	}

	public void shengyuview() {
		progressbar_shengyu.setProgress(100 - goumai);
		progressbar_status.setText((100 - goumai) + "%");
	}

	// 动态更新现金百分比的广播
	class updateSeekBar extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int goumai = intent.getIntExtra("goumai", 0);
			int shengyu = intent.getIntExtra("shengyu", 0);
			progressbar_shengyu.setProgress(shengyu);
			percent.setText(shengyu + "%");
		}
	}

	public void reset() {
		if(listGu.size()>0){
			int goumai = 0;
			for (int i = 0; i < listGu.size(); i++) {
				listCode.get(i).setCangwei(listGu.get(i).getPercent());
				goumai += listGu.get(i).getPercent();
			}
			progressbar_shengyu.setProgress(100 - goumai);
			progressbar_status.setText((100 - goumai) + "%");
			adapter.notifyDataSetChanged();
		}else{
			Toast.makeText(getBaseContext(),"对不起,没有股票可重置",Toast.LENGTH_SHORT).show();
		}
	}

	// 调整仓位
	public void sendPost() throws JSONException {
		ProgressDlgUtil.showProgressDlg("", this);
		String jsonResult = getJsonPost();
		JSONArray array = new JSONArray(jsonResult);
		if (array.length() == 0) {
			showTip("请调整仓位");
			ProgressDlgUtil.stopProgressDlg();
		} else {
			NetUtil.sendPost(UrlUtil.URL_tc, "action=tiaocang&uid="
					+ MyApplication.getInstance().self.getId() + "&zhid="
					+ getIntent().getStringExtra("zuid") + "&tiaocang="
					+ jsonResult, new NetResult() {
				@SuppressLint("SimpleDateFormat")
				@Override
				public void result(String msg) {
					try {
						JSONObject object = new JSONObject(msg);
						if (object.getInt("status") == 1) {
							// 发送广播
							for (int i = 0; i < listGu.size(); i++) {
								listGu.get(i).setPercent(listCode.get(i).getCangwei());
							}
							Message message = new Message();
							message.what = 2;
							mHandler.sendMessage(message);
						} else {
							ProgressDlgUtil.stopProgressDlg();
							showTip(object.getString("msg"));
						}
					} catch (Exception e) {
						ProgressDlgUtil.stopProgressDlg();
						showTip("调仓失败");
					}
				}
			});
		}
	}

	// 得到发送请求的json
	public String getJsonPost() {
		try {
			JSONArray array = new JSONArray();
			JSONObject object;
			for (int i = 0; i < listGu.size(); i++) {
				if (listGu.get(i).getKaipanjia() == 0.0) {
					continue;
				}
				// Log.e("src", listGu.get(i).getPercent() / 100f + "");
				// Log.e("to", listCode.get(i).getCangwei() / 100f + "");
				// Log.e("listGuId", listGu.get(i).getId());
				if (!(listGu.get(i).getMarket().equals("sh"))
						&& !(listGu.get(i).getMarket().equals("sz"))) {
					continue;
				}
				object = new JSONObject();
				float src = Float.valueOf(String.valueOf(listGu.get(i).getPercent() / 100f + ""));
				float to = Float.valueOf(String.valueOf(listCode.get(i).getCangwei() / 100f + ""));
				if (src != to) {
					object.put("id", listGu.get(i).getId());
					object.put("srcPercent", Float.valueOf(src));
					object.put("toPercent", Float.valueOf(to));
					object.put("code", listGu.get(i).getCode());
					object.put("market", listGu.get(i).getMarket());
					submitCode.put(listCode.get(i).getCode(),listCode.get(i).getCangwei());
					array.put(object);
				}
			}
			return array.toString();
		} catch (Exception e) {
			showTip("获取本地数据出错");
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tljr_tiaocang_fanhui:
			finish();
			break;
		case R.id.tljr_tiaocang_reset:
			reset();
			break;
		case R.id.tljr_tiaocang_sure:
			try {
				sendPost();
			} catch (Exception e) {

			}
			break;
		}
	}

	public void showTip(String msg) {
		Message message = Message.obtain();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("msg", msg);
		message.setData(bundle);
		mHandler.sendMessage(message);
	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getBaseContext(),msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
				break;
			case 2:
				ProgressDlgUtil.stopProgressDlg();
				// 股票初始化
				showTip("调仓成功");
				Intent intent=new Intent("tiaocang_update_piechart");
				List<Map<String, Integer>> params = new ArrayList<Map<String, Integer>>();
				params.add(submitCode);
				intent.putExtra("params",(Serializable) params);
				sendBroadcast(intent);
				finish();
				break;
			case 3:
				GuDealImpl.RefreshKaiPangJia(listGu,getIntent().getStringExtra("zuName"), mHandler,4);
				break;
			case 4:
				GuDealImpl.getZhGuId(getIntent().getStringExtra("zuid"),listGu, mHandler,5);
				break;
			case 5:
				buildListView();
				shengyuview();
				Progressdialog.dismiss();
				break;
			}
		};
	};

}
