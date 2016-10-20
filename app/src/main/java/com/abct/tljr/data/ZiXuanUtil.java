package com.abct.tljr.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant.readComplete;
import com.abct.tljr.hangqing.database.OneFenZuModel;
import com.abct.tljr.hangqing.database.OneFenZuRealmImpl;
import com.abct.tljr.hangqing.database.ZiXuanGuRealmImpl;
import com.abct.tljr.hangqing.model.ZiXuanOneGu;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.hangqing.zuhe.TljrZuHe;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.Actions;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.widget.ZiXuanShanchuProgressbar;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.qh.common.volley.AuthFailureError;
import com.qh.common.volley.Request.Method;
import com.qh.common.volley.Response;
import com.qh.common.volley.VolleyError;
import com.qh.common.volley.toolbox.StringRequest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 自选股管理类
 * 
 * @author Administrator
 * 
 */

public class ZiXuanUtil {
	public static ArrayList<Actions> actions = new ArrayList<Actions>();
	public static String ACTIONS = "DEFAULT";
	public static String ACTIONSKEY = "USERID";
	public static String NOWFENZUKEY = "nowfenzukey";
	public static String nowFenZu;
	public static Map<String, OneFenZu> fzMap = new HashMap<String, OneFenZu>();
	public static ArrayList<String> fzLists = new ArrayList<String>();
	public static Map<String, OneFenZu> defaultfzMap = new HashMap<String, OneFenZu>();
	public static String DATAKEY = "MAP";
	private static List<Map<String, String>> ListParam = new ArrayList<Map<String, String>>();

	public static boolean isHas(String zhName, String guKey) {
		ArrayList<OneGu> list = fzMap.get(zhName).getList();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getKey()!=null&&list.get(i).getKey().equals(guKey)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isHas(String guKey) {
		for (String key : fzMap.keySet()) {
			ArrayList<OneGu> list = fzMap.get(key).getList();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getKey().equals(guKey)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void sendActions(User self, final MainActivity activity, final Complete... completes) {
		JSONArray array = new JSONArray();
		for (int i = 0; i < actions.size(); i++) {
			actions.get(i).addObject(array);
		}
		if (!ACTIONS.equals(self.getId()) && !ACTIONS.equals("DEFAULT")) {
			if (actions.size() > 0) {
				sendActions(self);
			}
		}
//		String url=UrlUtil.URL_dmg+"?id=" + self.getId()+"&operaList="+array.toString()+"&type=get&v=1";
		activity.showMessage(5, "正在同步组合数据");
		NetUtil.sendPost(UrlUtil.URL_dmg,"id=" + self.getId()+"&operaList="+array.toString()+"&type=get&v=1",
				new NetResult() {
					@Override
					public void result(String msg) {
						activity.mHandler.sendEmptyMessage(2);
						getGuFenZu(msg,activity.mHandler);
						if (completes != null) {
							for (Complete complete : completes) {
								complete.complete();
							}
						}
					}
				});
		Constant.lastSynTime = Util.getDate(System.currentTimeMillis());
	}

	public static ArrayList<Actions> getAction() {
		return actions;
	}

	public static void sendActions(User self, final NetResult revMsg) {
		JSONArray array = new JSONArray();
		for (int i = 0; i < actions.size(); i++) {
			actions.get(i).addObject(array);
		}
		if (actions.size() > 0) {
			NetUtil.sendPost(UrlUtil.URL_dmg,
					"id=" + self.getId() + "&operaList=" + array.toString() + "&type=send&v=1", new NetResult() {
						@Override
						public void result(String msg) {
							try {
								JSONObject object = new JSONObject(msg);
								if (object.getInt("status") == 1) {
									Constant.lastSynTime = Util.getDate(System.currentTimeMillis());
									revMsg.result("1");
									actions.clear();
								} else {
									revMsg.result("0");
								}
							} catch (JSONException e) {
								e.printStackTrace();
								revMsg.result("0");
							}
						}
					});
		} else {
			Constant.lastSynTime = Util.getDate(System.currentTimeMillis());
			revMsg.result("1");
		}
	}

	public static void sendActions(final User self, final Complete... completes) {
		final JSONArray array = new JSONArray();
		for (int i = 0; i < actions.size(); i++) {
			actions.get(i).addObject(array);
		}
		if (actions.size() > 0) {
			final Message message = new Message();
			message.what = 6;
			final Bundle bundle = new Bundle();
			
			NetUtil.mQueue.add(new StringRequest(Method.POST,UrlUtil.URL_dmg,new Response.Listener<String>() {
						@Override
						public void onResponse(String msg) {
							try {
								JSONObject object = new JSONObject(msg);
								if (object.getInt("status") == 1) {
									bundle.putString("msg", "同步成功");
									message.setData(bundle);
									TljrZuHe.mHandler.sendMessage(message);
									ProgressDlgUtil.stopProgressDlg();
									Constant.lastSynTime = Util.getDate(System.currentTimeMillis());
									actions.clear();
									if (completes != null) {
										for (Complete complete : completes) {
											complete.complete();
										}
									}
									//发送删除请求
									Intent intentMessage = new Intent("com.tiaocang.tljrdeletegu");
									intentMessage.putExtra("message",array.toString());
									MyApplication.getInstance().sendBroadcast(intentMessage);
									//发送更新id请求
									Intent intentAddId=new Intent("tljr_tiaocang_updateZuId");
									MyApplication.getInstance().sendBroadcast(intentAddId);
								} else {
									ProgressDlgUtil.stopProgressDlg();
									bundle.putString("msg", "同步失败");
									message.setData(bundle);
									TljrZuHe.mHandler.sendMessage(message);
								}
								ZiXuanShanchuProgressbar.closeProgressDialog();
							} catch (JSONException e) {
								ZiXuanShanchuProgressbar.closeProgressDialog();
								bundle.putString("msg", "同步失败");
								message.setData(bundle);
								TljrZuHe.mHandler.sendMessage(message);
							}
						}	
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							ZiXuanShanchuProgressbar.closeProgressDialog();
							bundle.putString("msg", "同步失败");
							message.setData(bundle);
							TljrZuHe.mHandler.sendMessage(message);
						}
					}) {
						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("id", self.getId());
							params.put("operaList", array.toString());
							params.put("type", "send");
							params.put("v", "1");
							return params;
						}

						@Override
						public Map<String, String> getHeaders() throws AuthFailureError {
							HashMap<String, String> headers = new HashMap<String, String>();
							headers.put("accept", "*/*");
							headers.put("connection", "Keep-Alive");
							headers.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
							headers.put("Charsert", "UTF-8");
							return headers;
						}
					});
		} else {
			Constant.lastSynTime = Util.getDate(System.currentTimeMillis());
		}
	}

	public static void getGuFenZu(String msg, Handler handler) {
		try {
			JSONObject object = new JSONObject(msg);
			if (object.getInt("status") == 1) {
				Constant.lastSynTime = Util.getDate(System.currentTimeMillis());
				actions.clear();
				JSONArray array = object.getJSONArray("msg");
				Map<String, OneFenZu> map = new HashMap<String, OneFenZu>();
				for (String key : fzMap.keySet()) {
					map.put(key, fzMap.get(key));
				}
				fzMap = new HashMap<String, OneFenZu>();
				fzLists.clear();
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					OneFenZu zu = new OneFenZu(obj.getString("name"));
					zu.setTag(obj.optString("tag"));
					zu.setTime(obj.optLong("time"));
					if (zu.getTime() < 1000000000000l){
						zu.setTime(obj.optLong("createDate"));
					}
					zu.setId(obj.optString("id"));
					JSONArray arr = obj.getJSONArray("stocks");
					ArrayList<OneGu> list = new ArrayList<OneGu>();
					for (int j = 0; j < arr.length(); j++) {
						JSONObject stock = arr.getJSONObject(j);
						String key = stock.optString("key", stock.optString("market") + stock.optString("code"));
						OneGu gu = null;
						if (map.containsKey(zu.getName()))
							gu = isHasGu(key, map.get(zu.getName()).getList());
						if (gu == null)
							gu = new OneGu();
						gu.setCode(stock.optString("code"));
						gu.setZuidkey(zu.getId()+key);
						gu.setMarket(stock.optString("market"));
						gu.setName(stock.optString("name"));
						gu.setTag(stock.optString("tag"));
						gu.setFirst(stock.optDouble("price"));
						gu.setTime(stock.optLong("time"));
						gu.setZuid(obj.optString("id"));
						if (gu.getTime() < 1000000000000l) {
							gu.setTime(stock.optLong("createDate"));
						}
						gu.setTop(stock.optInt("top"));
						gu.setP_change((float) stock.optDouble("changeP"));
						gu.setKey(key);
						gu.setId(stock.optString("id"));
						list.add(gu);
					}
					zu.setList(list);
					fzMap.put(zu.getName(), zu);
				}
				map.clear();
//				String name = MyApplication.getInstance().getResources().getString(R.string.morenfenzu);
//				//没有组合则默认给他一个组合
//				if (!fzMap.containsKey(name)) {
//					addNewFenzu(name, false);
////					TljrZuHe.addNewFenZu(name);
//					MainActivity.AddZuHuStatus = 1;
//					ZiXuanUtil.emitNowFenZu(name);
//					if (MyApplication.getInstance().self != null) {
//						ZiXuanUtil.sendActions(MyApplication.getInstance().self
//								,MyApplication.getInstance().getMainActivity(), null);
//					}
//				}
				
				fzLists = new ArrayList<String>(fzMap.keySet());
				
				List<ZiXuanOneGu> ZiXuanGu=new ArrayList<ZiXuanOneGu>();
				List<OneFenZuModel> listOneZu=new ArrayList<OneFenZuModel>();
				ZiXuanOneGu gu=null;
	            OneFenZuModel model=null;
	            for (OneFenZu oneFenZu:ZiXuanUtil.fzMap.values()){
	            	model=new OneFenZuModel();
	            	model.setBeizu(oneFenZu.getTag());
	            	model.setName(oneFenZu.getName());
	            	model.setTime(oneFenZu.getTime()+"");
	            	model.setZuid(oneFenZu.getId());
	            	listOneZu.add(model);
	                List<OneGu> l=oneFenZu.getList();
	                for (int j=0;j<l.size();j++) {
	                	gu=GuDealImpl.getZiXuanGu(l.get(j));
	                    ZiXuanGu.add(gu);
	                }
	            }
	            
	            if(listOneZu.size()>0){
	            	OneFenZuRealmImpl.addUpdateZuHe(listOneZu);
	            }
				
	            if(ZiXuanGu.size()>0){
	            	ZiXuanGuRealmImpl.saveAndUpdateListZiXuanGu(ZiXuanGu);
	            }
	            
				handler.sendEmptyMessage(7);
				handler.sendEmptyMessage(11);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static OneGu isHasGu(String key, ArrayList<OneGu> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getKey().equals(key)) {
				return list.get(i);
			}
		}
		return null;
	}

	public static void readStorage(final Complete complete, Context app) {
		ACTIONS = Constant.preference.getString(ACTIONSKEY, "DEFAULT");
		Constant.dataRead(DATAKEY, new readComplete() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void read(Object object) {
				if (object == null) {
					defaultfzMap = new HashMap<String, OneFenZu>();
				} else {
					defaultfzMap = (Map) object;
				}
				if (defaultfzMap == null)
					defaultfzMap = new HashMap<String, OneFenZu>();
				nowFenZu = MyApplication.getInstance().getResources().getString(R.string.morenfenzu);
				if (!defaultfzMap.containsKey(nowFenZu)) {
					addNewFenzu(defaultfzMap, nowFenZu, true);
				}
				fzLists = new ArrayList<String>(defaultfzMap.keySet());
				fzMap = defaultfzMap;
				if (!fzMap.containsKey(nowFenZu)) {
					nowFenZu = fzLists.get(0);
				}
				Constant.readNums++;
				if (Constant.readNums == 3) {
					complete.complete();
				}
			}
		});
		Constant.dataRead(ACTIONS, new readComplete() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void read(Object object) {
				ZiXuanUtil.actions = (ArrayList) object;
				if (ZiXuanUtil.actions == null) {
					ZiXuanUtil.actions = new ArrayList<Actions>();
				}
				Constant.readNums++;
				if (Constant.readNums == 3) {
					complete.complete();
				}
			}
		});
	}

	public static OneFenZu addNewFenzu(String name, boolean isFirst) {
		OneFenZu zu = new OneFenZu(name);
		zu.setTime(System.currentTimeMillis());
		fzMap.put(name, zu);
		fzLists.add(name);
		if (!isFirst)
			addGroup(name);
		return zu;
	}

	public static OneFenZu addNewFenzu(Map<String, OneFenZu> map, String name, boolean isFirst) {
		OneFenZu zu = new OneFenZu(name);
		zu.setTime(System.currentTimeMillis());
		map.put(name, zu);
		fzLists.add(name);
		if (!isFirst)
			addGroup(name);
		return zu;
	}

	public static void changeFenzu(String name, String newName) {
		OneFenZu zu = fzMap.get(name);
		zu.setName(newName);
		fzMap.remove(name);
		fzMap.put(newName, zu);
		for (int i = 0; i < fzLists.size(); i++) {
			if (fzLists.get(i).equals(name)) {
				fzLists.set(i, newName);
				break;
			}
		}
		updateGroup(name, newName);
	}

	public static void deleteFenzu(String name) {
		fzMap.remove(name);
		fzLists.remove(name);
		delGroup(name);
	}

	public static void updateTag(String name, String tag) {
		if (name != null && tag != null) {
			fzMap.get(name).setTag(tag);
			updateGroupTag(name, tag);
		}
	}

	public static void addStock(OneGu gu) {
		fzMap.get(nowFenZu).getList().add(gu);
		addStock(nowFenZu, gu);
	}

	public static void addStock(OneGu gu, String name) {
		fzMap.get(name).getList().add(gu);
		addStock(name, gu);
	}

	public static void delStock(OneGu gu) {
		fzMap.get(nowFenZu).getList().remove(gu);
		delStock(nowFenZu, gu);
	}

	public static void delStock(String guKey, String name) {
		for (int i = 0; i < fzMap.get(name).getList().size(); i++) {
			LogUtil.e("delStockKey",fzMap.get(name).getList().get(i).getName());
			if (fzMap.get(name).getList().get(i).getKey().equals(guKey)) {
				OneGu gu = fzMap.get(name).getList().get(i);
				LogUtil.e("delStickKey_name",gu.getName());
				delStock(name, gu);
				fzMap.get(name).getList().remove(gu);
				break;
			}
		}
	}

	public static void emitNowFenZu(String key) {
		nowFenZu = key;
	}

	/**
	 * 添加分组
	 * 
	 * @param name分组名称
	 */
	public static void addGroup(String name) {
		Actions action = new Actions();
		action.setType("g");
		action.setAction("add");
		action.setgName(name);
		action.setTime(System.currentTimeMillis());
		actions.add(action);
	}

	/**
	 * 删除分组
	 * 
	 * @param name分组名称
	 */
	public static void delGroup(String name) {
		Actions action = new Actions();
		action.setType("g");
		action.setAction("del");
		action.setgName(name);
		action.setTime(System.currentTimeMillis());
		actions.add(action);
	}

	/**
	 * 分组改名
	 * 
	 * @param name旧分组名称
	 * @param newName新分组名称
	 */
	public static void updateGroup(String name, String newName) {
		Actions action = new Actions();
		action.setType("g");
		action.setAction("updateName");
		action.setgName(name);
		action.setgNewName(newName);
		action.setTime(System.currentTimeMillis());
		actions.add(action);
	}

	/**
	 * 分组添加标签
	 * 
	 * @param name分组名
	 * @param tag分组标签
	 */
	public static void updateGroupTag(String name, String tag) {
		Actions action = new Actions();
		action.setType("g");
		action.setAction("updateTag");
		action.setgName(name);
		action.setGTag(tag);
		action.setTime(System.currentTimeMillis());
		actions.add(action);
	}

	/**
	 * 添加股票
	 * 
	 * @param name分组名
	 * @param gu添加的股票
	 */
	public static void addStock(String name, OneGu gu) {
		Actions action = new Actions();
		action.setType("s");
		action.setAction("add");
		action.setgName(name);
		action.setTime(System.currentTimeMillis());
		action.setsCode(gu.getCode());
		action.setsMarket(gu.getMarket());
		action.setsPrice(gu.getNow());
		action.setPrice(gu.getNow());
		action.setsName(gu.getName());
		action.setChangeP(gu.getP_change());
		action.setKey(gu.getKey());
		actions.add(action);
	}

	/**
	 * 删除股票
	 * 
	 * @param name分组名
	 * @param gu删除的股票
	 */
	public static void delStock(String name, OneGu gu) {
		Actions action = new Actions();
		action.setType("s");
		action.setAction("del");
		action.setgName(name);
		action.setTime(System.currentTimeMillis());
		action.setsCode(gu.getCode());
		action.setsMarket(gu.getMarket());
		action.setsName(gu.getName());
		actions.add(action);
	}

	/**
	 * 置顶股票
	 * 
	 * @param name分组名
	 * @param gu
	 */
	public static void topStock(String name, OneGu gu) {
		Actions action = new Actions();
		action.setType("s");
		action.setAction("top");
		action.setgName(name);
		action.setTime(System.currentTimeMillis());
		action.setsCode(gu.getCode());
		action.setsMarket(gu.getMarket());
		action.setsName(gu.getName());
		actions.add(action);
	}

	/**
	 * 取消置顶
	 * 
	 * @param name分组名
	 * @param gu
	 */
	public static void cancelTopStock(String name, OneGu gu) {
		Actions action = new Actions();
		action.setType("s");
		action.setAction("cancelTop");
		action.setgName(name);
		action.setTime(System.currentTimeMillis());
		action.setsCode(gu.getCode());
		action.setsMarket(gu.getMarket());
		action.setsName(gu.getName());
		actions.add(action);
	}

	/**
	 * 添加股票标签
	 * 
	 * @param name分组名
	 * @param gu
	 */
	public static void updateStockTag(String name, OneGu gu) {
		Actions action = new Actions();
		action.setType("s");
		action.setAction("updateTag");
		action.setgName(name);
		action.setTime(System.currentTimeMillis());
		action.setsCode(gu.getCode());
		action.setsMarket(gu.getMarket());
		action.setsName(gu.getName());
		action.setsTag(gu.getTag());
		actions.add(action);
	}

	// 加百分比
	public static void updateStockPercent(String name, OneGu gu) {
		Actions action = new Actions();
		action.setType("s");
		action.setAction("tiaocang");
		action.setgName(name);
		action.setTime(System.currentTimeMillis());
		action.setsCode(gu.getCode());
		action.setsMarket(gu.getMarket());
		action.setsName(gu.getName());
		action.setPercent(gu.getPercent());
		actions.add(action);
		sendActions(MyApplication.getInstance().self);
	}

	public static void addNewCodeGu(final String zhid, final String zhName) {
		try {
			NetUtil.sendPost(UrlUtil.URL_dmg, "id=" + MyApplication.getInstance().self.getId() + "&operaList=" + "[]"
					+ "&type=get&v=1&zhid=" + zhid, new NetResult() {
						@Override
						public void result(String msg) {
							parseGroupData(msg, zhName);
						}
					});
		} catch (Exception e) {
		}
	}

	public static void parseGroupData(String msg, String zhName) {
		try {
			Map<String, String> param = null;
			JSONObject object = new JSONObject(msg);
			if (object.getInt("status") == 1) {
				JSONArray array = object.getJSONArray("msg");
				JSONObject obj = array.getJSONObject(0);
				JSONArray stocks = obj.getJSONArray("stocks");
				JSONObject stock = null;
				for (int i = 0; i < stocks.length(); i++) {
					stock = stocks.getJSONObject(i);
					param = new HashMap<String, String>();
					param.put("code", stock.getString("code"));
					param.put("id", stock.getString("id"));
					ListParam.add(param);
				}
			} else {
			}
			OneFenZu addCodeZu = ZiXuanUtil.fzMap.get(zhName);
			for (int j = 0; j < addCodeZu.getList().size(); j++) {
				if (addCodeZu.getList().get(j).getId() == null || addCodeZu.getList().get(j).getId().equals("")) {
					for (int a = 0; a < ListParam.size(); a++) {
						if (ListParam.get(a).get("code").equals(addCodeZu.getList().get(j).getCode())) {
							addCodeZu.getList().get(j).setId(ListParam.get(a).get("id"));
						}
					}
				}
			}
			Message message = new Message();
			message.what = 6;
			Bundle bundle = new Bundle();
			bundle.putString("msg", "同步成功");
			message.setData(bundle);
			TljrZuHe.mHandler.sendMessage(message);
		} catch (Exception e) {
		}
	}

}
