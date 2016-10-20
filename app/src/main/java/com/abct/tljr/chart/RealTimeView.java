package com.abct.tljr.chart;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.RealTimeAdapter.RealBean;
import com.abct.tljr.data.Constant;
import com.abct.tljr.dialog.EditDialog;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.CompleteStr;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class RealTimeView implements OnRefreshListener {

	Activity activity;
	View RealTimeView;
	ListView listview;
	ArrayList<RealBean> array;
	private RealTimeAdapter adapter;
	private int loadingsize = 10;
	private String TAG = "RealTimeView";
	private Handler ChartAcHandler;
	public static String im_realtimeid = "";
	private boolean nomore = false;
	private SwipeRefreshLayout mSwipeRefreshLayout=null;
	
	public RealTimeView(Activity activity, Handler handler) {
		this.activity = activity;
		ProgressDlgUtil.showProgressDlg("", activity);
		RealTimeView = activity.getLayoutInflater().inflate(R.layout.tljr_chart_realtime, null);
		RealTimeView.findViewById(R.id.write).setOnClickListener(onclick);
		mSwipeRefreshLayout=(SwipeRefreshLayout)RealTimeView.findViewById(R.id.realtime_refresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
		findView();
		Ask(10,"", 1, false, false);
		ChartAcHandler = handler;
	}

	public View getView() {
		return RealTimeView;
	}

	private void findView() {
		listview = (ListView) RealTimeView.findViewById(R.id.list);
		
		if (MyApplication.getInstance().self.isIsadmin()) {
			RealTimeView.findViewById(R.id.write).setVisibility(View.VISIBLE);
		} else {
			RealTimeView.findViewById(R.id.write).setVisibility(View.GONE);
		}
		
		array = new ArrayList<RealBean>();
	}

	public Runnable run = new Runnable() {
		@Override
		public void run() {
			((ChartActivity) activity).reflushDP("sh", "000001", ((ChartActivity) activity).hushenview);
			((ChartActivity) activity).reflushDP("sz", "399001", ((ChartActivity) activity).hushenview);
			((ChartActivity) activity).reflushDP("sz", "399006", ((ChartActivity) activity).zhongchuanview);
			((ChartActivity) activity).reflushDP("sz", "399005", ((ChartActivity) activity).zhongchuanview);
			if (array.size() > 0) {
				Ask(10, array.get(0).id, 1, true, false);
			} else {
				Ask(10, "", 1, false, false);
			}
			if (((ChartActivity) activity).weseeview.array.size() > 0) {
				((ChartActivity) activity).weseeview.Ask(10,
						((ChartActivity) activity).weseeview.array.get(((ChartActivity) activity).weseeview.array.size() - 1).id, 1, true, false);
			} else {
				((ChartActivity) activity).weseeview.Ask(10, "", 1, false, false);
			}
			handler.postDelayed(run, 5000);
		}
	};

	// 获得列表
	public void Ask(int size, String id, int direction, final boolean isadd, final boolean needmore) {
		try {
			String param = "size=" + size;
			if (!id.equals("")) {
				param += "&id=" + id + "&direction=" + direction;
			}
			// &mobile= &avatar=
			NetUtil.sendGet(UrlUtil.Url_chat_getchatlist, param, new NetResult() {
				@Override
				public void result(String msg) {
					if (msg.equals("error")) {
						Toast.makeText(activity, "获取失败，请重新刷新", Toast.LENGTH_LONG).show();
						ProgressDlgUtil.stopProgressDlg();
					} else {
					 
						try {
							JSONObject	js = new JSONObject(msg);
							if (js != null && js.optInt("status") == 1) {
								JSONArray arr = js.getJSONArray("msg");
								if (needmore) {
									ArrayList<RealBean> list = new ArrayList<RealTimeAdapter.RealBean>();
									for (int i = 0; i < arr.length(); i++) {
										JSONObject ob = arr.getJSONObject(i);
										RealBean real = new RealBean();
										real.id = ob.optString("id");
										real.uid = ob.optString("uid");
										real.name = ob.optString("nickName");
										real.msg = ob.optString("msg");
										real.time = ob.optLong("date");
										list.add(real);
									}
									if (list.size() > 0) {
										array.addAll(list);
										handler.sendEmptyMessage(4);
									} else {
										nomore = false;
									}
								} else {
									if (isadd) {
										for (int i = arr.length() - 1; i >= 0; i--) {
											JSONObject ob = arr.getJSONObject(i);
											RealBean real = new RealBean();
											real.id = ob.optString("id");
											real.uid = ob.optString("uid");
											real.name = ob.optString("nickName");
											real.msg = ob.optString("msg");
											real.time = ob.optLong("date");
											array.add(0, real);
										}
										if (arr.length() > 0) {
											handler.sendEmptyMessage(1);
										}
									} else {
										ArrayList<RealBean> list = new ArrayList<RealTimeAdapter.RealBean>();
										for (int i = 0; i < arr.length(); i++) {
											JSONObject ob = arr.getJSONObject(i);
											RealBean real = new RealBean();
											real.id = ob.optString("id");
											real.uid = ob.optString("uid");
											real.name = ob.optString("nickName");
											real.msg = ob.optString("msg");
											real.time = ob.optLong("date");
											list.add(real);
										}
										array.clear();
										array.addAll(list);
										ProgressDlgUtil.stopProgressDlg();
										handler.sendEmptyMessage(0);
									}
								}
							} else {
								ProgressDlgUtil.stopProgressDlg();
								Toast.makeText(activity, "获取信息错误，请重新刷新", Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 发表
	public void send(String msg) {
		try {
			NetUtil.sendPost(UrlUtil.Url_chat_sendchat, "token="+Configs.token+"&uid="+MyApplication.getInstance().self.getId()+"&msg="+msg, new NetResult() {

				@Override
				public void result(final String msg) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							if (msg.equals("error")) {
							} else if (msg.equals("")) {
								handler.sendEmptyMessage(3);
							} else {
								try {
									JSONObject js = new JSONObject(msg);
									if (js != null && js.optInt("status") == 1) {
										handler.sendEmptyMessage(2);
									} else {
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								

							}	
						}
					});

				}
			});
			
			
//			RequestParams params = new RequestParams();
//			params.addBodyParameter("token", Configs.token);
//			params.addBodyParameter("uid", MyApplication.getInstance().self.getId());
//			params.addBodyParameter("msg", msg);
//			XUtilsHelper.sendPost(UrlUtil.Url_chat_sendchat, params, new HttpCallback() {
//				@Override
//				public void callback(String data) {
//					if (data.equals("error")) {
//					} else if (data.equals("")) {
//						handler.sendEmptyMessage(3);
//					} else {
//						try {
//							JSONObject js = new JSONObject(data);
//							if (js != null && js.optInt("status") == 1) {
//								handler.sendEmptyMessage(2);
//							} else {
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						
//
//					}
//				}
//			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (ChartActivity.islive) {
				switch (msg.what) {
				case 0:
					adapter = new RealTimeAdapter(activity, array);
					listview.setAdapter(adapter);
					// adapter.notifyDataSetChanged();
					// listview.setSelection(listview.FOCUS_DOWN);
					Constant.preference.edit().putString("im_realtimeid", array.get(0).id).commit();
					im_realtimeid = array.get(0).id;
					nomore = false;
//					listview.startLoadMore();
//					listview.setRefreshSuccess();
					mSwipeRefreshLayout.setRefreshing(false);
					break;
				case 1:
					Constant.preference.edit().putString("im_realtimeid", array.get(0).id).commit();
					im_realtimeid = array.get(0).id;
					adapter.notifyDataSetChanged();
					// listview.setSelection(listview.FOCUS_DOWN);
					mSwipeRefreshLayout.setRefreshing(false);
					break;
				case 2:
					Toast.makeText(activity, "发送成功", Toast.LENGTH_LONG).show();
					break;
				case 3:
					Toast.makeText(activity, "发送失败，请重新发送或询问服务器", Toast.LENGTH_LONG).show();
					break;
				case 4:
					adapter.notifyDataSetChanged();
//					listview.stopLoadMore();
					mSwipeRefreshLayout.setRefreshing(false);
					break;
				default:
					break;
				}
			}
		};
	};

	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.write:
				// send(edit.getText().toString());
				// edit.setText("");
				new EditDialog(activity, "实时解盘", "", new CompleteStr() {
					@Override
					public void completeStr(String msg) {
						// TODO Auto-generated method stub
						if (!msg.equals("")) {
							send(msg);
						}
					}
				}).show();
				break;
			default:
				break;
			}
		}
	};

	// private TextWatcher warcher = new TextWatcher() {
	//
	// @Override
	// public void onTextChanged(CharSequence arg0, int arg1, int arg2, int
	// arg3) {
	// // TODO Auto-generated method stub
	// if(edit.getText().toString().equals("")){
	// send.setVisibility(View.GONE);
	// }else{
	// send.setVisibility(View.VISIBLE);
	// }
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	// int arg3) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void afterTextChanged(Editable arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	// };

	public static int time = 0;
	public static boolean redpointisshow = false;

	public static void CheckNewIMReal() {
		try {
			if (redpointisshow) {
				return;
			}
			if (ChartActivity.islive) {
				return;
			}
			if (im_realtimeid.equals("")) {
				return;
			}
			time++;
			if (time < 10) {
				return;
			}
			time = 0;
			String param = "size=10&id=" + im_realtimeid + "&direction=1";
			NetUtil.sendGet(UrlUtil.Url_chat_getchatlist, param, new NetResult() {
				@Override
				public void result(String msg) {
					if (msg.equals("error") || msg.equals("")) {
					} else {

						try {
							JSONObject js = new JSONObject(msg);
							if (js != null && js.optInt("status") == 1) {
								JSONArray arr = js.getJSONArray("msg");
								if (arr.length() > 0) {
									MyApplication.getInstance().getMainActivity().mHandler.sendEmptyMessage(95);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRefresh() {
		Ask(10, "", 1, false, false);
	}
}