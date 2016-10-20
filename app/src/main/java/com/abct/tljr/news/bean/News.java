package com.abct.tljr.news.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;


/**
 * id title time url source purl
 * 
 * @author Administrator
 * 
 */

public class News extends RealmObject   {
	@PrimaryKey
	private String id;// ID
	private String title = "";// 标题
	private String summary = ""; // 新闻内容总结
	private String content = "";// 内容
	private Long time;// 时间
	private String imp_time;
	private String simple_time;
	private String source;// 新闻来源
	private String url;// 链接URL
	private String pUrl;// 图片URL
	private int SYXW_TYPE;
	private String type;
	private boolean isLive;// 是否为直播
	private String date = "";// 是否显示左边日期戳
	private int index;// 当前为第几条
	private String zan;// 赞量
	private String cai; // 踩量
	private String digest; // 摘要
	private String author;
	private String authorAvatar;
	private String collect; // 收藏量
	private long delete;// 删除量
	private long see;// 观看量
	private long speak;// 评论量
	private boolean isHaveZan;
	private boolean isHaveSee;
	private boolean isHaveCollect;
	private boolean isHaveCai;

	private String specialTitle;
	private String specialContent;

	private String comments; // 评论数

	private String tagIds; // 标签Id,选择用哪种标签
	 

	private String surl; // 分享新闻链接


	private String special;

	// news
 
	
	
	private String textDate;
	private String textHHmm;
	
	
	private String wxh;
	
	
	
	
	public String getWxh() {
		return wxh;
	}


	public void setWxh(String wxh) {
		this.wxh = wxh;
	}


	public String getTextHHmm() {
		return textHHmm;
	}


	public void setTextHHmm(String textHHmm) {
		this.textHHmm = textHHmm;
	}


	public String getTextDate() {
		return textDate;
	}


	public void setTextDate(String textDate) {
		this.textDate = textDate;
	}


	private boolean isLoadDetails;

	
	private int listLayout;
	private String nowTypeName ;
	
	private String defaultPicture;
	

	public String getDefaultPicture() {
		return defaultPicture;
	}


	public void setDefaultPicture(String defaultPicture) {
		this.defaultPicture = defaultPicture;
	}


	public int getListLayout() {
		return listLayout;
	}


	public void setListLayout(int listLayout) {
		this.listLayout = listLayout;
	}


 

	public String getNowTypeName() {
		return nowTypeName;
	}


	public void setNowTypeName(String nowTypeName) {
		this.nowTypeName = nowTypeName;
	}


	public String getTagIds() {
		return tagIds;
	}


	public void setTagIds(String tagIds) {
		this.tagIds = tagIds;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getSummary() {
		return summary;
	}


	public void setSummary(String summary) {
		this.summary = summary;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Long getTime() {
		return time;
	}


	public void setTime(Long time) {
		this.time = time;
	}


	public String getImp_time() {
		return imp_time;
	}


	public void setImp_time(String imp_time) {
		this.imp_time = imp_time;
	}


	public String getSimple_time() {
		return simple_time;
	}


	public void setSimple_time(String simple_time) {
		this.simple_time = simple_time;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getpUrl() {
		return pUrl;
	}


	public void setpUrl(String pUrl) {
		this.pUrl = pUrl;
	}


	public int getSYXW_TYPE() {
		return SYXW_TYPE;
	}


	public void setSYXW_TYPE(int sYXW_TYPE) {
		SYXW_TYPE = sYXW_TYPE;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public boolean isLive() {
		return isLive;
	}


	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public String getZan() {
		return zan;
	}


	public void setZan(String zan) {
		this.zan = zan;
	}


	public String getCai() {
		return cai;
	}


	public void setCai(String cai) {
		this.cai = cai;
	}


	public String getDigest() {
		return digest;
	}


	public void setDigest(String digest) {
		this.digest = digest;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getAuthorAvatar() {
		return authorAvatar;
	}


	public void setAuthorAvatar(String authorAvatar) {
		this.authorAvatar = authorAvatar;
	}


	public String getCollect() {
		return collect;
	}


	public void setCollect(String collect) {
		this.collect = collect;
	}


	public long getDelete() {
		return delete;
	}


	public void setDelete(long delete) {
		this.delete = delete;
	}


	public long getSee() {
		return see;
	}


	public void setSee(long see) {
		this.see = see;
	}


	public long getSpeak() {
		return speak;
	}


	public void setSpeak(long speak) {
		this.speak = speak;
	}




	public boolean isHaveZan() {
		return isHaveZan;
	}


	public void setHaveZan(boolean isHaveZan) {
		this.isHaveZan = isHaveZan;
	}


	public boolean isHaveSee() {
		return isHaveSee;
	}


	public void setHaveSee(boolean isHaveSee) {
		this.isHaveSee = isHaveSee;
	}


	public boolean isHaveCollect() {
		return isHaveCollect;
	}


	public void setHaveCollect(boolean isHaveCollect) {
		this.isHaveCollect = isHaveCollect;
	}


	public boolean isHaveCai() {
		return isHaveCai;
	}


	public void setHaveCai(boolean isHaveCai) {
		this.isHaveCai = isHaveCai;
	}


	public String getSpecialTitle() {
		return specialTitle;
	}


	public void setSpecialTitle(String specialTitle) {
		this.specialTitle = specialTitle;
	}


	public String getSpecialContent() {
		return specialContent;
	}


	public void setSpecialContent(String specialContent) {
		this.specialContent = specialContent;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}

 
 


	public String getSurl() {
		return surl;
	}


	public void setSurl(String surl) {
		this.surl = surl;
	}


	 


	public String getSpecial() {
		return special;
	}


	public void setSpecial(String special) {
		this.special = special;
	}

 
 


	 

	public boolean isLoadDetails() {
		return isLoadDetails;
	}


	public void setLoadDetails(boolean isLoadDetails) {
		this.isLoadDetails = isLoadDetails;
	}

	
	 
}
