package com.abct.tljr.chart;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.FutureAskViewAdapter.AskBean;
import com.abct.tljr.dialog.EditDialog;
import com.qh.common.listener.CompleteStr;
import com.qh.common.listener.NetResult;
import com.qh.common.ui.widget.ProgressDlgUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class FutureAskView implements OnRefreshListener {
	Activity activity;
	View RealTimeView;
	ListView listview;
	public static ArrayList<AskBean> array;
	public FutureAskViewAdapter adapter;
	private int page = 1;
	private boolean nomore = false;
	private SwipeRefreshLayout chartedit_refresh;
	
	public FutureAskView(Activity activity) {
		this.activity = activity;
		RealTimeView = activity.getLayoutInflater().inflate(R.layout.tljr_chart_edit, null);
		chartedit_refresh=(SwipeRefreshLayout)RealTimeView.findViewById(R.id.chartedit_refresh);
		chartedit_refresh.setOnRefreshListener(this);
		chartedit_refresh.setColorSchemeResources(android.R.color.holo_red_light);
		findView();
	}

	public View getView() {
		return RealTimeView;
	}

	private void findView() {
		RealTimeView.findViewById(R.id.bedown).setVisibility(View.GONE);
		RealTimeView.findViewById(R.id.write).setOnClickListener(onclick);
		listview = (ListView) RealTimeView.findViewById(R.id.list);
		
		listview.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState==OnScrollListener.SCROLL_STATE_IDLE){
					if(view.getLastVisiblePosition()==view.getCount()-1){
						if (!nomore) {
							page++;
							ProgressDlgUtil.showProgressDlg("",activity);
							GetAskList(true);
						} else {
							ProgressDlgUtil.stopProgressDlg();
							//listview.setLoadMoreSuccess();
						}
					}
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				
			}
		});
		
//		listview.startLoadMore();
//
//		SimpleHeader header = new SimpleHeader(activity);
//		header.setTextColor(0xffeb5041);
//		header.setCircleColor(0xffeb5041);
//		listview.setHeadable(header);
//
//		// 设置加载更多的样式（可选）
//		SimpleFooter footer = new SimpleFooter(activity);
//		footer.setCircleColor(0xffeb5041);
//		listview.setFootable(footer);
//		listview.setOnRefreshStartListener(new OnStartListener() {
//			@Override
//			public void onStart() {
//				page = 1;
//				GetAskList(false);
//			}
//		});
//		listview.setOnLoadMoreStartListener(new OnStartListener() {
//			@Override
//			public void onStart() {
//				if (!nomore) {
//					page++;
//					GetAskList(true);
//				} else {
//					listview.setLoadMoreSuccess();
//				}
//			}
//		});
		array = new ArrayList<AskBean>();
		GetAskList(false);
	}

	private OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.write:
				new EditDialog(activity, "个股问答", "", new CompleteStr() {
					@Override
					public void completeStr(String msg) {
						if (!msg.equals("")) {
							Ask(msg);
						}
					}
				}).show();
				break;
			default:
				break;
			}
		}
	};

	// 提问
	public void Ask(String msg) {
		try {

			
			NetUtil.sendPost(UrlUtil.URL_chat_ask, "uid="+MyApplication.getInstance().self.getId()+"&msg="+msg, new NetResult() {

				@Override
				public void result(final String msg) {

					handler.post(new Runnable() {

						@Override
						public void run() {
							if (msg.equals("error")) {
								Toast.makeText(activity, "提问失败，请保持网络畅通或咨询客服", Toast.LENGTH_LONG).show();
							} else {
								try {
									JSONObject js = new JSONObject(msg);
									if (js.getInt("status") == 1) {
										Toast.makeText(activity, "提问成功", Toast.LENGTH_LONG).show();
										handler.sendEmptyMessage(1);
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
//			params.addBodyParameter("uid", MyApplication.getInstance().self.getId());
//			params.addBodyParameter("msg", msg);
//			XUtilsHelper.sendPost(UrlUtil.URL_chat_ask, params, new HttpCallback() {
//
//				@Override
//				public void callback(String msg) {
//					if (msg.equals("error")) {
//						Toast.makeText(activity, "提问失败，请保持网络畅通或咨询客服", Toast.LENGTH_LONG).show();
//					} else {
//						try {
//							JSONObject js = new JSONObject(msg);
//							if (js.getInt("status") == 1) {
//								Toast.makeText(activity, "提问成功", Toast.LENGTH_LONG).show();
//								handler.sendEmptyMessage(1);
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取提问列表
	public void GetAskList(final boolean isadd) {
		try {
			NetUtil.sendGet(UrlUtil.Url_chat_getask,
					"page=" + page + "&size=10" + "&uid=" + MyApplication.getInstance().self.getId(), new NetResult() {
						@Override
						public void result(String msg) {
							if (!msg.equals("error")) {
								try {
									JSONObject js = new JSONObject(msg);
									if (js.getInt("status") == 1) {
										JSONArray arr = js.getJSONArray("msg");
										ArrayList<AskBean> list = new ArrayList<AskBean>();
										for (int i = 0; i < arr.length(); i++) {
											JSONObject ob = arr.getJSONObject(i);
											AskBean ask = new AskBean();
											ask.answers = ob.getString("answers");
											ask.date = ob.getLong("date") * 1000;
											ask.focus = ob.getString("focus");
											ask.unfocus = ob.getString("step");
											ask.id = ob.getString("id");
											ask.msg = ob.getString("msg");
											ask.nickname = ob.getString("nickname");
											ask.isfocus = ob.getBoolean("isfocus");
											ask.isunfocus = ob.getBoolean("isstep");
											ask.uid = ob.getString("uid");
											list.add(ask);
										}
										if (isadd) {
											if (list.size() == 0) {
												nomore = true;
											} else {
												array.addAll(list);
											}
											handler.sendEmptyMessage(2);
										} else {
											nomore = false;
											array.clear();
											array.addAll(list);
											handler.sendEmptyMessage(0);
										}
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (ChartActivity.islive) {
				switch (msg.what) {
				case 0:
					adapter = new FutureAskViewAdapter(activity, array);
					listview.setAdapter(adapter);
					RealTimeView.findViewById(R.id.jiazai).setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
//					listview.setRefreshSuccess();
					chartedit_refresh.setRefreshing(false);
					ProgressDlgUtil.stopProgressDlg();
					break;
				case 1:
					GetAskList(false);
					break;
				case 2:
					if (adapter != null && listview != null) {
						adapter.notifyDataSetChanged();
//						listview.setLoadMoreSuccess();
						chartedit_refresh.setRefreshing(false);
						ProgressDlgUtil.stopProgressDlg();
					}
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	public void onRefresh() {
		page = 1;
		GetAskList(false);
	}

}