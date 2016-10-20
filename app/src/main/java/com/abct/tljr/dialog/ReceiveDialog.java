package com.abct.tljr.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015-6-2 上午11:04:17
 */

public class ReceiveDialog extends Dialog implements OnClickListener {
	Activity activity;
	Intent intent;
	TextView txt_receive_title, txt_receive_text, txt_receive_msg,
			txt_receive_tishi;
	Button btn_receive_cancel, btn_receive_sure;
	JSONObject obj;
	boolean isFenzu;
	boolean isHave;

	public ReceiveDialog(Activity activity, Intent intent) {
		super(activity, R.style.dialog);
		this.activity = activity;
		this.intent = intent;
		setContentView(R.layout.tljr_dialog_receive);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		init();
	}

	private void init() {
		txt_receive_title = (TextView) findViewById(R.id.tljr_txt_receive_title);
		txt_receive_text = (TextView) findViewById(R.id.tljr_txt_receive_text);
		txt_receive_msg = (TextView) findViewById(R.id.tljr_txt_receive_msg);
		txt_receive_tishi = (TextView) findViewById(R.id.tljr_txt_receive_tishi);
		btn_receive_cancel = (Button) findViewById(R.id.tljr_btn_receive_cancel);
		btn_receive_sure = (Button) findViewById(R.id.tljr_btn_receive_sure);
		btn_receive_cancel.setOnClickListener(this);
		btn_receive_sure.setOnClickListener(this);
		txt_receive_title.setText(intent.getStringExtra("title"));
		txt_receive_text.setText(intent.getStringExtra("text"));
		try {
			obj = new JSONObject(intent.getStringExtra("msg"));
			txt_receive_msg.setText(obj.getString("msg"));
			isFenzu = obj.has("stocks");
			if (isFenzu) {
				isHave = ZiXuanUtil.fzMap.containsKey(obj.optString("name"));
				if (isHave)
					txt_receive_tishi.setText("此分组已存在，是否合并？");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tljr_btn_receive_cancel:
			this.dismiss();
			break;
		case R.id.tljr_btn_receive_sure:
			if (isFenzu) {
				addShareZu(obj);
			} else {
				addShareGu(obj);
			}                                                                                                                                                                                                                                                                                                                                                                     
			this.dismiss();
			break;
		default:
			break;
		}
	}

	private void addShareZu(JSONObject obj) {
		String name = obj.optString("name");
		OneFenZu zu;
		ArrayList<OneGu> list;
		if (isHave) {
			zu = ZiXuanUtil.fzMap.get(name);
		} else {
			zu = ZiXuanUtil.addNewFenzu(obj.optString("name"), false);
		}
		list = zu.getList();
		HashMap<String, OneGu> map = new HashMap<String, OneGu>();
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i).getMarket() + list.get(i).getCode()
					+ list.get(i).getName(), list.get(i));
		}
		JSONArray arr = obj.optJSONArray("stocks");
		for (int j = 0; j < arr.length(); j++) {
			JSONObject stock = arr.optJSONObject(j);
			OneGu gu = new OneGu();
			gu.setTime(System.currentTimeMillis());
			gu.setCode(stock.optString("code"));
			gu.setMarket(stock.optString("market"));
			gu.setName(stock.optString("name"));
			gu.setNow(stock.optDouble("now"));
			gu.setKey(stock.optString("key", gu.getMarket() + gu.getCode()));
			if (!map.containsKey(gu.getMarket() + gu.getCode() + gu.getName())) {
				ZiXuanUtil.addStock(gu, zu.getName());
			}
		}
		map = null;
		zu.setList(list);
		if (activity instanceof MainActivity) {
			if (((MainActivity) activity).hangQing.mTljrZuHe != null)
				((MainActivity) activity).hangQing.mTljrZuHe.addNewFenZu(zu.getName());
		} else if (MyApplication.getInstance().getMainActivity() != null) {
			MainActivity activity = MyApplication.getInstance().getMainActivity();
			if (activity.hangQing.mTljrZuHe != null)
				activity.hangQing.mTljrZuHe.addNewFenZu(zu.getName());
		}
		
		MainActivity.AddZuHuStatus = 1;
		ZiXuanUtil.emitNowFenZu(name);
		if (MyApplication.getInstance().self != null) {
			ZiXuanUtil.sendActions(MyApplication.getInstance().self,MainActivity.activity, null);
		}
		
	}

	// 添加股票弹窗显示
	public void DialogShow(final OneGu gu, final Context context) {
		// 获取所有的组
		ArrayList<OneFenZu> list = new ArrayList<OneFenZu>(ZiXuanUtil.fzMap.values());
		boolean dialogstatus = true;
	}

	private void addShareGu(JSONObject obj) {
		OneGu gu = new OneGu();
		gu.setCode(obj.optString("code"));
		gu.setMarket(obj.optString("market"));
		gu.setName(obj.optString("name"));
		gu.setKey(obj.optString("key"));
		gu.setNow(obj.optDouble("now"));
		gu.setTime(System.currentTimeMillis());
		GuDealImpl.DialogShow(gu,null,activity);
	}
}
