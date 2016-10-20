package com.abct.tljr.kline.gegu.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.kline.gegu.entity.LdblEntity;
import com.abct.tljr.kline.gegu.entity.LxfgbsEntity;
import com.abct.tljr.kline.gegu.entity.ProfitsEntity;
import com.abct.tljr.kline.gegu.entity.RkdpBase;
import com.abct.tljr.kline.gegu.entity.RkdpEntity;
import com.abct.tljr.kline.gegu.entity.SxjylEntity;
import com.abct.tljr.kline.gegu.entity.TenManJavaBean;
import com.abct.tljr.kline.gegu.entity.YYSRBEntity;
import com.abct.tljr.utils.OneGuConstance;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * 主要用在onegu页面的缓存策略的工具类
 */
public class NetUtils {

	public static String profitsFileName = "profitsFileName";
	public static String sxjylFileName = "sxjylFileName";
	public static String ldblChartFileName = "ldblChartFileName";
	public static String lxfgbsFileName = "lxfgbsFileName";
	public static String yYSRBFileName = "yYSRBFileName";
	public static String rkdpFileName = "rkdpFileName";

	public static String tenBigFileName = "tenBigFileName";

	public static String charsetName = "utf-8";
	public static long TIME_OUT = 24 * 60 * 60 * 1000; // 间隔的时间

	private static boolean isTextEmpty(String text) {
		if (text == null || "".equals(text)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取所需要的数据*/
	public   static    void  getNeedData(String url,File file,Context context,final DataListener   listener){
		boolean notGetDataFromNet = NetUtils.getDataFromLocal(file, new DataFromLocalListener() {

			@Override
			public void addChartData(String diskJsonString) throws JSONException {
				// TODO Auto-generated method stub
				JSONArray jsonArray = new JSONArray(diskJsonString);
				// 添加十大股东图表的数据
				//addTenBigChart(NetUtils.getTenManListByJsonArray(jsonArray));
				listener.addChartData(jsonArray);
			}
		});
		if (!notGetDataFromNet) {
			NetUtils.getDataFromNet(file, url, context, new DataFromNetListener() {

				@Override
				public void addChartData(JSONArray jsonArray) throws JSONException {
					// 添加十大股东图表的数据
					//addTenBigChart(NetUtils.getTenManListByJsonArray(jsonArray));
					listener.addChartData(jsonArray);

				}
			});

		}
	}
	
	
	
	
	
	/**
	 * 根据十大股东图表的jsonarray来获取arraylist
	 */
	public static ArrayList<TenManJavaBean> getTenManListByJsonArray(JSONArray jsonArray) {
		ArrayList<TenManJavaBean> list = new ArrayList<TenManJavaBean>();
		TenManJavaBean bean = null;
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject obj = jsonArray.optJSONObject(i);
			bean = new TenManJavaBean();
			bean.setName(obj.optString("name"));
			bean.setRanking(obj.optString("ranking"));
			bean.setRate(obj.optString("rate"));
			bean.setType(obj.optString("type"));
			bean.setUd(obj.optString("ud"));
			bean.setUd_rate(obj.optString("ud_rate"));
			bean.setVolume(obj.optString("volume"));
			list.add(bean);

		}
		return list;

	}

	/**
	 * 根据估值比率图表的jsonarray来获取arraylist
	 */
	public static ArrayList<SxjylEntity> getSxjylListByJsonArray(JSONArray jsonArray) throws JSONException {
		ArrayList<SxjylEntity> list = new ArrayList<SxjylEntity>();
		SxjylEntity sxjylEntity = null;
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject obj = (JSONObject) jsonArray.get(i);
			sxjylEntity = new SxjylEntity();
			sxjylEntity.setENDDATE(obj.getLong("ENDDATE"));
			sxjylEntity.setJTSYL(obj.getDouble("JTSYL") + "");
			sxjylEntity.setSJL(obj.getDouble("SJL") + "");
			sxjylEntity.setSXL(obj.getDouble("SXL") + "");
			list.add(sxjylEntity);

		}
		return list;
	}

	/**
	 * 根据利息覆盖倍数的图表的jsonarray来获取arraylist
	 * 
	 * @throws JSONException
	 */
	public static ArrayList<LxfgbsEntity> getLxfgbsListByJson(JSONArray jsonArray) throws JSONException {
		LxfgbsEntity lxfgbsEntity = null;
		ArrayList<LxfgbsEntity> list = new ArrayList<LxfgbsEntity>();
		for (int i = 0; i < jsonArray.length(); i++) {
			lxfgbsEntity = new LxfgbsEntity();
			JSONObject obj = (JSONObject) jsonArray.get(i);
			lxfgbsEntity.seteNDDATE(obj.getLong("eNDDATE"));
			lxfgbsEntity.setLxfgbs((float) obj.getDouble("lxfgbs"));

			list.add(lxfgbsEntity);
		}
		return list;
	}

	/**
	 * 根据流动速率的图表的jsonarray来获取arraylist
	 * 
	 * @throws JSONException
	 */
	public static ArrayList<LdblEntity> getldblListByJsonArray(JSONArray jsonArray) throws JSONException {

		ArrayList<LdblEntity> list = new ArrayList<LdblEntity>();
		LdblEntity ldblEntity = null;
		for (int i = 0; i < jsonArray.length(); i++) {
			ldblEntity = new LdblEntity();
			JSONObject obj = (JSONObject) jsonArray.get(i);
			ldblEntity.seteNDDATE(obj.getLong("eNDDATE"));
			ldblEntity.setLdbl((float) obj.getDouble("ldbl"));
			ldblEntity.setSdbl((float) obj.getDouble("sdbl"));
			list.add(ldblEntity);
		}
		return list;

	}

	/**
	 * 根据走势对比图表的jsonarray来获取arraylist
	 * 
	 * @throws JSONException
	 */
	public static ArrayList<RkdpEntity> getRkdpListByJsonArray(JSONArray jsonArray) throws JSONException {
		ArrayList<RkdpEntity> list = new ArrayList<RkdpEntity>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject ojb = (JSONObject) jsonArray.get(i);
			RkdpEntity rkdpEntity = new RkdpEntity();
			rkdpEntity.setName(ojb.getString("name"));
			JSONArray ja = ojb.optJSONArray("result");
			ArrayList<RkdpBase> rkdpBaseList = new ArrayList<>();
			for (int j = 0; j < ja.length(); j++) {
				JSONObject tk = (JSONObject) ja.get(j);
				RkdpBase rkdpBase = new RkdpBase();
				rkdpBase.setRate((float) tk.optDouble("rate"));
				rkdpBase.setClose((float) tk.optDouble("close"));
				rkdpBase.setTime(tk.optLong("time"));
				rkdpBaseList.add(rkdpBase);
			}

			rkdpEntity.setResult(rkdpBaseList);
			list.add(rkdpEntity);

		}
		return list;
	}

	/**
	 * 根据营业收入比重图标的jsonarray来获取arraylist
	 * 
	 * @throws JSONException
	 */
	public static ArrayList<YYSRBEntity> getYYSRBListByJsonArray(JSONArray jsonArray) throws JSONException {
		ArrayList<YYSRBEntity> list = new ArrayList<YYSRBEntity>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = (JSONObject) jsonArray.get(i);
			YYSRBEntity entity = new YYSRBEntity();
			entity.setiNCOME(object.optLong("iNCOME"));
			entity.setiTEM_NAME(object.optString("iTEM_NAME"));
			entity.setpRI_RVNU_PCT((float) object.optDouble("pRI_RVNU_PCT"));
			list.add(entity);
		}
		return list;
	}

	/**
	 * 根据总收益图表的jsonarray来获取arraylist
	 * 
	 * @throws JSONException
	 */
	public static ArrayList<ProfitsEntity> getProfitsListByJsonArray(JSONArray jsonArray) {
		ArrayList<ProfitsEntity> list = new ArrayList<ProfitsEntity>();
		ProfitsEntity profitsEntity = null;
		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject obj;
			try {
				obj = (JSONObject) jsonArray.get(i);

				profitsEntity = new ProfitsEntity();
				profitsEntity.seteNDDATE(obj.getLong("eNDDATE"));
				profitsEntity.setKfjlr((float) obj.getDouble("kfjlr"));
				profitsEntity.setKfjlrbzsr((float) obj.getDouble("kfjlrbzsr"));
				profitsEntity.setZsr((long) obj.getDouble("zsr"));

				list.add(profitsEntity);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return list;
	}

	/**
	 * 从本地获取图表数据 如果成功从本地获取数据就返回true
	 * 
	 * @param profitsFile
	 */
	public static boolean getDataFromLocal(File profitsFile, DataFromLocalListener dataFromLocalListener) {
		// TODO Auto-generated method stub
		// 先获取本地数据
		if (profitsFile.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(profitsFile));
			
				String insertTime = br.readLine();//插入时间
				Long insertTime_ = Long.parseLong(insertTime);
				//当前时间
				Long  currentTime_=System.currentTimeMillis();
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				  Date currentTimeDate = new Date(currentTime_);
				  Date insertTimeDate = new Date(insertTime_);
				  String  currentTimeString=     sdf.format(currentTimeDate);
				  String  insertTimeString=     sdf.format(insertTimeDate);
				if (currentTimeString.equals(insertTimeString)) {
					// 没有超过时间限制
					LogUtil.e("shijian", "没有超出时间限制");
					// 读取缓存内容
					String diskJsonString = br.readLine();
					if (isTextEmpty(diskJsonString)) {
						// 缓存的数据为空,就从网络获取数据
						return false;
					} else {
						// 添加图表数据
						dataFromLocalListener.addChartData(diskJsonString);
						return true;
					}
				} else {
					LogUtil.e("shijian", "超出了时间限制");
					// 超出了时间限制,从网络获取数据
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}

		} else {
			// 本地文件不存在，从网络获取数据
			return false;
		}

	}

	private static void showMessage(String message, Context context) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 从网络获取图表数据
	 * 
	 * @param profitsFile
	 */
	public static void getDataFromNet(final File profitsFile, String url, final Context context,
			final DataFromNetListener listener) {
		// String url = OneGuConstance.getURL("KfjlrbzsrImpf", "kfjlrbzsr",
		// param);
		NetUtil.sendPost(url, new NetResult() {

			@Override
			public void result(String result) {
				// TODO Auto-generated method stub
				try {

					if (result == null || "".equals(result)) {
						showMessage("没有数据", context);
						return;

					}
					if (!profitsFile.exists()) {
						profitsFile.createNewFile();

					}
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.optInt("status") != 1) {
						showMessage("服务器异常", context);
						return;

					}
					JSONArray jsonArray = jsonObject.optJSONArray("result");
					if (jsonArray == null || jsonArray.length() == 0) {
						return;
					}

					// 缓存保存
					BufferedWriter bw = null;
					try {
						// 存本地
						bw = new BufferedWriter(new FileWriter(profitsFile));
						// 缓存生成时间
						bw.write(System.currentTimeMillis() + "");
						// 换行
						bw.newLine();
						// 缓存生成内容
						bw.write(jsonArray.toString());
						// 添加图表数据
						// addProfitsCombinedBar(NetUtils.getProfitsListByJsonArray(jsonArray));
						listener.addChartData(jsonArray);

					} catch (Exception e) {
					} finally {
						bw.close();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 获取绝对的文件 可以看到，当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，
	 * 否则就调用getCacheDir()方法来获取缓存路径。前者获取到的就是 /sdcard/Android/data/<application
	 * package>/cache 这个路径，而后者获取到的是 /data/data/<application package>/cache 这个路径。
	 */
	public static File getAbsoluteFile(String fileName, Context context, String name) {
		String cachePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		File f = new File(cachePath + "/onegu");
		if (!f.exists()) {
			// 如果目录不存在就创建
			f.mkdirs();
		}

		String encodeString = "null";
		try {
			encodeString = URLEncoder.encode(name + fileName, NetUtils.charsetName);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f = new File(f.getAbsolutePath() + "/" + encodeString + ".txt");

		return f;
	}

}
