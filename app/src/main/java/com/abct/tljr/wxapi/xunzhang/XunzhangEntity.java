package com.abct.tljr.wxapi.xunzhang;


public class XunzhangEntity {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	String name,detail,iconUrl;
	boolean has;
	public boolean isHas() {
		return has;
	}
	public void setHas(boolean has) {
		this.has = has;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
}
