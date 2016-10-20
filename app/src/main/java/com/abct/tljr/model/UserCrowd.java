package com.abct.tljr.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/28.
 */
public class UserCrowd implements Serializable{
	private static final long serialVersionUID = 1L;
	
	String CcrowdId;
    String Cid;
    long Cdate;
    boolean Cinit;
    int Cmoney;
    int allMoney;
    boolean isFocs;
    String msg;
    UserCrowdUser user = new UserCrowdUser();

    public UserCrowdUser getUser() {
        return user;
    }

    public void setUser(UserCrowdUser user) {
        this.user = user;
    }

    public String getCcrowdId() {
        return CcrowdId;
    }

    public void setCcrowdId(String ccrowdId) {
        CcrowdId = ccrowdId;
    }

    public String getCid() {
        return Cid;
    }

    public void setCid(String cid) {
        Cid = cid;
    }

    public long getCdate() {
        return Cdate;
    }

    public void setCdate(long cdate) {
        Cdate = cdate;
    }

    public boolean isCinit() {
        return Cinit;
    }

    public void setCinit(boolean cinit) {
        Cinit = cinit;
    }

    public int getCmoney() {
        return Cmoney;
    }

    public void setCmoney(int cmoney) {
        Cmoney = cmoney;
    }

	public int getAllMoney() {
		return allMoney;
	}

	public void setAllMoney(int allMoney) {
		this.allMoney = allMoney;
	}

	public boolean isFocs() {
		return isFocs;
	}

	public void setFocs(boolean isFocs) {
		this.isFocs = isFocs;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
    
}
