package com.abct.tljr.kline;

import java.util.ArrayList;

import com.abct.tljr.R;
import com.abct.tljr.kline.gegu.fragment.OneguCaiwuFragment;
import com.abct.tljr.kline.gegu.fragment.OneguGaikuangFragment;
import com.abct.tljr.kline.gegu.fragment.OneguGuzhiFragment;
import com.abct.tljr.kline.gegu.fragment.OneguXinwenFragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * momo写的，该类是实现点击onegu页面下方一组按钮对应的变化
 */
public class OneguTitleBar {

	/**
	 * 按钮上面的滑动页面
	 */
	private ViewPager viewpager;
	/**
	 * 下方一组按钮的容器
	 */
	private LinearLayout buts;

	/** 上一个被选中的号码 */
	private int lastSelectNumber = 0;

	private int selectNumber = 0;

	private ImageView[] imgs;
	private TextView[] texts;

	private ViewGroup layout;
	private ArrayList<Fragment> list;

	/**
	 * 当前股票的market是否是深圳或者上海
	 */
	private boolean isShenzhenOrShangHai;

	public OneguTitleBar(ViewPager viewpager, LinearLayout buts, ArrayList<Fragment> list,
			boolean isShenZhenOrShangHai) {
		OneguTitleBar.this.viewpager = viewpager;
		OneguTitleBar.this.buts = buts;
		this.list = list;
		this.isShenzhenOrShangHai = isShenZhenOrShangHai;
		initView();
		// 一打开初始化的时候就先全部设置为没有选中状态

		// 设置一开始就选中第0个
		// viewpager.setCurrentItem(0);
		onPagerSelect(0);
	}

	/**
	 * 处理当前被选中的按钮
	 */
	private void onPagerSelect(int selectNumber) {
		// TODO Auto-generated method stub

		if (isShenzhenOrShangHai) {

			texts[selectNumber].setTextColor(Color.parseColor("#EE6B5F"));
			switch (selectNumber) {
			case 0:
				imgs[0].setImageResource(R.drawable.img_guzhi2);
				break;
			case 1:
				imgs[1].setImageResource(R.drawable.img_caiwu2);
				break;
			case 2:
				imgs[2].setImageResource(R.drawable.img_xinwen2);
				break;
			case 3:
				imgs[3].setImageResource(R.drawable.img_gaikuang2);
				break;
			case 4:
				imgs[4].setImageResource(R.drawable.img_zhiyan2);
				texts[5].setTextColor(Color.parseColor("#EE6B5F"));
				break;

			default:
				break;
			}
		} else {
			switch (selectNumber) {
			case 0:
				texts[0].setTextColor(Color.parseColor("#EE6B5F"));
				imgs[0].setImageResource(R.drawable.img_guzhi2);
				break;
			case 1:
			case 4:
				texts[4].setTextColor(Color.parseColor("#EE6B5F"));
				imgs[4].setImageResource(R.drawable.img_zhiyan2);
				texts[5].setTextColor(Color.parseColor("#EE6B5F"));
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 处理上一个被选中的按钮
	 */
	private void onPagerLastSelect(int lastSelectNumber) {
		// TODO Auto-generated method stub

		if (isShenzhenOrShangHai) {

			texts[lastSelectNumber].setTextColor(Color.parseColor("#757575"));
			switch (lastSelectNumber) {
			case 0:
				imgs[0].setImageResource(R.drawable.img_guzhi1);
				break;
			case 1:
				imgs[1].setImageResource(R.drawable.img_caiwu1);
				break;
			case 2:
				imgs[2].setImageResource(R.drawable.img_xinwen1);
				break;
			case 3:
				imgs[3].setImageResource(R.drawable.img_gaikuang1);
				break;
			case 4:
				imgs[4].setImageResource(R.drawable.img_zhiyan1);
				texts[5].setTextColor(Color.parseColor("#757575"));
				break;

			default:
				break;
			}
		} else {
			switch (lastSelectNumber) {
			case 0:
				texts[0].setTextColor(Color.parseColor("#757575"));
				imgs[0].setImageResource(R.drawable.img_guzhi1);
				break;

			case 1:
			case 4:
				imgs[4].setImageResource(R.drawable.img_zhiyan1);
				texts[4].setTextColor(Color.parseColor("#757575"));
				texts[5].setTextColor(Color.parseColor("#757575"));
				break;

			default:
				break;
			}
		}

	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// TODO Auto-generated method stub

		imgs = new ImageView[buts.getChildCount()];
		texts = new TextView[buts.getChildCount() + 1];
		for (int i = 0; i < buts.getChildCount(); i++) {

			layout = (ViewGroup) buts.getChildAt(i);
			imgs[i] = (ImageView) layout.getChildAt(0);// 图片

			if (i == 4) {
				// 第4个layout有两个文字
				RelativeLayout relativeLayout = (RelativeLayout) layout.getChildAt(1);
				texts[4] = (TextView) relativeLayout.getChildAt(0);
				texts[5] = (TextView) relativeLayout.getChildAt(1);
				texts[4].setTextColor(Color.parseColor("#757575"));
				texts[5].setTextColor(Color.parseColor("#757575"));
			} else {
				// 第0个到第3个layout

				texts[i] = (TextView) layout.getChildAt(1);// 文字
				texts[i].setTextColor(Color.parseColor("#757575"));
			}
		}

		for (int i = 0; i < buts.getChildCount(); i++) {
			final int m = i;
			layout = (ViewGroup) buts.getChildAt(i);
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					lastSelectNumber = selectNumber;
					selectNumber = m;
					// 先处理上一个被选中的按钮状态
					onPagerLastSelect(lastSelectNumber);
					// 处理当前被选中的按钮状态
					onPagerSelect(selectNumber);
					// 页面跳转
					if (isShenzhenOrShangHai) {

						viewpager.setCurrentItem(selectNumber);
					} else {
						if (selectNumber == 0) {
							viewpager.setCurrentItem(0);
						} else {
							viewpager.setCurrentItem(1);
						}
					}

				}
			});
			if (!isShenzhenOrShangHai && i > 0 && i < 4) {
				layout.setVisibility(View.GONE);

			}
		}

		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int num) {
				// TODO Auto-generated method stub
				// 根据当前页面来加载对应页面的数据
				refleshFragmentByNumber(num);
				lastSelectNumber = selectNumber;
				selectNumber = num;
				// 先处理上一个被选中的按钮状态
				onPagerLastSelect(lastSelectNumber);
				// 处理当前被选中的按钮状态
				onPagerSelect(selectNumber);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/*
	 * 根据当前页面来加载对应页面的数据
	 */
	private void refleshFragmentByNumber(int num) {
		// TODO Auto-generated method stub
		if (isShenzhenOrShangHai) {
			
			switch (num) {
			case 0:
				OneguGuzhiFragment oneguGuzhiFragment = (OneguGuzhiFragment) list.get(num);
				oneguGuzhiFragment.initRootView();
				break;
			case 1:
				OneguCaiwuFragment oneguCaiwuFragment = (OneguCaiwuFragment) list.get(num);
				oneguCaiwuFragment.initRootView();
				break;
				
			case 2:
				OneguXinwenFragment oneguXinwenFragment = (OneguXinwenFragment) list.get(num);
				oneguXinwenFragment.initRootView();
				break;
			case 3:
				OneguGaikuangFragment oneguGaikuangFragment = (OneguGaikuangFragment) list.get(num);
				oneguGaikuangFragment.initRootView();
				break;
				
			default:
				break;
			}
		}else {
			switch (num) {
			case 0:
				OneguGuzhiFragment oneguGuzhiFragment = (OneguGuzhiFragment) list.get(num);
				oneguGuzhiFragment.initRootView();
				break;
			
				
			default:
				break;
			}
		}
	}

}
