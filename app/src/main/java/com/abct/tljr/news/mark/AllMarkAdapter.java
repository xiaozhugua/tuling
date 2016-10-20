package com.abct.tljr.news.mark;

import java.util.ArrayList;

import com.abct.tljr.R;
import com.abct.tljr.news.NewsActivity;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.fragment.NewsMarkFragment;
import com.abct.tljr.ui.activity.StartActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AllMarkAdapter extends BaseAdapter {
	/**
	 *  所有订阅界面
	 */
	Activity activity;
	public ArrayList<News[]> newsList;
	ListView listView;
	String nowTypeName;

	public AllMarkAdapter(Activity activity, ListView listView, ArrayList<News[]> newsList, String nowTypeName) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.newsList = newsList;
		this.listView = listView;
		this.nowTypeName = nowTypeName;
		// addTopPinned();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder mHolder;
		if (view == null) {
			view = View.inflate(activity, R.layout.tljr_item_news_hqss_mark, null);
			mHolder = new ViewHolder();

			mHolder.title = (TextView) view.findViewById(R.id.title);
			mHolder.title1 = (TextView) view.findViewById(R.id.title1);
			mHolder.title2 = (TextView) view.findViewById(R.id.title2);

			mHolder.date = (TextView) view.findViewById(R.id.date);
			mHolder.date1 = (TextView) view.findViewById(R.id.date1);
			mHolder.date2 = (TextView) view.findViewById(R.id.date2);

			mHolder.author = (TextView) view.findViewById(R.id.mark_author);
			mHolder.arrow = (ImageView) view.findViewById(R.id.arrow);
			mHolder.avatar = (ImageView) view.findViewById(R.id.mark_avatar);
			mHolder.digest = (TextView) view.findViewById(R.id.digest);

			mHolder.ly_author = (RelativeLayout) view.findViewById(R.id.ly_author);
			mHolder.ly_news = (RelativeLayout) view.findViewById(R.id.ly_news);
			mHolder.ly_news1 = (RelativeLayout) view.findViewById(R.id.ly_news1);
			mHolder.ly_news2 = (RelativeLayout) view.findViewById(R.id.ly_news2);

			view.setTag(mHolder);

		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		final News news = newsList.get(position)[0];
		mHolder.title.setText(Html.fromHtml(news.getTitle()));
		mHolder.date.setText(news.getSimple_time());

		mHolder.digest.setText(Html.fromHtml(news.getDigest()));
		mHolder.author.setText(news.getAuthor());

		News news1 = newsList.get(position)[1];
		mHolder.title1.setText(Html.fromHtml(news1.getTitle()));
		mHolder.date1.setText(news1.getSimple_time());

		News news2 = newsList.get(position)[2];
		mHolder.title2.setText(Html.fromHtml(news2.getTitle()));
		mHolder.date2.setText(news2.getSimple_time());

		StartActivity.options = getListOptions();
		StartActivity.imageLoader.displayImage(news.getAuthorAvatar(), mHolder.avatar, getListOptions());

		mHolder.arrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goOneMarkActivity(news);
			}
		});

		mHolder.ly_author.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goOneMarkActivity(news);
			}

		});

		mHolder.ly_news.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goNewsActivity(position, 0);
			}
		});

		mHolder.ly_news1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goNewsActivity(position, 1);
			}
		});

		mHolder.ly_news2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goNewsActivity(position, 2);
			}
		});

		return view;
	}

	private void goOneMarkActivity(final News news) {
		OneMark one = new OneMark();
		one.setId(news.getId());
		one.setNumber(news.getWxh());
		one.setAvatar(news.getAuthorAvatar());
		one.setInfo("");
		one.setName(news.getAuthor());

		OneMarkActivity.mark = one;
		activity.startActivity(new Intent(activity, OneMarkActivity.class));
	}

	private void goNewsActivity(final int position, int p) {
		Intent intent = new Intent(activity, NewsActivity.class);

		Bundle bundle = new Bundle();
		bundle.putString("wxh", "");
		bundle.putInt("position", p);
		bundle.putString("nowTypeName", nowTypeName);
		NewsMarkFragment.newsList = new ArrayList<News>();

		for (int i = 0; i < newsList.get(position).length; i++) {
			NewsMarkFragment.newsList.add(newsList.get(position)[i]);
		}

		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

	public static DisplayImageOptions getListOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// // 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.img_morentupian)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.img_morentupian)
				// 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.img_morentupian).cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)

				// 设置图片下载前的延迟
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// 。preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 淡入

				.build();
		return options;
	}

	static class ViewHolder {

		TextView title, title1, title2;
		TextView date, date1, date2;
		TextView digest;
		ImageView avatar;
		TextView author;
		ImageView arrow;

		RelativeLayout ly_author, ly_news, ly_news1, ly_news2;

	}

}
