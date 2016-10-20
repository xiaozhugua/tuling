//package com.abct.tljr.news.adapter;
//
//import java.util.ArrayList;
//
//import com.abct.tljr.R;
//import com.abct.tljr.data.Constant;
//import com.abct.tljr.model.Options;
//import com.abct.tljr.news.bean.News;
//import com.abct.tljr.ui.activity.StartActivity;
//import com.abct.tljr.utils.Util;
//import com.abct.tljr.zrclistview.ZrcListView;
//import com.nostra13.universalimageloader.core.imageaware.ImageAware;
//import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
//
//import android.app.Activity;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//public class NewsFeatureAdapter extends BaseAdapter {
//	private ArrayList<News> newsList;
//	Activity activity;
//	LayoutInflater inflater = null;
//	ZrcListView listView;
//	public String imageUri_moren = "drawable://" + R.drawable.img_morentupian;
//
//	/*
//	 * 图片地址
//	 */
//
//	public NewsFeatureAdapter(Activity activity, ArrayList<News> newsList, ZrcListView listView) {
//		this.activity = activity;
//		this.newsList = newsList;
//		this.listView = listView;
//		inflater = LayoutInflater.from(activity);
//	}
//
//	@Override
//	public int getCount()
//	{
//		return newsList == null ? 0 : newsList.size();
//	}
//
//	@Override
//	public News getItem(int position)
//	{
//		if (newsList != null && newsList.size() != 0)
//		{
//			if (position >= newsList.size())
//			{
//				return newsList.get(0);
//			}
//			return newsList.get(position);
//		}
//		return null;
//	}
//
//	@Override
//	public long getItemId(int position)
//	{
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent)
//	{
//		ViewHolder mHolder = null;
//
//		int type = getItemViewType(position);
//
//		// 获取position对应的数据
//		final News news = getItem(position);
//
//		if (convertView == null)
//		{
//
//			convertView = inflater.inflate(R.layout.tljr_item_news_feature, null);
//			mHolder = new ViewHolder();
//			mHolder.title = (TextView) convertView.findViewById(R.id.tv_title_news_feature);
//			mHolder.date = (TextView) convertView.findViewById(R.id.tv_time_news_feature);
//			mHolder.picture = (ImageView) convertView.findViewById(R.id.img_news_feature);
//
//			mHolder.position = position;
//			convertView.setTag(mHolder);
//			Util.startAni(position, convertView, listView);
//
//		} else
//		{
//			mHolder = (ViewHolder) convertView.getTag();
//
//		}
//		mHolder.title.setText(news.getTitle());
//		mHolder.date.setText(news.getTime());
//	 
//		
//		
//		
//		
//		
//		
//		
//		final ImageAware imageAware = new ImageViewAware(mHolder.picture, false);
//
//		if (!Constant.noPictureMode)
//		{
//
//			/*
//			 * 在线图片加载
//			 */
//			if (!TextUtils.isEmpty(news.getpUrl()))
//			{
//
//				// 防止重复加载闪烁错位
//				if (mHolder.picture.getTag() == null
//						|| !mHolder.picture.getTag().equals(news.getpUrl()))
//				{
//					StartActivity.imageLoader.displayImage(news.getpUrl(), imageAware, Options.getListOptions());
//					mHolder.picture.setTag(news.getpUrl());
//				}
//
//			} else
//			{
//
//				StartActivity.imageLoader.displayImage(imageUri_moren, imageAware);
//				mHolder.picture.setTag("moren");
//
//			}
//			;
//		} else
//		{
//
//			mHolder.picture.setTag(imageUri_moren);
//		}
//
//		return convertView;
//
//	}
//
//	public static class ViewHolder {
//		int position;
//		TextView title;
//		TextView date;
//		ImageView picture;
//	}
//
//}