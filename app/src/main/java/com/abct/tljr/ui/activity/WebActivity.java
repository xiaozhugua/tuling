package com.abct.tljr.ui.activity;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.model.AppInfo;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class WebActivity extends BaseActivity {

	private int PROGRESS = 0;
	private int WEBVIEW = 1;
	private WebView mWebView;
	private TextView txt_web_name;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tljr_activity_web);
		findViewById(R.id.tljr_webview_collect).setVisibility(View.GONE);
		init();
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		if (intent.getStringExtra("name") != null) {
			txt_web_name.setText(intent.getStringExtra("name"));
		} else {
			findViewById(R.id.tljr_grp_web_top).setVisibility(View.GONE);
		}
		if (url == null || url.length() == 0)
			url = "http://www.baidu.com/";

		loadView(mWebView, url);

	}

	private boolean isfirst = true;

	public final class Back {// 这个Java 对象是绑定在另一个线程里的，
		public void back() {// js中对象,调用的方法
			if (isfirst) {
				isfirst = false;
				return;
			}
			post(new Runnable() {
				public void run() {
					WebActivity.this.finish();
				}
			});
		}
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void init() {
		txt_web_name = (TextView) findViewById(R.id.tljr_txt_web_name);
		findViewById(R.id.tljr_img_web_fanhui).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				} else {
					WebActivity.this.finish();
				}
			}
		});
		mWebView = (WebView) findViewById(R.id.tljr_webview);

		mWebView.setInitialScale(100);
		mWebView.addJavascriptInterface(new Back(), "Back");//
		// 后面字符串与js中调用的对象名对应
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.requestFocus();
		mWebView.setScrollBarStyle(0);
		mWebView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
					long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.e("WebActivity", url);
				if (url.indexOf(UrlUtil.Url_tuling) == 0)
					finish();
				return false;
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int newProgress) {
				if (100 == newProgress) {
					if (handler != null) {
						handler.sendEmptyMessage(WEBVIEW);
					}
				}
				super.onProgressChanged(view, newProgress);
			}
		});
	}

	private void loadView(final WebView webView, final String url) {
		webView.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event != null && event.getRepeatCount() == 0) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				WebActivity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void btnConfirm(View view) {

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ProgressDlgUtil.stopProgressDlg();
	}
}
