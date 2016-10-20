package com.abct.tljr.news.bean;

public class Comment {

	private String id;
	private String name; // 用户名
	private String time; // 时间
	private String content; // 内容
	private String praise; // 赞数量
	private String cai; // 踩数量
	private String aurl; // 用户头像
	private String user_id;
	private String newsId;
	private String species;
	private boolean isZan;
	private boolean isCai;
	private Reply[] reply; // 子评论
	

 

	public Reply[] getReply() {
		return reply;
	}

	public void setReply(Reply[] reply) {
		this.reply = reply;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}



	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getAurl() {
		return aurl;
	}

	public void setAurl(String aurl) {
		this.aurl = aurl;
	}

	public String getCai() {
		return cai;
	}

	public void setCai(String cai) {
		this.cai = cai;
	}

	public boolean isZan() {
		return isZan;
	}

	public void setZan(boolean isZan) {
		this.isZan = isZan;
	}

	public boolean isCai() {
		return isCai;
	}

	public void setCai(boolean isCai) {
		this.isCai = isCai;
	}

	public String getNewsId() {
		return newsId;
	}

	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}

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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPraise() {
		return praise;
	}

	public void setPraise(String praise) {
		this.praise = praise;
	}

}
