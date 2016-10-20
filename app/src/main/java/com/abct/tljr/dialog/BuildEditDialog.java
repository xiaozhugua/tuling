package com.abct.tljr.dialog;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.news.widget.BlurUtils;
import com.abct.tljr.ui.activity.tools.BuildActivity;
import com.abct.tljr.ui.activity.tools.OneBuild;
import com.abct.tljr.ui.activity.tools.OnlySearchDialog;
import com.abct.tljr.utils.Util;
import com.qh.common.login.util.HttpRevMsg;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015-6-2 上午11:04:17
 */
public class BuildEditDialog extends Dialog implements OnClickListener {
	/*
	 * 透明模糊
	 */
	private View overlayLayout;
	private View contentFrame;
	private Bitmap scaled;

	public void setBlurImage() {
		// scaled = null;
		// scaled = BlurUtils.drawViewToBitmap(scaled, contentFrame,
		// contentFrame.getMeasuredWidth(),
		// contentFrame.getMeasuredHeight(), 1, null);
		// Bitmap blured = BlurUtils.apply(context, scaled, 10);
		// Drawable drawable = new BitmapDrawable(blured);
		// overlayLayout.setBackgroundDrawable(drawable);
		scaled = null;
		contentFrame.setDrawingCacheEnabled(true);
		scaled = contentFrame.getDrawingCache();
		Bitmap blured = BlurUtils.apply(context, scaled, 10);
		contentFrame.setDrawingCacheEnabled(false);
		Drawable drawable = new BitmapDrawable(blured);
		overlayLayout.setBackgroundDrawable(drawable);
	}

	private Activity context;
	private OneBuild gu = new OneBuild();
	private String[] type = { "+", "-", "×", "÷" };
	private Handler handler = new Handler();
	private View baseView;
	private boolean hasModify = false;

	public BuildEditDialog(Activity context, View contentFrame) {
		super(context, R.style.dialog);
		this.contentFrame = contentFrame;
		init(context);
	}

	private void init(Activity context) {
		this.context = context;
		baseView = View.inflate(context, R.layout.tljr_dialog_buildedit, null);
		setContentView(baseView);
		setCanceledOnTouchOutside(false);
		Window dialogWindow = getWindow();
		// dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams p = dialogWindow.getAttributes();
		p.height = (int) (this.contentFrame.getMeasuredHeight());
		p.width = (int) (Util.WIDTH);
		dialogWindow.setAttributes(p);
		dialogWindow.setWindowAnimations(R.style.AnimationPreview); // 设置窗口弹出动画
		overlayLayout = findViewById(R.id.bj);
		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.ok).setOnClickListener(this);
		findViewById(R.id.tljr_build_edit_type).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						showPopWindow(v, type, new HttpRevMsg() {

							@Override
							public void revMsg(String msg) {
								((TextView) findViewById(R.id.tljr_build_type))
										.setText(msg);
								if (!gu.getType().equals(msg))
									hasModify = true;
								gu.setType(msg);
							}
						});
					}
				});
		((EditText) findViewById(R.id.tljr_build_edit_et_type))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						String a = s.toString();
						if (a.length() > 0 && !a.equals(".")) {
							if (gu.getValue() != Float.parseFloat(a))
								hasModify = true;
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
		findViewById(R.id.tljr_build_edit_grp_type).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String[] a = new String[BuildActivity.buildType.size()];
						Iterator<String> it = BuildActivity.buildType.values()
								.iterator();
						int count = 0;
						while (it.hasNext()) {
							a[count] = it.next();
							count++;
						}
						showPopWindow(v, a, new HttpRevMsg() {

							@Override
							public void revMsg(String msg) {
								((TextView) findViewById(R.id.tljr_build_edit_code))
										.setText(msg);
								Iterator<String> it = BuildActivity.buildType
										.keySet().iterator();
								while (it.hasNext()) {
									String key = it.next();
									if (BuildActivity.buildType.get(key)
											.equals(msg)) {
										if (gu.getBuildType() != null
												|| !gu.getBuildType().equals(
														key))
											hasModify = true;
										gu.setBuildType(key);
										gu.setName(null);
										gu.setProductId(null);
										((TextView) findViewById(R.id.tljr_build_edit_name))
												.setText("");
									}
								}
							}
						});
					}
				});
		findViewById(R.id.tljr_build_edit_grp_name).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						new OnlySearchDialog(BuildEditDialog.this.context,
								new HttpRevMsg() {
									@Override
									public void revMsg(final String msg) {
										handler.post(new Runnable() {

											@Override
											public void run() {
												try {
													JSONObject object = new JSONObject(
															msg);
													if (gu.getName()==null||!gu.getName()
															.equals(object
																	.getString("name")))
														hasModify = true;
													gu.setName(object
															.getString("name"));
													gu.setProductId(object
															.getString("id"));
													((TextView) findViewById(R.id.tljr_build_edit_name))
															.setText(gu
																	.getName());
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										});
									}
								}, baseView, gu.getBuildType()).show(null);
					}
				});
	}

	/**
	 * @param v
	 * @param list
	 * @param msg
	 */
	@SuppressWarnings("deprecation")
	private void showPopWindow(View v, String[] list, final HttpRevMsg msg) {
		ScrollView scroll = new ScrollView(context);
		LinearLayout layout = new LinearLayout(context);
		scroll.addView(layout);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(context.getResources().getColor(
				R.color.tljr_white));
		final PopupWindow window = new PopupWindow(scroll,
				v.getMeasuredWidth(), LayoutParams.WRAP_CONTENT, true);
		window.setBackgroundDrawable(new BitmapDrawable());
		window.setOutsideTouchable(true);
		window.showAsDropDown(v);
		for (int i = 0; i < list.length; i++) {
			TextView view = new TextView(context);
			view.setText(list[i]);
			view.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, Util.HEIGHT / 20));
			view.setGravity(Gravity.CENTER);
			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (window != null)
						window.dismiss();
					msg.revMsg(((TextView) v).getText().toString());
				}
			});
			layout.addView(view);
			View view2 = new View(context);
			view2.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, 1));
			view2.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.img_222fengexian));
			layout.addView(view2);
		}
	}

	private OneBuild build;

	public void show(int y, OneBuild build) {
		this.build = build;
		try {
			this.gu = (OneBuild) build.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((TextView) findViewById(R.id.tljr_build_type)).setText(gu.getType());
		((TextView) findViewById(R.id.tljr_build_edit_et_type)).setText(gu
				.getValue() + "");
		((TextView) findViewById(R.id.tljr_build_edit_code)).setText(gu
				.getBuildType());
		((TextView) findViewById(R.id.tljr_build_edit_name)).setText(gu
				.getName());
		if (BuildActivity.buildType.containsKey(gu.getBuildType()))
			((TextView) findViewById(R.id.tljr_build_edit_code))
					.setText(BuildActivity.buildType.get(gu.getBuildType()));
		setBlurImage();
		show();
		((RelativeLayout.LayoutParams) findViewById(R.id.layout)
				.getLayoutParams()).topMargin = y - Util.HEIGHT / 20;
		
		
		((RelativeLayout.LayoutParams) findViewById(R.id.bottom)
				.getLayoutParams()).bottomMargin = Util.HEIGHT / 20;
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel:
			dismiss();
			break;
		case R.id.ok:
			if (gu.getName() == null) {
				((BuildActivity) MyApplication.getInstance().getNowActivity())
						.showToast("请选择添加的股票");
				return;
			}
			dismiss();
			if (MyApplication.getInstance().getNowActivity() instanceof BuildActivity
					&& hasModify) {
				build.setBuildType(this.gu.getBuildType());
				build.setName(this.gu.getName());
				build.setType(this.gu.getType());
				build.setValue(this.gu.getValue());
				build.setDelete(this.gu.isDelete());
				build.setProductId(this.gu.getProductId());
				((BuildActivity) MyApplication.getInstance().getNowActivity())
						.editBuild(build);
			}
			break;

		default:
			break;
		}
	}
}
