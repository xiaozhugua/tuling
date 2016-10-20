package com.abct.tljr.news;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseFragmentActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.dialog.CueDialog;
import com.abct.tljr.news.bean.News;
import com.abct.tljr.news.fragment.DetailNewsFragment;
import com.abct.tljr.news.fragment.NewsMarkFragment;
import com.abct.tljr.news.fragment.PictureNewsFragment;
import com.abct.tljr.news.fragment.PictureNewsFragment.OnPagerAdapterListener;
import com.abct.tljr.news.mark.OneMarkActivity;
import com.qh.common.login.BwManager;
import com.qh.common.util.LogUtil;

public class NewsActivity extends BaseFragmentActivity implements android.view.View.OnClickListener {

	public static final int MODE_PICTURE = 0; // 图文新闻
	public static final int MODE_WXH = 1; // 公共号
	public static final int MODE_PUSH = 2; // 推送新闻
	public static final int MODE_ONEMARK = 3; // 某一个公共号
	public static int current_Mode = 4;



	/*
	 * 新闻正文页面
	 */
	public long PUBLISH_COMMENT_TIME;
	NewsActivity activity;
	public ViewPager viewpager;
	public static NewsDetailFragmentAdapter adapter;
	public DetailNewsFragment fragment_NewsDetails;

	private TextView tljr_txt_from_name; // 新闻来源

	public HashMap<String, Fragment> fragmentList = new HashMap<String, Fragment>();

	public News news;
	public int Current_position;
	private ArrayList<News> currentList = new ArrayList<News>();
	public String Tag = "NewsActivity";

	String nowTypeName;

	Realm myRealm;

	// RealmAsyncTask transaction ;
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		HuanQiuShiShi.gotoDetailsNews = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myRealm.close();
		fragmentList = null;
	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		BwManager.getInstance().initShare(this);
		setContentView(R.layout.tljr_activity_news);
		activity = (NewsActivity) this;

		if (Constant.isNewsGuideToast != 2) {
			new CueDialog(this, 2).show();
		}

		initData();
		initView();

	}

	private void initData() {
		Bundle bundleObject = getIntent().getExtras();
		try {
			myRealm = Realm.getDefaultInstance();
		} catch (RealmMigrationNeededException e) {
			RealmConfiguration config = new RealmConfiguration.Builder(MyApplication.getInstance()).deleteRealmIfMigrationNeeded().build();
			Realm.setDefaultConfiguration(config);
			myRealm = Realm.getDefaultInstance();
		}
		Current_position = bundleObject.containsKey("position") ? bundleObject.getInt("position") : 0;
		nowTypeName = bundleObject.containsKey("nowTypeName") ? bundleObject.getString("nowTypeName") : "新闻";

		if (bundleObject.containsKey("PictureNewsFragment")) {
			current_Mode = MODE_PICTURE;
			PictureNewsFragment fragment = (PictureNewsFragment) HuanQiuShiShi.fragmentList.get(nowTypeName);
			currentList = fragment.newsManager.getList();

		} else if (bundleObject.containsKey("wxh")) {
			current_Mode = MODE_WXH;

			ArrayList<News> list = new ArrayList<News>(NewsMarkFragment.newsList);

			currentList = list;

		} else if (bundleObject.containsKey("OneMark")) {
			current_Mode = MODE_ONEMARK;

			ArrayList<News> list = new ArrayList<News>(OneMarkActivity.list);

			currentList = list;

		}  else if (bundleObject.containsKey("pushNews")) {
			current_Mode = MODE_PICTURE;
			news = (News) bundleObject.getSerializable("news");
			currentList.add(news);
			Current_position = 0;
		} else {
			finish();
		}
	}

	private void initView() {
		/*
		 * title bar
		 */
		tljr_txt_from_name = (TextView) findViewById(R.id.tljr_txt_news_from_name);

		((RelativeLayout) findViewById(R.id.tljr_img_news_back)).setOnClickListener(this);
		viewpager = (ViewPager) findViewById(R.id.tljr_hqss_news_mViewPager);

	
		tljr_txt_from_name.setText(nowTypeName);

		adapter = new NewsDetailFragmentAdapter(activity.getSupportFragmentManager());
		adapter.notifyDataSetChanged();
		viewpager.setAdapter(adapter);

		viewpager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Constant.addClickCount();
				Current_position = position;

				if (position == 0) {
					Toast.makeText(activity, "已经是第一页了", Toast.LENGTH_LONG).show();
				}

				/*
				 * 更新具体页面的已阅
				 */
				if (fragmentList.get(position + "") != null) {
					DetailNewsFragment detailsFragment = (DetailNewsFragment) fragmentList.get(position + "");
					detailsFragment.reFresh();
				}

				if (current_Mode == MODE_PICTURE) {
					/*
					 * 上传已阅
					 */
					final int f = position;
					if (currentList.get(position) != null) {
						postDelayed(new Runnable() {
							@Override
							public void run() {
								if (!currentList.get(f).isHaveSee() && !HuanQiuShiShi.id.contains(currentList.get(f).getId())) {
									JSONObject obj = new JSONObject();

									try {
										obj.put("time", currentList.get(f).getTime());
										obj.put("id", currentList.get(f).getId());
										obj.put("species", currentList.get(f).getSpecial());
									} catch (JSONException e) {
										e.printStackTrace();
									}

									HuanQiuShiShi.id.add(currentList.get(f).getId());
									if (HuanQiuShiShi.readId == null) {
										HuanQiuShiShi.readId = new JSONArray();
									}
									HuanQiuShiShi.readId.put(obj);
									if (HuanQiuShiShi.readId.length() > 5) {
										HuanQiuShiShi.updataUserIsRead();
									}
								}
								myRealm.beginTransaction();
								currentList.get(f).setHaveSee(true);
								myRealm.copyToRealmOrUpdate(currentList.get(f));
								myRealm.commitTransaction();
								LogUtil.i("read", "viewpager see:" + currentList.get(f).isHaveSee());

							}
						}, 1000);

					}

					if (position == (currentList.size() - 10) || position == (currentList.size() - 1)) {
						if (currentList.size() < 10) {
							return;
						}
						Toast.makeText(activity, "正在加载更多新闻...", Toast.LENGTH_LONG).show();
						if (HuanQiuShiShi.fragmentList.get(nowTypeName) instanceof PictureNewsFragment) {
							PictureNewsFragment fg = (PictureNewsFragment) HuanQiuShiShi.fragmentList.get(nowTypeName);
							fg.getNewestNews(false, false);
							fg.setAdapterListener(new OnPagerAdapterListener() {
								@Override
								public void notifyDataAdapter() {
									post(new Runnable() {
										@Override
										public void run() {
											PictureNewsFragment fragment = (PictureNewsFragment) HuanQiuShiShi.fragmentList.get(nowTypeName);
											currentList = fragment.newsManager.getList();

											adapter.notifyDataSetChanged();
											viewpager.setCurrentItem(Current_position, true);
											Toast.makeText(activity, "加载新闻完成...", Toast.LENGTH_LONG).show();
										}
									});
								}
							});
						}
					}

				} else if (current_Mode == MODE_WXH) {

					if (position == (currentList.size() - 10) || position == (currentList.size() - 1)) {
						if (currentList.size() < 10) {
							return;
						}
						Toast.makeText(activity, "正在加载更多新闻...", Toast.LENGTH_LONG).show();
						if (HuanQiuShiShi.fragmentList.get(nowTypeName) instanceof NewsMarkFragment) {
							NewsMarkFragment fg = (NewsMarkFragment) HuanQiuShiShi.fragmentList.get(nowTypeName);
							fg.getData();
							fg.setAdapterListener(new OnPagerAdapterListener() {
								@Override
								public void notifyDataAdapter() {
									post(new Runnable() {
										@Override
										public void run() {

											ArrayList<News> list = new ArrayList<News>(NewsMarkFragment.newsList);
											currentList = list;

											adapter.notifyDataSetChanged();
											viewpager.setCurrentItem(Current_position, true);
											Toast.makeText(activity, "加载新闻完成...", Toast.LENGTH_LONG).show();
										}
									});
								}
							});
						}
					}

				}  

				
				
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		viewpager.setCurrentItem(Current_position);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (currentList != null && currentList.size() > 0 && current_Mode == MODE_PICTURE) {

			PictureNewsFragment fragment = (PictureNewsFragment) HuanQiuShiShi.fragmentList.get(nowTypeName);
			if (fragment != null && fragment.newsManager != null) {
				fragment.newsManager.setList(currentList);
			}
		}

	}

	public class NewsDetailFragmentAdapter extends FragmentPagerAdapter {
		FragmentManager fm;

		public NewsDetailFragmentAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;

		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);

			View view = (View) object;
			((ViewPager) container).removeView(view);
			view = null;

		}

		@Override
		public Fragment getItem(int position) {

			if (fragmentList.get(position) == null) {
				DetailNewsFragment fg = new DetailNewsFragment();
				fg.setNews(currentList.get(position));
				fg.setRealm(myRealm);

				fragmentList.put(position + "", fg);
				return fg;
			} else {
				DetailNewsFragment fg = (DetailNewsFragment) fragmentList.get(position);
				return fg;
			}

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return currentList.size();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		BwManager.getInstance().onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		BwManager.getInstance().onNewIntent(intent);
		super.onNewIntent(intent);
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.tljr_img_news_back:
			finish();
			break;

		default:
			break;
		}
	}

}