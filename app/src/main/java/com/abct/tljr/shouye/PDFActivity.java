package com.abct.tljr.shouye;

import java.io.File;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.data.Constant;
import com.abct.tljr.utils.DownloadProUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;
import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.DLUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.os.Bundle;

/**
 * @author xbw
 * @version 创建时间：2015年12月10日 下午2:43:57
 */
public class PDFActivity extends BaseActivity {
	private String filePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String s = getIntent().getData().getPath();
		LogUtil.e("pdf位置", s);
		this.filePath = s;
		File file = new File(Util.filePath + "/" + "PDF.apk");
		if (!file.exists()) {
			downLoadFile(UrlUtil.aliyun + "/apk/PDF.apk", "PDF阅读器", "PDF.apk",
					new Complete() {
						@Override
						public void complete() {
							openPdf();
						}
					});
		} else {
			openPdf();
		}
	}

	private void openPdf() {
		try {
			final File file = new File(Util.filePath + "/PDF.apk");
			DLPluginManager.getInstance(this).loadApk(file.getAbsolutePath(),true);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					PackageInfo info = DLUtils.getPackageInfo(PDFActivity.this,file.getAbsolutePath());
					DLPluginManager pluginManager = DLPluginManager.getInstance(PDFActivity.this);
					DLIntent intent = new DLIntent(info.packageName);
					File file1 = new File(filePath);
					intent.putExtra("path", file1.getAbsolutePath());
					pluginManager.startPluginActivity(PDFActivity.this, intent);
				}
			}, 200);
			finish();
		} catch (Exception e) {
			finish();
			showToast("读取文件失败，请重试!");
		}
	}

	private void downLoadFile(final String url, final String title,
			final String apkName, final Complete complete) {
		if (!Constant.netType.equals("WIFI")) {
			new AlertDialog.Builder(this)
					.setTitle("图灵金融")
					.setMessage("当前为" + Constant.netType + "网络，下载会消耗流量，确认下载？")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									DownloadProUtil.showProgressDlg(title,url,apkName,PDFActivity.this, true,complete);
								}
							}).setNegativeButton("否", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					}).show();

		} else {
			DownloadProUtil.showProgressDlg(title, url, apkName,
					PDFActivity.this, true, complete);
		}

	}
}
