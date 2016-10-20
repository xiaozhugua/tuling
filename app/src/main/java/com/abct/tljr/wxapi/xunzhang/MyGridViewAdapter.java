package com.abct.tljr.wxapi.xunzhang;

import java.io.File;
import java.util.List;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.qh.common.util.FileUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridViewAdapter extends BaseAdapter {

	private Context context;
	private List<XunzhangEntity> list;
	
	public MyGridViewAdapter(Context context,List<XunzhangEntity> list ) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		XunzhangEntity entity = list.get(position);
		VieHolder holder =null;
		if(convertView==null){
			holder = new VieHolder();
			convertView = View.inflate(context, R.layout.item_xunzhang_gridview, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.detail = (TextView) convertView.findViewById(R.id.detail);
			convertView.setTag(holder);
		}else holder = (VieHolder) convertView.getTag();
		String url = entity.getIconUrl();
		if(!new File("/sdcard/tljr/" + url.substring(url.lastIndexOf("/"), url.length())).exists()){
			MyApplication.imageLoader.displayImage(entity.getIconUrl(), holder.iv);
		}
		Util.setImageOnlyDown(url, holder.iv);
		holder.name.setText(entity.getName());
		holder.detail.setText(entity.getDetail());
		return convertView;
	}
	class VieHolder{
		ImageView iv;
		TextView name,detail;
	}
}
