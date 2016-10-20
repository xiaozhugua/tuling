package com.abct.tljr.news.channel.bean;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * ITEM的对应可序化队列属性
 *  */

public class ChannelItem extends RealmObject implements Serializable{
	 
	/**
	 * 栏目对应ID
	 *  */
	 private Integer id;
	/**
	 * 栏目对应NAME
	 *  */
    private String name;
	/**
	 * 栏目在整体中的排序顺序  rank
	 *  */
    private Integer orderId;
	/**
	 * 栏目是否选中
	 *  */
     private Integer selected;

	/*
	 * 频道类型
	 */
    private String species ;


  

    private String contentPictureUrl ;

    private int channelType ; // 0重要频道 1推荐频道 2其他频道

  //  private boolean hasCheck ;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
 

    public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }
 
 
    public String getContentPictureUrl() {
        return contentPictureUrl;
    }

    public void setContentPictureUrl(String contentPictureUrl) {
        this.contentPictureUrl = contentPictureUrl;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

//    public boolean isHasCheck() {
//        return hasCheck;
//    }
//
//    public void setHasCheck(boolean hasCheck) {
//        this.hasCheck = hasCheck;
//    }
}
