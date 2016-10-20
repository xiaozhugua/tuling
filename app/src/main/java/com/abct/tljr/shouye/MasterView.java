package com.abct.tljr.shouye;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.R;
import com.abct.tljr.ranking.OnePerson;
import com.abct.tljr.ranking.OneRankInfoActivity;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MasterView {
	Activity activity;
	private ArrayList<OnePerson> mList = new ArrayList<OnePerson>();
	private LinearLayout master;
	private Handler handler;
	private View view;
	
	public View getView(){
		return view;
	}
	
	public MasterView(Activity activity,final Handler handler){
		this.activity = activity;
		this.handler = handler;
		view = activity.getLayoutInflater().inflate(
				R.layout.shouye_master, null);
		master = (LinearLayout) view.findViewById(R.id.tljr_grp_master);
		view.findViewById(R.id.tljr_master).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(3);
					}
				});
	}
	
	public void getMaster() {
		NetUtil.sendPost(UrlUtil.URL_ranking, "method=gettop5" + "&page=1"
				+ "&size=5", new NetResult() {

			@Override
			public void result(String msg) {
				// TODO Auto-generated method stub
				if (msg.equals("")) {
					getMaster();
					return;
				}
				try {
					final JSONObject object = new JSONObject(msg);
					if (object.getInt("status") == 1) {
						final org.json.JSONArray array = object
								.getJSONArray("msg");
						mList.clear();
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							OnePerson person = new OnePerson();
							person.setName(obj.optString("nickname"));
							person.setZhname(obj.optString("zhname"));
							person.setAvatar(obj.optString("avatar"));
							person.setFollow(new Random().nextBoolean());
							person.setValue((float) obj.optDouble("yueshouyi"));
							person.setOtherValue((float) obj
									.optDouble("rishouyi"));
							person.setUid(obj.optString("uid"));
							person.setZhid(obj.optString("zhid",
									obj.optString("id")));
							person.setSource(obj.optString("tag"));
							person.setTime(obj.optLong("updateDate"));
							person.setColor(obj.optString("color"));
							person.setFollowNum(obj.has("followNum") ? obj
									.getInt("followNum") : -1);
							person.setPurl(obj.optString("imgurl"));
							mList.add(person);
						}
						showMaster();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private void showMaster() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0);
				master.removeAllViews();
				int m = 0;
				float changep = 0;
				for (int i = 0; i < mList.size(); i++) {
					if (mList.get(i).getValue() > changep) {
						m = i;
						changep = mList.get(i).getValue();
					}
				}
				for (int i = 0; i < mList.size(); i++) {
					final OnePerson person = mList.get(i);
					View v = View.inflate(activity, R.layout.tljr_item_symater,
							null);
					Util.setSize(v, Util.IMAGEWIDTH, 220);
					if (!person.getPurl().equals("")) {
						Util.setNetImage(person.getPurl(), (ImageView) v
								.findViewById(R.id.tljr_item_symaster_pic),
								handler);
					}
					((TextView) v.findViewById(R.id.tljr_item_symaster_name))
							.setText(person.getZhname());
					((TextView) v.findViewById(R.id.tljr_item_symaster_source))
							.setText(person.getSource());
					((TextView) v
							.findViewById(R.id.tljr_item_symaster_username))
							.setText(person.getName());
					((TextView) v.findViewById(R.id.tljr_item_symaster_time))
							.setText("["
									+ Util.getDateOnlyDat(person.getTime())
									+ "]");
					((TextView) v.findViewById(R.id.tljr_item_symaster_changep))
							.setText(Util.df.format(person.getValue() * 100)
									+ "%");
					((TextView) v
							.findViewById(R.id.tljr_item_symaster_todayChangeP)).setTextColor(person
							.getOtherValue() > 0 ? ColorUtil.red : ColorUtil.green);
					((TextView) v
							.findViewById(R.id.tljr_item_symaster_todayChangeP)).setText(Util.df
							.format(person.getOtherValue() * 100) + "%");
					if (person.getFollowNum() > 0) {
						(v.findViewById(R.id.tljr_item_symaster_followt))
								.setVisibility(View.VISIBLE);
						((TextView) v
								.findViewById(R.id.tljr_item_symaster_follow))
								.setText(person.getFollowNum() + "");
					}
					if (i == m)
						v.findViewById(R.id.tljr_grp_symaster_changep)
								.setBackgroundResource(R.drawable.img_dayuan2);
					v.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							activity.startActivity(new Intent(activity,
									OneRankInfoActivity.class)
									.putExtra("uid", person.getUid())
									.putExtra("zhid", person.getZhid())
									.putExtra("name", person.getName())
									.putExtra("zhname", person.getZhname()));
						}
					});
					master.addView(v);
				}
			}
		});
	}

}
