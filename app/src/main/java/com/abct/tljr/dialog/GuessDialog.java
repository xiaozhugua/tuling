package com.abct.tljr.dialog;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.kline.OneGuActivity;
import com.abct.tljr.model.Options;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.ui.widget.MySeekBar;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.util.ColorUtil;
import com.qh.common.util.LogUtil;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xbw
 * @version 创建时间：2015年11月2日 上午11:26:39
 */
public class GuessDialog extends Dialog implements OnClickListener {
	public static String url = UrlUtil.Url_guess + "3002/";
	public static String userId = "";
	private Context context;
	private String stockId;
	private boolean guessUp;// true看涨false看跌
	private View my, other, check;
	private ImageView custor;
	private TextView txt_my, txt_other, hint;
	private int red, black;
	private boolean isInitOther = false;
	private ListView lv;
	private ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	private MySeekBar seekBarTime;
	private String TAG = "GuessDialog";
	private MySeekBar seekBarupdown;
	private RadioGroup RadioTimes;
	private String time = "周";
	private int multiple = 7;
	private boolean isSend = false;
	private String msg = "";
	private String DialogName="";
	
	public static void login(final Complete complete) {
		NetUtil.sendPost(url + "api/user/login",
				"uname=" + MyApplication.getInstance().self.getUserName() + "&upass="
						+ MyApplication.getInstance().self.getUserPwd() + "&nickname="
						+ MyApplication.getInstance().self.getNickName() + "&avatar="
						+ MyApplication.getInstance().self.getAvatarUrl(),
				new NetResult() {

					@Override
					public void result(String msg) {
						// TODO Auto-generated method stub
						try {
							JSONObject object = new JSONObject(msg);
							if (object.getInt("code") == 1) {
								JSONObject obj = object.getJSONObject("result");
								userId = obj.optString("userid");
								// user.setScore(obj.getLong("score"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (complete != null)
							complete.complete();
					}
				});
	}

	@SuppressWarnings("deprecation")
	public GuessDialog(final Context context,final String name,final String stockId,final boolean guessUp,final OneGuActivity activity) {
		this(context);
		this.guessUp = guessUp;
		this.stockId = stockId;                                    
		this.DialogName=name;
//		findViewById(R.id.tljr_grp_guessup).setVisibility(guessUp ? View.VISIBLE : View.INVISIBLE);
//		findViewById(R.id.tljr_grp_guessdown).setVisibility(guessUp ? View.INVISIBLE : View.VISIBLE);
		// ((TextView) findViewById(R.id.tljr_txt_hq_name)).setText(name);
		((ImageView) findViewById(R.id.custor)).setImageDrawable(guessUp ? context.getResources().getDrawable(R.drawable.img_jiantou1)
						: context.getResources().getDrawable(R.drawable.img_jiantou2));
		((TextView) findViewById(R.id.tv_updown)).setTextColor(guessUp ? ColorUtil.red : ColorUtil.green);
 		custor = (ImageView) findViewById(R.id.custor);
		custor.getLayoutParams().width = Util.dp2px(context, 15);
		int offsetX = (Util.WIDTH / 4 - Util.dp2px(context, 15)) / 2;
		int to = (guessUp ? 0 : 1);
		Animation animation = null;
		animation = new TranslateAnimation(0, to * Util.WIDTH / 4 + offsetX, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(0);
		custor.startAnimation(animation);
		handler.sendEmptyMessage(1);
		if (!guessUp) {
			seekBarTime.setOther(R.drawable.img_zhangdiefuzhizhen2);
			seekBarTime.setThumb(context.getResources().getDrawable(R.drawable.seekbar_thumb2));
			seekBarTime.setThumbOffset(0);
			seekBarTime.setProgressDrawable(context.getResources().getDrawable(R.drawable.seekbar_color2));
			seekBarupdown.setOther(R.drawable.img_zhangdiefuzhizhen2);
			seekBarupdown.setThumb(context.getResources().getDrawable(R.drawable.seekbar_thumb2));
			seekBarupdown.setThumbOffset(0);
			seekBarupdown.setProgressDrawable(context.getResources().getDrawable(R.drawable.seekbar_color2));
		}
		for (int i = 0; i < RadioTimes.getChildCount(); i++) {
			if (RadioTimes.getChildAt(i) instanceof RadioButton) {
				RadioTimes.getChildAt(i).setBackgroundDrawable(guessUp ? context.getResources().getDrawable(R.drawable.tljr_check_red)
								: context.getResources().getDrawable(R.drawable.tljr_check_green));
			}
		}
		if(guessUp){
			findViewById(R.id.tljr_grp_guessdown).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					new GuessDialog(context,name,stockId,!guessUp,activity).show();
				}
			});
		}else{
			findViewById(R.id.tljr_grp_guessup).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					new GuessDialog(context,name,stockId,!guessUp,activity).show();
				}
			});
		}
		findViewById(R.id.tljr_txt_tjzx).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//activity.sharedInfo();
				dismiss();
			}
		});
		findViewById(R.id.tljr_btn_tjzx).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//activity.PingLun();
				dismiss();
			}
		});
		
	}

	public GuessDialog(Context context) {
		super(context, R.style.dialog);
		// super(context, android.R.style.Theme_Light_NoTitleBar);
		this.context = context;
		setContentView(R.layout.tljr_dialog_guess);
		Window dialogWindow = getWindow();
		dialogWindow.setWindowAnimations(R.style.AnimationPreview); // 设置窗口弹出动画
		dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// dialogWindow
		// .setSoftInputMode(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		// | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
		// | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
		// p.width = (int) (AppInfo.WIDTH);
		// p.height = (int) (AppInfo.HEIGHT);
		p.width = LayoutParams.FILL_PARENT;
		p.height = LayoutParams.FILL_PARENT;
		dialogWindow.setAttributes(p);
		red = context.getResources().getColor(R.color.c_red_text);
		black = context.getResources().getColor(R.color.gray);
		initView();
	}

	private void initView() {
		findViewById(R.id.tljr_grp_hq_top).setOnClickListener(this);
		findViewById(R.id.bj).setOnClickListener(this);
		findViewById(R.id.tljr_close).setOnClickListener(this);
		findViewById(R.id.tljr_grp_guessup).setOnClickListener(this);
		findViewById(R.id.tljr_grp_guessdown).setOnClickListener(this);
		findViewById(R.id.tljr_txt_tjzx).setOnClickListener(this);
		// findViewById(R.id.tljr_btn_tjzx).setOnClickListener(
		// new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// dismiss();
		// }
		// });
		my = findViewById(R.id.tljr_dialog_grp_my);
		other = findViewById(R.id.tljr_dialog_grp_other);
		check = findViewById(R.id.tljr_view_check);
		check.getLayoutParams().width = Util.WIDTH / 2 - Util.dp2px(context, 50) / 2;
		check.setTag(1);
		txt_my = (TextView) findViewById(R.id.tljr_txt_my);
		txt_other = (TextView) findViewById(R.id.tljr_txt_other);
		hint = (TextView) findViewById(R.id.tv_hint);
		lv = (ListView) findViewById(R.id.tljr_dialog_lv);
		lv.setAdapter(adapter);
		Setabout();

		handler.sendEmptyMessageDelayed(0, 100);
	}

	private void Setabout() {
		RadioTimes = (RadioGroup) findViewById(R.id.times);
		RadioTimes.check(R.id.radio2);
		RadioTimes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				switch (arg1) {
				case R.id.radio1:
					time = "日";
					seekBarTime.setMax(6);
					((TextView) findViewById(R.id.text_right)).setText("7");
					multiple = 1;
					break;
				case R.id.radio2:
					time = "周";
					seekBarTime.setMax(3);
					((TextView) findViewById(R.id.text_right)).setText("4");
					multiple = 7;
					break;
				case R.id.radio3:
					time = "月";
					seekBarTime.setMax(11);
					((TextView) findViewById(R.id.text_right)).setText("12");
					multiple = 30;
					break;
				case R.id.radio4:
					time = "年";
					seekBarTime.setMax(2);
					((TextView) findViewById(R.id.text_right)).setText("3");
					multiple = 365;
					break;
				default:
					break;
				}
				seekBarTime.setProgress(0);
				handler.sendEmptyMessage(1);
			}
		});

		// mybar = (TextView) findViewById(R.id.mybar);
		seekBarTime = (MySeekBar) findViewById(R.id.tljr_dialog_seekbar);
		// 初始设置一周
		seekBarTime.setMax(3);
		seekBarTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			@Override
			public void onProgressChanged(SeekBar bar, int position, boolean arg2) {
				seekBarTime.setProText(position + 1 + "");
				handler.sendEmptyMessage(1);
			}
		});
		seekBarupdown = (MySeekBar) findViewById(R.id.seekbar2);
		seekBarupdown.setMax(300);
		seekBarupdown.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar bar, int position, boolean arg2) {
				handler.sendEmptyMessage(1);
			}
		});

		txt_my.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tabChangedArrow(check, 0);
				txt_my.setTextColor(red);
				txt_other.setTextColor(black);
				my.setVisibility(View.VISIBLE);
				other.setVisibility(View.GONE);
			}
		});
		txt_other.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabChangedArrow(check, 1);
				txt_my.setTextColor(black);
				txt_other.setTextColor(red);
				my.setVisibility(View.GONE);
				other.setVisibility(View.VISIBLE);
				getNewestSpeak();
			}
		});
		findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSendDialog();
			}
		});
		findViewById(R.id.tljr_close).getLayoutParams().width = Util.dp2px(context, 50);
	}

	private void initLv() {
		int h = findViewById(R.id.tljr_scrollView1).getMeasuredHeight() - Util.dp2px(context, 110);
		lv.getLayoutParams().height = h;
	}

	private void showSendDialog() {
		new PromptDialog(context, "确定提交吗？", new Complete() {

			@Override
			public void complete() {
				if (MyApplication.getInstance().self == null) {
					if (context instanceof OneGuActivity) {
						((OneGuActivity) context).login();
					}
					return;
				}
				if (GuessDialog.userId.equals("")) {
					login(new Complete() {

						@Override
						public void complete() {
							send();
						}
					});
				} else {
					send();
				}
			}
		}).show();
	}

	private void tabChangedArrow(View v, int to) {
		int from = (Integer) v.getTag();
		int offset = Util.WIDTH / 2 - Util.dp2px(context, 50) / 2;
		Animation animation = null;
		animation = new TranslateAnimation(from * offset, to * offset, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(200);
		v.startAnimation(animation);
		v.setTag(to);
	}

	private void getOneInfo() {
		NetUtil.sendPost(url + "api/stock/" + stockId + "/info", "", new NetResult() {

			@Override
			public void result(String msg) {
				// TODO Auto-generated method stub
				LogUtil.e("getOneInfo", msg);
				try {
					JSONObject object = new JSONObject(msg);
					if (object.getInt("code") == 1) {
						JSONObject obj = object.getJSONObject("result");
						final int totalnumber = obj.optInt("totalnumber");
						final int bearnumber = obj.optInt("bearnumber");
						final int bullnumber = totalnumber - bearnumber;
						handler.post(new Runnable() {
							@Override
							public void run() {
								((TextView) findViewById(R.id.tljr_dialog_txt_upperson)).setText(bullnumber + "人看涨");
								((TextView) findViewById(R.id.tljr_dialog_txt_downperson)).setText(bearnumber + "人看跌");
								((TextView) findViewById(R.id.tljr_dialog_txt_up))
										.setText((int) (((float) bullnumber / totalnumber * 100)) + "%");
								((TextView) findViewById(R.id.tljr_dialog_txt_down))
										.setText((int) (((float) bearnumber / totalnumber * 100)) + "%");
								findViewById(R.id.tljr_dialog_img_pro)
										.getLayoutParams().width = (int) (((float) bearnumber / totalnumber)
												* Util.WIDTH);
							}
						});
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void getNewestSpeak() {
		if (!isSend && !this.msg.equals("")) {
			// showSpeak(this.msg);
			return;
		}
		initLv();
		isSend = false;
		ProgressDlgUtil.showProgressDlg("", (Activity) context);
		getOneInfo();
		NetUtil.sendPost(url + "api/stock/" + stockId + "/newest", "", new NetResult() {
			@Override
			public void result(String msg) {
				ProgressDlgUtil.stopProgressDlg();
				LogUtil.e("getNewestSpeak", msg);
				showSpeak(msg);
			}
		});
	}

	static class ViewHolder {
		TextView nickname, name, info, detail, time;
		ImageView avatar;
	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (v == null) {
				v = View.inflate(getContext(), R.layout.tljr_item_guess, null);
				holder = new ViewHolder();
				holder.nickname = (TextView) v.findViewById(R.id.onesee_nickname);
				holder.name = (TextView) v.findViewById(R.id.onesee_name);
				holder.info = (TextView) v.findViewById(R.id.onesee_info);
				holder.detail = (TextView) v.findViewById(R.id.textView4);
				holder.time = (TextView) v.findViewById(R.id.textView5);
				holder.avatar = (ImageView) v.findViewById(R.id.onesee_icon);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			JSONObject obj = list.get(position);
			StartActivity.imageLoader.displayImage(obj.optJSONObject("user").optString("avatar"), holder.avatar,
					Options.getCircleListOptions());
			// Util.setImage(obj.optJSONObject("user").optString("avatar"),
			// holder.avatar, handler);
			int guessType = obj.optInt("direction");
			holder.name.setText(guessType == 1 ? "看涨" : (guessType == 0 ? "看平" : "看跌"));
			holder.name.setTextColor(guessType == -1 ? ColorUtil.green : ColorUtil.red);
			holder.nickname.setText(obj.optJSONObject("user").optString("nickname"));
			holder.nickname.setBackgroundColor(guessType == -1 ? ColorUtil.green : ColorUtil.red);
			if (obj.optString("detail").equals("")) {
				holder.detail.setVisibility(View.GONE);
			} else {
				holder.detail.setVisibility(View.VISIBLE);
				holder.detail.setText("理由:" + obj.optString("detail"));
			}
			holder.time.setText("发表于:" + Util.getDate(obj.optLong("createtime")));
			float price = (float) ((1 + obj.optDouble("range") * (guessType == 1 ? 1 : -1)) * obj.optDouble("last"));
			if (price < 0)
				price = 0;
			holder.info.setText("截至到" + Util.getDateOnlyDat(obj.optLong("closetime")) + (guessType == 1 ? "涨幅" : "跌幅")
					+ (int) (obj.optDouble("range") * 100) + "%目标价" + Util.df.format(price));
			changeText(holder.info, guessType != -1);
			return v;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
	};

	private void changeText(TextView view, boolean guessUp) {
		String str = view.getText().toString();
		int a = str.indexOf("到");
		int b = str.indexOf("幅");
		int c = str.indexOf("目");
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new AbsoluteSizeSpan(16, true), b + 1, c, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new AbsoluteSizeSpan(16, true), c + 3, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new ForegroundColorSpan(guessUp ? ColorUtil.red : ColorUtil.green), a + 1, b - 1,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new ForegroundColorSpan(guessUp ? ColorUtil.red : ColorUtil.green), b + 1, c,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new ForegroundColorSpan(guessUp ? ColorUtil.red : ColorUtil.green), c + 3, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(style);
	}

	ArrayList<JSONObject> mlist = new ArrayList<JSONObject>();

	private void showSpeak(final String msg) {
		this.msg = msg;
		try {
			JSONObject object = new JSONObject(msg);
			if (object.getInt("code") == 1) {
				JSONArray array = object.getJSONArray("result");
				mlist.clear();
				for (int i = 0; i < array.length(); i++) {
					mlist.add(array.getJSONObject(i));
				}
				handler.post(new Runnable() {

					@Override
					public void run() {
						list.clear();
						list.addAll(mlist);
						adapter.notifyDataSetChanged();
					}
				});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void send() {
		ProgressDlgUtil.showProgressDlg("", (Activity) context);
		String p = guessUp ? "bull" : "bear";
		String now = "";
		if (context instanceof OneGuActivity) {
			//now = ((OneGuActivity) context).getNow();
		}
		LogUtil.e("send:",
				url + "api/submit/stock/" + stockId + "/" + p + "/deal" + "?userid=" + userId + "&days="
						+ multiple * (seekBarTime.getProgress() + 1) + "&detail="
						+ ((EditText) findViewById(R.id.tljr_dialog_et)).getText().toString() + "&last=" + now
						+ "&range=" + (float) seekBarupdown.getProgress() / 100);
		NetUtil.sendPost(url + "api/submit/stock/" + stockId + "/" + p + "/deal",
				"userid=" + userId + "&days=" + multiple * (seekBarTime.getProgress() + 1) + "&detail="
						+ ((EditText) findViewById(R.id.tljr_dialog_et)).getText().toString() + "&last=" + now
						+ "&range=" + (float) seekBarupdown.getProgress() / 100,
				new NetResult() {

					@Override
					public void result(final String msg) {
						LogUtil.e("返回:", msg);
						handler.post(new Runnable() {
							@Override
							public void run() {
								try {
									org.json.JSONObject object = new org.json.JSONObject(msg);
									if (object.getInt("code") == 1) {
										ProgressDlgUtil.stopProgressDlg();
										Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
										isSend = true;
									} else {
										ProgressDlgUtil.stopProgressDlg();
										Toast.makeText(context, "评论失败，请重试!", Toast.LENGTH_SHORT).show();
									}
								} catch (org.json.JSONException e) {
									e.printStackTrace();
									ProgressDlgUtil.stopProgressDlg();
									Toast.makeText(context, "评论失败，请重试!", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				break;
			case 1:
				hint.setText("预计" + (seekBarTime.getProgress()+1) + time + "后" + (guessUp ? "涨" : "跌") + "幅"
						+ seekBarupdown.getProgress() + "%");
				changeText(hint);
				break;
			default:
				break;
			}
		}
	};

	private void changeText(TextView view) {
		String str = view.getText().toString();
		int index = str.indexOf("后");
		if (index < 0) {
			return;
		}
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new AbsoluteSizeSpan(20, true), 2, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new ForegroundColorSpan(guessUp ? Color.parseColor("#ed3535") : Color.parseColor("#09C990")), 2,
				index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new AbsoluteSizeSpan(20, true), index + 3, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new ForegroundColorSpan(guessUp ? Color.parseColor("#ed3535") : Color.parseColor("#09C990")),
				index + 3, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(style);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tljr_grp_hq_top:
//		case R.id.tljr_txt_tjzx:
//		case R.id.tljr_btn_tjzx:
		case R.id.bj:
		case R.id.tljr_close:
			dismiss();
			break;
//		case R.id.tljr_grp_guessup:
//			if (!guessUp) {
//				dismiss();
//			}
//			break;
//		case R.id.tljr_grp_guessdown:
//			if (guessUp) {
//				dismiss();
//			}
//			break;
		default:
			break;
		}
	}
}
