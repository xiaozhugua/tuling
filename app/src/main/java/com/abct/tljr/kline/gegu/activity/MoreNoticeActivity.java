package com.abct.tljr.kline.gegu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.MySuperBaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.ui.activity.WebActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 更多页面的activity
 */
public class MoreNoticeActivity extends MySuperBaseActivity {

	private TextView headerName;
	// private LinearLayout container;
	private ListView listView;
	
	private   ArrayList<View>    moreNoticeList=new  ArrayList<View>();
	private LinearLayout noDataContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_notice);
		initRootView();

		initData();
	}

	/**
	 * 找出各个控件id号
	 */
	private void initRootView() {
		// TODO Auto-generated method stub
		headerName = (TextView) findViewById(R.id.activity_more_notice_header_name);
		findViewById(R.id.activity_more_notice_header_comeback).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MoreNoticeActivity.this.finish();
			}
		});
		// container=
		// (LinearLayout)findViewById(R.id.activity_more_notice_container);
		listView = (ListView) findViewById(R.id.activity_more_notice_lv);// listview

		noDataContainer=          (LinearLayout)findViewById(R.id.activity_more_notice_no_data_ll);


	}

	/**
	 * 从上一个页面传递过来的数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		String noticeJsonArrayString = bundle.getString("noticeJsonArray");
		String name = bundle.getString("name");
		if (noticeJsonArrayString == null) {
			showMessage("还没有数据哦···");
			noDataContainer.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			TextView   tv=new TextView(this);
			tv.setText("还没有数据");
			tv.setTextColor(Color.BLACK);
			noDataContainer.addView(tv);
			return;
		} else {

			noDataContainer.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			try {
				headerName.setText(name);
				parserJsonArray(new JSONArray(noticeJsonArrayString), name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 返回公司公告的条目
	 */
	public View getOneXw(final String title, final String time, final String name, final String url) {
		View v = View.inflate(this, R.layout.tljr_item_syxw2, null);
		((TextView) v.findViewById(R.id.tljr_txt_syxw)).setText(title);
		// TextView comefrom = (TextView)
		// v.findViewById(R.id.tljr_txt_syxw_laiyuan);// 新闻来源
		if (time != null) {
			v.findViewById(R.id.tljr_txt_syxw_time).setVisibility(View.VISIBLE);
			((TextView) v.findViewById(R.id.tljr_txt_syxw_time)).setText(time);
		}
		v.setPadding(0, 10, 0, 10);
		if (!url.equals("")) {
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MoreNoticeActivity.this, WebActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("url", url);
					bundle.putString("name", name);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
		}
		return v;
	}

	private void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private void parserJsonArray(JSONArray jsonArray, String name) {
		// TODO Auto-generated method stub
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject obj = jsonArray.optJSONObject(i);
		     View   v=       	getOneXw(obj.optString("title"), obj.optString("datetime"), name, obj.optString("url"));
			//container.addView();
		     moreNoticeList.add(v);
			
		}
		listView.setAdapter(new   MyAdapter(moreNoticeList));
		

	}
	
	private  class   MyAdapter  extends    BaseAdapter{
		private   ArrayList<View>   list;
		
		
		@SuppressWarnings("unused")
		public MyAdapter(ArrayList<View>   list) {
			// TODO Auto-generated constructor stub
			this.list=list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (list!=null) {
				return  list.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (list!=null) {
				return   list.get(position);
			}
			return null;
		}
		
	}

}
