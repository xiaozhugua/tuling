package com.abct.tljr.ui.yousuan;

import java.util.ArrayList;
import java.util.List;
import com.abct.tljr.R;
import com.abct.tljr.ui.fragments.BaseFragment;
import com.abct.tljr.ui.fragments.ComputerFragment;
import com.abct.tljr.ui.yousuan.adapter.YouSuanAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class YouSuanBaseFragment extends BaseFragment implements OnCheckedChangeListener{
	private View yousuan;
	private ViewPager mViewPager;
	private RadioGroup mRadioGroup;
	private List<Fragment> listFragment=null;
	public YouSuanShishi mYouSuanShishi=null;
	public ComputerFragment mComputerFragment=null;
	public YouSuanMy mYouSuanMy=null;
	public boolean ComputerFragemntInitStatus=true;
	public boolean YouSuanMyInitStatus=true;
	public RadioButton gengtou;
	public RadioButton celuechi;
	public RadioButton wode;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		yousuan=inflater.inflate(R.layout.yousuanbasefragment,container,false);
		//初始化变量
		//initView();
		return yousuan;
	}
	
	//初始化变量
	@SuppressWarnings("deprecation")
	public void initView(){
		if(mViewPager!=null){
			return ;
		}
		mViewPager=(ViewPager)yousuan.findViewById(R.id.yousuanfragment);
		mViewPager.setOffscreenPageLimit(2);
		mRadioGroup=(RadioGroup)yousuan.findViewById(R.id.tljr_yousuanbase);
		gengtou=(RadioButton)yousuan.findViewById(R.id.tljr_yousuan_shishi);
		celuechi=(RadioButton)yousuan.findViewById(R.id.tljr_yousuan_yanjiu);
		wode=(RadioButton)yousuan.findViewById(R.id.tljr_yousuan_my);
		mRadioGroup.setOnCheckedChangeListener(this);
		mComputerFragment=new ComputerFragment();
		mYouSuanShishi=new YouSuanShishi();
		mYouSuanMy=new YouSuanMy();
		listFragment=new ArrayList<>();
		listFragment.add(mYouSuanShishi);
		listFragment.add(mComputerFragment);
		listFragment.add(mYouSuanMy);
		//初始化viewpager
		mViewPager.setAdapter(new YouSuanAdapter(getFragmentManager(),listFragment));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int page) {
				switch(page){
				case 0:
					mRadioGroup.check(R.id.tljr_yousuan_shishi);
					break;
				case 1:
					mRadioGroup.check(R.id.tljr_yousuan_yanjiu);
					if(ComputerFragemntInitStatus){
						mComputerFragment.init();
						ComputerFragemntInitStatus=false;
					}
					break;
				case 2:
					if(YouSuanMyInitStatus){
						YouSuanMyInitStatus=false;
						mYouSuanMy.initData();
					}
					mRadioGroup.check(R.id.tljr_yousuan_my);
					break;
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2){}
			@Override
			public void onPageScrollStateChanged(int arg0){}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.tljr_yousuan_shishi:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.tljr_yousuan_yanjiu:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.tljr_yousuan_my:
			mViewPager.setCurrentItem(2);
			break;
		}
	}
	
	public void flush(){
		if(mYouSuanShishi!=null){
			mYouSuanShishi.page=1;
			if(mYouSuanShishi.listModel!=null){
				mYouSuanShishi.listModel.clear();
				mYouSuanShishi.initData();
			}
			if(mYouSuanMy.listYouSuan!=null){
				mYouSuanMy.listYouSuan.clear();
				mYouSuanMy.initData();
			}
			mComputerFragment.initData();
		}
	}
	
}
