package com.abct.tljr.shouye;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.news.bean.Comment;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.CustomerRatingDialog;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.DateUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SpeakActiviy extends BaseActivity {
	private static final int ID_TITLE = 1;
	private RelativeLayout newest_comment;
	private RecyclerView lv;
	private LinearLayoutManager manager;
	private int lastVisibleItem;
	private int page = 0;
	private int size = 10;
	private ArrayList<Comment> list = new ArrayList<Comment>();
	private ArrayList<Comment> mList = new ArrayList<Comment>();
	private String id;
	private boolean isFlush = false;
	private int action=0;
	private int item=0;
	private int leftposition=0;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speak);
		
		id = getIntent().getStringExtra("id");
		action=getIntent().getIntExtra("action",-1);
		item=getIntent().getIntExtra("item",-1);
		leftposition=getIntent().getIntExtra("leftposition",-1);
		
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		 
		findViewById(R.id.save).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MyApplication.getInstance().self == null) {
					showToast("请先登录");
					return;
				}
				new CustomerRatingDialog(SpeakActiviy.this, UrlUtil.URL_ZR + "crowd/remark",
						"id=" + id + "&uid=" + User.getUser().getId() + "&token=" + Configs.token, new Complete() {
					@Override
					public void complete() {
						if(action==1){
							ZhongchouBean bean=MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.
								listLeftBeans.get(leftposition).getFinishViewItemShow().listBean.get(item);
							bean.setRemark(bean.getRemark()+1);
							MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.
									listLeftBeans.get(leftposition).getFinishViewItemShow().adapter.notifyDataSetChanged();
						}else if(action==2){
							ZhongchouBean bean=MyApplication.getInstance().getMainActivity().zhiyanFragment.artzhongchou.list.get(item);
							bean.setMarket(bean.getMarket()+1);
							MyApplication.getInstance().getMainActivity().zhiyanFragment.artzhongchou.adapter.notifyDataSetChanged();
						}
						initData();
					}
				}).show();
			}
		});
		 
		newest_comment =(RelativeLayout)findViewById(R.id.newestlayout);
		 
		lv = (RecyclerView) findViewById(R.id.lv);
	 
		manager = new LinearLayoutManager(this);
		lv.setLayoutManager(manager);
		lv.setItemAnimator(new DefaultItemAnimator());
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
		initData();
	}

	public void initData() {
		page = 0;
		list.clear();
		isFlush = false;
		loadMore();
	}

	public void loadMore() {
		if (User.getUser() == null) {
			return;
		}
		if (isFlush) {
			lv.stopScroll();
			return;
		}
		ProgressDlgUtil.showProgressDlg("", this);
		isFlush = true;
		page++;
		NetUtil.sendGet(UrlUtil.URL_ZR +"crowd/getRemakList","id="+id+"&page="+page+"&size="+size,
				new NetResult() {
					@Override
					public void result(String msg) {
						ProgressDlgUtil.stopProgressDlg();
						if (msg != null && !msg.equals("")) {
							try {
								JSONObject jsonobject = new JSONObject(msg);
								if (jsonobject.getInt("status") == 1) {
									JSONArray array = jsonobject.getJSONArray("msg");
									 if(page==1){
										 newest_comment.setVisibility(array.length() > 0 ? View.VISIBLE : View.INVISIBLE);
									 }	
									mList.clear();
									for (int i = 0; i < array.length(); i++) {
										JSONObject object = array.getJSONObject(i);
										Comment comment = new Comment();
										comment.setId(object.getString("crowdId"));
										comment.setTime(DateUtil.getDate(object.getLong("date") * 1000));
										comment.setContent(object.getString("msg"));
										JSONObject user = object.getJSONObject("user");
										comment.setAurl(user.optString("avatar", "default"));
										comment.setName(user.getString("nickName"));
										comment.setUser_id(user.getString("uid"));
										mList.add(comment);
									}
									if (mList.size() > 0) {
										list.addAll(mList);
										mList.clear();
										lv.stopScroll();
										adapter.notifyDataSetChanged();
										isFlush = false;
									} else {
										lv.stopScroll();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
								newest_comment.setVisibility(View.GONE);
							}
							adapter.notifyDataSetChanged();
						} else {
							newest_comment.setVisibility(View.GONE);
						}
					}
				});
	}

	public String default_avatar = "drawable://" + R.drawable.img_avatar;
	private RecyclerView.Adapter<ViewHolder> adapter = new RecyclerView.Adapter<ViewHolder>() {
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			ViewHolder holder = new ViewHolder(LayoutInflater.from(SpeakActiviy.this).inflate(R.layout.tljr_item_news_comment, parent, false));
			return holder;
		}
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			Comment cmt = list.get(position);
			holder.name.setText(cmt.getName());
			holder.content.setText(cmt.getContent());
			if (!cmt.getAurl().equals("default")){
				StartActivity.imageLoader.displayImage(cmt.getAurl(), holder.avatar, Options.getCircleListOptions());
			} else {
				StartActivity.imageLoader.displayImage(default_avatar, holder.avatar, Options.getCircleListOptions());
			}
			holder.time.setText(cmt.getTime());
		}
		@Override
		public int getItemCount() {
			return list.size();
		}
	};

	class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View convertView) {
			super(convertView);
			convertView.findViewById(R.id.tv_comment_delete).setVisibility(View.GONE);
			convertView.findViewById(R.id.tv_comment_vote).setVisibility(View.GONE);
			convertView.findViewById(R.id.tljr_ly_news_comment_praise).setVisibility(View.GONE);
			convertView.findViewById(R.id.tljr_btn_comment_praise).setVisibility(View.GONE);
			convertView.findViewById(R.id.tljr_tx_comment_praise_num).setVisibility(View.GONE);
			convertView.findViewById(R.id.tljr_ly_news_comment_cai).setVisibility(View.GONE);
			convertView.findViewById(R.id.tljr_btn_comment_cai).setVisibility(View.GONE);
			convertView.findViewById(R.id.tljr_tx_comment_cai_num).setVisibility(View.GONE);
			convertView.findViewById(R.id.blue_show_zan).setVisibility(View.GONE);
			convertView.findViewById(R.id.blue_show_cai).setVisibility(View.GONE);
			name = (TextView) convertView.findViewById(R.id.tljr_comment_name);
			content = (TextView) convertView.findViewById(R.id.tljr_comment_contents);
			avatar = (ImageView) convertView.findViewById(R.id.img_avatar);
			time = (TextView) convertView.findViewById(R.id.tljr_comment_time);
		}
		TextView name;
		TextView content;
		ImageView avatar;
		TextView time;
	}

}