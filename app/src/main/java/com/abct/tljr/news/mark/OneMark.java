package com.abct.tljr.news.mark;

/**
 * @author xbw
 * @version 创建时间：2015年9月7日 下午4:23:21
 */
public class OneMark {
	
	private String id;
	private boolean isMark;// 是否已关注
	private String name;// 名字
	private String avatar;// 头像url
	private String info;// 信息
	private String number;// 公众号
	
	

//	private int num;// 关注人数
//	private String url;
//	
//	
//	private int type;//类型

	
	 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}
//
//	public int getNum() {
//		return num;
//	}
//
//	public void setNum(int num) {
//		this.num = num;
//	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isMark() {
		return isMark;
	}

	public void setMark(boolean isMark) {
		this.isMark = isMark;
	}

//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}
	
}
