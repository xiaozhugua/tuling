package com.abct.tljr.news;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.qh.common.CommonApplication;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

@SuppressLint("HandlerLeak")
public class NewsWebActivity extends BaseActivity {

	public final String Tag ="NewsWebActivity";
	public static final int ADD_COLLECT = 2;
	TextView btn_collect;
	NewsWebActivity activity;
	private WebView mWebView;
	private TextView txt_web_name;
	//private News news;

	String nid,species,time;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tljr_activity_web);
		activity = this;

		init();

		Intent intent = getIntent();
		Bundle bundleObject = getIntent().getExtras();
 
		String url = intent.getStringExtra("url");
		txt_web_name.setText(intent.getStringExtra("name"));
		
		nid =intent.getStringExtra("nid");
		species =intent.getStringExtra("species");
		time =intent.getStringExtra("time");
		
		
	   
		
		

		boolean hascollect =intent.getBooleanExtra("hascollect", false);
		if(hascollect){
			btn_collect.setBackgroundResource(R.drawable.new_fav_icon);
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
					NewsWebActivity.this.finish();
				}
			});
		}
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void init() {

		RelativeLayout rl_collect = (RelativeLayout) findViewById(R.id.tljr_webview_collect);
		rl_collect.setVisibility(View.VISIBLE);
		btn_collect = (TextView) findViewById(R.id.web_btn_collect);
		txt_web_name = (TextView) findViewById(R.id.tljr_txt_web_name);
		findViewById(R.id.tljr_img_web_fanhui).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				} else {
					NewsWebActivity.this.finish();
				}
			}
		});
		mWebView = (WebView) findViewById(R.id.tljr_webview);
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

				// loadView(view, url);
				return false;
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int newProgress) {
				if (100 == newProgress) {
					ProgressDlgUtil.stopProgressDlg();
				}
				super.onProgressChanged(view, newProgress);
			}
		});
	}

	private void loadView(final WebView webView, final String url) {
		// new Thread() {
		// public void run() {
		// mHandler.sendEmptyMessage(PROGRESS);

		ProgressDlgUtil.showProgressDlg("", NewsWebActivity.this);
		// String data =
		// "<meta name="+"\"applicable-device"+"\""+"content="+"\"mobile"+"\">";
		// webView.loadDataWithBaseURL(url, data, "text/html", "utf-8", null);
		Log.i("HQ", "final url:" + url);
		webView.loadUrl(url);
		//
		// super.run();
		// }

		// }.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event != null && event.getRepeatCount() == 0) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				NewsWebActivity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void btnConfirm(View view) {
		

		if (MyApplication.getInstance().self == null) {
			activity.showToast("未登录或注册无法完成操作");
			activity.login();
			return;
		}
		String	url = UrlUtil.URL_new+"api/uc/collect";
		String params = "uid=" + CommonApplication.getInstance().self.getId()
				+ "&nid=" + nid + "&species="
				+ species + "&time=" + time;
		
		LogUtil.i(Tag, url + "?" + params);
		NetUtil.sendPost(url, params, new NetResult() {

			@Override
			public void result(final String msg) {
				LogUtil.i(Tag, msg);
				try{
					JSONObject allJson =new JSONObject(msg);
					if (allJson == null) {
						activity.showToast("暂时无法连接服务器，请稍后再试");
						return;
					}
					String status = allJson.getString("status");
					String message = allJson.getString("msg");
					if (status.equals("1")) {
						btn_collect.setBackgroundResource(R.drawable.new_fav_icon);
					 
					} else {

//						newsActivity.showToast("删除收藏成功");
//						news.setHaveCollect(false);
//						tljr_btn_news_addCollect
//								.setBackgroundResource(R.drawable.img_news_shoucang1);
						
					}
					activity.showToast(message);
				}catch(Exception e){
					
				}

			}
		});

		
		 
	}
 
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ProgressDlgUtil.stopProgressDlg();
	}
}