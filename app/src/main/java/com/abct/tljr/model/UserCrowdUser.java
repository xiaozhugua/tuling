package com.abct.tljr.model;

import java.io.Serializable;
 
/**
 * Created by Administrator on 2016/1/28.
 */
public class UserCrowdUser implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String Uavata;
    long Udata;
    int Ulevel;
    String Uuid;
    String Unickname;
    
    int allMoney;
    int count;
    int money;
    long createData=0;
    
    public long getCreateData() {
		return createData;
	}

	public void setCreateData(long createData) {
		this.createData = createData;
	}

	public int getAllMoney() {
       return allMoney;
   }
 
   public void setAllMoney(int allMoney) {
       this.allMoney = allMoney;
   }
 
   public int getCount() {
       return count;
   }
 
   public void setCount(int count) {
       this.count = count;
   }
 
   public int getMoney() {
       return money;
   }
 
   public void setMoney(int money) {
       this.money = money;
   }
 
   public String getUavata() {
        return Uavata;
    }
 
    public void setUavata(String uavata) {
        Uavata = uavata;
    }
 
    public long getUdata() {
        return Udata;
    }
 
    public void setUdata(long udata) {
        Udata = udata;
    }
 
    public int getUlevel() {
        return Ulevel;
    }
 
    public void setUlevel(int ulevel) {
        Ulevel = ulevel;
    }
 
    public String getUuid() {
        return Uuid;
    }
 
    public void setUuid(String uuid) {
        Uuid = uuid;
    }
 
    public String getUnickname() {
        return Unickname;
    }
 
    public void setUnickname(String unickname) {
        Unickname = unickname;
    }
    
}

