package com.abct.tljr.ui.activity.tools;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class MySimpleCursorAdapter extends SimpleAdapter {
	public MySimpleCursorAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// listview每次得到一个item，都要view去绘制，通过getView方法得到view
		// position为item的序号

		View view = null;
		try {
			if (convertView != null) {
				view = convertView;
				// 使用缓存的view,节约内存
				// 当listview的item过多时，拖动会遮住一部分item，被遮住的item的view就是convertView保存着。
				// 当滚动条回到之前被遮住的item时，直接使用convertView，而不必再去new view()

			} else {
				view = super.getView(position, convertView, parent);

			}
			int[] colors = { Color.WHITE, Color.rgb(240, 240, 240) };// RGB颜色
 
			view.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同
//			if (position == 0) {
//				((TextView) view).setGravity(Gravity.CENTER);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.getView(position, view, parent);
	}
}
