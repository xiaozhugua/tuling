package com.abct.tljr.shouye;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.zixuan.TljrZiXuanLineChart;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.shouye.SYChart.Beanchart;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends BaseActivity {
	HorizontalScrollView scrollView;
	LinearLayout layout;
	private Activity context;
	private String type;
	private EditText et;
	private int width;// cursor 的宽度
	// private View cursor;
	private View now;
	private RecyclerView lv;
	private int lastVisibleItem;
	private ListAdapter adapter;
	private LinearLayoutManager manager;
	private String currentSearchTip;
	private InputMethodManager inputMethodManager;
	private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);
	private boolean isFlush = false;
	private ArrayList<OneGu> guList = new ArrayList<OneGu>();
	private ArrayList<OneGu> list = new ArrayList<OneGu>();
	private JSONArray buildType;
	public static final String url = UrlUtil.Url_apicavacn + "tools/index/0.2";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.tljr_activity_search);
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		scrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
		layout = (LinearLayout) scrollView.getChildAt(0);
		getBuildType(new Complete() {
			@Override
			public void complete() {
				post(new Runnable() {
					@Override
					public void run() {
						initView();
					}
				});
			}
		});
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		lv = (RecyclerView) findViewById(R.id.tljr_lv_gplb);
		et = (EditText) findViewById(R.id.tljr_edit_search);
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				currentSearchTip = s.toString();
				showSearchTip(currentSearchTip);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				/* 判断是否是“Serch”键 */
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					hideSoftInput();
					currentSearchTip = et.getText().toString();
					showSearchTip(et.getText().toString());
					return true;
				}
				return false;
			}
		});
		findViewById(R.id.tljr_img_search).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideSoftInput();
				currentSearchTip = et.getText().toString();
				showSearchTip(et.getText().toString());
			}
		});
		manager = new LinearLayoutManager(this);
		lv.setLayoutManager(manager);
		adapter = new ListAdapter();
		lv.setAdapter(adapter);
		lv.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
					loadMore();
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = manager.findLastVisibleItemPosition();
			}

		});
		loadMore();
	}

	public void showSearchTip(String newText) {
		if (type != null)
			schedule(new SearchTipThread(newText), 1000);
	}

	class SearchTipThread implements Runnable {
		String newText;

		public SearchTipThread(String newText) {
			this.newText = newText;
		}

		public void run() {
			// keep only one thread to load current search tip, u can get data
			// from network here
			if (newText != null && newText.equals(currentSearchTip)) {
				handler.sendMessage(handler.obtainMessage(3, newText));
			}
		}
	}

	public ScheduledFuture<?> schedule(Runnable command, long delayTimeMills) {
		return scheduledExecutor.schedule(command, delayTimeMills, TimeUnit.MILLISECONDS);
	}


	private void addmove(final boolean isadd, int position) {
		OneGu gu = list.get(position);
		if (isadd) {
			Beanchart bean = new Beanchart();
			bean.setId(gu.getId());
			bean.setMarket(gu.getMarket());
			bean.setName(gu.getName());
			bean.setCode(gu.getCode());
			SYChart.arrlist.add(bean);
		} else {
			for (int i = 0; i < SYChart.arrlist.size(); i++) {
				Beanchart bean = SYChart.arrlist.get(i);
				if (bean != null) {
					if (bean.getId() == null) {
						SYChart.arrlist.remove(i);
						break;
					} else if (gu != null && bean.getId().equals(gu.getId())) {
						SYChart.arrlist.remove(i);
						break;
					}
				}
			}
		}
		String url = UrlUtil.URL_addremove + MyApplication.getInstance().self.getId();
		if (isadd) {
			url = url + "/add/" + list.get(position).getId();
		} else {
			url = url + "/remove/" + list.get(position).getId();
		}
		ProgressDlgUtil.showProgressDlg("", context);
		HttpRequest.sendPost(url, "", new HttpRevMsg() {
			@Override
			public void revMsg(String msg) {
				Log.d("addmove", "addmove :" + msg);
				ProgressDlgUtil.stopProgressDlg();
				JSONObject ob;
				try {
					ob = new JSONObject(msg);
					if (isadd) {
						if (ob.getInt("code") == 200) {
							handler.sendEmptyMessage(4);
						} else {
							handler.sendEmptyMessage(5);
						}
					} else {
						if (ob.getInt("code") == 200) {
							handler.sendEmptyMessage(6);
						} else {
							handler.sendEmptyMessage(7);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		TljrZiXuanLineChart.mSYChart.initView();
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		super.handleMsg(msg);

		switch (msg.what) {
		case 1:
			ProgressDlgUtil.showProgressDlg("", this);
			break;
		case 2:
			ProgressDlgUtil.stopProgressDlg();
			break;
		case 3:
			initAdapter((String) msg.obj);
			break;
		case 4:
			Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
			break;
		case 5:
			Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
			break;
		case 6:
			Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
			break;
		case 7:
			Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	/**
	 * hide soft input
	 */
	private void hideSoftInput() {

	}

	/**
	 * hide soft input
	 */
	private void showSoftInput() {
		if (inputMethodManager != null) {
			View v = context.getCurrentFocus();
			if (v == null) {
				return;
			}
		}
	}

	private void loadMore() {
		if (isFlush) {
			return;
		}
		isFlush = true;
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<OneGu> alist = new ArrayList<OneGu>();
				for (int i = 0; i < 20; i++) {
					if (i < guList.size()) {
						alist.add(guList.get(i));
						if (i == 19) {
							lv.stopScroll();
							break;
						}
					} else {
						lv.stopScroll();
					}
				}
				list.addAll(alist);
				guList.removeAll(alist);
				alist = null;
				isFlush = false;
				lv.stopScroll();
				adapter.notifyDataSetChanged();
			}
		});
	}

	private String parms;

	@SuppressLint("DefaultLocale")
	private void initAdapter(String input) {
		String params = "typeId=" + type + "&search=" + input;
		if (params.equals(parms)) {
			return;
		}
		parms = params;
		ProgressDlgUtil.showProgressDlg("", this);
		HttpRequest.sendPost(UrlUtil.Url_apicavacn + "tools/index/0.2" + "/search", params, new HttpRevMsg() {
			@Override
			public void revMsg(final String msg) {
				LogUtil.e("initAdapter", msg);
				ProgressDlgUtil.stopProgressDlg();
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONArray array = new JSONObject(msg).getJSONArray("result");
							// if (array.length() > 0) {
							guList.clear();
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								OneGu gu = new OneGu();
								gu.setCode(object.getString("code"));
								gu.setName(object.getString("name"));
								gu.setId(object.getString("productId"));
								gu.setPyName(object.getString("simaple"));
								JSONObject obj = object.getJSONObject("type");
								gu.setMarket(obj.getString("market"));
								gu.setTag(obj.getString("type"));
								guList.add(gu);
							}
							list.clear();
							loadMore();
							// }
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	int widthdex = 90;

	// for (int i = 0; i < array.length(); i++) {
	// buildType.put(array.getJSONObject(i).optString("type"),
	// array.getJSONObject(i).optString("name"));
	// }
	private void initView() {
		if (buildType == null || buildType.length() == 0) {
			return;
		}
		layout.removeAllViews();
		int w = (Util.WIDTH - widthdex) / 3;
		width = w;
		for (int i = 0; i < buildType.length(); i++) {
			try {
				String key = buildType.getJSONObject(i).optString("type");
				if (i == 0)
					type = key;
				// TextView view = new TextView(SearchActivity.this);
				View view = getLayoutInflater().inflate(R.layout.text_down_red, null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, LayoutParams.FILL_PARENT);
				view.setLayoutParams(params);
				((TextView) view.findViewById(R.id.content)).setText(buildType.getJSONObject(i).optString("name"));
				view.setTag(key);
				// view.setGravity(Gravity.CENTER);
				// view.setText(buildType.get(key));
				// view.setTag(key);
				// view.setTextColor(Color.BLACK);
				layout.addView(view);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						changeTextColor(arg0);
						SearchActivity.this.type = (String) arg0.getTag();
						// tabChangedArrow(m);
						handler.sendMessage(handler.obtainMessage(3, ""));
						LogUtil.e("click", (String) arg0.getTag());
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		scrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_UP:
					scrollX = scrollView.getScrollX();
					detectScrollX();
					break;
				}
				return false;
			}
		});
		showSoftInput();
		View arg0 = layout.getChildAt(0);
		changeTextColor(arg0);
		SearchActivity.this.type = (String) arg0.getTag();
		// tabChangedArrow(0);
		handler.sendMessage(handler.obtainMessage(3, ""));
	}

	private int scrollX;

	private void detectScrollX() {
		postDelayed(new Runnable() {
			@Override
			public void run() {
				int tempScrollX = scrollView.getScrollX();
				if (tempScrollX != scrollX) {
					scrollX = tempScrollX;
					detectScrollX();
				} else {
					int page = 0;
					if (tempScrollX > (Util.WIDTH - widthdex) / 6) {
						page = tempScrollX / (Util.WIDTH - widthdex) + 1;
					}
					int m = (int) (tempScrollX + (Util.WIDTH - widthdex) / 6) / ((Util.WIDTH - widthdex) / 3 - 10);
					LogUtil.i("DATA", "scroll x :" + (m * ((Util.WIDTH - widthdex))) + " m:" + m);
					scrollView.smoothScrollTo(m * ((Util.WIDTH - widthdex) / 3), 0);

					// if((Integer) cursor.getTag() < layout.getChildCount()){
					//
					// }
					// LogUtil.i("DATA", "position :"+(Integer)
					// cursor.getTag()+" la.c :"+layout.getChildCount());
					// View v = layout.getChildAt(m + (Integer)
					// cursor.getTag());
					// SearchActivity.this.type = (String) v.getTag();
					handler.sendMessage(handler.obtainMessage(3, ""));
					// changeTextColor((TextView) v);
				}
			}
		}, 200);
	}

	private void changeTextColor(View view) {
		if (SearchActivity.this.now != null) {
			((TextView) SearchActivity.this.now.findViewById(R.id.content)).setTextColor(Color.BLACK);
			SearchActivity.this.now.findViewById(R.id.check).setVisibility(View.GONE);
		}
		((TextView) view.findViewById(R.id.content)).setTextColor(Color.RED);
		view.findViewById(R.id.check).setVisibility(View.VISIBLE);
		SearchActivity.this.now = view;
	}

	// 获取类型列表
	private void getBuildType(final Complete... complete) {
		if (buildType != null) {
			for (Complete c : complete) {
				c.complete();
			}
			return;
		}
		ProgressDlgUtil.showProgressDlg("", this);
		LogUtil.e("getBuildType", url + "/homeline/list");
		HttpRequest.sendPost(url + "/homeline/list", "", new HttpRevMsg() {
			@Override
			public void revMsg(final String msg) {
				LogUtil.e("getBuildType", msg);
				ProgressDlgUtil.stopProgressDlg();
				try {
					buildType = new JSONObject(msg).getJSONArray("result");
					if (buildType.length() > 0) {
						for (Complete c : complete) {
							c.complete();
						}
					} else {
						showToast("获取服务器类型失败,请重试!");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tljr_item_search, parent, false));
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, final int position) {
			OneGu gu = list.get(position);
			holder.code.setText(gu.getCode());
			holder.name.setText(gu.getName());
			holder.addView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox b = (CheckBox) v.findViewById(R.id.tljr_img_search_jiahao);
					if (b.isChecked()) {
						b.setChecked(false);
						addmove(false, position);
					} else {
						OneGu gu = list.get(position);
						if (SYChart.arrlist.size() >= 20) {
							b.setChecked(false);
							showToast("已达最大添加个数！");
							return;
						}
						for (int i = 0; i < SYChart.arrlist.size(); i++) {
							if (SYChart.arrlist.get(i).getId() != null
									&& SYChart.arrlist.get(i).getId().equals(gu.getId())) {
								b.setChecked(false);
								showToast("请勿重复添加！");
								return;
							}
						}
						addmove(true, position);
						b.setChecked(true);
					}
					setResult(0);
				}
			});
			for (int i = 0; i < SYChart.arrlist.size(); i++) {
				holder.add.setChecked(false);
				Beanchart beanchart = SYChart.arrlist.get(i);
				if (beanchart.getId() != null && beanchart.getId().equals(gu.getId())) {
					holder.add.setChecked(true);
					break;
				} else if (beanchart.getName().equals(gu.getName())) {
					holder.add.setChecked(true);
					break;
				}
			}
		}

		@Override
		public int getItemCount() {
			return list.size();
		}

		public final class ViewHolder extends RecyclerView.ViewHolder {
			TextView name;
			TextView code;
			CheckBox add;
			View addView;
			OneGu gu;
			boolean isClear;

			public ViewHolder(View v) {
				super(v);
				name = (TextView) v.findViewById(R.id.tljr_txt_search_name);
				code = (TextView) v.findViewById(R.id.tljr_txt_search_code);
				add = (CheckBox) v.findViewById(R.id.tljr_img_search_jiahao);
				addView = v.findViewById(R.id.tljr_grp_search_jiahao);
			}
		}
	}
}
