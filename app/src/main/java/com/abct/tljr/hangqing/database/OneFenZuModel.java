package com.abct.tljr.hangqing.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OneFenZuModel extends RealmObject {
	
	@PrimaryKey
	private String zuid;
	private String name;
	private String beizu;
	private String time;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getZuid() {
		return zuid;
	}
	public void setZuid(String zuid){
		this.zuid = zuid;
	}
	public String getBeizu() {
		return beizu;
	}
	public void setBeizu(String beizu) {
		this.beizu = beizu;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
	
}
