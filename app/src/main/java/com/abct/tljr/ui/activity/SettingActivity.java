package com.abct.tljr.ui.activity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.InitData;
import com.abct.tljr.service.LoginResultReceiver;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.widget.SwitchButton;
import com.abct.tljr.utils.Util;
import com.abct.tljr.wxapi.ShareActivity;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.util.DataCleanManager;
import com.qh.common.util.NetUtil;
import com.qh.common.util.PreferenceUtils;
import com.qh.common.util.UrlUtil;
import com.umeng.update.UmengUpdateAgent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015-5-28 上午11:47:58
 */
public class SettingActivity extends BaseActivity implements OnClickListener {
	private RelativeLayout ztdx, czsj, sxpl, tbsj, cxll, czll, jcgx, fx, gywm;
	private RelativeLayout tbgpdm, tbggxx, tbxtsj;
	private LinearLayout tbsjm;
	private ImageView img_tbsj;
	private SwitchButton noPitureSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_setting);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		ztdx = (RelativeLayout) findViewById(R.id.tljr_grp_set_zxzt);// 资讯字体大小
		czsj = (RelativeLayout) findViewById(R.id.tljr_grp_set_czsj);// 重置数据
		sxpl = (RelativeLayout) findViewById(R.id.tljr_grp_set_sxpl);// 刷新频率
		tbsj = (RelativeLayout) findViewById(R.id.tljr_grp_set_tbsj);// 打开同步数据
		cxll = (RelativeLayout) findViewById(R.id.tljr_grp_set_cxll);// 查询流量
		czll = (RelativeLayout) findViewById(R.id.tljr_grp_set_czll);// 重置流量
		jcgx = (RelativeLayout) findViewById(R.id.tljr_grp_set_jcgx);// 检查更新
		fx = (RelativeLayout) findViewById(R.id.tljr_grp_set_fx);// 分享APP
		gywm = (RelativeLayout) findViewById(R.id.tljr_grp_set_gywm);// 关于我们
		findViewById(R.id.tljr_grp_set_mzsm).setOnClickListener(this);
		tbgpdm = (RelativeLayout) findViewById(R.id.tljr_grp_set_tbgpdm);// 同步股票代码
		tbggxx = (RelativeLayout) findViewById(R.id.tljr_grp_set_tbggxx);// 同步公告信息
		tbxtsj = (RelativeLayout) findViewById(R.id.tljr_grp_set_tbxtsj);// 同步系统时间
		tbsjm = (LinearLayout) findViewById(R.id.tljr_grp_set_tbsjm);// 同步数据
		img_tbsj = (ImageView) findViewById(R.id.tljr_img_set_tbsj);
		noPitureSwitch = (SwitchButton) findViewById(R.id.tljr_noPicture_switch); // 无图模式
		noPitureSwitch.setChecked(Constant.preference.getBoolean("isNoPitureMode", false));
		noPitureSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				Constant.noPictureMode = isChecked;
				Constant.preference.edit().putBoolean("isNoPitureMode", Constant.noPictureMode).commit();
				showMessage("设置成功," + (isChecked ? "当前为无图模式" : "关闭无图模式"));
			}
		});

		ztdx.setOnClickListener(this);
		czsj.setOnClickListener(this);
		sxpl.setOnClickListener(this);
		tbsj.setOnClickListener(this);
		cxll.setOnClickListener(this);
		czll.setOnClickListener(this);
		jcgx.setOnClickListener(this);
		fx.setOnClickListener(this);
		gywm.setOnClickListener(this);
		tbgpdm.setOnClickListener(this);
		tbggxx.setOnClickListener(this);
		tbxtsj.setOnClickListener(this);
		findViewById(R.id.tljr_img_set_fanhui).setOnClickListener(this);
		((TextView) findViewById(R.id.tljr_txt_set_dqbb))
				.setText("当前版本:" + Constant.appVersion + " 更新时间:" + Util.getMeteDate("RELEASETIME", this));
		((TextView) findViewById(R.id.tljr_txt_set_sxpl))
				.setText("当前为" + Constant.FlushTimeNames[Constant.preference.getInt(Constant.FLUSHKEY, 4)]);
		((TextView) findViewById(R.id.tljr_txt_set_tbgpdm)).setText(
				"同步股票代码(上次同步时间" + Constant.preference.getString(Constant.TBGPDMTIMEKEY, Util.getCurrentTime()) + ")");
		((TextView) findViewById(R.id.tljr_txt_set_tbggxx)).setText(
				"同步公告信息(上次同步时间" + Constant.preference.getString(Constant.TBGGXXTIMEKEY, Util.getCurrentTime()) + ")");
		((TextView) findViewById(R.id.tljr_txt_set_tbxtsj)).setText(
				"同步系统时间(上次同步时间" + Constant.preference.getString(Constant.TBXTSSTIMEKEY, Util.getCurrentTime()) + ")");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tljr_grp_set_zxzt:
			zxzt();
			break;
		case R.id.tljr_grp_set_czsj:
			cxsj();
			break;
		case R.id.tljr_grp_set_sxpl:
			sxpl();
			break;
		case R.id.tljr_grp_set_tbgpdm:
			tbgpdm();
			break;
		case R.id.tljr_grp_set_tbggxx:
			Message message = new Message();
			message.obj = "同步公告信息成功";
			handler.sendMessage(message);
			StartActivity.loadOnlineCfg();
			Constant.preference.edit().putString(Constant.TBGGXXTIMEKEY, Util.getCurrentTime()).commit();
			((TextView) findViewById(R.id.tljr_txt_set_tbggxx)).setText("同步公告信息(上次同步时间" + Util.getCurrentTime() + ")");
			break;
		case R.id.tljr_grp_set_tbxtsj:
			tbxtsj();
			break;
		case R.id.tljr_grp_set_tbsj:
			tbsjm.setVisibility(tbsjm.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
			img_tbsj.setRotation(tbsjm.getVisibility() == View.GONE ? 180 : 0);
			break;
		case R.id.tljr_grp_set_cxll:
			cxll();
			break;
		case R.id.tljr_grp_set_czll:
			czll();
			break;
		case R.id.tljr_grp_set_fx:
			sharedApp();
			break;
		case R.id.tljr_grp_set_gywm:
			aboutUs();
			break;
		case R.id.tljr_grp_set_jcgx:
			UmengUpdateAgent.forceUpdate(this);
			break;
		case R.id.tljr_grp_set_mzsm:
			disclaimer();
			break;
		case R.id.tljr_img_set_fanhui:
			this.finish();
			break;
		default:
			break;
		}
	}

	private void zxzt() {
		int index = Constant.preference.getInt(Constant.NEWSFONTSIZE, 2);
		new AlertDialog.Builder(this).setTitle("请选择资讯内容字体大小")
				.setSingleChoiceItems(Constant.FontSizeNames, index, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method
						// stub
						arg0.dismiss();
						Constant.preference.edit().putInt(Constant.NEWSFONTSIZE, arg1).commit();
						showMessage("设置成功,当前字体为" + Constant.FontSizeNames[arg1]);
					}
				}).setNegativeButton("取消", null).show();
	}

	private void sxpl() {
		int index = Constant.preference.getInt(Constant.FLUSHKEY, 4);
		new AlertDialog.Builder(this).setTitle("请选择无WIFI下刷新频率")
				.setSingleChoiceItems(Constant.FlushTimeNames, index, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method
						// stub
						arg0.dismiss();
						Constant.preference.edit().putInt(Constant.FLUSHKEY, arg1).commit();
						showMessage("设置成功,当前刷新频率为" + Constant.FlushTimeNames[arg1]);
						Constant.FlushTime = Constant.FlushTimes[arg1];
						((TextView) findViewById(R.id.tljr_txt_set_sxpl))
								.setText("当前为" + Constant.FlushTimeNames[arg1]);
					}
				}).setNegativeButton("取消", null).show();
	}

	private void tbgpdm() {
		handler.sendEmptyMessage(1);
		InitData.initGuMap(new Complete() {

			@Override
			public void complete() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(2);
				handler.sendEmptyMessage(3);
				showMessage("同步股票代码成功");
			}
		});
	}

	private void tbxtsj() {
		handler.sendEmptyMessage(1);
		NetUtil.sendGet(UrlUtil.URL_synTime, "", new NetResult() {

			@Override
			public void result(String msg) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(2);
				try {
					long time = Long.parseLong(msg);
					setDate(time);
					Message message = new Message();
					message.obj = "系统时间为:" + Util.getDate(time);
					handler.sendMessage(message);
					// AlarmManager am = (AlarmManager)
					// getSystemService(Context.ALARM_SERVICE);
					// am.setTime(time);
					handler.sendEmptyMessage(4);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Message message = new Message();
					message.obj = "更新系统时间失败";
					handler.sendMessage(message);
				}
			}
		});
	}

	public void setDate(long time) {
		try {
			Process process = Runtime.getRuntime().exec("su");
			String datetime = new SimpleDateFormat("yyyyMMdd.HHmmss", Locale.CHINA).format(new Date(time));
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("setprop persist.sys.timezone GMT\n");
			os.writeBytes("/system/bin/date -s " + datetime + "\n");
			os.writeBytes("clock -w\n");
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 重置数据
	private void cxsj() {
		new AlertDialog.Builder(this).setTitle("图灵金融").setMessage("警告:重置系统数据会清除已经保存的数据(如同步时间等用户数据),确定执行?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Constant.preference.edit().clear().commit();
						Configs.preference.edit().clear().commit();
						PreferenceUtils.getInstance().clearPreference();
						DataCleanManager.cleanApplicationData(SettingActivity.this.getApplication());
						DataCleanManager.cleanCustomCache(Util.sdPath);
						new AlertDialog.Builder(SettingActivity.this).setTitle("图灵金融").setMessage("重置成功,应用即将退出")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								restartApplication();
							}
						}).show();
					}
				}).setNegativeButton("取消", null).show();
	}

	private void cxll() {
		MyApplication.getInstance().getUidByte();
		long all = Constant.preference.getLong(Constant.LIULIANGKEY, (long) Constant.Liuliang);
		all = Math.max(all, (long) Constant.Liuliang);
		new AlertDialog.Builder(SettingActivity.this).setTitle("图灵金融")
				.setMessage("本次访问流量:" + Constant.Liuliang + "KB\n历史访问总流量:" + all + "KB\n上次重置时间:"
						+ Constant.preference.getString(Constant.LIULIANGTIMEKEY, "无数据"))
				.setPositiveButton("确定", null).show();
	}

	// 重置流量
	private void czll() {
		new AlertDialog.Builder(this).setTitle("图灵金融").setMessage("确定重置流量统计?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Constant.Liuliang = 0;
						MyApplication.all = 0;
						Constant.preference.edit().putLong(Constant.LIULIANGKEY, 0).commit();
						Constant.preference.edit()
								.putString(Constant.LIULIANGTIMEKEY, Util.getDate(System.currentTimeMillis())).commit();
					}
				}).setNegativeButton("取消", null).show();
	}

	private void restartApplication() {
		LoginResultReceiver.sendOnlineStop();
		MyApplication.getInstance().exit();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
		Intent intent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void aboutUs() {// 150176
		Intent intent = new Intent(this, WebActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("url", UrlUtil.aboutUsUrl);
		bundle.putString("name", "关于我们");
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void disclaimer() {
		Intent intent = new Intent(this, WebActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("url", UrlUtil.aliyun + "/h5/disclaimer.html");
		bundle.putString("name", "免责声明");
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void sharedApp() {
		startActivity(new Intent(this, ShareActivity.class));
	}

	public void showMessage(String msg) {
		Message message = new Message();
		message.obj = msg;
		handler.sendMessage(message);
	}

	@Override
	public void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		super.handleMsg(msg);
		switch (msg.what) {
		case 1:
			ProgressDlgUtil.showProgressDlg("", SettingActivity.this);
			break;
		case 2:
			ProgressDlgUtil.stopProgressDlg();
			break;
		case 3:
			Constant.preference.edit().putString(Constant.TBGPDMTIMEKEY, Util.getCurrentTime()).commit();
			((TextView) findViewById(R.id.tljr_txt_set_tbgpdm)).setText("同步股票代码(上次同步时间" + Util.getCurrentTime() + ")");
			break;
		case 4:
			Constant.preference.edit().putString(Constant.TBXTSSTIMEKEY, Util.getCurrentTime()).commit();
			((TextView) findViewById(R.id.tljr_txt_set_tbxtsj)).setText("同步系统时间(上次同步时间" + Util.getCurrentTime() + ")");
			break;
		default:
			showToast(msg.obj.toString());
			break;
		}
	};

}
