package com.abct.tljr.utils;
 
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.data.Constant;
import com.abct.tljr.ui.widget.DefaultIconDrawable;
//import com.morgoo.droidplugin.pm.PluginManager;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.qh.common.listener.AvatarRev;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.DLUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import dalvik.system.DexClassLoader;
 
public class Util {
   public static JSONArray bankList;
   public static Pattern pattern = Pattern.compile("[+-]?[0-9]+[0-9]*(\\.[0-9]+)?");
   public static Pattern pattern1 = Pattern.compile("[a-zA-Z]+");
   public static final String filePath = MyApplication.getInstance().getFilesDir().getAbsolutePath();
   @SuppressLint("SdCardPath")
   public static String sdPath = "/sdcard/tljr/";
   @SuppressWarnings({ "static-access", "deprecation" })
   public static final String fileDirPath=MyApplication.getInstance().getExternalFilesDir("pdf").getPath();
   public static String appName;
   public static Animation rotateAni;
   public static int WIDTH;
   public static int HEIGHT;
   public static int IMAGEWIDTH = 720;
   public static int IMAGEHEIGTH = 1280;
   public static int c_blue = Color.rgb(54, 102, 180);
   public static int c_red = Color.rgb(237, 53, 53);
   public static int c_green = Color.parseColor("#149628");
   
   public static void init() {
       appName = getMeteDate("APPNAME", MyApplication.getInstance());
       WIDTH = getScreenWidth(MyApplication.getInstance());
       HEIGHT = getScreenHeight(MyApplication.getInstance());
       format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
       format1.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
       format2.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
       format3.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
       format4.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
       format5.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
       format6.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
   }
   
   public static boolean isValidMobile(String mobiles) {
       Pattern p = Pattern
               // .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
               .compile("^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
       Matcher m = p.matcher(mobiles);
       return m.matches();
 
   }
   
   public static boolean isValidEmail(String mail) {
       Pattern pattern = Pattern.compile("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
       Matcher mc = pattern.matcher(mail);
       return mc.matches();
   }
 
   /**
    * 获取有效数字
    * 
    * @param a
    * @param num
    *            有效数字个数
    * @return
    */
   public static String getUsedNum(double a, int num) {
       BigDecimal b = new BigDecimal(String.valueOf(a));
       BigDecimal divisor = BigDecimal.ONE;
       MathContext mc = new MathContext(2);
       return b.divide(divisor, mc).toString();
   }
 
   
	 public static boolean deleteDir(File dir) {
	        if (dir != null && dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i = 0; i < children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }
	        return dir.delete();
	    }
   
   public static void setParams(View view, int w, int h, int x, int y) {
       RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams((int) (Util.WIDTH * w / Util.IMAGEWIDTH),
               (int) (Util.HEIGHT * h / Util.IMAGEHEIGTH));
       Params.setMargins((int) (Util.WIDTH * x / Util.IMAGEWIDTH),
               (int) (Util.HEIGHT * (Util.IMAGEHEIGTH - h - y) / Util.IMAGEHEIGTH), 0, 0);
       view.setLayoutParams(Params);
   }
 
   public static void setParams(View view1, View view2, int w, int h, int x, int y) {
       RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams((int) (Util.WIDTH * w / Util.IMAGEWIDTH),
               (int) (Util.HEIGHT * h / Util.IMAGEHEIGTH));
       Params.addRule(RelativeLayout.ALIGN_LEFT, view2.getId());
       Params.addRule(RelativeLayout.ALIGN_BOTTOM, view2.getId());
       Params.leftMargin = (int) (Util.WIDTH * x / Util.IMAGEWIDTH);
       Params.bottomMargin = (int) (Util.HEIGHT * y / Util.IMAGEHEIGTH);
       view1.setLayoutParams(Params);
   }
 
   public static void setSize(View view, int w, int h) {
       try {
           if (view.getLayoutParams().getClass().equals(RelativeLayout.LayoutParams.class)) {
               RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(
                       (int) (Util.WIDTH * w / Util.IMAGEWIDTH), (int) (Util.HEIGHT * h / Util.IMAGEHEIGTH));
               view.setLayoutParams(Params);
           } else if (view.getLayoutParams().getClass().equals(LinearLayout.LayoutParams.class)) {
               LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(
                       (int) (Util.WIDTH * w / Util.IMAGEWIDTH), (int) (Util.HEIGHT * h / Util.IMAGEHEIGTH));
               view.setLayoutParams(Params1);
           }
       } catch (Exception e) {
           // TODO: handle exception
           RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(
                   (int) (Util.WIDTH * w / Util.IMAGEWIDTH), (int) (Util.HEIGHT * h / Util.IMAGEHEIGTH));
           view.setLayoutParams(Params);
       }
 
   }
 
   public static String shortName(String name) {
       if (name.length() > 10) {
           name = name.substring(0, 3) + "***" + name.substring(name.length() - 4, name.length());
       }
       return name;
   }
 
   public static String shortGuName(String name) {
       if (name.length() > 5) {
           name = name.substring(0, 4) + "...";
       }
       return name;
   }
 
   public static boolean isNumeric(String str) {
       Pattern pattern = Pattern.compile("[0-9]*");
       Matcher isNum = pattern.matcher(str);
       if (!isNum.matches()) {
           return false;
       }
       return true;
   }
 
   /*
    * 根据屏幕分辨率得到字体大小
    */
   public static int getFontSize(int textSize) {
       int rate = (int) (textSize * (float) WIDTH / IMAGEWIDTH);
       return rate;
   }
 
   /**
    * 得到屏幕宽度
    * 
    * @return 单位:px
    */
   public static int getScreenWidth(Context context) {
       int screenWidth;
       WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
       DisplayMetrics dm = new DisplayMetrics();
       wm.getDefaultDisplay().getMetrics(dm);
       screenWidth = dm.widthPixels;
       return screenWidth;
   }
 
   /**
    * 得到屏幕高度
    * 
    * @return 单位:px
    */
   public static int getScreenHeight(Context context) {
       int screenHeight;
       WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
       DisplayMetrics dm = new DisplayMetrics();
       wm.getDefaultDisplay().getMetrics(dm);
       screenHeight = dm.heightPixels;
       return screenHeight;
   }
 
   public static int dp2px(Context context, int dp) {
       return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
               context.getResources().getDisplayMetrics());
   }
 
   /**
    * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    */
   public static int dip2px(Context context, float dpValue) {
       final float scale = context.getResources().getDisplayMetrics().density;
       return (int) (dpValue * scale + 0.5f);
   }
 
   /**
    * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
    */
   public static int px2dip(Context context, float pxValue) {
       final float scale = context.getResources().getDisplayMetrics().density;
       return (int) (pxValue / scale + 0.5f);
   }
 
   public static void downLoadFile(String url, String dirName, String fileName, Complete complete) throws IOException {
       int numBytes = 0;
       HttpURLConnection conn = null;
       conn = (HttpURLConnection) new URL(url).openConnection();
       conn.setConnectTimeout(5000);
       conn.setDoInput(true);
       conn.setDoOutput(false);
       conn.setUseCaches(true);
       conn.connect();
       if (conn.getContentLength() < 0) {
           return;
       }
       byte[] bytes = new byte[conn.getContentLength()];
       numBytes = download(conn, bytes, url);
       if (numBytes != 0) {
           writeFile(dirName, fileName, bytes, numBytes);
           complete.complete();
       }
   }
 
   public static int download(HttpURLConnection conn, byte[] out, String url) {
       InputStream in = null;
       try {
           in = conn.getInputStream();
           int readBytes = 0;
           while (true) {
               int length = in.read(out, readBytes, out.length - readBytes);
               if (length == -1)
                   break;
               readBytes += length;
           }
           conn.disconnect();
           return readBytes;
       } catch (Exception ex) {
           ex.printStackTrace();
           return 0;
       } finally {
           try {
               if (in != null)
                   in.close();
           } catch (Exception ignored) {
               ignored.printStackTrace();
           }
       }
   }
 
   public static void writeFile(final String dirName, final String fileName, 
		   final byte[] b, final int byteCount)throws IOException {
       File path = new File(dirName);
       File file = new File(dirName + "/" + fileName);
       if (!path.exists()) {
           path.mkdir();
       }
       if (file.exists()) {
           file.delete();// 如果目标文件已经存在，则删除，产生覆盖旧文件的效果
       }
       file.createNewFile();
       FileOutputStream stream = new FileOutputStream(file);
       stream.write(b, 0, byteCount);
       stream.close();
   }
 
   public static String getProperties(String name, String key) {
       File file = new File(filePath + "/" + name + ".properties");
       Properties dbProps = new Properties();
       String value = null;
       try {
           InputStream is = new FileInputStream(file);
           dbProps.load(is);
           for (Entry<Object, Object> string : dbProps.entrySet()) {
               if (string.getKey().toString().equals(key)) {
                   value = (String) string.getValue();
               }
           }
       } catch (Exception e) {
       }
       return value;
   }
 
   public static void getFromAssets(final String fileName, final readComplete complete) {
       new Thread(new Runnable() {
 
           @Override
           public void run() {
               // TODO Auto-generated method stub
               try {
                   InputStreamReader inputReader = new InputStreamReader(
                           MyApplication.getInstance().getResources().getAssets().open(fileName), "utf-8");
                   BufferedReader bufReader = new BufferedReader(inputReader);
                   String line = "";
                   String Result = "";
                   while ((line = bufReader.readLine()) != null)
                       Result += line;
                   bufReader.close();
                   complete.read(Result);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }).start();
   }
 
   public static String getFromAssets(final String fileName) {
       String Result = "";
       try {
           InputStreamReader inputReader = new InputStreamReader(
                   MyApplication.getInstance().getResources().getAssets().open(fileName), "utf-8");
           BufferedReader bufReader = new BufferedReader(inputReader);
           String line = "";
           while ((line = bufReader.readLine()) != null)
               Result += line;
           bufReader.close();
           return Result;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return Result;
   }
 
   /**
    * 
    * @param jarName
    *            加载的jar名 testdex.jar
    * @param className
    *            类名com.abcs.ljf.tlgc.TuLingGongChang
    * @param objClass
    *            构造className对象传参数Class NewsScreen.class
    * @param object
    *            构造className对象传参数对象 NewsScreen对象
    * @return
    */
   public static Object getApp(String jarName, String className) {
       Object obj = null;
       // CrossPlatForm.CopyAssertJarToFile(CrossPlatForm.activity, jarName,
       // jarName);
       File file = new File(filePath + "/" + jarName);
       DexClassLoader classLoader = new DexClassLoader(file.toString(), filePath, null,
               MyApplication.getInstance().getClassLoader());
       try {
           Class<?> myClass = classLoader.loadClass(className);
           Constructor<?> istructor = myClass.getConstructor();
           obj = istructor.newInstance();
           // Method action = myClass.getMethod("click", null);
           // String data = (String) action.invoke(obj);
           return obj;
       } catch (Exception e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return obj;
   }
 
   public static boolean isHaveFile(String fileName) {
       File file = new File(filePath + "/" + fileName);
       return file.exists();
   }
 
   public static Point getSize(View view) {
       int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
       int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
       view.measure(width, height);
       return new Point(view.getMeasuredWidth(), view.getMeasuredHeight());
 
   }
 
   /**
    * 得到当前时间
    * 
    * @return 时间的字符串
    */
   public static String getCurrentTime() {
       Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
       String str = format.format(curDate);
       return str;
   }
 
   public static String getCurrentTime2() {
       Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
       String str = format7.format(curDate);
       return str;
   }
 
   public static long getLongTime(String time) {
       try {
           Date date = format.parse(time);
           return date.getTime();
       } catch (ParseException e) {
           // TODO Auto-generated catch block
           return 0;
       }
   }
 
   public static String getMeteDate(String key, Context context) {
       String msg = "";
       try {
           ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
        		   context.getPackageName(),PackageManager.GET_META_DATA);
           msg = appInfo.metaData.getString(key);
       } catch (NameNotFoundException e) {
       }
       return msg;
   }
 
   public static DecimalFormat df = new DecimalFormat("0.00");
   public static DecimalFormat df1 = new DecimalFormat("0.0");
   public static DecimalFormat df2 = new DecimalFormat("0.0000");
   public static DecimalFormat df3 = new DecimalFormat("0.000");
   public static DecimalFormat dfNumber = new DecimalFormat("###,##0");
   static TimeZone tz = TimeZone.getTimeZone("ETC/GMT+8");
   public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   public static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
   static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM");
   // static SimpleDateFormat format2 = new SimpleDateFormat("yy-MM");
   public static SimpleDateFormat format3 = new SimpleDateFormat("HH:mm");
   static SimpleDateFormat format4 = new SimpleDateFormat("MM-dd");
   static SimpleDateFormat format5 = new SimpleDateFormat("dd/HH:mm");
   static SimpleDateFormat format6 = new SimpleDateFormat("HHmm");
   public static SimpleDateFormat format7 = new SimpleDateFormat("yyyy/MM/dd");
   static SimpleDateFormat format8 = new SimpleDateFormat("MM月dd日");
   public static SimpleDateFormat format9 = new SimpleDateFormat("dd");
   public static SimpleDateFormat format10 = new SimpleDateFormat("MM-dd HH:mm");
   public static SimpleDateFormat format11 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
   public static SimpleDateFormat format12 = new SimpleDateFormat("HH:mm:ss");
 
   // 一.日期转换为时间戳
   public static long getTime() {
       Date date = null;
       try {
           date = format.parse("2009/12/11 00:00:00");
       } catch (ParseException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return date.getTime();
   }
 
   public static boolean iskaishi() {
       boolean iskaishi = false;
       String now = Util.getDateOnlyDat(System.currentTimeMillis());
       try {
           long dayFirst = Util.format.parse(now + " 09:15:00").getTime();
           long dayEnd = Util.format.parse(now + " 15:15:00").getTime();
           long dayMiFirst = Util.format.parse(now + " 11:30:00").getTime();
           long dayMiEnd = Util.format.parse(now + " 13:00:00").getTime();
           if (System.currentTimeMillis() >= dayFirst && System.currentTimeMillis() <= dayMiFirst
                   || System.currentTimeMillis() >= dayMiEnd && System.currentTimeMillis() < dayEnd) {
               iskaishi = true;
           }
       } catch (ParseException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return iskaishi;
   }
 
   // 得到现在今日有的分钟历史数
   public static int getkaishinum() {
       int num = 280;
       String now = Util.getDateOnlyDat(System.currentTimeMillis());
       try {
           long dayFirst = Util.format.parse(now + " 09:15:00").getTime();
           long dayEnd = Util.format.parse(now + " 15:15:00").getTime();
           long dayMiFirst = Util.format.parse(now + " 11:30:00").getTime();
           long dayMiEnd = Util.format.parse(now + " 13:00:00").getTime();
           if (System.currentTimeMillis() > dayEnd || System.currentTimeMillis() < dayFirst) {
               return num;
           }
           long dex = System.currentTimeMillis() - dayFirst;
           dex = dex / 60000;
           if (dex > 0) {
               if (dex < 225) {
                   return 145;
               } else if (dex <= 360) {
                   return (int) (dex - 80);
               }
           }
       } catch (ParseException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return num;
   }
 
   // 二.时间戳转换为date 型
   public static String getDate(long time) {
       Date date = new Date(time);
       return format.format(date);
   }
 
   public static String getDateNoss(long time) {
       Date date = new Date(time);
       return format11.format(date);
   }
 
   public static String getDateHHmmss(long time) {
       Date date = new Date(time);
       return format12.format(date);
   }
 
   public static String getDateOnlyDat(long time) {
       Date date = new Date(time);
       return format1.format(date);
   }
 
   public static String getDateOnlyMonth(long time) {
       Date date = new Date(time);
       return format2.format(date);
   }
 
   public static String getDateOnlyHour(long time) {
       Date date = new Date(time);
       return format3.format(date);
   }
 
   public static String getDateOnlyDay(long time) {
       Date date = new Date(time);
       return format4.format(date);
   }
 
   public static String getDateDayhhmm(long time) {
       Date date = new Date(time);
       return format5.format(date);
   }
 
   public static String getDatehhmm(long time) {
       Date date = new Date(time);
       return format6.format(date);
   }
 
   public static String getDateMY(long time) {
       Date date = new Date(time);
       return format8.format(date);
   }
 
   public static String getDateMDhhmm(long time) {
       Date date = new Date(time);
       return format10.format(date);
   }
 
   public static void setSizeNews(View view) {
       try {
           if (view.getLayoutParams().getClass().equals(RelativeLayout.LayoutParams.class)) {
               RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                       LayoutParams.MATCH_PARENT);
               view.setLayoutParams(Params);
           } else if (view.getLayoutParams().getClass().equals(LinearLayout.LayoutParams.class)) {
               LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                       LayoutParams.MATCH_PARENT);
               view.setLayoutParams(Params1);
           }
       } catch (Exception e) {
           RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                   LayoutParams.MATCH_PARENT);
           view.setLayoutParams(Params);
       }
   }
 
   /**
    * 获取字符串的长度，中文占2个字符,英文数字占1个字符
    * 
    * @param value
    *            指定的字符串
    * @return 字符串的长度
    */
   public static int length(String value) {
       int valueLength = 0;
       String chinese = "[\u4e00-\u9fa5]";
       for (int i = 0; i < value.length(); i++) {
           String temp = value.substring(i, i + 1);
           if (temp.matches(chinese)) {
               valueLength += 2;
           } else {
               valueLength += 1;
           }
       }
       return valueLength;
   }
 
   public static String shortFenZuName(String name) {
       if (length(name) > 20) {
           name = substring(name, 20);
       }
       return name;
   }
 
   public static String substring(String orignal, int count) {
       if (orignal != null && !"".equals(orignal)) {
           String chinese = "[\u4e00-\u9fa5]";
           if (count > 0) {
               StringBuffer buff = new StringBuffer();
               String c;
               for (int i = 0; i < count; i++) {
                   c = orignal.substring(i, i + 1);
                   buff.append(c);
                   if (c.matches(chinese)) {
                       // 遇到中文汉字，截取字节总数减1
                       --count;
                   }
               }
               return buff.toString();
           }
       }
       return orignal;
   }
 
   public static void getLocalAvatar(final String avatarUrl, final AvatarRev avatarRev) {
       new Thread(new Runnable() {
 
           @Override
           public void run() {
               String pathName = sdPath;
               File file = new File(pathName + avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1));
               // byte[] out = new byte[50 * 1024];
               if (file.exists()) {
                   FileInputStream stream = null;
                   try {
                       stream = new FileInputStream(file);
                       int size = stream.available();
                       byte[] out = new byte[size];
                       stream.read(out);
                       stream.close();
                       avatarRev.revBtype(out, out.length);
                   } catch (Exception e) {
                       avatarRev.revBtype(null, 0);
                       e.printStackTrace();
                   } finally {
                       if (stream != null) {
                           try {
                               stream.close();
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   }
               } else {
                   getAvatar(avatarUrl, avatarRev);
               }
           }
       }).start();
 
   }
 
   public static void getAvatar(final String avatarUrl, final AvatarRev avatarRev) {
 
       new Thread(new Runnable() {
 
           @Override
           public void run() {
               String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
               byte[] bytes = download(avatarUrl);
               if (bytes != null && bytes.length != 0) {
                   avatarRev.revBtype(bytes, bytes.length);
                   writeFileToSD(fileName, bytes, bytes.length);
               } else {
                   avatarRev.revBtype(null, 0);
               }
           }
       }).start();
 
   }
 
   /**
    * 根据URL下载头像
    * 
    * @param out
    *            字节流
    * @param url
    *            头像的URL
    * @return 字节数组的长度
    */
   public static byte[] download(String url) {
       InputStream in = null;
       try {
           HttpURLConnection conn = null;
           conn = (HttpURLConnection) new URL(url).openConnection();
           conn.setDoInput(true);
           conn.setDoOutput(false);
           conn.setUseCaches(true);
           conn.connect();
           in = conn.getInputStream();
           int size = conn.getContentLength();
           byte[] out = new byte[size];
           int readBytes = 0;
           while (true) {
               int length = in.read(out, readBytes, out.length - readBytes);
               if (length == -1)
                   break;
               readBytes += length;
           }
           conn.disconnect();
           return out;
       } catch (Exception ex) {
           return null;
       } finally {
           try {
               if (in != null)
                   in.close();
           } catch (Exception ignored) {
           }
       }
   }
 
   public static void writeFileToFile(final String fileName, final byte[] b, final int byteCount) {
       new Thread(new Runnable() {
 
           @Override
           public void run() {
               try {
                   String pathName = filePath;
                   File path = new File(pathName);
                   File file = new File(pathName + "/" + fileName);
                   if (!path.exists()) {
                       path.mkdir();
                   }
                   if (file.exists()) {
                       file.delete();// 如果目标文件已经存在，则删除，产生覆盖旧文件的效果
                   }
                   file.createNewFile();
                   FileOutputStream stream = new FileOutputStream(file);
                   stream.write(b, 0, byteCount);
                   stream.close();
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }).start();
 
   }
 
   public static void writeFileToSD(final String fileName, final byte[] b, final int byteCount,
           final Complete... completes) {
       new Thread(new Runnable() {
 
           @Override
           public void run() {
               try {
                   String pathName = sdPath;
                   File path = new File(pathName);
                   File file = new File(pathName + fileName);
                   if (!path.exists()) {
                       path.mkdir();
                   }
                   if (file.exists()) {
                       file.delete();// 如果目标文件已经存在，则删除，产生覆盖旧文件的效果
                   }
                   file.createNewFile();
                   FileOutputStream stream = new FileOutputStream(file);
                   stream.write(b, 0, byteCount);
                   stream.close();
 
                   for (Complete complete : completes) {
                       complete.complete();
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }).start();
 
   }
 
   public static String getStringFromFile(final String fileName) {
       String Result = "";
       try {
           File file = new File(filePath + "/" + fileName);
           FileInputStream fin = new FileInputStream(file);
           int length = fin.available();
           byte[] buffer = new byte[length];
           fin.read(buffer);
           Result = EncodingUtils.getString(buffer, "UTF-8");
           fin.close();
           return Result;
       } catch (Exception e) {
           e.printStackTrace();
       }
       return Result;
   }
 
   public static void getLocalImage(final String avatarUrl, final AvatarRev avatarRev) {
       if (avatarUrl.length() == 0) {
           return;
       }
       new Thread(new Runnable() {
           @Override
           public void run() {
               String pathName = sdPath;
               File file = new File(pathName + avatarUrl.substring(avatarUrl.lastIndexOf("/"), avatarUrl.length()));
               if (file.exists()) {
                   FileInputStream stream = null;
                   try {
                       stream = new FileInputStream(file);
                       int size = stream.available();
                       byte[] out = new byte[size];
                       stream.read(out);
                       stream.close();
                       avatarRev.revBtype(out, out.length);
                       stream = null;
                   } catch (Exception e) {
                       avatarRev.revBtype(null, 0);
                       e.printStackTrace();
                   } finally {
                       if (stream != null) {
                           try {
                               stream.close();
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   }
 
               } else {
                   getImage(avatarUrl, avatarRev);
               }
           }
       }).start();
   }
 
   public static void getImage(final String avatarUrl, final AvatarRev avatarRev) {
 
       new Thread(new Runnable() {
 
           @Override
           public void run() {
               String fileName = avatarUrl.substring(avatarUrl.lastIndexOf("/"), avatarUrl.length());
               byte[] bytes = download(avatarUrl);
               if (bytes != null && bytes.length != 0) {
                   avatarRev.revBtype(bytes, bytes.length);
                   writeFileToSD(fileName, bytes, bytes.length);
               } else {
                   avatarRev.revBtype(null, 0);
               }
           }
       }).start();
   }
 
   public static void setImage(String url, final View img, final Handler handler) {
       if (url.length() == 0) {
           return;
       }
       getLocalImage(url, new AvatarRev() {
           @Override
           public void revBtype(byte[] b, int bytelength) {
               if (b != null) {
                   final Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                   handler.post(new Runnable() {
                       @Override
                       public void run() {
                           if (img instanceof ImageView)
                               ((ImageView) img).setImageBitmap(bitmap);
                           try {
                               img.setBackground(new BitmapDrawable(bitmap));
                           } catch (Exception e) {
                           }
                       }
                   });
               }
           }
       });
   }
 
   public static void setNetImage(String url, final ImageView img, final Handler handler) {
       getImage(url, new AvatarRev() {
 
           @Override
           public void revBtype(byte[] b, int bytelength) {
               // TODO Auto-generated method stub
               if (b != null) {
                   final Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                   handler.post(new Runnable() {
 
                       @Override
                       public void run() {
                           // TODO Auto-generated method stub
                           img.setImageBitmap(bitmap);
                       }
                   });
               }
           }
       });
   }
 
   public static void setRoundImage(String url, final ImageView img, final Handler handler) {
       getLocalImage(url, new AvatarRev() {
 
           @Override
           public void revBtype(byte[] b, int bytelength) {
               // TODO Auto-generated method stub
               if (b != null) {
                   final Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                   handler.post(new Runnable() {
 
                       @Override
                       public void run() {
                           // TODO Auto-generated method stub
                           img.setImageBitmap(getRoundedCornerBitmap(bitmap));
                       }
                   });
               }
           }
       });
   }
 
   public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
       Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
       Canvas canvas = new Canvas(output);
 
       final int color = 0xff424242;
       final Paint paint = new Paint();
       final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
       final RectF rectF = new RectF(rect);
       final float roundPx = bitmap.getWidth() / 2;
 
       paint.setAntiAlias(true);
       canvas.drawARGB(0, 0, 0, 0);
       paint.setColor(color);
       canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
 
       paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
       canvas.drawBitmap(bitmap, rect, rect, paint);
       return output;
   }
 
   public static void setImageOnlyDown(final String url, final ImageView img) {
       if (url.equals("") || url.length() < 3) {
           return;
       }
       String pathName = sdPath;
       File file = new File(pathName + url.substring(url.lastIndexOf("/"), url.length()));
       if (file.exists()) {
           FileInputStream stream = null;
           try {
               stream = new FileInputStream(file);
               final Bitmap bitmap = BitmapFactory.decodeStream(stream);
               img.post(new Runnable() {
 
                   @Override
                   public void run() {
                       // TODO Auto-generated method stub
                       img.setImageBitmap(bitmap);
                   }
               });
               stream = null;
           } catch (Exception e) {
               e.printStackTrace();
           } finally {
               if (stream != null) {
                   try {
                       stream.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
       } else {
           new Thread(new Runnable() {
 
               @Override
               public void run() {
                   String fileName = url.substring(url.lastIndexOf("/"), url.length());
                   byte[] bytes = download(url);
                   if (bytes != null && bytes.length != 0) {
                       writeFileToSD(fileName, bytes, bytes.length);
                   }
               }
           }).start();
       }
   }
 
   public static void onlyDownImage(final String url) {
       if (url.length() == 0) {
           return;
       }
       String pathName = sdPath;
       File file = new File(pathName + url.substring(url.lastIndexOf("/"), url.length()));
       if (!file.exists()) {
           new Thread(new Runnable() {
 
               @Override
               public void run() {
                   String fileName = url.substring(url.lastIndexOf("/"), url.length());
                   byte[] bytes = download(url);
                   if (bytes != null && bytes.length != 0) {
                       writeFileToSD(fileName, bytes, bytes.length);
                   }
               }
           }).start();
       }
   }
 
   /**
    * 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络,4:3g
    * 
    * @param context
    * @return
    */
   public static String getAPNType(Context context) {
       String netType = "未知";
       ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
       if (networkInfo == null) {
           return netType;
       }
       int nType = networkInfo.getType();
       if (nType == ConnectivityManager.TYPE_MOBILE) {
           if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
               netType = "CMNET";
           } else if (networkInfo.getExtraInfo().toLowerCase().equals("3gnet")) {
               netType = "3GNET";
           } else {
               netType = "CMWAP";
           }
       } else if (nType == ConnectivityManager.TYPE_WIFI) {
           netType = "WIFI";
       }
       return netType;
   }
 
   public static String getMarketAddCode(String s) {
       String a = "";
       String b = "";
       Matcher m = pattern.matcher(s);
       Matcher n = pattern1.matcher(s);
       while (n.find()) {
           a = n.group();
       }
       while (m.find()) {
           b = n.group();
       }
       return a + "|" + b;
   }
 
   public static void readAssets(Context context, String fileName, Complete... completes) {
       try {
           InputStream is = context.getAssets().open(fileName);
           int size = is.available();
 
           // Read the entire asset into a local byte buffer.
           byte[] buffer = new byte[size];
           is.read(buffer);
           is.close();
           getFile(buffer, filePath, fileName, completes);
       } catch (IOException e) {
           // Should never happen!
           throw new RuntimeException(e);
       }
   }
 
   /**
    * 根据byte数组，生成文件
    */
   public static void getFile(byte[] bfile, String filePath, String fileName, Complete... completes) {
       BufferedOutputStream bos = null;
       FileOutputStream fos = null;
       File file = null;
       try {
           File dir = new File(filePath);
           if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
               dir.mkdirs();
           }
           file = new File(filePath + "/" + fileName);
           fos = new FileOutputStream(file);
           bos = new BufferedOutputStream(fos);
           bos.write(bfile);
       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           if (bos != null) {
               try {
                   bos.close();
               } catch (IOException e1) {
                   e1.printStackTrace();
               }
           }
           if (fos != null) {
               try {
                   fos.close();
               } catch (IOException e1) {
                   e1.printStackTrace();
               }
           }
           if (completes != null) {
               for (Complete complete : completes) {
                   complete.complete();
               }
           }
       }
   }
 
   @SuppressLint("ShowToast")
   public static void lanchApp(Activity context, String apkName) {
       File file = new File(Util.fileDirPath + "/" + apkName);
      // Toast.makeText(context.getApplicationContext(),"地址是Util.filePath："+ Util.filePath + "/" + apkName, Toast.LENGTH_SHORT).show();
       if (!file.exists()) {
           Toast.makeText(context.getApplicationContext(), "插件不存在,请重新进入应用", Toast.LENGTH_SHORT).show();
           return;
       }
       PackageInfo info = DLUtils.getPackageInfo(context, file.getAbsolutePath());
      // LogUtil.i("momo", " DLPluginManager.getInstance(context).loadApk");
     //  System.err.println("DLPluginManager.getInstance(context).loadApk");
       DLPluginManager.getInstance(context).loadApk(file.getAbsolutePath(), true);
       DLPluginManager pluginManager = DLPluginManager.getInstance(context);
   //    LogUtil.i("momo", "pluginManager.startPluginActivity");
       pluginManager.startPluginActivity(context, new DLIntent(info.packageName));
   }
 
   public static void lanchApp(Context context, String apkFilepath) {
       PackageInfo info = DLUtils.getPackageInfo(context, apkFilepath);
       File file = new File(apkFilepath);
       DLPluginManager.getInstance(context).loadApk(file.getAbsolutePath(), true);
       DLPluginManager pluginManager = DLPluginManager.getInstance(context);
       pluginManager.startPluginActivity(context, new DLIntent(info.packageName));
   }
 
   public static void startApp(final Context context, String apkName) {
       final File file = new File(Util.filePath + "/" + apkName);
       readAssets(context, apkName, new Complete() {
           @Override
           public void complete() {
               lanchApp(context, file.getAbsolutePath());
           }
       });
   }
 
   public static void start360App(final Context context, String apkName) {
//       final File file = new File(Util.filePath + "/" + apkName);
//       readAssets(context, apkName, new Complete() {
//           @Override
//           public void complete() {
//        	   try{
////                 lanchApp(context, file.getAbsolutePath());
//            	    PluginManager.getInstance().installPackage(file.getAbsolutePath(),0);
//					PackageManager pm = context.getPackageManager();
//				    Intent intent = pm.getLaunchIntentForPackage("com.qh.turingfinance.etf.activity");
//				    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				    context.startActivity(intent);
//        	   }catch(Exception e){
//        		   
//        	   }
//           }
//       });
   }
   
   public static boolean isEmptyAndSpace(String str) {
       if (TextUtils.isEmpty(str)) {
           return true;
       }
       if (str.equals(" ")) {
           return true;
       }
       return false;
   }
 
   public static String myTrim(String rs) { // 去掉空格 以及奇葩字符
 
       String str = rs.replaceAll("\\?", "").replaceAll("target=\"_blank\">", "").replaceAll("target=\"blank\">", "")
               .replaceAll("target='_blank'>", "").replaceAll("target='blank'>", "").replaceAll("&nbsp;", "")
               .replaceAll("&sbquo;", ",")
 
               .replaceAll("   ", "").replaceAll("@@@", "\n");
 
       if (str != null) {
           int len = str.length();
           if (len > 0) {
               char[] dest = new char[len];
               int destPos = 0;
               for (int i = 0; i < len; ++i) {
                   char c = str.charAt(i);
                   if (!Character.isWhitespace(c)) {
                       dest[destPos++] = c;
                   }
               }
               return new String(dest, 0, destPos);
           }
       }
       return str;
   }
 
   public static String getStandardDate(String timeStr) throws ParseException {
 
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
       Date date = sdf.parse(timeStr);
       Calendar cal = Calendar.getInstance();
       cal.setTime(date);
 
       StringBuffer sb = new StringBuffer();
 
       long t = cal.getTimeInMillis();
       long time = System.currentTimeMillis() - t;
       long mill = (long) Math.ceil(time / 1000);// 秒前
 
       long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前
 
       long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时
 
       long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前
 
       if (day - 1 > 0) {
           sb.append(day + "天");
       } else if (hour - 1 > 0) {
           if (hour >= 24) {
               sb.append("1天");
           } else {
               sb.append(hour + "小时");
           }
       } else if (minute - 1 > 0) {
           if (minute == 60) {
               sb.append("1小时");
           } else {
               sb.append(minute + "分钟");
           }
       } else if (mill - 1 > 0) {
           if (mill == 60) {
               sb.append("1分钟");
           } else {
               sb.append(mill + "秒");
           }
       } else {
           sb.append("刚刚");
       }
       if (!sb.toString().equals("刚刚")) {
           sb.append("前");
       }
       return sb.toString();
   }
 
   public static String ToDBC(String input) {
       char c[] = input.toCharArray();
       for (int i = 0; i < c.length; i++) {
           if (c[i] == '\u3000') {
               c[i] = ' ';
           } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
               c[i] = (char) (c[i] - 65248);
 
           }
       }
       return new String(c);
   }
 
   private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
   private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
   private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
   private static final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符
 
   public static String delHTMLTag(String htmlStr) {
       Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
       Matcher m_script = p_script.matcher(htmlStr);
       htmlStr = m_script.replaceAll(""); // 过滤script标签
 
       Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
       Matcher m_style = p_style.matcher(htmlStr);
       htmlStr = m_style.replaceAll(""); // 过滤style标签
 
       Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
       Matcher m_html = p_html.matcher(htmlStr);
       htmlStr = m_html.replaceAll(""); // 过滤html标签
 
       Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
       Matcher m_space = p_space.matcher(htmlStr);
       htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
       return htmlStr.trim(); // 返回文本字符串
   }
 
   public static String delHTMLTag2(String htmlStr) {
       String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
       Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
       Matcher m_html = p_html.matcher(htmlStr);
       htmlStr = m_html.replaceAll(""); // 过滤html标签
       return htmlStr.trim(); // 返回文本字符串
   }
 
   public static String getTextFromHtml(String htmlStr) {
       htmlStr = delHTMLTag(htmlStr);
       htmlStr = htmlStr.replaceAll("&nbsp;", "");
       htmlStr = htmlStr.substring(0, htmlStr.indexOf("。") + 1);
       return htmlStr;
   }
 
   public static String getNowTime() {
       return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
   }
 
   public static void startAni(final int position, final View v, final ListView lv) {
       v.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
           @SuppressWarnings("deprecation")
           @Override
           public void onGlobalLayout() {
               // 判断在屏幕显示范围内个数才播放动画
               int h = Util.HEIGHT;
               if (position >= (h / v.getMeasuredHeight())-1 ) {
                   return;
               }
               ViewHelper.setAlpha(v, 0);
               AnimatorSet set = new AnimatorSet();
               set.playTogether(ObjectAnimator.ofFloat(v, "translationY", -v.getMeasuredHeight(), 0),
                       ObjectAnimator.ofFloat(v, "alpha", 0f, 1f));
               set.setStartDelay(100 * position);
               set.setDuration(200).start();
               v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
           }
       });
 
   }
 
   public static void setLEDFont(TextView textView) {
       textView.setTypeface(Typeface.createFromAsset(MyApplication.getInstance().getAssets(), "digital-7.ttf"));
   }
 
   public static String getStringByObject(Object object) throws Throwable {
       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
       ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
       objectOutputStream.writeObject(object);
       String s = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
       objectOutputStream.close();
       return s;
   }
 
   public static interface readComplete {
       public void read(Object object);
   }
 
   public static Object dataRead(final String mobilesString) {
 
       try {
           Object obj = null;
           if (mobilesString == null) {
               return obj;
           }
           byte[] mobileBytes = Base64.decode(mobilesString.getBytes(), Base64.DEFAULT);
           ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
           ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
           obj = objectInputStream.readObject();
           objectInputStream.close();
           return obj;
       } catch (Throwable e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
           return null;
       }
 
   }
 
   /*
    * 检查是否有网络
    */
   public static boolean checkNetUsable() {
 
       return !Constant.netType.equals("未知");
   }
 
   /**
    * 最小内存获取图片
    * 
    * @param res
    * @param resId
    * @param reqWidth
    * @param reqHeight
    * @return
    */
   public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
       final BitmapFactory.Options options = new BitmapFactory.Options();
       options.inJustDecodeBounds = true;
       BitmapFactory.decodeResource(res, resId, options);
       options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
       options.inJustDecodeBounds = false;
       options.inPurgeable = true;
       options.inInputShareable = true;
       options.inPreferredConfig = Bitmap.Config.RGB_565;
       Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);
       Bitmap b = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
       if (b != bitmap) {
           bitmap.recycle();
           bitmap = null;
       }
       return b;
   }
 
   public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
       // 原始图片的宽高
       final int height = options.outHeight;
       final int width = options.outWidth;
       int inSampleSize = 1;
 
       if (height > reqHeight || width > reqWidth) {
 
           final int halfHeight = height / 2;
           final int halfWidth = width / 2;
 
           // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
           while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
               inSampleSize *= 2;
           }
       }
 
       return inSampleSize;
   }
 
   public static long getSDAvailableSize() {
       File path = Environment.getExternalStorageDirectory();
       StatFs stat = new StatFs(path.getPath());
       long blockSize = stat.getBlockSize();
       long availableBlocks = stat.getAvailableBlocks();
       // return Formatter.formatFileSize(MyApplication.getInstance(),
       // blockSize * availableBlocks);
       return (availableBlocks * blockSize) / 1024 / 1024;
   }
 
   public static void getRealInfo(final String parms, final NetResult result, final boolean... isList) {
//      LogUtil.e("getRealInfo_url", UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/real?" + parms);
	   //String url=UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/real?"+parms;
       NetUtil.sendPost(UrlUtil.Url_apicavacn + "tools/stock/quotation/0.2/real", parms, new NetResult() {
           @Override
           public void result(final String msg) {
               try {
                   // Log.e("reflushDP_msg",msg);
                   // {"code":"000001","name":"上证指数","close":3125,"open":3309.66,"high":3309.66,"low":3115.89,"turnoverRate":null,"amplitude":5.76,"pe市盈率":null,"pb市净率":284.56,"volume":70569123,"turnover成交额":7998199,"value总市值":null,"流通市值（亿元）circulation":null,"ud":-236.84,"upRate":-7.04,"preClose":3361.84
                   // 代码，名称，
                   // {"code":"IC1602","name":"IC1602","close":6327,"turnover":148326480,"volume":114,"preClose":6660,"open":6590,"high":6590,"low":6327,"ud":-333,"upRate":-5,"position":946,"openPosition":1079,"closePosition":133,"addPosition":18}
                   // 代码，名称，当前价 ，成交额，成交量，昨收，开盘价，最高价，最低价，涨跌，涨跌幅，持仓,开仓，平仓，加仓
                   org.json.JSONObject object = new org.json.JSONObject(msg);
                   JSONObject resultObject = new JSONObject();
                   if (object.getInt("code") == 200) {
                       resultObject.put("code", 1);
                       JSONArray array = new JSONArray();
                       if (isList.length > 0&&object.has("result")) {
                           JSONArray jsonArray = object.getJSONArray("result");
                           for (int i = 0; i < jsonArray.length(); i++) {
                               org.json.JSONObject obj = jsonArray.getJSONObject(i);
                               JSONObject object2 = new JSONObject();
                               object2.put("market", obj.optString("market").toLowerCase());
                               object2.put("code", obj.opt("code"));
                               JSONArray array1 = new JSONArray();
                               array1.put(obj.opt("close"));
                               array1.put(obj.opt("preClose"));
                               array1.put(obj.opt("open"));
                               array1.put(obj.opt("volume"));
                               array1.put(obj.opt("turnover"));
                               array1.put(obj.opt("amplitude"));
                               array1.put(obj.opt("high"));
                               array1.put(obj.opt("low"));
                               array1.put(obj.opt("ud"));
                               array1.put(obj.opt("upRate"));
                               array1.put(obj.opt("pe"));
                               array1.put(obj.opt("pe"));
                               array1.put(obj.opt("pb"));
                               array1.put(obj.opt("value"));
                               array1.put(obj.opt("circulation"));
                               array1.put(obj.opt("state"));
                               array1.put(obj.optString("status"));
                               object2.put("data", array1);
                               array.put(object2);
                               // resultObject.put("market",
                               // obj.optString("market").toLowerCase());
                               // resultObject.put("code", obj.opt("code"));
                           }
                       } else {
                           org.json.JSONObject obj = object.getJSONObject("result");
                           array.put(obj.opt("close"));
                           array.put(obj.opt("preClose"));
                           array.put(obj.opt("open"));
                           array.put(obj.opt("volume"));
                           array.put(obj.opt("turnover"));
                           array.put(obj.opt("amplitude"));
                           array.put(obj.opt("high"));
                           array.put(obj.opt("low"));
                           array.put(obj.opt("ud"));
                           array.put(obj.opt("upRate"));
                           array.put(obj.opt("pe"));
                           array.put(obj.opt("pe"));
                           array.put(obj.opt("pb"));
                           array.put(obj.opt("value"));
                           array.put(obj.opt("circulation"));
                           array.put(obj.opt("state"));
                           array.put(obj.optString("status"));
                           // resultObject.put("market",
                           // obj.optString("market").toLowerCase());
                           // resultObject.put("code", obj.opt("code"));
                       }
                       resultObject.put("result", array);
                   }
                   result.result(resultObject.toString());
               } catch (org.json.JSONException e) {
                   LogUtil.e("UtilMessage", e.getMessage());
                   e.printStackTrace();
                   result.result("");
               }
 
           }
       });
 
   }
 
   /**
    * 分隔时间，形成以space为分隔的ArrayList
    * <String> 使用此方法分隔必须XAxis.setAdjustXLabels(false)；自适应开启将影响Label的显示。
    * 
    * @param array
    *            中间间隔
    * @return
    */
   public static ArrayList<String> getXVals(SimpleDateFormat format1, JSONArray jsonArray, long space) {
       ArrayList<String> xVals = new ArrayList<String>();
       try {
           long a = 28800000;
           long m = -space;
           for (int i = 0; i < jsonArray.length(); i++) {
               JSONArray array = jsonArray.getJSONArray(i);
               for (long j = array.getLong(0); j <= array.getLong(1); j += 60000) {
                   if (j == array.getLong(0) && i > 0) {
                       m = j;
                   } else if (j == array.getLong(1) && i < jsonArray.length() - 1) {
                       if ((j - a) % space == 0) {
                           xVals.add(format1.format(new Date(j - a)) + "/"
                                   + format1.format(new Date(jsonArray.getJSONArray(i + 1).getLong(0) - a)));
                       } else {
                           xVals.add("");
                       }
                   } else {
                       if ((j - a) % space == 0 && j - m >= space) {
                           xVals.add(format1.format(new Date(j - a)));
                           m = j;
                       } else {
                           xVals.add("");
                       }
                   }
               }
           }
       } catch (JSONException e) {
           e.printStackTrace();
       }
       return xVals;
   }
   
   public static void ChangePhoneEmailFinish(final Context context){
//	    String test=UrlUtil.Url_cavacn+"3000/api/user/refresh"+"token="+Configs.token+"&iou=m";
		NetUtil.sendGet(UrlUtil.Url_cavacn+"3000/api/user/refresh","token="+Configs.token+"&iou=m",new NetResult() {
			@Override
			public void result(String response) {
				try{
					JSONObject object=new JSONObject(response);
					if(object.getInt("code")==1){
						String token=object.getString("result");
						Configs.preference = PreferenceManager.getDefaultSharedPreferences(context);
						Configs.preference.edit().putString("xbwautologintoken",token).commit();
					}
				}catch(Exception e){
				}
			}
		});
	}


    /**
     * 防止重复点击
     */
    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
   
}
