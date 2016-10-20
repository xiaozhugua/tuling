package com.abct.tljr.news.mark;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.news.HuanQiuShiShi;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;

/**
 * @author xbw
 * @version 创建时间：2015年9月7日 下午4:10:58
 */
public class NewsMarkActivity extends BaseActivity {
	public ViewPager viewpager = null;
	private MarkTitleBar pagerTitleBar;
	private ArrayList<View> list = new ArrayList<View>();
	private MarkView my;
	private MarkView all;
	public View noMark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View v = View.inflate(this, R.layout.tljr_activity_news_mark, null);
		setContentView(v);
		v.findViewById(R.id.tljr_img_newsmark_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
		noMark = findViewById(R.id.tljr_titleGroup2);
		noMark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewpager.setCurrentItem(1);
				all.getData();
			}
		});
		viewpager = (ViewPager) findViewById(R.id.tljr_mViewPager);
		pagerTitleBar = new MarkTitleBar(viewpager, v);
		showPane();
	}

	private void showPane() {
		my = new MarkView(this, false);
		my.getData();
		all = new MarkView(this, true);
		list.add(my);
		list.add(all);
		viewpager.setAdapter(new PagerAdapter() {

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(list.get(position));
				return list.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(list.get(position));
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return list.size();
			}
		});
		viewpager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int page, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int page) {
				Constant.addClickCount();
				pagerTitleBar.onPageSelected(page);
				if (page == 1) {
					all.getData();
					noMark.setVisibility(View.GONE);
				} else if (page == 0) {
					noMark.setVisibility(my.getCount() == 0 ? View.VISIBLE
							: View.GONE);
				}
			}
		});
	}

	public void mark(final boolean isMark, final OneMark onemark,
			final CompoundButton buttonView) {
		
		if(MyApplication.getInstance().self ==null){
			return;
		}
		String uid = PreferenceUtils.getInstance().preferences.getString("UserId", "0");
		String mark = isMark ? "add" : "cancel";
 
		
		LogUtil.i("newsMark", UrlUtil.URL_new +"api/subscribe/" + mark+"?"+"platform=1&uid=" + uid
						+ "&wxh=" + onemark.getNumber()+"&version="+HuanQiuShiShi.version);
		
		NetUtil.sendPost(UrlUtil.URL_new +"api/subscribe/" + mark,
				"platform=1&uid=" + uid
						+ "&wxh=" + onemark.getNumber()+"&version="+HuanQiuShiShi.version,
				new NetResult() {

					@Override
					public void result(final String msg) {
						post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									LogUtil.i("newsMark", msg);
									JSONObject object = new JSONObject(msg);
									
									
									if (object.optString("status","0").equals("1")) {
										onemark.setMark(isMark);
										my.changeMark(onemark, isMark);
										all.changeMark(onemark, isMark);
									} else {
										showToast((isMark ? "订阅" : "取消订阅")
												+ "失败");
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								buttonView.setChecked(onemark.isMark());
							}
						});
					}
				});
	}

	public boolean isMark(String id) {
		return my.isMark(id);
	}
}