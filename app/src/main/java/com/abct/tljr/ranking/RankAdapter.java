package com.abct.tljr.ranking;
 
import java.util.ArrayList;
 
import com.abct.tljr.R;
import com.abct.tljr.chart.ImageOptions;
import com.abct.tljr.data.InitData;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.ShapeTextView;
import com.abct.tljr.utils.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
 
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import io.realm.Realm;
 
/**
 * Created by Administrator on 2016/1/31.
 */
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {
 
   private Context context;
   private ArrayList<OnePerson> list;
   private int layout;
   private String name;
   private int percent;
 
   public RankAdapter(Context context, ArrayList<OnePerson> list, int layout, String name, int percent) {
       this.context = context;
       this.list = list;
       this.layout = layout;
       this.name = name;
       this.percent = percent;
   }
 
   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       ViewHolder holder;
       if (layout == 1) {
           holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tljr_item_rankother, parent, false));
       } else {
           holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tljr_item_rank, parent, false));
       }
       return holder;
   }
 
   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
       final OnePerson person = list.get(position);
       holder.itemView.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               // TODO Auto-generated method stub
               context.startActivity(new Intent(context, OneRankInfoActivity.class).putExtra("uid", person.getUid())
                       .putExtra("zhid", person.getZhid()).putExtra("name", person.getName())
                       .putExtra("zhname", person.getZhname()));
           }
       });
       holder.zhname.setText(person.getZhname());
       holder.name.setText(person.getName());
       if (layout == 1) {
           holder.number.setText(Util.getDateOnlyDay(person.getTime()) + "创建");
           holder.typeS.setText(name.replace("最大", ""));
           if (percent == 0) {
               holder.valuep.setVisibility(View.GONE);
               holder.value.setText(Util.df.format(person.getValue()));
           } else {
               holder.value.setText(Util.df.format(person.getValue() * 100));
           }
       } else {
           if (percent == 0) {
               holder.value.setText(Util.df.format(person.getValue()));
           } else {
               holder.value.setText(Util.df.format(person.getValue() * 100) + "%");
           }
           holder.typeS.setText(name);
           // Util.setRoundImage(person.getAvatar(), holder.avatar,
           // handler);
           StartActivity.imageLoader.displayImage(person.getAvatar(), holder.avatar, Options.getCircleListOptions());
           if (person.getNumber() > 3) {
               holder.number.setVisibility(View.VISIBLE);
               holder.number.setText(person.getNumber() + "");
               holder.num.setVisibility(View.INVISIBLE);
           } else {
               holder.number.setVisibility(View.INVISIBLE);
               holder.num.setVisibility(View.VISIBLE);
               switch (person.getNumber()) {
               case 1:
                   holder.num.setBackgroundResource(R.drawable.img_diyiming);
                   break;
               case 2:
                   holder.num.setBackgroundResource(R.drawable.img_dierming);
                   break;
               case 3:
                   holder.num.setBackgroundResource(R.drawable.img_disanming);
                   break;
               default:
                   break;
               }
           }
       }
 
   }
 
   @Override
   public int getItemCount() {
       return list.size();
   }
 
   public final class ViewHolder extends RecyclerView.ViewHolder {
       TextView number;
       ImageView num;
       ImageView avatar;
       TextView name;
       TextView zhname;
       TextView typeS;
       TextView value;
       TextView valuep;
 
       public ViewHolder(View v) {
           super(v);
           if (layout == 1) {
               number = (TextView) v.findViewById(R.id.tljr_item_rank_time);
               valuep = (TextView) v.findViewById(R.id.tljr_item_rank_valuea);
           } else {
               number = (TextView) v.findViewById(R.id.tljr_txt_item_rank_number);
               num = (ImageView) v.findViewById(R.id.tljr_img_item_rank_number);
               avatar = (ImageView) v.findViewById(R.id.tljr_item_rank_avatar);
           }
           zhname = (TextView) v.findViewById(R.id.tljr_item_rank_zhname);
           name = (TextView) v.findViewById(R.id.tljr_item_rank_name);
           value = (TextView) v.findViewById(R.id.tljr_item_rank_value);
           typeS = (TextView) v.findViewById(R.id.tljr_item_rank_type);
       }
   }
}