package com.abct.tljr.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ABHbAdapter  extends   BaseAdapter {

	private  JSONArray   array;
	private  Context  context;
	public ABHbAdapter(JSONArray array, Context context) {
		// TODO Auto-generated constructor stub
		this.array=array;
		this.context=context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (array!=null) {
			return  array.length();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (array!=null) {
			try {
				return   array.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder   holder=null;
		if (convertView==null) {
			convertView=View.inflate(context, R.layout.item_abh_b, null);
			holder=new  ViewHolder();
			holder.fundb_id=(TextView) convertView.findViewById(R.id.item_abh_b_fundb_id);
			holder.fundb_name=(TextView) convertView.findViewById(R.id.item_abh_b_fundb_name);
			holder.fundb_current_price=(TextView) convertView.findViewById(R.id.item_abh_b_fundb_current_price);
			holder.b_est_val=(TextView) convertView.findViewById(R.id.item_abh_b_b_est_val);
			holder.fundb_base_est_dis_rt=(TextView) convertView.findViewById(R.id.item_abh_b_fundb_base_est_dis_rt);
			holder.fundb_price_leverage_rt=(TextView) convertView.findViewById(R.id.item_abh_b_fundb_price_leverage_rt);
			
			
			convertView.setTag(holder);
			
			
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		try {
			
			JSONObject   json=           (JSONObject) array.get(position);
			holder.fundb_id.setText(json.optString("fundb_id"));
			holder.fundb_name.setText(json.optString("fundb_name"));
			holder.fundb_current_price.setText(json.optString("fundb_current_price"));
			holder.b_est_val.setText(json.optString("b_est_val"));
			holder.fundb_base_est_dis_rt.setText(json.optString("fundb_base_est_dis_rt"));
			holder.fundb_price_leverage_rt.setText(json.optString("fundb_price_leverage_rt"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return convertView;
	}

	
	
	class   ViewHolder {
		/**
		 *    "b_est_val": "1.123",
            "fundb_base_est_dis_rt": "1.441%",
            "fundb_current_price": "1.040",
            "fundb_discount_rt": "-7.42%",
            "fundb_id": "150048",
            "fundb_index_name": "主动基金",
            "fundb_name": "消费B",
            "fundb_price_leverage_rt": "1.326",
            "fundb_upper_recalc_rt": "-"*/
		
		
		TextView   b_est_val;
		TextView   fundb_base_est_dis_rt;
		TextView   fundb_current_price;
		TextView   fundb_discount_rt;
		TextView   fundb_id;
		TextView   fundb_index_name;
		TextView   fundb_name;
		TextView   fundb_price_leverage_rt;
		TextView   fundb_upper_recalc_rt;
		
		
	}
	
	
	
	
	
	
	
}
