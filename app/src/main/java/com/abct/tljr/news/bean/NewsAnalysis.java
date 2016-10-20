//package com.abct.tljr.news.bean;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//import android.util.Log;
//
//import com.abct.tljr.utils.Util;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//
//public class NewsAnalysis {
// 
// 
//
//	public ArrayList<News> addNewsToList(String special,JSONArray news_array,ArrayList<News> newsList){
//		 
//		
//         for (int i = 0; i < news_array.size(); i++) {
//             JSONObject rs = news_array.getJSONObject(i);
//             
//             
//             News news = new News(); 
//             
//             news.setId(rs.containsKey("id")?rs.getString("id"):"11111");
//             news.setSpecial(special);
//             news.setLetterSpecies(rs.getString("species"));
//             news.setType(rs.containsKey("type")?rs.getString("type"):"notype");
//             news.setTitle(rs.getString("title"));
//             news.setContent(rs.getString("content"));
//             news.setTime(rs.getString("time"));
//             news.setSource(rs.getString("source"));
//             news.setUrl(rs.getString("url"));
//              news.setpUrl(rs.getString("purl"));            
//             news.setZan(rs.getString("praise"));
//             news.setCollect(rs.getString("collects"));
//             news.setSurl(rs.getString("surl"));
//             
//             news.setHaveCai( rs.containsKey("hasOppose")?rs.getBoolean("hasOppose")  :false  );
//             news.setHaveZan( rs.containsKey("hasPraise")?rs.getBoolean("hasPraise")  :false  );
//             news.setHaveCollect( rs.containsKey("hasCollect")?rs.getBoolean("hasCollect")  :false  );
//             
//             
//             if(rs.containsKey("startColor")){
//                 news.setStartColor(rs.getString("startColor"));
//             }
//             if(rs.containsKey("middleColor")){
//                 news.setMiddleColor(rs.getString("middleColor"));
//             }
//             if(rs.containsKey("endColor")){
//                 news.setEndColor(rs.getString("endColor"));
//             }
//             
//             
//             if(rs.containsKey("read")){            //已阅
//                  news.setHaveSee(Boolean.valueOf(rs.getString("read")));
//                  Log.i("read",rs.getString("read")+"----"+ rs.getString("id") +"-----RealRead:"+ Boolean.valueOf(rs.getString("read"))) ;
//             }else{
//                  news.setHaveSee(false);
//             }
//             
//             
//             //tagId
//             if(rs.containsKey("tagIDs")){
//                 
//             JSONArray tagIdsArray = rs.getJSONArray("tagIDs") ;
//             String[] tagIds = new String[tagIdsArray.size()];
//             for (int j = 0; j < tagIdsArray.size(); j++) {
//                 tagIds[j] = tagIdsArray.getString(j);
//             }
//             news.setTagIds(tagIds);
//             }
//             
//             //tagPosition
//             if(rs.containsKey("tagPos")){
//                 
//             JSONArray tagPosArray = rs.getJSONArray("tagPos") ;
//             String[] tagPos = new String[tagPosArray.size()];
//             for (int k = 0; k < tagPosArray.size(); k++) {
//                 tagPos[k] = tagPosArray.getString(k);
//             }
//             news.setTagPos(tagPos);
//             }
//             
//             
//             
//             news.setSpecialTitle(Util.myTrim(Util.delHTMLTag2(rs.getString("title"))));
//             news.setSpecialContent(Util.myTrim(Util.delHTMLTag(rs.getString("content"))));
//             news.setDate(news.getTime().substring(5, 10)
//                 .replaceFirst("-", "月")
//                 + "日");
//             try {
//                 news.setImp_time(getStandardDate(rs.getString("time")));
//             } catch (ParseException e) {
//                 // TODO Auto-generated catch block
//                 e.printStackTrace();
//             }
//             newsList.add(news);
//
//         }
//      
//		return newsList;
//       
//            
//		 
//	}
//	
//    public static String getStandardDate(String timeStr) throws ParseException {
//    	  
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
//                Locale.getDefault());
//        Date date = sdf.parse(timeStr);
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(System.currentTimeMillis());
//        String dayCurrent = cal.get(Calendar.MONDAY) + "-"
//                + cal.get(Calendar.DAY_OF_MONTH);
//        cal.setTime(date);
//        String day = cal.get(Calendar.MONDAY) + "-"
//                + cal.get(Calendar.DAY_OF_MONTH);
// 
//        if (day.equals(dayCurrent)) {
//            return timeStr.substring(11, 16);
// 
//        } else {
//            return timeStr.substring(5, 16);
//        }
// 
//    }
//}
