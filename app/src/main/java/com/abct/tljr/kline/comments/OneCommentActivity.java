package com.abct.tljr.kline.comments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.kline.comments.XScrollView.IXScrollViewListener;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class OneCommentActivity extends BaseActivity {
	LinearLayout liearlist;
	private CommentsBean bean;
	private String code, titlename;
	private String TAG = "OneCommentActivity";
	private ArrayList<CommentsBean> arraylist;
	private TextView name, time, contentt;
	private CommentsBean MainComment;
	private ArrayList<View> views;
	private LayoutInflater inflater;
	private int page = 0;
	private XScrollView mScrollView;
	private boolean isupdata = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_comments_onecomment);

		findview();
	}

	private void findview() {
		inflater = LayoutInflater.from(this);
		views = new ArrayList<View>();
		MainComment = new CommentsBean();
		name = (TextView) findViewById(R.id.name);
		name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(OneCommentActivity.this, OnePerson.class);
				i.putExtra("cid", MainComment.getCid());
				i.putExtra("uid", MainComment.getUid());
				i.putExtra("name", titlename);
				startActivity(i);
			}
		});
		time = (TextView) findViewById(R.id.time);
		contentt = (TextView) findViewById(R.id.contentt);

		arraylist = new ArrayList<CommentsBean>();
		code = getIntent().getStringExtra("code");
		bean = new CommentsBean();
		try{
			JSONArray arr =new JSONArray(getIntent().getStringExtra("data"));
			JSONObject js = arr.getJSONObject(0);
			// // bean.setAvatar(js.getString("avatar"));
			// bean.setCid(js.getString("cid"));
			bean.setContent(js.getString("content"));
			bean.setId(js.getString("id"));
			bean.setUid(js.getString("uid"));
			bean.setUname(js.getString("uname"));
		}catch(Exception e){
			
		}

		name.setText(bean.getUname());
		titlename = getIntent().getStringExtra("name");
		contentt.setText("#" + titlename + "#" + bean.getContent());
		((TextView) findViewById(R.id.guname)).setText(titlename);

		mScrollView = (XScrollView) findViewById(R.id.tljr_sy_sc);
		mScrollView.initWithContext(OneCommentActivity.this);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(false);
		mScrollView.setAutoLoadEnable(false);
		mScrollView.setRefreshTime(Util.getNowTime());
		// mScrollView.startLoadMore();
		mScrollView.setIXScrollViewListener(new IXScrollViewListener() {
			@Override
			public void onRefresh() {
				isupdata = true;
				getdata();
			}

			@Override
			public void onLoadMore() {
				// System.out.println("加载更多");
				// if(load.getVisibility()==View.VISIBLE){
				// Log.d(TAG, "loadmore");
				// getlist(true);
				// }
			}
		});

		findViewById(R.id.love).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!MainComment.isIspraise()) {
					ItemLikeorOn(true, MainComment.getId());
					arg0.setBackgroundResource(R.drawable.social_subject_menu_up_d);
					((TextView) findViewById(R.id.love_number))
							.setText(MainComment.getPraise() + 1 + "");
				}
			}
		});

		findViewById(R.id.tljr_img_futures_join_back).setOnClickListener(
				onclick);
		findViewById(R.id.comment).setOnClickListener(onclick);

		liearlist = (LinearLayout) findViewById(R.id.linear);
		getdata();
	}

	private void initview(boolean isload) {
		if (!isload) {
			liearlist.removeAllViews();
			views.clear();
		}
		if (MainComment.isIspraise()) {
			findViewById(R.id.love).setBackgroundResource(
					R.drawable.social_subject_menu_up_d);
		}
		((TextView) findViewById(R.id.pinglun)).setText("全部评论("
				+ arraylist.size() + ")");
		if (MainComment.getAvatar() != null) {
			StartActivity.imageLoader.displayImage(MainComment.getAvatar(),
					((ImageView) findViewById(R.id.im)));
		}
		((TextView) findViewById(R.id.love_number)).setText(MainComment
				.getPraise() + "");

		for (int i = 0; i < arraylist.size(); i++) {
			View view = inflater.inflate(
					R.layout.tljr_activity_comment_oneitem, null);
			CommentsBean bean = arraylist.get(i);
			StartActivity.imageLoader.displayImage(bean.getAvatar(),
					(ImageView) view.findViewById(R.id.im));
			((TextView) view.findViewById(R.id.name)).setText(bean.getUname());
			((TextView) view.findViewById(R.id.name)).setTag(i);
			((TextView) view.findViewById(R.id.name))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							CommentsBean bean = arraylist.get(Integer
									.parseInt(arg0.getTag().toString()));
							Intent i = new Intent(OneCommentActivity.this,
									OnePerson.class);
							i.putExtra("cid", bean.getCid());
							i.putExtra("uid", bean.getUid());
							i.putExtra("name", titlename);
							startActivity(i);
						}
					});

			((TextView) view.findViewById(R.id.contentt)).setText(bean
					.getContent());

			view.findViewById(R.id.comment).setTag(i);
			view.findViewById(R.id.comment).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent i = new Intent(OneCommentActivity.this,
									EditActivity.class);
							i.putExtra("code", code);
							i.putExtra("type", 1);// 0是新增评论 1是对评论进行追评
							i.putExtra(
									"id",
									arraylist.get(
											Integer.parseInt(arg0.getTag()
													.toString())).getId());
							if (arraylist.get(
									Integer.parseInt(arg0.getTag().toString()))
									.getOid() != null) {
								i.putExtra(
										"oid",
										arraylist.get(
												Integer.parseInt(arg0.getTag()
														.toString())).getOid());
							}
							startActivityForResult(i, 99);
						}
					});

			if (bean.isIspraise()) {
				((ImageView) view.findViewById(R.id.love))
						.setBackgroundResource(R.drawable.social_subject_menu_up_d);
			}

			((TextView) view.findViewById(R.id.love_number)).setText(bean
					.getPraise() + "");
			view.findViewById(R.id.love).setTag(i + "");
			view.findViewById(R.id.love).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							CommentsBean bean = arraylist.get(Integer
									.parseInt(arg0.getTag().toString()));
							if (!bean.isIspraise()) {
								ItemLikeorOn(true, bean.getId());
								((TextView) views.get(
										Integer.parseInt(arg0.getTag()
												.toString())).findViewById(
										R.id.love_number)).setText((bean
										.getPraise() + 1) + "");
								arg0.setBackgroundResource(R.drawable.social_subject_menu_up_d);
							}
						}
					});

			long nowtime = System.currentTimeMillis();
			long dextime = nowtime - bean.getTime();
			dextime = dextime / 1000;
			if (dextime >= 86400) {
				((TextView) view.findViewById(R.id.time)).setText(Util
						.getDateOnlyDat(bean.getTime()));
			} else {
				if (dextime < 86400 && dextime >= 3600) {
					((TextView) view.findViewById(R.id.time))
							.setText((int) (dextime / 3600) + "小时前");
				} else if (dextime < 3600 & dextime >= 60) {
					((TextView) view.findViewById(R.id.time))
							.setText((int) (dextime / 60) + "分钟前");
				} else {
					((TextView) view.findViewById(R.id.time)).setText("刚刚");
				}
			}

			liearlist.addView(view);
			views.add(view);
		}

		// 结束刷新
		if (isupdata) {
			mScrollView.setRefreshTime(Util.getNowTime());
			mScrollView.stopRefresh();
			isupdata = false;
		}
	}

	private void getdata() {
		
		
		
		
		   NetUtil.sendPost(UrlUtil.URL_Comments_getbyoid, "uid="+MyApplication.getInstance().self.getId()+"&cid="+"symbol_" + code+"&oid="+bean.getId()+"&page="+page, new NetResult() {

  				@Override
  				public void result(final String msg) {

  					handler.post(new Runnable() {

  						@Override
  						public void run() {
  						// TODO Auto-generated method stub
  							try{
  								if (!msg.equals("error")) {
  									JSONObject ob =new JSONObject(msg);
  									if (ob.getBoolean("isok")) {
  										JSONArray arr = ob.getJSONArray("datas");
  										arraylist.clear();
  										for (int i = 0; i < arr.length(); i++) {
  											JSONObject ob1 = arr.getJSONObject(i);
  											if (i == 0) {
  												MainComment.setAvatar(ob1
  														.getString("avatar"));
  												MainComment.setTime(ob1
  														.getLong("time"));
  												MainComment.setContent(ob1
  														.getString("content"));
  												MainComment.setUname(ob1
  														.getString("uname"));
  												MainComment.setIspraise(ob1
  														.getBoolean("ispraise"));
  												MainComment.setIstread(ob1
  														.getBoolean("istread"));
  												MainComment.setPraise(ob1
  														.getInt("praise"));
  												MainComment.setTread(ob1
  														.getInt("tread"));
  												MainComment.setId(ob1.getString("id"));
  												MainComment.setCid(ob1.getString("cid"));
  												MainComment.setUid(ob1.getString("uid"));
  											} else {
  												CommentsBean bean = new CommentsBean();
  												bean.setAvatar(ob1.getString("avatar"));
  												bean.setCid(ob1.getString("cid"));
  												bean.setContent(ob1
  														.getString("content"));
  												bean.setId(ob1.getString("id"));
  												bean.setPraise(ob1
  														.getInt("praise"));
  												bean.setTime(ob1.getLong("time"));
  												bean.setTread(ob1.getInt("tread"));
  												bean.setIspraise(ob1
  														.getBoolean("ispraise"));
  												bean.setIstread(ob1
  														.getBoolean("istread"));
  												bean.setUid(ob1.getString("uid"));
  												bean.setUname(ob1.getString("uname"));
  												// arraylist.add(bean);
  												arraylist.add(0, bean);
  											}
  										}
  										if(handler != null){
  											handler.sendEmptyMessage(1);
  										}
  									} else {
  										if(handler != null){
  											handler.sendEmptyMessage(0);
  										}
  									}
  								}
  							}catch(Exception e){
  								
  							}
  						}
  					});

  				}
  			});
		
		
		
		
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("cid", "symbol_" + code);
//	 	params.addBodyParameter("uid", MyApplication.getInstance().self.getId());
//	 
//		params.addBodyParameter("oid", bean.getId());
//		params.addBodyParameter("page", page + "");
//		XUtilsHelper.sendPost(UrlUtil.URL_Comments_getbyoid, params,
//				new HttpCallback() {
//
//					@Override
//					public void callback(String data) {
//						// TODO Auto-generated method stub
//						try{
//							if (!data.equals("error")) {
//								JSONObject ob =new JSONObject(data);
//								if (ob.getBoolean("isok")) {
//									JSONArray arr = ob.getJSONArray("datas");
//									arraylist.clear();
//									for (int i = 0; i < arr.length(); i++) {
//										JSONObject ob1 = arr.getJSONObject(i);
//										if (i == 0) {
//											MainComment.setAvatar(ob1
//													.getString("avatar"));
//											MainComment.setTime(ob1
//													.getLong("time"));
//											MainComment.setContent(ob1
//													.getString("content"));
//											MainComment.setUname(ob1
//													.getString("uname"));
//											MainComment.setIspraise(ob1
//													.getBoolean("ispraise"));
//											MainComment.setIstread(ob1
//													.getBoolean("istread"));
//											MainComment.setPraise(ob1
//													.getInt("praise"));
//											MainComment.setTread(ob1
//													.getInt("tread"));
//											MainComment.setId(ob1.getString("id"));
//											MainComment.setCid(ob1.getString("cid"));
//											MainComment.setUid(ob1.getString("uid"));
//										} else {
//											CommentsBean bean = new CommentsBean();
//											bean.setAvatar(ob1.getString("avatar"));
//											bean.setCid(ob1.getString("cid"));
//											bean.setContent(ob1
//													.getString("content"));
//											bean.setId(ob1.getString("id"));
//											bean.setPraise(ob1
//													.getInt("praise"));
//											bean.setTime(ob1.getLong("time"));
//											bean.setTread(ob1.getInt("tread"));
//											bean.setIspraise(ob1
//													.getBoolean("ispraise"));
//											bean.setIstread(ob1
//													.getBoolean("istread"));
//											bean.setUid(ob1.getString("uid"));
//											bean.setUname(ob1.getString("uname"));
//											// arraylist.add(bean);
//											arraylist.add(0, bean);
//										}
//									}
//									if(handler != null){
//										handler.sendEmptyMessage(1);
//									}
//								} else {
//									if(handler != null){
//										handler.sendEmptyMessage(0);
//									}
//								}
//							}
//						}catch(Exception e){
//							
//						}
//					}
//				});
	}

	private void ItemLikeorOn(final boolean islike, String id) {
		
		
		String params = "uid="+MyApplication.getInstance().self.getId()+"&cid="+"symbol_" + code+"&id="+id ;
		if (islike) {
			params = params+"&type=praise";
		} else {
			params = params+"&type=tread";
		}
		
		   NetUtil.sendPost(UrlUtil.URL_Comments_update, params , new NetResult() {

  				@Override
  				public void result(final String msg) {

  					handler.post(new Runnable() {

  						@Override
  						public void run() {
  							if (!msg.equals("error") && msg != null) {
  								try {
  									JSONObject ob =new JSONObject(msg);
  								} catch (JSONException e) {
  									// TODO Auto-generated catch block
  									e.printStackTrace();
  								}

  							}
  						}
  					});

  				}
  			});
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("cid", "symbol_" + code);
//		params.addBodyParameter("uid", MyApplication.getInstance().self.getId());
//		params.addBodyParameter("id", id);
//		if (islike) {
//			params.addBodyParameter("type", "praise");
//		} else {
//			params.addBodyParameter("type", "tread");
//		}
//		XUtilsHelper.sendPost(UrlUtil.URL_Comments_update, params,
//				new HttpCallback() {
//
//					@Override
//					public void callback(String data) {
//						// TODO Auto-generated method stub
//						if (!data.equals("error") && data != null) {
//							try {
//								JSONObject ob =new JSONObject(data);
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//
//						}
//					}
//				});	
	}

	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.tljr_img_futures_join_back:
				finish();
				break;
			case R.id.comment:
				Intent i = new Intent(OneCommentActivity.this,
						EditActivity.class);
				i.putExtra("code", code);
				i.putExtra("type", 1);// 0是新增评论 1是对评论进行追评
				i.putExtra("id", MainComment.getId());
				if (MainComment.getOid() != null) {
					i.putExtra("oid", MainComment.getOid());
				}
				startActivityForResult(i, 99);
				break;
			default:
				break;
			}
		}
	};

	public void handleMsg(android.os.Message msg) {
		super.handleMsg(msg);
		switch (msg.what) {
		case 0:
			Toast.makeText(OneCommentActivity.this, "请求错误", Toast.LENGTH_SHORT)
					.show();
			break;
		case 1:
			initview(false);
			break;
		default:
			break;
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 99) {
			switch (resultCode) {
			case 1:
				try{
					String msg = data.getStringExtra("datas");
					JSONObject obt =new JSONObject(msg);
					CommentsBean bean = new CommentsBean();
					bean.setAvatar(obt.getString("avatar"));
					bean.setCid(obt.getString("cid"));
					bean.setContent(obt.getString("content"));
					bean.setId(obt.getString("id"));
					bean.setPraise(obt.getInt("praise"));
					bean.setTime(obt.getLong("time"));
					bean.setTread(obt.getInt("tread"));
					bean.setUid(obt.getString("uid"));
					bean.setUname(obt.getString("uname"));
					if (obt.getString("comment_values") != null) {
						bean.setComment_content(obt.getString("comment_values"));
					}
					if (obt.getString("oid") != null) {
						bean.setOid(obt.getString("oid"));
					}
					arraylist.add(0, bean);
					initview(false);
				}catch(Exception e){
					
				}
				break;
			case 2:

				break;
			default:
				break;
			}
		}
	}
}