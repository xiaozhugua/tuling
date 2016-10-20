package com.abct.tljr.ui.activity.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.dialog.BuildEditDialog;
import com.abct.tljr.hangqing.util.Menu.BinarySlidingMenu;
import com.abct.tljr.hangqing.util.Menu.BinarySlidingMenu.OnMenuOpenListener;
import com.abct.tljr.kline.MyValueFormatter;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.widget.XScrollView;
import com.abct.tljr.utils.Util;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.qh.common.listener.Complete;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年11月14日 下午2:59:07
 */
public class BuildActivity extends BaseActivity implements OnClickListener {
	private BinarySlidingMenu slidingMenu;
	public static final String url = UrlUtil.Url_apicavacn + "tools/index/0.2";
	public static Map<String, String> buildType = new HashMap<String, String>();
	private String OneBuildID;
	private String selfId;
	private String[] type = { "+", "-", "×", "÷" };
	// private JSONObject[] buildType;
	private TextView name, tip;
	private ListView lv;
	private XScrollView mScrollView = null;
	private ArrayList<OneBuild> list = new ArrayList<OneBuild>();
	private JSONArray datas;
	private View menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		OneBuildID = getIntent().getStringExtra("id");
		selfId = MyApplication.getInstance().self.getId();
		slidingMenu = (BinarySlidingMenu) View.inflate(this,
				R.layout.tljr_activity_build, null);
		setContentView(slidingMenu);
		name = (TextView) findViewById(R.id.name);
		tip = (TextView) findViewById(R.id.btip);
		menu = findViewById(R.id.menu);
		((LayoutParams) menu.getLayoutParams()).setMargins(-30
				* Util.WIDTH / Util.IMAGEWIDTH, 0, 0, 0);
		// 下滑刷新
		mScrollView = (XScrollView) findViewById(R.id.scrollview);
		mScrollView.initWithContext(this);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(false);
		mScrollView.setAutoLoadEnable(false);
		mScrollView.setRefreshTime(Util.getNowTime());
		// 下滑刷新
		mScrollView.setIXScrollViewListener(new XScrollView.IXScrollViewListener() {
			@Override
			public void onRefresh() {
				getBuildType(new Complete() {

					@Override
					public void complete() {
						getMyList();
					}
				});
			}

			@Override
			public void onLoadMore() {
			}
		});
		lv = (ListView) findViewById(R.id.tljr_grp_build_layout);
		lv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					return true;

				default:
					break;
				}
				return false;
			}
		});
		lv.setAdapter(adapter);
		((ImageView) slidingMenu.findViewById(R.id.tljr_view_image))
				.setVisibility(View.INVISIBLE);
		findViewById(R.id.tljr_img_build_back).setOnClickListener(this);
		findViewById(R.id.tljr_img_edit).setOnClickListener(this);
		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.delete).setOnClickListener(this);
		findViewById(R.id.modify).setOnClickListener(this);
		findViewById(R.id.tip).setOnClickListener(this);
		initItem();
		getBuildType(new Complete() {

			@Override
			public void complete() {
				getMyList();
			}
		});

		slidingMenu.setOnMenuOpenListener(new OnMenuOpenListener() {
			@Override
			public void onMenuOpen(boolean isOpen, int flag) {
				if (isOpen) {
					// 关闭
					((ImageView) slidingMenu.findViewById(R.id.tljr_view_image))
							.setVisibility(View.INVISIBLE);
				} else {
					// 张开
					((ImageView) slidingMenu.findViewById(R.id.tljr_view_image))
							.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	private void getMyList() {
		ProgressDlgUtil.showProgressDlg("", this);
		HttpRequest.sendPost(url + "/myindex/" + selfId + "/" + OneBuildID
				+ "/info","", new HttpRevMsg() {
			@Override
			public void revMsg(final String msg) {
				LogUtil.e("getMyList", msg);
				ProgressDlgUtil.stopProgressDlg();
				post(new Runnable() {

					@Override
					public void run() {
						try {
							JSONObject jsonObject = new JSONObject(msg)
									.getJSONObject("result");
							name.setText(jsonObject.optString("name"));
							tip.setText(jsonObject.optString("detail"));
							JSONArray array = jsonObject.getJSONArray("list");
							list.clear();
							for (int i = 0; i < array.length(); i++) {
								editOneBuild(null, array.getString(i), false);
							}
							initUi();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	private void showAddDialog() {
		final View view = getOneView();
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("请填写配置信息")
				.setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						OneBuild build = (OneBuild) view.getTag();
						if (build.getName() == null || build.getValue() == 0) {
							// 条件不成立不能关闭 AlertDialog 窗口
							try {
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, false);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (build.getName() == null) {
							showToast("请选择添加的股票");
							return;
						}
						if (build.getValue() == 0) {
							showToast("请输入系数值");
							return;
						}
						addBuild(build);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).show();
		dialog.setCanceledOnTouchOutside(false);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		p.height = (int) (Util.HEIGHT / 3);
		dialogWindow.setAttributes(p);
	}

	private void addBuild(final OneBuild build) {
		LogUtil.e(
				"addBuilda",
				url + "/myindex/" + selfId + "/" + OneBuildID + "/addSubItem?"
						+ "operation=" + build.getType() + "&coefficient="
						+ build.getValue() + "&productId="
						+ build.getProductId());
		HttpRequest.sendPost(
				url + "/myindex/" + selfId + "/" + OneBuildID + "/addSubItem",
				"operation=" + build.getType() + "&coefficient="
						+ build.getValue() + "&productId="
						+ build.getProductId(), new HttpRevMsg() {

					@Override
					public void revMsg(final String msg) {
						LogUtil.e("addBuild", msg);
						post(new Runnable() {

							@Override
							public void run() {
								try {
									editOneBuild(null, new JSONObject(msg)
											.getString("result"), true);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
				});
	}

	private void editOneBuild(OneBuild gu, String msg, boolean isInit) {
		try {
			if (gu == null) {
				gu = new OneBuild();
				list.add(gu);
			}
			JSONObject object = new JSONObject(msg);
			gu.setId(object.getString("subItemId"));
			gu.setType(object.getString("operation"));
			gu.setValue((float) object.getDouble("coefficient"));
			JSONObject obj = object.getJSONObject("product");
			gu.setName(obj.getString("name"));
			gu.setCode(obj.getString("code"));
			gu.setBuildType(obj.getJSONObject("type").getString("typeId"));
			gu.setProductId(obj.getString("productId"));
			if (isInit)
				initUi();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void editBuild(final OneBuild build) {
		LogUtil.e("editBuild", url + "/myindex/" + selfId + "/" + OneBuildID
				+ "/editSubItem" + "/" + build.getId() + "?" + "operation="
				+ build.getType() + "&coefficient=" + build.getValue()
				+ "&productId=" + build.getProductId());
		HttpRequest.sendPost(
				url + "/myindex/" + selfId + "/" + OneBuildID + "/editSubItem"
						+ "/" + build.getId(),
				"operation=" + build.getType() + "&coefficient="
						+ build.getValue() + "&productId="
						+ build.getProductId(), new HttpRevMsg() {

					@Override
					public void revMsg(final String msg) {
						LogUtil.e("editBuild", msg);
						post(new Runnable() {

							@Override
							public void run() {
								try {
									editOneBuild(build, new JSONObject(msg)
											.getString("result"), true);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
				});
	}

	private void initUi() {
		adapter.notifyDataSetChanged();
		ViewGroup.LayoutParams params = lv.getLayoutParams();
		params.height = Util.HEIGHT / 10 * list.size();
		lv.setLayoutParams(params);
		getLineData();
		postDelayed(new Runnable() {

			@Override
			public void run() {
				slidingMenu.hideRight();
			}
		}, 1000);
	}

	private View view;

	private View getOneView() {
		view = null;
		view = View.inflate(this, R.layout.tljr_item_build_edit, null);
		OneBuild gu = new OneBuild();
		view.setTag(gu);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				Util.HEIGHT / 10);
		view.setLayoutParams(params);
		view.findViewById(R.id.tljr_build_edit_type).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showPopWindow(v, type, new HttpRevMsg() {

							@Override
							public void revMsg(String msg) {
								OneBuild gu = (OneBuild) view.getTag();
								((TextView) view
										.findViewById(R.id.tljr_build_type))
										.setText(msg);
								gu.setType(msg);
							}
						});
					}
				});
		((EditText) view.findViewById(R.id.tljr_build_edit_et_type))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						OneBuild gu = (OneBuild) view.getTag();
						String a = s.toString();
						if (a.length() > 0 && !a.equals(".")) {
							gu.setValue(Float.parseFloat(a.toString()));
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
		view.findViewById(R.id.tljr_build_edit_grp_type).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						String[] a = new String[buildType.size()];
						Iterator<String> it = buildType.values().iterator();
						int count = 0;
						while (it.hasNext()) {
							a[count] = it.next();
							count++;
						}
						showPopWindow(v, a, new HttpRevMsg() {

							@Override
							public void revMsg(String msg) {
								OneBuild gu = (OneBuild) view.getTag();
								((TextView) view
										.findViewById(R.id.tljr_build_edit_code))
										.setText(msg);
								Iterator<String> it = buildType.keySet()
										.iterator();
								while (it.hasNext()) {
									String key = it.next();
									if (buildType.get(key).equals(msg)) {
										gu.setBuildType(key);
									}
								}
							}
						});
					}
				});
		view.findViewById(R.id.tljr_build_edit_grp_name).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(final View v) {
						final OneBuild gu = (OneBuild) view.getTag();
						if (gu.getBuildType() == null) {
							showToast("请先选择类型");
							return;
						}
						new OnlySearchDialog(BuildActivity.this,
								new HttpRevMsg() {
									@Override
									public void revMsg(final String msg) {
										post(new Runnable() {

											@Override
											public void run() {
												try {
													JSONObject object = new JSONObject(
															msg);
													gu.setName(object
															.getString("name"));
													gu.setProductId(object
															.getString("id"));
													((TextView) view
															.findViewById(R.id.tljr_build_edit_name))
															.setText(gu
																	.getName());
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										});
									}
								}, slidingMenu, gu.getBuildType()).show(null);
					}
				});
		return view;
	}

	private void getLineData() {
		LogUtil.e("getLineData", url + "/myindex/" + selfId
				+ "/" + OneBuildID + "/detail");
		HttpRequest.sendPost(url + "/myindex/" + selfId + "/" + OneBuildID
				+ "/detail", "", new HttpRevMsg() {

			@Override
			public void revMsg(String msg) {
				try {
					datas = new JSONObject(msg).getJSONObject("result")
							.getJSONArray("result");
					if (datas.length() == 0) {
						postDelayed(new Runnable() {

							@Override
							public void run() {
								getLineData();
							}
						}, 5000);
					} else {
						showLineChart(holder.lineChart);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					postDelayed(new Runnable() {

						@Override
						public void run() {
							getLineData();
						}
					}, 5000);
				}
			}
		});
	}

	private void showLineChart(final LineChart chart) {
		post(new Runnable() {

			@Override
			public void run() {
				chart.getXAxis().setTextSize(8f);
				findViewById(R.id.tljr_grp_line_layout).setVisibility(
						datas.length() == 0 ? View.GONE : View.VISIBLE);
				if (datas.length() == 0) {
					return;
				}
				holder.change.setText(Util.df.format(datas.optJSONObject(
						datas.length() - 1).optDouble("last"))+"点");
				if (datas.optJSONObject(datas.length() - 1).optDouble("close") >= 0) {
					findViewById(R.id.tljr_item_build_title)
							.setBackgroundColor(
									getResources().getColor(
											R.color.tljr_hq_zx_up));
				} else {
					findViewById(R.id.tljr_item_build_title)
							.setBackgroundColor(
									getResources().getColor(
											R.color.tljr_hq_zx_down));
				}
				int prog = datas.length() - 1;

				ArrayList<String> xVals = new ArrayList<String>();
				// xVals = BasisActivity
				// .getXVals(datas.optJSONObject(0).optLong("time"),
				// datas.optJSONObject(datas.length() - 1)
				// .optLong("time"), false);
				// for (int i = 0; i < prog; i++) {
				// xVals.add(datas.get(i).getDate());
				// }
				ArrayList<Entry> vals1 = new ArrayList<Entry>();
				ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
				for (int i = 0; i < prog; i++) {
					xVals.add(Util.getDateOnlyHour(datas.optJSONObject(i)
							.optLong("time")));
					vals1.add(new Entry((float) (datas.optJSONObject(i)
							.optDouble("last")), i));
					yVals2.add(new BarEntry(0, i, Color.rgb(255, 70, 41)));
				}
				if (vals1.size() > xVals.size()) {
					showToast("服务器数据错误!");
					return;
				}
				ValueFormatter formatter = new MyValueFormatter(4);
				chart.getAxisLeft().setValueFormatter(formatter);
				LineDataSet set1 = new LineDataSet(vals1, "Data Set 1");
				set1.setDrawCubic(true);
				// set1.setCubicIntensity(0.2f * xVals.size() / vals1.size());
				set1.setDrawFilled(true);
				set1.setDrawCircles(false);
				set1.setLineWidth(1f);
				// set1.setCircleSize(5f);
				set1.setHighLightColor(Color.rgb(244, 117, 117));
				set1.setColor(Color.rgb(78, 128, 172));
				set1.setFillColor(ColorTemplate.getHoloBlue());

				LineData data = new LineData(xVals, set1);
				data.setValueTextSize(9f);
				data.setDrawValues(false);
				chart.setData(data);
				chart.invalidate();

			}
		});
	}

	private ChartViewHolder holder;

	private void initItem() {
		holder = new ChartViewHolder();
		holder.change = (TextView) findViewById(R.id.tljr_item_build_change);
		holder.lineChart = (LineChart) findViewById(R.id.tljr_item_build_chart1);
		initLineChart(holder.lineChart);
		holder.fiveLineChart = (LineChart) findViewById(R.id.tljr_item_build_chart2);
		holder.stickChart = (CandleStickChart) findViewById(R.id.tljr_item_build_chart3);
		RadioGroup rg = (RadioGroup) findViewById(R.id.tljr_item_build_rg);
		final View check = findViewById(R.id.tljr_item_build_arrow);
		tabChangedArrow(check, 0);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tljr_item_build_rb1:
					holder.lineChart.setVisibility(View.VISIBLE);
					holder.fiveLineChart.setVisibility(View.GONE);
					holder.stickChart.setVisibility(View.GONE);
					tabChangedArrow(check, 0);
					break;
				case R.id.tljr_item_build_rb2:
					holder.lineChart.setVisibility(View.GONE);
					holder.fiveLineChart.setVisibility(View.VISIBLE);
					holder.stickChart.setVisibility(View.GONE);
					tabChangedArrow(check, 1);
					break;
				case R.id.tljr_item_build_rb3:
					holder.lineChart.setVisibility(View.GONE);
					holder.fiveLineChart.setVisibility(View.GONE);
					holder.stickChart.setVisibility(View.VISIBLE);
					tabChangedArrow(check, 2);
					break;
				default:
					break;
				}
			}
		});
	}

	private void tabChangedArrow(View v, int to) {
		if (v.getTag() == null)
			initImageView(v);
		int from = (Integer) v.getTag();
		int offset = Util.WIDTH / 3 - 10;
		int offsetX = Util.WIDTH / 6 - 20 / 2 + 20;
		Animation animation = null;
		animation = new TranslateAnimation(from * offset + offsetX, to * offset
				+ offsetX, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(200);
		v.startAnimation(animation);
		v.setTag(to);
	}

	private void initImageView(View cursor) {
		cursor.getLayoutParams().width = 20;
		cursor.setTag(1);
	}

	static class ChartViewHolder {
		TextView change;
		LineChart lineChart;
		LineChart fiveLineChart;
		CandleStickChart stickChart;
	}

	private void initLineChart(LineChart chart) {
		chart.setDescription("");
		chart.setMaxVisibleValueCount(60);
		chart.setPinchZoom(false);

		XAxis xAxis = chart.getXAxis();
		xAxis.setAdjustXLabels(true);
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setSpaceBetweenLabels(2);
		xAxis.setDrawGridLines(true);
		xAxis.setTextColor(Color.rgb(139, 148, 153));

		YAxis leftAxis = chart.getAxisLeft();
		leftAxis.setLabelCount(5);
		leftAxis.setStartAtZero(false);
		leftAxis.setTextColor(Color.rgb(139, 148, 153));
		chart.setDrawGridBackground(false);
		chart.getLegend().setEnabled(false);
		chart.getAxisRight().setEnabled(false);
	}

	// 获取类型列表
	private void getBuildType(final Complete... complete) {
		ProgressDlgUtil.showProgressDlg("", this);
		if (buildType.size() > 0) {
			ProgressDlgUtil.stopProgressDlg();
			for (Complete c : complete) {
				c.complete();
			}
			return;
		}
		HttpRequest.sendPost(url + "/type/list", "", new HttpRevMsg() {

			@Override
			public void revMsg(final String msg) {
				LogUtil.e("getBuildType", msg);
				ProgressDlgUtil.stopProgressDlg();
				post(new Runnable() {

					@Override
					public void run() {
						try {
							buildType.clear();
							JSONArray array = new JSONObject(msg)
									.getJSONArray("result");
							if (array.length() > 0) {
								for (int i = 0; i < array.length(); i++) {
									buildType
											.put(array.getJSONObject(i)
													.optString("typeId"), array
													.getJSONObject(i)
													.optString("name"));
								}
								for (Complete c : complete) {
									c.complete();
								}
							} else {
								showToast("获取服务器类型失败,请重试!");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (view == null) {
				view = View.inflate(BuildActivity.this,
						R.layout.tljr_item_build_edit, null);
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(
						AbsListView.LayoutParams.FILL_PARENT, Util.HEIGHT / 10);
				view.setLayoutParams(params);
				holder = new ViewHolder();
				view.findViewById(R.id.tljr_build_edit_cb_type).setVisibility(
						View.GONE);
				holder.type = (TextView) view
						.findViewById(R.id.tljr_build_type);
				holder.value = (TextView) view
						.findViewById(R.id.tljr_build_edit_et_type);
				holder.buildType = (TextView) view
						.findViewById(R.id.tljr_build_edit_code);
				holder.name = (TextView) view
						.findViewById(R.id.tljr_build_edit_name);
				holder.value.setEnabled(false);
				view.findViewById(R.id.tljr_build_edit_type)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								showEditDialog(v, position);
							}
						});
				view.findViewById(R.id.tljr_build_edit_et_type)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								showEditDialog(v, position);
							}
						});
				view.findViewById(R.id.tljr_build_edit_grp_type)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								showEditDialog(v, position);
							}
						});
				view.findViewById(R.id.tljr_build_edit_grp_name)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								showEditDialog(v, position);
							}
						});
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			OneBuild gu = list.get(position);
			holder.type.setText(gu.getType());
			holder.value.setText(gu.getValue() + "");
			holder.buildType.setText(gu.getBuildType());
			holder.name.setText(gu.getName());
			if (BuildActivity.buildType.containsKey(gu.getBuildType()))
				holder.buildType.setText(BuildActivity.buildType.get(gu
						.getBuildType()));
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
	};

	private void showEditDialog(final View v, final int position) {
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		final int y = location[1];
		mScrollView.scrollTo(0, y
				- findViewById(R.id.tljr_grp_build_title).getMeasuredHeight()
				- Util.HEIGHT / 20);
		postDelayed(new Runnable() {

			@Override
			public void run() {
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				new BuildEditDialog(BuildActivity.this, slidingMenu).show(
						location[1], list.get(position));
			}
		}, 10);
	}

	static class ViewHolder {
		TextView type, value, buildType, name;
	}

	/**
	 * @param v
	 * @param list
	 * @param msg
	 */
	private void showPopWindow(View v, String[] list, final HttpRevMsg msg) {
		ScrollView scroll = new ScrollView(this);
		LinearLayout layout = new LinearLayout(this);
		scroll.addView(layout);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(getResources().getColor(R.color.tljr_white));
		final PopupWindow window = new PopupWindow(scroll,
				v.getMeasuredWidth(), LayoutParams.WRAP_CONTENT, true);
		window.setBackgroundDrawable(new BitmapDrawable());
		window.setOutsideTouchable(true);
		window.showAsDropDown(v);
		for (int i = 0; i < list.length; i++) {
			TextView view = new TextView(this);
			view.setText(list[i]);
			view.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, Util.HEIGHT / 20));
			view.setGravity(Gravity.CENTER);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (window != null)
						window.dismiss();
					msg.revMsg(((TextView) v).getText().toString());
				}
			});
			layout.addView(view);
			View view2 = new View(this);
			view2.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, 1));
			view2.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.img_222fengexian));
			layout.addView(view2);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tljr_img_build_back:
			finish();
			break;
		case R.id.tljr_img_edit:
			slidingMenu.showRight();
			break;
		case R.id.add:
			slidingMenu.hideRight();
			getBuildType(new Complete() {

				@Override
				public void complete() {
					showAddDialog();
				}
			});
			break;
		case R.id.delete:
			slidingMenu.hideRight();
			startActivity(new Intent(this, DeleteBuildActivity.class)
					.putExtra("id", OneBuildID)
					.putExtra("name", name.getText().toString())
					.putExtra("btip", tip.getText().toString()));
			break;
		case R.id.modify:
			Edit("editName", "name");
			slidingMenu.hideRight();
			break;
		case R.id.tip:
			Edit("editDetail", "detail");
			slidingMenu.hideRight();
			break;
		default:
			break;
		}
	}

	private void Edit(final String type, final String value) {
		final EditText et = new EditText(this);
		et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
		new AlertDialog.Builder(this).setView(et).setTitle("请输入(最多10字)")
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						HttpRequest.sendPost(url + "/myindex/" + selfId + "/"
								+ OneBuildID + "/" + type, value + "="
								+ et.getText().toString(), new HttpRevMsg() {

							@Override
							public void revMsg(final String msg) {
								ProgressDlgUtil.stopProgressDlg();
								post(new Runnable() {

									@Override
									public void run() {
										try {
											JSONObject object = new JSONObject(
													msg)
													.getJSONObject("result");
											name.setText(object
													.optString("name"));
											tip.setText(object
													.optString("detail"));
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});
							}
						});
					}
				}).show();

	}

	public static boolean isEdit = false;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isEdit) {
			getMyList();
			isEdit = false;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		list = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK && event != null
		// && event.getRepeatCount() == 0) {
		// return true;
		// }
		return super.onKeyDown(keyCode, event);
	}
}
