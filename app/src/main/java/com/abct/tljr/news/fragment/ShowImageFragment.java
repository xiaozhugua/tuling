package com.abct.tljr.news.fragment;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.abct.tljr.R;
import com.abct.tljr.news.ShowWebImageActivity;
import com.abct.tljr.news.widget.CircularProgressView;
import com.abct.tljr.news.widget.ImageViewPager;
import com.abct.tljr.news.widget.ZoomableImageView;
import com.abct.tljr.ui.activity.StartActivity;
import com.abct.tljr.utils.Util;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qh.common.util.LogUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ShowImageFragment extends Fragment {
	private String Tag ="ShowImageFragment";
	private RelativeLayout view;
	private ShowWebImageActivity activity;

	private ZoomableImageView imageView = null;
	private CircularProgressView progressView ;
	private String imagePath ;
	private int position ;
	ImageViewPager viewPager ;

//	public ShowImageFragment(String imagePath,int position,ImageViewPager viewPager){
//	this.imagePath =imagePath ;
//	this.position =position ;
//	this.viewPager =viewPager ;
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.imagePath=bundle.getString("imagePath");
        this.position=bundle.getInt("position");
        this.viewPager=(ImageViewPager)bundle.getSerializable("viewPager");

		activity = (ShowWebImageActivity) getActivity();
		view = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.tljr_fragment_showimage, null);
		progressView = (CircularProgressView)view.findViewById(R.id.web_progress_view);
		progressView.setColor(0xffffffff);
		progressView.setIndeterminate(false);
		imageView = (ZoomableImageView)view.findViewById(R.id.show_webimage_imageview);
		imageView.setViewPager(viewPager);
		imageView.setVisibility(View.INVISIBLE);
		StartActivity.imageLoader.loadImage(imagePath, null, StartActivity.options,
				new SimpleImageLoadingListener() {
			
				@Override
				public void onLoadingStarted(String imageUri, View view)
				{
					// TODO Auto-generated method stub
					super.onLoadingStarted(imageUri, view);
				}
			
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
					{
						// TODO Auto-generated method stub
						super.onLoadingComplete(imageUri, view, loadedImage);
						 
						imageView.setImageBitmap(loadedImage);
						imageView.setVisibility(View.VISIBLE);
						progressView.setVisibility(View.GONE);
					}

				}, new ImageLoadingProgressListener() {

					@Override
					public void onProgressUpdate(String imageUri, View view, int current, int total)
					{
						// TODO Auto-generated method stub
						int progress = (int) (((float) current / (float) total) * 100);
					//	System.out.println("progress" + progress);
						progressView.setProgress(progress) ;
					}
				});
		
		
	
		
	}

	
	public void savePicture(){
		new Thread(new  Runnable() {
			public void run()
			{
				String sdPath = Util.sdPath+"newsPicture/" ;
				String fileNme =imagePath.substring(imagePath.lastIndexOf("/") + 1) ;
				
				if(! imagePath.contains(".png") &&! imagePath.contains(".jpg")) {
					fileNme =position+"_"+fileNme +".png" ;
				}else{
					fileNme =position +"_" +fileNme;
				}
				
				
				
				Bitmap bmp=null ;
				final String line = sdPath + fileNme ;
				if(imageView.getmBitmap()!=null){
					LogUtil.i(Tag, "Bitmap != null") ;
					bmp =imageView.getmBitmap() ;
					  File f = new File(sdPath+fileNme);
					   File path= new File(sdPath );
					  
					  if (!path.exists()) {
						   f.mkdirs();
						  }
					  if (f.exists()) {
						  f.delete();
					  } 
					  try {
					   FileOutputStream out = new FileOutputStream(f);
					   bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
					   out.flush();
					   out.close();
					  
					  } catch (FileNotFoundException e) {
					   // TODO Auto-generated catch block
					   e.printStackTrace();
					  } catch (IOException e) {
					   // TODO Auto-generated catch block
					   e.printStackTrace();
					  }
				}else{
					LogUtil.i(Tag, "Bitmap == null") ;
				}
				activity.showToast("保存图片成功到"+line);
			}
		}).start();
	
		
	}
	
 
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup p = (ViewGroup) view.getParent();
		if (p != null)
			p.removeAllViewsInLayout();
		return view;
	}
}
