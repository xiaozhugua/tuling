package com.abct.tljr.shouye;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.BaseFragmentActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SYFerdls {
	private Activity activity;
	private Handler handler;
	private View view;
	private ViewPager viewpager = null;
	private List<View> list = null;
	private ImageView[] img = null;
	private HashMap<String, View> viewMap = new HashMap<String, View>();
	private Handler mhandler = null;
	private JSONArray array;

	public JSONArray getArray() {
		return array;
	}

	public View getView() {
		return view;
	}

	@SuppressLint("InflateParams")
	public SYFerdls(Activity activity) {
		this.activity = activity;
		if (activity instanceof BaseActivity) {
			handler = ((BaseActivity) activity).handler;
		} else if (activity instanceof BaseFragmentActivity) {
			handler = ((BaseFragmentActivity) activity).handler;
		} else {
			handler = new Handler();
		}
		view = activity.getLayoutInflater().inflate(R.layout.tljr_view_ferdls, null);
		viewpager = (ViewPager) view.findViewById(R.id.tljr_ferdls_viewpager);
		view.findViewById(R.id.tljr_ferdls_title).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SYFerdls.this.activity.startActivity(new Intent(SYFerdls.this.activity, AllFerdlsActivity.class));
			}
		});
		reflush();
	}

	public void setHandler(Handler mhandler) {
		this.mhandler = mhandler;
	}

	public void reflush(final Complete... c) {
		NetUtil.sendPost(UrlUtil.URL_ferdls, "", new NetResult() {
			@Override
			public void result(final String msg) {
				if (msg.equals("")) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							reflush();
						}
					}, 1000);
					return;
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject jsonObject = new JSONObject(msg);
							if (jsonObject.getInt("code") == 1) {
								array = jsonObject.getJSONArray("data");
								initView();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				for (Complete complete : c) {
					complete.complete();
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		int size = array.length();
		if (size == 0) {
			return;
		}
		size = array.length();
		((TextView) view.findViewById(R.id.count)).setText("(" + size + ")");
		viewpager.getLayoutParams().height = (int) (Util.HEIGHT * 278 / Util.IMAGEHEIGTH);
		list = new ArrayList<View>();
		size = (size > 30 ? 30 : size);
		for (int i = 0; i < size; i += 6) {
			list.add(getOneGrid(i, array));
		}
		img = new ImageView[list.size()];
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.tljr_ferdls_viewGroup);
		layout.getLayoutParams().height = (int) (Util.HEIGHT * 35 / Util.IMAGEHEIGTH);
		layout.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			img[i] = new ImageView(activity);
			if (0 == i) {
				img[i].setBackgroundResource(R.drawable.img_yuandian1);
			} else {
				img[i].setBackgroundResource(R.drawable.img_yuandian2);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.leftMargin = 15;
			params.width = 15;
			params.height = 15;
			layout.addView(img[i], params);
		}

		adapter.notifyDataSetChanged();
		if (viewpager.getAdapter() == null) {
			viewpager.setAdapter(adapter);
			viewpager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageScrollStateChanged(int state) {
				}

				@Override
				public void onPageScrolled(int page, float positionOffset, int positionOffsetPixels) {
				}

				@Override
				public void onPageSelected(int page) {
					Constant.addClickCount();
					check(page);
				}
			});
		}
		ProgressDlgUtil.stopProgressDlg();
		setMessage();
	}

	private void check(int page) {
		for (int i = 0; i < list.size(); i++) {
			if (page == i) {
				img[i].setBackgroundResource(R.drawable.img_yuandian1);
			} else {
				img[i].setBackgroundResource(R.drawable.img_yuandian2);
			}
		}
	}

	private void changeText(TextView view) {
		String str = view.getText().toString();
		if (str.length() < 3) {
			return;
		}
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new AbsoluteSizeSpan(16, true), 0, str.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(style);

	}

	private GridView getOneGrid(int i, JSONArray array) {
		int space = Util.WIDTH * 18 / Util.IMAGEWIDTH;
		GridView layout = new GridView(activity);
		layout.setNumColumns(3);
		layout.setPadding(space, 0, space, 0);
		layout.setVerticalScrollBarEnabled(false);
		layout.setHorizontalSpacing(space);
		layout.setVerticalSpacing(space);
		final ArrayList<View> list = new ArrayList<View>();
		try {
			for (int j = 0; j < 6 && i + j < array.length(); j++) {
				final JSONObject object = array.getJSONObject(i + j);
				View view = View.inflate(activity, R.layout.tljr_item_ferdls, null);
				view.setTag(object);
				viewMap.put(object.optString("market") + object.optString("code"), view);
				if (!object.optString("iconUrl").equals("")) {
					StartActivity.imageLoader.displayImage(object.optString("iconUrl"),
							(ImageView) view.findViewById(R.id.tljr_item_ferdls_icon), StartActivity.options);
				}
				view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT,
						(int) (Util.HEIGHT * 128 / Util.IMAGEHEIGTH)));
				((TextView) view.findViewById(R.id.tljr_item_ferdls_name)).setText(object.optString("name"));
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(activity, FerdlsActivity.class);
						intent.putExtra("info", object.toString());
						activity.startActivity(intent);
					}
				});
				list.add(view);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		layout.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				return list.get(position);
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
		});
		return layout;
	}

	PagerAdapter adapter = new PagerAdapter() {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position));
			return list.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}
	};

	// 自动刷新listview数据
	public void reflushData() {
		GridView v = (GridView) list.get(viewpager.getCurrentItem());
		if (v.getChildCount() == 0) {
			return;
		}
		// 获取股票列表参数
		String parm = "list=";
		for (int i = 0; i < v.getChildCount(); i++) {
			View view = v.getChildAt(i);
			JSONObject object = (JSONObject) view.getTag();
			if (i == 0) {
				parm += (object.optString("market") + "|" + object.optString("code"));
			} else {
				parm += ("," + object.optString("market") + "|" + object.optString("code"));
			}
		}
		// 网上获取最新数据
		Util.getRealInfo(parm, new NetResult() {
			@Override
			public void result(final String msg) {
				try {
					final org.json.JSONObject object = new org.json.JSONObject(msg);
					if (object.getInt("code") == 1) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								try {
									org.json.JSONArray arr = object.getJSONArray("result");
									for (int i = 0; i < arr.length(); i++) {
										JSONObject obj = arr.getJSONObject(i);
										String key = obj.getString("market") + obj.getString("code");
										JSONArray array = obj.getJSONArray("data");
										if (viewMap.containsKey(key)) {
											View v = viewMap.get(key);
											float change = (float) array.optDouble(9, 0);
											TextView view = (TextView) v.findViewById(R.id.tljr_item_ferdls_gain);
											if (array.optDouble(0, 0) == 0 && array.optDouble(1) != 0) {
												view.setText("停牌");
												view.setTextColor(activity.getResources().getColor(R.color.tljr_gray));
											} else {
												view.setText((change > 0 ? "+" : "") + change + "%");
												view.setTextColor(change > 0 ? ColorUtil.red : ColorUtil.green);
												changeText(view);
											}
										}
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

	public void setMessage() {
		if (mhandler != null) {
			Message message = Message.obtain();
			message.what = 2;
			mhandler.sendMessage(message);
		}
	}
}