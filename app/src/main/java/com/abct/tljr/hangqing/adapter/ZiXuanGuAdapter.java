package com.abct.tljr.hangqing.adapter;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.database.ZiXuanGu;
import com.abct.tljr.kline.OneGuActivity;
import com.qh.common.util.ColorUtil;


public class ZiXuanGuAdapter extends RecyclerView.Adapter<ZiXuanViewHolder> {

	private int ResourceId;
	private Context context;
	private List<ZiXuanGu> list;

	public ZiXuanGuAdapter(Context context, int resource, List<ZiXuanGu> objects) {
		this.context = context;
		this.ResourceId = resource;
		this.list = objects;
	}

	@Override
	public ZiXuanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ZiXuanViewHolder(LayoutInflater.from(context).inflate(ResourceId, parent, false));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(ZiXuanViewHolder holder, int position) {
		final ZiXuanGu mZiXuanGu = list.get(position);
		if(mZiXuanGu.getName()!=null){
			holder.name.setText(mZiXuanGu.getName());
		}
		holder.code.setText(mZiXuanGu.getCode());
		
		if(mZiXuanGu.getPrice()==0.0f){
			holder.price.setText("--");
		}else{
			holder.price.setText(mZiXuanGu.getPrice() + "");
		}
		
		holder.change.setTextColor(ColorUtil.white);
		if(mZiXuanGu.getStatus().equals("--")){
			holder.change.setText("--");
		}else if(mZiXuanGu.getStatus().equals("停牌")||mZiXuanGu.getStatus().equals("退市")){
			holder.change.setText(mZiXuanGu.getStatus());
			holder.foritem_item.setBackgroundColor(context.getResources().getColor(R.color.tljr_gray));
		}else{
			if (mZiXuanGu.getChange() > 0) {
				holder.change.setText("+" + mZiXuanGu.getChange() + "%");
				holder.foritem_item.setBackgroundColor(context.getResources().getColor(R.color.tljr_hq_zx_up));
			} else if(mZiXuanGu.getChange()<0) {
				holder.change.setText(mZiXuanGu.getChange() + "%");
				holder.foritem_item.setBackgroundColor(context.getResources().getColor(R.color.tljr_hq_zx_down));
			}else{
				holder.change.setText(mZiXuanGu.getChange()+"%");
				holder.foritem_item.setBackgroundColor(context.getResources().getColor(R.color.tljr_gray));
			}
		}
		
	    holder.mView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,OneGuActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("code",mZiXuanGu.getCode());
				bundle.putString("market", mZiXuanGu.getMarket().toLowerCase());
				bundle.putString("name",mZiXuanGu.getName());
				bundle.putString("key", mZiXuanGu.getMarket().toLowerCase()+mZiXuanGu.getCode());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
}

class ZiXuanViewHolder extends RecyclerView.ViewHolder {
	TextView name;
	TextView code;
	TextView price;
	TextView change;
	RelativeLayout foritem_item;
	View mView;
	public ZiXuanViewHolder(View view){
		super(view);
		name = (TextView) view.findViewById(R.id.zixuangu_name);
		code = (TextView) view.findViewById(R.id.zixuangu_code);
		price = (TextView) view.findViewById(R.id.zixuangu_price);
		change = (TextView) view.findViewById(R.id.zixuangu_change);
		foritem_item=(RelativeLayout)view.findViewById(R.id.zixuangu_item);
		mView=view;
	}
}


