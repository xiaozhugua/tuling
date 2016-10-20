package com.abct.tljr.wxapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.ChartActivity;
import com.abct.tljr.data.Constant;
import com.abct.tljr.model.Options;
import com.abct.tljr.service.LoginResultReceiver;
import com.abct.tljr.ui.activity.zhiyan.RechargeActivity;
import com.abct.tljr.ui.activity.zhiyan.YhjActivity;
import com.abct.tljr.ui.activity.zhiyan.YhjShareActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.capturePhoto.CapturePhoto;
import com.abct.tljr.wxapi.xunzhang.XunzhangActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.http.client.entity.InputStreamUploadEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.BwManager;
import com.qh.common.login.Configs;
import com.qh.common.login.login.LoginCallBack;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.model.AppInfo;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.umeng.analytics.MobclickAgent;

public class PersonalActivity extends BaseActivity implements OnClickListener, LoginCallBack {
	private LinearLayout ll;
	private CapturePhoto capture;
	private ArrayList<OnePersonal> list = new ArrayList<OnePersonal>();
	private View v;
	public int upImageId = 0;
	private TextView name, id;
	private TextView payback, yue, juan;
	private RelativeLayout xunzhang;
	private byte[] ByteBs=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BwManager.getInstance().initLogin(this);
		v = View.inflate(this, R.layout.tljr_activity_person, null);
		this.setContentView(v);
		payback = (TextView) findViewById(R.id.payback);
		yue = (TextView) findViewById(R.id.yue);
		juan = (TextView) findViewById(R.id.juan);
		xunzhang=(RelativeLayout)findViewById(R.id.percent_xunzhang);
		findViewById(R.id.yueitem).setOnClickListener(this);
		findViewById(R.id.juanitem).setOnClickListener(this);
		findViewById(R.id.yaoqingitem).setOnClickListener(this);
		xunzhang.setOnClickListener(this);
		CollapsingToolbarLayout.LayoutParams params = (android.support.design.widget.CollapsingToolbarLayout.LayoutParams) findViewById(
				R.id.info).getLayoutParams();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			RelativeLayout.LayoutParams params2 = (android.widget.RelativeLayout.LayoutParams) findViewById(R.id.main)
					.getLayoutParams();
			params2.topMargin = -getStatusBarHeight();
			params.bottomMargin = getStatusBarHeight();
		}
		params.height = AppInfo.WIDTH * 2 / 3;
		findViewById(R.id.avatar).getLayoutParams().height = (int) (AppInfo.WIDTH * 0.1875f);
		findViewById(R.id.avatar).getLayoutParams().width = (int) (AppInfo.WIDTH * 0.1875f);
		ll = (LinearLayout) findViewById(R.id.tljr_per_ll);
		capture = new CapturePhoto(this);
		initAllTabInfo();
		initListener();
		if (getIntent().getBooleanExtra("showSelectImage", false)) {
			upImageId = 2;
			selectImage();
		}
		initRatio();
	}

	public void selectImage() {
		final Dialog dialog = new Dialog(this, R.style.dialog);
		View v = View.inflate(this, R.layout.dialog_changeavatar, null);
		switch (upImageId) {
		case 0:
			((TextView) v.findViewById(R.id.title)).setText("请上传头像");
			break;
		case 1:
			((TextView) v.findViewById(R.id.title)).setText("请上传本人身份证照片");
			v.findViewById(R.id.moren).setVisibility(View.GONE);
			break;
		case 2:
			((TextView) v.findViewById(R.id.title)).setText("请上传本人名片");
			v.findViewById(R.id.moren).setVisibility(View.GONE);
			break;
		default:
			break;
		}
		v.findViewById(R.id.guanbi).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		v.findViewById(R.id.paizhao).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				capture.dispatchTakePictureIntent(CapturePhoto.SHOT_IMAGE, 0);
			}
		});
		v.findViewById(R.id.xiangce).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				capture.dispatchTakePictureIntent(CapturePhoto.PICK_IMAGE, 1);
			}
		});
		v.findViewById(R.id.moren).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.img_avatar);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
				if (upImageId == 0) {
					uploadAvatar(baos.toByteArray());
				} else {
					uploadPic(baos.toByteArray());
				}
			}
		});
		dialog.setContentView(v);
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		byte[] bs = null;
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (capture.getActionCode() == CapturePhoto.PICK_IMAGE) {
				System.err.println(data.getData().toString());
				Uri targetUri = data.getData();
				if (targetUri != null)
					bs = capture.handleMediaPhoto(targetUri);
			} else {
				bs = capture.handleCameraPhoto();
			}
			MobclickAgent.onEvent(PersonalActivity.this, "modifyAvatar");
			if (bs != null && bs.length > 0) {
				if (upImageId == 0) {
					uploadAvatar(bs);
				} else {
					uploadPic(bs);
				}
			} else {
				showToast("获取图片信息失败，请确认重试!");
			}
		}

	}

	private void initListener() {
		findViewById(R.id.tljr_per_btn_lfanhui).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		findViewById(R.id.tljr_btn_exit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(PersonalActivity.this, "Logout");
				new AlertDialog.Builder(PersonalActivity.this).setTitle("提示").setMessage("确认退出帐号吗?")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						BwManager.getInstance().logout();

					}
				}).setNegativeButton("取消", null).show();
			}
		});
	}

	private void initAvatar(String avatar) {
		ImageLoader.getInstance().loadImage(avatar, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				ImageLoader.getInstance().displayImage(imageUri, (ImageView) findViewById(R.id.avatar),
						Options.getCircleListOptions());
				// Bitmap blured = BlurUtils.apply(PersonalActivity.this,
				// loadedImage, 5);
				// Drawable drawable = new BitmapDrawable(loadedImage);
				// findViewById(R.id.info).setBackgroundDrawable(drawable);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}

	private void initAllTabInfo() {
		User user = User.getUser();
		name = (TextView) findViewById(R.id.name);
		name.setText(user.getNickName());
		id = (TextView) findViewById(R.id.id);
		id.setText("ID:" + user.getId());
		initAvatar(user.getAvatarUrl());
		String[] s = { "地区", "来源", "最后登录时间", "绑定邮箱", "绑定手机" };
		int[] a = {R.drawable.img_diqu,R.drawable.img_laiyuan,R.drawable.img_shijian,R.drawable.img_youxiang,R.drawable.img_shouji };
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < s.length; i++) {
			names.add(s[i]);
			ids.add(a[i]);
		}
		if (!Configs.isThirdLogin) {
			names.add("修改密码");
			ids.add(R.drawable.img_mima);
			name.setOnClickListener(this);
			findViewById(R.id.avatar).setOnClickListener(this);
		}
		names.add("系统设置");
//		names.add("切换到工作模式");
		ids.add(R.drawable.img_xitongshezhi);
//		ids.add(R.drawable.img_qiehuanmoshi);
		for (int i = 0; i < names.size(); i++){
			OnePersonal personal = new OnePersonal(this, handler, names.get(i), getInfo(i), ids.get(i), i);
			list.add(personal);
			@SuppressWarnings("deprecation")
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			params.height = AppInfo.dp2px(this, 60);
			if (i % 3 == 0) {
				params.topMargin = AppInfo.dp2px(this, 15);
			}
			ll.addView(personal.getView(), params);
		}
		if (user.getLevelUnit() == null) {
			ProgressDlgUtil.showProgressDlg("", this);
			LoginResultReceiver.getUserInfo(new Complete() {
				@Override
				public void complete() {
					ProgressDlgUtil.stopProgressDlg();
					post(new Runnable() {
						@Override
						public void run() {
							initInfo();
							initValidInfo();
						}
					});
				}
			});
		} else {
			initInfo();
			initValidInfo();
		}
	}

	private String getInfo(int i) {
		switch (i) {
		case 0:
			return User.getUser().getArea();
		case 1:
			return User.getUser().getFrom();
		case 2:
			return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
				.format(new Date(User.getUser().getLast().optLong("time")));
		case 3:
			return User.getUser().getEmail();
		case 4:
			return User.getUser().getPhone();
		default:
			return "";
		}
	}

	// 获取认证信息，等级勋章信息
	private View initInfo() {
		TextView info = (TextView) v.findViewById(R.id.lv);
		info.setText("等级\nLv:" + User.getUser().getLevel());
		changeText(info);
		double pro = (User.getUser().getLevelNeedTotal() - User.getUser().getLevelNeed())
				/ User.getUser().getLevelNeedTotal() * 100;
		if (!Double.isNaN(pro)) {
			((TextView) v.findViewById(R.id.lvInfo)).setText("经验值\n" + Constant.df.format(pro) + "%");
			changeText((TextView) v.findViewById(R.id.lvInfo));
			((ProgressBar) v.findViewById(R.id.pro)).setProgress((int) pro);
		}
		((TextView) v.findViewById(R.id.jifen)).setText("积分\n" + User.getUser().getIntegral());
		changeText((TextView) v.findViewById(R.id.jifen));
		return v;
	}

	private void initValidInfo() {
		TextView name = (TextView) v.findViewById(R.id.nameauth);
		if (User.getUser().isValid()) {
			name.setText(User.getUser().getIdName());
			v.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
		} else {
			name.setText(User.getUser().getValidStatus() == 0 ? "未认证" : "认证中");
			v.findViewById(R.id.valid).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					upImageId = 1;
					selectImage();
				}
			});
		}
		TextView dav = (TextView) v.findViewById(R.id.vauth);
		if (User.getUser().isValidDv()) {
			dav.setText(User.getUser().getCompany());
			v.findViewById(R.id.varrow).setVisibility(View.INVISIBLE);
			((CheckBox) v.findViewById(R.id.vauthcb1)).setChecked(true);
			((CheckBox) v.findViewById(R.id.vauthcb)).setChecked(true);
		} else {
			dav.setText(User.getUser().getIdentityStatus() == 0 ? "未认证" : "认证中");
			((CheckBox) v.findViewById(R.id.vauthcb1)).setChecked(false);
			((CheckBox) v.findViewById(R.id.vauthcb)).setChecked(false);
			v.findViewById(R.id.dvvalid).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					upImageId = 2;
					selectImage();
				}
			});
		}
	}

	private void changeText(TextView view) {
		String str = view.getText().toString();
		int index = str.indexOf("\n");
		if (index < 0) {
			return;
		}
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new AbsoluteSizeSpan(12, true), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(style);
	}

	private void uploadAvatar(final byte[] bs) {
		this.ByteBs=bs;
		ProgressDlgUtil.showProgressDlg("上传中", this);
		LogUtil.e("uploadAvatar","uploadAvatar上传中");
		NetUtil.sendGet(UrlUtil.URL_getAvatar, new NetResult() {
			@Override
			public void result(String msg) {
				if (msg.length() == 0) {
					ProgressDlgUtil.stopProgressDlg();
					showToast("上传头像失败");
					return;
				}
				if (msg.equals("error")) {
					ProgressDlgUtil.stopProgressDlg();
					showToast("上传头像失败");
				} else {
					try{
						JSONObject obj =new JSONObject(msg);
						final String putURL = obj.optString("puturl");
						final String avatar = obj.optString("objname");

//						new ImageUpdateThead().execute(putURL,avatar);
						
						final HttpUtils hu = new HttpUtils(10000);
						final RequestParams params = new RequestParams();
						InputStream fStream = new ByteArrayInputStream(bs);
						hu.configTimeout(20000);
						params.addQueryStringParameter("Content-Length", bs.length + "");
						params.setBodyEntity(new InputStreamUploadEntity(fStream, bs.length));
						params.setContentType("image/jpeg");
						hu.configResponseTextCharset("utf-8");
						hu.configRequestRetryCount(5);
						post(new Runnable() {
							@Override
							public void run() {
								hu.send(HttpMethod.PUT, putURL, params, new RequestCallBack<String>() {
									@Override
									public void onFailure(HttpException arg0, String arg1) {
										ProgressDlgUtil.stopProgressDlg();
										showToast("修改头像失败");
									}
									@Override
									public void onSuccess(ResponseInfo<String> arg0) {
										String postUrl = UrlUtil.URL_user + "changeavatar?iou=1";
										String params = "token=" + Configs.token + "&avatar=" + UrlUtil.URL_avatar + avatar;
										HttpRequest.sendPost(postUrl, params, new HttpRevMsg() {
											@Override
											public void revMsg(String msg) {
												ProgressDlgUtil.stopProgressDlg();
												if (msg.length() == 0) {
													showToast("修改头像失败");
												} else {
													try{
														JSONObject obj =new JSONObject(msg);
														int code = obj.optInt("code");
														if (code == 1) {
															User.getUser().setAvatarUrl(UrlUtil.URL_avatar + avatar);
															post(new Runnable() {
																@Override
																public void run() {
																	initAvatar(User.getUser().getAvatarUrl());
																	MyApplication.getInstance().getMainActivity().main.initAvatar();
																	showToast("修改头像成功");
																}
															});
															ChartActivity.getYunZhiToken(User.getUser().getId(),
																	User.getUser().getNickName(), null,
																	User.getUser().getAvatarUrl());
														} else {
															showToast("修改头像失败");
														}
													}catch(Exception e){
														
													}
												}
											}
										});
									}
								});
							}
						});

					}catch(Exception e){
						
					}
				}
			}
		});
	}

	private void uploadPic(final byte[] bs) {
		ProgressDlgUtil.showProgressDlg("", this);
		HttpUtils http = new HttpUtils(10000);
		RequestParams params = new RequestParams();
		InputStream stream = new ByteArrayInputStream(bs);
		params.addBodyParameter("key", stream, bs.length);
		http.send(HttpMethod.POST, UrlUtil.URL_validAddress, params, new RequestCallBack<String>() {
			@Override
			public void onStart() {
			}
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				LogUtil.e("onLoading", current + "/" + total);
			}
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtil.e("onSuccess", responseInfo.result);
				try {
					org.json.JSONObject object = new org.json.JSONObject(responseInfo.result);
					if (object.getInt("status") == 200) {
						if (upImageId == 1) {
							sendValid(object.getJSONArray("result").getJSONObject(0).getString("url"));
						} else {
							sendValidDv(object.getJSONArray("result").getJSONObject(0).getString("url"));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					ProgressDlgUtil.stopProgressDlg();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.e("onFailure", error.getExceptionCode() + ":" + msg);
				error.printStackTrace();
				showToast("上传失败，请重试！");
				ProgressDlgUtil.stopProgressDlg();
			}
		});
	}

	private void sendValid(String url) {
		LogUtil.e("sendValid", "identityId=" + User.getUser().getIdentityId() + "&identityImg=" + url);
		NetUtil.sendGet(UrlUtil.URL_valid + "/submit",
				"identityId=" + User.getUser().getIdentityId() + "&identityImg=" + url, new NetResult() {
					@Override
					public void result(final String msg) {
						LogUtil.e("sendValid", msg);
						// {"code":500,"err":{"message":"identity.record
						// validation failed"}}
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {

							@Override
							public void run() {
								try {
									org.json.JSONObject object = new org.json.JSONObject(msg);
									if (object.getInt("code") == 200) {
										showToast("上传成功,请等待后台认证!");
										LoginResultReceiver.getUserInfo(new Complete() {
											@Override
											public void complete() {
												post(new Runnable() {
													@Override
													public void run() {
														initValidInfo();
													}
												});
											}
										});
									} else {
										showToast(object.getJSONObject("err").getString("message"));
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						});
					}
				});
	}

	private void sendValidDv(String url) {
		LogUtil.e("sendValidDv",
				UrlUtil.URL_validDv + "/" + User.getUser().getIdentityDvId() + "/submit?" + "main=" + url);
		NetUtil.sendGet(UrlUtil.URL_validDv + "/" + User.getUser().getIdentityDvId() + "/submit?" + "main=" + url, "",
				new NetResult() {

					@Override
					public void result(final String msg) {
						LogUtil.e("sendValidDv", msg);
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {
							@Override
							public void run() {
								try {
									org.json.JSONObject object = new org.json.JSONObject(msg);
									if (object.getInt("code") == 200) {
										showToast("上传成功,请等待后台认证!");
										LoginResultReceiver.getUserInfo(new Complete() {

											@Override
											public void complete() {
												post(new Runnable() {

													@Override
													public void run() {
														initValidInfo();
													}
												});
											}
										});
									} else {
										showToast(object.getJSONObject("err").getString("message"));
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						});
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event != null && event.getRepeatCount() == 0) {
			// logout("");
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		initYue();
		initJuan();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.avatar:
			upImageId = 0;
			selectImage();
			break;
		case R.id.name:
			modifyNickName(name);
			MobclickAgent.onEvent(this, "modifyNickName");
			break;
		case R.id.juanitem:
			startActivity(new Intent(this, YhjActivity.class));
			break;
		case R.id.yaoqingitem:
			startActivity(new Intent(this, YhjShareActivity.class));
			break;
		case R.id.yueitem:
			if (isHasCrash && isHasRatio) {
				startActivity(
						new Intent(this, RechargeActivity.class).putExtra("crash", crash).putExtra("ratio", ratio));
			} else {
				showToast("连接服务器失败，请稍后重试!");
			}
			break;
		case R.id.percent_xunzhang:
			Intent intent=new Intent(this,XunzhangActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void modifyNickName(final TextView name) {
		final EditText inputNickName = new EditText(this);
		inputNickName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("输入您的新昵称(最多10字符)").setView(inputNickName).setNegativeButton("取消", null);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				final String newNickName = inputNickName.getText().toString().trim();
				if (newNickName.length() == 0) {
					showToast("请输入昵称");
					return;
				}
				ProgressDlgUtil.showProgressDlg("修改中", PersonalActivity.this);
				String geturl = UrlUtil.URL_user + "changenickname?iou=1";
				String param = "token=" + Configs.token + "&nickname=" + newNickName;
				NetUtil.sendPost(geturl, param, new NetResult() {
					@Override
					public void result(final String msg) {
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									org.json.JSONObject object = new org.json.JSONObject(msg);
									if (object.getInt("code") == 1) {
										showToast("修改成功");
										name.setText(newNickName);
										User.getUser().setNickName(newNickName);
										MyApplication.getInstance().getMainActivity().initUser();
										ChartActivity.getYunZhiToken(User.getUser().getId(),
												User.getUser().getNickName(), null, User.getUser().getAvatarUrl());
									} else {
										showToast("修改失败");
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									showToast("修改失败");
								}

							}
						});
					}
				});

			}
		});
		builder.show();
	}

	private float crash;
	private float ratio;
	private boolean isHasCrash = false;
	private boolean isHasRatio = false;

	private void initYue() {
		NetUtil.sendGet(UrlUtil.URL_ZR + "user/getQuan", "uid=" + User.getUser().getId() + "&token=" + Configs.token,
				new NetResult() {

					@Override
					public void result(final String msg) {
						ProgressDlgUtil.stopProgressDlg();
						try {
							org.json.JSONObject object = new org.json.JSONObject(msg);
							if (object.getInt("status") == 1) {
								crash = (float) object.getDouble("msg");
								yue.setText("余额" + (float) crash / 100 + "元");
								isHasCrash = true;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});
	}

	private void initRatio() {
		NetUtil.sendPost(UrlUtil.URL_ZR + "user/getRatio", "", new NetResult() {

			@Override
			public void result(final String msg) {
				ProgressDlgUtil.stopProgressDlg();
				try {
					org.json.JSONObject object = new org.json.JSONObject(msg);
					if (object.getInt("status") == 1) {
						ratio = (float) object.getDouble("msg");
						payback.setText("充100返" + (int) (100 * ratio));
						isHasRatio = true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void initJuan() {
		NetUtil.sendGet(UrlUtil.URL_ZR + "coupon/getMyCouponList",
				"uid=" + User.getUser().getId() + "&token=" + Configs.token + "&page=1&size=100", new NetResult() {

					@Override
					public void result(String msg) {
						try {
							org.json.JSONObject jsonObject = new org.json.JSONObject(msg);
							if (jsonObject.getInt("status") == 1) {
								JSONArray array = jsonObject.getJSONArray("msg");
								juan.setText(array.length() + "张");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	public void login(String s) {

	}

	@Override
	public void logout() {
		Intent intent = new Intent(Configs.serviceName);
		intent.putExtra("type", "logout");
		sendBroadcast(intent);
		finish();
	}

	@Override
	public void error(String s) {
		showToast(s);
	}
	
	class ImageUpdateThead extends AsyncTask<String,Void,Integer>{
		@Override
		protected Integer doInBackground(String... params) {
			ImageUpdate(ByteBs,params[0],params[1]);
			return null;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
		}
		
	}
	
	
	public void ImageUpdate(byte[] bs,String putURL,String avatar){
		try{
			String BOUNDARY=UUID.randomUUID().toString();
			String CONTENT_TYPE="image/jpeg";
			String PREFIX="--";  
		    String LINE_END="\r\n";  
		    String CHARSET="utf-8";
		    
			URL url=new URL(putURL);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setReadTimeout(10*1000);
			conn.setConnectTimeout(10*1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Charset",CHARSET);
			//conn.setRequestProperty("Connection","keep-alive");
			//conn.setRequestProperty("User-agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0)Gecko/20100101 Firefox/33.0"); 
			conn.setRequestProperty("Content-type",CONTENT_TYPE+";boundary="+BOUNDARY);
			
			DataOutputStream dos=new DataOutputStream(conn.getOutputStream());
			StringBuffer sb=new StringBuffer();
			sb.append(BOUNDARY);
			sb.append(PREFIX);
			sb.append(LINE_END);
			
			sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+avatar+"\""+LINE_END);  
            sb.append("Content-Type: image/jpeg; charset="+CHARSET+LINE_END);   
			
            dos.write(sb.toString().getBytes());   
            InputStream is=new ByteArrayInputStream(bs);  
            byte[] bytes=new byte[1024];
            
            int len = 0;   
            while((len=is.read(bytes))!=-1)   
            {   
               dos.write(bytes, 0, len);   
            }   
            is.close();   
            dos.write(LINE_END.getBytes());   
            byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();   
            dos.write(end_data);   
            dos.flush();  
             
            /**  
             * 获取响应码 200=成功  
             * 当响应成功,获取响应的流  
             */   
            int res = conn.getResponseCode();   
            if(res==200)   
            {  
            	LogUtil.e("ResponseCode","ResponseCode="+res);
            }  
            
		}catch(Exception e){
			LogUtil.e("PersonalActivityException",e.getMessage());
		}
	}
	
	public static void ChangePhoneEmailFinish(){
		NetUtil.sendGet(Configs.URL_oauth,"token="+Configs.token+"&iou=m",new NetResult() {
			@Override
			public void result(String arg0) {
				
			}
		});
	}
	
}
