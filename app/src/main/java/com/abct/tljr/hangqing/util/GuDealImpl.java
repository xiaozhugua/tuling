package com.abct.tljr.hangqing.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.realm.Realm;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.dialog.SearchDialog;
import com.abct.tljr.hangqing.HangQing;
import com.abct.tljr.hangqing.database.OneFenZuModel;
import com.abct.tljr.hangqing.database.OneFenZuRealmImpl;
import com.abct.tljr.hangqing.database.ZiXuanGu;
import com.abct.tljr.hangqing.database.ZiXuanGuDataBaseImpl;
import com.abct.tljr.hangqing.database.ZiXuanGuRealmImpl;
import com.abct.tljr.hangqing.model.ZiXuanOneGu;
import com.abct.tljr.hangqing.model.ZuHeModel;
import com.abct.tljr.hangqing.zixuan.tljr_zixuan_gu_recyclerview;
import com.abct.tljr.hangqing.zuhe.TljrZuHe;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.model.OneFenZu;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.umeng.analytics.MobclickAgent;

public class GuDealImpl {

	private static Context contexts=null;
	
	// 添加个股
	public static void addGuInfo(final Context context, String gukey,final String zhName,final String zuid) {
		MobclickAgent.onEvent(context, "AddStock");
			if (ZiXuanUtil.isHas(zhName, gukey)) {
				showMessage(context, "请勿重复添加");
				return;
			}
			Realm realm = Realm.getDefaultInstance();
			final OneGu gu = Constant.cloneGu(realm.where(OneGu.class).equalTo("marketCode", gukey).findFirst());
			realm.close();
			if (gu == null) {
				showMessage(context, "添加失败，请稍后重试");
				return;
			}
		// showMessage(5, "更新自选股数据中");
		Util.getRealInfo("market=" + gu.getMarket()+"&code="+gu.getCode(),
				new NetResult() {
					@Override
					public void result(final String msg) {
						try {
							org.json.JSONObject object = new org.json.JSONObject(msg);
							if (object.getInt("code") == 1) {
								final org.json.JSONArray array = object.getJSONArray("result");
								gu.setZuid(zuid);
								gu.setTime(System.currentTimeMillis());
								gu.setNow(array.getDouble(0));
								gu.setChange((float) array.getDouble(8));
								gu.setP_change((float) array.getDouble(9));
								gu.setP_changes((gu.getP_change() > 0 ? "+":"") + gu.getP_change() + "%");
								gu.setFirst(gu.getNow());
								gu.setPercent(0);
								gu.setKaipanjia(array.getDouble(2));
								gu.setKey(gu.getMarket()+gu.getCode());
							}
							ZiXuanUtil.addStock(gu, zhName);	
							Intent intent=new Intent("tljr_TiaocangGu_updateadd");
							intent.putExtra("oneGu",gu);
							context.sendBroadcast(intent);
							ZiXuanGuRealmImpl.addOneGu(getZiXuanGu(gu));
							if(SearchDialog.map!=null){
								SearchDialog.map.put(gu.getKey(),gu);
							}
							
							ProgressDlgUtil.stopProgressDlg();
						} catch (Exception e) {
								
						}
					}
			});
	}

	public static ZiXuanOneGu getZiXuanGu(OneGu mOneGu){
		return new ZiXuanOneGu(mOneGu.getCode(),mOneGu.getName(),
				mOneGu.getFirst(),mOneGu.getP_change(),mOneGu.getMarket(),
				mOneGu.getKey(),mOneGu.getTag(),mOneGu.getTime(),mOneGu.getTop()
				,mOneGu.getId(),mOneGu.getZuid(),mOneGu.getZuid()+mOneGu.getMarket()+mOneGu.getCode());
	}
	
	public static OneGu getOneGu(ZiXuanOneGu mOneGu){
		return new OneGu(mOneGu.getCode(),mOneGu.getName(),mOneGu.getFirst(),mOneGu.getP_change(),
				mOneGu.getMarket(),mOneGu.getKey(),mOneGu.getTag(),mOneGu.getTime(),mOneGu.getTop(),
				mOneGu.getId(),mOneGu.getZuid(),mOneGu.getZuidkey());
	}
	
	public static OneGu getNewOneGu(OneGu mOneGu){
		return new OneGu(mOneGu.getCode(),mOneGu.getName(),
				mOneGu.getFirst(),mOneGu.getP_change(),mOneGu.getMarket(),
				mOneGu.getKey(),mOneGu.getTag(),mOneGu.getTime(),mOneGu.getTop()
				,mOneGu.getId(),mOneGu.getZuid(),mOneGu.getZuid()+mOneGu.getCode());
	}
	
	
	// 添加股票弹窗显示
	public static void DialogShow(final OneGu gu, final String zuname,final Context context) {
		// 获取所有的组
		ArrayList<OneFenZu> list = new ArrayList<OneFenZu>(ZiXuanUtil.fzMap.values());
		boolean dialogstatus = true;
		// 过滤添加过分组
		ArrayList<OneFenZu> templist = new ArrayList<OneFenZu>();
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).getList().size(); j++) {
				if(list.get(i).getList().get(j).getName()==null){
					Realm realm = Realm.getDefaultInstance();
					OneGu gus = Constant.cloneGu(realm.where(OneGu.class).equalTo("marketCode",list.get(i).getList().get(j)
							.getMarket()+list.get(i).getList().get(j).getCode()).findFirst());
					list.get(i).getList().get(j).setName(gus.getName());
				}
				if (list.get(i).getList().get(j).getName()!=null&&
						list.get(i).getList().get(j).getName().equals(gu.getName())) {
					dialogstatus = false;
				}
			}
			if (dialogstatus) {
				templist.add(list.get(i));
			} else {
				dialogstatus = true;
			}
		}
		// 初始个股数据
		final String[] items = new String[templist.size()];
		final boolean[] checkedItems = new boolean[templist.size()];
		boolean SearchStatus = false;
		for (int z = 0; z < templist.size(); z++) {
			items[z] = templist.get(z).getName();
			if (templist.get(z).getName().equals(zuname)) {
				SearchStatus = true;
				checkedItems[z] = true;
			} else {
				checkedItems[z] = false;
			}
		}

		if (checkedItems.length>0&&SearchStatus == false) {
			checkedItems[0] = true;
		}

		String title = "";
		if (zuname != null) {
			title = "请选择添加股票的分组";
		}
		// 初始化并显示Dialog
		AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setTitle(title)
				.setMultiChoiceItems(items, checkedItems,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which, boolean isChecked) {
								// 点击时各组数据的变化
								checkedItems[which] = isChecked;
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 确定添加股票
						for (int i = 0; i < checkedItems.length; i++) {
							if (checkedItems[i] == true) {
								Intent intent = new Intent("com.tljr.mainActivityResult");
								intent.putExtra("code", gu.getKey());
								intent.putExtra("name",gu.getName());
								intent.putExtra("zuid",ZiXuanUtil.fzMap.get(items[i]).getId());
								
								if (items[i] != null && !items[i].equals("")) {
									intent.putExtra("zuName", items[i]);
								}
								context.sendBroadcast(intent);
								ProgressDlgUtil.showProgressDlg("",(Activity) context);
							}
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 取消按钮
					}
				}).create();
		alertDialog.show();
	}

	public static void AddZuHeGu(Context context,String key,String name,String zuname){
		Intent intent = new Intent("com.tljr.mainActivityResult");
		intent.putExtra("code",key);
		intent.putExtra("name",name);
		intent.putExtra("zuid",ZiXuanUtil.fzMap.get(zuname).getId());
		if (zuname != null && !zuname.equals("")) {
			intent.putExtra("zuName",zuname);
		}
		context.sendBroadcast(intent);
		ProgressDlgUtil.showProgressDlg("",(Activity) context);
	}
	
	
	// 调仓删除个股
	public static void showRemoveOneGuDialog(final Context context,final List<OneGu> listGu, String zuname) {
		if (listGu.size() != 0) {
			final String ZuName = zuname;
			final String[] arrayItems = new String[listGu.size()];
			final boolean[] checkedItems = new boolean[listGu.size()];
			final String[] arraykey = new String[listGu.size()];
			for (int i = 0; i < listGu.size(); i++) {
				arrayItems[i] = listGu.get(i).getName();
				arraykey[i] = listGu.get(i).getKey();
				checkedItems[i] = false;
			}
			AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(context);
			mAlertDialog.setTitle("删除股票");
			mAlertDialog.setMultiChoiceItems(arrayItems, checkedItems,
							new DialogInterface.OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog,int which, boolean isChecked) {
									checkedItems[which] = true;
								}
							})
					    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,int which) {
									boolean status = false;
									for (int j = 0; j < checkedItems.length; j++) {
										if (checkedItems[j] == true) {
											status = true;
										}
									}
									if (status) {
										//确定按钮
										for(int i = 0; i < checkedItems.length; i++) {
											if(checkedItems[i] == true) {
												OneGu mOneGu=null;
												//执行删除操作
												for (int j=0;j<listGu.size();j++) {
													if (listGu.get(j).getName().equals(arrayItems[i])) {
														mOneGu=listGu.get(j);
														listGu.remove(j);
													}
												}
												GuDealImpl.deleteGu(context,mOneGu.getCode(),mOneGu.getMarket(),arraykey[i],ZuName);
											}
										}
									} else {
										showMessage(context, "对不起，没有选择股票");
									}
								}
							}).setNegativeButton("取消", null);
					mAlertDialog.create().show();
		} else {
			showMessage(context, "对不起,没有股票可以删除");
		}
	}

	public static void deleteGu(Context context,String code,String market,String key,String zuName){
		//删除更新自选
		//TljrZiXuanGu.RemoveZiXuanGuView(code);
		//不是现在的组就直接删除
		ZiXuanUtil.delStock(key,zuName);
		//更新组合股票列表
		Intent tiaocanggu=new Intent("tljr_TiaocangGu_update");
		tiaocanggu.putExtra("code",code);
		tiaocanggu.putExtra("market",market);
		if(contexts!=null){
			contexts.sendBroadcast(tiaocanggu);
		}
		//更新组合股票百分比
		Intent tiaocangpercent=new Intent("tljr_tiaocang_changeupdate");
		tiaocangpercent.putExtra("code",code);
		tiaocangpercent.putExtra("market",market);
		if(contexts!=null){
			contexts.sendBroadcast(tiaocangpercent);
		}
	}
	
	
	// 改名和备份
	@SuppressLint("InflateParams")
	public static void showGaimingAndBeizu(final Context context, String title,
			String hint, final int action, final String zuname,final TextView ZuHeItemName) {
		AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(context);
		View view = LayoutInflater.from(context).inflate(R.layout.chengpercent_alertdialog, null);
		final EditText mEditText = (EditText) view.findViewById(R.id.changpercent_alertdialog_show);
		mEditText.setHint(hint);
		mEditText.setHintTextColor(context.getResources().getColor(R.color.black));
		mEditText.setTextColor(context.getResources().getColor(R.color.black));
		if (action == 1) {
			mAlertDialog.setIcon(R.drawable.img_fenzugaiming);
		} else {
			mAlertDialog.setIcon(R.drawable.img_beizhu);
		}
		mAlertDialog.setTitle(title);
		mAlertDialog.setView(view);
		mAlertDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@SuppressWarnings("static-access")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (action == 1) {
							String s = Util.shortFenZuName(mEditText.getText().toString());
							if (s.equals("")) {
								Toast.makeText(context,"请输入名称",Toast.LENGTH_SHORT).show();
								return;
							}
							if (ZiXuanUtil.fzMap.containsKey(s)) {
								Toast.makeText(context,"存在重名分组",Toast.LENGTH_SHORT).show();
								return;
							}
							ZiXuanUtil.changeFenzu(zuname, s);
							ZiXuanUtil.emitNowFenZu(s);
							ZuHeModel model;
							for(int i=0;i<HangQing.mTljrZuHe.zuheAdapter.getCount();i++){
								model = HangQing.mTljrZuHe.zuheAdapter.getItem(i);
								if (zuname.equals(model.getName())) {
									HangQing.mTljrZuHe.zuheAdapter.getItem(i).setName(s);
								}
							}
							HangQing.mTljrZuHe.zuheAdapter.notifyDataSetChanged();
							if(ZuHeItemName!=null){
								ZuHeItemName.setText(s);
							}
						} else {
							String s = mEditText.getText().toString();
							if (s.equals("")) {
								Toast.makeText(context, "输入不能为空",Toast.LENGTH_SHORT).show();
								return;
							}
							ZiXuanUtil.updateTag(zuname, s);
							ZuHeModel model;
							for (int i = 0; i < HangQing.mTljrZuHe.zuheAdapter.getCount(); i++) {
								model = HangQing.mTljrZuHe.zuheAdapter.getItem(i);
								if (zuname.equals(model.getName())) {
									HangQing.mTljrZuHe.zuheAdapter.getItem(i).setBeizhu(s);
								}
							}
							HangQing.mTljrZuHe.zuheAdapter.notifyDataSetChanged();
							if(ZuHeItemName!=null){
								ZuHeItemName.setText(s);
							}
						}
					}
				});
		mAlertDialog.setNegativeButton("取消", null);
		mAlertDialog.create().show();
	}

	//删除分组
	public static void RemoveFenZu(Context context,final String zuName,final String zuid){
		if (ZiXuanUtil.fzMap.size() == 1) {
            showMessage(context,"最少保留一个分组!");
            return;
        }
        MobclickAgent.onEvent(context, "delGroup");
        new AlertDialog.Builder(context)
                .setMessage("确定要删除此分组？")
                .setCancelable(false)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
							@Override
                            public void onClick(DialogInterface arg0,int arg1) {
                            	TljrZuHe.removeFenZu(zuName);
                            	//TljrZiXuanGu.removeZuGu(zuName);
                                ZiXuanUtil.deleteFenzu(zuName);
                                ZiXuanUtil.emitNowFenZu(ZiXuanUtil.fzLists.get(0));
                                ZiXuanGuRealmImpl.removeZuCode(zuid);
                                OneFenZuRealmImpl.deleteZuHe(zuid);
                            }
         }).setNegativeButton("取消", null).show();
	}
	
	// 刷新调仓数据得到开盘价
	public static void RefreshKaiPangJia(final List<OneGu> listGus,String key,final Handler mHandler,final int what) {
			if(listGus.size()>0){
				final OneFenZu zus = ZiXuanUtil.fzMap.get(key);
				Map<String, OneGu> listGuParams = new HashMap<String, OneGu>();
				for (OneGu gu : listGus) {
					listGuParams.put(gu.getCode(), gu);
				}
				String param = "list=";
				int m = 0;
				for (int i = 0; i < listGus.size(); i++) {
					OneGu gu = zus.getList().get(i);
					if (m == 0) {
						param += (gu.getMarket()+"|"+gu.getCode());
						m++;
					} else {
						param += (","+gu.getMarket()+"|"+gu.getCode());
					}
				}
				Util.getRealInfo(param, new NetResult() {
					@Override
					public void result(String msg) {
						try {
							final JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 1) {
									org.json.JSONArray arr = object.getJSONArray("result");
									for (int i = 0; i < arr.length(); i++) {
										JSONObject object2 = arr.getJSONObject(i);
										JSONArray array = object2.getJSONArray("data");
										String code = object2.getString("code");

										for (int j = 0; j < listGus.size(); j++) {
											if (listGus.get(j).getCode().equals(code)) {
												listGus.get(j).setKaipanjia(array.optDouble(2));
												listGus.get(j).setNow(array.optDouble(0));
												listGus.get(j).setYclose(array.optDouble(1));
												listGus.get(j).setP_changes(((float) array.optDouble(9,0)>0?"+":"")
																		+ (float) array.optDouble(9,0)+"%");
											}
										}
									}
								}
								if(mHandler!=null){
									Message message=Message.obtain();
									message.what=what;
									mHandler.sendMessage(message);
								}
							} catch (JSONException e) {
								if(contexts!=null){
									Toast.makeText(contexts, "解析出错！", Toast.LENGTH_SHORT).show();
								}
							}
					}
					},true);
			}
	}
	
	//刷新数据得到组合百分比
	public static void getZhPercent(final String paramkey,final List<OneGu> listGu,final Handler mHandler,final int what) {
		if(listGu.size()<0){
			return ;
		}
		final Map<String, Integer> tempPercent= new HashMap<String, Integer>();
		final Map<String, String> tempCodeId= new HashMap<String, String>();
		final Map<String, Double> tempCodeKaipanjia= new HashMap<String, Double>();
		if (MyApplication.getInstance().self != null) {
			NetUtil.sendPost(UrlUtil.URL_tc, "action=getzh&uid="
						+ MyApplication.getInstance().self.getId() + "&zhid="
						+ paramkey, new NetResult() {
				@Override
				public void result(String msg) {
					try {
						JSONObject object = new JSONObject(msg);
						DecimalFormat df = new DecimalFormat("#.00");
						int status = object.getInt("status");
						if (status == 1) {
							JSONArray array = object.getJSONArray("msg");
							if (!array.toString().equals("[]")) {
								String code;
								for (int i = 0; i < array.length(); i++) {
									JSONObject json = array.getJSONObject(i);
									code = json.getString("code");
									tempCodeKaipanjia.put(code, Double.valueOf(df.format(json.getDouble("open"))));
									tempPercent.put(code,Math.round((Float.valueOf(json.getString("percent")) * 100)));
									tempCodeId.put(code, json.getString("id"));
								}
								for (String keys : tempPercent.keySet()) {
									for (int i = 0; i < listGu.size(); i++) {
										if (listGu.get(i).getCode().equals(keys)) {
											listGu.get(i).setPercent(tempPercent.get(keys));
											listGu.get(i).setId(tempCodeId.get(keys));
											listGu.get(i).setKaipanjia(tempCodeKaipanjia.get(keys));
										}
									}
								}
							} else {
						}
					  } else if (status == 2) {
							JSONArray array = object.getJSONArray("msg");
							String code;
							for (int i = 0; i < array.length(); i++) {
								JSONObject json = array.getJSONObject(i);
								code = json.getString("code");
								tempCodeKaipanjia.put(code, Double.valueOf(df.format(json.getDouble("open"))));
								tempCodeId.put(code, json.getString("id"));
							}
							for (String keys : tempPercent.keySet()) {
								for (int i = 0; i < listGu.size(); i++) {
									if (listGu.get(i).getCode().equals(keys)) {
										listGu.get(i).setId(tempCodeId.get(keys));
										listGu.get(i).setKaipanjia(tempCodeKaipanjia.get(keys));
									}
								}
							}
						}
					 } catch (Exception e) {
					
					 }
					 if(mHandler!=null){
						 Message message=Message.obtain();
						 message.what=what;
						 mHandler.sendMessage(message);
					 }
					
					}
				});
			}
		}
	
	//得到组合所有股票的id
	public static void getZhGuId(String zhid,final List<OneGu> listGu,final Handler mHandler,final int what) {	
			NetUtil.sendPost(UrlUtil.URL_dmg,"id=" + MyApplication.getInstance().self.getId()
					+ "&operaList=" + "[]" + "&type=get&v=1&zhid="+ zhid, new NetResult() {
				@Override
				public void result(String msg) {
					List<Map<String, String>> ListParam = new ArrayList<Map<String, String>>();
					Map<String, String> param = null;
					try {
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
						
							for (Map<String, String> map : ListParam) {
								for (int j = 0; j < listGu.size(); j++) {
									if (map.get("code").equals(listGu.get(j).getCode())) {
										listGu.get(j).setId(map.get("id"));
									}
								}
							}
						}
						if(mHandler!=null){
							Message message=Message.obtain();
							message.what=what;
							mHandler.sendMessage(message);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				});
	}

	//增加一只股票刷新
	public static void addZuHeUpdate(){
        List<OneFenZu> listFenZu = new ArrayList<OneFenZu>(ZiXuanUtil.fzMap.values());
        OneFenZu mOneFenZu=null;
        OneFenZuModel model=new OneFenZuModel();
        final ZuHeModel mZuHeModel=TljrZuHe.listzuhe.get(TljrZuHe.listzuhe.size()-1);
        for(int i=0;i<listFenZu.size();i++){
        	 mOneFenZu=listFenZu.get(i);
             if(mZuHeModel.getName().equals(mOneFenZu.getName())){
                 mZuHeModel.setId(mOneFenZu.getId());
                 mZuHeModel.setName(mOneFenZu.getName());
                 String time = String.valueOf(mOneFenZu.getTime());
                    if (time.equals("")) {
                        time = "0天";
                    } else {
                        time = ((System.currentTimeMillis() - Long.valueOf(time)) / 86400000l + 1)+ "天";
                  }
                 mZuHeModel.setTime(time);
                 mZuHeModel.setImgUrl("");
                 mZuHeModel.setJsonData("");
                 mZuHeModel.setKey(mOneFenZu.getId());
                 
                 model.setBeizu("");
                 model.setName(mOneFenZu.getName());
                 model.setTime(mOneFenZu.getTime()+"");
                 model.setZuid(mOneFenZu.getId());
                 OneFenZuRealmImpl.addZuHe(model);
                 break;
             }
        }
        if(MyApplication.getInstance().self!=null){
        	NetUtil.sendGet(UrlUtil.URL_tc, "action=getnewzhinfo&uid="+MyApplication.getInstance().self.getId() + 
                "&zhid="+mZuHeModel.getId(), new NetResult() {
            @SuppressWarnings("rawtypes")
			@Override
            public void result(String msg) {
                try{
                    JSONObject object=new JSONObject(msg);
                    JSONObject msgObject=object.getJSONObject("msg");
                    Iterator it=msgObject.keys();
                    while(it.hasNext()){
                        JSONObject temp=msgObject.getJSONObject(it.next().toString());
                        mZuHeModel.setImgUrl(temp.getString("imgurl"));
                        mZuHeModel.setJsonData(temp.toString());
                        mZuHeModel.setZong(Float.valueOf(temp.getString("rishouyi")));
                        if(mZuHeModel.getZong()==0.0){
                        	mZuHeModel.setZong_float(".0%");
                        	mZuHeModel.setZong_int("0");
                        }
                        mZuHeModel.setRi(Float.valueOf(temp.getString("yueshouyi")));
                        if(mZuHeModel.getRi()==0.0){
                        	mZuHeModel.setRi_float(".0%");
                        	mZuHeModel.setRi_int("0");
                        }
                        mZuHeModel.setYue(Float.valueOf(temp.getString("zongshouyi")));
                        if(mZuHeModel.getYue()==0.0){
                        	mZuHeModel.setYue_float(".0%");
                        	mZuHeModel.setYue_int("0");
                        }
                    }
                    TljrZuHe.zuheAdapter.notifyDataSetChanged();
                    MainActivity.AddZuHuStatus = 0;
                }catch(Exception e){
                    Log.e("GuDealImpl_message",e.getMessage());
                    MainActivity.AddZuHuStatus = 0;
                }
            }
        });
        }
    }
	
	public static void DeleteZiXuanGuImpl(final Context context,final ZiXuanGu gu,final Map<String,ZiXuanGu> MapGu){
		if(gu.getId()!=null){
			JSONArray array=new JSONArray();
			array.put(Integer.valueOf(gu.getId()));
			String url="http://120.24.235.202:8080/ZhiLiYinHang/ZiXuanServlet?method=del&uid="
					+MyApplication.getInstance().self.getId()+"&token="+Configs.token+"&del="+array.toString();
			ProgressDlgUtil.showProgressDlg("",context);
			NetUtil.sendGet(url,new NetResult() {
				@Override
				public void result(String response){
					try{
						if(!response.equals("")){
						JSONObject object=new JSONObject(response);
							if(object.getInt("status")==1){
								ZiXuanGuDataBaseImpl.DeleteZiXuanGu(gu.getId());
								for(int i=0;i<tljr_zixuan_gu_recyclerview.listZiXuanGu.size();i++){
									String key=tljr_zixuan_gu_recyclerview.listZiXuanGu.get(i).getMarket()+
											tljr_zixuan_gu_recyclerview.listZiXuanGu.get(i).getCode();
									if(key.equals(gu.getMarket()+gu.getCode())){
										tljr_zixuan_gu_recyclerview.listZiXuanGu.remove(i);
										MapGu.remove(gu.getMarket()+gu.getCode());
										break;
									}
								}
								tljr_zixuan_gu_recyclerview.adapter.notifyDataSetChanged();
								Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
							}
								ProgressDlgUtil.stopProgressDlg();		
							}else{
								Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
							}
						}catch(Exception e){
								Toast.makeText(context,"服务器出错",Toast.LENGTH_SHORT).show();
						}
					}
			});
		}
	}
	
	public static void showMessage(Context context, String msg){
		GuDealImpl.contexts=context;
		Message message=Message.obtain();
		message.what=1;
		Bundle bundle=new Bundle();
		bundle.putString("msg",msg);
		message.setData(bundle);
		mHandler.sendMessage(message);	
	}
	
	final static Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				if(GuDealImpl.contexts!=null){
					Toast.makeText(GuDealImpl.contexts,msg.getData().getString("msg"),Toast.LENGTH_SHORT).show();
				}
				break;
			}
		};
	};
}
