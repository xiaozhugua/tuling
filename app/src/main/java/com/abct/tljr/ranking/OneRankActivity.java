package com.abct.tljr.ranking;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年8月8日 下午2:21:21
 */
public class OneRankActivity extends BaseActivity implements OnClickListener {
	private RecyclerView lv;
	private ArrayList<OnePerson> list = new ArrayList<OnePerson>();
	private ArrayList<OnePerson> mList = new ArrayList<OnePerson>();
	private String name;
	private String type;
	private int percent;
	private int layout;
	private boolean isFlush = false;
	private int page = 0;
	private int onePageCount = 10;
	private int lastVisibleItem;
	private RankAdapter adapter;
	private LinearLayoutManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_rank_one);
		findViewById(R.id.tljr_img_onerank_fanhui).setOnClickListener(this);
		type = getIntent().getStringExtra("reqkey");
		percent = getIntent().getIntExtra("percent", 0);
		name = getIntent().getStringExtra("key");
		layout = getIntent().getIntExtra("layout", 0);
		if (!getIntent().getStringExtra("time").equals("")) {
			findViewById(R.id.tljr_txt_onerank_desc).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.tljr_txt_onerank_desc)).setText(getIntent().getStringExtra("time"));
		}
		((TextView) findViewById(R.id.tljr_txt_onerank_title)).setText(getIntent().getStringExtra("title") + "(前100名)");
		lv = (RecyclerView) findViewById(R.id.tljr_lv_onerank);
		manager = new LinearLayoutManager(this);
		lv.setLayoutManager(manager);
		adapter = new RankAdapter(this, list, layout, name, percent);
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

	private void getData() {
		page++;
		ProgressDlgUtil.showProgressDlg("", this);
		NetUtil.sendPost(UrlUtil.URL_ranking, "method=" + type + "&page=" + page + "&size=" + onePageCount,
				new NetResult() {

					@Override
					public void result(String msg) {
						ProgressDlgUtil.stopProgressDlg();
						try {
							final JSONObject object = new JSONObject(msg);
							if (object.getInt("status") == 1) {
								final org.json.JSONArray array = object.getJSONArray("msg");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									OnePerson person = new OnePerson();
									person.setName(obj.optString("nickname", obj.optString("desc")));
									person.setZhname(obj.optString("zhname"));
									person.setAvatar(obj.optString("avatar"));
									person.setNumber(list.size() + i + 1);
									person.setFollow(new Random().nextBoolean());
									person.setValue((float) obj.optDouble(type, (float) obj.optDouble("zongshouyi")));
									person.setUid(obj.optString("uid"));
									person.setZhid(obj.optString("zhid", obj.optString("id")));
									person.setTime(obj.optLong("time"));
									mList.add(person);
								}
								post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										if (array.length() > 0) {
											list.addAll(mList);
											mList.clear();
											lv.stopScroll();
											adapter.notifyDataSetChanged();
										} else {
											lv.stopScroll();
											showToast("没有更多数据");
										}
										isFlush = false;
									}
								});
							} else {
								post(new Runnable() {

									@SuppressLint("ShowToast")
									@Override
									public void run() {
										showToast("没有更多数据");
										lv.stopScroll();
									}
								});
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void loadMore() {
		if (isFlush || list.size() >= 100) {
			showToast("没有更多数据");
			lv.stopScroll();
			return;
		}
		isFlush = true;
		getData();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tljr_img_onerank_fanhui:
			finish();
			break;
		default:
			break;
		}
	}
}
