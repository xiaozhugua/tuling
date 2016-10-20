package com.abct.tljr.hangqing.model;

public class CodePercent {

	private String codename;
	private double codeprice;
	private int cangwei;
	private String market;
	private double kaipanjia;
	private String code;
	private String key;
	private String zuname;
	private float change;
	
	public CodePercent(String zuname,String codename,double codeprice,int cangwei,String market,
			double kaipanjia,String code,String key,float change){
		this.zuname=zuname;
		this.codename = codename;
		this.codeprice = codeprice;
		this.cangwei = cangwei;
		this.market=market;
		this.kaipanjia=kaipanjia;
		this.code=code;
		this.key=key;
		this.change=change;
	}
	
	public float getChange() {
		return change;
	}
	public void setChange(float change) {
		this.change = change;
	}
	public String getZuname(){
		return this.zuname;
	}
	public void setZuname(String zuname){
		this.zuname=zuname;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public double getKaipanjia() {
		return kaipanjia;
	}
	public void setKaipanjia(double kaipanjia) {
		this.kaipanjia = kaipanjia;
	}
	public String getCodename() {
		return codename;
	}
	public void setCodename(String codename) {
		this.codename = codename;
	}
	public double getCodeprice() {
		return codeprice;
	}
	public void setCodeprice(double codeprice) {
		this.codeprice = codeprice;
	}
	public int getCangwei() {
		return cangwei;
	}
	public void setCangwei(int cangwei) {
		this.cangwei = cangwei;
	}
	public void setMarket(String market){
		this.market=market;
	}
	public String getMarket(){
		return this.market;
	}
	
	
}
