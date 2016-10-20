package com.abct.tljr.shouye;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author xbw 我的关注 Ferdls
 * @version 创建时间：2015年12月19日 下午2:55:21
 */
public class AllFerdlsActivity extends BaseActivity {
	private static final int ID_TITLE = 1;
	private Context context;
	private LinearLayout view;
	private RelativeLayout title;
	private ListView lv;
	private ArrayList<OneGu> list = new ArrayList<OneGu>();
	private ArrayList<JSONObject> list1 = new ArrayList<JSONObject>();
	private boolean isStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		view = new LinearLayout(this);
		view.setOrientation(LinearLayout.VERTICAL);
		view.setBackgroundColor(getResources().getColor(R.color.tljr_bj));
		title = (RelativeLayout) View.inflate(this, R.layout.tljr_view_activity_title, null);
		title.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title.setId(ID_TITLE);
		view.addView(title, new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.FILL_PARENT,
				Util.dp2px(this, MyApplication.TitleHeight)));
		((TextView) title.findViewById(R.id.name)).setText("Ferdls研究所");
		((TextView) title.findViewById(R.id.other)).setText("我的关注");
		title.findViewById(R.id.other).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (MyApplication.getInstance().self == null) {
					login(true);
					return;
				}
				startActivity(new Intent(context, MyCareFerdlsActivity.class));
			}
		});
		lv = new ListView(this);
		RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// lv.setLayoutParams(params);
		view.addView(lv, params);
		setContentView(view);
		lv.setAdapter(adapter);
		isStart = true;
		reflush();
	}

	public void reflush() {
		ProgressDlgUtil.showProgressDlg("", this);
		NetUtil.sendPost(UrlUtil.URL_ferdls, "", new NetResult() {
			@Override
			public void result(final String msg) {
				ProgressDlgUtil.stopProgressDlg();
				if (msg.equals("")) {
					postDelayed(new Runnable() {
						@Override
						public void run() {
							reflush();
						}
					}, 1000);
					return;
				}
				post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 1) {
								JSONArray array = object.getJSONArray("data");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									OneGu gu = new OneGu();
									gu.setName(obj.optString("name", ""));
									gu.setCode(obj.optString("code", ""));
									gu.setMarket(obj.optString("market", ""));
									gu.setTag(obj.optString("iconUrl", ""));
									gu.setKey(gu.getMarket() + gu.getCode());
									list.add(gu);

									list1.add(obj);
								}
							}
							adapter.notifyDataSetChanged();
							post(r);
						} catch (JSONException e) {
							e.printStackTrace();
							showToast("获取失败");
						}
					}
				});
			}
		});
	}

	Runnable r = new Runnable() {

		@Override
		public void run() {
			if (isStart)
				postDelayed(r, 3000);
			reflushData();
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		isStart = false;
		list = null;
		list1 = null;
	};

	class viewHolder {
		TextView name, code, now, changep;
		CheckBox cb;
		JSONObject obj;
		OneGu gu;
	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			viewHolder holder;
			if (v == null) {
				v = View.inflate(context, R.layout.tljr_hq_zixuan_item, null);
				holder = new viewHolder();
				holder.cb = (CheckBox) v.findViewById(R.id.tljr_hq_checkbox);
				// holder.cb.setVisibility(View.VISIBLE);
				// holder.cb.setClickable(false);
				holder.name = (TextView) v.findViewById(R.id.tljr_hq_name_test);
				holder.code = (TextView) v.findViewById(R.id.tljr_hq_info_test);
				holder.now = (TextView) v.findViewById(R.id.tljr_hq_now_test);
				holder.changep = (TextView) v.findViewById(R.id.tljr_hq_qchange_test);
				v.setTag(holder);
			} else {
				holder = (viewHolder) v.getTag();
			}
			JSONObject obj = list1.get(position);
			holder.obj = obj;
			OneGu gu = list.get(position);
			holder.name.setText(gu.getName());
			holder.code.setText(gu.getCode());
			holder.gu = gu;
			float change = (float) gu.getChange();
			holder.now.setText(Util.df.format(gu.getNow()));
			if (gu.getNow() == 0 && gu.getKaipanjia() != 0) {
				holder.changep.setText("停牌");
				holder.changep.setBackgroundColor(context.getResources().getColor(R.color.tljr_gray));
			} else {
				holder.changep.setText((change > 0 ? "+" : "") + change + "%");
				holder.changep.setBackgroundColor(change > 0 ? ColorUtil.red : ColorUtil.green);
			}
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewHolder holder = (viewHolder) v.getTag();
					Intent intent = new Intent(context, FerdlsActivity.class);
					intent.putExtra("info", holder.obj.toString());
					context.startActivity(intent);
				}
			});
			return v;
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
	};

	public void reflushData() {
		if (list == null) {
			return;
		}
		String parm = "list=";
		for (int i = 0; i < list.size(); i++) {
			OneGu gu = list.get(i);
			if (i == 0) {
				parm += (gu.getMarket() + "|" + gu.getCode());
			} else {
				parm += ("," + gu.getMarket() + "|" + gu.getCode());
			}
		}
		Util.getRealInfo(parm, new NetResult() {
			@Override
			public void result(final String msg) {
				try {
					final org.json.JSONObject object = new org.json.JSONObject(msg);
					if (object.getInt("code") == 1) {
						post(new Runnable() {
							@Override
							public void run() {
								try {
									org.json.JSONArray arr = object.getJSONArray("result");
									for (int i = 0; i < arr.length(); i++) {
										JSONObject obj = arr.getJSONObject(i);
										String key = obj.getString("market") + obj.getString("code");
										if (list == null) {
											return;
										}
										for (int j = 0; j < list.size(); j++) {
											OneGu gu = list.get(j);
											if ((gu.getMarket() + gu.getCode()).equals(key)) {
												JSONArray array = obj.getJSONArray("data");
												gu.setNow(array.optDouble(0, 0));
												gu.setKaipanjia(array.optDouble(1, 0));
												gu.setChange((float) array.optDouble(9, 0));
											}
										}
										adapter.notifyDataSetChanged();
									}
								} catch (org.json.JSONException e) {
								}
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, true);
	}
}
