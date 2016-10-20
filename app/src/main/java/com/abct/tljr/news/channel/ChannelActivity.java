package com.abct.tljr.news.channel;
 
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;    
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.news.HuanQiuShiShi;
import com.abct.tljr.news.channel.adapter.ChannelListAdapter;
import com.abct.tljr.news.channel.adapter.DragAdapter;
import com.abct.tljr.news.channel.bean.ChannelItem;
import com.abct.tljr.news.channel.view.ChannelListView;
import com.abct.tljr.news.channel.view.DragGrid;
import com.qh.common.listener.NetResult;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
 
/**
 * 频道管理
 * 
 */
public class ChannelActivity extends BaseActivity implements OnItemClickListener {
 
   public final String Tag = "ChannelActivity";
 
   /** 用户栏目的GRIDVIEW */
   private DragGrid userGridView;
   /** 其它栏目的GRIDVIEW */
   // private OtherGridView otherGridView;
   /** 用户栏目对应的适配器，可以拖动 */
   public static DragAdapter userAdapter;
   /** 其它栏目对应的适配器 */
   // public static OtherAdapter otherAdapter;
 
   /** 用户栏目列表 */
   public static ArrayList<ChannelItem> userChannelList;
   /** 其它栏目列表 */
   public static ArrayList<ChannelItem> otherChannelList;
 
   /** 重要频道列表 */
   ArrayList<ChannelItem> impNewsChannelList = new ArrayList<ChannelItem>();
   /** 推荐（普通）频道列表 */
   ArrayList<ChannelItem> comNewsChannelList = new ArrayList<ChannelItem>();
   /** 其他频道列表 */
   ArrayList<ChannelItem> otherNewsChannelList = new ArrayList<ChannelItem>();
 
   RadioGroup radioGroup;
 
   /** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
   boolean isMove = false;
 
   ChannelListAdapter channelListAdapter;
   ChannelListView channelListView;
 
   TextView channel_edit;
 
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.tljr_activity_news_channel);
       initView();
       initData();
   }
 
   /** 初始化数据 */
   private void initData() {
       userChannelList = new ArrayList<ChannelItem>();
       otherChannelList = new ArrayList<ChannelItem>();
       Realm myRealm = Realm.getDefaultInstance();
       ;
       RealmResults<ChannelItem> results = myRealm.where(ChannelItem.class).equalTo("selected", 1).findAll();
       results.sort("orderId");
       for (ChannelItem item : results) {
           userChannelList.add(item);
       }
       RealmResults<ChannelItem> results2 = myRealm.where(ChannelItem.class).equalTo("selected", 0).findAll();
       for (ChannelItem item : results2) {
           otherChannelList.add(item);
       }
 
       for (ChannelItem cItem : otherChannelList) {
 
           if (cItem.getChannelType() == 0) {
               impNewsChannelList.add(cItem);
           } else if (cItem.getChannelType() == 1) {
               comNewsChannelList.add(cItem);
           } else if (cItem.getChannelType() == 2 || cItem.getChannelType() == 3) {
               otherNewsChannelList.add(cItem);
           }
       }
 
       channelListAdapter = new ChannelListAdapter(this, comNewsChannelList); // 一开始首选为推荐频道，所以加载推荐频道的数据
       channelListView.setAdapter(channelListAdapter);
 
       userAdapter = new DragAdapter(this, userGridView);
       userGridView.setAdapter(userAdapter);
       // otherAdapter = new OtherAdapter(this, otherChannelList);
       // otherGridView.setAdapter(this.otherAdapter);
       // 设置GRIDVIEW的ITEM的点击监听
       // otherGridView.setOnItemClickListener(this);
       userGridView.setOnItemClickListener(this);
 
       radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
 
           @Override
           public void onCheckedChanged(RadioGroup arg0, int checkId) {
               switch (checkId) {
               case R.id.tljr_hqss_channel_rg_0:
                   channelListAdapter.setChannelList(comNewsChannelList);
                   break;
               case R.id.tljr_hqss_channel_rg_1:
                   channelListAdapter.setChannelList(impNewsChannelList);
                   break;
               case R.id.tljr_hqss_channel_rg_2:
                   channelListAdapter.setChannelList(otherNewsChannelList);
                   break;
               default:
                   break;
               }
 
           }
       });
 
   }
 
   /** 初始化布局 */
   private void initView() {
       userGridView = (DragGrid) findViewById(R.id.userGridView);
       // otherGridView = (OtherGridView) findViewById(R.id.otherGridView);
       radioGroup = (RadioGroup) findViewById(R.id.tljr_hqss_channel_rg);
 
       channelListView = (ChannelListView) findViewById(R.id.channelListView);
 
       channel_edit = (TextView) findViewById(R.id.channel_edit);
       userGridView.setEditText(channel_edit);
       channel_edit.setOnClickListener(new OnClickListener() {
 
           @Override
           public void onClick(View v) {
               userGridView.setEditingMode(!userGridView.isEditingMode());
               channel_edit.setText(userGridView.isEditingMode() ? "完成" : "编辑");
               userAdapter.notifyDataSetChanged();
 
           }
       });
   }
 
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.main, menu);
       return true;
   }
 
   public void btnConfirm(View view) {
       save_upload();
   }
 
   private void save_upload() {
       this.setResult(HuanQiuShiShi.RESULT_CODE_TRUE);
 
       if (userChannelList.size() <= 0) {
           Toast.makeText(this, "至少保留一个频道", Toast.LENGTH_LONG).show();
           return;
       }
 
       saveChannel();
       finish();
       overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
 
       if (Constant.netType.equals("未知") || !HuanQiuShiShi.channel_change) {
           return;
       }
 
       /*
        * 打包用户频道排序习惯上传到服务端
        */
       try {
           JSONArray array = new JSONArray();
           for (int i = 0; i < userChannelList.size(); i++) {
               ChannelItem item = userChannelList.get(i);
               JSONObject objjj = new JSONObject();
               objjj.put("channelType", item.getChannelType());
               if (item.getContentPictureUrl() != null) {
                   objjj.put("contentPictureUrl", item.getContentPictureUrl());
               }
               objjj.put("name", item.getName());
               objjj.put("orderId", item.getOrderId());
               objjj.put("species", item.getSpecies());
               array.put(objjj);
           }
 
           JSONArray array2 = new JSONArray();
           for (int i = 0; i < otherChannelList.size(); i++) {
               ChannelItem item = otherChannelList.get(i);
               JSONObject objjj2 = new JSONObject();
               objjj2.put("channelType", item.getChannelType());
               if (item.getContentPictureUrl() != null) {
                   objjj2.put("contentPictureUrl", item.getContentPictureUrl());
               }
               objjj2.put("name", item.getName());
               objjj2.put("orderId", item.getOrderId());
               objjj2.put("species", item.getSpecies());
               array2.put(objjj2);
           }
 
           JSONObject js = new JSONObject();
           js.put("selected", array.toString());
           LogUtil.i(Tag, "selected :" + array.toString());
           js.put("inSelected", array2.toString());
           if (MyApplication.getInstance().self != null) {
               String url = UrlUtil.URL_new + "api/utc/update";
               String params = "platform=" + HuanQiuShiShi.platform + "&uid=" + MyApplication.getInstance().self.getId() + "&data=" + js.toString()
                       + "&version=" + HuanQiuShiShi.version;
               LogUtil.i(Tag, js.toString());
               NetUtil.sendPost(url, params, new NetResult() {
                   @Override
                   public void result(String msg) {
                       LogUtil.i(Tag, "msg :" + msg);
                   }
               });
           }
       } catch (JSONException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
   }
 
   public void btnCancel(View view) {
       this.setResult(HuanQiuShiShi.RESULT_CODE_FALSE);
       finish();
       // MainActivity.huanQiuShiShi.adapter.notifyDataSetChanged();
       overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_right);
   }
 
   /** GRIDVIEW对应的ITEM点击监听接口 */
   @Override
   public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
       // 如果点击的时候，之前动画还没结束，那么就让点击事件无效
 
       if (userGridView.isEditingMode()) {
           // if (isMove) {
           // return;
           // }
           switch (parent.getId()) {
           case R.id.userGridView: {
               HuanQiuShiShi.channel_change = true;
               // position为 0，1 的不可以进行任何操作
               // if (position != 0 && position != 1) {
               final ImageView moveImageView = getView(view);
               if (moveImageView != null) {
                   TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                   final int[] startLocation = new int[2];
                   newTextView.getLocationInWindow(startLocation);
                   final ChannelItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);// 获取点击的频道内容
                   // otherAdapter.setVisible(false);
                   // 添加到最后一个
 
                   postDelayed(new Runnable() {
                       public void run() {
                           try {
                               int[] endLocation = new int[2];
                               // 获取终点的坐标
                               // otherAdapter.addItem(channel);
                               // otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                               userAdapter.setRemove(position);
                               MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                               if (channel.getChannelType() == 0) {
                                   impNewsChannelList.add(channel);
                               } else if (channel.getChannelType() == 1) {
                                   comNewsChannelList.add(channel);
                               } else if (channel.getChannelType() == 2 || channel.getChannelType() == 3) {
                                   otherNewsChannelList.add(channel);
                               }
 
                               channelListAdapter.notifyDataSetChanged();
                           } catch (Exception localException) {
                           }
                       }
                   }, 50L);
               }
           }
               break;
 
           default:
               break;
           }
       } else {
           HuanQiuShiShi.turntoPage = position;
           save_upload();
 
       }
 
   }
 
   /**
    * 点击ITEM移动动画
    * 
    * @param moveView
    * @param startLocation
    * @param endLocation
    * @param moveChannel
    * @param clickGridView
    */
   private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final ChannelItem moveChannel, final GridView clickGridView) {
       int[] initLocation = new int[2];
       // 获取传递过来的VIEW的坐标
       moveView.getLocationInWindow(initLocation);
       // 得到要移动的VIEW,并放入对应的容器中
       final ViewGroup moveViewGroup = getMoveViewGroup();
       final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
       // 创建移动动画
 
       moveViewGroup.removeView(mMoveView);
       // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
       if (clickGridView instanceof DragGrid) {
           // otherAdapter.setVisible(true);
           // otherAdapter.notifyDataSetChanged();
           userAdapter.remove();
       } else {
           userAdapter.setVisible(true);
           userAdapter.notifyDataSetChanged();
           // otherAdapter.remove();
       }
 
   }
 
   /**
    * 获取移动的VIEW，放入对应ViewGroup布局容器
    * 
    * @param viewGroup
    * @param view
    * @param initLocation
    * @return
    */
   private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
       int x = initLocation[0];
       int y = initLocation[1];
       viewGroup.addView(view);
       LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
       mLayoutParams.leftMargin = x;
       mLayoutParams.topMargin = y;
       view.setLayoutParams(mLayoutParams);
       return view;
   }
 
   /**
    * 创建移动的ITEM对应的ViewGroup布局容器
    */
   private ViewGroup getMoveViewGroup() {
       ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
       LinearLayout moveLinearLayout = new LinearLayout(this);
       moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
       moveViewGroup.addView(moveLinearLayout);
       return moveLinearLayout;
   }
 
   /**
    * 获取点击的Item的对应View，
    * 
    * @param view
    * @return
    */
   private ImageView getView(View view) {
       view.destroyDrawingCache();
       view.setDrawingCacheEnabled(true);
       Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
       view.setDrawingCacheEnabled(false);
       ImageView iv = new ImageView(this);
       iv.setImageBitmap(cache);
       return iv;
   }
 
   /** 退出时候保存选择后数据库的设置 */
   private void saveChannel() {
 
       otherChannelList = new ArrayList<ChannelItem>();
       for (ChannelItem item : impNewsChannelList) {
           otherChannelList.add(item);
       }
       for (ChannelItem item : comNewsChannelList) {
           otherChannelList.add(item);
       }
       for (ChannelItem item : otherNewsChannelList) {
           otherChannelList.add(item);
       }
 
       ArrayList<ChannelItem> item1 = new ArrayList<ChannelItem>(userChannelList);
       ArrayList<ChannelItem> item2 = new ArrayList<ChannelItem>(otherChannelList);
 
       saveUserChannel(item1, item2);
 
   }
 
   @Override
   public void onBackPressed() {
       super.onBackPressed();
       this.setResult(HuanQiuShiShi.RESULT_CODE_FALSE);
       // MainActivity.huanQiuShiShi.adapter.notifyDataSetChanged();
       overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
 
   }
 
   /**
    * 保存用户频道到数据库
    * 
    * @param userList
    */
   public void saveUserChannel(List<ChannelItem> userList, List<ChannelItem> otherList) {
 
       Realm myRealm = Realm.getDefaultInstance();
       myRealm.beginTransaction();
       for (int i = 0; i < userList.size(); i++) {
           ChannelItem channelItem = userList.get(i);
           channelItem.setOrderId(i);
           channelItem.setSelected(Integer.valueOf(1));
           myRealm.copyToRealm(channelItem);
       }
 
       for (int i = 0; i < otherList.size(); i++) {
           ChannelItem channelItem = otherList.get(i);
           channelItem.setOrderId(i);
           channelItem.setSelected(Integer.valueOf(0));
           myRealm.copyToRealm(channelItem);
       }
       myRealm.commitTransaction();
       myRealm.close();
   }
 
}