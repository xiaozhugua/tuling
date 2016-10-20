package com.abct.tljr.ui.activity.tools;

/**
 * @author xbw
 * @version 创建时间：2015年11月14日 下午3:05:26
 */
public class OneBuild implements Cloneable {
	// enum TYPE {
	// stock, froex, future, options, constant
	// }

	private String productId;
	private String id;
	private String type = "+";// 类型 + - * /
	private String buildType; // 股票 期指 外汇 期权 常量
	private float value = 1.0f;// 值
	private String market;
	private String code;
	private String name;
	private boolean isDelete;// 确定删除
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBuildType() {
		return buildType;
	}

	public void setBuildType(String buildType) {
		this.buildType = buildType;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
