package com.abct.tljr.wxapi.xunzhang;

import java.util.ArrayList;
import java.util.List;
import com.abct.tljr.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class MyXunzhangFragment extends Fragment{
	private GridView gridView;
	private List<XunzhangEntity> list = new ArrayList<XunzhangEntity>();
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_xunzhang, null);
		gridView = (GridView) v.findViewById(R.id.gv);
		getData();
		gridView.setAdapter(new MyGridViewAdapter(getActivity(), list));
		return v;
	}
	private void getData() {
		list= ((XunzhangActivity)getActivity()).myXzs;
	}
}
