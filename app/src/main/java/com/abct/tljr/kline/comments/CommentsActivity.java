package com.abct.tljr.kline.comments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.kline.comments.XScrollView.IXScrollViewListener;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

public class CommentsActivity extends BaseActivity {

	private String TAG = "CommentsActivity";
	private String market, code, name;
	private LinearLayout Linear;
	private LayoutInflater inflater;
	private ArrayList<View> views;
	private int page = 0;
	private ArrayList<CommentsBean> arrayList;
	private XScrollView mScrollView;
	private boolean isupdata = false;
	private TextView load;
	public int presize = 0;
	private TextView red, greed;
	private boolean havemore = false;
	private ImageView zan, cai;
	private String type = "isnull";
	public String praiseStr = "praise";
	private int praise = 0, tread = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_comments);
		findview();
	}

	private void findview() {
		mScrollView = (XScrollView) findViewById(R.id.tljr_sy_sc);
		mScrollView.initWithContext(CommentsActivity.this);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(true);
		mScrollView.setAutoLoadEnable(false);
		mScrollView.setRefreshTime(Util.getNowTime());
		// mScrollView.startLoadMore();
		mScrollView.setIXScrollViewListener(new IXScrollViewListener() {
			@Override
			public void onRefresh() {
				isupdata = true;
				getlist(false);
			}

			@Override
			public void onLoadMore() {
				if (load.getVisibility() == View.VISIBLE) {
					getlist(true);
				}
			}
		});

		zan = (ImageView) findViewById(R.id.zan);
		cai = (ImageView) findViewById(R.id.cai);
		red = (TextView) findViewById(R.id.red);
		greed = (TextView) findViewById(R.id.greed);
		load = (TextView) findViewById(R.id.load);
		load.setOnClickListener(onclick);
		views = new ArrayList<View>();
		arrayList = new ArrayList<CommentsBean>();
		inflater = LayoutInflater.from(this);
		market = getIntent().getStringExtra("market");
		code = getIntent().getStringExtra("code");
		name = getIntent().getStringExtra("name");
		((TextView) findViewById(R.id.title)).setText(name);

		Linear = (LinearLayout) findViewById(R.id.ll);
		findViewById(R.id.tljr_img_futures_join_back).setOnClickListener(onclick);
		findViewById(R.id.write).setOnClickListener(onclick);
		zan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (type.equals("isnull")) {
					LikeorNO(true, true);
				} else if (type.equals(praiseStr)) {
					LikeorNO(true, false);
				}
			}
		});
		cai.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (type.equals("isnull")) {
					LikeorNO(false, true);
				} else if (type.equals("tread")) {
					LikeorNO(false, false);
				}
			}
		});

		reflushDP();
		addItemInLL(false);
		getlist(false);
	}

	private void addItemInLL(boolean isload) {
		int num = presize;
		if (!isload) {
			Linear.removeAllViews();
			views.clear();
			num = 0;
		}
		handler.sendEmptyMessage(8);
		
		
		if (type.equals("isnull")) {
			zan.setBackgroundResource(R.drawable.img_zan3);
			cai.setBackgroundResource(R.drawable.img_zan3);
		} else if (type.equals(praiseStr)) {
			zan.setBackgroundResource(R.drawable.img_zan4);
		} else if (type.equals("tread")) {
			cai.setBackgroundResource(R.drawable.img_zan4);
		}
		if(arrayList.size()<=0){
			return;
		}
		
		for (int i = num; i < arrayList.size(); i++) {
			CommentsBean bean = arrayList.get(i);
			View view = inflater.inflate(R.layout.tljr_activity_comments_item, null);
			((TextView) view.findViewById(R.id.name)).setText(bean.getUname());
			((TextView) view.findViewById(R.id.name)).setTag(i);
			((TextView) view.findViewById(R.id.name)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					CommentsBean bean = arrayList.get(Integer.parseInt(arg0.getTag().toString()));
					Intent i = new Intent(CommentsActivity.this, OnePerson.class);
					i.putExtra("cid", bean.getCid());
					i.putExtra("name", name);
					i.putExtra("uid", bean.getUid());
					startActivity(i);
				}
			});

			StartActivity.imageLoader.displayImage(bean.getAvatar(), (ImageView) view.findViewById(R.id.im));

			view.findViewById(R.id.comment).setTag(i);
			view.findViewById(R.id.comment).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent(CommentsActivity.this, EditActivity.class);
					i.putExtra("code", code);
					i.putExtra("type", 1);// 0是新增评论 1是对评论进行追评
					i.putExtra("id", arrayList.get(Integer.parseInt(arg0.getTag().toString())).getId());
					if (arrayList.get(Integer.parseInt(arg0.getTag().toString())).getOid() != null) {
						i.putExtra("oid", arrayList.get(Integer.parseInt(arg0.getTag().toString())).getOid());
					}
					startActivityForResult(i, 99);
				}
			});
			if (bean.isIspraise()) {
				((ImageView) view.findViewById(R.id.love)).setBackgroundResource(R.drawable.social_subject_menu_up_d);
			}

			((TextView) view.findViewById(R.id.love_number)).setText(bean.getPraise() + "");
			view.findViewById(R.id.love).setTag(i + "");
			// Log.d(TAG, "isispraise :"+bean.isIspraise());
			view.findViewById(R.id.love).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					CommentsBean bean = arrayList.get(Integer.parseInt(arg0.getTag().toString()));
					if (!bean.isIspraise()) {
						ItemLikeorOn(true, bean.getId());
						((TextView) views.get(Integer.parseInt(arg0.getTag().toString()))
								.findViewById(R.id.love_number)).setText((bean.getPraise() + 1) + "");
						arg0.setBackgroundResource(R.drawable.social_subject_menu_up_d);
					}
				}
			});
			String contents = "";
			if (bean.getComment_content() != null) {
				view.findViewById(R.id.becontent).setVisibility(View.VISIBLE);
				view.findViewById(R.id.becontent).setTag(bean.getComment_content());
				view.findViewById(R.id.becontent).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(CommentsActivity.this, OneCommentActivity.class);
						i.putExtra("data", arg0.getTag().toString());
						i.putExtra("code", code);
						i.putExtra("name", name);
						startActivity(i);
					}
				});
				try{
					JSONArray arr =new JSONArray(bean.getComment_content());
					if(arr==null){
						return;
					}
					if(arr.length()<=0){
						return;
					}
					for (int j = 0; j < arr.length(); j++) {
						JSONObject ob = arr.getJSONObject(j);
						if (j == 0) {
							((TextView) view.findViewById(R.id.content3))
									.setText(ob.getString("uname") + "#" + name + "#" + ob.getString("content"));
						} else {
							contents = "||" + ob.getString("uname") + ":" + ob.getString("content");
						}
					}
					JSONObject ob1 = arr.getJSONObject(0);
				}catch(Exception e){
					
				}
				// ((TextView)view.findViewById(R.id.content3)).setText(bean.getComment_content());
			} else {
				view.findViewById(R.id.becontent).setVisibility(View.GONE);
			}
			long nowtime = System.currentTimeMillis();
			long dextime = nowtime - bean.getTime();
			dextime = dextime / 1000;
			if (dextime >= 86400) {
				((TextView) view.findViewById(R.id.time)).setText(Util.getDateOnlyDat(bean.getTime()));
			} else {
				if (dextime < 86400 && dextime >= 3600) {
					((TextView) view.findViewById(R.id.time)).setText((int) (dextime / 3600) + "小时前");
				} else if (dextime < 3600 & dextime >= 60) {
					((TextView) view.findViewById(R.id.time)).setText((int) (dextime / 60) + "分钟前");
				} else {
					((TextView) view.findViewById(R.id.time)).setText("刚刚");
				}
			}
			((TextView) view.findViewById(R.id.contentt)).setText(bean.getContent() + contents);
			if (MyApplication.getInstance().self != null) {
				if (bean.getUid().equals(MyApplication.getInstance().self.getId())) {
					view.findViewById(R.id.delete).setVisibility(View.VISIBLE);
					view.findViewById(R.id.delete).setTag(bean.getId() + "," + i);
					view.findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							String[] s = arg0.getTag().toString().split(",");
							Delete(s[0], Integer.parseInt(s[1]));
						}
					});
				} else {
					view.findViewById(R.id.delete).setVisibility(View.GONE);
				}
			} else {
				view.findViewById(R.id.delete).setVisibility(View.GONE);
			}
			Linear.addView(view);
			views.add(view);
		}

		// 结束刷新
		if (isupdata) {
			mScrollView.setRefreshTime(Util.getNowTime());
			mScrollView.stopRefresh();
			isupdata = false;
		}
		if (havemore) {
			load.setVisibility(View.VISIBLE);
		} else {
			load.setVisibility(View.GONE);
		}
		if (isload) {
			mScrollView.stopLoadMore();
			load.setText("加载更多");
		}

		Animation();
	}

	private void getlist(final boolean isload) {
		
		if (isload) {
			// page++;
			presize = arrayList.size();
			load.setText("加载中......");
		} else {
			presize = 0;
			page = 0;
		}
		
		
		   NetUtil.sendPost(UrlUtil.URL_Comments_getlist,"cid="+"symbol_" + code+"&page="+page, new NetResult() {

  				@Override
  				public void result(final String msg) {

  					handler.post(new Runnable() {

  						@Override
  						public void run() {
  							LogUtil.i(TAG, "data :" + msg);
  							if (msg.equals("error") || msg==null) {
  								
  							} else {
  							
  								try {
  									org.json.JSONObject ob = new org.json.JSONObject(msg);
  									if(ob==null &&!ob.has("datas")){
  										return;
  									}
  									org.json.JSONArray arr;
  									arr = ob.getJSONArray("datas");
  									type = ob.optString("type","isnull");
  									if (ob.optInt("page") > 0) {
  										page = ob.optInt("page");
  										havemore = true;
  									} else {
  										havemore = false;
  									}
  									if (!isload) {
  										arrayList.clear();
  									}
  									for (int i = arr.length() - 1; i >= 0; i--) {
  										org.json.JSONObject obt = arr.getJSONObject(i);
  										CommentsBean bean = new CommentsBean();
  										bean.setAvatar(obt.optString("avatar"));
  										bean.setCid(obt.optString("cid"));
  										bean.setContent(obt.optString("content"));
  										bean.setId(obt.optString("id"));
  										bean.setPraise(obt.optInt("praise"));
  										bean.setTime(obt.optLong("time"));
  										bean.setTread(obt.optInt("tread"));
  										bean.setIspraise(obt.optBoolean("ispraise"));
  										bean.setIstread(obt.optBoolean("istread"));
  										bean.setUid(obt.optString("uid"));
  										bean.setUname(obt.optString("uname"));
  										if (obt.optString("comment_values") != null) {
  											bean.setComment_content(obt.optString("comment_values"));
  										}
  										if (obt.optString("oid") != null) {
  											bean.setOid(obt.optString("oid"));
  										}
  										arrayList.add(bean);
  									}
  									if (ob.getJSONObject("result") != null) {
  										org.json.JSONObject ob2 = ob.getJSONObject("result");
  										praise = ob2.optInt("praise");
  										tread = ob2.optInt("tread");
  									}
  									if (handler != null) {
  										if (isload) {
  											handler.sendEmptyMessage(4);
  										} else {
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

  				}
  			});
		
		
		
		
		
//		RequestParams params = new RequestParams();
//		// Log.d(TAG, "");
//		params.addBodyParameter("cid", "symbol_" + code);
//		params.addBodyParameter("uid", MyApplication.getInstance().self.getId());
//		
//		params.addBodyParameter("page", page + "");
//		XUtilsHelper.sendPost(UrlUtil.URL_Comments_getlist, params, new HttpCallback() {
//			@Override
//			public void callback(String data) {
//				// TODO Auto-generated method stub
//				LogUtil.i(TAG, "data :" + data);
//				if (data.equals("error") || data==null) {
//					
//				} else {
//				
//					try {
//						org.json.JSONObject ob = new org.json.JSONObject(data);
//						if(ob==null &&!ob.has("datas")){
//							return;
//						}
//						org.json.JSONArray arr;
//						arr = ob.getJSONArray("datas");
//						type = ob.optString("type","isnull");
//						if (ob.optInt("page") > 0) {
//							page = ob.optInt("page");
//							havemore = true;
//						} else {
//							havemore = false;
//						}
//						if (!isload) {
//							arrayList.clear();
//						}
//						for (int i = arr.length() - 1; i >= 0; i--) {
//							org.json.JSONObject obt = arr.getJSONObject(i);
//							CommentsBean bean = new CommentsBean();
//							bean.setAvatar(obt.optString("avatar"));
//							bean.setCid(obt.optString("cid"));
//							bean.setContent(obt.optString("content"));
//							bean.setId(obt.optString("id"));
//							bean.setPraise(obt.optInt("praise"));
//							bean.setTime(obt.optLong("time"));
//							bean.setTread(obt.optInt("tread"));
//							bean.setIspraise(obt.optBoolean("ispraise"));
//							bean.setIstread(obt.optBoolean("istread"));
//							bean.setUid(obt.optString("uid"));
//							bean.setUname(obt.optString("uname"));
//							if (obt.optString("comment_values") != null) {
//								bean.setComment_content(obt.optString("comment_values"));
//							}
//							if (obt.optString("oid") != null) {
//								bean.setOid(obt.optString("oid"));
//							}
//							arrayList.add(bean);
//						}
//						if (ob.getJSONObject("result") != null) {
//							org.json.JSONObject ob2 = ob.getJSONObject("result");
//							praise = ob2.optInt("praise");
//							tread = ob2.optInt("tread");
//						}
//						if (handler != null) {
//							if (isload) {
//								handler.sendEmptyMessage(4);
//							} else {
//								handler.sendEmptyMessage(0);
//							}
//						}
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}
//			}
//		});
	}

	private boolean isDeleting = false;

	// 删除
	private void Delete(String id, final int position) {
		if (isDeleting) {
			showToast("删除操作过快!");
			return;
		}
		isDeleting = true;
		ProgressDlgUtil.showProgressDlg("", this);
		LogUtil.e("cid", "symbol_" + code);
		LogUtil.e("id", id);
		   NetUtil.sendPost(UrlUtil.URL_Comments_delete, "cid="+"symbol_" + code+"&id="+id, new NetResult() {

  				@Override
  				public void result(final String msg) {

  					handler.post(new Runnable() {

  						@Override
  						public void run() {
  							LogUtil.e("data", msg);
  							try{
  								JSONObject ob =new JSONObject(msg);
  								if (handler != null) {
  									if (ob.getBoolean("isok")) {
//  										arrayList.remove(position);
  										handler.sendEmptyMessage(1);
  									} else {
  										handler.sendEmptyMessage(2);
  									}
  								}
  								ProgressDlgUtil.stopProgressDlg();
  								isDeleting = false;
  							}catch(Exception e){
  								
  							}
  						}
  					});

  				}
  			});
		
		
		
		
		
//		RequestParams params = new RequestParams();
//
//		params.addBodyParameter("cid", "symbol_" + code);
//		params.addBodyParameter("id", id);
//		// if(arrayList.get(position).getOid()!=null){
//		// params.addBodyParameter("oid", arrayList.get(position).getOid());
//		// Log.d(TAG, "have oid :"+arrayList.get(position).getOid());
//		// }
//		ProgressDlgUtil.showProgressDlg("", this);
//		XUtilsHelper.sendPost(UrlUtil.URL_Comments_delete, params, new HttpCallback() {
//			@Override
//			public void callback(String data) {
//				// TODO Auto-generated method stub
//				LogUtil.e("data", data);
//				try{
//					JSONObject ob =new JSONObject(data);
//					if (handler != null) {
//						if (ob.getBoolean("isok")) {
////							arrayList.remove(position);
//							handler.sendEmptyMessage(1);
//						} else {
//							handler.sendEmptyMessage(2);
//						}
//					}
//					ProgressDlgUtil.stopProgressDlg();
//					isDeleting = false;
//				}catch(Exception e){
//					
//				}
//			}
//		});
	}

	private void ItemLikeorOn(final boolean islike, String id) {
		
		
		
		String params = "uid="+MyApplication.getInstance().self.getId()+"&cid="+"symbol_" + code+"&id="+id ;
		
		if (islike) {
			params = params+"&type=praise";
		} else {
			params = params+"&type=tread";
		}
		   NetUtil.sendPost(UrlUtil.URL_Comments_update, params, new NetResult() {

  				@Override
  				public void result(final String msg) {

  					handler.post(new Runnable() {

  						@Override
  						public void run() {
  							try{
  								if (!msg.equals("error") && msg != null) {
  									JSONObject ob =new JSONObject(msg);
  								}
  							}catch(Exception e){
  								
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
//		XUtilsHelper.sendPost(UrlUtil.URL_Comments_update, params, new HttpCallback() {
//
//			@Override
//			public void callback(String data) {
//				// TODO Auto-generated method stub
//				try{
//					if (!data.equals("error") && data != null) {
//						JSONObject ob =new JSONObject(data);
//					}
//				}catch(Exception e){
//					
//				}
//			}
//		});
	}

	// 点赞或踩
	private void LikeorNO(final boolean islike, final boolean iscancel) {
		ProgressDlgUtil.showProgressDlg("", this);
		
		String params = "uid="+MyApplication.getInstance().self.getId()+"&cid="+"symbol_" + code+"&name=symbol_all" ;
		
		if (islike) {
			params = params+"&type=praise";
		} else {
			params = params+"&type=tread";
		}
		if (iscancel) {
			params = params+"&issave=true";
		} else {
			params = params+"&issave=false";
		}
		   NetUtil.sendPost(UrlUtil.URL_Comments_update, params, new NetResult() {

  				@Override
  				public void result(final String msg) {

  					handler.post(new Runnable() {

  						@Override
  						public void run() {
  							LogUtil.e("LikeorNO", msg);
  							try{
  								if (!msg.equals("error")) {
  									JSONObject ob =new JSONObject(msg);
  									if (ob.getBoolean("isok")) {
  										if (islike) {
  											type = iscancel ? praiseStr : "isnull";
  											if (iscancel) {
  												praise++;
  											} else {
  												praise--;
  											}
  											zan.setBackground(iscancel ? getResources().getDrawable(R.drawable.img_zan4)
  													: getResources().getDrawable(R.drawable.img_zan3));
  										} else {
  											type = iscancel ? "tread" : "isnull";
  											if (iscancel) {
  												tread++;
  											} else {
  												tread--;
  											}
  											cai.setBackground(iscancel ? getResources().getDrawable(R.drawable.img_zan4)
  													: getResources().getDrawable(R.drawable.img_zan3));
  										}
  									}
  								}
  							}catch(Exception e){
  								
  							}
  							if (handler != null) {	
  								handler.sendEmptyMessage(8);
  							}
  						}
  					});

  				}
  			});
		
		
		
		
		
//		RequestParams params = new RequestParams();
//	//	params.addBodyParameter("cid", "symbol_" + code);
//	//	params.addBodyParameter("uid", MyApplication.getInstance().self.getId());
//	//	params.addBodyParameter("name", "symbol_all");
//		if (islike) {
//			params.addBodyParameter("type", praiseStr);
//		} else {
//			params.addBodyParameter("type", "tread");
//		}
//		if (iscancel) {
//			params.addBodyParameter("issave", true + "");
//		} else {
//			params.addBodyParameter("issave", false + "");
//		}
//		XUtilsHelper.sendPost(UrlUtil.URL_Comments_update, params, new HttpCallback() {
//			@Override
//			public void callback(String data) {
//				// TODO Auto-generated method stub
//				LogUtil.e("LikeorNO", data);
//				try{
//					if (!data.equals("error")) {
//						JSONObject ob =new JSONObject(data);
//						if (ob.getBoolean("isok")) {
//							if (islike) {
//								type = iscancel ? praiseStr : "isnull";
//								if (iscancel) {
//									praise++;
//								} else {
//									praise--;
//								}
//								zan.setBackground(iscancel ? getResources().getDrawable(R.drawable.img_zan4)
//										: getResources().getDrawable(R.drawable.img_zan3));
//							} else {
//								type = iscancel ? "tread" : "isnull";
//								if (iscancel) {
//									tread++;
//								} else {
//									tread--;
//								}
//								cai.setBackground(iscancel ? getResources().getDrawable(R.drawable.img_zan4)
//										: getResources().getDrawable(R.drawable.img_zan3));
//							}
//						}
//					}
//				}catch(Exception e){
//					
//				}
//				if (handler != null) {
//					handler.sendEmptyMessage(8);
//				}
//			}
//		});
	}

	private void reflushDP() {
		Util.getRealInfo("market=" + market + "&code=" + code, new NetResult() {
			@Override
			public void result(final String msg) {
				if (handler != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								org.json.JSONObject object = new org.json.JSONObject(msg);
								if (object.getInt("code") == 1) {
									final org.json.JSONArray array = object.getJSONArray("result");
									((TextView) findViewById(R.id.number)).setText(array.optString(0));
									((TextView) findViewById(R.id.updown)).setText((array.optDouble(8) > 0 ? "+" : "")
											+ array.optString(8).replace("null", "0.0"));
									((TextView) findViewById(R.id.updown_persen))
											.setText((array.optDouble(8) > 0 ? "+" : "")
													+ array.optString(9).replace("null", "0.0") + "%");
									GradientDrawable g = (GradientDrawable) findViewById(R.id.title_red)
											.getBackground();
									g.setColor(array.getDouble(8) > 0 ? ColorUtil.red : ColorUtil.green);
								}
							} catch (org.json.JSONException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
	}

	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.tljr_img_futures_join_back:
				finish();
				break;
			case R.id.write:
				Intent i = new Intent(CommentsActivity.this, EditActivity.class);
				i.putExtra("code", code);
				i.putExtra("type", 0);// 0是新增评论 1是对评论 评论
				startActivityForResult(i, 99);
				break;
			case R.id.zan:
				if (type.equals("isnull")) {
					LikeorNO(true, true);
				} else if (type.equals("paries")) {
					LikeorNO(true, false);
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void handleMsg(Message msg) {
		super.handleMsg(msg);
		switch (msg.what) {
		case 0:
			addItemInLL(false);
			break;
		case 1:
			// 删除成功后的操作
//			addItemInLL(false);
			isupdata = true;
			getlist(false);
			break;
		case 2:
			Toast.makeText(CommentsActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
			break;
		case 4:
			// 加载
			addItemInLL(true);
			break;
		case 8:
			int praiseper;
			int treadper;
			if (praise == 0 && tread == 0) {
				praiseper = 0;
				treadper = 0;
			} else if (praise == 0 && tread != 0) {
				praiseper = 0;
				treadper = 100;
			} else if (tread == 0 && praise != 0) {
				praiseper = 100;
				treadper = 0;
			} else {
				int all = tread + praise;
				praiseper = (praise * 100) / all;
				treadper = 100 - praiseper;
			}
			((TextView) findViewById(R.id.zan_number)).setText(praise + "(" + praiseper + "%)");
			((TextView) findViewById(R.id.cai_number)).setText("(" + treadper + "%)" + tread);
			Animation();
			ProgressDlgUtil.stopProgressDlg();
			break;
		default:
			break;
		}
	};

	private void Animation() {

		float bili = 1;
		float bili2 = 1;
		if (praise != 0) {
			bili2 = Float.valueOf(tread + "") / Float.valueOf((tread + praise) + "");
		}
		if (tread != 0) {
			bili = Float.valueOf(praise + "") / Float.valueOf((tread + praise) + "");
		}
		// int width = Util.WIDTH - Util.dp2px(this, 60);
		int width = findViewById(R.id.bian).getWidth();
		LogUtil.i(TAG, "width :" + width);
		Animation animation = new TranslateAnimation(-width, -width * bili2, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(1000);
		red.startAnimation(animation);
		Animation animation2 = new TranslateAnimation(width, width * bili, 0, 0);
		animation2.setFillAfter(true);
		animation2.setDuration(1000);
		greed.startAnimation(animation2);

	}

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
					arrayList.add(0, bean);
					addItemInLL(false);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler = null;
	}
}