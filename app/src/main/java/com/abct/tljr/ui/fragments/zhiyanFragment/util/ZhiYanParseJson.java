package com.abct.tljr.ui.fragments.zhiyanFragment.util;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.model.UserCrowd;
import com.abct.tljr.model.UserCrowdUser;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanFinishHeader;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanFinishHeaderItem;
import com.abct.tljr.ui.fragments.zhiyanFragment.model.ZhiYanLeftBean;
import com.abct.tljr.ui.yousuan.model.DuoKongYinKuiModel;
import com.abct.tljr.ui.yousuan.model.HuiCeQuXianModel;
import com.abct.tljr.ui.yousuan.model.QiYuXianModel;
import com.abct.tljr.ui.yousuan.model.YouSuanDataModel;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
import com.abct.tljr.ui.yousuan.model.YouSuanMyItem;
import com.abct.tljr.ui.yousuan.model.YouSuanRecordModel;
import com.abct.tljr.ui.yousuan.model.YueDuYinkuiModel;
import com.qh.common.util.LogUtil;

public class ZhiYanParseJson {

	public static List<ZhiYanLeftBean> parseZhiYanTitle(String json){
		try{
			JSONObject object = new JSONObject(json);
		    if (object.getInt("status") == 1) {
		        List<ZhiYanLeftBean> listBean=new ArrayList<ZhiYanLeftBean>();
		    	JSONArray arr = object.getJSONArray("msg");
		        for (int i = 0; i < arr.length(); i++) {
		            JSONObject ob = arr.getJSONObject(i);
		            ZhiYanLeftBean bean = new ZhiYanLeftBean();
		            bean.setName(ob.optString("name"));
		            bean.setType(ob.optString("type"));
		            bean.setCount(ob.optInt("count"));
		            bean.setStar(ob.optDouble("star"));
		            listBean.add(bean);
		        }
		        return listBean;
		     }else{
		    	return null;
		     }
		}catch(Exception e){
			return null;
		}
	}

	public static boolean parseZhiYanCrowd(ArrayList<ZhongchouBean> listBean,String msg){
		try{
			if (msg != null && !msg.equals("")) {
               JSONObject object = new JSONObject(msg);
               ArrayList<ZhongchouBean> mList =new ArrayList<ZhongchouBean>();
               if (object.getInt("status") == 1) {
                    JSONArray arr = object.getJSONArray("msg");
                    for(int i = 0; i < arr.length(); i++) {
                       JSONObject ob = arr.getJSONObject(i);
                       ZhongchouBean bean = new ZhongchouBean();
                       bean.setCode(ob.optString("code"));
                       bean.setCount(ob.optInt("count"));
                       bean.setCreateDate(ob.optLong("createDate"));
                       bean.setEndDate(ob.optLong("endDate"));
                       bean.setHasMoney(ob.optInt("hasMoney"));
                       bean.setIconurl(ob.optString("icon"));
                       bean.setId(ob.optString("id"));
                       bean.setMarket(ob.optString("market"));
                       bean.setPrice(ob.optInt("price"));
                       bean.setStatus(ob.optInt("status"));
                       bean.setTotalMoney(ob.optInt("totalMoney"));
                       bean.setType(ob.optString("type"));
                       bean.setTypedesc(ob.optString("typedesc"));
                       bean.setFocus(ob.optInt("focus"));
                       bean.setRemark(ob.optInt("remark"));
                       bean.setIsfocus(ob.optBoolean("isfocus"));
                       bean.setReward(ob.optInt("reward"));
                       bean.setRewardMoney(ob.optInt("rewardMoney"));
                       bean.setLook(ob.optBoolean("isLook"));
                       bean.setSection(ob.optString("section"));
                       
                       bean.setCustomerRating(ob.optDouble("starCount")!=0? (ob.optDouble("star")/ob.optDouble("starCount")) : 0);
                       bean.setPrivateStar(ob.optDouble("privateStar"));
                       
                       JSONArray ar2 = ob.getJSONArray("userCrowd");
                       ArrayList<UserCrowd> list = new ArrayList<UserCrowd>();
                       for (int x = 0; x < ar2.length(); x++) {
                            JSONObject o = ar2.getJSONObject(x);
                            UserCrowd crowd = new UserCrowd();
                            crowd.setCcrowdId(o.optString("crowdId"));
                            crowd.setCdate(o.optLong("date"));
                            crowd.setCid(o.optString("id"));
                            crowd.setCinit(o.optBoolean("init"));
                            crowd.setCmoney(o.optInt("money"));
                            JSONObject object1 = o.getJSONObject("user");
                            UserCrowdUser u = new UserCrowdUser();
                            u.setUavata(object1.optString("avatar"));
                            u.setUdata(object1.optLong("date"));
                            u.setUlevel(object1.optInt("level"));
                                u.setUuid(object1.optString("uid"));
                                u.setUnickname(object1.optString("nickName"));
                                crowd.setUser(u);
                                list.add(crowd);
                            }
                            bean.setUserCrowdList(list);
                            mList.add(bean);
                        }
                    }
               		
                    listBean.addAll(mList);
               	        return true; 
               	  }else{
                    	return false;
                  }
               }catch(Exception e){
            	   return false;
               }
	}
	
	
	public static boolean ParseMyViewJson(ArrayList<ZhongchouBean> listbean,String msg){
		try{
			JSONObject object = new JSONObject(msg);
			if (object.getInt("status") == 1) {
				ArrayList<ZhongchouBean> mList=new ArrayList<ZhongchouBean>();
				JSONArray arr = object.getJSONArray("msg");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject ob = arr.getJSONObject(i);
					ZhongchouBean bean = new ZhongchouBean();
					bean.setCode(ob.optString("code"));
					bean.setCount(ob.optInt("count"));
					bean.setCreateDate(ob.optLong("createDate"));
					bean.setEndDate(ob.optLong("endDate"));
					bean.setHasMoney(ob.optInt("hasMoney"));
					bean.setIconurl(ob.optString("icon"));
					bean.setId(ob.optString("id"));
					bean.setMarket(ob.optString("market"));
					bean.setPrice(ob.optInt("price"));
					bean.setStatus(ob.optInt("status"));
					bean.setTotalMoney(ob.optInt("totalMoney"));
					bean.setType(ob.optString("type"));
					bean.setTypedesc(ob.optString("typedesc"));
					bean.setFocus(ob.optInt("focus"));
					bean.setRemark(ob.optInt("remark"));
					bean.setIsfocus(ob.optBoolean("isfocus"));
					bean.setReward(ob.optInt("reward"));
					bean.setRewardMoney(ob.optInt("rewardMoney"));
					bean.setSection(ob.optString("section"));
					bean.setLook(ob.optBoolean("isLook"));
					
					bean.setCustomerRating(ob.optDouble("starCount")!=0?
							(ob.optDouble("star")/ob.optDouble("starCount")):0);
                    bean.setPrivateStar(ob.optDouble("privateStar"));
					
					JSONArray ar2 = ob.getJSONArray("userCrowd");
					ArrayList<UserCrowd> list = new ArrayList<UserCrowd>();
					for (int x = 0; x < ar2.length(); x++) {
						JSONObject o = ar2.getJSONObject(x);
						UserCrowd crowd = new UserCrowd();
						crowd.setCcrowdId(o.optString("crowdId"));
						crowd.setCdate(o.optLong("date"));
						crowd.setCid(o.optString("id"));
						crowd.setCinit(o.optBoolean("init"));
						crowd.setCmoney(o.optInt("money"));
						crowd.setAllMoney(o.optInt("allMoney"));
						crowd.setFocs(o.optBoolean("isFocus"));
						crowd.setMsg(o.optString("msg"));
						JSONObject object1 = o.getJSONObject("user");
						UserCrowdUser u = new UserCrowdUser();
						u.setUavata(object1.optString("avatar"));
						u.setUdata(object1.optLong("date"));
						u.setUlevel(object1.optInt("level"));
						u.setUuid(object1.optString("uid"));
						u.setUnickname(object1.optString("nickName"));
						crowd.setUser(u);
						JSONObject object2 = object1.getJSONObject("userInfo");
						u.setAllMoney(object2.optInt("allMoney"));
						u.setCount(object2.optInt("count"));
						u.setCreateData(object2.getLong("createDate"));
						list.add(crowd);
					}
					bean.setUserCrowdList(list);
					mList.add(bean);
				}
				listbean.addAll(mList);
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean ParseZhiYanJsonZhichizhe(List<UserCrowd> mlist,String msg){
		try{
			JSONObject object=new JSONObject(msg);
			if(object.getInt("status")==1){
				JSONArray array=object.getJSONArray("msg");
				JSONObject OneObject=array.getJSONObject(0);
				JSONArray userCrowsArray=OneObject.getJSONArray("userCrowd");
				List<UserCrowd> listUserCrowds=new ArrayList<UserCrowd>();
				UserCrowd mUserCrowd=null;
				JSONObject josnUserCrowd=null;
				for(int i=1;i<userCrowsArray.length();i++){
					josnUserCrowd=userCrowsArray.getJSONObject(i);
					mUserCrowd=new UserCrowd();
					mUserCrowd.setCcrowdId(josnUserCrowd.optString("crowdId"));
					mUserCrowd.setCdate(josnUserCrowd.optLong("date"));
					mUserCrowd.setCid(josnUserCrowd.optString("id"));
					mUserCrowd.setCinit(josnUserCrowd.optBoolean("init"));
					mUserCrowd.setCmoney(josnUserCrowd.optInt("money"));
					mUserCrowd.setAllMoney(josnUserCrowd.optInt("allMoney"));
					JSONObject object1 = josnUserCrowd.getJSONObject("user");
					UserCrowdUser u = new UserCrowdUser();
					u.setUavata(object1.optString("avatar"));
					u.setUdata(object1.optLong("date"));
					u.setUlevel(object1.optInt("level"));
					u.setUuid(object1.optString("uid"));
					u.setUnickname(object1.optString("nickName"));
					JSONObject object2 = object1.getJSONObject("userInfo");
					u.setAllMoney(object2.optInt("allMoney"));
					u.setCount(object2.optInt("count"));
					mUserCrowd.setUser(u);
					listUserCrowds.add(mUserCrowd);
				}
				mlist.clear();
				mlist.addAll(listUserCrowds);
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean parseYouSuanShishi(List<YouSuanItemModel> YouSuanList,String msg){
		try{
			JSONObject object=new JSONObject(msg);
			JSONArray array=null;
			JSONObject datason=null;
		    List<YouSuanItemModel> model=new ArrayList<YouSuanItemModel>();
			YouSuanItemModel mYouSuanItemModel=null;
		    if(object.getInt("status")==1){
				array=object.getJSONArray("msg");
                if(array.length()==0){
                    return false;
                }
				for(int i=0;i<array.length();i++){
					datason=array.getJSONObject(i);
					mYouSuanItemModel=new YouSuanItemModel(datason.optInt("id"), datason.optString("name"),datason.optDouble("annualRate"),datason.optDouble("maxDrawDown"), 
							datason.optInt("maxDrawDownPeriod"),datason.optDouble("sharpRate"),datason.optLong("adviceMoney"), datason.optString("curvePicUrl"),
							datason.optString("saturation"),datason.optDouble("currentMoonRate"),datason.optDouble("actualRate"),datason.optInt("tradingCount"),
							datason.optLong("updateDate"),datason.optInt("leverMultiple"),datason.optDouble("earningsRisk"),datason.optInt("dealCount"),
							datason.optInt("profitCount"),datason.optLong("profitMoney"),datason.optLong("lossMoney"),datason.optInt("days"),datason.optString("variety"),
							datason.optString("period"),datason.optString("positionType"),datason.optString("category"),datason.optString("desc")
							,datason.optString("key"),datason.optDouble("odds"),datason.optDouble("profitAndLossThan"),datason.optInt("money")
							,datason.optLong("adviceMoney"),datason.optDouble("maxDrawDownMoney"),datason.optDouble("chuShiJingZhi"),datason.optDouble("zuiXinJingZhi"),
                            datason.optDouble("totalYield"),datason.optLong("maxDrawDownMoneyStartTime"),datason.optLong("maxDrawDownMoneyEndTime"),
                            datason.optLong("maxDrawDownStartTime"),datason.optLong("maxDrawDownEndTime"));
					model.add(mYouSuanItemModel);
				}
			}
		    YouSuanList.addAll(model);
		    return true;
		}catch(Exception e){
		    LogUtil.e("parseYouSuanShishi_exception",e.getMessage());
			return false;
		}
	}
	
	public static ZhiYanFinishHeader parseZhiYanHeaderData(String json){
		try{
			JSONObject object=new JSONObject(json);
			if(object.getInt("status")==1){
				JSONObject joData=object.getJSONObject("joData");
				JSONObject newsetObject=joData.getJSONObject("newest");
				//解析主item
				ZhiYanFinishHeader newest=new ZhiYanFinishHeader(
				newsetObject.optString("area"), newsetObject.optInt("emation"),newsetObject.optInt("emationRanking"),
				newsetObject.optDouble("emationRate"), newsetObject.optInt("media"),newsetObject.optInt("mediaRanking"),
				newsetObject.optDouble("mediaRate"),newsetObject.optString("name"),newsetObject.optInt("searchRanking"),
				newsetObject.optDouble("searchRate"),newsetObject.optInt("search"),newsetObject.optString("time"),
				newsetObject.optInt("type"));
				JSONArray items=joData.getJSONArray("items");
				ZhiYanFinishHeaderItem zyitem=null;
				JSONObject objitem=null;
				//遍历解析子item
				for(int i=items.length()-1;i>=0;i--){
					objitem=items.getJSONObject(i);
					zyitem=new ZhiYanFinishHeaderItem(
							objitem.optString("area"), objitem.optInt("emation"),
							objitem.optInt("emationRanking"),objitem.optDouble("emationRate"),objitem.optInt("media"),
							objitem.optInt("mediaRanking"), objitem.optInt("mediaRate"),objitem.optString("name"),
							objitem.optInt("searchRanking"),objitem.optInt("searchRate"),objitem.optInt("search"),
							objitem.optString("time"),objitem.optInt("type"));
					newest.getListItem().add(zyitem);
				}
				return newest;
			}else{
				return null;
			}
		}catch(Exception e){
			LogUtil.e("ZhiYanParseJson_Exception",e.getMessage());
			return null;
		}
	}
	
	//解析优算我的json
	public static void parseYouSuanMyData(String json,List<YouSuanMyItem> YouSuanItem){
		try{
			JSONObject object=new JSONObject(json);
			List<YouSuanMyItem> listItem=new ArrayList<YouSuanMyItem>();
			if(object.getInt("status")==1){
				JSONArray array=object.getJSONArray("msg");
				JSONObject son=null;
				JSONObject datason=null;
				YouSuanMyItem item=null;
				for(int i=0;i<array.length();i++){
					son=array.getJSONObject(i);
					item=new YouSuanMyItem(son.optString("id"),son.optString("uid"),son.optInt("genTouId"),son.optInt("status"),son.optInt("money"),
							son.optInt("profitAndLossMoney"),son.optString("createDate"),son.optString("updateDate"),(float)son.optDouble("maxDrawDown"),
							(float)son.optDouble("moonRate"),(float)son.optDouble("dayRate"),(float)son.optDouble("allRate"),(float)son.optDouble("quan"),
							son.optString("payWay"),son.optString("no"),(float)son.optDouble("allMoney"),son.optInt("couponId"),son.getBoolean("isTest"));
					datason=son.getJSONObject("genTou");
					item.setGetTou(new YouSuanItemModel(datason.optInt("id"), datason.optString("name"),datason.optDouble("annualRate"),datason.optDouble("maxDrawDown"), 
							datason.optInt("maxDrawDownPeriod"),datason.optDouble("sharpRate"),datason.optLong("adviceMoney"), datason.optString("curvePicUrl"),
							datason.optString("saturation"),datason.optDouble("currentMoonRate"),datason.optDouble("actualRate"),datason.optInt("tradingCount"),
							datason.optLong("updateDate"),datason.optInt("leverMultiple"),datason.optDouble("earningsRisk"),datason.optInt("dealCount"),
							datason.optInt("profitCount"),datason.optLong("profitMoney"),datason.optLong("lossMoney"),datason.optInt("days"),datason.optString("variety"),
							datason.optString("period"),datason.optString("positionType"),datason.optString("category"),datason.optString("desc")
							,datason.getString("key"),datason.optDouble("odds"),datason.optDouble("profitAndLossThan"),datason.optInt("money")
							,datason.getLong("adviceMoney"),datason.getDouble("maxDrawDownMoney"),datason.optDouble("chuShiJingZhi"),datason.optDouble("zuiXinJingZhi"),
                            datason.optDouble("totalYield"),datason.optLong("maxDrawDownMoneyStartTime"),datason.optLong("maxDrawDownMoneyEndTime"),
                            datason.optLong("maxDrawDownStartTime"),datason.optLong("maxDrawDownEndTime")));
				listItem.add(item);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
				}
				YouSuanItem.addAll(listItem);
			}else{
				
			}
		}catch(Exception e){
			LogUtil.e("parseYouSuanDataException",e.getMessage());
		}
	}
	
	public static boolean ParseZhiYanRecord(List<YouSuanRecordModel> listmodel,String json){
		try{
			JSONObject object=new JSONObject(json);
			if(object.getInt("status")==1){
				JSONArray array=object.getJSONArray("msg");
				JSONObject msgitem=null;
				for(int i=0;i<array.length();i++){
					msgitem=array.getJSONObject(i);
					listmodel.add(new YouSuanRecordModel(msgitem.optString("productName"),
							msgitem.optString("direction"),msgitem.optInt("count"),msgitem.optInt("point"),
							msgitem.optString("date"),msgitem.optString("name"),msgitem.optInt("leftCount"),
							msgitem.optBoolean("deal"),msgitem.optBoolean("complete")));
				}
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean ParseZhiYanRecord2(List<YouSuanDataModel> listmodel,String json){
		try{
			JSONObject object=new JSONObject(json);
			if(object.getInt("status")==1){
				JSONArray array=object.getJSONArray("msg");
				JSONObject msgitem=null;
				for(int i=0;i<array.length();i++){
					msgitem=array.getJSONObject(i);
					YouSuanDataModel model=new YouSuanDataModel(msgitem.optString("productName"),msgitem.optString("direction"),msgitem.optInt("count"),msgitem.optInt("point"),
							msgitem.optString("date"),msgitem.optString("name"),msgitem.optInt("leftCount"),msgitem.optBoolean("deal"),
							msgitem.optBoolean("complete"),msgitem.getDouble("currentProfit"),msgitem.getDouble("allProfit"));
                    JSONObject closeObj=msgitem.optJSONObject("closeObj");
                    if(closeObj!=null){
                        model.setOpenprice(closeObj.optDouble("point"));
                        model.setOpentime(closeObj.optLong("date"));
                        listmodel.add(model);
                    }
				}
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			LogUtil.e("Exceptionrecored2",e.getMessage());
			return false;
		}
	}
	
	public static boolean ParseQuanyiquxian(List<QiYuXianModel> listModel,String json){
		try{
			JSONObject object=new JSONObject(json);
			if(object.optInt("status")==1){
				JSONArray array=object.getJSONArray("msg");
				JSONObject item=null;
				for(int i=0;i<array.length();i++){
					item=array.getJSONObject(i);
					listModel.add(new QiYuXianModel(item.getInt("jingZhi"),item.getLong("date"),item.getString("name")));
				}
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean ParseYueDuYinKui(List<YueDuYinkuiModel> listmodel,String json){
		try{
			JSONObject object=new JSONObject(json);
			if(object.optInt("status")==1){
				JSONArray array=object.getJSONArray("msg");
				JSONObject item=null;
				for(int i=0;i<array.length();i++){
					item=array.getJSONObject(i);
					listmodel.add(new YueDuYinkuiModel(item.optLong("date"),item.optInt("profit"),item.optString("name")));
				}
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean ParseDuoKongYinKui(List<DuoKongYinKuiModel> listModel,String json){
		try{
			JSONObject object=new JSONObject(json);
			JSONObject item=null;
			if(object.optInt("status")==1){
				JSONArray array=object.getJSONArray("msg");
				if(array.length()==0){
					return false;
				}
				for(int i=0;i<array.length();i++){
					item=array.getJSONObject(i);
					listModel.add(new DuoKongYinKuiModel(item.optDouble("duo"),item.optDouble("kong"),
							item.optString("type"),item.optString("name")));
				}
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			LogUtil.e("Exception_e",e.getMessage());
			return false;
		}
	}
	
	public static boolean ParseHuiCeQuXian(List<HuiCeQuXianModel> listModel,String json){
		try{
			JSONObject object=new JSONObject(json);
			if(object.optInt("status")==1){
				JSONArray array=object.getJSONArray("msg");
				if(array.length()<=0){
					return false;
				}
				JSONObject item=null;
				for(int i=0;i<array.length();i++){
					item=array.getJSONObject(i);
					listModel.add(new HuiCeQuXianModel(item.optString("_id"),
							item.optLong("date"),item.optInt("point"), item.optString("name")));
				}
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
		
	}
	
	
}
