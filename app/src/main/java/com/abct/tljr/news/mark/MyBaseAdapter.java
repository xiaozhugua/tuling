package com.abct.tljr.news.mark;

import java.util.ArrayList;

import com.abct.tljr.R;
import com.abct.tljr.ui.activity.StartActivity;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年9月8日 下午12:08:31
 */
public class MyBaseAdapter extends BaseAdapter {
	private ArrayList<OneMark> list = new ArrayList<OneMark>();
	private MarkView markView;

	public void setList(ArrayList<OneMark> list) {
		this.list = list;
	}

	public MyBaseAdapter(MarkView view) {
		// TODO Auto-generated constructor stub
		this.markView = view;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder1 holder1;
		if (view == null) {
			holder1 = new ViewHolder1();
			view = View.inflate(markView.getActivity(),
					R.layout.tljr_item_news_mark, null);
			holder1.name = (TextView) view.findViewById(R.id.tljr_rank_name);
			holder1.info = (TextView) view.findViewById(R.id.tljr_rank_info);
			holder1.icon = (ImageView) view.findViewById(R.id.tljr_rank_icon);
			holder1.mark = (CheckBox) view.findViewById(R.id.tljr_rank_mark);

			holder1.mark.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					markView.getActivity().mark(((CheckBox) v).isChecked(),
							(OneMark) v.getTag(), (CheckBox) v);
				}
			});
			holder1.mark
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							buttonView.setText(isChecked ? "已关注" : "+关注");
						}
					});
			view.setTag(holder1);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					OneMarkActivity.mark = (OneMark) ((ViewHolder1) v.getTag()).mark
							.getTag();
					markView.getActivity().startActivity(
							new Intent(markView.getActivity(),
									OneMarkActivity.class));
				}
			});
		} else {
			holder1 = (ViewHolder1) view.getTag();
		}
		OneMark mark = list.get(position);
		holder1.name.setText(mark.getName());
		holder1.info.setText(Html.fromHtml(mark.getInfo()));
		StartActivity.imageLoader.displayImage(mark.getAvatar(), holder1.icon,
				StartActivity.options);
		holder1.mark.setTag(mark);
		holder1.mark.setChecked(mark.isMark());
		return view;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	static class ViewHolder1 {
		TextView name;// 首字母tljr_rank_name
		TextView info;// 箭头tljr_rank_info
		ImageView icon;// 容器tljr_rank_icon
		CheckBox mark;// 订阅tljr_rank_mark
	}
}