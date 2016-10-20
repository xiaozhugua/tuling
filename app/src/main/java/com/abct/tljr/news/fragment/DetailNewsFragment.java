package com.abct.tljr.news.fragment;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.model.Options;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.news.NewsActivity;
import com.abct.tljr.news.NewsCommentActivity;
import com.abct.tljr.news.NewsWebView;
import com.abct.tljr.news.bean.Comment;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.bean.Reply;
import com.abct.tljr.news.widget.CircularProgressView;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.CommonApplication;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.share.ShareQQPlatform;
import com.qh.common.login.share.ShareWeiXinPlatform;
import com.qh.common.login.share.ShareWeiboPlatform;
import com.qh.common.login.util.ShareContent;
import com.qh.common.util.InputTools;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.ryg.utils.LOG;

/**
 * 新闻内页
 */
public class DetailNewsFragment extends Fragment implements OnClickListener {
	private NewsActivity activity;
	private View view;
	private TextView tljr_tv_title,  tljr_tv_date, tljr_tv_news_source; // 新闻标题，内容,日期
	private ImageView img_piyue;	
	private NewsWebView newsWebView; // webview 控件
	private WebView webview;
	public final String Tag = "NewsDetails";
	public static long PUBLISH_COMMENT_TIME;
	public long PUBLISH_ZAN_TIME = -1;
	public long PUBLISH_CAI_TIME = -1;
	 
	
	private Button tljr_btn_news_addCollect, tljr_btn_news_share, tljr_btn_news_AddComment;

	private TextView tv_collect;
	
	
	private RelativeLayout tljr_ly_btn_news_addCollect, tljr_ly_btn_news_share, tljr_ly_btn_news_AddComment;

	private View newest_comment;
	SwipeRefreshLayout ddd;

	int position = 0;
	public News news;
	private ImageView iv_zan, iv_cai;
	private TextView tv_zan_num, tv_cai_num;
	private RelativeLayout ly_zan, ly_cai;
	public Button btn_more;
	public String default_avatar = "drawable://" + R.drawable.img_avatar;

	Realm mRealm;
	LinearLayout cmt_ly;
	DetailNewsFragment fragment;

	LinearLayout linear_ly;

	public int red = Color.parseColor("#eb5244");

	public ScrollView scrollView;
	SwipeRefreshLayout refreshLayout;
	//Realm myRealm;
	public void setNews(News news) {
		this.news = news;
	}

	public void setRealm(Realm mRealm) {
		this.mRealm = mRealm;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		fragment = this;
		activity = (NewsActivity) getActivity();

		// viewAnim = new ViewAnimUtil(newsActivity); // 初始化移动动画工具
		view = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.tljr_fragment_hqss_news_details, null);

		 
	
		
		
		init();

		ChangeNews();

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			 
			
			activity.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (!news.isLoadDetails() && !Constant.netType.equals("未知")) {
						LogUtil.i(Tag, "没有加载过Details，发出请求");
					//	refreshLayout.setRefreshing(true);
					
						getDetailNews(new Complete() {
							@Override
							public void complete() {
								// TODO Auto-generated method stub
								ChangeNews();
						//		refreshLayout.setRefreshing(false);
								 activity.postDelayed(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										 Loading(false);
									}
								}, 300);
								
							}
						});
					} else {
						LogUtil.i(Tag, "已经加载过了Details，或者离线");
						
						
						RealmResults<News> results = mRealm.where(News.class)
								 .equalTo("id", news.getId()).findAll();
						
						
					 
						boolean isCollect = false;
						if (results != null) {
							
							for (News n:results) {
								LogUtil.i("collect","重新进入: n.isHaveCollect:"+ n.isHaveCollect());
								isCollect  = n.isHaveCollect() ;
							
								
							}
							
							mRealm.beginTransaction();
							news.setHaveCollect(isCollect);
							mRealm.commitTransaction();
							 
							 
						}
						
						
						
						 Loading(false);
						ChangeNews();
						activity.postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								linear_ly.setVisibility(View.VISIBLE);
							}
						}, 1000);
					}

				}
			}, 300);

		}
	}

	public void getDetailNews(final Complete complete) {

		String pid = "0";// 默认无
		if (MyApplication.getInstance().self != null) {
			pid = MyApplication.getInstance().self.getId();
		}
		String url ="";
		String params ="";
		
		 url = UrlUtil.URL_new + "api/detail";
		  params = "species=" + news.getSpecial() + "&id=" + news.getId() + "&time=" + news.getTime() + "&uid=" + pid;
		
//		if(NewsActivity.current_Mode ==NewsActivity.MODE_PICTURE){
//			 
//		}
//		else if(NewsActivity.current_Mode ==NewsActivity.MODE_WXH){
//			
//			
//			url =UrlUtil.URL_new +"api/subscribe/detail";
//			 params = "id=" + news.getId() + "&time=" + news.getTime() + "&uid=" + pid+"&platform=1";
//		}
		
	

		LogUtil.i(Tag, url + "?" + params);
		NetUtil.sendPost(url, params, new NetResult() {

			@Override
			public void result(final String msg) {
				try {
					JSONObject cmdInfo = new JSONObject(msg);
					LogUtil.i(Tag, "singel news:" + msg);
				
					int status = cmdInfo.optInt("status", 0);
					String message = cmdInfo.optString("msg", "网络连接错误，请稍后重试");
					if (status == 0) {
						Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
						return;
					}
					
					 
					mRealm.beginTransaction();
					JSONObject newsObject = cmdInfo.getJSONObject("joData");
					news.setId(newsObject.getString("id"));
					news.setContent(newsObject.getString("content"));
					news.setSource(newsObject.getString("source"));

					news.setZan(newsObject.optString("allPraise", "0"));
					news.setCai(newsObject.optString("allOppose", "0"));
					news.setCollect(newsObject.optString("allCollect", "0"));
					news.setSurl(newsObject.optString("surl"));
					LogUtil.i(Tag, "getSurl"+news.getSurl());
					news.setHaveZan(newsObject.optBoolean("hasPraise", false));
					news.setHaveCai(newsObject.optBoolean("hasOppose", false));
					news.setHaveCollect(newsObject.optBoolean("hasCollect", false));
					//news.setHaveSee(newsObject.optBoolean("read", false));

					news.setLoadDetails(true);
					mRealm.copyToRealmOrUpdate(news);
					mRealm.commitTransaction();
					complete.complete();
					activity.postDelayed(new Runnable() {

						@Override
						public void run() {
							linear_ly.setVisibility(View.VISIBLE);
						}
					}, 1000);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					activity.handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(activity, "无法连接服务器请稍后再试", Toast.LENGTH_SHORT).show();
						}
					});
				}

			}
		});

	}

	public void reFresh() {
		img_piyue.setVisibility(news.isHaveSee() ? View.VISIBLE : View.GONE);
	}

	private void ChangeNews() {
		LogUtil.i(Tag, "ChangeNews");
		/*
		 * 头部 新闻 -标题 日期 来源
		 */
		if (news!=null &&news.getTitle() == null && !Util.isEmptyAndSpace(news.getTitle())) {
			return;
		}
		String title = Util.ToDBC(Util.myTrim(news.getTitle()));
		title = title.length() > 40 ? title.substring(0, 40) + "..." : title;
		tljr_tv_title.setText(title);
		tljr_tv_date.setText(news.getDate());
		tljr_tv_news_source.setText(news.getSource());

		tv_zan_num.setText(news.getZan());
		tv_cai_num.setText(news.getCai());

		/*
		 * webview 内容切换
		 */
		// linear_ly.setVisibility(View.VISIBLE);
		newsWebView.changNews();

		/*
		 * 底部 已收藏，已点赞踩 状态更新
		 */
		bottomBarStateUpdate();
		// /*
		// * 获取评论
		// */

	}
 
	private boolean isPause = false;

	@Override
	public void onPause() {
		LogUtil.i("read", Tag+": onPause()");
		// TODO Auto-generated method stub
		super.onPause();
		isPause = true;
		LogUtil.i(Tag, "isPause");
		HuanQiuShiShi.gotoDetailsNews=false;
		// scrollView.setFocusable(true);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isPause = false;
		// newsActivity.handler.postDelayed(r, 5000);
	}

	boolean isScrollLoadComment;

	private FrameLayout loadingLayout;
	private CircularProgressView progressView ;
	public void Loading(boolean load){
		loadingLayout.setVisibility(load?View.VISIBLE:View.INVISIBLE);
	}
	@SuppressWarnings("deprecation")
	private void init() {

		progressView = (CircularProgressView)view.findViewById(R.id.web_progress_view);
		progressView.setColor(0xffEB5041);
		progressView.setIndeterminate(true);
		loadingLayout =(FrameLayout)view.findViewById(R.id.loadinglayout);
		
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);

		refreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				LogUtil.i(Tag, "------Refresh-------");

				activity.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						getNewsComment();
						getDetailNews(new Complete() {

							@Override
							public void complete() {
								ChangeNews();
								refreshLayout.setRefreshing(false);
							}
						});
					}
				});

			}
		});

		linear_ly = (LinearLayout) view.findViewById(R.id.linear_ly);

		cmt_ly = (LinearLayout) view.findViewById(R.id.cmt_ly);
		newest_comment = view.findViewById(R.id.cmt_tip);
		// 设置滑动到一半时加载评论
		scrollView = (ScrollView) view.findViewById(R.id.news_scroll);
		scrollView.setOnTouchListener(touchListener);
		/*
		 * bottom bar
		 */
		tljr_btn_news_addCollect = (Button) view.findViewById(R.id.tljr_btn_news_addCollect);
		tljr_btn_news_share = (Button) view.findViewById(R.id.tljr_btn_news_share);
		
		tv_collect = (TextView) view.findViewById(R.id.tljr_btn_news_addCollect_tv);
		
		
		tljr_btn_news_AddComment = (Button) view.findViewById(R.id.tljr_btn_news_AddComment);
		tljr_ly_btn_news_addCollect = (RelativeLayout) view.findViewById(R.id.tljr_ly_btn_news_addCollect);
		tljr_ly_btn_news_share = (RelativeLayout) view.findViewById(R.id.tljr_ly_btn_news_share);
		tljr_ly_btn_news_AddComment = (RelativeLayout) view.findViewById(R.id.tljr_ly_btn_news_AddComment);
		tljr_ly_btn_news_addCollect.setOnClickListener(this);
		tljr_ly_btn_news_share.setOnClickListener(this);
		tljr_ly_btn_news_AddComment.setOnClickListener(this);
		tljr_btn_news_addCollect.setOnClickListener(this);
		tljr_btn_news_share.setOnClickListener(this);
		tljr_btn_news_AddComment.setOnClickListener(this);

		tljr_tv_title = (TextView) view.findViewById(R.id.title);
		tljr_tv_date = (TextView) view.findViewById(R.id.news_date);
		tljr_tv_news_source = (TextView) view.findViewById(R.id.news_sources);
		img_piyue = (ImageView) view.findViewById(R.id.img_piyue);

		/*
		 * webview 控件
		 */
		webview = (WebView) view.findViewById(R.id.webView1);
		newsWebView = new NewsWebView(webview, news, activity);

		iv_zan = (ImageView) view.findViewById(R.id.iv_zan);
		iv_cai = (ImageView) view.findViewById(R.id.iv_cai);
		iv_zan.setOnClickListener(this);
		iv_cai.setOnClickListener(this);
		tv_zan_num = (TextView) view.findViewById(R.id.tv_zan_num);
		tv_cai_num = (TextView) view.findViewById(R.id.tv_cai_num);
		tv_zan_num.setOnClickListener(this);
		tv_cai_num.setOnClickListener(this);

		ly_zan = (RelativeLayout) view.findViewById(R.id.ly_zan);
		ly_cai = (RelativeLayout) view.findViewById(R.id.ly_cai);
		ly_zan.setOnClickListener(this);
		ly_cai.setOnClickListener(this);

		btn_more = (Button) view.findViewById(R.id.btn_more);
		btn_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent it = new Intent(activity, NewsCommentActivity.class);
				it.putExtra("id", news.getId());
				it.putExtra("species", news.getSpecial());
				it.putExtra("time", news.getTime() + "");
				startActivity(it);

			}
		});

	}

	public void addCommentView(ArrayList<Comment> list) {
		cmt_ly.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			final Comment cmt = list.get(i);
			View v = activity.getLayoutInflater().inflate(R.layout.tljr_item_news_details_comment, null);

			LinearLayout layout_child = ((LinearLayout) v.findViewById(R.id.layout_child));
			layout_child.setVisibility(View.VISIBLE);

			if (cmt.getReply() != null && cmt.getReply().length > 0) {
				Reply[] replys = cmt.getReply();
				for (Reply reply : replys) {
					TextView tv = new TextView(activity);
					tv.setPadding(10, 10, 10, 10);
					SpannableString ss = new SpannableString(reply.getNickname() + "： " + reply.getReply());
					ss.setSpan(new ForegroundColorSpan(red), 0, reply.getNickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					tv.setText(ss);
					layout_child.addView(tv);
					;
				}
			}

			final TextView tv_num = (TextView) v.findViewById(R.id.tljr_tx_comment_praise_num);

			if (!cmt.getAurl().equals("default")) {
				StartActivity.imageLoader.displayImage(cmt.getAurl(), ((ImageView) v.findViewById(R.id.img_avatar)), Options.getCircleListOptions());
			} else {
				StartActivity.imageLoader.displayImage(default_avatar, ((ImageView) v.findViewById(R.id.img_avatar)), Options.getCircleListOptions());
			}

			((TextView) v.findViewById(R.id.tljr_comment_name)).setText(cmt.getName());

			((TextView) v.findViewById(R.id.tljr_comment_contents)).setText(cmt.getContent());

			((TextView) v.findViewById(R.id.tljr_comment_time)).setText(cmt.getTime());

			tv_num.setText(cmt.getPraise());

			((ImageView) v.findViewById(R.id.add_child)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SpeakDialog dialog = new SpeakDialog(activity, fragment);
					dialog.setComment(cmt);
					dialog.show();
				}
			});

			// 评论点赞
			((RelativeLayout) v.findViewById(R.id.tljr_ly_news_comment_praise)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {

					if (MyApplication.getInstance().self == null) {
						activity.showToast("未登录或注册无法完成操作");
						activity.login();
						return;
					} else {

						ProgressDlgUtil.showProgressDlg("", activity);
						String url = UrlUtil.URL_new + "api/uc/cadd";
						String param = "oper=3&uid=" + MyApplication.getInstance().self.getId() + "&cid=" + cmt.getId() + "&id=" + news.getId();
						LogUtil.i(Tag, url + "?" + param);
						NetUtil.sendPost(url, param, new NetResult() {
							@Override
							public void result(String msg) {

								try {
									LogUtil.i(Tag, msg);
									JSONObject allJson = new JSONObject(msg);
									String status = allJson.optString("status");
									String message = allJson.optString("msg");
									if (status.equals("1")) {
										activity.handler.post(new Runnable() {
											@Override
											public void run() {
												tv_num.setText(Integer.valueOf(cmt.getPraise()) + 1 + "");
												((ImageView) v.findViewById(R.id.tljr_btn_comment_praise)).setImageResource(R.drawable.img_zan_dianliang);
											}
										});
									}
									Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
									ProgressDlgUtil.stopProgressDlg();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									activity.handler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											Toast.makeText(activity, "无法连接服务器请稍后再试", Toast.LENGTH_SHORT).show();
										}
									});
								}

							}
						});
					}

				}
			});

			ImageView iv = (ImageView) v.findViewById(R.id.img_avatar);
			if (!cmt.getAurl().equals("default")) {
				StartActivity.imageLoader.displayImage(cmt.getAurl(), iv, Options.getCircleListOptions());
			} else {
				StartActivity.imageLoader.displayImage(default_avatar, iv, Options.getCircleListOptions());
			}
			cmt_ly.addView(v);
		}

	}

	public void addCai() {

		if (MyApplication.getInstance().self == null) {
			activity.showToast("未登录或注册无法完成操作");
			activity.login();
			return;
		}
		if (PUBLISH_CAI_TIME > 0) {
			activity.showToast("太快了，休息下吧");
			return;
		}
		PUBLISH_CAI_TIME = 5;
		activity.handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				PUBLISH_CAI_TIME = 0;
			}
		}, 5000);
		activity.handler.post(new Runnable() {

			@Override
			public void run() {

				String url = UrlUtil.URL_new + "api/uc/oppose";
				String params = "uid=" + CommonApplication.getInstance().self.getId() + "&nid=" + news.getId() + "&species=" + news.getSpecial() + "&time=" + news.getTime();

				NetUtil.sendPost(url, params, new NetResult() {

					@Override
					public void result(final String msg) {

						LogUtil.i(Tag, msg);
						activity.handler.post(new Runnable() {
							@Override
							public void run() {

								try {
									JSONObject allJson = new JSONObject(msg);

									String status = allJson.getString("status");
									String message = allJson.getString("msg");

									if (status.equals("1")) {
										JSONObject jodata = allJson.getJSONObject("joData");
										String num = jodata.optInt("len", 0) + "";
										mRealm.beginTransaction();
										 
										
										news.setCai(num);
										news.setHaveCai(true);
										mRealm.commitTransaction();
										tv_cai_num.setText(num);
										tv_cai_num.setTextColor(getResources().getColor(R.color.redtitlebj));
										ImageView btn_cai = (ImageView) view.findViewById(R.id.iv_cai);
										btn_cai.setImageResource(R.drawable.img_news_cai2);
									} else {

									}
									activity.showToast(message);
								} catch (NotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									activity.handler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											Toast.makeText(activity, "无法连接服务器请稍后再试", Toast.LENGTH_SHORT).show();
										}
									});
								}

							}
						});

					}
				});
			}

		});

	}

	public void addCollect() {

		if (MyApplication.getInstance().self == null) {
			activity.showToast("未登录或注册无法完成操作");
			activity.login();
			return;
		}
		String url = UrlUtil.URL_new + "api/uc/collect";
		String params = "uid=" + CommonApplication.getInstance().self.getId() + "&nid=" + news.getId() + "&species=" + news.getSpecial() + "&time=" + news.getTime();

		LogUtil.i(Tag, url + "?" + params);
		NetUtil.sendPost(url, params, new NetResult() {

			@Override
			public void result(final String msg) {
				
				try {
					LogUtil.i(Tag, msg);

					JSONObject allJson = new JSONObject(msg);
					String status = allJson.getString("status");
					String message = allJson.getString("msg");
					if (status.equals("1")) {

						if (!news.isHaveCollect()) {
							tljr_btn_news_addCollect.setBackgroundResource(R.drawable.img_news_shoucang2);
							mRealm.beginTransaction();
							news.setHaveCollect(true);
							mRealm.commitTransaction();
							
							tv_collect.setText("已收藏");
							
							RealmResults<News> results = mRealm.where(News.class)
									 .equalTo("id", news.getId()).findAll();
							
							
							ArrayList<News> cList = new ArrayList<News>();
							
							if (results != null) {
								
								for (News n:results) {
									cList.add(n);
									
								}
								
								for(News v: cList){
									mRealm.beginTransaction();
									v.setHaveCollect(true);
									mRealm.copyToRealm(v);
									mRealm.commitTransaction();
									LogUtil.i("collect","v.special:"+ v.getSpecial());
								}
							
								
								
								
								 LogUtil.i("collect","results.size()"+ results.size());
								
								 
							}
							
							
						} else {
							tljr_btn_news_addCollect.setBackgroundResource(R.drawable.img_news_shoucang1);
							mRealm.beginTransaction();
							news.setHaveCollect(false);
							mRealm.commitTransaction();
							tv_collect.setText("收藏");
							RealmResults<News> results = mRealm.where(News.class)
									 .equalTo("id", news.getId()).findAll();
							
							
							ArrayList<News> cList = new ArrayList<News>();
							
							if (results != null) {
								
								for (News n:results) {
									cList.add(n);
								}
								
								for(News v: cList){
									mRealm.beginTransaction();
									v.setHaveCollect(false);
									 LogUtil.i("collect","v.special:"+ v.getSpecial());
									mRealm.copyToRealm(v);
									mRealm.commitTransaction();
								}
							 
								 LogUtil.i("collect","results.size()"+ results.size());
								
								 
							}
							
						}
						
						
					
						

					} else {

						// newsActivity.showToast("删除收藏成功");
						// news.setHaveCollect(false);
						// tljr_btn_news_addCollect
						// .setBackgroundResource(R.drawable.img_news_shoucang1);

					}
					activity.showToast(message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					activity.handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(activity, "无法连接服务器请稍后再试", Toast.LENGTH_SHORT).show();
						}
					});
				}

			}
		});

	}

	public void addZan() {
		if (MyApplication.getInstance().self == null) {
			activity.showToast("未登录或注册无法完成操作");
			activity.login();
			return;
		}
		if (PUBLISH_ZAN_TIME > 0) {
			activity.showToast("太快了，休息下吧");
			return;
		}
		PUBLISH_ZAN_TIME = 5;
		activity.handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				PUBLISH_ZAN_TIME = 0;
			}
		}, 5000);
		activity.handler.post(new Runnable() {

			@Override
			public void run() {
				String url = UrlUtil.URL_new + "api/uc/praise";
				String params = "uid=" + CommonApplication.getInstance().self.getId() + "&nid=" + news.getId() + "&species=" + news.getSpecial() + "&time=" + news.getTime();
				LogUtil.i(Tag, url + "?" + params);
				NetUtil.sendPost(url, params, new NetResult() {

					@Override
					public void result(final String msg) {

						LogUtil.i(Tag, msg);
						activity.handler.post(new Runnable() {
							@Override
							public void run() {
								
								try {
									JSONObject allJson = new JSONObject(msg);
 
									String status = allJson.getString("status");
									String message = allJson.getString("msg");
									if (status.equals("1")) {
										
										JSONObject jodata = allJson.getJSONObject("joData");
										String num = jodata.optInt("len",0) + "";
										mRealm.beginTransaction();
										news.setHaveZan(true);
										news.setZan(num);
										mRealm.commitTransaction();
										tv_zan_num.setText(num);
										
										ImageView btn_zan = (ImageView) view.findViewById(R.id.iv_zan);
										btn_zan.setImageResource(R.drawable.img_news_zan2);
										tv_zan_num.setTextColor(getResources().getColor(R.color.redtitlebj));
									} else {

									}
									activity.showToast(message);
								} catch (NotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									activity.handler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											Toast.makeText(activity, "无法连接服务器请稍后再试", Toast.LENGTH_SHORT).show();
										}
									});
								}

							}
						});

					}
				});
			}

		});

	}

	private PopupWindow popupWindow;

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void showPopupView() {
		if (popupWindow == null) {
			// 一个自定义的布局，作为显示的内容
			RelativeLayout contentView = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.tljr_dialog_share_news, null);

			((Button) contentView.findViewById(R.id.btn_cancle)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (popupWindow != null)
						popupWindow.dismiss();
				}
			});

			LinearLayout ly1 = (LinearLayout) contentView.findViewById(R.id.ly1);

			for (int i = 0; i < ly1.getChildCount(); i++) {
				final int m = i;
				ly1.getChildAt(i).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						shareNewsUrl(m);
						popupWindow.dismiss();
					}
				});
			}
			popupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
			popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
//			popupWindow.getContentView().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setAnimationStyle(R.style.AnimationPreview);
			popupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					setAlpha(1f);
				}
			});
		}

		setAlpha(0.8f);
		int[] location = new int[2];
		View v = view.findViewById(R.id.tljr_news_bottom_f);
		v.getLocationOnScreen(location);
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]);
	}

	private void setAlpha(float f) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = f;
		lp.dimAmount = f;
		activity.getWindow().setAttributes(lp);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	// type 0:QQ 1 微信 2新浪微博 3 朋友圈
	private void shareNewsUrl(int type) {
		
		/** 分享勋章记录*/
		activity.handler.post(new Runnable() {

			@Override
			public void run() {
				String url = UrlUtil.URL_new + "api/uc/share";
				String params = "uid=" + CommonApplication.getInstance().self.getId() + "&nid=" + news.getId() + "&species=" + news.getSpecial() + "&time=" + news.getTime();
				LogUtil.i(Tag, url + "?" + params);
				NetUtil.sendPost(url, params, new NetResult() {

					@Override
					public void result(final String msg) {

						LogUtil.i(Tag, msg);
						activity.handler.post(new Runnable() {
							@Override
							public void run() {
								
								try {
									LogUtil.i(Tag, msg);
								} catch (NotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
//						 

							}
						});

					}
				});
			}

		});
		
		switch (type) {
		case 0:
			LogUtil.i(Tag, "surl:" + news.getSurl() + "--purl:" + news.getpUrl());
			ShareQQPlatform.getInstance().share(activity, news.getSurl(), news.getTitle(), news.getpUrl(), null, ShareContent.appName);
			break;
		case 1:
			ShareWeiXinPlatform.getInstance().setUrl(news.getSurl());
			ShareWeiXinPlatform.getInstance().setTitle(news.getTitle().length() > 22 ? news.getTitle().substring(0, 22) + "..." : news.getTitle());
			LOG.i("surl", "news.getSurl():"+news.getSurl());
			String ct = Util.getTextFromHtml(news.getContent());

			ShareWeiXinPlatform.getInstance().setContent(ct.length() > 26 ? ct.substring(0, 26) + "..." : ct);
			ShareWeiXinPlatform.getInstance().wechatShare(0);
			break;
		case 2:
			ShareWeiboPlatform.getInstanse().share(news.getSurl(), news.getTitle(), news.getTitle());
			break;
		case 3:
			ShareWeiXinPlatform.getInstance().setUrl(news.getSurl());
			ShareWeiXinPlatform.getInstance().setTitle(news.getTitle());
			Bitmap icon2 = BitmapFactory.decodeResource(getResources(), R.drawable.tljr_launcher);
			ShareWeiXinPlatform.getInstance().wechatShare(1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tljr_img_news_back: // 返回
			activity.finish();
			break;
		case R.id.tljr_btn_news_addCollect:
		case R.id.tljr_ly_btn_news_addCollect:
			addCollect();
			break;
		case R.id.tljr_btn_news_share:
		case R.id.tljr_ly_btn_news_share:

			showPopupView();
			break;
		case R.id.tljr_btn_news_AddComment:
		case R.id.tljr_ly_btn_news_AddComment:
			new SpeakDialog(activity, this).show();
			break;
		case R.id.iv_zan:
		case R.id.tv_zan_num:
		case R.id.ly_zan:

			addZan();
			break;
		case R.id.iv_cai:
		case R.id.tv_cai_num:
		case R.id.ly_cai:
			addCai();
			break;
		default:
			break;
		}

	}

	public void getNewsComment() {
		 if ( Constant.netType.equals("未知")) {
		 return;
		 }
		String url = UrlUtil.URL_new + "api/uc/cget";
		String param = "oper=1&id=" + news.getId();
		try {
			// NetUtil.sendPost(UrlUtil.URL_comment, param, new NetResult() {
			LogUtil.i(Tag, url + "?" + param);
			NetUtil.sendPost(url, param, new NetResult() {
				@Override
				public void result(final String msg) {
					activity.post(new Runnable() {
						@Override
						public void run() {
							
							try {
								LogUtil.i(Tag, msg);
								// newest_comment.setVisibility(View.VISIBLE);
								ArrayList<Comment> list = new ArrayList<Comment>();
								JSONObject allJson = new JSONObject(msg);
								String status = allJson.getString("status");
								if (status!=null &&!status.equals("1")) {
									return;
								}
								JSONObject commentObject = allJson.getJSONObject("joData");
								JSONArray array = commentObject.getJSONArray("data");
								if (array == null || array.length() <= 0) {
									newest_comment.setVisibility(View.GONE);
									return;
								} else {
									newest_comment.setVisibility(View.VISIBLE);
								}

								cmt_ly.removeAllViews();
								for (int i = 0; i < array.length(); i++) {

									Comment cmt = new Comment();

									cmt.setContent(array.getJSONObject(i).optString("comment"));
									cmt.setId(array.getJSONObject(i).optString("id"));

									cmt.setTime(HuanQiuShiShi.getStandardDate(array.getJSONObject(i).optLong("insertTime")));

									cmt.setNewsId(array.getJSONObject(i).optString("nid"));
									cmt.setSpecies(array.getJSONObject(i).optString("species"));
									cmt.setUser_id(array.getJSONObject(i).optString("uid"));

									cmt.setName(array.getJSONObject(i).optString("nickname"));
									cmt.setAurl(array.getJSONObject(i).optString("avatar","default") );

									cmt.setPraise(array.getJSONObject(i).optString("likes", "0"));
									
								 
									JSONArray childArray = array.getJSONObject(i).getJSONArray("replys");
									if (childArray != null && childArray.length() > 0) {
										Reply[] replys = new Reply[childArray.length()];
										for (int g = 0; g < childArray.length(); g++) {
											JSONObject ob = childArray.getJSONObject(g);
											Reply reply = new Reply();
											reply = new Reply();
											reply.setReply(ob.optString("reply"));
											reply.setNickname(ob.optString("nickname"));
											replys[g] = reply;
										}
										cmt.setReply(replys);
									}

									list.add(cmt);
								}

								int allNum = commentObject.optInt("all");
								((TextView) view.findViewById(R.id.cmt_num)).setText("最新评论(" + allNum + ")");
								if (allNum > 2) {
									btn_more.setVisibility(View.VISIBLE);
								}

								addCommentView(list);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								activity.handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Toast.makeText(activity, "无法连接服务器请稍后再试", Toast.LENGTH_SHORT).show();
									}
								});
							}

						}
					});

				}
			});
		} catch (Exception e) {
		}
	}

	private void bottomBarStateUpdate() {
		if (news.isHaveSee()) {
			img_piyue.setVisibility(View.VISIBLE);
		}

		if (news.isHaveCollect()) {
			tljr_btn_news_addCollect.setBackgroundResource(R.drawable.img_news_shoucang2);
			
			tv_collect.setText("已收藏");
			
		}else{
			tljr_btn_news_addCollect.setBackgroundResource(R.drawable.img_news_shoucang1);
			tv_collect.setText("收藏");
		}

		if (news.isHaveZan()) {
			iv_zan.setImageResource(R.drawable.img_news_zan2);
			tv_zan_num.setTextColor(getResources().getColor(R.color.redtitlebj));
		}

		if (news.isHaveCai()) {
			iv_cai.setImageResource(R.drawable.img_news_cai2);
			tv_cai_num.setTextColor(getResources().getColor(R.color.redtitlebj));
		}
	}

	public OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				break;
			case MotionEvent.ACTION_MOVE:
				int scrollY = v.getScrollY();
				int height = v.getHeight();
				int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredHeight();
				// if(scrollY==0){
				// System.out.println("滑动到了顶端 view.getScrollY()="+scrollY);
				// }

				// LogUtil.i(Tag, "scrollViewMeasuredHeight /2:"
				// + scrollViewMeasuredHeight / 2);
				// LogUtil.i(Tag, "scrollY+height :" + scrollY + height);
				// if((scrollY+height)==scrollViewMeasuredHeight){

				// 滑动到中间时获取评论
				if ((scrollY + height) > (scrollViewMeasuredHeight / 2)) {
					if (!isScrollLoadComment) {
						getNewsComment();
					}
					isScrollLoadComment = true;

				}
				break;

			default:
				break;
			}
			return false;
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup p = (ViewGroup) view.getParent();

		if (p != null)
			p.removeAllViewsInLayout();
		return view;
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//mRealm.close();
	}
	
}

class SpeakDialog extends Dialog implements OnClickListener {
	private NewsActivity activity;
	private EditText et;
	private DetailNewsFragment fragment_NewsDetails;
	private Comment comment;

	public SpeakDialog(NewsActivity activity, DetailNewsFragment fragment_NewsDetails) {
		super(activity, R.style.dialog);
		this.activity = activity;
		this.fragment_NewsDetails = fragment_NewsDetails;
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
		if (System.currentTimeMillis() - activity.PUBLISH_COMMENT_TIME < 15 * 1000) { // 15秒发言时间间隔
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
		String url = "";
		String params = "";
		String uid = CommonApplication.getInstance().self.getId();
		if (comment == null) {
			url = UrlUtil.URL_new + "api/uc/cadd";
			params = "uid=" + uid + "&oper=1&comment=" + s + "&species=" + fragment_NewsDetails.news.getSpecial() + "&id=" + fragment_NewsDetails.news.getId() + "&time=" + fragment_NewsDetails.news.getTime();
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
									fragment_NewsDetails.getNewsComment();
									InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
									activity.PUBLISH_COMMENT_TIME = System.currentTimeMillis();
								} else {
									Toast.makeText(activity, obj.optString("msg"), Toast.LENGTH_SHORT).show();
								}

							}
							ProgressDlgUtil.stopProgressDlg();
							InputTools.HideKeyboard(et);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							activity.handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									Toast.makeText(activity, "无法连接服务器请稍后再试", Toast.LENGTH_SHORT).show();
								}
							});
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
