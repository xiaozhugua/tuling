package com.abct.tljr.ui.activity.tools;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ranking.RankAdapter;
import com.abct.tljr.ranking.RankAdapter.ViewHolder;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.InputTools;
import com.qh.common.util.LogUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年11月2日 上午11:26:39
 */
public class OnlySearchDialog extends Dialog {
	private String type;
	private ArrayList<OneGu> guList = new ArrayList<OneGu>();
	private ArrayList<OneGu> list = new ArrayList<OneGu>();
	private EditText et;
	private RecyclerView listView;
	private LinearLayoutManager manager;
	private int lastVisibleItem;
	private MyHandler handler;
	private RecyclerView.Adapter baseAdapter;
	private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);
	private String currentSearchTip;

	private InputMethodManager inputMethodManager;
	private boolean isFlush = false;
	/*
	 * 透明模糊
	 */
	private View overlayLayout;
	private View contentFrame;
	private Bitmap scaled;

	public void setBlurImage() {
		scaled = null;
		// scaled = BlurUtils.drawViewToBitmap(scaled, contentFrame,
		// contentFrame.getMeasuredWidth(),
		// contentFrame.getMeasuredHeight(), 1, null);
		// Bitmap blured = BlurUtils.apply(context, scaled, 5);
		// Drawable drawable = new BitmapDrawable(blured);
		// overlayLayout.setBackgroundDrawable(drawable);
		// contentFrame.setDrawingCacheEnabled(true);
		// scaled = contentFrame.getDrawingCache();
		// Bitmap blured = BlurUtils.apply(context, scaled, 5);
		// contentFrame.setDrawingCacheEnabled(false);
		// Drawable drawable = new BitmapDrawable(blured);
		// overlayLayout.setBackgroundDrawable(drawable);
	}

	private Activity context;

	public OnlySearchDialog(final Activity context, final HttpRevMsg msg, View contentFrame, String type) {
		super(context, android.R.style.Theme_Light_NoTitleBar);
		setContentView(R.layout.tljr_dialog_serch);
		this.context = context;
		this.contentFrame = contentFrame;
		this.type = type;
		// overlayLayout = findViewById(R.id.bj);
		Window dialogWindow = getWindow();
		dialogWindow.setWindowAnimations(R.style.AnimationPreview); // 设置窗口弹出动画
		dialogWindow.setSoftInputMode(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		p.width = (int) (Util.WIDTH);
		p.height = (int) (Util.HEIGHT);
		dialogWindow.setAttributes(p);
		findViewById(R.id.tljr_txt_close).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hidedialog();
			}
		});
		inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		handler = new MyHandler();
		listView = (RecyclerView) findViewById(R.id.tljr_lv_gplb);
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
		findViewById(R.id.tljr_img_search).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hideSoftInput();
				currentSearchTip = et.getText().toString();
				showSearchTip(et.getText().toString());
			}
		});
		baseAdapter = new RecyclerView.Adapter<ViewHolder>() {
			@Override
			public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
				return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tljr_item_search, parent, false));
			}

			@Override
			public void onBindViewHolder(ViewHolder holder, int position) {
				final OneGu gu = list.get(position);
				holder.itemView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						JSONObject object = new JSONObject();
						try {
							object.put("name", gu.getName());
							object.put("id", gu.getId());
							dismiss();
							msg.revMsg(object.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				holder.code.setText(gu.getPyName());
				holder.name.setText(gu.getName());
			}

			@Override
			public int getItemCount() {
				return list.size();
			}
		};
		// // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
		// SimpleHeader header = new SimpleHeader(context);
		// header.setTextColor(0xff0066aa);
		// header.setCircleColor(0xff33bbee);
		// listView.setHeadable(header);

		// 设置加载更多的样式（可选）
//		SimpleFooter footer = new SimpleFooter(context);
//		footer.setCircleColor(0xff33bbee);
		manager = new LinearLayoutManager(context);
		listView.setLayoutManager(manager);
		listView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == baseAdapter.getItemCount()) {
					loadMore();
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = manager.findLastVisibleItemPosition();
			}

		});
		listView.setAdapter(baseAdapter);
		loadMore();
	}

	public void show(String zuName) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						setBlurImage();
					}
				});
			}
		}).start();
		show();
		showSoftInput();
		handler.sendMessage(handler.obtainMessage(3, ""));
	}

	public boolean isShow() {
		return scaled != null;
	}

	public void hidedialog() {
		hideSoftInput();
		this.hide();
		scaled = null;
	}

	class ViewHolder extends RecyclerView.ViewHolder {
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
			addView.setVisibility(View.GONE);
		}
	}

	public void showSearchTip(String newText) {
		// excute after 500ms, and when excute, judge current search tip and
		// newText
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

	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				ProgressDlgUtil.showProgressDlg("", context);
				break;
			case 2:
				ProgressDlgUtil.stopProgressDlg();
				break;
			case 3:
				initAdapter((String) msg.obj);
				break;
			}
		}
	}

	/**
	 * hide soft input
	 */
	private void hideSoftInput() {
		InputTools.HideKeyboard(et);
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
			InputTools.ShowKeyboard(v);
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
				ArrayList<OneGu> alist = new ArrayList<OneGu>();
				for (int i = 0; i < 20; i++) {
					if (i < guList.size()) {
						alist.add(guList.get(i));
						if (i == 19) {
							listView.stopScroll();
							break;
						}
					} else {
						listView.stopScroll();
					}
				}
				list.addAll(alist);
				guList.removeAll(alist);
				alist = null;
				isFlush = false;
				listView.stopScroll();
				baseAdapter.notifyDataSetChanged();
			}
		});
	}

	@SuppressLint("DefaultLocale")
	private void initAdapter(String input) {
		HttpRequest.sendPost(BuildActivity.url + "/search", "typeId=" + type + "&search=" + input, new HttpRevMsg() {

			@Override
			public void revMsg(final String msg) {
				LogUtil.e("initAdapter", msg);

				handler.post(new Runnable() {

					@Override
					public void run() {
						try {
							JSONArray array = new JSONObject(msg).getJSONArray("result");
							if (array.length() > 0) {
								guList.clear();
								for (int i = 0; i < array.length(); i++) {
									JSONObject object = array.getJSONObject(i);
									OneGu gu = new OneGu();
									gu.setName(object.getString("name"));
									gu.setCode(object.getString("code"));
									gu.setPyName(object.getString("simaple"));
									gu.setId(object.getString("productId"));
									gu.setTag(object.getString("type"));
									guList.add(gu);
								}
								list.clear();
								listView.stopScroll();
								loadMore();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
}
