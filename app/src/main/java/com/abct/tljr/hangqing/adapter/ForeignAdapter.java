package com.abct.tljr.hangqing.adapter;

import java.util.List;

import com.abct.tljr.R;
import com.abct.tljr.hangqing.model.Foreign;
import com.abct.tljr.kline.OneGuActivity;
import com.qh.common.util.ColorUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ForeignAdapter extends RecyclerView.Adapter<ViewHolder> {

	private int ResourceId;
	private Context context;
	private List<Foreign> list;

	public ForeignAdapter(Context context, int resource, List<Foreign> objects) {
		this.context = context;
		this.ResourceId = resource;
		this.list = objects;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(context).inflate(ResourceId, parent, false));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Foreign mForign = list.get(position);
		holder.name.setText(mForign.getName());
		holder.code.setText(mForign.getCode());
		
		if(mForign.getPrice()==0.0f){
			holder.price.setText("--");
		}else{
			holder.price.setText(mForign.getPrice() + "");
		}
		
		holder.change.setTextColor(ColorUtil.white);
		
		if(mForign.getStatus().equals("--")){
			holder.change.setText("--");
		}else if(mForign.getStatus().equals("停牌")||mForign.getStatus().equals("退市")){
			holder.change.setText(mForign.getStatus());
			holder.foritem_item.setBackgroundColor(context.getResources().getColor(R.color.tljr_gray));
		}else{
			if (mForign.getChange() > 0) {
				holder.change.setText("+" + mForign.getChange() + "%");
				holder.foritem_item.setBackgroundColor(context.getResources().getColor(R.color.tljr_hq_zx_up));
			} else if(mForign.getChange()<=0) {
				holder.change.setText(mForign.getChange() + "%");
				holder.foritem_item.setBackgroundColor(context.getResources().getColor(R.color.tljr_hq_zx_down));
			}else {
				holder.change.setText(mForign.getChange() + "%");
				holder.foritem_item.setBackgroundColor(context.getResources().getColor(R.color.tljr_gray));
			}
		}
		
	    holder.mView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,OneGuActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("code",mForign.getCode());
				bundle.putString("market", mForign.getMarket().toLowerCase());
				bundle.putString("name", mForign.getName());
				bundle.putString("key", mForign.getMarket().toLowerCase()+mForign.getCode());
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

class ViewHolder extends RecyclerView.ViewHolder {
	TextView name;
	TextView code;
	TextView price;
	TextView change;
	RelativeLayout foritem_item;
	View mView;
	public ViewHolder(View view){
		super(view);
		name = (TextView) view.findViewById(R.id.foreign_name);
		code = (TextView) view.findViewById(R.id.foreign_code);
		price = (TextView) view.findViewById(R.id.foreign_price);
		change = (TextView) view.findViewById(R.id.foreign_change);
		foritem_item=(RelativeLayout)view.findViewById(R.id.foritem_item);
		mView=view;
	}
	
}
