package com.abct.tljr.news.channel.adapter;

import java.util.List;

import com.abct.tljr.R;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.news.channel.ChannelActivity;
import com.abct.tljr.news.channel.bean.ChannelItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelListAdapter extends BaseAdapter{

	private Context context;
	public List<ChannelItem> channelList;
	 LayoutInflater inflater = null; 
	
	public ChannelListAdapter(Context context,List<ChannelItem> channelList) {
		this.context = context;
		this.channelList = channelList;
		inflater = LayoutInflater.from(context);
	}
	
	
	  public void setChannelList(List<ChannelItem> channelList){
			this.channelList = channelList;
			notifyDataSetChanged();
	  }
	
	@Override
	public int getCount() {
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public ChannelItem getItem(int position) {
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder mHolder=null;
		if(convertView == null){
			   convertView = inflater.inflate(R.layout.item_hqss_channel_select, null);
			   mHolder = new ViewHolder();
			   mHolder.channelName =(TextView)convertView.findViewById(R.id.hqss_channel_name);
			   mHolder.channelAdd =(ImageView)convertView.findViewById(R.id.hqss_channel_add);
			   convertView.setTag(mHolder);
        }else{
            mHolder = (ViewHolder) convertView.getTag();
        }
		
		ChannelItem channel = getItem(position);
		
		mHolder.channelName.setText(channel.getName());
		
		mHolder.channelAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HuanQiuShiShi.channel_change = true;
				ChannelItem channel = getItem(position);
				  ChannelActivity.userAdapter.addItem(channel);
                  // userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
				//  ChannelActivity.otherAdapter.channelList.remove(channel);
				  ChannelActivity.otherChannelList.remove(channel);
				  channelList.remove(channel);
                    notifyDataSetChanged();
			}
		});
		
		return convertView;
	}
	
	
	static class ViewHolder{
		  TextView channelName ;
		  ImageView channelAdd ;
	}

}
