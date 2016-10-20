package com.abct.tljr.hangqing.adapter;

import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.database.ZiXuanGu;
import com.abct.tljr.hangqing.model.DeleteGuModel;
import com.abct.tljr.hangqing.zixuan.DeleteZiXuanGu;
import com.abct.tljr.hangqing.zixuan.tljr_zixuan_gu_recyclerview;
import com.qh.common.util.LogUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DeleteZiXuanGuAdapter extends ArrayAdapter<DeleteGuModel>{
	private int RecourceId;
	public DeleteZiXuanGuAdapter(Context context, int resource,List<DeleteGuModel> objects) {
		super(context, resource, objects);
		this.RecourceId=resource;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=null;
		ViewHolder mViewHolder=null;
		final DeleteGuModel model=getItem(position);
		if(convertView==null){
			mViewHolder=new ViewHolder();
			view=LayoutInflater.from(getContext()).inflate(this.RecourceId,parent,false);
			mViewHolder.name=(TextView)view.findViewById(R.id.gudelete_name);
			mViewHolder.code=(TextView)view.findViewById(R.id.gudelete_code);
			mViewHolder.zhidin=(ImageView)view.findViewById(R.id.gudelete_zhiding);
			mViewHolder.checkBox=(CheckBox)view.findViewById(R.id.tljr_hq_checkbox);
			view.setTag(mViewHolder);
		}else{
			view=convertView;
			mViewHolder=(ViewHolder)view.getTag();
		}		
		
		mViewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
     	   @Override
     	   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
     		   LogUtil.e("check",isChecked+"");
     		   if(isChecked){
     			   LogUtil.e("check1","action1");
     			   model.setAction(1);
     		   }else{
     			   LogUtil.e("check0","action0");
     			   model.setAction(0);
     		   }
     	   }
		});
		
		if(model.getAction()==0){
			LogUtil.e("checkaction","checkaction1");
			mViewHolder.checkBox.setChecked(false);
		}else{
			LogUtil.e("checkaction","checkaction0");
			mViewHolder.checkBox.setChecked(true);
		}
		
		mViewHolder.name.setText(model.getName());
		mViewHolder.code.setText(model.getCode());
		
		final int from=position;
		mViewHolder.zhidin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteZiXuanGu.updateStatus=true;
				DeleteGuModel temp=model;
				DeleteZiXuanGu.adapter.remove(model);
				DeleteZiXuanGu.adapter.insert(temp,0);
				ZiXuanGu gu=tljr_zixuan_gu_recyclerview.listZiXuanGu.get(from);
				tljr_zixuan_gu_recyclerview.listZiXuanGu.remove(from);
				tljr_zixuan_gu_recyclerview.listZiXuanGu.add(0,gu);
				tljr_zixuan_gu_recyclerview.adapter.notifyDataSetChanged();
			}
		});
		
		return view;
	}
	
	
	class ViewHolder{
		public TextView name;
		public TextView code;
		public ImageView zhidin;
		public CheckBox checkBox;
	}
	
}
