package com.abct.tljr.hangqing.adapter;

import java.util.List;

import com.abct.tljr.R;
import com.abct.tljr.hangqing.model.DeleteGuModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DeleteGuAdapter extends ArrayAdapter<DeleteGuModel>{

	private int RecourceId;
	
	public DeleteGuAdapter(Context context, int resource,List<DeleteGuModel> objects) {
		super(context, resource, objects);
		this.RecourceId=resource;
	}

	@SuppressWarnings("deprecation")
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
			mViewHolder.price=(TextView)view.findViewById(R.id.gudelete_price);
			mViewHolder.change=(TextView)view.findViewById(R.id.gudelete_change);
			mViewHolder.checkBox=(CheckBox)view.findViewById(R.id.tljr_hq_checkbox);
			view.setTag(mViewHolder);
		}else{
			view=convertView;
			mViewHolder=(ViewHolder)view.getTag();
		}		
		
		mViewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
     	   @Override
     	   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
     		   if(isChecked){
     			   model.setAction(1);
     		   }else{
     			   model.setAction(0);
     		   }
     	   }
		});
		
		if(model.getAction()==0){
			mViewHolder.checkBox.setChecked(false);
		}else{
			mViewHolder.checkBox.setChecked(true);
		}
		
		if(model.getP_changes()>0.0f){
			mViewHolder.change.setBackgroundColor(getContext().getResources().getColor(R.color.tljr_hq_zx_up));
		}else{
			mViewHolder.change.setBackgroundColor(getContext().getResources().getColor(R.color.tljr_hq_zx_down));
		}
		
		mViewHolder.name.setText(model.getName());
		mViewHolder.code.setText(model.getCode());
		mViewHolder.price.setText(model.getPrice());
		mViewHolder.change.setText(model.getChange());
		
		return view;
	}
	
	class ViewHolder{
		public TextView name;
		public TextView code;
		public TextView price;
		public TextView change;
		public CheckBox checkBox;
	}
	
}
