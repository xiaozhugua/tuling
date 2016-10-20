package com.abct.tljr.hangqing.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.hangqing.database.ZiXuanGu;
import com.abct.tljr.hangqing.model.ZiXuanOneGu;
import com.abct.tljr.hangqing.model.ZuHeModel;
import com.abct.tljr.model.OneGu;
import com.qh.common.util.LogUtil;
import com.ryg.utils.LOG;

public class ParseJson {

	//解析股票信息(Map类型)
	public static List<OneGu> ParseJosnMapGuInfo(Map<String,ZiXuanOneGu> ZiXuanGu,String json){
		try{
			List<OneGu> listGu=new ArrayList<OneGu>();
            final org.json.JSONObject object = new org.json.JSONObject(json);
            if (object.getInt("code") == 1) {
            	JSONArray arr = object.getJSONArray("result");
            	OneGu gu;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String code = obj.getString("code");
                    if (ZiXuanGu.containsKey(code)) {
                        gu =GuDealImpl.getOneGu(ZiXuanGu.get(code));
                        JSONArray array = obj.getJSONArray("data");
                        
                        ZiXuanGu.get(code).setNow(array.optDouble(0, 0));//当前价格
                        ZiXuanGu.get(code).setYclose(array.optDouble(1, 0));// 昨收
                        ZiXuanGu.get(code).setKaipanjia(array.optDouble(2, 0));//开盘价
                        ZiXuanGu.get(code).setChange((float) array.optDouble(8, 0));// 变动价格
                        ZiXuanGu.get(code).setP_change((float) array.optDouble(9, 0));// 变动百分比
                        ZiXuanGu.get(code).setP_changes((ZiXuanGu.get(code).getP_change() > 0 ? "+": "")
                        		+ ZiXuanGu.get(code).getP_change() + "%");// 变动百分比
                        
                        gu.setNow(array.optDouble(0, 0));//当前价格
                        gu.setYclose(array.optDouble(1, 0));// 昨收
                        gu.setKaipanjia(array.optDouble(2, 0));//开盘价
                        gu.setChange((float) array.optDouble(8, 0));// 变动价格
                        gu.setP_change((float) array.optDouble(9, 0));// 变动百分比
                        gu.setP_changes((gu.getP_change() > 0 ? "+": "") + gu.getP_change() + "%");// 变动百分比

                    }
                }
            }
            return listGu;
		}catch(Exception e){
			LOG.e("ParseJsonException",e.getMessage());
			return null;
		}
	}
	
	//解析更新股票信息(list类型)
	public static void ParseJsonListGuInfo(List<OneGu> listOneGu,String json){
		try{
			 final org.json.JSONObject object = new org.json.JSONObject(json);
	            if (object.getInt("code") == 1) {
	            	JSONArray arr = object.getJSONArray("result");
	                for (int i = 0; i < arr.length(); i++) {
	                	JSONObject obj = arr.getJSONObject(i);
	                    String code = obj.getString("code");
	                    String market=obj.getString("market");
	                    for(OneGu mOneGu:listOneGu) {
	                        if(mOneGu.getKey().equals(market+code)){
		                        JSONArray array = obj.getJSONArray("data");
		                        mOneGu.setNow(array.optDouble(0, 0));//当前价格
		                        mOneGu.setYclose(array.optDouble(8, 0));// 昨收
		                        mOneGu.setKaipanjia(array.optDouble(1, 0));//开盘价
		                        mOneGu.setChange((float) array.optDouble(2, 0));// 变动价格
		                        mOneGu.setP_change((float) array.optDouble(9, 0));// 变动百分比
		                        mOneGu.setP_changes((mOneGu.getP_change() > 0 ? "+": "") 
		                        		+ mOneGu.getP_change() + "%");// 变动百分比
	                        }
	                    }
	                }
	            }
		}catch(Exception e){
		}
	}
	
	//解析组合信息
    public static List<ZuHeModel> ParseInitZuHe(List<ZuHeModel> listZuHe,JSONObject object) {
        try {
            if (object.getInt("status") == 1) {
                JSONObject allObject = object.getJSONObject("msg");
                Iterator<String> keys = allObject.keys();
                JSONObject objectitem = null;
                while (keys.hasNext()) {
                    String key = keys.next();
                    objectitem = allObject.getJSONObject(key);
                    String url = objectitem.getString("imgurl");
                    // 日收益
                    Float rishouyi = Float.valueOf(objectitem.getString("rishouyi"));
                    // 月收益
                    Float yueshouyi = Float.valueOf(objectitem.getString("yueshouyi"));
                    // 总收益
                    Float zongshouyi = Float.valueOf(objectitem.getString("zongshouyi"));
                    // 更新数据
                    for (int i = 0; i < listZuHe.size(); i++) {
                        if (listZuHe.get(i).getKey().equals(key)) {
                            listZuHe.get(i).setImgUrl(url);
                            listZuHe.get(i).setJsonData(objectitem.toString());
                            listZuHe.get(i).setZong(zongshouyi);
                            listZuHe.get(i).setRi(rishouyi);
                            listZuHe.get(i).setYue(yueshouyi);
                            //得到总，月，日收益
                            List<String> zong=intAndFloat(zongshouyi);
                            List<String> yue=intAndFloat(yueshouyi);
                            List<String> ri=intAndFloat(rishouyi);
                            listZuHe.get(i).setZong_int(zong.get(0));
                            listZuHe.get(i).setZong_float(zong.get(1));
                            listZuHe.get(i).setYue_int(yue.get(0));
                            listZuHe.get(i).setYue_float(yue.get(1));
                            listZuHe.get(i).setRi_int(ri.get(0));
                            listZuHe.get(i).setRi_float(ri.get(1));
                        }
                    }
                }
                return listZuHe;
            }else{
            	return null;
            }
        } catch (Exception e) {
        	return null;
        }
    }
	
    //获取整数和小数
    public static List<String> intAndFloat(float param){
    	try{
    		List<String> result=new ArrayList<String>();
    		NumberFormat format= NumberFormat.getPercentInstance();// 获取格式化类实例
    		format.setMinimumFractionDigits(1);// 设置小数位
    		String StringResult=format.format(param);
    		int index=StringResult.indexOf(".");
    		if(param>0.0f){
    			result.add("+"+StringResult.subSequence(0,index));
    			result.add(StringResult.substring(index,StringResult.length()));
    		}else if(param<0.0f){
    			result.add(StringResult.subSequence(0, index)+"");
    			result.add(StringResult.substring(index,StringResult.length()));
    		}else{
    			result.add("0");
    			result.add(".0%");
    		}
    		return result;
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public static Map<String,String> ParseHangQingTab(String json){
    	try{
    		Map<String,String> param=new LinkedHashMap<>();
    		JSONObject object=new JSONObject(json);
    		if(object.getString("code").equals("200")){
    			JSONArray array=object.getJSONArray("result");
    			for(int i=0;i<array.length();i++){
    				JSONObject some=array.getJSONObject(i);
    				param.put(some.getString("name"),some.getString("key"));
    			}
    		}
    		return param;
    	}catch(Exception exception){
    		return null;
    	}
    }
    
    public static List<ZiXuanGu> ParseZiXuanData(String json){
    	try{
    		JSONObject object=new JSONObject(json);
    		List<ZiXuanGu> listGu=new ArrayList<ZiXuanGu>();
    		if(object.getInt("status")==1){
    			JSONArray array=object.getJSONArray("msg");
    			JSONObject temp=null;
    			ZiXuanGu mZiXuanGu=null;
    			for(int i=array.length()-1;i>=0;i--){
    				temp=array.getJSONObject(i);
    				LogUtil.e("temptoString",temp.toString());
    				mZiXuanGu=new ZiXuanGu();
    				mZiXuanGu.setCode(temp.getString("code"));
    				mZiXuanGu.setId(temp.getString("id"));
    				mZiXuanGu.setMarket(temp.getString("market"));
    				mZiXuanGu.setName(temp.getString("name"));
    				mZiXuanGu.setPrice(0);
    				mZiXuanGu.setChange(0);
    				mZiXuanGu.setYclose(-1);
    				mZiXuanGu.setStatus("--");
    				mZiXuanGu.setLocation(array.length()-i);
    				listGu.add(mZiXuanGu);
    			}
    			return listGu;
    		}else{
    			return null;
    		}
    	}catch(Exception e){
    		LogUtil.e("ParseZiXuanData",e.getMessage());
    		return null;
    	}
    }
    
    
}
