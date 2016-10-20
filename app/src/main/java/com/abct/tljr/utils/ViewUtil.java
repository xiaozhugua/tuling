package com.abct.tljr.utils;

import com.abct.tljr.MyApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author xbw
 * @version 创建时间：2015年11月13日 下午6:28:22
 */
public class ViewUtil {
	public static int WIDTH;
	public static int HEIGHT;

	public static void init() {
		WIDTH = getScreenWidth(MyApplication.getInstance());
		HEIGHT = getScreenHeight(MyApplication.getInstance());
	}

	/**
	 * 得到当前手机屏幕的宽
	 */
	public static int getScreenWidth(Context context) {
		int screenWidth;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		return screenWidth;
	}

	/**
	 * 得到当前手机屏幕的高
	 */
	public static int getScreenHeight(Context context) {
		int screenHeight;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels;
		return screenHeight;
	}

	public static BitmapDrawable getNewDrawable(Context context, int restId, int dstWidth, int dstHeight) {
		Bitmap Bmp = BitmapFactory.decodeResource(context.getResources(), restId);
		Bitmap bmp = Bmp.createScaledBitmap(Bmp, dstWidth, dstHeight, true);
		BitmapDrawable d = new BitmapDrawable(bmp);
		d.setTargetDensity(context.getResources().getDisplayMetrics());
		return d;
	}
}
