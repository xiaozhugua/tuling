package com.abct.tljr.chart;

import com.abct.tljr.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.qh.common.util.CircleBitmapDisplayer;

import android.graphics.Bitmap;

public class ImageOptions {
	/** 新闻列表中用到的图片加载配置 */
	public static DisplayImageOptions newsOptions; 
	public static DisplayImageOptions getListOptions() {
		if (newsOptions != null) {
			return newsOptions;
		}
		  newsOptions = new DisplayImageOptions.Builder()
				// // 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.img_tou)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.img_tou)
				// 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.img_tou)
				.cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)
				.delayBeforeLoading(100)
				// 设置图片下载前的延迟
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// 。preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
		 		.displayer(new FadeInBitmapDisplayer(900))// 淡入

				.build();
		return newsOptions;
	}

	public static DisplayImageOptions options;

	/** 新闻列表中用到的图片加载配置圆角 */
	public static DisplayImageOptions getCircleListOptions() {
		if (options != null) {
			return options;
		}
		options = new DisplayImageOptions.Builder()
				// // 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.img_tou)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.img_tou)
				// 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.img_tou).cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)
				// 设置图片下载前的延迟
				// .delayBeforeLoading(int delayInMillis)//int
				.delayBeforeLoading(100)
				
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// 。preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 淡入
				.displayer(new CircleBitmapDisplayer()).build();
		return options;
	}

	public static DisplayImageOptions noMemOptions;

	/** 新闻列表中用到的图片加载配置圆角 */
	public static DisplayImageOptions getNoMemListOptions() {
		if (noMemOptions != null) {
			return noMemOptions;
		}
		noMemOptions = new DisplayImageOptions.Builder()
				// // 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.img_morentupian)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.img_morentupian)
				// 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.img_morentupian)
				.cacheInMemory(false)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(false)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
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
