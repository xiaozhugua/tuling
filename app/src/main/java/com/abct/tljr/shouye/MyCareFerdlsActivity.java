package com.abct.tljr.shouye;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
public class MyCareFerdlsActivity extends BaseActivity {
	private static final int ID_TITLE = 1;
	private Context context;
	private LinearLayout view;
	private RelativeLayout title;
	private ListView lv;
	private ArrayList<OneGu> list = new ArrayList<OneGu>();
	private boolean isStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (MyApplication.getInstance().self == null) {
			new PromptDialog(this, "获取列表失败,请先登录", new Complete() {

				@Override
				public void complete() {
					login(true);
					finish();
				}
			}, new Complete() {

				@Override
				public void complete() {
					finish();
				}
			}).show();
			return;
		}
		context = this;
		view = new LinearLayout(this);
		view.setOrientation(LinearLayout.VERTICAL);
		view.setBackgroundColor(getResources().getColor(R.color.tljr_bj));
		title = (RelativeLayout) View.inflate(this,
				R.layout.tljr_view_activity_title, null);
		title.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		title.setId(ID_TITLE);
		view.addView(title,
				new LinearLayout.LayoutParams(
						android.widget.LinearLayout.LayoutParams.FILL_PARENT,
						Util.dp2px(this, MyApplication.TitleHeight)));
		((TextView) title.findViewById(R.id.name)).setText("我的关注列表");
		title.findViewById(R.id.tip).setVisibility(View.VISIBLE);
		((TextView) title.findViewById(R.id.tip)).setText("长按取消关注");
		lv = new ListView(this);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// lv.setLayoutParams(params);
		view.addView(lv, params);
		setContentView(view);
		lv.setAdapter(adapter);
		isStart = true;
		getData();
	}

	private void getData() {
		NetUtil.sendPost(UrlUtil.URL_ferdlsevent, "oper=[6]&uid="
				+ MyApplication.getInstance().self.getId(), new NetResult() {

			@Override
			public void result(final String msg) {
				LogUtil.e("getData", msg);
				post(new Runnable() {

					@Override
					public void run() {
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 1) {
								list.clear();
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
								}
							}
							adapter.notifyDataSetChanged();
							showToast(object.getString("msg"));
							post(r);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
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
	};

	class viewHolder {
		TextView name, code, now, changep;
		CheckBox cb;
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
				holder.changep = (TextView) v
						.findViewById(R.id.tljr_hq_qchange_test);
				v.setTag(holder);
			} else {
				holder = (viewHolder) v.getTag();
			}
			OneGu gu = list.get(position);
			holder.name.setText(gu.getName());
			holder.code.setText(gu.getCode());
			holder.gu = gu;
			// if (gu.getTag() != null) {
			// Util.setImage(gu.getTag(), holder.cb, handler);
			// }
			float change = (float) gu.getChange();
			holder.now.setText(Util.df.format(gu.getNow()));
			if (gu.getNow() == 0 && gu.getKaipanjia() != 0) {
				holder.changep.setText("停牌");
				holder.changep.setBackgroundColor(context.getResources()
						.getColor(R.color.tljr_gray));
			} else {
				holder.changep.setText((change > 0 ? "+" : "") + change + "%");
				holder.changep.setBackgroundColor(change > 0 ? ColorUtil.red
						: ColorUtil.green);
			}
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewHolder holder = (viewHolder) v.getTag();
					Intent intent = new Intent(context, OneGuActivity.class);
					Bundle bundle = new Bundle();
					OneGu gu = holder.gu;
					bundle.putString("code", gu.getCode());
					bundle.putString("name", gu.getName());
					bundle.putString("market", gu.getMarket());
					bundle.putString("key", gu.getKey());
					bundle.putSerializable("onegu", gu);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					viewHolder holder = (viewHolder) v.getTag();
					deleteCare(holder.gu);
					return false;
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

	private void deleteCare(final OneGu gu) {
		new PromptDialog(this, "确定删除此关注？", new Complete() {

			@Override
			public void complete() {
				sendEvent(gu);
			}
		}).show();
	}

	private void sendEvent(final OneGu gu) {
		final int event = 5;
		LogUtil.e("sendEvent", "oper=[" + event + "]&code=" + gu.getCode()
				+ "&uid=" + MyApplication.getInstance().self.getId());
		ProgressDlgUtil.showProgressDlg("", this);
		NetUtil.sendPost(UrlUtil.URL_ferdlsevent,
				"oper=[" + event + "]&code=" + gu.getCode() + "&uid="
						+ MyApplication.getInstance().self.getId(),
				new NetResult() {

					@Override
					public void result(final String msg) {
						LogUtil.e("sendEventResult", msg);
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {
							@Override
							public void run() {
								try {
									JSONObject object = new JSONObject(msg);
									boolean success = object.getInt(object
											.keys().next()) == 1;
									if (success) {
										showToast("删除成功");
										getData();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
	}

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
					final org.json.JSONObject object = new org.json.JSONObject(
							msg);
					if (object.getInt("code") == 1) {
						post(new Runnable() {
							@Override
							public void run() {
								try {
									org.json.JSONArray arr = object
											.getJSONArray("result");
									for (int i = 0; i < arr.length(); i++) {
										JSONObject obj = arr.getJSONObject(i);
										String key = obj.getString("market")
												+ obj.getString("code");
										for (int j = 0; j < list.size(); j++) {
											OneGu gu = list.get(j);
											if ((gu.getMarket() + gu.getCode())
													.equals(key)) {
												JSONArray array = obj
														.getJSONArray("data");
												gu.setNow(array.optDouble(0, 0));
												gu.setKaipanjia(array.optDouble(1,0));
												gu.setChange((float) array
														.optDouble(9, 0));
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
		},true);
	}
}