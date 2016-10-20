package com.abct.tljr.wxapi;

import java.lang.reflect.Field;

import org.json.JSONObject;

import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.dialog.EnterBindSecurityDialog;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.ui.activity.SettingActivity;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.umeng.analytics.MobclickAgent;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OnePersonal {
	private Activity activity;
	private View v;
	private View v1;

	public View getView() {
		return v;
	}

	@SuppressWarnings("deprecation")
	public OnePersonal(Activity activity, Handler handler, String name, String info, int id, int type) {
		this.activity = activity;
		this.handler = handler;
		this.v = View.inflate(activity, R.layout.tljr_item_personal, null);
		((ImageView) v.findViewById(R.id.icon)).setImageDrawable(activity.getResources().getDrawable(id));
		v1 = (TextView) v.findViewById(R.id.tljr_item_personal_info);
		((TextView) v1).setText(info);
		if (type == 3 && User.getUser() != null && User.getUser().getEmail() != null) {
			if (!User.getUser().isInvail()) {
				((TextView) v1).setText(info + "(未验证)");
			} else {
				((TextView) v1).setText(info + "(已验证)");
			}
		}
		if (type == 4 && User.getUser() != null && User.getUser().getPhone() != null) {
			if (!User.getUser().isInvailPhone()) {
				((TextView) v1).setText(info + "(未验证)");
			} else {
				((TextView) v1).setText(info + "(已验证)");
			}
		}
		((TextView) v.findViewById(R.id.tljr_item_personal_name)).setText(name);
		OnClickListener listener = getOnClickListener(type);
		if (listener != null) {
			v.setOnClickListener(listener);
		} else {
			v.findViewById(R.id.tljr_item_personal_arrow).setVisibility(View.INVISIBLE);
		}
	}

	private OnClickListener getOnClickListener(int i) {
		switch (i) {
		case 0:
			return null;
		case 1:
			return null;
		case 2:
			return null;
		case 3:
			return new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					bindEmail((TextView) v1);
				}
			};
		case 4:
			return new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					bindPhone((TextView) v1);
				}
			};
		case 5:
			return new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (!Configs.isThirdLogin) {
						modifyPwd();
					} else {
						activity.startActivity(new Intent(activity, SettingActivity.class));
					}
				}
			};
		case 6:
			return new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					activity.startActivity(new Intent(activity, SettingActivity.class));
				}
			};
		case 7:
			return new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					modifyMode();
				}

			};
		default:
			return null;
		}
	}

	@SuppressWarnings("unused")
	private void synStock(final TextView tag) {
		ZiXuanUtil.sendActions(MyApplication.getInstance().self, new NetResult() {
			@Override
			public void result(final String msg) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (msg.equals("1")) {
							showToast("同步成功");
							tag.setText(Constant.lastSynTime);
						} else {
							showToast("同步失败");
						}
					}
				});
			}
		});
	}

	private void bindEmail(final TextView tag) {
		MobclickAgent.onEvent(activity, "bindEmail");
		final EditText inputEmail = new EditText(activity);
		inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("输入您的邮箱").setView(inputEmail).setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				setDialogDis(dialog, true);
			}
		});
		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				setDialogDis(dialog, false);
				final String newMail = inputEmail.getText().toString().trim();
				if (newMail.length() == 0) {
					showToast("邮箱不能为空");
					return;
				}
				if (!Util.isValidEmail(newMail)) {
					showToast("请输入正确的邮箱地址");
					return;
				}
				String geturl = UrlUtil.URL_user + "bind?iou=1";
				String param = "token=" + Configs.token + "&type=email" + "&value=" + newMail;
				setDialogDis(dialog, true);
				ProgressDlgUtil.showProgressDlg("修改中", activity);
				LogUtil.e("bindEmail", geturl + "?" + param);
				NetUtil.sendPost(geturl, param, new NetResult() {
					@Override
					public void result(final String msg) {
						LogUtil.e("bindEmail", msg);
						ProgressDlgUtil.stopProgressDlg();
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (msg.length() > 0) {
									try {
										JSONObject obj =new JSONObject(msg);
										int code = obj.optInt("code");
										if (code == 1) {
											Util.ChangePhoneEmailFinish(activity);
											User.getUser().setEmail(ChangeBind(newMail));
											User.getUser().setInvail(false);
											new PromptDialog(activity,"绑定完成\n已发验证邮件到您的邮箱,请验证完下次重启应用即可完成", new Complete() {
												@Override
												public void complete() {}
											}).showNoCancel();
											tag.setText(ChangeBind(newMail)+"(未验证)");
										} else {
											if (code == -1015) {
												showToast("此邮箱已绑定其它帐号");
											} else if (code == -1024) {
												showToast("此类邮箱暂不支持，请重试!");
											} else {
												showToast("绑定失败");
											}
										}
									} catch (Exception e) {
										showToast("绑定失败，请重试!");
									}
								} else {
									showToast("绑定失败，请重试!");
								}
							}
						});
					}
				});

			}
		});
		builder.show();
	}

	private void bindPhone(final TextView tag) {
		MobclickAgent.onEvent(activity, "bindPhone");
		final EditText inputPhone = new EditText(activity);
		inputPhone.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("输入手机号,绑定重新登录生效").setView(inputPhone).setPositiveButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						setDialogDis(dialog, true);
					}
				});
		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				setDialogDis(dialog, false);
				final String newPhone = inputPhone.getText().toString().trim();
				if (newPhone.length() == 0) {
					showToast("手机号码不能为空");
					return;
				}
				if (!Util.isValidMobile(newPhone)) {
					showToast("请输入正确的手机号码");
					return;
				}
				String geturl = UrlUtil.URL_user + "bind?iou=1";
				String param = "token=" + Configs.token + "&type=phone" + "&value=" + newPhone;
				setDialogDis(dialog, true);
				ProgressDlgUtil.showProgressDlg("修改中", activity);
				NetUtil.sendPost(geturl, param, new NetResult() {
					@Override
					public void result(final String msg) {
						LogUtil.e("bindPhone", msg);
						ProgressDlgUtil.stopProgressDlg();
						handler.post(new Runnable() {
							@Override
							public void run() {
								try{
									if (msg.length() > 0) {
										JSONObject obj =new JSONObject(msg);
										try {
											int code = obj.optInt("code");
											if (code == 1) {
												new EnterBindSecurityDialog(activity, newPhone, obj.getString("result"),tag,newPhone).show();
											} else {
												if (code == -1015 || code == -1016) {
													showToast("此手机号已绑定其它帐号");
												} else if (code == -1024) {
													showToast("此类绑定暂不支持，请重试!");
												} else {
													showToast("绑定失败");
												}
											}
										} catch (Exception e) {
											showToast(obj.getString("code"));
										}
									} else {
										showToast("绑定失败");
									}
								}catch(Exception e){
									
								}
							}
						});
					}
				});

			}
		});
		builder.show();
	}

	public String ChangeBind(String param){
		String start=param.substring(0,3);
		String end=param.substring(param.length()-5,param.length());
		return start+"****"+end;
	}
	
	private void setDialogDis(DialogInterface dialog, boolean isDimiss) {
		try {// 下面三句控制弹框的关闭

			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");

			field.setAccessible(true);

			field.set(dialog, isDimiss);// true表示要关闭

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	@SuppressLint("InflateParams")
	private void modifyPwd() {
		final View view = activity.getLayoutInflater().inflate(R.layout.tljr_dialog_modiypwd, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("修改密码").setView(view).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				setDialogDis(dialog, true);
			}
		});
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, int which) {
				setDialogDis(dialog, false);
				EditText et_pwd = (EditText) view.findViewById(R.id.tljr_mdy_pwd);
				EditText et_n_pwd = (EditText) view.findViewById(R.id.tljr_mdy_n_pwd);
				EditText et_n_rpwd = (EditText) view.findViewById(R.id.tljr_mdy_n_rpwd);
				String str_pwd = et_pwd.getText().toString().trim();
				String str_n_pwd = et_n_pwd.getText().toString().trim();
				String str_n_rpwd = et_n_rpwd.getText().toString().trim();
				if (str_pwd.length() == 0) {
					showToast("请输入密码");
					return;
				}
				if (str_n_pwd.length() == 0) {
					showToast("请输入新密码");
					return;
				}
				if (str_n_rpwd.length() == 0) {
					showToast("请输入确认新密码");
					return;
				}
				if (!str_n_pwd.equals(str_n_rpwd)) {
					showToast("两次密码不一致");
					return;
				}
				if (str_n_pwd.length() < 6) {
					showToast("密码长度不能少于6");
					return;
				}
				MobclickAgent.onEvent(activity, "modifyPwd");
				String postUrl = UrlUtil.URL_user + "changepwd?iou=1";
				String param = "uname=" + MyApplication.getInstance().self.getUserName() + "&pwd=" + str_pwd
						+ "&changepwd=" + str_n_pwd;
				setDialogDis(dialog, true);
				ProgressDlgUtil.showProgressDlg("修改中", activity);
				NetUtil.sendPost(postUrl, param, new NetResult() {
					@Override
					public void result(final String msg) {
						ProgressDlgUtil.stopProgressDlg();
						handler.post(new Runnable() {
							@Override
							public void run() {
								try{
									dialog.dismiss();
									if (msg.length() == 0) {
										showToast("修改密码失败");
										return;
									}
									JSONObject obj =new JSONObject(msg);
									int code = obj.optInt("code");
									if (code == 1) {
										showToast("修改密码成功");
									} else {
										showToast("修改密码失败");
									}
								}catch(Exception e){
									
								}
							}
						});
					}
				});
			}
		});
		builder.show();
	}

	private Handler handler;

	public void showToast(String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}

	// 切换工作模式
	private void modifyMode() {
		Util.startApp(activity, "WorkMode.apk");
	}
}
