package com.abct.tljr.news.adapter;

/**
 * Created by mac on 16/2/1.
 */


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.abct.tljr.R;
import com.abct.tljr.news.channel.bean.ChannelItem;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
   // private ArrayList<Fragment> fragments;
    HashMap<String,Fragment> fragmentList ;
    public FragmentManager fm;
    private ArrayList<ChannelItem> userChannelList;
    private FragmentActivity activity ;
    public NewsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public NewsFragmentPagerAdapter(FragmentManager fm, ArrayList<ChannelItem> userChannelList, FragmentActivity activity,HashMap<String,Fragment> fragmentList) {
        super(fm);
        this.fm = fm;
        this.userChannelList = userChannelList;
        this.activity =activity;
        this.fragmentList =fragmentList;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        String title = userChannelList.get(position).getName();

        if (title.equals("大V精选"))
        {
            title = "订阅";
        }
        return title.length() == 2 ? activity.getResources().getString(R.string.space) + title
                + activity.getResources().getString(R.string.space) : title;


    }

    @Override
    public int getCount() {
        return userChannelList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(userChannelList.get(position).getName());
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    @Override
    public void destroyItem(View container, int position, Object object)
    {
        // TODO Auto-generated method stub
        // super.destroyItem(container, position, object);

        View view = (View) object;
        ((ViewPager) container).removeView(view);
        view = null;

    }




    public void setFragments(HashMap<String,Fragment> fragmentList) {
        if (this.fragmentList != null && this.fragmentList.size() > 0) {
        	
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragmentList.values()) {
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragmentList = fragmentList;
        notifyDataSetChanged();
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, final int position) {
//        Object obj = super.instantiateItem(container, position);
//        return obj;
//    }

}
