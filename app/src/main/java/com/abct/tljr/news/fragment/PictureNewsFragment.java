package com.abct.tljr.news.fragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.DatePicker;
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
import com.abct.tljr.news.NewsWebActivity;
import com.abct.tljr.news.adapter.NewsAdapter;
import com.abct.tljr.news.adapter.NewsAdapter.ViewHolder2;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.utils.DownloadProUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

public class PictureNewsFragment extends Fragment {
	private PictureNewsFragment fragment;
	public final String Tag = "PictureNewsFragment";
	private RelativeLayout view;
	private RelativeLayout noNetView;
	private RelativeLayout viewNewsListView;
	private MainActivity activity;
	/*
	 * 传入的新闻频道所需参数 -
	 */
	public String nowTypeName = "全球直播";
	public String nowTypeSpecial = "r";
	private String defaultPicture = "default";
		
	public int emptyLayout =99; 
	/*
	 * listview
	 */
	ListView listview;
	SwipeRefreshLayout refreshLayout;
	boolean startLoadMore = false;
	/*
	 * adapter
	 */
	public NewsAdapter adapter;
	OnPagerAdapterListener adapterListener;
	/*
	 * 新闻刷新条数通知栏
	 */
	private TextView toast_refresh_text;
	private RelativeLayout toast_refresh;

	private boolean haveLoad;
	
	private RelativeLayout tljr_lny_noCollect ;
	 
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// fragment可见时加载数据
			if (!haveLoad) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (!Constant.netType.equals("未知")) {
							activity.handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									 
							
									getNewestNews(true, false);
								}
							});

						}

					}
				}).start();
				haveLoad = true;
			} 

		} else {
			// fragment不可见时不执行操作
			 
			LogUtil.i("read", "unVisibleToUser and updataUserIsRead");
			HuanQiuShiShi.updataUserIsRead();
		}

		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	 
		Bundle args = getArguments();
		nowTypeName = args != null ? args.getString("nowTypeName") : "";
		nowTypeSpecial = args != null ? args.getString("nowTypeSpecial") : "";
		defaultPicture = args != null ? args.getString("defaultPicture") : "";
		super.onCreate(savedInstanceState);
		fragment = this;
		activity = (MainActivity) getActivity();
		view = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.tljr_fragment_hqss, null);
		toast_refresh = (RelativeLayout) view.findViewById(R.id.toast_refresh);
		toast_refresh_text = (TextView) view.findViewById(R.id.toast_refresh_text);
		noNetView = (RelativeLayout) view.findViewById(R.id.tljr_lny_noNet_hqss);
		tljr_lny_noCollect= (RelativeLayout) view.findViewById(R.id.tljr_lny_noCollect);
		InitListView();

		getNewestNews(true, true, new Complete() {
			@Override
			public void complete() {
				activity.handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(!Constant.netType.equals("未知")){
							refreshLayout.setRefreshing(true);
						}
						
					}
				});

			}
		});
	}

	View footer;
	ProgressBar progressbar;
	TextView tips;

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

	private void InitListView() {

		viewNewsListView = (RelativeLayout) view.findViewById(R.id.tljr_lv_content_hqss);
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);
		listview = (ListView) view.findViewById(R.id.listview);

		footer = LayoutInflater.from(activity).inflate(R.layout.listview_footer, null);
		footer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogUtil.i(Tag, "------foot loadmore-------");

				loadMoreMode(0);
				if (isChooseDayGetNews) {
					getNewsForDate(currentChooseDate, false);
				} else {
					getNewestNews(false, false);
				}

			}
		});
		progressbar = (ProgressBar) footer.findViewById(R.id.footer_progressbar);
		tips = (TextView) footer.findViewById(R.id.footer_tv);
		// footer.setVisibility(View.INVISIBLE);

		listview.addFooterView(footer);

		refreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				LogUtil.i(Tag, "------Refresh-------");
				loadMoreMode(0);
				activity.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getNewestNews(true, false);
					}
				});

			}
		});

		listview.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView v, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (visibleItemCount + firstVisibleItem == totalItemCount) {

					if (newsManager != null && newsManager.getListSize() > 18) {
						if (startLoadMore) {
							footer.setVisibility(View.VISIBLE);
							if (isChooseDayGetNews) {
								getNewsForDate(currentChooseDate, false);
							} else {

								activity.handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										getNewestNews(false, false);
									}
								});

							}

							LogUtil.i(Tag, "------LoadMore-------");
						}
						startLoadMore = false;
					}

				}

			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
				// TODO Auto-generated method stub
                if (Util.isFastClick()) {
                    return;
                }
				News news = adapter.getItem(position);

				if (news.getType().contains("my_news")) { // 类型字段不包含my_news就转到webview

						HuanQiuShiShi.gotoDetailsNews =true;
						if (!news.isHaveSee() && !HuanQiuShiShi.id.contains(news.getId())) {
							JSONObject obj = new JSONObject();
							try {
								obj.put("time", news.getTime());
								obj.put("id", news.getId());
								obj.put("species", news.getSpecial());
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							if (HuanQiuShiShi.readId == null) {
								HuanQiuShiShi.readId = new JSONArray();
							}
							HuanQiuShiShi.readId.put(obj);
							HuanQiuShiShi.id.add(news.getId());
							if(HuanQiuShiShi.readId.length()>3){
								HuanQiuShiShi.updataUserIsRead();
							}

							LogUtil.i("read", "isReadNewsId :" + HuanQiuShiShi.readId.toString());
						}
					 
					 
					
					
						Intent intent = new Intent(activity, NewsActivity.class);

						Bundle bundle = new Bundle();
						bundle.putInt("position", position);
						bundle.putString("nowTypeName", nowTypeName);
						bundle.putString("PictureNewsFragment", nowTypeName);
						
						
						intent.putExtras(bundle);
						activity.startActivity(intent);


				} else if (news.getUrl() != null && news.getUrl().contains(".pdf")) {
					final String name = news.getId();
					File file = new File(Util.fileDirPath+"/"+name);
					final String filename="."+name+".pdf";
					if (file.exists()){
						openPdf(file);
					} else {
						downLoadFile(news.getUrl(), "附件中", filename, new Complete() {
							@Override
							public void complete() {
								openPdf(new File(Util.fileDirPath + "/" + filename));
							}
						});
					}
				}

				else {
					if (news.getUrl() != null && !news.getUrl().equals("")) {
						Intent intent = new Intent(activity, NewsWebActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("url", news.getUrl());
						bundle.putString("name", news.getSource());
						bundle.putString("title", news.getTitle());
						bundle.putString("date", news.getTime() + "");
						bundle.putBoolean("hascollect", news.isHaveCollect());
						bundle.putString("nid", news.getId());
						bundle.putString("species", news.getSpecial());
						bundle.putString("time", news.getTime() + "");

						intent.putExtras(bundle);
						getActivity().startActivity(intent);
					}

				}

				activity.mHandler.postDelayed(new Runnable() { // 点击显示已批阅，以及字体颜色变灰
							@Override
							public void run() {

								int firstVisiblePosition = parent.getFirstVisiblePosition();
								int lastVisiblePosition = parent.getLastVisiblePosition();
								if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
									View mView = parent.getChildAt(position - firstVisiblePosition);
									if (mView.getTag() instanceof ViewHolder2) {
										ViewHolder2 vh = (ViewHolder2) view.getTag();
										vh.iv_isRead.setVisibility(View.VISIBLE);
										vh.imp_news_title.setTextColor(Color.GRAY);
									}
								}
							}
						}, 1000);

			}
		});

	}

	private boolean isCover = false;
	private String lastId;
	private String lastTime;
	private String newestId;
	private String newestTime;
	private int listLayout = 2;
	public NewsManager newsManager;

	public void loadJson(String msg, final boolean isNew) {

		activity.handler.post(new Runnable() {

			@Override
			public void run() {
				refreshLayout.setRefreshing(false);
			}
		});

		try {
			LogUtil.i(Tag, "isNews:" + isNew + "-msg:" + msg);
			JSONObject cmdInfo = new JSONObject(msg);

			int status = cmdInfo.optInt("status", 0);
			final String message = cmdInfo.optString("msg", "网络连接错误，请稍后重试");
			if (status == 0) {
				activity.showMessage(message);
				loadMoreMode(1);
				return;
			}
			JSONObject newsObject = cmdInfo.getJSONObject("joData");
			if(newsObject==null){
				return;
			}
			int size = newsObject.optInt("size");

			lastId = newsObject.optString("lastId", lastId);

			lastTime = newsObject.optString("lastTime", lastTime);
			if (isNew) {
				isCover = newsObject.optBoolean("isCover");

				newestId = newsObject.optString("newestId", newestId);
				newestTime = newsObject.optString("newestTime", newestTime);
				listLayout = newsObject.optInt("layout",2);
				
				 
				
				
				if (listLayout == 1 ||listLayout == 4) {
					listview.setDivider(null);
				}
				LogUtil.i(Tag, nowTypeSpecial+"网络获取layout:"+ listLayout);
				
				 PreferenceUtils.getInstance().preferences.edit().putInt("layout_"+nowTypeSpecial, listLayout).commit();
				
			} else {

				if ((lastId == null && lastTime == null) || size == 0) {
					loadMoreMode(2);
					return;
				}
			}
			
			
			
			

			LogUtil.i(Tag, "isCover" + isCover);

			if (isNew && !isCover && size == 0) {
				ToastTipNotify(message);
		 
				if (newsManager!=null &&newsManager.getListSize() < 19) {
					//	footer.setVisibility(View.INVISIBLE);
						loadMoreMode(2);
					} 
				
				if(nowTypeSpecial.equals("z")){
					 
					if(newsManager ==null || newsManager.getListSize()==0){
						tljr_lny_noCollect.setVisibility(View.VISIBLE);
						viewNewsListView.setVisibility(View.GONE);
					}else{
						tljr_lny_noCollect.setVisibility(View.GONE);
						viewNewsListView.setVisibility(View.VISIBLE);
					}
					
				}
				
			 
			 
				return;
			}

			final JSONArray array = new JSONArray(newsObject.getString("news"));

			if (newsManager == null) {
				newsManager = new NewsManager(nowTypeSpecial);
			}
			newsManager.setListLayout(listLayout);
			newsManager.addNews(nowTypeSpecial + "", array, isCover);
			
			activity.handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (adapter == null) {
						adapter = new NewsAdapter(fragment, activity, newsManager.getList(), listview, listLayout, defaultPicture, nowTypeName);
						listview.setAdapter(adapter);
						ToastTipNotify(message);
					} else {
						adapter.setLayoutType(listLayout);
						adapter.setList(newsManager.getList());
						 
						adapter.notifyDataSetChanged();

						if (isNew) {
							ToastTipNotify(message);
						} else {	
							// listView.setLoadMoreSuccess();

						}
					}
					LogUtil.i(Tag, "newsManager.getList()" + newsManager.getList().size());
					// listView.startLoadMore();

					isCover = false;
					// footer.setVisibility(View.GONE);

					if (lastId != null && lastTime != null) {

						loadMoreMode(0);
					} else {
						loadMoreMode(2);
					}

					if (adapterListener != null) {
						adapterListener.notifyDataAdapter();
					}

					if (newsManager.getListSize() < 19) {
					//	footer.setVisibility(View.INVISIBLE);
						loadMoreMode(2);
					}

				}
			});
			if(nowTypeSpecial.equals("z") && newsManager.getListSize()==0){
				tljr_lny_noCollect.setVisibility(View.VISIBLE);
				viewNewsListView.setVisibility(View.GONE);
			}else{
				tljr_lny_noCollect.setVisibility(View.GONE);
				viewNewsListView.setVisibility(View.VISIBLE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			activity.handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(activity, "获取新闻失败，请稍后再试", Toast.LENGTH_SHORT).show();
					loadMoreMode(1);
				}
			});
		}

	}

	public void getNewestNews(final boolean isNew, boolean firstLoad, Complete... completes) {
		if (Constant.netType.equals("未知") || firstLoad) {
			
			if(nowTypeSpecial.equals("z")){
				return;
			}
			
			LogUtil.i(Tag, "在离线环境下 getNewestNews :" + isNew);

			if (Constant.netType.equals("未知")) {
				refreshLayout.setRefreshing(false);
			}

			if (newsManager == null) {
				newsManager = new NewsManager(nowTypeSpecial);
			}
			ArrayList<News> list = newsManager.getNoNetNews(isNew);
			if (isNew) {
				if (list.size() <= 0) {
					refreshLayout.setRefreshing(false);
					if (Constant.netType.equals("未知")) {
						showViewNoNet();
					}
				} else {
					
					int layout = PreferenceUtils.getInstance().preferences.getInt("layout_"+nowTypeSpecial, emptyLayout);
					if(layout==emptyLayout){
						//refreshLayout.setRefreshing(false);
						if (completes.length > 0 && completes[0] != null) {
							completes[0].complete();
						}
						return;
					}
					if (layout == 1 ||layout == 4) {
						listview.setDivider(null);
					}
					
					
					LogUtil.i(Tag, "layout_"+nowTypeSpecial +"----"+layout);
					adapter = new NewsAdapter(this, activity, list, listview, layout, defaultPicture, nowTypeName);
//					if (listLayout == 1 ||listLayout == 4) {
//						listview.setDivider(null);
//					}
					listview.setAdapter(adapter);
					refreshLayout.setRefreshing(false);
					listview.scrollTo(0, 0);
					if(list.size()<18){
						tips.setText("已无更多新闻");
						progressbar.setVisibility(View.INVISIBLE);
					}else{
						loadMoreMode(0);
					}
				}
				LogUtil.i(Tag, "离线-下拉刷新");
			} else {
				if (list.size() <= 0 || newsManager.isLoadAll) {
					LogUtil.i(Tag, "离线-上拉加载更多-没有更多缓存新闻了");
					// listView.stopLoadMore();
					startLoadMore = false;
					Toast.makeText(activity, "没有更多缓存新闻了", Toast.LENGTH_LONG).show();
					loadMoreMode(2);
				} else {
					
					if(list.size()<18){
						loadMoreMode(2);
					}
					
					adapter.setList(list);
					adapter.notifyDataSetChanged();
					// listView.setLoadMoreSuccess();
					startLoadMore = true;
					LogUtil.i(Tag, "离线-上拉加载更多-成功");
				}
			}

		} else {

			String url;
			String params;
			String pid = "0";// 默认无
			if (MyApplication.getInstance().self != null) {
				pid = MyApplication.getInstance().self.getId();
			}

			if (isNew) { 
				url = UrlUtil.URL_new + "api/main/new";
				if (newestTime == null && newestId == null) {
					params = "species=" + nowTypeSpecial + "&platform="+HuanQiuShiShi.platform + "&uid=" + pid+"&version="+HuanQiuShiShi.version+"&digest="+HuanQiuShiShi.digest;
				} else {
					params = "species=" + nowTypeSpecial + "&time=" + newestTime + "&id=" + newestId + "&platform="+HuanQiuShiShi.platform + "&uid=" + pid+"&version="+HuanQiuShiShi.version+"&digest="+HuanQiuShiShi.digest;
				}
			} else { 
				url = UrlUtil.URL_new + "api/main/old";
				params = "species=" + nowTypeSpecial + "&time=" + lastTime + "&id=" + lastId + "&platform="+HuanQiuShiShi.platform + "&uid=" + pid+"&version="+HuanQiuShiShi.version+"&digest="+HuanQiuShiShi.digest;
			}
			LogUtil.i(Tag, "getNewestNews:" + isNew + " -----url:" + url + "?" + params);

			NetUtil.sendPost(url, params, new NetResult() {

				@Override
				public void result(final String msg) {

					activity.mHandler.post(new Runnable() {

						@Override
						public void run() {
							loadJson(msg, isNew);
						}
					});

				}
			});

		}

		if (completes.length > 0 && completes[0] != null) {
			completes[0].complete();
		}

	}

	private int index; // 按日期获取的页数
	private Calendar calendar;
	private DatePicker datePicker;
	private boolean isChooseDayGetNews = false;
	long currentChooseDate;

	public void choseDayGetNews() {
		calendar = Calendar.getInstance();
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.tljr_news_date_dialog, null);
		datePicker = (DatePicker) layout.findViewById(R.id.datePicker1);
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
		datePicker.setMaxDate(calendar.getTimeInMillis());

		new AlertDialog.Builder(activity).setView(layout).setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				newestId = null;
				newestTime = null;
				index = 0;
				// listView.refresh();
				refreshLayout.setRefreshing(true);

				isChooseDayGetNews = true;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				int m2 = datePicker.getMonth() + 1;
				String month = m2 >= 10 ? m2 + "" : "0" + m2;
				String day = datePicker.getDayOfMonth() >= 10 ? datePicker.getDayOfMonth() + "" : "0" + datePicker.getDayOfMonth();
				String date = datePicker.getYear() + "-" + month + "-" + day;
				try {
					currentChooseDate = sdf.parse(date).getTime();
					Toast.makeText(activity, "开始查询-" + date, Toast.LENGTH_SHORT).show();
					getNewsForDate(currentChooseDate, true);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).setNegativeButton("取消", null).setTitle("请选择新闻日期").show();
	}

	private void getNewsForDate(final long time, final boolean isNews) {
		activity.mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {

				String pId = PreferenceUtils.getInstance().preferences.getString("UserId", "0");

				// String url =
				// "http://192.168.0.12:8080/QhWebNewsServer/api/main/dn";
				String url = UrlUtil.URL_new + "api/main/dn";
				String params = "species=" + nowTypeSpecial + "&time=" + time + "&index=" + index + "&uid=" + pId+"&version="+HuanQiuShiShi.version+"&digest="+HuanQiuShiShi.digest+ "&platform="+HuanQiuShiShi.platform ;

				LogUtil.i(Tag, "getDate URL :" + url + "?" + params);
				NetUtil.sendGet(url, params, new NetResult() {

					@Override
					public void result(final String msg) {
						LogUtil.i(Tag, "getDate msg :" + msg);
						activity.mHandler.post(new Runnable() {

							@Override
							public void run() {
								loadJsonForDate(msg, isNews);
							}
						});

					}
				});

			}
		}, 200);

	}

	public void loadJsonForDate(String msg, boolean isNew) {

		try {
			JSONObject cmdInfo = new JSONObject(msg);

			int status = cmdInfo.optInt("status", 0);
			 String message = cmdInfo.optString(("msg"),"网络连接错误，请稍后重试");
			if (status == 0) {
				activity.showMessage(message);
				refreshLayout.setRefreshing(false);

				return;
			}

			JSONObject newsObject = cmdInfo.getJSONObject("joData");
			index = newsObject.optInt("next");

			final JSONArray array = new JSONArray(newsObject.getString("news"));

			if (isNew) {
				if (index == -1 && array.length() <= 0) {
					Toast.makeText(activity, "当日没有新闻，请选择其他日期", Toast.LENGTH_LONG).show();
					refreshLayout.setRefreshing(false);
					isChooseDayGetNews = false;
					return;
				}
			}

			if (newsManager == null) {
				newsManager = new NewsManager(nowTypeSpecial);
			}
			newsManager.setListLayout(listLayout);
			newsManager.addNews(nowTypeSpecial + "", array, isNew);

			if (adapter == null) {
				adapter = new NewsAdapter(this, activity, newsManager.getList(), listview, listLayout, defaultPicture, nowTypeName);
				listview.setAdapter(adapter);
				refreshLayout.setRefreshing(false);
			} else {

				adapter.setList(newsManager.getList());
				adapter.notifyDataSetChanged();

				if (isNew) {
					refreshLayout.setRefreshing(false);
					// ToastTipNotify(message);
					listview.scrollTo(0, 0);
				} else {
					// listView.setLoadMoreSuccess();
					startLoadMore = true;
				}
			}
			if (index == -1) {
				// listView.stopLoadMore();
				isChooseDayGetNews = false;
				loadMoreMode(2);
				Toast.makeText(activity, "当日新闻已加载完毕", Toast.LENGTH_LONG).show();
			} else {
				// listView.startLoadMore();
				startLoadMore = true;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	 
	

	private void showViewNoNet() {
		viewNewsListView.setVisibility(View.GONE);

		noNetView.setVisibility(View.VISIBLE);
		noNetView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!Constant.netType.equals("未知")) {
					refreshLayout.setRefreshing(true);
					getNewestNews(true, false);

					viewNewsListView.setVisibility(View.VISIBLE);
					noNetView.setVisibility(View.GONE);
				}

			}
		});
	}

	private void ToastTipNotify(final String msg) {
		activity.mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				toast_refresh_text.setText(msg);
				toast_refresh.setVisibility(View.VISIBLE);
				activity.mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						toast_refresh.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}, 1000);
	}

	private void downLoadFile(final String url, final String title, final String apkName, final Complete complete) {
		if (!Constant.netType.equals("WIFI")) {
			new AlertDialog.Builder(activity).setTitle("图灵金融").setMessage("当前为" + Constant.netType + "网络，下载会消耗流量，确认下载？")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							DownloadProUtil.showProgressDlg(title, url, apkName, activity, true, complete);
						}
					}).setNegativeButton("否", null).show();

		} else {
			DownloadProUtil.showProgressDlg(title, url, apkName, activity, true, complete);
		}

	}

	private void openPdf(File file) {
		// Intent intent = new Intent(FerdlsActivity.this,
		// PDFActivity.class);
		// intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME,
		// Util.filePath + "/" + name);
		// startActivity(intent);
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/pdf");// 文档格式
		startActivity(intent);
	}

	@Override
	public void onResume() {

		super.onResume();
		 LogUtil.i("shoucang", "onresume");
		if (adapter != null && newsManager != null) {
			adapter.setList(newsManager.getList());
			adapter.notifyDataSetChanged(); 
		}
		
		 if(nowTypeSpecial.equals("z") &&  haveLoad){
			 LogUtil.i("shoucang", "z");
			 LogUtil.i("shoucang", "重新加载我的收藏");
			 	newestTime=null;
				newestId=null;
				newsManager = null;
			 getNewestNews(true, false);
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

	@Override
	public void onPause() {

		super.onPause();
		LogUtil.i("read", Tag+": onPause()");
	}

	

	public void setAdapterListener(OnPagerAdapterListener listener) {
		this.adapterListener = listener;
	}

	public interface OnPagerAdapterListener {
		void notifyDataAdapter();
	}

}
