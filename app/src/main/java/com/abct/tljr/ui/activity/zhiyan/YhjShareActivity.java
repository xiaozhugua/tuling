package com.abct.tljr.ui.activity.zhiyan;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.Ticket;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.login.BwManager;
import com.qh.common.login.Configs;
import com.qh.common.login.util.ShareContent;
import com.qh.common.model.AppInfo;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年12月2日 上午10:54:34
 */
public class YhjShareActivity extends BaseActivity implements OnClickListener {
	private LinearLayout baseView;
	private RelativeLayout title;
	public RecyclerView lv;
	private int lastVisibleItem;
	private ShareAdapter adapter;
	private ArrayList<Ticket> list = new ArrayList<Ticket>();
	private ArrayList<Ticket> mList = new ArrayList<Ticket>();
	private LinearLayoutManager manager;

	private boolean isFlush = false;
	private int page = 0;
	private int onePageCount = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		BwManager.getInstance().initShare(this);
		baseView = new LinearLayout(this);
		init();
	}

	private void init() {
		setContentView(baseView);
		((LinearLayout) baseView).setOrientation(LinearLayout.VERTICAL);
		baseView.setBackgroundColor(getResources().getColor(R.color.tljr_bj));
		title = (RelativeLayout) View.inflate(this, R.layout.tljr_view_activity_title, null);
		title.findViewById(R.id.back).setOnClickListener(this);
		((LinearLayout) baseView).addView(title, new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.FILL_PARENT, AppInfo.dp2px(this, MyApplication.TitleHeight)));
		((TextView) title.findViewById(R.id.name)).setText("邀好友，得奖励");
		lv = new RecyclerView(this);
		RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		((LinearLayout) baseView).addView(lv, params);
		lv.setAdapter(adapter);
		manager = new LinearLayoutManager(this);
		lv.setLayoutManager(manager);
		adapter = new ShareAdapter(this, list);
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

	private void loadMore() {
		if (isFlush) {
			lv.stopScroll();
			return;
		}
		isFlush = true;
		getData();
	}

	private void getData() {
		if (User.getUser() == null) {
			showToast("获取优惠卷失败，请先登录！");
			return;
		}
		ProgressDlgUtil.showProgressDlg("", this);
		page++;
		NetUtil.sendPost(UrlUtil.URL_ZR + "user/getMyShareList",
				"uid=" + User.getUser().getId() + "&token=" + Configs.token + "&page=" + page + "&size=" + onePageCount,
				new NetResult() {

					@Override
					public void result(String msg) {
						ProgressDlgUtil.stopProgressDlg();
						LogUtil.e("getData", msg);
						ProgressDlgUtil.stopProgressDlg();
						try {
							final JSONObject jsonObject = new JSONObject(msg);
							if (jsonObject.getInt("status") == 1) {
								final org.json.JSONArray array = jsonObject.getJSONArray("msg");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(i);
									Ticket ticket = new Ticket();
									ticket.setDesc(obj.optString("desc"));
									ticket.setBgurl(obj.optString("shareUrl"));
									ticket.setName(obj.optString("name"));
									mList.add(ticket);
								}
								if (array.length() > 0) {
									list.addAll(mList);
									mList.clear();
									lv.stopScroll();
									adapter.notifyDataSetChanged();
								} else {
									lv.stopScroll();
								}
								isFlush = false;
							} else {
								lv.stopScroll();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	private PopupWindow popupWindow;

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	public void showPopupView() {
		if (popupWindow == null) {
			// 一个自定义的布局，作为显示的内容
			View contentView = View.inflate(this, R.layout.tljr_dialog_share_news, null);
			contentView.findViewById(R.id.btn_cancle).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
				}
			});
			LinearLayout v = (LinearLayout) contentView.findViewById(R.id.ly1);
			for (int i = 0; i < v.getChildCount(); i++) {
				final int m = i;
				v.getChildAt(i).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						share(m);
						popupWindow.dismiss();
					}
				});
			}
			popupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
			popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
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
		View v = lv;
		popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}// type 0:QQ 1微信 2新浪微博 3朋友圈

	private void setAlpha(float f) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = f;
		lp.dimAmount = f;
		getWindow().setAttributes(lp);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	private void share(int type) {
		switch (type) {
		case 0:
			BwManager.getInstance().shareQQ();
			break;
		case 1:
			BwManager.getInstance().shareWeiXin();
			break;
		case 2:
			BwManager.getInstance().shareSina();
			break;
		case 3:
			BwManager.getInstance().shareWeiXinPyq();
			break;
		default:
			break;
		}
	}

}

class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {

	private Context context;
	private ArrayList<Ticket> list;

	public ShareAdapter(Context context, ArrayList<Ticket> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public ShareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = new ViewHolder(
				LayoutInflater.from(context).inflate(R.layout.fragment_zhiyan_yhjshare_item, parent, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Ticket ticket = list.get(position);
		holder.name.setText(ticket.getName());
		holder.desc.setText(ticket.getDesc());
		holder.money.setTag(ticket);
		holder.money.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ticket ticket = (Ticket) v.getTag();
				ShareContent.actionUrl = ticket.getBgurl();
				ShareContent.title = ticket.getName();
				ShareContent.content = ticket.getDesc();
				((YhjShareActivity)context).showPopupView();
			}
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public final class ViewHolder extends RecyclerView.ViewHolder {
		private TextView name, desc;
		private View money;

		public ViewHolder(View v) {
			super(v);
			money = v.findViewById(R.id.money);
			name = (TextView) v.findViewById(R.id.name);
			desc = (TextView) v.findViewById(R.id.desc);
		}
	}
}