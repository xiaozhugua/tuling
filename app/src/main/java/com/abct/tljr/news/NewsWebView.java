package com.abct.tljr.news;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.abct.tljr.data.Constant;
import com.abct.tljr.news.bean.News;

public class NewsWebView {
	NewsActivity newsActivity;
	WebView webview;
	News news;
	private ArrayList<String> uList = new ArrayList<String>();

	public NewsWebView(WebView webView, News news, NewsActivity newsActivity) {
		this.webview = webView;
		this.newsActivity = newsActivity;
		this.news = news;
		init();
	}

	public void init()
	{
		
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.TEXT_AUTOSIZING);
		} else {
			webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		}
		
	//	webview.setFocusable(false);
	//	webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); // 图片自适应
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webview.getSettings().setUseWideViewPort(false);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setBlockNetworkImage(Constant.noPictureMode); // 无图模式
		webview.getSettings().setBlockNetworkImage(Constant.netType.equals("未知"));

		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{ // 屏蔽超链接
				// loadView(view, url);
				return true;
			}
		});

		webview.addJavascriptInterface(new JavascriptInterface(newsActivity), "imagelistner");
		webview.setWebViewClient(new MyWebViewClient());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
		} else {
			webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		}
	}

	public void changNews( )
	{
		
		String k = news.getContent()==null?news.getSummary():news.getContent();
		
		String htmlCode =  delATag(k.replaceAll("href", ""));


		// 加载网页
		webview.loadDataWithBaseURL(null, htmlCode, "text/html", "utf-8", null);

		/*w
		 * 新闻内图片,遍历获取链接地址 用于 ShowWebImageActivity
		 */
		Document doc_Dis = Jsoup.parse(htmlCode);
		Elements ele_Img = doc_Dis.getElementsByTag("img");
		if (ele_Img.size() != 0)
		{
			for (Element e_Img : ele_Img)
			{
				String img = e_Img.attr("src");
				uList.add(img);
			}
		}
		 
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{

			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{

			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			addImageClickListner();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{

			super.onReceivedError(view, errorCode, description, failingUrl);
			addImageClickListner();
		}
	}

	// 注入js函数监听
	private void addImageClickListner()
	{
		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
		webview.loadUrl("javascript:(function(){" + "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)  " + "{" + "    objs[i].onclick=function()  " + "    {  "
				+ "  window.imagelistner.openImage(this.src);  " + "    }  " + "}" + "})()");
	}

	// js通信接口

	public class JavascriptInterface {

		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		@android.webkit.JavascriptInterface
		public void openImage(String img)
		{
			// System.out.println(img);
			Intent intent = new Intent();
			intent.putExtra("image", img);
			intent.putExtra("ulist", uList);
			intent.setClass(context, ShowWebImageActivity.class);
			context.startActivity(intent);

		}
	}

	public static String delATag(String content)
	{
		Pattern p = Pattern.compile("</?a[^>]*>");
		Matcher m = p.matcher(content);
		content = m.replaceAll("");
		return content;
	}
 

}
