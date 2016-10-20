package com.abct.tljr.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.R;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 首页的AH数据页面的适配器
 */
public class AHDataAdapter extends BaseAdapter {

	//private int[] colors = { Color.WHITE, Color.rgb(240, 240, 240) };// RGB颜色
	private int[] colors = { Color.WHITE, Color.BLUE};// RGB颜色
	private JSONArray mArray;
	private Activity mActivity;

	public AHDataAdapter(JSONArray array, Activity activity) {
		this.mActivity = activity;
		this.mArray = array;
	}

	@Override
	public int getCount() {
		return mArray.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		try {
			return mArray.get(position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		try {

			ListView listView = (ListView) parent;
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.ah_view_layout, null);
				holder = new ViewHolder();
				holder.a_code = (TextView) convertView.findViewById(R.id.ah_a_code);
				holder.name = (TextView) convertView.findViewById(R.id.ah_name);
				holder.h_code = (TextView) convertView.findViewById(R.id.ah_h_code);
				holder.a_price = (TextView) convertView.findViewById(R.id.ah_a_price);
				holder.a_inc = (TextView) convertView.findViewById(R.id.ah_a_zdf);//A股涨跌幅
				holder.h_price = (TextView) convertView.findViewById(R.id.ah_h_price_gyuan);
				holder.h_price_rmb = (TextView) convertView.findViewById(R.id.ah_h_price_yuan);
				holder.h_inc = (TextView) convertView.findViewById(R.id.ah_h_zdf);
				holder.ha_ratio = (TextView) convertView.findViewById(R.id.ah_ratio);
				convertView.setTag(holder);// 绑定ViewHolder对象
			} else {
				holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象

			}
/**
 *    "a_code": "002672",
            "a_inc": "0.00%",
            "a_price": "0.00",
            "h_code": "00895",
            "h_inc": "-0.17%",
            "h_price": "11.480",
            "h_price_rmb": "9.67",
            "ha_ratio": "0.00",
            "name": "东江环保"*/
			JSONObject      json=        mArray.optJSONObject(position);
			holder.a_code.setText(json.optString("a_code"));
			holder.name.setText(json.optString("name"));
			holder.h_code.setText(json.optString("h_code"));
			holder.a_price.setText(json.optString("a_price"));
			holder.a_inc.setText(json.optString("a_inc"));
			holder.h_price.setText(json.optString("h_price"));
			holder.h_price_rmb.setText(json.optString("h_price_rmb"));
			holder.h_inc.setText(json.optString("h_inc"));
			holder.ha_ratio.setText(json.optString("ha_ratio"));
			
			convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同
			
			
			
			
		} catch (Exception e) {
			return convertView;
		}

		return convertView;
	}

	class ViewHolder {
		/**
		 * "a_code": "002672", "a_inc": "0.00%", "a_price": "0.00", "h_code":
		 * "00895", "h_inc": "-0.17%", "h_price": "11.480", "h_price_rmb":
		 * "9.67", "ha_ratio": "0.00", "name": "东江环保"
		 */

		TextView a_code;
		TextView a_inc;
		TextView a_price;
		TextView h_code;
		TextView h_inc;
		TextView h_price;
		TextView h_price_rmb;
		TextView ha_ratio;
		TextView name;

	}

}
