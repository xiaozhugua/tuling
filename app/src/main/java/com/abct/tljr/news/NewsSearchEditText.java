package com.abct.tljr.news;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.utils.Util;
import com.qh.common.util.InputTools;
import com.qh.common.util.LogUtil;

public class NewsSearchEditText {
	EditText et_seach;
	NewsSeachActivity activity;
	ImageView iv_delete;

	RelativeLayout rootView;
	ListView listView;

	public final String Tag = "NewsSeachActivity";
	SearchAdapter searchAdapter;

	View cleanHistroy;

	private View searchHistoryView;

	ArrayList<String> list_all = new ArrayList<String>();
	ArrayList<String> list_temp = new ArrayList<String>();

	public interface StartSearch {
		void search();
	}

	StartSearch startSearch;

	public StartSearch getStartSearch() {
		return startSearch;
	}

	public void setStartSearch(StartSearch startSearch) {
		this.startSearch = startSearch;
	}

	public void saveHistory() {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list_all.size(); i++) {
			sb.append(list_all.get(i) + "#");
		}
		if (sb.length() > 1) {
			sb.delete(sb.length() - 1, sb.length());
		}

		Constant.preference.edit()
				.putString("newsSearchHistroy", sb.toString()).commit(); // 记录搜索记录
	}

	public void getHistory() {

		String newsSearchHistroy = Constant.preference.getString(
				"newsSearchHistroy", "");
		String[] history = newsSearchHistroy.split("#");
		for (int k = 0; k < history.length; k++) {
			if (Util.isEmptyAndSpace(history[k])) {
				return;
			}
			list_all.add(history[k]);
		}

	}

	public NewsSearchEditText(NewsSeachActivity activity,
			RelativeLayout rootView, EditText et_seach, ImageView iv_delete) {
		this.activity = activity;
		this.rootView = rootView;
		this.et_seach = et_seach;
		this.iv_delete = iv_delete;
		init();
		searchHistoryView.setVisibility(View.GONE);
		getHistory();
		cleanHistroy.setVisibility(list_all.size() > 0 ? View.VISIBLE
				: View.GONE);

	}

	public void setHistoryVisiable() {
		searchHistoryView.setVisibility(View.GONE);
	}

	public void init() {

		searchHistoryView = activity.getLayoutInflater().inflate(
				R.layout.tljr_item_news_search_history, null);
		listView = (ListView) searchHistoryView
				.findViewById(R.id.listview_history);

		rootView.addView(searchHistoryView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				String text = (String) ((TextView) view
						.findViewById(R.id.tv_news_search_name)).getText();
				et_seach.setText(text);
				searchHistoryView.setVisibility(View.GONE);
				InputTools.HideKeyboard(et_seach);
				startSearch.search();
			}
		});

		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_seach.getWindowToken(), 0);

		cleanHistroy = (LayoutInflater.from(activity).inflate( // 最新评论
				R.layout.tljr_listview_history_footer, null));
		cleanHistroy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cleanHistroy.setVisibility(View.GONE);
				list_all.clear();
				list_temp.clear();
				searchAdapter.setList(list_temp);
			}
		});

		listView.addFooterView(cleanHistroy);

		et_seach.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(Tag, "et_seach.setOnClickListen");

			}
		});

		et_seach.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				searchHistoryView.setVisibility(View.VISIBLE);

				return false;
			}
		});

		(activity.findViewById(R.id.searchContent))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						searchHistoryView.setVisibility(View.GONE);
					}
				});

		searchAdapter = new SearchAdapter(activity, list_all);

		listView.setAdapter(searchAdapter);

		iv_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				et_seach.setText("");
			}
		});

		et_seach.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int start, int before,
					int count) {
				LogUtil.i(Tag, "arg0:" + arg0 + "-start:" + start + "-before:"
						+ before + "-count:" + count);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				LogUtil.i(Tag, "beforeTextChanged");

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				LogUtil.i(Tag, "afterTextChanged");
				if (et_seach.getText().length() > 0) {
					iv_delete.setVisibility(View.VISIBLE);
				} else {
					iv_delete.setVisibility(View.INVISIBLE);
				}

				list_temp.clear();
				for (int i = 0; i < list_all.size(); i++) {
					if (list_all.get(i).contains(arg0)) {
						list_temp.add(list_all.get(i));
					}
				}

				cleanHistroy.setVisibility(list_temp.size() > 0 ? View.VISIBLE
						: View.GONE);

				searchAdapter.setList(list_temp);

			}
		});

		/*
		 * 软键盘上搜索按钮
		 */
		et_seach.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int actionId,
					KeyEvent arg2) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					startSearch.search();
					searchHistoryView.setVisibility(View.GONE);
					InputTools.HideKeyboard(et_seach);
					addHistrory();
				}
				return false;
			}

		});

	}

	public void addHistrory() {

		if (list_all.contains(et_seach.getText() + "")) {
			return;
		}

		list_all.add(et_seach.getText() + "");
		searchAdapter.setList(list_all);
	}

}

class SearchAdapter extends BaseAdapter {
	ArrayList<String> list = new ArrayList<String>();

	Activity activity;

	public SearchAdapter(Activity activity, ArrayList<String> list) {
		this.activity = activity;
		this.list = list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		String searchKeys = list.get(position);
		if (v == null) {
			v = View.inflate(activity, R.layout.tljr_item_news_search2, null);
			holder = new ViewHolder();
			holder.name = (TextView) v.findViewById(R.id.tv_news_search_name);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.name.setText(searchKeys);
		return v;
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

	static class ViewHolder {
		TextView name;
	}
}
