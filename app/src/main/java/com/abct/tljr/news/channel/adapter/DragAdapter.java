package com.abct.tljr.news.channel.adapter;

import java.util.List;

import com.abct.tljr.R;
import com.abct.tljr.news.channel.ChannelActivity;
import com.abct.tljr.news.channel.bean.ChannelItem;
import com.abct.tljr.news.channel.view.DragGrid;
import com.abct.tljr.utils.Util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DragAdapter extends BaseAdapter {
	/** TAG*/
	private final static String TAG = "DragAdapter";
	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** 控制的postion */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 是否可见 */
	boolean isVisible = true;
	/** 可以拖动的列表（即用户选择的频道列表） */
	//public List<ChannelItem> ChannelActivity.userChannelList;
	/** TextView 频道内容 */
	private TextView item_text;
	
	private ImageView item_edit ;
	
	/** 要删除的position */
	public int remove_position = -1;
	DragGrid userGridView;
	public DragAdapter(Context context ,DragGrid userGridView) {
		this.context = context;
		this.userGridView =userGridView ;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ChannelActivity.userChannelList == null ? 0 : ChannelActivity.userChannelList.size();
	}

	@Override
	public ChannelItem getItem(int position) {
		// TODO Auto-generated method stub
		if (ChannelActivity.userChannelList != null && ChannelActivity.userChannelList.size() != 0) {
			return ChannelActivity.userChannelList.get(position);
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
		View view = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
		item_text = (TextView) view.findViewById(R.id.text_item);
		item_edit= (ImageView) view.findViewById(R.id.icon_edit);
		
		
		ChannelItem channel = getItem(position);
		item_text.setText(channel.getName());
		
		
		item_edit.setVisibility(userGridView.isEditingMode()?View.VISIBLE:View.GONE);
		
		 
		
		
		if(item_text.getText().length()>=6){
		//	item_text.setTextSize(Util.get_SP_FontSize(12));
			item_text.setTextSize(12);
		}
		if ((position == 0) || (position == 1)){
//			item_text.setTextColor(context.getResources().getColor(R.color.black));
		//	item_text.setEnabled(false);
		}
		if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + ChannelActivity.userChannelList.size())) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
		}
		if(remove_position == position){
			item_text.setText("");
		}
		return view;
	}

	/** 添加频道列表 */
	public void addItem(ChannelItem channel) {
		ChannelActivity.userChannelList.add(channel);
		notifyDataSetChanged();
	}

	/** 拖动变更频道排序 */
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		ChannelItem dragItem = getItem(dragPostion);
		if (dragPostion < dropPostion) {
			ChannelActivity.userChannelList.add(dropPostion + 1, dragItem);
			ChannelActivity.userChannelList.remove(dragPostion);
		} else {
			ChannelActivity.userChannelList.add(dropPostion, dragItem);
			ChannelActivity.userChannelList.remove(dragPostion + 1);
		}
		isChanged = true;
		notifyDataSetChanged();
	}
	
	/** 获取频道列表 */
	public List<ChannelItem> getChannnelLst() {
		return ChannelActivity.userChannelList;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** 删除频道列表 */
	public void remove() {
		ChannelActivity.userChannelList.remove(remove_position);
		remove_position = -1;
		notifyDataSetChanged();
	}
	
	 
	
	/** 获取是否可见 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}
	/** 显示放下的ITEM */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}
 
}
