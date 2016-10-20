package com.abct.tljr.ui.activity.tools;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.dialog.PromptDialog;
import com.abct.tljr.ui.widget.ProgressDlgUtil;
import com.abct.tljr.utils.Util;
import com.qh.common.listener.Complete;
import com.qh.common.listener.NetResult;
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.LogUtil;
import com.qh.common.util.UrlUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年11月14日 下午2:59:07 自建指数页面
 */
public class BuildListActivity extends BaseActivity implements OnClickListener {
	public static final String url = UrlUtil.Url_apicavacn + "tools/index/0.2";
	private ListView lv;
	private ArrayList<OneBuil> list = new ArrayList<OneBuil>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (MyApplication.getInstance().self == null) {
			new PromptDialog(this, "获取配置失败,请先登录", new Complete() {

				@Override
				public void complete() {
					login(true);
					finish();
				}
			}, new Complete() {

				@Override
				public void complete() {
					finish();
				}
			}).show();
			return;
		}
		setContentView(R.layout.tljr_activity_buildlist);
		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(adapter);
		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.tljr_img_build_back).setOnClickListener(this);
		// getMyList();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (MyApplication.getInstance().self != null) {
					getMyList();
				}
			}
		}, 500);

	}

	private void getMyList() {
		ProgressDlgUtil.showProgressDlg("", this);
		LogUtil.e("getMyList", "url=="+url + "/myindex/" + MyApplication.getInstance().self.getId());
		
		HttpRequest.sendPost(url + "/myindex/" + MyApplication.getInstance().self.getId(), "", new HttpRevMsg() {

			@Override
			public void revMsg(final String msg) {
			//	LogUtil.e("getMyList", msg);
				ProgressDlgUtil.stopProgressDlg();
				post(new Runnable() {

					@Override
					public void run() {
						try {
							JSONObject object = new JSONObject(msg);
							JSONArray array = object.getJSONArray("result");
							list.clear();
							for (int i = 0; i < array.length(); i++) {
								list.add(getOneBuld(array.getJSONObject(i)));
							}
							adapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	private void addItem(String name) {
		if (name==null ||  "".equals(name)) {
			
			showToast("还没有输入分组名字呢");
			
			return;
		}
		
		boolean flat = false;
		if (list != null && list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getName().equalsIgnoreCase(name)) {
					// 忽略大小写比较
					flat = true;

				}
			}
			if (flat) {
				showToast("分组名字不可以重复！字母不区分大小写");
				return;
			}

		}
		ProgressDlgUtil.showProgressDlg("", this);
		HttpRequest.sendPost(url + "/myindex/" + MyApplication.getInstance().self.getId() + "/add", "name=" + name,
				new HttpRevMsg() {

					@Override
					public void revMsg(final String msg) {
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject obj = new JSONObject(msg).getJSONObject("result");
									String nameString = getOneBuld(obj).getName();
									if (nameString == null || "".equals(nameString)) {
										return;

									}

									list.add(getOneBuld(obj));
									adapter.notifyDataSetChanged();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
	}

	private OneBuil getOneBuld(JSONObject object) {
		OneBuil b = new OneBuil();
		b.setCreateAt(object.optLong("createAt"));
		b.setDetail(object.optString("detail"));
		b.setIspublic(object.optBoolean("ispublic"));
		b.setId(object.optString("myindexId"));
		b.setName(object.optString("name"));
		b.setUname(object.optString("uname"));
		return b;
	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (v == null) {
				v = View.inflate(BuildListActivity.this, R.layout.tljr_item_buildlist, null);
			}
			OneBuil buil = list.get(position);
			v.setTag(buil);
			((TextView) v.findViewById(R.id.tljr_hq_name_test)).setText(buil.getName());
			((TextView) v.findViewById(R.id.tljr_hq_info_test)).setText(buil.getDetail());
			((TextView) v.findViewById(R.id.tljr_hq_now_test)).setText(Util.getDate(buil.getCreateAt()));
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(BuildListActivity.this, BuildActivity.class).putExtra("id",
							((OneBuil) v.getTag()).getId()));
				}
			});
			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					remove((OneBuil) v.getTag());
					return false;
				}
			});
			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					remove((OneBuil) v.getTag());
					return false;
				}
			});
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

	private void remove(final OneBuil buil) {
		new PromptDialog(this, "确定移除此分组？", new Complete() {

			@Override
			public void complete() {
				ProgressDlgUtil.showProgressDlg("", BuildListActivity.this);
				HttpRequest.sendPost(
						url + "/myindex/" + MyApplication.getInstance().self.getId() + "/remove/" + buil.getId(), "",
						new HttpRevMsg() {

					@Override
					public void revMsg(final String msg) {
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject object = new JSONObject(msg);
									if (object.getJSONObject("result").getInt("n") != 1) {
										showToast("删除失败，请重试!");
										return;
									}
									list.remove(buil);
									adapter.notifyDataSetChanged();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
			}
		}).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add:
			if (list.size() > 9) {
				showToast("不支持更多分组的创建!");
				return;
			}
			new PromptDialog(this).showEdit("请输入分组名称(最多10字)", new NetResult() {

				@Override
				public void result(String msg) {
					if (msg.length() > 10)
						msg = msg.substring(0, 10);
					addItem(msg);
				}
			});
			break;
		case R.id.tljr_img_build_back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		list = null;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
}

class OneBuil {
	private String id;
	private String name;
	private boolean ispublic;
	private String uname;
	private String detail;
	private long createAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIspublic() {
		return ispublic;
	}

	public void setIspublic(boolean ispublic) {
		this.ispublic = ispublic;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

}
