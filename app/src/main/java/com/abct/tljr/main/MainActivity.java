package com.abct.tljr.main;

/*
 _ooOoo_
 o8888888o
 88" . "88
 (| -_- |)
 O\  =  /O
 ____/`---'\____
 .'  \\|     |//  `.
 /  \\|||  :  |||//  \
 /  _||||| -:- |||||-  \
 |   | \\\  -  /// |   |
 | \_|  ''\---/''  |   |
 \  .-\__  `-`  ___/-. /
 ___`. .'  /--.--\  `. . __
 ."" '<  `.___\_<|>_/___.'  >'"".
 | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 \  \ `-.   \_ __\ /__ _/   .-` /  /
 ======`-.____`-.___\_____/___.-`____.-'======
 `=---='
 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 佛祖保佑       永无BUG
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.abct.tljr.BaseFragmentActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.ChartActivity;
import com.abct.tljr.chart.RealTimeView;
import com.abct.tljr.data.Constant;
import com.abct.tljr.data.InitData;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.dialog.NoticeDialog;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.dialog.SearchDialog;
import com.abct.tljr.hangqing.HangQing;
import com.abct.tljr.hangqing.database.OneFenZuRealmImpl;
import com.abct.tljr.hangqing.database.ZiXuanGuDataBaseImpl;
import com.abct.tljr.hangqing.database.ZiXuanGuRealmImpl;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.hangqing.zixuan.TljrZiXuanLineChart;
import com.abct.tljr.hangqing.zixuan.tljr_zixuan_gu_recyclerview;
import com.abct.tljr.hangqing.zuhe.TljrZuHe;
import com.abct.tljr.jiaoyi.JiaoYi;
import com.abct.tljr.kline.gegu.view.OneguGuZhiScrollView;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.service.LoginResultReceiver;
import com.abct.tljr.ui.fragments.MainFragment;
import com.abct.tljr.ui.fragments.zhiyanFragment.zhiyanFragment;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.ui.yousuan.YouSuanBaseFragment;
import com.abct.tljr.utils.Util;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.qh.common.listener.Complete;
import com.qh.common.login.BwManager;
import com.qh.common.login.Configs;
import com.qh.common.login.login.Login;
import com.qh.common.model.User;
import com.qh.common.pay.PayCallBack;
import com.qh.common.util.LogUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.wstt.gt.client.GT;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("HandlerLeak")
public class MainActivity extends BaseFragmentActivity implements PayCallBack {
    protected static final String TAG = MainActivity.class.getSimpleName();
    private PushAgent                mPushAgent;
    public  FragmentViewPagerAdapter adapter;
    public  ViewPager                viewpager;
    public List<Fragment> list = null;
    private       MainTitleBar   titleBar;
    public        RelativeLayout v;
    public        HangQing       hangQing;// 行情
    public        zhiyanFragment zhiyanFragment;
    public        MainFragment   main;
    public static HuanQiuShiShi  huanQiuShiShi;// 新闻
    public        JiaoYi         jiaoYi;// 交易
    //	public ComputerFragment computerFragment;
    public        YouSuanBaseFragment mYouSuanBaseFragment = null;
    private       boolean             isPause              = false;
    public static MainActivity        activity             = null;
    public        int                 ZhiYanfinishItem     = 0;
    public static int                 AddZuHuStatus        = 0;
    public IWXAPI api;

    //用于给主页的scrollview 设置滚动距离的
    //public static int currentX = 0;
    public static int currentY = 0;

    public static int currentY2 = 0;
    //public static boolean scrollFloat=true;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        api = WXAPIFactory.createWXAPI(this, Configs.WeiXinAppId);
        if (Util.WIDTH == 0) {
            InitData.refreshByUser(this);
            Util.init();
        }
        LoginResultReceiver.getInstance(this);// 注册登录注册监听
        isPause = false;
        if (!MyApplication.isupdate) {
            //友盟自动更新
            //			UmengUpdateAgent.update(this);
            MyApplication.isupdate = true;
        }
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
        mPushAgent.enable(mRegisterCallback);
        Constant.device_token = mPushAgent.getRegistrationId();
        v = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(v);
        Constant.noPictureMode = Constant.preference.getBoolean("isNoPitureMode", false);
        Constant.isNewsGuideToast = Constant.preference.getInt("isNewsGuideToast", 0);
        Constant.preference.edit().putBoolean("Mainislive", true).commit();
        RealTimeView.im_realtimeid = Constant.preference.getString("im_realtimeid", "");
        init();

        //		setCurrent(2);
        new startMain().execute();
    }

    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {
        @Override
        public void onRegistered(String registrationId) {
            Constant.device_token = mPushAgent.getRegistrationId();
        }
    };

    private void init() {
        viewpager = (ViewPager) findViewById(R.id.viewPager);
        list = new ArrayList<>();
        hangQing = new HangQing();
        zhiyanFragment = new zhiyanFragment();
        huanQiuShiShi = new HuanQiuShiShi();
        jiaoYi = new JiaoYi();
        main = new MainFragment();
        //		computerFragment = new ComputerFragment();
        mYouSuanBaseFragment = new YouSuanBaseFragment();
        list.add(hangQing);//行情
        list.add(huanQiuShiShi);//资讯
        list.add(main);//主页面
        list.add(zhiyanFragment);//智研
        //		list.add(computerFragment);
        list.add(mYouSuanBaseFragment);//优算
        titleBar = new MainTitleBar(viewpager, (LinearLayout) findViewById(R.id.tljr_grp_bottom));
        adapter = new FragmentViewPagerAdapter(this.getSupportFragmentManager(), viewpager, list);
        viewpager.setOffscreenPageLimit(list.size());
        System.err.println("初始化界面");
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int page) {
                titleBar.onPageSelected(page);
                Constant.addClickCount();
                switch (page) {
                    case 2://主按钮
                        //main.init();
                        break;
                    case 3://智研
                        zhiyanFragment.init();
                        break;
                    case 4://优算
                        //computerFragment.init();
                        //mYouSuanBaseFragment.mYouSuanShishi.initData();
                        mYouSuanBaseFragment.initView();
                        break;
                    case 1://资讯
                        huanQiuShiShi.initView();
                        break;
                    default:
                        break;
                }
            }
        });
        findViewById(R.id.main).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpager.setCurrentItem(2);
                ((ImageView) findViewById(R.id.logo)).setImageResource(R.drawable.img_dibulogo1);
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("Configs.preferencetoken", Configs.preference.getString(Login.TLJR_AUTOLOGIN_TOKEN, ""));
                BwManager.getInstance().loginAuto();
                hangQing.show();
                mHandler.post(runnable);
                if (!Constant.isRelease)
                    PgyUpdateManager.register(MainActivity.this, new UpdateManagerListener() {
                        @Override
                        public void onUpdateAvailable(final String result) {
                            // 将新版本信息封装到AppBean中
                            final AppBean appBean = getAppBeanFromString(result);
                            new PromptDialog(MainActivity.this, "有新版本，是否更新?", new Complete() {
                                @Override
                                public void complete() {
                                    startDownloadTask(MainActivity.this, appBean.getDownloadURL());
                                }
                            }).show();
                        }

                        @Override
                        public void onNoUpdateAvailable() {
                        }
                    });
            }
        }, 500);

    }

    public void setCurrent(int p) {
        viewpager.setCurrentItem(p);
    }

    public void showSouSuo(String zuName) {
        new SearchDialog(this).show(zuName);
    }

    public void showMessage(String msg) {
        Message message = new Message();
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    public void showMessage(int what, String msg) {
        Message message = new Message();
        message.what = what;
        message.obj = msg;
        mHandler.sendMessage(message);
        mHandler.sendEmptyMessage(101);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null == v) {
                return;
            }
            switch (msg.what) {
                case 1:
                    NoticeDialog.showNoticeDlg("正在加载中...", MainActivity.this);
                    break;
                case 2:
                    NoticeDialog.stopNoticeDlg();
                    break;
                case 5:
                    NoticeDialog.showNoticeDlg(msg.obj.toString(), MainActivity.this);
                    break;
                case 6:
                    // if (shouYe.syXinWen != null)
                    // shouYe.syXinWen.ChangeFontSize();
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    break;
                case 10:
                    mHandler.sendEmptyMessage(8);
                    break;
                case 11:
                    try {
                        if (hangQing != null) {
                            if (AddZuHuStatus == 0) {
                                TljrZuHe.initView(true);
                                tljr_zixuan_gu_recyclerview.getZiXuanGu();
                            } else {
                                GuDealImpl.addZuHeUpdate();
                            }
                        }
                    } catch (Exception e) {
                    }
                    break;
                case 92:
                    ProgressDlgUtil.stopProgressDlg();
                    break;
                case 93:
                    login();
                    break;
                case 94:
                    // 登陆后，获取个人添加的股票
                    if (TljrZiXuanLineChart.mSYChart != null) {
                        TljrZiXuanLineChart.mSYChart.gettitlelist();
                        TljrZiXuanLineChart.mSYChart.getpreposition();
                    }
                    break;
                case 95:
                    RealTimeView.redpointisshow = true;
                    break;
                case 96:
                    if (User.getUser() == null) {
                        login(false);
                    } else {
                        Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                        RealTimeView.redpointisshow = false;
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.this.startActivity(intent);
                    }
                    break;
                case 98:
                    break;
                case 99:
                    // 提醒相关
                    break;
                case 101:
                    break;
                case 102:
                    if (hangQing != null && hangQing.getActivity() != null && hangQing.hqGridView != null)
                        hangQing.hqGridView.freshGrid();
                    break;
                case 103:
                    // 注销登录
                    if (AddZuHuStatus != 1) {
                        if (hangQing != null) {
                            hangQing.refreshSynTime();
                            TljrZuHe.initView(true);
                            tljr_zixuan_gu_recyclerview.getZiXuanGu();
                            if (TljrZuHe.zuheAdapter != null) {
                                TljrZuHe.zuheAdapter.clear();
                                TljrZuHe.listzuhe.clear();
                            }
                            ZiXuanUtil.fzMap.clear();
                            OneFenZuRealmImpl.DeleteAllZu();
                            ZiXuanGuRealmImpl.removeAllCode();
                            ZiXuanGuDataBaseImpl.DeleteAllZiXuanGu();
                        }
                    }
                    if (TljrZiXuanLineChart.mSYChart != null) {
                        TljrZiXuanLineChart.mSYChart.unlogin();
                    }
                    break;
                case 201:
                    login();
                    break;
                case 1000:// 网络变化
                    zhiyanFragment.flush();
                    if (mYouSuanBaseFragment.mComputerFragment != null) {
                        mYouSuanBaseFragment.mComputerFragment.initData();
                    }
                    break;
                case 301:
                    showToast(getIntent().getStringExtra("tonghu"));
                    break;
                case 302:
                    // hangQing.ziXuan.adapter.notifyDataSetChanged();
                    // hangQing.setZiXuanNum(hangQing.ziXuan.listZiXuan.size() - 1);
                    break;
                default:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    private int sendTime = 0;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(runnable, 1000);
            RealTimeView.CheckNewIMReal();
            sendTime++;
            if (sendTime >= 2 && MyApplication.getInstance().self != null && ZiXuanUtil.actions.size() > 0) {
                ZiXuanUtil.sendActions(MyApplication.getInstance().self);
                sendTime = 0;
            }
            if (!isPause) {
                switch (viewpager.getCurrentItem()) {
                    case 0:
                        hangQing.oneSecAction();
                        break;
                    case 2:
                        main.oneSecAction();
                        break;
                    default:
                        break;
                }
            }
            // if (shouYe.syTitlePage != null)
            // shouYe.syTitlePage.showTime();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event != null && event.getRepeatCount() == 0) {
            showNoticeDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        MobclickAgent.onResume(this);
    /*	if (OneGuActivity.addZuName.size() > 0) {
			for (int i = 0; i < OneGuActivity.addZuName.size(); i++) {
				// GuDealImpl.addGuInfo(MainActivity.this,OneGuActivity.gukey,OneGuActivity.addZuName.get(i));
			}
			OneGuActivity.addZuName.clear();
		}
		if (OneGuActivity.removeZuName.size() > 0) {
			for (int i = 0; i < OneGuActivity.removeZuName.size(); i++) {
				ZiXuanUtil.delStock(OneGuActivity.gukey, OneGuActivity.removeZuName.get(i));
				showMessage(9, OneGuActivity.gukey);
			}
			OneGuActivity.removeZuName.clear();
		}*/
        int flushTime = Constant.FlushTimes[Constant.preference.getInt(Constant.FLUSHKEY, 4)];
        if (Constant.FlushTime != flushTime) {
            Constant.FlushTime = flushTime;
        }
        if (MyApplication.getInstance().self != null) {
            mHandler.sendEmptyMessage(98);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        MobclickAgent.onPause(this);
    }

    public void showNoticeDialog() {
        if (!MainActivity.this.isFinishing()) {
            new PromptDialog(MainActivity.this).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(LoginResultReceiver.getInstance(this));
        unregisterReceiver(tljr_zixuan_gu_recyclerview.mUpdateZiXuanGu);
        LoginResultReceiver.clearReceiver();
        LoginResultReceiver.sendOnlineStop();
        MyApplication.getInstance().exit();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        GT.disconnect(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && TljrZiXuanLineChart.mSYChart != null) {
            TljrZiXuanLineChart.mSYChart.initView();
        }
        if (resultCode == 1) {
            String type = data.getStringExtra("type");
            float quan = data.getFloatExtra("quan", 0);
            if (requestCode == 1) {
                zhiyanFragment.artzhongchou.adapter.onpay(type, quan, null);
            } else if (requestCode == 2) {
                ZhiYanfinishItem = data.getIntExtra("item", 0);
                zhiyanFragment.mZhiYanFinishView.listLeftBeans.get(ZhiYanfinishItem).
                        getFinishViewItemShow().adapter.onpay(type, quan, null);
            } else if (requestCode == 3) {
                LogUtil.e("requestCode", requestCode + "");
                mYouSuanBaseFragment.mComputerFragment.onpay(type, quan, null);
            } else if (requestCode == 4) {
                LogUtil.e("requestCode", requestCode + "");
                mYouSuanBaseFragment.mYouSuanShishi.onpay(type, quan * 100, null);
            }
        }
    }

    public void initUser() {
        main.initUser();
        huanQiuShiShi.refreshFragment();
        zhiyanFragment.flush();
        mYouSuanBaseFragment.flush();
    }

    @Override
    public void payerror(String arg0) {
        showToast(arg0);
        if (zhiyanFragment != null && zhiyanFragment.mZhiYanFinishView != null
                && zhiyanFragment.mZhiYanFinishView.listLeftBeans.get(ZhiYanfinishItem).getFinishViewItemShow().adapter != null
                && zhiyanFragment.mZhiYanFinishView.listLeftBeans.get(ZhiYanfinishItem).getFinishViewItemShow().adapter.paybean != null) {
            zhiyanFragment.mZhiYanFinishView.listLeftBeans.get(ZhiYanfinishItem).getFinishViewItemShow().adapter.paybean = null;
        }
    }

    @Override
    public void paysuccess(String arg0) {
        showToast(arg0);
        Toast.makeText(activity, "paysuccess", Toast.LENGTH_SHORT).show();
        if (zhiyanFragment != null && zhiyanFragment.mZhiYanFinishView != null) {
            zhiyanFragment.artzhongchou.initData();

            if (zhiyanFragment.mZhiYanFinishView.listLeftBeans.get(ZhiYanfinishItem).getFinishViewItemShow().adapter.paybean != null) {
                zhiyanFragment.mZhiYanFinishView.listLeftBeans.get(ZhiYanfinishItem).getFinishViewItemShow().adapter.paySuccess(arg0);
            }

            if (mYouSuanBaseFragment.mComputerFragment != null && mYouSuanBaseFragment.mComputerFragment.getBean() != null)
                mYouSuanBaseFragment.mComputerFragment.paySuccess(arg0);
        }
    }

    class startMain extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                setCurrent(2);
                ((ImageView) findViewById(R.id.logo)).setImageResource(R.drawable.img_dibulogo1);
            }
        }
    }


    private   OneguGuZhiScrollView   mainFragmentScrollView;
    public OneguGuZhiScrollView getMainFragmentScrollView() {
             return   mainFragmentScrollView;
    }

    public void setMainFragmentScrollView(OneguGuZhiScrollView  scrollView) {
            this.mainFragmentScrollView=scrollView;
    }
}
