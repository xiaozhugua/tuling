package com.abct.tljr.ui.fragments.zhiyanFragment.adapter;

import io.realm.Realm;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.OneGu;
import com.abct.tljr.model.Options;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.shouye.SpeakActiviy;
import com.abct.tljr.ui.activity.zhiyan.ReSearchActivity;
import com.abct.tljr.ui.widget.DefaultIconDrawable;
import com.abct.tljr.ui.widget.RoundProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;

public class MyViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context context=null;
	private List<ZhongchouBean> list=null;
	private Realm realm;
	private boolean FinishStatus=false;
	private boolean FinishingStatus=false;
    private SimpleDateFormat mSimpleDateFormat=null;
    private NumberFormat numFormat=NumberFormat.getNumberInstance() ;
    
	public MyViewAdapter(Context context,List<ZhongchouBean> list){
		this.context=context;
		this.list=list;
		realm = Realm.getDefaultInstance();
		mSimpleDateFormat=new SimpleDateFormat("MM月dd日 HH:mm");
		numFormat.setMaximumFractionDigits(1); 
	}
	
	@Override
	public int getItemCount() {
		return list.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
		final ZhongchouBean bean = list.get(position);
		OneGu gu = realm.where(OneGu.class).equalTo("code",bean.getCode()).equalTo("market",bean.getMarket()).findFirst();
		if(bean.getStatus()==0){	
		  //众筹研究
		  ViewHolderFinish holderfinish=(ViewHolderFinish)holder;
		  if (gu != null){
			if(!FinishStatus){
				FinishStatus=true;
				bean.setFinishstatus(true);
			}else{
				if(bean.isFinishstatus()){
					holderfinish.zhiyan_header.setVisibility(View.VISIBLE);
					bean.setFinishstatus(true);
				}else{
					holderfinish.zhiyan_header.setVisibility(View.GONE);
					bean.setFinishstatus(false);
				}
			}
			  
			holderfinish.name.setText(gu.getName());
			holderfinish.code.setText(bean.getCode());
			holderfinish.type.setText(bean.getType());
			holderfinish.authorname.setText(bean.getUserCrowdList().get(0).getUser().getUnickname());
			holderfinish.lev.setText("lv"+bean.getUserCrowdList().get(0).getUser().getUlevel()+"");
			ImageLoader.getInstance().displayImage(bean.getUserCrowdList().get(0).getUser().getUavata(),
					 holderfinish.authorimg, Options.getCircleListOptions());
			holderfinish.context.setText("于"+mSimpleDateFormat.format(new Date(
					bean.getUserCrowdList().get(0).getUser().getCreateData()))+"发起/已认购"
					+bean.getUserCrowdList().get(0).getUser().getCount()+"份"+"("+
					+bean.getUserCrowdList().get(0).getUser().getAllMoney()/100+"元)");
			final int persen = bean.getHasMoney() * 100 / bean.getTotalMoney();
			holderfinish.persen.setText(persen + "/100");
	
			String[] s = bean.getSection().split("-");
			for (int i = 0; i < holderfinish.tvs.length; i++) {
				if (i < s.length && !s[i].equals("")) {
					holderfinish.tvs[i].setVisibility(View.VISIBLE);
					holderfinish.tvs[i].setText(s[i]);
				} else {
					holderfinish.tvs[i].setVisibility(View.GONE);
				}
			}
			holderfinish.rl_xianqing.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
						Intent i = new Intent(context,ReSearchActivity.class);
						i.putExtra("market", bean.getMarket());
						i.putExtra("code", bean.getCode());
						i.putExtra("id", bean.getId());
						i.putExtra("url", bean.getIconurl());
						i.putExtra("type", bean.getType());
						i.putExtra("typedesc", bean.getTypedesc());
						i.putExtra("persen", persen);
						i.putExtra("focus", bean.getFocus());
						i.putExtra("remark", bean.getRemark());
						i.putExtra("isfocus", bean.isIsfocus());
						i.putExtra("section", bean.getSection());
                        i.putExtra("myposition",position);
						ZhiyanArtZhongchouAdapter.userCrowdList = bean.getUserCrowdList();
						context.startActivity(i);
				}
			});
			holderfinish.im.setProgress((bean.getHasMoney()*100)/bean.getTotalMoney());

            if(bean.isCheckStatus()){
                holderfinish.checkBox.setVisibility(View.VISIBLE);
            }else{
                holderfinish.checkBox.setVisibility(View.GONE);
            }

            holderfinish.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        bean.setCheckAction(true);
                    }else{
                        bean.setCheckAction(false);
                    }
                }
            });

              if(bean.isCheckAction()){
                  holderfinish.checkBox.setChecked(true);
              }else{
                  LogUtil.e("acation",false+"");
                  holderfinish.checkBox.setChecked(false);
              }

		 }
		}else{
			//已完成
			ViewHolderFinishing holderfinishing=(ViewHolderFinishing)holder;
			if (gu != null){
				if(!FinishingStatus){
					FinishingStatus=true;
					bean.setFinishstatus(true);
					holderfinishing.zhiyan_header2.setVisibility(View.GONE);
				}else{
					if(bean.isFinishstatus()){
						holderfinishing.zhiyan_header.setVisibility(View.VISIBLE);
						holderfinishing.zhiyan_header2.setVisibility(View.GONE);
						bean.setFinishstatus(true);
					}else{
						holderfinishing.zhiyan_header.setVisibility(View.GONE);
						holderfinishing.zhiyan_header2.setVisibility(View.VISIBLE);
						bean.setFinishstatus(false);
					}
				}
				holderfinishing.name.setText(gu.getName());
				holderfinishing.code.setText(bean.getCode());
				holderfinishing.type.setText(bean.getType());
				
				holderfinishing.flower.setText(numFormat.format(bean.getCustomerRating())+"");
				holderfinishing.star.setText(bean.getPrivateStar()+"");
			       
				holderfinishing.progress_star.setProgress((int)(100*((double)bean.getPrivateStar()/5)));
				holderfinishing.progress_flower.setProgress((int)(100*((double)bean.getCustomerRating()/5)));
				
				holderfinishing.pinglun.setText(mSimpleDateFormat.format(new Date(bean.getCreateDate())));
				holderfinishing.faqiren_name.setText(bean.getUserCrowdList().get(0).getUser().getUnickname());
				holderfinishing.pinlunqu.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						context.startActivity(new Intent(context, SpeakActiviy.class).putExtra("id",bean.getId()));
					}
				});
				
				String[] s = bean.getSection().split("-");
				for (int i = 0; i < holderfinishing.tvs.length; i++) {
					if (i < s.length && !s[i].equals("")) {
						holderfinishing.tvs[i].setVisibility(View.VISIBLE);
						holderfinishing.tvs[i].setText(s[i]);
					} else {
						holderfinishing.tvs[i].setVisibility(View.GONE);
					}
				}
				
				holderfinishing.rl_xianqing.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.listLeftBeans.get(
								MyApplication.getInstance().getMainActivity().zhiyanFragment.mZhiYanFinishView.nowSection)
								.getFinishViewItemShow().adapter.pay(bean);
					}
				});
				
				if (bean.getIconurl().equals("http://www.baidu.com")) {
					  holderfinishing.im.setImageDrawable(new DefaultIconDrawable(holderfinishing.im.getLayoutParams().height,
							  holderfinishing.name.getText().toString(), false));
				} else {
					ImageLoader.getInstance().displayImage(bean.getIconurl(), holderfinishing.im, Options.getListOptions());
			    }

                if(bean.isCheckStatus()){
                    holderfinishing.checkBox.setVisibility(View.VISIBLE);
                }else{
                    holderfinishing.checkBox.setVisibility(View.GONE);
                }

                holderfinishing.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            bean.setCheckAction(true);
                        }else{
                            bean.setCheckAction(false);
                        }
                    }
                });

                if(bean.isCheckAction()){
                    holderfinishing.checkBox.setChecked(true);
                }else{
                    LogUtil.e("acation",false+"");
                    holderfinishing.checkBox.setChecked(false);
                }
			}
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		if(arg1==0){
			//众筹研究
			return new ViewHolderFinish(LayoutInflater.from(context).inflate(R.layout.tljr_zhiyan_my_finish,arg0, false));
		}else{
			//已完成
			return new ViewHolderFinishing(LayoutInflater.from(context).inflate(R.layout.tljr_zhiyan_my_finishing,arg0, false));
		}
	}

	@Override
	public int getItemViewType(int position) {
		return list.get(position).getStatus();
	}
	
}

class ViewHolderFinish extends RecyclerView.ViewHolder {
	public ViewHolderFinish(View itemView) {
		super(itemView);
		int[] colors = { ColorUtil.red, ColorUtil.green, ColorUtil.blue };
		name = (TextView) itemView.findViewById(R.id.name);
		code = (TextView) itemView.findViewById(R.id.code);
		type = (TextView) itemView.findViewById(R.id.level);
		persen = (TextView) itemView.findViewById(R.id.persen);
		rl_xianqing = (View) itemView.findViewById(R.id.rl_xianqing);
		authorimg=(ImageView)itemView.findViewById(R.id.zhiyan_finish_authorimg);
		authorname=(TextView)itemView.findViewById(R.id.zhiyan_finish_authorname);
		context=(TextView)itemView.findViewById(R.id.zhiyan_finish_context);
		lev = (TextView) itemView.findViewById(R.id.zhiyan_finish_authordengji);
		zhiyan_header=(RelativeLayout)itemView.findViewById(R.id.zhiyan_header);
		//zhiyan_header2=(TextView)itemView.findViewById(R.id.zhiyan_header2);
		im = (RoundProgressBar) itemView.findViewById(R.id.im);
        checkBox=(CheckBox)itemView.findViewById(R.id.myzhiyan_select) ;
		im.setRoundWidth(8);
		im.setTextColor(context.getResources().getColor(R.color.black));
		im.setTextSize(35);
		im.setCricleColor(context.getResources().getColor(R.color.morelightgray));
		im.setCricleProgressColor(context.getResources().getColor(R.color.zhiyanround));
		tvs[0] = (TextView) itemView.findViewById(R.id.tv1);
		tvs[1] = (TextView) itemView.findViewById(R.id.tv2);
		tvs[2] = (TextView) itemView.findViewById(R.id.tv3);
		((GradientDrawable) type.getBackground()).setStroke(2, colors[0]);
		for (int i = 0; i < tvs.length; i++) {
			tvs[i].setTextColor(colors[i]);
			GradientDrawable gd = (GradientDrawable) tvs[i].getBackground();
			gd.setStroke(2, colors[i]);
		}
	}
	TextView name;
	TextView code;
	TextView type;
	TextView lev;
	TextView persen;
	View rl_xianqing;
	RoundProgressBar im;
	ImageView authorimg;
	TextView authorname;
	TextView context;
	RelativeLayout zhiyan_header;
	//TextView zhiyan_header2;
	TextView[] tvs = new TextView[3];
    CheckBox checkBox;
}

class ViewHolderFinishing extends RecyclerView.ViewHolder {
	public ViewHolderFinishing(View arg0) {
		super(arg0);
		int[] colors = { ColorUtil.red, ColorUtil.green, ColorUtil.blue };
		name = (TextView) itemView.findViewById(R.id.name);
		code = (TextView) itemView.findViewById(R.id.code);
		type = (TextView) itemView.findViewById(R.id.level);
		rl_xianqing = (View) itemView.findViewById(R.id.rl_xianqing);
		im = (ImageView) itemView.findViewById(R.id.im);
		pinglun=(TextView)itemView.findViewById(R.id.zhiyan_finish_authordengji);
		pinlunqu=(RelativeLayout)itemView.findViewById(R.id.zhiyan_pinlunqu);
		progress_star=(ProgressBar)itemView.findViewById(R.id.progress_star);
		progress_flower=(ProgressBar)itemView.findViewById(R.id.progress_flower);
        checkBox=(CheckBox)itemView.findViewById(R.id.myzhiyan_select);
		star=(TextView)itemView.findViewById(R.id.star);
		zhiyan_header=(RelativeLayout)itemView.findViewById(R.id.zhiyan_header);
		zhiyan_header2=(TextView)itemView.findViewById(R.id.zhiyan_header2);
		flower=(TextView)itemView.findViewById(R.id.flower);
		faqiren_name=(TextView)itemView.findViewById(R.id.zhiyan_finish_faqiren_name);
		tvs[0] = (TextView) itemView.findViewById(R.id.tv1);
		tvs[1] = (TextView) itemView.findViewById(R.id.tv2);
		tvs[2] = (TextView) itemView.findViewById(R.id.tv3);
		((GradientDrawable) type.getBackground()).setStroke(2, colors[0]);
		for (int i = 0; i < tvs.length; i++) {
			tvs[i].setTextColor(colors[i]);
			GradientDrawable gd = (GradientDrawable) tvs[i].getBackground();
			gd.setStroke(2, colors[i]);
		}
	}
	TextView name;
	TextView code;
	TextView type;
	View rl_xianqing;
	ImageView im;
	TextView pinglun;
	RelativeLayout pinlunqu;
	ProgressBar progress_star;
	ProgressBar progress_flower;
	TextView star;
	TextView flower;
	RelativeLayout zhiyan_header;
	TextView zhiyan_header2;
	TextView faqiren_name;
	TextView[] tvs = new TextView[3];
	CheckBox checkBox;
}
