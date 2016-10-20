package com.abct.tljr.ui.yousuan.adapter;

import java.text.DecimalFormat;
import java.util.List;

import org.json.JSONObject;

import com.abct.tljr.R;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.activity.zhiyan.PayActivity;
import com.abct.tljr.ui.yousuan.activity.YouSuanItem;
import com.abct.tljr.ui.yousuan.model.YouSuanItemModel;
import com.abct.tljr.ui.yousuan.util.YouSuanShiShiChart;
import com.github.mikephil.charting.charts.LineChart;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class YouSuanItemAdapter extends RecyclerView.Adapter<YousuanViewHolder> {

	private int ResourceId;
	private DecimalFormat mDecimalFormat=null;
	private List<YouSuanItemModel> listModel=null;
	private Context context=null;
	public ZhongchouBean paybean=null;
	public String ShiyongUrl=UrlUtil.Url_235+"8080/yousuan/rest/gentou/test";

	public YouSuanItemAdapter(Context context, int resource,List<YouSuanItemModel> objects) {
		this.ResourceId = resource;
		mDecimalFormat=new DecimalFormat("#0.00");
		this.listModel=objects;
		this.context=context;
	}

	@Override
	public int getItemCount() {
		return listModel.size();
	}

	@Override
	public void onBindViewHolder(final YousuanViewHolder viewHolder, final int position) {
		final YouSuanItemModel model=listModel.get(position);
		
		viewHolder.itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,YouSuanItem.class);
                intent.putExtra("position",position);
				context.startActivity(intent);
			}
		});

		viewHolder.name.setText(dealName(model.getName()));
		viewHolder.nianhuibao.setText(mDecimalFormat.format(model.getAnnualRate()*100)+"%");
		viewHolder.zuidahuice.setText(mDecimalFormat.format(model.getMaxDrawDown()*100)+"%");
		viewHolder.huicetianshu.setText(model.getMaxDrawDownPeriod()+"");
		viewHolder.xiapu.setText(mDecimalFormat.format(model.getSharpRate())+"");
		viewHolder.touzi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(viewHolder.money.getText().toString().equals("")){
					Toast.makeText(context,"请输入金额",Toast.LENGTH_SHORT).show();
					return ;
				}
				if(Integer.valueOf(viewHolder.money.getText().toString())<10){
					Toast.makeText(context,"跟投金额不能少于10块",Toast.LENGTH_SHORT).show();
					return ;
				}
				final float money=Float.valueOf(viewHolder.money.getText().toString()+"");
				if (User.getUser() == null) {
					Toast.makeText(context, "请先登录！", Toast.LENGTH_SHORT).show();
					return;
				}
				new PromptDialog(context,"购买需要支付"+money+"元,是否支付？",
					new Complete() {
					@Override
					public void complete() {
						paybean = new ZhongchouBean();
						paybean.setRewardMoney(new Float(money*100).intValue());
						paybean.setId(model.getId()+"");
						((Activity) context).startActivityForResult(new Intent(context, PayActivity.class)
							.putExtra("money",(float)money).putExtra("canuse", false),4);
					}
				}).show();
			}
		});
		
		viewHolder.shiyong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(User.getUser()==null){
					return ;
				}
				if(viewHolder.money.getText().toString().equals("")){
					Toast.makeText(context,"金额不能为空",Toast.LENGTH_SHORT).show();
					return ;
				}
				float money=Float.valueOf(viewHolder.money.getText().toString()+"");
				String param="uid="+User.getUser().getId()+"&token="+Configs.token+"&money="+money+"&id="+model.getId();
				NetUtil.sendPost(ShiyongUrl,param,new NetResult() {
					@Override
					public void result(String response) {
						try{
							JSONObject object=new JSONObject(response);
							if(object.getInt("status")==1){
								Toast.makeText(context,object.getString("msg"),Toast.LENGTH_SHORT).show();
								viewHolder.money.setText("");
							}
						}catch(Exception e){
						}
					}
				});
			}
		});

//		ImageLoader.getInstance().displayImage(model.getCurvePicUrl(),viewHolder.img, Options.getListOptions());
        if(model.getYouSuanShiShiChart()==null){
            model.setYouSuanShiShiChart(new YouSuanShiShiChart(viewHolder.chart,model.getKey()));
        }else{
            model.getYouSuanShiShiChart().UpdateChart(viewHolder.chart);
        }
	}

    public String dealName(String name){
        int index=name.indexOf("(");
        String first=name.substring(0,index-1);
        String end=name.substring(index,name.length());
        return first+"\n"+end;
    }

	@Override
	public YousuanViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		return new YousuanViewHolder(LayoutInflater.from(context).inflate(ResourceId,null));
	}

}

class YousuanViewHolder extends RecyclerView.ViewHolder{

	public YousuanViewHolder(View view) {
		super(view);
//		img = (ImageView) view.findViewById(R.id.yousuan_img);
		chart=(LineChart)view.findViewById(R.id.yousuan_linechart);
        name = (TextView) view.findViewById(R.id.youshishi_name);
		nianhuibao = (TextView) view.findViewById(R.id.nianhuibiao);
		zuidahuice = (TextView) view.findViewById(R.id.zuidahuice);
		huicetianshu = (TextView) view.findViewById(R.id.huicetianshu);
		xiapu = (TextView) view.findViewById(R.id.xiapu);
		shiyong = (Button) view.findViewById(R.id.gengzongshiyong);
		touzi = (Button) view.findViewById(R.id.lijiyouzi);
		money=(EditText)view.findViewById(R.id.yousuanshishi_money);
	}
	
//	public ImageView img;
	public TextView name;
	public TextView nianhuibao;
	public TextView zuidahuice;
	public TextView huicetianshu;
	public TextView xiapu;
	public Button shiyong;
	public Button touzi;
	public EditText money;
    public LineChart chart;
}
