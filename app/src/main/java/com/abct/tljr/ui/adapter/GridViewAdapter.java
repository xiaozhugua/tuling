package com.abct.tljr.ui.adapter;

import java.util.ArrayList;

import com.abct.tljr.R;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.shouye.AllFerdlsActivity;
import com.abct.tljr.ui.activity.zhiyan.NullSearchActivity;
import com.qh.common.util.ColorUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

	private ArrayList<OneGu> list;
	private Context context;
	private View view;

	public GridViewAdapter(Context context, ArrayList<OneGu> list, View view) {
		this.list = list;
		this.context = context;
		this.view = view;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.view_mainframent_grid, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.now = (TextView) convertView.findViewById(R.id.number);
			holder.change = (TextView) convertView.findViewById(R.id.updown);
			holder.changep = (TextView) convertView.findViewById(R.id.updownper);
			convertView.setTag(holder);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (position == 0) {
						((MainActivity) context).setCurrent(0);
					} else if (position == 1) {
						context.startActivity(new Intent(context, AllFerdlsActivity.class));
					} else if (position == 2) {
						context.startActivity(new Intent(context, NullSearchActivity.class));
					} else if (position == 3) {
						context.startActivity(new Intent(context, NullSearchActivity.class));
					}
				}
			});
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 2) {
			convertView.findViewById(R.id.background).setBackgroundResource(R.drawable.img_bankuaibeijing2);
		} else if (position == 3) {
			convertView.findViewById(R.id.background).setBackgroundResource(R.drawable.img_bankuaibeijing3);
		}
		convertView.findViewById(R.id.background).getLayoutParams().height = view.getHeight() / 2 - px2dip(context, 22);

		View view = convertView.findViewById(R.id.layout);
		OneGu gu = list.get(position);
		if (gu.getName() != null) {
			holder.name.setText(gu.getName());
			holder.now.setText((float) gu.getNow() + "");
			holder.changep.setText(gu.getP_change() > 0 ? "+" + gu.getP_change() + "%" : gu.getP_change() + "%");
			holder.change.setText(gu.getChange() > 0 ? "+" + (float) gu.getChange() + "" : (float) gu.getChange() + "");
			holder.now.setTextColor((float) gu.getChange() > 0 ? ColorUtil.red : ColorUtil.green);
			holder.changep.setTextColor((float) gu.getChange() > 0 ? ColorUtil.red : ColorUtil.green);
			holder.change.setTextColor((float) gu.getChange() > 0 ? ColorUtil.red : ColorUtil.green);
		}
		if (position == 1) {
			((View) convertView.findViewById(R.id.color)).setBackgroundResource(R.drawable.img_dian2);
			((TextView) convertView.findViewById(R.id.from)).setText("Powered by 图灵Ferdls");
			((TextView) convertView.findViewById(R.id.tv)).setText("关注什么 | 中长线");
			convertView.findViewById(R.id.im_bottom).setVisibility(View.GONE);
			convertView.findViewById(R.id.im_bottom2).setVisibility(View.VISIBLE);
		} else if (position == 2) {
			view.findViewById(R.id.rlright).setVisibility(View.GONE);
			view.findViewById(R.id.name_bottom).setVisibility(View.VISIBLE);
			view.findViewById(R.id.imm).setRotation(180);
			((View) convertView.findViewById(R.id.color)).setBackgroundResource(R.drawable.img_dian3);
			((TextView) convertView.findViewById(R.id.tv)).setText("怎么看 | 诊股");
			((TextView) convertView.findViewById(R.id.from)).setText("Powered by 图灵芒果诊股");
			((View) convertView.findViewById(R.id.im_bottom)).setBackgroundResource(R.drawable.img_mainshuiyin3);
		} else if (position == 3) {
			((View) convertView.findViewById(R.id.color)).setBackgroundResource(R.drawable.img_dian4);
			((TextView) convertView.findViewById(R.id.tv)).setText("我想要 | 研究");
			((TextView) convertView.findViewById(R.id.from)).setText("Powered by 图灵研究");
			convertView.findViewById(R.id.im_bottom).setVisibility(View.GONE);
			convertView.findViewById(R.id.im_bottom2).setVisibility(View.VISIBLE);
			((View) convertView.findViewById(R.id.im_bottom2)).setBackgroundResource(R.drawable.img_mainshuiyin2);
			view.findViewById(R.id.tv).setVisibility(View.GONE);
			view.findViewById(R.id.rl_bar).setVisibility(View.VISIBLE);
			if (gu.getName() != null) {
				((ProgressBar) view.findViewById(R.id.seekbar)).setProgress(gu.getPercent() * 100 / gu.getSection());
				((TextView) view.findViewById(R.id.p)).setText(gu.getPercent() * 100 / gu.getSection() + "%");
				((TextView) view.findViewById(R.id.person)).setText(gu.getTop() + "人");
				((TextView) view.findViewById(R.id.tmoney)).setText(gu.getPercent() + "元");
				((TextView) view.findViewById(R.id.day))
						.setText((System.currentTimeMillis() - gu.getTime() * 1000) / 86400000l + 1 + "天");
			}

		}

		return convertView;
	}

	public final class ViewHolder {
		TextView name, now, change, changep;
	}

	private int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
