package com.abct.tljr.hangqing.database;

import java.util.List;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;
import android.content.Context;
import com.abct.tljr.MyApplication;

public class ZiXuanGuDataBaseImpl {

	public static Realm mRealm=null;
	
	public static Realm ZiXuanGumImpl() {
		try {
			return  Realm.getInstance(new RealmConfiguration.Builder(
					MyApplication.getInstance()).name("zixuangu.realm").build());
		} catch (RealmMigrationNeededException e) {
			return Realm.getInstance(new RealmConfiguration.Builder(
					MyApplication.getInstance()).name("zixuangu.realm").deleteRealmIfMigrationNeeded().build());
		}
	}
	
	//更新保存所有股票
	public static void saveZiXuanGu(List<ZiXuanGu> listgu){
			try{
				mRealm=ZiXuanGumImpl();
				mRealm.beginTransaction();
				for(ZiXuanGu gu:listgu){
					mRealm.copyToRealmOrUpdate(gu);
				}
				mRealm.commitTransaction();
			}catch(Exception e){
				
			}
	}
	
	
	//查找所有股票
	public static RealmResults<ZiXuanGu> SelectAllZiXuanGu(Context context){
			try{
				mRealm=ZiXuanGumImpl();
				RealmResults<ZiXuanGu> result=mRealm.where(ZiXuanGu.class).findAll();
				result.sort("location",Sort.ASCENDING);
				return result;
			}catch(Exception e){
				return null;
			}
	}
	
	//删除一只股票
	public static void DeleteZiXuanGu(String id){
		try{
			mRealm=ZiXuanGumImpl();
			RealmResults<ZiXuanGu> result=mRealm.where(ZiXuanGu.class).equalTo("id",id).findAll();
			mRealm.beginTransaction();
			result.clear();
			mRealm.commitTransaction();
		}catch(Exception e){
			
		}
	}
	
	//删除所有股票
	public static void DeleteAllZiXuanGu(){
		try{
			mRealm=ZiXuanGumImpl();
			RealmResults<ZiXuanGu> result=mRealm.where(ZiXuanGu.class).findAll();
			mRealm.beginTransaction();
			result.clear();
			mRealm.commitTransaction();
		}catch(Exception e){
				
		}
	}
	
	
//	//保存所有数据
//	public static void saveZiXuanGu(Context context,List<ZiXuanGu> listgu){
//		try{
//			ZiXuanGuSQLDataHelp dbHelper=new ZiXuanGuSQLDataHelp(context,"zixuan.db",null,1);
//			SQLiteDatabase db=dbHelper.getWritableDatabase();
//			ContentValues Values=new ContentValues();
//			for(ZiXuanGu gu:listgu){
//				Values.put("id",gu.getId());
//				Values.put("code",gu.getCode());
//				Values.put("market",gu.getMarket());
//				Values.put("name",gu.getName());
//				Values.put("price",gu.getPrice());
//				Values.put("change",gu.getChange());
//				db.insert("zixuangu",null,Values);
//				Values.clear();
//			}
//		}catch(Exception e){
//			LogUtil.e("saveZiXuanGu",e.getMessage());
//		}
//	}
//	
//	//查找所有数据
//	public static List<ZiXuanGu> SelectAllZiXuanGu(Context context){
//		try{
//			ZiXuanGuSQLDataHelp dbHelper=new ZiXuanGuSQLDataHelp(context,"zixuan.db",null,1);
//			SQLiteDatabase db=dbHelper.getWritableDatabase();
////			Cursor cursor=db.query("zixuangu",null,null,null,null,null,"");
//			Cursor cursor=db.query("zixuangu",null,null,null,null,null,null,null);
//			List<ZiXuanGu> listgu=new ArrayList<ZiXuanGu>();
//			if(cursor.moveToFirst()){
//				ZiXuanGu zixuangu=null;
//				int code=cursor.getColumnIndex("code");
//				int id=cursor.getColumnIndex("id");
//				int market=cursor.getColumnIndex("market");
//				int name=cursor.getColumnIndex("name");
//				int price=cursor.getColumnIndex("price");
//				int change=cursor.getColumnIndex("change");
//				do{
////					zixuangu=new ZiXuanGu(cursor.getString(code),cursor.getString(id),
////							cursor.getString(market),cursor.getString(name),
////							cursor.getFloat(price), cursor.getFloat(change));
//					zixuangu=new ZiXuanGu();
//					zixuangu.setCode(cursor.getString(code));
//					zixuangu.setId(cursor.getString(id));
//					zixuangu.setMarket(cursor.getString(market));
//					zixuangu.setName(cursor.getString(name));
//					zixuangu.setPrice(cursor.getFloat(price));
//					zixuangu.setChange(cursor.getFloat(change));					
//					listgu.add(zixuangu);
//				}while(cursor.moveToNext());
//			}
//			LogUtil.e("listsize",listgu.size()+"");
//			return listgu;
//		}catch(Exception e){
//			LogUtil.e("SelectAllZiXuanGu",e.getMessage());
//			return null;
//		}
//	}
//	
//	public static void DeleteZiXuanGu(Context context,String id){
//		try{
//			ZiXuanGuSQLDataHelp dbHelper=new ZiXuanGuSQLDataHelp(context,"zixuan.db",null,1);
//			SQLiteDatabase db=dbHelper.getWritableDatabase();
//			db.delete("zixuangu","id=?",new String[]{id});
//		}catch(Exception e){
//			
//		}
//	}
//	
//	public static void UdpateZiXuanGu(Context context,List<ZiXuanGu> listgu){
//		try{
//			ZiXuanGuSQLDataHelp dbHelper=new ZiXuanGuSQLDataHelp(context,"zixuan.db",null,1);
//			SQLiteDatabase db=dbHelper.getWritableDatabase();
//			ContentValues values=null;
//			for(ZiXuanGu gu:listgu){
//				values=new ContentValues();
//				values.put("code",gu.getCode());
//				values.put("market",gu.getMarket());
//				values.put("name",gu.getName());
//				values.put("price",gu.getPrice());
//				values.put("change",gu.getChange());
//				db.update("zixuangu", values,"id=?",new String[]{gu.getId()});
//			}
//		}catch(Exception e){
//			
//		}
//	}
	
	
	
}
