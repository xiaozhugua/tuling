package com.abct.tljr.chart;

import java.util.ArrayList;

import com.abct.tljr.R;
import com.abct.tljr.utils.Util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RealTimeAdapter extends BaseAdapter{
	Context context;
	ArrayList<RealBean> array;
	
	public RealTimeAdapter(Context context,ArrayList<RealBean> array){
		this.context = context;
		this.array = array;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return array.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder  holder = null;
		if(v == null){
			holder = new ViewHolder();
			v = View.inflate(context, R.layout.tljr_chart_realtime_item, null);
			holder.time = (TextView)v.findViewById(R.id.time);
			holder.content = (TextView)v.findViewById(R.id.content);
			holder.name = (TextView) v.findViewById(R.id.name);
			v.setTag(holder);
		}else{
			holder = (ViewHolder) v.getTag();
		}
//		Log.d("RealTimeView", "adapter  :"+array.get(position).name+"  size :"+array.size());
//		holder.time.setText(Util.getDateMDhhmm(array.get(position).time));
		holder.time.setText(Util.getDateOnlyDat(System.currentTimeMillis()).equals(Util.getDateOnlyDat(array.get(position).time))
	    		   ?Util.getDateOnlyHour(array.get(position).time) : Util.getDateNoss(array.get(position).time));
		holder.content.setText("    "+array.get(position).msg);
		holder.name.setText(array.get(position).name);
		if(position == 0){
			v.findViewById(R.id.top1).setVisibility(View.VISIBLE);
			v.findViewById(R.id.top2).setVisibility(View.VISIBLE);
		}else{
			v.findViewById(R.id.top1).setVisibility(View.GONE);
			v.findViewById(R.id.top2).setVisibility(View.GONE);
		}
		
		return v;
	}
	
	public final class ViewHolder{
		TextView time;
		TextView content;
		TextView name;
	}
	
	public static class RealBean{
		long time;
		String name;
		String msg;
		String uid;
		String id;
	}
}
