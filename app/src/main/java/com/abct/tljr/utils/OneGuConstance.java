package com.abct.tljr.utils;

/**
 * momo写的关于onegu页面的获取URL地址的工具类
 */
public class OneGuConstance {

	public  static  final   String  TU_LING="http://ys.tuling.me/";//这里后期可能要修改
	public  static  final   String  AHDataAddress="http://news.tuling.me/";
	private static final String URL_CLASS = TU_LING+"YSServer/YSServlet?class=";
	private static final String URL_PACKAGE = "&package=me.tuling.ys.";
	private static final String URL_PARAMAS = ".&params=";

	/**
	 * 传入类名，包名，还有参数
	 */
	public static String getURL(String className, String packageName, String paramas) {
		return URL_CLASS + className + URL_PACKAGE + packageName + URL_PARAMAS + paramas;
	}
}
