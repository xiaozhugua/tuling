package com.abct.tljr.news;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.news.adapter.CommentAdapter;
import com.abct.tljr.news.bean.Comment;
import com.abct.tljr.news.bean.Reply;
import com.abct.tljr.news.fragment.DetailNewsFragment;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.CommonApplication;
import com.qh.common.listener.NetResult;
import com.qh.common.util.InputTools;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class NewsCommentActivity extends BaseActivity {
	SwipeRefreshLayout refreshLayout;
	ListView listview;
	CommentAdapter adapter;
	String Tag = "CommentActivity";
	private boolean startLoadMore = true;
	private int index = 0;
	public String news_id, news_species, news_time;

	private ArrayList<Comment> list = new ArrayList<Comment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_comment);
		news_id = getIntent().getStringExtra("id");
		news_species = getIntent().getStringExtra("species");
		news_time = getIntent().getStringExtra("time");

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		listview = (ListView) findViewById(R.id.listview);
		adapter = new CommentAdapter(this, list, listview, news_id);
		listview.setAdapter(adapter);
		refreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				LogUtil.i(Tag, "------Refresh-------");

				refresh();

			}

		});

		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView v, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (visibleItemCount + firstVisibleItem == totalItemCount && listview.getChildCount() > 16) {

					if (startLoadMore) {
						LogUtil.i(Tag, "允许上拉加载条件----");
						list.clear();
						getComment();
						index++;
						LogUtil.i(Tag, "------LoadMore-------");
					}
					startLoadMore = false;
				}

			}
		});
		getComment();

		((RelativeLayout) findViewById(R.id.tljr_img_news_back)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	public void showCmtDialog(Comment cmt) {
		SpeakDialog sp = new SpeakDialog(this);
		sp.setComment(cmt);
		sp.show();
	}

	public void refresh() {
		index = 0;
		getComment();
		startLoadMore = true;
	}

	public void getComment() {

		String url = UrlUtil.URL_new + "api/uc/cget";
		String params = "oper=2" + "&id=" + news_id + "&index=" + index;

		LogUtil.i(Tag, "getDate URL :" + url + "?" + params);
		NetUtil.sendPost(url, params, new NetResult() {

			@Override
			public void result(final String msg) {

				handler.post(new Runnable() {

					@Override
					public void run() {

						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									ArrayList<Comment> tempList = new ArrayList<Comment>();
									LogUtil.i(Tag, msg);

									// newest_comment.setVisibility(View.VISIBLE);
									JSONObject allJson = new JSONObject(msg);

									if (allJson == null) {

										return;
									}

									String status = allJson.getString("status");
									if (!status.equals("1")) {
										return;
									}
									JSONObject commentObject = allJson.getJSONObject("joData");
									JSONArray array = commentObject.getJSONArray("data");
									if (array == null || array.length() <= 0) {
										// newest_comment.setVisibility(View.GONE);
										return;
									} else {
										// newest_comment.setVisibility(View.VISIBLE);
									}
									if (index == 0) {
										list.clear();
									}

									for (int i = 0; i < array.length(); i++) {

										Comment cmt = new Comment();

										cmt.setContent(array.getJSONObject(i).optString("comment"));
										cmt.setId(array.getJSONObject(i).getString("id"));

										cmt.setTime(HuanQiuShiShi.getStandardDate(array.getJSONObject(i).optLong("insertTime")));
										cmt.setNewsId(array.getJSONObject(i).optString("nid"));
										cmt.setSpecies(array.getJSONObject(i).optString("species"));
										cmt.setUser_id(array.getJSONObject(i).optString("uid"));

										cmt.setName(array.getJSONObject(i).optString("nickname"));

										cmt.setAurl(array.getJSONObject(i).optString("avatar", "default"));

										cmt.setPraise(array.getJSONObject(i).optString("likes", "0"));

										JSONArray childArray = array.getJSONObject(i).getJSONArray("replys");
										if (childArray != null && childArray.length() > 0) {
											Reply[] replys = new Reply[childArray.length()];
											for (int g = 0; g < childArray.length(); g++) {
												JSONObject ob = childArray.getJSONObject(g);
												Reply reply = new Reply();
												reply = new Reply();
												reply.setReply(ob.getString("reply"));
												reply.setNickname(ob.getString("nickname"));
												replys[g] = reply;
											}
											cmt.setReply(replys);
										}

										tempList.add(cmt);

										adapter.notifyDataSetChanged();
										if (index == 0) {
											refreshLayout.setRefreshing(false);
										}
										startLoadMore = true;
									}
									list.addAll(tempList);
									int next = commentObject.optInt("next");
									if (next == -1) {
										startLoadMore = false;
									} else {
										startLoadMore = true;
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								
								
								
							}
						});

					}
				});

			}
		});
	}

}

class SpeakDialog extends Dialog implements OnClickListener {
	private NewsCommentActivity activity;
	private EditText et;
	private Comment comment;

	public SpeakDialog(NewsCommentActivity activity) {
		super(activity, R.style.dialog);
		this.activity = activity;
		setContentView(R.layout.tljr_dialog_speak);
		setCanceledOnTouchOutside(false);
		init();
		windowDeploy();
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	// 设置窗口显示
	public void windowDeploy() {
		Window win = getWindow(); // 得到对话框
		win.setWindowAnimations(R.style.speakdialog_bottom); // 设置窗口弹出动画
		win.getDecorView().setPadding(0, 0, 0, 0); // 宽度占满，因为style里面本身带有padding
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);

	}

	private void init() {

		findViewById(R.id.tljr_img_speak_fanhui).setOnClickListener(this);
		findViewById(R.id.tljr_btn_speak).setOnClickListener(this);
		et = (EditText) findViewById(R.id.tljr_et_speak_msg);
		// et.setFocusable(true);

		activity.handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				InputTools.ShowKeyboard(et);
			}
		}, 400);

	}

	private void speak() {
		if (System.currentTimeMillis() - DetailNewsFragment.PUBLISH_COMMENT_TIME < 15 * 1000) { // 15秒发言时间间隔
			activity.showToast("太快了，休息下吧");
			return;
		}
		String s = et.getText().toString().trim();
		if (s.equals("")) {
			activity.showToast("请输入评论内容");
			return;
		}
		if (MyApplication.getInstance().self == null) {
			activity.showToast("未登录或注册无法完成操作");
			activity.login();
			return;
		}
		// String params = "comment=" + s + "&pId=" +
		// MyApplication.getInstance().self.getId() + "&nId="
		// + fragment_NewsDetails.news.getId() + "&timeKey=" +
		// fragment_NewsDetails.news.getTime()
		// + "&sp=" + fragment_NewsDetails.news.getLetterSpecies() +
		// "&type=1&name="
		// + MyApplication.getInstance().self.getNickName();

		String url = "";
		String params = "";
		String uid = CommonApplication.getInstance().self.getId();
		if (comment == null) {
			url = UrlUtil.URL_new + "api/uc/cadd";
			params = "uid=" + uid + "&oper=1&comment=" + s + "&species=" + activity.news_species + "&id=" + activity.news_id + "&time=" + activity.news_time;
		} else {
			url = UrlUtil.URL_new + "api/uc/cadd";
			params = "uid=" + uid + "&oper=2&reply=" + s + "&cid=" + comment.getId() + "&id=" + comment.getNewsId();
		}

		LogUtil.i("NewsDetails", url + "?" + params);
		ProgressDlgUtil.showProgressDlg("", activity);
		NetUtil.sendPost(url, params, new NetResult() {
			// NetUtil.sendPost(UrlUtil.URL_comment, params, new NetResult() {

			@Override
			public void result(final String msg) {

				activity.handler.post(new Runnable() {

					@Override
					public void run() {

						try {
							LogUtil.i("NewsDetails", "msg:" + msg);

							JSONObject obj = new JSONObject(msg);
							if (obj != null) {
								String status = obj.optString("status");
								if (status.equals("1")) {
									Toast.makeText(activity, obj.optString("msg"), Toast.LENGTH_SHORT).show();
									dismiss();
									activity.refresh();
									;
									InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
									DetailNewsFragment.PUBLISH_COMMENT_TIME = System.currentTimeMillis();
									if (comment != null) {
										comment = null;
									}
								} else {
									Toast.makeText(activity, obj.optString("msg"), Toast.LENGTH_SHORT).show();
								}

							}
							ProgressDlgUtil.stopProgressDlg();
							InputTools.HideKeyboard(et);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
					}
				});
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tljr_img_speak_fanhui:
			InputTools.HideKeyboard(et);
			this.dismiss();
			break;
		case R.id.tljr_btn_speak:
			speak();
			break;
		default:
			break;
		}
	}

}