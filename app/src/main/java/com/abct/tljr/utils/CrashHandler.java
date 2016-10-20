package com.abct.tljr.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.abct.tljr.MyApplication;
import com.abct.tljr.data.Constant;
import com.pgyersdk.crash.PgyCrashManager;
import com.qh.common.util.LogUtil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author user
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";

	// CrashHandler 实例
	private static CrashHandler INSTANCE = new CrashHandler();

	// 程序的 Context 对象
	private Context mContext;

	// 系统默认的 UncaughtException 处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 保证只有一个 CrashHandler 实例 */
	private CrashHandler() {
	}

	/** 获取 CrashHandler 实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;

		// 获取系统默认的 UncaughtException 处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

		// 设置该 CrashHandler 为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当 UncaughtException 发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}

			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
	 * 
	 * @param ex
	 * @return true：如果处理了该异常信息；否则返回 false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		// 使用 Toast 来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				long now = System.currentTimeMillis();
				long lastTime = Constant.preference.getLong("ISRESTART",
						System.currentTimeMillis());
				if (now - lastTime > 60000 || now == lastTime) {
					Toast.makeText(mContext, "很抱歉，图灵出现错误，即将重启",
							Toast.LENGTH_LONG).show();
					MyApplication.getInstance().exit();
					Intent intent = MyApplication
							.getInstance()
							.getPackageManager()
							.getLaunchIntentForPackage(
									MyApplication.getInstance()
											.getPackageName());
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mContext.startActivity(intent);
					Constant.preference.edit().putLong("ISRESTART", now)
							.commit();
				} else {
					Toast.makeText(mContext, "很抱歉，图灵出现错误，即将退出",
							Toast.LENGTH_LONG).show();
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							MyApplication.getInstance().exit();
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(0);
						}
					}, 1000);

				}
				Looper.loop();
			}
		}.start();

		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);

			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存错误信息到文件中 *
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private void saveCrashInfo2File(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		// MobclickAgent.reportError(mContext, ex);
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		sb.append(result);
		LogUtil.e("程序错误", sb.toString());
		PgyCrashManager.reportCaughtException(mContext, new Exception(result));
		// try {
		// long timestamp = System.currentTimeMillis();
		// String time = formatter.format(new Date());
		// String fileName = "crash-" + time + "-" + timestamp + ".log";
		//
		// if (Environment.getExternalStorageState().equals(
		// Environment.MEDIA_MOUNTED)) {
		// String path = "/sdcard/tljr/crash/";
		// File dir = new File(path);
		// if (!dir.exists()) {
		// dir.mkdirs();
		// }
		// FileOutputStream fos = new FileOutputStream(path + fileName);
		// fos.write(sb.toString().getBytes());
		// fos.close();
		// }
		//
		// return fileName;
		// } catch (Exception e) {
		// Log.e(TAG, "an error occured while writing file...", e);
		// }
	}
}