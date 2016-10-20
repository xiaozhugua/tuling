package com.abct.tljr.ui.fragments;

import com.abct.tljr.main.MainActivity;
import com.ryg.dynamicload.internal.DLIntent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author xbw
 * @version 创建时间：2015年8月31日 下午4:42:57
 */
public class BaseFragment extends Fragment {
    public FragmentActivity activity;
    public View baseView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (FragmentActivity) getActivity();
        baseView = new TextView(activity);
        ((TextView) baseView).setText("这是Fragment页面");
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height","dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup p = (ViewGroup) baseView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return baseView;
    }

    public View findViewById(int id) {
    	if(baseView==null){
    		startActivity(new DLIntent(activity.getPackageName(),MainActivity.class));
    		return null;
    	}
        return baseView.findViewById(id);
    }
}
