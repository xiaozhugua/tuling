package com.abct.tljr.ranking;

/**
 * @author xbw
 * @version 创建时间：2015年8月8日 下午2:41:17 一个排行玩家的信息
 */
public class OnePerson {
	private int number;// 名次
	private String avatar;// 头像url
	private boolean isFollow;// 是否关注
	private String name;// 名字
	private String zhname;//组合名称
	private float value;// 成绩
	private float otherValue;
	private String source;
	private String uid;
	private String zhid;
	private long time;
	private String color;
	private int followNum;//关注人数
	private String purl;//收益曲线url

	public int getFollowNum() {
		return followNum;
	}

	public void setFollowNum(int followNum) {
		this.followNum = followNum;
	}

	public String getPurl() {
		return purl;
	}

	public void setPurl(String purl) {
		this.purl = purl;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isFollow() {
		return isFollow;
	}

	public void setFollow(boolean isFollow) {
		this.isFollow = isFollow;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getZhname() {
		return zhname;
	}

	public void setZhname(String zhname) {
		this.zhname = zhname;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	public float getOtherValue() {
		return otherValue;
	}

	public void setOtherValue(float otherValue) {
		this.otherValue = otherValue;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getZhid() {
		return zhid;
	}

	public void setZhid(String zhid) {
		this.zhid = zhid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
}
