package com.abct.tljr.hangqing.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ZiXuanOneGu extends RealmObject{

	@PrimaryKey
	private String zuidkey;
	private String zuid;
	private String key;
	private int section = 0;// 判断是不是标题
	private int checkstatus = 0;// 自选选择删除
	private boolean checkreult = false;// 回复check状态
	private String name;// 股票名称
	private double yclose;// 昨收
	private double now;// 现在价格
	private double first;// 第一次添加价格
	private float change;// 变动价格
	private float p_change;// 变动百分比
	private String p_changes;// 变动百分比
	private String market;// 所属如sz,sh
	private String pyName;// 简写如MSYX
	private String code;// 股票代码
	public String getZuid() {
		return zuid;
	}

	public void setZuid(String zuid) {
		this.zuid = zuid;
	}

	private String marketCode;
	private String tag;// 自己添加标签
	private long time;// 自己创建时间
	private int top;// 置顶
	private int percent = 0;// 调整仓位
	private String id;// 股票id
	private double kaipanjia;
	
	public ZiXuanOneGu(){}
	
	public ZiXuanOneGu(String code, String name, double first, float p_change,
			String market, String key, String tag, long time, int top,
			String id, String zuid,String zuidkey) {
		this.code = code;
		this.name = name;
		this.first = first;
		this.p_change = p_change;
		this.market = market;
		this.key = key;
		this.tag = tag;
		this.time = time;
		this.top = top;
		this.id = id;
		this.zuid= zuid;
		this.zuidkey=zuidkey;
	}
	
	public String getZuidkey() {
		return zuidkey;
	}

	public void setZuidkey(String zuidkey) {
		this.zuidkey = zuidkey;
	}

	public String getMarketCode() {
		return marketCode;
	}

	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}

	public boolean isCheckreult() {
		return checkreult;
	}

	public void setCheckreult(boolean checkreult) {
		this.checkreult = checkreult;
	}

	public int getCheckstatus() {
		return checkstatus;
	}

	public void setCheckstatus(int checkstatus) {
		this.checkstatus = checkstatus;
	}

	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPercent() {
		return this.percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getYclose() {
		return yclose;
	}

	public void setYclose(double yclose) {
		this.yclose = yclose;
	}

	public double getNow() {
		return now;
	}

	public void setNow(double now) {
		this.now = now;
	}

	public double getFirst() {
		return first;
	}

	public void setFirst(double first) {
		this.first = first;
	}

	public float getChange() {
		return change;
	}

	public void setChange(float change) {
		this.change = change;
	}

	public float getP_change() {
		return p_change;
	}

	public void setP_change(float p_change) {
		this.p_change = p_change;
	}

	public String getP_changes() {
		return p_changes;
	}

	public void setP_changes(String p_changes) {
		this.p_changes = p_changes;
	}

	public String getPyName() {
		return pyName;
	}

	public void setPyName(String pyName) {
		this.pyName = pyName;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public double getKaipanjia() {
		return kaipanjia;
	}

	public void setKaipanjia(double kaipanjia) {
		this.kaipanjia = kaipanjia;
	}	
	
}
