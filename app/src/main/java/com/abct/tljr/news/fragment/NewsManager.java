package com.abct.tljr.news.fragment;
 
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;
 




import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
 




import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
 




import com.abct.tljr.MyApplication;
import com.abct.tljr.news.bean.News;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
 
public class NewsManager {
 
   private final String Tag = "NewsJson";
   public ArrayList<News> currentList = new ArrayList<News>();
   public ArrayList<String> newsId = new ArrayList<String>();
   int listLayout;
   String nowTypeSpecial ;
   public Handler handler = new Handler();
   public NewsManager( ) {
   }
   public NewsManager(String nowTypeSpecial) {
       this.nowTypeSpecial =nowTypeSpecial ;
   }
 
   public boolean addNews(String special, JSONArray array, boolean isCover) {
 
       if (isCover) {
           currentList.clear();
           newsId.clear();
       }
       addNewsToList(array);
       return true;
   }
 
   public void setList(ArrayList<News> list) {
       this.currentList = list;
   }
 
   public ArrayList<News> getList() {
       ArrayList<News> tempList = new ArrayList<News>(currentList);
        sortList(tempList);
       return tempList;
   }
   
   public int getListSize(){
       return currentList.size();
   }
   
    
   
 
   Realm myRealm;
   RealmResults<News> results;
 
   public void initRealm() {
       if (myRealm == null) {
           try {
               myRealm = Realm.getDefaultInstance();
           } catch (RealmMigrationNeededException e) {
               RealmConfiguration config = new RealmConfiguration.Builder(
                       MyApplication.getInstance())
                       .deleteRealmIfMigrationNeeded().build();
               Realm.setDefaultConfiguration(config);
               myRealm = Realm.getDefaultInstance();
           }
 
       }
   }
 
   int num = 0;
   int temp = 20;
   public boolean isLoadAll;
 
   public ArrayList<News> getNoNetNews(boolean isNews) {
       ArrayList<News> tempList = new ArrayList<News>();
       if (myRealm == null) {
           initRealm();
       }
       results = myRealm.where(News.class).equalTo("special", nowTypeSpecial)
               .findAll() ;
       results.sort("time", Sort.DESCENDING);
       if (isNews) {	 
           num = 0;
           currentList.clear();
           newsId.clear(); 	 	 	
       }
       for (int i = 0; i < num + temp; i++) {
           if (i >= results.size()) {
               isLoadAll = true;
               break;
           }
           tempList.add(results.get(i));
       }
       num += tempList.size();
         LogUtil.i(Tag, "currentList size");
         currentList=tempList;
       return tempList;
   }
 
   // 暂无网络，下拉刷新加载缓存新闻
      Date  date = new Date(System.currentTimeMillis()); ;
      SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
   public void addNewsToList(final JSONArray array) {
	   
	   LogUtil.i("DownLoadService","addNewsToList");
       News news = null;
       Realm myRealm;
       try {
           myRealm = Realm.getDefaultInstance();
       } catch (RealmMigrationNeededException e) {
           RealmConfiguration config = new RealmConfiguration.Builder(
                   MyApplication.getInstance()).deleteRealmIfMigrationNeeded()
                   .build();
           Realm.setDefaultConfiguration(config);
           myRealm = Realm.getDefaultInstance();
       }
 
       myRealm.beginTransaction();
       for (int i = 0; i < array.length(); i++) {
    		LogUtil.i("DownLoadService", "加入新闻:"+i);
           JSONObject rs;
		try {
			rs = array.getJSONObject(i);

	           news = new News();
	 
	           news.setListLayout(listLayout);
	           
	           news.setId( rs.optString("id")  );
	           
	           
	            if(newsId.contains(news.getId())){
	            	continue;
	            }
	           
	           news.setSpecial(rs.optString("species"));
	           news.setType(rs.optString("type", "notype"));
	           news.setTitle(removeStr(rs.optString("title")));
	           news.setSummary(rs.optString("summary", ""));
	           news.setContent(rs.optString("content"));
	           news.setDigest(rs.optString("digest"));
	           news.setTime(rs.optLong("time"));
	           news.setSource(rs.optString("source"));
	           news.setUrl(rs.optString("url"));
	           news.setpUrl(rs.optString("purl"));
	           news.setZan(rs.optString("praise"));
	           news.setCai(rs.optString("oppose"));
	           news.setCollect(rs.optString("collects"));
	           news.setSurl(rs.optString("surl"));
	            
	           news.setHaveCai(rs.optBoolean("hasOppose", false));
	           news.setHaveZan(rs.optBoolean("hasPraise", false));
	           news.setHaveCollect(rs.optBoolean("hasCollect", false));
	            news.setHaveSee(rs.optBoolean("read", false));
	          
	           news.setDate(DateUtil.getDateMDhhmm(rs.optLong("time")));
	           news.setImp_time( getNewsDate(rs.optLong("time"), true));
	           news.setSimple_time( getNewsDate(rs.optLong("time"), false));
	           news.setTextHHmm(DateUtil.getDateOnlyHour(rs.optLong("time")));
	           date.setTime(rs.optLong("time"));
	           news.setTextDate(format.format(date));
	            
	           if (rs.has("tagIDs")) {
	               JSONArray tagIdsArray = rs.getJSONArray("tagIDs");
	               String tagIds = "";
	               if (tagIdsArray.length() > 0) {
	                   for (int j = 0; j < tagIdsArray.length(); j++) {
	                       tagIds = tagIdsArray.getString(j) + "," + tagIds;
	                   }
	                   news.setTagIds(tagIds.substring(0, tagIds.length() - 1));
	                   tagIds = null;
	               }
	 
	           }
	           newsId.add(news.getId());
	           currentList.add(news);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           myRealm.copyToRealmOrUpdate(news);
       }
       myRealm.commitTransaction();
       myRealm.close();
   }
   public void sortList(ArrayList<News> list) {
       Collections.sort(list, new Comparator<News>() {
           @Override
           public int compare(News n1, News n2) {
               return n2.getTime().compareTo(n1.getTime());
           }
       });
 
   }
 
   public int getListLayout() {
       return listLayout;
   }
 
   public void setListLayout(int listLayout) {
       this.listLayout = listLayout;
   }
 
   public String removeStr(String rs)
   {
       if(rs!=null && rs.length()>0){
           String str = rs.replaceAll("\\?", "").replaceAll("target=\"_blank\">", "").replaceAll("target=\"blank\">", "")
                   .replaceAll("target='_blank'>", "").replaceAll("target='blank'>", "").replaceAll("&nbsp;", "")
                   .replaceAll("\r", "").replaceAll("   ", "")
                   .replaceAll("@@@", "<br/> &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;").replaceAll("&sbquo;", ",");
           return str;
       }else{
           return "";
       }
   
   
   }
   public static  String getNewsDate(long time,boolean isPictureNews){
       Date date = new Date(time);
       Calendar cal = Calendar.getInstance();
       cal.setTimeInMillis(System.currentTimeMillis());
       String dayCurrent = cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
       cal.setTime(date);
       String day = cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
    
           if (day.equals(dayCurrent)) {
               return isPictureNews?DateUtil.getDateOnlyHour(time):"今天     " + DateUtil.getDateOnlyHour(time);
 
           } else {
               return DateUtil.getDateMDhhmm(time);
           } 
        
   
   }
}