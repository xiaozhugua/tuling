package com.abct.tljr.hangqing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.data.ZiXuanUtil;
import com.abct.tljr.dialog.SearchDialog;
import com.abct.tljr.dialog.ShareDialog;
import com.abct.tljr.hangqing.model.ZuHeModel;
import com.abct.tljr.hangqing.tiaocang.HuiCeView;
import com.abct.tljr.hangqing.tiaocang.TljrTiaoCangView;
import com.abct.tljr.hangqing.util.BitmapCache;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.hangqing.zuhe.TljrZuHe;
import com.abct.tljr.main.MainActivity;
import com.abct.tljr.news.widget.BlurUtils;
import com.qh.common.volley.toolbox.ImageLoader;
import com.qh.common.volley.toolbox.NetworkImageView;
import com.qh.common.volley.toolbox.Volley;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/2/15.
 */

public class TljrZuheAdapter extends ArrayAdapter<ZuHeModel> {

    private int ResourceId;
    private ImageLoader imageLoader;
    private PopupWindow popupWindow;
    private Bitmap scaled;
    private View MenuView;
    
    public TljrZuheAdapter(Context context, int resource, List<ZuHeModel> objects) {
        super(context, resource, objects);
        imageLoader = new ImageLoader(Volley.newRequestQueue(context), new BitmapCache());
        this.ResourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ZuHeModel model=getItem(position);
        final View view;
        ZuHeViewHolder viewHolder=null;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(this.ResourceId,null);
            viewHolder=new ZuHeViewHolder();
            viewHolder.status=(ImageView)view.findViewById(R.id.zuhe_status);
            viewHolder.zongshouyi_int=(TextView)view.findViewById(R.id.zong_int);
            viewHolder.zongshouyi_float=(TextView)view.findViewById(R.id.zong_float);
            viewHolder.rishouyi_int=(TextView)view.findViewById(R.id.ri_int);
            viewHolder.rishouyi_float=(TextView)view.findViewById(R.id.ri_float);
            viewHolder.yueshouyi_int=(TextView)view.findViewById(R.id.yue_int);
            viewHolder.yueshouyi_float=(TextView)view.findViewById(R.id.yue_float);
            viewHolder.zhoushitu=(NetworkImageView)view.findViewById(R.id.zuhe_image);
            viewHolder.name=(TextView)view.findViewById(R.id.zh_name);
            viewHolder.menu=(ImageView)view.findViewById(R.id.zuhe_menu);
            viewHolder.mRelativeLayout=(RelativeLayout)view.findViewById(R.id.page_item_relative);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ZuHeViewHolder)view.getTag();
        }
        viewHolder.zongshouyi_int.setText(model.getZong_int()+"");
        viewHolder.zongshouyi_float.setText(model.getZong_float()+"");
        viewHolder.rishouyi_int.setText(model.getRi_int()+"");
        viewHolder.rishouyi_float.setText(model.getRi_float()+"");
        viewHolder.yueshouyi_int.setText(model.getYue_int()+"");
        viewHolder.yueshouyi_float.setText(model.getYue_float()+"");
        viewHolder.name.setText(model.getName());
        if(model.getZong()>0){
        	viewHolder.mRelativeLayout.setBackgroundResource(R.drawable.img_shangzhangzuhe);
        	viewHolder.status.setBackgroundResource(R.drawable.img_taiyang);
        }else{
        	viewHolder.mRelativeLayout.setBackgroundResource(R.drawable.img_xiadiezuhe);
        	viewHolder.status.setBackgroundResource(R.drawable.img_yun);
        }
        viewHolder.zhoushitu.setImageUrl(model.getImgUrl(),imageLoader);
        view.setTag(R.id.tiaocang_into_info,model);
        
        viewHolder.menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopupWindow(view,model.getId(),model.getName());
			}
		});
        return view;
    }

    public class ZuHeViewHolder{
    	public RelativeLayout mRelativeLayout;
        public ImageView status;
        public ImageView menu;
        public TextView zongshouyi_int;
        public TextView zongshouyi_float;
        public TextView rishouyi_int;
        public TextView rishouyi_float;
        public TextView yueshouyi_int;
        public TextView yueshouyi_float;
        public NetworkImageView zhoushitu;
        public TextView name;
    }

    private ImageView image=null;
    private Context context=null;
    private String zuid=null;
    private String zuName=null;
    
    //组合菜单
    @SuppressLint("InflateParams")
    public void showPopupWindow(final View view, final String zuids,final String zuNames) {
    	this.zuid=zuids;
    	this.zuName=zuNames;
    	this.MenuView=view;
    	// 一个自定义的布局，作为显示的内容
    	ImageView image=(ImageView)view.findViewById(R.id.zuhe_menu);
    	try{
    		Thread.sleep(300);
    	}catch(Exception e){
    		
    	}
    	image.setImageResource(R.drawable.img_quxiao2);
        if (popupWindow == null)
            initPopupWindow(image,getContext(),zuid,zuName);
        
        final View contentView = popupWindow.getContentView();
        TableLayout menu = (TableLayout) contentView.findViewById(R.id.tljr_menu_select);
        for (int i = 0; i < menu.getChildCount(); i++){
            menu.getChildAt(i).startAnimation(menu.getLayoutAnimation().getAnimation());
        }
        RelativeLayout header = (RelativeLayout)contentView.findViewById(R.id.tljr_hq_zh_menu_header);
        header.startAnimation(header.getLayoutAnimation().getAnimation());
        int[] location = new int[2];
        view.findViewById(R.id.zuhe_menu).getLocationOnScreen(location);
        ((ImageView) (contentView.findViewById(R.id.tljr_zh_contentimage))).setImageBitmap(getViewImage(view));
        TljrZuHe.zuheAdapter.notifyDataSetChanged();
        popupWindow.showAtLocation(view.findViewById(R.id.zuhe_menu),Gravity.BOTTOM, 0, 0);
    }

    @SuppressLint("InflateParams")
    public void initPopupWindow(final ImageView images,final Context contexts,final String zuids,final String zuNames) {
        
    	this.image=images;
    	this.context=contexts;
    	
    	View contentView = LayoutInflater.from(context).inflate(R.layout.tljr_hq_zh_menu, null);
        (contentView.findViewById(R.id.tljr_grp_zxfz_info))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	popupWindow.dismiss();
                    	Intent huiceintent = new Intent(context, HuiCeView.class);
            			huiceintent.putExtra("zhid",zuid);
            			huiceintent.putExtra("zuname",zuName);
            			context.startActivity(huiceintent);
            			closeIcon(image);
                    }
                });
        (contentView.findViewById(R.id.tljr_grp_zxfz_tianjia))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	popupWindow.dismiss();
                    	new SearchDialog(context).show(zuName);
                    	closeIcon(image);
                    }
                });
        (contentView.findViewById(R.id.tljr_grp_zxfz_gaiming))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	popupWindow.dismiss();
                    	GuDealImpl.showGaimingAndBeizu(context,
                    			"分组改名","最多10个中文字符",1,zuName,null);
                    	closeIcon(image);
                    }
                });
        (contentView.findViewById(R.id.tljr_grp_zxfz_shanchu))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	popupWindow.dismiss();
                    	GuDealImpl.RemoveFenZu(context,zuName,zuid);
                    	closeIcon(image);
                    }
                });
        (contentView.findViewById(R.id.tljr_grp_zxfz_fx))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	popupWindow.dismiss();
                        showFenXiang(ZiXuanUtil.fzMap.get(zuName));
                        closeIcon(image);
                    }
                });
        (contentView.findViewById(R.id.tljr_grp_zxfz_percent))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	popupWindow.dismiss();
                    	Intent intent = new Intent(context,TljrTiaoCangView.class);
            			intent.putExtra("zulist", (Serializable)ZiXuanUtil.fzMap.get(zuName).getList());
            			intent.putExtra("zuid",zuid);
            			intent.putExtra("zuName",zuName);
            			intent.putExtra("action",2);
            			context.startActivity(intent);
            			closeIcon(image);
                    }
                });
        ((RelativeLayout) (contentView.findViewById(R.id.tljr_hq_zh_menu_header)))
                .setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    	popupWindow.dismiss();
                    	closeIcon(image);
                    }
                });
        popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.popup_anim_style);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
    }
    
    public Bitmap getViewImage(View view) {
        return BlurUtils.drawViewToBitmap(scaled, view,view.getMeasuredWidth(),view.getMeasuredHeight(), 1, null);
    }
 
    public void showFenXiang(Object object) {
        if (MyApplication.getInstance().self == null) {
        	MyApplication.getInstance().getMainActivity().login();
            return;
        }
        new ShareDialog(MyApplication.getInstance().getMainActivity(),object).show();
    }
    
    
    public void closeIcon(ImageView image){
    	((ImageView)MenuView.findViewById(R.id.zuhe_menu)).setImageResource(R.drawable.img_hqss_tianjia);
    }
    
}
