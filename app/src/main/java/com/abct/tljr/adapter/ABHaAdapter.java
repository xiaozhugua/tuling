package com.abct.tljr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.abct.tljr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * dg
 */
public class ABHaAdapter extends BaseAdapter {

	private JSONArray array;
	private Context context;

	public ABHaAdapter(JSONArray array, Context context) {
		this.array = array;
		this.context = context;
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
				// dddTODO Auto-generated catch block
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
			convertView=View.inflate(context, R.layout.item_abh_a, null);
			holder=new  ViewHolder();
			holder.funda_id=(TextView) convertView.findViewById(R.id.item_abh_a_code);
			holder.funda_name=(TextView) convertView.findViewById(R.id.item_abh_a_name);
			holder.funda_current_price=(TextView) convertView.findViewById(R.id.item_abh_a_nowprice);
			holder.funda_coupon_next=(TextView) convertView.findViewById(R.id.item_abh_a_next_rate);//下期利率
			holder.funda_base_est_dis_rt=(TextView) convertView.findViewById(R.id.item_abh_a_whole_premium);//整体溢价率
			
			holder.funda_amount_increase_rt=(TextView) convertView.findViewById(R.id.item_abh_a_share_increase);//份额增长
			
			holder.funda_profit_rt_next=(TextView) convertView.findViewById(R.id.item_abh_a_next_profit);
			
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		try {
			JSONObject    json=         (JSONObject) array.get(position);
			holder.funda_id.setText(json.optString("funda_id"));
			holder.funda_name.setText(json.optString("funda_name"));
			holder.funda_current_price.setText(json.optString("funda_current_price"));
			holder.funda_profit_rt_next.setText(json.optString("funda_profit_rt_next"));
			holder.funda_amount_increase_rt.setText(json.optString("funda_amount_increase_rt"));
			holder.funda_base_est_dis_rt.setText(json.optString("funda_base_est_dis_rt"));
			holder.funda_coupon_next.setText(json.optString("funda_coupon_next"));
			
		} catch (Exception e) {
			
			
		}
		
		
		return convertView;
	}
	
	class   ViewHolder{
		/**
		 *   "funda_amount_increase_rt": "0.00%",
            "funda_base_est_dis_rt": "-0.44%",
            "funda_coupon_next": "5.50",
            "funda_current_price": "1.054",
            "funda_id": "150188",
            "funda_name": "转债优先",
            "funda_profit_rt_next": "0.169%"*/
		
		TextView   funda_amount_increase_rt;
		TextView   funda_base_est_dis_rt;
		TextView   funda_coupon_next;
		TextView   funda_current_price;
		TextView   funda_id;
		TextView   funda_name;
		TextView   funda_profit_rt_next;
		
	}

}
