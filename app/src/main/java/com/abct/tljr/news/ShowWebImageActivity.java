package com.abct.tljr.news;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.abct.tljr.BaseFragmentActivity;
import com.abct.tljr.R;
import com.abct.tljr.data.Constant;
import com.abct.tljr.news.fragment.ShowImageFragment;
import com.abct.tljr.news.widget.ImageViewPager;

public class ShowWebImageActivity extends BaseFragmentActivity {

	private String imagePath = null;

	private ArrayList<String> uList;

	ImageViewPager pager;
	ShowImageAdapter adapter;
	
	TextView tljr_txt_page ;
	
    private  HashMap<Integer, Fragment> fragmentList = new HashMap<Integer, Fragment>();
	
	int currentPosition ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tljr_activity_webimageshow);
		this.imagePath = getIntent().getStringExtra("image");
		uList = getIntent().getExtras().getStringArrayList("ulist");
		currentPosition =uList.indexOf(imagePath) ;
		adapter = new ShowImageAdapter(getSupportFragmentManager());
		
		tljr_txt_page = (TextView)findViewById(R.id.tljr_txt_page);
		  findViewById(R.id.tljr_img_news_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)
			{
				finish();
			}
		});
		
		pager = (ImageViewPager) findViewById(R.id.imgeview_pager);
		pager.setAdapter(adapter);
		pager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position)
			{
				Constant.addClickCount();
				// TODO Auto-generated method stub
				tljr_txt_page.setText(position+1+"/"+ uList.size()  ) ;
				currentPosition = position ;
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
		pager.setCurrentItem(currentPosition) ;
		tljr_txt_page.setText(currentPosition+1+"/"+ uList.size()  ) ;


	}
	
	public void svaePic(View view){
		
		
		ShowImageFragment showFragment=(ShowImageFragment)fragmentList.get(currentPosition) ;
		showFragment.savePicture() ;
		
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		 
		
	}
	

	public class ShowImageAdapter extends FragmentPagerAdapter {
		FragmentManager fm;

		public ShowImageAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;
		}

		@Override
		public void destroyItem(View container, int position, Object object)
		{
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);

			View view = (View) object;
			((ViewPager) container).removeView(view);
			view = null;

		}

		@Override
		public Fragment getItem(int position)
		{
			if(  fragmentList.get(position)==null){
//				ShowImageFragment sif = new ShowImageFragment(uList.get(position),position,pager);
				ShowImageFragment sif = newInstance(uList.get(position),position,pager);
				fragmentList.put(position, sif) ;
				return sif;
			}else{
				ShowImageFragment sif  =  (ShowImageFragment) fragmentList.get(position) ;
				return sif;
			}
		}

        public ShowImageFragment newInstance(String imagePath,int position,ImageViewPager viewPager){
            ShowImageFragment showImageFragment=new ShowImageFragment();
            Bundle bundle=new Bundle();
            bundle.putString("imagePath",imagePath);
            bundle.putInt("position",position);
            bundle.putSerializable("viewPager",viewPager);
            showImageFragment.setArguments(bundle);
            return showImageFragment;
        }

		@Override
		public int getCount()
		{
			return uList.size();
		}
	}

}
