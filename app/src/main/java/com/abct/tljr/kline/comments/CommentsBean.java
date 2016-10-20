package com.abct.tljr.kline.comments;
 
public class CommentsBean {
   private String avatar;
   private String cid;
   private String content;
   private String id ;//评论的ID
   private long time ;
   private int praise;//赞
   private int tread;//踩
   private boolean ispraise = false;
   private boolean istread = false;
   private String uid;
   private String uname;
   private String comment_content;
   private String oid;
   
   
   public boolean isIspraise() {
	return ispraise;
	}
	public void setIspraise(boolean ispraise) {
		this.ispraise = ispraise;
	}
	public boolean isIstread() {
		return istread;
	}
	public void setIstread(boolean istread) {
		this.istread = istread;
	}
	public String getOid() {
       return oid;
   }
   public void setOid(String oid) {
       this.oid = oid;
   }
   public String getAvatar() {
       return avatar;
   }
   public void setAvatar(String avatar) {
       this.avatar = avatar;
   }
   public String getCid() {
       return cid;
   }
   public void setCid(String cid) {
       this.cid = cid;
   }
   public String getContent() {
       return content;
   }
   public void setContent(String content) {
       this.content = content;
   }
   public String getId() {
       return id;
   }
   public void setId(String id) {
       this.id = id;
   }
   public long getTime() {
       return time;
   }
   public void setTime(long time) {
       this.time = time;
   }
   public int getPraise() {
       return praise;
   }
   public void setPraise(int praise) {
       this.praise = praise;
   }
   public int getTread() {
       return tread;
   }
   public void setTread(int tread) {
       this.tread = tread;
   }
   public String getUid() {
       return uid;
   }
   public void setUid(String uid) {
       this.uid = uid;
   }
   public String getUname() {
       return uname;
   }
   public void setUname(String uname) {
       this.uname = uname;
   }
   public String getComment_content() {
       return comment_content;
   }
   public void setComment_content(String comment_content) {
       this.comment_content = comment_content;
   }
}