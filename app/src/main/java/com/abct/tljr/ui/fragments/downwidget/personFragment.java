package com.abct.tljr.ui.fragments.downwidget;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

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
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.ChartActivity;
import com.abct.tljr.model.Options;
import com.abct.tljr.service.LoginResultReceiver;
import com.abct.tljr.ui.activity.zhiyan.RechargeActivity;
import com.abct.tljr.ui.activity.zhiyan.YhjActivity;
import com.abct.tljr.ui.activity.zhiyan.YhjShareActivity;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.capturePhoto.CapturePhoto;
import com.abct.tljr.wxapi.OnePersonal;
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
import com.qh.common.login.Configs;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.model.AppInfo;
import com.qh.common.model.User;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.umeng.analytics.MobclickAgent;

public class personFragment extends BaseFragment implements OnClickListener {
	// private TextView name, id;
	private TextView payback, yue, juan;
	private Handler handler = new Handler();
	private CapturePhoto capture;
	public int upImageId = 0;
	private LinearLayout ll;
	private ArrayList<OnePersonal> list = new ArrayList<OnePersonal>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		baseView = View.inflate(activity, R.layout.tljr_item_widget_person, null);
		ll = (LinearLayout) findViewById(R.id.tljr_per_ll);

		payback = (TextView) findViewById(R.id.payback);
		yue = (TextView) findViewById(R.id.yue);
		juan = (TextView) findViewById(R.id.juan);
		findViewById(R.id.yueitem).setOnClickListener(this);
		findViewById(R.id.juanitem).setOnClickListener(this);
		findViewById(R.id.yaoqingitem).setOnClickListener(this);

		initAllTabInfo();
		capture = new CapturePhoto(activity);
		initRatio();
	}

	private void initAllTabInfo() {
		User user = User.getUser();
		// name = (TextView) findViewById(R.id.name);
		// name.setText(user.getNickName());
		// id = (TextView) findViewById(R.id.id);
		// id.setText("ID:" + user.getId());
		// initAvatar(user.getAvatarUrl());
		String[] s = { "地区", "来源", "最后登录时间", "绑定邮箱", "绑定手机" };
		int[] a = { R.drawable.img_diqu, R.drawable.img_laiyuan, R.drawable.img_shijian, R.drawable.img_youxiang,
				R.drawable.img_shouji };
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < s.length; i++) {
			names.add(s[i]);
			ids.add(a[i]);
		}
		if (!Configs.isThirdLogin) {
			names.add("修改密码");
			ids.add(R.drawable.img_mima);
			// name.setOnClickListener(this);
			// findViewById(R.id.avatar).setOnClickListener(this);
		}
		names.add("系统设置");
		names.add("切换到工作模式");
		ids.add(R.drawable.img_xitongshezhi);
		ids.add(R.drawable.img_qiehuanmoshi);

		for (int i = 0; i < names.size(); i++) {
			OnePersonal personal = new OnePersonal(activity, handler, names.get(i), getInfo(i), ids.get(i), i);
			list.add(personal);
			@SuppressWarnings("deprecation")
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			params.height = 80 * AppInfo.HEIGHT / AppInfo.IMAGEWIDTH;
			if (i % 3 == 0) {
				params.topMargin = 20 * AppInfo.HEIGHT / 720;
			}
			ll.addView(personal.getView(), params);
		}

		if (user.getIdentityId() == null) {
			ProgressDlgUtil.showProgressDlg("", activity);
			LoginResultReceiver.getUserInfo(new Complete() {
				@Override
				public void complete() {
					ProgressDlgUtil.stopProgressDlg();
					handler.post(new Runnable() {
						@Override
						public void run() {
							initValidInfo();
						}
					});
				}
			});
		} else {
			initValidInfo();
		}
	}

	private void initValidInfo() {
		TextView name = (TextView) findViewById(R.id.nameauth);
		if (User.getUser().isValid()) {
			name.setText(User.getUser().getIdName());
			findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
		} else {
			name.setText(User.getUser().getValidStatus() == 0 ? "未认证" : "认证中");
			findViewById(R.id.valid).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					upImageId = 1;
					selectImage();
				}
			});
		}
		TextView dav = (TextView) findViewById(R.id.vauth);
		if (User.getUser().isValidDv()) {
			dav.setText(User.getUser().getCompany());
			findViewById(R.id.varrow).setVisibility(View.INVISIBLE);
			// ((CheckBox) findViewById(R.id.vauthcb1)).setChecked(true);
			// ((CheckBox) findViewById(R.id.vauthcb)).setChecked(true);
		} else {
			dav.setText(User.getUser().getIdentityStatus() == 0 ? "未认证" : "认证中");
			// ((CheckBox) findViewById(R.id.vauthcb1)).setChecked(false);
			// ((CheckBox) findViewById(R.id.vauthcb)).setChecked(false);
			findViewById(R.id.dvvalid).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					upImageId = 2;
					selectImage();
				}
			});
		}
	}

	public void selectImage() {
		final Dialog dialog = new Dialog(activity, R.style.dialog);
		View v = View.inflate(activity, R.layout.dialog_changeavatar, null);
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

	private String getInfo(int i) {
		switch (i) {
		case 0:
			return User.getUser().getArea();
		case 1:
			return User.getUser().getFrom();
		case 2:
			return DateUtil.getDate(User.getUser().getLast().optLong("time"));
		case 3:
			return User.getUser().getEmail();
		case 4:
			return User.getUser().getPhone();
		default:
			return "";
		}
	}

	@Override
	public void onClick(View v) {
		switch (getId()) {
		// case R.id.avatar:
		// selectImage();
		// upImageId = 0;
		// break;
		case R.id.name:
			// modifyNickName(name);
			// MobclickAgent.onEvent(activity, "modifyNickName");
			break;
		case R.id.juanitem:
			startActivity(new Intent(activity, YhjActivity.class));
			break;
		case R.id.yaoqingitem:
			startActivity(new Intent(activity, YhjShareActivity.class));
			break;
		case R.id.yueitem:
			if (isHasCrash && isHasRatio) {
				startActivity(
						new Intent(activity, RechargeActivity.class).putExtra("crash", crash).putExtra("ratio", ratio));
			} else {
				showToast("连接服务器失败，请稍后重试!");
			}
			break;
		default:
			break;
		}

	}

	private void modifyNickName(final TextView name) {
		final EditText inputNickName = new EditText(activity);
		inputNickName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("输入您的新昵称(最多10字符)").setView(inputNickName).setNegativeButton("取消", null);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				final String newNickName = inputNickName.getText().toString().trim();
				if (newNickName.length() == 0) {
					showToast("请输入昵称");
					return;
				}
				ProgressDlgUtil.showProgressDlg("修改中", activity);
				String geturl = UrlUtil.URL_user + "changenickname?iou=1";
				String param = "token=" + Configs.token + "&nickname=" + newNickName;
				NetUtil.sendPost(geturl, param, new NetResult() {
					@Override
					public void result(final String msg) {
						ProgressDlgUtil.stopProgressDlg();
						handler.post(new Runnable() {

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

	// 获取认证信息，等级勋章信息
	private void initInfo() {
		// TextView info = (TextView) findViewById(R.id.lv);
		// info.setText("等级\nLv:" + User.getUser().getLevel());
		// changeText(info);
		// double pro = (User.getUser().getLevelNeedTotal() -
		// User.getUser().getLevelNeed())
		// / User.getUser().getLevelNeedTotal() * 100;
		// if (!Double.isNaN(pro)) {
		// ((TextView) findViewById(R.id.lvInfo)).setText("经验值\n" +
		// Constant.df.format(pro) + "%");
		// changeText((TextView) findViewById(R.id.lvInfo));
		// ((ProgressBar) findViewById(R.id.pro)).setProgress((int) pro);
		// }
		// if (User.getUser().getIntegral() < 0) {
		// ProgressDlgUtil.showProgressDlg("", activity);
		// LoginResultReceiver.getUserEvent(new Complete() {
		//
		// @Override
		// public void complete() {
		// ProgressDlgUtil.stopProgressDlg();
		// if (User.getUser() != null) {
		// handler.post(new Runnable() {
		// @Override
		// public void run() {
		// if (User.getUser().getIntegral() != -1) {
		// ((TextView) findViewById(R.id.jifen))
		// .setText("积分\n" + User.getUser().getIntegral());
		// changeText((TextView) findViewById(R.id.jifen));
		// }
		// }
		// });
		// }
		// }
		// });
		// } else {
		// ((TextView) findViewById(R.id.jifen)).setText("积分\n" +
		// User.getUser().getIntegral());
		// changeText((TextView) findViewById(R.id.jifen));
		// }
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

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(activity);
		if (User.getUser() != null) {
			initYue();
			initJuan();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(activity);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		byte[] bs = null;
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == activity.RESULT_OK) {
			if (capture.getActionCode() == CapturePhoto.PICK_IMAGE) {
				Uri targetUri = data.getData();
				if (targetUri != null)
					bs = capture.handleMediaPhoto(targetUri);
			} else {
				bs = capture.handleCameraPhoto();
			}
			MobclickAgent.onEvent(activity, "modifyAvatar");
			if (bs != null && bs.length > 0) {
				if (upImageId == 0) {
					uploadAvatar(bs);
				} else {
					uploadPic(bs);
				}
			} else {
				showToast("上传头像失败");
			}
		}

	}

	private void uploadPic(final byte[] bs) {
		ProgressDlgUtil.showProgressDlg("", activity);
		HttpUtils http = new HttpUtils(10000);
		RequestParams params = new RequestParams();
		InputStream stream = new ByteArrayInputStream(bs);
		params.addBodyParameter("key", stream, bs.length);
		http.send(HttpMethod.POST, UrlUtil.URL_validAddress, params, new RequestCallBack	<String>() {
			@Override
			public void onStart() {
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				LogUtil.e("onLoading", current + "/" + total);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// msgTextview.setText("reply: " + responseInfo.result);
				LogUtil.e("onSuccess", responseInfo.result);
				// {"status":200,"result":[{"url":"tuling.img-cn-hangzhou.aliyuncs.com/api.tuling%2F0.11696740705519915","protocol":"http"}]}
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
					// TODO Auto-generated catch block
					e.printStackTrace();
					ProgressDlgUtil.stopProgressDlg();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtil.e("onFailure", error.getExceptionCode() + ":" + msg);
				ProgressDlgUtil.stopProgressDlg();
			}
		});
	}

	private void uploadAvatar(final byte[] bs) {
		ProgressDlgUtil.showProgressDlg("上传中", activity);
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
						final String putURL = obj.getString("puturl");
						final String avatar = obj.getString("objname");

						final HttpUtils hu = new HttpUtils(10000);
						final RequestParams params = new RequestParams();
						InputStream fStream = new ByteArrayInputStream(bs);
						hu.configTimeout(20000);
						params.addQueryStringParameter("Content-Length", bs.length + "");
						params.setBodyEntity(new InputStreamUploadEntity(fStream, bs.length));
						params.setContentType("image/jpeg");
						hu.configResponseTextCharset("utf-8");
						hu.configRequestRetryCount(5);
						handler.post(new Runnable() {

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
														int code = obj.getInt("code");
														if (code == 1) {
															User.getUser().setAvatarUrl(UrlUtil.URL_avatar + avatar);
															handler.post(new Runnable() {
																@Override
																public void run() {
																	ImageView img_avatar = (ImageView) findViewById(
																			R.id.avatar);
																	initAvatar(User.getUser().getAvatarUrl());
																	// setResult("");
																	MyApplication.getInstance().getMainActivity().initUser();
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

	private void sendValid(String url) {
		NetUtil.sendGet(UrlUtil.URL_valid + "/submit",
				"identityId=" + User.getUser().getIdentityId() + "&identityImg=" + url, new NetResult() {

					@Override
					public void result(final String msg) {
						LogUtil.e("sendValid", msg);
						// {"code":500,"err":{"message":"identity.record
						// validation failed"}}
						ProgressDlgUtil.stopProgressDlg();
						handler.post(new Runnable() {

							@Override
							public void run() {
								try {
									org.json.JSONObject object = new org.json.JSONObject(msg);
									if (object.getInt("code") == 200) {
										showToast("上传成功,请等待后台认证!");
										LoginResultReceiver.getUserInfo(new Complete() {

											@Override
											public void complete() {
												handler.post(new Runnable() {

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
		NetUtil.sendGet(UrlUtil.URL_validDv + "/" + User.getUser().getIdentityDvId() + "/submit" + "main=" + url, "",
				new NetResult() {

					@Override
					public void result(final String msg) {
						LogUtil.e("sendValidDv", msg);
						// {"code":500,"err":{"message":"identity.record
						// validation failed"}}
						ProgressDlgUtil.stopProgressDlg();
						handler.post(new Runnable() {

							@Override
							public void run() {
								try {
									org.json.JSONObject object = new org.json.JSONObject(msg);
									if (object.getInt("code") == 200) {
										showToast("上传成功,请等待后台认证!");
										LoginResultReceiver.getUserInfo(new Complete() {

											@Override
											public void complete() {
												handler.post(new Runnable() {

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

	public void showToast(String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}
}
