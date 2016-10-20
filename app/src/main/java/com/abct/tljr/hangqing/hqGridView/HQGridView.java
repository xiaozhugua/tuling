package com.abct.tljr.hangqing.hqGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.hangqing.HangQing;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.XScrollView;
import com.abct.tljr.ui.widget.XScrollView.IXScrollViewListener;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015-6-4 下午5:21:32
 */
public class HQGridView extends Fragment {
	private HangQing hangQing;
	private MainActivity activity;
	private Map<String, OneGrid> gridsMap = new HashMap<String, OneGrid>();
	private int freshTime = Constant.FlushTime;
	private View guzhi;
	public boolean isInit = false;
	private LinearLayout grp;
	public static ArrayList<OneGrid> guList = null;
	public static LinearLayout v;
	private XScrollView mScrollView = null;

	public View getView() {
		return guzhi;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.activity = (MainActivity) getActivity();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		guzhi = inflater.inflate(R.layout.tljr_page_guzhi, container, false);
		grp = (LinearLayout) guzhi.findViewById(R.id.tljr_page_guzhi_grp);
		// 下拉刷新
		mScrollView = (XScrollView) guzhi.findViewById(R.id.tljr_scrollView1);
		mScrollView.initWithContext(activity);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(false);
		mScrollView.setAutoLoadEnable(false);
		mScrollView.setRefreshTime(Util.getNowTime());
		mScrollView.setIXScrollViewListener(new IXScrollViewListener() {
			@Override
			public void onRefresh() {
				try {
					reflushDP(false);
					mScrollView.stopRefresh();
				} catch (Exception e) {
					mScrollView.stopRefresh();
				}
			}

			@Override
			public void onLoadMore() {
				mScrollView.stopRefresh();
			}
		});
		getGridFormNet();

		return guzhi;
	}

	private void getGridFormNet() {
		HttpRequest.sendGet(UrlUtil.Url_apicavacn + "tools/index/0.2/mainline", new HttpRevMsg() {
			@Override
			public void revMsg(final String msg) {
				LogUtil.e("getGridFormNet", msg);
				if (msg.equals("")) {
					activity.postDelayed(new Runnable() {
						@Override
						public void run() {
							getGridFormNet();
						}
					}, 1000);
					return;
				}
				try {
					org.json.JSONObject obj = new org.json.JSONObject(msg);
					if (obj.getInt("code") == 200) {
						Constant.guMap.clear();
						org.json.JSONArray array = obj.getJSONArray("result");
						Constant.names = new String[array.length()];
						for (int i = 0; i < array.length(); i++) {
							org.json.JSONObject object = array.getJSONObject(i);
							String name = object.getString("name");
							Constant.names[i] = name;
							ArrayList<OneGu> list = new ArrayList<OneGu>();
							JSONArray guarr = object.getJSONArray("items");
							for (int j = 0; j < guarr.length(); j++) {
								JSONObject guj = guarr.getJSONObject(j).getJSONObject("product");
								OneGu gu = new OneGu();
								gu.setCode(guj.getString("code"));
								gu.setMarket(guj.getJSONObject("type").getString("market").toLowerCase());
								gu.setName(guj.getString("name"));
								gu.setPyName(guj.optString("simaple"));
								gu.setKey(gu.getMarket() + gu.getCode());
								LogUtil.e("Add", gu.getName());
								list.add(gu);
							}
							LogUtil.e("Add" + name, list.size() + "");
							Constant.guMap.put(name, list);
						}
						Constant.preference.edit().putString(Constant.GUKEY, Constant.getStringByObject(Constant.guMap))
								.commit();
						if (activity != null) {
							activity.mHandler.sendEmptyMessage(102);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	private int[] background = { R.drawable.img_dian1, R.drawable.img_dian2, R.drawable.img_dian3,
			R.drawable.img_dian4 };

	public void freshGrid() {
		// Log.e("HQGridView", "freshGrid");
		isInit = true;
		gridsMap.clear();
		grp.removeAllViews();
		int m = 0;
		for (int i = 0; i < Constant.names.length; i++) {
			String name = Constant.names[i];
			View v;
			if (i == 0) {
				v = View.inflate(activity, R.layout.tljr_item_gridview1, null);
				GridView gv = (GridView) v.findViewById(R.id.gridview_content);
				gv.setTag(2);
				initOneGrid(gv, Constant.guMap.get(name).subList(0, 2));
				GridView gv1 = (GridView) v.findViewById(R.id.gridview_content1);
				gv1.setTag(4);
				initOneGrid(gv1, Constant.guMap.get(name).subList(2, Constant.guMap.get(name).size()));
			} else {
				v = View.inflate(activity, R.layout.tljr_item_gridview, null);
				GridView gv = (GridView) v.findViewById(R.id.gridview_content);
				gv.setTag(3);
				initOneGrid(gv, Constant.guMap.get(name));
			}
			grp.addView(v);
			TextView tv = (TextView) v.findViewById(R.id.tljr_view_title_name);
			tv.setText(name);
			if (m > 3) {
				m = 0;
			}
			((ImageView) v.findViewById(R.id.tljr_view_title_img)).setBackgroundResource(background[m]);
			m++;
		}
		reflushDP(false);
	}

	private void initOneGrid(GridView gridView, List<OneGu> list) {
		if (null == list || list.size() == 0) {
			return;
		}
		int numcount = (Integer) gridView.getTag();
		guList = new ArrayList<OneGrid>();
		for (int i = 0; i < list.size(); i++) {
			OneGrid grid = new OneGrid(list.get(i), hangQing.activity, numcount);
			initAppLine(grid.getView(), i);
			guList.add(grid);
			gridsMap.put(grid.getKey(), grid);
		}
		setAdapter(gridView, guList);
		LogUtil.e("initOneGrid", numcount + "");
		int height = Util.dp2px(activity, numcount == 2 ? 100 : 80);
		int num = (list.size() / numcount + (list.size() % numcount == 0 ? 0 : 1));
		LayoutParams params = gridView.getLayoutParams();
		params.height = num * height;
		gridView.setLayoutParams(params);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.invalidate();
	}

	private void initAppLine(View view, int i) {
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.tljr_grp_app_di);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
		switch (i % 3) {
		case 0:
			params.setMargins(0, 0, 1, 1);
			break;
		case 1:
			params.setMargins(0, 0, 1, 1);
			break;
		case 2:
			params.setMargins(0, 0, 0, 1);
			break;
		default:
			break;
		}
	}

	public void setAdapter(GridView gridView, final ArrayList<OneGrid> list) {
		gridView.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				return list.get(arg0).getView();
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Object getItem(int arg0) {
				return list.get(arg0);
			}

			@Override
			public int getCount() {
				return list.size();
			}
		});

		// gridView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// LogUtil.e("行情点击", list.get(arg2).getGu().getName());
		// Intent intent = new Intent(hangQing.activity,
		// OneGuActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putString("code", list.get(arg2).getGu().getCode());
		// bundle.putString("name", list.get(arg2).getGu().getName());
		// bundle.putString("market", list.get(arg2).getGu().getMarket());
		// bundle.putString("key", list.get(arg2).getGu().getKey());
		// bundle.putSerializable("onegu", list.get(arg2).getGu());
		// bundle.putInt("type", 1);
		// intent.putExtras(bundle);
		// hangQing.activity.startActivity(intent);
		// }
		// });
	}

	public void reflushDP(boolean... isWait) {
		if (isWait.length > 0)
			freshTime = Constant.FlushTime;
		freshTime++;
		if (freshTime < (Constant.netType.equals("WIFI") ? 5
				: ((Constant.FlushTime == 0 ? 9999 : Constant.FlushTime)))) {
			return;
		}
		if (gridsMap.isEmpty()) {
			return;
		}
		freshTime = 0;
		int m = 0;
		String parm = "list=";
		for (OneGrid grid : gridsMap.values()) {
			if (m == 0) {
				parm += (grid.getGu().getMarket() + "|" + grid.getGu().getCode());
			} else {
				parm += ("," + grid.getGu().getMarket() + "|" + grid.getGu().getCode());
			}
			m++;
		}
		LogUtil.e("freshGridMapDataparm", parm);
		Util.getRealInfo(parm, new NetResult() {
			@Override
			public void result(final String msg) {
				freshGridMapData(msg);
			}
		}, true);
	}

	private void freshGridMapData(final String msg) {
		try {
			final org.json.JSONObject object = new org.json.JSONObject(msg);
			if (object.getInt("code") != 1) {
				return;
			}
			hangQing.activity.post(new Runnable() {
				@Override
				public void run() {
					try {
						org.json.JSONArray arr = object.getJSONArray("result");
						for (int i = 0; i < arr.length(); i++) {
							JSONObject obj = arr.getJSONObject(i);
							String key = obj.getString("market") + obj.getString("code");
							if (gridsMap.containsKey(key)) {
								OneGrid grid = gridsMap.get(key);
								OneGu gu = grid.getGu();
								JSONArray array = obj.getJSONArray("data");
								gu.setChange((float) array.optDouble(8, 0));
								gu.setP_change((float) array.optDouble(9, 0));
								// if (gu.getNow() != 0 && gu.getNow() !=
								// array.optDouble(0)) {
								// grid.showChange();
								// }
								gu.setNow(array.optDouble(0));
								grid.initOneGu(gu);
							}
						}
						mScrollView.stopRefresh();
					} catch (org.json.JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (org.json.JSONException e) {
			e.printStackTrace();
		}
	}

}
