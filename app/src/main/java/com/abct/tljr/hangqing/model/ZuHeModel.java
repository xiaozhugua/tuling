package com.abct.tljr.hangqing.model;

import java.io.Serializable;


public class ZuHeModel implements Serializable{

	private String id;
	private String name;
	private String imgUrl;
	private String jsonData;
	private String time;
	private String beizhu;
	private String key;
	private float zong;
	private float yue;
	private float ri;
	private String zong_int=null;
	private String zong_float=null;
	private String yue_int=null;
	private String yue_float=null;
	private String ri_int=null;
	private String ri_float=null;
	private int rocketStatus=0;
	
	public ZuHeModel(String id,String name, String time, String beizhu, String imgUrl,String jsonData, String key) {
		this.id=id;
		this.name = name;
		this.time = time;
		this.beizhu = beizhu;
		this.imgUrl = imgUrl;
		this.jsonData = jsonData;
		this.key = key;
	}

	public ZuHeModel(String id,String name, String imgUrl,String jsonData, String key,
			String zong_int,String zong_float,String yue_int,String yue_float,String ri_int,String ri_float) {
		this.id=id;
		this.name = name;
		this.imgUrl = imgUrl;
		this.jsonData = jsonData;
		this.key = key;
		this.zong_int=zong_int;
		this.zong_float=zong_float;
		this.yue_int=yue_int;
		this.yue_float=yue_float;
		this.ri_int=ri_int;
		this.ri_float=ri_float;
	}
	
	public String getZong_int() {
		return zong_int;
	}

	public void setZong_int(String zong_int) {
		this.zong_int = zong_int;
	}

	public String getZong_float() {
		return zong_float;
	}

	public void setZong_float(String zong_float) {
		this.zong_float = zong_float;
	}

	public String getYue_int() {
		return yue_int;
	}

	public void setYue_int(String yue_int) {
		this.yue_int = yue_int;
	}

	public String getYue_float() {
		return yue_float;
	}

	public void setYue_float(String yue_float) {
		this.yue_float = yue_float;
	}

	public String getRi_int() {
		return ri_int;
	}

	public void setRi_int(String ri_int) {
		this.ri_int = ri_int;
	}

	public String getRi_float() {
		return ri_float;
	}

	public void setRi_float(String ri_float) {
		this.ri_float = ri_float;
	}

	public int getRocketStatus() {
		return rocketStatus;
	}

	public void setRocketStatus(int rocketStatus) {
		this.rocketStatus = rocketStatus;
	}

	public void setId(String id){
		this.id=id;
	}
	
	public String getId(){
		return this.id;
	}
	
	public float getZong() {
		return zong;
	}

	public void setZong(float zong) {
		this.zong = zong;
	}

	public float getYue() {
		return yue;
	}

	public void setYue(float yue) {
		this.yue = yue;
	}

	public float getRi() {
		return ri;
	}

	public void setRi(float ri) {
		this.ri = ri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}