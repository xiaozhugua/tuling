package com.abct.tljr.hangqing.jingwu;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ViewFlipperAdapter {

	private List<View> listView=null;
	BaseAdapter adapter=null;
	
	public ViewFlipperAdapter(List<View> view){
		listView=view;
		adapter=new BaseAdapter()
		{
		   @Override
		   public int getCount() {
		       return listView.size();
		   }
		   //该方法返回的View代表了每个列表项
		   @Override
		   public View getView(int position, View convertView, ViewGroup parent) {
		       return listView.get(position);
		   }
		   @Override
		   public Object getItem(int position) {
			   return position;
		   }
		   @Override
		   public long getItemId(int position) {
			   return position;
		   }
		};
	}
	
	public BaseAdapter getAdapter(){
		return adapter;
	}
	
}
