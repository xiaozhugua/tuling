package com.abct.tljr.jiaoyi;

import java.util.ArrayList;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.util.UrlUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class JiaoYi extends Fragment implements OnClickListener {
	private View view;
	private MainActivity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = (MainActivity) getActivity();
		view = getActivity().getLayoutInflater().inflate(
				R.layout.tljr_fragment_change, null);
		// view.findViewById(R.id.xebb).getLayoutParams().height = (int)
		// (Util.WIDTH / 4.5f);
		// view.findViewById(R.id.xejq).getLayoutParams().height = (int)
		// (Util.WIDTH / 4.5f);
		// view.findViewById(R.id.dewj).getLayoutParams().height = (int)
		// (Util.WIDTH / 4.5f);
		// view.findViewById(R.id.dewjus).getLayoutParams().height = (int)
		// (Util.WIDTH / 4.5f);
		view.findViewById(R.id.xebb).setOnClickListener(this);
		view.findViewById(R.id.xejq).setOnClickListener(this);
		view.findViewById(R.id.dewj).setOnClickListener(this);
		view.findViewById(R.id.dewjus).setOnClickListener(this);
		view.findViewById(R.id.futures).setOnClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup p = (ViewGroup) view.getParent();
		if (p != null) {
			p.removeAllViewsInLayout();
		}
		return view;
	}

	private void addGrid() {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
		int numcolumns = 2;
		GridView gv = new GridView(activity);
		gv.setNumColumns(numcolumns);
		gv.setVerticalScrollBarEnabled(false);
		gv.setAdapter(new MyBaseAdapter());
		LinearLayout.LayoutParams params;
		int spacing = Util.WIDTH * 18 / Util.IMAGEWIDTH;
		params = new LinearLayout.LayoutParams(Util.WIDTH, (int) ((Util.dp2px(
				activity, 125) + spacing) * 2) + spacing);
		params.topMargin = Util.dp2px(activity, 20);
		gv.setLayoutParams(params);
		gv.setPadding(spacing, spacing, spacing, spacing);
		gv.setHorizontalSpacing(spacing);
		gv.setVerticalSpacing(spacing);
		layout.addView(gv);
	}

	private String[] imgs = { "img_zhengquankaihu.png", "img_waihuijiaoyi.png",
			"img_qihuokaihu.png", "img_bitebikaihu.png" };
	private int id = R.drawable.img_jijiangtuichu;

	private ArrayList<View> getGridView() {
		ArrayList<View> list = new ArrayList<View>();
		for (int i = 0; i < imgs.length; i++) {
			RelativeLayout v = new RelativeLayout(activity);
			ImageView view = new ImageView(activity);
			RelativeLayout.LayoutParams pa = new RelativeLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			StartActivity.imageLoader.displayImage(UrlUtil.aliyun + "/user/"
					+ imgs[i], view, StartActivity.options);
			v.addView(view, pa);
			// Util.setImage(TLUrl.aliyun + "/user/" + imgs[i], v,
			// activity.handler);
			int spacing = Util.WIDTH * 18 / Util.IMAGEWIDTH;
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					(Util.WIDTH - spacing * 3) / 2, (int) (Util.dp2px(activity,
							125)));
			v.setLayoutParams(params);
			ImageView img = new ImageView(activity);
			img.setImageDrawable(activity.getResources().getDrawable(id));
			RelativeLayout.LayoutParams imgparams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			imgparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			v.addView(img, imgparams);
			list.add(v);
		}
		return list;
	}

	class MyBaseAdapter extends BaseAdapter {
		private ArrayList<View> list;

		public MyBaseAdapter() {
			this.list = getGridView();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.xebb:
			// activity.startActivity(new Intent(activity, ZhlActivity.class));
			Util.startApp(activity, "ZSL.apk");
			break;
		case R.id.xejq:
			Util.startApp(activity, "Occft.apk");
			break;
		case R.id.dewj:
			if (MyApplication.getInstance().self == null) {
				activity.login();
				return;
			}
			// activity.startActivity(new Intent(activity, SimuActivity.class));
			Util.startApp(activity, "YGSM.apk");
			break;
		case R.id.dewjus:
			if (MyApplication.getInstance().self == null) {
				activity.login();
				return;
			}
			// activity.startActivity(new Intent(activity, SimuActivity.class));
			Util.startApp(activity, "YGSM.apk");
			break;
		case R.id.futures:
			Util.startApp(activity, "Futures.apk");
			break;

		default:
			break;
		}
	}

}
