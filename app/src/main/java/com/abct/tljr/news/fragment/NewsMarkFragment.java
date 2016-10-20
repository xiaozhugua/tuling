package com.abct.tljr.news.fragment;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.news.NewsActivity;
import com.abct.tljr.news.adapter.NewsAdapter;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.fragment.PictureNewsFragment.OnPagerAdapterListener;
import com.abct.tljr.news.mark.AllMarkAdapter;
import com.abct.tljr.news.mark.MarkRecommend;
import com.abct.tljr.news.mark.NewsMarkActivity;
import com.qh.common.listener.NetResult;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

public class NewsMarkFragment extends Fragment {
	public final String Tag = "NewsMarkFragment";
	private RelativeLayout view;
	private MainActivity activity;
	public static ArrayList<News> newsList;

	public ArrayList<News[]> list = new ArrayList<News[]>();

	// private ZrcListView lv;
	private int page = 0;
	AllMarkAdapter adapter;

	ListView listview;
	SwipeRefreshLayout refreshLayout;
	boolean startLoadMore = true;
	View footer;
	ProgressBar progressbar;
	TextView tips;
	RelativeLayout noNet_mark;

	public String nowTypeName = "大V精选";
	public static String nowTypeSpecial = "xa";
	OnPagerAdapterListener adapterListener;

	RelativeLayout mark_top_add;
	LinearLayout recommend_mark;
	private boolean isRefresh = false;
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (MyApplication.getInstance().self != null) {
			activity.handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(refreshLayout.getVisibility()==View.VISIBLE)
					refresh();
				}
			});

		}else{
			Toast.makeText(activity, "未登录或注册无法完成操作", Toast.LENGTH_SHORT).show();
			((MainActivity) activity).login();
		}
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle args = getArguments();
		nowTypeName = args != null ? args.getString("nowTypeName") : "";
		nowTypeSpecial = args != null ? args.getString("nowTypeSpecial") : "";

		super.onCreate(savedInstanceState);

		activity = (MainActivity) getActivity();
		view = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.tljr_fragment_mark, null);

		initListView();

		if (Constant.netType.equals("未知")) {
			noNet_mark.setVisibility(View.VISIBLE);
			footer.setVisibility(View.INVISIBLE);
		}
		noNet_mark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Constant.netType.equals("未知")) {
					return;
				}

				noNet_mark.setVisibility(View.GONE);
				loadMoreMode(0);
				page = 0;
				list.clear();
				adapter.notifyDataSetChanged();
				getData();
				footer.setVisibility(View.VISIBLE);
			}
		});

		TextView add_more = (TextView) view.findViewById(R.id.add_more);
		mark_top_add = (RelativeLayout) view.findViewById(R.id.ly_mark_top_add);
		add_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (MyApplication.getInstance().self == null) {

					Toast.makeText(activity, "未登录或注册无法完成操作", Toast.LENGTH_SHORT).show();
					activity.login();
					return;
				}

				Intent it = new Intent(activity, NewsMarkActivity.class);
				startActivity(it);
			}
		});
		mark_top_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (MyApplication.getInstance().self == null) {

					Toast.makeText(activity, "未登录或注册无法完成操作", Toast.LENGTH_SHORT).show();
					activity.login();
					return;
				}

				Intent it = new Intent(activity, NewsMarkActivity.class);
				startActivity(it);
			}
		});
	}

	private void initListView() {
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly2);
		refreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
		recommend_mark = (LinearLayout) view.findViewById(R.id.recommend_mark);
		listview = (ListView) view.findViewById(R.id.listview);
		// listview.setDivider(null);
		footer = LayoutInflater.from(activity).inflate(R.layout.listview_footer, null);
		noNet_mark = (RelativeLayout) view.findViewById(R.id.noNet_mark);
		footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtil.i(Tag, "------foot loadmore-------");
				loadMoreMode(0);
				getData();

			}
		});
		progressbar = (ProgressBar) footer.findViewById(R.id.footer_progressbar);
		tips = (TextView) footer.findViewById(R.id.footer_tv);

		listview.addFooterView(footer);
		footer.setVisibility(View.INVISIBLE);

		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refresh();

			}

			
		});

		adapter = new AllMarkAdapter(activity, listview, list, nowTypeName);
		listview.setAdapter(adapter);

		listview.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView v, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (visibleItemCount + firstVisibleItem == totalItemCount) {

					if (list.size() > 6) {
						if (startLoadMore) {
							footer.setVisibility(View.VISIBLE);

							getData();

							LogUtil.i(Tag, "------LoadMore-------");
						}
						startLoadMore = false;
					}

				}

			}
		});

		// listview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(final AdapterView<?> parent, final View view,
		// final int position, long id) {
		// // TODO Auto-generated method stub
		//
		// Intent intent = new Intent(activity, NewsActivity.class);
		//
		// Bundle bundle = new Bundle();
		// bundle.putString("wxh", "");
		// bundle.putInt("position", position);
		// bundle.putString("nowTypeName", nowTypeName);
		// newsList = new ArrayList<News>();
		//
		// for (int i = 0; i < list.get(position).length; i++) {
		// newsList.add(list.get(position)[i]);
		// }
		//
		// intent.putExtras(bundle);
		// activity.startActivity(intent);
		//
		// }
		// });

		((View) view.findViewById(R.id.finsih)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refreshLayout.setVisibility(View.VISIBLE);
				mark_top_add.setVisibility(View.VISIBLE);
				recommend_mark.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				refresh();
			}
		});

	}
	private void refresh() {
		loadMoreMode(0);
		page = 0;
		getData();
	
	}
	
	public void getData() {
		refreshLayout.setRefreshing(true);
		String uid = PreferenceUtils.getInstance().preferences.getString("UserId", "0");

		// ProgressDlgUtil.showProgressDlg("", activity);
		LogUtil.i(Tag, UrlUtil.URL_new + "api/subscribe/article" + "?" + "platform=1&uid=" + uid + "&index=" + page + "&version="
				+ HuanQiuShiShi.version);

		NetUtil.sendPost(UrlUtil.URL_new + "api/subscribe/article", "platform=1&uid=" + uid + "&index=" + page + "&version=" + HuanQiuShiShi.version,
				new NetResult() {

					@Override
					public void result(final String msg) {
						LogUtil.i(Tag, msg);
						// TODO Auto-generated method stub

						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								if (page == 0) {
									list.clear();
								}
									
								try {
									JSONObject joooo = new JSONObject(msg);
									if (!joooo.has("joData")) {
										handler.post(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												refreshLayout.setVisibility(View.GONE);
												listview.setVisibility(View.GONE);
												mark_top_add.setVisibility(View.GONE);
												recommend_mark.setVisibility(View.VISIBLE);
												refreshLayout.setRefreshing(false);
												  new MarkRecommend(activity, view, handler);
											}
										});

										return;
									}

									JSONObject object = joooo.getJSONObject("joData");

									final JSONArray mark_array = object.getJSONArray("data");
									if (mark_array.length() > 0) {
										footer.setVisibility(View.VISIBLE);
									}
									for (int k = 0; k < mark_array.length(); k++) {

										JSONArray news_array = mark_array.getJSONArray(k);
										if (news_array.length() < 3) {
											continue;
										}
										News[] mNews = new News[news_array.length()];
										for (int y = 0; y < news_array.length(); y++) {
											JSONObject obj = news_array.getJSONObject(y);
											News news = new News();
											news.setTitle(obj.optString("title"));
											news.setDate(DateUtil.getDateMDhhmm(DateUtil.format.parse(obj.optString("time")).getTime()));
											news.setTime(DateUtil.format.parse(obj.optString("time")).getTime());
											news.setSimple_time(NewsAdapter.getNewsDate(news.getTime(), true));
											news.setId(obj.optString("id"));
											news.setType(obj.optString("type"));
											news.setSpecial(nowTypeSpecial);
											news.setAuthor(obj.optString("name"));
											news.setAuthorAvatar(obj.optString("purl"));
											news.setDigest(obj.optString("digest"));
											news.setWxh(obj.optString("wxh"));
											mNews[y] = news;
										}
										list.add(mNews);

									}

									adapter.notifyDataSetChanged();

									page = object.optInt("next");
									if (mark_array.length() > 0) {
										startLoadMore = page != -1;

									} else {
										loadMoreMode(2);
									}

									if (object.optInt("next", -1) == -1) {
										loadMoreMode(2);
									}
									if (adapterListener != null) {
										adapterListener.notifyDataAdapter();
									}
									refreshLayout.setRefreshing(false);
								} catch (JSONException | ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									loadMoreMode(2);
									refreshLayout.setRefreshing(false);
									// lv.stopLoadMore();

								}
							}
						});

					}
				});
	}

	private Handler handler = new Handler() {
	};

	public void loadMoreMode(int type) {

		if (type == 0) {
			tips.setText("正在加载...");
			progressbar.setVisibility(View.VISIBLE);
			startLoadMore = true;
		} else if (type == 1) {
			tips.setText("加载失败，点击重试");
			progressbar.setVisibility(View.INVISIBLE);
			startLoadMore = false;
		} else if (type == 2) {
			tips.setText("已无更多新闻");
			progressbar.setVisibility(View.INVISIBLE);
			startLoadMore = false;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup p = (ViewGroup) view.getParent();

		if (p != null)
			p.removeAllViewsInLayout();
		return view;
	}

	public void setAdapterListener(OnPagerAdapterListener listener) {
		this.adapterListener = listener;
	}

}
