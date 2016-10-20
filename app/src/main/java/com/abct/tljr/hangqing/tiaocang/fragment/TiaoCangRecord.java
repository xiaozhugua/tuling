package com.abct.tljr.hangqing.tiaocang.fragment;

import io.realm.Realm;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.hangqing.util.GuDealImpl;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.OneGu;
import com.qh.common.listener.NetResult;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TiaoCangRecord extends Fragment {

	private View tiaocang = null;
	private LinearLayout mainView;
	private SimpleDateFormat DateFormat;
	private SimpleDateFormat TimeFormat;
	private int status = 1;
	private NumberFormat nf;
	private TextView NotTiaoCangData;
	private String code;
	private String market;
	private OneGu gu;

	@SuppressLint("SimpleDateFormat")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		tiaocang = inflater.inflate(R.layout.showtiaocangview, container, false);
		DateFormat = new SimpleDateFormat("yyyy-MM-dd");
		TimeFormat = new SimpleDateFormat("HH:mm:ss");

		nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(1);// 这个1的意识是保存结果到小数点后几位，但是特别声明：这个结果已经先＊100了。

		NotTiaoCangData = (TextView) tiaocang.findViewById(R.id.tljr_zx_nottiaocangdata);
		mainView = (LinearLayout) tiaocang.findViewById(R.id.tljr_zx_showtiaocang_linear);

		getTiaoCangData();
		
		return tiaocang;
	}

	// 网上获取数据
	public void getTiaoCangData() {
		try {
			final Message message = new Message();
			message.what = 2;
			final Bundle bundle = new Bundle();
			NetUtil.sendPost(UrlUtil.URL_tc, "action=tiaocangjilu&uid="
					+ MyApplication.getInstance().self.getId() + "&zhid="
					+ getActivity().getIntent().getStringExtra("zuid"),
					new NetResult() {
						@Override
						public void result(String msg) {
							bundle.putString("msg", msg);
							message.setData(bundle);
							handler.sendMessage(message);
						}
					});
		} catch (Exception e) {
			showMessage("网上获取数据失败");
		}
	}

	public void showMessage(String tip) {
		Message message = Message.obtain();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("msg", tip);
		message.setData(bundle);
		handler.sendMessage(message);
	}

	// 解析网上的数据
	@SuppressLint({ "InflateParams", "HandlerLeak" })
	public void ParseMsg(String msg) {
		try {
			JSONObject object = new JSONObject(msg);
			if (object.getInt("status") == 1) {
				JSONArray array = object.getJSONArray("msg");
				if (array.toString().equals("[]")) {
					NotTiaoCangData.setVisibility(View.VISIBLE);
				} else {
					JSONObject tempObject;
					JSONArray object2;
					JSONObject obj;
					for (int i = 0; i < array.length(); i++) {
						obj = array.getJSONObject(i);
						// 得到时间
						String time = obj.getString("time");
						object2 = new JSONArray(obj.getString("tiaocang"));
						// 获取视图item
						View view = LayoutInflater.from(getActivity()).inflate(
								R.layout.showtiaocangitem, null);
						((TextView) view.findViewById(R.id.showtiaocangitem_time))
								.setText(DateFormat.format(new Date(Long.valueOf(time))));
						((TextView) view.findViewById(R.id.tljr_zx_xiangxicangwei))
								.setText(TimeFormat.format(new Date(Long.valueOf(time))));
						// 加入总视图
						mainView.addView(view);
						// 设置点击事件
						itemClick(view);
						// 创建tiaocang具体的view
						View view2;
						for (int j = 0; j < object2.length(); j++) {
							tempObject = object2.getJSONObject(j);
							// 加载界面
							view2 = LayoutInflater.from(getActivity()).inflate(R.layout.tiaocangitem, null);
							code = tempObject.getString("code");
							market = tempObject.getString("market");
							gu = GetCodeName(market + code);
							((TextView) view2.findViewById(R.id.tljr_zx_tiaocangname))
									.setText(gu.getName());
							((TextView) view2.findViewById(R.id.tljr_zx_tiaocangcode))
									.setText(code);
							((TextView) view2.findViewById(R.id.tljr_zx_topercent))
									.setText(nf.format(Double.valueOf(tempObject.getString("toPercent"))));
							((TextView) view2.findViewById(R.id.tljr_zx_srcpercenti))
									.setText(nf.format(Double.valueOf(tempObject.getString("srcPercent"))));
							view2.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(getActivity(),OneGuActivity.class);
									// 用Bundle携带数据
									Bundle bundle = new Bundle();
									// 传递name参数为tinyphp
									bundle.putString("code", code);
									bundle.putString("market", market);
									bundle.putString("name", gu.getName());
									bundle.putString("key", gu.getKey());
									bundle.putString("zuName",getActivity().getIntent().getStringExtra("zuName"));
									bundle.putSerializable("onegu", GuDealImpl.getNewOneGu(gu));
									intent.putExtras(bundle);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									getActivity().startActivity(intent);
								}
							});
							// 添加界面
					       ((LinearLayout) view.findViewById(R.id.tiaocangtiem)).addView(view2);
						}
						if (status == 1) {
							((LinearLayout) view.findViewById(R.id.tiaocangtiem)).setVisibility(View.VISIBLE);
							openItem((ImageView) view.findViewById(R.id.tljr_zx_xiangxicangweiimage));
							status += 1;
						}
					}
				}
			} else {
			}
		} catch (Exception e) {
		}
	}

	public void itemClick(final View view) {
		((RelativeLayout) view.findViewById(R.id.showtiaocang_head))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						View linearView = ((LinearLayout) view.findViewById(R.id.tiaocangtiem));
						if (linearView.getVisibility() == View.GONE) {
							linearView.setVisibility(View.VISIBLE);
							openItem((ImageView) view.findViewById(R.id.tljr_zx_xiangxicangweiimage));
						} else {
							linearView.setVisibility(View.GONE);
							closeItem((ImageView) view.findViewById(R.id.tljr_zx_xiangxicangweiimage));
						}
					}
				});
	}

	public void closeItem(ImageView image) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(image,"rotation",-90f, 0f);
		animator.setDuration(300);
		animator.start();
	}

	public void openItem(ImageView image) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(image,"rotation",0f,-90f);
		animator.setDuration(300);
		animator.start();
	}

	public static OneGu GetCodeName(String text) {
		Realm realm = Realm.getDefaultInstance();
		OneGu gu = realm.where(OneGu.class).equalTo("marketCode", text).findFirst();
		realm.close();
		return gu;
	}
	
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getActivity(), msg.getData().getString("msg"),
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Bundle bundle = msg.getData();
				ParseMsg(bundle.getString("msg"));
				break;
			}
		};
	};

}
