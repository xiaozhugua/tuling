package com.abct.tljr.news.mark;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.news.NewsActivity;
import com.abct.tljr.news.adapter.NewsAdapter;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.fragment.NewsMarkFragment;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author xbw
 * @version 创建时间：2015年9月8日 下午3:29:48
 */
public class OneMarkActivity extends BaseActivity {
	public static OneMark mark;
	private ListView lv;
	private BaseAdapter adapter;
	private Map<String, News> map = new HashMap<String, News>();
	public static ArrayList<News> list = new ArrayList<News>();
	private ArrayList<News> mList = new ArrayList<News>();
	private int page = 0;
	private boolean isFlush = false;
	private boolean startLoadMore = true;
	private Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_news_onemark);
		 list = new ArrayList<News>();
		 mList = new ArrayList<News>();
		findViewById(R.id.tljr_mark_btn_lfanhui).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
		Drawable dw = new Drawable() {

			@Override
			public void setColorFilter(ColorFilter arg0) {
			}

			@Override
			public void setAlpha(int arg0) {
			}

			@Override
			public int getOpacity() {
				return 0;
			}

			@Override
			public void draw(Canvas canvas) {
				// TODO Auto-generated method stub
				Paint p = new Paint();
				String start = "#36648B";
				String middle = "#828282";
				String end = "#36648B";

				int[] color = { Color.parseColor(start),
						Color.parseColor(middle), Color.parseColor(end) };
				float[] position = { 0, 0.3f, 0.9f };
				LinearGradient lg = new LinearGradient(0, 0, Util.WIDTH, 1000,
						color, position, TileMode.MIRROR);
				p.setShader(lg);
				canvas.drawRect(0, 0, Util.WIDTH, 1000, p);
			}
		};
		findViewById(R.id.layout_title_background).setBackground(dw);
		((TextView) findViewById(R.id.tljr_tv_news_name)).setText(mark
				.getName());
		((TextView) findViewById(R.id.tljr_tv_news_info)).setText(Html.fromHtml(mark
				.getInfo()));
		StartActivity.imageLoader.displayImage(mark.getAvatar(),
				((ImageView) findViewById(R.id.tljr_tv_news_avatar)),
				StartActivity.options);
		lv = (ListView) findViewById(R.id.tljr_mark_grp_lv);
		 
	 
		
		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView v, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (visibleItemCount + firstVisibleItem == totalItemCount) {

					 
						if (startLoadMore) {
								 handler.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										loadMore();
									}
								});
 
						}
						startLoadMore = false;
				}

			}
		});
		
		
		
		
		
		
		
		adapter = new BaseAdapter() {

			@Override
			public View getView(final int position, View view, ViewGroup parent) {
				// TODO Auto-generated method stub]
				ViewHolder mHolder;
				if (view == null) {
					view = View.inflate(OneMarkActivity.this,
							R.layout.tljr_item_news_onemark, null);
					mHolder = new ViewHolder();
					mHolder.imp_news_title = (TextView) view
							.findViewById(R.id.imp_hqss_news_title);
					mHolder.imp_news_date = (TextView) view
							.findViewById(R.id.imp_news_time);
					mHolder.imp_news_picture = (ImageView) view
							.findViewById(R.id.imp_news_picture);
					view.setTag(mHolder);
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							ViewHolder mHolder = (ViewHolder) v.getTag(); 
							
							Intent intent = new Intent(OneMarkActivity.this, NewsActivity.class);

							Bundle bundle = new Bundle();
							bundle.putString("OneMark", "");
							bundle.putInt("position", position);
							bundle.putString("nowTypeName", mark
									.getName());

							intent.putExtras(bundle);
							 startActivity(intent);
							
							
							
							
						}
					});
				} else {
					mHolder = (ViewHolder) view.getTag();
				}
				News news = list.get(position);
				mHolder.imp_news_title.setText(news.getTitle());
				mHolder.imp_news_date.setText(news.getDate());
				mHolder.url = news.getUrl();
				StartActivity.imageLoader.displayImage(news.getpUrl(),
						mHolder.imp_news_picture, StartActivity.options);
				return view;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return list.get(position);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}
		};
		lv.setAdapter(adapter);
		 
		loadMore();
	}

	static class ViewHolder {
		TextView imp_news_title;
		TextView imp_news_date;
		ImageView imp_news_picture;
		String url;
	}

	private void getData() {
		
		ProgressDlgUtil.showProgressDlg("", this);
		
		 
		
		String uid = PreferenceUtils.getInstance().preferences.getString("UserId", "0");
		
		
		LogUtil.i("PictureNewsFragment", UrlUtil.URL_new +"api/subscribe/article"+"?"+"platform=1&uid=" + uid+ "&index="
				+ page+"&wxh="+mark.getNumber());
		
			NetUtil.sendPost(UrlUtil.URL_new +"api/subscribe/article",
					"platform=1&uid=" + uid+ "&index="
							+ page+"&wxh="+mark.getNumber(), new NetResult() {
			
			@Override
			public void result(String msg) {
				// TODO Auto-generated method stub
				try {
					LogUtil.i("PictureNewsFragment", msg);
					
					final JSONObject object = new JSONObject(msg).getJSONObject("joData");
					final org.json.JSONArray array = object
							.getJSONArray("articles");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						News news = new News();
						news.setTitle(obj.optString("title"));
						try {
							news.setTime( DateUtil.format.parse(obj.optString("time")).getTime());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						news.setSimple_time(NewsAdapter.getNewsDate(news.getTime(), true));
						
					    news.setDate(DateUtil.getDateMDhhmm(news.getTime()));
						
						news.setId(obj.optString("id"));
						news.setType(obj.optString("type"));
						news.setSpecial(NewsMarkFragment.nowTypeSpecial);
						news.setAuthor(obj.optString("name"));
						news.setAuthorAvatar(obj.optString("purl"));
						news.setpUrl(obj.optString("purl"));
						mList.add(news);
					}
					
					
					
					
					 
					ProgressDlgUtil.stopProgressDlg();
					post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (array.length() > 0    ) {
								list.addAll(mList);
								mList.clear();
								
								adapter.notifyDataSetChanged();
								
								if(object.optInt("next",-1)!=-1)
									startLoadMore=true;
								page++;
							} else {
								startLoadMore=false;
								showToast("没有更多数据");
							}
							isFlush = false;
						}
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ProgressDlgUtil.stopProgressDlg();
				}
			}
		});
	}

	private void loadMore() {
		if (isFlush || list.size() >= 100) {
			return;
		}
		isFlush = true;
		getData();
	}

}
