package com.abct.tljr.ui.adapter.research;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.abct.tljr.R;
import com.abct.tljr.chart.ImageOptions;
import com.abct.tljr.model.UserCrowd;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mac on 16/1/27.
 */
public class SupportAdapter extends BaseAdapter {

	LayoutInflater inflater;

	Activity activity;
	private ArrayList<UserCrowd> list;

	public SupportAdapter(Activity activity, ArrayList<UserCrowd> list) {
		this.activity = activity;
		this.list = list;
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;

		final UserCrowd userCrowd = list.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_research_support, null);
			holder = new Holder();
			holder.name = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.im = (ImageView) convertView.findViewById(R.id.img_avatar);
			holder.Lv = (TextView) convertView.findViewById(R.id.tv_user_level);
			holder.support = (TextView) convertView.findViewById(R.id.tv_user_support);
			holder.money = (TextView) convertView.findViewById(R.id.textView2);
			holder.focus = convertView.findViewById(R.id.focus);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.name.setText(userCrowd.getUser().getUnickname());
		holder.Lv.setText("Lv " + userCrowd.getUser().getUlevel());
		holder.support.setText(
				"累计发起:" + userCrowd.getUser().getCount() + "次/累计支持:" + userCrowd.getUser().getAllMoney() / 100 + "元");
		holder.money.setText("支持:" + userCrowd.getAllMoney() / 100 + "元");

		final ImageAware imageAware2 = new ImageViewAware(holder.im, false);
		// 防止重复加载闪烁错位
		if (holder.im.getTag() == null || !holder.im.getTag().equals(userCrowd.getUser().getUavata())) {
			ImageLoader.getInstance().displayImage(userCrowd.getUser().getUavata(), imageAware2,
					ImageOptions.getCircleListOptions(), new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							// TODO Auto-generated method stub
							ImageLoader.getInstance().displayImage(userCrowd.getUser().getUnickname(), imageAware2);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onLoadingCancelled(String imageUri, View view) {
							// TODO Auto-generated method stub

						}
					});
			holder.im.setTag(userCrowd.getUser().getUavata());
		}
		if (User.getUser() != null && userCrowd.getUser().getUuid().equals(User.getUser().getId())) {
			holder.focus.setVisibility(View.GONE);
		}else{
			holder.focus.setVisibility(View.VISIBLE);
		}
		holder.focus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (User.getUser() == null) {
					Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
				} else {
					focus(userCrowd, (TextView) v.findViewById(R.id.focust));
				}
			}
		});
		return convertView;
	}

	private void showToast(String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}

	private void focus(final UserCrowd userCorwd, final TextView v) {
		if (userCorwd.getUser().getUuid().equals(User.getUser().getId())) {
			showToast("不能关注自己！");
			return;
		}
		String a = v.getText().toString();
		int type = a.contains("已") ? 0 : 1;
		ProgressDlgUtil.showProgressDlg("", activity);
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
								} else {
									userCorwd.setFocs(true);
									v.setText(" " + "已关注");
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

	static class Holder {

		TextView name, Lv, support, money;
		ImageView im;
		View focus;
	}

}
