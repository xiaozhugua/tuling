package com.abct.tljr.model;

public class OneItem {
	private int appType;// 应用类型 apk/html
	private String id;// apk Id 用于点击次数添加
	private String url;// apk包url 用于下载apk
	private String appName;// app名
	private String avatar;// 图片url 显示的图片icon
	private String title;// 名称  显示的名称
	private String version;// 版本  用于判断是否有新版本
	private int users;// 使用量  
	private String packageName;// 包名
	private int type;// 所属类别  
	private float level;// 评级
	private String msg;// 描述

	public OneItem(int appType, String id, String url, String appName,
			String packageName, String avatar, String title, String version,
			int type, int users, float level, String msg) {
		this.appType = appType;
		this.id = id;
		this.url = url;
		this.appName = appName;
		this.packageName = packageName;
		this.avatar = avatar;
		this.title = title;
		this.version = version;
		this.type = type;
		this.users = users;
		this.level = level;
		this.msg = msg;
		if (this.appName.equals("")) {
			this.appName = this.url.substring(this.url.lastIndexOf("/") + 1,
					this.url.lastIndexOf("."));
		}
	}
	
	
	
	public String getPackageName() {
		return packageName;
	}



	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}



	public OneItem(String avatar, String title){
		this.avatar = avatar;
		this.title = title;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getUsers() {
		return users;
	}

	public void setUsers(int users) {
		this.users = users;
	}

}
