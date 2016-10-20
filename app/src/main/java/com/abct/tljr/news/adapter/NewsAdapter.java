package com.abct.tljr.news.adapter;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.bean.Tag;
import com.abct.tljr.news.fragment.PictureNewsFragment;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	private ArrayList<News> newsList;
	Activity activity;
	LayoutInflater inflater = null;
	ListView listView;
	public static HashMap<Integer, Tag> tagsMap = new HashMap<Integer, Tag>();

	PictureNewsFragment fragment;

	private int LIST_VIEW_TYPE = 2;
	private final String Tag = "NewsAdapter";
	/*
	 * 布局类型
	 */
	final int TYPE_TEXT = 0;
	final int TYPE_PICTURE = 1;
	final int TYPE_XINWENLIANBO = 2;
	/*
	 * 图片地址
	 */

	private String defaultPicture = "default";
	String imageUri_Local = "file:///sdcard/tljr/"; // 离线状态时加载图片使用下载到本地的地址

	public static String imageUri_moren = "drawable://" + R.drawable.img_morentupian;
	private String nowTypeName;
	private String currentDate = "";
	Date date;
	SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
	SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	Drawable bigPoint1, bigPoint2, smallPoint1, smallPoint2;
	
	  

	public NewsAdapter(PictureNewsFragment fragment, Activity activity, ArrayList<News> newsList, ListView listView, int layoutType,
			String defaultPicture, String nowTypeName) {
		this.fragment = fragment;
		this.activity = activity;
		this.newsList = newsList;
		this.listView = listView;
		this.defaultPicture = defaultPicture;
		this.LIST_VIEW_TYPE = layoutType;
		this.nowTypeName = nowTypeName;
		inflater = LayoutInflater.from(activity);

		date = new Date(System.currentTimeMillis());
		currentDate = format.format(date);
		// currentDate ="02-24-2016" ;
		imageUri_Local = "file:///" + ((File) activity.getApplicationContext()
				.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).getPath();
		bigPoint1 = activity.getResources().getDrawable(R.drawable.img_tishi1);
		smallPoint1 = activity.getResources().getDrawable(R.drawable.img_tishi2);
		bigPoint2 = activity.getResources().getDrawable(R.drawable.img_tishi3);
		smallPoint2 = activity.getResources().getDrawable(R.drawable.img_tishi4);
		 
	}

	public void setLayoutType(int layoutType){
		this.LIST_VIEW_TYPE =layoutType;
	}
	
	public void setList(ArrayList<News> list) {
		this.newsList = list;
	}

	@Override
	public int getCount() {
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public News getItem(int position) {
		if (newsList != null && newsList.size() != 0) {
			if (position >= newsList.size()) {
				return newsList.get(0);
			}
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getViewTypeCount() {

		return 3;
	};

	public int getItemViewType(int position) {

		if (LIST_VIEW_TYPE == 1) {
			return TYPE_TEXT;
		} else if (LIST_VIEW_TYPE == 2) {
			return TYPE_PICTURE;
		} else if (LIST_VIEW_TYPE == 4) {
			return TYPE_XINWENLIANBO;
		}
		return TYPE_PICTURE;
	};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = null;
		ViewHolder2 mHolder2 = null;
		ViewHolder3 mHolder3 = null;

		int type = getItemViewType(position);
		// 获取position对应的数据
		final News news = getItem(position);
		
		switch (type) {
		case TYPE_TEXT:
			if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
				convertView = inflater.inflate(R.layout.tljr_item_news_text, null);
				mHolder = new ViewHolder();
				mHolder.title = (TextView) convertView.findViewById(R.id.title);
				mHolder.source = (TextView) convertView.findViewById(R.id.source);
				mHolder.date = (TextView) convertView.findViewById(R.id.date);
				mHolder.time = (TextView) convertView.findViewById(R.id.time);
				mHolder.bigPoint = (ImageView) convertView.findViewById(R.id.bigpoint);
				mHolder.smallPoint = (ImageView) convertView.findViewById(R.id.smallpoint);
				mHolder.topLayout = (LinearLayout) convertView.findViewById(R.id.toplayout);
				convertView.setTag(mHolder);
				Util.startAni(position, convertView, listView);
			} else {
				if (convertView.getTag() instanceof ViewHolder) {
					mHolder = (ViewHolder) convertView.getTag();
				}
			}

			if (position == 0) {
				mHolder.topLayout.setVisibility(View.VISIBLE);
			} else {
				final News beforeNews = getItem(position - 1);
				if (beforeNews != null) {
					if (!news.getTextDate().equals(beforeNews.getTextDate())) {
						mHolder.topLayout.setVisibility(View.VISIBLE);
					} else {
						mHolder.topLayout.setVisibility(View.GONE);
					}
				}
			}

			if (currentDate.equals(news.getTextDate())) {
				mHolder.bigPoint.setImageDrawable(bigPoint1);
				;
				mHolder.smallPoint.setImageDrawable(smallPoint1);
			} else {
				mHolder.bigPoint.setImageDrawable(bigPoint2);
				;
				mHolder.smallPoint.setImageDrawable(smallPoint2);
			}

			date.setTime(news.getTime());

			mHolder.date.setText(format.format(date));
			mHolder.time.setText(DateUtil.getDateOnlyHour(news.getTime()));
			if (!Util.isEmptyAndSpace(news.getSource())) {
				mHolder.source.setText(news.getSource());
			} else {
				mHolder.source.setVisibility(View.GONE);
			}

			/*
			 * 文字新闻部分无标题或者无内容 -需要做处理
			 */
			String text = news.getTitle();
			;
			if (Util.isEmptyAndSpace(news.getTitle())) {
				text = news.getContent();
				if (Util.isEmptyAndSpace(news.getContent())) {
					text = news.getDigest();
				}	
			}
			if (nowTypeName.equals("我的收藏") && !Util.isEmptyAndSpace(news.getDigest()) && news.getDigest() != null && news.getDigest().length() > 6) {
				text = news.getDigest();
			}
			mHolder.title.setText(Html.fromHtml(text) + " ");

			return convertView;

		case TYPE_PICTURE:

			if (convertView == null || !(convertView.getTag() instanceof ViewHolder2)) {

				convertView = inflater.inflate(R.layout.tljr_item_news_imp, null);
				mHolder2 = new ViewHolder2();
				mHolder2.ly_hqss_tags = (LinearLayout) convertView.findViewById(R.id.ly_hqss_tags);
				mHolder2.imp_news_title = (TextView) convertView.findViewById(R.id.imp_hqss_news_title);
				mHolder2.imp_news_date = (TextView) convertView.findViewById(R.id.imp_news_date);
				mHolder2.imp_news_time = (TextView) convertView.findViewById(R.id.imp_news_time);
				mHolder2.imp_news_picture = (ImageView) convertView.findViewById(R.id.imp_news_picture);
				mHolder2.iv_isRead = (ImageView) convertView.findViewById(R.id.iv_imp_news_isRead);

				mHolder2.tv_summary = (TextView) convertView.findViewById(R.id.tv_summary);

				mHolder2.position = position;
				convertView.setTag(mHolder2);
				Util.startAni(position, convertView, listView);

			} else {
				if (convertView.getTag() instanceof ViewHolder2) {
					mHolder2 = (ViewHolder2) convertView.getTag();
				}
			}
			final ImageAware imageAware = new ImageViewAware(mHolder2.imp_news_picture, false);

			if (!Constant.noPictureMode) {
				/*
				 * 离线图片加载
				 */
				if (Constant.netType.equals("未知")) {
					if (!TextUtils.isEmpty(news.getpUrl())) {

						StartActivity.imageLoader.displayImage(news.getpUrl(), imageAware, newsOptions(), new ImageLoadingListener() {
							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								/*
								 * 若联网时imageloader没有下载的图片，将使用从离线下载获取到的图片
								 */
								// news.setpUrl(imageUri_Local +
								// news.getId() + ".png");
								LogUtil.i(Tag, imageUri_Local + "/" + news.getId());
								StartActivity.imageLoader.displayImage(imageUri_Local + "/" + news.getId(), imageAware);
							}

							@Override
							public void onLoadingStarted(String imageUri, View view) {
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view) {
							}
						});

					} else {
						if (defaultPicture.equals("default")) {

							StartActivity.imageLoader.displayImage(imageUri_moren, imageAware);
							mHolder2.imp_news_picture.setTag(imageUri_moren);
						} else {
							StartActivity.imageLoader.displayImage(defaultPicture, imageAware);
							mHolder2.imp_news_picture.setTag(defaultPicture);
						}

					}
				} else {
					/*
					 * 在线图片加载
					 */
					if (!TextUtils.isEmpty(news.getpUrl())) {

						// 防止重复加载闪烁错位
						if (mHolder2.imp_news_picture.getTag() == null || !mHolder2.imp_news_picture.getTag().equals(news.getpUrl())) {
							StartActivity.imageLoader.displayImage(news.getpUrl(), imageAware, newsOptions());
							mHolder2.imp_news_picture.setTag(news.getpUrl());
						}

					} else {
						if (defaultPicture.equals("default")) {

							StartActivity.imageLoader.displayImage(imageUri_moren, imageAware);
							mHolder2.imp_news_picture.setTag(imageUri_moren);
						} else {
							StartActivity.imageLoader.displayImage(defaultPicture, imageAware);
							mHolder2.imp_news_picture.setTag(defaultPicture);
						}
					}
				}
				;
			} else {
				StartActivity.imageLoader.displayImage(imageUri_moren, imageAware);
				mHolder2.imp_news_picture.setTag(imageUri_moren);
			}

			/*
			 * 文字数据加载
			 */
			mHolder2.imp_news_title.setText(news.getTitle());

			mHolder2.imp_news_date.setText(DateUtil.getDateMDhhmm(news.getTime()));

			mHolder2.imp_news_time.setText(getNewsDate(news.getTime(), true));
			if (Util.isEmptyAndSpace(news.getSummary())) {
				mHolder2.tv_summary.setText(news.getDigest());
			} else {
				mHolder2.tv_summary.setText(news.getSummary());
			}

			/*
			 * 已阅
			 */

			if (news.isHaveSee() || news.isLoadDetails()) {
				mHolder2.iv_isRead.setVisibility(View.VISIBLE);
				mHolder2.imp_news_title.setTextColor(ColorUtil.lightGray);
			} else {
				mHolder2.iv_isRead.setVisibility(View.INVISIBLE);
				mHolder2.imp_news_title.setTextColor(ColorUtil.drakGray);
			}

			/*
			 * 国内国外新闻去掉红色日期封条
			 */
			if (news.getSpecial() != null) {
				mHolder2.imp_news_date.setVisibility(news.getSpecial().equals("p") || news.getSpecial().equals("n") ? View.VISIBLE : View.INVISIBLE);
			} else {
				mHolder2.imp_news_date.setVisibility(View.INVISIBLE);
			}

			/*
			 * 标签Tag
			 */
			mHolder2.ly_hqss_tags.removeAllViews();

			if (news.getTagIds() != null) {

				String[] tagss = news.getTagIds().split(",");

				if (tagss.length > 0 && tagsMap.size() > 0) {
					for (int i = 0; i < tagss.length; i++) {
						LogUtil.i("jsonTest", Integer.valueOf(tagss[i]) + "");
						View v = createTextTag(tagsMap.get(Integer.valueOf(tagss[i])));
						mHolder2.ly_hqss_tags.addView(v);
					}
					mHolder2.ly_hqss_tags.setVisibility(View.VISIBLE);
				}

			} else {
				mHolder2.ly_hqss_tags.setVisibility(View.GONE);
			}

			mHolder2.ly_hqss_tags.setTag("2");

			return convertView;

		case TYPE_XINWENLIANBO:
			if (convertView == null || !(convertView.getTag() instanceof ViewHolder3)) {
				convertView = inflater.inflate(R.layout.tljr_item_news_xinwenlianbo, null);

				mHolder3 = new ViewHolder3();
 
				mHolder3.date = (TextView) convertView.findViewById(R.id.date);
				mHolder3.digest = (TextView) convertView.findViewById(R.id.digest);

				mHolder3.img_cricle = (ImageView) convertView.findViewById(R.id.img_cricle);

				mHolder3.line = (View) convertView.findViewById(R.id.line_down2);
				mHolder3.red_tips = (TextView) convertView.findViewById(R.id.red_tips);
				
				convertView.setTag(mHolder3);
			} else {
				if (convertView.getTag() instanceof ViewHolder3) {
					mHolder3 = (ViewHolder3) convertView.getTag();
				}
			}

			mHolder3.line.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

			date.setTime(news.getTime());
			mHolder3.date.setText(format2.format(date));
			 
			
			
			if(position==0){
				mHolder3.digest.setTextSize(15);
				mHolder3.digest.setTextColor(Color.parseColor("#323232"));
				mHolder3.digest.setText(news.getDigest());
				mHolder3.red_tips.setTextSize(16);
			}else{
				mHolder3.digest.setTextSize(13);
				mHolder3.red_tips.setTextSize(13);
				mHolder3.digest.setTextColor(Color.parseColor("#555555"));
				mHolder3.digest.setText(news.getDigest());
			}
			
			//StartActivity.imageLoader.displayImage(news.getpUrl(), mHolder3.img_cricle, newsOptions());

			if (!TextUtils.isEmpty(news.getpUrl())) {

				// 防止重复加载闪烁错位
				if (mHolder3.img_cricle.getTag() == null || !mHolder3.img_cricle.getTag().equals(news.getpUrl())) {
					StartActivity.imageLoader.displayImage(news.getpUrl(), mHolder3.img_cricle, newsOptions());
					mHolder3.img_cricle.setTag(news.getpUrl());
				}

			} else {
				if (defaultPicture.equals("default")) {

					StartActivity.imageLoader.displayImage(imageUri_moren, mHolder3.img_cricle);
					mHolder3.img_cricle.setTag(imageUri_moren);
				} else {
					StartActivity.imageLoader.displayImage(defaultPicture, mHolder3.img_cricle);
					mHolder3.img_cricle.setTag(defaultPicture);
				}
			}

			return convertView;

		default:
			break;

		}
		return convertView;
	}

	public DisplayImageOptions newsOptions() {
			if(LIST_VIEW_TYPE == 4){
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

						  .displayer(new RoundedBitmapDisplayer(30))//是否设置为圆角，弧度为多少
						.displayer(new FadeInBitmapDisplayer(100))// 淡入

						.build();
				return options;
			}else{
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

						// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
						.displayer(new FadeInBitmapDisplayer(100))// 淡入

						.build();
				return options;
			}


	 
	}

	public boolean isScroll = false;

	public void setScrollState(boolean scrollState) {
		this.isScroll = scrollState;
	}

	public View createTextTag(Tag tag) {

		View v = activity.getLayoutInflater().inflate(R.layout.tljr_item_hqss_tags, null);
		((TextView) v.findViewById(R.id.tljr_tv_hqss_tags)).setText(tag.getText());
		((TextView) v.findViewById(R.id.tljr_tv_hqss_tags)).setBackgroundColor(Color.parseColor(tag.getColor()));
		v.setPadding(5, 0, 5, 7);

		return v;
	}

	static class ViewHolder {
		LinearLayout topLayout;
		ImageView bigPoint, smallPoint;
		TextView date, time, source, title;
	}

	public class ViewHolder2 {
		int position;
		public TextView imp_news_title;
		public ImageView iv_isRead; // 已批阅
		TextView imp_news_date;
		TextView imp_news_time;
		ImageView imp_news_picture;
		LinearLayout ly_hqss_tags; // 标签layout
		TextView tv_summary;
	}

	public class ViewHolder3 {
		View line;
		TextView date, digest,red_tips;
		ImageView img_cricle;
	}

	public static String getNewsDate(long time, boolean isPictureNews) {
		Date date = new Date(time);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		String dayCurrent = cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(date);
		String day = cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);

		if (day.equals(dayCurrent)) {
			return isPictureNews ? DateUtil.getDateOnlyHour(time) : "今天     " + DateUtil.getDateOnlyHour(time);

		} else {
			return DateUtil.getDateMDhhmm(time);
		}

	}

}