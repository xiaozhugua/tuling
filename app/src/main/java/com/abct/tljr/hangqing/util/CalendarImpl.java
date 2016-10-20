package com.abct.tljr.hangqing.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarImpl {

	 	private static Date getMonthStart(Date date) {
		   Calendar calendar = Calendar.getInstance();
	         calendar.setTime(date);
	         calendar.add(Calendar.MONTH, -1);
	         int index = calendar.get(Calendar.DAY_OF_MONTH);
	         calendar.add(Calendar.DATE, (1 - index));
	         return calendar.getTime();
	    }
	 
	    private static Date getMonthEnd(Date date) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        calendar.add(Calendar.MONTH, 1);
	        int index = calendar.get(Calendar.DAY_OF_MONTH);
	        calendar.add(Calendar.DATE, (-index));
	        return calendar.getTime();
	    }
	 
	    private static Date getNext(Date date) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        calendar.add(Calendar.DATE, 1);
	        return calendar.getTime();
	    }

	    
	@SuppressLint("SimpleDateFormat")
	public static ArrayList<String> getDate(int day){
			int i=1,j=1;
			ArrayList<String> listData=new ArrayList<String>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d = new Date();
	        Date date = getMonthStart(d);
	        Date monthEnd = getMonthEnd(d);
	        
	        while (!date.after(monthEnd)) {
	            //System.out.println(sdf.format(date));
	            String tempdate=sdf.format(date);
	            if(tempdate.substring(tempdate.length()-2).equals((day-1)+"")){
	            	i=2;
	            }
	            if(i>1){
	            	listData.add(tempdate);
	            	if(tempdate.substring(tempdate.length()-2).equals((day-1)+"")&&j!=1){
	            		break;
	            	}
	            	j++;
	            }
	            date = getNext(date);
	        }
		   return listData;
	   } 
	   
	public static Calendar getDateOfLastMonth(Calendar date,int month) {  
	    Calendar lastDate = (Calendar) date.clone();  
	    lastDate.add(Calendar.MONTH, -month);  
	    return lastDate;  
	}  
	  
	
	//获取多少个月前的今天
	@SuppressLint("SimpleDateFormat")
	public static Calendar getDateOfLastMonth(String dateStr,int month) {  
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	    try {  
	        Date date = sdf.parse(dateStr);  
	        Calendar c = Calendar.getInstance();  
	        c.setTime(date);  
	        return getDateOfLastMonth(c,month);  
	    } catch (ParseException e) {  
	        throw new IllegalArgumentException("Invalid date format(yyyy-MM-dd): " + dateStr);  
	    }  
	}     
	
	//保存调仓的时间
	@SuppressLint("SimpleDateFormat")
	public static String getSaveTiaoCangTime(String bDate) throws ParseException{
		
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");        
		Date bdate = format1.parse(bDate); 
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(bdate);
	    if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)
	    {
	    	 Calendar cal2 = Calendar.getInstance();
	         cal2.setTime(format1.parse(bDate));
	         int week = cal2.get(Calendar.DAY_OF_WEEK);
	         if(week>2){
	             cal2.add(Calendar.DAY_OF_MONTH,-(week-2)+7);
	         }else{
	             cal2.add(Calendar.DAY_OF_MONTH,2-week+7);
	         }
	         return format1.format(cal2.getTime());
	    }else if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
	    	Calendar calendar = new GregorianCalendar();
	    	calendar.setTime(format1.parse(bDate));
	    	calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
	    	return format1.format(calendar.getTime()); 
	    }
	    else {
	    	 return bDate;
		}
	}
	
	public static String getBeforeMonthDay(Date date){
		try{
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			Calendar c=Calendar.getInstance();
			c.add(Calendar.MONTH,-1);
			return dateFormat.format(c.getTime());
		}catch(Exception e){
			return null;
		}
	}
	
	public static String getWeekDay(Long time){
		try{
			  Calendar c = Calendar.getInstance();
			  c.setTime(new Date(time));
			  int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			  switch (dayOfWeek) {
			  case 1:
				  return "星期日";
			  case 2:
				  return "星期一";
			  case 3:
				  return "星期二";
			  case 4:
				  return "星期三";
			  case 5:
				  return "星期四";
			  case 6:
				  return "星期五";
			  case 7:
				  return "星期六";
			  default:
				  return null;
			  }
		}catch(Exception e){
			return null;
		}
	}
	
	public static List<Integer> getListTime(long time){
		try{
			 List<Integer> listint=new ArrayList<Integer>();
			 Calendar c = Calendar.getInstance();
			 c.setTime(new Date(time));
			 listint.add(c.get(Calendar.DAY_OF_MONTH));
			 listint.add(c.get(Calendar.MONTH)+1);
			 listint.add(c.get(Calendar.YEAR));
			return listint;
		}catch(Exception e){
			return null;
		}
	}
	
	public static Integer getActionStatus(long time){
			try{
				 Calendar c1 = Calendar.getInstance();
				 c1.setTime(new Date(time));
				 Calendar c2=Calendar.getInstance();
				 c2.setTime(new Date());
				 SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd");
				 if(time==date.parse(date.format(new Date())).getTime()){
					 int status=checkTime();
					 if(status==1){
						 //交易中
						 return 1;
					 }else if(status==2){
						 //停市中
						 return 2;
					 }else if(status==3){
						 //将开市
						 return 3;
					 }else{
						 //已闭市
						 return 4;
					 }
					 //设置时间大于现在的时间
				 }else if(time>date.parse(date.format(new Date())).getTime()){
					 //将开市
					 return 5;
				 }{
					 //已闭市
					 return 6;
				 }
			}catch(Exception e){
				return 7;
			}
	}
	
	// 查看是不是应该刷新
	public static int checkTime() {
			Calendar today = Calendar.getInstance();
			// 早上刷新时间段
			long morningstart = 0L;
			long morningend = 0L;
			// 下午刷新时间段
			long afternoonstart = 0L;
			long afternoonend = 0L;
			// 现在的时间
			long nowtime = 0L;
			Calendar now = Calendar.getInstance();
			// 早上开始时间
			today.set(Calendar.HOUR_OF_DAY, 9);
			today.set(Calendar.MINUTE, 29);
			today.set(Calendar.SECOND, 59);
			today.set(Calendar.MILLISECOND, 0);
			morningstart = today.getTime().getTime();
			// 早上结束时间
			today.set(Calendar.HOUR_OF_DAY, 11);
			today.set(Calendar.MINUTE, 29);
			today.set(Calendar.SECOND, 59);
			today.set(Calendar.MILLISECOND, 999);
			morningend = today.getTime().getTime();
			// 上午开始时间
			today.set(Calendar.HOUR_OF_DAY, 13);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			afternoonstart = today.getTime().getTime();
			// 下午结束时间
			today.set(Calendar.HOUR_OF_DAY, 15);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			afternoonend = today.getTime().getTime();
			// 当前时间
			nowtime = now.getTime().getTime();
			// 判断时间区域
			if (morningstart < nowtime) {
				//九点之后
				if(nowtime < morningend){
					//11点半之前
					return 1;
				}else{
					if(nowtime>afternoonstart){
						if(nowtime>afternoonend){
							//3点之后结束
							return 4;
						}else{
							//3点之前
							return 1;
						}
					}else{
					//午休时间
					return 2;
					}
				}
			} else {
				//九点之前
				return 3;
			}
			
		}
	
}
