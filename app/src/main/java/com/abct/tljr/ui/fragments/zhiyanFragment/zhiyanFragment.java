package com.abct.tljr.ui.fragments.zhiyanFragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.R;
import com.abct.tljr.dialog.SearchDialog;
import com.abct.tljr.model.UserCrowd;
import com.abct.tljr.model.UserCrowdUser;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.activity.zhiyan.LaunchReSearchActivity;
import com.abct.tljr.ui.activity.zhiyan.ReSearchActivity;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.ZhiyanArtZhongchouAdapter;
import com.abct.tljr.ui.fragments.zhiyanFragment.fragment.ArtificialZhongchou;
import com.abct.tljr.ui.fragments.zhiyanFragment.fragment.ZhiYanFinishView;
import com.abct.tljr.ui.fragments.zhiyanFragment.fragment.ZhiYanMyView;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.yousuan.activity.DeleteMyZhiYan;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import io.realm.Realm;

/**
 * Created by Administrator on 2016/1/26.
 */
public class zhiyanFragment extends BaseFragment implements OnClickListener {
	ViewPager viewpager;
	ArrayList<View> list1;
	public ArtificialZhongchou artzhongchou;
	//public FinishView finishView;
	public ZhiYanFinishView mZhiYanFinishView;
	//public MyView myView;
	public ZhiYanMyView mMyView;
	public RadioGroup rg;
	public RadioButton myRadioButton=null;
	public int myNum=0;
	private RelativeLayout zhiyanmenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baseView = View.inflate(activity,R.layout.fragment_zhiyan, null);
        zhiyanmenu=(RelativeLayout)baseView.findViewById(R.id.zhiyanmenu);
        zhiyanmenu.setOnClickListener(this);
	}

	public void flush() {
		if (mZhiYanFinishView == null)
			return;
			flushNums();
			mZhiYanFinishView.LogoutInOut();
			mMyView.initData();
			artzhongchou.initData();
	}

	@SuppressWarnings("deprecation")
	public void init() {
		if (viewpager != null) {
			return;
		}
		list1 = new ArrayList<View>();
		artzhongchou = new ArtificialZhongchou(activity);
//		finishView = new FinishView(activity);
		mZhiYanFinishView=new ZhiYanFinishView(activity);
		//myView = new MyView(activity);
		mMyView=new ZhiYanMyView(activity);
		list1.add(mZhiYanFinishView.getView());
		list1.add(artzhongchou.getView());
//		list1.add(myView);
		list1.add(mMyView.getView());
		viewpager = (ViewPager) findViewById(R.id.scroll_viewPager);
		viewpager.setOffscreenPageLimit(3);
		rg = (RadioGroup) findViewById(R.id.tljr_rg_futures_change);
		myRadioButton=((RadioButton)rg.getChildAt(2));
		viewpager.setAdapter(new PagerAdapter() {
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(list1.get(position));
				return list1.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(list1.get(position));
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return list1.size();
			}
		});

		viewpager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int page) {
                rg.check(rg.getChildAt(page).getId());
                if(page==2){
                    showMenu();
                }else{
                    hideMenu();
                }
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				viewpager.getParent().requestDisallowInterceptTouchEvent(true);
			}
			@Override
			public void onPageScrollStateChanged(int arg0){}
		});
		
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				for (int i = 0; i < rg.getChildCount(); i++) {
					if (rg.getChildAt(i).getId() == checkedId) {
						viewpager.setCurrentItem(i);
                        if(i==2){
                            showMenu();
                        }else{
                            hideMenu();
                        }
					}
				}
				rg.check(checkedId);
			}
		});
		
		rg.check(R.id.radio1);
		findViewById(R.id.serch).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SearchDialog(activity, new NetResult() {
					@Override
					public void result(String msg) {
						serchCode(msg);
					}
				}).show(null);
			}
		});
		flushNums();
	}

	public void serchCode(String msg) {
		LogUtil.e("msg", msg);
		String[] info = msg.split("&");
		final String market = info[0];
		final String code = info[1];
		Realm realm = Realm.getDefaultInstance();
		realm.close();
		String url;
		if (User.getUser() == null) {
			url = UrlUtil.URL_ZR + "crowd/getCrowListByCode?code=" + code;
		} else {
			url = UrlUtil.URL_ZR + "crowd/getCrowListByCode?code=" + code + "&uid=" + User.getUser().getId();
		}
		LogUtil.e("serchCode", url);
		ProgressDlgUtil.showProgressDlg("", activity);
		NetUtil.sendGet(url, new NetResult() {
			@Override
			public void result(String arg0) {
				LogUtil.e("serchCode", arg0);
				ProgressDlgUtil.stopProgressDlg();
				try {
					JSONObject jsonObject = new JSONObject(arg0);
					if (jsonObject.getInt("status") == 1) {
						JSONArray array = jsonObject.getJSONArray("msg");
						if (array.length() == 0) {
							startLanchCode(market, code);
							return;
						}
						for (int j = 0; j < array.length(); j++) {
							JSONObject ob = array.getJSONObject(j);
							ZhongchouBean bean = new ZhongchouBean();
							bean.setCode(ob.optString("code"));
							bean.setMarket(ob.optString("market"));
							bean.setCount(ob.optInt("count"));
							bean.setCreateDate(ob.optLong("createDate"));
							bean.setEndDate(ob.optLong("endDate"));
							bean.setHasMoney(ob.optInt("hasMoney"));
							bean.setIconurl(ob.optString("icon"));
							bean.setId(ob.optString("id"));
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
							JSONArray ar2 = ob.getJSONArray("userCrowd");
							ArrayList<UserCrowd> list = new ArrayList<UserCrowd>();
							for (int x = 0; x < ar2.length(); x++) {
								JSONObject o = ar2.getJSONObject(x);
								UserCrowd crowd = new UserCrowd();
								crowd.setCcrowdId(o.getString("crowdId"));
								crowd.setCdate(o.getLong("date"));
								crowd.setCid(o.getString("id"));
								crowd.setCinit(o.getBoolean("init"));
								crowd.setCmoney(o.getInt("money"));
								crowd.setFocs(o.optBoolean("isFocus"));
								crowd.setMsg(o.optString("msg"));
								JSONObject object1 = o.getJSONObject("user");
								UserCrowdUser u = new UserCrowdUser();
								u.setUavata(object1.getString("avatar"));
								u.setUdata(object1.getLong("date"));
								u.setUlevel(object1.getInt("level"));
								u.setUuid(object1.getString("uid"));
								u.setUnickname(object1.getString("nickName"));
								crowd.setUser(u);
								list.add(crowd);
							}
							bean.setUserCrowdList(list);
							if (bean.getMarket().equals(market) && bean.getCode().equals(code)) {
								if (ob.getInt("status") == 1) {// 已完成
									int updateSatatus=-1;
									String key=bean.getMarket()+bean.getCode();
									for(Integer sextion:mZhiYanFinishView.saveSection.keySet()){
										if(mZhiYanFinishView.listLeftBeans.get(sextion)!=null&&mZhiYanFinishView.listLeftBeans
												.get(sextion).getFinishViewItemShow()!=null){
											ArrayList<ZhongchouBean> listBean=mZhiYanFinishView.listLeftBeans
													.get(sextion).getFinishViewItemShow().listBean;
											for(int i=0;i<listBean.size();i++){
												if(key.equals(listBean.get(i).getMarket()+listBean.get(i).getCode())){
													updateSatatus=sextion;
													break;
												}
											}
										}
									}
									if(updateSatatus==-1){
										mZhiYanFinishView.listLeftBeans.get(mZhiYanFinishView.nowSection).getFinishViewItemShow().adapter.pay(bean);
									}else{
										mZhiYanFinishView.listLeftBeans.get(updateSatatus).getFinishViewItemShow().adapter.pay(bean);
									}
								} else {// 正在众筹
									Intent i = new Intent(activity, ReSearchActivity.class);
									i.putExtra("market", bean.getMarket());
									i.putExtra("code", bean.getCode());
									i.putExtra("id", bean.getId());
									i.putExtra("url", bean.getIconurl());
									i.putExtra("type", bean.getType());
									i.putExtra("typedesc", bean.getTypedesc());
									int persen = bean.getHasMoney() * 100 / bean.getTotalMoney();
									i.putExtra("persen", persen);
									i.putExtra("focus", bean.getFocus());
									i.putExtra("remark", bean.getRemark());
									i.putExtra("isfocus", bean.isIsfocus());
									i.putExtra("section", bean.getSection());
									ZhiyanArtZhongchouAdapter.userCrowdList = bean.getUserCrowdList();
									activity.startActivity(i);
								}
								return;
							}
						}
						startLanchCode(market, code);
					} else {
						Toast.makeText(activity, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					startLanchCode(market, code);
				}
			}
		});
	}

    public void hideMenu(){
        if(zhiyanmenu.getVisibility()==View.VISIBLE)
        zhiyanmenu.setVisibility(View.GONE);
    }

    public void showMenu(){
        if(zhiyanmenu.getVisibility()==View.GONE)
        zhiyanmenu.setVisibility(View.VISIBLE);
    }

	private void startLanchCode(String market, String code) {
		Intent i = new Intent(activity, LaunchReSearchActivity.class);
		i.putExtra("market", market);
		i.putExtra("code", code);
		activity.startActivity(i);
	}

	public void flushNums() {
		String parms = "";
		if (User.getUser() != null) {
			parms += ("uid=" + User.getUser().getId());
		}
		NetUtil.sendGet(UrlUtil.URL_ZR + "crowd/getCrowdTypeCount", parms, new NetResult() {
			@Override
			public void result(String arg0) {
				try {
					JSONObject jsonObject = new JSONObject(arg0);
					if (jsonObject.getInt("status") == 1) {
						JSONObject ob = jsonObject.getJSONObject("msg");
						((RadioButton) rg.getChildAt(0)).setText("已完成(" + ob.getInt("finished") + ")");
						((RadioButton) rg.getChildAt(1)).setText("众筹研究(" + ob.getInt("unfinished") + ")");
						((RadioButton) rg.getChildAt(2)).setText("我的(" + ob.getInt("my") + ")");
						myNum=ob.getInt("my");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// 智研模块相关-登陆调用与服务器对接
	public static void Loginzhiyan() {
		NetUtil.sendPost(UrlUtil.URL_ZR + "user/login",
				"uid=" + User.getUser().getId() + "&token=" + Configs.token + "&avatar=" + User.getUser().getAvatarUrl()
						+ "&nickName=" + User.getUser().getNickName() + "&level=" + User.getUser().getLevel(),
				new NetResult() {
					@Override
					public void result(String msg) {
						// TODO Auto-generated method stub
						LogUtil.e("Test", "zhiyan login msg :" + msg);
					}
				});
	}

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.zhiyanmenu:
                startActivity(new Intent(activity, DeleteMyZhiYan.class));
                break;
        }
    }
}
