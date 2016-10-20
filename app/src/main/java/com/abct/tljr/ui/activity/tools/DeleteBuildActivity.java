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
import com.qh.common.login.util.HttpRequest;
import com.qh.common.login.util.HttpRevMsg;
import com.qh.common.util.LogUtil;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年11月25日 上午10:41:58
 */
/**
 * @author xbw
 *
 */
public class DeleteBuildActivity extends BaseActivity implements
		OnClickListener {
	private String OneBuildID;
	private ArrayList<OneBuild> list = new ArrayList<OneBuild>();
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_builddelete);
		OneBuildID = getIntent().getStringExtra("id");
		((TextView) findViewById(R.id.name)).setText(getIntent()
				.getStringExtra("name"));
		((TextView) findViewById(R.id.btip)).setText(getIntent()
				.getStringExtra("btip"));
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		findViewById(R.id.tljr_img_build_back).setOnClickListener(this);
		findViewById(R.id.check).setOnClickListener(this);
		findViewById(R.id.delete).setOnClickListener(this);
		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(adapter);
		getMyList();
	}

	private void getMyList() {
		ProgressDlgUtil.showProgressDlg("", this);
		HttpRequest.sendPost(
				BuildActivity.url + "/myindex/"
						+ MyApplication.getInstance().self.getId() + "/"
						+ OneBuildID + "/info", "", new HttpRevMsg() {

					@Override
					public void revMsg(final String msg) {
						LogUtil.e("getMyList", msg);
						ProgressDlgUtil.stopProgressDlg();
						post(new Runnable() {

							@Override
							public void run() {
								initUi(msg);
							}
						});
					}
				});
	}

	private void initUi(String msg) {
		try {
			JSONObject jsonObject = new JSONObject(msg).getJSONObject("result");
			JSONArray array = jsonObject.getJSONArray("list");
			list.clear();
			for (int i = 0; i < array.length(); i++) {
				editOneBuild(null, array.getString(i), false);
			}
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void editOneBuild(OneBuild gu, String msg, boolean isInit) {
		try {
			if (gu == null) {
				gu = new OneBuild();
				list.add(gu);
			}
			JSONObject object = new JSONObject(msg);
			gu.setId(object.getString("subItemId"));
			gu.setType(object.getString("operation"));
			gu.setValue((float) object.getDouble("coefficient"));
			JSONObject obj = object.getJSONObject("product");
			gu.setName(obj.getString("name"));
			gu.setCode(obj.getString("code"));
			gu.setProductId(obj.getString("productId"));
			if (isInit)
				adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (view == null) {
				view = View.inflate(DeleteBuildActivity.this,
						R.layout.tljr_item_build_edit, null);
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(
						AbsListView.LayoutParams.FILL_PARENT, Util.HEIGHT / 10);
				view.setLayoutParams(params);
				view.findViewById(R.id.tljr_build_edit_typearrow)
						.setVisibility(View.GONE);
				view.findViewById(R.id.tljr_build_edit_codearrow)
						.setVisibility(View.GONE);
				view.findViewById(R.id.tljr_build_edit_namearrow)
						.setVisibility(View.GONE);
				holder = new ViewHolder();
				holder.cb = (CheckBox) view
						.findViewById(R.id.tljr_build_edit_cb_type);
				holder.cb.setVisibility(View.VISIBLE);
				holder.type = (TextView) view
						.findViewById(R.id.tljr_build_type);
				holder.value = (TextView) view
						.findViewById(R.id.tljr_build_edit_et_type);
				holder.buildType = (TextView) view
						.findViewById(R.id.tljr_build_edit_code);
				holder.name = (TextView) view
						.findViewById(R.id.tljr_build_edit_name);
				holder.value.setEnabled(false);
				holder.cb
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								list.get(position).setDelete(isChecked);
								boolean isAllCheck = true;
								for (int i = 0; i < list.size(); i++) {
									isAllCheck = isAllCheck
											&& list.get(i).isDelete();
								}
								((TextView) findViewById(R.id.check))
										.setText(isAllCheck ? "取消全选" : "全选");
							}
						});
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			OneBuild gu = list.get(position);
			holder.type.setText(gu.getType());
			holder.value.setText(gu.getValue() + "");
			holder.buildType.setText(gu.getBuildType());
			holder.name.setText(gu.getName());
			holder.cb.setChecked(gu.isDelete());
			holder.cb.setTag(gu);
			if (BuildActivity.buildType.containsKey(gu.getBuildType()))
				holder.buildType.setText(BuildActivity.buildType.get(gu
						.getBuildType()));
			return view;
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

	static class ViewHolder {
		CheckBox cb;
		TextView type, value, buildType, name;
	}

	private void allCheck() {
		boolean isAllCheck = true;
		for (int i = 0; i < list.size(); i++) {
			isAllCheck = isAllCheck && list.get(i).isDelete();
		}
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setDelete(isAllCheck ? false : true);
		}
		((TextView) findViewById(R.id.check)).setText(isAllCheck ? "取消全选"
				: "全选");
		adapter.notifyDataSetChanged();
	}

	private void delete() {
		int checkCount = 0;
		for (int i = 0; i < list.size(); i++) {
			checkCount += (list.get(i).isDelete() ? 1 : 0);
		}
		if (checkCount == 0) {
			showToast("请先选择要删除的分组!");
			return;
		}
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isDelete()) {
				OneBuild build = list.get(i);
				s += (build.getId() + "|");
			}
		}
		s = s.substring(0, s.length() - 1);
		LogUtil.e("editBuildParmes", BuildActivity.url + "/myindex/"
				+ MyApplication.getInstance().self.getId() + "/" + OneBuildID
				+ "/removeSubItems/" + s);
		HttpRequest.sendPost(
				BuildActivity.url + "/myindex/"
						+ MyApplication.getInstance().self.getId() + "/"
						+ OneBuildID + "/removeSubItems/" + s, "",
				new HttpRevMsg() {

					@Override
					public void revMsg(final String msg) {
						LogUtil.e("editBuild", msg);
						BuildActivity.isEdit = true;
						getMyList();
					}
				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tljr_img_build_back:
			finish();
			break;
		case R.id.check:
			allCheck();
			break;
		case R.id.delete:
			int checkCount = 0;
			for (int i = 0; i < list.size(); i++) {
				checkCount += (list.get(i).isDelete() ? 1 : 0);
			}
			if (checkCount == 0) {
				showToast("请先选择要删除的分组!");
				return;
			}
			new PromptDialog(this, "确定要删除选择的分组？", new Complete() {

				@Override
				public void complete() {
					delete();
				}
			}).show();
			break;

		default:
			break;
		}
	}
}
