package com.abct.tljr.hangqing.adapter;

import java.util.List;

import com.abct.tljr.R;
import com.abct.tljr.hangqing.model.JingWuModel;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.ui.activity.StartActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JingWuAdapter extends ArrayAdapter<JingWuModel> {

	private Context context;

	public JingWuAdapter(Context context, int resource,
			List<JingWuModel> objects) {
		super(context, resource, objects);
		this.context = context;
	}
	//hhjgkgnajkngan

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		final JingWuModel jingwu = getItem(position);
		ViewHolder viewHolder = null;
		viewHolder = new ViewHolder();
		if (jingwu.getStatus() == 1) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.tljr_rankview_date, null);
			viewHolder.timeormoney = (TextView) view
					.findViewById(R.id.rankview_time);
			viewHolder.actionornum = (TextView) view
					.findViewById(R.id.rankview_action);
			viewHolder.dataorpercent = (TextView) view
					.findViewById(R.id.rankview_day);
			viewHolder.timedateorname = (TextView) view
					.findViewById(R.id.rankview_date);
		} else {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.tljr_rankview_item_view, null);
			viewHolder.timeormoney = (TextView) view
					.findViewById(R.id.rankview_now);
			viewHolder.actionornum = (TextView) view
					.findViewById(R.id.rankview_num);
			viewHolder.dataorpercent = (TextView) view
					.findViewById(R.id.rankview_percent);
			viewHolder.timedateorname = (TextView) view
					.findViewById(R.id.rankview_name);
			viewHolder.code=(TextView)view.findViewById(R.id.rankview_code);
			viewHolder.sign = (ImageView)view.findViewById(R.id.rankview_img);
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(context,OneGuActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("code",jingwu.getCode());
					bundle.putString("market",jingwu.getMarket());
					bundle.putString("name", jingwu.getTimedateorname());
					bundle.putString("key", jingwu.getMarket()+jingwu.getCode());
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
			
			viewHolder.code.setText(jingwu.getCode());
		}

		viewHolder.timeormoney.setText(jingwu.getTimeormoney());
		viewHolder.actionornum.setText(jingwu.getActionornum());
		if (jingwu.getStatus() == 1) {
			viewHolder.dataorpercent.setText(jingwu.getDataorpercent());
		} else {
			viewHolder.dataorpercent.setText(jingwu.getDataorpercent() + "%");
		}
		viewHolder.timedateorname.setText(jingwu.getTimedateorname());
		
		if (!(jingwu.getStatus() == 1)) {
			StartActivity.imageLoader.displayImage(jingwu.getSign(),viewHolder.sign, StartActivity.options);
		}

		float num = 0;
		if (!(jingwu.getActionornum().equals("交易中")
				|| jingwu.getActionornum().equals("将开市") 
				|| jingwu.getActionornum().equals("已闭市")
				||jingwu.getActionornum().equals("停市中"))){
			if(!jingwu.getActionornum().equals("--")){
				num = Float.valueOf(jingwu.getActionornum());
			}
			if (num > 0) {
//				viewHolder.dataorpercent.setTextColor(getContext()
//						.getResources().getColor(R.color.red));
//				((RelativeLayout) view.findViewById(R.id.rankview_header))
//						.setBackgroundColor(getContext().getResources()
//								.getColor(R.color.red));
				((RelativeLayout) view.findViewById(R.id.tljr_rankview_item)).setBackgroundResource(R.drawable.img_tuijian02);
			} else {
//				viewHolder.dataorpercent.setTextColor(getContext()
//						.getResources().getColor(R.color.tljr_c_green));
//				((RelativeLayout) view.findViewById(R.id.rankview_header))
//						.setBackgroundColor(getContext().getResources()
//								.getColor(R.color.tljr_c_green));
				  ((RelativeLayout) view.findViewById(R.id.tljr_rankview_item)).setBackgroundResource(R.drawable.img_tuijian01);
			}
		}

		return view;
	}

	class ViewHolder {
		TextView timeormoney;
		TextView actionornum;
		TextView dataorpercent;
		TextView timedateorname;
		TextView code;
		ImageView sign;
		int action;
	}

}