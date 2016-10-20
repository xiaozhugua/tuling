package com.abct.tljr.ui.activity.zhiyan;
 
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.chart.ImageOptions;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.dialog.SpeakDialog;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.UserCrowd;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.news.bean.Comment;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.activity.WebActivity;
import com.abct.tljr.ui.adapter.research.SupportAdapter;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.ZhiyanArtZhongchouAdapter;
import com.abct.tljr.ui.fragments.zhiyanFragment.util.ZhiYanParseJson;
import com.abct.tljr.ui.widget.ContainListView;
import com.abct.tljr.ui.widget.DefaultIconDrawable;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.BwManager;
import com.qh.common.login.Configs;
import com.qh.common.login.util.ShareContent;
import com.qh.common.model.User;
import com.qh.common.pay.AliPay;
import com.qh.common.pay.PayCallBack;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.DateUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import io.realm.Realm;
 
/**
 * Created by mac on 16/1/26.
 */
public class ReSearchActivity extends BaseActivity implements View.OnClickListener, PayCallBack {
 
   private SupportAdapter adapter;
   private ContainListView listView;
   private EditText et_support_num;
   private TextView sure;
   private String market, code, id;
   private int focus, remark;
   private boolean isfocus;
   private ArrayList<UserCrowd> arraylist;
   private IWXAPI api;
   private LinearLayout tljr_zListView_use;
   private View newest_comment;
   private ArrayList<Comment> list = new ArrayList<Comment>();
   private int[] colors = { ColorUtil.red, ColorUtil.green, ColorUtil.blue };
   private TextView[] tvs = new TextView[3];
   private UserCrowd userCorwd;
   private boolean hasChange;// 有过支付或者是关注
   public static boolean paystatus=true;
   public float tempmoney;
   private int persen=0;
   private ZhongchouBean bean=null;
   
   @SuppressLint("InflateParams")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_research);
       api = WXAPIFactory.createWXAPI(this, Configs.WeiXinAppId);
       BwManager.getInstance().initShare(this);
       market = getIntent().getStringExtra("market");
       code = getIntent().getStringExtra("code");
       id = getIntent().getStringExtra("id");
       focus = getIntent().getIntExtra("focus", 0);
       remark = getIntent().getIntExtra("remark", 0);
       isfocus = getIntent().getBooleanExtra("isfocus", false);
       ((TextView) findViewById(R.id.speaknum)).setText(remark + "");
       ((TextView) findViewById(R.id.guanzhunum)).setText(focus + "");
       persen = getIntent().getIntExtra("persen", 0);
       ((ProgressBar) findViewById(R.id.seekbar)).setProgress(persen);
       ((TextView) findViewById(R.id.tv_num_progress)).setText(persen + "/100");
       Realm realm = Realm.getDefaultInstance();
       OneGu gu = realm.where(OneGu.class).equalTo("code", code).equalTo("market", market).findFirst();
       if (gu != null) {
           ((TextView) findViewById(R.id.tv_name)).setText(gu.getName());
           ((TextView) findViewById(R.id.research_title)).setText("智研·" + gu.getName());
       }
       ((TextView) findViewById(R.id.tv_code)).setText(code);
       String url = getIntent().getStringExtra("url");
       if (url.equals("http://www.baidu.com")) {
           ((ImageView) findViewById(R.id.img_logo)).setImageDrawable(
                   new DefaultIconDrawable(((ImageView) findViewById(R.id.img_logo)).getLayoutParams().height,
                           ((TextView) findViewById(R.id.tv_name)).getText().toString(), false));
       } else {
           ImageLoader.getInstance().displayImage(url, (ImageView) findViewById(R.id.img_logo),
                   ImageOptions.getCircleListOptions());
       }
       String typ = getIntent().getStringExtra("type");
       String typedesc = getIntent().getStringExtra("typedesc");
       ((TextView) findViewById(R.id.level)).setText(typ);
       ((TextView) findViewById(R.id.research_introduce)).setText(typedesc);
 
       userCorwd = ZhiyanArtZhongchouAdapter.userCrowdList.get(0);
       String msg = userCorwd.getMsg().equals("") ? "暂未提供说明！" : userCorwd.getMsg();
       ((TextView) findViewById(R.id.msg)).setText(msg);
       ((TextView) findViewById(R.id.date)).setText(DateUtil.getDateNoss(userCorwd.getCdate() * 1000));
       ((TextView) findViewById(R.id.textView2)).setText("支持:" + userCorwd.getAllMoney() / 100 + "元");
       ImageLoader.getInstance().displayImage(userCorwd.getUser().getUavata(),
               (ImageView) findViewById(R.id.img_avatar), Options.getCircleListOptions());
       ((TextView) findViewById(R.id.tv_user_name)).setText(userCorwd.getUser().getUnickname());
       ((TextView) findViewById(R.id.tv_user_level)).setText("Lv" + userCorwd.getUser().getUlevel());
       ((TextView) findViewById(R.id.tv_user_support)).setText(
               "累计发起:" + userCorwd.getUser().getCount() + "次/累计支持:" + userCorwd.getUser().getAllMoney() / 100 + "元");
       String[] s = getIntent().getStringExtra("section").split("-");
       tvs[0] = (TextView) findViewById(R.id.tv1);
       tvs[1] = (TextView) findViewById(R.id.tv2);
       tvs[2] = (TextView) findViewById(R.id.tv3);
       ((GradientDrawable) findViewById(R.id.level).getBackground()).setStroke(2, colors[0]);
       for (int i = 0; i < tvs.length; i++) {
           tvs[i].setTextColor(colors[i]);
           GradientDrawable gd = (GradientDrawable) tvs[i].getBackground();
           gd.setStroke(2, colors[i]);
           if (i < s.length && !s[i].equals("")) {
               tvs[i].setVisibility(View.VISIBLE);
               tvs[i].setText(s[i]);
           } else {
               tvs[i].setVisibility(View.GONE);
           }
       }
       listView = (ContainListView) findViewById(R.id.lv_support);
 
       View header = (LayoutInflater.from(this).inflate(R.layout.item_research_header, null));
       ((TextView) header.findViewById(R.id.tv_support_num))
               .setText("支持者(" + (ZhiyanArtZhongchouAdapter.userCrowdList.size() - 1) + ")");
 
       listView.addHeaderView(header);
       // listView.addFooterView(footer);
       arraylist = new ArrayList<UserCrowd>();
       if (ZhiyanArtZhongchouAdapter.userCrowdList.size() > 1)
           arraylist.addAll(ZhiyanArtZhongchouAdapter.userCrowdList.subList(1, ZhiyanArtZhongchouAdapter.userCrowdList.size()));
       // if (ZhiyanArtZhongchouAdapter.userCrowdList.size() == 1) {
       // arraylist = new ArrayList<UserCrowd>();
       // } else {
       // arraylist = (ArrayList<UserCrowd>)
       // ZhiyanArtZhongchouAdapter.userCrowdList.subList(1,
       // ZhiyanArtZhongchouAdapter.userCrowdList.size());
       // }
       adapter = new SupportAdapter(this, arraylist);
       listView.setAdapter(adapter);
 
       et_support_num = (EditText) findViewById(R.id.et_support_num);
       et_support_num.addTextChangedListener(new TextWatcher() {
           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
           }
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           }
           @Override
           public void afterTextChanged(Editable s) {
               sure.setText("我要支持(" + s.toString() + ")");
           }
       });
       findViewById(R.id.btn_back).setOnClickListener(this);
       findViewById(R.id.btn_reduce).setOnClickListener(this);
       findViewById(R.id.btn_add).setOnClickListener(this);
       sure = (TextView) findViewById(R.id.btn_ensure_support);
       sure.setOnClickListener(this);
       findViewById(R.id.speak).setOnClickListener(this);
       findViewById(R.id.guanzhu).setOnClickListener(this);
       tljr_zListView_use = (LinearLayout) findViewById(R.id.tljr_zListView_use);
       newest_comment = (LayoutInflater.from(this).inflate( // 最新评论
               R.layout.tljr_hqss_news_comment_header_newest, null));
       newest_comment.setVisibility(View.GONE);
       tljr_zListView_use.addView(newest_comment);
       getSpeak();
 
       if (User.getUser() != null && userCorwd.getUser().getUuid().equals(User.getUser().getId())) {
           findViewById(R.id.focus).setVisibility(View.GONE);
       }
       ((TextView) findViewById(R.id.focust)).setText(userCorwd.isFocs() ? "已关注" : "关注");
       findViewById(R.id.focus).setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               if (User.getUser() == null) {
                   showToast("请先登录");
               } else {
                   focus((TextView) findViewById(R.id.focust));
               }
           }
       });
       if(getIntent().getIntExtra("tempposition",-1)!=-1){
    	   bean=MyApplication.getInstance().getMainActivity().zhiyanFragment
        		   .artzhongchou.list.get(getIntent().getIntExtra("tempposition",-1));
       }

       if(getIntent().getIntExtra("myposition",-1)!=-1){
           LogUtil.e("myview",getIntent().getIntExtra("myposition",-1)+"");
           bean=MyApplication.getInstance().getMainActivity().zhiyanFragment
                   .mMyView.listBean.get(getIntent().getIntExtra("myposition",-1));
       }else{
           LogUtil.e("myview2",getIntent().getIntExtra("myposition",-1)+"");
       }

   }
 
   private void focus(final TextView v) {
       if (userCorwd.getUser().getUuid().equals(User.getUser().getId())) {
           showToast("不能关注自己！");
           return;
       }
       String a = v.getText().toString();
       int type = a.contains("已") ? 0 : 1;
       ProgressDlgUtil.showProgressDlg("", this);
       NetUtil.sendPost(UrlUtil.URL_ZR + "user/focus", "otherUid=" + userCorwd.getUser().getUuid() + "&uid="
               + User.getUser().getId() + "&token=" + Configs.token + "&type=" + type, new NetResult() {
                   @Override
                   public void result(String msg) {
                       LogUtil.e("focus", msg);
                       ProgressDlgUtil.stopProgressDlg();
                       try {
                           JSONObject jsonObject = new JSONObject(msg);
                           if (jsonObject.getInt("status") == 1) {
                               showToast(jsonObject.getString("msg"));
                               String a = v.getText().toString();
                               int type = a.contains("已") ? 0 : 1;
                               if (type == 0) {
                                   userCorwd.setFocs(false);
                                   v.setText(" " + "关注");
                                  // updateArtifical(false); 	
                               } else {
                                   userCorwd.setFocs(true);
                                   v.setText(" " + "已关注");
                                   //updateArtifical(true);
                               }
                               
                           } else {
                               showToast("服务器连接失败！");
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                           showToast("服务器连接失败！");
                       }
                   }
       });
   }
 
   public void updateArtifical(boolean status){
	   MyApplication.getInstance().getMainActivity().zhiyanFragment.
  		artzhongchou.list.get(getIntent().getIntExtra("tempposition",-1))
  		.getUserCrowdList().get(0).setFocs(status);
	   
	   MyApplication.getInstance().getMainActivity().zhiyanFragment.
 		artzhongchou.adapter.notifyDataSetChanged();
	   
   }
   
   @Override
   public void onClick(View v) {
       switch (v.getId()) {
       case R.id.btn_back:
           finish();
           break;
       case R.id.speak:
           if (User.getUser() == null) {
               Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
           } else {
               new SpeakDialog(this, UrlUtil.URL_ZR + "crowd/remark",
                       "id=" + id + "&uid=" + User.getUser().getId() + "&token=" + Configs.token, new Complete() {
 
                           @Override
                           public void complete() {
                               remark++;
                               ((TextView) findViewById(R.id.speaknum)).setText(remark + "");
                               getSpeak();
                           }
                       }).show();
           }
           break;
       case R.id.guanzhu:
           if (User.getUser() == null) {
               Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
           } else {
               focus();
           }
           break;
       case R.id.btn_reduce:
           int num = Integer.parseInt(et_support_num.getText().toString());
           if (num > 10)
               num -= 10;
           et_support_num.setText(num + "");
           break;
       case R.id.btn_add:
           int num2 = Integer.parseInt(et_support_num.getText().toString());
           num2 += 10;
           et_support_num.setText(num2 + "");
           break;
       case R.id.btn_ensure_support:
           if (et_support_num.getText().toString().equals("")) {
               showToast("请输入支持的金额！");
               return;
           }
           if (MyApplication.getInstance().self != null) {
               float money = Float.parseFloat(et_support_num.getText().toString());
               tempmoney=money;
               startActivityForResult(new Intent(this, PayActivity.class).putExtra("money", money), 1);
           } else {
               login();
           }
           break;
       }
   }
 
   public static String buyId;
   private JSONObject shareObject;
 
   private void showShare() {
       NetUtil.sendGet(UrlUtil.URL_ZR + "crowd/getUserShareId",
               "id=" + buyId + "&uid=" + User.getUser().getId() + "&token=" + Configs.token, new NetResult() {
                   @Override
                   public void result(String arg0) {
                       try {
                           JSONObject object = new JSONObject(arg0);
                           if (object.getInt("status") == 1) {
                               JSONObject obj = object.getJSONObject("msg");
                               shareObject = obj;
                               new PromptDialog(ReSearchActivity.this, obj.optString("name") + "，是否立刻分享给好友？",
                                       new Complete() {
                                   @Override
                                   public void complete() {
                                       showPopupView();
                                   }
                               }).show();
                           } else {
                               showToast(object.getString("msg"));
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               });
   }
 
   private PopupWindow popupWindow;
 
   @SuppressLint("InflateParams")
   @SuppressWarnings("deprecation")
   private void showPopupView() {
       if (popupWindow == null) {
           // 一个自定义的布局，作为显示的内容
           View contentView = LayoutInflater.from(this).inflate(R.layout.tljr_dialog_share_news, null);
           contentView.findViewById(R.id.btn_cancle).setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   popupWindow.dismiss();
               }
           });
           LinearLayout v = (LinearLayout) contentView.findViewById(R.id.ly1);
           for (int i = 0; i < v.getChildCount(); i++) {
               final int m = i;
               v.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       share(m);
                       popupWindow.dismiss();
                   }
               });
           }
           popupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
           popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
           popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
           popupWindow.setBackgroundDrawable(new BitmapDrawable());
           popupWindow.setOutsideTouchable(true);
           popupWindow.setAnimationStyle(R.style.AnimationPreview);
           popupWindow.setOnDismissListener(new OnDismissListener() {
               @Override
               public void onDismiss() {
                   setAlpha(1f);
               }
           });
       }
       setAlpha(0.8f);
       int[] location = new int[2];
       View v = findViewById(R.id.bar_bottom);
       v.getLocationOnScreen(location);
       popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]);
   }// type 0:QQ 1微信 2新浪微博 3朋友圈
 
   private void setAlpha(float f) {
       WindowManager.LayoutParams lp = getWindow().getAttributes();
       lp.alpha = f;
       lp.dimAmount = f;
       getWindow().setAttributes(lp);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
   }
 
   private void share(int type) {
       ShareContent.actionUrl = shareObject.optString("shareUrl");
       ShareContent.content = shareObject.optString("desc");
       switch (type) {
       case 0:
           BwManager.getInstance().shareQQ();
           break;
       case 1:
           BwManager.getInstance().shareWeiXin();
           break;
       case 2:
           BwManager.getInstance().shareSina();
           break;
       case 3:
           BwManager.getInstance().shareWeiXinPyq();
           break;
       default:
           break;
       }
   }
 
   private void onpay(final String type, float quan, String couponId) {
//       int money = Integer.parseInt(et_support_num.getText().toString());
       String parms = "uid=" + MyApplication.getInstance().self.getId() + "&money=" +
    		   	et_support_num.getText().toString() + "&type=" + type
               + "&market=" + market + "&code=" + code + "&id=" + id + "&token=" + Configs.token;
       if (quan != 0)
           parms += ("&quan=" + quan);
       if (couponId != null)
           parms += ("&couponId=" + couponId);
       LogUtil.e("Test", UrlUtil.URL_ZR + "crowd/pay?" + parms);
       ProgressDlgUtil.showProgressDlg("", this);
       NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/pay", parms, new NetResult() {
           @Override
           public void result(final String msg) {
               LogUtil.e("Test", "onpay :" + msg);
               post(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           JSONObject object = new JSONObject(msg);
                           buyId = object.optString("buyId");
                           if (object.getInt("status") == 1) {
                               if (type.equals("0")) {
                                   paysuccess(object.getString("msg"));
                                   ProgressDlgUtil.stopProgressDlg();
                               } else if (type.equals("1")) {
                                   AliPay.getInstance().init(ReSearchActivity.this).pay(object.getString("msg"));
                               } else if (type.equals("3")) {
                                   LogUtil.e("Test", "in 3");
                                   PayReq req = new PayReq();
                                   // req.appId = "wxf8b4f85f3a794e77";
                                   // // 测试用appId
                                   JSONObject json = object.getJSONObject("msg");
                                   req.appId = json.getString("appid");
                                   req.partnerId = json.getString("partnerid");
                                   req.prepayId = json.getString("prepayid");
                                   req.nonceStr = json.getString("noncestr");
                                   req.timeStamp = json.getString("timestamp");
                                   req.packageValue = json.getString("package");
                                   req.sign = json.getString("sign");
                                   // req.extData = "app data"; //
                                   // optional
//                                   Toast.makeText(ReSearchActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                                   // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                   LogUtil.e("Test", "in 3");
                                   api.sendReq(req);
                               } else {
                                   Intent intent = new Intent(ReSearchActivity.this, WebActivity.class);
                                   intent.putExtra("url", object.getString("msg"));
                                   startActivity(intent);
                               }
                           } else {
                               showToast(object.getString("msg"));
                           }
                       } catch (JSONException e) {
                           ProgressDlgUtil.stopProgressDlg();
                       }
 
                   }
               });
           }
       });
   }
 
   private void getSpeak() {
       NetUtil.sendGet(UrlUtil.URL_ZR + "crowd/getRemakList", "id=" + id + "&page=1" + "&size=" + 30, new NetResult() {
           @Override
           public void result(String msg) {
               LogUtil.e("getSpeak", msg);
               ProgressDlgUtil.stopProgressDlg();
               try {
                   JSONObject jsonObject = new JSONObject(msg);
                   if (jsonObject.getInt("status") == 1) {
                       JSONArray array = jsonObject.getJSONArray("msg");
                       newest_comment.setVisibility(array.length() > 0 ? View.VISIBLE : View.GONE);
                       list.clear();
                       for (int i = 0; i < array.length(); i++) {
                           JSONObject object = array.getJSONObject(i);
                           Comment comment = new Comment();
                           comment.setId(object.getString("crowdId"));
                           comment.setTime(DateUtil.getDate(object.getLong("date") * 1000));
                           comment.setContent(object.getString("msg"));
                           JSONObject user = object.getJSONObject("user");
                           comment.setAurl(user.optString("avatar", "default"));
                           comment.setName(user.getString("nickName"));
                           comment.setUser_id(user.getString("uid"));
                           list.add(comment);
                       }
                       initSpeak();
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
                   newest_comment.setVisibility(View.GONE);
               }
           }
       });
   }
 
   private void initSpeak() {
       tljr_zListView_use.removeAllViews();
       tljr_zListView_use.addView(newest_comment);
       for (int i = 0; i < list.size(); i++) {
           View convertView = View.inflate(this, R.layout.tljr_item_news_comment, null);
 
           convertView.findViewById(R.id.tv_comment_delete).setVisibility(View.GONE);
 
           convertView.findViewById(R.id.tv_comment_vote).setVisibility(View.GONE);
 
           convertView.findViewById(R.id.tljr_ly_news_comment_praise).setVisibility(View.GONE);
           convertView.findViewById(R.id.tljr_btn_comment_praise).setVisibility(View.GONE);
           convertView.findViewById(R.id.tljr_tx_comment_praise_num).setVisibility(View.GONE);
 
           convertView.findViewById(R.id.tljr_ly_news_comment_cai).setVisibility(View.GONE);
           convertView.findViewById(R.id.tljr_btn_comment_cai).setVisibility(View.GONE);
           convertView.findViewById(R.id.tljr_tx_comment_cai_num).setVisibility(View.GONE);
 
           convertView.findViewById(R.id.blue_show_zan).setVisibility(View.GONE);
 
           convertView.findViewById(R.id.blue_show_cai).setVisibility(View.GONE);
 
           Comment cmt = list.get(i);
           ((TextView) convertView.findViewById(R.id.tljr_comment_name)).setText(cmt.getName());
           ((TextView) convertView.findViewById(R.id.tljr_comment_contents)).setText(cmt.getContent());
 
           /*
            * 头像
            */
           if (!cmt.getAurl().equals("default")) {
               StartActivity.imageLoader.displayImage(cmt.getAurl(),
                       (ImageView) convertView.findViewById(R.id.img_avatar), Options.getCircleListOptions());
           } else {
               StartActivity.imageLoader.displayImage(default_avatar,
                       (ImageView) convertView.findViewById(R.id.img_avatar), Options.getCircleListOptions());
           }
           ((TextView) convertView.findViewById(R.id.tljr_comment_time)).setText(cmt.getTime());
           tljr_zListView_use.addView(convertView);
       }
   }
 
   public String default_avatar = "drawable://" + R.drawable.img_avatar;
 
   private void focus() {
       int type = isfocus ? 0 : 1;
       ProgressDlgUtil.showProgressDlg("", this);
       NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/focus",
               "id=" + id + "&uid=" + User.getUser().getId() + "&token=" + Configs.token + "&type=" + type,
               new NetResult() {
                   @Override
                   public void result(String msg) {
                       LogUtil.e("focus", msg);
                       ProgressDlgUtil.stopProgressDlg();
                       try {
                           JSONObject jsonObject = new JSONObject(msg);
                           if (jsonObject.getInt("status") == 1) {
                               int type = isfocus ? 0 : 1;
                               if (type == 0) {
                                   focus--;
                                   bean.setFocus(bean.getFocus()-1);
                                   bean.setIsfocus(false);
                                   quxiaoguanzhu();
                               }else{
                                   focus++;
                                   bean.setFocus(bean.getFocus()+1);
                                   bean.setIsfocus(true);
                                   guanzhu();
                               }
                               ((TextView) findViewById(R.id.guanzhunum)).setText(focus+"");
                               isfocus = !isfocus;
                           }
                           showToast(jsonObject.getString("msg"));
                       } catch (JSONException e) {
                           e.printStackTrace();
                           showToast("服务器连接失败！");
                       }
                   }
               });
   }
 
   public void quxiaoguanzhu(){
	   List<ZhongchouBean> listbean=MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean;
		String key=bean.getMarket()+bean.getCode();
		for(int i=0;i<listbean.size();i++){
			if(key.equals(listbean.get(i).getMarket()+listbean.get(i).getCode())){
				listbean.remove(i);
				break;
			}
		}
		MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
		MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum=
				MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum-1;
		MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton.setText(
				"我的("+(MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)+")");
   }
   
   public void guanzhu(){
	    bean.setFinishstatus(true);
		for(int i=0;i<MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.size();i++){
			if(MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.get(i).getStatus()==0){
				MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.get(i).setFinishstatus(false);
			}
		}
		MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.add(0,bean);
		MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
		MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum=
				MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum+1;
		MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton.setText(
				"我的("+(MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)+")");
   }
   
   @Override
   protected void onResume() {
       super.onResume();
       ProgressDlgUtil.stopProgressDlg();
   }
 
   @Override
   protected void onDestroy() {
       super.onDestroy();
       if (hasChange)
           MyApplication.getInstance().getMainActivity().zhiyanFragment.artzhongchou.initData();
   }
 
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == 1 && resultCode == 1) {
           String type = data.getStringExtra("type");
           float quan = data.getFloatExtra("quan", 0);
           String couponId = data.getStringExtra("couponId");
           LogUtil.e("Test", "type :" + type);
           LogUtil.e("Test", "quan :" + quan);
           LogUtil.e("Test", "couponId :" + couponId);
           onpay(type, quan, couponId);
           //sendBroadcast(new Intent("com.tuling.OneguTuLingZhiYan"));
       }
   }
   
   @Override
   public void payerror(String arg0) {
       showToast(arg0);
   }
 
   @Override
   public void paysuccess(String arg0) {
       hasChange = true;
       showToast(arg0);
       showShare();
       UpdateMyData();
       UpdateMyView();
   }
 
   public void UpdateMyView(){
	  boolean status=true; 
	  List<ZhongchouBean> listBean= MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean;
	  for(int y=0;y<listBean.size();y++){
		  if(listBean.get(y).getCode().equals(bean.getCode())){
			  status=false;
			  break;
		  }
	  } 
	  if(status){
		  bean.setFinishstatus(true);
			for(int i=0;i<MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.size();i++){
				if(MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.get(i).getStatus()==0){
					MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.get(i).setFinishstatus(false);
				}
			}
			MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean.add(0,bean);
			MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
			MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum=
					MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum+1;
			MyApplication.getInstance().getMainActivity().zhiyanFragment.myRadioButton.setText(
					"我的("+(MyApplication.getInstance().getMainActivity().zhiyanFragment.myNum)+")");
	  }
	  
   }
   
   public void UpdateMyData(){
	   List<ZhongchouBean> UpdateList=MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean;
	   String key=market+code;
	   for(int i=0;i<UpdateList.size();i++){
		   if(key.equals(UpdateList.get(i).getMarket()+UpdateList.get(i).getCode())){
			   UpdateList.get(i).setHasMoney(UpdateList.get(i).getHasMoney()+(int)(tempmoney*100));
		   }
	   }
	   if(MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter!=null){
		   MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.adapter.notifyDataSetChanged();
	   }
	   persen=persen+(int)tempmoney;
       ((ProgressBar) findViewById(R.id.seekbar)).setProgress(persen);
       ((TextView) findViewById(R.id.tv_num_progress)).setText(persen + "/100");
       UpdateZhiChiZhe();
   }
   
   public void UpdateZhiChiZhe(){
	   NetUtil.sendGet(UrlUtil.URL_ZR + "crowd/getCrowListByCode?","uid="+MyApplication.getInstance().self.getId()+"&code="+code,
		new NetResult() {
           @Override
           public void result(String msg) {
        	  if(ZhiYanParseJson.ParseZhiYanJsonZhichizhe(arraylist,msg)){
        		  adapter.notifyDataSetChanged();
        	  }
        	  UserCrowd tempUserCrowd=ZhiyanArtZhongchouAdapter.userCrowdList.get(0);
        	  ZhiyanArtZhongchouAdapter.userCrowdList.clear();
        	  ZhiyanArtZhongchouAdapter.userCrowdList.add(0,tempUserCrowd);
        	  ZhiyanArtZhongchouAdapter.userCrowdList.addAll(arraylist);
           
           }}
      );
   }
   
   
}

