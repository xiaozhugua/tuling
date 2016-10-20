package com.abct.tljr.news.mark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年9月7日 下午4:21:41
 */
public class MarkView extends ListView {
	private boolean isInit = false;
	private NewsMarkActivity activity;
	private boolean isAll;
	private Map<String, OneMark> map = new HashMap<String, OneMark>();
	private Map<String, View> viewMap = new HashMap<String, View>();
	private ArrayList<ArrayList<OneMark>> list = new ArrayList<ArrayList<OneMark>>();

	public MarkView(NewsMarkActivity context, boolean isAll) {
		super(context);
		this.activity = context;
		this.isAll = isAll;
		setDividerHeight(0);
		setAdapter(adapter);
	}

	public NewsMarkActivity getActivity() {
		return activity;
	}

	public boolean isMark(String id) {
		return map.containsKey(id);
	}

	public void getData() {
		if (isInit) {
			return;
		}
		String uid = PreferenceUtils.getInstance().preferences.getString("UserId", "0");
		isInit = true;
		ProgressDlgUtil.showProgressDlg("", activity);
		String url = UrlUtil.URL_new + "api/subscribe/" + (isAll ? "all" : "my");
		String param = "platform=1&" + "uid=" + uid + "&version=" + HuanQiuShiShi.version;
		LogUtil.i("newsmark", url + "?" + param);
		NetUtil.sendPost(url, param, new NetResult() {

			@Override
			public void result(String msg) {
				// TODO Auto-generated method stub
				LogUtil.i("newsmark", msg);
				try {

					JSONObject jsonObject = new JSONObject(msg).getJSONObject("joData");

					JSONArray array = jsonObject.getJSONArray("data");

					map.clear();

					for (int i = 0; i < array.length(); i++) {

						JSONObject object = array.getJSONObject(i);
						OneMark mark = new OneMark();
						mark.setId(object.getString("id"));
						mark.setMark(isAll ? object.optBoolean("isSubscribe") : true);
						mark.setName(object.optString("name"));
						mark.setAvatar(object.optString("purl"));
						mark.setInfo(object.optString("summary"));
						mark.setNumber(object.optString("wxh"));
						if (!isAll) {
							LogUtil.i("MarkView", "添加：" + i + object.optString("name"));
						}
						map.put(mark.getId(), mark);
					}

					activity.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							list = sort();
							adapter.notifyDataSetChanged();
							ProgressDlgUtil.stopProgressDlg();
							if (!isAll && activity.viewpager.getCurrentItem() == 0)
								activity.noMark.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
						}
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					activity.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ProgressDlgUtil.stopProgressDlg();
							if (!isAll && activity.viewpager.getCurrentItem() == 0)
								activity.noMark.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
						}
					});
				}
			}
		});
	}

	public void changeMark(OneMark mark, boolean isMark) {


		if (isMark) {
			map.put(mark.getId(), mark);
		} else {
			map.remove(mark.getId());
		}
		activity.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				isInit=false;
				list = sort();
				adapter.notifyDataSetChanged();
				if (!isAll && activity.viewpager.getCurrentItem() == 0)
					activity.noMark.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
			}
		});
	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				v = View.inflate(activity, R.layout.tljr_view_news_mark, null);
				v.setTag(holder);
				holder.grp =  (CustomListView) v.findViewById(R.id.tljr_view_item_newsmark_grp);
				holder.name = (TextView) v.findViewById(R.id.tljr_view_item_newsmark_name);
				holder.arrow = (ImageView) v.findViewById(R.id.tljr_view_item_newsmark_arrow);
                //chagne x
				holder.arrow.setRotation(90);
				holder.newsMark = v.findViewById(R.id.tljr_view_item_newsmark);
				holder.newsMark.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ViewHolder holder = ((ViewHolder) ((View) v.getParent()).getTag());
						holder.arrow.setRotation(holder.arrow.getRotation() == 90 ? -90 : 90);
						holder.grp.setVisibility(holder.arrow.getRotation() == 90 ? View.VISIBLE : View.GONE);
					}
				});
				holder.grp.setAdapter(new MyBaseAdapter(MarkView.this));
			} else {
				holder = (ViewHolder) v.getTag();
			}
			holder.name.setText(String2AlphaFirst(list.get(position).get(0).getName(), "b"));
			((MyBaseAdapter) holder.grp.getAdapter()).setList(list.get(position));
			((MyBaseAdapter) holder.grp.getAdapter()).notifyDataSetChanged();
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
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
	};

	static class ViewHolder {
		View newsMark;// 标题tljr_view_item_newsmark
		TextView name;// 首字母tljr_view_item_newsmark_name
		ImageView arrow;// 箭头tljr_view_item_newsmark_arrow
		CustomListView grp;// 容器tljr_view_item_newsmark_grp
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	// 字母Z使用了两个标签，这里有２７个值
	// i, u, v都不做声母, 跟随前面的字母
	private char[] chartable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌', '挖',
			'昔', '压', '匝', '座' };
	private char[] alphatableb = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z' };
	private char[] alphatables = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z' };
	private int[] table = new int[27]; // 初始化
	{
		for (int i = 0; i < 27; ++i) {
			table[i] = gbValue(chartable[i]);
		}
	}

	// 主函数,输入字符,得到他的声母,
	// 英文字母返回对应的大小写字母
	// 其他非简体汉字返回 '0' 按参数
	public char Char2Alpha(char ch, String type) {
		if (ch >= 'a' && ch <= 'z')
			return (char) (ch - 'a' + 'A');// 为了按字母排序先返回大写字母
		// return ch;
		if (ch >= 'A' && ch <= 'Z')
			return ch;

		int gb = gbValue(ch);
		if (gb < table[0])
			return '0';

		int i;
		for (i = 0; i < 26; ++i) {
			if (match(i, gb))
				break;
		}

		if (i >= 26) {
			return '0';
		} else {
			if ("b".equals(type)) {// 大写
				return alphatableb[i];
			} else {// 小写
				return alphatables[i];
			}
		}
	}

	// 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串
	public String String2Alpha(String SourceStr, String type) {
		String Result = "";
		int StrLength = SourceStr.length();
		int i;
		try {
			for (i = 0; i < StrLength; i++) {
				Result += Char2Alpha(SourceStr.charAt(i), type);
			}
		} catch (Exception e) {
			Result = "";
		}
		return Result;
	}

	// 根据一个包含汉字的字符串返回第一个汉字拼音首字母的字符串
	public String String2AlphaFirst(String SourceStr, String type) {
		String Result = "";
		try {
			Result += Char2Alpha(SourceStr.charAt(0), type);
			if(SourceStr.length()>2 && SourceStr.substring(0,1).equals("荀")){
				return "X";
			}
		} catch (Exception e) {
			Result = "";
		}
		return Result;
	}

	private boolean match(int i, int gb) {
		if (gb < table[i])
			return false;
		int j = i + 1;

		// 字母Z使用了两个标签
		while (j < 26 && (table[j] == table[i]))
			++j;
		if (j == 26)
			return gb <= table[j];
		else
			return gb < table[j];
	}

	// 取出汉字的编码
	private int gbValue(char ch) {
		String str = new String();
		str += ch;
		try {
			byte[] bytes = str.getBytes("GBK");
			if (bytes.length < 2)
				return 0;
			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			return 0;
		}
	}

	public ArrayList sort() {
		ArrayList resultList = new ArrayList();
		String[] alphatableb = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
				"X", "Y", "Z" };
		ArrayList arraylist;
		for (String a : alphatableb) {
			arraylist = new ArrayList();
			for (OneMark mark : map.values()) {
				if (a.equals(String2AlphaFirst(mark.getName().toString(), "b"))) {
					 
					arraylist.add(mark);
				}
			}
			if (arraylist.size() > 0)
				resultList.add(arraylist);
		}
		return resultList;
	}
}