package com.abct.tljr.news.mark;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.news.adapter.NewsAdapter;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.fragment.NewsMarkFragment;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MarkRecommend {
	/**
	 * 大V精选订阅
	 */
	public Activity activity;
	GridView gv;
	View view;
	LayoutInflater inflater = null;
	ArrayList<Recommend> list = new ArrayList<Recommend>();
	private Handler handler;
	 
	 

	public MarkRecommend(Activity activity, View view, Handler handler) {
		this.activity = activity;
		this.view = view;
		inflater = LayoutInflater.from(activity);
		this.handler = handler;
		initView();
	}

	public void initView() {
		gv = (GridView) view.findViewById(R.id.gv_recommend);
	

		String url = UrlUtil.URL_new + "api/subscribe/recommend";
		String params = "platform=1&uid=10159&version=4";

		LogUtil.i("NewsMarkRecommend", url + "?" + params);
	 ProgressDlgUtil.showProgressDlg(null, activity);
		NetUtil.sendPost(url, params, new NetResult() {
			@Override
			public void result(String msg) {
				LogUtil.i("NewsMarkRecommend", msg);
				try {
					JSONObject object = new JSONObject(msg).getJSONObject("joData");
					JSONArray array = object.getJSONArray("data");

					for (int i = 0; i < 6; i++) {
						Recommend cmd = new Recommend();
						JSONObject obj = array.getJSONObject(i);
						cmd.setImg(obj.optString("purl"));
						cmd.setName(obj.optString("name"));
						cmd.setMark(obj.optBoolean("isSubscribe"));
						cmd.setWxh(obj.optString("wxh"));
						cmd.setCount(obj.optString("count"));
						// cmd.img = i + "";
						// cmd.count = "1" + i + "万";
						// cmd.name = "订阅号" + i;
						// cmd.isMark = false;
						list.add(cmd);
					}
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
					 	ProgressDlgUtil.stopProgressDlg();
							gv.setAdapter(new RecommendAdapter());
						}
					});

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					ProgressDlgUtil.stopProgressDlg();
					e.printStackTrace();
				}

			}
 

		});

	}

	class RecommendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder mHolder;
			final Recommend cmd = list.get(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.tljr_item_news_mark_recommend, null);

				mHolder = new ViewHolder();

				mHolder.name = (TextView) convertView.findViewById(R.id.name);
				mHolder.count = (TextView) convertView.findViewById(R.id.count);
				mHolder.mark = (TextView) convertView.findViewById(R.id.mark);
				mHolder.iv = (ImageView) convertView.findViewById(R.id.iv);

				convertView.setTag(mHolder);

			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			StartActivity.imageLoader.displayImage(cmd.getImg(), mHolder.iv, StartActivity.options);

			mHolder.name.setText(cmd.getName());
			mHolder.count.setText(cmd.getCount() + "万人订阅");

			mHolder.mark.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {

					if (MyApplication.getInstance().self == null) {

						Toast.makeText(activity, "未登录或注册无法完成操作", Toast.LENGTH_SHORT).show();
						((MainActivity) activity).login();
						return;
					}

					String uid = PreferenceUtils.getInstance().preferences.getString("UserId", "0");
					String mark = !cmd.isMark ? "add" : "cancel";

					LogUtil.i("NewsMarkRecommend", UrlUtil.URL_new + "api/subscribe/" + mark + "?" + "platform=1&uid=" + uid + "&wxh=" + cmd.getWxh()
							+ "&version=" + HuanQiuShiShi.version);

					NetUtil.sendPost(UrlUtil.URL_new + "api/subscribe/" + mark, "platform=1&uid=" + uid + "&wxh=" + cmd.getWxh() + "&version="
							+ HuanQiuShiShi.version, new NetResult() {

						@Override
						public void result(String msg) {
							// TODO Auto-generated method stub
							LogUtil.i("NewsMarkRecommend", msg);
							TextView tv = (TextView) v;

							if (cmd.isMark) {
								tv.setText("订阅");
								tv.setTextColor(activity.getResources().getColor(R.color.redtitlebj));
								cmd.isMark = false;
							} else {

								tv.setText("已订阅");
								tv.setTextColor(activity.getResources().getColor(R.color.gray_light));
								cmd.isMark = true;
							}
						}
					});

				}
			});

			return convertView;
		}

	}

	class ViewHolder {
		TextView name;
		TextView count;
		ImageView iv;
		TextView mark;
	}

	class Recommend {
		private String img;
		private String name;
		private String count;
		private boolean isMark;
		private String wxh;

		public String getWxh() {
			return wxh;
		}

		public void setWxh(String wxh) {
			this.wxh = wxh;
		}

		public boolean isMark() {
			return isMark;
		}

		public void setMark(boolean isMark) {
			this.isMark = isMark;
		}

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCount() {
			return count;
		}

		public void setCount(String count) {
			this.count = count;
		}

	}

}
