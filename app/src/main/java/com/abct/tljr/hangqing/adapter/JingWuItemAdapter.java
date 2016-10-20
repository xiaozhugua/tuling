package com.abct.tljr.hangqing.adapter;
 
import java.util.List;
 
import com.abct.tljr.R;
import com.abct.tljr.hangqing.model.JingWuItemModel;
 
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
 
public class JingWuItemAdapter extends ArrayAdapter<JingWuItemModel>{
 
   private int Resoured;
   private Context context;
//	private TextView date;
   
   
   public JingWuItemAdapter(Context context,int resource,List<JingWuItemModel> objects) {
       super(context, resource, objects);
       this.Resoured=resource;
       this.context=context;
   }
 
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
       final int pos=position;
       View view=null;
       final ViewHolder viewHolder;
       JingWuItemModel model=getItem(position);
       if(convertView==null){
           viewHolder=new ViewHolder();
           view=LayoutInflater.from(getContext()).inflate(this.Resoured,null);
           viewHolder.time=(TextView)view.findViewById(R.id.tljr_rankview_time_item);
           view.setTag(viewHolder);
       }else{
           view=convertView;
           viewHolder=(ViewHolder)view.getTag();
       }
       viewHolder.time.setText(""+model.getTuijian()+" 推荐");
       view.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent("com.example.jingwuchange");
               intent.putExtra("position",pos);
               viewHolder.time.setTextColor(getContext().getResources().getColor(R.color.gary_dan));
               context.sendBroadcast(intent);
           }
       });
       return view;
   }
   
   class ViewHolder{
       public TextView time;
       public GridView mGridView;
   }
   
}