package com.abct.tljr.news.adapter;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.abct.tljr.BaseFragmentActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.model.Options;
import com.abct.tljr.news.NewsCommentActivity;
import com.abct.tljr.news.NewsActivity;
import com.abct.tljr.news.bean.Comment;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.bean.Reply;
import com.abct.tljr.news.bean.Tag;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult; 
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommentAdapter extends BaseAdapter {

	ArrayList<Comment> commentList;
	LayoutInflater inflater = null;
	NewsCommentActivity activity;
	ViewHolder mHolder = null;
	ListView listview_comment;
	 public final String Tag = "CommentAdapter";
	public   String default_avatar = "drawable://" + R.drawable.img_avatar;
 
	public static int red = Color.parseColor("#eb5244");
	String newsid ;
	public CommentAdapter(NewsCommentActivity activity, ArrayList<Comment> commentList,
			ListView listview ,String newsid) {
		this.activity = activity;
		this.commentList = commentList;
		inflater = LayoutInflater.from(activity);
		this.listview_comment = listview;
		this.newsid =newsid ;
	 
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commentList == null ? 0 : commentList.size();
	}

	@Override
	public Comment getItem(int position) {
		if (commentList != null && commentList.size() != 0) {
			return commentList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Comment cmt = getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.tljr_item_news_details_comment,
					null);
			mHolder = new ViewHolder();

			
			mHolder.img_avatar = (ImageView) convertView
			.findViewById(R.id.img_avatar);
			
//			mHolder.delect = (TextView) convertView
//					.findViewById(R.id.tv_comment_delete);
//			
//			mHolder.vote = (TextView) convertView
//					.findViewById(R.id.tv_comment_vote);
			
			mHolder.add_child = (ImageView)convertView.findViewById(R.id.add_child);
			
			mHolder.cm_content = (TextView) convertView
					.findViewById(R.id.tljr_comment_contents);
			mHolder.cm_content.setTextSize(Constant.ZXFontSize);
			mHolder.cm_name = (TextView) convertView
					.findViewById(R.id.tljr_comment_name);
 			mHolder.cm_time = (TextView) convertView
 					.findViewById(R.id.tljr_comment_time);

			mHolder.ly_praise = (RelativeLayout) convertView
					.findViewById(R.id.tljr_ly_news_comment_praise);
			mHolder.btn_praise = (ImageView) convertView
					.findViewById(R.id.tljr_btn_comment_praise);
			mHolder.tv_praise_num = (TextView) convertView
					.findViewById(R.id.tljr_tx_comment_praise_num);

			mHolder.layout_child =(LinearLayout)convertView.findViewById(R.id.layout_child);
			
		
			
			
			
			
//			mHolder.vote.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v)
//				{
//					if (MyApplication.getInstance().self == null) {
//						activity.showToast("未登录或注册无法完成操作");
//						((NewsActivity) activity).login();
//						return;
//					}
//					sendVoteComment(v);
//				}
//			});
			
			
			
//			//删除评论
//			mHolder.delect.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v)
//				{
//					 
//					commentList.remove(position);
//					notifyDataSetChanged(); 
//					sendDeleteComment(v);
//					 
//				}
//			});
			
			
			
			mHolder.btn_praise.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					if (MyApplication.getInstance().self == null) {
						activity.showToast("未登录或注册无法完成操作");
						((NewsCommentActivity) activity).login();
						return;
					}  else {
						 
						ProgressDlgUtil.showProgressDlg("", activity);
						String url = UrlUtil.URL_new+"api/uc/cadd";
						String param = "oper=3&uid="
								+ MyApplication.getInstance().self
										.getId() + "&cid="
								+ cmt.getId()+"&id="+newsid;
						LogUtil.i(Tag, url + "?" + param);
						NetUtil.sendPost(url, param, new NetResult() {
							@Override
							public void result(String msg) {
								// TODO Auto-generated method stub
								LogUtil.i(Tag, msg);
								try{
									JSONObject allJson =new JSONObject(msg);
									if (allJson == null) {
										Toast.makeText(activity, "无法连接服务器请稍后再试", Toast.LENGTH_SHORT).show();
										return;
									}
									String status = allJson.getString("status");
									String message = allJson.getString("msg");
									if ( status.equals("1")) {
										activity.handler.post(new Runnable() {
										@Override
										public void run() {
											
											ViewHolder mHolder = (ViewHolder) v.getTag();
											
											mHolder.tv_praise_num.setText(Integer.valueOf(cmt.getPraise())+1+"");
											mHolder.btn_praise.setImageResource(R.drawable.img_zan_dianliang);
										}
									});
									}
									Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
									ProgressDlgUtil.stopProgressDlg();
								}catch(Exception e){
									
								}
							}
						});
					}

					
					
					
					
				}
			});
			
			 
			
		 
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
	
		mHolder.cmt = cmt;
		mHolder.cm_name.setText(cmt.getName());
		mHolder.cm_content.setText(cmt.getContent());
		mHolder.tv_praise_num.setText(cmt.getPraise());
		mHolder.cm_time.setText(cmt.getTime());
		mHolder.ly_praise.setTag(mHolder);
	 
		mHolder.btn_praise.setTag(mHolder);
		
		
		mHolder.add_child.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.showCmtDialog(cmt);
			}
		});
		
		
		LinearLayout layout_child = ((LinearLayout) convertView
				.findViewById(R.id.layout_child));
		layout_child.setVisibility(View.VISIBLE);

		if (cmt.getReply() != null && cmt.getReply().length > 0) {
			layout_child.removeAllViews();
			Reply[] replys = cmt.getReply();
			for (Reply reply : replys) {
				TextView tv = new TextView(activity);
				tv.setPadding(10, 10, 10, 10);
				SpannableString ss = new SpannableString(
						reply.getNickname() + "： " + reply.getReply());
				ss.setSpan(new ForegroundColorSpan(red), 0, reply
						.getNickname().length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				tv.setText(ss);
				layout_child.addView(tv);
				;
			}
		}
		
//		mHolder.delect.setTag(mHolder);
//		mHolder.vote.setTag(mHolder);
		/*
		 * 删除自己评论
		 */
//		if(MyApplication.getInstance().self != null){
//			
//			if(cmt.getUser_id().equals(MyApplication.getInstance().self.getId())){
//				mHolder.delect.setVisibility(View.VISIBLE);
//			} 
//		}
		
		/*
		 * 头像
		 */
		if(!cmt.getAurl().equals("default")){
			StartActivity.imageLoader.displayImage(cmt.getAurl(), mHolder.img_avatar,Options.getCircleListOptions());
		}else{
			StartActivity.imageLoader.displayImage(default_avatar, mHolder.img_avatar,Options.getCircleListOptions());
		}
	
		
		
		
//		try {
//		 	mHolder.cm_time.setText(Util.getStandardDate(cmt.getTime()));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return convertView;
	}

//	
//	private void sendVoteComment(View v) {
//		
//	 
//			 
//		
//	 	ProgressDlgUtil.showProgressDlg("", activity);
//		final ViewHolder mHolder = (ViewHolder) v.getTag();
//		Comment cmt = mHolder.cmt;
//		String param = "cId=" + cmt.getId() + "&auditorId="
//				+ MyApplication.getInstance().self.getId() + "&nId="
//				+ cmt.getNewsId() + "&platform=1"+"&sp="+news.getLetterSpecies()+"&timeKey="+news.getTime()+"&pId="+cmt.getUser_id();
//		LogUtil.i(Tag, UrlUtil.URL_vote+"?"+param);
//			
//		
//		//TLUrl.URL_vote
//		NetUtil.sendPost(UrlUtil.URL_vote, param, new NetResult() {
//
//			@Override
//			public void result(final String msg) {
//				activity.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						LogUtil.i(Tag, "msg"+msg);
//						try
//						{
//							JSONObject obj = new JSONObject(msg);
//							String status = obj.getString("status");
//							if(status.equals("1")){
//								Toast.makeText(activity, obj.getString("result"), 1).show();
//							}else{
//								Toast.makeText(activity, "举报失败，请稍后重试", 1).show();
//							}
//							ProgressDlgUtil.stopProgressDlg();
//						} catch (JSONException e)
//						{
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
// 
//					}
//				});
//			}
//		});
//	}
//	
//	
//	private void sendDeleteComment(View v) {
//	 	ProgressDlgUtil.showProgressDlg("", activity);
//		final ViewHolder mHolder = (ViewHolder) v.getTag();
//		Comment cmt = mHolder.cmt;
//		String param = "cId=" + cmt.getId() + "&pId="
//				+ MyApplication.getInstance().self.getId() + "&nId="
//				+ cmt.getNewsId() + "&type=4"+"&sp="+news.getLetterSpecies()+"&timeKey="+news.getTime();
//		LogUtil.i(Tag, UrlUtil.URL_comment+"?"+param);
//		NetUtil.sendPost(UrlUtil.URL_comment, param, new NetResult() {
//
//			@Override
//			public void result(final String msg) {
//				activity.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						LogUtil.i(Tag,msg+"*");
//						try
//						{
//							JSONObject obj = new JSONObject(msg);
//							String status = obj.getString("status");
//							if(status.equals("1")){
//								Toast.makeText(activity, obj.getString("result"), 1).show();
//							}else{
//								Toast.makeText(activity, "删除失败，请稍后重试", 1).show();
//							}
//							ProgressDlgUtil.stopProgressDlg();
//						} catch (JSONException e)
//						{
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
// 
//					}
//				});
//			}
//		});
//	}
//	
	    
	static class ViewHolder {
		TextView cm_name; // 用户名
	 	TextView cm_time; // 时间
		TextView cm_content; // 内容
		RelativeLayout ly_praise; // 点赞布局
		ImageView btn_praise; // 点赞按钮
		TextView tv_praise_num; // 点赞数

//		TextView delect;  //删除评论
//		TextView vote; //举报评论
		
		ImageView img_avatar ;
		ImageView add_child ;
		LinearLayout layout_child ; // 子评论
		 

//		TextView blue_show_zan;
//		TextView blue_show_cai;
		Comment cmt;
	}

}