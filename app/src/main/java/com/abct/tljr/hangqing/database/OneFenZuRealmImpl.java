package com.abct.tljr.hangqing.database;

import java.util.List;

import com.abct.tljr.MyApplication;
import com.abct.tljr.model.OneGu;
import com.qh.common.util.LogUtil;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class OneFenZuRealmImpl {
	
	public static Realm mRealm=null;
	
	public static Realm GetOneFenZuRealmImpl() {
		try {
			return  Realm.getInstance(new RealmConfiguration.Builder(
					MyApplication.getInstance()).name("ZuHeData.realm").build());
		} catch (RealmMigrationNeededException e) {
			return Realm.getInstance(new RealmConfiguration.Builder(
					MyApplication.getInstance()).name("ZuHeData.realm").deleteRealmIfMigrationNeeded().build());
		}
	}

	// 更新组合
	public static void addUpdateZuHe(List<OneFenZuModel> listzu) {
		try {
			mRealm=GetOneFenZuRealmImpl();
			mRealm.beginTransaction();
			for (OneFenZuModel zu : listzu) {
				mRealm.copyToRealmOrUpdate(zu);
			}
			mRealm.commitTransaction();
		} catch (Exception e) {

		}
	}

	// 添加组合
	public static void addZuHe(OneFenZuModel zu) {
		try {
			mRealm=GetOneFenZuRealmImpl();
			mRealm.beginTransaction();
			mRealm.copyToRealmOrUpdate(zu);
			mRealm.commitTransaction();
		} catch (Exception e) {

		}
	}

	// 删除组合
	public static void deleteZuHe(String zuid) {
		try {
			mRealm=GetOneFenZuRealmImpl();
			RealmResults<OneFenZuModel> listmodel = mRealm.where(OneFenZuModel.class).equalTo("zuid", zuid).findAll();
			mRealm.beginTransaction();
			for (int i = 0; i < listmodel.size(); i++) {
				listmodel.get(i).removeFromRealm();
			}
			mRealm.commitTransaction();
		} catch (Exception e) {
			LogUtil.e("OneFenZu_Exception", e.getMessage());
		}
	}

	// 查找所有组合
	public static RealmResults<OneFenZuModel> SelectAllZuHe() {
		try {
			mRealm=GetOneFenZuRealmImpl();
			RealmResults<OneFenZuModel> result = mRealm.where(OneFenZuModel.class).findAll();
			return result;
		} catch (Exception e) {
			LogUtil.e("OneFenZuResult_all", e.getMessage());
			return null;
		}
	}

	//删除所有组合
	public static void DeleteAllZu(){
		try{
			mRealm=GetOneFenZuRealmImpl();
			RealmResults<OneFenZuModel> listmodel = mRealm.where(OneFenZuModel.class).findAll();
			mRealm.beginTransaction();
			listmodel.clear();
			mRealm.commitTransaction();
		}catch(Exception e){
			
		}
	}
	
	// 查找一只组合
	public static OneGu SelectOneGu(String zuid) {
		try {
			mRealm=GetOneFenZuRealmImpl();
			OneGu gu= mRealm.where(OneGu.class).equalTo("zuid", zuid).findFirstAsync();
			return gu;
		} catch (Exception e) {
			return null;
		}
	}

}
