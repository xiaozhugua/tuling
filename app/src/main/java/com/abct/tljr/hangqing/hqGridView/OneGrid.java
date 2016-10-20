package com.abct.tljr.hangqing.hqGridView;

import com.abct.tljr.R;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.utils.Util;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

public class OneGrid {
	private View v;
	private OneGu gu;
	private MainActivity activity;
	private ImageView bj;

	public View getView() {
		return v;
	}

	private String key;

	public String getKey() {
		return key;
	}

	public OneGu getGu() {
		return gu;
	}

	private TextView name, now, change;

	public OneGrid(OneGu gu, MainActivity activity, int num) {
		this.gu = gu;
		this.activity = activity;
		int height = Util.dp2px(activity, num == 2 ? 100 : 80);
		v = activity.getLayoutInflater().inflate(R.layout.tljr_item_gird, null);
		bj = (ImageView) v.findViewById(R.id.tljr_g_bg);
		name = (TextView) v.findViewById(R.id.tljr_g_name);
		now = (TextView) v.findViewById(R.id.tljr_g_now);
		change = (TextView) v.findViewById(R.id.tljr_g_change);
		name.setHeight(height * 5 / 16);
		now.setHeight(height * 6 / 16);
		change.setHeight(height * 5 / 16);
		if (num == 2) {
			name.setTextSize(20);
			now.setTextSize(26);
			change.setTextSize(18);
		}
		initOneGu(gu);
		v.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, height));
		this.key = gu.getMarket() + gu.getCode();
		addListener();
	}

	private void addListener() {
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtil.e("行情点击", gu.getName());
				Intent intent = new Intent(activity, OneGuActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("code", gu.getCode());
				bundle.putString("name", gu.getName());
				bundle.putString("market", gu.getMarket());
				bundle.putString("key", gu.getKey());
				bundle.putSerializable("onegu", gu);
				bundle.putInt("type", 1);
				intent.putExtras(bundle);
				activity.startActivity(intent);
			}
		});
	}

	private void changeText(TextView view, String str, int start) {
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new AbsoluteSizeSpan(10, true), start, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(style);
	}

	public void initOneGu(OneGu gu) {
		// String a = ((gu.getTag() == null || gu.getTag().equals("") || gu
		// .getTag().equals(gu.getName())) ? gu.getName() : (gu.getName()
		// + "(" + gu.getTag() + ")"));
		String a = (gu.getTag() == null ? gu.getName() : gu.getTag());
		name.setText(a);
		// if (a.contains("("))
		// changeText(name, a, a.indexOf("("));
		if (gu.getNow() >= 100000000000l) {
			now.setText(Util.df.format(gu.getNow() / 1000000000000l) + "万亿");
		} else if (gu.getNow() >= 10000000) {
			now.setText(Util.df.format(gu.getNow() / 100000000) + "亿");
		} else {
			// now.setText(gu.getNow() == 0 ? "--" :
			// Util.df.format(gu.getNow()));
			now.setText(gu.getNow() == 0 ? "--" : (gu.getNow() + ""));
		}
		if (Math.abs(gu.getChange()) >= 100000000000l) {
			change.setText((gu.getChange() > 0 ? "+" : "") + (Util.df.format(gu.getChange() / 1000000000000l) + "万亿")
					+ "  " + (gu.getP_change() > 0 ? "+" : "") + Util.df.format(gu.getP_change()) + "%");
		} else if (Math.abs(gu.getChange()) >= 10000000) {
			change.setText((gu.getChange() > 0 ? "+" : "") + (Util.df.format(gu.getChange() / 100000000) + "亿") + "  "
					+ (gu.getP_change() > 0 ? "+" : "") + Util.df.format(gu.getP_change()) + "%");
		} else if (Math.abs(gu.getChange()) >= 1) {
			change.setText((gu.getChange() > 0 ? "+" : "") + gu.getChange() + "  " + (gu.getP_change() > 0 ? "+" : "")
					+ Util.df.format(gu.getP_change()) + "%");
		} else {
			change.setText((gu.getChange() > 0 ? "+" : "") + Util.getUsedNum(gu.getChange(), 2) + "  "
					+ (gu.getP_change() > 0 ? "+" : "") + Util.getUsedNum(gu.getP_change(), 2) + "%");
		}
		// v.setBackgroundResource(gu.getChange() >= 0 ? R.drawable.img_red:
		// R.drawable.img_green);
		now.setTextColor(gu.getChange() >= 0 ? ColorUtil.red : ColorUtil.green);
		change.setTextColor(gu.getChange() >= 0 ? ColorUtil.red : ColorUtil.green);
		bj.setBackgroundResource(gu.getChange() > 0 ? R.drawable.img_hong1 : R.drawable.img_lv1);
	}

	public void showChange() {
		// bj.startAnimation(AnimationUtils.loadAnimation(activity,
		// R.anim.alphaanim));
		if (bj.getVisibility() == View.GONE) {
			bj.setVisibility(View.VISIBLE);
			activity.mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					bj.setVisibility(View.GONE);
				}
			}, 1000);
		}
	}

}
