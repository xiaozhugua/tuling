package com.abct.tljr.hangqing.database;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import java.util.List;
import java.util.Map;
import android.content.Context;
import com.abct.tljr.MyApplication;
import com.abct.tljr.hangqing.model.ZiXuanOneGu;
import com.qh.common.util.LogUtil;

public class ZiXuanGuRealmImpl {
	
	public static Realm mRealm=null;
	
	public static Realm GetZiXuanGuRealmImpl(){
		try {
			return Realm.getInstance(new RealmConfiguration.Builder(
					MyApplication.getInstance()).name("ZiXuanGuData.realm").build());
		} catch (Exception e) {
			return Realm.getInstance(new RealmConfiguration.Builder(
					MyApplication.getInstance()).name("ZiXuanGuData.realm").deleteRealmIfMigrationNeeded().build());
		}
	}
	
	//增加更新所有股
	public static void saveAndUpdateZiXuanGu(Map<String,ZiXuanOneGu> mapGu){
		try{
			mRealm=GetZiXuanGuRealmImpl();
			mRealm.beginTransaction();
			for(String key:mapGu.keySet()){
				mRealm.copyToRealmOrUpdate(mapGu.get(key));
			}
			mRealm.commitTransaction();
		}catch(Exception e){
			
		}
	}

	public static void saveAndUpdateListZiXuanGu(List<ZiXuanOneGu> listGus){
		try{
			mRealm=GetZiXuanGuRealmImpl();
			mRealm.beginTransaction();
			for(ZiXuanOneGu gu:listGus){
				mRealm.copyToRealmOrUpdate(gu);
			}
			mRealm.commitTransaction();
		}catch(Exception e){
			
		}
	}
	
	//增加一只股
	public static void addOneGu(ZiXuanOneGu mOneGu){
		try{
			mRealm=GetZiXuanGuRealmImpl();
			mRealm.beginTransaction();
			mRealm.copyToRealm(mOneGu);
			mRealm.commitTransaction();
		}catch(Exception e){
			
		}		
	}
	
	//删除一只股
	public static void RemoveOneGu(String zuidkey,String zuid){
		try{
			mRealm=GetZiXuanGuRealmImpl();
		    RealmResults<ZiXuanOneGu> mRealmResult=mRealm.where(ZiXuanOneGu.class).equalTo("zuidkey",zuidkey).equalTo("zuid",zuid).findAll();
		    mRealm.beginTransaction();
		    mRealmResult.clear();
		    mRealm.commitTransaction();
		}catch(Exception e){
			LogUtil.e("RemoveOneGu",e.getMessage());
		}
	}
	
	//查找所有股票
	public static RealmResults<ZiXuanOneGu> SelectAllGu(Context context){
		try{
			mRealm=GetZiXuanGuRealmImpl();
			RealmResults<ZiXuanOneGu> result=mRealm.where(ZiXuanOneGu.class).findAll();
			return result;
		}catch(Exception e){
			return null;
		}
	}

	//查找一只股票
	public static ZiXuanOneGu SelectOneGu(String code,String zuid){
		try{
			mRealm=GetZiXuanGuRealmImpl();
			ZiXuanOneGu onegu= mRealm.where(ZiXuanOneGu.class).equalTo("code",code).equalTo("zuid",zuid).findFirstAsync();
			return onegu;
		}catch(Exception e){
			return null;
		}
	}
	
	//查找一个组合的股票
	public static RealmResults<ZiXuanOneGu> SelectOneGu(String zuid){
		try{
			mRealm=GetZiXuanGuRealmImpl();
			RealmResults<ZiXuanOneGu> listRealm=mRealm.where(ZiXuanOneGu.class).equalTo("zuid",zuid).findAll();
			return listRealm;
		}catch(Exception e){
			return null;
		}
	}

	//删除一个组合的股票
	public static void removeZuCode(String zuid){
		try{
			mRealm=GetZiXuanGuRealmImpl();
			RealmResults<ZiXuanOneGu> listGu=mRealm.where(ZiXuanOneGu.class).equalTo("zuid",zuid).findAll();
			mRealm.beginTransaction();
			for(int i=0;i<listGu.size();i++){
				listGu.get(i).removeFromRealm();
			}
			mRealm.commitTransaction();
		}catch(Exception e){
			
		}
	}
	
	//删除所有股票
	public static void removeAllCode(){
		try{
			mRealm=GetZiXuanGuRealmImpl();
			RealmResults<ZiXuanOneGu> listGu=mRealm.where(ZiXuanOneGu.class).findAll();
			mRealm.beginTransaction();
			listGu.clear();
			mRealm.commitTransaction();
		}catch(Exception e){
		}
	}
	
}

