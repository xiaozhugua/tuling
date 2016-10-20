package com.abct.tljr.ui.activity.zhiyan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.model.Ticket;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.FileUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年12月2日 上午10:54:34
 */
public class YhjActivity extends BaseActivity implements OnClickListener {
	private RecyclerView lv;
	private int lastVisibleItem;
	private MyAdapter adapter;
	private ArrayList<Ticket> list = new ArrayList<Ticket>();
	private ArrayList<Ticket> mList = new ArrayList<Ticket>();
	private LinearLayoutManager manager;
	private boolean isFlush = false;
	private int page = 0;
	private int onePageCount = 10;
	private SimpleDateFormat mSimpleDateFormat=null;
	private View back=null;
	private TextView title=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_zhiyan_yhj);
		
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		lv =(RecyclerView)findViewById(R.id.yhj_recycler);
		back=(View)findViewById(R.id.back);
		back.setOnClickListener(this);
		lv.setAdapter(adapter);
		manager = new LinearLayoutManager(this);
		lv.setLayoutManager(manager);
		adapter = new MyAdapter(this, list);
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
		page++;
		NetUtil.sendGet(UrlUtil.URL_ZR + "coupon/getMyCouponList",
				"uid=" + User.getUser().getId() + "&token=" + Configs.token + "&page=" + page + "&size=" + onePageCount,
				new NetResult() {
					@Override
					public void result(String msg) {
						LogUtil.e("getData", msg);
						ProgressDlgUtil.stopProgressDlg();
						try {
							final JSONObject jsonObject = new JSONObject(msg);
							if (jsonObject.getInt("status") == 1) {
								final org.json.JSONArray array = jsonObject.getJSONArray("msg");
								for (int i = 0; i < array.length(); i++) {
									JSONObject object = array.getJSONObject(i);
									JSONObject obj = object.optJSONObject("info");
									Ticket ticket = new Ticket();
									ticket.setCouponId(object.optString("couponId"));
									ticket.setEnd(object.optBoolean("isEnd"));
									ticket.setId(object.optString("id"));
									ticket.setMoney(obj.optInt("money"));
									String start=mSimpleDateFormat.format(new Date(object.getLong("createDate")*1000));
									String end=mSimpleDateFormat.format(new Date(object.getLong("endDate")*1000));
									int money=obj.getInt("canBeUseMoney")/100;
									ticket.setDesc("有效期:"+start+"至"+end+"满"+money+"元可用");
									ticket.setBgurl(obj.optString("bgUrl"));
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
							LogUtil.e("YHJgetData",e.getMessage());
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
		case R.id.other:
			break;
		default:
			break;
		}
	}

}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

	private Context context;
	private ArrayList<Ticket> list;

	public MyAdapter(Context context, ArrayList<Ticket> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = new ViewHolder(
				LayoutInflater.from(context).inflate(R.layout.fragment_zhiyan_yhj_item, parent, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Ticket ticket = list.get(position);
		FileUtil.showImage(FileUtil.filePath, ticket.getBgurl(), holder.bj);
		holder.money.setText(ticket.getMoney() / 100 + "");
		holder.desc.setText(ticket.getDesc().replace("|", "\n"));
		holder.itemView.setTag(ticket);
		holder.itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ticket ticket = (Ticket) v.getTag();
				// if (ticket.isEnd()) {
				// showToast("此卷已过期！");
				// } else {
				((Activity) context).setResult(1,
						new Intent().putExtra("id", ticket.getId()).putExtra("money", ticket.getMoney()));
				((Activity) context).finish();
				// }
			}
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public final class ViewHolder extends RecyclerView.ViewHolder {
		private TextView money, desc;
		private View bj;

		public ViewHolder(View v) {
			super(v);
			bj = v.findViewById(R.id.bj);
			money = (TextView) v.findViewById(R.id.money);
			desc = (TextView) v.findViewById(R.id.desc);
		}
	}
}