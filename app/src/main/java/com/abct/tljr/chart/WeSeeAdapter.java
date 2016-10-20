package com.abct.tljr.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.CircularImage;
import com.abct.tljr.utils.Util;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WeSeeAdapter extends BaseAdapter {

	Context context;
	ArrayList<WeseeBean> array;
	private String TAG = "WeSeeAdapter";
	private Handler weseehandler;
	private HashMap<String, Integer> HashColor;

	public WeSeeAdapter(Context context, ArrayList<WeseeBean> array, Handler handler) {
		this.context = context;
		this.array = array;
		weseehandler = handler;
		HashColor = new HashMap<String, Integer>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return array.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	};

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (array.get(position).uid.equals(MyApplication.getInstance().self.getId())) {
			return 0;
		} else {
			return 1;
		}
	}

	MediaPlayer mp;
	String path;

	@Override
	public View getView(int position, View v, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final WeseeBean we = array.get(position);
		ViewHolder holder = null;
		int type = getItemViewType(position);
		if (v == null) {
			holder = new ViewHolder();
			if (type == 0) {
				v = View.inflate(context, R.layout.tljr_activity_chart_item_right, null);
			} else {
				v = View.inflate(context, R.layout.tljr_activity_chart_item, null);
			}
			holder.name = (TextView) v.findViewById(R.id.name);
			holder.image = (CircularImage) v.findViewById(R.id.im);
			holder.shenyin = (View) v.findViewById(R.id.message_voice_image);
			holder.content = (TextView) v.findViewById(R.id.content);
			holder.Time = (TextView) v.findViewById(R.id.time);
			v.setTag(holder);
		} else {

			holder = (ViewHolder) v.getTag();
		}
		holder.name.setText(we.nickname);
		// StartActivity.imageLoader.displayImage(holder.image.getTag().toString(),
		// holder.image);
		final ImageAware imageAware = new ImageViewAware(holder.image, false);

		if (HashColor.get(we.id) == null) {
			Random random = new Random();
			int r = random.nextInt(256);
			int g = random.nextInt(256);
			int b = random.nextInt(256);
			HashColor.put(we.id, Color.rgb(r, g, b));
		}
		GradientDrawable my = (GradientDrawable) holder.image.getBackground();
		my.setColor(HashColor.get(we.id));

		if (we.avatar.equals("http://user.cavacn.com:3000/images/avatar-default.jpg")) {
			holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.img_tou));
		} else {
			// 防止重复加载闪烁错位
			if (holder.image.getTag() == null || !holder.image.getTag().equals(we.avatar)) {
				StartActivity.imageLoader.displayImage(we.avatar, imageAware, ImageOptions.getListOptions(),
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri, View view) {
								// TODO Auto-generated method stub

							}


							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingCancelled(String imageUri, View view) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								// TODO Auto-generated method stub
								StartActivity.imageLoader.displayImage(we.avatar, imageAware);
							}
						});
				holder.image.setTag(we.avatar);
			}
		}
		holder.shenyin.setVisibility(View.GONE);
		holder.content.setText(we.content);

		// if(position==0){
		// holder.Time.setVisibility(View.VISIBLE);
		// }else{
		// if(Util.getDateOnlyHour(we.time).equals(Util.getDateOnlyHour(array.get(position-1).time))){
		// holder.Time.setVisibility(View.GONE);
		// }else{
		// holder.Time.setVisibility(View.VISIBLE);
		// }
		// }
		// holder.Time.setText(Util.getDateHHmmss(we.time));
		holder.Time.setText(Util.getDateNoss(we.time));

		return v;
	}

	public final class ViewHolder {
		TextView name;
		TextView content;
		TextView Time;
		View shenyin;
		CircularImage image;
	}

	public static class Person {
		String Avatar;
		String NickName;
	}

	public static class WeseeBean {
		String content;
		long time;
		String nickname;
		String uid;
		String avatar;
		String id;
	}

}